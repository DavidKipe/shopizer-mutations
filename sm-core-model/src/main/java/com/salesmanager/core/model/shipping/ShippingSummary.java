package com.salesmanager.core.model.shipping;

import java.io.Serializable;
import java.math.BigDecimal;

import com.salesmanager.core.model.common.Delivery;

/**
 * Contains shipping fees according to user selections
 * @author casams1
 *
 */
public class ShippingSummary implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BigDecimal shipping;
	private BigDecimal handling;
	private String shippingModule;
	private String shippingOption;
	private String shippingOptionCode;
	private boolean freeShipping;
	private boolean taxOnShipping;
	private boolean shippingQuote;
	
	private Delivery deliveryAddress;
	
	
	public BigDecimal getShipping() {
		System.out.println("$#4656#"); return shipping;
	}
	public void setShipping(BigDecimal shipping) {
		this.shipping = shipping;
	}
	public BigDecimal getHandling() {
		System.out.println("$#4657#"); return handling;
	}
	public void setHandling(BigDecimal handling) {
		this.handling = handling;
	}
	public String getShippingModule() {
		System.out.println("$#4658#"); return shippingModule;
	}
	public void setShippingModule(String shippingModule) {
		this.shippingModule = shippingModule;
	}
	public String getShippingOption() {
		System.out.println("$#4659#"); return shippingOption;
	}
	public void setShippingOption(String shippingOption) {
		this.shippingOption = shippingOption;
	}
	public boolean isFreeShipping() {
		System.out.println("$#4661#"); System.out.println("$#4660#"); return freeShipping;
	}
	public void setFreeShipping(boolean freeShipping) {
		this.freeShipping = freeShipping;
	}
	public boolean isTaxOnShipping() {
		System.out.println("$#4663#"); System.out.println("$#4662#"); return taxOnShipping;
	}
	public void setTaxOnShipping(boolean taxOnShipping) {
		this.taxOnShipping = taxOnShipping;
	}
	public Delivery getDeliveryAddress() {
		System.out.println("$#4664#"); return deliveryAddress;
	}
	public void setDeliveryAddress(Delivery deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}
	public String getShippingOptionCode() {
		System.out.println("$#4665#"); return shippingOptionCode;
	}
	public void setShippingOptionCode(String shippingOptionCode) {
		this.shippingOptionCode = shippingOptionCode;
	}
	public boolean isShippingQuote() {
		System.out.println("$#4667#"); System.out.println("$#4666#"); return shippingQuote;
	}
	public void setShippingQuote(boolean shippingQuote) {
		this.shippingQuote = shippingQuote;
	}

}
