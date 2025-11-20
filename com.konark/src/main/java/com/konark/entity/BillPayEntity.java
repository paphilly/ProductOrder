package com.konark.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Bill")
public class BillPayEntity {

	@Id
	@Column(name = "Id")
	private String billID;

	@Column(name = "DocNumber")
	private String billNumber;

	@Column(name = "TxnDate")
	private Date billDate;

	@Column(name = "PrivateNote")
	private String comment;

	@Column(name = "VendorRefId")
	private String vendorID;

	@Column(name = "VendorRefName")
	private String vendorName;

	@Column(name = "TotalAmt")
	private BigDecimal totalAmount;

	@Column(name = "DueDate")
	private Date dueDate;

	@Column(name = "Balance")
	private BigDecimal balance;
	
	@Column(name = "Status")
	private String status;
	
	@Column(name = "OverrideDueDate")
	private Date overrideDueDate;
	
	@Column(name = "SalesTermRefName")
	private String location;

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getBillID() {
		return billID;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getOverrideDueDate() {
		return overrideDueDate;
	}

	public void setOverrideDueDate(Date overrideDueDate) {
		this.overrideDueDate = overrideDueDate;
	}

	public void setBillID(String billID) {
		this.billID = billID;
	}

	public String getBillNumber() {
		return billNumber;
	}

	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}

	public Date getBillDate() {
		return billDate;
	}

	public void setBillDate(Date billDate) {
		this.billDate = billDate;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getVendorID() {
		return vendorID;
	}

	public void setVendorID(String vendorID) {
		this.vendorID = vendorID;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

}
