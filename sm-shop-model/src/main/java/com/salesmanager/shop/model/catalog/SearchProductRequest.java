package com.salesmanager.shop.model.catalog;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

/**
 * Search product request
 * @author c.samson
 *
 */
public class SearchProductRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_COUNT = 100;
	private static final int START_COUNT = 0;
	@NotEmpty
	private String query;
	private int count = DEFAULT_COUNT;
	private int start = START_COUNT;

	public String getQuery() {
		System.out.println("$#8975#"); return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public int getStart() {
		System.out.println("$#8976#"); return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getCount() {
		System.out.println("$#8977#"); return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
