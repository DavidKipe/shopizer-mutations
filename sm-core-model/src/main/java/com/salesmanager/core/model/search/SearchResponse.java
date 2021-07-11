package com.salesmanager.core.model.search;

import java.util.List;
import java.util.Map;

public class SearchResponse {
	
	private long totalCount = 0;//total number of entries
	private long entryCount = 0;//number of entries asked
	
	private List<SearchEntry> entries;
	private Map<String,List<SearchFacet>> facets;//facet key (example : category) & facet description (example : category code)
	
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
	public long getTotalCount() {
		System.out.println("$#4521#"); return totalCount;
	}
	public void setEntryCount(long entryCount) {
		this.entryCount = entryCount;
	}
	public long getEntryCount() {
		System.out.println("$#4522#"); return entryCount;
	}
	public void setEntries(List<SearchEntry> entries) {
		this.entries = entries;
	}
	public List<SearchEntry> getEntries() {
		System.out.println("$#4523#"); return entries;
	}
	public void setFacets(Map<String,List<SearchFacet>> facets) {
		this.facets = facets;
	}
	public Map<String,List<SearchFacet>> getFacets() {
		System.out.println("$#4524#"); return facets;
	}

}
