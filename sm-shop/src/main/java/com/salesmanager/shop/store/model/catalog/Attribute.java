package com.salesmanager.shop.store.model.catalog;

import java.io.Serializable;
import java.util.List;

import com.salesmanager.shop.model.entity.ShopEntity;


public class Attribute extends ShopEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name = null;
	private String type = null;
	private String code = null;
	private List<AttributeValue> values = null;
	private AttributeValue readOnlyValue = null;
	public String getName() {
		System.out.println("$#15172#"); return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		System.out.println("$#15173#"); return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	


	public void setValues(List<AttributeValue> values) {
		this.values = values;
	}
	public List<AttributeValue> getValues() {
		System.out.println("$#15174#"); return values;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCode() {
		System.out.println("$#15175#"); return code;
	}
	public void setReadOnlyValue(AttributeValue readOnlyValue) {
		this.readOnlyValue = readOnlyValue;
	}
	public AttributeValue getReadOnlyValue() {
		System.out.println("$#15176#"); return readOnlyValue;
	}



	

}
