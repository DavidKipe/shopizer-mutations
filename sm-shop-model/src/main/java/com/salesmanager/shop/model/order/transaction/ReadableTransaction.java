package com.salesmanager.shop.model.order.transaction;

import java.io.Serializable;

import com.salesmanager.core.model.payments.PaymentType;
import com.salesmanager.core.model.payments.TransactionType;

public class ReadableTransaction extends TransactionEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PaymentType paymentType;
	private TransactionType transactionType;
	public PaymentType getPaymentType() {
		System.out.println("$#9209#"); return paymentType;
	}
	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}
	public TransactionType getTransactionType() {
		System.out.println("$#9210#"); return transactionType;
	}
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}


}
