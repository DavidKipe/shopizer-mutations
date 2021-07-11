package com.salesmanager.core.model.order.orderstatus;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.salesmanager.core.constants.SchemaConstant;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.utils.CloneUtils;

@Entity
@Table (name="ORDER_STATUS_HISTORY" , schema=SchemaConstant.SALESMANAGER_SCHEMA)
public class OrderStatusHistory implements Serializable {
	private static final long serialVersionUID = 3438730310126102187L;
	
	@Id
	@Column ( name="ORDER_STATUS_HISTORY_ID")
	@TableGenerator(name = "TABLE_GEN", table = "SM_SEQUENCER", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT",
		pkColumnValue = "STATUS_HIST_ID_NEXT_VALUE")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN")
	private Long id;
	
	@JsonIgnore
	@ManyToOne(targetEntity = Order.class)
	@JoinColumn(name = "ORDER_ID", nullable = false)
	private Order order;
	
	@Enumerated(value = EnumType.STRING)
	private OrderStatus status;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATE_ADDED", nullable = false)
	private Date dateAdded;
	
	@Column(name = "CUSTOMER_NOTIFIED")
	private java.lang.Integer customerNotified;
	
	@Column(name = "COMMENTS")
	@Type(type = "org.hibernate.type.TextType")
	private String comments;
	
	public OrderStatusHistory() {
	}

	public Long getId() {
		System.out.println("$#4378#"); return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Order getOrder() {
		System.out.println("$#4379#"); return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public OrderStatus getStatus() {
		System.out.println("$#4380#"); return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public Date getDateAdded() {
		System.out.println("$#4381#"); return CloneUtils.clone(dateAdded);
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = CloneUtils.clone(dateAdded);
	}

	public java.lang.Integer getCustomerNotified() {
		System.out.println("$#4382#"); return customerNotified;
	}

	public void setCustomerNotified(java.lang.Integer customerNotified) {
		this.customerNotified = customerNotified;
	}

	public String getComments() {
		System.out.println("$#4383#"); return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

}