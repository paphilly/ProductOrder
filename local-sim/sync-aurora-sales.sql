:setvar SourceDatabase "AuroraSalesSource"
:setvar CentralDatabase "Central"
:setvar AuroraStoreId "7890"

-- Import Aurora invoice lines into the existing Central sales-history store.
--
-- Run with SQLCMD mode enabled. Override variables with -v when the restored
-- source or Central database uses a different name:
--
--   sqlcmd ... -v SourceDatabase="AuroraSalesSource" \
--                    CentralDatabase="Central" AuroraStoreId="7890" \
--                    ReplaceAuroraSnapshot="0" \
--              -i sync-aurora-sales.sql
--
-- The import is idempotent. Existing invoice lines are updated and new lines
-- are inserted using the Central key Invoice_Number + LineNum + Store_ID.
-- It deliberately does not delete Central records that are absent from a
-- backup unless the required ReplaceAuroraSnapshot variable is set to 1.
-- Use ReplaceAuroraSnapshot=0 for the non-destructive default behavior.
-- Snapshot replacement is
-- useful locally to remove simulated rows, but should only be enabled when the
-- backup is authoritative for the requested store.

SET NOCOUNT ON;
SET XACT_ABORT ON;

IF DB_ID(N'$(SourceDatabase)') IS NULL
    THROW 51000, 'Aurora source database does not exist.', 1;

IF DB_ID(N'$(CentralDatabase)') IS NULL
    THROW 51001, 'Central database does not exist.', 1;

IF OBJECT_ID(N'$(SourceDatabase).dbo.Invoice_Itemized', N'U') IS NULL
    THROW 51002, 'Aurora source table dbo.Invoice_Itemized does not exist.', 1;

IF OBJECT_ID(N'$(CentralDatabase).dbo.vw_AllInvoiceItemized', N'U') IS NULL
    THROW 51003, 'Central sales-history target is not a writable table.', 1;

DECLARE @SourceRows bigint;
DECLARE @UpdatedRows bigint;
DECLARE @InsertedRows bigint;
DECLARE @DeletedRows bigint = 0;

SELECT @SourceRows = COUNT_BIG(*)
FROM [$(SourceDatabase)].dbo.Invoice_Itemized
WHERE Store_ID = N'$(AuroraStoreId)';

IF @SourceRows = 0
    THROW 51004, 'No invoice lines were found for the requested Aurora store.', 1;

BEGIN TRANSACTION;

UPDATE target
SET target.ItemNum = source.ItemNum,
    target.Quantity = source.Quantity,
    target.CostPer = source.CostPer,
    target.PricePer = source.PricePer,
    target.DiffItemName = source.DiffItemName,
    -- ModifiedDate in the app-facing table is the sales-history timestamp.
    -- CRE can update source.ModifiedDate long after the receipt was rung up,
    -- so source.CreateDate is the authoritative line transaction time.
    target.ModifiedDate = source.CreateDate
FROM [$(CentralDatabase)].dbo.vw_AllInvoiceItemized AS target
JOIN [$(SourceDatabase)].dbo.Invoice_Itemized AS source
  ON target.Invoice_Number = CONVERT(nvarchar(50), source.Invoice_Number)
 AND target.LineNum = CONVERT(nvarchar(20), source.LineNum)
 AND target.Store_ID = N'$(AuroraStoreId)'
WHERE source.Store_ID = N'$(AuroraStoreId)';

SET @UpdatedRows = @@ROWCOUNT;

INSERT INTO [$(CentralDatabase)].dbo.vw_AllInvoiceItemized
    (Invoice_Number, LineNum, Store_ID, ItemNum, Quantity, CostPer,
     PricePer, DiffItemName, ModifiedDate)
SELECT CONVERT(nvarchar(50), source.Invoice_Number),
       CONVERT(nvarchar(20), source.LineNum),
       N'$(AuroraStoreId)',
       source.ItemNum,
       CONVERT(numeric(19,2), source.Quantity),
       CONVERT(numeric(19,2), source.CostPer),
       CONVERT(numeric(19,2), source.PricePer),
       source.DiffItemName,
       source.CreateDate
FROM [$(SourceDatabase)].dbo.Invoice_Itemized AS source
WHERE source.Store_ID = N'$(AuroraStoreId)'
  AND NOT EXISTS (
      SELECT 1
      FROM [$(CentralDatabase)].dbo.vw_AllInvoiceItemized AS target WITH (UPDLOCK, HOLDLOCK)
      WHERE target.Invoice_Number = CONVERT(nvarchar(50), source.Invoice_Number)
        AND target.LineNum = CONVERT(nvarchar(20), source.LineNum)
        AND target.Store_ID = N'$(AuroraStoreId)'
  );

SET @InsertedRows = @@ROWCOUNT;

IF N'$(ReplaceAuroraSnapshot)' = N'1'
BEGIN
    DELETE target
    FROM [$(CentralDatabase)].dbo.vw_AllInvoiceItemized AS target
    WHERE target.Store_ID = N'$(AuroraStoreId)'
      AND NOT EXISTS (
          SELECT 1
          FROM [$(SourceDatabase)].dbo.Invoice_Itemized AS source
          WHERE source.Store_ID = N'$(AuroraStoreId)'
            AND target.Invoice_Number = CONVERT(nvarchar(50), source.Invoice_Number)
            AND target.LineNum = CONVERT(nvarchar(20), source.LineNum)
      );

    SET @DeletedRows = @@ROWCOUNT;
END;

COMMIT TRANSACTION;

SELECT N'$(SourceDatabase)' AS source_database,
       N'$(CentralDatabase)' AS central_database,
       N'$(AuroraStoreId)' AS store_id,
       @SourceRows AS source_rows,
       @UpdatedRows AS updated_rows,
       @InsertedRows AS inserted_rows,
       @DeletedRows AS deleted_rows,
       (SELECT COUNT_BIG(*)
        FROM [$(CentralDatabase)].dbo.vw_AllInvoiceItemized
        WHERE Store_ID = N'$(AuroraStoreId)') AS central_aurora_rows;
