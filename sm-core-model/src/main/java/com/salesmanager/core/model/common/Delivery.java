package com.salesmanager.core.model.common;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.zone.Zone;

@Embeddable
public class Delivery {
	
	@Column (name ="DELIVERY_LAST_NAME", length=64)
	private String lastName;




	@Column (name ="DELIVERY_FIRST_NAME", length=64)
	private String firstName;

	


	@Column (name ="DELIVERY_COMPANY", length=100)
	private String company;
	
	@Column (name ="DELIVERY_STREET_ADDRESS", length=256)
	private String address;

	@Column (name ="DELIVERY_CITY", length=100)
	private String city;
	
	@Column (name ="DELIVERY_POSTCODE", length=20)
	private String postalCode;
	
	@Column (name ="DELIVERY_STATE", length=100)
	private String state;
	
	@Column(name="DELIVERY_TELEPHONE", length=32)
	private String telephone;
	
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Country.class)
	@JoinColumn(name="DELIVERY_COUNTRY_ID", nullable=true)
	private Country country;
	

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Zone.class)
	@JoinColumn(name="DELIVERY_ZONE_ID", nullable=true)
	private Zone zone;
	
	@Transient
	private String latitude = null;
	


	@Transient
	private String longitude = null;


	public String getCompany() {
		System.out.println("$#4056#"); return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getAddress() {
		System.out.println("$#4057#"); return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		System.out.println("$#4058#"); return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPostalCode() {
		System.out.println("$#4059#"); return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public Country getCountry() {
		System.out.println("$#4060#"); return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Zone getZone() {
		System.out.println("$#4061#"); return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}
	

	public String getState() {
		System.out.println("$#4062#"); return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getTelephone() {
		System.out.println("$#4063#"); return telephone;
	}	
	
	public String getLastName() {
		System.out.println("$#4064#"); return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getFirstName() {
		System.out.println("$#4065#"); return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLatitude() {
		System.out.println("$#4066#"); return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		System.out.println("$#4067#"); return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
}
