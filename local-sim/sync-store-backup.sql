:setvar SourceDatabase "CresqlSource20260814"
:setvar CentralDatabase "Central"
:setvar StoreId "3456"

-- Import one authoritative PC America store backup into the consolidated
-- Central database. All destructive operations are scoped to StoreId.
-- Shared Vendors and Departments are additive so importing one store cannot
-- rewrite reference data already used by another store.

SET NOCOUNT ON;
SET XACT_ABORT ON;

IF DB_ID(N'$(SourceDatabase)') IS NULL
    THROW 51200, 'Source database does not exist.', 1;

IF DB_ID(N'$(CentralDatabase)') IS NULL
    THROW 51201, 'Central database does not exist.', 1;

IF NOT EXISTS (
    SELECT 1
    FROM [$(CentralDatabase)].dbo.Store
    WHERE Store_ID = N'$(StoreId)'
)
    THROW 51202, 'StoreId is not registered in Central.dbo.Store.', 1;

IF NOT EXISTS (
    SELECT 1
    FROM [$(SourceDatabase)].dbo.Inventory
    WHERE Store_ID = N'$(StoreId)'
)
    THROW 51203, 'No source inventory exists for StoreId.', 1;

IF NOT EXISTS (
    SELECT 1
    FROM [$(SourceDatabase)].dbo.Invoice_Itemized
    WHERE Store_ID = N'$(StoreId)'
)
    THROW 51204, 'No source sales exist for StoreId.', 1;

DECLARE @InventoryRows bigint;
DECLARE @VendorMappingRows bigint;
DECLARE @SalesRows bigint;

BEGIN TRANSACTION;

-- Shared reference tables are additive. Existing rows may be used by other
-- stores and therefore are deliberately not updated from this store backup.
INSERT INTO [$(CentralDatabase)].dbo.Vendors
    (Vendor_Number, First_Name, Last_Name, Company, Address_1, Address_2,
     City, State, Zip_Code, Phone, Fax, Country, Email)
SELECT LTRIM(RTRIM(source.Vendor_Number)), source.First_Name, source.Last_Name,
       source.Company, source.Address_1, source.Address_2, source.City,
       source.State, source.Zip_Code, source.Phone, source.Fax,
       source.Country, source.Email
FROM [$(SourceDatabase)].dbo.Vendors AS source
WHERE NULLIF(LTRIM(RTRIM(source.Vendor_Number)), N'') IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM [$(CentralDatabase)].dbo.Vendors AS target
      WHERE target.Vendor_Number = LTRIM(RTRIM(source.Vendor_Number))
  );

INSERT INTO [$(CentralDatabase)].dbo.Departments (Dept_ID, Description, Type)
SELECT LTRIM(RTRIM(source.Dept_ID)), source.Description,
       CONVERT(nvarchar(50), source.Type)
FROM [$(SourceDatabase)].dbo.Departments AS source
WHERE source.Store_ID = N'$(StoreId)'
  AND NULLIF(LTRIM(RTRIM(source.Dept_ID)), N'') IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM [$(CentralDatabase)].dbo.Departments AS target
      WHERE target.Dept_ID = LTRIM(RTRIM(source.Dept_ID))
  );

-- Replace only this store's placeholder/current snapshot.
DELETE FROM [$(CentralDatabase)].dbo.Inventory_Vendors
WHERE Store_ID = N'$(StoreId)';

DELETE FROM [$(CentralDatabase)].dbo.Inventory
WHERE Store_ID = N'$(StoreId)';

DELETE FROM [$(CentralDatabase)].dbo.vw_AllInventory
WHERE Store_ID = N'$(StoreId)';

INSERT INTO [$(CentralDatabase)].dbo.Inventory
    (ItemNum, ItemName, In_Stock, Dept_ID, Vendor_Number, Store_ID,
     Vendor_Part_Num)
SELECT source.ItemNum,
       source.ItemName,
       CONVERT(numeric(19,2), source.In_Stock),
       NULLIF(LTRIM(RTRIM(source.Dept_ID)), N''),
       NULLIF(LTRIM(RTRIM(source.Vendor_Number)), N''),
       N'$(StoreId)',
       source.Vendor_Part_Num
FROM [$(SourceDatabase)].dbo.Inventory AS source
WHERE source.Store_ID = N'$(StoreId)'
  AND ISNULL(source.IsDeleted, 0) = 0
  AND ISNULL(source.Inactive, 0) = 0;

SET @InventoryRows = @@ROWCOUNT;

INSERT INTO [$(CentralDatabase)].dbo.vw_AllInventory
    (ItemNum, Store_ID, Vendor_Number, Vendor_Part_Num, In_Stock, ItemName)
SELECT source.ItemNum,
       N'$(StoreId)',
       NULLIF(LTRIM(RTRIM(source.Vendor_Number)), N''),
       source.Vendor_Part_Num,
       CONVERT(numeric(19,2), source.In_Stock),
       source.ItemName
FROM [$(SourceDatabase)].dbo.Inventory AS source
WHERE source.Store_ID = N'$(StoreId)'
  AND ISNULL(source.IsDeleted, 0) = 0
  AND ISNULL(source.Inactive, 0) = 0;

;WITH ranked_mappings AS (
    SELECT mapping.ItemNum,
           LTRIM(RTRIM(mapping.Vendor_Number)) AS Vendor_Number,
           mapping.CostPer,
           mapping.Case_Cost,
           mapping.NumPerVenCase,
           mapping.Vendor_Part_Num,
           ROW_NUMBER() OVER (
               PARTITION BY mapping.ItemNum,
                            LTRIM(RTRIM(mapping.Vendor_Number))
               ORDER BY mapping.ModifiedDate DESC,
                        mapping.CreateDate DESC,
                        mapping.Vendor_Part_Num
           ) AS mapping_rank
    FROM [$(SourceDatabase)].dbo.Inventory_Vendors AS mapping
    JOIN [$(SourceDatabase)].dbo.Inventory AS inventory
      ON inventory.ItemNum = mapping.ItemNum
     AND inventory.Store_ID = mapping.Store_ID
    WHERE mapping.Store_ID = N'$(StoreId)'
      AND NULLIF(LTRIM(RTRIM(mapping.Vendor_Number)), N'') IS NOT NULL
      AND ISNULL(inventory.IsDeleted, 0) = 0
      AND ISNULL(inventory.Inactive, 0) = 0
)
INSERT INTO [$(CentralDatabase)].dbo.Inventory_Vendors
    (ItemNum, Store_ID, Vendor_Number, Vendor_Part_Num, costPer,
     Case_Cost, NumPerVenCase)
SELECT ItemNum,
       N'$(StoreId)',
       Vendor_Number,
       Vendor_Part_Num,
       CONVERT(nvarchar(50), CostPer),
       CONVERT(nvarchar(50), Case_Cost),
       CONVERT(nvarchar(50), NumPerVenCase)
FROM ranked_mappings
WHERE mapping_rank = 1;

SET @VendorMappingRows = @@ROWCOUNT;

-- This backup is the authoritative Desmoines snapshot, so remove the seeded
-- rows for this store and load its actual receipt lines. Other stores remain.
DELETE FROM [$(CentralDatabase)].dbo.vw_AllInvoiceItemized
WHERE Store_ID = N'$(StoreId)';

INSERT INTO [$(CentralDatabase)].dbo.vw_AllInvoiceItemized
    (Invoice_Number, LineNum, Store_ID, ItemNum, Quantity, CostPer,
     PricePer, DiffItemName, ModifiedDate)
SELECT CONVERT(nvarchar(50), source.Invoice_Number),
       CONVERT(nvarchar(20), source.LineNum),
       N'$(StoreId)',
       source.ItemNum,
       CONVERT(numeric(19,2), source.Quantity),
       CONVERT(numeric(19,2), source.CostPer),
       CONVERT(numeric(19,2), source.PricePer),
       source.DiffItemName,
       source.CreateDate
FROM [$(SourceDatabase)].dbo.Invoice_Itemized AS source
WHERE source.Store_ID = N'$(StoreId)';

SET @SalesRows = @@ROWCOUNT;

COMMIT TRANSACTION;

SELECT N'$(SourceDatabase)' AS source_database,
       N'$(CentralDatabase)' AS central_database,
       N'$(StoreId)' AS store_id,
       @InventoryRows AS active_inventory_rows,
       @VendorMappingRows AS vendor_mapping_rows,
       @SalesRows AS sales_rows,
       (SELECT COUNT_BIG(*)
        FROM [$(CentralDatabase)].dbo.vw_AllInvoiceItemized AS sales
        JOIN [$(CentralDatabase)].dbo.Inventory AS inventory
          ON inventory.ItemNum = sales.ItemNum
         AND inventory.Store_ID = sales.Store_ID
        WHERE sales.Store_ID = N'$(StoreId)') AS sales_lines_with_active_product;
