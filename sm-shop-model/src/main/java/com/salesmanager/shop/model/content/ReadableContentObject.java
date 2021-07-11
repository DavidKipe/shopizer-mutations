package com.salesmanager.shop.model.content;

public class ReadableContentObject extends ObjectContent {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private boolean isDisplayedInMenu;
  private String code;
  private Long id;
  public boolean isDisplayedInMenu() {
				System.out.println("$#9022#"); System.out.println("$#9021#"); return isDisplayedInMenu;
  }
  public void setDisplayedInMenu(boolean isDisplayedInMenu) {
    this.isDisplayedInMenu = isDisplayedInMenu;
  }
  public String getCode() {
				System.out.println("$#9023#"); return code;
  }
  public void setCode(String code) {
    this.code = code;
  }
  public Long getId() {
				System.out.println("$#9024#"); return id;
  }
  public void setId(Long id) {
    this.id = id;
  }

}
