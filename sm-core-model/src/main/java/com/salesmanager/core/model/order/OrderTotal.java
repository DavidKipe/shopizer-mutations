package com.salesmanager.core.model.order;

import java.math.BigDecimal;

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

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.salesmanager.core.constants.SchemaConstant;
import com.salesmanager.core.model.generic.SalesManagerEntity;


/**
 * Order line items related to an order.
 * @author casams1
 *
 */

@Entity
@Table (name="ORDER_TOTAL" , schema=SchemaConstant.SALESMANAGER_SCHEMA)
public class OrderTotal extends SalesManagerEntity<Long, OrderTotal> {
	private static final long serialVersionUID = -5885315557404081674L;
	
	@Id
	@Column(name = "ORDER_ACCOUNT_ID", unique=true, nullable=false)
	@TableGenerator(name = "TABLE_GEN", table = "SM_SEQUENCER", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT", pkColumnValue = "ORDER_TOTAL_ID_NEXT_VALUE")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN")
	private Long id;
	
	@Column (name ="CODE", nullable=false)
	private String orderTotalCode;//SHIPPING, TAX
	
	@Column (name ="TITLE", nullable=true)
	private String title;
	
	@Column (name ="TEXT", nullable=true)
	@Type(type = "org.hibernate.type.TextType")
	private String text;
	
	@Column (name ="VALUE", precision=15, scale=4, nullable=false )
	private BigDecimal value;
	
	@Column (name ="MODULE", length=60 , nullable=true )
	private String module;
	
	@Column (name ="ORDER_VALUE_TYPE")
	@Enumerated(value = EnumType.STRING)
	private OrderValueType orderValueType = OrderValueType.ONE_TIME;
	
	@Column (name ="ORDER_TOTAL_TYPE")
	@Enumerated(value = EnumType.STRING)
	private OrderTotalType orderTotalType = null;
	
	@Column (name ="SORT_ORDER", nullable=false)
	private int sortOrder;
	
	@JsonIgnore
	@ManyToOne(targetEntity = Order.class)
	@JoinColumn(name = "ORDER_ID", nullable=false)
	private Order order;
	
	public OrderTotal() {
	}

	public Long getId() {
		System.out.println("$#4388#"); return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		System.out.println("$#4389#"); return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		System.out.println("$#4390#"); return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public BigDecimal getValue() {
		System.out.println("$#4391#"); return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public String getModule() {
		System.out.println("$#4392#"); return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public int getSortOrder() {
		System.out.println("$#4393#"); return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Order getOrder() {
		System.out.println("$#4394#"); return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public void setOrderTotalCode(String orderTotalCode) {
		this.orderTotalCode = orderTotalCode;
	}

	public String getOrderTotalCode() {
		System.out.println("$#4395#"); return orderTotalCode;
	}

	public void setOrderValueType(OrderValueType orderValueType) {
		this.orderValueType = orderValueType;
	}

	public OrderValueType getOrderValueType() {
		System.out.println("$#4396#"); return orderValueType;
	}

	public void setOrderTotalType(OrderTotalType orderTotalType) {
		this.orderTotalType = orderTotalType;
	}

	public OrderTotalType getOrderTotalType() {
		System.out.println("$#4397#"); return orderTotalType;
	}


}