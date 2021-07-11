package com.salesmanager.shop.model.customer.address;

import java.io.Serializable;

public class AddressLocation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String postalCode;
	private String countryCode;
	
	public String getPostalCode() {
		System.out.println("$#9041#"); return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

  public String getCountryCode() {
				System.out.println("$#9042#"); return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

}
