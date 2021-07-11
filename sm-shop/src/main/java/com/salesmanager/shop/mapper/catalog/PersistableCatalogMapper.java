package com.salesmanager.shop.mapper.catalog;

import org.springframework.stereotype.Component;

import com.salesmanager.core.model.catalog.catalog.Catalog;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.catalog.catalog.PersistableCatalog;

@Component
public class PersistableCatalogMapper implements Mapper<PersistableCatalog, Catalog> {

	@Override
	public Catalog convert(PersistableCatalog source, MerchantStore store, Language language) {
		Catalog c = new Catalog();
		System.out.println("$#8241#"); return this.convert(source, c, store, language);
	}

	@Override
	public Catalog convert(PersistableCatalog source, Catalog destination, MerchantStore store, Language language) {
		
		
		System.out.println("$#8242#"); destination.setCode(source.getCode());
		System.out.println("$#8243#"); destination.setDefaultCatalog(source.isDefaultCatalog());
		System.out.println("$#8244#"); destination.setId(source.getId());
		System.out.println("$#8245#"); destination.setMerchantStore(store);
		System.out.println("$#8246#"); destination.setVisible(source.isVisible());
		
		System.out.println("$#8247#"); return destination;
	}

}
