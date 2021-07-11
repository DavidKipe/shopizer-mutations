package com.salesmanager.core.model.catalog.product.attribute;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.salesmanager.core.constants.SchemaConstant;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.generic.SalesManagerEntity;

@Entity
@Table(name="PRODUCT_ATTRIBUTE", schema=SchemaConstant.SALESMANAGER_SCHEMA,
	uniqueConstraints={
		@UniqueConstraint(columnNames={
				"OPTION_ID",
				"OPTION_VALUE_ID",
				"PRODUCT_ID"
			})
	}
)
public class ProductAttribute extends SalesManagerEntity<Long, ProductAttribute> {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "PRODUCT_ATTRIBUTE_ID", unique=true, nullable=false)
	@TableGenerator(name = "TABLE_GEN", table = "SM_SEQUENCER", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT", pkColumnValue = "PRODUCT_ATTR_SEQ_NEXT_VAL")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN")
	private Long id;

	
	@Column(name="PRODUCT_ATRIBUTE_PRICE")
	private BigDecimal productAttributePrice;


	@Column(name="PRODUCT_ATTRIBUTE_SORT_ORD")
	private Integer productOptionSortOrder;
	
	@Column(name="PRODUCT_ATTRIBUTE_FREE")
	private boolean productAttributeIsFree;
	

	@Column(name="PRODUCT_ATTRIBUTE_WEIGHT")
	private BigDecimal productAttributeWeight;
	
	@Column(name="PRODUCT_ATTRIBUTE_DEFAULT")
	private boolean attributeDefault=false;
	
	@Column(name="PRODUCT_ATTRIBUTE_REQUIRED")
	private boolean attributeRequired=false;
	
	/**
	 * a read only attribute is considered as a core attribute addition
	 */
	@Column(name="PRODUCT_ATTRIBUTE_FOR_DISP")
	private boolean attributeDisplayOnly=false;
	

	@Column(name="PRODUCT_ATTRIBUTE_DISCOUNTED")
	private boolean attributeDiscounted=false;
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="OPTION_ID", nullable=false)
	private ProductOption productOption;
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="OPTION_VALUE_ID", nullable=false)
	private ProductOptionValue productOptionValue;
	
	
	/**
	 * This transient object property
	 * is a utility used only to submit from a free text
	 */
	@Transient
	private String attributePrice = "0";
	
	
	/**
	 * This transient object property
	 * is a utility used only to submit from a free text
	 */
	@Transient
	private String attributeSortOrder = "0";
	


	/**
	 * This transient object property
	 * is a utility used only to submit from a free text
	 */
	@Transient
	private String attributeAdditionalWeight = "0";
	
	public String getAttributePrice() {
		System.out.println("$#3782#"); return attributePrice;
	}

	public void setAttributePrice(String attributePrice) {
		this.attributePrice = attributePrice;
	}

	@JsonIgnore
	@ManyToOne(targetEntity = Product.class)
	@JoinColumn(name = "PRODUCT_ID", nullable = false)
	private Product product;
	
	public ProductAttribute() {
	}

	@Override
	public Long getId() {
		System.out.println("$#3783#"); return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}



	public Integer getProductOptionSortOrder() {
		System.out.println("$#3784#"); return productOptionSortOrder;
	}

	public void setProductOptionSortOrder(Integer productOptionSortOrder) {
		this.productOptionSortOrder = productOptionSortOrder;
	}

	public boolean getProductAttributeIsFree() {
		System.out.println("$#3786#"); System.out.println("$#3785#"); return productAttributeIsFree;
	}

	public void setProductAttributeIsFree(boolean productAttributeIsFree) {
		this.productAttributeIsFree = productAttributeIsFree;
	}

	public BigDecimal getProductAttributeWeight() {
		System.out.println("$#3787#"); return productAttributeWeight;
	}

	public void setProductAttributeWeight(BigDecimal productAttributeWeight) {
		this.productAttributeWeight = productAttributeWeight;
	}

	public boolean getAttributeDefault() {
		System.out.println("$#3789#"); System.out.println("$#3788#"); return attributeDefault;
	}

	public void setAttributeDefault(boolean attributeDefault) {
		this.attributeDefault = attributeDefault;
	}

	public boolean getAttributeRequired() {
		System.out.println("$#3791#"); System.out.println("$#3790#"); return attributeRequired;
	}

	public void setAttributeRequired(boolean attributeRequired) {
		this.attributeRequired = attributeRequired;
	}

	public boolean getAttributeDisplayOnly() {
		System.out.println("$#3793#"); System.out.println("$#3792#"); return attributeDisplayOnly;
	}

	public void setAttributeDisplayOnly(boolean attributeDisplayOnly) {
		this.attributeDisplayOnly = attributeDisplayOnly;
	}

	public boolean getAttributeDiscounted() {
		System.out.println("$#3795#"); System.out.println("$#3794#"); return attributeDiscounted;
	}

	public void setAttributeDiscounted(boolean attributeDiscounted) {
		this.attributeDiscounted = attributeDiscounted;
	}

	public ProductOption getProductOption() {
		System.out.println("$#3796#"); return productOption;
	}

	public void setProductOption(ProductOption productOption) {
		this.productOption = productOption;
	}

	public ProductOptionValue getProductOptionValue() {
		System.out.println("$#3797#"); return productOptionValue;
	}

	public void setProductOptionValue(ProductOptionValue productOptionValue) {
		this.productOptionValue = productOptionValue;
	}

	public Product getProduct() {
		System.out.println("$#3798#"); return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
	
	
	public String getAttributeSortOrder() {
		System.out.println("$#3799#"); return attributeSortOrder;
	}

	public void setAttributeSortOrder(String attributeSortOrder) {
		this.attributeSortOrder = attributeSortOrder;
	}

	public String getAttributeAdditionalWeight() {
		System.out.println("$#3800#"); return attributeAdditionalWeight;
	}

	public void setAttributeAdditionalWeight(String attributeAdditionalWeight) {
		this.attributeAdditionalWeight = attributeAdditionalWeight;
	}
	
	public BigDecimal getProductAttributePrice() {
		System.out.println("$#3801#"); return productAttributePrice;
	}

	public void setProductAttributePrice(BigDecimal productAttributePrice) {
		this.productAttributePrice = productAttributePrice;
	}



}
