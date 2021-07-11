package com.salesmanager.shop.admin.controller.shipping;

import com.salesmanager.core.business.services.shipping.ShippingService;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.system.IntegrationConfiguration;
import com.salesmanager.core.model.system.IntegrationModule;
import com.salesmanager.core.modules.integration.IntegrationException;
import com.salesmanager.core.modules.integration.shipping.model.CustomShippingQuotesConfiguration;
import com.salesmanager.shop.admin.controller.ControllerConstants;
import com.salesmanager.shop.admin.model.web.Menu;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.utils.LabelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
public class ShippingMethodsController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ShippingMethodsController.class);
	

	@Inject
	private ShippingService shippingService;
	
	@Inject
	LabelUtils messages;
	
	/**
	 * Configures the shipping shows shipping methods
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("hasRole('SHIPPING')")
	@RequestMapping(value="/admin/shipping/shippingMethods.html", method=RequestMethod.GET)
	public String displayShippingMethods(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {


		System.out.println("$#7297#"); this.setMenu(model, request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		//get shipping methods
		List<IntegrationModule> modules = shippingService.getShippingMethods(store);

		//get configured shipping modules
		Map<String,IntegrationConfiguration> configuredModules = shippingService.getShippingModulesConfigured(store);
		


		model.addAttribute("modules", modules);
		model.addAttribute("configuredModules", configuredModules);
	
		
		System.out.println("$#7298#"); return ControllerConstants.Tiles.Shipping.shippingMethods;
		
		
	}
	@PreAuthorize("hasRole('SHIPPING')")
	@RequestMapping(value="/admin/shipping/shippingMethod.html", method=RequestMethod.GET)
	public String displayShippingMethod(@RequestParam("code") String code, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {


		System.out.println("$#7299#"); this.setMenu(model, request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		

		//get configured shipping modules
		IntegrationConfiguration configuration = shippingService.getShippingConfiguration(code, store);
		System.out.println("$#7300#"); if(configuration==null) {
			configuration = new IntegrationConfiguration();
		}
		
		System.out.println("$#7301#"); configuration.setModuleCode(code);
		
		List<String> environments = new ArrayList<String>();
		environments.add(com.salesmanager.core.business.constants.Constants.TEST_ENVIRONMENT);
		environments.add(com.salesmanager.core.business.constants.Constants.PRODUCTION_ENVIRONMENT);
		
		model.addAttribute("configuration", configuration);
		model.addAttribute("environments", environments);
		System.out.println("$#7302#"); return ControllerConstants.Tiles.Shipping.shippingMethod;
		
		
	}
	@PreAuthorize("hasRole('SHIPPING')")
	@RequestMapping(value="/admin/shipping/saveShippingMethod.html", method=RequestMethod.POST)
	public String saveShippingMethod(@ModelAttribute("configuration") IntegrationConfiguration configuration, BindingResult result, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {


		System.out.println("$#7303#"); this.setMenu(model, request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		String moduleCode = configuration.getModuleCode();
		LOGGER.debug("Saving module code " + moduleCode);
		
		List<String> environments = new ArrayList<String>();
		environments.add(com.salesmanager.core.business.constants.Constants.TEST_ENVIRONMENT);
		environments.add(com.salesmanager.core.business.constants.Constants.PRODUCTION_ENVIRONMENT);

		model.addAttribute("environments", environments);
		model.addAttribute("configuration", configuration);

		try {
			System.out.println("$#7304#"); shippingService.saveShippingQuoteModuleConfiguration(configuration, store);
		} catch (Exception e) {
			System.out.println("$#7305#"); if(e instanceof IntegrationException) {
				System.out.println("$#7306#"); if(((IntegrationException)e).getErrorCode()==IntegrationException.ERROR_VALIDATION_SAVE) {
					
					List<String> errorCodes = ((IntegrationException)e).getErrorFields();
					for(String errorCode : errorCodes) {
						model.addAttribute(errorCode,messages.getMessage("message.fielderror", locale));
					}
					model.addAttribute("validationError","validationError");
					System.out.println("$#7307#"); return ControllerConstants.Tiles.Shipping.shippingMethod;
				}
			} else {
				throw new Exception(e);
			}
		}
		
		
		
		model.addAttribute("success","success");
		System.out.println("$#7308#"); return ControllerConstants.Tiles.Shipping.shippingMethod;
		
		
	}
	
	@PreAuthorize("hasRole('SHIPPING')")
	@RequestMapping(value="/admin/shipping/weightBased.html", method=RequestMethod.POST)
	public String saveCustomWeightBasedShippingMethod(@ModelAttribute("configuration") CustomShippingQuotesConfiguration configuration, BindingResult result, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {

		model.addAttribute("success","success");
		System.out.println("$#7309#"); return ControllerConstants.Tiles.Shipping.shippingMethod;

	}
	
	@RequestMapping(value="/admin/shipping/deleteShippingMethod.html", method=RequestMethod.POST)
	public String deleteShippingMethod(@RequestParam("code") String code, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		
		System.out.println("$#7310#"); this.setMenu(model, request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		System.out.println("$#7311#"); shippingService.removeShippingQuoteModuleConfiguration(code, store);
		
		System.out.println("$#7312#"); return "redirect:/admin/shipping/shippingMethods.html";
		
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
