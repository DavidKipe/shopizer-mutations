package com.salesmanager.shop.model.catalog.product.attribute;

public class ReadableProductAttributeValue extends ProductOptionValue {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String lang;
	private String description;

	public String getName() {
		System.out.println("$#8817#"); return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLang() {
		System.out.println("$#8818#"); return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getDescription() {
		System.out.println("$#8819#"); return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


}
