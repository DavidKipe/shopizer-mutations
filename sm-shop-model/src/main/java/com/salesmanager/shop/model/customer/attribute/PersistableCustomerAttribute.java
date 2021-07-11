package com.salesmanager.shop.model.customer.attribute;

public class PersistableCustomerAttribute extends CustomerAttributeEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CustomerOption customerOption;
	private CustomerOptionValue customerOptionValue;
	public void setCustomerOptionValue(CustomerOptionValue customerOptionValue) {
		this.customerOptionValue = customerOptionValue;
	}
	public CustomerOptionValue getCustomerOptionValue() {
		System.out.println("$#9049#"); return customerOptionValue;
	}
	public void setCustomerOption(CustomerOption customerOption) {
		this.customerOption = customerOption;
	}
	public CustomerOption getCustomerOption() {
		System.out.println("$#9050#"); return customerOption;
	}


}
