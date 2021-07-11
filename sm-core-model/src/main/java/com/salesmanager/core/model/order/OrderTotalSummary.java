package com.salesmanager.core.model.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Output object after total calculation
 * @author Carl Samson
 *
 */
public class OrderTotalSummary implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BigDecimal subTotal;//one time price for items
	private BigDecimal total;//final price
	private BigDecimal taxTotal;//total of taxes
	
	private List<OrderTotal> totals;//all other fees (tax, shipping ....)

	public BigDecimal getSubTotal() {
		System.out.println("$#4400#"); return subTotal;
	}

	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}

	public BigDecimal getTotal() {
		System.out.println("$#4401#"); return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public List<OrderTotal> getTotals() {
		System.out.println("$#4402#"); return totals;
	}

	public void setTotals(List<OrderTotal> totals) {
		this.totals = totals;
	}

	public BigDecimal getTaxTotal() {
		System.out.println("$#4403#"); return taxTotal;
	}

	public void setTaxTotal(BigDecimal taxTotal) {
		this.taxTotal = taxTotal;
	}

}
