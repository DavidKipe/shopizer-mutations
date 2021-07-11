package com.salesmanager.shop.model.catalog.catalog;

import java.io.Serializable;

import com.salesmanager.shop.model.entity.Entity;

public class CatalogEntity extends Entity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean visible;
	private boolean defaultCatalog;
	private String code;
	public boolean isVisible() {
		System.out.println("$#8717#"); System.out.println("$#8716#"); return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public boolean isDefaultCatalog() {
		System.out.println("$#8719#"); System.out.println("$#8718#"); return defaultCatalog;
	}
	public void setDefaultCatalog(boolean defaultCatalog) {
		this.defaultCatalog = defaultCatalog;
	}
	public String getCode() {
		System.out.println("$#8720#"); return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

}
