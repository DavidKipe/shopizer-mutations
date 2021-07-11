package com.salesmanager.shop.model.catalog.product.type;

import java.io.Serializable;

import com.salesmanager.shop.model.entity.Entity;

public class ProductTypeEntity extends Entity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String code;
	private String name;
	boolean allowAddToCart;

	public boolean isAllowAddToCart() {
		System.out.println("$#8966#"); System.out.println("$#8965#"); return allowAddToCart;
	}

	public void setAllowAddToCart(boolean allowAddToCart) {
		this.allowAddToCart = allowAddToCart;
	}

	public String getCode() {
		System.out.println("$#8967#"); return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		System.out.println("$#8968#"); return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
