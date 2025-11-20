package com.konark.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.konark.entity.DepartmentEntity;
import com.konark.model.DepartmentModel;
import com.konark.repository.DepartmentRepository;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DepartmentService {

	@Autowired
	DepartmentRepository departmentRepository;

	@Autowired
	private DepartmentModel departmentModel;

	public DepartmentModel getAllDepartments() {
		
		List<DepartmentEntity> departmentsList = new ArrayList<DepartmentEntity>();
		Iterable<DepartmentEntity> departments = departmentRepository.findAll();
		departments.forEach(departmentsList::add);
		departmentModel.setDepartments(departmentsList);
		return departmentModel;
	}

	public void setDepartmentModel(DepartmentModel departmentModel) {
		this.departmentModel = departmentModel;
	}

	public DepartmentModel getDepartmentByID(String departmentID) {
		Optional<DepartmentEntity> departmentResult = departmentRepository.findById(departmentID);
		departmentResult.ifPresent(department -> departmentModel.getDepartments().add(department));
		return departmentModel;
	}

}
