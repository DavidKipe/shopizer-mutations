package com.salesmanager.shop.admin.model.merchant;

import com.salesmanager.core.model.reference.language.Language;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

public class StoreLandingDescription implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@NotEmpty
	private String title;
	private String description;
	private String keywords;
	private String homePageContent;
	
	
	private Language language;

	
	
	
	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		System.out.println("$#7810#"); return description;
	}

	public void setHomePageContent(String homePageContent) {
		this.homePageContent = homePageContent;
	}

	public String getHomePageContent() {
		System.out.println("$#7811#"); return homePageContent;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getKeywords() {
		System.out.println("$#7812#"); return keywords;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		System.out.println("$#7813#"); return title;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public Language getLanguage() {
		System.out.println("$#7814#"); return language;
	}

}
