package com.konark.controller;


import com.konark.model.OrderModel;
import com.konark.service.ApplicationService;
import com.konark.util.ApplicationUtils;
import com.konark.util.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApplicationController {
    @Autowired
    ApplicationService orderService;
    @GetMapping("/testGET")
    private ResponseEntity<ResponseModel> testGET() {
        OrderModel orderModel = new OrderModel();
        orderService.seModel(orderModel);
        ResponseModel responseModel = new ResponseModel();
        orderModel.setReslutList(orderService.getDepts());
        responseModel.addModel(OrderModel.class.getSimpleName(), orderModel);
        return ApplicationUtils.buildOkResponse(responseModel);
    }



}
