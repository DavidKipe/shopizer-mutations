package com.salesmanager.shop.model.order.transaction;

import java.io.Serializable;

public class PaymentEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String paymentModule;//stripe|paypal|braintree|moneyorder ...
	private String amount;
	
	public String getPaymentModule() {
		System.out.println("$#9200#"); return paymentModule;
	}
	public void setPaymentModule(String paymentModule) {
		this.paymentModule = paymentModule;
	}
	public String getAmount() {
		System.out.println("$#9201#"); return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}

}
