package com.konark.entity;


import javax.persistence.*;

@Entity
@Table(name = "Konark_User")
public class UserEntity {

	@Id
	@Column(name = "Username")
	private String Username;

	@Column(name = "Password")
	private String Password;

	@Column(name = "Active")
	private String isActive;



	@Column(name = "Role")
	private String Role;

	@Column(name = "UserJson")
	private String userJson;
	public String getUsername() {
		return Username;
	}

	public void setUsername(String username) {
		Username = username;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public String getUserJson() {
		return userJson;
	}

	public void setUserJson(String userJson) {
		this.userJson = userJson;
	}

	public String getRole() {
		return Role;
	}

	public void setRole(String role) {
		Role = role;
	}





}
