package com.salesmanager.shop.model.user;

public class UserEntity extends UserNameEntity {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String firstName;
  private String lastName;
  private String emailAddress;
  private String defaultLanguage;
  private boolean active;




  public String getFirstName() {
				System.out.println("$#9436#"); return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
				System.out.println("$#9437#"); return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmailAddress() {
				System.out.println("$#9438#"); return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }


  public String getDefaultLanguage() {
				System.out.println("$#9439#"); return defaultLanguage;
  }

  public void setDefaultLanguage(String defaultLanguage) {
    this.defaultLanguage = defaultLanguage;
  }

  public boolean isActive() {
				System.out.println("$#9441#"); System.out.println("$#9440#"); return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }


}
