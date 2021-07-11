package com.salesmanager.core.business.modules.integration.shipping.impl;

public class ShippingInputParameters {
	
	private String moduleName;
	private long weight;
	private long volume;
	private String country;
	private String province;
	private long distance;
	private long size;
	private int price;//integer should be rounded from BigBecimal
	private String priceQuote;
	
	public String getModuleName() {
		System.out.println("$#1249#"); return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public long getWeight() {
		System.out.println("$#1250#"); return weight;
	}
	public void setWeight(long weight) {
		this.weight = weight;
	}
	public long getVolume() {
		System.out.println("$#1251#"); return volume;
	}
	public void setVolume(long volume) {
		this.volume = volume;
	}
	public String getCountry() {
		System.out.println("$#1252#"); return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getProvince() {
		System.out.println("$#1253#"); return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public long getDistance() {
		System.out.println("$#1254#"); return distance;
	}
	public void setDistance(long distance) {
		this.distance = distance;
	}
	public String getPriceQuote() {
		System.out.println("$#1255#"); return priceQuote;
	}
	public void setPriceQuote(String priceQuote) {
		this.priceQuote = priceQuote;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" weight : ").append(this.getWeight());
		sb.append(" volume : ").append(this.getVolume())
		.append(" size : ").append(this.getSize())
		.append(" distance : ").append(this.getDistance())
		.append(" province : ").append(this.getProvince())
		.append(" price : ").append(this.getPrice())
		.append(" country : ").append(this.getCountry());
		System.out.println("$#1256#"); return sb.toString();
	}
	
	public long getSize() {
		System.out.println("$#1257#"); return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public int getPrice() {
		System.out.println("$#1258#"); return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}


}
