/*
   Run once in Central before deploying the matching application build.
   Existing duplicate drafts are retained for audit, but only the newest
   draft per owner/store/vendor remains active. Older ones are marked
   SUPERSEDED, not deleted.
*/
SET XACT_ABORT ON;
BEGIN TRANSACTION;

;WITH RankedDrafts AS
(
    SELECT
        OrderID,
        ROW_NUMBER() OVER
        (
            PARTITION BY CreatedBy, Store_ID, Vendor_Number
            ORDER BY OrderDate DESC, OrderID DESC
        ) AS DraftRank
    FROM dbo.SW_ProductOrders WITH (UPDLOCK, HOLDLOCK)
    WHERE OrderStatus = N'DRAFT'
      AND CreatedBy IS NOT NULL
      AND Store_ID IS NOT NULL
      AND Vendor_Number IS NOT NULL
)
UPDATE orders
SET OrderStatus = N'SUPERSEDED'
FROM dbo.SW_ProductOrders AS orders
INNER JOIN RankedDrafts AS ranked ON ranked.OrderID = orders.OrderID
WHERE ranked.DraftRank > 1;

IF NOT EXISTS
(
    SELECT 1
    FROM sys.indexes
    WHERE object_id = OBJECT_ID(N'dbo.SW_ProductOrders')
      AND name = N'UX_SW_ProductOrders_OneActiveDraftPerOwnerStoreVendor'
)
BEGIN
    CREATE UNIQUE INDEX UX_SW_ProductOrders_OneActiveDraftPerOwnerStoreVendor
        ON dbo.SW_ProductOrders (CreatedBy, Store_ID, Vendor_Number)
        WHERE OrderStatus = N'DRAFT'
          AND CreatedBy IS NOT NULL
          AND Store_ID IS NOT NULL
          AND Vendor_Number IS NOT NULL;
END;

COMMIT TRANSACTION;
GO
