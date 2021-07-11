package com.salesmanager.shop.model.catalog.product.attribute;

import java.io.Serializable;

public class ReadableProductVariantValue implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private Long option;//option id
	private Long value;//option value id
	


	public Long getValue() {
		System.out.println("$#8828#"); return value;
	}



	public void setValue(Long value) {
		this.value = value;
	}



  public Long getOption() {
				System.out.println("$#8829#"); return option;
  }



  public void setOption(Long option) {
    this.option = option;
  }



  public String getName() {
				System.out.println("$#8830#"); return name;
  }



  public void setName(String name) {
    this.name = name;
  }



public String getDescription() {
	System.out.println("$#8831#"); return description;
}



public void setDescription(String description) {
	this.description = description;
}



}
