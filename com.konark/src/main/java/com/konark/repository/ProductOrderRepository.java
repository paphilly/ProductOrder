package com.konark.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.konark.entity.ProductOrderEntity;

public interface ProductOrderRepository extends CrudRepository<ProductOrderEntity, Long>  {

	List<ProductOrderEntity> findByCreatedByAndOrderStatusOrderByOrderDateDesc(String createdBy, String orderStatus);

	Optional<ProductOrderEntity> findFirstByCreatedByAndStoreIDAndVendorNumberAndOrderStatusOrderByOrderDateDesc(
			String createdBy, String storeID, String vendorNumber, String orderStatus);

}
