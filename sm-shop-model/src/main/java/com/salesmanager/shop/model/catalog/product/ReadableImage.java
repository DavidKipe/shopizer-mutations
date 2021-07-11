package com.salesmanager.shop.model.catalog.product;

import java.io.Serializable;

import com.salesmanager.shop.model.entity.Entity;

public class ReadableImage extends Entity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String imageName;
	private String imageUrl;
	private String externalUrl;
	private String videoUrl;
	private int imageType;
	private boolean defaultImage;
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public String getImageName() {
		System.out.println("$#8927#"); return imageName;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getImageUrl() {
		System.out.println("$#8928#"); return imageUrl;
	}
	public int getImageType() {
		System.out.println("$#8929#"); return imageType;
	}
	public void setImageType(int imageType) {
		this.imageType = imageType;
	}
	public String getExternalUrl() {
		System.out.println("$#8930#"); return externalUrl;
	}
	public void setExternalUrl(String externalUrl) {
		this.externalUrl = externalUrl;
	}
	public String getVideoUrl() {
		System.out.println("$#8931#"); return videoUrl;
	}
	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}
	public boolean isDefaultImage() {
		System.out.println("$#8933#"); System.out.println("$#8932#"); return defaultImage;
	}
	public void setDefaultImage(boolean defaultImage) {
		this.defaultImage = defaultImage;
	}

}
