package com.salesmanager.core.business.modules.cms.impl;

/**
 * Http server bootstrap
 * 
 * @author carlsamson
 *
 */
public class LocalCacheManagerImpl implements CMSManager {

  private String rootName;// file location root

  public LocalCacheManagerImpl(String rootName) {
    this.rootName = rootName;
  }


  @Override
  public String getRootName() {
				System.out.println("$#181#"); return rootName;
  }

  @Override
  public String getLocation() {
    return "";
  }


}
