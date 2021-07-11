package com.salesmanager.core.model.common.description;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotEmpty;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.salesmanager.core.model.common.audit.AuditListener;
import com.salesmanager.core.model.common.audit.AuditSection;
import com.salesmanager.core.model.common.audit.Auditable;
import com.salesmanager.core.model.reference.language.Language;

@MappedSuperclass
@EntityListeners(value = AuditListener.class)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Description implements Auditable, Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "DESCRIPTION_ID")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "description_gen")
	private Long id;
	
	@JsonIgnore
	@Embedded
	private AuditSection auditSection = new AuditSection();
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "LANGUAGE_ID")
	private Language language;
	
	@NotEmpty
	@Column(name="NAME", nullable = false, length=120)
	private String name;
	
	@Column(name="TITLE", length=100)
	private String title;
	
	@Column(name="DESCRIPTION")
	@Type(type = "org.hibernate.type.TextType")
	private String description;
	
	public Description() {
	}
	
	public Description(Language language, String name) {
		System.out.println("$#4068#"); this.setLanguage(language);
		System.out.println("$#4069#"); this.setName(name);
	}
	
	@Override
	public AuditSection getAuditSection() {
		System.out.println("$#4070#"); return auditSection;
	}

	@Override
	public void setAuditSection(AuditSection auditSection) {
		this.auditSection = auditSection;
	}

	public Language getLanguage() {
		System.out.println("$#4071#"); return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public String getName() {
		System.out.println("$#4072#"); return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		System.out.println("$#4073#"); return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		System.out.println("$#4074#"); return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getId() {
		System.out.println("$#4075#"); return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
