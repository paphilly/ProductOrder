package com.konark.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.konark.repository.SalesHistoryRepository;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SalesHistoryService {

	@Autowired
	SalesHistoryRepository salesHistoryRepository;

	public List<Object[]> getSalesHistorybyItemNumberAndStore(String itemNumber, String storeID) {

		List<Object[]> salesHistory = salesHistoryRepository.findSalesHistoryByItemNumberAndStore(itemNumber, storeID);

		return salesHistory;
	}

	public Map<String, String> getTotalSaleByStoreID(String storeID) {

		List<Object[]> totalSales = salesHistoryRepository.getTotalSaleByStoreID(storeID);

		Map<String, String> salesMap = new HashMap<String, String>();

		BigDecimal div = new BigDecimal(4);
		totalSales.stream().forEach(resultRow -> salesMap.put(((Date) resultRow[0]).toString(), (((BigDecimal)resultRow[1]).divide(div)).toString()));

		return salesMap;
	}

}
