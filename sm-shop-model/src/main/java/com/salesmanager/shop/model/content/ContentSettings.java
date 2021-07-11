package com.salesmanager.shop.model.content;

import java.io.Serializable;


/**
 * System configuration settings for content management
 * @author carlsamson
 *
 */
public class ContentSettings implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String httpBasePath;

	public String getHttpBasePath() {
		System.out.println("$#8999#"); return httpBasePath;
	}

	public void setHttpBasePath(String httpBasePath) {
		this.httpBasePath = httpBasePath;
	}

}
