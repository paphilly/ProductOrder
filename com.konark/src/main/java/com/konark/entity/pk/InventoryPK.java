package com.konark.entity.pk;

import java.io.Serializable;
import java.util.Objects;

public class InventoryPK implements Serializable {

	private String itemNumber;
	private String storeID;

	public InventoryPK() {

	}

	public InventoryPK(String itemNumber, String storeID) {
		this.itemNumber = itemNumber;
		this.storeID = storeID;
	}

	public String getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}

	public String getStoreID() {
		return storeID;
	}

	public void setStoreID(String storeID) {
		this.storeID = storeID;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		InventoryPK that = (InventoryPK) o;
		return Objects.equals(itemNumber, that.itemNumber) && Objects.equals(storeID, that.storeID);
	}

	@Override
	public int hashCode() {
		return Objects.hash(itemNumber, storeID);
	}
}
