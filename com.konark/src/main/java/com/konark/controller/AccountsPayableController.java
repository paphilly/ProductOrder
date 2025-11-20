package com.konark.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.konark.model.AccountsPayableModel;
import com.konark.service.AccountsPayableService;
import com.konark.util.ApplicationUtils;
import com.konark.util.ExceptionUtil;
import com.konark.util.ResponseModel;

@RestController
@RequestMapping("/accountspayable")
@Scope("prototype")
public class AccountsPayableController {

	@Autowired
	AccountsPayableService accountsPayableService;

	@GetMapping("/bills")
	private ResponseEntity<ResponseModel> getAccountsPayableByLocation(
			@RequestParam(value = "locations[]") String[] locations) {

		ResponseModel responseModel = new ResponseModel();

		List<AccountsPayableModel> accountsPayableList = accountsPayableService
				.getAccountsPayableForPastSixtyDaysByLocations(locations);

		Comparator<AccountsPayableModel> dateComparator = (AccountsPayableModel bill1,
				AccountsPayableModel bill2) -> bill2.getBillDate().compareTo(bill1.getBillDate());

		Collections.sort(accountsPayableList, dateComparator);

		responseModel.addModel("AccountsPayable", accountsPayableList);
		return ApplicationUtils.buildOkResponse(responseModel);

	}

	@PutMapping(value = "", consumes = "application/json")
	ResponseEntity<ResponseModel> updateAccountPayable(@RequestBody AccountsPayableModel accountsPayableModel) {

		ResponseModel responseModel = new ResponseModel();
		try {
			accountsPayableModel = accountsPayableService.updateAccountPayable(accountsPayableModel);
			responseModel.addModel("AccountsPayable", accountsPayableModel);
			return ApplicationUtils.buildOkResponse(responseModel);
		} catch (ExceptionUtil exception) {
			responseModel.addModel("Error", exception.getErrorModel());
			return ApplicationUtils.buildErrorResponse(exception.getErrorModel().getStatus(), responseModel);
		}

	}

	@GetMapping("/locations")
	private ResponseEntity<ResponseModel> getAccountsPayableLocations() {

		ResponseModel responseModel = new ResponseModel();

		Map<String, String> locations = accountsPayableService.getAccountsPayableLocations();

		responseModel.addModel("Locations", locations);
		return ApplicationUtils.buildOkResponse(responseModel);
	}

	@GetMapping("/vendors")
	private ResponseEntity<ResponseModel> getAccountsPayableVendorsByLocation(
			@RequestParam(value = "locations[]") String[] locations) {

		ResponseModel responseModel = new ResponseModel();

		Map<String, String> vendors = accountsPayableService.getAccountsPayableVendorsByLocation(locations);

		responseModel.addModel("Vendors", vendors);
		return ApplicationUtils.buildOkResponse(responseModel);
	}

//	@GetMapping("/bills/deprecated")
//	private ResponseEntity<ResponseModel> getAccountsPayableByLocationDeprecated(@RequestParam("location") String location)	 {
//		
//		ResponseModel responseModel = new ResponseModel();
//		List<AccountsPayableModel> accountsPayableList = accountsPayableService.getAccountsPayableForPastSixtyDaysByLocation(location);
//		responseModel.addModel("AccountsPayable", accountsPayableList);
//		return ApplicationUtils.buildOkResponse(responseModel);
//	}

}
