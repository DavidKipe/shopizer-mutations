package com.salesmanager.shop.model.store;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.salesmanager.core.constants.MeasureUnit;

public class MerchantStoreEntity implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	@NotNull
	private String code;
	@NotNull
	private String name;

	private String defaultLanguage;//code
	private String currency;//code
	private String inBusinessSince;
	@NotNull
	private String email;
	private String phone;
	private String template;
	
	private boolean useCache;
	private boolean currencyFormatNational;
	private boolean retailer;
	private MeasureUnit dimension;
	private MeasureUnit weight;
	

	public int getId() {
		System.out.println("$#9351#"); return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		System.out.println("$#9352#"); return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDefaultLanguage() {
		System.out.println("$#9353#"); return defaultLanguage;
	}

	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

	public String getName() {
		System.out.println("$#9354#"); return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCurrency() {
		System.out.println("$#9355#"); return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getInBusinessSince() {
		System.out.println("$#9356#"); return inBusinessSince;
	}

	public void setInBusinessSince(String inBusinessSince) {
		this.inBusinessSince = inBusinessSince;
	}

	public String getEmail() {
		System.out.println("$#9357#"); return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTemplate() {
		System.out.println("$#9358#"); return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public boolean isCurrencyFormatNational() {
		System.out.println("$#9360#"); System.out.println("$#9359#"); return currencyFormatNational;
	}

	public void setCurrencyFormatNational(boolean currencyFormatNational) {
		this.currencyFormatNational = currencyFormatNational;
	}

	public String getPhone() {
		System.out.println("$#9361#"); return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public boolean isUseCache() {
		System.out.println("$#9363#"); System.out.println("$#9362#"); return useCache;
	}

	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

	public MeasureUnit getDimension() {
		System.out.println("$#9364#"); return dimension;
	}

	public void setDimension(MeasureUnit dimension) {
		this.dimension = dimension;
	}

	public MeasureUnit getWeight() {
		System.out.println("$#9365#"); return weight;
	}

	public void setWeight(MeasureUnit weight) {
		this.weight = weight;
	}

	public boolean isRetailer() {
		System.out.println("$#9367#"); System.out.println("$#9366#"); return retailer;
	}

	public void setRetailer(boolean retailer) {
		this.retailer = retailer;
	}


}
