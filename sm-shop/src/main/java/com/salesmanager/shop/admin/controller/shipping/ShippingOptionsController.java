package com.salesmanager.shop.admin.controller.shipping;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.shipping.ShippingConfiguration;
import com.salesmanager.core.model.shipping.ShippingType;
import com.salesmanager.core.business.services.shipping.ShippingService;
import com.salesmanager.core.business.utils.ProductPriceUtils;
import com.salesmanager.shop.admin.controller.ControllerConstants;
import com.salesmanager.shop.admin.model.web.Menu;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.utils.LabelUtils;

@Controller
public class ShippingOptionsController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ShippingOptionsController.class);
	

	@Inject
	private ShippingService shippingService;
	
	@Inject
	LabelUtils messages;
	
	@Inject
	private ProductPriceUtils priceUtil;
	
	/**
	 * Displays shipping options
	 * @param request
	 * @param response
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("hasRole('SHIPPING')")
	@RequestMapping(value="/admin/shipping/shippingOptions.html", method=RequestMethod.GET)
	public String displayShippingOptions(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {


		System.out.println("$#7313#"); this.setMenu(model, request);

		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		

		
		ShippingConfiguration shippingConfiguration =  shippingService.getShippingConfiguration(store);
		
		System.out.println("$#7314#"); if(shippingConfiguration==null) {
			shippingConfiguration = new ShippingConfiguration();
			System.out.println("$#7315#"); shippingConfiguration.setShippingType(ShippingType.INTERNATIONAL);
		}
		
		System.out.println("$#7316#"); if(shippingConfiguration!=null) {
			
			System.out.println("$#7317#"); if(shippingConfiguration.getHandlingFees()!=null) {
				System.out.println("$#7318#"); shippingConfiguration.setHandlingFeesText(priceUtil.getAdminFormatedAmount(store,shippingConfiguration.getHandlingFees()));
			}
			
			System.out.println("$#7319#"); if(shippingConfiguration.getOrderTotalFreeShipping()!=null) {
				System.out.println("$#7320#"); shippingConfiguration.setOrderTotalFreeShippingText(priceUtil.getAdminFormatedAmount(store,shippingConfiguration.getOrderTotalFreeShipping()));
			}
			
		}
		

		model.addAttribute("configuration", shippingConfiguration);
		System.out.println("$#7321#"); return ControllerConstants.Tiles.Shipping.shippingOptions;
		
		
	}
	
	/**
	 * Saves shipping options
	 * @param configuration
	 * @param result
	 * @param model
	 * @param request
	 * @param response
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("hasRole('SHIPPING')")
	@RequestMapping(value="/admin/shipping/saveShippingOptions.html", method=RequestMethod.POST)
	public String saveShippingOptions(@ModelAttribute("configuration") ShippingConfiguration configuration, BindingResult result, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {


		System.out.println("$#7322#"); this.setMenu(model, request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		//get original configuration
		ShippingConfiguration shippingConfiguration =  shippingService.getShippingConfiguration(store);
		
		System.out.println("$#7323#"); if(shippingConfiguration==null) {
			shippingConfiguration = new ShippingConfiguration();
		}
		
		BigDecimal submitedOrderPrice = null;
		System.out.println("$#7324#"); if(!StringUtils.isBlank(configuration.getOrderTotalFreeShippingText())){
			try {
				submitedOrderPrice = priceUtil.getAmount(configuration.getOrderTotalFreeShippingText());
				System.out.println("$#7325#"); shippingConfiguration.setOrderTotalFreeShipping(submitedOrderPrice);
			} catch (Exception e) {
				ObjectError error = new ObjectError("orderTotalFreeShippingText",messages.getMessage("message.invalid.price", locale));
				System.out.println("$#7326#"); result.addError(error);
			}
		}
		
		BigDecimal submitedHandlingPrice = null;
		System.out.println("$#7327#"); if(!StringUtils.isBlank(configuration.getHandlingFeesText())){
			try {
				submitedHandlingPrice = priceUtil.getAmount(configuration.getHandlingFeesText());
				System.out.println("$#7328#"); shippingConfiguration.setHandlingFees(submitedHandlingPrice);
			} catch (Exception e) {
				ObjectError error = new ObjectError("handlingFeesText",messages.getMessage("message.invalid.price", locale));
				System.out.println("$#7329#"); result.addError(error);
			}
		}
		
		System.out.println("$#7330#"); shippingConfiguration.setFreeShippingEnabled(configuration.isFreeShippingEnabled());
		System.out.println("$#7331#"); shippingConfiguration.setTaxOnShipping(configuration.isTaxOnShipping());
		System.out.println("$#7332#"); if(configuration.getShipFreeType()!=null) {
			System.out.println("$#7333#"); shippingConfiguration.setShipFreeType(configuration.getShipFreeType());
		}
		System.out.println("$#7334#"); shippingConfiguration.setShipOptionPriceType(configuration.getShipOptionPriceType());

		System.out.println("$#7335#"); shippingService.saveShippingConfiguration(shippingConfiguration, store);
		
		model.addAttribute("configuration", configuration);
		model.addAttribute("success","success");
		System.out.println("$#7336#"); return ControllerConstants.Tiles.Shipping.shippingOptions;
		
		
	}
	
	private void setMenu(Model model, HttpServletRequest request) throws Exception {
		
		//display menu
		Map<String,String> activeMenus = new HashMap<String,String>();
		activeMenus.put("shipping", "shipping");
		activeMenus.put("shipping-options", "shipping-options");
		
		@SuppressWarnings("unchecked")
		Map<String, Menu> menus = (Map<String, Menu>)request.getAttribute("MENUMAP");
		
		Menu currentMenu = (Menu)menus.get("shipping");
		model.addAttribute("currentMenu",currentMenu);
		model.addAttribute("activeMenus",activeMenus);
		//
		
	}


}
