package com.salesmanager.core.model.payments;

/**
 * When the user performs a payment using a credit card
 * @author Carl Samson
 *
 */
public class CreditCardPayment extends Payment {
	
	private String creditCardNumber;
	private String credidCardValidationNumber;
	private String expirationMonth;
	private String expirationYear;
	private String cardOwner;
	private CreditCardType creditCard;
	public String getCreditCardNumber() {
		System.out.println("$#4410#"); return creditCardNumber;
	}
	public void setCreditCardNumber(String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}
	public String getCredidCardValidationNumber() {
		System.out.println("$#4411#"); return credidCardValidationNumber;
	}
	public void setCredidCardValidationNumber(String credidCardValidationNumber) {
		this.credidCardValidationNumber = credidCardValidationNumber;
	}
	public String getExpirationMonth() {
		System.out.println("$#4412#"); return expirationMonth;
	}
	public void setExpirationMonth(String expirationMonth) {
		this.expirationMonth = expirationMonth;
	}
	public String getExpirationYear() {
		System.out.println("$#4413#"); return expirationYear;
	}
	public void setExpirationYear(String expirationYear) {
		this.expirationYear = expirationYear;
	}
	public String getCardOwner() {
		System.out.println("$#4414#"); return cardOwner;
	}
	public void setCardOwner(String cardOwner) {
		this.cardOwner = cardOwner;
	}
	public CreditCardType getCreditCard() {
		System.out.println("$#4415#"); return creditCard;
	}
	public void setCreditCard(CreditCardType creditCard) {
		this.creditCard = creditCard;
	}

}
