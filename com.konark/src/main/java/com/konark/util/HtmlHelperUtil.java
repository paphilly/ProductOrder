package com.konark.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import javax.mail.MessagingException;

import org.dom4j.DocumentException;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.konark.model.ProductItemModel;
import com.konark.model.ProductOrderModel;


public class HtmlHelperUtil {

	private static StringBuilder htmlBuilder = new StringBuilder();
	private static String CONST_HTMLHEADER_START_TAG = "<html><head>"
			+"<style>\r\n"
			+ "table {\r\n"
			+ "  font-family: arial, sans-serif;\r\n"
			+ "  border-collapse: collapse;\r\n"
			+ "  width: 100%;\r\n"
			+ "}\r\n"
			+ "\r\n"
			+ "td, th {\r\n"
			+ "  border: 1px solid #dddddd;\r\n"
			+ "  text-align: left;\r\n"
			+ "  padding: 8px;\r\n"
			+ "}\r\n"
			+ "\r\n"
			+ "tr:nth-child(even) {\r\n"
			+ "  background-color: #dddddd;\r\n"
			+ "}\r\n"
			+ "</style>"
			+ "</head><body><h2>Product Order</h2>";
	private static String CONST_HTMLHEADER_END_TAG = "</body></html>";

	private static String CONST_TABLESTART_TAG = "<table>";
	private static String CONST_TABLEEND_TAG = "</table>";

	private static String CONST_ROWSTART_TAG = "<tr>";
	private static String CONST_ROWEND_TAG = "</tr>";

	private static String CONST_HEADERSTART_TAG = "<th>";
	private static String CONST_HEADEREND_TAG = "</th>";

	private static String CONST_TD_START_TAG = "<td>";
	private static String CONST_TD_END_TAG = "</td>";

//	private static String CONST_VENDORNUMBER = "Vendor Number";
//	private static String CONST_FIRSTNAME = "First Name";
//	private static String CONST_LASTNAME = "Last Name";
//	private static String CONST_COMPANY = "Company";
	
	private static String CONST_ItemName = "Item Name";
	private static String CONST_Quantity = "Order Quantity";
//	private static String CONST_LASTNAME = "Last Name";
//	private static String CONST_COMPANY = "Company";
	private static String CONST_STYLE = "<style>\r\n"
			+ "table {\r\n"
			+ "  font-family: arial, sans-serif;\r\n"
			+ "  border-collapse: collapse;\r\n"
			+ "  width: 100%;\r\n"
			+ "}\r\n"
			+ "\r\n"
			+ "td, th {\r\n"
			+ "  border: 1px solid #dddddd;\r\n"
			+ "  text-align: left;\r\n"
			+ "  padding: 8px;\r\n"
			+ "}\r\n"
			+ "\r\n"
			+ "tr:nth-child(even) {\r\n"
			+ "  background-color: #dddddd;\r\n"
			+ "}\r\n"
			+ "</style>";

	public static <T> String generateHtml(ProductOrderModel productOrderModel, String filename) throws  IOException, DocumentException, MessagingException {

		htmlBuilder.append(CONST_HTMLHEADER_START_TAG).append(CONST_TABLESTART_TAG).append(CONST_ROWSTART_TAG)
				.append(CONST_HEADERSTART_TAG).append(CONST_ItemName).append(CONST_HEADEREND_TAG)
				.append(CONST_HEADERSTART_TAG).append(CONST_Quantity).append(CONST_HEADEREND_TAG)
//				.append(CONST_HEADERSTART_TAG).append(CONST_LASTNAME).append(CONST_HEADEREND_TAG)
//				.append(CONST_HEADERSTART_TAG).append(CONST_COMPANY).append(CONST_HEADEREND_TAG)
				.append(CONST_ROWEND_TAG);

		productOrderModel.getProductItems().stream().forEach((row) -> {

			if (row instanceof ProductItemModel) {

				htmlBuilder.append(CONST_ROWSTART_TAG).append(CONST_TD_START_TAG)
						.append(((ProductItemModel) row).getItemName()).append(CONST_TD_END_TAG).append(CONST_TD_START_TAG)
						.append(((ProductItemModel) row).getQuantityOrdered()).append(CONST_TD_END_TAG).append(CONST_ROWEND_TAG);
						//append(CONST_TD_START_TAG);
//						.append(((Vendor) row).getLastName()).append(CONST_TD_END_TAG).append(CONST_TD_START_TAG)
//						.append(((Vendor) row).getCompany()).append(CONST_TD_END_TAG).append(CONST_ROWEND_TAG);
			}

		});

		htmlBuilder.append(CONST_TABLEEND_TAG);
		htmlBuilder.append(CONST_HTMLHEADER_END_TAG);

		System.out.println(htmlBuilder.toString());
		storeHTMLFile(htmlBuilder.toString(), filename);
		
		try {
			generatePDFFromHTML(filename);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//sendMail(getSession());

		return htmlBuilder.toString();

	}

	private static void storeHTMLFile(String fileContent, String filename) {

		File newFile = new File("D:\\" + filename+ ".html");

		try {
			BufferedWriter writer;
			writer = new BufferedWriter(new FileWriter(newFile));
			writer.write(fileContent);

			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	private static void generatePDFFromHTML(String filename) throws Exception, IOException, DocumentException {
		Document document = new Document();
		PdfWriter writer;
		try {
			writer = PdfWriter.getInstance(document, new FileOutputStream("D:\\" + filename + ".pdf"));
			document.open();
			XMLWorkerHelper.getInstance().parseXHtml(writer, document, new FileInputStream("D:\\" + filename + ".html"));
			document.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (com.itextpdf.text.DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
