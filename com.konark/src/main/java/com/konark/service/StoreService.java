package com.konark.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.konark.entity.StoreEntity;
import com.konark.repository.StoreRepository;

@Component
public class StoreService {

	@Autowired
	StoreRepository storeRepository;

	private List<StoreEntity> stores;

	public List<StoreEntity> getAllStores() {
		if (stores == null || stores.size() == 0) {

			stores = new ArrayList<StoreEntity>();
			Iterable<StoreEntity> storeResults = storeRepository.findAll();
			storeResults.forEach(stores::add);
		} else {
			return stores;
		}
		return stores;
	}

	public Map<String, String> getAllStoresMap() {

		Map<String, String> storesMap = new HashMap<String, String>();
		if (stores == null || stores.size() == 0) {

			stores = new ArrayList<StoreEntity>();
			Iterable<StoreEntity> storeResults = storeRepository.findAll();
			storeResults.forEach(storeEntity -> {
				storesMap.put(storeEntity.getStoreName(), storeEntity.getStoreID());
			});
		} else {
			stores.forEach(storeEntity -> {
				storesMap.put(storeEntity.getStoreName(), storeEntity.getStoreID());
			});
		}
		return storesMap;
	}

	public Map<String, String> getStoresMapWitKeyID() {

		Map<String, String> storesMap = new HashMap<String, String>();
		if (stores == null || stores.size() == 0) {

			stores = new ArrayList<StoreEntity>();
			Iterable<StoreEntity> storeResults = storeRepository.findAll();
			storeResults.forEach(storeEntity -> {
				storesMap.put(storeEntity.getStoreID(), storeEntity.getStoreName());
			});
		} else {
			stores.forEach(storeEntity -> {
				storesMap.put(storeEntity.getStoreID(), storeEntity.getStoreName());
			});
		}
		return storesMap;
	}

}
