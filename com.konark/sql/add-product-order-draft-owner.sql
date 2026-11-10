/* Run once in Central before deploying saved-draft retrieval. */
IF COL_LENGTH(N'dbo.SW_ProductOrders', N'CreatedBy') IS NULL
BEGIN
    ALTER TABLE dbo.SW_ProductOrders ADD CreatedBy nvarchar(100) NULL;
END;
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE object_id = OBJECT_ID(N'dbo.SW_ProductOrders')
      AND name = N'IX_SW_ProductOrders_CreatedBy_Status_Date'
)
BEGIN
    SET QUOTED_IDENTIFIER ON;
    CREATE INDEX IX_SW_ProductOrders_CreatedBy_Status_Date
        ON dbo.SW_ProductOrders (CreatedBy, OrderStatus, OrderDate DESC);
END;
GO
