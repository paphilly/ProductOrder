package com.konark.model;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GiftCardModel {
	
	private String firstName;
	
	private String lastName;
	
	private String balance;
	
	private List<Map<String,String>> transactions;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public List<Map<String, String>> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Map<String, String>> transactions) {
		this.transactions = transactions;
	}

}
