package com.salesmanager.shop.mapper.catalog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.salesmanager.core.model.catalog.catalog.Catalog;
import com.salesmanager.core.model.catalog.catalog.CatalogCategoryEntry;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.catalog.catalog.ReadableCatalog;
import com.salesmanager.shop.model.catalog.catalog.ReadableCatalogCategoryEntry;
import com.salesmanager.shop.model.catalog.category.ReadableCategory;
import com.salesmanager.shop.model.store.ReadableMerchantStore;
import com.salesmanager.shop.store.controller.store.facade.StoreFacade;
import com.salesmanager.shop.utils.DateUtil;

@Component
public class ReadableCatalogMapper implements Mapper<Catalog, ReadableCatalog> {
	
	@Autowired
	private StoreFacade storeFacade;
	

	@Autowired
	private ReadableCategoryMapper readableCategoryMapper;

	@Override
	public ReadableCatalog convert(Catalog source, MerchantStore store, Language language) {
		ReadableCatalog destination = new ReadableCatalog();
		System.out.println("$#8350#"); return convert(source, destination, store, language);
	}

	@Override
	public ReadableCatalog convert(Catalog source, ReadableCatalog destination, MerchantStore store,
			Language language) {
		System.out.println("$#8351#"); if(destination == null) {
			destination = new ReadableCatalog();
		}
		
		System.out.println("$#8353#"); System.out.println("$#8352#"); if(source.getId()!=null && source.getId().longValue() >0) {
			System.out.println("$#8355#"); destination.setId(source.getId());
		}
		
		System.out.println("$#8356#"); destination.setCode(source.getCode());
		System.out.println("$#8357#"); destination.setDefaultCatalog(source.isDefaultCatalog());
		System.out.println("$#8358#"); destination.setVisible(source.isVisible());
		
		System.out.println("$#8359#"); if(source.getMerchantStore() != null) {
			ReadableMerchantStore st = storeFacade.getByCode(source.getMerchantStore().getCode(), language);
			System.out.println("$#8360#"); destination.setStore(st);
		}
		
		System.out.println("$#8361#"); destination.setDefaultCatalog(source.isDefaultCatalog());
		
		System.out.println("$#8362#"); if(source.getAuditSection()!=null) {
			System.out.println("$#8363#"); destination.setCreationDate(DateUtil.formatDate(source.getAuditSection().getDateCreated()));
		}
		
		System.out.println("$#8364#"); if(!CollectionUtils.isEmpty(source.getEntry())) {
			
			//hierarchy temp object
			Map<Long, ReadableCategory> hierarchy = new HashMap<Long, ReadableCategory>();
			Map<Long, ReadableCategory> processed = new HashMap<Long, ReadableCategory>();
			
			System.out.println("$#8365#"); source.getEntry().stream().forEach(entry -> {
				System.out.println("$#8366#"); processCategory(entry.getCategory(), store, language, hierarchy, processed);
			});
			
			System.out.println("$#8367#"); destination.setCategory(hierarchy.values().stream().collect(Collectors.toList()));
		}
		
		System.out.println("$#8368#"); return destination;
		
	}
	
	/**
	 * B
	 * 	1
	 * 	  D
	 * 	2
	 * C
	 * 	1
	 * 	4
	 * A
	 * @param parent
	 * @param c
	 * @param store
	 * @param language
	 * @param hierarchy
	 */
	
	private void processCategory(Category c, MerchantStore store, Language language, Map<Long, ReadableCategory> hierarchy, Map<Long, ReadableCategory> processed ) {
		
		//build category hierarchy
		
		ReadableCategory rc = null;
		ReadableCategory rp = null;
		
		System.out.println("$#8369#"); if(! CollectionUtils.isEmpty(c.getCategories())) {
			System.out.println("$#8370#"); c.getCategories().stream().forEach(element -> {
				System.out.println("$#8371#"); this.processCategory(element, store, language, hierarchy, processed);
			});
		}

		System.out.println("$#8372#"); if(c.getParent() != null) {
			rp = hierarchy.get(c.getParent().getId());
			System.out.println("$#8373#"); if(rp == null) {
				rp = this.toReadableCategory(c.getParent(), store, language, processed);
				hierarchy.put(c.getParent().getId(), rp);
			}
		}

		rc =  this.toReadableCategory(c, store, language, processed);
		System.out.println("$#8374#"); if(rp != null) {
			rp.getChildren().add(rc);
		} else {
			hierarchy.put(c.getId(), rc);
		}

	}
	
	private ReadableCategory toReadableCategory (Category c, MerchantStore store, Language lang, Map<Long, ReadableCategory> processed) {
		System.out.println("$#8375#"); if(processed.get(c.getId()) != null) {
			System.out.println("$#8376#"); return processed.get(c.getId());
		}
		ReadableCategory readable =  readableCategoryMapper.convert(c, store, lang);
		processed.put(readable.getId(), readable);
		System.out.println("$#8377#"); return readable;
	}

}
