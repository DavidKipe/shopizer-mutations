package com.salesmanager.shop.admin.controller.shipping;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.shipping.ShippingService;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shipping.ShippingConfiguration;
import com.salesmanager.core.model.shipping.ShippingType;
import com.salesmanager.shop.admin.model.web.Menu;
import com.salesmanager.shop.constants.Constants;


@Controller
public class ShippingConfigsController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ShippingConfigsController.class);

	
	@Inject
	private ShippingService shippingService;
	
	@Inject
	private CountryService countryService;
	
	/**
	 * Configures the shipping mode, shows shipping countries
	 * @param request
	 * @param response
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("hasRole('SHIPPING')")
	@RequestMapping(value="/admin/shipping/shippingConfigs.html", method=RequestMethod.GET)
	public String displayShippingConfigs(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		System.out.println("$#7272#"); this.setMenu(model, request);

		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		

		
		ShippingConfiguration shippingConfiguration =  shippingService.getShippingConfiguration(store);
		
		System.out.println("$#7273#"); if(shippingConfiguration==null) {
			shippingConfiguration = new ShippingConfiguration();
			System.out.println("$#7274#"); shippingConfiguration.setShippingType(ShippingType.INTERNATIONAL);
		}
		

		model.addAttribute("configuration", shippingConfiguration);
		System.out.println("$#7275#"); return "shipping-configs";
		
		
	}
	
	@PreAuthorize("hasRole('SHIPPING')")
	@RequestMapping(value="/admin/shipping/saveShippingConfiguration.html", method=RequestMethod.POST)
	public String saveShippingConfiguration(@ModelAttribute("configuration") ShippingConfiguration configuration, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		
		System.out.println("$#7276#"); this.setMenu(model, request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		//get original configuration
		ShippingConfiguration shippingConfiguration =  shippingService.getShippingConfiguration(store);
		
		System.out.println("$#7277#"); if(shippingConfiguration==null) {
			shippingConfiguration = new ShippingConfiguration();
		}
		
		System.out.println("$#7278#"); shippingConfiguration.setShippingType(configuration.getShippingType());
		
		System.out.println("$#7279#"); shippingService.saveShippingConfiguration(shippingConfiguration, store);
		
		model.addAttribute("configuration", shippingConfiguration);
		model.addAttribute("success","success");
		System.out.println("$#7280#"); return "shipping-configs";
		
	}
	
	@SuppressWarnings({ "unchecked"})
	@PreAuthorize("hasRole('SHIPPING')")
	@RequestMapping(value="/admin/shipping/countries/paging.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> pageCountries(HttpServletRequest request, HttpServletResponse response) {
		String countryName = request.getParameter("name");
		AjaxResponse resp = new AjaxResponse();

		try {
			
			Language language = (Language)request.getAttribute("LANGUAGE");
			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			
			//get list of countries
			Map<String,Country> countries = countryService.getCountriesMap(language);
			
			//get inclusions
			List<String> includedCountries = shippingService.getSupportedCountries(store);
			

			for(String key : countries.keySet()) {
				
				Country country = (Country)countries.get(key);

				@SuppressWarnings("rawtypes")
				Map entry = new HashMap();
				entry.put("code", country.getIsoCode());
				entry.put("name", country.getName());
				
				System.out.println("$#7281#"); if(includedCountries.contains(key)) {
					entry.put("supported", true);
				} else {
					entry.put("supported", false);
				}
				
				System.out.println("$#7282#"); if(!StringUtils.isBlank(countryName)) {
					System.out.println("$#7283#"); if(country.getName().contains(countryName)){
						System.out.println("$#7284#"); resp.addDataEntry(entry);
					}
				} else {
					System.out.println("$#7285#"); resp.addDataEntry(entry);
				}
			}
			
			System.out.println("$#7286#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_SUCCESS);

		} catch (Exception e) {
			LOGGER.error("Error while paging shipping countries", e);
			System.out.println("$#7287#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
		}
		
		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#7288#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		System.out.println("$#7289#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('SHIPPING')")
	@RequestMapping(value="/admin/shipping/countries/update.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> updateCountry(HttpServletRequest request, HttpServletResponse response) {
		String values = request.getParameter("_oldValues");
		String supported = request.getParameter("supported");
		
		
		

		
		
		AjaxResponse resp = new AjaxResponse();

		try {
			
			ObjectMapper mapper = new ObjectMapper();
			@SuppressWarnings("rawtypes")
			Map conf = mapper.readValue(values, Map.class);
			
			String countryCode = (String)conf.get("code");

			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			
			//get list of countries
			List<String> includedCountries = shippingService.getSupportedCountries(store);
			
			System.out.println("$#7290#"); if(!StringUtils.isBlank(supported)) {
				System.out.println("$#7291#"); if("true".equals(supported)) {
					includedCountries.add(countryCode);
				} else {
					includedCountries.remove(countryCode);
				}
			}
			
			
			System.out.println("$#7292#"); shippingService.setSupportedCountries(store, includedCountries);
			
			System.out.println("$#7293#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);
			

		
		} catch (Exception e) {
			LOGGER.error("Error while paging shipping countries", e);
			System.out.println("$#7294#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
		}
		
		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#7295#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		System.out.println("$#7296#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	
	private void setMenu(Model model, HttpServletRequest request) throws Exception {
		
		//display menu
		Map<String,String> activeMenus = new HashMap<String,String>();
		activeMenus.put("shipping", "shipping");
		activeMenus.put("shipping-configs", "shipping-configs");
		
		@SuppressWarnings("unchecked")
		Map<String, Menu> menus = (Map<String, Menu>)request.getAttribute("MENUMAP");
		
		Menu currentMenu = (Menu)menus.get("shipping");
		model.addAttribute("currentMenu",currentMenu);
		model.addAttribute("activeMenus",activeMenus);
		//
		
	}
	

}
