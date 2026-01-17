package com.konark.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.konark.entity.UserEntity;
import com.konark.model.UserInfo;
import com.konark.model.UserModel;
import com.konark.repository.UserRepository;
import com.konark.util.ApplicationUtils;
import com.konark.util.JsonUtil;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserService {

	@Autowired
	UserRepository userRepository;

	public UserModel processLogin(UserModel userModel) {

		List<Object[]> queriedUser = userRepository.authenticateUser(userModel.getUsername(), userModel.getPassword());
		if (queriedUser.size() > 0) {
			  userModel.setUserJSON(ApplicationUtils.getString(queriedUser.get(0)[4]));
			  userModel.setUsername(ApplicationUtils.getString(queriedUser.get(0)[0]));
			  userModel.setRole(ApplicationUtils.getString(queriedUser.get(0)[2]));
		}

		return userModel;
	}

	public UserModel findUser(UserModel userModel) {
		UserEntity userEntity = userRepository.findByUsernameAndPassword(userModel.getUsername(),userModel.getPassword());
		if (userEntity != null) {
			userModel.setUserJSON(userEntity.getUserJson());
			userModel.setRole(userEntity.getRole());
			userModel.setEmail( userEntity.getEmail() );
			userModel.setUserInfo( JsonUtil.fromJson( userEntity.getUserJson(), UserInfo.class));
			return userModel;
		}
		return null;
	}


	public boolean isUserExists(String userName) {
		Optional<UserEntity> user = userRepository.findById(userName);
		return user.isPresent();
	}

	public Boolean createUser(UserModel userModel) {

		UserEntity userEntity = new UserEntity();
		userEntity.setUsername(userModel.getUsername());
		userEntity.setPassword(userModel.getPassword());
		userEntity.setRole(userModel.getRole());
		userEntity.setIsActive(userModel.getStatus());
		userEntity.setUserJson(userModel.getUserJSON());
		userRepository.save(userEntity);
		return true;

	}

}