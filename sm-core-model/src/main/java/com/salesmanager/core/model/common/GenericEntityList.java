package com.salesmanager.core.model.common;

import java.util.List;

public class GenericEntityList<T>  extends EntityList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	List<T> list;

	public List<T> getList() {
		System.out.println("$#4080#"); return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

}
