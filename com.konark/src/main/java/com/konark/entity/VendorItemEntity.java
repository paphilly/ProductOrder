package com.konark.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.konark.entity.pk.VendorItemPK;

@Entity
@Table(name = "SW_VendorItems")
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

	@Id
	@Column(name = "Date_Created")
	private Date dateCreated;

	@Column(name = "ItemName")
	private String itemName;

	@Column(name = "Dept_ID")
	private String departmentID;

	@Column(name = "CostPer")
	private String costPerItem;
	

	@Column(name = "vendorItemName")
	private String vendorItemName;

	@Column(name = "Case_Cost")
	private String caseCost;
	
	@Column(name = "UnitMeasure")
	private String unitMeasure;

	@Column(name = "NumPerVenCase")
	private String numberOfItemsPerVendorCase;
	
	@Column(name = "vendorDept")
	private String department;
	
	
	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getVendorItemName() {
		return vendorItemName;
	}

	public void setVendorItemName(String vendorItemName) {
		this.vendorItemName = vendorItemName;
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

	public String getUnitMeasure() {
		return unitMeasure;
	}

	public void setUnitMeasure(String unitMeasure) {
		this.unitMeasure = unitMeasure;
	}

	public String getNumberOfItemsPerVendorCase() {
		return numberOfItemsPerVendorCase;
	}

	public void setNumberOfItemsPerVendorCase(String numberOfItemsPerVendorCase) {
		this.numberOfItemsPerVendorCase = numberOfItemsPerVendorCase;
	}

}
