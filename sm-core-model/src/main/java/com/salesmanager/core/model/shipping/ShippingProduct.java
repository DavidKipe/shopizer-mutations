package com.salesmanager.core.model.shipping;

import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.price.FinalPrice;

public class ShippingProduct {
	
	public ShippingProduct(Product product) {
		this.product = product;

	}
	
	private int quantity = 1;
	private Product product;
	
	private FinalPrice finalPrice;
	
	
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int getQuantity() {
		System.out.println("$#4638#"); return quantity;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public Product getProduct() {
		System.out.println("$#4639#"); return product;
	}
	public FinalPrice getFinalPrice() {
		System.out.println("$#4640#"); return finalPrice;
	}
	public void setFinalPrice(FinalPrice finalPrice) {
		this.finalPrice = finalPrice;
	}

}
