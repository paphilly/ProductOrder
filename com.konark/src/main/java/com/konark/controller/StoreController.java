package com.konark.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.konark.entity.StoreEntity;
import com.konark.service.StoreService;
import com.konark.util.ApplicationUtils;
import com.konark.util.ResponseModel;

@RestController
@RequestMapping("/stores")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StoreController {

	@Autowired
	StoreService storeService;
	
	@GetMapping("")
	private ResponseEntity<ResponseModel> getAllStores() {
		
		ResponseModel responseModel = new ResponseModel();
		Map<String,String> stores = new HashMap<String, String>();
		
		List<StoreEntity> storeEntities = storeService.getAllStores();

		
//		stores.put("Omaha", "1234");
//		stores.put("Namaste", "2345");
//		stores.put("DesMoines", "3456");
//		stores.put("Parker", "6789");
//		stores.put("Omaha Web", "1001");
//		stores.put("Denver", "5678");
//		    	  
		stores.put("Aurora", "7890");
		stores.put("Charlotte", "8901");
		stores.put("Denver", "5678");
		stores.put("Desmoines", "3456");
		stores.put("Maple", "4567");
		stores.put("Namaste", "2345");
		stores.put("Omaha", "1234");
		stores.put("Parker", "6789");
		stores.put("StLouis", "9012");

		responseModel.addModel("Stores", stores);
		return ApplicationUtils.buildOkResponse(responseModel);
	}
}

