package com.salesmanager.shop.model.order.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.salesmanager.shop.model.order.transaction.PersistablePayment;

/**
 * This object is used when processing an order from the API
 * It will be used for processing the payment and as Order meta data
 * @author c.samson
 *
 */
public class PersistableOrder extends Order {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PersistablePayment payment;
	private Long shippingQuote;
	@JsonIgnore
	private Long shoppingCartId;
	@JsonIgnore
	private Long customerId;
	
	
	
	public Long getShoppingCartId() {
		System.out.println("$#9237#"); return shoppingCartId;
	}

	public void setShoppingCartId(Long shoppingCartId) {
		this.shoppingCartId = shoppingCartId;
	}

	public Long getCustomerId() {
		System.out.println("$#9238#"); return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public PersistablePayment getPayment() {
		System.out.println("$#9239#"); return payment;
	}

	public void setPayment(PersistablePayment payment) {
		this.payment = payment;
	}

	public Long getShippingQuote() {
		System.out.println("$#9240#"); return shippingQuote;
	}

	public void setShippingQuote(Long shippingQuote) {
		this.shippingQuote = shippingQuote;
	}
	


}
