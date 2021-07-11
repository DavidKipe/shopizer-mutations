package com.salesmanager.shop.model.order;

import java.io.Serializable;

import com.salesmanager.shop.model.entity.Entity;

public class ReadableOrderProductAttribute extends Entity implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String attributeName;
	private String attributePrice;
	private String attributeValue;
	public String getAttributeName() {
		System.out.println("$#9146#"); return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	public String getAttributePrice() {
		System.out.println("$#9147#"); return attributePrice;
	}
	public void setAttributePrice(String attributePrice) {
		this.attributePrice = attributePrice;
	}
	public String getAttributeValue() {
		System.out.println("$#9148#"); return attributeValue;
	}
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

}
