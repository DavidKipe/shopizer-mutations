package com.salesmanager.shop.model.catalog.product.attribute.optionset;

import java.util.List;

import com.salesmanager.shop.model.catalog.product.attribute.ReadableProductOption;
import com.salesmanager.shop.model.catalog.product.attribute.ReadableProductOptionValue;
import com.salesmanager.shop.model.catalog.product.type.ReadableProductType;

public class ReadableProductOptionSet extends ProductOptionSetEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ReadableProductOption option;
	private List<ReadableProductOptionValue> values;
	private List<ReadableProductType> productTypes;
	
	public ReadableProductOption getOption() {
		System.out.println("$#8791#"); return option;
	}
	public void setOption(ReadableProductOption option) {
		this.option = option;
	}
	public List<ReadableProductOptionValue> getValues() {
		System.out.println("$#8792#"); return values;
	}
	public void setValues(List<ReadableProductOptionValue> values) {
		this.values = values;
	}
	public List<ReadableProductType> getProductTypes() {
		System.out.println("$#8793#"); return productTypes;
	}
	public void setProductTypes(List<ReadableProductType> productTypes) {
		this.productTypes = productTypes;
	}

}
