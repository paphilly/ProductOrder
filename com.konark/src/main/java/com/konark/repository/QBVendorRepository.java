package com.konark.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.konark.entity.QBVendorEntity;

public interface QBVendorRepository extends CrudRepository<QBVendorEntity, String>  {
	
	@Query(value = "select CAST(b.res1 AS NVARCHAR(255)) from bill a, QB_Vendor b where a.VendorRefId=b.VendorRefId and a.VendorRefId=:vendorReferenceID", nativeQuery = true)
	List<String> findAccountsPayableVendorEmailID(@Param("vendorReferenceID") String vendorReferenceID);
	
}
