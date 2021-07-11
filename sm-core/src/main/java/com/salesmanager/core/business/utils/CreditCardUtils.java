package com.salesmanager.core.business.utils;


public class CreditCardUtils {
	
	
	public static final int MASTERCARD = 0, VISA = 1;
	public static final int AMEX = 2, DISCOVER = 3, DINERS = 4;

	public static String maskCardNumber(String clearcardnumber)
			throws Exception {

		System.out.println("$#3463#"); System.out.println("$#3462#"); if (clearcardnumber.length() < 10) {
			throw new Exception("Invalid number of digits");
		}

		int length = clearcardnumber.length();

		String prefix = clearcardnumber.substring(0, 4);
		System.out.println("$#3464#"); String suffix = clearcardnumber.substring(length - 4);

		StringBuffer mask = new StringBuffer();
		mask.append(prefix).append("XXXXXXXXXX").append(suffix);

		System.out.println("$#3465#"); return mask.toString();
	}

	



}
