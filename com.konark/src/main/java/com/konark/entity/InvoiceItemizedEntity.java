package com.konark.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.konark.entity.pk.InvoiceItemizedPK;

@Entity
@Table(name = "vw_AllInvoiceItemized")
@IdClass(InvoiceItemizedPK.class)
public class InvoiceItemizedEntity {
	
	@Id
	@Column(name = "Invoice_Number")
	private String invoiceNumber;

	@Id
	@Column(name = "LineNum")
	private String lineNumber;


	@Id
	@Column(name = "Store_ID")
	private String storeID;


	
	@Column(name = "ItemNum")
	private String itemNumber;
	
	@Column(name = "Quantity")
	private String quantity;
	
	@Column(name = "CostPer")
	private String costPer;
	
	@Column(name = "PricePer")
	private String pricePer;
	
	@Column(name = "DiffItemName")
	private String diffItemName;
	
	@Column(name = "ModifiedDate")
	private Date transactionDate;

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public String getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getStoreID() {
		return storeID;
	}

	public void setStoreID(String storeID) {
		this.storeID = storeID;
	}

	public String getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getCostPer() {
		return costPer;
	}

	public void setCostPer(String costPer) {
		this.costPer = costPer;
	}

	public String getPricePer() {
		return pricePer;
	}

	public void setPricePer(String pricePer) {
		this.pricePer = pricePer;
	}

	public String getDiffItemName() {
		return diffItemName;
	}

	public void setDiffItemName(String diffItemName) {
		this.diffItemName = diffItemName;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}
	
	

}

