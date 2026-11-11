/* Run once in Central before deploying the draft-order feature. */
IF COL_LENGTH(N'dbo.SW_ProductOrders', N'OrderStatus') IS NULL
BEGIN
    ALTER TABLE dbo.SW_ProductOrders
        ADD OrderStatus nvarchar(16) NOT NULL
            CONSTRAINT DF_SW_ProductOrders_OrderStatus DEFAULT N'COMMITTED' WITH VALUES;
END;
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE object_id = OBJECT_ID(N'dbo.SW_ProductOrders')
      AND name = N'IX_SW_ProductOrders_OrderStatus'
)
BEGIN
    SET QUOTED_IDENTIFIER ON;
    CREATE INDEX IX_SW_ProductOrders_OrderStatus
        ON dbo.SW_ProductOrders (OrderStatus)
        WHERE OrderStatus = N'DRAFT';
END;
GO
