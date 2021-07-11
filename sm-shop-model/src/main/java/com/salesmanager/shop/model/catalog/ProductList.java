package com.salesmanager.shop.model.catalog;

import com.salesmanager.shop.model.catalog.product.ReadableProduct;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductList implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int productCount;
	private BigDecimal minPrice;
	private BigDecimal maxPrice;
	private List<ReadableProduct> products = new ArrayList<ReadableProduct>();
	public void setProducts(List<ReadableProduct> products) {
		this.products = products;
	}
	public List<ReadableProduct> getProducts() {
		System.out.println("$#8970#"); return products;
	}
	public int getProductCount() {
		System.out.println("$#8971#"); return productCount;
	}
	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}
	public BigDecimal getMinPrice() {
		System.out.println("$#8972#"); return minPrice;
	}
	public void setMinPrice(BigDecimal minPrice) {
		this.minPrice = minPrice;
	}
	public BigDecimal getMaxPrice() {
		System.out.println("$#8973#"); return maxPrice;
	}
	public void setMaxPrice(BigDecimal maxPrice) {
		this.maxPrice = maxPrice;
	}


}
