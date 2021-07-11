package com.salesmanager.core.model.shipping;

public class PackageDetails {
	
	private String code;
	private double shippingWeight;
	private double shippingMaxWeight;
	private double shippingLength;
	private double shippingHeight;
	private double shippingWidth;
	private int shippingQuantity;
	private int treshold;
	private String type; //BOX, ITEM
	
	
	private String itemName = "";
	
	
	public String getItemName() {
		System.out.println("$#4537#"); return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public double getShippingWeight() {
		System.out.println("$#4538#"); return shippingWeight;
	}
	public void setShippingWeight(double shippingWeight) {
		this.shippingWeight = shippingWeight;
	}
	public double getShippingMaxWeight() {
		System.out.println("$#4539#"); return shippingMaxWeight;
	}
	public void setShippingMaxWeight(double shippingMaxWeight) {
		this.shippingMaxWeight = shippingMaxWeight;
	}
	public double getShippingLength() {
		System.out.println("$#4540#"); return shippingLength;
	}
	public void setShippingLength(double shippingLength) {
		this.shippingLength = shippingLength;
	}
	public double getShippingHeight() {
		System.out.println("$#4541#"); return shippingHeight;
	}
	public void setShippingHeight(double shippingHeight) {
		this.shippingHeight = shippingHeight;
	}
	public double getShippingWidth() {
		System.out.println("$#4542#"); return shippingWidth;
	}
	public void setShippingWidth(double shippingWidth) {
		this.shippingWidth = shippingWidth;
	}
	public int getShippingQuantity() {
		System.out.println("$#4543#"); return shippingQuantity;
	}
	public void setShippingQuantity(int shippingQuantity) {
		this.shippingQuantity = shippingQuantity;
	}
	public int getTreshold() {
		System.out.println("$#4544#"); return treshold;
	}
	public void setTreshold(int treshold) {
		this.treshold = treshold;
	}
	public String getCode() {
		System.out.println("$#4545#"); return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getType() {
		System.out.println("$#4546#"); return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
