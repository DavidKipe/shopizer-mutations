package com.salesmanager.core.business.modules.integration.shipping.impl;

public class DecisionResponse {
	
	private String moduleName;
	private String customPrice;

	public String getModuleName() {
		System.out.println("$#1052#"); return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getCustomPrice() {
		System.out.println("$#1053#"); return customPrice;
	}

	public void setCustomPrice(String customPrice) {
		this.customPrice = customPrice;
	}

}
