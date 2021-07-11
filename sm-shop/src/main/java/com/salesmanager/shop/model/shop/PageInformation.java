package com.salesmanager.shop.model.shop;

import java.io.Serializable;

public class PageInformation implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String pageTitle;
	private String pageDescription;
	private String pageKeywords;
	private String pageUrl;
	public String getPageTitle() {
		System.out.println("$#9291#"); return pageTitle;
	}
	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}
	public String getPageDescription() {
		System.out.println("$#9292#"); return pageDescription;
	}
	public void setPageDescription(String pageDescription) {
		this.pageDescription = pageDescription;
	}
	public String getPageKeywords() {
		System.out.println("$#9293#"); return pageKeywords;
	}
	public void setPageKeywords(String pageKeywords) {
		this.pageKeywords = pageKeywords;
	}
	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}
	public String getPageUrl() {
		System.out.println("$#9294#"); return pageUrl;
	}

}
