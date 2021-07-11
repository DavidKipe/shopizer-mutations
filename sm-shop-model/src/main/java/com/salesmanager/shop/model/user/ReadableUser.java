package com.salesmanager.shop.model.user;

import java.util.ArrayList;
import java.util.List;
import com.salesmanager.shop.model.security.ReadableGroup;
import com.salesmanager.shop.model.security.ReadablePermission;

public class ReadableUser extends UserEntity {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String lastAccess;
  private String loginTime;
  private String merchant;

  private List<ReadablePermission> permissions = new ArrayList<ReadablePermission>();
  private List<ReadableGroup> groups = new ArrayList<ReadableGroup>();
  
  

  public List<ReadableGroup> getGroups() {
				System.out.println("$#9430#"); return groups;
  }

  public void setGroups(List<ReadableGroup> groups) {
    this.groups = groups;
  }
  
  
  public String getLastAccess() {
				System.out.println("$#9431#"); return lastAccess;
  }
  public void setLastAccess(String lastAccess) {
    this.lastAccess = lastAccess;
  }
  public String getLoginTime() {
				System.out.println("$#9432#"); return loginTime;
  }
  public void setLoginTime(String loginTime) {
    this.loginTime = loginTime;
  }
  public String getMerchant() {
				System.out.println("$#9433#"); return merchant;
  }
  public void setMerchant(String merchant) {
    this.merchant = merchant;
  }
  public List<ReadablePermission> getPermissions() {
				System.out.println("$#9434#"); return permissions;
  }
  public void setPermissions(List<ReadablePermission> permissions) {
    this.permissions = permissions;
  }


}
