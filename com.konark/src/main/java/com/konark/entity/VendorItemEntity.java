package com.konark.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.konark.entity.pk.VendorItemPK;

@Entity
@Table(name = "Inventory_Vendors_v1")
@IdClass(VendorItemPK.class)
public class VendorItemEntity {

	@Id
	@Column(name = "Vendor_Number")
	private String vendorNumber;

	@Id
	@Column(name = "Vendor_Part_Num")
	private String vendorPartNumber;

	@Id
	@Column(name = "ItemNum")
	private String itemNumber;

	@Column(name = "CreateDate")
	private Date dateCreated;

	@Column(name = "ModifiedDate")
	private Date dateModified;

	@Column(name = "ItemName")
	private String itemName;

	@Column(name = "CostPer")
	private String costPerItem;

	@Column(name = "WeightCost")
	private String weightCost;

	@Column(name = "Case_Cost")
	private String caseCost;

	@Column(name = "NumPerVenCase")
	private String numberOfItemsPerVendorCase;

	@Id
	@Column(name = "Store_ID")
	private String storeID;

	public String getWeightCost() {

		return weightCost;
	}

	public Date getDateModified() {

		return dateModified;
	}

	public String getStoreID() {

		return storeID;
	}

	public String getCaseCost() {
		return caseCost;
	}

	public void setCaseCost(String caseCost) {
		this.caseCost = caseCost;
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

	public String getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

//	public String getDepartmentID() {
//		return departmentID;
//	}
//
//	public void setDepartmentID(String departmentID) {
//		this.departmentID = departmentID;
//	}

	public String getCostPerItem() {
		return costPerItem;
	}

	public void setCostPerItem(String costPerItem) {
		this.costPerItem = costPerItem;
	}

	public String getNumberOfItemsPerVendorCase() {
		return numberOfItemsPerVendorCase;
	}

	public void setNumberOfItemsPerVendorCase(String numberOfItemsPerVendorCase) {
		this.numberOfItemsPerVendorCase = numberOfItemsPerVendorCase;
	}

	public void setWeightCost( String weightCost ) {

		this.weightCost = weightCost;
	}

	public void setDateModified( Date dateModified ) {

		this.dateModified = dateModified;
	}

	public void setStoreID( String storeID ) {

		this.storeID = storeID;
	}
}
