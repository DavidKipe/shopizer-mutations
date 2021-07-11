package com.salesmanager.shop.store.api.v0.customer;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.services.customer.attribute.CustomerOptionService;
import com.salesmanager.core.business.services.customer.attribute.CustomerOptionValueService;
import com.salesmanager.core.business.services.merchant.MerchantStoreService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.services.system.EmailService;
import com.salesmanager.core.business.services.user.GroupService;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.user.Group;
import com.salesmanager.core.model.user.GroupType;
import com.salesmanager.shop.admin.model.userpassword.UserReset;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.customer.PersistableCustomer;
import com.salesmanager.shop.model.customer.ReadableCustomer;
import com.salesmanager.shop.model.customer.attribute.PersistableCustomerOption;
import com.salesmanager.shop.model.customer.attribute.PersistableCustomerOptionValue;
import com.salesmanager.shop.populator.customer.CustomerPopulator;
import com.salesmanager.shop.populator.customer.PersistableCustomerOptionPopulator;
import com.salesmanager.shop.populator.customer.PersistableCustomerOptionValuePopulator;
import com.salesmanager.shop.populator.customer.ReadableCustomerPopulator;
import com.salesmanager.shop.store.api.v0.category.ShoppingCategoryRESTController;
import com.salesmanager.shop.utils.EmailTemplatesUtils;
import com.salesmanager.shop.utils.LabelUtils;
import com.salesmanager.shop.utils.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/services")
public class CustomerRESTController {

	@Inject
	private CustomerService customerService;
	
	@Inject
	private CustomerOptionValueService customerOptionValueService;
	
	@Inject
	private CustomerOptionService customerOptionService;
	
	
	@Inject
	private MerchantStoreService merchantStoreService;
	
	@Inject
	private LanguageService languageService;
	

	@Inject
	private CountryService countryService;
	
	@Inject
	private GroupService   groupService;
	
	@Inject
	private ZoneService zoneService;
	
	@Inject
	private PasswordEncoder passwordEncoder;
	
	@Inject
	EmailService emailService;
	
	@Inject
	private LabelUtils messages;
	
	@Inject
	private EmailTemplatesUtils emailTemplatesUtils;
	
	@Autowired
	private CustomerPopulator customerPopulator;


	
	private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingCategoryRESTController.class);
	
	
	/**
	 * Returns a single customer for a given MerchantStore
	 */
	@RequestMapping( value="/private/{store}/customer/{id}", method=RequestMethod.GET)
	@ResponseBody
	public ReadableCustomer getCustomer(@PathVariable final String store, @PathVariable Long id, HttpServletRequest request, HttpServletResponse response) throws Exception {
		MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
		System.out.println("$#11232#"); if(merchantStore!=null) {
			System.out.println("$#11233#"); if(!merchantStore.getCode().equals(store)) {
				merchantStore = null;
			}
		}
		
		System.out.println("$#11234#"); if(merchantStore== null) {
			merchantStore = merchantStoreService.getByCode(store);
		}
		
		System.out.println("$#11235#"); if(merchantStore==null) {
			LOGGER.error("Merchant store is null for code " + store);
			System.out.println("$#11236#"); response.sendError(503, "Merchant store is null for code " + store);
			return null;
		}
		
		Customer customer = customerService.getById(id);
		com.salesmanager.shop.model.customer.Customer customerProxy;
		System.out.println("$#11237#"); if(customer == null){
			System.out.println("$#11238#"); response.sendError(404, "No Customer found with id : " + id);
			return null;
		}
		
		ReadableCustomerPopulator populator = new ReadableCustomerPopulator();
		ReadableCustomer readableCustomer = new ReadableCustomer();
		populator.populate(customer, readableCustomer, merchantStore, merchantStore.getDefaultLanguage());
		
		System.out.println("$#11239#"); return readableCustomer;
	}
	
	
	/**
	 * Create a customer option value
	 * @param store
	 * @param optionValue
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping( value="/private/{store}/customer/optionValue", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public PersistableCustomerOptionValue createCustomerOptionValue(@PathVariable final String store, @Valid @RequestBody PersistableCustomerOptionValue optionValue, HttpServletRequest request, HttpServletResponse response) throws Exception {

		
		try {
			
			MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
			System.out.println("$#11240#"); if(merchantStore!=null) {
				System.out.println("$#11241#"); if(!merchantStore.getCode().equals(store)) {
					merchantStore = null;
				}
			}
			
			System.out.println("$#11242#"); if(merchantStore== null) {
				merchantStore = merchantStoreService.getByCode(store);
			}
			
			System.out.println("$#11243#"); if(merchantStore==null) {
				LOGGER.error("Merchant store is null for code " + store);
				System.out.println("$#11244#"); response.sendError(503, "Merchant store is null for code " + store);
				return null;
			}

			PersistableCustomerOptionValuePopulator populator = new PersistableCustomerOptionValuePopulator();
			System.out.println("$#11245#"); populator.setLanguageService(languageService);
			
			com.salesmanager.core.model.customer.attribute.CustomerOptionValue optValue = new com.salesmanager.core.model.customer.attribute.CustomerOptionValue();
			populator.populate(optionValue, optValue, merchantStore, merchantStore.getDefaultLanguage());
		
			System.out.println("$#11246#"); customerOptionValueService.save(optValue);
			
			System.out.println("$#11247#"); optionValue.setId(optValue.getId());
			
			System.out.println("$#11248#"); return optionValue;
			
		} catch (Exception e) {
			LOGGER.error("Error while saving customer option value",e);
			try {
				System.out.println("$#11249#"); response.sendError(503, "Error while saving product option value" + e.getMessage());
			} catch (Exception ignore) {
			}	
			return null;
		}
	}
	
	/**
	 * Create a customer option
	 * @param store
	 * @param option
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping( value="/private/{store}/customer/option", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public PersistableCustomerOption createCustomerOption(@PathVariable final String store, @Valid @RequestBody PersistableCustomerOption option, HttpServletRequest request, HttpServletResponse response) throws Exception {

		
		try {
			
			MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
			System.out.println("$#11250#"); if(merchantStore!=null) {
				System.out.println("$#11251#"); if(!merchantStore.getCode().equals(store)) {
					merchantStore = null;
				}
			}
			
			System.out.println("$#11252#"); if(merchantStore== null) {
				merchantStore = merchantStoreService.getByCode(store);
			}
			
			System.out.println("$#11253#"); if(merchantStore==null) {
				LOGGER.error("Merchant store is null for code " + store);
				System.out.println("$#11254#"); response.sendError(503, "Merchant store is null for code " + store);
				return null;
			}

			PersistableCustomerOptionPopulator populator = new PersistableCustomerOptionPopulator();
			System.out.println("$#11255#"); populator.setLanguageService(languageService);
			
			com.salesmanager.core.model.customer.attribute.CustomerOption opt = new com.salesmanager.core.model.customer.attribute.CustomerOption();
			populator.populate(option, opt, merchantStore, merchantStore.getDefaultLanguage());
		
			System.out.println("$#11256#"); customerOptionService.save(opt);
			
			System.out.println("$#11257#"); option.setId(opt.getId());
			
			System.out.println("$#11258#"); return option;
			
		} catch (Exception e) {
			LOGGER.error("Error while saving customer option",e);
			try {
				System.out.println("$#11259#"); response.sendError(503, "Error while saving product option value" + e.getMessage());
			} catch (Exception ignore) {
			}	
			return null;
		}
	}
	
	
	/**
	 * Returns all customers for a given MerchantStore
	 */
	@RequestMapping( value="/private/{store}/customer", method=RequestMethod.GET)
	@ResponseBody
	public List<ReadableCustomer> getCustomers(@PathVariable final String store, HttpServletRequest request, HttpServletResponse response) throws Exception {
		MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
		System.out.println("$#11260#"); if(merchantStore!=null) {
			System.out.println("$#11261#"); if(!merchantStore.getCode().equals(store)) {
				merchantStore = null;
			}
		}
		
		System.out.println("$#11262#"); if(merchantStore== null) {
			merchantStore = merchantStoreService.getByCode(store);
		}
		
		System.out.println("$#11263#"); if(merchantStore==null) {
			LOGGER.error("Merchant store is null for code " + store);
			System.out.println("$#11264#"); response.sendError(503, "Merchant store is null for code " + store);
			System.out.println("$#11265#"); return null;
		}
		
		List<Customer> customers = customerService.getListByStore(merchantStore);
		List<ReadableCustomer> returnCustomers = new ArrayList<ReadableCustomer>();
		for(Customer customer : customers) {

			ReadableCustomerPopulator populator = new ReadableCustomerPopulator();
			ReadableCustomer readableCustomer = new ReadableCustomer();
			populator.populate(customer, readableCustomer, merchantStore, merchantStore.getDefaultLanguage());
			returnCustomers.add(readableCustomer);
			
		}
		
		System.out.println("$#11266#"); return returnCustomers;
	}

	
	
	/**
	 * Deletes a customer for a given MerchantStore
	 */
	@RequestMapping( value="/private/{store}/customer/{id}", method=RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCustomer(@PathVariable final String store, @PathVariable Long id, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		try {
			
			Customer customer = customerService.getById(id);
			
			System.out.println("$#11267#"); if(customer==null) {
				System.out.println("$#11268#"); response.sendError(404, "No Customer found for ID : " + id);
				return;
			} 
				
				MerchantStore merchantStore = merchantStoreService.getByCode(store);
				System.out.println("$#11269#"); if(merchantStore == null) {
					System.out.println("$#11270#"); response.sendError(404, "Invalid merchant store : " + store);
					return;
				}
				
				System.out.println("$#11271#"); if(merchantStore.getId().intValue()!= customer.getMerchantStore().getId().intValue()){
					System.out.println("$#11272#"); response.sendError(404, "Customer id: " + id + " is not part of store " + store);
					return;
				}			
				
				System.out.println("$#11273#"); customerService.delete(customer);
			
			
		} catch (ServiceException se) {
			LOGGER.error("Cannot delete customer",se);
			System.out.println("$#11274#"); response.sendError(404, "An exception occured while removing the customer");
			return;
		}

	}
	
	
	/**
	 * Create new customer for a given MerchantStore
	 */
	@RequestMapping( value="/private/{store}/customer", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@Deprecated
	public PersistableCustomer createCustomer(@PathVariable final String store, @Valid @RequestBody PersistableCustomer customer, HttpServletRequest request, HttpServletResponse response) throws Exception {
		MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
		System.out.println("$#11275#"); if(merchantStore!=null) {
			System.out.println("$#11276#"); if(!merchantStore.getCode().equals(store)) {
				merchantStore = null;
			}
		}
		
		System.out.println("$#11277#"); if(merchantStore== null) {
			merchantStore = merchantStoreService.getByCode(store);
		}
		
		System.out.println("$#11278#"); if(merchantStore==null) {
			LOGGER.error("Merchant store is null for code " + store);
			System.out.println("$#11279#"); response.sendError(503, "Merchant store is null for code " + store);
			return null;
		}
		
		Customer cust = new Customer();
		
/*		CustomerPopulator populator = new CustomerPopulator();
		populator.setCountryService(countryService);
		populator.setCustomerOptionService(customerOptionService);
		populator.setCustomerOptionValueService(customerOptionValueService);
		populator.setLanguageService(languageService);
		populator.setZoneService(zoneService);
		populator.setGroupService(groupService);*/
		customerPopulator.populate(customer, cust, merchantStore, merchantStore.getDefaultLanguage());
		
		List<Group> groups = groupService.listGroup(GroupType.ADMIN);
		System.out.println("$#11280#"); cust.setGroups(groups);

		Locale customerLocale = LocaleUtils.getLocale(cust.getDefaultLanguage());
		
		String password = customer.getPassword();
		System.out.println("$#11281#"); if(StringUtils.isBlank(password)) {
			password = UserReset.generateRandomString();
			System.out.println("$#11282#"); customer.setPassword(password);
		}


		System.out.println("$#11283#"); customerService.save(cust);
		System.out.println("$#11284#"); customer.setId(cust.getId());
		
		System.out.println("$#11285#"); emailTemplatesUtils.sendRegistrationEmail(customer, merchantStore, customerLocale, request.getContextPath());


		System.out.println("$#11286#"); return customer;
	}
	
}
