package com.salesmanager.core.model.user;

import java.util.List;
import java.util.Set;

import com.salesmanager.core.model.common.Criteria;

public class PermissionCriteria extends Criteria {
	
	
	private String permissionName;

	
	private Boolean available = null;
	
	private Set<Integer> groupIds;
	
	private List<String> availabilities;


	public List<String> getAvailabilities() {
		System.out.println("$#4853#"); return availabilities;
	}

	public void setAvailabilities(List<String> availabilities) {
		this.availabilities = availabilities;
	}

	public Boolean getAvailable() {
		System.out.println("$#4855#"); System.out.println("$#4854#"); return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
	}

	public String getPermissionName() {
		System.out.println("$#4856#"); return permissionName;
	}

	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}

	public Set<Integer> getGroupIds() {
		System.out.println("$#4857#"); return groupIds;
	}

	public void setGroupIds(Set<Integer> groupIds) {
		this.groupIds = groupIds;
	}


}
