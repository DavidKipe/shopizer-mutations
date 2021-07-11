package com.salesmanager.core.model.reference.language;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.salesmanager.core.constants.SchemaConstant;
import com.salesmanager.core.model.common.audit.AuditListener;
import com.salesmanager.core.model.common.audit.AuditSection;
import com.salesmanager.core.model.common.audit.Auditable;
import com.salesmanager.core.model.generic.SalesManagerEntity;
import com.salesmanager.core.model.merchant.MerchantStore;

@Entity
@EntityListeners(value = AuditListener.class)
@Table(name = "LANGUAGE", schema = SchemaConstant.SALESMANAGER_SCHEMA, indexes = {@Index(name = "CODE_IDX2", columnList = "CODE")})
@Cacheable
public class Language extends SalesManagerEntity<Integer, Language> implements Auditable {

	private static final long serialVersionUID = 1L;


	@Id
	@Column(name = "LANGUAGE_ID")
	@TableGenerator(name = "TABLE_GEN", table = "SM_SEQUENCER", pkColumnName = "SEQ_NAME",
			valueColumnName = "SEQ_COUNT", pkColumnValue = "LANG_SEQ_NEXT_VAL")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN")
	private Integer id;

	@JsonIgnore
	@Embedded
	private AuditSection auditSection = new AuditSection();

	@Column(name = "CODE", nullable = false)
	private String code;

	@JsonIgnore
	@Column(name = "SORT_ORDER")
	private Integer sortOrder;

	@JsonIgnore
	@OneToMany(mappedBy = "defaultLanguage", targetEntity = MerchantStore.class)
	private List<MerchantStore> storesDefaultLanguage;

	@JsonIgnore
	@ManyToMany(mappedBy = "languages", targetEntity = MerchantStore.class, fetch = FetchType.LAZY)
	private List<MerchantStore> stores = new ArrayList<MerchantStore>();

	public Language() {
	}

	public Language(String code) {
		System.out.println("$#4476#");
		this.setCode(code);
	}

	@Override
	public Integer getId() {
		System.out.println("$#4477#");
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}


	public String getCode() {
		System.out.println("$#4478#");
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getSortOrder() {
		System.out.println("$#4479#");
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public AuditSection getAuditSection() {
		System.out.println("$#4480#");
		return auditSection;
	}

	@Override
	public void setAuditSection(AuditSection auditSection) {
		this.auditSection = auditSection;
	}

	@Override
	public boolean equals(Object obj) {
		System.out.println("$#4481#");
		if (null == obj) {
			System.out.println("$#4482#");
			return false;
		}
		System.out.println("$#4483#");
		if (!(obj instanceof Language)) {
			System.out.println("$#4484#");
			return false;
		} else {
			Language language = (Language) obj;
			System.out.println("$#4486#");
			System.out.println("$#4485#");
			return (this.id == language.getId());
		}
	}

}
