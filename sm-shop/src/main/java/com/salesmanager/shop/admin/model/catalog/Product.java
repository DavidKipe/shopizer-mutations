package com.salesmanager.shop.admin.model.catalog;

import com.salesmanager.core.model.catalog.product.availability.ProductAvailability;
import com.salesmanager.core.model.catalog.product.description.ProductDescription;
import com.salesmanager.core.model.catalog.product.image.ProductImage;
import com.salesmanager.core.model.catalog.product.price.ProductPrice;
import javax.validation.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Product implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */

	//provides wrapping to the main product entity
	@Valid
	private com.salesmanager.core.model.catalog.product.Product product;
	
	@Valid
	private List<ProductDescription> descriptions = new ArrayList<ProductDescription>();
	
	@Valid
	private ProductAvailability availability = null;
	
	@Valid
	private ProductPrice price = null;
	
	private MultipartFile image = null;
	
	private ProductImage productImage = null;
	
	@NotEmpty
	private String productPrice = "0";
	
	private String dateAvailable;

	private ProductDescription description = null;
	
	public String getDateAvailable() {
		System.out.println("$#7780#"); return dateAvailable;
	}
	public void setDateAvailable(String dateAvailable) {
		this.dateAvailable = dateAvailable;
	}
	public com.salesmanager.core.model.catalog.product.Product getProduct() {
		System.out.println("$#7781#"); return product;
	}
	public void setProduct(com.salesmanager.core.model.catalog.product.Product product) {
		this.product = product;
	}
	
	public List<ProductDescription> getDescriptions() {
		System.out.println("$#7782#"); return descriptions;
	}
	public void setDescriptions(List<ProductDescription> descriptions) {
		this.descriptions = descriptions;
	}
	public void setAvailability(ProductAvailability availability) {
		this.availability = availability;
	}
	public ProductAvailability getAvailability() {
		System.out.println("$#7783#"); return availability;
	}
	public void setPrice(ProductPrice price) {
		this.price = price;
	}
	public ProductPrice getPrice() {
		System.out.println("$#7784#"); return price;
	}
	public MultipartFile getImage() {
		System.out.println("$#7785#"); return image;
	}
	public void setImage(MultipartFile image) {
		this.image = image;
	}

	public void setProductPrice(String productPrice) {
		this.productPrice = productPrice;
	}
	public String getProductPrice() {
		System.out.println("$#7786#"); return productPrice;
	}
	public void setProductImage(ProductImage productImage) {
		this.productImage = productImage;
	}
	public ProductImage getProductImage() {
		System.out.println("$#7787#"); return productImage;
	}
	public void setDescription(ProductDescription description) {
		this.description = description;
	}
	public ProductDescription getDescription() {
		System.out.println("$#7788#"); return description;
	}
	





}
