package com.salesmanager.shop.store.controller.items;

import com.salesmanager.core.business.services.catalog.product.manufacturer.ManufacturerService;
import com.salesmanager.core.model.catalog.product.manufacturer.Manufacturer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.catalog.manufacturer.ReadableManufacturer;
import com.salesmanager.shop.model.shop.PageInformation;
import com.salesmanager.shop.populator.manufacturer.ReadableManufacturerPopulator;
import com.salesmanager.shop.store.controller.ControllerConstants;
import com.salesmanager.shop.utils.PageBuilderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * Drives various product listings
 * @author carlsamson
 *
 */
@Controller
public class ListItemsController {
	
	@Inject
	ManufacturerService manufacturerService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ListItemsController.class);
	
	@RequestMapping("/shop/listing/{url}.html")
	public String displayListingPage(@PathVariable String url, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
		
		Language language = (Language)request.getAttribute("LANGUAGE");
		
		//Manufacturer manufacturer = manufacturerService.getByUrl(store, language, url); // this needs to be checked

		Manufacturer manufacturer =null;
		
		System.out.println("$#12758#"); if(manufacturer==null) {
			LOGGER.error("No manufacturer found for url " + url);
			//redirect on page not found
			System.out.println("$#12759#"); return PageBuilderUtils.build404(store);
			
		}
		
		ReadableManufacturer readableManufacturer = new ReadableManufacturer();
		
		ReadableManufacturerPopulator populator = new ReadableManufacturerPopulator();
		readableManufacturer = populator.populate(manufacturer, readableManufacturer, store, language);
		
		//meta information
		PageInformation pageInformation = new PageInformation();
		System.out.println("$#12760#"); pageInformation.setPageDescription(readableManufacturer.getDescription().getMetaDescription());
		System.out.println("$#12761#"); pageInformation.setPageKeywords(readableManufacturer.getDescription().getKeyWords());
		System.out.println("$#12762#"); pageInformation.setPageTitle(readableManufacturer.getDescription().getTitle());
		System.out.println("$#12763#"); pageInformation.setPageUrl(readableManufacturer.getDescription().getFriendlyUrl());
		
		model.addAttribute("manufacturer", readableManufacturer);
		
		/** template **/
		StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Items.items_manufacturer).append(".").append(store.getStoreTemplate());

		System.out.println("$#12764#"); return template.toString();
	}
	

}
