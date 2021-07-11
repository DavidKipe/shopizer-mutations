package com.salesmanager.core.model.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PermissionList implements Serializable {
	

	private static final long serialVersionUID = -3122326940968441727L;
	private int totalCount;
	private List<Permission> permissions = new ArrayList<Permission>();
	public int getTotalCount() {
		System.out.println("$#4858#"); return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public List<Permission> getPermissions() {
		System.out.println("$#4859#"); return permissions;
	}
	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

}
