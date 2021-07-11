package com.salesmanager.shop.store.controller.store.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.salesmanager.shop.populator.store.ReadableMerchantStorePopulatorWithDetails;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.drools.core.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.content.ContentService;
import com.salesmanager.core.business.services.merchant.MerchantStoreService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.services.system.MerchantConfigurationService;
import com.salesmanager.core.constants.MeasureUnit;
import com.salesmanager.core.model.common.GenericEntityList;
import com.salesmanager.core.model.content.InputContentFile;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.merchant.MerchantStoreCriteria;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.system.MerchantConfiguration;
import com.salesmanager.core.model.system.MerchantConfigurationType;
import com.salesmanager.shop.model.content.ReadableImage;
import com.salesmanager.shop.model.store.MerchantConfigEntity;
import com.salesmanager.shop.model.store.PersistableBrand;
import com.salesmanager.shop.model.store.PersistableMerchantStore;
import com.salesmanager.shop.model.store.ReadableBrand;
import com.salesmanager.shop.model.store.ReadableMerchantStore;
import com.salesmanager.shop.model.store.ReadableMerchantStoreList;
import com.salesmanager.shop.populator.store.PersistableMerchantStorePopulator;
import com.salesmanager.shop.populator.store.ReadableMerchantStorePopulator;
import com.salesmanager.shop.store.api.exception.ConversionRuntimeException;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.utils.ImageFilePath;
import com.salesmanager.shop.utils.LanguageUtils;

@Service("storeFacade")
public class StoreFacadeImpl implements StoreFacade {

	@Inject
	private MerchantStoreService merchantStoreService;

	@Inject
	private MerchantConfigurationService merchantConfigurationService;

	@Inject
	private LanguageService languageService;

	@Inject
	private CountryService countryService;

	@Inject
	private ZoneService zoneService;

	@Inject
	private ContentService contentService;

	@Inject
	private PersistableMerchantStorePopulator persistableMerchantStorePopulator;

	@Inject
	@Qualifier("img")
	private ImageFilePath imageUtils;

	@Inject
	private LanguageUtils languageUtils;

	private static final Logger LOG = LoggerFactory.getLogger(StoreFacadeImpl.class);

	@Override
	public MerchantStore getByCode(HttpServletRequest request) {
		String code = request.getParameter("store");
		System.out.println("$#14072#"); if (StringUtils.isEmpty(code)) {
			code = com.salesmanager.core.business.constants.Constants.DEFAULT_STORE;
		}
		System.out.println("$#14073#"); return get(code);
	}

	@Override
	public MerchantStore get(String code) {
		try {
			System.out.println("$#14074#"); return merchantStoreService.getByCode(code);
		} catch (ServiceException e) {
			LOG.error("Error while getting MerchantStore", e);
			throw new ServiceRuntimeException(e);
		}

	}

	@Override
	public ReadableMerchantStore getByCode(String code, String lang) {
		Language language = getLanguage(lang);
		System.out.println("$#14075#"); return getByCode(code, language);
	}

	@Override
	public ReadableMerchantStore getFullByCode(String code, String lang) {
		Language language = getLanguage(lang);
		System.out.println("$#14076#"); return getFullByCode(code, language);
	}

	private Language getLanguage(String lang) {
		System.out.println("$#14077#"); return languageUtils.getServiceLanguage(lang);
	}

	@Override
	public ReadableMerchantStore getByCode(String code, Language language) {
		MerchantStore store = getMerchantStoreByCode(code);
		System.out.println("$#14078#"); return convertMerchantStoreToReadableMerchantStore(language, store);
	}

	@Override
	public ReadableMerchantStore getFullByCode(String code, Language language) {
		MerchantStore store = getMerchantStoreByCode(code);
		System.out.println("$#14079#"); return convertMerchantStoreToReadableMerchantStoreWithFullDetails(language, store);
	}

	@Override
	public boolean existByCode(String code) {
		try {
			System.out.println("$#14081#"); System.out.println("$#14080#"); return merchantStoreService.getByCode(code) != null;
		} catch (ServiceException e) {
			throw new ServiceRuntimeException(e);
		}
	}

	private ReadableMerchantStore convertMerchantStoreToReadableMerchantStore(Language language, MerchantStore store) {
		ReadableMerchantStore readable = new ReadableMerchantStore();

		ReadableMerchantStorePopulator populator = new ReadableMerchantStorePopulator();
		System.out.println("$#14082#"); populator.setCountryService(countryService);
		System.out.println("$#14083#"); populator.setZoneService(zoneService);
		System.out.println("$#14084#"); populator.setFilePath(imageUtils);

		/**
		 * Language is not important for this conversion using default language
		 */
		try {			readable = populator.populate(store, readable, store, language);
		} catch (Exception e) {
			throw new ConversionRuntimeException("Error while populating MerchantStore " + e.getMessage());
		}
		System.out.println("$#14085#"); return readable;
	}

	private ReadableMerchantStore convertMerchantStoreToReadableMerchantStoreWithFullDetails(Language language, MerchantStore store) {
		ReadableMerchantStore readable = new ReadableMerchantStore();

		ReadableMerchantStorePopulatorWithDetails populator = new ReadableMerchantStorePopulatorWithDetails();
		System.out.println("$#14086#"); populator.setCountryService(countryService);
		System.out.println("$#14087#"); populator.setZoneService(zoneService);
		System.out.println("$#14088#"); populator.setFilePath(imageUtils);

		/**
		 * Language is not important for this conversion using default language
		 */
		try {
			readable = populator.populate(store, readable, store, language);
		} catch (Exception e) {
			throw new ConversionRuntimeException("Error while populating MerchantStore " + e.getMessage());
		}
		System.out.println("$#14089#"); return readable;
	}

	private MerchantStore getMerchantStoreByCode(String code) {
		System.out.println("$#14090#"); return Optional.ofNullable(get(code))
				.orElseThrow(() -> new ResourceNotFoundException("Merchant store code [" + code + "] not found"));
	}

	@Override
	public void create(PersistableMerchantStore store) {

		System.out.println("$#14092#"); Validate.notNull(store, "PersistableMerchantStore must not be null");
		System.out.println("$#14093#"); Validate.notNull(store.getCode(), "PersistableMerchantStore.code must not be null");

		// check if store code exists
		MerchantStore storeForCheck = get(store.getCode());
		System.out.println("$#14094#"); if (storeForCheck != null) {
			throw new ServiceRuntimeException("MerhantStore " + store.getCode() + " already exists");
		}

		MerchantStore mStore = convertPersistableMerchantStoreToMerchantStore(store, languageService.defaultLanguage());
		System.out.println("$#14095#"); createMerchantStore(mStore);

	}

	private void createMerchantStore(MerchantStore mStore) {
		try {
			System.out.println("$#14096#"); merchantStoreService.create(mStore);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException(e);
		}
	}

	private MerchantStore convertPersistableMerchantStoreToMerchantStore(PersistableMerchantStore store,
			Language language) {
		MerchantStore mStore = new MerchantStore();

		// set default values
		System.out.println("$#14097#"); mStore.setWeightunitcode(MeasureUnit.KG.name());
		System.out.println("$#14098#"); mStore.setSeizeunitcode(MeasureUnit.IN.name());

		try {
			mStore = persistableMerchantStorePopulator.populate(store, mStore, language);
		} catch (ConversionException e) {
			throw new ConversionRuntimeException(e);
		}
		System.out.println("$#14099#"); return mStore;
	}

	@Override
	public void update(PersistableMerchantStore store) {

		System.out.println("$#14100#"); Validate.notNull(store);

		MerchantStore mStore = mergePersistableMerchantStoreToMerchantStore(store, store.getCode(),
				languageService.defaultLanguage());

		System.out.println("$#14101#"); updateMerchantStore(mStore);

	}

	private void updateMerchantStore(MerchantStore mStore) {
		try {
			System.out.println("$#14102#"); merchantStoreService.update(mStore);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException(e);
		}

	}

	private MerchantStore mergePersistableMerchantStoreToMerchantStore(PersistableMerchantStore store, String code,
			Language language) {

		MerchantStore mStore = getMerchantStoreByCode(code);

		System.out.println("$#14103#"); store.setId(mStore.getId());

		try {
			mStore = persistableMerchantStorePopulator.populate(store, mStore, language);
		} catch (ConversionException e) {
			throw new ConversionRuntimeException(e);
		}
		System.out.println("$#14104#"); return mStore;
	}

	@Override
	public ReadableMerchantStoreList getByCriteria(MerchantStoreCriteria criteria, Language lang) {
		System.out.println("$#14105#"); return  getMerchantStoresByCriteria(criteria, lang);

	}



	private ReadableMerchantStoreList getMerchantStoresByCriteria(MerchantStoreCriteria criteria, Language language) {
		try {
			GenericEntityList<MerchantStore> stores =  Optional.ofNullable(merchantStoreService.getByCriteria(criteria))
					.orElseThrow(() -> new ResourceNotFoundException("Criteria did not match any store"));
			
			
			ReadableMerchantStoreList storeList = new ReadableMerchantStoreList();
			System.out.println("$#14107#"); storeList.setData(
					(List<ReadableMerchantStore>) stores.getList().stream()
					.map(s -> convertMerchantStoreToReadableMerchantStore(language, s))
			        .collect(Collectors.toList())
					);
			System.out.println("$#14109#"); storeList.setTotalPages(stores.getTotalPages());
			System.out.println("$#14110#"); storeList.setRecordsTotal(stores.getTotalCount());
			System.out.println("$#14111#"); storeList.setNumber(stores.getList().size());
			
			System.out.println("$#14112#"); return storeList;
			
		} catch (ServiceException e) {
			throw new ServiceRuntimeException(e);
		}

	}

	@Override
	public void delete(String code) {

		System.out.println("$#14113#"); if (MerchantStore.DEFAULT_STORE.equals(code.toUpperCase())) {
			throw new ServiceRuntimeException("Cannot remove default store");
		}

		MerchantStore mStore = getMerchantStoreByCode(code);

		try {
			System.out.println("$#14114#"); merchantStoreService.delete(mStore);
		} catch (Exception e) {
			LOG.error("Error while deleting MerchantStore", e);
			throw new ServiceRuntimeException("Error while deleting MerchantStore " + e.getMessage());
		}

	}

	@Override
	public ReadableBrand getBrand(String code) {
		MerchantStore mStore = getMerchantStoreByCode(code);

		ReadableBrand readableBrand = new ReadableBrand();
		System.out.println("$#14115#"); if (!StringUtils.isEmpty(mStore.getStoreLogo())) {
			String imagePath = imageUtils.buildStoreLogoFilePath(mStore);
			ReadableImage image = createReadableImage(mStore.getStoreLogo(), imagePath);
			System.out.println("$#14116#"); readableBrand.setLogo(image);
		}
		List<MerchantConfigEntity> merchantConfigTOs = getMerchantConfigEntities(mStore);
		readableBrand.getSocialNetworks().addAll(merchantConfigTOs);
		System.out.println("$#14117#"); return readableBrand;
	}

	private List<MerchantConfigEntity> getMerchantConfigEntities(MerchantStore mStore) {
		List<MerchantConfiguration> configurations = getMergeConfigurationsByStore(MerchantConfigurationType.SOCIAL,
				mStore);

		System.out.println("$#14119#"); System.out.println("$#14118#"); return configurations.stream().map(config -> convertToMerchantConfigEntity(config))
				.collect(Collectors.toList());
	}

	private List<MerchantConfiguration> getMergeConfigurationsByStore(MerchantConfigurationType configurationType,
			MerchantStore mStore) {
		try {
			System.out.println("$#14120#"); return merchantConfigurationService.listByType(configurationType, mStore);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Error wile getting merchantConfigurations " + e.getMessage());
		}
	}

	private MerchantConfigEntity convertToMerchantConfigEntity(MerchantConfiguration config) {
		MerchantConfigEntity configTO = new MerchantConfigEntity();
		System.out.println("$#14121#"); configTO.setId(config.getId());
		System.out.println("$#14122#"); configTO.setKey(config.getKey());
		System.out.println("$#14123#"); configTO.setType(config.getMerchantConfigurationType());
		System.out.println("$#14124#"); configTO.setValue(config.getValue());
		System.out.println("$#14126#"); configTO.setActive(config.getActive() != null ? config.getActive().booleanValue() : false);
		System.out.println("$#14127#"); return configTO;
	}

	private MerchantConfiguration convertToMerchantConfiguration(MerchantConfigEntity config,
			MerchantConfigurationType configurationType) {
		MerchantConfiguration configTO = new MerchantConfiguration();
		System.out.println("$#14128#"); configTO.setId(config.getId());
		System.out.println("$#14129#"); configTO.setKey(config.getKey());
		System.out.println("$#14130#"); configTO.setMerchantConfigurationType(configurationType);
		System.out.println("$#14131#"); configTO.setValue(config.getValue());
		System.out.println("$#14132#"); configTO.setActive(new Boolean(config.isActive()));
		System.out.println("$#14133#"); return configTO;
	}

	private ReadableImage createReadableImage(String storeLogo, String imagePath) {
		ReadableImage image = new ReadableImage();
		System.out.println("$#14134#"); image.setName(storeLogo);
		System.out.println("$#14135#"); image.setPath(imagePath);
		System.out.println("$#14136#"); return image;
	}

	@Override
	public void deleteLogo(String code) {
		MerchantStore store = getByCode(code);
		String image = store.getStoreLogo();
		System.out.println("$#14137#"); store.setStoreLogo(null);

		try {
			System.out.println("$#14138#"); updateMerchantStore(store);
			System.out.println("$#14139#"); if (!StringUtils.isEmpty(image)) {
				System.out.println("$#14140#"); contentService.removeFile(store.getCode(), image);
			}
		} catch (ServiceException e) {
			throw new ServiceRuntimeException(e.getMessage());
		}
	}

	@Override
	public MerchantStore getByCode(String code) {
		System.out.println("$#14141#"); return getMerchantStoreByCode(code);
	}

	@Override
	public void addStoreLogo(String code, InputContentFile cmsContentImage) {
		MerchantStore store = getByCode(code);
		System.out.println("$#14142#"); store.setStoreLogo(cmsContentImage.getFileName());
		System.out.println("$#14143#"); saveMerchantStore(store);
		System.out.println("$#14144#"); addLogoToStore(code, cmsContentImage);
	}

	private void addLogoToStore(String code, InputContentFile cmsContentImage) {
		try {
			System.out.println("$#14145#"); contentService.addLogo(code, cmsContentImage);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException(e);
		}
	}

	private void saveMerchantStore(MerchantStore store) {
		try {
			System.out.println("$#14146#"); merchantStoreService.save(store);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException(e);
		}

	}

	@Override
	public void createBrand(String merchantStoreCode, PersistableBrand brand) {
		MerchantStore mStore = getMerchantStoreByCode(merchantStoreCode);

		List<MerchantConfigEntity> createdConfigs = brand.getSocialNetworks();

		List<MerchantConfiguration> configurations = createdConfigs.stream()
				.map(config -> convertToMerchantConfiguration(config, MerchantConfigurationType.SOCIAL))
				.collect(Collectors.toList());
		try {
			for (MerchantConfiguration mConfigs : configurations) {
				System.out.println("$#14148#"); mConfigs.setMerchantStore(mStore);
				System.out.println("$#14149#"); if (!StringUtils.isEmpty(mConfigs.getValue())) {
					System.out.println("$#14150#"); mConfigs.setMerchantConfigurationType(MerchantConfigurationType.SOCIAL);
					System.out.println("$#14151#"); merchantConfigurationService.saveOrUpdate(mConfigs);
				} else {// remove if submited blank and exists
					MerchantConfiguration config = merchantConfigurationService
							.getMerchantConfiguration(mConfigs.getKey(), mStore);
					System.out.println("$#14152#"); if (config != null) {
						System.out.println("$#14153#"); merchantConfigurationService.delete(config);
					}
				}
			}
		} catch (ServiceException se) {
			throw new ServiceRuntimeException(se);
		}

	}

	@Override
	public ReadableMerchantStoreList getChildStores(Language language, String code, int page, int count) {
		try {

			// first check if store is retailer
			MerchantStore retailer = this.getByCode(code);
			System.out.println("$#14154#"); if (retailer == null) {
				throw new ResourceNotFoundException("Merchant [" + code + "] not found");
			}

			System.out.println("$#14155#"); if (retailer.isRetailer() == null || !retailer.isRetailer().booleanValue()) {
				throw new ResourceNotFoundException("Merchant [" + code + "] not a retailer");
			}

			
			Page<MerchantStore> children = merchantStoreService.listChildren(code, page, count);
			List<ReadableMerchantStore> readableStores = new ArrayList<ReadableMerchantStore>();
			ReadableMerchantStoreList readableList = new ReadableMerchantStoreList();
			System.out.println("$#14157#"); if (!CollectionUtils.isEmpty(children.getContent())) {
				for (MerchantStore store : children)
					readableStores.add(convertMerchantStoreToReadableMerchantStore(language, store));
			}
			System.out.println("$#14158#"); readableList.setData(readableStores);
			System.out.println("$#14159#"); readableList.setRecordsFiltered(children.getSize());
			System.out.println("$#14160#"); readableList.setTotalPages(children.getTotalPages());
			System.out.println("$#14161#"); readableList.setRecordsTotal(children.getTotalElements());
			System.out.println("$#14162#"); readableList.setNumber(children.getNumber());
			
			System.out.println("$#14163#"); return readableList;
			
			
			
/*			List<MerchantStore> children = merchantStoreService.listChildren(code);
			List<ReadableMerchantStore> readableStores = new ArrayList<ReadableMerchantStore>();
			if (!CollectionUtils.isEmpty(children)) {
				for (MerchantStore store : children)
					readableStores.add(convertMerchantStoreToReadableMerchantStore(language, store));
			}
			return readableStores;*/
		} catch (ServiceException e) {
			throw new ServiceRuntimeException(e);
		}

	}

	@Override
	public ReadableMerchantStoreList findAll(MerchantStoreCriteria criteria, Language language, int page, int count) {
		
		try {
			Page<MerchantStore> stores = null;
			List<ReadableMerchantStore> readableStores = new ArrayList<ReadableMerchantStore>();
			ReadableMerchantStoreList readableList = new ReadableMerchantStoreList();
			
			Optional<String> code = Optional.ofNullable(criteria.getStoreCode());
			Optional<String> name = Optional.ofNullable(criteria.getName());
			System.out.println("$#14164#"); if(code.isPresent()) {
				
				stores = merchantStoreService.listByGroup(name, code.get(), page, count);

			} else {
				System.out.println("$#14165#"); if(criteria.isRetailers()) {
					stores = merchantStoreService.listAllRetailers(name, page, count);
				} else {
					stores = merchantStoreService.listAll(name, page, count);
				}
			}


			System.out.println("$#14166#"); if (!CollectionUtils.isEmpty(stores.getContent())) {
				for (MerchantStore store : stores)
					readableStores.add(convertMerchantStoreToReadableMerchantStore(language, store));
			}
			System.out.println("$#14167#"); readableList.setData(readableStores);
			System.out.println("$#14168#"); readableList.setRecordsTotal(stores.getTotalElements());
			System.out.println("$#14169#"); readableList.setTotalPages(stores.getTotalPages());
			System.out.println("$#14170#"); readableList.setNumber(stores.getSize());
			System.out.println("$#14171#"); readableList.setRecordsFiltered(stores.getSize());
						System.out.println("$#14172#"); return readableList;

		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Error while finding all merchant", e);
		}


	}
	
	private ReadableMerchantStore convertStoreName(MerchantStore store) {
		ReadableMerchantStore convert = new ReadableMerchantStore();
		System.out.println("$#14173#"); convert.setId(store.getId());
		System.out.println("$#14174#"); convert.setCode(store.getCode());
		System.out.println("$#14175#"); convert.setName(store.getStorename());
		System.out.println("$#14176#"); return convert;
	}

	@Override
	public List<ReadableMerchantStore> getMerchantStoreNames(MerchantStoreCriteria criteria) {
		System.out.println("$#14177#"); Validate.notNull(criteria, "MerchantStoreCriteria must not be null");
		
		try {
			
			List<ReadableMerchantStore> stores = null;
			Optional<String> code = Optional.ofNullable(criteria.getStoreCode());
			
			
			//TODO Pageable
			System.out.println("$#14178#"); if(code.isPresent()) {
				
				stores = merchantStoreService.findAllStoreNames(code.get()).stream()
						.map(s -> convertStoreName(s))
						.collect(Collectors.toList());
			} else {
				stores = merchantStoreService.findAllStoreNames().stream()
						.map(s -> convertStoreName(s))
						.collect(Collectors.toList());
			}
			
			
			System.out.println("$#14181#"); return stores;
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Exception while getting store name",e);
		}
		

	}

	@Override
	public List<Language> supportedLanguages(MerchantStore store) {
		
		System.out.println("$#14182#"); Validate.notNull(store, "MerchantStore cannot be null");
		System.out.println("$#14183#"); Validate.notNull(store.getClass(), "MerchantStore code cannot be null");
		
		System.out.println("$#14184#"); if(!CollectionUtils.isEmpty(store.getLanguages())) {
			System.out.println("$#14185#"); return store.getLanguages();
		}
		
		//refresh
		try {
			store = merchantStoreService.getByCode(store.getCode());
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("An exception occured when getting store [" + store.getCode() + "]");
		}
		
		System.out.println("$#14186#"); if(store!=null) {
			System.out.println("$#14187#"); return store.getLanguages();
		}
		
		return Collections.emptyList();
	}

}