package com.salesmanager.shop.populator.catalog;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValue;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.catalog.product.attribute.PersistableProductOptionValue;
import com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValueDescription;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.Validate;

import java.util.HashSet;
import java.util.Set;



/**
 * Converts a PersistableProductOptionValue to
 * a ProductOptionValue model object
 * @author Carl Samson
 *
 */
public class PersistableProductOptionValuePopulator extends
		AbstractDataPopulator<PersistableProductOptionValue, ProductOptionValue> {

	
	private LanguageService languageService;
	
	public LanguageService getLanguageService() {
		System.out.println("$#9511#"); return languageService;
	}

	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}

	@Override
	public ProductOptionValue populate(PersistableProductOptionValue source,
			ProductOptionValue target, MerchantStore store, Language language)
			throws ConversionException {
		
		System.out.println("$#9512#"); Validate.notNull(languageService, "Requires to set LanguageService");
		
		
		try {
			

			System.out.println("$#9513#"); target.setMerchantStore(store);
			System.out.println("$#9514#"); target.setProductOptionValueSortOrder(source.getOrder());
			System.out.println("$#9515#"); target.setCode(source.getCode());
			
			System.out.println("$#9516#"); if(!CollectionUtils.isEmpty(source.getDescriptions())) {
				Set<com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription> descriptions = new HashSet<com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription>();
				for(ProductOptionValueDescription desc  : source.getDescriptions()) {
					com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription description = new com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription();
					Language lang = languageService.getByCode(desc.getLanguage());
					System.out.println("$#9517#"); if(lang==null) {
						throw new ConversionException("Language is null for code " + description.getLanguage() + " use language ISO code [en, fr ...]");
					}
					System.out.println("$#9518#"); description.setLanguage(lang);
					System.out.println("$#9519#"); description.setName(desc.getName());
					System.out.println("$#9520#"); description.setTitle(desc.getTitle());
					System.out.println("$#9521#"); description.setProductOptionValue(target);
					descriptions.add(description);
				}
				System.out.println("$#9522#"); target.setDescriptions(descriptions);
			}
		
		} catch (Exception e) {
			throw new ConversionException(e);
		}
		
		
		System.out.println("$#9523#"); return target;
	}

	@Override
	protected ProductOptionValue createTarget() {
		return null;
	}

}
