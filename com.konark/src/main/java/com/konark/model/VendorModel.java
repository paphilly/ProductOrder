package com.konark.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.konark.entity.VendorEntity;

@Component
@Scope("prototype")
public class VendorModel {

	private List<VendorEntity> vendors = new ArrayList<VendorEntity>();

	public List<VendorEntity> getVendors() {
		return vendors;
	}

	public void setVendors(List<VendorEntity> vendors) {
		this.vendors = vendors;
	}
	
	

}
