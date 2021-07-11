package com.salesmanager.shop.model.tax;

public class ReadableTaxRate extends TaxRateEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String rate;
	private String store;
	private String zone;
	private String country;
	private ReadableTaxRateDescription description;
	private ReadableTaxClass taxClass;
	
	public ReadableTaxClass getTaxClass() {
		System.out.println("$#9415#"); return taxClass;
	}
	public void setTaxClass(ReadableTaxClass taxClass) {
		this.taxClass = taxClass;
	}
	public ReadableTaxRateDescription getDescription() {
		System.out.println("$#9416#"); return description;
	}
	public void setDescription(ReadableTaxRateDescription description) {
		this.description = description;
	}

	public String getRate() {
		System.out.println("$#9417#"); return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	public String getStore() {
		System.out.println("$#9418#"); return store;
	}
	public void setStore(String store) {
		this.store = store;
	}
	public String getZone() {
		System.out.println("$#9419#"); return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	public String getCountry() {
		System.out.println("$#9420#"); return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}

}
