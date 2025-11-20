package com.konark.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.konark.model.GiftCardModel;
import com.konark.service.GiftCardService;
import com.konark.util.ApplicationUtils;
import com.konark.util.ResponseModel;

@RestController
@RequestMapping("/giftCard")
@Scope("prototype")
public class GiftCardController {

	@Autowired
	GiftCardService giftCardService;

	@GetMapping("/transactions")
	private ResponseEntity<ResponseModel> getGiftCardTransactions(@RequestParam("giftCardNumber") String giftCardNumber, @RequestParam("mobileNumber") String mobileNumber) {

		ResponseModel responseModel = new ResponseModel();

		GiftCardModel giftCardModel = giftCardService.findGiftCardTransactions(mobileNumber, giftCardNumber);
				
		responseModel.addModel(GiftCardModel.class.getSimpleName(), giftCardModel);
		return ApplicationUtils.buildOkResponse(responseModel);

	}

}
