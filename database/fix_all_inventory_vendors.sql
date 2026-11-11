CREATE OR ALTER VIEW dbo.vw_AllInventoryVendors AS
SELECT ItemNum, Store_ID, Vendor_Number, CostPer, Case_Cost, NumPerVenCase, Vendor_Part_Num FROM Central.dbo.Inventory_Vendors
UNION ALL SELECT ItemNum, Store_ID, Vendor_Number, CostPer, Case_Cost, NumPerVenCase, Vendor_Part_Num FROM Aurora.dbo.Inventory_Vendors
UNION ALL SELECT ItemNum, Store_ID, Vendor_Number, CostPer, Case_Cost, NumPerVenCase, Vendor_Part_Num FROM Denver.dbo.Inventory_Vendors
UNION ALL SELECT ItemNum, Store_ID, Vendor_Number, CostPer, Case_Cost, NumPerVenCase, Vendor_Part_Num FROM Desmoines.dbo.Inventory_Vendors
UNION ALL SELECT ItemNum, Store_ID, Vendor_Number, CostPer, Case_Cost, NumPerVenCase, Vendor_Part_Num FROM Maple.dbo.Inventory_Vendors
UNION ALL SELECT ItemNum, Store_ID, Vendor_Number, CostPer, Case_Cost, NumPerVenCase, Vendor_Part_Num FROM Namaste.dbo.Inventory_Vendors
UNION ALL SELECT ItemNum, Store_ID, Vendor_Number, CostPer, Case_Cost, NumPerVenCase, Vendor_Part_Num FROM Omaha.dbo.Inventory_Vendors
UNION ALL SELECT ItemNum, Store_ID, Vendor_Number, CostPer, Case_Cost, NumPerVenCase, Vendor_Part_Num FROM Parker.dbo.Inventory_Vendors
UNION ALL SELECT ItemNum, Store_ID, Vendor_Number, CostPer, Case_Cost, NumPerVenCase, Vendor_Part_Num FROM SLT.dbo.Inventory_Vendors;
