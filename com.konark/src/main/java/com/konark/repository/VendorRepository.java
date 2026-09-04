package com.konark.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.konark.entity.VendorEntity;

public interface VendorRepository extends CrudRepository<VendorEntity, String>  {

	@Query(value = "SELECT vendor.* "
			+ "FROM Vendors vendor "
			+ "WHERE EXISTS ("
			+ "    SELECT 1 FROM vw_AllInventoryVendors mapping "
			+ "    WHERE LTRIM(RTRIM(mapping.Vendor_Number)) = LTRIM(RTRIM(vendor.Vendor_Number)) "
			+ "    AND LTRIM(RTRIM(mapping.Store_ID)) = LTRIM(RTRIM(:storeID))"
			+ ") "
			+ "ORDER BY vendor.Company", nativeQuery = true)
	List<VendorEntity> findByStoreID(@Param("storeID") String storeID);
}
