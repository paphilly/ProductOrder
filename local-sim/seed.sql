-- ============================================================================
-- Konark local simulation - seed data (enriched)
-- Realistic test data for all screens. Run AFTER schema.sql against Central.
-- Idempotent: deletes existing rows first.
-- ============================================================================
USE Central;
GO

DELETE FROM dbo.SW_ProductOrderItems;
DELETE FROM dbo.SW_ProductOrders;
DELETE FROM dbo.vw_AllInvoiceItemized;
DELETE FROM dbo.vw_AllInventory;
DELETE FROM dbo.Inventory_Vendors_v1;
DELETE FROM dbo.Inventory_Vendors;
DELETE FROM dbo.Inventory;
DELETE FROM dbo.Inventory_SKUS;
DELETE FROM dbo.Inventory_SKUS1;
DELETE FROM dbo.SW_VendorItems;
DELETE FROM dbo.QB_Vendor;
DELETE FROM dbo.Bill;
DELETE FROM dbo.Gift_Card_Trans;
DELETE FROM dbo.Gift_Cards;
DELETE FROM dbo.Customer_Swipes;
DELETE FROM dbo.Customer;
DELETE FROM dbo.Departments;
DELETE FROM dbo.Store;
DELETE FROM dbo.Vendors;
DELETE FROM dbo.Konark_User;
GO

-- ===================== STORES =====================
-- Store_ID values match the hardcoded map returned by StoreController
INSERT INTO dbo.Store (Store_ID, Store_Name, Address_Info, Address_Info_1, Address_Info_2, Email) VALUES
('1234','Omaha','5818 S 108th St','Omaha','NE 68137','omaha@konarkgrocers.com'),
('2345','Namaste','2221 Q St','Omaha','NE 68107','namaste@konarkgrocers.com'),
('3456','Desmoines','2601 E Euclid Ave','Des Moines','IA 50317','desmoines@konarkgrocers.com'),
('4567','Maple','6505 Maple St','Omaha','NE 68104','maple@konarkgrocers.com'),
('5678','Denver','601 S Holly St','Denver','CO 80246','denver@konarkgrocers.com'),
('6789','Parker','10807 S Parker Rd','Parker','CO 80134','parker@konarkgrocers.com'),
('7890','Aurora','14091 E Mississippi Ave','Aurora','CO 80012','aurora@konarkgrocers.com'),
('8901','Charlotte','6801 N Tryon St','Charlotte','NC 28213','charlotte@konarkgrocers.com'),
('9012','StLouis','10300 Watson Rd','St. Louis','MO 63127','stlouis@konarkgrocers.com');
GO

-- ===================== USERS =====================
-- Passwords are stored in plain text (the app compares them directly).
--   admin / admin123  -> full access
--   user  / user123   -> restricted access
INSERT INTO dbo.Konark_User (Username, Password, Active, Role, UserJson, Email) VALUES
('admin', 'admin123', 'true', 'Admin',
 '{"firstname":"Alex","lastname":"Admin","email":"admin@konarkgrocers.com","role":"Admin",' +
 '"stores":{"Omaha":"1234","Denver":"5678","Aurora":"7890","Desmoines":"3456"},' +
 '"locations":{"Omaha":"Omaha","Denver":"Denver"},' +
 '"pages":["Order Placement","Accounts Payable","Store Dashboard","Gift Card","Vendor Management"]}',
 'admin@konarkgrocers.com'),
('user', 'user123', 'true', 'User',
 '{"firstname":"Priya","lastname":"User","email":"user@konarkgrocers.com","role":"User",' +
 '"stores":{"Omaha":"1234","Denver":"5678"},' +
 '"locations":{"Omaha":"Omaha"},' +
 '"pages":["Order Placement","Store Dashboard","Gift Card"]}',
 'user@konarkgrocers.com');
GO

-- ===================== DEPARTMENTS =====================
INSERT INTO dbo.Departments (Dept_ID, Description, Type) VALUES
('D01','Produce','Perishable'),
('D02','Dairy & Eggs','Perishable'),
('D03','Bakery','Perishable'),
('D04','Beverages','Grocery'),
('D05','Snacks','Grocery'),
('D06','Canned & Dry Goods','Grocery'),
('D07','Frozen Foods','Frozen'),
('D08','Meat & Seafood','Perishable');
GO

-- ===================== VENDORS =====================
INSERT INTO dbo.Vendors (Vendor_Number, First_Name, Last_Name, Company, Address_1, Address_2, City, State, Zip_Code, Phone, Fax, Country, Email) VALUES
('V0001','John','Carter','Fresh Farms Produce','1101 Market St','Suite 200','Omaha','NE','68102','402-555-0110','402-555-0111','USA','sales@freshfarmsproduce.com'),
('V0002','Sarah','Kim','Kraft Heinz Distribution','2200 N Mason Ave','Bldg 3, Dock 12','Chicago','IL','60639','312-555-0142','312-555-0143','USA','orders@kraftheinz.com'),
('V0003','Mike','Rossi','PepsiCo Beverages','700 Anderson Hill Rd','Floor 4','Purchase','NY','10577','914-555-0177','914-555-0178','USA','b2b@pepsico.com'),
('V0004','Lisa','Nguyen','Coca-Cola Refreshments','1 Coca-Cola Plz','NW Campus','Atlanta','GA','30313','404-555-0199','404-555-0198','USA','orders@coca-cola.com'),
('V0005','David','Brown','Kellogg Sales','1 Kellogg Sq','PO Box 3599','Battle Creek','MI','49016','269-555-0133','269-555-0134','USA','csr@kellogg.com'),
('V0006','Ana','Garcia','Dole Fresh Vegetables','1 Dole Dr','Westlake Village','Westlake Village','CA','91362','805-555-0161','805-555-0162','USA','sales@dole.com'),
('V0007','Tom','Wilson','Tyson Foods','2200 Don Tyson Pkwy','Corporate HQ','Springdale','AR','72762','479-555-0188','479-555-0189','USA','orders@tyson.com'),
('V0008','Maria','Santos','Bimbo Bakeries USA','1 Bimbo Pl','Bakery District','Fort Worth','TX','76102','817-555-0121','817-555-0122','USA','sales@bimbobakeries.com'),
('V0009','James','Lee','Nestle Frozen Foods','1812 N Moore St','Suite 900','Arlington','VA','22209','703-555-0150','703-555-0151','USA','frozen@nestleusa.com');
GO

-- ===================== ITEM MASTER =====================
-- CostPer = per-unit price, WeightCost = price per lb/kg, Case_Cost = price per vendor case,
-- NumPerVenCase = units per case. Temp table (#Items) persists across GO batches.
IF OBJECT_ID('tempdb..#Items') IS NOT NULL DROP TABLE #Items;
CREATE TABLE #Items (
    ItemNum nvarchar(50), ItemName nvarchar(255), Dept_ID nvarchar(20),
    Vendor_Number nvarchar(50), Vendor_Part_Num nvarchar(50), Category nvarchar(100), UPC_Code nvarchar(50),
    CostPer nvarchar(20), WeightCost nvarchar(20), Case_Cost nvarchar(20), NumPerVenCase nvarchar(20),
    StockOmaha numeric(19,2), StockDenver numeric(19,2)
);
INSERT INTO #Items VALUES
-- Produce (V0001 Fresh Farms, V0006 Dole)
('1001','Bananas','D01','V0001','BAN-01','Produce','04010010011','0.46','0.46','18.50','40',120.00, 95.00),
('1002','Gala Apples 3lb Bag','D01','V0001','APL-02','Produce','04010010021','0.71','2.13','21.25','30',85.00, 60.00),
('1003','Roma Tomatoes 2lb','D01','V0001','TOM-03','Produce','04010010031','0.60','1.20','14.90','25',70.00, 45.00),
('1004','Baby Spinach 1lb','D01','V0006','SPN-01','Produce','04010010041','1.84','1.84','22.10','12',55.00, 40.00),
('1005','Romaine Lettuce','D01','V0006','ROM-02','Produce','04010010051','0.82','0.82','19.60','24',110.00, 80.00),
('1006','Red Bell Peppers 2lb Bag','D01','V0006','PEP-03','Produce','04010010061','1.98','0.99','15.84','8',60.00, 48.00),
-- Dairy & Eggs (V0002 Kraft Heinz)
('1007','Whole Milk 1 Gallon','D02','V0002','MLK-01','Dairy','04010020071','3.45','3.45','31.05','9',140.00, 120.00),
('1008','Large Brown Eggs 18ct','D02','V0002','EGG-02','Dairy','04010020081','4.85','4.85','58.20','12',90.00, 75.00),
('1009','American Cheese Slices 16oz','D02','V0002','CHZ-03','Dairy','04010020091','5.20','0.33','41.60','8',65.00, 50.00),
('1010','Unsalted Butter 16oz','D02','V0002','BTR-04','Dairy','04010020101','4.95','0.31','39.60','8',55.00, 42.00),
('1011','Plain Greek Yogurt 32oz','D02','V0002','YGT-05','Dairy','04010020111','4.25','0.13','25.50','6',48.00, 36.00),
-- Bakery (V0008 Bimbo Bakeries)
('1012','White Sandwich Bread 24oz','D03','V0008','BRD-01','Bakery','04010030121','2.89','2.89','34.68','12',95.00, 78.00),
('1013','Hamburger Buns 8ct','D03','V0008','HMB-02','Bakery','04010030131','2.49','2.49','29.88','12',70.00, 58.00),
('1014','Plain Bagels 6ct','D03','V0008','BGL-03','Bakery','04010030141','3.29','3.29','39.48','12',45.00, 35.00),
('1015','Butter Croissants 4ct','D03','V0008','CRS-04','Bakery','04010030151','4.49','4.49','26.94','6',30.00, 24.00),
-- Beverages (V0003 PepsiCo, V0004 Coca-Cola)
('1016','Cola 12oz 24-Pack','D04','V0004','COL-01','Beverages','04010040161','8.99','0.31','35.96','4',200.00, 180.00),
('1017','Diet Cola 12oz 24-Pack','D04','V0004','DCO-02','Beverages','04010040171','8.99','0.31','35.96','4',150.00, 130.00),
('1018','Orange Soda 12oz 24-Pack','D04','V0004','ORS-03','Beverages','04010040181','7.49','0.26','29.96','4',80.00, 60.00),
('1019','Cola 2L Bottle','D04','V0004','COL-2L','Beverages','04010040191','2.19','0.65','26.28','12',300.00, 250.00),
('1020','Mountain Dew 12-Pack','D04','V0003','DEW-01','Beverages','04010040201','8.49','0.30','33.96','4',95.00, 85.00),
('1021','Pepsi 12-Pack','D04','V0003','PPS-02','Beverages','04010040211','8.49','0.30','33.96','4',100.00, 90.00),
('1022','Gatorade 32oz','D04','V0003','GAT-03','Beverages','04010040221','2.49','0.08','14.94','6',180.00, 160.00),
('1023','Spring Water 24-Pack','D04','V0003','WTR-04','Beverages','04010040231','6.99','0.29','27.96','4',260.00, 220.00),
-- Snacks (V0005 Kellogg)
('1024','Potato Chips Original 8oz','D05','V0005','CHP-01','Snacks','04010050241','4.29','0.54','34.32','8',160.00, 140.00),
('1025','Corn Flakes 18oz','D05','V0005','CRN-01','Snacks','04010050251','4.79','0.27','28.74','6',75.00, 65.00),
('1026','Frosted Flakes 19oz','D05','V0005','FRF-02','Snacks','04010050261','4.99','0.26','29.94','6',68.00, 55.00),
('1027','Granola Bars 12ct','D05','V0005','GRN-03','Snacks','04010050271','5.49','0.46','32.94','6',90.00, 72.00),
('1028','Cheez-It Snack Crackers 12.4oz','D05','V0005','CHK-04','Snacks','04010050281','4.39','0.35','26.34','6',85.00, 70.00),
-- Canned & Dry Goods (V0002, V0005)
('1029','Canned Corn 15oz','D06','V0005','CNC-02','Canned','04010060291','1.29','1.29','15.48','12',130.00, 110.00),
('1030','Spaghetti Pasta 16oz','D06','V0002','PST-01','Dry Goods','04010060301','1.49','1.49','17.88','12',145.00, 125.00),
('1031','Marinara Sauce 24oz','D06','V0002','MRN-02','Dry Goods','04010060311','3.29','3.29','13.16','4',60.00, 45.00),
('1032','White Rice 5lb','D06','V0002','RIC-03','Dry Goods','04010060321','6.99','1.40','33.55','4',52.00, 40.00),
-- Frozen (V0009 Nestle)
('1033','Frozen Cheese Pizza 12in','D07','V0009','PZZ-01','Frozen','04010070331','5.99','5.99','35.94','6',42.00, 34.00),
('1034','Vanilla Ice Cream 48oz','D07','V0009','ICM-02','Frozen','04010070341','5.49','5.49','32.94','6',38.00, 30.00),
('1035','Frozen Mixed Vegetables 16oz','D07','V0009','FVG-03','Frozen','04010070351','2.19','2.19','26.28','12',50.00, 40.00),
('1036','Chicken Nuggets 32oz','D07','V0009','NUG-04','Frozen','04010070361','8.99','8.99','53.94','6',36.00, 28.00),
('1037','Frozen French Fries 32oz','D07','V0009','FRY-05','Frozen','04010070371','3.49','3.49','41.88','12',44.00, 35.00),
-- Meat & Seafood (V0007 Tyson)
('1038','Chicken Thighs 4lb','D08','V0007','CHK-01','Meat','04010080381','10.99','2.75','43.96','4',70.00, 55.00),
('1039','Ground Beef 80/20 3lb','D08','V0007','GBD-02','Meat','04010080391','13.49','4.50','26.98','2',45.00, 35.00),
('1040','Whole Chicken 4lb','D08','V0007','WHC-03','Meat','04010080401','8.99','2.25','35.96','4',55.00, 42.00),
('1041','Pork Chops 2lb','D08','V0007','PRK-04','Meat','04010080411','7.99','4.00','31.96','4',40.00, 32.00),
('1042','Atlantic Salmon Fillet 1lb','D08','V0007','SLM-05','Seafood','04010080421','11.99','11.99','47.96','4',25.00, 18.00);
GO

-- ===================== STORE STOCK FACTORS =====================
-- Every store gets inventory so Order Placement and the dashboard work for all locations.
IF OBJECT_ID('tempdb..#Stores') IS NOT NULL DROP TABLE #Stores;
CREATE TABLE #Stores (Store_ID nvarchar(20) NOT NULL, StockFactor numeric(5,2) NOT NULL);
INSERT INTO #Stores (Store_ID, StockFactor) VALUES
('1234', 1.00),  -- Omaha (uses StockOmaha values)
('2345', 0.85),  -- Namaste
('3456', 1.10),  -- Desmoines
('4567', 0.90),  -- Maple
('5678', 1.00),  -- Denver (uses StockDenver values)
('6789', 0.95),  -- Parker
('7890', 0.80),  -- Aurora
('8901', 1.05),  -- Charlotte
('9012', 0.90);  -- StLouis
GO

-- Inventory master rows (per store) - all 9 stores
INSERT INTO dbo.Inventory (ItemNum, ItemName, In_Stock, Dept_ID, Vendor_Number, Store_ID, Vendor_Part_Num)
SELECT i.ItemNum, i.ItemName,
       CASE WHEN s.Store_ID = '1234' THEN i.StockOmaha ELSE i.StockDenver * s.StockFactor END,
       i.Dept_ID, i.Vendor_Number, s.Store_ID, i.Vendor_Part_Num
FROM #Items i
CROSS JOIN #Stores s;
GO

-- vw_AllInventory (per store view-equivalent) - all 9 stores
INSERT INTO dbo.vw_AllInventory (ItemNum, Store_ID, Vendor_Number, Vendor_Part_Num, In_Stock, ItemName)
SELECT i.ItemNum, s.Store_ID, i.Vendor_Number, i.Vendor_Part_Num,
       CASE WHEN s.Store_ID = '1234' THEN i.StockOmaha ELSE i.StockDenver * s.StockFactor END,
       i.ItemName
FROM #Items i
CROSS JOIN #Stores s;
GO

-- Inventory_Vendors (vendor item mappings per store) - all 9 stores
INSERT INTO dbo.Inventory_Vendors (ItemNum, Store_ID, Vendor_Number, Vendor_Part_Num, costPer, Case_Cost, NumPerVenCase)
SELECT i.ItemNum, s.Store_ID, i.Vendor_Number, i.Vendor_Part_Num, i.CostPer, i.Case_Cost, i.NumPerVenCase
FROM #Items i
CROSS JOIN #Stores s;
GO

-- Inventory_Vendors_v1 (vendor catalog - powers Vendor Management & Order Placement)
INSERT INTO dbo.Inventory_Vendors_v1 (Vendor_Number, Vendor_Part_Num, Category, UPC_Code, CreateDate, ModifiedDate, ItemName, CostPer, WeightCost, Case_Cost, NumPerVenCase, Status)
SELECT i.Vendor_Number, i.Vendor_Part_Num, i.Category, i.UPC_Code, DATEADD(DAY,-180,GETDATE()), DATEADD(DAY,-2,GETDATE()),
       i.ItemName, i.CostPer, i.WeightCost, i.Case_Cost, i.NumPerVenCase,
       CASE WHEN i.ItemNum IN ('1005','1014','1028') THEN 'Inactive' ELSE 'Active' END
FROM #Items i;
GO

-- SW_VendorItems (legacy vendor items)
INSERT INTO dbo.SW_VendorItems (Vendor_Number, Vendor_Part_Num, Category, UPC_Code, CreateDate, ModifiedDate, ItemName, CostPer, WeightCost, Case_Cost, NumPerVenCase, Status)
SELECT i.Vendor_Number, i.Vendor_Part_Num, i.Category, i.UPC_Code, DATEADD(DAY,-180,GETDATE()), DATEADD(DAY,-2,GETDATE()),
       i.ItemName, i.CostPer, i.WeightCost, i.Case_Cost, i.NumPerVenCase,
       CASE WHEN i.ItemNum IN ('1005','1014','1028') THEN 'Inactive' ELSE 'Active' END
FROM #Items i;
GO

-- ===================== ALTERNATE VENDORS (Vendor Reference) =====================
-- Some products are carried by TWO vendors so the "Vendor Reference" panel in
-- Order Placement has data: it shows other vendors selling the same/similar item.
-- (e.g. Dole + Fresh Farms both carry produce, PepsiCo + Coca-Cola both carry soda.)

-- Inventory_Vendors: add the alternate vendor mapping for the same item per store
INSERT INTO dbo.Inventory_Vendors (ItemNum, Store_ID, Vendor_Number, Vendor_Part_Num, costPer, Case_Cost, NumPerVenCase)
SELECT a.ItemNum, s.Store_ID, a.Vendor_Number, a.Vendor_Part_Num, a.CostPer, a.Case_Cost, a.NumPerVenCase
FROM (
    VALUES
    ('1001','V0006','BAN-06','0.45','18.00','40'),   -- Bananas also via Dole
    ('1002','V0006','APL-06','0.69','20.70','30'),   -- Gala Apples also via Dole
    ('1004','V0001','SPN-11','1.79','21.48','12'),   -- Baby Spinach also via Fresh Farms
    ('1005','V0001','ROM-11','0.79','18.96','24'),   -- Romaine Lettuce also via Fresh Farms
    ('1006','V0001','PEP-11','1.92','15.36','8'),    -- Red Bell Peppers also via Fresh Farms
    ('1016','V0003','COL-31','8.79','35.16','4'),    -- Cola 12oz also via PepsiCo
    ('1017','V0003','DCO-31','8.79','35.16','4'),    -- Diet Cola 12oz also via PepsiCo
    ('1024','V0003','CHP-31','4.19','33.52','8')     -- Potato Chips also via PepsiCo
) a(ItemNum, Vendor_Number, Vendor_Part_Num, CostPer, Case_Cost, NumPerVenCase)
CROSS JOIN #Stores s;
GO

-- Inventory_Vendors_v1: alternate vendor catalog rows (powers Vendor Management + Reference)
INSERT INTO dbo.Inventory_Vendors_v1 (Vendor_Number, Vendor_Part_Num, Category, UPC_Code, CreateDate, ModifiedDate, ItemName, CostPer, WeightCost, Case_Cost, NumPerVenCase, Status)
SELECT a.Vendor_Number, a.Vendor_Part_Num, i.Category, i.UPC_Code, DATEADD(DAY,-180,GETDATE()), DATEADD(DAY,-2,GETDATE()),
       i.ItemName, a.CostPer, i.WeightCost, a.Case_Cost, a.NumPerVenCase, 'Active'
FROM #Items i
JOIN (
    VALUES
    ('1001','V0006','BAN-06','0.45','18.00','40'),
    ('1002','V0006','APL-06','0.69','20.70','30'),
    ('1004','V0001','SPN-11','1.79','21.48','12'),
    ('1005','V0001','ROM-11','0.79','18.96','24'),
    ('1006','V0001','PEP-11','1.92','15.36','8'),
    ('1016','V0003','COL-31','8.79','35.16','4'),
    ('1017','V0003','DCO-31','8.79','35.16','4'),
    ('1024','V0003','CHP-31','4.19','33.52','8')
) a(ItemNum, Vendor_Number, Vendor_Part_Num, CostPer, Case_Cost, NumPerVenCase) ON a.ItemNum = i.ItemNum;
GO

-- SW_VendorItems: same alternate vendor catalog rows (legacy table)
INSERT INTO dbo.SW_VendorItems (Vendor_Number, Vendor_Part_Num, Category, UPC_Code, CreateDate, ModifiedDate, ItemName, CostPer, WeightCost, Case_Cost, NumPerVenCase, Status)
SELECT a.Vendor_Number, a.Vendor_Part_Num, i.Category, i.UPC_Code, DATEADD(DAY,-180,GETDATE()), DATEADD(DAY,-2,GETDATE()),
       i.ItemName, a.CostPer, i.WeightCost, a.Case_Cost, a.NumPerVenCase, 'Active'
FROM #Items i
JOIN (
    VALUES
    ('1001','V0006','BAN-06','0.45','18.00','40'),
    ('1002','V0006','APL-06','0.69','20.70','30'),
    ('1004','V0001','SPN-11','1.79','21.48','12'),
    ('1005','V0001','ROM-11','0.79','18.96','24'),
    ('1006','V0001','PEP-11','1.92','15.36','8'),
    ('1016','V0003','COL-31','8.79','35.16','4'),
    ('1017','V0003','DCO-31','8.79','35.16','4'),
    ('1024','V0003','CHP-31','4.19','33.52','8')
) a(ItemNum, Vendor_Number, Vendor_Part_Num, CostPer, Case_Cost, NumPerVenCase) ON a.ItemNum = i.ItemNum;
GO

-- ===================== SKU TABLES =====================
INSERT INTO dbo.Inventory_SKUS (Store_ID, ItemNum, AltSKU) VALUES
('1234','1001','ALT-1001-1234'),
('2345','1002','ALT-1002-2345'),
('3456','1003','ALT-1003-3456'),
('4567','1004','ALT-1004-4567'),
('5678','1001','ALT-1001-5678'),
('6789','1005','ALT-1005-6789'),
('7890','1006','ALT-1006-7890'),
('8901','1007','ALT-1007-8901'),
('9012','1008','ALT-1008-9012');
INSERT INTO dbo.Inventory_SKUS1 (Store_ID, ItemNum, AltSKU) VALUES
('1234','1012','ALT-1012-1234'),
('2345','1013','ALT-1013-2345'),
('3456','1014','ALT-1014-3456'),
('4567','1015','ALT-1015-4567'),
('5678','1016','ALT-1016-5678'),
('6789','1017','ALT-1017-6789'),
('7890','1018','ALT-1018-7890'),
('8901','1019','ALT-1019-8901'),
('9012','1020','ALT-1020-9012');
GO

-- ===================== SALES HISTORY (vw_AllInvoiceItemized) =====================
-- Generates invoice line items across the last 30 days for ALL stores,
-- so the 7/15/30-day sales history and the store dashboard chart have data everywhere.
-- Invoice numbers are unique per item: 1000 + itemNum*100 + offset (< 100).
INSERT INTO dbo.vw_AllInvoiceItemized (Invoice_Number, LineNum, Store_ID, ItemNum, Quantity, CostPer, PricePer, DiffItemName, ModifiedDate)
SELECT CONCAT('INV-', 1000 + CAST(i.ItemNum AS INT) * 100 + o.OffsetNum), '1', s.Store_ID, i.ItemNum,
       (5 + (CAST(i.ItemNum AS INT) % 12)),
       CAST(i.CostPer AS numeric(19,2)),
       CAST(i.CostPer AS numeric(19,2)) * 2.5,
       i.ItemName,
       DATEADD(DAY, -o.OffsetNum, GETDATE())
FROM #Items i
CROSS JOIN (VALUES (1),(2),(3),(4),(5),(6),(7),(8),(9),(10),(12),(14),(16),(18),(21),(24),(28)) o(OffsetNum)
CROSS JOIN #Stores s;
GO

-- ===================== BILLS (accounts payable) =====================
-- TxnDate within the last 60 days so the DATEADD(DAY,-60,GETDATE()) filter matches.
-- Mix of statuses (Pending/Overdue computed when NULL, plus explicit Approved/Paid/Notified),
-- partial balances, override due dates, and one UNASSIGNED bill for the NULL-location path.
INSERT INTO dbo.Bill (Id, DocNumber, TxnDate, PrivateNote, VendorRefId, VendorRefName, TotalAmt, DueDate, Balance, Status, OverrideDueDate, SalesTermRefName) VALUES
('B-001','BILL-2026-1001',DATEADD(DAY,-5,GETDATE()),'Weekly produce delivery','QBV-101','Fresh Farms Produce',1850.00,DATEADD(DAY,25,GETDATE()),1850.00,NULL,NULL,'Omaha'),
('B-002','BILL-2026-1002',DATEADD(DAY,-10,GETDATE()),'Dairy order - follow up on payment terms','QBV-102','Kraft Heinz Distribution',2140.00,DATEADD(DAY,-15,GETDATE()),2140.00,NULL,NULL,'Omaha'),
('B-003','BILL-2026-1003',DATEADD(DAY,-12,GETDATE()),'Soda truckload - half paid, awaiting approval','QBV-104','Coca-Cola Refreshments',3250.00,DATEADD(DAY,20,GETDATE()),1625.00,'Approved',DATEADD(DAY,10,GETDATE()),'Denver'),
('B-004','BILL-2026-1004',DATEADD(DAY,-20,GETDATE()),'Snacks restock - overdue, escalated to manager','QBV-105','Kellogg Sales',980.00,DATEADD(DAY,-5,GETDATE()),980.00,NULL,NULL,'Denver'),
('B-005','BILL-2026-1005',DATEADD(DAY,-25,GETDATE()),'Beverage delivery - waiting on statement','QBV-103','PepsiCo Beverages',1510.00,DATEADD(DAY,10,GETDATE()),1510.00,'Pending',NULL,'Omaha'),
('B-006','BILL-2026-1006',DATEADD(DAY,-30,GETDATE()),'Meat shipment - 30 days net','QBV-107','Tyson Foods',2730.00,DATEADD(DAY,-20,GETDATE()),2730.00,NULL,NULL,'Denver'),
('B-007','BILL-2026-1007',DATEADD(DAY,-8,GETDATE()),'Paid in full','QBV-106','Dole Fresh Vegetables',620.00,DATEADD(DAY,30,GETDATE()),0.00,'Paid',NULL,'Omaha'),
('B-008','BILL-2026-1008',DATEADD(DAY,-15,GETDATE()),'Unassigned location bill','QBV-101','Fresh Farms Produce',440.00,DATEADD(DAY,5,GETDATE()),440.00,NULL,NULL,NULL),
('B-009','BILL-2026-1009',DATEADD(DAY,-2,GETDATE()),'Bakery weekly order','QBV-108','Bimbo Bakeries USA',890.00,DATEADD(DAY,28,GETDATE()),890.00,'Approved',NULL,'Omaha'),
('B-010','BILL-2026-1010',DATEADD(DAY,-35,GETDATE()),'Frozen goods - statement sent to vendor','QBV-109','Nestle Frozen Foods',1675.00,DATEADD(DAY,2,GETDATE()),1675.00,'Notified',NULL,'Denver'),
('B-011','BILL-2026-1011',DATEADD(DAY,-18,GETDATE()),'Partial payment received, balance remaining','QBV-103','PepsiCo Beverages',2200.00,DATEADD(DAY,-2,GETDATE()),800.00,'Approved',DATEADD(DAY,15,GETDATE()),'Omaha'),
('B-012','BILL-2026-1012',DATEADD(DAY,-1,GETDATE()),'Fresh vegetables - due soon','QBV-106','Dole Fresh Vegetables',750.00,DATEADD(DAY,21,GETDATE()),750.00,NULL,NULL,'Denver');
GO

-- ===================== QB VENDORS (refs + emails) =====================
INSERT INTO dbo.QB_Vendor (VendorRefId, StoreId, VendorRefName, StoreName, res1) VALUES
('QBV-101','1234','Fresh Farms Produce','Omaha','sales@freshfarmsproduce.com'),
('QBV-102','1234','Kraft Heinz Distribution','Omaha','orders@kraftheinz.com'),
('QBV-103','1234','PepsiCo Beverages','Omaha','b2b@pepsico.com'),
('QBV-104','5678','Coca-Cola Refreshments','Denver','orders@coca-cola.com'),
('QBV-105','5678','Kellogg Sales','Denver','csr@kellogg.com'),
('QBV-106','1234','Dole Fresh Vegetables','Omaha','sales@dole.com'),
('QBV-107','5678','Tyson Foods','Denver','orders@tyson.com'),
('QBV-108','1234','Bimbo Bakeries USA','Omaha','sales@bimbobakeries.com'),
('QBV-109','5678','Nestle Frozen Foods','Denver','frozen@nestleusa.com');
GO

-- ===================== GIFT CARDS =====================
INSERT INTO dbo.Gift_Cards (Card_ID, Balance) VALUES
('GC-100001', 75.50),
('GC-100002', 25.00),
('GC-100003', 120.00),
('GC-100004', 10.25),
('GC-100005', 0.00);
GO
INSERT INTO dbo.Gift_Card_Trans (Card_ID, TransType, Amt, Store_ID, Invoice_Number, DateTimeStamp) VALUES
('GC-100001', 0, 100.00, '1234', 9000001, DATEADD(DAY,-40,GETDATE())),
('GC-100001', 1, 24.50,  '1234', 9000002, DATEADD(DAY,-12,GETDATE())),
('GC-100001', 0, 25.00,  '5678', 9000003, DATEADD(DAY,-6,GETDATE())),
('GC-100001', 1, 25.00,  '5678', 9000004, DATEADD(DAY,-2,GETDATE())),
('GC-100002', 0, 25.00,  '1234', 9000005, DATEADD(DAY,-20,GETDATE())),
('GC-100002', 1, 10.00,  '1234', 9000006, DATEADD(DAY,-15,GETDATE())),
('GC-100002', 1, 5.00,   '1234', 9000007, DATEADD(DAY,-9,GETDATE())),
('GC-100003', 0, 50.00,  '3456', 9000008, DATEADD(DAY,-30,GETDATE())),
('GC-100003', 0, 70.00,  '3456', 9000009, DATEADD(DAY,-22,GETDATE())),
('GC-100003', 1, 15.00,  '4567', 9000010, DATEADD(DAY,-14,GETDATE())),
('GC-100003', 1, 35.00,  '4567', 9000011, DATEADD(DAY,-5,GETDATE())),
('GC-100004', 0, 20.00,  '5678', 9000012, DATEADD(DAY,-18,GETDATE())),
('GC-100004', 1, 9.75,   '5678', 9000013, DATEADD(DAY,-3,GETDATE())),
('GC-100005', 0, 50.00,  '1234', 9000014, DATEADD(DAY,-10,GETDATE())),
('GC-100005', 1, 50.00,  '1234', 9000015, DATEADD(DAY,-1,GETDATE()));
GO

-- ===================== CUSTOMERS =====================
-- Look up gift cards by mobile number: 4025550100 etc.
INSERT INTO dbo.Customer (CustNum, First_Name, Last_Name) VALUES
('4025550100','John','Doe'),
('4025550101','Jane','Smith'),
('4025550102','Robert','Chen'),
('4025550103','Maria','Lopez'),
('4025550104','Ahmed','Khan');
GO
INSERT INTO dbo.Customer_Swipes (CustNum, Swipe_ID) VALUES
('4025550100','GC-100001'),
('4025550101','GC-100002'),
('4025550102','GC-100003'),
('4025550103','GC-100004'),
('4025550104','GC-100005');
GO

-- ===================== SAMPLE PRODUCT ORDERS =====================
SET IDENTITY_INSERT dbo.SW_ProductOrders ON;
INSERT INTO dbo.SW_ProductOrders (OrderID, Vendor_Number, Store_ID, OrderDate, TotalCost) VALUES
(101,'V0001','1234',DATEADD(DAY,-3,GETDATE()),185.00),
(102,'V0004','5678',DATEADD(DAY,-1,GETDATE()),143.84),
(103,'V0008','1234',DATEADD(DAY,-7,GETDATE()),98.22),
(104,'V0009','5678',DATEADD(DAY,-2,GETDATE()),119.90);
SET IDENTITY_INSERT dbo.SW_ProductOrders OFF;

SET IDENTITY_INSERT dbo.SW_ProductOrderItems ON;
INSERT INTO dbo.SW_ProductOrderItems (OrderItemID, OrderID, Vendor_Part_Num, ItemNum, ItemName, QtyOrdered, CaseOrIndividual, NumPerCase) VALUES
(5001,101,'BAN-01','1001','Bananas','5','Case','40'),
(5002,101,'APL-02','1002','Gala Apples 3lb Bag','2','Case','30'),
(5003,101,'TOM-03','1003','Roma Tomatoes 2lb','3','Case','25'),
(5004,102,'COL-01','1016','Cola 12oz 24-Pack','4','Case','4'),
(5005,103,'BRD-01','1012','White Sandwich Bread 24oz','2','Case','12'),
(5006,103,'HMB-02','1013','Hamburger Buns 8ct','1','Case','12'),
(5007,104,'PZZ-01','1033','Frozen Cheese Pizza 12in','2','Case','6'),
(5008,104,'ICM-02','1034','Vanilla Ice Cream 48oz','1','Case','6');
SET IDENTITY_INSERT dbo.SW_ProductOrderItems OFF;
GO

DROP TABLE #Items;
DROP TABLE #Stores;

PRINT 'Seed data loaded OK';
GO
