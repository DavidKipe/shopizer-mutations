package com.salesmanager.shop.model.catalog.catalog;

import java.util.ArrayList;
import java.util.List;

import com.salesmanager.shop.model.catalog.category.ReadableCategory;
import com.salesmanager.shop.model.store.ReadableMerchantStore;

public class ReadableCatalog extends ReadableCatalogName {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ReadableMerchantStore store;

	private List<ReadableCategory> category = new ArrayList<ReadableCategory>();

	
/*	public List<ReadableCatalogCategoryEntry> getEntry() {
		return entry;
	}
	public void setEntry(List<ReadableCatalogCategoryEntry> entry) {
		this.entry = entry;
	}*/

	public ReadableMerchantStore getStore() {
		System.out.println("$#8726#"); return store;
	}
	public void setStore(ReadableMerchantStore store) {
		this.store = store;
	}
	public List<ReadableCategory> getCategory() {
		System.out.println("$#8727#"); return category;
	}
	public void setCategory(List<ReadableCategory> category) {
		this.category = category;
	}



}
