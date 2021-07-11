package com.salesmanager.shop.store.model.filter;

/**
 * Used in Category and Search to filter display based on other
 * entities such as Manufacturer
 * @author Carl Samson
 *
 */
public class QueryFilter {
	
	/**
	 * used when filtering on an entity code (example property)
	 */
	private String filterCode;
	/**
	 * used when filtering on an entity id
	 */
	private Long filterId;
	private QueryFilterType filterType;
	public String getFilterCode() {
		System.out.println("$#15184#"); return filterCode;
	}
	public void setFilterCode(String filterCode) {
		this.filterCode = filterCode;
	}
	public Long getFilterId() {
		System.out.println("$#15185#"); return filterId;
	}
	public void setFilterId(Long filterId) {
		this.filterId = filterId;
	}
	public QueryFilterType getFilterType() {
		System.out.println("$#15186#"); return filterType;
	}
	public void setFilterType(QueryFilterType filterType) {
		this.filterType = filterType;
	}

}
