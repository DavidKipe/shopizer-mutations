package com.salesmanager.shop.model.catalog.product.attribute.api;

import java.util.ArrayList;
import java.util.List;

import com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValueDescription;

public class ReadableProductOptionValueFull extends ReadableProductOptionValueEntity {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private List<ProductOptionValueDescription> descriptions = new ArrayList<ProductOptionValueDescription>();
  public List<ProductOptionValueDescription> getDescriptions() {
				System.out.println("$#8782#"); return descriptions;
  }
  public void setDescriptions(List<ProductOptionValueDescription> descriptions) {
    this.descriptions = descriptions;
  }

}
