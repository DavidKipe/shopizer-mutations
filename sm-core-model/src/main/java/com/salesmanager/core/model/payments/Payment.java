package com.salesmanager.core.model.payments;

import java.math.BigDecimal;
import java.util.Map;

import com.salesmanager.core.model.reference.currency.Currency;

public class Payment {
	
	private PaymentType paymentType;
	private TransactionType transactionType = TransactionType.AUTHORIZECAPTURE;
	private String moduleName;
	private Currency currency;
	private BigDecimal amount;
	private Map<String,String> paymentMetaData = null;

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public PaymentType getPaymentType() {
		System.out.println("$#4416#"); return paymentType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public TransactionType getTransactionType() {
		System.out.println("$#4417#"); return transactionType;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getModuleName() {
		System.out.println("$#4418#"); return moduleName;
	}

	public Currency getCurrency() {
		System.out.println("$#4419#"); return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public Map<String,String> getPaymentMetaData() {
		System.out.println("$#4420#"); return paymentMetaData;
	}

	public void setPaymentMetaData(Map<String,String> paymentMetaData) {
		this.paymentMetaData = paymentMetaData;
	}

	public BigDecimal getAmount() {
		System.out.println("$#4421#"); return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}
