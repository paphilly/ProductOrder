package com.konark.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.konark.entity.OrderEntity;

public interface GiftCardRepository extends CrudRepository<OrderEntity, String> {
	
	@Query(value = "SELECT Gift_Cards.Balance, Gift_Card_Trans.TransType, Gift_Card_Trans.Amt, \r\n"
			+ "		Gift_Card_Trans.Store_ID, Gift_Card_Trans.Invoice_Number, Gift_Card_Trans.DateTimeStamp \r\n"
			+ "		FROM Gift_Cards \r\n"
			+ "		JOIN Gift_Card_Trans ON Gift_Card_Trans.Card_ID = Gift_Cards.Card_ID \r\n"
			+ "		WHERE Gift_Cards.Card_ID = :giftCardNumber ORDER BY Gift_Card_Trans.DateTimeStamp DESC", nativeQuery = true)
	List<Object[]> findGiftCardTransactionsByCardNumber(String giftCardNumber);

	
	@Query(value="SELECT Customer.First_Name, Customer.Last_Name, Customer_Swipes.Swipe_ID \r\n"
			+ "		FROM Customer \r\n"
			+ "		JOIN Customer_Swipes ON Customer_Swipes.CustNum = Customer.CustNum \r\n"
			+ "		WHERE Customer.CustNum LIKE :mobileNumber \r\n"
			+ "		AND Customer_Swipes.Swipe_ID LIKE :giftCardNumber ORDER BY Customer.CustNum", nativeQuery=true)
	List<Object[]> findCustomersByMobileNumber(String mobileNumber, String giftCardNumber);
	
}
