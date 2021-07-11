package com.salesmanager.shop.model.order.history;

import com.salesmanager.shop.model.entity.Entity;

public class OrderStatusHistory extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long orderId;
	private String orderStatus;
	private String comments;
	
	
	
	public long getOrderId() {
		System.out.println("$#9114#"); return orderId;
	}
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	public String getOrderStatus() {
		System.out.println("$#9115#"); return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	public String getComments() {
		System.out.println("$#9116#"); return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}

}
