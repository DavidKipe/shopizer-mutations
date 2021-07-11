package com.salesmanager.shop.utils;

import com.salesmanager.core.model.user.Group;
import com.salesmanager.core.model.user.User;

import java.util.List;

public class UserUtils {
	
	public static boolean userInGroup(User user,String groupName) {
		
		
		
		List<Group> logedInUserGroups = user.getGroups();
		for(Group group : logedInUserGroups) {
			System.out.println("$#15814#"); if(group.getGroupName().equals(groupName)) {
				System.out.println("$#15815#"); return true;
			}
		}
		
		System.out.println("$#15816#"); return false;
		
	}

}
