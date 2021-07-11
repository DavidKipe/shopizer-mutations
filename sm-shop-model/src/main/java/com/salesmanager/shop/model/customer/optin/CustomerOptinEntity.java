package com.salesmanager.shop.model.customer.optin;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Email;


public class CustomerOptinEntity extends CustomerOptin {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String firstName;
	private String lastName;
	@NotNull
	@Email
	private String email;
	public String getFirstName() {
		System.out.println("$#9081#"); return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		System.out.println("$#9082#"); return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		System.out.println("$#9083#"); return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

}
