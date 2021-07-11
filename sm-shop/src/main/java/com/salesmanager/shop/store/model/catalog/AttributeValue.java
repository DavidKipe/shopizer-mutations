package com.salesmanager.shop.store.model.catalog;

import java.io.Serializable;

import com.salesmanager.shop.model.entity.ShopEntity;

public class AttributeValue extends ShopEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name = null;
	private String description = null;
	private boolean defaultAttribute;
	private String image;
	private String price;
	private int sortOrder;

	public String getName() {
		System.out.println("$#15177#"); return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isDefaultAttribute() {
		System.out.println("$#15179#"); System.out.println("$#15178#"); return defaultAttribute;
	}
	public void setDefaultAttribute(boolean defaultAttribute) {
		this.defaultAttribute = defaultAttribute;
	}
	public String getImage() {
		System.out.println("$#15180#"); return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getPrice() {
		System.out.println("$#15181#"); return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getDescription() {
		System.out.println("$#15182#"); return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getSortOrder() {
		System.out.println("$#15183#"); return sortOrder;
	}
	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

}
