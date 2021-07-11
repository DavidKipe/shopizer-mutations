package com.salesmanager.core.model.content;

public abstract class StaticContentFile extends ContentFile {
	
	private FileContentType fileContentType;

	public FileContentType getFileContentType() {
		System.out.println("$#4116#"); return fileContentType;
	}

	public void setFileContentType(FileContentType fileContentType) {
		this.fileContentType = fileContentType;
	}


	

}
