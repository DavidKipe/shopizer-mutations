package com.salesmanager.shop.model.order;

import java.io.Serializable;
import java.util.List;

public class ReadableOrderProduct extends OrderProductEntity implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String productName;
	private String price;
	private String subTotal;
	
	private List<ReadableOrderProductAttribute> attributes = null;
	
	private String sku;
	private String image;
	public String getProductName() {
		System.out.println("$#9140#"); return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getSku() {
		System.out.println("$#9141#"); return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public String getImage() {
		System.out.println("$#9142#"); return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getPrice() {
		System.out.println("$#9143#"); return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getSubTotal() {
		System.out.println("$#9144#"); return subTotal;
	}
	public void setSubTotal(String subTotal) {
		this.subTotal = subTotal;
	}
	public List<ReadableOrderProductAttribute> getAttributes() {
		System.out.println("$#9145#"); return attributes;
	}
	public void setAttributes(List<ReadableOrderProductAttribute> attributes) {
		this.attributes = attributes;
	}


}
