package com.salesmanager.core.model.content;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import com.salesmanager.core.constants.SchemaConstant;
import com.salesmanager.core.model.common.description.Description;
import com.salesmanager.core.model.reference.language.Language;

@Entity
@Table(name="CONTENT_DESCRIPTION", schema= SchemaConstant.SALESMANAGER_SCHEMA,uniqueConstraints={
		@UniqueConstraint(columnNames={
			"CONTENT_ID",
			"LANGUAGE_ID"
		})
	}
)

@TableGenerator(name = "description_gen", table = "SM_SEQUENCER", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT", pkColumnValue = "content_description_seq", allocationSize = SchemaConstant.DESCRIPTION_ID_ALLOCATION_SIZE, initialValue = SchemaConstant.DESCRIPTION_ID_START_VALUE)
//@SequenceGenerator(name = "description_gen", sequenceName = "content_description_seq", allocationSize = SchemaConstant.DESCRIPTION_ID_SEQUENCE_START)
public class ContentDescription extends Description implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne(targetEntity = Content.class)
	@JoinColumn(name = "CONTENT_ID", nullable = false)
	private Content content;

	@Column(name="SEF_URL", length=120)
	private String seUrl;

	
	@Column(name="META_KEYWORDS")
	private String metatagKeywords;
	
	@Column(name="META_TITLE")
	private String metatagTitle;
	
	public String getMetatagTitle() {
		System.out.println("$#4103#"); return metatagTitle;
	}

	public void setMetatagTitle(String metatagTitle) {
		this.metatagTitle = metatagTitle;
	}

	@Column(name="META_DESCRIPTION")
	private String metatagDescription;
	
	public ContentDescription() {
	}
	
	public ContentDescription(String name, Language language) {
		System.out.println("$#4104#"); this.setName(name);
		System.out.println("$#4105#"); this.setLanguage(language);
		System.out.println("$#4106#"); super.setId(0L);
	}

	public Content getContent() {
		System.out.println("$#4107#"); return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public String getSeUrl() {
		System.out.println("$#4108#"); return seUrl;
	}

	public void setSeUrl(String seUrl) {
		this.seUrl = seUrl;
	}


	public String getMetatagKeywords() {
		System.out.println("$#4109#"); return metatagKeywords;
	}

	public void setMetatagKeywords(String metatagKeywords) {
		this.metatagKeywords = metatagKeywords;
	}

	public String getMetatagDescription() {
		System.out.println("$#4110#"); return metatagDescription;
	}

	public void setMetatagDescription(String metatagDescription) {
		this.metatagDescription = metatagDescription;
	}

}
