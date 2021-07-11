package com.salesmanager.shop.model.security;

import java.io.Serializable;

public class GroupEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private String type;

	public String getName() {
		System.out.println("$#9265#"); return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		System.out.println("$#9266#"); return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
