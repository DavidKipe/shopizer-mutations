package com.salesmanager.shop.model.catalog.product;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
/**
 * A product entity is used by services API
 * to populate or retrieve a Product price entity
 * @author Carl Samson
 *
 */
public class ProductPriceEntity extends ProductPrice implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	private boolean discounted = false;
	private String discountStartDate;
	private String discountEndDate;
	private boolean defaultPrice = false;
	private BigDecimal originalPrice;
	private BigDecimal discountedPrice;
	
	public boolean isDiscounted() {
		System.out.println("$#8905#"); System.out.println("$#8904#"); return discounted;
	}
	public void setDiscounted(boolean discounted) {
		this.discounted = discounted;
	}
	public String getDiscountStartDate() {
		System.out.println("$#8906#"); return discountStartDate;
	}
	public void setDiscountStartDate(String discountStartDate) {
		this.discountStartDate = discountStartDate;
	}
	public String getDiscountEndDate() {
		System.out.println("$#8907#"); return discountEndDate;
	}
	public void setDiscountEndDate(String discountEndDate) {
		this.discountEndDate = discountEndDate;
	}
	public boolean isDefaultPrice() {
		System.out.println("$#8909#"); System.out.println("$#8908#"); return defaultPrice;
	}
	public void setDefaultPrice(boolean defaultPrice) {
		this.defaultPrice = defaultPrice;
	}
	public BigDecimal getOriginalPrice() {
		System.out.println("$#8910#"); return originalPrice;
	}
	public void setOriginalPrice(BigDecimal originalPrice) {
		this.originalPrice = originalPrice;
	}
	public BigDecimal getDiscountedPrice() {
		System.out.println("$#8911#"); return discountedPrice;
	}
	public void setDiscountedPrice(BigDecimal discountedPrice) {
		this.discountedPrice = discountedPrice;
	}
	public String getCode() {
		System.out.println("$#8912#"); if(StringUtils.isBlank(this.code)) {
			code = DEFAULT_PRICE_CODE;
		}
		System.out.println("$#8913#"); return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	
	
	


}
