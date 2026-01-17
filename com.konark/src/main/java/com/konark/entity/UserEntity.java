package com.konark.entity;


import javax.persistence.*;

@Entity
@Table(name = "Konark_User")
public class UserEntity {

	@Id
	@Column(name = "Username")
	private String username;

	@Column(name = "Password")
	private String password;

	@Column(name = "Active")
	private String isActive;



	@Column(name = "Role")
	private String Role;

	@Column(name = "UserJson")
	private String userJson;

	@Column(name = "Email")
	private String email;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public void setEmail(String email) {
		 username = email;
	}
	public String getEmail() {
		return email;
	}




}
