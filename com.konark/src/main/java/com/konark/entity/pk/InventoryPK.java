package com.konark.entity.pk;

import java.io.Serializable;

@SuppressWarnings("unused")
public class InventoryPK implements Serializable {

	
	private String itemNumber;
	private String storeID;

	public InventoryPK() {

	}

	public InventoryPK(String itemNumber, String storeID) {
		this.itemNumber = itemNumber;
		this.storeID = storeID;
	
	}
	
}
