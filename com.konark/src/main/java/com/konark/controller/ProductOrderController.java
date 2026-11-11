package com.konark.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.konark.model.ProductOrderModel;
import com.konark.model.DraftOrderSummaryModel;
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
	ResponseEntity<ResponseModel> newOrder(@RequestBody ProductOrderModel productOrderModel, Principal principal) {

		ResponseModel responseModel = new ResponseModel();
		productOrderModel = productOrderService.commitProductOrder(productOrderModel, principal.getName());

		responseModel.addModel("productOrderID", productOrderModel.getProductOrderID());
		responseModel.addModel("pdfBase64", productOrderModel.getPdfBase64());
		return ApplicationUtils.buildOkResponse(responseModel);

	}

	@PostMapping(value = "/draft", consumes = "application/json")
	ResponseEntity<ResponseModel> saveDraft(@RequestBody ProductOrderModel productOrderModel, Principal principal) {
		ResponseModel responseModel = new ResponseModel();
		productOrderModel = productOrderService.saveDraftProductOrder(productOrderModel, principal.getName());
		responseModel.addModel("productOrderID", productOrderModel.getProductOrderID());
		return ApplicationUtils.buildOkResponse(responseModel);
	}

	@GetMapping("/drafts")
	ResponseEntity<ResponseModel> getDrafts(Principal principal) {
		ResponseModel responseModel = new ResponseModel();
		List<DraftOrderSummaryModel> drafts = productOrderService.getDrafts(principal.getName());
		responseModel.addModel("drafts", drafts);
		return ApplicationUtils.buildOkResponse(responseModel);
	}

	@GetMapping("/drafts/{productOrderID}")
	ResponseEntity<ResponseModel> getDraft(@PathVariable String productOrderID, Principal principal) {
		ResponseModel responseModel = new ResponseModel();
		responseModel.addModel("draft", productOrderService.getDraft(productOrderID, principal.getName()));
		return ApplicationUtils.buildOkResponse(responseModel);
	}

}
