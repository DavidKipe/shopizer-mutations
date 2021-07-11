package com.salesmanager.core.business.modules.cms.impl;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VendorCacheManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(VendorCacheManager.class);
  private EmbeddedCacheManager manager = null;
  private static VendorCacheManager vendorCacheManager = null;


  private VendorCacheManager() {

    try {
      manager = new DefaultCacheManager();
    } catch (Exception e) {
      LOGGER.error("Cannot start manager " + e.toString());
    }

  }


  public static VendorCacheManager getInstance() {
				System.out.println("$#192#"); if (vendorCacheManager == null) {
      vendorCacheManager = new VendorCacheManager();

    }
				System.out.println("$#193#"); return vendorCacheManager;
  }


  public EmbeddedCacheManager getManager() {
				System.out.println("$#194#"); return manager;
  }

}
