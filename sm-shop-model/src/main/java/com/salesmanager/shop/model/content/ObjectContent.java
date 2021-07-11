package com.salesmanager.shop.model.content;

import com.salesmanager.shop.model.entity.ResourceUrlAccess;

public class ObjectContent extends ContentPath implements ResourceUrlAccess {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String slug;
  private String metaDetails;
  private String title;
  private String pageContent;
  private String language;
  public String getPageContent() {
						System.out.println("$#9000#"); return pageContent;
  }
  public void setPageContent(String pageContent) {
      this.pageContent = pageContent;
  }

  public String getSlug() {
				System.out.println("$#9001#"); return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

  public String getMetaDetails() {
				System.out.println("$#9002#"); return metaDetails;
  }

  public void setMetaDetails(String metaDetails) {
    this.metaDetails = metaDetails;
  }

  public String getTitle() {
				System.out.println("$#9003#"); return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
  public String getLanguage() {
				System.out.println("$#9004#"); return language;
  }
  public void setLanguage(String language) {
    this.language = language;
  }


}
