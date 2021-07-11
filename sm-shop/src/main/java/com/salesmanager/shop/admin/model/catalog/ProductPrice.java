package com.salesmanager.shop.admin.model.catalog;

import com.salesmanager.core.model.catalog.product.availability.ProductAvailability;
import com.salesmanager.core.model.catalog.product.price.ProductPriceDescription;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

public class ProductPrice {
	
	@Valid
	private com.salesmanager.core.model.catalog.product.price.ProductPrice price = null;
	@Valid
	private List <ProductPriceDescription> descriptions = new ArrayList<ProductPriceDescription>();
	private String priceText;
	private String specialPriceText;
	private ProductAvailability productAvailability;
	
	
	//cannot convert in this object to date ??? needs to use a string, parse, bla bla
	private String productPriceSpecialStartDate;
	private String productPriceSpecialEndDate;
	
	private com.salesmanager.core.model.catalog.product.Product product;
	
	
	
	
	
	public List <ProductPriceDescription> getDescriptions() {
		System.out.println("$#7789#"); return descriptions;
	}
	public void setDescriptions(List <ProductPriceDescription> descriptions) {
		this.descriptions = descriptions;
	}
	public ProductAvailability getProductAvailability() {
		System.out.println("$#7790#"); return productAvailability;
	}
	public void setProductAvailability(ProductAvailability productAvailability) {
		this.productAvailability = productAvailability;
	}
	public String getPriceText() {
		System.out.println("$#7791#"); return priceText;
	}
	public void setPriceText(String priceText) {
		this.priceText = priceText;
	}
	public com.salesmanager.core.model.catalog.product.price.ProductPrice getPrice() {
		System.out.println("$#7792#"); return price;
	}
	public void setPrice(com.salesmanager.core.model.catalog.product.price.ProductPrice price) {
		this.price = price;
	}
	public String getSpecialPriceText() {
		System.out.println("$#7793#"); return specialPriceText;
	}
	public void setSpecialPriceText(String specialPriceText) {
		this.specialPriceText = specialPriceText;
	}

	public com.salesmanager.core.model.catalog.product.Product getProduct() {
		System.out.println("$#7794#"); return product;
	}
	public void setProduct(com.salesmanager.core.model.catalog.product.Product product) {
		this.product = product;
	}
	public String getProductPriceSpecialStartDate() {
		System.out.println("$#7795#"); return productPriceSpecialStartDate;
	}
	public void setProductPriceSpecialStartDate(
			String productPriceSpecialStartDate) {
		this.productPriceSpecialStartDate = productPriceSpecialStartDate;
	}
	public String getProductPriceSpecialEndDate() {
		System.out.println("$#7796#"); return productPriceSpecialEndDate;
	}
	public void setProductPriceSpecialEndDate(String productPriceSpecialEndDate) {
		this.productPriceSpecialEndDate = productPriceSpecialEndDate;
	}

}
