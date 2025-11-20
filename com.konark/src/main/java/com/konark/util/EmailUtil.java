package com.konark.util;

import java.io.File;
import java.util.ArrayList;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {


	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private EmailUtil emailUtil;

	public void sendEmailWithAttachment(String filename) {

		MimeMessage message = javaMailSender.createMimeMessage();

		MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(message, true);
			
			//String[] toEmails = {"paphilly@gmail.com"};
			//String[] ccEmails = {"paphilly@gmail.com"};

			String[] toEmails = {"info@konarkgrocers.com","savan.koppisetti@gmail.com"};
			String[] ccEmails = {"paphilly@gmail.com"};

			helper.setTo(toEmails);
			helper.setCc(ccEmails);
			helper.setSubject(filename);
			helper.setText("Attached : " + filename);

			//FileSystemResource file = new FileSystemResource(new File("D:\\" + filename + ".pdf"));
			FileSystemResource file = new FileSystemResource(new File( filename + ".pdf"));
			helper.addAttachment(filename + ".pdf", file);

			javaMailSender.send(message);
		} catch (MessagingException e) {

			e.printStackTrace();
		}

	}

	
	
	public void sendPaymentNotification(String billNumber, String vendorEmail, String generatedEmailText) throws ExceptionUtil {

		MimeMessage message = javaMailSender.createMimeMessage();

		MimeMessageHelper helper;
		ArrayList<String> toAddresses = new ArrayList<String>();
		
		
		
		try {
			helper = new MimeMessageHelper(message, true);
			
			helper.setFrom("info@konarkgrocers.com");
			
		//	toAddresses.add(vendorEmail);
		    toAddresses.add("paphilly@gmail.com");
		    
		    String[] toEmails = new String[toAddresses.size()];
			toAddresses.toArray(toEmails);
		    helper.setTo(toEmails);
		    
			helper.setSubject("Bill Number : " + billNumber + " Paid" );
			helper.setText(generatedEmailText,true);

			javaMailSender.send(message);
		} catch (MessagingException exception) {
			throw new ExceptionUtil(HttpStatus.INTERNAL_SERVER_ERROR,"E-Mail Notification Failed" , exception.getMessage());
		}

	}
	
	
	public void sendEmail(String filename) {

		try {

		emailUtil.sendEmailWithAttachment(filename);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
