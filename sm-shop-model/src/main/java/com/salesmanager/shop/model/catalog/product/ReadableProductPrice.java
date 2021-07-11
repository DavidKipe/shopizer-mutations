package com.salesmanager.shop.model.catalog.product;

import java.io.Serializable;
import com.salesmanager.shop.model.entity.Entity;

public class ReadableProductPrice extends Entity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String originalPrice;
	private String finalPrice;
	private boolean discounted = false;
	private ProductPriceDescription description;

	public String getOriginalPrice() {
		System.out.println("$#8953#"); return originalPrice;
	}
	public void setOriginalPrice(String originalPrice) {
		this.originalPrice = originalPrice;
	}
	public String getFinalPrice() {
		System.out.println("$#8954#"); return finalPrice;
	}
	public void setFinalPrice(String finalPrice) {
		this.finalPrice = finalPrice;
	}
	public boolean isDiscounted() {
		System.out.println("$#8956#"); System.out.println("$#8955#"); return discounted;
	}
	public void setDiscounted(boolean discounted) {
		this.discounted = discounted;
	}
  public ProductPriceDescription getDescription() {
				System.out.println("$#8957#"); return description;
  }
  public void setDescription(ProductPriceDescription description) {
    this.description = description;
  }

}
