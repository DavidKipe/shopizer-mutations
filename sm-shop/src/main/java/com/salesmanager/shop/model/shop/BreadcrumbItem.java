package com.salesmanager.shop.model.shop;

import java.io.Serializable;

public class BreadcrumbItem implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String label;
	private String url;
	private BreadcrumbItemType itemType;
	public Long getId() {
		System.out.println("$#9283#"); return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getLabel() {
		System.out.println("$#9284#"); return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getUrl() {
		System.out.println("$#9285#"); return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public BreadcrumbItemType getItemType() {
		System.out.println("$#9286#"); return itemType;
	}
	public void setItemType(BreadcrumbItemType itemType) {
		this.itemType = itemType;
	}

}
