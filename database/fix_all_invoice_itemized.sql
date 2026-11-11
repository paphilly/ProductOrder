CREATE OR ALTER VIEW dbo.vw_AllInvoiceItemized AS
SELECT Invoice_Number, LineNum, Store_ID, ItemNum, Quantity, CostPer, PricePer, DiffItemName, ModifiedDate FROM Desmoines.dbo.Invoice_Itemized
UNION ALL
SELECT Invoice_Number, LineNum, Store_ID, ItemNum, Quantity, CostPer, PricePer, DiffItemName, ModifiedDate FROM Aurora.dbo.Invoice_Itemized
UNION ALL
SELECT Invoice_Number, LineNum, Store_ID, ItemNum, Quantity, CostPer, PricePer, DiffItemName, ModifiedDate FROM Denver.dbo.Invoice_Itemized
UNION ALL
SELECT Invoice_Number, LineNum, Store_ID, ItemNum, Quantity, CostPer, PricePer, DiffItemName, ModifiedDate FROM Parker.dbo.Invoice_Itemized
UNION ALL
SELECT Invoice_Number, LineNum, Store_ID, ItemNum, Quantity, CostPer, PricePer, DiffItemName, ModifiedDate FROM Omaha.dbo.Invoice_Itemized
UNION ALL
SELECT Invoice_Number, LineNum, Store_ID, ItemNum, Quantity, CostPer, PricePer, DiffItemName, ModifiedDate FROM Maple.dbo.Invoice_Itemized
UNION ALL
SELECT Invoice_Number, LineNum, Store_ID, ItemNum, Quantity, CostPer, PricePer, DiffItemName, ModifiedDate FROM Namaste.dbo.Invoice_Itemized
UNION ALL
SELECT Invoice_Number, LineNum, Store_ID, ItemNum, Quantity, CostPer, PricePer, DiffItemName, ModifiedDate FROM SLT.dbo.Invoice_Itemized;
