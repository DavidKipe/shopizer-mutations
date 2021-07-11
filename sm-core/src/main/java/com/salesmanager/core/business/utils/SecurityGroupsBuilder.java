package com.salesmanager.core.business.utils;

import java.util.ArrayList;
import java.util.List;

import com.salesmanager.core.model.user.Group;
import com.salesmanager.core.model.user.GroupType;
import com.salesmanager.core.model.user.Permission;

/**
 * Helper for building security groups and permissions
 * @author carlsamson
 *
 */
public class SecurityGroupsBuilder {
	
	private List<Group> groups = new ArrayList<Group>();
	private Group lastGroup = null;
	
	
	public SecurityGroupsBuilder addGroup(String name, GroupType type) {
		
		Group g = new Group();
		System.out.println("$#3706#"); g.setGroupName(name);
		System.out.println("$#3707#"); g.setGroupType(type);
		groups.add(g);
		this.lastGroup = g;
		
		System.out.println("$#3708#"); return this;
	}
	
	public SecurityGroupsBuilder addPermission(String name) {
		System.out.println("$#3709#"); if(this.lastGroup == null) {
			Group g = this.groups.get(0);
			System.out.println("$#3710#"); if(g == null) {
				g = new Group();
				System.out.println("$#3711#"); g.setGroupName("UNDEFINED");
				System.out.println("$#3712#"); g.setGroupType(GroupType.ADMIN);
				groups.add(g);
				this.lastGroup = g;
			}
		}
		
		Permission permission = new Permission();
		System.out.println("$#3713#"); permission.setPermissionName(name);
		lastGroup.getPermissions().add(permission);
		
		System.out.println("$#3714#"); return this;
	}
	
	public SecurityGroupsBuilder addPermission(Permission permission) {
		
		System.out.println("$#3715#"); if(this.lastGroup == null) {
			Group g = this.groups.get(0);
			System.out.println("$#3716#"); if(g == null) {
				g = new Group();
				System.out.println("$#3717#"); g.setGroupName("UNDEFINED");
				System.out.println("$#3718#"); g.setGroupType(GroupType.ADMIN);
				groups.add(g);
				this.lastGroup = g;
			}
		}
		

		lastGroup.getPermissions().add(permission);
		
		System.out.println("$#3719#"); return this;
	}
	
	public List<Group> build() {
		System.out.println("$#3720#"); return groups;
	}

}
