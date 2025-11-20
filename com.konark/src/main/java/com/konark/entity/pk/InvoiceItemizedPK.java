package com.konark.entity.pk;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;

public class InvoiceItemizedPK implements Serializable {

	private String invoiceNumber;
	private String storeID;

	private String lineNumber;

	public InvoiceItemizedPK(String invoiceNumber, String lineNumber, String storeID) {
		this.invoiceNumber = invoiceNumber;
		this.lineNumber = lineNumber;
		this.storeID = storeID;

	}

}
