package com.konark.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.konark.model.ProductOrderModel;
import com.konark.service.ProductOrderService;
import com.konark.util.ApplicationUtils;
import com.konark.util.ResponseModel;

@RestController
@RequestMapping("/productorder")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProductOrderController {

	@Autowired
	ProductOrderService productOrderService;

	@PostMapping(value = "", consumes = "application/json")
	ResponseEntity<ResponseModel> newOrder(@RequestBody ProductOrderModel productOrderModel) {

		ResponseModel responseModel = new ResponseModel();
		productOrderModel = productOrderService.saveProductOrder(productOrderModel);

		responseModel.addModel("productOrderID", productOrderModel.getProductOrderID());
		return ApplicationUtils.buildOkResponse(responseModel);

	}

}
