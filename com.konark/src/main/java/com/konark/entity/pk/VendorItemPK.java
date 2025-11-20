package com.konark.entity.pk;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("unused")
public class VendorItemPK implements Serializable {

	private String vendorNumber;
	private String vendorPartNumber;
	private String itemNumber;
	private Date dateCreated;

	public VendorItemPK() {

	}

	public VendorItemPK(String vendorNumber, String vendorPartNumber, String itemNumber, Date dateCreated) {
		this.vendorNumber = vendorNumber;
		this.vendorPartNumber = vendorPartNumber;
		this.itemNumber = itemNumber;
		this.dateCreated = dateCreated;
	}
}
