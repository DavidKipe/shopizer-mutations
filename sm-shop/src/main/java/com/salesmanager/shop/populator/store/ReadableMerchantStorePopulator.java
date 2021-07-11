package com.salesmanager.shop.populator.store;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.constants.MeasureUnit;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.shop.model.content.ReadableImage;
import com.salesmanager.shop.model.entity.ReadableAudit;
import com.salesmanager.shop.model.references.ReadableAddress;
import com.salesmanager.shop.model.store.ReadableMerchantStore;
import com.salesmanager.shop.utils.DateUtil;
import com.salesmanager.shop.utils.ImageFilePath;

/**
 * Populates MerchantStore core entity model object
 * @author carlsamson
 *
 */
public class ReadableMerchantStorePopulator extends
		AbstractDataPopulator<MerchantStore, ReadableMerchantStore> {
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	private CountryService countryService;
	private ZoneService zoneService;
	private ImageFilePath filePath;



	@Override
	public ReadableMerchantStore populate(MerchantStore source,
			ReadableMerchantStore target, MerchantStore store, Language language)
			throws ConversionException {
		System.out.println("$#11100#"); Validate.notNull(countryService,"Must use setter for countryService");
		System.out.println("$#11101#"); Validate.notNull(zoneService,"Must use setter for zoneService");
		
		System.out.println("$#11102#"); if(target == null) {
			target = new ReadableMerchantStore();
		}
		
		System.out.println("$#11103#"); target.setId(source.getId());
		System.out.println("$#11104#"); target.setCode(source.getCode());
		System.out.println("$#11105#"); if(source.getDefaultLanguage() != null) {
			System.out.println("$#11106#"); target.setDefaultLanguage(source.getDefaultLanguage().getCode());
		}

		System.out.println("$#11107#"); target.setCurrency(source.getCurrency().getCode());
		System.out.println("$#11108#"); target.setPhone(source.getStorephone());
		
		ReadableAddress address = new ReadableAddress();
		System.out.println("$#11109#"); address.setAddress(source.getStoreaddress());
		System.out.println("$#11110#"); address.setCity(source.getStorecity());
		System.out.println("$#11111#"); if(source.getCountry()!=null) {
			try {
				System.out.println("$#11112#"); address.setCountry(source.getCountry().getIsoCode());
				Country c =countryService.getCountriesMap(language).get(source.getCountry().getIsoCode());
				System.out.println("$#11113#"); if(c!=null) {
					System.out.println("$#11114#"); address.setCountry(c.getIsoCode());
				}
			} catch (ServiceException e) {
				logger.error("Cannot get Country", e);
			}
		}
		
		System.out.println("$#11115#"); if(source.getParent() != null) {
		  ReadableMerchantStore parent = populate(source.getParent(),
            new ReadableMerchantStore(), source, language);
				System.out.println("$#11116#"); target.setParent(parent);
		}
		
		System.out.println("$#11117#"); if(target.getParent() == null) {
			System.out.println("$#11118#"); target.setRetailer(true);
		} else {
			System.out.println("$#11120#"); target.setRetailer(source.isRetailer()!=null?source.isRetailer().booleanValue():false);
		}
		
		
		System.out.println("$#11121#"); target.setDimension(MeasureUnit.valueOf(source.getSeizeunitcode()));
		System.out.println("$#11122#"); target.setWeight(MeasureUnit.valueOf(source.getWeightunitcode()));
		
		System.out.println("$#11123#"); if(source.getZone()!=null) {
			System.out.println("$#11124#"); address.setStateProvince(source.getZone().getCode());
			try {
				Zone z = zoneService.getZones(language).get(source.getZone().getCode());
				System.out.println("$#11125#"); address.setStateProvince(z.getCode());
			} catch (ServiceException e) {
				logger.error("Cannot get Zone", e);
			}
		}
		
		
		System.out.println("$#11126#"); if(!StringUtils.isBlank(source.getStorestateprovince())) {
			System.out.println("$#11127#"); address.setStateProvince(source.getStorestateprovince());
		}
		
		System.out.println("$#11128#"); if(!StringUtils.isBlank(source.getStoreLogo())) {
			ReadableImage image = new ReadableImage();
			System.out.println("$#11129#"); image.setName(source.getStoreLogo());
			System.out.println("$#11130#"); if(filePath!=null) {
				System.out.println("$#11131#"); image.setPath(filePath.buildStoreLogoFilePath(source));
			}
			System.out.println("$#11132#"); target.setLogo(image);
		}
		
		System.out.println("$#11133#"); address.setPostalCode(source.getStorepostalcode());

		System.out.println("$#11134#"); target.setAddress(address);
		
		System.out.println("$#11135#"); target.setCurrencyFormatNational(source.isCurrencyFormatNational());
		System.out.println("$#11136#"); target.setEmail(source.getStoreEmailAddress());
		System.out.println("$#11137#"); target.setName(source.getStorename());
		System.out.println("$#11138#"); target.setId(source.getId());
		System.out.println("$#11139#"); target.setInBusinessSince(DateUtil.formatDate(source.getInBusinessSince()));
		System.out.println("$#11140#"); target.setUseCache(source.isUseCache());
		
		
/*		List<Language> languages = source.getLanguages();
		if(!CollectionUtils.isEmpty(languages)) {
			
			List<String> langs = new ArrayList<String>();
			for(Language lang : languages) {
				langs.add(lang.getCode());
			}
			
			//target.setSupportedLanguages(langs);
		}*/
		
		System.out.println("$#11141#"); if(!CollectionUtils.isEmpty(source.getLanguages())) {
			List<Language> supported = new ArrayList<Language>();
			for(Language lang : source.getLanguages()) {
				supported.add(lang);
			}
			System.out.println("$#11142#"); target.setSupportedLanguages(supported);
		}
		
		System.out.println("$#11143#"); if(source.getAuditSection()!=null) {
			ReadableAudit audit = new ReadableAudit();
			System.out.println("$#11144#"); if(source.getAuditSection().getDateCreated()!=null) {
				System.out.println("$#11145#"); audit.setCreated(DateUtil.formatDate(source.getAuditSection().getDateCreated()));
			}
			System.out.println("$#11146#"); if(source.getAuditSection().getDateModified()!=null) {
				System.out.println("$#11147#"); audit.setModified(DateUtil.formatDate(source.getAuditSection().getDateCreated()));
			}
			System.out.println("$#11148#"); audit.setUser(source.getAuditSection().getModifiedBy());
			System.out.println("$#11149#"); target.setReadableAudit(audit);
		}

		System.out.println("$#11150#"); return target;
	}

	@Override
	protected ReadableMerchantStore createTarget() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public CountryService getCountryService() {
		System.out.println("$#11151#"); return countryService;
	}

	public void setCountryService(CountryService countryService) {
		this.countryService = countryService;
	}

	public ZoneService getZoneService() {
		System.out.println("$#11152#"); return zoneService;
	}

	public void setZoneService(ZoneService zoneService) {
		this.zoneService = zoneService;
	}

	public ImageFilePath getFilePath() {
		System.out.println("$#11153#"); return filePath;
	}

	public void setFilePath(ImageFilePath filePath) {
		this.filePath = filePath;
	}


}
