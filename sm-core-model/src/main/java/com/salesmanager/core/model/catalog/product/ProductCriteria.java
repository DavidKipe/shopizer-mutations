package com.salesmanager.core.model.catalog.product;

import java.util.List;

import com.salesmanager.core.model.catalog.product.attribute.AttributeCriteria;
import com.salesmanager.core.model.common.Criteria;

public class ProductCriteria extends Criteria {
	
	
	private String productName;
	private List<AttributeCriteria> attributeCriteria;

	
	private Boolean available = null;
	
	private List<Long> categoryIds;
	private List<String> availabilities;
	private List<Long> productIds;
	
	private String status;
	
	private Long manufacturerId = null;
	
	private Long ownerId = null;

	public String getProductName() {
		System.out.println("$#3972#"); return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}


	public List<Long> getCategoryIds() {
		System.out.println("$#3973#"); return categoryIds;
	}

	public void setCategoryIds(List<Long> categoryIds) {
		this.categoryIds = categoryIds;
	}

	public List<String> getAvailabilities() {
		System.out.println("$#3974#"); return availabilities;
	}

	public void setAvailabilities(List<String> availabilities) {
		this.availabilities = availabilities;
	}

	public Boolean getAvailable() {
		System.out.println("$#3976#"); System.out.println("$#3975#"); return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
	}

	public void setAttributeCriteria(List<AttributeCriteria> attributeCriteria) {
		this.attributeCriteria = attributeCriteria;
	}

	public List<AttributeCriteria> getAttributeCriteria() {
		System.out.println("$#3977#"); return attributeCriteria;
	}

	public void setProductIds(List<Long> productIds) {
		this.productIds = productIds;
	}

	public List<Long> getProductIds() {
		System.out.println("$#3978#"); return productIds;
	}

	public void setManufacturerId(Long manufacturerId) {
		this.manufacturerId = manufacturerId;
	}

	public Long getManufacturerId() {
		System.out.println("$#3979#"); return manufacturerId;
	}

	public String getStatus() {
		System.out.println("$#3980#"); return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getOwnerId() {
		System.out.println("$#3981#"); return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}



}
