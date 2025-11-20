package com.konark.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.konark.entity.DepartmentEntity;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DepartmentModel {
	
	private List<DepartmentEntity> departments = new ArrayList<DepartmentEntity>();

	public List<DepartmentEntity> getDepartments() {
		return departments;
	}

	public void setDepartments(List<DepartmentEntity> departments) {
		this.departments = departments;
	}
	
	

}
