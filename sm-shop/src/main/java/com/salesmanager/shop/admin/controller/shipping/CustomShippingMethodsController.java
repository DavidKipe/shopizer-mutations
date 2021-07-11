package com.salesmanager.shop.admin.controller.shipping;

import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.shipping.ShippingService;
import com.salesmanager.core.business.utils.ProductPriceUtils;
import com.salesmanager.core.business.utils.ajax.AjaxPageableResponse;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shipping.ShippingConfiguration;
import com.salesmanager.core.model.shipping.ShippingType;
import com.salesmanager.core.model.system.IntegrationConfiguration;
import com.salesmanager.core.modules.integration.IntegrationException;
import com.salesmanager.core.modules.integration.shipping.model.CustomShippingQuoteWeightItem;
import com.salesmanager.core.modules.integration.shipping.model.CustomShippingQuotesConfiguration;
import com.salesmanager.core.modules.integration.shipping.model.CustomShippingQuotesRegion;
import com.salesmanager.shop.admin.controller.ControllerConstants;
import com.salesmanager.shop.admin.model.web.Menu;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.utils.LabelUtils;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

@Controller
public class CustomShippingMethodsController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomShippingMethodsController.class);
	

	public final static String WEIGHT_BASED_SHIPPING_METHOD = "weightBased";
	
	@Inject
	private ShippingService shippingService;
	
	@Inject
	private CountryService countryService;
	
	@Inject
	private ProductPriceUtils priceUtil;
	
	@Inject
	LabelUtils messages;
	

	@PreAuthorize("hasRole('SHIPPING')")
	@RequestMapping(value="/admin/shipping/weightBased.html", method=RequestMethod.GET)
	public String getWeightBasedShippingMethod(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {


		System.out.println("$#7143#"); this.setMenu(model, request);

		System.out.println("$#7144#"); populateModel(model, request, response);

		System.out.println("$#7145#"); return ControllerConstants.Tiles.Shipping.shippingMethod;
		
		
	}
	
	
	@PreAuthorize("hasRole('SHIPPING')")
	@RequestMapping(value="/admin/shipping/addCustomRegion.html", method=RequestMethod.POST)
	public String addCustomRegion(@ModelAttribute("region") String region, BindingResult result, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {

		System.out.println("$#7146#"); this.setMenu(model, request);
		System.out.println("$#7147#"); populateModel(model, request, response);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		CustomShippingQuotesConfiguration customConfiguration = (CustomShippingQuotesConfiguration)shippingService.getCustomShippingConfiguration(WEIGHT_BASED_SHIPPING_METHOD, store);

		List<CustomShippingQuotesRegion> regions = customConfiguration.getRegions();
		
		System.out.println("$#7148#"); if(StringUtils.isBlank(region)) {
			model.addAttribute("errorMessage",messages.getMessage("message.region.null", locale));
			ObjectError error = new ObjectError("region",messages.getMessage("message.region.null", locale));
			System.out.println("$#7149#"); result.addError(error);
		}
		
		
		for(CustomShippingQuotesRegion customRegion : regions) {
			System.out.println("$#7150#"); if(customRegion.getCustomRegionName().equals(region)) {
				model.addAttribute("errorMessage",messages.getMessage("message.region.null", locale));
				ObjectError error = new ObjectError("region",messages.getMessage("message.region.exists", locale));
				System.out.println("$#7151#"); result.addError(error);
				break;
			}
		}
		
		System.out.println("$#7152#"); if (result.hasErrors()) {
			System.out.println("$#7153#"); return ControllerConstants.Tiles.Shipping.shippingMethod;
		}
		
		
		CustomShippingQuotesRegion quoteRegion = new CustomShippingQuotesRegion();
		System.out.println("$#7154#"); quoteRegion.setCustomRegionName(region);
		
		customConfiguration.getRegions().add(quoteRegion);
		System.out.println("$#7155#"); shippingService.saveCustomShippingConfiguration(this.WEIGHT_BASED_SHIPPING_METHOD, customConfiguration, store);
		
		model.addAttribute("customConfiguration", customConfiguration);
		model.addAttribute("success","success");
		
		System.out.println("$#7156#"); return ControllerConstants.Tiles.Shipping.shippingMethod;
	
	}
	
	@PreAuthorize("hasRole('SHIPPING')")
	@RequestMapping(value="/admin/shipping/addCountryToRegion.html", method=RequestMethod.POST)
	public String addCountryToCustomRegion(@ModelAttribute("customRegion") CustomShippingQuotesRegion customRegion, BindingResult result, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {

		System.out.println("$#7157#"); this.setMenu(model, request);
		System.out.println("$#7158#"); populateModel(model, request, response);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		CustomShippingQuotesConfiguration customConfiguration = (CustomShippingQuotesConfiguration)shippingService.getCustomShippingConfiguration(WEIGHT_BASED_SHIPPING_METHOD, store);

		List<CustomShippingQuotesRegion> regions = customConfiguration.getRegions();
		
		
		System.out.println("$#7159#"); if(StringUtils.isBlank(customRegion.getCustomRegionName())) {
			model.addAttribute("errorMessageAssociation",messages.getMessage("message.region.null", locale));
			ObjectError error = new ObjectError("region",messages.getMessage("message.region.exists", locale));
			System.out.println("$#7160#"); result.addError(error);
		}
		
		
		for(CustomShippingQuotesRegion region : regions) {
			System.out.println("$#7161#"); if(region.getCustomRegionName().equals(customRegion.getCustomRegionName())) {
				List<String> countries = region.getCountries();
				System.out.println("$#7162#"); if(countries!=null) {
					for(String countryCode : countries) {
						System.out.println("$#7163#"); if(countryCode.equals(customRegion.getCountries().get(0))) {
							model.addAttribute("errorMessageAssociation",messages.getMessage("message.region.exists", locale));
							ObjectError error = new ObjectError("region",messages.getMessage("message.region.exists", locale));
							System.out.println("$#7164#"); result.addError(error);
							break;
						}
					}
					
					countries.add(customRegion.getCountries().get(0));
					
				} else {
					List<String> countriesList = new ArrayList<String>();
					countriesList.add(customRegion.getCountries().get(0));
					System.out.println("$#7165#"); region.setCountries(countriesList);
				}
			}
		}
		
		System.out.println("$#7166#"); if (result.hasErrors()) {
			System.out.println("$#7167#"); return ControllerConstants.Tiles.Shipping.shippingMethod;
		}
		

		System.out.println("$#7168#"); shippingService.saveCustomShippingConfiguration(this.WEIGHT_BASED_SHIPPING_METHOD, customConfiguration, store);
		model.addAttribute("customConfiguration", customConfiguration);
		model.addAttribute("success","success");
		
		System.out.println("$#7169#"); return ControllerConstants.Tiles.Shipping.shippingMethod;
	
	}
	
	@PreAuthorize("hasRole('SHIPPING')")
	@RequestMapping(value="/admin/shipping/saveweightBasedShippingMethod.html", method=RequestMethod.POST)
	public String saveShippingMethod(@ModelAttribute("configuration") CustomShippingQuotesConfiguration configuration, BindingResult result, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {


		System.out.println("$#7170#"); this.setMenu(model, request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		System.out.println("$#7171#"); populateModel(model, request, response);
		
		String moduleCode = configuration.getModuleCode();
		LOGGER.debug("Saving module code " + moduleCode);
		
		List<String> environments = new ArrayList<String>();
		environments.add(com.salesmanager.core.business.constants.Constants.TEST_ENVIRONMENT);
		environments.add(com.salesmanager.core.business.constants.Constants.PRODUCTION_ENVIRONMENT);

		model.addAttribute("environments", environments);
		model.addAttribute("configuration", configuration);

		try {
			
			
			CustomShippingQuotesConfiguration dbConfig = (CustomShippingQuotesConfiguration) shippingService.getCustomShippingConfiguration(this.WEIGHT_BASED_SHIPPING_METHOD, store);
			
			
			System.out.println("$#7172#"); shippingService.saveShippingQuoteModuleConfiguration(configuration, store);
			System.out.println("$#7173#"); if(dbConfig!=null) {
				System.out.println("$#7174#"); dbConfig.setActive(configuration.isActive());
				System.out.println("$#7175#"); shippingService.saveCustomShippingConfiguration(WEIGHT_BASED_SHIPPING_METHOD, dbConfig, store);
			} else {
				System.out.println("$#7176#"); shippingService.saveCustomShippingConfiguration(WEIGHT_BASED_SHIPPING_METHOD, configuration, store);
			}
			

			
			
		} catch (Exception e) {
			System.out.println("$#7177#"); if(e instanceof IntegrationException) {
				System.out.println("$#7178#"); if(((IntegrationException)e).getErrorCode()==IntegrationException.ERROR_VALIDATION_SAVE) {
					
					List<String> errorCodes = ((IntegrationException)e).getErrorFields();
					for(String errorCode : errorCodes) {
						model.addAttribute(errorCode,messages.getMessage("message.fielderror", locale));
					}
					System.out.println("$#7179#"); return ControllerConstants.Tiles.Shipping.shippingMethod;
				}
			} else {
				throw new Exception(e);
			}
		}
		

		model.addAttribute("success","success");
		System.out.println("$#7180#"); return ControllerConstants.Tiles.Shipping.shippingMethod;
		
		
	}
	
	@PreAuthorize("hasRole('SHIPPING')")
	@RequestMapping(value="/admin/shipping/weightBased/removeCountry.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> deleteCountry(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		String country = request.getParameter("regionCode");

		AjaxResponse resp = new AjaxResponse();


		try {
			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			CustomShippingQuotesConfiguration customConfiguration = (CustomShippingQuotesConfiguration)shippingService.getCustomShippingConfiguration(WEIGHT_BASED_SHIPPING_METHOD, store);

			System.out.println("$#7181#"); if(customConfiguration!=null) {
				
				List<CustomShippingQuotesRegion> quotes = customConfiguration.getRegions();
				for (CustomShippingQuotesRegion quote : quotes) {
						List<String> countries = quote.getCountries();
						List<String> newCountries = new ArrayList<String>();
						System.out.println("$#7182#"); if(countries!=null) {
							for(String cntry : countries) {
								System.out.println("$#7183#"); if(!cntry.equals(country)) {
									newCountries.add(cntry);
								}
							}
						}
						System.out.println("$#7184#"); quote.setCountries(newCountries);
				}
				
			}
			
			System.out.println("$#7185#"); shippingService.saveCustomShippingConfiguration(WEIGHT_BASED_SHIPPING_METHOD, customConfiguration, store);

			System.out.println("$#7186#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);

		} catch (Exception e) {
			LOGGER.error("Error while paging custom weight based", e);
			System.out.println("$#7187#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
		}

		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#7188#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		System.out.println("$#7189#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	
	
	@PreAuthorize("hasRole('SHIPPING')")
	@RequestMapping(value="/admin/shipping/weightBased/removePrice.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> deletePrice(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		String weight = request.getParameter("weight");
		String region = request.getParameter("region");
		int maxWeight = 0;
		try {
			maxWeight = Integer.parseInt(weight);
		} catch (Exception e) {
			LOGGER.error("Weight (integer) malformed " + weight);
		}

		AjaxResponse resp = new AjaxResponse();


		try {
			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			CustomShippingQuotesConfiguration customConfiguration = (CustomShippingQuotesConfiguration)shippingService.getCustomShippingConfiguration(WEIGHT_BASED_SHIPPING_METHOD, store);

			System.out.println("$#7190#"); if(customConfiguration!=null) {
				
				List<CustomShippingQuotesRegion> quotes = customConfiguration.getRegions();
				
				for (CustomShippingQuotesRegion quote : quotes) {
					
					
						System.out.println("$#7191#"); if(quote.getCustomRegionName().equals(region)) {
							List<CustomShippingQuoteWeightItem> quoteItems = quote.getQuoteItems();
							
							System.out.println("$#7192#"); if(quoteItems!=null) {
								List<CustomShippingQuoteWeightItem> newQuoteItems = new ArrayList<CustomShippingQuoteWeightItem>();
								for(CustomShippingQuoteWeightItem q : quoteItems) {
									System.out.println("$#7193#"); if(maxWeight!=q.getMaximumWeight()) {
										newQuoteItems.add(q);
									}
								}
								System.out.println("$#7194#"); quote.setQuoteItems(newQuoteItems);
								break;
							}
						}
				
				}
				
			}
			
			System.out.println("$#7195#"); shippingService.saveCustomShippingConfiguration(WEIGHT_BASED_SHIPPING_METHOD, customConfiguration, store);

			System.out.println("$#7196#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);

		} catch (Exception e) {
			LOGGER.error("Error while paging custom weight based", e);
			System.out.println("$#7197#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
		}

		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#7198#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		System.out.println("$#7199#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	

	@PreAuthorize("hasRole('SHIPPING')")
	@RequestMapping(value="/admin/shipping/deleteWeightBasedShippingMethod.html", method=RequestMethod.POST)
	public String deleteShippingMethod(BindingResult result, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		
		System.out.println("$#7200#"); this.setMenu(model, request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		System.out.println("$#7201#"); shippingService.removeCustomShippingQuoteModuleConfiguration(WEIGHT_BASED_SHIPPING_METHOD, store);
		
		
		System.out.println("$#7202#"); return ControllerConstants.Tiles.Shipping.shippingMethods;
		
	}
	
	/**
	 * Check if a region code already exist with the same name
	 * @param request
	 * @param response
	 * @param locale
	 * @return
	 */
	@PreAuthorize("hasRole('SHIPPING')")
	@RequestMapping(value="/admin/shipping/checkRegionCode.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> checkRegionCode(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		String code = request.getParameter("code");


		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#7203#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		try {
			
			System.out.println("$#7204#"); if(StringUtils.isBlank(code)) {
				System.out.println("$#7205#"); resp.setStatus(AjaxResponse.CODE_ALREADY_EXIST);
				String returnString = resp.toJSONString();
				System.out.println("$#7206#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			CustomShippingQuotesConfiguration customConfiguration = (CustomShippingQuotesConfiguration)shippingService.getCustomShippingConfiguration(WEIGHT_BASED_SHIPPING_METHOD, store);

			System.out.println("$#7207#"); if(customConfiguration!=null) {
				List<CustomShippingQuotesRegion> regions =  customConfiguration.getRegions();
				for(CustomShippingQuotesRegion region : regions) {
					
					System.out.println("$#7208#"); if(code.equals(region.getCustomRegionName())) {
						System.out.println("$#7209#"); resp.setStatus(AjaxResponse.CODE_ALREADY_EXIST);
						String returnString = resp.toJSONString();
						System.out.println("$#7210#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
					}
					
				}
			}

			System.out.println("$#7211#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);

		} catch (Exception e) {
			LOGGER.error("Error while getting user", e);
			System.out.println("$#7212#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#7213#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#7214#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('SHIPPING')")
	@RequestMapping(value = "/admin/shipping/weightBased/page.html", method = RequestMethod.POST)
	public @ResponseBody
	ResponseEntity<String> pageCustomShipping(HttpServletRequest request,
			HttpServletResponse response) {

		AjaxResponse resp = new AjaxResponse();

		Language language = (Language)request.getAttribute("LANGUAGE");
		try {
			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			CustomShippingQuotesConfiguration customConfiguration = (CustomShippingQuotesConfiguration)shippingService.getCustomShippingConfiguration(WEIGHT_BASED_SHIPPING_METHOD, store);
			
			Map<String,Country> countriesMap = countryService.getCountriesMap(language);

			System.out.println("$#7215#"); if(customConfiguration!=null) {
				List<CustomShippingQuotesRegion> quotes = customConfiguration.getRegions();
				for (CustomShippingQuotesRegion quote : quotes) {
						List<String> countries = quote.getCountries();
						System.out.println("$#7216#"); if(countries!=null) {
							for(String country : countries) {
								Map<String,String> entry = new HashMap<String,String> ();
								entry.put("regionCode", country);
								entry.put("region", quote.getCustomRegionName());
								entry.put("country", countriesMap.get(country).getName());
								System.out.println("$#7217#"); resp.addDataEntry(entry);
							}
						}
				}
			}

			System.out.println("$#7218#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_SUCCESS);

		} catch (Exception e) {
			LOGGER.error("Error while paging custom weight based", e);
			System.out.println("$#7219#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
		}

		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#7220#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		System.out.println("$#7221#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}

	
	/**
	 * Edit custom region
	 * @param region
	 * @param model
	 * @param request
	 * @param response
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("hasRole('SHIPPING')")
	@RequestMapping(value="/admin/shipping/weightBased/edit.html", method=RequestMethod.GET)
	public String editCustomShipping(@ModelAttribute("customRegionName") String region, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		
		System.out.println("$#7222#"); setMenu(model,request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		CustomShippingQuotesConfiguration customConfiguration = (CustomShippingQuotesConfiguration)shippingService.getCustomShippingConfiguration(WEIGHT_BASED_SHIPPING_METHOD, store);
		CustomShippingQuotesRegion aRegion = null;

		List<CustomShippingQuotesRegion> regions = customConfiguration.getRegions();
		for(CustomShippingQuotesRegion customRegion : regions) {
			System.out.println("$#7223#"); if(customRegion.getCustomRegionName().equals(region)) {
				aRegion = customRegion;
				break;
			}
		}
		
		System.out.println("$#7224#"); if(aRegion==null) {
			System.out.println("$#7225#"); return "redirect:/admin/shipping/shippingMethods.html";
		}
		
		model.addAttribute("customRegion", aRegion);


		System.out.println("$#7226#"); return ControllerConstants.Tiles.Shipping.customShippingWeightBased;
	}
	
	
	@PreAuthorize("hasRole('SHIPPING')")
	@RequestMapping(value = "/admin/shipping/weightBasedDetails/page.html", method = RequestMethod.POST)
	public @ResponseBody
	ResponseEntity<String> pageCustomShippingDetails(HttpServletRequest request,
			HttpServletResponse response) {
		
		String region = request.getParameter("region");
		
		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#7227#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		System.out.println("$#7228#"); if(StringUtils.isBlank(region)){
			
			System.out.println("$#7229#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#7230#"); resp.setErrorString("Region is not valid");
			String returnString = resp.toJSONString();
			System.out.println("$#7231#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			
		}


		try {
			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			CustomShippingQuotesConfiguration customConfiguration = (CustomShippingQuotesConfiguration)shippingService.getCustomShippingConfiguration(WEIGHT_BASED_SHIPPING_METHOD, store);

			List<CustomShippingQuotesRegion> quotes = customConfiguration.getRegions();
			for (CustomShippingQuotesRegion quote : quotes) {

					System.out.println("$#7232#"); if(quote.getCustomRegionName().equals(region)) {

						List<CustomShippingQuoteWeightItem> quoteItems = quote.getQuoteItems();
						System.out.println("$#7233#"); if(quoteItems!=null) {
							for(CustomShippingQuoteWeightItem quoteItem : quoteItems) {
								Map<String,String> entry = new HashMap<String,String> ();
								entry.put("price", priceUtil.getAdminFormatedAmountWithCurrency(store,quoteItem.getPrice()));
								entry.put("weight", String.valueOf(quoteItem.getMaximumWeight()));
								System.out.println("$#7234#"); resp.addDataEntry(entry);
							}
						}
					}
			}

			System.out.println("$#7235#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_SUCCESS);

		} catch (Exception e) {
			LOGGER.error("Error while paging products", e);
			System.out.println("$#7236#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
		}

		String returnString = resp.toJSONString();
		System.out.println("$#7237#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	@PreAuthorize("hasRole('SHIPPING')")
	@RequestMapping(value="/admin/shipping/weightBased/addPrice.html", method=RequestMethod.POST)
	public String addPrice(@ModelAttribute("region") String customRegion, @ModelAttribute("customQuote") CustomShippingQuoteWeightItem customQuote, BindingResult result, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {

		System.out.println("$#7238#"); this.setMenu(model, request);
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		CustomShippingQuotesConfiguration customConfiguration = (CustomShippingQuotesConfiguration)shippingService.getCustomShippingConfiguration(WEIGHT_BASED_SHIPPING_METHOD, store);

		List<CustomShippingQuotesRegion> regions = customConfiguration.getRegions();
		
		try {
			BigDecimal price = new BigDecimal(customQuote.getPriceText());
			System.out.println("$#7239#"); customQuote.setPrice(price);
		} catch(Exception e) {
			ObjectError error = new ObjectError("priceText",messages.getMessage("message.invalid.price", locale));
			System.out.println("$#7240#"); result.addError(error);
		}
		

		int weight = customQuote.getMaximumWeight();
		System.out.println("$#7242#"); System.out.println("$#7241#"); if(weight<=0) {
			ObjectError error = new ObjectError("maximumWeight",messages.getMessage("message.maximumWeight.invalid", locale));
			System.out.println("$#7243#"); result.addError(error);
		}

		

		for(CustomShippingQuotesRegion customReg : regions) {
			System.out.println("$#7244#"); if(customReg.getCustomRegionName().equals(customRegion)) {
				model.addAttribute("customRegion", customReg);
				break;
			}
		}
		

		System.out.println("$#7245#"); if(StringUtils.isBlank(customQuote.getPriceText())) {
			ObjectError error = new ObjectError("priceText",messages.getMessage("message.invalid.price", locale));
			System.out.println("$#7246#"); result.addError(error);
		}
		
		CustomShippingQuotesRegion currentRegion = null;
		
		
		for(CustomShippingQuotesRegion region : regions) {
			System.out.println("$#7247#"); if(region.getCustomRegionName().equals(customRegion)) {
				currentRegion = region;
				List<CustomShippingQuoteWeightItem> quotes = region.getQuoteItems();
				System.out.println("$#7248#"); if(quotes!=null) {
					for(CustomShippingQuoteWeightItem quote : quotes) {
						
						System.out.println("$#7249#"); if(quote.getMaximumWeight()==customQuote.getMaximumWeight()){
							ObjectError error = new ObjectError("maximumWeight",messages.getMessage("label.message.maximumWeight.exist", locale));
							System.out.println("$#7250#"); result.addError(error);
							break;
						} 
					}
					quotes.add(customQuote);
				} else {
					quotes = new ArrayList<CustomShippingQuoteWeightItem>();
					quotes.add(customQuote);
					System.out.println("$#7251#"); region.setQuoteItems(quotes);
				}
			}
		}
		
		model.addAttribute("customConfiguration", customConfiguration);
		
		System.out.println("$#7252#"); if (result.hasErrors()) {
			System.out.println("$#7253#"); return ControllerConstants.Tiles.Shipping.customShippingWeightBased;
		}
		
		//order weights
		System.out.println("$#7254#"); if(currentRegion!=null) {
			List<CustomShippingQuoteWeightItem> quotes = currentRegion.getQuoteItems();
			System.out.println("$#7255#"); if(quotes!=null) {
				
				
				BeanComparator beanComparator = new BeanComparator("maximumWeight");
				System.out.println("$#7256#"); Collections.sort(quotes, beanComparator);
				

			}
		}
		
		
		System.out.println("$#7257#"); shippingService.saveCustomShippingConfiguration(this.WEIGHT_BASED_SHIPPING_METHOD, customConfiguration, store);
		
		model.addAttribute("success","success");
		
		System.out.println("$#7258#"); return ControllerConstants.Tiles.Shipping.customShippingWeightBased;
	
	}
	
	@PreAuthorize("hasRole('SHIPPING')")
	@RequestMapping(value="/admin/shipping/weightBased/deleteRegion.html", method=RequestMethod.POST)
	public String deleteRegion(@ModelAttribute("customRegionName") String region, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		
		System.out.println("$#7259#"); this.setMenu(model, request);
		
		
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		
		CustomShippingQuotesConfiguration customConfiguration = (CustomShippingQuotesConfiguration)shippingService.getCustomShippingConfiguration(WEIGHT_BASED_SHIPPING_METHOD, store);

		List<CustomShippingQuotesRegion> regions = customConfiguration.getRegions();
		
		List<CustomShippingQuotesRegion> newRegions = new ArrayList<CustomShippingQuotesRegion>();
		for(CustomShippingQuotesRegion reg : regions) {

			System.out.println("$#7260#"); if(!reg.getCustomRegionName().equals(region)) {
				newRegions.add(reg);
			}
		}

		System.out.println("$#7261#"); customConfiguration.setRegions(newRegions);
		System.out.println("$#7262#"); shippingService.saveCustomShippingConfiguration(this.WEIGHT_BASED_SHIPPING_METHOD, customConfiguration, store);
		System.out.println("$#7263#"); populateModel(model, request, response);
		model.addAttribute("success","success");
		System.out.println("$#7264#"); return ControllerConstants.Tiles.Shipping.shippingMethod;
	}
	
	
	
	

	private void populateModel(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		Language language = (Language)request.getAttribute("LANGUAGE");
		
		ShippingConfiguration shippingConfiguration =  shippingService.getShippingConfiguration(store);
		
		System.out.println("$#7265#"); if(shippingConfiguration==null) {
			shippingConfiguration = new ShippingConfiguration();
			System.out.println("$#7266#"); shippingConfiguration.setShippingType(ShippingType.INTERNATIONAL);
		}
		

		//get configured shipping modules
		Map<String,IntegrationConfiguration> configuredModules = shippingService.getShippingModulesConfigured(store);
		IntegrationConfiguration configuration = new IntegrationConfiguration();
		System.out.println("$#7267#"); if(configuredModules!=null) {
			for(String key : configuredModules.keySet()) {
				System.out.println("$#7268#"); if(key.equals(WEIGHT_BASED_SHIPPING_METHOD)) {
					configuration = configuredModules.get(key);
					break;
				}
			}
		}
		System.out.println("$#7269#"); configuration.setModuleCode(WEIGHT_BASED_SHIPPING_METHOD);
		
		//get custom information
		CustomShippingQuotesConfiguration customConfiguration = (CustomShippingQuotesConfiguration)shippingService.getCustomShippingConfiguration(WEIGHT_BASED_SHIPPING_METHOD, store);

		System.out.println("$#7270#"); if(customConfiguration==null) {
			customConfiguration = new CustomShippingQuotesConfiguration();
			System.out.println("$#7271#"); customConfiguration.setModuleCode(this.WEIGHT_BASED_SHIPPING_METHOD);
		}
		
		
		//get supported countries
		//List<String> includedCountries = shippingService.getSupportedCountries(store);
		List<Country> shipToCountries = shippingService.getShipToCountryList(store, language);
/*		List<Country> shippingCountries = new ArrayList<Country>();
		Map<String,Country> countries = countryService.getCountriesMap(language);
		if(shippingConfiguration.getShippingType().name().equals(ShippingType.INTERNATIONAL.name())){
			
			for(String key : countries.keySet()) {
				Country country = (Country)countries.get(key);
				if(includedCountries.contains(key)) {
					shippingCountries.add(country);
				}
			}
		} else {//if national only store country
			if(!includedCountries.contains(store.getCountry().getIsoCode())) {
				shippingCountries.add((Country)countries.get(store.getCountry().getIsoCode()));
			}
		}*/
		
		CustomShippingQuotesRegion customRegion = new CustomShippingQuotesRegion();
		
		
		List<String> environments = new ArrayList<String>();
		environments.add(com.salesmanager.core.business.constants.Constants.PRODUCTION_ENVIRONMENT);//only production
		
		model.addAttribute("environments", environments);
		model.addAttribute("configuration", configuration);
		model.addAttribute("customConfiguration", customConfiguration);
		model.addAttribute("customRegion", customRegion);
		model.addAttribute("shippingCountries", shipToCountries);

		
	}
	
	private void setMenu(Model model, HttpServletRequest request) throws Exception {
		
		//display menu
		Map<String,String> activeMenus = new HashMap<String,String>();
		activeMenus.put("shipping", "shipping");
		activeMenus.put("shipping-methods", "shipping-methods");
		
		@SuppressWarnings("unchecked")
		Map<String, Menu> menus = (Map<String, Menu>)request.getAttribute("MENUMAP");
		
		Menu currentMenu = (Menu)menus.get("shipping");
		model.addAttribute("currentMenu",currentMenu);
		model.addAttribute("activeMenus",activeMenus);
		//
		
	}


}
