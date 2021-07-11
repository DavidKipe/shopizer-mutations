package com.salesmanager.shop.mapper.catalog;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.salesmanager.core.model.catalog.catalog.Catalog;
import com.salesmanager.core.model.catalog.catalog.CatalogCategoryEntry;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.catalog.catalog.PersistableCatalogCategoryEntry;
import com.salesmanager.shop.store.api.exception.ConversionRuntimeException;
import com.salesmanager.shop.store.controller.catalog.facade.CatalogFacade;
import com.salesmanager.shop.store.controller.category.facade.CategoryFacade;
import com.salesmanager.shop.store.controller.product.facade.ProductFacade;

@Component
public class PersistableCatalogCategoryEntryMapper implements Mapper<PersistableCatalogCategoryEntry, CatalogCategoryEntry> {

	
	@Autowired
	private CategoryFacade categoryFacade;
	
	@Autowired
	private CatalogFacade catalogFacade;
	
	
	@Override
	public CatalogCategoryEntry convert(PersistableCatalogCategoryEntry source, MerchantStore store, Language language) {
		CatalogCategoryEntry destination = new CatalogCategoryEntry();
		System.out.println("$#8227#"); return this.convert(source, destination, store, language);
	}

	@Override
	public CatalogCategoryEntry convert(PersistableCatalogCategoryEntry source, CatalogCategoryEntry destination, MerchantStore store,
			Language language) {
		System.out.println("$#8228#"); Validate.notNull(source, "CatalogEntry must not be null");
		System.out.println("$#8229#"); Validate.notNull(store, "MerchantStore must not be null");
		System.out.println("$#8230#"); Validate.notNull(source.getProductCode(), "ProductCode must not be null");
		System.out.println("$#8231#"); Validate.notNull(source.getCategoryCode(), "CategoryCode must not be null");
		System.out.println("$#8232#"); Validate.notNull(source.getCatalog(), "Catalog must not be null");
		
		
		
		System.out.println("$#8233#"); if(destination == null) {
			destination = new CatalogCategoryEntry();
			
		}
		System.out.println("$#8234#"); destination.setId(source.getId());
		System.out.println("$#8235#"); destination.setVisible(source.isVisible());

		
		try {
			
			String catalog = source.getCatalog();
			
			Catalog catalogModel = catalogFacade.getCatalog(catalog, store);
			System.out.println("$#8236#"); if(catalogModel == null) {
				throw new ConversionRuntimeException("Error while converting CatalogEntry product [" + source.getCatalog() + "] not found");
			}
			
			System.out.println("$#8237#"); destination.setCatalog(catalogModel);

/*			Product productModel = productFacade.getProduct(source.getProductCode(), store);
			if(productModel == null) {
				throw new ConversionRuntimeException("Error while converting CatalogEntry product [" + source.getProductCode() + "] not found");
			}*/

			//destination.setProduct(productModel);
			
			Category categoryModel = categoryFacade.getByCode(source.getCategoryCode(), store);
			System.out.println("$#8238#"); if(categoryModel == null) {
				throw new ConversionRuntimeException("Error while converting CatalogEntry category [" + source.getCategoryCode() + "] not found");
			}
			
			System.out.println("$#8239#"); destination.setCategory(categoryModel);
			
		} catch (Exception e) {
			throw new ConversionRuntimeException("Error while converting CatalogEntry", e);
		}
		
		System.out.println("$#8240#"); return destination;
	}

}
