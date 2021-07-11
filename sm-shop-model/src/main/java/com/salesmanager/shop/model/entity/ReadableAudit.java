package com.salesmanager.shop.model.entity;

public class ReadableAudit {

	private String created;
	private String modified;
	private String user;
	public String getCreated() {
		System.out.println("$#9099#"); return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getModified() {
		System.out.println("$#9100#"); return modified;
	}
	public void setModified(String modified) {
		this.modified = modified;
	}
	public String getUser() {
		System.out.println("$#9101#"); return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
}
