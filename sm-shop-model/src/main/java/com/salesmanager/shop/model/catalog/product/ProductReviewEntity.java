package com.salesmanager.shop.model.catalog.product;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import javax.validation.constraints.NotEmpty;

import com.salesmanager.shop.model.entity.ShopEntity;


public class ProductReviewEntity extends ShopEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@NotEmpty
	private String description;
	private Long productId;
	private String date;
	
	@NotNull
	@Min(1)
	@Max(5)
	private Double rating;
	public String getDescription() {
		System.out.println("$#8914#"); return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getProductId() {
		System.out.println("$#8915#"); return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Double getRating() {
		System.out.println("$#8916#"); return rating;
	}
	public void setRating(Double rating) {
		this.rating = rating;
	}
	public String getDate() {
		System.out.println("$#8917#"); return date;
	}
	public void setDate(String date) {
		this.date = date;
	}


}
