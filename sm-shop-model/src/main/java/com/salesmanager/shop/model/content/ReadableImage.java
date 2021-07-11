package com.salesmanager.shop.model.content;

import java.io.Serializable;

/**
 * Used for defining an image name and its path
 * @author carlsamson
 *
 */
public class ReadableImage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String path;
	public String getPath() {
		System.out.println("$#9025#"); return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getName() {
		System.out.println("$#9026#"); return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
