package com.salesmanager.shop.model.content;

import java.util.ArrayList;
import java.util.List;

import com.salesmanager.shop.model.entity.Entity;

public class ReadableContentFull extends Entity {
	
	private String code;
	private boolean visible;
	private String contentType;
	
	private boolean isDisplayedInMenu;

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<ContentDescriptionEntity> descriptions = new ArrayList<ContentDescriptionEntity>();
	public List<ContentDescriptionEntity> getDescriptions() {
		System.out.println("$#9014#"); return descriptions;
	}
	public void setDescriptions(List<ContentDescriptionEntity> descriptions) {
		this.descriptions = descriptions;
	}
	public String getCode() {
		System.out.println("$#9015#"); return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public boolean isVisible() {
		System.out.println("$#9017#"); System.out.println("$#9016#"); return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public String getContentType() {
		System.out.println("$#9018#"); return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public boolean isDisplayedInMenu() {
		System.out.println("$#9020#"); System.out.println("$#9019#"); return isDisplayedInMenu;
	}
	public void setDisplayedInMenu(boolean isDisplayedInMenu) {
		this.isDisplayedInMenu = isDisplayedInMenu;
	}

}
