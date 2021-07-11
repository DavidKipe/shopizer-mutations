package com.salesmanager.shop.store.api.exception;


public class GenericRuntimeException extends RuntimeException {

  /**
  * 
  */
  private static final long serialVersionUID = 1L;
  private String errorCode;
  private String errorMessage;

  public GenericRuntimeException(String errorCode, String errorMessage) {
				System.out.println("$#11202#"); this.setErrorCode(errorCode);
				System.out.println("$#11203#"); this.setErrorMessage(errorMessage);
  }

  public GenericRuntimeException(String errorMessage) {
				System.out.println("$#11204#"); this.setErrorMessage(errorMessage);
  }

  public GenericRuntimeException(Throwable exception) {
    super(exception);
				System.out.println("$#11205#"); this.setErrorCode(null);
				System.out.println("$#11206#"); this.setErrorMessage(null);
  }

  public GenericRuntimeException(String errorMessage, Throwable exception) {
    super(exception);
				System.out.println("$#11207#"); this.setErrorCode(null);
				System.out.println("$#11208#"); this.setErrorMessage(errorMessage);
  }

  public GenericRuntimeException(String errorCode, String errorMessage, Throwable exception) {
    super(exception);
				System.out.println("$#11209#"); this.setErrorCode(errorCode);
				System.out.println("$#11210#"); this.setErrorMessage(errorMessage);
  }

  public String getErrorCode() {
				System.out.println("$#11211#"); return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public String getErrorMessage() {
				System.out.println("$#11212#"); return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
}
