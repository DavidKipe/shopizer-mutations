package com.salesmanager.core.business.services.reference.zone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.reference.zone.ZoneRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.business.utils.CacheUtils;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.core.model.reference.zone.ZoneDescription;

@Service("zoneService")
public class ZoneServiceImpl extends SalesManagerEntityServiceImpl<Long, Zone> implements
		ZoneService {
	
	private final static String ZONE_CACHE_PREFIX = "ZONES_";

	private ZoneRepository zoneRepository;
	
	@Inject
	private CacheUtils cache;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ZoneServiceImpl.class);

	@Inject
	public ZoneServiceImpl(ZoneRepository zoneRepository) {
		super(zoneRepository);
		this.zoneRepository = zoneRepository;
	}

	@Override
	@Cacheable("zoneByCode")
	public Zone getByCode(String code) {
		System.out.println("$#2808#"); return zoneRepository.findByCode(code);
	}

	@Override
	public void addDescription(Zone zone, ZoneDescription description) throws ServiceException {
		System.out.println("$#2809#"); if (zone.getDescriptions()!=null) {
				System.out.println("$#2810#"); if(!zone.getDescriptions().contains(description)) {
					zone.getDescriptions().add(description);
					System.out.println("$#2811#"); update(zone);
				}
		} else {
			List<ZoneDescription> descriptions = new ArrayList<ZoneDescription>();
			descriptions.add(description);
			System.out.println("$#2812#"); zone.setDescriptons(descriptions);
			System.out.println("$#2813#"); update(zone);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Zone> getZones(Country country, Language language) throws ServiceException {
		
		//Validate.notNull(country,"Country cannot be null");
		System.out.println("$#2814#"); Validate.notNull(language,"Language cannot be null");
		
		List<Zone> zones = null;
		try {
			
			String countryCode = Constants.DEFAULT_COUNTRY;
			System.out.println("$#2815#"); if(country!=null) {
				countryCode = country.getIsoCode();
			}

			String cacheKey = ZONE_CACHE_PREFIX + countryCode + Constants.UNDERSCORE + language.getCode();
			
			zones = (List<Zone>) cache.getFromCache(cacheKey);

		
		
			System.out.println("$#2816#"); if(zones==null) {
			
				zones = zoneRepository.listByLanguageAndCountry(countryCode, language.getId());
			
				//set names
				for(Zone zone : zones) {
					ZoneDescription description = zone.getDescriptions().get(0);
					System.out.println("$#2817#"); zone.setName(description.getName());
					
				}
				System.out.println("$#2818#"); cache.putInCache(zones, cacheKey);
			}

		} catch (Exception e) {
			LOGGER.error("getZones()", e);
		}
		System.out.println("$#2819#"); return zones;
		
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Zone> getZones(String countryCode, Language language) throws ServiceException {
		
		System.out.println("$#2820#"); Validate.notNull(countryCode,"countryCode cannot be null");
		System.out.println("$#2821#"); Validate.notNull(language,"Language cannot be null");
		
		List<Zone> zones = null;
		try {
			

			String cacheKey = ZONE_CACHE_PREFIX + countryCode + Constants.UNDERSCORE + language.getCode();
			
			zones = (List<Zone>) cache.getFromCache(cacheKey);

		
		
			System.out.println("$#2822#"); if(zones==null) {
			
				zones = zoneRepository.listByLanguageAndCountry(countryCode, language.getId());
			
				//set names
				for(Zone zone : zones) {
					ZoneDescription description = zone.getDescriptions().get(0);
					System.out.println("$#2823#"); zone.setName(description.getName());
					
				}
				System.out.println("$#2824#"); cache.putInCache(zones, cacheKey);
			}

		} catch (Exception e) {
			LOGGER.error("getZones()", e);
		}
		System.out.println("$#2825#"); return zones;
		
		
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Zone> getZones(Language language) throws ServiceException {
		
		Map<String, Zone> zones = null;
		try {

			String cacheKey = ZONE_CACHE_PREFIX + language.getCode();
			
			zones = (Map<String, Zone>) cache.getFromCache(cacheKey);

		
		
			System.out.println("$#2826#"); if(zones==null) {
				zones = new HashMap<String, Zone>();
				List<Zone> zns = zoneRepository.listByLanguage(language.getId());
			
				//set names
				for(Zone zone : zns) {
					ZoneDescription description = zone.getDescriptions().get(0);
					System.out.println("$#2827#"); zone.setName(description.getName());
					zones.put(zone.getCode(), zone);
					
				}
				System.out.println("$#2828#"); cache.putInCache(zones, cacheKey);
			}

		} catch (Exception e) {
			LOGGER.error("getZones()", e);
		}
		System.out.println("$#2829#"); return zones;
		
		
	}

}
