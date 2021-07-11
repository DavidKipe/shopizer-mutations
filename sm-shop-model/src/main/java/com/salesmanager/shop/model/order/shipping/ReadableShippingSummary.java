package com.salesmanager.shop.model.order.shipping;

import com.salesmanager.core.model.shipping.ShippingOption;
import com.salesmanager.shop.model.customer.ReadableDelivery;
import com.salesmanager.shop.model.customer.address.Address;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadableShippingSummary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BigDecimal shipping;
	private BigDecimal handling;
	private String shippingModule;
	private String shippingOption;
	private boolean freeShipping;
	private boolean taxOnShipping;
	private boolean shippingQuote;
	private String shippingText;
	private String handlingText;
	private ReadableDelivery delivery;
	
	
	private ShippingOption selectedShippingOption = null;//Default selected option
	private List<ShippingOption> shippingOptions = null;
	
	/** additional information that comes from the quote **/
	private Map<String,String> quoteInformations = new HashMap<String,String>();
	
	
	public BigDecimal getShipping() {
		System.out.println("$#9163#"); return shipping;
	}
	public void setShipping(BigDecimal shipping) {
		this.shipping = shipping;
	}
	public BigDecimal getHandling() {
		System.out.println("$#9164#"); return handling;
	}
	public void setHandling(BigDecimal handling) {
		this.handling = handling;
	}
	public String getShippingModule() {
		System.out.println("$#9165#"); return shippingModule;
	}
	public void setShippingModule(String shippingModule) {
		this.shippingModule = shippingModule;
	}
	public String getShippingOption() {
		System.out.println("$#9166#"); return shippingOption;
	}
	public void setShippingOption(String shippingOption) {
		this.shippingOption = shippingOption;
	}
	public boolean isFreeShipping() {
		System.out.println("$#9168#"); System.out.println("$#9167#"); return freeShipping;
	}
	public void setFreeShipping(boolean freeShipping) {
		this.freeShipping = freeShipping;
	}
	public boolean isTaxOnShipping() {
		System.out.println("$#9170#"); System.out.println("$#9169#"); return taxOnShipping;
	}
	public void setTaxOnShipping(boolean taxOnShipping) {
		this.taxOnShipping = taxOnShipping;
	}
	public String getShippingText() {
		System.out.println("$#9171#"); return shippingText;
	}
	public void setShippingText(String shippingText) {
		this.shippingText = shippingText;
	}
	public String getHandlingText() {
		System.out.println("$#9172#"); return handlingText;
	}
	public void setHandlingText(String handlingText) {
		this.handlingText = handlingText;
	}
	public static long getSerialversionuid() {
		System.out.println("$#9173#"); return serialVersionUID;
	}
	public List<ShippingOption> getShippingOptions() {
		System.out.println("$#9174#"); return shippingOptions;
	}
	public void setShippingOptions(List<ShippingOption> shippingOptions) {
		this.shippingOptions = shippingOptions;
	}
	public ShippingOption getSelectedShippingOption() {
		System.out.println("$#9175#"); return selectedShippingOption;
	}
	public void setSelectedShippingOption(ShippingOption selectedShippingOption) {
		this.selectedShippingOption = selectedShippingOption;
	}
	public Map<String,String> getQuoteInformations() {
		System.out.println("$#9176#"); return quoteInformations;
	}
	public void setQuoteInformations(Map<String,String> quoteInformations) {
		this.quoteInformations = quoteInformations;
	}
	public Address getDelivery() {
		System.out.println("$#9177#"); return delivery;
	}
	public void setDelivery(ReadableDelivery delivery) {
		this.delivery = delivery;
	}
	public boolean isShippingQuote() {
		System.out.println("$#9179#"); System.out.println("$#9178#"); return shippingQuote;
	}
	public void setShippingQuote(boolean shippingQuote) {
		this.shippingQuote = shippingQuote;
	}

}
