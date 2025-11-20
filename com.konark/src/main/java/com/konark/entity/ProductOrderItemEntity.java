package com.konark.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "SW_ProductOrderItems")
public class ProductOrderItemEntity {

	// @ManyToOne(fetch=FetchType.LAZY)
	  @ManyToOne(fetch = FetchType.LAZY, optional = false)
	    @JoinColumn(name = "OrderID", nullable = false)
	private ProductOrderEntity productOrderEntity;

	public ProductOrderEntity getProductOrderEntity() {
		return productOrderEntity;
	}

	public void setProductOrderEntity(ProductOrderEntity productOrderEntity) {
		this.productOrderEntity = productOrderEntity;
	}

//	

//	@Column(name = "OrderID")
//	private Long orderID;
//
//	public Long getOrderID() {
//		return orderID;
//	}
//
//	public void setOrderID(Long orderID) {
//		this.orderID = orderID;
//	}

	@Id
	@GeneratedValue
	@Column(name = "OrderItemID")
	private Long orderItemID;

	@Column(name = "Vendor_Part_Num")
	private String vendorPartNumber;

	@Column(name = "ItemNum")
	private String itemNumber;

	@Column(name = "ItemName")
	private String itemName;

	@Column(name = "QtyOrdered")
	private String quantityOrdered;

	@Column(name = "CaseOrIndividual")
	private String caseOrIndividual;

	@Column(name = "NumPerCase")
	private String numberPerCase;

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

	public String getQuantityOrdered() {
		return quantityOrdered;
	}

	public void setQuantityOrdered(String quantityOrdered) {
		this.quantityOrdered = quantityOrdered;
	}

	public String getCaseOrIndividual() {
		return caseOrIndividual;
	}

	public void setCaseOrIndividual(String caseOrIndividual) {
		this.caseOrIndividual = caseOrIndividual;
	}

	public String getNumberPerCase() {
		return numberPerCase;
	}

	public void setNumberPerCase(String numberPerCase) {
		this.numberPerCase = numberPerCase;
	}

}
