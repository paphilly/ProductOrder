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
		List<Object[]> queriedUser = null;
		try {
			queriedUser = userRepository.authenticateUser(userModel.getUsername(), userModel.getPassword());
		} catch (Exception e) {
			// Fallback if collation clause fails
		}
		if (queriedUser == null || queriedUser.isEmpty()) {
			try {
				queriedUser = userRepository.authenticateUserSimple(userModel.getUsername(), userModel.getPassword());
			} catch (Exception e) {
				// Ignore
			}
		}

		if (queriedUser != null && !queriedUser.isEmpty()) {
			Object[] row = queriedUser.get(0);
			userModel.setUsername(ApplicationUtils.getString(row[0]));
			// Col 0: Username, Col 1: Password, Col 2: Active, Col 3: Role, Col 4: UserJson, Col 5: Email
			String role = row.length > 3 && row[3] != null ? ApplicationUtils.getString(row[3]) : ApplicationUtils.getString(row[2]);
			userModel.setRole(role);
			String userJson = row.length > 4 ? ApplicationUtils.getString(row[4]) : null;
			userModel.setUserJSON(userJson);
			if (row.length > 5) {
				userModel.setEmail(ApplicationUtils.getString(row[5]));
			}
			if (userJson != null && !userJson.trim().isEmpty()) {
				try {
					userModel.setUserInfo(JsonUtil.fromJson(userJson, UserInfo.class));
				} catch (Exception e) {
					// Ignore malformed JSON
				}
			}
			return userModel;
		}
		return null;
	}

	public UserModel findUser(UserModel userModel) {
		if (userModel == null || userModel.getUsername() == null || userModel.getPassword() == null) {
			return null;
		}
		try {
			UserEntity userEntity = userRepository.findByUsernameAndPassword(userModel.getUsername(),
					userModel.getPassword());
			if (userEntity != null) {
				userModel.setUserJSON(userEntity.getUserJson());
				userModel.setRole(userEntity.getRole());
				userModel.setEmail(userEntity.getEmail());
				if (userEntity.getUserJson() != null && !userEntity.getUserJson().trim().isEmpty()) {
					try {
						userModel.setUserInfo(JsonUtil.fromJson(userEntity.getUserJson(), UserInfo.class));
					} catch (Exception e) {
						// Ignore or log malformed userJson
					}
				}
				return userModel;
			}
		} catch (Exception e) {
			// Fallback to native SQL query if JPA entity mapping fails
			return processLogin(userModel);
		}
		return processLogin(userModel);
	}

	public boolean isUserExists(String userName) {
		Optional<UserEntity> user = userRepository.findById(userName);
		return user.isPresent();
	}

	public UserModel getUserByUsername(String username) {
		Optional<UserEntity> userEntityOpt = userRepository.findById(username);
		if (userEntityOpt.isPresent()) {
			UserEntity userEntity = userEntityOpt.get();
			UserModel userModel = new UserModel();
			userModel.setUsername(userEntity.getUsername());
			userModel.setRole(userEntity.getRole());
			userModel.setEmail(userEntity.getEmail());
			if (userEntity.getUserJson() != null && !userEntity.getUserJson().trim().isEmpty()) {
				try {
					userModel.setUserInfo(JsonUtil.fromJson(userEntity.getUserJson(), UserInfo.class));
				} catch (Exception e) {
					// Ignore or log malformed userJson
				}
			}
			return userModel;
		}
		return null;
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