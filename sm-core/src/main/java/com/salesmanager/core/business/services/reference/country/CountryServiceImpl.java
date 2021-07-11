package com.salesmanager.core.business.services.reference.country;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.reference.country.CountryRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.business.utils.CacheUtils;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.country.CountryDescription;
import com.salesmanager.core.model.reference.language.Language;

@Service("countryService")
public class CountryServiceImpl extends SalesManagerEntityServiceImpl<Integer, Country>
		implements CountryService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CountryServiceImpl.class);
	
	private CountryRepository countryRepository;
	
	@Inject
	private CacheUtils cache;

	
	@Inject
	public CountryServiceImpl(CountryRepository countryRepository) {
		super(countryRepository);
		this.countryRepository = countryRepository;
	}
	
	@Cacheable("countrByCode")
	public Country getByCode(String code) throws ServiceException {
		System.out.println("$#2624#"); return countryRepository.findByIsoCode(code);
	}

	@Override
	public void addCountryDescription(Country country, CountryDescription description) throws ServiceException {
		country.getDescriptions().add(description);
		System.out.println("$#2625#"); description.setCountry(country);
		System.out.println("$#2626#"); update(country);
	}
	
	@Override
	@Cacheable("countriesMap")
	public Map<String,Country> getCountriesMap(Language language) throws ServiceException {
		
		List<Country> countries = getCountries(language);
		
		Map<String,Country> returnMap = new LinkedHashMap<String,Country>();
		
		for(Country country : countries) {
			returnMap.put(country.getIsoCode(), country);
		}
		
		System.out.println("$#2627#"); return returnMap;
	}
	
	
	@Override
	public List<Country> getCountries(final List<String> isoCodes, final Language language) throws ServiceException {
		List<Country> countryList = getCountries(language);
		List<Country> requestedCountryList = new ArrayList<Country>();
		System.out.println("$#2628#"); if(!CollectionUtils.isEmpty(countryList)) {
			for(Country c : countryList) {
				System.out.println("$#2629#"); if(isoCodes.contains(c.getIsoCode())) {
					requestedCountryList.add(c);
				}
			}
		}
		System.out.println("$#2630#"); return requestedCountryList;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Country> getCountries(Language language) throws ServiceException {
		
		List<Country> countries = null;
		try {

			countries = (List<Country>) cache.getFromCache("COUNTRIES_" + language.getCode());
			System.out.println("$#2631#"); if(countries==null) {
			
				countries = countryRepository.listByLanguage(language.getId());
			
				//set names
				for(Country country : countries) {
					
					CountryDescription description = country.getDescriptions().iterator().next();
					System.out.println("$#2632#"); country.setName(description.getName());
					
				}
				
				System.out.println("$#2633#"); cache.putInCache(countries, "COUNTRIES_" + language.getCode());
			}

		} catch (Exception e) {
			LOGGER.error("getCountries()", e);
		}
		
		System.out.println("$#2634#"); return countries;
		
		
	}

	@Override
	public List<Country> listCountryZones(Language language) throws ServiceException {
		try {
			System.out.println("$#2635#"); return countryRepository.listCountryZonesByLanguage(language.getId());
		} catch(Exception e) {
			LOGGER.error("listCountryZones", e);
			throw new ServiceException(e);
		}

	}


}
