package com.konark.repository;

import com.konark.entity.InventoryEntity;
import com.konark.entity.UserEntity;
import com.konark.entity.OrderEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends CrudRepository<UserEntity, String> {

	@Query(value = "SELECT * FROM Konark_User WHERE Username= :userName COLLATE Latin1_General_CS_AS AND Password= :password COLLATE Latin1_General_CS_AS", nativeQuery = true)
	List<Object[]> authenticateUser(@Param("userName") String userName, @Param("password") String password);
	
	@Query(value = "SELECT UserJson FROM Konark_User WHERE Username= :userName", nativeQuery = true)
	String findByUsername(@Param("userName") String userName);

	UserEntity findByUsernameAndPassword(String username,String password);
}
