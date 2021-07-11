package com.salesmanager.shop.model.catalog.category;

import java.util.ArrayList;
import java.util.List;

public class ReadableCategory extends CategoryEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CategoryDescription description;//one category based on language
	private int productCount;
	private String store;
	private List<ReadableCategory> children = new ArrayList<ReadableCategory>();
	
	
	public void setDescription(CategoryDescription description) {
		this.description = description;
	}
	public CategoryDescription getDescription() {
		System.out.println("$#8744#"); return description;
	}

	public int getProductCount() {
		System.out.println("$#8745#"); return productCount;
	}
	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}
	public List<ReadableCategory> getChildren() {
		System.out.println("$#8746#"); return children;
	}
	public void setChildren(List<ReadableCategory> children) {
		this.children = children;
	}
	public String getStore() {
		System.out.println("$#8747#"); return store;
	}
	public void setStore(String store) {
		this.store = store;
	}

}
