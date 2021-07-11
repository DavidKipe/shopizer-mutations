package com.salesmanager.shop.model.catalog;

import java.io.Serializable;

import com.salesmanager.shop.model.entity.ShopEntity;


public abstract class NamedEntity extends ShopEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private String friendlyUrl;
	private String keyWords;
	private String highlights;
	private String metaDescription;
	private String title;
	public String getName() {
		System.out.println("$#8757#"); return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		System.out.println("$#8758#"); return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFriendlyUrl() {
		System.out.println("$#8759#"); return friendlyUrl;
	}
	public void setFriendlyUrl(String friendlyUrl) {
		this.friendlyUrl = friendlyUrl;
	}
	public String getKeyWords() {
		System.out.println("$#8760#"); return keyWords;
	}
	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}
	public String getHighlights() {
		System.out.println("$#8761#"); return highlights;
	}
	public void setHighlights(String highlights) {
		this.highlights = highlights;
	}
	public String getMetaDescription() {
		System.out.println("$#8762#"); return metaDescription;
	}
	public void setMetaDescription(String metaDescription) {
		this.metaDescription = metaDescription;
	}
	public String getTitle() {
		System.out.println("$#8763#"); return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}


}
