package com.konark.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.konark.entity.VendorItemEntity;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class VendorItemModel {

	private List<VendorItemEntity> vendorItems = new ArrayList<VendorItemEntity>();

	public List<VendorItemEntity> getVendorItems() {
		return vendorItems;
	}

	public void setvendorItems(List<VendorItemEntity> vendorItems) {
		this.vendorItems = vendorItems;
	}

}
