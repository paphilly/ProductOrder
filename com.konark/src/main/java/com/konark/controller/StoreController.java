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
	
	@GetMapping({"", "/"})
	public ResponseEntity<ResponseModel> getAllStores() {
		
		ResponseModel responseModel = new ResponseModel();
		Map<String,String> stores = new HashMap<String, String>();
		
		List<StoreEntity> storeEntities = storeService.getAllStores();

		// Central.dbo.Store is the source of truth. Keep Maple as a compatibility
		// entry for deployments whose Store row has not been populated yet.
		for (StoreEntity storeEntity : storeEntities) {
			if (storeEntity.getStoreName() != null && storeEntity.getStoreID() != null) {
				stores.put(storeEntity.getStoreName(), storeEntity.getStoreID());
			}
		}
		stores.putIfAbsent("Maple", "4567");
		// The physical source database is Charlotte; this is the North Carolina
		// store identifier used by the order application.
		stores.putIfAbsent("North Carolina", "8901");

		responseModel.addModel("Stores", stores);
		return ApplicationUtils.buildOkResponse(responseModel);
	}
}
