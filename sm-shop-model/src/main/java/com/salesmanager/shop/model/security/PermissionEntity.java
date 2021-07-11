package com.salesmanager.shop.model.security;

import java.io.Serializable;

public class PermissionEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	public String getName() {
		System.out.println("$#9267#"); return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getId() {
		System.out.println("$#9268#"); return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

}
