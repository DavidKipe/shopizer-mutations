package com.salesmanager.core.model.order.payment;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.salesmanager.core.model.payments.CreditCardType;

@Embeddable
public class CreditCard {
	
	@Column (name ="CARD_TYPE")
	@Enumerated(value = EnumType.STRING)
	private CreditCardType cardType;
	
	@Column (name ="CC_OWNER")
	private String ccOwner;
	
	@Column (name ="CC_NUMBER")
	private String ccNumber;
	
	@Column (name ="CC_EXPIRES")
	private String ccExpires;
	
	@Column (name ="CC_CVV")
	private String ccCvv;

	public String getCcOwner() {
		System.out.println("$#4405#"); return ccOwner;
	}

	public void setCcOwner(String ccOwner) {
		this.ccOwner = ccOwner;
	}

	public String getCcNumber() {
		System.out.println("$#4406#"); return ccNumber;
	}

	public void setCcNumber(String ccNumber) {
		this.ccNumber = ccNumber;
	}

	public String getCcExpires() {
		System.out.println("$#4407#"); return ccExpires;
	}

	public void setCcExpires(String ccExpires) {
		this.ccExpires = ccExpires;
	}

	public String getCcCvv() {
		System.out.println("$#4408#"); return ccCvv;
	}

	public void setCcCvv(String ccCvv) {
		this.ccCvv = ccCvv;
	}

	public void setCardType(CreditCardType cardType) {
		this.cardType = cardType;
	}

	public CreditCardType getCardType() {
		System.out.println("$#4409#"); return cardType;
	}

}
