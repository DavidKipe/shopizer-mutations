package com.salesmanager.core.business.modules.cms.impl;

/**
 * Infinispan asset manager for download files
 * 
 * @author casams1
 *
 */
public class DownloadCacheManagerImpl extends CacheManagerImpl {


  private final static String NAMED_CACHE = "DownlaodRepository";
  private String root;


  public DownloadCacheManagerImpl(String location, String root) {
				System.out.println("$#175#"); super.init(NAMED_CACHE, location);
    this.root = root;
  }


  @Override
  public String getRootName() {
				System.out.println("$#176#"); return root;
  }


  @Override
  public String getLocation() {
				System.out.println("$#177#"); return location;
  }



}

