package com.salesmanager.shop.model.user;

import java.util.ArrayList;
import java.util.List;
import com.salesmanager.shop.model.security.PersistableGroup;

public class PersistableUser extends UserEntity {

  private String password;
  private String store;

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private List<PersistableGroup> groups = new ArrayList<PersistableGroup>();

  public String getPassword() {
				System.out.println("$#9427#"); return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public List<PersistableGroup> getGroups() {
				System.out.println("$#9428#"); return groups;
  }

  public void setGroups(List<PersistableGroup> groups) {
    this.groups = groups;
  }

public String getStore() {
	System.out.println("$#9429#"); return store;
}

public void setStore(String store) {
	this.store = store;
}

}
