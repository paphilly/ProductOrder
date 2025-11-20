package com.konark.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.konark.repository.QBVendorRepository;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class QBVendorService {
	
	@Autowired
	QBVendorRepository qbVendorRepository;

	public String getAccountsPayableVendorEmailId(String vendorReferenceID) {
		
		List<String> emailResults = qbVendorRepository.findAccountsPayableVendorEmailID(vendorReferenceID);
		
		if (emailResults!=null && !emailResults.isEmpty()) {
			
			String vendorEmail = emailResults.get(0); 
			if(vendorEmail!=null) {
				if(vendorEmail.contains("@")) {
					return emailResults.get(0);
				}				
			}
			return "info@konarkgrocers.com";
			
		}
		
		return "info@konarkgrocers.com";
	}
		
		
}
