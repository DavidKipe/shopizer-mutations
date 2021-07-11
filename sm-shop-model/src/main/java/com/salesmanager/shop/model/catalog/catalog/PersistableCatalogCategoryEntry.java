package com.salesmanager.shop.model.catalog.catalog;

public class PersistableCatalogCategoryEntry extends CatalogEntryEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String productCode;
	private String categoryCode;
	public String getProductCode() {
		System.out.println("$#8724#"); return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getCategoryCode() {
		System.out.println("$#8725#"); return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

}
