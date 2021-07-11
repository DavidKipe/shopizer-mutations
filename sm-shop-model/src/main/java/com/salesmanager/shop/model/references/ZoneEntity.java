package com.salesmanager.shop.model.references;

import com.salesmanager.shop.model.entity.Entity;

public class ZoneEntity extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String countryCode;
	private String code;
	public String getCountryCode() {
		System.out.println("$#9263#"); return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getCode() {
		System.out.println("$#9264#"); return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

}
