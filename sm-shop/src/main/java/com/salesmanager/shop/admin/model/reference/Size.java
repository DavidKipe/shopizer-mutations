package com.salesmanager.shop.admin.model.reference;

import java.io.Serializable;

public class Size implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4737272153685816396L;
	private String code;
	private String name;
	public String getCode() {
		System.out.println("$#7833#"); return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		System.out.println("$#7834#"); return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Size(String code, String name) {
		this.code = code;
		this.name = name;
	}

}
