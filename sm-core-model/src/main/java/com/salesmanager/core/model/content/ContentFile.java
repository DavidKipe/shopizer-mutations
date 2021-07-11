package com.salesmanager.core.model.content;

public abstract class ContentFile {
	
	
	private String fileName;
	private String mimeType;
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public String getMimeType() {
		System.out.println("$#4111#"); return mimeType;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileName() {
		System.out.println("$#4112#"); return fileName;
	}


}
