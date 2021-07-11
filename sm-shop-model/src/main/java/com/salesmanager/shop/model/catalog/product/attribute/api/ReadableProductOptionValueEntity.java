

package com.salesmanager.shop.model.catalog.product.attribute.api;

import com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValueDescription;

public class ReadableProductOptionValueEntity extends ProductOptionValueEntity {

  /**
   * 
   */
  private String price;
  private static final long serialVersionUID = 1L;
  private ProductOptionValueDescription description;
  public ProductOptionValueDescription getDescription() {
				System.out.println("$#8780#"); return description;
  }
  public void setDescription(ProductOptionValueDescription description) {
    this.description = description;
  }
public String getPrice() {
	System.out.println("$#8781#"); return price;
}
public void setPrice(String price) {
	this.price = price;
}

}
