package com.konark.repository;

import org.springframework.data.repository.CrudRepository;

import com.konark.entity.VendorEntity;

public interface VendorRepository extends CrudRepository<VendorEntity, String>  {

}
