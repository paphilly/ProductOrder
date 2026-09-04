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

	@Query(value = "SELECT * FROM Inventory_Vendors invVendors WHERE invVendors.Vendor_Number NOT IN (:vendorNumber) AND invVendors.ItemNum = :itemNumber", nativeQuery = true)
	Iterable<VendorItemEntity> findByItemNumberContainingAndVendorNumberNotIn(@Param("itemNumber") String itemNumber, @Param("vendorNumber") List<String> vendorNumber);
	
	/*@Query(value = "SELECT DISTINCT vendorDept FROM SW_VendorItems v WHERE v.Vendor_Number = :vendorNumber", nativeQuery = true)
	List<String> findDistinctDepartmentsByVendorNumber(String vendorNumber);*/
	
	@Query(
		    value = "SELECT DISTINCT " +
		            "inventory.Dept_ID, " +
		            "CAST(department.Description AS NVARCHAR(255)) AS Description " +
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
);	@Query(value = "SELECT " +
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
        "LEFT JOIN Inventory inventory ON inventory.ItemNum = invVend.ItemNum AND inventory.Store_ID = invVend.Store_ID " +
        "LEFT JOIN Departments department ON department.Dept_ID = inventory.Dept_ID " +
        "WHERE invVend.Store_ID = '9999' " +
        "AND invVend.ItemNum = :itemNumber",
    nativeQuery = true)
List<VendorInventoryProjection> findReferenceVendorItems(
@Param("itemNumber") String itemNumber
);@Query(value = "SELECT " +
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
        "JOIN Inventory inventory ON inventory.ItemNum = invVend.ItemNum AND inventory.Store_ID = invVend.Store_ID " +
        "JOIN Departments department ON department.Dept_ID = inventory.Dept_ID " +
        "WHERE invVend.Vendor_Number = :vendorNumber",
    nativeQuery = true)
List<VendorInventoryProjection> findItemsByVendorNumber( @Param("vendorNumber") String vendorNumber
);

	@Query(
		    value = """
            WITH productLink AS (
                SELECT link.ItemNum, link.Store_ID, link.Vendor_Number,
                       link.CostPer, link.Case_Cost, link.NumPerVenCase,
                       link.Vendor_Part_Num
                FROM vw_AllInventoryVendors link
                WHERE link.Store_ID = :storeId
                  AND link.Vendor_Number = :vendorNumber
                UNION ALL
                SELECT ref.ItemNum, ref.Store_ID, ref.Vendor_Number,
                       ref.CostPer, ref.Case_Cost, ref.NumPerVenCase,
                       ref.Vendor_Part_Num
                FROM vw_AllInventoryVendors ref
                WHERE ref.Store_ID = '9999'
                  AND ref.Vendor_Number = :vendorNumber
                  AND NOT EXISTS (
                      SELECT 1 FROM vw_AllInventoryVendors localLink
                      WHERE localLink.Store_ID = :storeId
                        AND localLink.Vendor_Number = :vendorNumber
                        AND (localLink.ItemNum = ref.ItemNum
                             OR localLink.Vendor_Part_Num = ref.Vendor_Part_Num)
                  )
            )
            SELECT
                vendor.Company AS vendorName,
                link.ItemNum AS itemNum,
                link.Store_ID AS storeId,
                link.Vendor_Number AS vendorNumber,
                CONVERT(nvarchar(50), COALESCE(NULLIF(link.CostPer, 0), inventory.Cost, 0)) AS costPer,
                CONVERT(nvarchar(50), COALESCE(NULLIF(link.Case_Cost, 0), NULLIF(link.CostPer, 0), inventory.Cost, 0)) AS caseCost,
                CONVERT(nvarchar(50), link.NumPerVenCase) AS numPerVenCase,
                link.Vendor_Part_Num AS vendorPartNum,
                COALESCE(inventory.ItemName, referenceInventory.ItemName) AS itemName,
                NULL AS category,
                CONVERT(nvarchar(50), CASE WHEN inventory.In_Stock < 0 THEN 0 ELSE inventory.In_Stock END) AS inStock,
                COALESCE(inventory.Dept_ID, referenceInventory.Dept_ID) AS deptId,
                CAST(COALESCE(department.Description, referenceDepartment.Description) AS nvarchar(255)) AS description
            FROM productLink link
            LEFT JOIN vw_AllInventoryVendors localLink
              ON localLink.ItemNum = link.ItemNum
             AND localLink.Store_ID = :storeId
             AND localLink.Vendor_Number = :vendorNumber
            LEFT JOIN vw_AllInventory inventory
              ON localLink.ItemNum IS NOT NULL
             AND inventory.ItemNum = localLink.ItemNum
             AND inventory.Store_ID = :storeId
            LEFT JOIN vw_AllInventory referenceInventory
              ON referenceInventory.ItemNum = link.ItemNum
             AND referenceInventory.Store_ID = '9999'
            LEFT JOIN Vendors vendor
              ON vendor.Vendor_Number = link.Vendor_Number
            LEFT JOIN Departments department
              ON department.Dept_ID = inventory.Dept_ID
            LEFT JOIN Departments referenceDepartment
              ON referenceDepartment.Dept_ID = referenceInventory.Dept_ID
            """,
		nativeQuery = true
	)
	List<VendorInventoryProjection> findConsolidatedItemsByVendorNumber( @Param("vendorNumber") String vendorNumber,
			@Param("storeId") String storeId
	);

}
