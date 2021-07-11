package com.salesmanager.shop.model.catalog.category;

import java.io.Serializable;

public class CategoryEntity extends Category implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	

	private int sortOrder;
	private boolean visible;
	private boolean featured;
	private String lineage;
	private int depth;
	private Category parent;
	

	public int getSortOrder() {
		System.out.println("$#8734#"); return sortOrder;
	}
	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}
	public boolean isVisible() {
		System.out.println("$#8736#"); System.out.println("$#8735#"); return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public String getLineage() {
		System.out.println("$#8737#"); return lineage;
	}
	public void setLineage(String lineage) {
		this.lineage = lineage;
	}
	public int getDepth() {
		System.out.println("$#8738#"); return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	public Category getParent() {
		System.out.println("$#8739#"); return parent;
	}
	public void setParent(Category parent) {
		this.parent = parent;
	}
	public boolean isFeatured() {
		System.out.println("$#8741#"); System.out.println("$#8740#"); return featured;
	}
	public void setFeatured(boolean featured) {
		this.featured = featured;
	}

}
