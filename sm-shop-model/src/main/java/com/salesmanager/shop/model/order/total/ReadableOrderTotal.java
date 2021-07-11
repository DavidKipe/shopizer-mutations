package com.salesmanager.shop.model.order.total;

import java.io.Serializable;

public class ReadableOrderTotal extends OrderTotal implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String total;
	private boolean discounted;
	public String getTotal() {
		System.out.println("$#9195#"); return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public boolean isDiscounted() {
		System.out.println("$#9197#"); System.out.println("$#9196#"); return discounted;
	}
	public void setDiscounted(boolean discounted) {
		this.discounted = discounted;
	}

}
