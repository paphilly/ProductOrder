package com.konark.model;

import java.util.List;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProductOrderModel {

	private List<ProductItemModel> productItems;
	
	private String vendorNumber;
	
	private String storeID;
	
	private String productOrderID;
	private String pdfBase64;

	public List<ProductItemModel> getProductItems() {
		return productItems;
	}

	public void setProductItems(List<ProductItemModel> productItems) {
		this.productItems = productItems;
	}

	public String getVendorNumber() {
		return vendorNumber;
	}

	public void setVendorNumber(String vendorNumber) {
		this.vendorNumber = vendorNumber;
	}

	public String getStoreID() {
		return storeID;
	}

	public void setStoreID(String storeID) {
		this.storeID = storeID;
	}

	public String getProductOrderID() {
		return productOrderID;
	}

	public void setProductOrderID(String productOrderID) {
		this.productOrderID = productOrderID;
	}

	public String getPdfBase64() {
		return pdfBase64;
	}

	public void setPdfBase64(String pdfBase64) {
		this.pdfBase64 = pdfBase64;
	}
}
