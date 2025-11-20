package com.konark.model;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountsPayableModel {
	
	private String billNumber;

	private String billDate;

	private String comment;

	private String vendorID;

	private String vendorName;

	private BigDecimal totalAmount;

	private String dueDate;

	private BigDecimal balance;
	
	private boolean isPending;
	
	private boolean isOverdue;
	
	private String status;
	
	private String id;
	
	private String overrideDueDate;
	
	public boolean getIsPending() {
		return isPending;
	}

	public void setIsPending(boolean isPending) {
		this.isPending = isPending;
	}

	public boolean getIsOverdue() {
		return isOverdue;
	}

	public void setIsOverdue(boolean isOverdue) {
		this.isOverdue = isOverdue;
	}


	public String getBillNumber() {
		return billNumber;
	}

	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}

	public String getBillDate() {
		return billDate;
	}

	public void setBillDate(String billDate) {
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

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOverrideDueDate() {
		return overrideDueDate;
	}

	public void setOverrideDueDate(String overrideDueDate) {
		this.overrideDueDate = overrideDueDate;
	}
	
}
