package com.salesmanager.shop.model.order.v0;

import com.salesmanager.core.model.reference.currency.Currency;
import com.salesmanager.shop.model.customer.ReadableBilling;
import com.salesmanager.shop.model.customer.ReadableCustomer;
import com.salesmanager.shop.model.customer.ReadableDelivery;
import com.salesmanager.shop.model.customer.address.Address;
import com.salesmanager.shop.model.order.OrderEntity;
import com.salesmanager.shop.model.order.ReadableOrderProduct;
import com.salesmanager.shop.model.order.total.OrderTotal;
import com.salesmanager.shop.model.store.ReadableMerchantStore;

import java.io.Serializable;
import java.util.List;


public class ReadableOrder extends OrderEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ReadableCustomer customer;
	private List<ReadableOrderProduct> products;
	private Currency currencyModel;
	
	private ReadableBilling billing;
	private ReadableDelivery delivery;
	private ReadableMerchantStore store;
	
	
	
	public void setCustomer(ReadableCustomer customer) {
		this.customer = customer;
	}
	public ReadableCustomer getCustomer() {
		System.out.println("$#9221#"); return customer;
	}
	public OrderTotal getTotal() {
		System.out.println("$#9222#"); return total;
	}
	public void setTotal(OrderTotal total) {
		this.total = total;
	}
	public OrderTotal getTax() {
		System.out.println("$#9223#"); return tax;
	}
	public void setTax(OrderTotal tax) {
		this.tax = tax;
	}
	public OrderTotal getShipping() {
		System.out.println("$#9224#"); return shipping;
	}
	public void setShipping(OrderTotal shipping) {
		this.shipping = shipping;
	}

	public List<ReadableOrderProduct> getProducts() {
		System.out.println("$#9225#"); return products;
	}
	public void setProducts(List<ReadableOrderProduct> products) {
		this.products = products;
	}

	public Currency getCurrencyModel() {
		System.out.println("$#9226#"); return currencyModel;
	}
	public void setCurrencyModel(Currency currencyModel) {
		this.currencyModel = currencyModel;
	}

	public ReadableBilling getBilling() {
		System.out.println("$#9227#"); return billing;
	}
	public void setBilling(ReadableBilling billing) {
		this.billing = billing;
	}

	public Address getDelivery() {
		System.out.println("$#9228#"); return delivery;
	}
	public void setDelivery(ReadableDelivery delivery) {
		this.delivery = delivery;
	}

	public ReadableMerchantStore getStore() {
		System.out.println("$#9229#"); return store;
	}
	public void setStore(ReadableMerchantStore store) {
		this.store = store;
	}

	private OrderTotal total;
	private OrderTotal tax;
	private OrderTotal shipping;

}
