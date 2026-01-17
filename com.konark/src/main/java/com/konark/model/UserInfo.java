package com.konark.model;

import java.util.List;
import java.util.Map;

public class UserInfo {

	private String firstname;
	private String lastname;
	private String email;
	private String role;
	private Map<String, String> stores;
	private Map<String, String> locations;
	private List<String> pages;

	// No-args constructor (required for Jackson)
	public UserInfo() {
	}

	// Getters and Setters
	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Map<String, String> getStores() {
		return stores;
	}

	public void setStores(Map<String, String> stores) {
		this.stores = stores;
	}

	public Map<String, String> getLocations() {
		return locations;
	}

	public void setLocations(Map<String, String> locations) {
		this.locations = locations;
	}

	public List<String> getPages() {
		return pages;
	}

	public void setPages(List<String> pages) {
		this.pages = pages;
	}
}
