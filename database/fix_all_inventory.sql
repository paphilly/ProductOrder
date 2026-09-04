CREATE OR ALTER VIEW dbo.vw_AllInventory AS
SELECT ItemNum, Store_ID, Vendor_Number, Vendor_Part_Num, In_Stock, ItemName, Dept_ID FROM Central.dbo.Inventory
UNION ALL SELECT ItemNum, Store_ID, Vendor_Number, Vendor_Part_Num, In_Stock, ItemName, Dept_ID FROM Aurora.dbo.Inventory
UNION ALL SELECT ItemNum, Store_ID, Vendor_Number, Vendor_Part_Num, In_Stock, ItemName, Dept_ID FROM Charlotte.dbo.Inventory
UNION ALL SELECT ItemNum, Store_ID, Vendor_Number, Vendor_Part_Num, In_Stock, ItemName, Dept_ID FROM Denver.dbo.Inventory
UNION ALL SELECT ItemNum, Store_ID, Vendor_Number, Vendor_Part_Num, In_Stock, ItemName, Dept_ID FROM Desmoines.dbo.Inventory
UNION ALL SELECT ItemNum, Store_ID, Vendor_Number, Vendor_Part_Num, In_Stock, ItemName, Dept_ID FROM Maple.dbo.Inventory
UNION ALL SELECT ItemNum, Store_ID, Vendor_Number, Vendor_Part_Num, In_Stock, ItemName, Dept_ID FROM Namaste.dbo.Inventory
UNION ALL SELECT ItemNum, Store_ID, Vendor_Number, Vendor_Part_Num, In_Stock, ItemName, Dept_ID FROM Omaha.dbo.Inventory
UNION ALL SELECT ItemNum, Store_ID, Vendor_Number, Vendor_Part_Num, In_Stock, ItemName, Dept_ID FROM Parker.dbo.Inventory
UNION ALL SELECT ItemNum, Store_ID, Vendor_Number, Vendor_Part_Num, In_Stock, ItemName, Dept_ID FROM SLT.dbo.Inventory;
