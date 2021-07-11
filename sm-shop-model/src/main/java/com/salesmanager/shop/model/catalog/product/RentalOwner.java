package com.salesmanager.shop.model.catalog.product;

import com.salesmanager.shop.model.customer.address.Address;
import com.salesmanager.shop.model.entity.Entity;

/**
 * RENTAL customer
 * @author c.samson
 *
 */
public class RentalOwner extends Entity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String firstName;
	private String lastName;
	private Address address;
	private String emailAddress;
	public String getFirstName() {
		System.out.println("$#8961#"); return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		System.out.println("$#8962#"); return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Address getAddress() {
		System.out.println("$#8963#"); return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public String getEmailAddress() {
		System.out.println("$#8964#"); return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
}
