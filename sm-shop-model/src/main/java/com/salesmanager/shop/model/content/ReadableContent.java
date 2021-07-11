package com.salesmanager.shop.model.content;

/**
 * A simple piece of content
 * @author carlsamson
 *
 */
public class ReadableContent extends Content {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String content;

  public String getContent() {
				System.out.println("$#9010#"); return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

}
