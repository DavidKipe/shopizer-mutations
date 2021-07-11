package com.salesmanager.shop.admin.model.web;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Menu implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	private String url;
	private String icon;
	private String role;
	private int order;
	private List<Menu> menus = new ArrayList<Menu>();
	public String getCode() {
		System.out.println("$#7855#"); return code;
	}
	@JsonProperty("code")  
	public void setCode(String code) {
		this.code = code;
	}
	public String getUrl() {
		System.out.println("$#7856#"); return url;
	}
	@JsonProperty("url")  
	public void setUrl(String url) {
		this.url = url;
	}

	 

	public int getOrder() {
		System.out.println("$#7857#"); return order;
	}
	@JsonProperty("order")  
	public void setOrder(int order) {
		this.order = order;
	}
	public List<Menu> getMenus() {
		System.out.println("$#7858#"); return menus;
	}
	@JsonProperty("menus")  
	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getIcon() {
		System.out.println("$#7859#"); return icon;
	}
	public String getRole() {
		System.out.println("$#7860#"); return role;
	}
	@JsonProperty("role") 
	public void setRole(String role) {
		this.role = role;
	}

}
