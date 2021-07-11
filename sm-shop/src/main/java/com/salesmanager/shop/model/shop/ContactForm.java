package com.salesmanager.shop.model.shop;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class ContactForm {
	
	@NotEmpty
	private String name;
	@NotEmpty
	private String subject;
	@Email
	private String email;
	@NotEmpty
	private String comment;

	
	public String getName() {
		System.out.println("$#9287#"); return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		System.out.println("$#9288#"); return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getComment() {
		System.out.println("$#9289#"); return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getSubject() {
		System.out.println("$#9290#"); return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}


}
