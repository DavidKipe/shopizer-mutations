package com.salesmanager.shop.utils;

import com.salesmanager.core.business.utils.CacheUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class WebApplicationCacheUtils {
	
	@Inject
	private CacheUtils cache;
	
	public Object getFromCache(String key) throws Exception {
		System.out.println("$#15817#"); return cache.getFromCache(key);
	}
	
	public void putInCache(String key, Object object) throws Exception {
		System.out.println("$#15818#"); cache.putInCache(object, key);
	}

}
