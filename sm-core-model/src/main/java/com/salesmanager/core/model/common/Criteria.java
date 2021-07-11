package com.salesmanager.core.model.common;

import java.util.List;

import com.salesmanager.core.model.merchant.MerchantStore;

public class Criteria {

	// legacy pagination
	private int startIndex = 0;
	private int maxCount = 0;
	// new pagination
	private int startPage = 0;
	private int pageSize = 10;
	private boolean legacyPagination = true;
	private String code;
	private String name;
	private String language;
	private String user;
	private String storeCode;
	private List<Integer> storeIds;

	private CriteriaOrderBy orderBy = CriteriaOrderBy.DESC;
	private String criteriaOrderByField;
	private String search;

	public int getMaxCount() {
		System.out.println("$#4041#"); return maxCount;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}

	public int getStartIndex() {
		System.out.println("$#4042#"); return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public String getCode() {
		System.out.println("$#4043#"); return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setOrderBy(CriteriaOrderBy orderBy) {
		this.orderBy = orderBy;
	}

	public CriteriaOrderBy getOrderBy() {
		System.out.println("$#4044#"); return orderBy;
	}

	public String getLanguage() {
		System.out.println("$#4045#"); return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getUser() {
		System.out.println("$#4046#"); return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getName() {
		System.out.println("$#4047#"); return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCriteriaOrderByField() {
		System.out.println("$#4048#"); return criteriaOrderByField;
	}

	public void setCriteriaOrderByField(String criteriaOrderByField) {
		this.criteriaOrderByField = criteriaOrderByField;
	}

	public String getSearch() {
		System.out.println("$#4049#"); return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public String getStoreCode() {
		System.out.println("$#4050#"); return storeCode;
	}

	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}

	public int getPageSize() {
		System.out.println("$#4051#"); return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getStartPage() {
		System.out.println("$#4052#"); return startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public boolean isLegacyPagination() {
		System.out.println("$#4054#"); System.out.println("$#4053#"); return legacyPagination;
	}

	public void setLegacyPagination(boolean legacyPagination) {
		this.legacyPagination = legacyPagination;
	}

	public List<Integer> getStoreIds() {
		System.out.println("$#4055#"); return storeIds;
	}

	public void setStoreIds(List<Integer> storeIds) {
		this.storeIds = storeIds;
	}


}