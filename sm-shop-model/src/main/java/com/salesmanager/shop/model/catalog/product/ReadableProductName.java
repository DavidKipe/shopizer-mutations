package com.salesmanager.shop.model.catalog.product;

public class ReadableProductName extends ProductEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;

	public String getName() {
		System.out.println("$#8952#"); return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
