package com.salesmanager.shop.mapper.catalog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.model.catalog.catalog.CatalogCategoryEntry;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.catalog.catalog.ReadableCatalogCategoryEntry;
import com.salesmanager.shop.model.catalog.category.ReadableCategory;
import com.salesmanager.shop.populator.catalog.ReadableProductPopulator;
import com.salesmanager.shop.store.api.exception.ConversionRuntimeException;
import com.salesmanager.shop.utils.ImageFilePath;

@Component
public class ReadableCatalogCategoryEntryMapper implements Mapper<CatalogCategoryEntry, ReadableCatalogCategoryEntry> {
	
	
	@Autowired
	private ReadableCategoryMapper readableCategoryMapper;
	
	//@Autowired
	//private PricingService pricingService;
	
	@Autowired
	@Qualifier("img")
	private ImageFilePath imageUtils;

	@Override
	public ReadableCatalogCategoryEntry convert(CatalogCategoryEntry source, MerchantStore store, Language language) {
		ReadableCatalogCategoryEntry destination = new ReadableCatalogCategoryEntry();
		System.out.println("$#8343#"); return convert(source, destination, store, language);
	}

	@Override
	public ReadableCatalogCategoryEntry convert(CatalogCategoryEntry source, ReadableCatalogCategoryEntry destination, MerchantStore store,
			Language language) {
		System.out.println("$#8344#"); if(destination == null) {
			destination = new ReadableCatalogCategoryEntry();
		}
		
		try {
			
			//ReadableProductPopulator readableProductPopulator = new ReadableProductPopulator();
			//readableProductPopulator.setimageUtils(imageUtils);
			//readableProductPopulator.setPricingService(pricingService);
			
			//ReadableProduct readableProduct = readableProductPopulator.populate(source.getProduct(), store, language);
			ReadableCategory readableCategory = readableCategoryMapper.convert(source.getCategory(), store, language);
			
			System.out.println("$#8345#"); destination.setCatalog(source.getCatalog().getCode());
			
			System.out.println("$#8346#"); destination.setId(source.getId());
			System.out.println("$#8347#"); destination.setVisible(source.isVisible());
			System.out.println("$#8348#"); destination.setCategory(readableCategory);
			//destination.setProduct(readableProduct);
			System.out.println("$#8349#"); return destination;
			
		} catch (Exception e) {
			throw new ConversionRuntimeException("Error while creating ReadableCatalogEntry", e);
		}
		

	}

}
