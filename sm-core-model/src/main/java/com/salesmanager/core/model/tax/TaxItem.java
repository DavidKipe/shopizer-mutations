package com.salesmanager.core.model.tax;

import com.salesmanager.core.model.order.OrderTotalItem;
import com.salesmanager.core.model.tax.taxrate.TaxRate;

public class TaxItem extends OrderTotalItem {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String label;
	private TaxRate taxRate=null;

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		System.out.println("$#4825#"); return label;
	}

	public void setTaxRate(TaxRate taxRate) {
		this.taxRate = taxRate;
	}

	public TaxRate getTaxRate() {
		System.out.println("$#4826#"); return taxRate;
	}


}
