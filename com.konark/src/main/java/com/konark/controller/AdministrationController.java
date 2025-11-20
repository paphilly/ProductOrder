package com.konark.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.konark.model.UserModel;
import com.konark.service.AccountsPayableService;
import com.konark.service.StoreService;
import com.konark.service.UserService;
import com.konark.util.ApplicationUtils;
import com.konark.util.ResponseModel;

@RestController
@RequestMapping("/administration")
public class AdministrationController {

	@Autowired
	UserService userService;

	@Autowired
	StoreService storeService;

	@Autowired
	AccountsPayableService accountsPayableService;
	@PostMapping(value ="/users/user", consumes = "application/json")
	public ResponseEntity<ResponseModel> createUser(@RequestParam("username") String userName,	@RequestBody(required = false) UserModel userModel) {
		ResponseModel responseModel = new ResponseModel();
		Boolean userExists = false;
		if (userModel == null) {
			userExists = userService.isUserExists(userName);
			responseModel.addModel("UserExists", userExists);
			return ApplicationUtils.buildOkResponse(responseModel);
		} else {
			responseModel.addModel( "UserCreationSuccess", userService.createUser(userModel));
		}

		return ApplicationUtils.buildOkResponse(responseModel);

	}

	@GetMapping("/stores")
	private ResponseEntity<ResponseModel> getAllStores() {

		ResponseModel responseModel = new ResponseModel();

		Map<String, String> stores = storeService.getAllStoresMap();

		responseModel.addModel("Stores", stores);
		return ApplicationUtils.buildOkResponse(responseModel);
	}

	@GetMapping("/locations")
	private ResponseEntity<ResponseModel> getLocations() {

		ResponseModel responseModel = new ResponseModel();

		Map<String, String> locations = accountsPayableService.getAllLocations();

		responseModel.addModel("Locations", locations);
		return ApplicationUtils.buildOkResponse(responseModel);
	}

}
