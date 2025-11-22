package com.konark.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
	@Autowired
	private VendorItemModel vendorItemModel;

	@GetMapping("")
	private ResponseEntity<ResponseModel> getAllVendorItemsByVendorNumber()	 {
		
		ResponseModel responseModel = new ResponseModel();
		VendorItemModel vendorItemModel = vendorItemService.getAllVendorItems();
		responseModel.addModel(VendorItemModel.class.getSimpleName(), vendorItemModel);
		return ApplicationUtils.buildOkResponse(responseModel);
	}

	@PostMapping("/upload")
	private ResponseEntity<ResponseModel> uploadVendorInventory(@RequestParam("file") MultipartFile file, @RequestParam("vendorNumber") String vendorNumber) {
		ResponseModel responseModel = new ResponseModel();
		try {
			VendorItemModel vendorItemModel = vendorItemService.parseAndSave( file,vendorNumber );
			responseModel.addModel(VendorItemModel.class.getSimpleName(), vendorItemModel);
			return ApplicationUtils.buildOkResponse(responseModel);
		} catch( Exception e ) {
			responseModel.addModel( String.class.getName(), e.getMessage() );
			return ApplicationUtils.buildErrorResponse( HttpStatus.BAD_REQUEST , responseModel) ;
			// return ResponseEntity.badRequest().body( "Error: " + e.getMessage() );
		}
	}

		@GetMapping("/{vendorNumber}")
	private ResponseEntity<ResponseModel> getVendorItemsByVendorNumber(@PathVariable String vendorNumber)	 {

		ResponseModel responseModel = new ResponseModel();
		VendorItemModel vendorItemModel = vendorItemService.getVendorItemsByVendorNumber(vendorNumber);
		responseModel.addModel(VendorItemModel.class.getSimpleName(), vendorItemModel);
		return ApplicationUtils.buildOkResponse(responseModel);
	}

	@PutMapping(value = "/updateVendorItems" , consumes = "application/json")
	private ResponseEntity<ResponseModel> updateVendorInventory( @RequestBody VendorItemModel vendorItemModel, @RequestParam("vendorNumber") String vendorNumber) {
		ResponseModel responseModel = new ResponseModel();
		try {
			vendorItemModel = vendorItemService.updateVendorItems( vendorItemModel,vendorNumber );
			responseModel.addModel(VendorItemModel.class.getSimpleName(), vendorItemModel);
			return ApplicationUtils.buildOkResponse(responseModel);
		} catch( Exception e ) {
			responseModel.addModel( String.class.getName(), e.getMessage() );
			return ApplicationUtils.buildErrorResponse( HttpStatus.BAD_REQUEST , responseModel) ;
			// return ResponseEntity.badRequest().body( "Error: " + e.getMessage() );
		}
	}


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
