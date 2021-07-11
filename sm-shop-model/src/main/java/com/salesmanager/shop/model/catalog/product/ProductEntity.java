package com.salesmanager.shop.model.catalog.product;

import java.io.Serializable;
import java.math.BigDecimal;
import com.salesmanager.core.model.catalog.product.ProductCondition;
import com.salesmanager.core.model.catalog.product.RentalStatus;

/**
 * A product entity is used by services API to populate or retrieve a Product
 * entity
 * 
 * @author Carl Samson
 *
 */
public class ProductEntity extends Product implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BigDecimal price;
	private int quantity = 0;
	private String sku;
	private boolean productShipeable = false;
	private boolean preOrder = false;
	private boolean productVirtual = false;
	private int quantityOrderMaximum = -1;// default unlimited
	private int quantityOrderMinimum = 1;// default 1
	private boolean productIsFree;
	private boolean available;
	private boolean visible = true;

	/*
	 * private BigDecimal productLength; private BigDecimal productWidth;
	 * private BigDecimal productHeight; private BigDecimal productWeight;
	 */
	private ProductSpecification productSpecifications;
	private Double rating = 0D;
	private int ratingCount;
	private int sortOrder;
	private String dateAvailable;
	private String refSku;
	private ProductCondition condition;
	private String creationDate;

	/**
	 * RENTAL additional fields
	 * 
	 * @return
	 */

	private int rentalDuration;
	private int rentalPeriod;
	private RentalStatus rentalStatus;

	/**
	 * End RENTAL fields
	 * 
	 * @return
	 */

	public BigDecimal getPrice() {
		System.out.println("$#8875#"); return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public int getQuantity() {
		System.out.println("$#8876#"); return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getSku() {
		System.out.println("$#8877#"); return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public boolean isProductShipeable() {
		System.out.println("$#8879#"); System.out.println("$#8878#"); return productShipeable;
	}

	public void setProductShipeable(boolean productShipeable) {
		this.productShipeable = productShipeable;
	}

	public boolean isProductIsFree() {
		System.out.println("$#8881#"); System.out.println("$#8880#"); return productIsFree;
	}

	public void setProductIsFree(boolean productIsFree) {
		this.productIsFree = productIsFree;
	}

	public int getSortOrder() {
		System.out.println("$#8882#"); return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public void setQuantityOrderMaximum(int quantityOrderMaximum) {
		this.quantityOrderMaximum = quantityOrderMaximum;
	}

	public int getQuantityOrderMaximum() {
		System.out.println("$#8883#"); return quantityOrderMaximum;
	}

	public void setProductVirtual(boolean productVirtual) {
		this.productVirtual = productVirtual;
	}

	public boolean isProductVirtual() {
		System.out.println("$#8885#"); System.out.println("$#8884#"); return productVirtual;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public boolean isAvailable() {
		System.out.println("$#8887#"); System.out.println("$#8886#"); return available;
	}

	public void setDateAvailable(String dateAvailable) {
		this.dateAvailable = dateAvailable;
	}

	public String getDateAvailable() {
		System.out.println("$#8888#"); return dateAvailable;
	}

	public int getQuantityOrderMinimum() {
		System.out.println("$#8889#"); return quantityOrderMinimum;
	}

	public void setQuantityOrderMinimum(int quantityOrderMinimum) {
		this.quantityOrderMinimum = quantityOrderMinimum;
	}

	public int getRatingCount() {
		System.out.println("$#8890#"); return ratingCount;
	}

	public void setRatingCount(int ratingCount) {
		this.ratingCount = ratingCount;
	}

	public Double getRating() {
		System.out.println("$#8891#"); return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public boolean isPreOrder() {
		System.out.println("$#8893#"); System.out.println("$#8892#"); return preOrder;
	}

	public void setPreOrder(boolean preOrder) {
		this.preOrder = preOrder;
	}

	public String getRefSku() {
		System.out.println("$#8894#"); return refSku;
	}

	public void setRefSku(String refSku) {
		this.refSku = refSku;
	}

	public boolean isVisible() {
		System.out.println("$#8896#"); System.out.println("$#8895#"); return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public ProductCondition getCondition() {
		System.out.println("$#8897#"); return condition;
	}

	public void setCondition(ProductCondition condition) {
		this.condition = condition;
	}

	public int getRentalDuration() {
		System.out.println("$#8898#"); return rentalDuration;
	}

	public void setRentalDuration(int rentalDuration) {
		this.rentalDuration = rentalDuration;
	}

	public RentalStatus getRentalStatus() {
		System.out.println("$#8899#"); return rentalStatus;
	}

	public void setRentalStatus(RentalStatus rentalStatus) {
		this.rentalStatus = rentalStatus;
	}

	public int getRentalPeriod() {
		System.out.println("$#8900#"); return rentalPeriod;
	}

	public void setRentalPeriod(int rentalPeriod) {
		this.rentalPeriod = rentalPeriod;
	}

	public ProductSpecification getProductSpecifications() {
		System.out.println("$#8901#"); return productSpecifications;
	}

	public void setProductSpecifications(ProductSpecification productSpecifications) {
		this.productSpecifications = productSpecifications;
	}

	public String getCreationDate() {
		System.out.println("$#8902#"); return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}


}
