package com.salesmanager.shop.model.entity;

import java.io.Serializable;
import javax.validation.constraints.NotNull;

public class UniqueEntity implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @NotNull
  private String unique;
  @NotNull
  private String merchant;

  public String getUnique() {
				System.out.println("$#9110#"); return unique;
  }

  public void setUnique(String unique) {
    this.unique = unique;
  }

  public String getMerchant() {
				System.out.println("$#9111#"); return merchant;
  }

  public void setMerchant(String merchant) {
    this.merchant = merchant;
  }

}
