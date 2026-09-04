:setvar SourceDatabase "AuroraSalesSource"
:setvar CentralDatabase "Central"
:setvar AuroraStoreId "7890"

-- Replace only Aurora's local demo product snapshot with the real product
-- identities used by Aurora invoice lines. Other stores remain untouched.
--
-- Inventory_Vendors in the store backup can contain more than one historical
-- row for the same Central logical key (ItemNum + Store_ID + Vendor_Number).
-- The most recently modified mapping is selected deterministically.

SET NOCOUNT ON;
SET XACT_ABORT ON;

IF DB_ID(N'$(SourceDatabase)') IS NULL
    THROW 51100, 'Aurora source database does not exist.', 1;

IF DB_ID(N'$(CentralDatabase)') IS NULL
    THROW 51101, 'Central database does not exist.', 1;

DECLARE @InventoryRows bigint;
DECLARE @VendorMappingRows bigint;

BEGIN TRANSACTION;

-- Shared reference data is additive. Numeric Aurora identifiers do not replace
-- the existing Central demo identifiers such as V0001 and D01.
UPDATE target
SET target.First_Name = source.First_Name,
    target.Last_Name = source.Last_Name,
    target.Company = source.Company,
    target.Address_1 = source.Address_1,
    target.Address_2 = source.Address_2,
    target.City = source.City,
    target.State = source.State,
    target.Zip_Code = source.Zip_Code,
    target.Phone = source.Phone,
    target.Fax = source.Fax,
    target.Country = source.Country,
    target.Email = source.Email
FROM [$(CentralDatabase)].dbo.Vendors AS target
JOIN [$(SourceDatabase)].dbo.Vendors AS source
  ON target.Vendor_Number = LTRIM(RTRIM(source.Vendor_Number));

INSERT INTO [$(CentralDatabase)].dbo.Vendors
    (Vendor_Number, First_Name, Last_Name, Company, Address_1, Address_2,
     City, State, Zip_Code, Phone, Fax, Country, Email)
SELECT LTRIM(RTRIM(source.Vendor_Number)), source.First_Name, source.Last_Name,
       source.Company, source.Address_1, source.Address_2, source.City,
       source.State, source.Zip_Code, source.Phone, source.Fax,
       source.Country, source.Email
FROM [$(SourceDatabase)].dbo.Vendors AS source
WHERE NOT EXISTS (
    SELECT 1
    FROM [$(CentralDatabase)].dbo.Vendors AS target
    WHERE target.Vendor_Number = LTRIM(RTRIM(source.Vendor_Number))
);

UPDATE target
SET target.Description = source.Description,
    target.Type = CONVERT(nvarchar(50), source.Type)
FROM [$(CentralDatabase)].dbo.Departments AS target
JOIN [$(SourceDatabase)].dbo.Departments AS source
  ON target.Dept_ID = LTRIM(RTRIM(source.Dept_ID))
WHERE source.Store_ID = N'$(AuroraStoreId)';

INSERT INTO [$(CentralDatabase)].dbo.Departments (Dept_ID, Description, Type)
SELECT LTRIM(RTRIM(source.Dept_ID)), source.Description,
       CONVERT(nvarchar(50), source.Type)
FROM [$(SourceDatabase)].dbo.Departments AS source
WHERE source.Store_ID = N'$(AuroraStoreId)'
  AND NOT EXISTS (
      SELECT 1
      FROM [$(CentralDatabase)].dbo.Departments AS target
      WHERE target.Dept_ID = LTRIM(RTRIM(source.Dept_ID))
  );

-- Replace store-scoped product data only for Aurora.
DELETE FROM [$(CentralDatabase)].dbo.Inventory_Vendors
WHERE Store_ID = N'$(AuroraStoreId)';

DELETE FROM [$(CentralDatabase)].dbo.Inventory
WHERE Store_ID = N'$(AuroraStoreId)';

DELETE FROM [$(CentralDatabase)].dbo.vw_AllInventory
WHERE Store_ID = N'$(AuroraStoreId)';

INSERT INTO [$(CentralDatabase)].dbo.Inventory
    (ItemNum, ItemName, In_Stock, Dept_ID, Vendor_Number, Store_ID,
     Vendor_Part_Num)
SELECT source.ItemNum,
       source.ItemName,
       CONVERT(numeric(19,2), source.In_Stock),
       NULLIF(LTRIM(RTRIM(source.Dept_ID)), N''),
       NULLIF(LTRIM(RTRIM(source.Vendor_Number)), N''),
       N'$(AuroraStoreId)',
       source.Vendor_Part_Num
FROM [$(SourceDatabase)].dbo.Inventory AS source
WHERE source.Store_ID = N'$(AuroraStoreId)'
  AND ISNULL(source.IsDeleted, 0) = 0
  AND ISNULL(source.Inactive, 0) = 0;

SET @InventoryRows = @@ROWCOUNT;

INSERT INTO [$(CentralDatabase)].dbo.vw_AllInventory
    (ItemNum, Store_ID, Vendor_Number, Vendor_Part_Num, In_Stock, ItemName)
SELECT source.ItemNum,
       N'$(AuroraStoreId)',
       NULLIF(LTRIM(RTRIM(source.Vendor_Number)), N''),
       source.Vendor_Part_Num,
       CONVERT(numeric(19,2), source.In_Stock),
       source.ItemName
FROM [$(SourceDatabase)].dbo.Inventory AS source
WHERE source.Store_ID = N'$(AuroraStoreId)'
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
    WHERE mapping.Store_ID = N'$(AuroraStoreId)'
      AND ISNULL(inventory.IsDeleted, 0) = 0
      AND ISNULL(inventory.Inactive, 0) = 0
)
INSERT INTO [$(CentralDatabase)].dbo.Inventory_Vendors
    (ItemNum, Store_ID, Vendor_Number, Vendor_Part_Num, costPer,
     Case_Cost, NumPerVenCase)
SELECT ItemNum,
       N'$(AuroraStoreId)',
       Vendor_Number,
       Vendor_Part_Num,
       CONVERT(nvarchar(50), CostPer),
       CONVERT(nvarchar(50), Case_Cost),
       CONVERT(nvarchar(50), NumPerVenCase)
FROM ranked_mappings
WHERE mapping_rank = 1;

SET @VendorMappingRows = @@ROWCOUNT;

COMMIT TRANSACTION;

SELECT N'$(AuroraStoreId)' AS store_id,
       @InventoryRows AS active_inventory_rows,
       @VendorMappingRows AS vendor_mapping_rows,
       (SELECT COUNT_BIG(*)
        FROM [$(CentralDatabase)].dbo.vw_AllInvoiceItemized AS sales
        JOIN [$(CentralDatabase)].dbo.Inventory AS inventory
          ON inventory.ItemNum = sales.ItemNum
         AND inventory.Store_ID = sales.Store_ID
        WHERE sales.Store_ID = N'$(AuroraStoreId)') AS sales_lines_with_product_match;

