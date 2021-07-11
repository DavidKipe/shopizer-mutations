package com.salesmanager.core.model.catalog.product.attribute;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import com.salesmanager.core.constants.SchemaConstant;
import com.salesmanager.core.model.catalog.product.type.ProductType;
import com.salesmanager.core.model.generic.SalesManagerEntity;
import com.salesmanager.core.model.merchant.MerchantStore;

/**
 * Create a list of option and option value in order to accelerate and 
 * prepare product attribute creation
 * @author carlsamson
 *
 */
@Entity
@Table(name="PRODUCT_OPTION_SET", schema=SchemaConstant.SALESMANAGER_SCHEMA,
uniqueConstraints={
		@UniqueConstraint(columnNames={
				"MERCHANT_ID",
				"PRODUCT_OPTION_SET_CODE"
			})
	}
)
public class ProductOptionSet extends SalesManagerEntity<Long, ProductOptionSet> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="PRODUCT_OPTION_SET_ID")
	@TableGenerator(name = "TABLE_GEN", table = "SM_SEQUENCER", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT", pkColumnValue = "PRODUCT_OPT_SET_SEQ_NEXT_VAL")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN")
	private Long id;
	
	@NotEmpty
	@Pattern(regexp="^[a-zA-Z0-9_]*$")
	@Column(name="PRODUCT_OPTION_SET_CODE")
	private String code;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="PRODUCT_OPTION_ID", nullable=false)
	private ProductOption option;
	
	@ManyToMany(fetch = FetchType.LAZY, targetEntity=ProductOptionValue.class)
	@JoinTable(name = "PRODUCT_OPT_SET_OPT_VALUE")
	private List<ProductOptionValue> values = new ArrayList<ProductOptionValue>();
	
	@ManyToMany(fetch = FetchType.LAZY, targetEntity=ProductType.class)
	@JoinTable(name = "PRODUCT_OPT_SET_PRD_TYPE")
	private Set<ProductType> productTypes = new HashSet<ProductType>();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="MERCHANT_ID", nullable=false)
	private MerchantStore store;
	
	@Column(name="PRODUCT_OPTION_SET_DISP")
	private boolean optionDisplayOnly = false;
	
	
	public ProductOption getOption() {
		System.out.println("$#3816#"); return option;
	}
	public void setOption(ProductOption option) {
		this.option = option;
	}
	public List<ProductOptionValue> getValues() {
		System.out.println("$#3817#"); return values;
	}
	public void setValues(List<ProductOptionValue> values) {
		this.values = values;
	}
	public MerchantStore getStore() {
		System.out.println("$#3818#"); return store;
	}
	public void setStore(MerchantStore store) {
		this.store = store;
	}
	@Override
	public Long getId() {
		System.out.println("$#3819#"); return this.id;
	}
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	public String getCode() {
		System.out.println("$#3820#"); return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public boolean isOptionDisplayOnly() {
		System.out.println("$#3822#"); System.out.println("$#3821#"); return optionDisplayOnly;
	}
	public void setOptionDisplayOnly(boolean optionDisplayOnly) {
		this.optionDisplayOnly = optionDisplayOnly;
	}
	
	public Set<ProductType> getProductTypes() {
		System.out.println("$#3823#"); return productTypes;
	}
	public void setProductTypes(Set<ProductType> productTypes) {
		this.productTypes = productTypes;
	}

}
