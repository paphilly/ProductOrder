package com.konark.repository;

import org.springframework.data.repository.CrudRepository;

import com.konark.entity.StoreEntity;

public interface StoreRepository extends CrudRepository<StoreEntity, String> {

}
