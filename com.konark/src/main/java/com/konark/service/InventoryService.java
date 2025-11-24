package com.konark.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.konark.entity.InventoryEntity;
import com.konark.model.InventoryModel;
import com.konark.model.ProductItemModel;
import com.konark.model.ProductOrderModel;
import com.konark.repository.InventoryRepository;
import com.konark.util.LevenshteinItemSearch;

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

		Iterable<InventoryEntity> vendors = inventoryRepository.findStoreInventoryByVendorNumber(vendorNumber, storeID);
		vendors.forEach(inventoryModel.getInventory()::add);
		return inventoryModel;
	}

	public ProductOrderModel getStoreInventoryByItemName(String productName, String storeID) {

		String[] productSplitArray = productName.split("\\s+");

		Stream<String> stream = Arrays.stream(productSplitArray);

		StringBuffer searchWord = new StringBuffer();

		int length = productSplitArray.length;

		if (length > 2) {
			searchWord.append(productSplitArray[0]);
			searchWord.append(" ");
			searchWord.append(productSplitArray[1]);
		} else {
			searchWord.append(productName);
		}

		// Iterable<InventoryEntity> inventory =
		// inventoryRepository.findByItemNameContainingAndStoreID(searchWord.toString(),
		// storeID);

		List<InventoryEntity> inventory = inventoryRepository.findAllByStoreID(storeID);

		Iterable<InventoryEntity> inventorySearchResults = LevenshteinItemSearch
				.findSimilarItemsByLevenshtein(productName, inventory);

		List<ProductItemModel> productItems = new ArrayList<ProductItemModel>();

		if (inventorySearchResults != null) {
			inventorySearchResults.forEach(inventoryItem -> {

				ProductItemModel productItemModel = new ProductItemModel();

				productItemModel.setItemName(inventoryItem.getItemName());
				productItemModel.setItemNumber(inventoryItem.getItemNumber());
				productItemModel.setVendorNumber(inventoryItem.getVendorNumber());

				productItemModel.setVendorPartNumber(inventoryItem.getVendorPartNumber());
				productItemModel.setQuantityInStock(inventoryItem.getQuantityInStock());

				productItems.add(productItemModel);

			});
		}

		productOrderModel.setProductItems(productItems);

		return productOrderModel;
	}

}
