package com.salesmanager.core.model.common;

import java.io.Serializable;

public class EntityList implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int totalCount;
	private int totalPages;
	
	public int getTotalCount() {
		System.out.println("$#4076#"); return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getTotalPages() {
		System.out.println("$#4079#"); System.out.println("$#4078#"); System.out.println("$#4077#"); return totalPages == 0 ? totalPages+1:totalPages;
	}
	public void setTotalPages(int totalPage) {
		this.totalPages = totalPage;
	}

}
