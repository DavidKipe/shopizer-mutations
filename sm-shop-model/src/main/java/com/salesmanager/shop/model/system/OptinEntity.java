package com.salesmanager.shop.model.system;

import java.util.Date;

public class OptinEntity extends Optin {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Date startDate;
	private Date endDate;
	private String optinType;
	private String store;
	private String code;
	private String description;
	
	public Date getStartDate() {
		System.out.println("$#9403#"); return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		System.out.println("$#9404#"); return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getStore() {
		System.out.println("$#9405#"); return store;
	}
	public void setStore(String store) {
		this.store = store;
	}
	public String getCode() {
		System.out.println("$#9406#"); return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		System.out.println("$#9407#"); return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
  public String getOptinType() {
				System.out.println("$#9408#"); return optinType;
  }
  public void setOptinType(String optinType) {
    this.optinType = optinType;
  }
	

}
