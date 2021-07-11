package com.salesmanager.core.model.customer;

import com.salesmanager.core.model.common.Criteria;

public class CustomerCriteria extends Criteria {
	
	private String firstName;
	private String lastName;
	private String name;
	private String email;
	private String country;
	public String getFirstName() {
		System.out.println("$#4203#"); return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		System.out.println("$#4204#"); return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getName() {
		System.out.println("$#4205#"); return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		System.out.println("$#4206#"); return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCountry() {
		System.out.println("$#4207#"); return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}

}
