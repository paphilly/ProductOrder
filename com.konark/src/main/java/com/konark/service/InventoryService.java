package com.konark.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.konark.entity.InventoryEntity;
import com.konark.model.InventoryModel;
import com.konark.model.ProductItemModel;
import com.konark.model.ProductOrderModel;
import com.konark.repository.InventoryRepository;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InventoryService {

	@Autowired
	InventoryRepository inventoryRepository;

	@Autowired
	private InventoryModel inventoryModel;

	@Autowired
	private ProductOrderModel productOrderModel;

	public InventoryModel getStoreInventoryByVendorNumber(String vendorNumber, String storeID) {

		Iterable<InventoryEntity> vendors = inventoryRepository.findCanonicalByStoreIDAndVendorNumber(storeID, vendorNumber);
		vendors.forEach(inventoryModel.getInventory()::add);
		return inventoryModel;
	}

	public InventoryModel getStoreInventory(String storeID) {
		List<InventoryEntity> inventory = inventoryRepository.findCanonicalByStoreID(storeID);
		inventoryModel.setInventory(inventory);
		return inventoryModel;
	}

	public ProductOrderModel getStoreInventoryByItemName(String productName, String storeID) {

		// Exact match only: return inventory rows for the SAME product (case-insensitive).
		// (Previously used LevenshteinItemSearch fuzzy matching, which surfaced unrelated items.)
		List<ProductItemModel> productItems = new ArrayList<ProductItemModel>();

		if (productName != null) {
			String normalizedProductName = productName.trim();
			List<InventoryEntity> inventory = inventoryRepository.findCanonicalByStoreID(storeID);

			if (inventory != null) {
				inventory.forEach(inventoryItem -> {
					if (inventoryItem.getItemName() != null
							&& normalizedProductName.equalsIgnoreCase(inventoryItem.getItemName().trim())) {

						ProductItemModel productItemModel = new ProductItemModel();

						productItemModel.setItemName(inventoryItem.getItemName());
						productItemModel.setItemNumber(inventoryItem.getItemNumber());
						productItemModel.setVendorNumber(inventoryItem.getVendorNumber());
						productItemModel.setVendorPartNumber(inventoryItem.getVendorPartNumber());
						productItemModel.setQuantityInStock(inventoryItem.getQuantityInStock());

						productItems.add(productItemModel);
					}
				});
			}
		}

		productOrderModel.setProductItems(productItems);

		return productOrderModel;
	}

}
