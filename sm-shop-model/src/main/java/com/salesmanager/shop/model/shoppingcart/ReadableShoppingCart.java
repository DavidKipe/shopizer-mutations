package com.salesmanager.shop.model.shoppingcart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.salesmanager.shop.model.order.total.ReadableOrderTotal;

/**
 * Compatible with v1
 * @author c.samson
 *
 */
public class ReadableShoppingCart extends ShoppingCartEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String code;
	private BigDecimal subtotal;


	private String displaySubTotal;
	private BigDecimal total;
	private String displayTotal;
	private int quantity;
	private Long order;
	
	List<ReadableShoppingCartItem> products = new ArrayList<ReadableShoppingCartItem>();
	List<ReadableOrderTotal> totals;
	
	private Long customer;



	public Long getCustomer() {
		System.out.println("$#9301#"); return customer;
	}



	public void setCustomer(Long customer) {
		this.customer = customer;
	}



	public List<ReadableOrderTotal> getTotals() {
		System.out.println("$#9302#"); return totals;
	}



	public void setTotals(List<ReadableOrderTotal> totals) {
		this.totals = totals;
	}



	public List<ReadableShoppingCartItem> getProducts() {
		System.out.println("$#9303#"); return products;
	}



	public void setProducts(List<ReadableShoppingCartItem> products) {
		this.products = products;
	}



	public String getCode() {
		System.out.println("$#9304#"); return code;
	}



	public void setCode(String code) {
		this.code = code;
	}
	
	public BigDecimal getSubtotal() {
		System.out.println("$#9305#"); return subtotal;
	}



	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}



	public String getDisplaySubTotal() {
		System.out.println("$#9306#"); return displaySubTotal;
	}



	public void setDisplaySubTotal(String displaySubTotal) {
		this.displaySubTotal = displaySubTotal;
	}



	public BigDecimal getTotal() {
		System.out.println("$#9307#"); return total;
	}



	public void setTotal(BigDecimal total) {
		this.total = total;
	}



	public String getDisplayTotal() {
		System.out.println("$#9308#"); return displayTotal;
	}



	public void setDisplayTotal(String displayTotal) {
		this.displayTotal = displayTotal;
	}



	public int getQuantity() {
		System.out.println("$#9309#"); return quantity;
	}



	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}



	public Long getOrder() {
		System.out.println("$#9310#"); return order;
	}



	public void setOrder(Long order) {
		this.order = order;
	}




}
