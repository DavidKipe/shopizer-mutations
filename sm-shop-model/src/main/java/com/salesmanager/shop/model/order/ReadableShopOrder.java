package com.salesmanager.shop.model.order;

import java.io.Serializable;
import java.util.List;

import com.salesmanager.shop.model.order.shipping.ReadableShippingSummary;
import com.salesmanager.shop.model.order.total.ReadableOrderTotal;
import com.salesmanager.shop.model.order.v0.ReadableOrder;

public class ReadableShopOrder extends ReadableOrder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ReadableShippingSummary shippingSummary;

	private String errorMessage = null;//global error message
	private List<ReadableOrderTotal> subTotals;//order calculation	
	private String grandTotal;//grand total - order calculation
	

	public String getErrorMessage() {
		System.out.println("$#9159#"); return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getGrandTotal() {
		System.out.println("$#9160#"); return grandTotal;
	}
	public void setGrandTotal(String grandTotal) {
		this.grandTotal = grandTotal;
	}
	public List<ReadableOrderTotal> getSubTotals() {
		System.out.println("$#9161#"); return subTotals;
	}
	public void setSubTotals(List<ReadableOrderTotal> subTotals) {
		this.subTotals = subTotals;
	}
	public ReadableShippingSummary getShippingSummary() {
		System.out.println("$#9162#"); return shippingSummary;
	}
	public void setShippingSummary(ReadableShippingSummary shippingSummary) {
		this.shippingSummary = shippingSummary;
	}

}
