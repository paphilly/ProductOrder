package com.konark.repository;

import org.springframework.data.repository.CrudRepository;

import com.konark.entity.ProductOrderEntity;

public interface ProductOrderRepository extends CrudRepository<ProductOrderEntity, String>  {

}
