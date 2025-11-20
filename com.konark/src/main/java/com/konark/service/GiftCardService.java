package com.konark.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.konark.model.GiftCardModel;
import com.konark.repository.GiftCardRepository;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GiftCardService {

	@Autowired
	GiftCardModel giftCardModel;

	@Autowired
	GiftCardRepository giftCardRepository;

	@Autowired
	StoreService storeService;

	public GiftCardModel findGiftCardTransactions(String mobileNumber, String giftCardNumber) {

	
		
		List<Map<String,String>> transactionsList = new ArrayList<Map<String,String>>();

		List<Object[]> customers = giftCardRepository.findCustomersByMobileNumber(mobileNumber, giftCardNumber);

		if (customers != null && customers.size() > 0) {

			giftCardModel.setFirstName((String) customers.get(0)[0]);
			giftCardModel.setLastName((String) customers.get(0)[1]);

		}
		List<Object[]> transactionRows = giftCardRepository.findGiftCardTransactionsByCardNumber(giftCardNumber);
		

		if (transactionRows != null && transactionRows.size() > 0) {

			for (int i = 0; i < transactionRows.size(); i++) {
				
				Map<String, String> transactionMap = new HashMap<String, String>();
				
				BigDecimal balance = (BigDecimal)transactionRows.get(i)[0];				
				String balanceString = balance.toString();			
				
				giftCardModel.setBalance(balanceString);
				
				Integer transaction = (Integer) transactionRows.get(i)[1];
				String trxType = transaction.toString();
				String transType = null;
				 if(trxType == "0") {
                     transType = "Load";
                 } else {
                	 transType = "Redeem";
                 }
				 transactionMap.put("transType",transType );
				
				BigDecimal amount = (BigDecimal)transactionRows.get(i)[2];				
				String amountString = amount.toString();	
				transactionMap.put("Amount", amountString);

				String storeID = (String) transactionRows.get(i)[3];
				String storeName = storeService.getStoresMapWitKeyID().get(storeID);

				transactionMap.put("Store", storeName);
				
				BigInteger invoice = (BigInteger)transactionRows.get(i)[4];				
				String invoiceNumber = invoice.toString();	
				
				transactionMap.put("InvoiceNumber",invoiceNumber);
				
				Date transDate = (Date)transactionRows.get(i)[5];				
				String transactionDate = transDate.toString();	
				
				transactionMap.put("transDate", transactionDate);
				
				transactionsList.add(transactionMap);
				
				

			}
			
			giftCardModel.setTransactions(transactionsList);

		}

		return giftCardModel;
	}
}
