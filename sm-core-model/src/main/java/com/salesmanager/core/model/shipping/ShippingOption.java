package com.salesmanager.core.model.shipping;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShippingOption implements Serializable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ShippingOption.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BigDecimal optionPrice;
	private Long shippingQuoteOptionId;


	private String optionName = null;
	private String optionCode = null;
	private String optionDeliveryDate = null;
	private String optionShippingDate = null;
	private String optionPriceText = null;
	private String optionId = null;
	private String description = null;
	private String shippingModuleCode = null;
	private String note = null;
	
	private String estimatedNumberOfDays;

	

	public BigDecimal getOptionPrice() {
		
		System.out.println("$#4614#"); if(optionPrice == null && !StringUtils.isBlank(this.getOptionPriceText())) {//if price text only is available, try to parse it
			try {
				this.optionPrice = new BigDecimal(this.getOptionPriceText());
			} catch(Exception e) {
				LOGGER.error("Can't convert price text " + this.getOptionPriceText() + " to big decimal");
			}
		}
		
		System.out.println("$#4616#"); return optionPrice;
	}
	
	public void setOptionPrice(BigDecimal optionPrice) {
		this.optionPrice = optionPrice;
	}

	public void setOptionCode(String optionCode) {
		this.optionCode = optionCode;
	}
	public String getOptionCode() {
		System.out.println("$#4617#"); return optionCode;
	}
	public void setOptionName(String optionName) {
		this.optionName = optionName;
	}
	public String getOptionName() {
		System.out.println("$#4618#"); return optionName;
	}

	public void setOptionPriceText(String optionPriceText) {
		this.optionPriceText = optionPriceText;
	}
	public String getOptionPriceText() {
		System.out.println("$#4619#"); return optionPriceText;
	}
	public void setOptionId(String optionId) {
		this.optionId = optionId;
	}
	public String getOptionId() {
		System.out.println("$#4620#"); return optionId;
	}
	public void setOptionDeliveryDate(String optionDeliveryDate) {
		this.optionDeliveryDate = optionDeliveryDate;
	}
	public String getOptionDeliveryDate() {
		System.out.println("$#4621#"); return optionDeliveryDate;
	}
	public void setOptionShippingDate(String optionShippingDate) {
		this.optionShippingDate = optionShippingDate;
	}
	public String getOptionShippingDate() {
		System.out.println("$#4622#"); return optionShippingDate;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDescription() {
		System.out.println("$#4623#"); return description;
	}
	public void setEstimatedNumberOfDays(String estimatedNumberOfDays) {
		this.estimatedNumberOfDays = estimatedNumberOfDays;
	}
	public String getEstimatedNumberOfDays() {
		System.out.println("$#4624#"); return estimatedNumberOfDays;
	}

	public String getShippingModuleCode() {
		System.out.println("$#4625#"); return shippingModuleCode;
	}

	public void setShippingModuleCode(String shippingModuleCode) {
		this.shippingModuleCode = shippingModuleCode;
	}

	public String getNote() {
		System.out.println("$#4626#"); return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Long getShippingQuoteOptionId() {
		System.out.println("$#4627#"); return shippingQuoteOptionId;
	}

	public void setShippingQuoteOptionId(Long shippingQuoteOptionId) {
		this.shippingQuoteOptionId = shippingQuoteOptionId;
	}

}
