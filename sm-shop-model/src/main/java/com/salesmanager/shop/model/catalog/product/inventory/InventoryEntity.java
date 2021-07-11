package com.salesmanager.shop.model.catalog.product.inventory;

import com.salesmanager.shop.model.entity.Entity;

public class InventoryEntity extends Entity {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private int quantity;
  private String region;
  private String regionVariant;
  private String owner;
  private String dateAvailable;
  private boolean available;
  private int productQuantityOrderMin = 0;
  private int productQuantityOrderMax = 0;
  public int getQuantity() {
				System.out.println("$#8836#"); return quantity;
  }
  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }
  public String getRegion() {
				System.out.println("$#8837#"); return region;
  }
  public void setRegion(String region) {
    this.region = region;
  }
  public String getRegionVariant() {
				System.out.println("$#8838#"); return regionVariant;
  }
  public void setRegionVariant(String regionVariant) {
    this.regionVariant = regionVariant;
  }
  public String getOwner() {
				System.out.println("$#8839#"); return owner;
  }
  public void setOwner(String owner) {
    this.owner = owner;
  }
  public boolean isAvailable() {
				System.out.println("$#8841#"); System.out.println("$#8840#"); return available;
  }
  public void setAvailable(boolean available) {
    this.available = available;
  }
  public int getProductQuantityOrderMin() {
				System.out.println("$#8842#"); return productQuantityOrderMin;
  }
  public void setProductQuantityOrderMin(int productQuantityOrderMin) {
    this.productQuantityOrderMin = productQuantityOrderMin;
  }
  public int getProductQuantityOrderMax() {
				System.out.println("$#8843#"); return productQuantityOrderMax;
  }
  public void setProductQuantityOrderMax(int productQuantityOrderMax) {
    this.productQuantityOrderMax = productQuantityOrderMax;
  }
  public String getDateAvailable() {
				System.out.println("$#8844#"); return dateAvailable;
  }
  public void setDateAvailable(String dateAvailable) {
    this.dateAvailable = dateAvailable;
  }
  

}
