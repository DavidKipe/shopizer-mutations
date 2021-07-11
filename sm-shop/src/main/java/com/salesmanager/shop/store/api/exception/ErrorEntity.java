package com.salesmanager.shop.store.api.exception;

public class ErrorEntity {
    private String errorCode;
    private String message;
    public String getErrorCode() {
						System.out.println("$#11200#"); return errorCode;
    }
    public void setErrorCode(String errorCode) {
      this.errorCode = errorCode;
    }
    public String getMessage() {
						System.out.println("$#11201#"); return message;
    }
    public void setMessage(String message) {
      this.message = message;
    }
}
