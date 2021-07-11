package com.salesmanager.shop.model.order.total;

import java.io.Serializable;
import java.math.BigDecimal;

import com.salesmanager.shop.model.entity.Entity;


public class OrderTotal extends Entity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String title;
    private String text;
	private String code;
	private int order;
	private String module;
	private BigDecimal value;
	
	
	public String getTitle() {
		System.out.println("$#9189#"); return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCode() {
		System.out.println("$#9190#"); return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getOrder() {
		System.out.println("$#9191#"); return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public String getModule() {
		System.out.println("$#9192#"); return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public BigDecimal getValue() {
		System.out.println("$#9193#"); return value;
	}
	public void setValue(BigDecimal value) {
		this.value = value;
	}
	public String getText() {
		System.out.println("$#9194#"); return text;
	}
	public void setText(String text) {
		this.text = text;
	}


}
