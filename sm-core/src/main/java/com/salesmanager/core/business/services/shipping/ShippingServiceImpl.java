package com.salesmanager.core.business.services.shipping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesmanager.core.business.constants.ShippingConstants;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.reference.loader.ConfigurationModulesLoader;
import com.salesmanager.core.business.services.system.MerchantConfigurationService;
import com.salesmanager.core.business.services.system.ModuleConfigurationService;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.common.Delivery;
import com.salesmanager.core.model.common.UserContext;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shipping.PackageDetails;
import com.salesmanager.core.model.shipping.Quote;
import com.salesmanager.core.model.shipping.ShippingConfiguration;
import com.salesmanager.core.model.shipping.ShippingMetaData;
import com.salesmanager.core.model.shipping.ShippingOption;
import com.salesmanager.core.model.shipping.ShippingOptionPriceType;
import com.salesmanager.core.model.shipping.ShippingOrigin;
import com.salesmanager.core.model.shipping.ShippingPackageType;
import com.salesmanager.core.model.shipping.ShippingProduct;
import com.salesmanager.core.model.shipping.ShippingQuote;
import com.salesmanager.core.model.shipping.ShippingSummary;
import com.salesmanager.core.model.shipping.ShippingType;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.core.model.system.CustomIntegrationConfiguration;
import com.salesmanager.core.model.system.IntegrationConfiguration;
import com.salesmanager.core.model.system.IntegrationModule;
import com.salesmanager.core.model.system.MerchantConfiguration;
import com.salesmanager.core.modules.integration.IntegrationException;
import com.salesmanager.core.modules.integration.shipping.model.Packaging;
import com.salesmanager.core.modules.integration.shipping.model.ShippingQuoteModule;
import com.salesmanager.core.modules.integration.shipping.model.ShippingQuotePrePostProcessModule;
import com.salesmanager.core.modules.utils.Encryption;
import com.shopizer.search.utils.DateUtil;


@Service("shippingService")
public class ShippingServiceImpl implements ShippingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ShippingServiceImpl.class);
	
	
	private final static String SUPPORTED_COUNTRIES = "SUPPORTED_CNTR";
	private final static String SHIPPING_MODULES = "SHIPPING";
	private final static String SHIPPING_DISTANCE = "shippingDistanceModule";

	
	@Inject
	private MerchantConfigurationService merchantConfigurationService;
	

	@Inject
	private PricingService pricingService;
	
	@Inject
	private ModuleConfigurationService moduleConfigurationService;
	
	@Inject
	private Packaging packaging;
	
	@Inject
	private CountryService countryService;
	
	@Inject
	private LanguageService languageService;
	
	@Inject
	private Encryption encryption;

	@Inject
	private ShippingOriginService shippingOriginService;
	
	@Inject
	private ShippingQuoteService shippingQuoteService;
	
	@Inject
	@Resource(name="shippingModules")
	private Map<String,ShippingQuoteModule> shippingModules;
	
	//shipping pre-processors
	@Inject
	@Resource(name="shippingModulePreProcessors")
	private List<ShippingQuotePrePostProcessModule> shippingModulePreProcessors;
	
	//shipping post-processors
	@Inject
	@Resource(name="shippingModulePostProcessors")
	private List<ShippingQuotePrePostProcessModule> shippingModulePostProcessors;
	
	@Override
	public ShippingConfiguration getShippingConfiguration(MerchantStore store) throws ServiceException {

		MerchantConfiguration configuration = merchantConfigurationService.getMerchantConfiguration(ShippingConstants.SHIPPING_CONFIGURATION, store);
		
		ShippingConfiguration shippingConfiguration = null;
		
		System.out.println("$#2904#"); if(configuration!=null) {
			String value = configuration.getValue();
			
			ObjectMapper mapper = new ObjectMapper();
			try {
				shippingConfiguration = mapper.readValue(value, ShippingConfiguration.class);
			} catch(Exception e) {
				throw new ServiceException("Cannot parse json string " + value);
			}
		}
		System.out.println("$#2905#"); return shippingConfiguration;
		
	}
	
	@Override
	public IntegrationConfiguration getShippingConfiguration(String moduleCode, MerchantStore store) throws ServiceException {

		
		Map<String,IntegrationConfiguration> configuredModules = getShippingModulesConfigured(store);
		System.out.println("$#2906#"); if(configuredModules!=null) {
			for(String key : configuredModules.keySet()) {
				System.out.println("$#2907#"); if(key.equals(moduleCode)) {
					System.out.println("$#2908#"); return configuredModules.get(key);
				}
			}
		}
		
		return null;
		
	}
	
	@Override
	public CustomIntegrationConfiguration getCustomShippingConfiguration(String moduleCode, MerchantStore store) throws ServiceException {

		
		ShippingQuoteModule quoteModule = (ShippingQuoteModule)shippingModules.get(moduleCode);
		System.out.println("$#2909#"); if(quoteModule==null) {
			return null;
		}
		System.out.println("$#2910#"); return quoteModule.getCustomModuleConfiguration(store);
		
	}
	
	@Override
	public void saveShippingConfiguration(ShippingConfiguration shippingConfiguration, MerchantStore store) throws ServiceException {
		
		MerchantConfiguration configuration = merchantConfigurationService.getMerchantConfiguration(ShippingConstants.SHIPPING_CONFIGURATION, store);

		System.out.println("$#2911#"); if(configuration==null) {
			configuration = new MerchantConfiguration();
			System.out.println("$#2912#"); configuration.setMerchantStore(store);
			System.out.println("$#2913#"); configuration.setKey(ShippingConstants.SHIPPING_CONFIGURATION);
		}
		
		String value = shippingConfiguration.toJSONString();
		System.out.println("$#2914#"); configuration.setValue(value);
		System.out.println("$#2915#"); merchantConfigurationService.saveOrUpdate(configuration);
		
	}
	
	@Override
	public void saveCustomShippingConfiguration(String moduleCode, CustomIntegrationConfiguration shippingConfiguration, MerchantStore store) throws ServiceException {
		
		
		ShippingQuoteModule quoteModule = (ShippingQuoteModule)shippingModules.get(moduleCode);
		System.out.println("$#2916#"); if(quoteModule==null) {
			throw new ServiceException("Shipping module " + moduleCode + " does not exist");
		}
		
		String configurationValue = shippingConfiguration.toJSONString();
		
		
		try {

			MerchantConfiguration configuration = merchantConfigurationService.getMerchantConfiguration(moduleCode, store);
	
			System.out.println("$#2917#"); if(configuration==null) {

				configuration = new MerchantConfiguration();
				System.out.println("$#2918#"); configuration.setKey(moduleCode);
				System.out.println("$#2919#"); configuration.setMerchantStore(store);
			}
			System.out.println("$#2920#"); configuration.setValue(configurationValue);
			System.out.println("$#2921#"); merchantConfigurationService.saveOrUpdate(configuration);
		
		} catch (Exception e) {
			throw new IntegrationException(e);
		}

		
		
	}
	

	@Override
	public List<IntegrationModule> getShippingMethods(MerchantStore store) throws ServiceException {
		
		List<IntegrationModule> modules =  moduleConfigurationService.getIntegrationModules(SHIPPING_MODULES);
		List<IntegrationModule> returnModules = new ArrayList<IntegrationModule>();
		
		for(IntegrationModule module : modules) {
			System.out.println("$#2922#"); if(module.getRegionsSet().contains(store.getCountry().getIsoCode())
					|| module.getRegionsSet().contains("*")) {
				
				returnModules.add(module);
			}
		}
		
		System.out.println("$#2924#"); return returnModules;
	}
	
	@Override
	public void saveShippingQuoteModuleConfiguration(IntegrationConfiguration configuration, MerchantStore store) throws ServiceException {
		
			//validate entries
			try {
				
				String moduleCode = configuration.getModuleCode();
				ShippingQuoteModule quoteModule = (ShippingQuoteModule)shippingModules.get(moduleCode);
				System.out.println("$#2925#"); if(quoteModule==null) {
					throw new ServiceException("Shipping quote module " + moduleCode + " does not exist");
				}
				System.out.println("$#2926#"); quoteModule.validateModuleConfiguration(configuration, store);
				
			} catch (IntegrationException ie) {
				throw ie;
			}
			
			try {
				Map<String,IntegrationConfiguration> modules = new HashMap<String,IntegrationConfiguration>();
				MerchantConfiguration merchantConfiguration = merchantConfigurationService.getMerchantConfiguration(SHIPPING_MODULES, store);
				System.out.println("$#2927#"); if(merchantConfiguration!=null) {
					System.out.println("$#2928#"); if(!StringUtils.isBlank(merchantConfiguration.getValue())) {
						
						String decrypted = encryption.decrypt(merchantConfiguration.getValue());
						modules = ConfigurationModulesLoader.loadIntegrationConfigurations(decrypted);
					}
				} else {
					merchantConfiguration = new MerchantConfiguration();
					System.out.println("$#2929#"); merchantConfiguration.setMerchantStore(store);
					System.out.println("$#2930#"); merchantConfiguration.setKey(SHIPPING_MODULES);
				}
				modules.put(configuration.getModuleCode(), configuration);
				
				String configs =  ConfigurationModulesLoader.toJSONString(modules);
				
				String encrypted = encryption.encrypt(configs);
				System.out.println("$#2931#"); merchantConfiguration.setValue(encrypted);
				System.out.println("$#2932#"); merchantConfigurationService.saveOrUpdate(merchantConfiguration);
				
			} catch (Exception e) {
				throw new ServiceException(e);
			}
	}
	
	
	@Override
	public void removeShippingQuoteModuleConfiguration(String moduleCode, MerchantStore store) throws ServiceException {
		
		

		try {
			Map<String,IntegrationConfiguration> modules = new HashMap<String,IntegrationConfiguration>();
			MerchantConfiguration merchantConfiguration = merchantConfigurationService.getMerchantConfiguration(SHIPPING_MODULES, store);
			System.out.println("$#2933#"); if(merchantConfiguration!=null) {
				System.out.println("$#2934#"); if(!StringUtils.isBlank(merchantConfiguration.getValue())) {
					String decrypted = encryption.decrypt(merchantConfiguration.getValue());
					modules = ConfigurationModulesLoader.loadIntegrationConfigurations(decrypted);
				}
				
				modules.remove(moduleCode);
				String configs =  ConfigurationModulesLoader.toJSONString(modules);
				String encrypted = encryption.encrypt(configs);
				System.out.println("$#2935#"); merchantConfiguration.setValue(encrypted);
				System.out.println("$#2936#"); merchantConfigurationService.saveOrUpdate(merchantConfiguration);
				
				
			} 
			
			MerchantConfiguration configuration = merchantConfigurationService.getMerchantConfiguration(moduleCode, store);
			
			System.out.println("$#2937#"); if(configuration!=null) {//custom module

				System.out.println("$#2938#"); merchantConfigurationService.delete(configuration);
			}

			
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	
	}
	
	@Override
	public void removeCustomShippingQuoteModuleConfiguration(String moduleCode, MerchantStore store) throws ServiceException {
		
		

		try {
			
			System.out.println("$#2939#"); removeShippingQuoteModuleConfiguration(moduleCode,store);
			MerchantConfiguration merchantConfiguration = merchantConfigurationService.getMerchantConfiguration(moduleCode, store);
			System.out.println("$#2940#"); if(merchantConfiguration!=null) {
				System.out.println("$#2941#"); merchantConfigurationService.delete(merchantConfiguration);
			} 
			
			
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	
	}
	
	@Override
	public Map<String,IntegrationConfiguration> getShippingModulesConfigured(MerchantStore store) throws ServiceException {
		try {
			

			Map<String,IntegrationConfiguration> modules = new HashMap<String,IntegrationConfiguration>();
			MerchantConfiguration merchantConfiguration = merchantConfigurationService.getMerchantConfiguration(SHIPPING_MODULES, store);
			System.out.println("$#2942#"); if(merchantConfiguration!=null) {
				System.out.println("$#2943#"); if(!StringUtils.isBlank(merchantConfiguration.getValue())) {
					String decrypted = encryption.decrypt(merchantConfiguration.getValue());
					modules = ConfigurationModulesLoader.loadIntegrationConfigurations(decrypted);
					
				}
			}
			System.out.println("$#2944#"); return modules;
		
		
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		
	}
	
	@Override
	public ShippingSummary getShippingSummary(MerchantStore store, ShippingQuote shippingQuote, ShippingOption selectedShippingOption) throws ServiceException {
		
		ShippingSummary shippingSummary = new ShippingSummary();
		System.out.println("$#2945#"); shippingSummary.setFreeShipping(shippingQuote.isFreeShipping());
		System.out.println("$#2946#"); shippingSummary.setHandling(shippingQuote.getHandlingFees());
		System.out.println("$#2947#"); shippingSummary.setShipping(selectedShippingOption.getOptionPrice());
		System.out.println("$#2948#"); shippingSummary.setShippingModule(shippingQuote.getShippingModuleCode());
		System.out.println("$#2949#"); shippingSummary.setShippingOption(selectedShippingOption.getDescription());
		
		System.out.println("$#2950#"); return shippingSummary;
	}

	@Override
	public ShippingQuote getShippingQuote(Long shoppingCartId, MerchantStore store, Delivery delivery, List<ShippingProduct> products, Language language) throws ServiceException  {
		
		
		//ShippingConfiguration -> Global configuration of a given store
		//IntegrationConfiguration -> Configuration of a given module
		//IntegrationModule -> The concrete module as defined in integrationmodules.properties
		
		//delivery without postal code is accepted
		Validate.notNull(store,"MerchantStore must not be null");
		Validate.notNull(delivery,"Delivery must not be null");
		Validate.notEmpty(products,"products must not be empty");
		Validate.notNull(language,"Language must not be null");
		
		
		
		ShippingQuote shippingQuote = new ShippingQuote();
		ShippingQuoteModule shippingQuoteModule = null;
		
		try {
			
			
			System.out.println("$#2951#"); if(StringUtils.isBlank(delivery.getPostalCode())) {
				shippingQuote.getWarnings().add("No postal code in delivery address");
				System.out.println("$#2952#"); shippingQuote.setShippingReturnCode(ShippingQuote.NO_POSTAL_CODE);
			}
		
			//get configuration
			ShippingConfiguration shippingConfiguration = getShippingConfiguration(store);
			ShippingType shippingType = ShippingType.INTERNATIONAL;
			
			/** get shipping origin **/
			ShippingOrigin shippingOrigin = shippingOriginService.getByStore(store);
			System.out.println("$#2953#"); if(shippingOrigin == null || !shippingOrigin.isActive()) {
				shippingOrigin = new ShippingOrigin();
				System.out.println("$#2955#"); shippingOrigin.setAddress(store.getStoreaddress());
				System.out.println("$#2956#"); shippingOrigin.setCity(store.getStorecity());
				System.out.println("$#2957#"); shippingOrigin.setCountry(store.getCountry());
				System.out.println("$#2958#"); shippingOrigin.setPostalCode(store.getStorepostalcode());
				System.out.println("$#2959#"); shippingOrigin.setState(store.getStorestateprovince());
				System.out.println("$#2960#"); shippingOrigin.setZone(store.getZone());
			}
			
			
			System.out.println("$#2961#"); if(shippingConfiguration==null) {
				shippingConfiguration = new ShippingConfiguration();
			}
			
			System.out.println("$#2962#"); if(shippingConfiguration.getShippingType()!=null) {
					shippingType = shippingConfiguration.getShippingType();
			}

			//look if customer country code excluded
			Country shipCountry = delivery.getCountry();
			
			//a ship to country is required
			Validate.notNull(shipCountry,"Ship to Country cannot be null");
			Validate.notNull(store.getCountry(), "Store Country canot be null");
			
			System.out.println("$#2963#"); if(shippingType.name().equals(ShippingType.NATIONAL.name())){
				//customer country must match store country
				System.out.println("$#2964#"); if(!shipCountry.getIsoCode().equals(store.getCountry().getIsoCode())) {
					System.out.println("$#2965#"); shippingQuote.setShippingReturnCode(ShippingQuote.NO_SHIPPING_TO_SELECTED_COUNTRY + " " + shipCountry.getIsoCode());
					System.out.println("$#2966#"); return shippingQuote;
				}
			} else if(shippingType.name().equals(ShippingType.INTERNATIONAL.name())){ System.out.println("$#2967#");
				
				//customer shipping country code must be in accepted list
				List<String> supportedCountries = this.getSupportedCountries(store);
				System.out.println("$#2968#"); if(!supportedCountries.contains(shipCountry.getIsoCode())) {
					System.out.println("$#2969#"); shippingQuote.setShippingReturnCode(ShippingQuote.NO_SHIPPING_TO_SELECTED_COUNTRY + " " + shipCountry.getIsoCode());
					System.out.println("$#2970#"); return shippingQuote;
				}
			} else {
				System.out.println("$#2967#"); // manual correction for else-if mutation coverage
			}
			
			//must have a shipping module configured
			Map<String, IntegrationConfiguration> modules = this.getShippingModulesConfigured(store);
			System.out.println("$#2971#"); if(modules == null){
				System.out.println("$#2972#"); shippingQuote.setShippingReturnCode(ShippingQuote.NO_SHIPPING_MODULE_CONFIGURED);
				System.out.println("$#2973#"); return shippingQuote;
			}

			
			/** uses this module name **/
			String moduleName = null;
			IntegrationConfiguration configuration = null;
			for(String module : modules.keySet()) {
				moduleName = module;
				configuration = modules.get(module);
				//use the first active module
				System.out.println("$#2974#"); if(configuration.isActive()) {
					shippingQuoteModule = shippingModules.get(module);
					System.out.println("$#2975#"); if(shippingQuoteModule instanceof ShippingQuotePrePostProcessModule) {
						shippingQuoteModule = null;
						continue;
					} else {
						break;
					}
				}
			}
			
			System.out.println("$#2976#"); if(shippingQuoteModule==null){
				System.out.println("$#2977#"); shippingQuote.setShippingReturnCode(ShippingQuote.NO_SHIPPING_MODULE_CONFIGURED);
				System.out.println("$#2978#"); return shippingQuote;
			}
			
			/** merchant module configs **/
			List<IntegrationModule> shippingMethods = this.getShippingMethods(store);
			IntegrationModule shippingModule = null;
			for(IntegrationModule mod : shippingMethods) {
				System.out.println("$#2979#"); if(mod.getCode().equals(moduleName)){
					shippingModule = mod;
					break;
				}
			}
			
			/** general module configs **/
			System.out.println("$#2980#"); if(shippingModule==null) {
				System.out.println("$#2981#"); shippingQuote.setShippingReturnCode(ShippingQuote.NO_SHIPPING_MODULE_CONFIGURED);
				System.out.println("$#2982#"); return shippingQuote;
			}
			
			//calculate order total
			BigDecimal orderTotal = calculateOrderTotal(products,store);
			List<PackageDetails> packages = getPackagesDetails(products, store);
			
			//free shipping ?
			boolean freeShipping = false;
			System.out.println("$#2983#"); if(shippingConfiguration.isFreeShippingEnabled()) {
				BigDecimal freeShippingAmount = shippingConfiguration.getOrderTotalFreeShipping();
				System.out.println("$#2984#"); if(freeShippingAmount!=null) {
					System.out.println("$#2986#"); System.out.println("$#2985#"); if(orderTotal.doubleValue()>freeShippingAmount.doubleValue()) {
						System.out.println("$#2987#"); if(shippingConfiguration.getFreeShippingType() == ShippingType.NATIONAL) {
							System.out.println("$#2988#"); if(store.getCountry().getIsoCode().equals(shipCountry.getIsoCode())) {
								freeShipping = true;
								System.out.println("$#2989#"); shippingQuote.setFreeShipping(true);
								System.out.println("$#2990#"); shippingQuote.setFreeShippingAmount(freeShippingAmount);
								System.out.println("$#2991#"); return shippingQuote;
							}
						} else {//international all
							freeShipping = true;
							System.out.println("$#2992#"); shippingQuote.setFreeShipping(true);
							System.out.println("$#2993#"); shippingQuote.setFreeShippingAmount(freeShippingAmount);
							System.out.println("$#2994#"); return shippingQuote;
						}
	
					}
				}
			}
			

			//handling fees
			BigDecimal handlingFees = shippingConfiguration.getHandlingFees();
			System.out.println("$#2995#"); if(handlingFees!=null) {
				System.out.println("$#2996#"); shippingQuote.setHandlingFees(handlingFees);
			}
			
			//tax basis
			System.out.println("$#2997#"); shippingQuote.setApplyTaxOnShipping(shippingConfiguration.isTaxOnShipping());
			

			Locale locale = languageService.toLocale(language, store);
			
			//invoke pre processors
			//the main pre-processor determines at runtime the shipping module
			//also available distance calculation
			System.out.println("$#2998#"); if(!CollectionUtils.isEmpty(shippingModulePreProcessors)) {
				for(ShippingQuotePrePostProcessModule preProcessor : shippingModulePreProcessors) {
					//System.out.println("Using pre-processor " + preProcessor.getModuleCode());
					System.out.println("$#2999#"); preProcessor.prePostProcessShippingQuotes(shippingQuote, packages, orderTotal, delivery, shippingOrigin, store, configuration, shippingModule, shippingConfiguration, shippingMethods, locale);
					//TODO switch module if required
					System.out.println("$#3000#"); if(shippingQuote.getCurrentShippingModule()!=null && !shippingQuote.getCurrentShippingModule().getCode().equals(shippingModule.getCode())) {
						shippingModule = shippingQuote.getCurrentShippingModule();//determines the shipping module
						configuration = modules.get(shippingModule.getCode());
						System.out.println("$#3002#"); if(configuration!=null) {
							System.out.println("$#3003#"); if(configuration.isActive()) {
								moduleName = shippingModule.getCode();
								shippingQuoteModule = this.shippingModules.get(shippingModule.getCode());
								configuration = modules.get(shippingModule.getCode());
							} //TODO use default
						}
						
					}
				}
			}

			//invoke module
			List<ShippingOption> shippingOptions = null;
					
			try {
				shippingOptions = shippingQuoteModule.getShippingQuotes(shippingQuote, packages, orderTotal, delivery, shippingOrigin, store, configuration, shippingModule, shippingConfiguration, locale);
			} catch(Exception e) {
				LOGGER.error("Error while calculating shipping : " + e.getMessage(), e);
/*				merchantLogService.save(
						new MerchantLog(store,
								"Can't process " + shippingModule.getModule()
								+ " -> "
								+ e.getMessage()));
				shippingQuote.setQuoteError(e.getMessage());
				shippingQuote.setShippingReturnCode(ShippingQuote.ERROR);
				return shippingQuote;*/
			}
			
			System.out.println("$#3004#"); if(shippingOptions==null && !StringUtils.isBlank(delivery.getPostalCode())) {
				
				//absolutely need to use in this case store pickup or other default shipping quote
				System.out.println("$#3006#"); shippingQuote.setShippingReturnCode(ShippingQuote.NO_SHIPPING_TO_SELECTED_COUNTRY);
			}
			
			
			System.out.println("$#3007#"); shippingQuote.setShippingModuleCode(moduleName);
			
			//filter shipping options
			ShippingOptionPriceType shippingOptionPriceType = shippingConfiguration.getShippingOptionPriceType();
			ShippingOption selectedOption = null;
			
			System.out.println("$#3008#"); if(shippingOptions!=null) {
				
				for(ShippingOption option : shippingOptions) {
					System.out.println("$#3009#"); if(selectedOption==null) {
						selectedOption = option;
					}
					//set price text
					String priceText = pricingService.getDisplayAmount(option.getOptionPrice(), store);
					System.out.println("$#3010#"); option.setOptionPriceText(priceText);
					System.out.println("$#3011#"); option.setShippingModuleCode(moduleName);
				
					System.out.println("$#3012#"); if(StringUtils.isBlank(option.getOptionName())) {
						
						String countryName = delivery.getCountry().getName();
						System.out.println("$#3013#"); if(countryName == null) {
							Map<String,Country> deliveryCountries = countryService.getCountriesMap(language);
							Country dCountry = (Country)deliveryCountries.get(delivery.getCountry().getIsoCode());
							System.out.println("$#3014#"); if(dCountry!=null) {
								countryName = dCountry.getName();
							} else {
								countryName = delivery.getCountry().getIsoCode();
							}
						}
							System.out.println("$#3015#"); option.setOptionName(countryName);
					}
				
					System.out.println("$#3016#"); if(shippingOptionPriceType.name().equals(ShippingOptionPriceType.HIGHEST.name())) {

						if (option.getOptionPrice()
								.longValue() > selectedOption
								.getOptionPrice()
								.longValue()) {
							selectedOption = option;
						}
					}

				
					System.out.println("$#3019#"); if(shippingOptionPriceType.name().equals(ShippingOptionPriceType.LEAST.name())) {

						if (option.getOptionPrice()
								.longValue() < selectedOption
								.getOptionPrice()
								.longValue()) {
							selectedOption = option;
						}
					}
					
				
					System.out.println("$#3022#"); if(shippingOptionPriceType.name().equals(ShippingOptionPriceType.ALL.name())) {
	
						if (option.getOptionPrice()
								.longValue() < selectedOption
								.getOptionPrice()
								.longValue()) {
							selectedOption = option;
						}
					}

				}
				
				System.out.println("$#3025#"); shippingQuote.setSelectedShippingOption(selectedOption);
				
				System.out.println("$#3026#"); if(selectedOption!=null && !shippingOptionPriceType.name().equals(ShippingOptionPriceType.ALL.name())) {
					shippingOptions = new ArrayList<ShippingOption>();
					shippingOptions.add(selectedOption);
				}

			}
			
			/** set final delivery address **/
			System.out.println("$#3028#"); shippingQuote.setDeliveryAddress(delivery);
			
			System.out.println("$#3029#"); shippingQuote.setShippingOptions(shippingOptions);
			
			/** post processors **/
			//invoke pre processors
			System.out.println("$#3030#"); if(!CollectionUtils.isEmpty(shippingModulePostProcessors)) {
				for(ShippingQuotePrePostProcessModule postProcessor : shippingModulePostProcessors) {
					//get module info
					
					//get module configuration
					IntegrationConfiguration integrationConfiguration = modules.get(postProcessor.getModuleCode());
					
					IntegrationModule postProcessModule = null;
					for(IntegrationModule mod : shippingMethods) {
						System.out.println("$#3031#"); if(mod.getCode().equals(postProcessor.getModuleCode())){
							postProcessModule = mod;
							break;
						}
					}
					
					IntegrationModule module = postProcessModule;
					System.out.println("$#3032#"); postProcessor.prePostProcessShippingQuotes(shippingQuote, packages, orderTotal, delivery, shippingOrigin, store, integrationConfiguration, module, shippingConfiguration, shippingMethods, locale);
				}
			}
			String ipAddress = null;
	    	UserContext context = UserContext.getCurrentInstance();
						System.out.println("$#3033#"); if(context != null) {
	    		ipAddress = context.getIpAddress();
	    	}
			
			System.out.println("$#3034#"); if(shippingQuote!=null && CollectionUtils.isNotEmpty(shippingQuote.getShippingOptions())) {
				//save SHIPPING OPTIONS
				List<ShippingOption> finalShippingOptions = shippingQuote.getShippingOptions();
				for(ShippingOption option : finalShippingOptions) {
					
					//transform to Quote
					Quote q = new Quote();
					System.out.println("$#3036#"); q.setCartId(shoppingCartId);
					System.out.println("$#3037#"); q.setDelivery(delivery);
					System.out.println("$#3038#"); if(!StringUtils.isBlank(ipAddress)) {
						System.out.println("$#3039#"); q.setIpAddress(ipAddress);
					}
					System.out.println("$#3040#"); if(!StringUtils.isBlank(option.getEstimatedNumberOfDays())) {
						try {
							System.out.println("$#3041#"); q.setEstimatedNumberOfDays(new Integer(option.getEstimatedNumberOfDays()));
						} catch(Exception e) {
							LOGGER.error("Cannot cast to integer " + option.getEstimatedNumberOfDays());
						}
					}
					
					System.out.println("$#3042#"); if(freeShipping) {
						System.out.println("$#3043#"); q.setFreeShipping(true);
						System.out.println("$#3044#"); q.setPrice(new BigDecimal(0));
						System.out.println("$#3045#"); q.setModule("FREE");
						System.out.println("$#3046#"); q.setOptionCode("FREE");
						System.out.println("$#3047#"); q.setOptionName("FREE");
					} else {
						System.out.println("$#3048#"); q.setModule(option.getShippingModuleCode());
						System.out.println("$#3049#"); q.setOptionCode(option.getOptionCode());
						System.out.println("$#3050#"); if(!StringUtils.isBlank(option.getOptionDeliveryDate())) {
							try {
							System.out.println("$#3051#"); q.setOptionDeliveryDate(DateUtil.formatDate(option.getOptionDeliveryDate()));
							} catch(Exception e) {
								LOGGER.error("Cannot transform to date " + option.getOptionDeliveryDate());
							}
						}
						System.out.println("$#3052#"); q.setOptionName(option.getOptionName());
						System.out.println("$#3053#"); q.setOptionShippingDate(new Date());
						System.out.println("$#3054#"); q.setPrice(option.getOptionPrice());
						
					}
					
					System.out.println("$#3055#"); if(handlingFees != null) {
						System.out.println("$#3056#"); q.setHandling(handlingFees);
					}
					
					System.out.println("$#3057#"); q.setQuoteDate(new Date());
					System.out.println("$#3058#"); shippingQuoteService.save(q);
					System.out.println("$#3059#"); option.setShippingQuoteOptionId(q.getId());
					
				}
			}
			
			
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
		
		System.out.println("$#3060#"); return shippingQuote;
		
	}

	@Override
	public List<String> getSupportedCountries(MerchantStore store) throws ServiceException {
		
		List<String> supportedCountries = new ArrayList<String>();
		MerchantConfiguration configuration = merchantConfigurationService.getMerchantConfiguration(SUPPORTED_COUNTRIES, store);
		
		System.out.println("$#3061#"); if(configuration!=null) {
			
			String countries = configuration.getValue();
			System.out.println("$#3062#"); if(!StringUtils.isBlank(countries)) {

				Object objRegions=JSONValue.parse(countries); 
				JSONArray arrayRegions=(JSONArray)objRegions;
				@SuppressWarnings("rawtypes")
				Iterator i = arrayRegions.iterator();
				System.out.println("$#3063#"); while(i.hasNext()) {
					supportedCountries.add((String)i.next());
				}
			}
			
		}
		
		System.out.println("$#3064#"); return supportedCountries;
	}
	
	@Override
	public List<Country> getShipToCountryList(MerchantStore store, Language language) throws ServiceException {
		
		
		ShippingConfiguration shippingConfiguration = getShippingConfiguration(store);
		ShippingType shippingType = ShippingType.INTERNATIONAL;
		List<String> supportedCountries = new ArrayList<String>();
		System.out.println("$#3065#"); if(shippingConfiguration==null) {
			shippingConfiguration = new ShippingConfiguration();
		}
		
		System.out.println("$#3066#"); if(shippingConfiguration.getShippingType()!=null) {
				shippingType = shippingConfiguration.getShippingType();
		}

		
		System.out.println("$#3067#"); if(shippingType.name().equals(ShippingType.NATIONAL.name())){
			
			supportedCountries.add(store.getCountry().getIsoCode());
			
		} else {

			MerchantConfiguration configuration = merchantConfigurationService.getMerchantConfiguration(SUPPORTED_COUNTRIES, store);
			
			System.out.println("$#3068#"); if(configuration!=null) {
				
				String countries = configuration.getValue();
				System.out.println("$#3069#"); if(!StringUtils.isBlank(countries)) {

					Object objRegions=JSONValue.parse(countries); 
					JSONArray arrayRegions=(JSONArray)objRegions;
					@SuppressWarnings("rawtypes")
					Iterator i = arrayRegions.iterator();
					System.out.println("$#3070#"); while(i.hasNext()) {
						supportedCountries.add((String)i.next());
					}
				}
				
			}

		}
		
		System.out.println("$#3071#"); return countryService.getCountries(supportedCountries, language);

	}
	

	@Override
	public void setSupportedCountries(MerchantStore store, List<String> countryCodes) throws ServiceException {
		
		
		//transform a list of string to json entry
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			String value  = mapper.writeValueAsString(countryCodes);
			
			MerchantConfiguration configuration = merchantConfigurationService.getMerchantConfiguration(SUPPORTED_COUNTRIES, store);
			
			System.out.println("$#3072#"); if(configuration==null) {
				configuration = new MerchantConfiguration();
				System.out.println("$#3073#");
				configuration.
				setKey(SUPPORTED_COUNTRIES);
				System.out.println("$#3074#"); configuration.setMerchantStore(store);
			} 
			
			System.out.println("$#3075#"); configuration.setValue(value);

			System.out.println("$#3076#"); merchantConfigurationService.saveOrUpdate(configuration);
			
		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}
	

	private BigDecimal calculateOrderTotal(List<ShippingProduct> products, MerchantStore store) throws Exception {
		
		BigDecimal total = new BigDecimal(0);
		for(ShippingProduct shippingProduct : products) {
			BigDecimal currentPrice = shippingProduct.getFinalPrice().getFinalPrice();
			currentPrice = currentPrice.multiply(new BigDecimal(shippingProduct.getQuantity()));
			total = total.add(currentPrice);
		}
		
		
		System.out.println("$#3077#"); return total;
		
		
	}

	@Override
	public List<PackageDetails> getPackagesDetails(
			List<ShippingProduct> products, MerchantStore store)
			throws ServiceException {
		
		List<PackageDetails> packages = null;
		
		ShippingConfiguration shippingConfiguration = this.getShippingConfiguration(store);
		//determine if the system has to use BOX or ITEM
		ShippingPackageType shippingPackageType = ShippingPackageType.ITEM;
		System.out.println("$#3078#"); if(shippingConfiguration!=null) {
			shippingPackageType = shippingConfiguration.getShippingPackageType();
		}
		
		System.out.println("$#3079#"); if(shippingPackageType.name().equals(ShippingPackageType.BOX.name())){
			packages = packaging.getBoxPackagesDetails(products, store);
		} else {
			packages = packaging.getItemPackagesDetails(products, store);
		}
		
		System.out.println("$#3080#"); return packages;
		
	}

	@Override
	public boolean requiresShipping(List<ShoppingCartItem> items,
			MerchantStore store) throws ServiceException {

		boolean requiresShipping = false;
		for(ShoppingCartItem item : items) {
			Product product = item.getProduct();
			System.out.println("$#3081#"); if(!product.isProductVirtual() && product.isProductShipeable()) {
				requiresShipping = true;
			}
		}

		System.out.println("$#3084#"); System.out.println("$#3083#"); return requiresShipping;
	}

	@Override
	public ShippingMetaData getShippingMetaData(MerchantStore store)
			throws ServiceException {
		
		
		try {
		
		ShippingMetaData metaData = new ShippingMetaData();

		// configured country
		List<Country> countries = getShipToCountryList(store, store.getDefaultLanguage());
		System.out.println("$#3085#"); metaData.setShipToCountry(countries);
		
		// configured modules
		Map<String,IntegrationConfiguration> modules = getShippingModulesConfigured(store);
		List<String> moduleKeys = new ArrayList<String>();
		System.out.println("$#3086#"); if(modules!=null) {
			for(String key : modules.keySet()) {
				moduleKeys.add(key);
			}
		}
		System.out.println("$#3087#"); metaData.setModules(moduleKeys);
		
		// pre processors
		List<ShippingQuotePrePostProcessModule> preProcessors = this.shippingModulePreProcessors;
		List<String> preProcessorKeys = new ArrayList<String>();
		System.out.println("$#3088#"); if(preProcessors!=null) {
			for(ShippingQuotePrePostProcessModule processor : preProcessors) {
				preProcessorKeys.add(processor.getModuleCode());
				System.out.println("$#3089#"); if(SHIPPING_DISTANCE.equals(processor.getModuleCode())) {
					System.out.println("$#3090#"); metaData.setUseDistanceModule(true);
				}
			}
		}
		System.out.println("$#3091#"); metaData.setPreProcessors(preProcessorKeys);
		
		//post processors
		List<ShippingQuotePrePostProcessModule> postProcessors = this.shippingModulePostProcessors;
		List<String> postProcessorKeys = new ArrayList<String>();
		System.out.println("$#3092#"); if(postProcessors!=null) {
			for(ShippingQuotePrePostProcessModule processor : postProcessors) {
				postProcessorKeys.add(processor.getModuleCode());
			}
		}
		System.out.println("$#3093#"); metaData.setPostProcessors(postProcessorKeys);
		
		
		System.out.println("$#3094#"); return metaData;
		
		} catch(Exception e) {
			throw new ServiceException("Exception while getting shipping metadata ",e);
		}
	}

	@Override
	public boolean hasTaxOnShipping(MerchantStore store) throws ServiceException {
		ShippingConfiguration shippingConfiguration = getShippingConfiguration(store);
		System.out.println("$#3096#"); System.out.println("$#3095#"); return shippingConfiguration.isTaxOnShipping();
	}
}
