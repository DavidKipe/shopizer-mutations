package com.salesmanager.core.model.order;

import com.salesmanager.core.model.common.Criteria;

public class OrderCriteria extends Criteria {
	
	private String customerName = null;
	private String customerPhone = null;
	private String status = null;
	private Long id = null;
	private String paymentMethod;
	private Long customerId;
	private String email;
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public String getPaymentMethod() {
		System.out.println("$#4335#"); return paymentMethod;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getCustomerName() {
		System.out.println("$#4336#"); return customerName;
	}
    public Long getCustomerId()
    {
								System.out.println("$#4337#"); return customerId;
    }
    public void setCustomerId( Long customerId )
    {
        this.customerId = customerId;
    }
	public String getCustomerPhone() {
		System.out.println("$#4338#"); return customerPhone;
	}
	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}
	public String getStatus() {
		System.out.println("$#4339#"); return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getId() {
		System.out.println("$#4340#"); return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEmail() {
		System.out.println("$#4341#"); return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
   
	
	
	

}
