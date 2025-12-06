package com.konark.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.konark.entity.StoreEntity;
import com.konark.entity.VendorEntity;
import com.konark.model.ProductOrderModel;
import com.lowagie.text.DocumentException;

//import com.lowagie.text.DocumentException;

public class PDFUtil {

	public static void generatePdfFromHtml(String html, String filename) throws IOException, DocumentException {
		
	//	String outputFolder = "D:\\" + filename + ".pdf";
		String outputFolder = filename + ".pdf";
		OutputStream outputStream = new FileOutputStream(outputFolder);

		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocumentFromString(html);
		renderer.layout();
		renderer.createPDF(outputStream);

		outputStream.close();
	}

	public static String parseProductOrderTemplate(ProductOrderModel productOrderModel, StoreEntity store, VendorEntity vendor, Date orderDate, BigDecimal totalCost) {
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setSuffix(".html");
		templateResolver.setPrefix("/templates/");
		templateResolver.setTemplateMode(TemplateMode.HTML);

		TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);

		Context context = new Context();
		context.setVariable("productOrderModel", productOrderModel);
		context.setVariable("store", store);
		context.setVariable("vendor",vendor);
		context.setVariable("orderDate", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(orderDate));
		context.setVariable("totalCost", totalCost);

//		return templateEngine.process("UpdatedInvoice", context);
		return templateEngine.process("InvoiceWithVariables", context);
		
	}
	
	public static String parseNotifyTemplate(String vendorRefName, String billNumber, String location) {
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setSuffix(".html");
		templateResolver.setPrefix("/templates/");
		templateResolver.setTemplateMode(TemplateMode.HTML);

		TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);

		ClassPathResource imgFile = new ClassPathResource( "static/css/images/konark_logo.png");
		byte[] bytes = null;
		try {
			bytes = StreamUtils.copyToByteArray( imgFile.getInputStream());
		} catch( IOException e ) {
			throw new RuntimeException( e );
		}
		String base64 = Base64.getEncoder().encodeToString( bytes);

		Context context = new Context();
		context.setVariable("logoBase64", base64);
		context.setVariable("vendorRefName", vendorRefName);
		context.setVariable("billNumber", billNumber);
		context.setVariable("location",location);
		

		return templateEngine.process("NotifyEmailTemplate", context);
		
	}
}