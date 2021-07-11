package com.salesmanager.shop.store.security;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

public class ResetPasswordRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Username and password must be used when requesting password request
	 */
	@NotEmpty(message="{NotEmpty.customer.userName}")
    private String username;
    


    public ResetPasswordRequest() {
        super();
    }

    public ResetPasswordRequest(String username) {
								System.out.println("$#15345#"); this.setUsername(username);
    }

    public String getUsername() {
								System.out.println("$#15346#"); return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
