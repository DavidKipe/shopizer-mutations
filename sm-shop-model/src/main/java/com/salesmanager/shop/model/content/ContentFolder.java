package com.salesmanager.shop.model.content;

import java.util.ArrayList;
import java.util.List;

/**
 * Folder containing content
 * images and other files
 * @author carlsamson
 *
 */
public class ContentFolder {
	
	private String path;
	List<Content> content = new ArrayList<Content>();
	public String getPath() {
		System.out.println("$#8996#"); return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public List<Content> getContent() {
		System.out.println("$#8997#"); return content;
	}
	public void setContent(List<Content> content) {
		this.content = content;
	}

}
