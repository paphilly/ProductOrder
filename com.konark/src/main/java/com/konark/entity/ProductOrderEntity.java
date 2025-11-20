package com.konark.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "SW_ProductOrders")
public class ProductOrderEntity {

	// @OneToMany(mappedBy = "orderID", cascade = CascadeType.ALL, orphanRemoval =
	// true)
//	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
//	@JoinColumn(name = "OrderID")
	
	

    @OneToMany(mappedBy = "productOrderEntity", fetch = FetchType.LAZY,  cascade = CascadeType.ALL)
	private List<ProductOrderItemEntity> productOrderItems = new ArrayList<>();

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "OrderID")
	private Long orderID;

	public Long getOrderID() {
		return orderID;
	}

	public void setOrderID(Long orderID) {
		this.orderID = orderID;
	}

	@Column(name = "Vendor_Number")
	private String vendorNumber;

	@Column(name = "Store_ID")
	private String storeID;

	@Column(name = "OrderDate")
	private Date orderDate;

	@Column(name = "TotalCost")
	private BigDecimal totalCost;

//	public String getOrderID() {
//		return OrderID;
//	}
//
//	public void setOrderID(String orderID) {
//		OrderID = orderID;
//	}

	public BigDecimal getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(BigDecimal totalCost) {
		this.totalCost = totalCost;
	}

	public String getVendorNumber() {
		return vendorNumber;
	}

	public void setVendorNumber(String vendorNumber) {
		this.vendorNumber = vendorNumber;
	}

	public String getStoreID() {
		return storeID;
	}

	public void setStoreID(String storeID) {
		this.storeID = storeID;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public List<ProductOrderItemEntity> getProductOrderItems() {
		return productOrderItems;
	}

	public void setProductOrderItems(List<ProductOrderItemEntity> productOrderItems) {
		this.productOrderItems = productOrderItems;
	}

}
