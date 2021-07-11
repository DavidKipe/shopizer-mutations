package com.salesmanager.core.model.common;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import javax.validation.constraints.NotEmpty;

import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.zone.Zone;

@Embeddable
public class Billing {
	
	@NotEmpty
	@Column (name ="BILLING_LAST_NAME", length=64, nullable=false)
	private String lastName;

	@NotEmpty
	@Column (name ="BILLING_FIRST_NAME", length=64, nullable=false)
	private String firstName;
	


	@Column (name ="BILLING_COMPANY", length=100)
	private String company;
	
	@Column (name ="BILLING_STREET_ADDRESS", length=256)
	private String address;
	
	
	@Column (name ="BILLING_CITY", length=100)
	private String city;
	
	@Column (name ="BILLING_POSTCODE", length=20)
	private String postalCode;
	
	@Column(name="BILLING_TELEPHONE", length=32)
	private String telephone;
	
	@Column (name ="BILLING_STATE", length=100)
	private String state;
	
	@Column (name ="LONGITUDE", length=100)
	private String longitude;
	
	@Column (name ="LATITUDE", length=100)
	private String latitude;


	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Country.class)
	@JoinColumn(name="BILLING_COUNTRY_ID", nullable=false)
	private Country country;
	
	
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Zone.class)
	@JoinColumn(name="BILLING_ZONE_ID", nullable=true)
	private Zone zone;



	public String getCompany() {
		System.out.println("$#4029#"); return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getAddress() {
		System.out.println("$#4030#"); return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		System.out.println("$#4031#"); return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPostalCode() {
		System.out.println("$#4032#"); return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	
	public Country getCountry() {
		System.out.println("$#4033#"); return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Zone getZone() {
		System.out.println("$#4034#"); return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}
	
	public String getState() {
		System.out.println("$#4035#"); return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getTelephone() {
		System.out.println("$#4036#"); return telephone;
	}
	
	
	public String getLastName() {
		System.out.println("$#4037#"); return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getFirstName() {
		System.out.println("$#4038#"); return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLongitude() {
		System.out.println("$#4039#"); return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		System.out.println("$#4040#"); return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
}
