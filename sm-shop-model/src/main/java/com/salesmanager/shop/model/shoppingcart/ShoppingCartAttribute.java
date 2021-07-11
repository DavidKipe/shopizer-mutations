package com.salesmanager.shop.model.shoppingcart;

import java.io.Serializable;

import com.salesmanager.shop.model.entity.ShopEntity;

public class ShoppingCartAttribute extends ShopEntity implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long optionId;
	private long optionValueId;
	private long attributeId;
	private String optionName;
	private String optionValue;
	public long getOptionId() {
		System.out.println("$#9317#"); return optionId;
	}
	public void setOptionId(long optionId) {
		this.optionId = optionId;
	}
	public long getOptionValueId() {
		System.out.println("$#9318#"); return optionValueId;
	}
	public void setOptionValueId(long optionValueId) {
		this.optionValueId = optionValueId;
	}
	public String getOptionName() {
		System.out.println("$#9319#"); return optionName;
	}
	public void setOptionName(String optionName) {
		this.optionName = optionName;
	}
	public String getOptionValue() {
		System.out.println("$#9320#"); return optionValue;
	}
	public void setOptionValue(String optionValue) {
		this.optionValue = optionValue;
	}
	public long getAttributeId() {
		System.out.println("$#9321#"); return attributeId;
	}
	public void setAttributeId(long attributeId) {
		this.attributeId = attributeId;
	}

}
