package com.salesmanager.core.model.reference.currency;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.salesmanager.core.constants.SchemaConstant;
import com.salesmanager.core.model.generic.SalesManagerEntity;

@Entity
@Table(name = "CURRENCY", schema = SchemaConstant.SALESMANAGER_SCHEMA)
@Cacheable
public class Currency extends SalesManagerEntity<Long, Currency> implements Serializable {
	private static final long serialVersionUID = -999926410367685145L;
	
	@Id
	@Column(name = "CURRENCY_ID")
	@TableGenerator(name = "TABLE_GEN", table = "SM_SEQUENCER", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT", pkColumnValue = "CURRENCY_SEQ_NEXT_VAL")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN")
	private Long id;
	
	@Column(name = "CURRENCY_CURRENCY_CODE" ,nullable = false, unique = true)
	private java.util.Currency currency;
	
	@Column(name = "CURRENCY_SUPPORTED")
	private Boolean supported = true;
	
	@Column(name = "CURRENCY_CODE", unique = true)
	private String code;
	
	@Column(name = "CURRENCY_NAME", unique = true)
	private String name;
	
	public Currency() {
	}
	
	@Override
	public Long getId() {
		System.out.println("$#4461#"); return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public java.util.Currency getCurrency() {
		System.out.println("$#4462#"); return currency;
	}

	public void setCurrency(java.util.Currency currency) {
		this.currency = currency;
		this.code = currency.getCurrencyCode();
	}

	public Boolean getSupported() {
		System.out.println("$#4464#"); System.out.println("$#4463#"); return supported;
	}

	public void setSupported(Boolean supported) {
		this.supported = supported;
	}
	
	public String getCode() {
		System.out.println("$#4465#"); if (currency.getCurrencyCode() != code) {
			System.out.println("$#4466#"); return currency.getCurrencyCode();
		}
		System.out.println("$#4467#"); return code;
	}
	
	public String getSymbol() {
		System.out.println("$#4468#"); return currency.getSymbol();
	}

	public String getName() {
		System.out.println("$#4469#"); return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
