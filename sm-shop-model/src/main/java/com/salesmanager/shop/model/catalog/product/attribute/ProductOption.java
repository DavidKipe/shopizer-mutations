package com.salesmanager.shop.model.catalog.product.attribute;

import java.io.Serializable;
import com.salesmanager.shop.model.entity.Entity;


public class ProductOption extends Entity implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String code;
  private String type;
  private boolean readOnly;

  public String getCode() {
				System.out.println("$#8801#"); return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getType() {
				System.out.println("$#8802#"); return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public boolean isReadOnly() {
				System.out.println("$#8804#"); System.out.println("$#8803#"); return readOnly;
  }

  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }

}