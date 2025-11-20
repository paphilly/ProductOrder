package com.konark.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Store")
public class StoreEntity {

	@Id
	@Column(name = "Store_ID")
	private String storeID;

	@Column(name = "Store_Name")
	private String storeName;

	public String getStoreID() {
		return storeID;
	}

	public void setStoreID(String storeID) {
		this.storeID = storeID;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getAddressInfo() {
		return addressInfo;
	}

	public void setAddressInfo(String addressInfo) {
		this.addressInfo = addressInfo;
	}

	public String getAddressInfo1() {
		return addressInfo1;
	}

	public void setAddressInfo1(String addressInfo1) {
		this.addressInfo1 = addressInfo1;
	}

	public String getAddressInfo2() {
		return addressInfo2;
	}

	public void setAddressInfo2(String addressInfo2) {
		this.addressInfo2 = addressInfo2;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "Address_Info")
	private String addressInfo;

	@Column(name = "Address_Info_1")
	private String addressInfo1;

	@Column(name = "Address_Info_2")
	private String addressInfo2;

	@Column(name = "Email")
	private String email;

}
