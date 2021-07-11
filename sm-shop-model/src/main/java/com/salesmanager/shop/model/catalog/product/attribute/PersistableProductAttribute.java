package com.salesmanager.shop.model.catalog.product.attribute;

import java.io.Serializable;
import java.math.BigDecimal;

import com.salesmanager.shop.model.catalog.product.attribute.api.ProductAttributeEntity;

public class PersistableProductAttribute extends ProductAttributeEntity
		implements Serializable {
	
	private BigDecimal productAttributeWeight;
	private BigDecimal productAttributePrice;
	private Long productId;
	
	private ProductOption option;
	private ProductOptionValue optionValue;
	public void setOptionValue(ProductOptionValue optionValue) {
		this.optionValue = optionValue;
	}
	public ProductOptionValue getOptionValue() {
		System.out.println("$#8794#"); return optionValue;
	}
	public void setOption(ProductOption option) {
		this.option = option;
	}
	public ProductOption getOption() {
		System.out.println("$#8795#"); return option;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public BigDecimal getProductAttributeWeight() {
		System.out.println("$#8796#"); return productAttributeWeight;
	}
	public void setProductAttributeWeight(BigDecimal productAttributeWeight) {
		this.productAttributeWeight = productAttributeWeight;
	}
	public BigDecimal getProductAttributePrice() {
		System.out.println("$#8797#"); return productAttributePrice;
	}
	public void setProductAttributePrice(BigDecimal productAttributePrice) {
		this.productAttributePrice = productAttributePrice;
	}
	public Long getProductId() {
		System.out.println("$#8798#"); return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}

}
