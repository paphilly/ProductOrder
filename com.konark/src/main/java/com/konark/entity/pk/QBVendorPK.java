package com.konark.entity.pk;

import java.io.Serializable;
import java.util.Objects;

public class QBVendorPK implements Serializable {

	private String vendorRefId;
	private String storeID;

	public QBVendorPK() {

	}

	public QBVendorPK(String vendorRefId, String storeID) {
		this.vendorRefId = vendorRefId;
		this.storeID = storeID;
	}

	public String getVendorRefId() {
		return vendorRefId;
	}

	public void setVendorRefId(String vendorRefId) {
		this.vendorRefId = vendorRefId;
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
		QBVendorPK that = (QBVendorPK) o;
		return Objects.equals(vendorRefId, that.vendorRefId) && Objects.equals(storeID, that.storeID);
	}

	@Override
	public int hashCode() {
		return Objects.hash(vendorRefId, storeID);
	}
}
