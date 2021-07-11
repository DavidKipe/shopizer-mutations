package com.salesmanager.shop.model.catalog.product.attribute;

import java.io.Serializable;

import com.salesmanager.shop.model.entity.Entity;


public class ProductOptionValue extends Entity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	private boolean defaultValue;
	private int sortOrder;
	private String image;
	
	public String getCode() {
		System.out.println("$#8807#"); return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public boolean isDefaultValue() {
		System.out.println("$#8809#"); System.out.println("$#8808#"); return defaultValue;
	}
	public void setDefaultValue(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}
	public int getSortOrder() {
		System.out.println("$#8810#"); return sortOrder;
	}
	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}
	public String getImage() {
		System.out.println("$#8811#"); return image;
	}
	public void setImage(String image) {
		this.image = image;
	}


}
