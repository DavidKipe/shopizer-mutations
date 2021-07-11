package com.salesmanager.shop.model.shoppingcart;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.salesmanager.shop.model.entity.ShopEntity;


public class ShoppingCartItem extends ShopEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String price;
	private String image;
	private BigDecimal productPrice;
	private int quantity;
	private long productId;
	private String productCode;
	private String code;//shopping cart code
	private boolean productVirtual;
	
	private String subTotal;
	
	private List<ShoppingCartAttribute> shoppingCartAttributes;
	
	public String getName() {
		System.out.println("$#9331#"); return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPrice() {
		System.out.println("$#9332#"); return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public int getQuantity() {
		System.out.println("$#9334#"); System.out.println("$#9333#"); if(quantity <= 0) {
			quantity = 1;
		}
		System.out.println("$#9335#"); return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}


	public String getCode() {
		System.out.println("$#9336#"); return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public List<ShoppingCartAttribute> getShoppingCartAttributes() {
		System.out.println("$#9337#"); return shoppingCartAttributes;
	}
	public void setShoppingCartAttributes(List<ShoppingCartAttribute> shoppingCartAttributes) {
		this.shoppingCartAttributes = shoppingCartAttributes;
	}
	public void setProductPrice(BigDecimal productPrice) {
		this.productPrice = productPrice;
	}
	public BigDecimal getProductPrice() {
		System.out.println("$#9338#"); return productPrice;
	}
	public void setProductId(long productId) {
		this.productId = productId;
	}
	public long getProductId() {
		System.out.println("$#9339#"); return productId;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getProductCode() {
		System.out.println("$#9340#"); return productCode;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getImage() {
		System.out.println("$#9341#"); return image;
	}
	public void setSubTotal(String subTotal) {
		this.subTotal = subTotal;
	}
	public String getSubTotal() {
		System.out.println("$#9342#"); return subTotal;
	}
	public boolean isProductVirtual() {
		System.out.println("$#9344#"); System.out.println("$#9343#"); return productVirtual;
	}
	public void setProductVirtual(boolean productVirtual) {
		this.productVirtual = productVirtual;
	}


}
