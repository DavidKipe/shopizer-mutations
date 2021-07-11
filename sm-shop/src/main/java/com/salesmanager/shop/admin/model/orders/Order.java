package com.salesmanager.shop.admin.model.orders;

import com.salesmanager.core.model.common.Billing;
import com.salesmanager.core.model.common.Delivery;
import com.salesmanager.core.model.order.orderstatus.OrderStatus;

import javax.persistence.Embedded;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;


public class Order implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long Id;
	private String orderHistoryComment = "";
	
	List<OrderStatus> orderStatusList = Arrays.asList(OrderStatus.values());     
	private String datePurchased = "";
	private  com.salesmanager.core.model.order.Order order;
	
	@Embedded
	private com.salesmanager.core.model.common.Delivery delivery = null;
	
	@Embedded
	private com.salesmanager.core.model.common.Billing billing = null;
	
	
	
	
	public String getDatePurchased() {
		System.out.println("$#7815#"); return datePurchased;
	}

	public void setDatePurchased(String datePurchased) {
		this.datePurchased = datePurchased;
	}

	public Long getId() {
		System.out.println("$#7816#"); return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getOrderHistoryComment() {
		System.out.println("$#7817#"); return orderHistoryComment;
	}

	public void setOrderHistoryComment(String orderHistoryComment) {
		this.orderHistoryComment = orderHistoryComment;
	}

	public List<OrderStatus> getOrderStatusList() {
		System.out.println("$#7818#"); return orderStatusList;
	}

	public void setOrderStatusList(List<OrderStatus> orderStatusList) {
		this.orderStatusList = orderStatusList;
	}

	public com.salesmanager.core.model.order.Order getOrder() {
		System.out.println("$#7819#"); return order;
	}

	public void setOrder(com.salesmanager.core.model.order.Order order) {
		this.order = order;
	}

	public Delivery getDelivery() {
		System.out.println("$#7820#"); return delivery;
	}

	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}

	public Billing getBilling() {
		System.out.println("$#7821#"); return billing;
	}

	public void setBilling(Billing billing) {
		this.billing = billing;
	}




	
}