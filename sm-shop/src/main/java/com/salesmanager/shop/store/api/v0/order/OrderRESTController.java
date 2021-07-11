package com.salesmanager.shop.store.api.v0.order;

import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductAttributeService;
import com.salesmanager.core.business.services.catalog.product.file.DigitalProductService;
import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.services.customer.attribute.CustomerOptionService;
import com.salesmanager.core.business.services.customer.attribute.CustomerOptionValueService;
import com.salesmanager.core.business.services.merchant.MerchantStoreService;
import com.salesmanager.core.business.services.order.OrderService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.services.user.GroupService;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.customer.PersistableCustomer;
import com.salesmanager.shop.model.order.v0.PersistableOrder;
import com.salesmanager.shop.model.order.v0.ReadableOrderList;
import com.salesmanager.shop.populator.customer.CustomerPopulator;
import com.salesmanager.shop.populator.order.PersistableOrderPopulator;
import com.salesmanager.shop.store.controller.order.facade.OrderFacade;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/services/private")
public class OrderRESTController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OrderRESTController.class);
	
	
	@Inject
	private MerchantStoreService merchantStoreService;
	
	@Inject
	private ProductService productService;
	
	@Inject
	private ProductAttributeService productAttributeService;
	
	@Inject
	private DigitalProductService digitalProductService;
	
	@Inject
	private OrderFacade orderFacade;
	
	@Inject
	private OrderService orderService;
	
	@Inject
	private CustomerService customerService;
	
	@Inject
	private LanguageService languageService;
	
	@Inject
	private CustomerOptionService customerOptionService;
	
	@Inject
	private ZoneService zoneService;
	
	@Inject
	private CustomerOptionValueService customerOptionValueService;
	
	@Inject
	private CountryService countryService;
	
	@Inject
	private GroupService   groupService;
	
	@Autowired
	private CustomerPopulator customerPopulator;

	/**
	 * This method is for adding order to the system. Generally used for the purpose of migration only
	 * This method won't process any payment nor create transactions
	 * @param store
	 * @param order
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * Use v1 methods
	 */
	@RequestMapping( value="/{store}/order", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@Deprecated
	public PersistableOrder createOrder(@PathVariable final String store, @Valid @RequestBody PersistableOrder order, HttpServletRequest request, HttpServletResponse response) throws Exception {
		MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
		System.out.println("$#11287#"); if(merchantStore!=null) {
			System.out.println("$#11288#"); if(!merchantStore.getCode().equals(store)) {
				merchantStore = null;
			}
		}
		
		System.out.println("$#11289#"); if(merchantStore== null) {
			merchantStore = merchantStoreService.getByCode(store);
		}
		
		System.out.println("$#11290#"); if(merchantStore==null) {
			LOGGER.error("Merchant store is null for code " + store);
			System.out.println("$#11291#"); response.sendError(503, "Merchant store is null for code " + store);
			return null;
		}
		
		
		PersistableCustomer cust = order.getCustomer();
		System.out.println("$#11292#"); if(cust!=null) {
		    Customer customer = new Customer();
/*			CustomerPopulator populator = new CustomerPopulator();
			populator.setCountryService(countryService);
			populator.setCustomerOptionService(customerOptionService);
			populator.setCustomerOptionValueService(customerOptionValueService);
			populator.setLanguageService(languageService);
			populator.setZoneService(zoneService);
			populator.setGroupService(groupService);*/
			customerPopulator.populate(cust, customer, merchantStore, merchantStore.getDefaultLanguage());
			System.out.println("$#11293#"); customerService.save(customer);
			System.out.println("$#11294#"); cust.setId(customer.getId());
		}
		
		
		Order modelOrder = new Order();
		PersistableOrderPopulator populator = new PersistableOrderPopulator();
		System.out.println("$#11295#"); populator.setDigitalProductService(digitalProductService);
		System.out.println("$#11296#"); populator.setProductAttributeService(productAttributeService);
		System.out.println("$#11297#"); populator.setProductService(productService);
		
		populator.populate(order, modelOrder, merchantStore, merchantStore.getDefaultLanguage());
		
	
		System.out.println("$#11298#"); orderService.save(modelOrder);
		System.out.println("$#11299#"); order.setId(modelOrder.getId());
		
		System.out.println("$#11300#"); return order;
	}
	
	
	/**
	 * Get a list of orders
	 * accept request parameter 'lang' [en,fr...] otherwise store dafault language
	 * accept request parameter 'start' start index for count
	 * accept request parameter 'max' maximum number count, otherwise returns all
	 * @param store
	 * @param order
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping( value="/{store}/orders/", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.ACCEPTED)
	@ResponseBody
	public ReadableOrderList listOrders(@PathVariable final String store, HttpServletRequest request, HttpServletResponse response) throws Exception {
		MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
		System.out.println("$#11301#"); if(merchantStore!=null) {
			System.out.println("$#11302#"); if(!merchantStore.getCode().equals(store)) {
				merchantStore = null;
			}
		}
		
		System.out.println("$#11303#"); if(merchantStore== null) {
			merchantStore = merchantStoreService.getByCode(store);
		}
		
		System.out.println("$#11304#"); if(merchantStore==null) {
			LOGGER.error("Merchant store is null for code " + store);
			System.out.println("$#11305#"); response.sendError(503, "Merchant store is null for code " + store);
			return null;
		}
		
		//get additional request parameters for orders
		String lang = request.getParameter(Constants.LANG);		
		String start = request.getParameter(Constants.START);
		String max = request.getParameter(Constants.MAX);
		
		int startCount = 0;
		int maxCount = 0;
		
		System.out.println("$#11306#"); if(StringUtils.isBlank(lang)) {
			lang = merchantStore.getDefaultLanguage().getCode();
		}
		
		
		Language language = languageService.getByCode(lang);
		
		System.out.println("$#11307#"); if(language==null) {
			LOGGER.error("Language is null for code " + lang);
			System.out.println("$#11308#"); response.sendError(503, "Language is null for code " + lang);
			return null;
		}
		
		try {
			startCount = Integer.parseInt(start);
		} catch (Exception e) {
			LOGGER.info("Invalid value for start " + start);
		}
		
		try {
			maxCount = Integer.parseInt(max);
		} catch (Exception e) {
			LOGGER.info("Invalid value for max " + max);
		}
		
		
		
		ReadableOrderList returnList = orderFacade.getReadableOrderList(merchantStore, startCount, maxCount, language);

		System.out.println("$#11309#"); return returnList;
	}
	
	/**
	 * Get a list of orders for a given customer
	 * accept request parameter 'lang' [en,fr...] otherwise store dafault language
	 * accept request parameter 'start' start index for count
	 * accept request parameter 'max' maximum number count, otherwise returns all
	 * @param store
	 * @param order
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping( value="/{store}/orders/customer/{id}", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.ACCEPTED)
	@ResponseBody
	public ReadableOrderList listOrders(@PathVariable final String store, @PathVariable final Long id, HttpServletRequest request, HttpServletResponse response) throws Exception {
		MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
		System.out.println("$#11310#"); if(merchantStore!=null) {
			System.out.println("$#11311#"); if(!merchantStore.getCode().equals(store)) {
				merchantStore = null;
			}
		}
		
		System.out.println("$#11312#"); if(merchantStore== null) {
			merchantStore = merchantStoreService.getByCode(store);
		}
		
		System.out.println("$#11313#"); if(merchantStore==null) {
			LOGGER.error("Merchant store is null for code " + store);
			System.out.println("$#11314#"); response.sendError(503, "Merchant store is null for code " + store);
			return null;
		}
		
		//get additional request parameters for orders
		String lang = request.getParameter(Constants.LANG);		
		String start = request.getParameter(Constants.START);
		String max = request.getParameter(Constants.MAX);
		
		int startCount = 0;
		int maxCount = 0;
		
		System.out.println("$#11315#"); if(StringUtils.isBlank(lang)) {
			lang = merchantStore.getDefaultLanguage().getCode();
		}
		
		
		Language language = languageService.getByCode(lang);
		
		System.out.println("$#11316#"); if(language==null) {
			LOGGER.error("Language is null for code " + lang);
			System.out.println("$#11317#"); response.sendError(503, "Language is null for code " + lang);
			return null;
		}
		
		try {
			startCount = Integer.parseInt(start);
		} catch (Exception e) {
			LOGGER.info("Invalid value for start " + start);
		}
		
		try {
			maxCount = Integer.parseInt(max);
		} catch (Exception e) {
			LOGGER.info("Invalid value for max " + max);
		}
		
		Customer customer = customerService.getById(id);
		
		System.out.println("$#11318#"); if(customer==null) {
			LOGGER.error("Customer is null for id " + id);
			System.out.println("$#11319#"); response.sendError(503, "Customer is null for id " + id);
			return null;
		}
		
		System.out.println("$#11320#"); if(customer.getMerchantStore().getId().intValue()!=merchantStore.getId().intValue()) {
			LOGGER.error("Customer is null for id " + id + " and store id " + store);
			System.out.println("$#11321#"); response.sendError(503, "Customer is null for id " + id + " and store id " + store);
			return null;
		}
		
		ReadableOrderList returnList = orderFacade.getReadableOrderList(merchantStore, startCount, maxCount, language);

		System.out.println("$#11322#"); return returnList;
	}

}
