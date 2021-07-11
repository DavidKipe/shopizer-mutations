package com.salesmanager.shop.model.catalog.catalog;

import com.salesmanager.shop.model.entity.Entity;

public class CatalogEntryEntity extends Entity  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String catalog;
	private boolean visible;
	public String getCatalog() {
		System.out.println("$#8721#"); return catalog;
	}
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}
	public boolean isVisible() {
		System.out.println("$#8723#"); System.out.println("$#8722#"); return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

}
