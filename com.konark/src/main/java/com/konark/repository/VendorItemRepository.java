package com.konark.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.konark.dto.VendorInventoryProjection;
import com.konark.entity.VendorItemEntity;
import com.konark.entity.pk.VendorItemPK;

public interface VendorItemRepository extends CrudRepository<VendorItemEntity, VendorItemPK> {

	@Query(value = "SELECT * FROM SW_VendorItems v WHERE v.Vendor_Number = :vendorNumber", nativeQuery = true)
	List<VendorItemEntity> findVendorItemsByVendorNumber(@Param("vendorNumber") String vendorNumber);

	List<VendorItemEntity> findByVendorNumber(String vendorNumber);

	Iterable<VendorItemEntity> findByItemNameContainingAndVendorNumberNotIn(String itemName, List<String> vendorNumber);
	
	List<VendorItemEntity> findByVendorNumberNotIn(List<String> vendorNumber);
	
	Iterable<VendorItemEntity> findByItemNumberContainingAndVendorNumberNotIn(String itemNumber, List<String> vendorNumber);
	
	/*@Query(value = "SELECT DISTINCT vendorDept FROM SW_VendorItems v WHERE v.Vendor_Number = :vendorNumber", nativeQuery = true)
	List<String> findDistinctDepartmentsByVendorNumber(String vendorNumber);*/
	
	@Query(
		    value = "SELECT DISTINCT " +
		            "inventory.Dept_ID, " +
		            "department.Description " +
		            "FROM Inventory_Vendors invVend " +
		            "JOIN Inventory inventory ON inventory.ItemNum = invVend.ItemNum " +
		            "JOIN Departments department ON department.Dept_ID = inventory.Dept_ID " +
		            "WHERE invVend.Vendor_Number = :vendorNumber",
		    nativeQuery = true
		)
		List<Object[]> findDistinctDepartmentsByVendorNumber(@Param("vendorNumber") String vendorNumber);

	@Query(value = "SELECT invVend.ItemNum, invVend.Store_ID, " +
            "invVend.Vendor_Number, invVend.costPer, " +
            "invVend.Case_Cost, invVend.NumPerVenCase, " +
            "invVend.Vendor_Part_Num, vendor.Company, " +
            "inventory.ItemName, inventory.In_Stock, " +
            "inventory.Dept_ID, department.Description " +
            "FROM Inventory_Vendors invVend " +
            "JOIN Vendors vendor ON vendor.Vendor_Number = invVend.Vendor_Number " +
            "JOIN Inventory inventory ON inventory.ItemNum = invVend.ItemNum " +
            "JOIN Departments department ON department.Dept_ID = inventory.Dept_ID",
    nativeQuery = true)
List<Object[]> findVendorInventoryDataRaw();

@Query(value = "SELECT " +
        "invVend.ItemNum, invVend.Store_ID, invVend.Vendor_Number, " +
        "invVend.costPer, invVend.Case_Cost, invVend.NumPerVenCase, " +
        "invVend.Vendor_Part_Num, vendor.Company, " +
        "inventory.ItemName, inventory.In_Stock, inventory.Dept_ID, " +
        "department.Description " +
        "FROM Inventory_Vendors invVend " +
        "JOIN Vendors vendor ON vendor.Vendor_Number = invVend.Vendor_Number " +
        "JOIN Inventory inventory ON inventory.ItemNum = invVend.ItemNum " +
        "JOIN Departments department ON department.Dept_ID = inventory.Dept_ID " +
        "WHERE invVend.Store_ID = :storeId AND invVend.Vendor_Number = :vendorNumber",
nativeQuery = true)
List<Object[]> findVendorInventoryDataByStoreAndVendor(@Param("storeId") String storeId,
                                                @Param("vendorNumber") String vendorNumber);

@Query(value = "SELECT " +
        "invVend.ItemNum AS itemNum, " +
        "invVend.Store_ID AS storeId, " +
        "invVend.Vendor_Number AS vendorNumber, " +
        "invVend.costPer AS costPer, " +
        "invVend.Case_Cost AS caseCost, " +
        "invVend.NumPerVenCase AS numPerVenCase, " +
        "invVend.Vendor_Part_Num AS vendorPartNum, " +
        "vendor.Company AS company, " +
        "inventory.ItemName AS itemName, " +
        "inventory.In_Stock AS inStock, " +
        "inventory.Dept_ID AS deptId, " +
        "department.Description AS description " +
        "FROM Inventory_Vendors invVend " +
        "JOIN Vendors vendor ON vendor.Vendor_Number = invVend.Vendor_Number " +
        "JOIN Inventory inventory ON inventory.ItemNum = invVend.ItemNum " +
        "JOIN Departments department ON department.Dept_ID = inventory.Dept_ID " +
        "WHERE invVend.Store_ID = :storeId AND invVend.Vendor_Number = :vendorNumber",
nativeQuery = true)
List<VendorInventoryProjection> findByStoreIdAndVendorNumber(
@Param("storeId") String storeId,
@Param("vendorNumber") String vendorNumber
);

@Query(value = "SELECT " +
		"vendor.Company AS vendorName, " +
        "invVend.ItemNum AS itemNum, " +
        "invVend.Store_ID AS storeId, " +
        "invVend.Vendor_Number AS vendorNumber, " +
        "invVend.costPer AS costPer, " +
        "invVend.Case_Cost AS caseCost, " +
        "invVend.NumPerVenCase AS numPerVenCase, " +
        "invVend.Vendor_Part_Num AS vendorPartNum, " +
        "vendor.Company AS company, " +
        "inventory.ItemName AS itemName, " +
        "inventory.In_Stock AS inStock, " +
        "inventory.Dept_ID AS deptId, " +
        "department.Description AS description " +
        "FROM Inventory_Vendors invVend " +
        "JOIN Vendors vendor ON vendor.Vendor_Number = invVend.Vendor_Number " +
        "JOIN Inventory inventory ON inventory.ItemNum = invVend.ItemNum " +
        "JOIN Departments department ON department.Dept_ID = inventory.Dept_ID " +
        "WHERE invVend.Vendor_Number <> :vendorNumber",
nativeQuery = true)
List<VendorInventoryProjection> findReferenceVendorItems(
@Param("vendorNumber") String vendorNumber
);

@Query(value = "SELECT " +
		"vendor.Company AS vendorName, " +
        "invVend.ItemNum AS itemNum, " +
        "invVend.Store_ID AS storeId, " +
        "invVend.Vendor_Number AS vendorNumber, " +
        "invVend.costPer AS costPer, " +
        "invVend.Case_Cost AS caseCost, " +
        "invVend.NumPerVenCase AS numPerVenCase, " +
        "invVend.Vendor_Part_Num AS vendorPartNum, " +
        "vendor.Company AS company, " +
        "inventory.ItemName AS itemName, " +
        "inventory.In_Stock AS inStock, " +
        "inventory.Dept_ID AS deptId, " +
        "department.Description AS description " +
        "FROM Inventory_Vendors invVend " +
        "JOIN Vendors vendor ON vendor.Vendor_Number = invVend.Vendor_Number " +
        "JOIN Inventory inventory ON inventory.ItemNum = invVend.ItemNum " +
        "JOIN Departments department ON department.Dept_ID = inventory.Dept_ID " +
        "WHERE invVend.Vendor_Number = :vendorNumber",
nativeQuery = true)
List<VendorInventoryProjection> findItemsByVendorNumber( @Param("vendorNumber") String vendorNumber
);

}
