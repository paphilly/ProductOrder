package com.konark.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.konark.model.ProductOrderModel;
import com.konark.model.VendorItemModel;
import com.konark.service.VendorItemService;
import com.konark.util.ApplicationUtils;
import com.konark.util.ResponseModel;

@RestController
@RequestMapping("/vendorItems")
@Scope("prototype")
public class VendorItemController {

	@Autowired
	VendorItemService vendorItemService;


	@GetMapping("")
	private ResponseEntity<ResponseModel> getAllVendorItemsByVendorNumber()	 {
		
		ResponseModel responseModel = new ResponseModel();
		VendorItemModel vendorItemModel = vendorItemService.getAllVendorItems();
		responseModel.addModel(VendorItemModel.class.getSimpleName(), vendorItemModel);
		return ApplicationUtils.buildOkResponse(responseModel);
	}
	
	
//	@GetMapping("/{vendorNumber}")
//	private ResponseEntity<ResponseModel> getVendorItemsByVendorNumber(@PathVariable String vendorNumber)	 {
//		
//		ResponseModel responseModel = new ResponseModel();
//		VendorItemModel vendorItemModel = vendorItemService.getVendorItemsByVendorNumber(vendorNumber);
//		responseModel.addModel(VendorItemModel.class.getSimpleName(), vendorItemModel);
//		return ApplicationUtils.buildOkResponse(responseModel);
//	}
	
	@GetMapping("/productorder")
	private ResponseEntity<ResponseModel> getVendorItemsForProductOrder(@RequestParam("vendorNumber") String vendorNumber, @RequestParam("storeID") String storeID) {
		
		ResponseModel responseModel = new ResponseModel();
		ProductOrderModel productOrderModel = vendorItemService.getVendorItemsForProductOrder(vendorNumber,storeID);
		responseModel.addModel(ProductOrderModel.class.getSimpleName(), productOrderModel);
		return ApplicationUtils.buildOkResponse(responseModel);
	}
	
	@GetMapping("/reference")
	private ResponseEntity<ResponseModel> getReferenceVendorItems(@RequestParam("itemNumber") String itemNumber, @RequestParam("vendorNumber") String vendorNumber) {
		
		ResponseModel responseModel = new ResponseModel();
		ProductOrderModel productOrderModel = vendorItemService.getReferenceVendorItems(itemNumber, vendorNumber);
		responseModel.addModel(ProductOrderModel.class.getSimpleName(), productOrderModel);
		return ApplicationUtils.buildOkResponse(responseModel);
	}
	
	@GetMapping("/departments")
	private ResponseEntity<ResponseModel> getDepartmentsByVendorNumber(@RequestParam("vendorNumber") String vendorNumber) {
		
		ResponseModel responseModel = new ResponseModel();
		Map<String,String> departments = vendorItemService.getDepartmentsByVendor(vendorNumber);
		responseModel.addModel("departments", departments);
		return ApplicationUtils.buildOkResponse(responseModel);
	}
	
}
