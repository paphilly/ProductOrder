package com.konark.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.konark.entity.InventoryEntity;
import com.konark.entity.pk.InventoryPK;

public interface InventoryRepository extends CrudRepository<InventoryEntity, InventoryPK> {

	@Query(value = "SELECT * FROM Inventory i WHERE i.ItemName=:itemName AND i.Vendor_Number= :vendorNumber AND i.Store_ID= :storeID", nativeQuery = true)
	List<InventoryEntity> findStoreInventoryByItemNameAndVendorNumber(@Param("itemName") String itemName, @Param("vendorNumber") String vendorNumber, @Param("storeID") String storeID);

//	@Query(value = "SELECT * FROM Inventory i WHERE i.ItemName=:itemName AND i.Store_ID= :storeID", nativeQuery = true)
//	List<InventoryEntity> findStoreInventoryByItemName(@Param("itemName") String itemName, @Param("storeID") String storeID);
	
	List<InventoryEntity> findByItemNameContainingAndStoreID(String itemName, String storeID);
	
	List<InventoryEntity> findAll();

	List<InventoryEntity> findAllByStoreID(String storeID);
	
	List<InventoryEntity> findByItemNumberContainingAndStoreID(String itemNumber, String storeID);

	@Query(value = "SELECT * FROM vw_AllInventory i WHERE i.Vendor_Number= :vendorNumber AND i.Store_ID= :storeID", nativeQuery = true)
	List<InventoryEntity> findStoreInventoryByVendorNumber(@Param("vendorNumber") String vendorNumber, @Param("storeID") String storeID);

}
