package com.salesmanager.shop.model.catalog.manufacturer;

import java.io.Serializable;

public class ReadableManufacturer extends ManufacturerEntity implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ManufacturerDescription description;
	public void setDescription(ManufacturerDescription description) {
		this.description = description;
	}
	public ManufacturerDescription getDescription() {
		System.out.println("$#8754#"); return description;
	}

}
