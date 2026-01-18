package com.konark.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.konark.model.UserModel;
import com.konark.service.UserService;
import com.konark.util.ApplicationUtils;
import com.konark.util.JwtUtil;
import com.konark.util.ResponseModel;

@RestController
public class LoginController {

	@Autowired
	UserService userService;

	@Autowired
	private JwtUtil jwtUtil;

	//
	// @PostMapping(value = "/login", consumes = "application/json")
	// ResponseEntity<ResponseModel> newOrder(@RequestBody UserModel userModel) {
	// ResponseModel responseModel = new ResponseModel();
	// UserModel userModel1 = userService.processLogin(userModel);
	// if(userModel1 != null) {
	// responseModel.addModel(UserModel.class.getSimpleName(),userModel1);
	// return ApplicationUtils.buildOkResponse(responseModel);
	// }else {
	// responseModel.addModel("isLoginSuccess",false);
	// return ApplicationUtils.buildOkResponse(responseModel);
	// }
	// }

	// @PostMapping(value = "/login", consumes = "application/json")
	// ResponseEntity<ResponseModel> newOrder(@RequestBody UserModel userModel) {
	// ResponseModel responseModel = new ResponseModel();
	// UserModel userModel1 = userService.processLogin(userModel);
	// if(userModel1 != null) {
	// responseModel.addModel(UserModel.class.getSimpleName(),userModel1);
	// return ApplicationUtils.buildOkResponse(responseModel);
	// }else {
	// responseModel.addModel("isLoginSuccess",false);
	// return ApplicationUtils.buildOkResponse(responseModel);
	// }
	// }

	@PostMapping(value = "/login", consumes = "application/json")
	public ResponseEntity<ResponseModel> login(@RequestBody UserModel userModel) {

		userModel = userService.findUser(userModel);
		ResponseModel responseModel = new ResponseModel();
		if (userModel != null) {
			String token = jwtUtil.generateToken(userModel);
			responseModel.getData().put("token", token);
			responseModel.setMessage("Login Success");
			// Removed userModel from response as per requirement
			return ResponseEntity.ok(responseModel);
		}
		responseModel.setMessage("Invalid Username or Password");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseModel);

	}

	@org.springframework.web.bind.annotation.GetMapping("/user/details")
	public ResponseEntity<ResponseModel> getUserDetails(java.security.Principal principal) {
		String username = principal.getName();
		UserModel userModel = userService.getUserByUsername(username);
		if (userModel != null) {
			ResponseModel responseModel = new ResponseModel();
			responseModel.getData().put("userModel", userModel);
			return ResponseEntity.ok(responseModel);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}
}
