package com.konark.model;

import java.math.BigDecimal;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProductItemModel {

	private String vendorNumber;

	private String vendorPartNumber;

	private String itemNumber;

	private String itemName;

	private String department;

	private String costPerItem;

	private String unitMeasure;

	private String quantityOrdered;

	private String numberPerCase;

	private BigDecimal quantityInStock;

	private String vendorName;

	private String caseCost;

	private String vendorItemName;

	private BigDecimal itemTotal;

	private boolean inventoryFlag = false;

	public String getCaseCost() {
		return caseCost;
	}

	public void setCaseCost(String caseCost) {
		this.caseCost = caseCost;
	}

	public String getVendorItemName() {
		return vendorItemName;
	}

	public void setVendorItemName(String vendorItemName) {
		this.vendorItemName = vendorItemName;
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

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

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

	public String getQuantityOrdered() {
		return quantityOrdered;
	}

	public void setQuantityOrdered(String quantityOrdered) {
		this.quantityOrdered = quantityOrdered;
	}

	public String getNumberPerCase() {
		return numberPerCase;
	}

	public void setNumberPerCase(String numberPerCase) {
		this.numberPerCase = numberPerCase;
	}

	public BigDecimal getQuantityInStock() {
		return quantityInStock;
	}

	public void setQuantityInStock(BigDecimal quantityInStock) {
		this.quantityInStock = quantityInStock;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public boolean getInventoryFlag() {
		return inventoryFlag;
	}

	public void setInventoryFlag(boolean inventoryFlag) {
		this.inventoryFlag = inventoryFlag;
	}

	public BigDecimal getItemTotal() {

		return itemTotal;
	}

	public void setItemTotal(BigDecimal itemTotal) {
		this.itemTotal = itemTotal;
	}

}
