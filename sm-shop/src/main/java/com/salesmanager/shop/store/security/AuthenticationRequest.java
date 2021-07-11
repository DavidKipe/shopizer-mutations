package com.salesmanager.shop.store.security;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

public class AuthenticationRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Username and password must be used when using normal system authentication
	 * for a registered customer
	 */
	@NotEmpty(message="{NotEmpty.customer.userName}")
    private String username;
	@NotEmpty(message="{message.password.required}")
    private String password;
    


    public AuthenticationRequest() {
        super();
    }

    public AuthenticationRequest(String username, String password) {
								System.out.println("$#15240#"); this.setUsername(username);
								System.out.println("$#15241#"); this.setPassword(password);
    }

    public String getUsername() {
								System.out.println("$#15242#"); return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
								System.out.println("$#15243#"); return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
