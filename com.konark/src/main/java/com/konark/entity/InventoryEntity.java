package com.konark.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.konark.entity.pk.InventoryPK;

@Entity
@Table(name = "vw_AllInventory")
@IdClass(InventoryPK.class)
public class InventoryEntity {

	@Id
	@Column(name = "ItemNum")
	private String itemNumber;
	
	@Id
	@Column(name = "Store_ID")
	private String storeID;
	
	@Column(name = "Vendor_Number")
	private String vendorNumber;
	
	@Column(name = "Vendor_Part_Num")
	private String vendorPartNumber;
	
	@Column(name = "In_Stock")
	private BigDecimal quantityInStock;
	
	@Column(name= "ItemName")
	private String itemName;

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

	public String getVendorNumber() {
		return vendorNumber;
	}

	public void setVendorNumber(String vendorNumber) {
		this.vendorNumber = vendorNumber;
	}

	public String getVendorPartNumber() {
		return vendorPartNumber;
	}

	public void setVendorPartNumber(String vendorPartNumber) {
		this.vendorPartNumber = vendorPartNumber;
	}

	public BigDecimal getQuantityInStock() {
		return quantityInStock;
	}

	public void setQuantityInStock(BigDecimal quantityInStock) {
		this.quantityInStock = quantityInStock;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	

					
	


}
