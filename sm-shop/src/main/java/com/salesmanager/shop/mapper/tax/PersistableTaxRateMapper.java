package com.salesmanager.shop.mapper.tax;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.services.tax.TaxClassService;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.tax.taxrate.TaxRate;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.tax.PersistableTaxRate;
import com.salesmanager.shop.model.tax.TaxRateDescription;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;

@Component
public class PersistableTaxRateMapper implements Mapper<PersistableTaxRate, TaxRate> {
	
	
	@Autowired
	private CountryService countryService;
	
	@Autowired
	private ZoneService zoneService;
	
	@Autowired
	private LanguageService languageService;
	
	@Autowired
	private TaxClassService taxClassService;

	@Override
	public TaxRate convert(PersistableTaxRate source, MerchantStore store, Language language) {
		TaxRate rate = new TaxRate();
		System.out.println("$#8649#"); return this.convert(source, rate, store, language);
	}

	@Override
	public TaxRate convert(PersistableTaxRate source, TaxRate destination, MerchantStore store, Language language) {
		System.out.println("$#8650#"); Validate.notNull(destination, "destination TaxRate cannot be null");
		System.out.println("$#8651#"); Validate.notNull(source, "source TaxRate cannot be null");
		try {
			System.out.println("$#8652#"); destination.setId(source.getId());
			System.out.println("$#8653#"); destination.setCode(source.getCode());
			System.out.println("$#8654#"); destination.setTaxPriority(source.getPriority());
			
			System.out.println("$#8655#"); destination.setCountry(countryService.getByCode(source.getCountry()));
			System.out.println("$#8656#"); destination.setZone(zoneService.getByCode(source.getZone()));
			System.out.println("$#8657#"); destination.setStateProvince(source.getZone());
			System.out.println("$#8658#"); destination.setMerchantStore(store);
			System.out.println("$#8659#"); destination.setTaxClass(taxClassService.getByCode(source.getTaxClass(), store));
			System.out.println("$#8660#"); destination.setTaxRate(source.getRate());
			this.taxRate(destination, source);
			
			System.out.println("$#8661#"); return destination;
		
		} catch (Exception e) {
			throw new ServiceRuntimeException("An error occured withe creating tax rate",e);
		}
		

		
		
	}
	
	private com.salesmanager.core.model.tax.taxrate.TaxRate taxRate(com.salesmanager.core.model.tax.taxrate.TaxRate destination, PersistableTaxRate source) throws Exception {
		//List<com.salesmanager.core.model.tax.taxrate.TaxRateDescription> descriptions = new ArrayList<com.salesmanager.core.model.tax.taxrate.TaxRateDescription>();
		
							System.out.println("$#8662#"); if(!CollectionUtils.isEmpty(source.getDescriptions())) {
	          for(TaxRateDescription desc : source.getDescriptions()) {
	        	com.salesmanager.core.model.tax.taxrate.TaxRateDescription description = null;
													System.out.println("$#8663#"); if(!CollectionUtils.isEmpty(destination.getDescriptions())) {
	              for(com.salesmanager.core.model.tax.taxrate.TaxRateDescription d : destination.getDescriptions()) {
																	System.out.println("$#8664#"); if(!StringUtils.isBlank(desc.getLanguage()) && desc.getLanguage().equals(d.getLanguage().getCode())) {
																		System.out.println("$#8666#"); d.setDescription(desc.getDescription());
																		System.out.println("$#8667#"); d.setName(desc.getName());
																		System.out.println("$#8668#"); d.setTitle(desc.getTitle());
	              	  description = d;
	              	  break;
	                } 
	              }
	            } 
													System.out.println("$#8669#"); if(description == null) {
	  	          description = description(desc);
														System.out.println("$#8670#"); description.setTaxRate(destination);
	  	          destination.getDescriptions().add(description);
	            }
	          }
	        }

									System.out.println("$#8671#"); return destination;

	}
	
	private com.salesmanager.core.model.tax.taxrate.TaxRateDescription description(TaxRateDescription source) throws Exception {
		
		
					System.out.println("$#8672#"); Validate.notNull(source.getLanguage(),"description.language should not be null");
	    com.salesmanager.core.model.tax.taxrate.TaxRateDescription desc = new com.salesmanager.core.model.tax.taxrate.TaxRateDescription();
					System.out.println("$#8673#"); desc.setId(null);
					System.out.println("$#8674#"); desc.setDescription(source.getDescription());
					System.out.println("$#8675#"); desc.setName(source.getName());
					System.out.println("$#8677#"); System.out.println("$#8676#"); if(source.getId() != null && source.getId().longValue()>0) {
							System.out.println("$#8679#"); desc.setId(source.getId());
	    }
	    Language lang = languageService.getByCode(source.getLanguage());
					System.out.println("$#8680#"); desc.setLanguage(lang);
					System.out.println("$#8681#"); return desc;
		

		
	}



}
