package com.salesmanager.shop.admin.model.orders;

import java.io.Serializable;

public class Refund implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2392736671094915447L;
	private Long orderId;
	private String amount;
	public Long getOrderId() {
		System.out.println("$#7822#"); return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public String getAmount() {
		System.out.println("$#7823#"); return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}

}
