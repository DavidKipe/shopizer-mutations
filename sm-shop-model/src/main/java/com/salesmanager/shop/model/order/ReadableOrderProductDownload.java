package com.salesmanager.shop.model.order;

import java.io.Serializable;

import com.salesmanager.shop.model.entity.Entity;

public class ReadableOrderProductDownload extends Entity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long orderId;
	
	private String productName;
	private String downloadUrl;
	
	private String fileName;
	
	private int downloadExpiryDays = 0;
	private int downloadCount = 0;
	public int getDownloadExpiryDays() {
		System.out.println("$#9149#"); return downloadExpiryDays;
	}
	public void setDownloadExpiryDays(int downloadExpiryDays) {
		this.downloadExpiryDays = downloadExpiryDays;
	}
	public int getDownloadCount() {
		System.out.println("$#9150#"); return downloadCount;
	}
	public void setDownloadCount(int downloadCount) {
		this.downloadCount = downloadCount;
	}
	public String getProductName() {
		System.out.println("$#9151#"); return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getDownloadUrl() {
		System.out.println("$#9152#"); return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	public long getOrderId() {
		System.out.println("$#9153#"); return orderId;
	}
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	public String getFileName() {
		System.out.println("$#9154#"); return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


}
