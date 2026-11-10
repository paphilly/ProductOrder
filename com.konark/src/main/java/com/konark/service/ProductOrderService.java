package com.konark.service;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;

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
import com.konark.model.DraftOrderSummaryModel;
import com.konark.repository.ProductOrderRepository;
import com.konark.repository.StoreRepository;
import com.konark.repository.VendorRepository;
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

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public ProductOrderModel saveDraftProductOrder(ProductOrderModel productOrderModel, String username) {
		return saveProductOrder(productOrderModel, false, username);
	}

	@Transactional
	public ProductOrderModel commitProductOrder(ProductOrderModel productOrderModel, String username) {
		return saveProductOrder(productOrderModel, true, username);
	}

	@Transactional(readOnly = true)
	public List<DraftOrderSummaryModel> getDrafts(String username) {
		List<DraftOrderSummaryModel> drafts = new ArrayList<>();
		java.util.Set<String> representedStoreVendors = new java.util.HashSet<>();
		for (ProductOrderEntity entity : productOrderRepository
				.findByCreatedByAndOrderStatusOrderByOrderDateDesc(username, "DRAFT")) {
			String storeVendorKey = String.valueOf(entity.getStoreID()) + "\u0000" + entity.getVendorNumber();
			if (!representedStoreVendors.add(storeVendorKey)) {
				continue;
			}
			DraftOrderSummaryModel draft = new DraftOrderSummaryModel();
			draft.setProductOrderID(entity.getOrderID().toString());
			draft.setStoreID(entity.getStoreID());
			storeRepository.findById(entity.getStoreID())
					.ifPresent(store -> draft.setStoreName(store.getStoreName()));
			draft.setVendorNumber(entity.getVendorNumber());
			vendorRepository.findById(entity.getVendorNumber())
					.ifPresent(vendor -> draft.setVendorName(vendor.getCompany()));
			draft.setOrderDate(entity.getOrderDate());
			draft.setTotalCost(entity.getTotalCost());
			drafts.add(draft);
		}
		return drafts;
	}

	@Transactional(readOnly = true)
	public ProductOrderModel getDraft(String productOrderID, String username) {
		Long orderID = Long.valueOf(productOrderID);
		ProductOrderEntity entity = productOrderRepository.findById(orderID)
				.filter(order -> "DRAFT".equalsIgnoreCase(order.getOrderStatus()))
				.filter(order -> username.equals(order.getCreatedBy()))
				.orElseThrow(() -> new IllegalArgumentException("Draft not found"));

		ProductOrderModel draft = new ProductOrderModel();
		draft.setProductOrderID(entity.getOrderID().toString());
		draft.setStoreID(entity.getStoreID());
		draft.setVendorNumber(entity.getVendorNumber());
		List<ProductItemModel> items = new ArrayList<>();
		for (ProductOrderItemEntity itemEntity : entity.getProductOrderItems()) {
			ProductItemModel item = new ProductItemModel();
			item.setItemName(itemEntity.getItemName());
			item.setItemNumber(itemEntity.getItemNumber());
			item.setVendorPartNumber(itemEntity.getVendorPartNumber());
			item.setQuantityOrdered(itemEntity.getQuantityOrdered());
			items.add(item);
		}
		draft.setProductItems(items);
		return draft;
	}

	private ProductOrderModel saveProductOrder(ProductOrderModel productOrderModel, boolean commitOrder, String username) {

		Date orderDate = new Date();
		BigDecimal totalCost = new BigDecimal(0);

		ProductOrderEntity newProductOrder = findDraftOrCreate(productOrderModel, username);
		newProductOrder.setOrderDate(orderDate);
		newProductOrder.setStoreID(productOrderModel.getStoreID());
		newProductOrder.setVendorNumber(productOrderModel.getVendorNumber());
		newProductOrder.setOrderStatus(commitOrder ? "COMMITTED" : "DRAFT");
		newProductOrder.setCreatedBy(username);
		newProductOrder.getProductOrderItems().clear();

		for (ProductItemModel productItemModel : productOrderModel.getProductItems()) {
			if (productItemModel.getQuantityOrdered() == null
					|| new BigDecimal(productItemModel.getQuantityOrdered()).compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
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
		if (!commitOrder) {
			return productOrderModel;
		}

		Optional<StoreEntity> store = storeRepository.findById(productOrderModel.getStoreID());
		Optional<VendorEntity> vendor = vendorRepository.findById(productOrderModel.getVendorNumber());
		String generatedHtml = PDFUtil.parseProductOrderTemplate(productOrderModel, store.get(), vendor.get(), orderDate,
				totalCost.stripTrailingZeros());

		try {
			byte[] pdfBytes = PDFUtil.generatePdfBytesFromHtml(generatedHtml);
			productOrderModel.setPdfBase64(Base64.getEncoder().encodeToString(pdfBytes));
		} catch (DocumentException e) {
			throw new IllegalStateException("Could not generate the order PDF", e);
		} catch (java.io.IOException e) {
			throw new IllegalStateException("Could not generate the order PDF", e);
		}

		return productOrderModel;
	}

	private ProductOrderEntity findDraftOrCreate(ProductOrderModel productOrderModel, String username) {
		if (productOrderModel.getProductOrderID() != null && !productOrderModel.getProductOrderID().trim().isEmpty()) {
			try {
				Long orderID = Long.valueOf(productOrderModel.getProductOrderID());
				Optional<ProductOrderEntity> existing = productOrderRepository.findById(orderID);
				if (existing.isPresent() && "DRAFT".equalsIgnoreCase(existing.get().getOrderStatus())
						&& username.equals(existing.get().getCreatedBy())
						&& java.util.Objects.equals(productOrderModel.getStoreID(), existing.get().getStoreID())
						&& java.util.Objects.equals(productOrderModel.getVendorNumber(), existing.get().getVendorNumber())) {
					return existing.get();
				}
			} catch (NumberFormatException ignored) {
				// Treat malformed draft ids as a new draft rather than updating an existing order.
			}
		}
		if (productOrderModel.getStoreID() != null && productOrderModel.getVendorNumber() != null) {
			Optional<ProductOrderEntity> matchingDraft = productOrderRepository
					.findFirstByCreatedByAndStoreIDAndVendorNumberAndOrderStatusOrderByOrderDateDesc(
							username, productOrderModel.getStoreID(), productOrderModel.getVendorNumber(), "DRAFT");
			if (matchingDraft.isPresent()) {
				return matchingDraft.get();
			}
		}
		return new ProductOrderEntity();
	}
}
