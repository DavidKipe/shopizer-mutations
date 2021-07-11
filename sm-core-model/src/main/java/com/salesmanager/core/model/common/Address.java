package com.salesmanager.core.model.common;

import java.io.Serializable;


public class Address implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String city;
	private String postalCode;
	private String stateProvince;
	private String zone;//code
	private String country;//code

	public void setStateProvince(String stateProvince) {
		this.stateProvince = stateProvince;
	}

	public void setCountry(String country) {
		this.country = country;
	}


	public String getCity() {
		System.out.println("$#4011#"); return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPostalCode() {
		System.out.println("$#4012#"); return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getStateProvince() {
		System.out.println("$#4013#"); return stateProvince;
	}

	public String getCountry() {
		System.out.println("$#4014#"); return country;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getZone() {
		System.out.println("$#4015#"); return zone;
	}



}
