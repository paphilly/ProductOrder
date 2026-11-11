package com.konark.model;

import java.math.BigDecimal;
import java.util.Date;

public class DraftOrderSummaryModel {
	private String productOrderID;
	private String storeID;
	private String storeName;
	private String vendorNumber;
	private String vendorName;
	private Date orderDate;
	private BigDecimal totalCost;

	public String getProductOrderID() { return productOrderID; }
	public void setProductOrderID(String productOrderID) { this.productOrderID = productOrderID; }
	public String getStoreID() { return storeID; }
	public void setStoreID(String storeID) { this.storeID = storeID; }
	public String getStoreName() { return storeName; }
	public void setStoreName(String storeName) { this.storeName = storeName; }
	public String getVendorNumber() { return vendorNumber; }
	public void setVendorNumber(String vendorNumber) { this.vendorNumber = vendorNumber; }
	public String getVendorName() { return vendorName; }
	public void setVendorName(String vendorName) { this.vendorName = vendorName; }
	public Date getOrderDate() { return orderDate; }
	public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
	public BigDecimal getTotalCost() { return totalCost; }
	public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
}
