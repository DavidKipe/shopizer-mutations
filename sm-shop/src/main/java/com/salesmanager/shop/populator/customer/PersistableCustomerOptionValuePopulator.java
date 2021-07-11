package com.salesmanager.shop.populator.customer;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.customer.attribute.CustomerOptionValue;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.customer.attribute.CustomerOptionValueDescription;
import com.salesmanager.shop.model.customer.attribute.PersistableCustomerOptionValue;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.Validate;

import java.util.HashSet;
import java.util.Set;

public class PersistableCustomerOptionValuePopulator extends
		AbstractDataPopulator<PersistableCustomerOptionValue, CustomerOptionValue> {

	
	private LanguageService languageService;
	
	@Override
	public CustomerOptionValue populate(PersistableCustomerOptionValue source,
			CustomerOptionValue target, MerchantStore store, Language language)
			throws ConversionException {
		
		
		System.out.println("$#10177#"); Validate.notNull(languageService, "Requires to set LanguageService");
		
		
		try {
			
			System.out.println("$#10178#"); target.setCode(source.getCode());
			System.out.println("$#10179#"); target.setMerchantStore(store);
			System.out.println("$#10180#"); target.setSortOrder(source.getOrder());
			
			System.out.println("$#10181#"); if(!CollectionUtils.isEmpty(source.getDescriptions())) {
				Set<com.salesmanager.core.model.customer.attribute.CustomerOptionValueDescription> descriptions = new HashSet<com.salesmanager.core.model.customer.attribute.CustomerOptionValueDescription>();
				for(CustomerOptionValueDescription desc  : source.getDescriptions()) {
					com.salesmanager.core.model.customer.attribute.CustomerOptionValueDescription description = new com.salesmanager.core.model.customer.attribute.CustomerOptionValueDescription();
					Language lang = languageService.getByCode(desc.getLanguage());
					System.out.println("$#10182#"); if(lang==null) {
						throw new ConversionException("Language is null for code " + description.getLanguage() + " use language ISO code [en, fr ...]");
					}
					System.out.println("$#10183#"); description.setLanguage(lang);
					System.out.println("$#10184#"); description.setName(desc.getName());
					System.out.println("$#10185#"); description.setTitle(desc.getTitle());
					System.out.println("$#10186#"); description.setCustomerOptionValue(target);
					descriptions.add(description);
				}
				System.out.println("$#10187#"); target.setDescriptions(descriptions);
			}
			
		} catch (Exception e) {
			throw new ConversionException(e);
		}
		System.out.println("$#10188#"); return target;
	}

	@Override
	protected CustomerOptionValue createTarget() {
		return null;
	}

	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}

	public LanguageService getLanguageService() {
		System.out.println("$#10189#"); return languageService;
	}

}
