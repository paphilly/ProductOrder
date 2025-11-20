package com.konark.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.konark.model.DepartmentModel;
import com.konark.service.DepartmentService;
import com.konark.util.ApplicationUtils;
import com.konark.util.ResponseModel;

@RestController
@RequestMapping("/departments")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DepartmentController {

	@Autowired
	DepartmentService departmentService;

	@GetMapping("")
	private ResponseEntity<ResponseModel> getDepartments() {
		
		ResponseModel responseModel = new ResponseModel();
		DepartmentModel departmentsModel = departmentService.getAllDepartments();
		responseModel.addModel(DepartmentModel.class.getSimpleName(), departmentsModel);
		return ApplicationUtils.buildOkResponse(responseModel);
	}
	
	@GetMapping("/{id}")
	private ResponseEntity<ResponseModel> getDepartmentByID(@PathVariable String departmentID)	 {
		
		ResponseModel responseModel = new ResponseModel();
		DepartmentModel departmentsModel = departmentService.getDepartmentByID(departmentID);
		responseModel.addModel(DepartmentModel.class.getSimpleName(), departmentsModel);
		return ApplicationUtils.buildOkResponse(responseModel);
	}

}
