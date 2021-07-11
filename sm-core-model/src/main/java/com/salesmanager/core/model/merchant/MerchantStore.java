package com.salesmanager.core.model.merchant;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Pattern;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.salesmanager.core.constants.MeasureUnit;
import com.salesmanager.core.constants.SchemaConstant;
import com.salesmanager.core.model.common.audit.AuditSection;
import com.salesmanager.core.model.common.audit.Auditable;
import com.salesmanager.core.model.generic.SalesManagerEntity;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.currency.Currency;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.core.utils.CloneUtils;

@Entity
@Table(name = "MERCHANT_STORE", schema = SchemaConstant.SALESMANAGER_SCHEMA)
public class MerchantStore extends SalesManagerEntity<Integer, MerchantStore> implements Auditable {

  private static final long serialVersionUID = 1L;

  public final static String DEFAULT_STORE = "DEFAULT";
  
  public MerchantStore(Integer id, String code, String name) {
	  this.id = id;
	  this.code = code;
	  this.storename = name;
	  
  }

  public MerchantStore(Integer id, String code, String name, String storeEmailAddress) {
    this.id = id;
    this.code = code;
    this.storename = name;
    this.storeEmailAddress = storeEmailAddress;
  }



	@Id
	@Column(name = "MERCHANT_ID", unique = true, nullable = false)
	@TableGenerator(name = "TABLE_GEN", table = "SM_SEQUENCER", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT", pkColumnValue = "STORE_SEQ_NEXT_VAL")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN")
	private Integer id;

	@Embedded
	private AuditSection auditSection = new AuditSection();

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "PARENT_ID")
	private MerchantStore parent;

	@JsonIgnore
	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
	private Set<MerchantStore> stores = new HashSet<MerchantStore>();

	@Column(name = "IS_RETAILER")
	private Boolean retailer = false;

	@NotEmpty
	@Column(name = "STORE_NAME", nullable = false, length = 100)
	private String storename;

	@NotEmpty
	@Pattern(regexp = "^[a-zA-Z0-9_]*$")
	@Column(name = "STORE_CODE", nullable = false, unique = true, length = 100)
	private String code;

	@NotEmpty
	@Column(name = "STORE_PHONE", length = 50)
	private String storephone;

	@Column(name = "STORE_ADDRESS")
	private String storeaddress;

	@NotEmpty
	@Column(name = "STORE_CITY", length = 100)
	private String storecity;

	@NotEmpty
	@Column(name = "STORE_POSTAL_CODE", length = 15)
	private String storepostalcode;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Country.class)
	@JoinColumn(name = "COUNTRY_ID", nullable = false, updatable = true)
	private Country country;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Zone.class)
	@JoinColumn(name = "ZONE_ID", nullable = true, updatable = true)
	private Zone zone;

	@Column(name = "STORE_STATE_PROV", length = 100)
	private String storestateprovince;

	@Column(name = "WEIGHTUNITCODE", length = 5)
	private String weightunitcode = MeasureUnit.LB.name();

	@Column(name = "SEIZEUNITCODE", length = 5)
	private String seizeunitcode = MeasureUnit.IN.name();

	@Temporal(TemporalType.DATE)
	@Column(name = "IN_BUSINESS_SINCE")
	private Date inBusinessSince = new Date();

	@Transient
	private String dateBusinessSince;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Language.class)
	@JoinColumn(name = "LANGUAGE_ID", nullable = false)
	private Language defaultLanguage;

	@JsonIgnore
	@NotEmpty
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "MERCHANT_LANGUAGE")
	private List<Language> languages = new ArrayList<Language>();

	@Column(name = "USE_CACHE")
	private boolean useCache = false;

	@Column(name = "STORE_TEMPLATE", length = 25)
	private String storeTemplate;

	@Column(name = "INVOICE_TEMPLATE", length = 25)
	private String invoiceTemplate;

	@Column(name = "DOMAIN_NAME", length = 80)
	private String domainName;

	@JsonIgnore
	@Column(name = "CONTINUESHOPPINGURL", length = 150)
	private String continueshoppingurl;

	@Email
	@NotEmpty
	@Column(name = "STORE_EMAIL", length = 60, nullable = false)
	private String storeEmailAddress;

	@JsonIgnore
	@Column(name = "STORE_LOGO", length = 100)
	private String storeLogo;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Currency.class)
	@JoinColumn(name = "CURRENCY_ID", nullable = false)
	private Currency currency;

	@Column(name = "CURRENCY_FORMAT_NATIONAL")
	private boolean currencyFormatNational;

	public MerchantStore() {
	}

	public boolean isUseCache() {
		System.out.println("$#4241#"); System.out.println("$#4240#"); return useCache;
	}

	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public Integer getId() {
		System.out.println("$#4242#"); return this.id;
	}

	public String getStorename() {
		System.out.println("$#4243#"); return storename;
	}

	public void setStorename(String storename) {
		this.storename = storename;
	}

	public String getStorephone() {
		System.out.println("$#4244#"); return storephone;
	}

	public void setStorephone(String storephone) {
		this.storephone = storephone;
	}

	public String getStoreaddress() {
		System.out.println("$#4245#"); return storeaddress;
	}

	public void setStoreaddress(String storeaddress) {
		this.storeaddress = storeaddress;
	}

	public String getStorecity() {
		System.out.println("$#4246#"); return storecity;
	}

	public void setStorecity(String storecity) {
		this.storecity = storecity;
	}

	public String getStorepostalcode() {
		System.out.println("$#4247#"); return storepostalcode;
	}

	public void setStorepostalcode(String storepostalcode) {
		this.storepostalcode = storepostalcode;
	}

	public Country getCountry() {
		System.out.println("$#4248#"); return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Zone getZone() {
		System.out.println("$#4249#"); return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}

	public String getStorestateprovince() {
		System.out.println("$#4250#"); return storestateprovince;
	}

	public void setStorestateprovince(String storestateprovince) {
		this.storestateprovince = storestateprovince;
	}

	public Currency getCurrency() {
		System.out.println("$#4251#"); return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public String getWeightunitcode() {
		System.out.println("$#4252#"); return weightunitcode;
	}

	public void setWeightunitcode(String weightunitcode) {
		this.weightunitcode = weightunitcode;
	}

	public String getSeizeunitcode() {
		System.out.println("$#4253#"); return seizeunitcode;
	}

	public void setSeizeunitcode(String seizeunitcode) {
		this.seizeunitcode = seizeunitcode;
	}

	public Date getInBusinessSince() {
		System.out.println("$#4254#"); return CloneUtils.clone(inBusinessSince);
	}

	public void setInBusinessSince(Date inBusinessSince) {
		this.inBusinessSince = CloneUtils.clone(inBusinessSince);
	}

	public Language getDefaultLanguage() {
		System.out.println("$#4255#"); return defaultLanguage;
	}

	public void setDefaultLanguage(Language defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

	public List<Language> getLanguages() {
		System.out.println("$#4256#"); return languages;
	}

	public void setLanguages(List<Language> languages) {
		this.languages = languages;
	}

	public String getStoreLogo() {
		System.out.println("$#4257#"); return storeLogo;
	}

	public void setStoreLogo(String storeLogo) {
		this.storeLogo = storeLogo;
	}

	public String getStoreTemplate() {
		System.out.println("$#4258#"); return storeTemplate;
	}

	public void setStoreTemplate(String storeTemplate) {
		this.storeTemplate = storeTemplate;
	}

	public String getInvoiceTemplate() {
		System.out.println("$#4259#"); return invoiceTemplate;
	}

	public void setInvoiceTemplate(String invoiceTemplate) {
		this.invoiceTemplate = invoiceTemplate;
	}

	public String getDomainName() {
		System.out.println("$#4260#"); return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getContinueshoppingurl() {
		System.out.println("$#4261#"); return continueshoppingurl;
	}

	public void setContinueshoppingurl(String continueshoppingurl) {
		this.continueshoppingurl = continueshoppingurl;
	}

	public String getStoreEmailAddress() {
		System.out.println("$#4262#"); return storeEmailAddress;
	}

	public void setStoreEmailAddress(String storeEmailAddress) {
		this.storeEmailAddress = storeEmailAddress;
	}

	public String getCode() {
		System.out.println("$#4263#"); return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDateBusinessSince(String dateBusinessSince) {
		this.dateBusinessSince = dateBusinessSince;
	}

	public String getDateBusinessSince() {
		System.out.println("$#4264#"); return dateBusinessSince;
	}

	public void setCurrencyFormatNational(boolean currencyFormatNational) {
		this.currencyFormatNational = currencyFormatNational;
	}

	public boolean isCurrencyFormatNational() {
		System.out.println("$#4266#"); System.out.println("$#4265#"); return currencyFormatNational;
	}

	@Override
	public AuditSection getAuditSection() {
		System.out.println("$#4267#"); return this.auditSection;
	}

	@Override
	public void setAuditSection(AuditSection audit) {
		this.auditSection = audit;

	}

	public MerchantStore getParent() {
		System.out.println("$#4268#"); return parent;
	}

	public void setParent(MerchantStore parent) {
		this.parent = parent;
	}

	public Set<MerchantStore> getStores() {
		System.out.println("$#4269#"); return stores;
	}

	public void setStores(Set<MerchantStore> stores) {
		this.stores = stores;
	}

	public Boolean isRetailer() {
		System.out.println("$#4271#"); System.out.println("$#4270#"); return retailer;
	}


	public void setRetailer(Boolean retailer) {
		this.retailer = retailer;
	}

}
