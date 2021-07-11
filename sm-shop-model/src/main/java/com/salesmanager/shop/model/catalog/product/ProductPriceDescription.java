package com.salesmanager.shop.model.catalog.product;

import com.salesmanager.shop.model.catalog.NamedEntity;

public class ProductPriceDescription extends NamedEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String priceAppender;

	public String getPriceAppender() {
		System.out.println("$#8903#"); return priceAppender;
	}

	public void setPriceAppender(String priceAppender) {
		this.priceAppender = priceAppender;
	}

}
