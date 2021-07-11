package com.salesmanager.shop.model.store;

import com.salesmanager.core.model.system.MerchantConfigurationType;
import com.salesmanager.shop.model.entity.Entity;

public class MerchantConfigEntity extends Entity {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String key;
  private MerchantConfigurationType type;
  private String value;
  private boolean active;
  public String getKey() {
				System.out.println("$#9345#"); return key;
  }
  public void setKey(String key) {
    this.key = key;
  }
  public MerchantConfigurationType getType() {
				System.out.println("$#9346#"); return type;
  }
  public void setType(MerchantConfigurationType type) {
    this.type = type;
  }
  public String getValue() {
				System.out.println("$#9347#"); return value;
  }
  public void setValue(String value) {
    this.value = value;
  }
  public boolean isActive() {
				System.out.println("$#9349#"); System.out.println("$#9348#"); return active;
  }
  public void setActive(boolean active) {
    this.active = active;
  }

}
