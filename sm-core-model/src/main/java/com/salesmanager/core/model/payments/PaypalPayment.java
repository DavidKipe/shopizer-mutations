package com.salesmanager.core.model.payments;

/**
 * When the user performs a payment using paypal
 * @author Carl Samson
 *
 */
public class PaypalPayment extends Payment {
	
	//express checkout
	private String payerId;
	private String paymentToken;
	
	public PaypalPayment() {
		System.out.println("$#4431#"); super.setPaymentType(PaymentType.PAYPAL);
	}
	
	public void setPayerId(String payerId) {
		this.payerId = payerId;
	}
	public String getPayerId() {
		System.out.println("$#4432#"); return payerId;
	}
	public void setPaymentToken(String paymentToken) {
		this.paymentToken = paymentToken;
	}
	public String getPaymentToken() {
		System.out.println("$#4433#"); return paymentToken;
	}

}
