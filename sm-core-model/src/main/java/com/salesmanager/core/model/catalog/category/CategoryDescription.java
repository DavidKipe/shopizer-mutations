package com.salesmanager.core.model.catalog.category;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.salesmanager.core.constants.SchemaConstant;
import com.salesmanager.core.model.common.description.Description;
import com.salesmanager.core.model.reference.language.Language;


@Entity
@Table(name="CATEGORY_DESCRIPTION", schema=SchemaConstant.SALESMANAGER_SCHEMA,uniqueConstraints={
		@UniqueConstraint(columnNames={
			"CATEGORY_ID",
			"LANGUAGE_ID"
		})
	}
)
@TableGenerator(name = "description_gen", table = "SM_SEQUENCER", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT", pkColumnValue = "category_description_seq", allocationSize = SchemaConstant.DESCRIPTION_ID_ALLOCATION_SIZE, initialValue = SchemaConstant.DESCRIPTION_ID_START_VALUE)
public class CategoryDescription extends Description {
	private static final long serialVersionUID = 1L;
	
	@JsonIgnore
	@ManyToOne(targetEntity = Category.class)
	@JoinColumn(name = "CATEGORY_ID", nullable = false)
	private Category category;

	@Column(name="SEF_URL", length=120)
	private String seUrl;
	
	@Column(name = "CATEGORY_HIGHLIGHT")
	private String categoryHighlight;

	public String getCategoryHighlight() {
		System.out.println("$#3758#"); return categoryHighlight;
	}

	public void setCategoryHighlight(String categoryHighlight) {
		this.categoryHighlight = categoryHighlight;
	}

	@Column(name="META_TITLE", length=120)
	private String metatagTitle;
	
	@Column(name="META_KEYWORDS")
	private String metatagKeywords;
	
	@Column(name="META_DESCRIPTION")
	private String metatagDescription;
	
	public CategoryDescription() {
	}
	
	public CategoryDescription(String name, Language language) {
		System.out.println("$#3759#"); this.setName(name);
		System.out.println("$#3760#"); this.setLanguage(language);
		System.out.println("$#3761#"); super.setId(0L);
	}
	
	public String getSeUrl() {
		System.out.println("$#3762#"); return seUrl;
	}

	public void setSeUrl(String seUrl) {
		this.seUrl = seUrl;
	}

	public String getMetatagTitle() {
		System.out.println("$#3763#"); return metatagTitle;
	}

	public void setMetatagTitle(String metatagTitle) {
		this.metatagTitle = metatagTitle;
	}

	public String getMetatagKeywords() {
		System.out.println("$#3764#"); return metatagKeywords;
	}

	public void setMetatagKeywords(String metatagKeywords) {
		this.metatagKeywords = metatagKeywords;
	}

	public String getMetatagDescription() {
		System.out.println("$#3765#"); return metatagDescription;
	}

	public void setMetatagDescription(String metatagDescription) {
		this.metatagDescription = metatagDescription;
	}

	public Category getCategory() {
		System.out.println("$#3766#"); return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
}
