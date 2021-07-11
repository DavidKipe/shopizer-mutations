package com.salesmanager.core.model.payments;

public enum PaymentType {
	
	
	
	CREDITCARD("creditcard"), FREE("free"), COD("cod"), MONEYORDER("moneyorder"), PAYPAL("paypal");

	
	private String paymentType;
	
	PaymentType(String type) {
		paymentType = type;
	}
	
    public static PaymentType fromString(String text) {
						System.out.println("$#4428#"); if (text != null) {
		      for (PaymentType b : PaymentType.values()) {
		    	String payemntType = text.toUpperCase(); 
										System.out.println("$#4429#"); if (payemntType.equalsIgnoreCase(b.name())) {
												System.out.println("$#4430#"); return b;
		        }
		      }
		    }
		    return null;
	}
}
