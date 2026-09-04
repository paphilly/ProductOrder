package com.konark.entity.pk;

import java.io.Serializable;
import java.util.Objects;

public class InvoiceItemizedPK implements Serializable {

	private String invoiceNumber;
	private String lineNumber;
	private String storeID;

	public InvoiceItemizedPK() {

	}

	public InvoiceItemizedPK(String invoiceNumber, String lineNumber, String storeID) {
		this.invoiceNumber = invoiceNumber;
		this.lineNumber = lineNumber;
		this.storeID = storeID;
	}

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		InvoiceItemizedPK that = (InvoiceItemizedPK) o;
		return Objects.equals(invoiceNumber, that.invoiceNumber) &&
				Objects.equals(lineNumber, that.lineNumber) &&
				Objects.equals(storeID, that.storeID);
	}

	@Override
	public int hashCode() {
		return Objects.hash(invoiceNumber, lineNumber, storeID);
	}
}
