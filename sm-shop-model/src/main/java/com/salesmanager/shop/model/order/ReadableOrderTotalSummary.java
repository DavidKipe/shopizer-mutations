package com.salesmanager.shop.model.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.salesmanager.shop.model.order.total.ReadableOrderTotal;

public class ReadableOrderTotalSummary implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String subTotal;//one time price for items
	private String total;//final price
	private String taxTotal;//total of taxes
	
	private List<ReadableOrderTotal> totals = new ArrayList<ReadableOrderTotal>();//all other fees (tax, shipping ....)

	public String getSubTotal() {
		System.out.println("$#9155#"); return subTotal;
	}

	public void setSubTotal(String subTotal) {
		this.subTotal = subTotal;
	}

	public String getTotal() {
		System.out.println("$#9156#"); return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getTaxTotal() {
		System.out.println("$#9157#"); return taxTotal;
	}

	public void setTaxTotal(String taxTotal) {
		this.taxTotal = taxTotal;
	}

	public List<ReadableOrderTotal> getTotals() {
		System.out.println("$#9158#"); return totals;
	}

	public void setTotals(List<ReadableOrderTotal> totals) {
		this.totals = totals;
	}

}
