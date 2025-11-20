package com.konark.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.konark.entity.QBVendorEntity;

public interface QBVendorRepository extends CrudRepository<QBVendorEntity, String>  {
	
	@Query(value = "select b.res1 from bill a, QB_Vendor b where a.VendorRefId=b.VendorRefId and a.VendorRefId=:vendorReferenceID", nativeQuery = true)
	List<String> findAccountsPayableVendorEmailID(String vendorReferenceID);
	
}
