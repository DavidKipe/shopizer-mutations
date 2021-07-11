package com.salesmanager.shop.populator.customer;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.customer.attribute.CustomerOption;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.customer.attribute.CustomerOptionDescription;
import com.salesmanager.shop.model.customer.attribute.PersistableCustomerOption;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

public class PersistableCustomerOptionPopulator extends
		AbstractDataPopulator<PersistableCustomerOption, CustomerOption> {

	
	private LanguageService languageService;
	
	@Override
	public CustomerOption populate(PersistableCustomerOption source,
			CustomerOption target, MerchantStore store, Language language)
			throws ConversionException {
		
		
		System.out.println("$#10160#"); Validate.notNull(languageService, "Requires to set LanguageService");
		
		
		try {
			
			System.out.println("$#10161#"); target.setCode(source.getCode());
			System.out.println("$#10162#"); target.setMerchantStore(store);
			System.out.println("$#10163#"); target.setSortOrder(source.getOrder());
			System.out.println("$#10164#"); if(!StringUtils.isBlank(source.getType())) {
				System.out.println("$#10165#"); target.setCustomerOptionType(source.getType());
			} else {
				System.out.println("$#10166#"); target.setCustomerOptionType("TEXT");
			}
			System.out.println("$#10167#"); target.setPublicOption(true);
			
			System.out.println("$#10168#"); if(!CollectionUtils.isEmpty(source.getDescriptions())) {
				Set<com.salesmanager.core.model.customer.attribute.CustomerOptionDescription> descriptions = new HashSet<com.salesmanager.core.model.customer.attribute.CustomerOptionDescription>();
				for(CustomerOptionDescription desc  : source.getDescriptions()) {
					com.salesmanager.core.model.customer.attribute.CustomerOptionDescription description = new com.salesmanager.core.model.customer.attribute.CustomerOptionDescription();
					Language lang = languageService.getByCode(desc.getLanguage());
					System.out.println("$#10169#"); if(lang==null) {
						throw new ConversionException("Language is null for code " + description.getLanguage() + " use language ISO code [en, fr ...]");
					}
					System.out.println("$#10170#"); description.setLanguage(lang);
					System.out.println("$#10171#"); description.setName(desc.getName());
					System.out.println("$#10172#"); description.setTitle(desc.getTitle());
					System.out.println("$#10173#"); description.setCustomerOption(target);
					descriptions.add(description);
				}
				System.out.println("$#10174#"); target.setDescriptions(descriptions);
			}
			
		} catch (Exception e) {
			throw new ConversionException(e);
		}
		System.out.println("$#10175#"); return target;
	}

	@Override
	protected CustomerOption createTarget() {
		return null;
	}

	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}

	public LanguageService getLanguageService() {
		System.out.println("$#10176#"); return languageService;
	}

}
