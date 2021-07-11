package com.salesmanager.core.model.security;

import java.io.Serializable;

public class Secrets implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String userName;
	private String password;
	public String getUserName() {
		System.out.println("$#4525#"); return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		System.out.println("$#4526#"); return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

}
