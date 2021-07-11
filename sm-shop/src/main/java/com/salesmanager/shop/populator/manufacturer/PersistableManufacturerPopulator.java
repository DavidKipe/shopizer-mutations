
package com.salesmanager.shop.populator.manufacturer;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.catalog.product.manufacturer.Manufacturer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.catalog.manufacturer.ManufacturerDescription;
import com.salesmanager.shop.model.catalog.manufacturer.PersistableManufacturer;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.Validate;

import java.util.HashSet;
import java.util.Set;


/**
 * @author Carl Samson
 *
 */


public class PersistableManufacturerPopulator extends AbstractDataPopulator<PersistableManufacturer, Manufacturer>
{
	
	
	private LanguageService languageService;

	@Override
	public Manufacturer populate(PersistableManufacturer source,
			Manufacturer target, MerchantStore store, Language language)
			throws ConversionException {
		
		System.out.println("$#10428#"); Validate.notNull(languageService, "Requires to set LanguageService");
		
		try {
			
			System.out.println("$#10429#"); target.setMerchantStore(store);
			System.out.println("$#10430#"); target.setCode(source.getCode());
			

			System.out.println("$#10431#"); if(!CollectionUtils.isEmpty(source.getDescriptions())) {
				Set<com.salesmanager.core.model.catalog.product.manufacturer.ManufacturerDescription> descriptions = new HashSet<com.salesmanager.core.model.catalog.product.manufacturer.ManufacturerDescription>();
				for(ManufacturerDescription description : source.getDescriptions()) {
					com.salesmanager.core.model.catalog.product.manufacturer.ManufacturerDescription desc = new com.salesmanager.core.model.catalog.product.manufacturer.ManufacturerDescription();
					System.out.println("$#10433#"); System.out.println("$#10432#"); if(desc.getId() != null && desc.getId().longValue()>0) {
						System.out.println("$#10435#"); desc.setId(description.getId());
					}
					System.out.println("$#10436#"); if(target.getDescriptions() != null) {
						for(com.salesmanager.core.model.catalog.product.manufacturer.ManufacturerDescription d : target.getDescriptions()) {
							System.out.println("$#10437#"); if(d.getLanguage().getCode().equals(description.getLanguage()) || desc.getId() != null && d.getId().longValue() == desc.getId().longValue()) {
								desc = d;
							}
						}
					}
					
					System.out.println("$#10440#"); desc.setManufacturer(target);
					System.out.println("$#10441#"); desc.setDescription(description.getDescription());
					System.out.println("$#10442#"); desc.setName(description.getName());
					Language lang = languageService.getByCode(description.getLanguage());
					System.out.println("$#10443#"); if(lang==null) {
						throw new ConversionException("Language is null for code " + description.getLanguage() + " use language ISO code [en, fr ...]");
					}
					System.out.println("$#10444#"); desc.setLanguage(lang);
					descriptions.add(desc);
				}
				System.out.println("$#10445#"); target.setDescriptions(descriptions);
			}
		
		} catch (Exception e) {
			throw new ConversionException(e);
		}
	
		
		System.out.println("$#10446#"); return target;
	}

	@Override
	protected Manufacturer createTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}

	public LanguageService getLanguageService() {
		System.out.println("$#10447#"); return languageService;
	}


}
