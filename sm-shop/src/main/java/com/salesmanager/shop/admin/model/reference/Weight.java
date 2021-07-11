package com.salesmanager.shop.admin.model.reference;

import java.io.Serializable;

public class Weight implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1006772612089740285L;
	private String code;
	private String name;
	public String getName() {
		System.out.println("$#7835#"); return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		System.out.println("$#7836#"); return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public Weight(String code, String name) {
		this.code = code;
		this.name = name;
	}

}
