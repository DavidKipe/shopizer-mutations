package com.salesmanager.shop.store.security;

import javax.validation.constraints.NotEmpty;

public class PasswordRequest extends AuthenticationRequest {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @NotEmpty(message = "{message.password.required}")
  private String current;
  
  @NotEmpty(message = "{message.password.required}")
  private String repeatPassword;

  public String getCurrent() {
				System.out.println("$#15343#"); return current;
  }

  public void setCurrent(String current) {
    this.current = current;
  }

  public String getRepeatPassword() {
				System.out.println("$#15344#"); return repeatPassword;
  }

  public void setRepeatPassword(String repeatPassword) {
    this.repeatPassword = repeatPassword;
  }

}
