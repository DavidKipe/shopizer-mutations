package com.salesmanager.shop.model.customer;

import java.io.Serializable;

import javax.validation.Valid;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import com.salesmanager.shop.model.customer.address.Address;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringEscapeUtils;

public class CustomerEntity extends Customer implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(notes = "Customer email address. Required for registration")
	@Email (message="{messages.invalid.email}")
    @NotEmpty(message="{NotEmpty.customer.emailAddress}")
	private String emailAddress;
	@Valid
	@ApiModelProperty(notes = "Customer billing address")
	private Address billing;
	private Address delivery;
	@ApiModelProperty(notes = "Customer gender M | F")
	private String gender;

	@ApiModelProperty(notes = "2 letters language code en | fr | ...")
	private String language;
	private String firstName;
	private String lastName;
	
	private String provider;//online, facebook ...

	
	private String storeCode;
	
	//@ApiModelProperty(notes = "Username (use email address)")
	//@NotEmpty(message="{NotEmpty.customer.userName}")
	//can be email or anything else
	private String userName;
	
	private Double rating = 0D;
	private int ratingCount;
	
	public void setUserName(final String userName) {
		this.userName = StringEscapeUtils.escapeHtml4(userName);
	}

	public String getUserName() {
		System.out.println("$#9060#"); return userName;
	}


	public void setStoreCode(final String storeCode) {
		this.storeCode = StringEscapeUtils.escapeHtml4(storeCode);
	}


	public String getStoreCode() {
		System.out.println("$#9061#"); return storeCode;
	}


	public void setEmailAddress(final String emailAddress) {
		this.emailAddress = StringEscapeUtils.escapeHtml4(emailAddress);
	}
	

	public String getEmailAddress() {
		System.out.println("$#9062#"); return emailAddress;
	}


	public void setLanguage(final String language) {
		this.language = StringEscapeUtils.escapeHtml4(language);
	}
	public String getLanguage() {
		System.out.println("$#9063#"); return language;
	}
	

	public Address getBilling() {
		System.out.println("$#9064#"); return billing;
	}
	public void setBilling(final Address billing) {
		this.billing = billing;
	}
	public Address getDelivery() {
		System.out.println("$#9065#"); return delivery;
	}
	public void setDelivery(final Address delivery) {
		this.delivery = delivery;
	}
	public void setGender(final String gender) {
		this.gender = StringEscapeUtils.escapeHtml4(gender);
	}
	public String getGender() {
		System.out.println("$#9066#"); return gender;
	}


	public String getFirstName() {
		System.out.println("$#9067#"); return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = StringEscapeUtils.escapeHtml4(firstName);
	}


	public String getLastName() {
		System.out.println("$#9068#"); return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = StringEscapeUtils.escapeHtml4(lastName);
	}


	public int getRatingCount() {
		System.out.println("$#9069#"); return ratingCount;
	}

	public void setRatingCount(int ratingCount) {
		this.ratingCount = ratingCount;
	}

	public Double getRating() {
		System.out.println("$#9070#"); return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public String getProvider() {
		System.out.println("$#9071#"); return provider;
	}

	public void setProvider(String provider) {
		this.provider = StringEscapeUtils.escapeHtml4(provider);
	}



    

}
