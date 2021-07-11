package com.salesmanager.core.modules.integration.shipping.model;

import java.math.BigDecimal;

public abstract class CustomShippingQuoteItem {
	
	private String priceText;
	private BigDecimal price;
	public void setPriceText(String priceText) {
		this.priceText = priceText;
	}
	public String getPriceText() {
		System.out.println("$#4888#"); return priceText;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public BigDecimal getPrice() {
		System.out.println("$#4889#"); return price;
	}

}
