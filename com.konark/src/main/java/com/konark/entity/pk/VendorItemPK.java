package com.konark.entity.pk;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("unused")
public class VendorItemPK implements Serializable {

	private String storeID;
	private String itemNumber;
	private String vendorNumber;
	private String vendorPartNumber;

	public VendorItemPK() {

	}

	public VendorItemPK(String vendorNumber, String vendorPartNumber, String itemNumber, String storeID) {
		this.vendorNumber = vendorNumber;
		this.vendorPartNumber = vendorPartNumber;
		this.itemNumber = itemNumber;
		this.storeID = storeID;

	}
}
