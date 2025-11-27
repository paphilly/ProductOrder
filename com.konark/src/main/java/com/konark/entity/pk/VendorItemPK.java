package com.konark.entity.pk;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("unused")
public class VendorItemPK implements Serializable {

	private String vendorNumber;
	private String vendorPartNumber;

	public VendorItemPK() {

	}

	public VendorItemPK(String vendorNumber, String vendorPartNumber) {
		this.vendorNumber = vendorNumber;
		this.vendorPartNumber = vendorPartNumber;


	}
}
