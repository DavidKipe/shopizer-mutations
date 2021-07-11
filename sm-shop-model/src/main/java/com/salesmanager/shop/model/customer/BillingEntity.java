package com.salesmanager.shop.model.customer;

import javax.persistence.Transient;

import com.salesmanager.shop.model.customer.address.Address;

public class BillingEntity extends Address {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String email;
	
	@Transient
	private String countryName;
	
	@Transient
	private String provinceName;

	public String getCountryName() {
		System.out.println("$#9057#"); return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getProvinceName() {
		System.out.println("$#9058#"); return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getEmail() {
		System.out.println("$#9059#"); return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
