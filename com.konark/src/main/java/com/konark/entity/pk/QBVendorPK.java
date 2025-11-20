package com.konark.entity.pk;

import java.io.Serializable;

@SuppressWarnings("unused")
public class QBVendorPK implements Serializable {

	
	private String vendorRefId;
	private String storeID;

		    		  
	public QBVendorPK() {

	}

	public QBVendorPK(String vendorRefId, String storeID) {
		this.vendorRefId = vendorRefId;
		this.storeID = storeID;
	
	}
	
	
}
