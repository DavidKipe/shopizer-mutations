package com.salesmanager.core.model.user;

import com.salesmanager.core.model.common.Criteria;

public class UserCriteria extends Criteria {
	
	private String adminEmail;
	private String adminName;
	private boolean active = true;
	public String getAdminEmail() {
		System.out.println("$#4882#"); return adminEmail;
	}
	public void setAdminEmail(String adminEmail) {
		this.adminEmail = adminEmail;
	}
	public boolean isActive() {
		System.out.println("$#4884#"); System.out.println("$#4883#"); return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getAdminName() {
		System.out.println("$#4885#"); return adminName;
	}
	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

}
