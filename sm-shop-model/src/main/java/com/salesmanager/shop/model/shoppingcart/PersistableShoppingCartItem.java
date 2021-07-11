package com.salesmanager.shop.model.shoppingcart;

import java.io.Serializable;
import java.util.List;

import com.salesmanager.shop.model.catalog.product.attribute.ProductAttribute;

/**
 * Compatible with v1
 * @author c.samson
 *
 */
public class PersistableShoppingCartItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long product;//product id
	private int quantity;
	private String promoCode;
	public String getPromoCode() {
		System.out.println("$#9297#"); return promoCode;
	}
	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}
	public int getQuantity() {
		System.out.println("$#9298#"); return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	private List<ProductAttribute> attributes;
	public Long getProduct() {
		System.out.println("$#9299#"); return product;
	}
	public void setProduct(Long product) {
		this.product = product;
	}
	public List<ProductAttribute> getAttributes() {
		System.out.println("$#9300#"); return attributes;
	}
	public void setAttributes(List<ProductAttribute> attributes) {
		this.attributes = attributes;
	}

}
