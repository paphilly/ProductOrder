package com.konark.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.konark.model.InventoryModel;
import com.konark.model.ProductOrderModel;
import com.konark.service.InventoryService;
import com.konark.util.ApplicationUtils;
import com.konark.util.ResponseModel;

@RestController
@RequestMapping("/inventory")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InventoryController {
	@Autowired
	InventoryService inventoryService;

	@GetMapping("")
	private ResponseEntity<ResponseModel> getStoreInventoryByVendorNumber(@RequestParam("vendorNumber") String vendorNumber, @RequestParam("storeID") String storeID) {
		
		ResponseModel responseModel = new ResponseModel();
		InventoryModel inventoryModel = inventoryService.getStoreInventoryByVendorNumber(vendorNumber, storeID);
		responseModel.addModel(InventoryModel.class.getSimpleName(), inventoryModel);
		return ApplicationUtils.buildOkResponse(responseModel);
	}
	
	@GetMapping("/product")
	private ResponseEntity<ResponseModel> getStoreInventoryByItemName(@RequestParam("productName") String productName, @RequestParam("storeID") String storeID) {
		
		ResponseModel responseModel = new ResponseModel();
		ProductOrderModel productInventoryModel = inventoryService.getStoreInventoryByItemName(productName, storeID);
		responseModel.addModel(InventoryModel.class.getSimpleName(), productInventoryModel);
		return ApplicationUtils.buildOkResponse(responseModel);
	}

}
