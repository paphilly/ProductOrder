package com.konark.entity.pk;

import java.io.Serializable;
import java.util.Objects;

public class VendorItemPK implements Serializable {

	private String vendorNumber;
	private String vendorPartNumber;

	public VendorItemPK() {

	}

	public VendorItemPK(String vendorNumber, String vendorPartNumber) {
		this.vendorNumber = vendorNumber;
		this.vendorPartNumber = vendorPartNumber;
	}

	public String getVendorNumber() {
		return vendorNumber;
	}

	public void setVendorNumber(String vendorNumber) {
		this.vendorNumber = vendorNumber;
	}

	public String getVendorPartNumber() {
		return vendorPartNumber;
	}

	public void setVendorPartNumber(String vendorPartNumber) {
		this.vendorPartNumber = vendorPartNumber;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VendorItemPK that = (VendorItemPK) o;
		return Objects.equals(vendorNumber, that.vendorNumber) && Objects.equals(vendorPartNumber, that.vendorPartNumber);
	}

	@Override
	public int hashCode() {
		return Objects.hash(vendorNumber, vendorPartNumber);
	}
}
