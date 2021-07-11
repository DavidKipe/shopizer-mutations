package com.salesmanager.core.model.catalog.product.price;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Transient entity used to display
 * different price information in the catalogue
 * @author Carl Samson
 *
 */
public class FinalPrice implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BigDecimal discountedPrice = null;//final price if a discount is applied
	private BigDecimal originalPrice = null;//original price
	private BigDecimal finalPrice = null;//final price discount or not
	private boolean discounted = false;
	private int discountPercent = 0;
	
	private Date discountEndDate = null;
	
	private boolean defaultPrice;
	private ProductPrice productPrice;
	List<FinalPrice> additionalPrices;

	public List<FinalPrice> getAdditionalPrices() {
		System.out.println("$#3899#"); return additionalPrices;
	}

	public void setAdditionalPrices(List<FinalPrice> additionalPrices) {
		this.additionalPrices = additionalPrices;
	}

	public BigDecimal getOriginalPrice() {
		System.out.println("$#3900#"); return originalPrice;
	}

	public void setOriginalPrice(BigDecimal originalPrice) {
		this.originalPrice = originalPrice;
	}



	public int getDiscountPercent() {
		System.out.println("$#3901#"); return discountPercent;
	}

	public void setDiscountPercent(int discountPercent) {
		this.discountPercent = discountPercent;
	}

	public Date getDiscountEndDate() {
		System.out.println("$#3902#"); return discountEndDate;
	}

	public void setDiscountEndDate(Date discountEndDate) {
		this.discountEndDate = discountEndDate;
	}

	public boolean isDiscounted() {
		System.out.println("$#3904#"); System.out.println("$#3903#"); return discounted;
	}

	public void setDiscounted(boolean discounted) {
		this.discounted = discounted;
	}

	public void setDiscountedPrice(BigDecimal discountedPrice) {
		this.discountedPrice = discountedPrice;
	}

	public BigDecimal getDiscountedPrice() {
		System.out.println("$#3905#"); return discountedPrice;
	}


	public void setFinalPrice(BigDecimal finalPrice) {
		this.finalPrice = finalPrice;
	}

	public BigDecimal getFinalPrice() {
		System.out.println("$#3906#"); return finalPrice;
	}

	public void setDefaultPrice(boolean defaultPrice) {
		this.defaultPrice = defaultPrice;
	}

	public boolean isDefaultPrice() {
		System.out.println("$#3908#"); System.out.println("$#3907#"); return defaultPrice;
	}

	public void setProductPrice(ProductPrice productPrice) {
		this.productPrice = productPrice;
	}

	public ProductPrice getProductPrice() {
		System.out.println("$#3909#"); return productPrice;
	}

}
