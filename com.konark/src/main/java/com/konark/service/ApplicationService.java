package com.konark.service;

import com.konark.model.OrderModel;
import com.konark.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Component
public class ApplicationService {
	@Autowired
	OrderRepository orderRepository;

	private OrderModel orderModel;


	
	public List<Object[]>  getDepts() {

		
		List<Object[]> aueriedList  = orderRepository.findList();
		ArrayList<String> al = new ArrayList<>();
		return aueriedList;
	}



	public OrderModel getDepartmentsModel() {
		return orderModel;
	}
	public void seModel(OrderModel orderModel) {
		this.orderModel = orderModel;
	}

}