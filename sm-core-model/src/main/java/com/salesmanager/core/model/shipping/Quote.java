package com.salesmanager.core.model.shipping;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.salesmanager.core.constants.SchemaConstant;
import com.salesmanager.core.model.common.Delivery;
import com.salesmanager.core.model.generic.SalesManagerEntity;


/**
 * Shipping quote received from external shipping quote module or calculated internally
 * @author c.samson
 *
 */

@Entity
@Table (name="SHIPPING_QUOTE" , schema=SchemaConstant.SALESMANAGER_SCHEMA)
public class Quote extends SalesManagerEntity<Long, Quote> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "SHIPPING_QUOTE_ID", unique=true, nullable=false)
	@TableGenerator(name = "TABLE_GEN", table = "SM_SEQUENCER", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT", pkColumnValue = "SHIP_QUOTE_ID_NEXT_VALUE")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN")
	Long id;

	@Column(name = "ORDER_ID", nullable = true)
	private Long orderId;
	
	@Column(name = "CUSTOMER_ID", nullable = true)
	private Long customerId;
	
	@Column(name = "CART_ID", nullable = true)
	private Long cartId;

	@Column(name = "MODULE", nullable = false)
	private String module;
	
	@Column(name = "OPTION_NAME", nullable = true)
	private String optionName = null;
	
	@Column(name = "OPTION_CODE", nullable = true)
	private String optionCode = null;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column (name ="OPTION_DELIVERY_DATE")
	private Date optionDeliveryDate = null;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column (name ="OPTION_SHIPPING_DATE")
	private Date optionShippingDate = null;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column (name ="QUOTE_DATE")
	private Date quoteDate;
	
	@Column(name = "SHIPPING_NUMBER_DAYS")
	private Integer estimatedNumberOfDays;
	
	@Column (name ="QUOTE_PRICE")
	private BigDecimal price = null;
	
	@Column (name ="QUOTE_HANDLING")
	private BigDecimal handling = null;
	
	@Column(name = "FREE_SHIPPING")
	private boolean freeShipping;
	
	@Column (name ="IP_ADDRESS")
	private String ipAddress;
	
	@Embedded
	private Delivery delivery = null;

	public Long getOrderId() {
		System.out.println("$#4547#"); return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getCustomerId() {
		System.out.println("$#4548#"); return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public String getModule() {
		System.out.println("$#4549#"); return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getOptionName() {
		System.out.println("$#4550#"); return optionName;
	}

	public void setOptionName(String optionName) {
		this.optionName = optionName;
	}

	public String getOptionCode() {
		System.out.println("$#4551#"); return optionCode;
	}

	public void setOptionCode(String optionCode) {
		this.optionCode = optionCode;
	}

	public Date getOptionDeliveryDate() {
		System.out.println("$#4552#"); return optionDeliveryDate;
	}

	public void setOptionDeliveryDate(Date optionDeliveryDate) {
		this.optionDeliveryDate = optionDeliveryDate;
	}

	public Date getOptionShippingDate() {
		System.out.println("$#4553#"); return optionShippingDate;
	}

	public void setOptionShippingDate(Date optionShippingDate) {
		this.optionShippingDate = optionShippingDate;
	}

	public Date getQuoteDate() {
		System.out.println("$#4554#"); return quoteDate;
	}

	public void setQuoteDate(Date quoteDate) {
		this.quoteDate = quoteDate;
	}

	public Integer getEstimatedNumberOfDays() {
		System.out.println("$#4555#"); return estimatedNumberOfDays;
	}

	public void setEstimatedNumberOfDays(Integer estimatedNumberOfDays) {
		this.estimatedNumberOfDays = estimatedNumberOfDays;
	}

	public BigDecimal getPrice() {
		System.out.println("$#4556#"); return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Delivery getDelivery() {
		System.out.println("$#4557#"); return delivery;
	}

	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}
	
	public boolean isFreeShipping() {
		System.out.println("$#4559#"); System.out.println("$#4558#"); return freeShipping;
	}

	public void setFreeShipping(boolean freeShipping) {
		this.freeShipping = freeShipping;
	}
	
	@Override
	public Long getId() {
		System.out.println("$#4560#"); return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
		
	}
	
	public BigDecimal getHandling() {
		System.out.println("$#4561#"); return handling;
	}

	public void setHandling(BigDecimal handling) {
		this.handling = handling;
	}
	
	public Long getCartId() {
		System.out.println("$#4562#"); return cartId;
	}

	public void setCartId(Long cartId) {
		this.cartId = cartId;
	}

	public String getIpAddress() {
		System.out.println("$#4563#"); return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	

}
