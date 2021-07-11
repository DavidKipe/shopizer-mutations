package com.salesmanager.shop.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Locale;

public class LabelUtils implements ApplicationContextAware {

	
	private ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;

	}
	
	public String getMessage(String key, Locale locale) {
		System.out.println("$#15742#"); return applicationContext.getMessage(key, null, locale);
	}
	
	public String getMessage(String key, Locale locale, String defaultValue) {
		try {
			System.out.println("$#15743#"); return applicationContext.getMessage(key, null, locale);
		} catch(Exception ignore) {}
		System.out.println("$#15744#"); return defaultValue;
	}
	
	public String getMessage(String key, String[] args, Locale locale) {
		System.out.println("$#15745#"); return applicationContext.getMessage(key, args, locale);
	}

}
