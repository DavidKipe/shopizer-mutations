package com.salesmanager.core.business.modules.cms.impl;

/**
 * Infinispan asset manager
 * 
 * @author casams1
 *
 */
public class StoreCacheManagerImpl extends CacheManagerImpl {


  private final static String NAMED_CACHE = "StoreRepository";
  private String root;


  public StoreCacheManagerImpl(String location, String root) {
				System.out.println("$#189#"); super.init(NAMED_CACHE, location);
    this.root = root;
  }


  @Override
  public String getRootName() {
				System.out.println("$#190#"); return root;
  }


  @Override
  public String getLocation() {
				System.out.println("$#191#"); return location;
  }



}

