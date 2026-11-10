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

	/**
	 * The application connects to Central, whereas inventory is physically held in
	 * the individual store databases.  Always use the union view for read paths so
	 * that a selected store is never silently served from Central.Inventory.
	 */
	@Query(value = "SELECT ItemNum, Store_ID, Vendor_Number, Vendor_Part_Num, "
			+ "In_Stock, ItemName "
			+ "FROM vw_AllInventory WHERE Store_ID = :storeID", nativeQuery = true)
	List<InventoryEntity> findCanonicalByStoreID(@Param("storeID") String storeID);

	@Query(value = "SELECT inv.ItemNum, inv.Store_ID, inv.Vendor_Number, inv.Vendor_Part_Num, "
			+ "inv.In_Stock, inv.ItemName "
			+ "FROM vw_AllInventory inv "
			+ "WHERE inv.Store_ID = :storeID "
			+ "AND EXISTS (SELECT 1 FROM vw_AllInventoryVendors link "
			+ "            WHERE link.Store_ID = inv.Store_ID "
			+ "              AND link.ItemNum = inv.ItemNum "
			+ "              AND link.Vendor_Number = :vendorNumber)", nativeQuery = true)
	List<InventoryEntity> findCanonicalByStoreIDAndVendorNumber(@Param("storeID") String storeID,
			@Param("vendorNumber") String vendorNumber);
	
	List<InventoryEntity> findByItemNumberContainingAndStoreID(String itemNumber, String storeID);

	@Query(value = "SELECT * FROM Inventory i WHERE LTRIM(RTRIM(i.Vendor_Number)) = LTRIM(RTRIM(:vendorNumber)) AND LTRIM(RTRIM(i.Store_ID)) = LTRIM(RTRIM(:storeID))", nativeQuery = true)
	List<InventoryEntity> findStoreInventoryByVendorNumber(@Param("vendorNumber") String vendorNumber, @Param("storeID") String storeID);

}
