package com.salesmanager.core.modules.integration.shipping.model;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class CustomShippingQuoteWeightItem extends CustomShippingQuoteItem implements JSONAware {
	
	private int maximumWeight;
	
	private String priceText;

	public String getPriceText() {
		System.out.println("$#4911#"); return priceText;
	}

	public void setPriceText(String priceText) {
		this.priceText = priceText;
	}

	public void setMaximumWeight(int maximumWeight) {
		this.maximumWeight = maximumWeight;
	}

	public int getMaximumWeight() {
		System.out.println("$#4912#"); return maximumWeight;
	}

	@SuppressWarnings("unchecked")
	public String toJSONString() {
		JSONObject data = new JSONObject();
		data.put("price", super.getPrice());
		data.put("maximumWeight", this.getMaximumWeight());
		
		System.out.println("$#4913#"); return data.toJSONString();
	}



}
