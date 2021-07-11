package com.salesmanager.shop.populator.store;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;

import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.merchant.MerchantStoreService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.currency.CurrencyService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.currency.Currency;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.shop.model.references.PersistableAddress;
import com.salesmanager.shop.model.store.PersistableMerchantStore;
import com.salesmanager.shop.utils.DateUtil;

@Component
public class PersistableMerchantStorePopulator extends AbstractDataPopulator<PersistableMerchantStore, MerchantStore> {

	@Inject
	private CountryService countryService;
	@Inject
	private ZoneService zoneService;
	@Inject
	private LanguageService languageService;
	@Inject
	private CurrencyService currencyService;
	@Inject
	private MerchantStoreService merchantStoreService;
	
	
	@Override
	public MerchantStore populate(PersistableMerchantStore source, MerchantStore target, MerchantStore store,
			Language language) throws ConversionException {

		Validate.notNull(source, "PersistableMerchantStore mst not be null");
		
		System.out.println("$#11059#"); if(target == null) {
			target = new MerchantStore();
		}
		
		System.out.println("$#11060#"); target.setCode(source.getCode());
		System.out.println("$#11061#"); if(source.getId()!=0) {
			System.out.println("$#11062#"); target.setId(source.getId());
		}
		
		System.out.println("$#11063#"); if(!StringUtils.isEmpty(source.getInBusinessSince())) {
			try {
				Date dt = DateUtil.getDate(source.getInBusinessSince());
				System.out.println("$#11064#"); target.setInBusinessSince(dt);
			} catch(Exception e) {
				throw new ConversionException("Cannot parse date [" + source.getInBusinessSince() + "]",e);
			}
		}

		System.out.println("$#11065#"); if(source.getDimension()!=null) {
				System.out.println("$#11066#"); target.setSeizeunitcode(source.getDimension().name());
		}
		System.out.println("$#11067#"); if(source.getWeight()!=null) {
				System.out.println("$#11068#"); target.setWeightunitcode(source.getWeight().name());
		}
		System.out.println("$#11069#"); target.setCurrencyFormatNational(source.isCurrencyFormatNational());
		System.out.println("$#11070#"); target.setStorename(source.getName());
		System.out.println("$#11071#"); target.setStorephone(source.getPhone());
		System.out.println("$#11072#"); target.setStoreEmailAddress(source.getEmail());
		System.out.println("$#11073#"); target.setUseCache(source.isUseCache());
		System.out.println("$#11074#"); target.setRetailer(source.isRetailer());
		
		//get parent store
		System.out.println("$#11075#"); if(!StringUtils.isBlank(source.getRetailerStore())) {
				System.out.println("$#11076#"); if(source.getRetailerStore().equals(source.getCode())) {
		    throw new ConversionException("Parent store [" + source.getRetailerStore() + "] cannot be parent of current store");
		  }
		  try {
            MerchantStore parent = merchantStoreService.getByCode(source.getRetailerStore());
												System.out.println("$#11077#"); if(parent == null) {
              throw new ConversionException("Parent store [" + source.getRetailerStore() + "] does not exist");
            }
												System.out.println("$#11078#"); target.setParent(parent);
          } catch (ServiceException e) {
              throw new ConversionException(e);
          }
		}
		
		
		try {
			
			System.out.println("$#11079#"); if(!StringUtils.isEmpty(source.getDefaultLanguage())) {
				Language l = languageService.getByCode(source.getDefaultLanguage());
				System.out.println("$#11080#"); target.setDefaultLanguage(l);
			}
			
			System.out.println("$#11081#"); if(!StringUtils.isEmpty(source.getCurrency())) {
				Currency c = currencyService.getByCode(source.getCurrency());
				System.out.println("$#11082#"); target.setCurrency(c);
			} else {
				System.out.println("$#11083#"); target.setCurrency(currencyService.getByCode(Constants.DEFAULT_CURRENCY.getCurrencyCode()));
			}
			
			List<String> languages = source.getSupportedLanguages();
			System.out.println("$#11084#"); if(!CollectionUtils.isEmpty(languages)) {
				for(String lang : languages) {
					Language ll = languageService.getByCode(lang);
					target.getLanguages().add(ll);
				}
			}
			
		} catch(Exception e) {
			throw new ConversionException(e);
		}
		
		//address population
		PersistableAddress address = source.getAddress();
		System.out.println("$#11085#"); if(address != null) {
			Country country;
			try {
				country = countryService.getByCode(address.getCountry());

				Zone zone = zoneService.getByCode(address.getStateProvince());
				System.out.println("$#11086#"); if(zone != null) {
					System.out.println("$#11087#"); target.setZone(zone);
				} else {
					System.out.println("$#11088#"); target.setStorestateprovince(address.getStateProvince());
				}
				
				System.out.println("$#11089#"); target.setStoreaddress(address.getAddress());
				System.out.println("$#11090#"); target.setStorecity(address.getCity());
				System.out.println("$#11091#"); target.setCountry(country);
				System.out.println("$#11092#"); target.setStorepostalcode(address.getPostalCode());
				
			} catch (ServiceException e) {
				throw new ConversionException(e);
			}
		}

		System.out.println("$#11093#"); if (StringUtils.isNotEmpty(source.getTemplate()))
			System.out.println("$#11094#"); target.setStoreTemplate(source.getTemplate());
		
		System.out.println("$#11095#"); return target;
	}

	@Override
	protected MerchantStore createTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	public ZoneService getZoneService() {
		System.out.println("$#11096#"); return zoneService;
	}

	public void setZoneService(ZoneService zoneService) {
		this.zoneService = zoneService;
	}
	public CountryService getCountryService() {
		System.out.println("$#11097#"); return countryService;
	}

	public void setCountryService(CountryService countryService) {
		this.countryService = countryService;
	}

	public LanguageService getLanguageService() {
		System.out.println("$#11098#"); return languageService;
	}

	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}

	public CurrencyService getCurrencyService() {
		System.out.println("$#11099#"); return currencyService;
	}

	public void setCurrencyService(CurrencyService currencyService) {
		this.currencyService = currencyService;
	}


}
