package com.salesmanager.shop.model.catalog.product;

import java.io.Serializable;
import java.util.List;

import com.salesmanager.shop.model.catalog.category.ReadableCategory;
import com.salesmanager.shop.model.catalog.manufacturer.ReadableManufacturer;
import com.salesmanager.shop.model.catalog.product.attribute.ReadableProductAttribute;
import com.salesmanager.shop.model.catalog.product.attribute.ReadableProductOption;
import com.salesmanager.shop.model.catalog.product.type.ReadableProductType;

public class ReadableProduct extends ProductEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ProductDescription description;
	private ReadableProductPrice productPrice;
	private String finalPrice = "0";
	private String originalPrice = null;
	private boolean discounted = false;
	private ReadableImage image;
	private List<ReadableImage> images;
	private ReadableManufacturer manufacturer;
	private List<ReadableProductAttribute> attributes;
	private List<ReadableProductOption> options;
	private List<ReadableCategory> categories;
	private ReadableProductType type;
	private boolean canBePurchased = false;

	// RENTAL
	private RentalOwner owner;

	public ProductDescription getDescription() {
		System.out.println("$#8934#"); return description;
	}

	public void setDescription(ProductDescription description) {
		this.description = description;
	}

	public String getFinalPrice() {
		System.out.println("$#8935#"); return finalPrice;
	}

	public void setFinalPrice(String finalPrice) {
		this.finalPrice = finalPrice;
	}

	public String getOriginalPrice() {
		System.out.println("$#8936#"); return originalPrice;
	}

	public void setOriginalPrice(String originalPrice) {
		this.originalPrice = originalPrice;
	}

	public boolean isDiscounted() {
		System.out.println("$#8938#"); System.out.println("$#8937#"); return discounted;
	}

	public void setDiscounted(boolean discounted) {
		this.discounted = discounted;
	}

	public void setImages(List<ReadableImage> images) {
		this.images = images;
	}

	public List<ReadableImage> getImages() {
		System.out.println("$#8939#"); return images;
	}

	public void setImage(ReadableImage image) {
		this.image = image;
	}

	public ReadableImage getImage() {
		System.out.println("$#8940#"); return image;
	}

	public void setAttributes(List<ReadableProductAttribute> attributes) {
		this.attributes = attributes;
	}

	public List<ReadableProductAttribute> getAttributes() {
		System.out.println("$#8941#"); return attributes;
	}

	public void setManufacturer(ReadableManufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}

	public ReadableManufacturer getManufacturer() {
		System.out.println("$#8942#"); return manufacturer;
	}

	public boolean isCanBePurchased() {
		System.out.println("$#8944#"); System.out.println("$#8943#"); return canBePurchased;
	}

	public void setCanBePurchased(boolean canBePurchased) {
		this.canBePurchased = canBePurchased;
	}

	public RentalOwner getOwner() {
		System.out.println("$#8945#"); return owner;
	}

	public void setOwner(RentalOwner owner) {
		this.owner = owner;
	}

	public List<ReadableCategory> getCategories() {
		System.out.println("$#8946#"); return categories;
	}

	public void setCategories(List<ReadableCategory> categories) {
		this.categories = categories;
	}

	public List<ReadableProductOption> getOptions() {
		System.out.println("$#8947#"); return options;
	}

	public void setOptions(List<ReadableProductOption> options) {
		this.options = options;
	}

	public ReadableProductType getType() {
		System.out.println("$#8948#"); return type;
	}

	public void setType(ReadableProductType type) {
		this.type = type;
	}

	public ReadableProductPrice getProductPrice() {
		System.out.println("$#8949#"); return productPrice;
	}

	public void setProductPrice(ReadableProductPrice productPrice) {
		this.productPrice = productPrice;
	}

}
