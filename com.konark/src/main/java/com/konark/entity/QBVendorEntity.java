package com.konark.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.konark.entity.pk.QBVendorPK;

@Entity
@Table(name = "QB_Vendor")
@IdClass(QBVendorPK.class)
public class QBVendorEntity {

	@Id
	@Column(name = "VendorRefId")
	private String vendorRefId;
	
	@Id
	@Column(name = "StoreId")
	private String storeID;
	
	@Column(name = "VendorRefName")
	private String vendorRefName;
	
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

	public String getVendorRefName() {
		return vendorRefName;
	}

	public void setVendorRefName(String vendorRefName) {
		this.vendorRefName = vendorRefName;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	@Column(name= "StoreName")
	private String storeName;
	
}
