package com.konark.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.konark.entity.ProductOrderEntity;
import com.konark.entity.ProductOrderItemEntity;
import com.konark.entity.StoreEntity;
import com.konark.entity.VendorEntity;
import com.konark.model.ProductItemModel;
import com.konark.model.ProductOrderModel;
import com.konark.repository.ProductOrderRepository;
import com.konark.repository.StoreRepository;
import com.konark.repository.VendorRepository;
import com.konark.util.EmailUtil;
import com.konark.util.PDFUtil;
import com.lowagie.text.DocumentException;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProductOrderService {

	@Autowired
	ProductOrderRepository productOrderRepository;

	@Autowired
	VendorRepository vendorRepository;

	@Autowired
	StoreRepository storeRepository;

	@Autowired
	private ProductOrderModel productOrderModel;

	@Autowired
	EmailUtil emailUtil;

	public ProductOrderModel saveProductOrder(ProductOrderModel productOrderModel) {

		Date orderDate = new Date();
		BigDecimal totalCost = new BigDecimal(0);

		ProductOrderEntity newProductOrder = new ProductOrderEntity();
		newProductOrder.setOrderDate(orderDate);
		newProductOrder.setStoreID(productOrderModel.getStoreID());
		newProductOrder.setVendorNumber(productOrderModel.getVendorNumber());

		for (ProductItemModel productItemModel : productOrderModel.getProductItems()) {
			ProductOrderItemEntity productOrderItem = new ProductOrderItemEntity();
			productOrderItem.setItemName(productItemModel.getItemName());
			productOrderItem.setItemNumber(productItemModel.getItemNumber());
			productOrderItem.setVendorPartNumber(productItemModel.getVendorPartNumber());

			productOrderItem.setQuantityOrdered(productItemModel.getQuantityOrdered());
			productOrderItem.setProductOrderEntity(newProductOrder);

			newProductOrder.getProductOrderItems().add(productOrderItem);

			if (productItemModel.getQuantityOrdered() != null && productItemModel.getCaseCost() != null) {

				BigDecimal cost = new BigDecimal(productItemModel.getCaseCost());
				BigDecimal quant = new BigDecimal(productItemModel.getQuantityOrdered());
				BigDecimal itemTotal = cost.multiply(quant);

				productItemModel.setItemTotal(itemTotal.stripTrailingZeros());
				totalCost = totalCost.add(itemTotal);

			}

		}

		newProductOrder.setTotalCost(totalCost);
		productOrderRepository.save(newProductOrder);		
		newProductOrder.setTotalCost(totalCost.stripTrailingZeros());
		productOrderModel.setProductOrderID(newProductOrder.getOrderID().toString());
		Optional<StoreEntity> store = storeRepository.findById(productOrderModel.getStoreID());
		Optional<VendorEntity> vendor = vendorRepository.findById(productOrderModel.getVendorNumber());
		String filename = "ProductOrder-" + newProductOrder.getOrderID().toString();
		String generatedHtml = PDFUtil.parseProductOrderTemplate(productOrderModel, store.get(), vendor.get(), orderDate,
				totalCost.stripTrailingZeros());

		try {
			PDFUtil.generatePdfFromHtml(generatedHtml, filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		emailUtil.sendEmail(filename);

		return productOrderModel;
	}
}
