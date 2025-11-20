package com.konark.repository;

import java.util.List;

import com.konark.entity.OrderEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<OrderEntity, String> {

 	@Query(value = "SELECT * from Inventory_SKUS ", nativeQuery = true)
	List<Object[]>  findList();

}
