package com.konark.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.konark.model.UserModel;
import com.konark.service.UserService;
import com.konark.util.ApplicationUtils;
import com.konark.util.ResponseModel;

@RestController
public class LoginController {
    @Autowired
    UserService userService;


    @PostMapping(value = "/login", consumes = "application/json")
    ResponseEntity<ResponseModel> newOrder(@RequestBody UserModel userModel) {
        ResponseModel responseModel = new ResponseModel();
        UserModel userModel1 = userService.processLogin(userModel);
        if(userModel1 != null) {
        	 responseModel.addModel(UserModel.class.getSimpleName(),userModel1);
        	 return ApplicationUtils.buildOkResponse(responseModel);
        }else {
        	responseModel.addModel("isLoginSuccess",false);
        	return ApplicationUtils.buildOkResponse(responseModel);
        }
       
        

    }


}
