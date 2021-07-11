package com.salesmanager.core.model.payments;

import java.io.Serializable;

import com.salesmanager.core.model.system.IntegrationConfiguration;
import com.salesmanager.core.model.system.IntegrationModule;

/**
 * Object to be used in store front with meta data and configuration
 * informations required to display to the end user
 * @author Carl Samson
 *
 */
public class PaymentMethod implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String paymentMethodCode;
	private PaymentType paymentType;
	private boolean defaultSelected;
	private IntegrationModule module;
	private IntegrationConfiguration informations;

	public PaymentType getPaymentType() {
		System.out.println("$#4422#"); return paymentType;
	}
	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}
	public String getPaymentMethodCode() {
		System.out.println("$#4423#"); return paymentMethodCode;
	}
	public void setPaymentMethodCode(String paymentMethodCode) {
		this.paymentMethodCode = paymentMethodCode;
	}
	public boolean isDefaultSelected() {
		System.out.println("$#4425#"); System.out.println("$#4424#"); return defaultSelected;
	}
	public void setDefaultSelected(boolean defaultSelected) {
		this.defaultSelected = defaultSelected;
	}
	public IntegrationModule getModule() {
		System.out.println("$#4426#"); return module;
	}
	public void setModule(IntegrationModule module) {
		this.module = module;
	}
	public IntegrationConfiguration getInformations() {
		System.out.println("$#4427#"); return informations;
	}
	public void setInformations(IntegrationConfiguration informations) {
		this.informations = informations;
	}

}
