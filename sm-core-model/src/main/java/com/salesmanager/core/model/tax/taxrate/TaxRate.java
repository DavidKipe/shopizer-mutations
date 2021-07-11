/*
 * Licensed to csti consulting 
 * You may obtain a copy of the License at
 *
 * http://www.csticonsulting.com
 * Copyright (c) 2006-Aug 24, 2010 Consultation CS-TI inc. 
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.salesmanager.core.model.tax.taxrate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;

import javax.validation.constraints.NotEmpty;

import com.salesmanager.core.constants.SchemaConstant;
import com.salesmanager.core.model.common.audit.AuditListener;
import com.salesmanager.core.model.common.audit.AuditSection;
import com.salesmanager.core.model.common.audit.Auditable;
import com.salesmanager.core.model.generic.SalesManagerEntity;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.core.model.tax.taxclass.TaxClass;

@Entity
@EntityListeners(value = AuditListener.class)
@Table(name = "TAX_RATE" , schema = SchemaConstant.SALESMANAGER_SCHEMA,uniqueConstraints={
		@UniqueConstraint(columnNames={
				"TAX_CODE",
				"MERCHANT_ID"
			})
		}
	)
public class TaxRate  extends SalesManagerEntity<Long, TaxRate> implements Auditable {
	private static final long serialVersionUID = 3356827741612925066L;
	
	@Id
	@Column(name = "TAX_RATE_ID")
	@TableGenerator(name = "TABLE_GEN", table = "SM_SEQUENCER", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT", pkColumnValue = "TAX_RATE_ID_NEXT_VALUE")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN")
	private Long id;
	
	@Embedded
	private AuditSection auditSection = new AuditSection();
	
	@Column(name = "TAX_PRIORITY")
	private Integer taxPriority = 0;
	
	@Column(name = "TAX_RATE" , nullable= false , precision=7, scale=4)
	private BigDecimal taxRate;
	
	@NotEmpty
	@Column(name = "TAX_CODE")
	private String code;
	

	@Column(name = "PIGGYBACK")
	private boolean piggyback;
	
	@ManyToOne
	@JoinColumn(name = "TAX_CLASS_ID" , nullable=false)
	private TaxClass taxClass;
	

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="MERCHANT_ID", nullable=false)
	private MerchantStore merchantStore;
	
	@Valid
	@OneToMany(mappedBy = "taxRate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<TaxRateDescription> descriptions = new ArrayList<TaxRateDescription>();
	
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Country.class)
	@JoinColumn(name="COUNTRY_ID", nullable=false, updatable=true)
	private Country country;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="ZONE_ID", nullable=true, updatable=true)
	private Zone zone;

	@Column(name = "STORE_STATE_PROV", length=100)
	private String stateProvince;
	
	@ManyToOne
	@JoinColumn(name = "PARENT_ID")
	private TaxRate parent;
	
	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<TaxRate> taxRates = new ArrayList<TaxRate>();
	
	@Transient
	private String rateText;
	
	
	public String getRateText() {
		System.out.println("$#4827#"); return rateText;
	}

	public void setRateText(String rateText) {
		this.rateText = rateText;
	}

	public TaxRate() {
	}

	public Long getId() {
		System.out.println("$#4828#"); return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public AuditSection getAuditSection() {
		System.out.println("$#4829#"); return auditSection;
	}
	
	@Override
	public void setAuditSection(AuditSection auditSection) {
		this.auditSection = auditSection;
	}

	public Integer getTaxPriority() {
		System.out.println("$#4830#"); return taxPriority;
	}

	public void setTaxPriority(Integer taxPriority) {
		this.taxPriority = taxPriority;
	}

	public BigDecimal getTaxRate() {
		System.out.println("$#4831#"); return taxRate;
	}

	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}

	public boolean isPiggyback() {
		System.out.println("$#4833#"); System.out.println("$#4832#"); return piggyback;
	}

	public void setPiggyback(boolean piggyback) {
		this.piggyback = piggyback;
	}

	public TaxClass getTaxClass() {
		System.out.println("$#4834#"); return taxClass;
	}

	public void setTaxClass(TaxClass taxClass) {
		this.taxClass = taxClass;
	}



	public List<TaxRateDescription> getDescriptions() {
		System.out.println("$#4835#"); return descriptions;
	}

	public void setDescriptions(List<TaxRateDescription> descriptions) {
		this.descriptions = descriptions;
	}



	public MerchantStore getMerchantStore() {
		System.out.println("$#4836#"); return merchantStore;
	}

	public void setMerchantStore(MerchantStore merchantStore) {
		this.merchantStore = merchantStore;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Country getCountry() {
		System.out.println("$#4837#"); return country;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}

	public Zone getZone() {
		System.out.println("$#4838#"); return zone;
	}


	public void setTaxRates(List<TaxRate> taxRates) {
		this.taxRates = taxRates;
	}

	public List<TaxRate> getTaxRates() {
		System.out.println("$#4839#"); return taxRates;
	}

	public void setParent(TaxRate parent) {
		this.parent = parent;
	}

	public TaxRate getParent() {
		System.out.println("$#4840#"); return parent;
	}

	public void setStateProvince(String stateProvince) {
		this.stateProvince = stateProvince;
	}

	public String getStateProvince() {
		System.out.println("$#4841#"); return stateProvince;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		System.out.println("$#4842#"); return code;
	}
}