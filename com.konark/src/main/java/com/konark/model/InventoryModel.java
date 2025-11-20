package com.konark.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.konark.entity.InventoryEntity;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InventoryModel {
	
	private List<InventoryEntity> inventory = new ArrayList<InventoryEntity>();

	public List<InventoryEntity> getInventory() {
		return inventory;
	}

	public void setInventory(List<InventoryEntity> inventory) {
		this.inventory = inventory;
	}
	
	

}
