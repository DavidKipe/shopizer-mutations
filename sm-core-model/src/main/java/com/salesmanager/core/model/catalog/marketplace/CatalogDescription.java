package com.salesmanager.core.model.catalog.marketplace;


import com.salesmanager.core.model.common.description.Description;
import com.salesmanager.core.model.reference.language.Language;


/*@Entity
@Table(name="CATEGORY_DESCRIPTION", schema=SchemaConstant.SALESMANAGER_SCHEMA,uniqueConstraints={
		@UniqueConstraint(columnNames={
			"CATEGORY_ID",
			"LANGUAGE_ID"
		})
	}
)*/
public class CatalogDescription extends Description {

	

/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*	@ManyToOne(targetEntity = Catalog.class)
	@JoinColumn(name = "CATALOG_ID", nullable = false)*/
	private Catalog catalog;


	
	public CatalogDescription() {
	}
	
	public CatalogDescription(String name, Language language) {
		System.out.println("$#3772#"); this.setName(name);
		System.out.println("$#3773#"); this.setLanguage(language);
		System.out.println("$#3774#"); super.setId(0L);
	}

	public Catalog getCatalog() {
		System.out.println("$#3775#"); return catalog;
	}

	public void setCatalog(Catalog catalog) {
		this.catalog = catalog;
	}
	

}
