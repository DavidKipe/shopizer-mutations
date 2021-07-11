package com.salesmanager.core.utils;

import java.util.Date;

public class CloneUtils {
	
	private CloneUtils() {};
	
	public static Date clone(Date date) {
		System.out.println("$#4914#"); if (date != null) {
			System.out.println("$#4915#"); return (Date) date.clone();
		}
		return null;
	}

}
