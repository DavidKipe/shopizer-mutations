package com.salesmanager.shop.model.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.salesmanager.shop.model.entity.Entity;

public class PersistableContent extends Entity implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String code;
  private boolean isDisplayedInMenu;

  public String getCode() {
				System.out.println("$#9005#"); return code;
  }

  public void setCode(String code) {
    this.code = code;
  }
  
  public List<ObjectContent> getDescriptions() {
				System.out.println("$#9006#"); return descriptions;
  }

  public void setDescriptions(List<ObjectContent> descriptions) {
    this.descriptions = descriptions;
  }

  public boolean isDisplayedInMenu() {
				System.out.println("$#9008#"); System.out.println("$#9007#"); return isDisplayedInMenu;
  }

  public void setDisplayedInMenu(boolean isDisplayedInMenu) {
    this.isDisplayedInMenu = isDisplayedInMenu;
  }

  private List<ObjectContent> descriptions = new ArrayList<ObjectContent>();

}
