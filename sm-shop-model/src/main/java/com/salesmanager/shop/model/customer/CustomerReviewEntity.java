package com.salesmanager.shop.model.customer;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import javax.validation.constraints.NotEmpty;

import com.salesmanager.shop.model.entity.ShopEntity;


public class CustomerReviewEntity extends ShopEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@NotEmpty
	private String description;
	private Long customerId;//review creator
	private String date;
	
	@NotNull
	@Min(1)
	@Max(5)
	private Double rating;
	public String getDescription() {
		System.out.println("$#9075#"); return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public Double getRating() {
		System.out.println("$#9076#"); return rating;
	}
	public void setRating(Double rating) {
		this.rating = rating;
	}
	public String getDate() {
		System.out.println("$#9077#"); return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Long getCustomerId() {
		System.out.println("$#9078#"); return customerId;
	}
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}


}
