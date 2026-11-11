:setvar SourceDatabase "CentralBackup963"
:setvar TargetDatabase "Central"
:setvar CentralStoreId "9999"

-- Synchronize the restored Central master into the smaller app-facing Central
-- schema. This keeps application-owned data (users, product orders, bills,
-- etc.) intact and replaces only the store-9999 master-data snapshot.
--
-- Run with SQLCMD mode enabled:
--
--   sqlcmd ... -v SourceDatabase="CentralBackup963" \
--                    TargetDatabase="Central" CentralStoreId="9999" \
--              -i sync-central-master.sql

SET NOCOUNT ON;
SET XACT_ABORT ON;

IF DB_ID(N'$(SourceDatabase)') IS NULL
    THROW 51200, 'Central source database does not exist.', 1;

IF DB_ID(N'$(TargetDatabase)') IS NULL
    THROW 51201, 'App-facing Central database does not exist.', 1;

DECLARE @InventoryRows bigint;
DECLARE @VendorMappingRows bigint;
DECLARE @VendorCatalogRows bigint;

BEGIN TRANSACTION;

-- Central store metadata.
UPDATE target
SET target.Store_Name = source.Store_Name,
    target.Address_Info = source.Address_Info,
    target.Address_Info_1 = source.Address_Info_1,
    target.Address_Info_2 = source.Address_Info_2,
    target.Email = source.Email
FROM [$(TargetDatabase)].dbo.Store AS target
JOIN [$(SourceDatabase)].dbo.Store AS source
  ON target.Store_ID = CONVERT(nvarchar(20), source.Store_ID)
WHERE source.Store_ID = N'$(CentralStoreId)';

INSERT INTO [$(TargetDatabase)].dbo.Store
    (Store_ID, Store_Name, Address_Info, Address_Info_1, Address_Info_2, Email)
SELECT CONVERT(nvarchar(20), source.Store_ID), source.Store_Name,
       source.Address_Info, source.Address_Info_1, source.Address_Info_2,
       source.Email
FROM [$(SourceDatabase)].dbo.Store AS source
WHERE source.Store_ID = N'$(CentralStoreId)'
  AND NOT EXISTS (
      SELECT 1
      FROM [$(TargetDatabase)].dbo.Store AS target
      WHERE target.Store_ID = CONVERT(nvarchar(20), source.Store_ID)
  );

-- Vendors are shared reference data across Central and stores.
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
FROM [$(TargetDatabase)].dbo.Vendors AS target
JOIN [$(SourceDatabase)].dbo.Vendors AS source
  ON target.Vendor_Number = LTRIM(RTRIM(source.Vendor_Number));

INSERT INTO [$(TargetDatabase)].dbo.Vendors
    (Vendor_Number, First_Name, Last_Name, Company, Address_1, Address_2,
     City, State, Zip_Code, Phone, Fax, Country, Email)
SELECT LTRIM(RTRIM(source.Vendor_Number)), source.First_Name, source.Last_Name,
       source.Company, source.Address_1, source.Address_2, source.City,
       source.State, source.Zip_Code, source.Phone, source.Fax,
       source.Country, source.Email
FROM [$(SourceDatabase)].dbo.Vendors AS source
WHERE NOT EXISTS (
    SELECT 1
    FROM [$(TargetDatabase)].dbo.Vendors AS target
    WHERE target.Vendor_Number = LTRIM(RTRIM(source.Vendor_Number))
);

-- The app schema stores departments as shared references. Store 9999 is the
-- authoritative Central description; the Aurora sync may add store-only IDs.
UPDATE target
SET target.Description = source.Description,
    target.Type = CONVERT(nvarchar(50), source.Type)
FROM [$(TargetDatabase)].dbo.Departments AS target
JOIN [$(SourceDatabase)].dbo.Departments AS source
  ON target.Dept_ID = LTRIM(RTRIM(source.Dept_ID))
WHERE source.Store_ID = N'$(CentralStoreId)';

INSERT INTO [$(TargetDatabase)].dbo.Departments (Dept_ID, Description, Type)
SELECT LTRIM(RTRIM(source.Dept_ID)), source.Description,
       CONVERT(nvarchar(50), source.Type)
FROM [$(SourceDatabase)].dbo.Departments AS source
WHERE source.Store_ID = N'$(CentralStoreId)'
  AND NOT EXISTS (
      SELECT 1
      FROM [$(TargetDatabase)].dbo.Departments AS target
      WHERE target.Dept_ID = LTRIM(RTRIM(source.Dept_ID))
  );

-- Replace only Central store-9999 master data. Other stores and all
-- application-owned tables remain untouched.
DELETE FROM [$(TargetDatabase)].dbo.Inventory_Vendors
WHERE Store_ID = N'$(CentralStoreId)';

DELETE FROM [$(TargetDatabase)].dbo.Inventory
WHERE Store_ID = N'$(CentralStoreId)';

DELETE FROM [$(TargetDatabase)].dbo.vw_AllInventory
WHERE Store_ID = N'$(CentralStoreId)';

INSERT INTO [$(TargetDatabase)].dbo.Inventory
    (ItemNum, ItemName, In_Stock, Dept_ID, Vendor_Number, Store_ID,
     Vendor_Part_Num)
SELECT source.ItemNum, source.ItemName,
       CONVERT(numeric(19,2), source.In_Stock),
       NULLIF(LTRIM(RTRIM(source.Dept_ID)), N''),
       NULLIF(LTRIM(RTRIM(source.Vendor_Number)), N''),
       N'$(CentralStoreId)', source.Vendor_Part_Num
FROM [$(SourceDatabase)].dbo.Inventory AS source
WHERE source.Store_ID = N'$(CentralStoreId)'
  AND ISNULL(source.IsDeleted, 0) = 0
  AND ISNULL(source.Inactive, 0) = 0;

SET @InventoryRows = @@ROWCOUNT;

INSERT INTO [$(TargetDatabase)].dbo.vw_AllInventory
    (ItemNum, Store_ID, Vendor_Number, Vendor_Part_Num, In_Stock, ItemName)
SELECT source.ItemNum, N'$(CentralStoreId)',
       NULLIF(LTRIM(RTRIM(source.Vendor_Number)), N''),
       source.Vendor_Part_Num, CONVERT(numeric(19,2), source.In_Stock),
       source.ItemName
FROM [$(SourceDatabase)].dbo.Inventory AS source
WHERE source.Store_ID = N'$(CentralStoreId)'
  AND ISNULL(source.IsDeleted, 0) = 0
  AND ISNULL(source.Inactive, 0) = 0;

;WITH ranked_mappings AS (
    SELECT mapping.ItemNum,
           LTRIM(RTRIM(mapping.Vendor_Number)) AS Vendor_Number,
           mapping.Vendor_Part_Num,
           mapping.CostPer,
           mapping.Case_Cost,
           mapping.NumPerVenCase,
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
    WHERE mapping.Store_ID = N'$(CentralStoreId)'
      AND ISNULL(inventory.IsDeleted, 0) = 0
      AND ISNULL(inventory.Inactive, 0) = 0
)
INSERT INTO [$(TargetDatabase)].dbo.Inventory_Vendors
    (ItemNum, Store_ID, Vendor_Number, Vendor_Part_Num, costPer,
     Case_Cost, NumPerVenCase)
SELECT ItemNum, N'$(CentralStoreId)', Vendor_Number, Vendor_Part_Num,
       CONVERT(nvarchar(50), CostPer), CONVERT(nvarchar(50), Case_Cost),
       CONVERT(nvarchar(50), NumPerVenCase)
FROM ranked_mappings
WHERE mapping_rank = 1;

SET @VendorMappingRows = @@ROWCOUNT;

-- The standardized Central vendor catalog is authoritative as a complete
-- snapshot and is what links store vendor-part numbers to Central products.
DELETE FROM [$(TargetDatabase)].dbo.Inventory_Vendors_v1;

INSERT INTO [$(TargetDatabase)].dbo.Inventory_Vendors_v1
    (Vendor_Number, Vendor_Part_Num, Category, UPC_Code, CreateDate,
     ModifiedDate, ItemName, CostPer, WeightCost, Case_Cost,
     NumPerVenCase, Status)
SELECT LTRIM(RTRIM(source.Vendor_Number)), source.Vendor_Part_Num,
       source.Category, source.UPC_Code, source.CreateDate,
       source.ModifiedDate, source.ItemName,
       CONVERT(nvarchar(50), source.CostPer),
       CONVERT(nvarchar(50), source.WeightCost),
       CONVERT(nvarchar(50), source.Case_Cost),
       CONVERT(nvarchar(50), source.NumPerVenCase), source.Status
FROM [$(SourceDatabase)].dbo.Inventory_Vendors_v1 AS source;

SET @VendorCatalogRows = @@ROWCOUNT;

COMMIT TRANSACTION;

SELECT N'$(SourceDatabase)' AS source_database,
       N'$(TargetDatabase)' AS target_database,
       N'$(CentralStoreId)' AS central_store_id,
       @InventoryRows AS active_inventory_rows,
       @VendorMappingRows AS vendor_mapping_rows,
       @VendorCatalogRows AS vendor_catalog_rows;
