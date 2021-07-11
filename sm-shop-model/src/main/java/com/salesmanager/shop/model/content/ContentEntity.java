package com.salesmanager.shop.model.content;

import com.salesmanager.core.model.content.ContentType;
import com.salesmanager.shop.model.entity.Entity;

public class ContentEntity extends Entity {
	
	  private static final long serialVersionUID = 1L;
	  private String code;
	  private String contentType = ContentType.BOX.name();
	  private boolean isDisplayedInMenu;
	  private boolean visible;
	public String getCode() {
		System.out.println("$#8988#"); return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getContentType() {
		System.out.println("$#8989#"); return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public boolean isDisplayedInMenu() {
		System.out.println("$#8991#"); System.out.println("$#8990#"); return isDisplayedInMenu;
	}
	public void setDisplayedInMenu(boolean isDisplayedInMenu) {
		this.isDisplayedInMenu = isDisplayedInMenu;
	}
	public boolean isVisible() {
		System.out.println("$#8993#"); System.out.println("$#8992#"); return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public static long getSerialversionuid() {
		System.out.println("$#8994#"); return serialVersionUID;
	}

}
