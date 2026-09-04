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
		try {
			List<Object[]> salesHistory = salesHistoryRepository.findSalesHistoryByItemNumberAndStore(itemNumber, storeID);
			try (java.io.FileWriter fw = new java.io.FileWriter("C:/ProductOrder/sales_debug.log", true)) {
				fw.write("SUCCESS for item: " + itemNumber + " store: " + storeID + " rows: " + (salesHistory != null ? salesHistory.size() : 0) + "\n");
				if (salesHistory != null && !salesHistory.isEmpty()) {
					Object[] row = salesHistory.get(0);
					fw.write("ROW: " + java.util.Arrays.toString(row) + "\n");
				}
			} catch (Exception ignored) {}
			return salesHistory;
		} catch (Exception e) {
			try (java.io.FileWriter fw = new java.io.FileWriter("C:/ProductOrder/sales_debug.log", true)) {
				fw.write("ERROR for item: " + itemNumber + " store: " + storeID + " EXCEPTION: " + e.toString() + "\n");
				java.io.PrintWriter pw = new java.io.PrintWriter(fw);
				e.printStackTrace(pw);
			} catch (Exception ignored) {}
			return new java.util.ArrayList<>();
		}
	}

	public Map<String, String> getTotalSaleByStoreID(String storeID) {
		Map<String, String> salesMap = new HashMap<String, String>();
		try {
			List<Object[]> totalSales = salesHistoryRepository.getTotalSaleByStoreID(storeID);
			totalSales.stream().forEach(resultRow -> salesMap.put(((Date) resultRow[0]).toString(), ((BigDecimal) resultRow[1]).toString()));
		} catch (Exception e) {
			// Return empty map on error
		}
		return salesMap;
	}

}
