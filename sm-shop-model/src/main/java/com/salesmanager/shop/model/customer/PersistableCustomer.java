package com.salesmanager.shop.model.customer;

import java.util.List;
import com.salesmanager.shop.model.customer.attribute.PersistableCustomerAttribute;
import com.salesmanager.shop.model.security.PersistableGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;



@ApiModel(value="Customer", description="Customer model object")
public class PersistableCustomer extends CustomerEntity {

	/**
	 * 
	 */
    @ApiModelProperty(notes = "Customer password")
	private String password = null;
	private static final long serialVersionUID = 1L;
	private List<PersistableCustomerAttribute> attributes;
	private List<PersistableGroup> groups;
	
	
	public void setAttributes(List<PersistableCustomerAttribute> attributes) {
		this.attributes = attributes;
	}
	public List<PersistableCustomerAttribute> getAttributes() {
		System.out.println("$#9084#"); return attributes;
	}

	public String getPassword() {
		System.out.println("$#9085#"); return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<PersistableGroup> getGroups() {
		System.out.println("$#9086#"); return groups;
	}
	public void setGroups(List<PersistableGroup> groups) {
		this.groups = groups;
	}
	

}
