package com.konark.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.konark.entity.InvoiceItemizedEntity;

public interface SalesHistoryRepository extends CrudRepository<InvoiceItemizedEntity, String>  {

	
	@Query(value = """
			WITH inv AS (
			  SELECT TOP 1 ItemNum, ItemName
			  FROM vw_AllInventory
			  WHERE LTRIM(RTRIM(ItemNum)) = LTRIM(RTRIM(:itemNumber))
			    AND LTRIM(RTRIM(Store_ID)) = LTRIM(RTRIM(:storeID))
			)
			SELECT COALESCE(MAX(inv.ItemNum), :itemNumber) AS ItemNum,
			       COALESCE(MAX(inv.ItemName), NULLIF(NULLIF(LTRIM(RTRIM(MAX(invoice.DiffItemName))), ''), :itemNumber), 'NO SALE') AS ITEMNAME,
			       SUM(CASE WHEN invoice.ModifiedDate BETWEEN DATEADD(DAY, -7, GETDATE()) AND GETDATE() THEN invoice.Quantity ELSE 0 END) AS seven,
			       SUM(CASE WHEN invoice.ModifiedDate BETWEEN DATEADD(DAY, -15, GETDATE()) AND GETDATE() THEN invoice.Quantity ELSE 0 END) AS fifteen,
			       SUM(CASE WHEN invoice.ModifiedDate BETWEEN DATEADD(DAY, -30, GETDATE()) AND GETDATE() THEN invoice.Quantity ELSE 0 END) AS thirty
			FROM (SELECT 1 AS anchor) base
			LEFT JOIN inv ON 1=1
			LEFT JOIN vw_AllInvoiceItemized invoice
			  ON LTRIM(RTRIM(invoice.ItemNum)) = LTRIM(RTRIM(:itemNumber))
			 AND LTRIM(RTRIM(invoice.Store_ID)) = LTRIM(RTRIM(:storeID))
			""", nativeQuery = true)
	List<Object[]> findSalesHistoryByItemNumberAndStore(@Param("itemNumber") String itemNumber, @Param("storeID") String storeID);

	@Query(value = "  SELECT CAST(ModifiedDate AS DATE) as DateField, SUM(PricePer*Quantity) as SaleAmount \r\n"
			+ "	  FROM vw_AllInvoiceItemized where LTRIM(RTRIM(Store_ID)) = LTRIM(RTRIM(:storeID)) \r\n"
			+ "	  GROUP BY CAST(ModifiedDate AS DATE) order by DateField desc", nativeQuery = true)
	List<Object[]> getTotalSaleByStoreID(@Param("storeID") String storeID);
}
