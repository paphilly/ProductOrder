-- ============================================================================
-- Konark local simulation - schema
-- Creates the 'Central' database with all tables the app expects.
-- Table/column names match com.konark entities AND native SQL queries.
-- Safe to re-run (drops and recreates all tables).
-- ============================================================================

IF DB_ID('Central') IS NULL
    CREATE DATABASE Central;
GO
USE Central;
GO

-- Drop tables in dependency order (idempotent re-run)
IF OBJECT_ID('dbo.SW_ProductOrderItems') IS NOT NULL DROP TABLE dbo.SW_ProductOrderItems;
IF OBJECT_ID('dbo.SW_ProductOrders')     IS NOT NULL DROP TABLE dbo.SW_ProductOrders;
IF OBJECT_ID('dbo.vw_AllInvoiceItemized') IS NOT NULL DROP TABLE dbo.vw_AllInvoiceItemized;
IF OBJECT_ID('dbo.vw_AllInventory')       IS NOT NULL DROP TABLE dbo.vw_AllInventory;
IF OBJECT_ID('dbo.Inventory_Vendors_v1')  IS NOT NULL DROP TABLE dbo.Inventory_Vendors_v1;
IF OBJECT_ID('dbo.Inventory_Vendors')     IS NOT NULL DROP TABLE dbo.Inventory_Vendors;
IF OBJECT_ID('dbo.Inventory')             IS NOT NULL DROP TABLE dbo.Inventory;
IF OBJECT_ID('dbo.Inventory_SKUS')        IS NOT NULL DROP TABLE dbo.Inventory_SKUS;
IF OBJECT_ID('dbo.Inventory_SKUS1')       IS NOT NULL DROP TABLE dbo.Inventory_SKUS1;
IF OBJECT_ID('dbo.SW_VendorItems')        IS NOT NULL DROP TABLE dbo.SW_VendorItems;
IF OBJECT_ID('dbo.QB_Vendor')             IS NOT NULL DROP TABLE dbo.QB_Vendor;
IF OBJECT_ID('dbo.Bill')                  IS NOT NULL DROP TABLE dbo.Bill;
IF OBJECT_ID('dbo.Gift_Card_Trans')       IS NOT NULL DROP TABLE dbo.Gift_Card_Trans;
IF OBJECT_ID('dbo.Gift_Cards')            IS NOT NULL DROP TABLE dbo.Gift_Cards;
IF OBJECT_ID('dbo.Customer_Swipes')       IS NOT NULL DROP TABLE dbo.Customer_Swipes;
IF OBJECT_ID('dbo.Customer')              IS NOT NULL DROP TABLE dbo.Customer;
IF OBJECT_ID('dbo.Departments')           IS NOT NULL DROP TABLE dbo.Departments;
IF OBJECT_ID('dbo.Store')                 IS NOT NULL DROP TABLE dbo.Store;
IF OBJECT_ID('dbo.Vendors')               IS NOT NULL DROP TABLE dbo.Vendors;
IF OBJECT_ID('dbo.Konark_User')           IS NOT NULL DROP TABLE dbo.Konark_User;
GO

-- ===================== USERS =====================
CREATE TABLE dbo.Konark_User (
    Username nvarchar(100) NOT NULL PRIMARY KEY,
    Password nvarchar(255) NOT NULL,
    Active   nvarchar(10)  NULL,
    Role     nvarchar(50)  NULL,
    UserJson nvarchar(max) NULL,
    Email    nvarchar(255) NULL
);
GO

-- ===================== VENDORS =====================
CREATE TABLE dbo.Vendors (
    Vendor_Number nvarchar(50)  NOT NULL PRIMARY KEY,
    First_Name    nvarchar(100) NULL,
    Last_Name     nvarchar(100) NULL,
    Company       nvarchar(255) NULL,
    Address_1     nvarchar(255) NULL,
    Address_2     nvarchar(255) NULL,
    City          nvarchar(100) NULL,
    State         nvarchar(50)  NULL,
    Zip_Code      nvarchar(20)  NULL,
    Phone         nvarchar(30)  NULL,
    Fax           nvarchar(30)  NULL,
    Country       nvarchar(50)  NULL,
    Email         nvarchar(255) NULL
);
GO

-- ===================== STORES =====================
CREATE TABLE dbo.Store (
    Store_ID      nvarchar(20)  NOT NULL PRIMARY KEY,
    Store_Name    nvarchar(100) NULL,
    Address_Info  nvarchar(255) NULL,
    Address_Info_1 nvarchar(255) NULL,
    Address_Info_2 nvarchar(255) NULL,
    Email         nvarchar(255) NULL
);
GO

-- ===================== DEPARTMENTS =====================
CREATE TABLE dbo.Departments (
    Dept_ID     nvarchar(20)  NOT NULL PRIMARY KEY,
    Description nvarchar(255) NULL,
    Type        nvarchar(50)  NULL
);
GO

-- ===================== INVENTORY (master items per store) =====================
CREATE TABLE dbo.Inventory (
    ItemNum        nvarchar(50)  NOT NULL,
    ItemName       nvarchar(255) NULL,
    In_Stock       numeric(19,2) NULL,
    Dept_ID        nvarchar(20)  NULL,
    Vendor_Number  nvarchar(50)  NULL,
    Store_ID       nvarchar(20)  NOT NULL,
    Vendor_Part_Num nvarchar(50) NULL,
    CONSTRAINT PK_Inventory PRIMARY KEY (ItemNum, Store_ID)
);
GO

-- ===================== INVENTORY (per store view-equivalent) =====================
CREATE TABLE dbo.vw_AllInventory (
    ItemNum         nvarchar(50)  NOT NULL,
    Store_ID        nvarchar(20)  NOT NULL,
    Vendor_Number   nvarchar(50)  NULL,
    Vendor_Part_Num nvarchar(50)  NULL,
    In_Stock        numeric(19,2) NULL,
    ItemName        nvarchar(255) NULL,
    CONSTRAINT PK_vw_AllInventory PRIMARY KEY (ItemNum, Store_ID)
);
GO

-- ===================== INVENTORY_VENDORS (vendor item mappings) =====================
CREATE TABLE dbo.Inventory_Vendors (
    ItemNum        nvarchar(50) NOT NULL,
    Store_ID       nvarchar(20) NOT NULL,
    Vendor_Number  nvarchar(50) NOT NULL,
    Vendor_Part_Num nvarchar(50) NULL,
    costPer        nvarchar(50)  NULL,
    Case_Cost      nvarchar(50)  NULL,
    NumPerVenCase  nvarchar(50)  NULL,
    CONSTRAINT PK_Inventory_Vendors PRIMARY KEY (ItemNum, Store_ID, Vendor_Number)
);
GO

-- ===================== INVENTORY_VENDORS_v1 (vendor catalog) =====================
CREATE TABLE dbo.Inventory_Vendors_v1 (
    Vendor_Number   nvarchar(50)  NOT NULL,
    Vendor_Part_Num nvarchar(50)  NOT NULL,
    Category        nvarchar(100) NULL,
    UPC_Code        nvarchar(50)  NULL,
    CreateDate      datetime      NULL,
    ModifiedDate    datetime      NULL,
    ItemName        nvarchar(255) NULL,
    CostPer         nvarchar(50)  NULL,
    WeightCost      nvarchar(50)  NULL,
    Case_Cost       nvarchar(50)  NULL,
    NumPerVenCase   nvarchar(50)  NULL,
    Status          nvarchar(20)  NULL,
    CONSTRAINT PK_Inventory_Vendors_v1 PRIMARY KEY (Vendor_Number, Vendor_Part_Num)
);
GO

-- ===================== SW_VendorItems (legacy vendor items) =====================
CREATE TABLE dbo.SW_VendorItems (
    Vendor_Number   nvarchar(50)  NOT NULL,
    Vendor_Part_Num nvarchar(50)  NOT NULL,
    Category        nvarchar(100) NULL,
    UPC_Code        nvarchar(50)  NULL,
    CreateDate      datetime      NULL,
    ModifiedDate    datetime      NULL,
    ItemName        nvarchar(255) NULL,
    CostPer         nvarchar(50)  NULL,
    WeightCost      nvarchar(50)  NULL,
    Case_Cost       nvarchar(50)  NULL,
    NumPerVenCase   nvarchar(50)  NULL,
    Status          nvarchar(20)  NULL,
    CONSTRAINT PK_SW_VendorItems PRIMARY KEY (Vendor_Number, Vendor_Part_Num)
);
GO

-- ===================== SKU tables =====================
CREATE TABLE dbo.Inventory_SKUS (
    Store_ID nvarchar(20) NOT NULL PRIMARY KEY,
    ItemNum  nvarchar(50) NULL,
    AltSKU   nvarchar(50) NULL
);
GO
CREATE TABLE dbo.Inventory_SKUS1 (
    Store_ID nvarchar(20) NOT NULL PRIMARY KEY,
    ItemNum  nvarchar(50) NULL,
    AltSKU   nvarchar(50) NULL
);
GO

-- ===================== PRODUCT ORDERS =====================
CREATE TABLE dbo.SW_ProductOrders (
    OrderID      bigint IDENTITY(1,1) NOT NULL PRIMARY KEY,
    Vendor_Number nvarchar(50) NULL,
    Store_ID     nvarchar(20) NULL,
    OrderDate    datetime NULL,
    TotalCost    numeric(19,2) NULL
);
GO
CREATE TABLE dbo.SW_ProductOrderItems (
    OrderItemID     bigint IDENTITY(1,1) NOT NULL PRIMARY KEY,
    OrderID         bigint NOT NULL,
    Vendor_Part_Num nvarchar(50) NULL,
    ItemNum         nvarchar(50) NULL,
    ItemName        nvarchar(255) NULL,
    QtyOrdered      nvarchar(50) NULL,
    CaseOrIndividual nvarchar(50) NULL,
    NumPerCase      nvarchar(50) NULL,
    CONSTRAINT FK_SW_ProductOrderItems_Order FOREIGN KEY (OrderID) REFERENCES dbo.SW_ProductOrders(OrderID)
);
GO

-- ===================== BILLS (accounts payable) =====================
CREATE TABLE dbo.Bill (
    Id               nvarchar(50)  NOT NULL PRIMARY KEY,
    DocNumber        nvarchar(50)  NULL,
    TxnDate          datetime      NULL,
    PrivateNote      nvarchar(max) NULL,
    VendorRefId      nvarchar(50)  NULL,
    VendorRefName    nvarchar(255) NULL,
    TotalAmt         numeric(19,2) NULL,
    DueDate          datetime      NULL,
    Balance          numeric(19,2) NULL,
    Status           nvarchar(20)  NULL,
    OverrideDueDate  datetime      NULL,
    SalesTermRefName nvarchar(100) NULL
);
GO

-- ===================== QB VENDOR (QuickBooks vendor refs) =====================
CREATE TABLE dbo.QB_Vendor (
    VendorRefId   nvarchar(50)  NOT NULL,
    StoreId       nvarchar(20)  NOT NULL,
    VendorRefName nvarchar(255) NULL,
    StoreName     nvarchar(100) NULL,
    res1          nvarchar(255) NULL,  -- used by native email lookup query
    CONSTRAINT PK_QB_Vendor PRIMARY KEY (VendorRefId, StoreId)
);
GO

-- ===================== GIFT CARDS =====================
CREATE TABLE dbo.Gift_Cards (
    Card_ID nvarchar(50)  NOT NULL PRIMARY KEY,
    Balance numeric(19,2) NULL
);
GO
CREATE TABLE dbo.Gift_Card_Trans (
    Card_ID       nvarchar(50) NOT NULL,
    TransType     int          NULL,   -- 0 = Load, else Redeem
    Amt           numeric(19,2) NULL,
    Store_ID      nvarchar(20) NULL,
    Invoice_Number bigint      NULL,
    DateTimeStamp datetime     NULL
);
GO

-- ===================== CUSTOMERS =====================
CREATE TABLE dbo.Customer (
    CustNum    nvarchar(50)  NOT NULL PRIMARY KEY,
    First_Name nvarchar(100) NULL,
    Last_Name  nvarchar(100) NULL
);
GO
CREATE TABLE dbo.Customer_Swipes (
    CustNum  nvarchar(50) NOT NULL,
    Swipe_ID nvarchar(50) NOT NULL,
    CONSTRAINT PK_Customer_Swipes PRIMARY KEY (CustNum, Swipe_ID)
);
GO

-- ===================== INVOICE ITEMIZED (sales history) =====================
CREATE TABLE dbo.vw_AllInvoiceItemized (
    Invoice_Number nvarchar(50)  NOT NULL,
    LineNum        nvarchar(20)  NOT NULL,
    Store_ID       nvarchar(20)  NOT NULL,
    ItemNum        nvarchar(50)  NULL,
    Quantity       numeric(19,2) NULL,
    CostPer        numeric(19,2) NULL,
    PricePer       numeric(19,2) NULL,
    DiffItemName   nvarchar(255) NULL,
    ModifiedDate   datetime      NULL,
    CONSTRAINT PK_vw_AllInvoiceItemized PRIMARY KEY (Invoice_Number, LineNum, Store_ID)
);
GO

PRINT 'Schema created OK';
GO
