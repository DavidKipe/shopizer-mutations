package com.salesmanager.shop.model.order.total;

import java.io.Serializable;
import java.util.List;

/**
 * Serves as the order total summary calculation
 * @author c.samson
 *
 */
public class ReadableTotal implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<ReadableOrderTotal> totals;
	private String grandTotal;
	public List<ReadableOrderTotal> getTotals() {
		System.out.println("$#9198#"); return totals;
	}
	public void setTotals(List<ReadableOrderTotal> totals) {
		this.totals = totals;
	}
	public String getGrandTotal() {
		System.out.println("$#9199#"); return grandTotal;
	}
	public void setGrandTotal(String grandTotal) {
		this.grandTotal = grandTotal;
	}

}
