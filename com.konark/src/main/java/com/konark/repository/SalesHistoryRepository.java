package com.konark.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.konark.entity.InvoiceItemizedEntity;

public interface SalesHistoryRepository extends CrudRepository<InvoiceItemizedEntity, String>  {

	
	@Query(value = """
			WITH itemKeys AS (
			  -- The product grid can contain either the canonical item number or
			  -- a POS alternate SKU.  Resolve both directions through the
			  -- store-specific Inventory_SKUs crosswalk before touching invoices.
			  SELECT LTRIM(RTRIM(:itemNumber)) AS ItemNum
			  UNION
			  SELECT LTRIM(RTRIM(sku.ItemNum))
			  FROM Inventory_SKUs sku
			  WHERE LTRIM(RTRIM(sku.Store_ID)) IN (LTRIM(RTRIM(:storeID)), '9999')
			    AND LTRIM(RTRIM(sku.AltSKU)) = LTRIM(RTRIM(:itemNumber))
			  UNION
			  SELECT LTRIM(RTRIM(sku.AltSKU))
			  FROM Inventory_SKUs sku
			  WHERE LTRIM(RTRIM(sku.Store_ID)) IN (LTRIM(RTRIM(:storeID)), '9999')
			    AND LTRIM(RTRIM(sku.ItemNum)) = LTRIM(RTRIM(:itemNumber))
			),
			inv AS (
			  SELECT TOP 1 inventory.ItemNum, inventory.ItemName
			  FROM vw_AllInventory inventory
			  WHERE LTRIM(RTRIM(inventory.Store_ID)) = LTRIM(RTRIM(:storeID))
			    AND LTRIM(RTRIM(inventory.ItemNum)) IN (SELECT ItemNum FROM itemKeys)
			)
			SELECT COALESCE(MAX(inv.ItemNum), :itemNumber) AS ItemNum,
			       COALESCE(MAX(inv.ItemName), NULLIF(NULLIF(LTRIM(RTRIM(MAX(invoice.DiffItemName))), ''), :itemNumber), 'NO SALE') AS ITEMNAME,
			       SUM(CASE WHEN invoice.ModifiedDate BETWEEN DATEADD(DAY, -7, GETDATE()) AND GETDATE() THEN invoice.Quantity ELSE 0 END) AS seven,
			       SUM(CASE WHEN invoice.ModifiedDate BETWEEN DATEADD(DAY, -15, GETDATE()) AND GETDATE() THEN invoice.Quantity ELSE 0 END) AS fifteen,
			       SUM(CASE WHEN invoice.ModifiedDate BETWEEN DATEADD(DAY, -30, GETDATE()) AND GETDATE() THEN invoice.Quantity ELSE 0 END) AS thirty
			FROM (SELECT 1 AS anchor) base
			LEFT JOIN inv ON 1=1
			LEFT JOIN vw_AllInvoiceItemized invoice
			  ON LTRIM(RTRIM(invoice.Store_ID)) = LTRIM(RTRIM(:storeID))
			 AND LTRIM(RTRIM(invoice.ItemNum)) IN (SELECT ItemNum FROM itemKeys)
			""", nativeQuery = true)
	List<Object[]> findSalesHistoryByItemNumberAndStore(@Param("itemNumber") String itemNumber, @Param("storeID") String storeID);

	@Query(value = "  SELECT CAST(ModifiedDate AS DATE) as DateField, SUM(PricePer*Quantity) as SaleAmount \r\n"
			+ "	  FROM vw_AllInvoiceItemized where LTRIM(RTRIM(Store_ID)) = LTRIM(RTRIM(:storeID)) \r\n"
			+ "	  GROUP BY CAST(ModifiedDate AS DATE) order by DateField desc", nativeQuery = true)
	List<Object[]> getTotalSaleByStoreID(@Param("storeID") String storeID);
}
