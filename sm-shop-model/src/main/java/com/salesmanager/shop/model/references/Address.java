package com.salesmanager.shop.model.references;

import java.io.Serializable;

public class Address implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String stateProvince;//code
	private String country;//code
	private String address;
	private String postalCode;
	private String city;
	
	private boolean active = true;
	public boolean isActive() {
		System.out.println("$#9249#"); System.out.println("$#9248#"); return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}

	public String getCountry() {
		System.out.println("$#9250#"); return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getAddress() {
		System.out.println("$#9251#"); return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPostalCode() {
		System.out.println("$#9252#"); return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getCity() {
		System.out.println("$#9253#"); return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getStateProvince() {
		System.out.println("$#9254#"); return stateProvince;
	}
	public void setStateProvince(String stateProvince) {
		this.stateProvince = stateProvince;
	}


}
