package com.salesmanager.shop.model.catalog.product.attribute.api;

public class ReadableProductAttributeEntity extends ProductAttributeEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String productAttributeWeight;
	private String productAttributePrice;
	
	private ReadableProductOptionEntity option;
	private ReadableProductOptionValueEntity optionValue;
	public String getProductAttributeWeight() {
		System.out.println("$#8772#"); return productAttributeWeight;
	}
	public void setProductAttributeWeight(String productAttributeWeight) {
		this.productAttributeWeight = productAttributeWeight;
	}
	public String getProductAttributePrice() {
		System.out.println("$#8773#"); return productAttributePrice;
	}
	public void setProductAttributePrice(String productAttributePrice) {
		this.productAttributePrice = productAttributePrice;
	}
	public ReadableProductOptionEntity getOption() {
		System.out.println("$#8774#"); return option;
	}
	public void setOption(ReadableProductOptionEntity option) {
		this.option = option;
	}
	public ReadableProductOptionValueEntity getOptionValue() {
		System.out.println("$#8775#"); return optionValue;
	}
	public void setOptionValue(ReadableProductOptionValueEntity optionValue) {
		this.optionValue = optionValue;
	}


}
