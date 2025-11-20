package com.konark.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.konark.entity.BillPayEntity;
import com.konark.model.AccountsPayableModel;
import com.konark.repository.AccountsPayableRepository;
import com.konark.util.EmailUtil;
import com.konark.util.ExceptionUtil;
import com.konark.util.PDFUtil;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountsPayableService {

	private static final ZoneId DEFAULT_TIME_ZONE = ZoneId.systemDefault();

	private static final String CONST_PENDING = "Pending";
	private static final String CONST_APPROVED = "Approved";
	private static final String CONST_PAID = "Paid";
	private static final String CONST_NOTIFIED = "Notified";

	private static final String CONST_OVERDUE = "Overdue";

	@Autowired
	AccountsPayableRepository accountsPayableRepository;

	@Autowired
	EmailUtil emailUtil;

	@Autowired
	QBVendorService qbVendorService;

	@Autowired
	UserService userService;

	public List<AccountsPayableModel> getAccountsPayableForPastSixtyDaysByLocations(String[] locations) {

		List<String> locationsList = Arrays.asList(locations);

		List<BillPayEntity> accountsPayableResults = accountsPayableRepository
				.findAccountsPayableForPast60DaysByLocations(locations);

		if (locationsList != null & locationsList.contains("UNASSIGNED")) {
			accountsPayableResults.addAll(accountsPayableRepository.findUnassignedAccountsPayableForPast60Days());
		}

		List<AccountsPayableModel> accountsPayableList = new ArrayList<AccountsPayableModel>();

		accountsPayableResults.forEach(bill -> {
			AccountsPayableModel accountsPayableModel = new AccountsPayableModel();
			accountsPayableModel.setBalance(bill.getBalance());
			accountsPayableModel.setTotalAmount(bill.getTotalAmount());
			accountsPayableModel.setBillNumber(bill.getBillNumber());
			accountsPayableModel.setVendorID(bill.getVendorID());
			accountsPayableModel.setVendorName(bill.getVendorName());
			accountsPayableModel.setId(bill.getBillID());
			accountsPayableModel.setComment(bill.getComment());

			setStatusAndDates(bill, accountsPayableModel);

			accountsPayableList.add(accountsPayableModel);

		});

		return accountsPayableList;
	}

	private void setStatusAndDates(BillPayEntity bill, AccountsPayableModel accountsPayableModel) {
		Date dueDate = bill.getDueDate();
		Date billDate = bill.getBillDate();

		BigDecimal balance = bill.getBalance();
		BigDecimal totalAmount = bill.getTotalAmount();

		Date overrideDueDate = bill.getOverrideDueDate();

		if (overrideDueDate != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"); // ISO String format
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			String overrideDateString = sdf.format(overrideDueDate);
			accountsPayableModel.setOverrideDueDate(overrideDateString);

		}
		
		LocalDate today = LocalDate.now();
		LocalDate due = dueDate.toInstant().atZone(DEFAULT_TIME_ZONE).toLocalDate();

		long diff = ChronoUnit.DAYS.between(today, due);

		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

		SimpleDateFormat isoFormatter = new SimpleDateFormat("MM/dd/yyyy KK:mm:ss a Z");
		isoFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

		accountsPayableModel.setDueDate(formatter.format(dueDate));
		accountsPayableModel.setBillDate(formatter.format(billDate));

		if (balance.intValue() > 0
				&& (!CONST_APPROVED.equalsIgnoreCase(bill.getStatus()) && !CONST_PAID.equalsIgnoreCase(bill.getStatus())
						&& !CONST_NOTIFIED.equalsIgnoreCase(bill.getStatus()))) {

			accountsPayableModel.setIsPending(true);
			if (diff < 0) {
				accountsPayableModel.setStatus(CONST_OVERDUE);
				accountsPayableModel.setIsOverdue(true);
			} else {
				accountsPayableModel.setStatus(CONST_PENDING);
			}
		} else if ((percentageBalance(balance, totalAmount) < 10 || balance.intValue() == 0)
				&& !CONST_NOTIFIED.equalsIgnoreCase(bill.getStatus())) {
			accountsPayableModel.setIsPending(false);
			accountsPayableModel.setIsOverdue(false);
			accountsPayableModel.setStatus(CONST_PAID);

		} else {
			accountsPayableModel.setStatus(bill.getStatus());
		}

	}

//	public List<AccountsPayableModel> getAllAccountsPayableForPastSixtyDays() {
//
//		List<BillPayEntity> accountsPayableResults = accountsPayableRepository.findAccountsPayableForPast60Days();
//
//		List<AccountsPayableModel> accountsPayable = new ArrayList<AccountsPayableModel>();
//
//		accountsPayableResults.forEach(bill -> {
//			AccountsPayableModel accountsPayableModel = new AccountsPayableModel();
//			accountsPayableModel.setBalance(bill.getBalance());
//			accountsPayableModel.setTotalAmount(bill.getTotalAmount());
//			accountsPayableModel.setBillNumber(bill.getBillNumber());
//			accountsPayableModel.setVendorID(bill.getVendorID());
//			accountsPayableModel.setVendorName(bill.getVendorName());
//			accountsPayableModel.setId(bill.getBillID());
//			Date dueDate = bill.getDueDate();
//			Date billDate = bill.getBillDate();
//			BigDecimal balance = bill.getBalance();
//			LocalDate today = LocalDate.now();
//
//			LocalDate due = dueDate.toInstant().atZone(DEFAULT_TIME_ZONE).toLocalDate();
//
//			long diff = ChronoUnit.DAYS.between(today, due);
//
//			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
//			accountsPayableModel.setDueDate(formatter.format(dueDate));
//			accountsPayableModel.setBillDate(formatter.format(billDate));
//
//			if (bill.getStatus() != null) {
//				accountsPayableModel.setStatus(bill.getStatus());
//			} else if (balance.intValue() > 0) {
//
//				accountsPayableModel.setIsPending(true);
//				if (diff < 0) {
//					accountsPayableModel.setStatus(CONST_OVERDUE);
//					accountsPayableModel.setIsOverdue(true);
//				} else {
//					accountsPayableModel.setStatus(CONST_PENDING);
//				}
//			} else {
//				accountsPayableModel.setStatus(CONST_PAID);
//			}
//
//			accountsPayable.add(accountsPayableModel);
//
//		});
//
//		return accountsPayable;
//	}

	public AccountsPayableModel updateAccountPayable(AccountsPayableModel accountsPayableModel) throws ExceptionUtil {

		
//		TimeZone tz = Calendar.getInstance().getTimeZone();
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
//		df.setTimeZone(tz);
//		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
//		ZonedDateTime zdt = ZonedDateTime.from(df.parse(accountsPayableModel.getOverrideDueDate()));

		Optional<BillPayEntity> billPayResult = accountsPayableRepository.findById(accountsPayableModel.getId());
		BillPayEntity billPay = billPayResult.get();

		if (billPay != null) {

			billPay.setStatus(accountsPayableModel.getStatus());
			billPay.setComment(accountsPayableModel.getComment());
			try {
				SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
				formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                billPay.setOverrideDueDate(formatter.parse(accountsPayableModel.getOverrideDueDate()));
			} catch (ParseException e) {
				// TODO
				e.printStackTrace();
			}
			accountsPayableRepository.save(billPay);
		}

		if (CONST_APPROVED.equals(accountsPayableModel.getStatus())
				|| CONST_PAID.equals(accountsPayableModel.getStatus())
				|| CONST_NOTIFIED.equals(accountsPayableModel.getStatus())) {
			accountsPayableModel.setIsPending(false);
			accountsPayableModel.setIsOverdue(false);

		}

		setStatusAndDates(billPay, accountsPayableModel);

		if (CONST_NOTIFIED.equalsIgnoreCase(accountsPayableModel.getStatus())) {
			String generatedHtml = PDFUtil.parseNotifyTemplate(billPay.getVendorName(), billPay.getBillNumber(),
					billPay.getLocation());
			String vendorEmail = qbVendorService.getAccountsPayableVendorEmailId(accountsPayableModel.getVendorID());
			// emailUtil.sendPaymentNotification(accountsPayableModel.getBillNumber(),
			// vendorEmail, generatedHtml);

		}

		return accountsPayableModel;
	}

	public Map<String, String> getAccountsPayableVendorsByLocation(String[] locations) {

		List<String> locationsList = Arrays.asList(locations);

		List<Object[]> vendorResults = accountsPayableRepository.findVendorsByLocation(locations);

		if (locationsList != null & locationsList.contains("UNASSIGNED")) {
			vendorResults.addAll(accountsPayableRepository.findUnassignedVendors());
		}

		Map<String, String> vendors = new HashMap<String, String>();

		vendorResults.stream().forEach(resultRow -> {

			if (vendors.get(resultRow[1].toString()) == null) {
				vendors.put(resultRow[1].toString(), resultRow[0].toString());
			}

		});

		return vendors;
	}

	public Map<String, String> getAccountsPayableLocations() {

		List<String> locationResults = accountsPayableRepository.findDistinctLocation();
		Map<String, String> locations = new HashMap<String, String>();
		locationResults.stream().forEach(locationRow -> {
			
			 if (locationRow == null || "".equals(locationRow)) {
				locations.put("UNASSIGNED", "UNASSIGNED");
			}else {
				locations.put(locationRow, locationRow);
			}
		});

		return locations;
	}

	public Map<String, String> getAllLocations() {

		List<String> locationResults = accountsPayableRepository.findDistinctLocation();

		Map<String, String> locations = new HashMap<String, String>();

		locationResults.stream().forEach(locationRow -> {
			if (locationRow != null || !"".equals(locationRow)) {
				locations.put(locationRow, locationRow);
			}
		});

		return locations;
	}

	public double percentageBalance(BigDecimal balance, BigDecimal totalAmount) {
		return balance.doubleValue() * 100 / totalAmount.doubleValue();
	}

}
