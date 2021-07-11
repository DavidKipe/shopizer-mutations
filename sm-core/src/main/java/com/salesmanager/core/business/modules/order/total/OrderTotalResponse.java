package com.salesmanager.core.business.modules.order.total;

public class OrderTotalResponse {
	
	private Double discount = null;
	private String expiration;

	public Double getDiscount() {
		System.out.println("$#1453#"); return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public String getExpiration() {
		System.out.println("$#1454#"); return expiration;
	}

	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}

}
