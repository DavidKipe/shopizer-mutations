package com.salesmanager.shop.model.shoppingcart;

import java.io.Serializable;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.salesmanager.shop.model.entity.ShopEntity;
import com.salesmanager.shop.model.order.total.OrderTotal;


@Component
@Scope(value = "prototype")
public class ShoppingCartData extends ShopEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;
	private String code;
	private int quantity;
	private String total;
	private String subTotal;
	private Long orderId;
	
	private List<OrderTotal> totals;//calculated from OrderTotalSummary
	private List<ShoppingCartItem> shoppingCartItems;
	private List<ShoppingCartItem> unavailables;
	
	
	public String getMessage() {
		System.out.println("$#9322#"); return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getCode() {
		System.out.println("$#9323#"); return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getQuantity() {
		System.out.println("$#9324#"); return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getTotal() {
		System.out.println("$#9325#"); return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public List<ShoppingCartItem> getShoppingCartItems() {
		System.out.println("$#9326#"); return shoppingCartItems;
	}
	public void setShoppingCartItems(List<ShoppingCartItem> shoppingCartItems) {
		this.shoppingCartItems = shoppingCartItems;
	}
	public String getSubTotal() {
		System.out.println("$#9327#"); return subTotal;
	}
	public void setSubTotal(String subTotal) {
		this.subTotal = subTotal;
	}
	public List<OrderTotal> getTotals() {
		System.out.println("$#9328#"); return totals;
	}
	public void setTotals(List<OrderTotal> totals) {
		this.totals = totals;
	}
	public List<ShoppingCartItem> getUnavailables() {
		System.out.println("$#9329#"); return unavailables;
	}
	public void setUnavailables(List<ShoppingCartItem> unavailables) {
		this.unavailables = unavailables;
	}
	public Long getOrderId() {
		System.out.println("$#9330#"); return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}



}
