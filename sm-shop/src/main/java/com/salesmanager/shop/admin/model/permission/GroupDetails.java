package com.salesmanager.shop.admin.model.permission;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;

import com.salesmanager.core.model.user.Group;

public class GroupDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Valid
	private Group group;
	private List<String> types;
	public Group getGroup() {
		System.out.println("$#7824#"); return group;
	}
	public void setGroup(Group group) {
		this.group = group;
	}
	public List<String> getTypes() {
		System.out.println("$#7825#"); return types;
	}
	public void setTypes(List<String> types) {
		this.types = types;
	}
	
}
