package com.salesmanager.core.business.services.reference.init;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.product.manufacturer.ManufacturerService;
import com.salesmanager.core.business.services.catalog.product.type.ProductTypeService;
import com.salesmanager.core.business.services.merchant.MerchantStoreService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.currency.CurrencyService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.reference.loader.IntegrationModulesLoader;
import com.salesmanager.core.business.services.reference.loader.ZonesLoader;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.services.system.ModuleConfigurationService;
import com.salesmanager.core.business.services.system.optin.OptinService;
import com.salesmanager.core.business.services.tax.TaxClassService;
import com.salesmanager.core.business.services.user.GroupService;
import com.salesmanager.core.business.services.user.PermissionService;
import com.salesmanager.core.business.utils.SecurityGroupsBuilder;
import com.salesmanager.core.constants.SchemaConstant;
import com.salesmanager.core.model.catalog.product.manufacturer.Manufacturer;
import com.salesmanager.core.model.catalog.product.manufacturer.ManufacturerDescription;
import com.salesmanager.core.model.catalog.product.type.ProductType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.country.CountryDescription;
import com.salesmanager.core.model.reference.currency.Currency;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.core.model.reference.zone.ZoneDescription;
import com.salesmanager.core.model.system.IntegrationModule;
import com.salesmanager.core.model.system.optin.Optin;
import com.salesmanager.core.model.system.optin.OptinType;
import com.salesmanager.core.model.tax.taxclass.TaxClass;
import com.salesmanager.core.model.user.Group;
import com.salesmanager.core.model.user.GroupType;
import com.salesmanager.core.model.user.Permission;

@Service("initializationDatabase")
public class InitializationDatabaseImpl implements InitializationDatabase {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InitializationDatabaseImpl.class);
	

	@Inject
	private ZoneService zoneService;
	
	@Inject
	private LanguageService languageService;
	
	@Inject
	private CountryService countryService;
	
	@Inject
	private CurrencyService currencyService;
	
	@Inject
	protected MerchantStoreService merchantService;
		
	@Inject
	protected ProductTypeService productTypeService;
	
	@Inject
	private TaxClassService taxClassService;
	
	@Inject
	private ZonesLoader zonesLoader;
	
	@Inject
	private IntegrationModulesLoader modulesLoader;
	
	@Inject
	private ManufacturerService manufacturerService;
	
	@Inject
	private ModuleConfigurationService moduleConfigurationService;
	
	@Inject
	private OptinService optinService;
	
	@Inject
	protected GroupService   groupService;
	
	@Inject
	protected PermissionService   permissionService;

	private String name;
	
	public boolean isEmpty() {
		System.out.println("$#2638#"); System.out.println("$#2637#"); return languageService.count() == 0;
	}
	
	@Transactional
	public void populate(String contextName) throws ServiceException {
		this.name =  contextName;
		
		System.out.println("$#2639#"); createSecurityGroups();
		System.out.println("$#2640#"); createLanguages();
		System.out.println("$#2641#"); createCountries();
		System.out.println("$#2642#"); createZones();
		System.out.println("$#2643#"); createCurrencies();
		System.out.println("$#2644#"); createSubReferences();
		System.out.println("$#2645#"); createModules();
		System.out.println("$#2646#"); createMerchant();


	}
	
	private void createSecurityGroups() throws ServiceException {
		
		  //create permissions
		  //Map name object
		  Map<String, Permission> permissionKeys = new HashMap<String, Permission>();
		  Permission AUTH = new Permission("AUTH");
				System.out.println("$#2647#"); permissionService.create(AUTH);
		  permissionKeys.put(AUTH.getPermissionName(), AUTH);
		  
		  Permission SUPERADMIN = new Permission("SUPERADMIN");
				System.out.println("$#2648#"); permissionService.create(SUPERADMIN);
		  permissionKeys.put(SUPERADMIN.getPermissionName(), SUPERADMIN);
		  
		  Permission ADMIN = new Permission("ADMIN");
				System.out.println("$#2649#"); permissionService.create(ADMIN);
		  permissionKeys.put(ADMIN.getPermissionName(), ADMIN);
		  
		  Permission PRODUCTS = new Permission("PRODUCTS");
				System.out.println("$#2650#"); permissionService.create(PRODUCTS);
		  permissionKeys.put(PRODUCTS.getPermissionName(), PRODUCTS);
		  
		  Permission ORDER = new Permission("ORDER");
				System.out.println("$#2651#"); permissionService.create(ORDER);
		  permissionKeys.put(ORDER.getPermissionName(), ORDER);
		  
		  Permission CONTENT = new Permission("CONTENT");
				System.out.println("$#2652#"); permissionService.create(CONTENT);
		  permissionKeys.put(CONTENT.getPermissionName(), CONTENT);
		  
		  Permission STORE = new Permission("STORE");
				System.out.println("$#2653#"); permissionService.create(STORE);
		  permissionKeys.put(STORE.getPermissionName(), STORE);
		  
		  Permission TAX = new Permission("TAX");
				System.out.println("$#2654#"); permissionService.create(TAX);
		  permissionKeys.put(TAX.getPermissionName(), TAX);
		  
		  Permission PAYMENT = new Permission("PAYMENT");
				System.out.println("$#2655#"); permissionService.create(PAYMENT);
		  permissionKeys.put(PAYMENT.getPermissionName(), PAYMENT);
		  
		  Permission CUSTOMER = new Permission("CUSTOMER");
				System.out.println("$#2656#"); permissionService.create(CUSTOMER);
		  permissionKeys.put(CUSTOMER.getPermissionName(), CUSTOMER);
		  
		  Permission SHIPPING = new Permission("SHIPPING");
				System.out.println("$#2657#"); permissionService.create(SHIPPING);
		  permissionKeys.put(SHIPPING.getPermissionName(), SHIPPING);
		  
		  Permission AUTH_CUSTOMER = new Permission("AUTH_CUSTOMER");
				System.out.println("$#2658#"); permissionService.create(AUTH_CUSTOMER);
		  permissionKeys.put(AUTH_CUSTOMER.getPermissionName(), AUTH_CUSTOMER);
		
		  SecurityGroupsBuilder groupBuilder = new SecurityGroupsBuilder();
		  groupBuilder
		  .addGroup("SUPERADMIN", GroupType.ADMIN)
		  .addPermission(permissionKeys.get("AUTH"))
		  .addPermission(permissionKeys.get("SUPERADMIN"))
		  .addPermission(permissionKeys.get("ADMIN"))
		  .addPermission(permissionKeys.get("PRODUCTS"))
		  .addPermission(permissionKeys.get("ORDER"))
		  .addPermission(permissionKeys.get("CONTENT"))
		  .addPermission(permissionKeys.get("STORE"))
		  .addPermission(permissionKeys.get("TAX"))
		  .addPermission(permissionKeys.get("PAYMENT"))
		  .addPermission(permissionKeys.get("CUSTOMER"))
		  .addPermission(permissionKeys.get("SHIPPING"))
		  
		  .addGroup("ADMIN", GroupType.ADMIN)
		  .addPermission(permissionKeys.get("AUTH"))
		  .addPermission(permissionKeys.get("ADMIN"))
		  .addPermission(permissionKeys.get("PRODUCTS"))
		  .addPermission(permissionKeys.get("ORDER"))
		  .addPermission(permissionKeys.get("CONTENT"))
		  .addPermission(permissionKeys.get("STORE"))
		  .addPermission(permissionKeys.get("TAX"))
		  .addPermission(permissionKeys.get("PAYMENT"))
		  .addPermission(permissionKeys.get("CUSTOMER"))
		  .addPermission(permissionKeys.get("SHIPPING"))
		  
		  .addGroup("ADMIN_RETAILER", GroupType.ADMIN)
		  .addPermission(permissionKeys.get("AUTH"))
		  .addPermission(permissionKeys.get("ADMIN"))
		  .addPermission(permissionKeys.get("PRODUCTS"))
		  .addPermission(permissionKeys.get("ORDER"))
		  .addPermission(permissionKeys.get("CONTENT"))
		  .addPermission(permissionKeys.get("STORE"))
		  .addPermission(permissionKeys.get("TAX"))
		  .addPermission(permissionKeys.get("PAYMENT"))
		  .addPermission(permissionKeys.get("CUSTOMER"))
		  .addPermission(permissionKeys.get("SHIPPING"))
		  
		  .addGroup("ADMIN_STORE", GroupType.ADMIN)
		  .addPermission(permissionKeys.get("AUTH"))
		  .addPermission(permissionKeys.get("CONTENT"))
		  .addPermission(permissionKeys.get("STORE"))
		  .addPermission(permissionKeys.get("TAX"))
		  .addPermission(permissionKeys.get("PAYMENT"))
		  .addPermission(permissionKeys.get("CUSTOMER"))
		  .addPermission(permissionKeys.get("SHIPPING"))
		  
		  .addGroup("ADMIN_CATALOGUE", GroupType.ADMIN)
		  .addPermission(permissionKeys.get("AUTH"))
		  .addPermission(permissionKeys.get("PRODUCTS"))
		  
		  .addGroup("ADMIN_ORDER", GroupType.ADMIN)
		  .addPermission(permissionKeys.get("AUTH"))
		  .addPermission(permissionKeys.get("ORDER"))
		  
		  .addGroup("ADMIN_CONTENT", GroupType.ADMIN)
		  .addPermission(permissionKeys.get("AUTH"))
		  .addPermission(permissionKeys.get("CONTENT"))
		  
		  .addGroup("CUSTOMER", GroupType.CUSTOMER)
		  .addPermission(permissionKeys.get("AUTH"))
		  .addPermission(permissionKeys.get("AUTH_CUSTOMER"));
		  
		  for(Group g : groupBuilder.build()) {
					System.out.println("$#2659#"); groupService.create(g);
		  }

		
	}
	


	private void createCurrencies() throws ServiceException {
		LOGGER.info(String.format("%s : Populating Currencies ", name));

		for (String code : SchemaConstant.CURRENCY_MAP.keySet()) {
  
            try {
            	java.util.Currency c = java.util.Currency.getInstance(code);
            	
													System.out.println("$#2660#"); if(c==null) {
            		LOGGER.info(String.format("%s : Populating Currencies : no currency for code : %s", name, code));
            	}
            	
            		//check if it exist
            		
	            	Currency currency = new Currency();
														System.out.println("$#2661#"); currency.setName(c.getCurrencyCode());
														System.out.println("$#2662#"); currency.setCurrency(c);
														System.out.println("$#2663#"); currencyService.create(currency);

            //System.out.println(l.getCountry() + "   " + c.getSymbol() + "  " + c.getSymbol(l));
            } catch (IllegalArgumentException e) {
            	LOGGER.info(String.format("%s : Populating Currencies : no currency for code : %s", name, code));
            }
        }  
	}

	private void createCountries() throws ServiceException {
		LOGGER.info(String.format("%s : Populating Countries ", name));
		List<Language> languages = languageService.list();
		for(String code : SchemaConstant.COUNTRY_ISO_CODE) {
			Locale locale = SchemaConstant.LOCALES.get(code);
			System.out.println("$#2664#"); if (locale != null) {
				Country country = new Country(code);
				System.out.println("$#2665#"); countryService.create(country);
				
				for (Language language : languages) {
					String name = locale.getDisplayCountry(new Locale(language.getCode()));
					//byte[] ptext = value.getBytes(Constants.ISO_8859_1); 
					//String name = new String(ptext, Constants.UTF_8); 
					CountryDescription description = new CountryDescription(language, name);
					System.out.println("$#2666#"); countryService.addCountryDescription(country, description);
				}
			}
		}
	}
	
	private void createZones() throws ServiceException {
		LOGGER.info(String.format("%s : Populating Zones ", name));
        try {

    		  Map<String,Zone> zonesMap = new HashMap<String,Zone>();
    		  zonesMap = zonesLoader.loadZones("reference/zoneconfig.json");
    		  
								System.out.println("$#2667#"); this.addZonesToDb(zonesMap);
/*              
              for (Map.Entry<String, Zone> entry : zonesMap.entrySet()) {
            	    String key = entry.getKey();
            	    Zone value = entry.getValue();
            	    if(value.getDescriptions()==null) {
            	    	LOGGER.warn("This zone " + key + " has no descriptions");
            	    	continue;
            	    }
            	    
            	    List<ZoneDescription> zoneDescriptions = value.getDescriptions();
            	    value.setDescriptons(null);

            	    zoneService.create(value);
            	    
            	    for(ZoneDescription description : zoneDescriptions) {
            	    	description.setZone(value);
            	    	zoneService.addDescription(value, description);
            	    }
              }*/
              
              //lookup additional zones
              //iterate configured languages
      		  LOGGER.info("Populating additional zones");

              //load reference/zones/* (zone config for additional country)
              //example in.json and in-fr.son
              //will load es zones and use a specific file for french es zones
      		  List<Map<String, Zone>> loadIndividualZones = zonesLoader.loadIndividualZones();
      		  
								System.out.println("$#2668#"); loadIndividualZones.stream().forEach(z -> {
					System.out.println("$#2669#"); addZonesToDb(z);
			});

  		} catch (Exception e) {
  		    
  			throw new ServiceException(e);
  		}

	}

	
	private void addZonesToDb(Map<String,Zone> zonesMap) throws RuntimeException {
		
		try {
		
	        for (Map.Entry<String, Zone> entry : zonesMap.entrySet()) {
	    	    String key = entry.getKey();
	    	    Zone value = entry.getValue();

										System.out.println("$#2670#"); if(value.getDescriptions()==null) {
	    	    	LOGGER.warn("This zone " + key + " has no descriptions");
	    	    	continue;
	    	    }
	    	    
	    	    List<ZoneDescription> zoneDescriptions = value.getDescriptions();
										System.out.println("$#2671#"); value.setDescriptons(null);
	
										System.out.println("$#2672#"); zoneService.create(value);
	    	    
	    	    for(ZoneDescription description : zoneDescriptions) {
											System.out.println("$#2673#"); description.setZone(value);
											System.out.println("$#2674#"); zoneService.addDescription(value, description);
	    	    }
	        }
        
		}catch(Exception e) {
			LOGGER.error("An error occured while loading zones",e);
			
		}
		
	}
	
	private void createLanguages() throws ServiceException {
		LOGGER.info(String.format("%s : Populating Languages ", name));
		for(String code : SchemaConstant.LANGUAGE_ISO_CODE) {
			Language language = new Language(code);
			System.out.println("$#2675#"); languageService.create(language);
		}
	}
	
	private void createMerchant() throws ServiceException {
		LOGGER.info(String.format("%s : Creating merchant ", name));
		
		Date date = new Date(System.currentTimeMillis());
		
		Language en = languageService.getByCode("en");
		Country ca = countryService.getByCode("CA");
		Currency currency = currencyService.getByCode("CAD");
		Zone qc = zoneService.getByCode("QC");
		
		List<Language> supportedLanguages = new ArrayList<Language>();
		supportedLanguages.add(en);
		
		//create a merchant
		MerchantStore store = new MerchantStore();
		System.out.println("$#2676#"); store.setCountry(ca);
		System.out.println("$#2677#"); store.setCurrency(currency);
		System.out.println("$#2678#"); store.setDefaultLanguage(en);
		System.out.println("$#2679#"); store.setInBusinessSince(date);
		System.out.println("$#2680#"); store.setZone(qc);
		System.out.println("$#2681#"); store.setStorename("Default store");
		System.out.println("$#2682#"); store.setStorephone("888-888-8888");
		System.out.println("$#2683#"); store.setCode(MerchantStore.DEFAULT_STORE);
		System.out.println("$#2684#"); store.setStorecity("My city");
		System.out.println("$#2685#"); store.setStoreaddress("1234 Street address");
		System.out.println("$#2686#"); store.setStorepostalcode("H2H-2H2");
		System.out.println("$#2687#"); store.setStoreEmailAddress("john@test.com");
		System.out.println("$#2688#"); store.setDomainName("localhost:8080");
		System.out.println("$#2689#"); store.setStoreTemplate("december");
		System.out.println("$#2690#"); store.setRetailer(true);
		System.out.println("$#2691#"); store.setLanguages(supportedLanguages);
		
		System.out.println("$#2692#"); merchantService.create(store);
		
		
		TaxClass taxclass = new TaxClass(TaxClass.DEFAULT_TAX_CLASS);
		System.out.println("$#2693#"); taxclass.setMerchantStore(store);
		
		System.out.println("$#2694#"); taxClassService.create(taxclass);
		
		//create default manufacturer
		Manufacturer defaultManufacturer = new Manufacturer();
		System.out.println("$#2695#"); defaultManufacturer.setCode("DEFAULT");
		System.out.println("$#2696#"); defaultManufacturer.setMerchantStore(store);
		
		ManufacturerDescription manufacturerDescription = new ManufacturerDescription();
		System.out.println("$#2697#"); manufacturerDescription.setLanguage(en);
		System.out.println("$#2698#"); manufacturerDescription.setName("DEFAULT");
		System.out.println("$#2699#"); manufacturerDescription.setManufacturer(defaultManufacturer);
		System.out.println("$#2700#"); manufacturerDescription.setDescription("DEFAULT");
		defaultManufacturer.getDescriptions().add(manufacturerDescription);
		
		System.out.println("$#2701#"); manufacturerService.create(defaultManufacturer);
		
	   Optin newsletter = new Optin();
				System.out.println("$#2702#"); newsletter.setCode(OptinType.NEWSLETTER.name());
				System.out.println("$#2703#"); newsletter.setMerchant(store);
				System.out.println("$#2704#"); newsletter.setOptinType(OptinType.NEWSLETTER);
				System.out.println("$#2705#"); optinService.create(newsletter);
		
		
	}

	private void createModules() throws ServiceException {
		
		try {
			
			List<IntegrationModule> modules = modulesLoader.loadIntegrationModules("reference/integrationmodules.json");
            for (IntegrationModule entry : modules) {
													System.out.println("$#2706#"); moduleConfigurationService.create(entry);
          }
			
			
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		
		
	}
	
	private void createSubReferences() throws ServiceException {
		
		LOGGER.info(String.format("%s : Loading catalog sub references ", name));
		
		
		ProductType productType = new ProductType();
		System.out.println("$#2707#"); productType.setCode(ProductType.GENERAL_TYPE);
		System.out.println("$#2708#"); productTypeService.create(productType);


		
		
	}
	

	



}
