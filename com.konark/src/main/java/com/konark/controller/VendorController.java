package com.konark.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.konark.model.VendorModel;
import com.konark.service.VendorService;
import com.konark.util.ApplicationUtils;
import com.konark.util.ResponseModel;

@RestController
@RequestMapping("/vendors")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class VendorController {

	@Autowired
	VendorService vendorService;

	@GetMapping("")
	private ResponseEntity<ResponseModel> getAllVendors() {
		
		ResponseModel responseModel = new ResponseModel();
		VendorModel vendorModel = vendorService.getAllVendors();
		responseModel.addModel(VendorModel.class.getSimpleName(), vendorModel);
		return ApplicationUtils.buildOkResponse(responseModel);
	}
	
	
}
