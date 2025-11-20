package com.konark.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.konark.entity.InvoiceItemizedEntity;

public interface SalesHistoryRepository extends CrudRepository<InvoiceItemizedEntity, String>  {

	
	@Query(value ="SELECT itemNum, ItemNum AS ITEMNAME, SUM(CASE WHEN ModifiedDate BETWEEN DATEADD(DAY, -7, GETDATE()) and  GETDATE() THEN Quantity ELSE 0 END) AS seven, 	"
			+ "SUM(CASE WHEN ModifiedDate BETWEEN DATEADD(DAY, -15, GETDATE()) and  GETDATE() THEN Quantity ELSE 0 END) AS fifteen, "
			+ "SUM(CASE WHEN ModifiedDate BETWEEN DATEADD(DAY, -30, GETDATE()) and  GETDATE() THEN Quantity ELSE 0 END) AS thirty "
			+ "FROM invoice_itemized_v1 invoice where invoice.itemNum = :itemNumber AND Store_ID=:storeID  "
			+ "GROUP BY itemNum ORDER BY 2 DESC", nativeQuery = true)
	List<Object[]> findSalesHistoryByItemNumberAndStore(String itemNumber, String storeID);

	
	  
	  @Query(value ="  SELECT CAST(ModifiedDate AS DATE) as DateField, SUM(PricePer*Quantity) as SaleAmount \r\n"
	  				+ "	  FROM Invoice_Itemized_v1 where Store_ID = :storeID \r\n"
	  					+ "	  GROUP BY CAST(ModifiedDate AS DATE) order by DateField desc", nativeQuery = true)
	  List<Object[]> getTotalSaleByStoreID(String storeID);
}
