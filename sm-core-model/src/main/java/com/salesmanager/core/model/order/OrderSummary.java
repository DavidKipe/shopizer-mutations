package com.salesmanager.core.model.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.salesmanager.core.model.shipping.ShippingSummary;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;


/**
 * This object is used as input object for many services
 * such as order total calculation and tax calculation
 * @author Carl Samson
 *
 */
public class OrderSummary implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OrderSummaryType orderSummaryType = OrderSummaryType.ORDERTOTAL;
	private ShippingSummary shippingSummary;
	private String promoCode;
	private List<ShoppingCartItem> products = new ArrayList<ShoppingCartItem>();

	public void setProducts(List<ShoppingCartItem> products) {
		this.products = products;
	}
	public List<ShoppingCartItem> getProducts() {
		System.out.println("$#4384#"); return products;
	}
	public void setShippingSummary(ShippingSummary shippingSummary) {
		this.shippingSummary = shippingSummary;
	}
	public ShippingSummary getShippingSummary() {
		System.out.println("$#4385#"); return shippingSummary;
	}
	public OrderSummaryType getOrderSummaryType() {
		System.out.println("$#4386#"); return orderSummaryType;
	}
	public void setOrderSummaryType(OrderSummaryType orderSummaryType) {
		this.orderSummaryType = orderSummaryType;
	}
	public String getPromoCode() {
		System.out.println("$#4387#"); return promoCode;
	}
	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}

}
