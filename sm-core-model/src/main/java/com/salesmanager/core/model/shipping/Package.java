package com.salesmanager.core.model.shipping;

import java.io.Serializable;

public class Package implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	public int getTreshold() {
		System.out.println("$#4527#"); return treshold;
	}
	public void setTreshold(int treshold) {
		this.treshold = treshold;
	}
	public String getCode() {
		System.out.println("$#4528#"); return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public double getBoxWidth() {
		System.out.println("$#4529#"); return boxWidth;
	}
	public void setBoxWidth(double boxWidth) {
		this.boxWidth = boxWidth;
	}
	public double getBoxHeight() {
		System.out.println("$#4530#"); return boxHeight;
	}
	public void setBoxHeight(double boxHeight) {
		this.boxHeight = boxHeight;
	}
	public double getBoxLength() {
		System.out.println("$#4531#"); return boxLength;
	}
	public void setBoxLength(double boxLength) {
		this.boxLength = boxLength;
	}
	public double getBoxWeight() {
		System.out.println("$#4532#"); return boxWeight;
	}
	public void setBoxWeight(double boxWeight) {
		this.boxWeight = boxWeight;
	}
	public double getMaxWeight() {
		System.out.println("$#4533#"); return maxWeight;
	}
	public void setMaxWeight(double maxWeight) {
		this.maxWeight = maxWeight;
	}

	public boolean isDefaultPackaging() {
		System.out.println("$#4535#"); System.out.println("$#4534#"); return defaultPackaging;
	}
	public void setDefaultPackaging(boolean defaultPackaging) {
		this.defaultPackaging = defaultPackaging;
	}
	public ShippingPackageType getShipPackageType() {
		System.out.println("$#4536#"); return shipPackageType;
	}
	public void setShipPackageType(ShippingPackageType shipPackageType) {
		this.shipPackageType = shipPackageType;
	}
	private String code;
	private double boxWidth = 0;
	private double boxHeight = 0;
	private double boxLength = 0;
	private double boxWeight = 0;
	private double maxWeight = 0;	
	//private int shippingQuantity;
	private int treshold;
	private ShippingPackageType shipPackageType;
	private boolean defaultPackaging;

}
