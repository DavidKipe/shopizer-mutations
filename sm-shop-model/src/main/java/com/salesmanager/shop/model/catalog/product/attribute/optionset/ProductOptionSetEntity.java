package com.salesmanager.shop.model.catalog.product.attribute.optionset;

import java.io.Serializable;

public class ProductOptionSetEntity implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String code;
	private boolean readOnly;
	public Long getId() {
		System.out.println("$#8787#"); return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCode() {
		System.out.println("$#8788#"); return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public boolean isReadOnly() {
		System.out.println("$#8790#"); System.out.println("$#8789#"); return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

}
