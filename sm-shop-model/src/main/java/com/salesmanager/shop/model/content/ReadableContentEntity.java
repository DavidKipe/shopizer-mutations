package com.salesmanager.shop.model.content;

public class ReadableContentEntity extends ContentEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ContentDescriptionEntity description = null;
	public ContentDescriptionEntity getDescription() {
		System.out.println("$#9013#"); return description;
	}
	public void setDescription(ContentDescriptionEntity description) {
		this.description = description;
	}

}
