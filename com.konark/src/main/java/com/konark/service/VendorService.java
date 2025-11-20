package com.konark.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.konark.entity.VendorEntity;
import com.konark.model.VendorModel;
import com.konark.repository.VendorRepository;

@Component
@Scope("prototype")
public class VendorService {

	@Autowired
	VendorRepository vendorRepository;

	@Autowired
	private VendorModel vendorModel;

	public VendorModel getAllVendors() {

		List<VendorEntity> vendorsList = new ArrayList<VendorEntity>();
		Iterable<VendorEntity> vendors = vendorRepository.findAll();
		vendors.forEach(vendorsList::add);
		vendorModel.setVendors(vendorsList);
		return vendorModel;
	}
}
