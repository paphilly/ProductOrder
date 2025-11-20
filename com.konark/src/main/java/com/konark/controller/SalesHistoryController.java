package com.konark.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.konark.service.SalesHistoryService;
import com.konark.util.ApplicationUtils;
import com.konark.util.ResponseModel;

@RestController
@RequestMapping("/salesHistory")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SalesHistoryController {

	@Autowired
	SalesHistoryService salesHistoryService;

	@GetMapping("")
	private ResponseEntity<ResponseModel> getVendorItemsForProductOrder(@RequestParam("itemNumber") String itemNumber,
			@RequestParam("storeID") String storeID) {

		ResponseModel responseModel = new ResponseModel();

		List<Object[]> sales = salesHistoryService.getSalesHistorybyItemNumberAndStore(itemNumber, storeID);

		List<Object> salesArray = new ArrayList<Object>();
		DecimalFormat df = new DecimalFormat("#0.00");
		Map<String, String> itemSaleHistory = new HashMap<String, String>();

		if (sales != null & sales.size() > 0) {
			Object[] salesColumns = sales.get(0);

		//	Strng itemNum = (String) salesColumns[0];
			String itemName = (String) salesColumns[1];
			BigDecimal seven = new BigDecimal(0.00);
			BigDecimal fifteen = new BigDecimal(0.00);
			BigDecimal thirty = new BigDecimal(0.00);
			if(salesColumns[2]!=null) {
				seven = (BigDecimal) salesColumns[2];
			}
			if(salesColumns[3]!=null) {
				fifteen = (BigDecimal) salesColumns[3];
			}
			if(salesColumns[4]!=null) {
				thirty = (BigDecimal) salesColumns[4];
			}
//			String sevenDay = (salesColumns[2]).toString();
//			String fifteenDay = (salesColumns[3]).toString();
//			String thirtyDay = (salesColumns[4]).toString();

			itemSaleHistory.put("itemNumber", "ItemNumber: " + itemNumber);
			itemSaleHistory.put("sevenDay", df.format(seven));
			itemSaleHistory.put("fifteenDay", df.format(fifteen));
			itemSaleHistory.put("thirtyDay", df.format(thirty));
			itemSaleHistory.put("vendorItemName", itemName);

		} else {

			itemSaleHistory.put("itemNumber", "ItemNumber: " + itemNumber);
			itemSaleHistory.put("sevenDay", "0");
			itemSaleHistory.put("fifteenDay", "0");
			itemSaleHistory.put("thirtyDay", "0");
			itemSaleHistory.put("vendorItemName", "NO SALE");
		}

		salesArray.add(itemSaleHistory);
		responseModel.addModel("Sales", salesArray);
		return ApplicationUtils.buildOkResponse(responseModel);
	}
	
	@GetMapping("/totalSales")
	private ResponseEntity<ResponseModel> getTotalSalesByStoreID(@RequestParam("storeID") String storeID) {

		ResponseModel responseModel = new ResponseModel();

		Map<String, String> sales = salesHistoryService.getTotalSaleByStoreID(storeID);

		responseModel.addModel("Sales", sales);
		return ApplicationUtils.buildOkResponse(responseModel);
	}


}
