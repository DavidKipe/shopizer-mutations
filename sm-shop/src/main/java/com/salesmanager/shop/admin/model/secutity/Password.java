package com.salesmanager.shop.admin.model.secutity;

import com.salesmanager.core.model.user.User;

import java.io.Serializable;

/**
 * Entity used in the cahange passord page
 * @author csamson777
 *
 */
public class Password implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String password;
	private String newPassword;
	private String repeatPassword;
	
	private User user;

	
	
	public String getPassword() {
		System.out.println("$#7837#"); return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNewPassword() {
		System.out.println("$#7838#"); return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getRepeatPassword() {
		System.out.println("$#7839#"); return repeatPassword;
	}

	public void setRepeatPassword(String repeatPassword) {
		this.repeatPassword = repeatPassword;
	}

	public User getUser() {
		System.out.println("$#7840#"); return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	
}
