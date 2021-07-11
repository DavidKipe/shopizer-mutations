package com.salesmanager.shop.populator.catalog;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.catalog.product.attribute.ProductOption;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.catalog.product.attribute.PersistableProductOption;
import com.salesmanager.shop.model.catalog.product.attribute.ProductOptionDescription;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.Validate;

import java.util.HashSet;
import java.util.Set;




public class PersistableProductOptionPopulator extends
		AbstractDataPopulator<PersistableProductOption, ProductOption> {
	
	private LanguageService languageService;

	public LanguageService getLanguageService() {
		System.out.println("$#9498#"); return languageService;
	}

	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}

	@Override
	public ProductOption populate(PersistableProductOption source,
			ProductOption target, MerchantStore store, Language language)
			throws ConversionException {
		System.out.println("$#9499#"); Validate.notNull(languageService, "Requires to set LanguageService");
		
		
		try {
			

			System.out.println("$#9500#"); target.setMerchantStore(store);
			System.out.println("$#9501#"); target.setProductOptionSortOrder(source.getOrder());
			System.out.println("$#9502#"); target.setCode(source.getCode());
			
			System.out.println("$#9503#"); if(!CollectionUtils.isEmpty(source.getDescriptions())) {
				Set<com.salesmanager.core.model.catalog.product.attribute.ProductOptionDescription> descriptions = new HashSet<com.salesmanager.core.model.catalog.product.attribute.ProductOptionDescription>();
				for(ProductOptionDescription desc  : source.getDescriptions()) {
					com.salesmanager.core.model.catalog.product.attribute.ProductOptionDescription description = new com.salesmanager.core.model.catalog.product.attribute.ProductOptionDescription();
					Language lang = languageService.getByCode(desc.getLanguage());
					System.out.println("$#9504#"); if(lang==null) {
						throw new ConversionException("Language is null for code " + description.getLanguage() + " use language ISO code [en, fr ...]");
					}
					System.out.println("$#9505#"); description.setLanguage(lang);
					System.out.println("$#9506#"); description.setName(desc.getName());
					System.out.println("$#9507#"); description.setTitle(desc.getTitle());
					System.out.println("$#9508#"); description.setProductOption(target);
					descriptions.add(description);
				}
				System.out.println("$#9509#"); target.setDescriptions(descriptions);
			}
		
		} catch (Exception e) {
			throw new ConversionException(e);
		}
		
		
		System.out.println("$#9510#"); return target;
	}

	@Override
	protected ProductOption createTarget() {
		return null;
	}

}
