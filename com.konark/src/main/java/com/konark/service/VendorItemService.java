package com.konark.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.konark.dto.VendorInventoryProjection;
import com.konark.entity.InventoryEntity;
import com.konark.entity.VendorEntity;
import com.konark.entity.VendorItemEntity;
import com.konark.entity.pk.VendorItemPK;
import com.konark.model.ProductItemModel;
import com.konark.model.ProductOrderModel;
import com.konark.model.VendorItemModel;
import com.konark.repository.InventoryRepository;
import com.konark.repository.VendorItemRepository;
import com.konark.repository.VendorRepository;
import com.konark.util.LevenshteinItemSearch;
import com.konark.util.LevenshteinWordSearch;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class VendorItemService {

	@Autowired
	VendorItemRepository vendorItemRepository;

	@Autowired
	VendorRepository vendorRepository;

	@Autowired
	private VendorItemModel vendorItemModel;

	@Autowired
	private ProductOrderModel productOrderModel;

	@Autowired
	private InventoryRepository inventoryRepository;

	public VendorItemModel getVendorItemsByVendorNumber(String vendorNumber) {

		Iterable<VendorItemEntity> vendorItems = vendorItemRepository.findVendorItemsByVendorNumber(vendorNumber);
		vendorItems.forEach(vendorItemModel.getVendorItems()::add);
		return vendorItemModel;
	}

	public VendorItemModel getAllVendorItems() {

		Iterable<VendorItemEntity> vendorItems = vendorItemRepository.findAll();
		vendorItems.forEach(vendorItemModel.getVendorItems()::add);
		return vendorItemModel;
	}

	public VendorItemModel getVendorItemsByID(VendorItemPK vendorItemPK) {

		List<VendorItemPK> vendorNumberPKList = new ArrayList<VendorItemPK>();
		vendorNumberPKList.add(vendorItemPK);

		Iterable<VendorItemEntity> vendorItems = vendorItemRepository.findAllById(vendorNumberPKList);
		vendorItems.forEach(vendorItemModel.getVendorItems()::add);
		return vendorItemModel;
	}

	public void seVendorItemModel(VendorItemModel vendorItemModel) {
		this.vendorItemModel = vendorItemModel;
	}

	public ProductOrderModel getVendorItemsForProductOrder(String vendorNumber, String storeID) {

		List<ProductItemModel> vendorItems = new ArrayList<ProductItemModel>();

//		Iterable<VendorItemEntity> vendorItemResults = vendorItemRepository.findVendorItemsByVendorNumber(vendorNumber);
//
//		vendorItemResults.forEach(vendorItem -> {
//
//			ProductItemModel productItemModel = new ProductItemModel();
//
//			productItemModel.setDepartment(vendorItem.getDepartment());
//			productItemModel.setItemName(vendorItem.getItemName());
//			productItemModel.setItemNumber(vendorItem.getItemNumber());
//			productItemModel.setVendorNumber(vendorItem.getVendorNumber());
//			productItemModel.setUnitMeasure(vendorItem.getUnitMeasure());
//			productItemModel.setVendorPartNumber(vendorItem.getVendorPartNumber());
//			productItemModel.setCostPerItem(vendorItem.getCostPerItem());
//			productItemModel.setCaseCost(vendorItem.getCaseCost());
//			productItemModel.setVendorItemName(vendorItem.getVendorItemName());
//			vendorItems.add(productItemModel);
//
//		});
//		
//		Iterable<InventoryEntity> inventoryResults = inventoryRepository.findStoreInventoryByVendorNumber(vendorNumber,	storeID);
//
//		for (InventoryEntity inventoryItem : inventoryResults) {
//			for (ProductItemModel vendorItem : vendorItems) {
//
//				if (vendorItem.getItemName().equals(inventoryItem.getItemName())) {
//					vendorItem.setQuantityInStock(inventoryItem.getQuantityInStock());
//					vendorItem.setInventoryFlag(true);					
//					break;
//				}				
//			}
//		}
//
//		int count = 0;
//
//		for (ProductItemModel item : vendorItems) {
//			if (item.getInventoryFlag()) {
//				count++;
//			}
//
//		}
//		System.out.println("inventory count" + count);
//		productOrderModel.setProductItems(vendorItems);
//		
		
//		 List<Object[]> rawResults = vendorItemRepository.findVendorInventoryDataRaw();
//
//		 List<String[]> stringResults= rawResults.stream()
//		        .map(row -> {
//		            String[] strRow = new String[row.length];
//		            for (int i = 0; i < row.length; i++) {
//		                strRow[i] = row[i] != null ? row[i].toString() : null;
//		            }
//		            return strRow;
//		        })
//		        .collect(Collectors.toList());
//		 
//		 List<Object[]> rawResultsWithFilter = vendorItemRepository.findVendorInventoryDataByStoreAndVendor("","");
//
//		 List<String[]> stringResultsWithFilter= rawResults.stream()
//		        .map(row -> {
//		            String[] strRow = new String[row.length];
//		            for (int i = 0; i < row.length; i++) {
//		                strRow[i] = row[i] != null ? row[i].toString() : null;
//		            }
//		            return strRow;
//		        })
//		        .collect(Collectors.toList());
//		 
//		 List<VendorInventoryProjection> results = vendorItemRepository.findByStoreIdAndVendorNumber(storeID, vendorNumber);

		 List<VendorInventoryProjection> vendorItemResults = vendorItemRepository.findByVendorNumber(vendorNumber);


			vendorItemResults.forEach(vendorItem -> {

				ProductItemModel productItemModel = new ProductItemModel();

				System.out.println("Failed One " + vendorItem.getItemName() + ":"+ vendorItem.getItemNum());
				productItemModel.setDepartment(vendorItem.getDescription());
				productItemModel.setItemName(vendorItem.getItemName());
				productItemModel.setItemNumber(vendorItem.getItemNum());
				productItemModel.setVendorNumber(vendorItem.getVendorNumber());
				productItemModel.setUnitMeasure(vendorItem.getNumPerVenCase());
				productItemModel.setVendorPartNumber(vendorItem.getVendorPartNum());
				productItemModel.setCostPerItem(vendorItem.getCostPer());
				productItemModel.setCaseCost(vendorItem.getCaseCost());
				productItemModel.setVendorItemName(vendorItem.getItemName());
				vendorItems.add(productItemModel);

			});
			
			Iterable<InventoryEntity> inventoryResults = inventoryRepository.findStoreInventoryByVendorNumber(vendorNumber,	"9999");

			for (InventoryEntity inventoryItem : inventoryResults) {
				for (ProductItemModel vendorItem : vendorItems) {

					if (vendorItem.getItemName().equals(inventoryItem.getItemName())) {
						vendorItem.setQuantityInStock(inventoryItem.getQuantityInStock());
						vendorItem.setInventoryFlag(true);					
						break;
					}				
				}
			}

			int count = 0;

			for (ProductItemModel item : vendorItems) {
				if (item.getInventoryFlag()) {
					count++;
				}

			}
			System.out.println("inventory count" + count);
			productOrderModel.setProductItems(vendorItems);
			
		return productOrderModel;
	}

	public ProductOrderModel getReferenceVendorItems(String itemName, String vendorNumber) {

		Map<String, String> vendorMap = new HashMap<String, String>();
		
		Iterable<VendorEntity> allVendors = vendorRepository.findAll();

		allVendors.forEach(vendor -> {
			vendorMap.put(vendor.getVendorNumber(), vendor.getCompany());
		});
	//	Iterable<VendorItemEntity> vendorItems = vendorItemRepository.findByItemNumberContainingAndVendorNumberNotIn(itemName, vendorNumbers);
		List<VendorInventoryProjection> referenceVendorItems = vendorItemRepository.findReferenceVendorItems(vendorNumber);

	//	List<VendorItemEntity> matchingVendorItems = LevenshteinItemSearch.findAlternateItemsByLevenshtein(itemName,vendorItems);
		
		List<VendorInventoryProjection> matchingVendorItems = LevenshteinWordSearch.findSimilarItems(referenceVendorItems, itemName);
		
		
		List<ProductItemModel> matchingProductItems = new ArrayList<ProductItemModel>();

		matchingVendorItems.forEach(vendorItem -> {

			ProductItemModel productItemModel = new ProductItemModel();
productItemModel.setVendorName(vendorItem.getVendorName());
			productItemModel.setDepartment(vendorItem.getDescription());
			productItemModel.setItemName(vendorItem.getItemName());
			productItemModel.setItemNumber(vendorItem.getItemNum());
			productItemModel.setVendorNumber(vendorItem.getVendorNumber());
			productItemModel.setUnitMeasure(vendorItem.getNumPerVenCase());
			productItemModel.setVendorPartNumber(vendorItem.getVendorPartNum());
			productItemModel.setCostPerItem(vendorItem.getCostPer());
			productItemModel.setCaseCost(vendorItem.getCaseCost());
			productItemModel.setVendorItemName(vendorItem.getItemName());
			matchingProductItems.add(productItemModel);

		});

		productOrderModel.setProductItems(matchingProductItems);

		return productOrderModel;
	}
	
//	public ProductOrderModel getReferenceVendorItems(String itemNumber, String vendorNumber) {
//
//		Map<String, String> vendorMap = new HashMap<String, String>();
//		List<String> vendorNumbers = new ArrayList<String>();
//		vendorNumbers.add(vendorNumber);
//
//		Iterable<VendorEntity> allVendors = vendorRepository.findAll();
//
//		allVendors.forEach(vendor -> {
//			vendorMap.put(vendor.getVendorNumber(), vendor.getCompany());
//		});
//	//	Iterable<VendorItemEntity> vendorItems = vendorItemRepository.findByItemNumberContainingAndVendorNumberNotIn(itemNumber, vendorNumbers);
//		Iterable<VendorItemEntity> vendorItems = vendorItemRepository.findVendorItemsVendorNumberNotIn(vendorNumbers);
//		List<ProductItemModel> productItems = new ArrayList<ProductItemModel>();
//
//		vendorItems.forEach(vendorItem -> {
//
//			ProductItemModel productItemModel = new ProductItemModel();
//
//			productItemModel.setDepartment(vendorItem.getDepartment());
//			productItemModel.setItemName(vendorItem.getItemName());
//			productItemModel.setItemNumber(vendorItem.getItemNumber());
//			productItemModel.setVendorNumber(vendorItem.getVendorNumber());
//			productItemModel.setUnitMeasure(vendorItem.getUnitMeasure());
//			productItemModel.setVendorPartNumber(vendorItem.getVendorPartNumber());
//			productItemModel.setVendorName(vendorMap.get(vendorItem.getVendorNumber()));
//			productItemModel.setCostPerItem(vendorItem.getCostPerItem());
//			productItemModel.setCaseCost(vendorItem.getCaseCost());
//			productItems.add(productItemModel);
//
//		});
//
//		productOrderModel.setProductItems(productItems);
//
//		return productOrderModel;
//	}

	public Map<String, String> getDepartmentsByVendor(String vendorNumber) {

		Map<String, String> departmentsMap = new HashMap<String, String>();

		List<Object[]> departments = vendorItemRepository.findDistinctDepartmentsByVendorNumber(vendorNumber);

	    for (Object[] row : departments) {
	        String  deptId= (String) row[0];
	        String description = (String) row[1];

	        departmentsMap.put(deptId, description);
	        System.out.println("Dept ID: " + deptId + ", Description: " + description);
	    }
		
	/*	departments.forEach(department -> {
			if (department != null)
				departmentsMap.put(department.toString(), department.toString());
		});*/

//		if(departments.size()>0) {
//			  departmentsMap = departments.stream().collect(
//		                Collectors.toMap(dept -> dept.toString(), dept -> dept.toString()));
//		}

		// departmentsMap =
		// departments.stream().collect(Collectors.toMap(String::toString,
		// String::toString));

		return departmentsMap;
	}

}
