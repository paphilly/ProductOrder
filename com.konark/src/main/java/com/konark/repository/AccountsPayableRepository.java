package com.konark.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.konark.entity.BillPayEntity;

public interface AccountsPayableRepository extends CrudRepository<BillPayEntity, String> {

	@Query(value = "SELECT * from Bill where TxnDate > DATEADD(DAY, -60, GETDATE())", nativeQuery = true)
	List<BillPayEntity> findAccountsPayableForPast60Days();
	
//	@Query(value = "SELECT * from Bill where TxnDate > DATEADD(DAY, -60, GETDATE()) AND SalesTermRefName= :location", nativeQuery = true)
//	List<BillPayEntity> findAccountsPayableForPast60DaysByLocation(String location);
	
	@Query(value = "SELECT * from Bill where TxnDate > DATEADD(DAY, -60, GETDATE()) AND SalesTermRefName IN (:locations)", nativeQuery = true)
	List<BillPayEntity> findAccountsPayableForPast60DaysByLocations(String[] locations);
	
	@Query(value = "SELECT * from Bill where TxnDate > DATEADD(DAY, -60, GETDATE()) AND SalesTermRefName IS NULL", nativeQuery = true)
	List<BillPayEntity> findUnassignedAccountsPayableForPast60Days();
	
	@Query(value = "SELECT VendorRefId,VendorRefName FROM Bill b WHERE b.SalesTermRefName IN (:locations)", nativeQuery = true)
	List<Object[]> findVendorsByLocation(String[] locations);
	
	@Query(value = "SELECT VendorRefId,VendorRefName FROM Bill b WHERE b.SalesTermRefName IS NULL", nativeQuery = true)
	List<Object[]> findUnassignedVendors();
	
	@Query(value = "SELECT DISTINCT SalesTermRefName FROM Bill b", nativeQuery = true)
	List<String> findDistinctLocation();
}
