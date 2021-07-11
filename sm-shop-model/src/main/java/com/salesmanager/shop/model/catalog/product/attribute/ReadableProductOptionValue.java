package com.salesmanager.shop.model.catalog.product.attribute;

public class ReadableProductOptionValue extends ProductOptionValue {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String price;
	private String image;
	private String name;


	public String getName() {
		System.out.println("$#8823#"); return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrice() {
		System.out.println("$#8824#"); return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getImage() {
		System.out.println("$#8825#"); return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

}
