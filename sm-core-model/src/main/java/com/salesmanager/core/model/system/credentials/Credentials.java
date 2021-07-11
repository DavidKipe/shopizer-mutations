package com.salesmanager.core.model.system.credentials;

public abstract class Credentials {
	
	private String userName;
	private String password;
	public String getUserName() {
		System.out.println("$#4699#"); return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		System.out.println("$#4700#"); return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

}
