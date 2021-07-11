package com.salesmanager.shop.model.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.salesmanager.core.model.order.orderstatus.OrderStatus;
import com.salesmanager.core.model.order.payment.CreditCard;
import com.salesmanager.core.model.payments.PaymentType;
import com.salesmanager.shop.model.order.total.OrderTotal;
import com.salesmanager.shop.model.order.v0.Order;

public class OrderEntity extends Order implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<OrderTotal> totals;
	private List<OrderAttribute> attributes = new ArrayList<OrderAttribute>();
	
	private PaymentType paymentType;
	private String paymentModule;
	private String shippingModule;
	private List<OrderStatus> previousOrderStatus;
	private OrderStatus orderStatus;
	private CreditCard creditCard;
	private Date datePurchased;
	private String currency;
	private boolean customerAgreed;
	private boolean confirmedAddress;
	private String comments;
	
	public void setTotals(List<OrderTotal> totals) {
		this.totals = totals;
	}
	public List<OrderTotal> getTotals() {
		System.out.println("$#9121#"); return totals;
	}
	public PaymentType getPaymentType() {
		System.out.println("$#9122#"); return paymentType;
	}
	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}
	public String getPaymentModule() {
		System.out.println("$#9123#"); return paymentModule;
	}
	public void setPaymentModule(String paymentModule) {
		this.paymentModule = paymentModule;
	}
	public String getShippingModule() {
		System.out.println("$#9124#"); return shippingModule;
	}
	public void setShippingModule(String shippingModule) {
		this.shippingModule = shippingModule;
	}

	public CreditCard getCreditCard() {
		System.out.println("$#9125#"); return creditCard;
	}
	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}
	public Date getDatePurchased() {
		System.out.println("$#9126#"); return datePurchased;
	}
	public void setDatePurchased(Date datePurchased) {
		this.datePurchased = datePurchased;
	}
	public void setPreviousOrderStatus(List<OrderStatus> previousOrderStatus) {
		this.previousOrderStatus = previousOrderStatus;
	}
	public List<OrderStatus> getPreviousOrderStatus() {
		System.out.println("$#9127#"); return previousOrderStatus;
	}
	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}
	public OrderStatus getOrderStatus() {
		System.out.println("$#9128#"); return orderStatus;
	}
	public String getCurrency() {
		System.out.println("$#9129#"); return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public boolean isCustomerAgreed() {
		System.out.println("$#9131#"); System.out.println("$#9130#"); return customerAgreed;
	}
	public void setCustomerAgreed(boolean customerAgreed) {
		this.customerAgreed = customerAgreed;
	}
	public boolean isConfirmedAddress() {
		System.out.println("$#9133#"); System.out.println("$#9132#"); return confirmedAddress;
	}
	public void setConfirmedAddress(boolean confirmedAddress) {
		this.confirmedAddress = confirmedAddress;
	}
	public String getComments() {
		System.out.println("$#9134#"); return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public List<OrderAttribute> getAttributes() {
		System.out.println("$#9135#"); return attributes;
	}
	public void setAttributes(List<OrderAttribute> attributes) {
		this.attributes = attributes;
	}


}
