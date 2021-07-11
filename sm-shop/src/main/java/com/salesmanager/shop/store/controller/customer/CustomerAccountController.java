package com.salesmanager.shop.store.controller.customer;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.services.customer.attribute.CustomerAttributeService;
import com.salesmanager.core.business.services.customer.attribute.CustomerOptionService;
import com.salesmanager.core.business.services.customer.attribute.CustomerOptionSetService;
import com.salesmanager.core.business.services.customer.attribute.CustomerOptionValueService;
import com.salesmanager.core.business.services.order.OrderService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.customer.attribute.CustomerAttribute;
import com.salesmanager.core.model.customer.attribute.CustomerOptionType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.customer.CustomerEntity;
import com.salesmanager.shop.model.customer.CustomerPassword;
import com.salesmanager.shop.model.customer.ReadableCustomer;
import com.salesmanager.shop.model.customer.address.Address;
import com.salesmanager.shop.populator.customer.ReadableCustomerPopulator;
import com.salesmanager.shop.store.controller.AbstractController;
import com.salesmanager.shop.store.controller.ControllerConstants;
import com.salesmanager.shop.store.controller.customer.facade.CustomerFacade;
import com.salesmanager.shop.store.controller.order.facade.OrderFacade;
import com.salesmanager.shop.utils.EmailTemplatesUtils;
import com.salesmanager.shop.utils.LabelUtils;
import com.salesmanager.shop.utils.LanguageUtils;
import com.salesmanager.shop.utils.LocaleUtils;

/**
 * Entry point for logged in customers
 * @author Carl Samson
 *
 */
@Controller
@RequestMapping("/shop/customer")
public class CustomerAccountController extends AbstractController {
	
	private static final String CUSTOMER_ID_PARAMETER = "customer";
    private static final String BILLING_SECTION="/shop/customer/billing.html";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerAccountController.class);
	
	@Inject
	private CustomerService customerService;
	
	@Inject
	private CustomerOptionService customerOptionService;
	
	@Inject
	private CustomerOptionValueService customerOptionValueService;
	
	@Inject
	private CustomerOptionSetService customerOptionSetService;
	
	@Inject
	private CustomerAttributeService customerAttributeService;
	
    @Inject
    private LanguageService languageService;
    
    @Inject
    private LanguageUtils languageUtils;
    
	@Inject
	private PasswordEncoder passwordEncoder;


    @Inject
    private CountryService countryService;
    
	@Inject
	private EmailTemplatesUtils emailTemplatesUtils;

    
    @Inject
    private ZoneService zoneService;
    
    @Inject
    private CustomerFacade customerFacade;
    
    @Inject
    private OrderService orderService;
    
    @Inject
    private OrderFacade orderFacade;
    
	@Inject
	private LabelUtils messages;


	
	/**
	 * Dedicated customer logon page
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/customLogon.html", method=RequestMethod.GET)
	public String displayLogon(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		

	    MerchantStore store = getSessionAttribute(Constants.MERCHANT_STORE, request);


		//dispatch to dedicated customer logon
		
		/** template **/
		StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Customer.customerLogon).append(".").append(store.getStoreTemplate());

		System.out.println("$#12326#"); return template.toString();
		
	}
	
	
	@RequestMapping(value="/accountSummary.json", method=RequestMethod.GET)
	public @ResponseBody ReadableCustomer customerInformation(@RequestParam String userName, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
	
	
		MerchantStore store = getSessionAttribute(Constants.MERCHANT_STORE, request);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Customer customer = null;
    	if(auth != null &&
        		 request.isUserInRole("AUTH_CUSTOMER")) {
    		customer = customerFacade.getCustomerByUserName(auth.getName(), store);

        } else {
									System.out.println("$#12329#"); response.sendError(401, "Customer not authenticated");
			return null;
        }
    	
					System.out.println("$#12330#"); if(StringUtils.isBlank(userName)) {
									System.out.println("$#12331#"); response.sendError(403, "Customer name required");
			return null;
    	}
    	
					System.out.println("$#12332#"); if(customer==null) {
									System.out.println("$#12333#"); response.sendError(401, "Customer not authenticated");
			return null;
    	}
    	
					System.out.println("$#12334#"); if(!customer.getNick().equals(userName)) {
									System.out.println("$#12335#"); response.sendError(401, "Customer not authenticated");
			return null;
    	}
    	
    	
    	ReadableCustomer readableCustomer = new ReadableCustomer();
    	

    	Language lang = languageUtils.getRequestLanguage(request, response);
    	
    	ReadableCustomerPopulator readableCustomerPopulator = new ReadableCustomerPopulator();
    	readableCustomerPopulator.populate(customer, readableCustomer, store, lang);
    	
					System.out.println("$#12336#"); return readableCustomer;
		
	}
		
	@PreAuthorize("hasRole('AUTH_CUSTOMER')")
	@RequestMapping(value="/account.html", method=RequestMethod.GET)
	public String displayCustomerAccount(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		

	    MerchantStore store = getSessionAttribute(Constants.MERCHANT_STORE, request);

		
		
		/** template **/
		StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Customer.customer).append(".").append(store.getStoreTemplate());

		System.out.println("$#12337#"); return template.toString();
		
	}
	
	@PreAuthorize("hasRole('AUTH_CUSTOMER')")
	@RequestMapping(value="/password.html", method=RequestMethod.GET)
	public String displayCustomerChangePassword(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		

	    MerchantStore store = getSessionAttribute(Constants.MERCHANT_STORE, request);

		CustomerPassword customerPassword = new CustomerPassword();
		model.addAttribute("password", customerPassword);
		
		/** template **/
		StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Customer.changePassword).append(".").append(store.getStoreTemplate());

		System.out.println("$#12338#"); return template.toString();
		
	}
	
	@PreAuthorize("hasRole('AUTH_CUSTOMER')")
	@RequestMapping(value="/changePassword.html", method=RequestMethod.POST)
	public String changePassword(@Valid @ModelAttribute(value="password") CustomerPassword password, BindingResult bindingResult, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		

	    MerchantStore store = getSessionAttribute(Constants.MERCHANT_STORE, request);
	    
		/** template **/
		StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Customer.changePassword).append(".").append(store.getStoreTemplate());

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Customer customer = null;
    	if(auth != null &&
        		 request.isUserInRole("AUTH_CUSTOMER")) {
    		customer = customerFacade.getCustomerByUserName(auth.getName(), store);

        }
    	
					System.out.println("$#12341#"); if(customer==null) {
						System.out.println("$#12342#"); return "redirect:/"+Constants.SHOP_URI;
    	}
    	
    	String currentPassword = password.getCurrentPassword();

    	BCryptPasswordEncoder encoder = (BCryptPasswordEncoder)passwordEncoder;
					System.out.println("$#12343#"); if(!encoder.matches(currentPassword, customer.getPassword())) {
          FieldError error = new FieldError("password","password",messages.getMessage("message.invalidpassword", locale));
										System.out.println("$#12344#"); bindingResult.addError(error);
    	}

    	
								System.out.println("$#12345#"); if ( bindingResult.hasErrors() )
        {
            LOGGER.info( "found {} validation error while validating customer password",
                         bindingResult.getErrorCount() );
						System.out.println("$#12346#"); return template.toString();

        }
    	
		CustomerPassword customerPassword = new CustomerPassword();
		model.addAttribute("password", customerPassword);
		
		String newPassword = password.getPassword();
		String encodedPassword = passwordEncoder.encode(newPassword);
		
		System.out.println("$#12347#"); customer.setPassword(encodedPassword);
		
		System.out.println("$#12348#"); customerService.saveOrUpdate(customer);
		
		System.out.println("$#12349#"); emailTemplatesUtils.changePasswordNotificationEmail(customer, store, LocaleUtils.getLocale(customer.getDefaultLanguage()), request.getContextPath());
		
		model.addAttribute("success", "success");

		System.out.println("$#12350#"); return template.toString();
		
	}
	

	
	/**
	 * Manage the edition of customer attributes
	 * @param request
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("hasRole('AUTH_CUSTOMER')")
	@RequestMapping(value={"/attributes/save.html"}, method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> saveCustomerAttributes(HttpServletRequest request, Locale locale) throws Exception {
		

		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#12351#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
		
		//1=1&2=on&3=eeee&4=on&customer=1

		@SuppressWarnings("rawtypes")
		Enumeration parameterNames = request.getParameterNames();
		
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Customer customer = null;
    	if(auth != null &&
        		 request.isUserInRole("AUTH_CUSTOMER")) {
    		customer = customerFacade.getCustomerByUserName(auth.getName(), store);

        }
    	
					System.out.println("$#12354#"); if(customer==null) {
    		LOGGER.error("Customer id [customer] is not defined in the parameters");
			System.out.println("$#12355#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			String returnString = resp.toJSONString();
			System.out.println("$#12356#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
    	}
		
		

		
		System.out.println("$#12357#"); if(customer.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
			LOGGER.error("Customer id does not belong to current store");
			System.out.println("$#12358#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			String returnString = resp.toJSONString();
			System.out.println("$#12359#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		}
		
		List<CustomerAttribute> customerAttributes = customerAttributeService.getByCustomer(store, customer);
		Map<Long,CustomerAttribute> customerAttributesMap = new HashMap<Long,CustomerAttribute>();
		
		for(CustomerAttribute attr : customerAttributes) {
			customerAttributesMap.put(attr.getCustomerOption().getId(), attr);
		}

		parameterNames = request.getParameterNames();
		
		System.out.println("$#12360#"); while(parameterNames.hasMoreElements()) {
			
			String parameterName = (String)parameterNames.nextElement();
			String parameterValue = request.getParameter(parameterName);
			try {
				
				String[] parameterKey = parameterName.split("-");
				com.salesmanager.core.model.customer.attribute.CustomerOption customerOption = null;
				com.salesmanager.core.model.customer.attribute.CustomerOptionValue customerOptionValue = null;

				
				System.out.println("$#12361#"); if(CUSTOMER_ID_PARAMETER.equals(parameterName)) {
					continue;
				}
				
					System.out.println("$#12363#"); System.out.println("$#12362#"); if(parameterKey.length>1) {
						//parse key - value
						String key = parameterKey[0];
						String value = parameterKey[1];
						//should be on
						customerOption = customerOptionService.getById(new Long(key));
						customerOptionValue = customerOptionValueService.getById(new Long(value));
						

						
					} else {
						customerOption = customerOptionService.getById(new Long(parameterName));
						customerOptionValue = customerOptionValueService.getById(new Long(parameterValue));

					}
					
					//get the attribute
					//CustomerAttribute attribute = customerAttributeService.getByCustomerOptionId(store, customer.getId(), customerOption.getId());
					CustomerAttribute attribute = customerAttributesMap.get(customerOption.getId());
					System.out.println("$#12364#"); if(attribute==null) {
						attribute = new CustomerAttribute();
						System.out.println("$#12365#"); attribute.setCustomer(customer);
						System.out.println("$#12366#"); attribute.setCustomerOption(customerOption);
					} else {
						customerAttributes.remove(attribute);
					}
					
					System.out.println("$#12367#"); if(customerOption.getCustomerOptionType().equals(CustomerOptionType.Text.name())) {
						System.out.println("$#12368#"); if(!StringUtils.isBlank(parameterValue)) {
							System.out.println("$#12369#"); attribute.setCustomerOptionValue(customerOptionValue);
							System.out.println("$#12370#"); attribute.setTextValue(parameterValue);
						}  else {
							System.out.println("$#12371#"); attribute.setTextValue(null);
						}
					} else {
						System.out.println("$#12372#"); attribute.setCustomerOptionValue(customerOptionValue);
					}
					
					
					System.out.println("$#12374#"); System.out.println("$#12373#"); if(attribute.getId()!=null && attribute.getId().longValue()>0) {
						System.out.println("$#12376#"); if(attribute.getCustomerOptionValue()==null){
							System.out.println("$#12377#"); customerAttributeService.delete(attribute);
						} else {
							System.out.println("$#12378#"); customerAttributeService.update(attribute);
						}
					} else {
						System.out.println("$#12379#"); customerAttributeService.save(attribute);
					}
					


			} catch (Exception e) {
				LOGGER.error("Cannot get parameter information " + parameterName,e);
			}
			
		}
		
		//and now the remaining to be removed
		for(CustomerAttribute attr : customerAttributes) {
			System.out.println("$#12380#"); customerAttributeService.delete(attr);
		}
		
		//refresh customer
		Customer c = customerService.getById(customer.getId());
		System.out.println("$#12381#"); super.setSessionAttribute(Constants.CUSTOMER, c, request);
		
		System.out.println("$#12382#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_SUCCESS);
		String returnString = resp.toJSONString();
		System.out.println("$#12383#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		

	}

	@PreAuthorize("hasRole('AUTH_CUSTOMER')")
	//@Secured("AUTH_CUSTOMER")
	@RequestMapping(value="/billing.html", method=RequestMethod.GET)
    public String displayCustomerBillingAddress(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        

        MerchantStore store = getSessionAttribute(Constants.MERCHANT_STORE, request);
        Language language = getSessionAttribute(Constants.LANGUAGE, request);
    
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Customer customer = null;
    	if(auth != null &&
        		 request.isUserInRole("AUTH_CUSTOMER")) {
    		customer = customerFacade.getCustomerByUserName(auth.getName(), store);

        }
    	
					System.out.println("$#12386#"); if(customer==null) {
						System.out.println("$#12387#"); return "redirect:/"+Constants.SHOP_URI;
    	}
        
        
        CustomerEntity customerEntity = customerFacade.getCustomerDataByUserName( customer.getNick(), store, language );
								System.out.println("$#12388#"); if(customer !=null){
           model.addAttribute( "customer",  customerEntity);
        }
        
        
        /** template **/
        StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Customer.Billing).append(".").append(store.getStoreTemplate());

								System.out.println("$#12389#"); return template.toString();
        
    }
    
	@PreAuthorize("hasRole('AUTH_CUSTOMER')")
    @RequestMapping(value="/editAddress.html", method={RequestMethod.GET,RequestMethod.POST})
    public String editAddress(final Model model, final HttpServletRequest request,
                              @RequestParam(value = "billingAddress", required = false) Boolean billingAddress) throws Exception {
        MerchantStore store = getSessionAttribute(Constants.MERCHANT_STORE, request);
        
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Customer customer = null;
    	if(auth != null &&
        		 request.isUserInRole("AUTH_CUSTOMER")) {
    		customer = customerFacade.getCustomerByUserName(auth.getName(), store);

        }
    	
					System.out.println("$#12392#"); if(customer==null) {
						System.out.println("$#12393#"); return "redirect:/"+Constants.SHOP_URI;
    	}
        
        
        
        Address address=customerFacade.getAddress( customer.getId(), store, billingAddress );
        model.addAttribute( "address", address);
        model.addAttribute( "customerId", customer.getId() );
        StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Customer.EditAddress).append(".").append(store.getStoreTemplate());
								System.out.println("$#12394#"); return template.toString();
    }
    
    
	@PreAuthorize("hasRole('AUTH_CUSTOMER')")
    @RequestMapping(value="/updateAddress.html", method={RequestMethod.GET,RequestMethod.POST})
    public String updateCustomerAddress(@Valid
                                        @ModelAttribute("address") Address address,BindingResult bindingResult,final Model model, final HttpServletRequest request,
                              @RequestParam(value = "billingAddress", required = false) Boolean billingAddress) throws Exception {
       
        MerchantStore store = getSessionAttribute(Constants.MERCHANT_STORE, request);
        
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Customer customer = null;
    	if(auth != null &&
        		 request.isUserInRole("AUTH_CUSTOMER")) {
    		customer = customerFacade.getCustomerByUserName(auth.getName(), store);

        }
    	
    	StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Customer.EditAddress).append(".").append(store.getStoreTemplate());
    	
					System.out.println("$#12397#"); if(customer==null) {
						System.out.println("$#12398#"); return "redirect:/"+Constants.SHOP_URI;
    	}
    	
    	model.addAttribute( "address", address);
        model.addAttribute( "customerId", customer.getId() );
        
        
								System.out.println("$#12399#"); if(bindingResult.hasErrors()){
            LOGGER.info( "found {} error(s) while validating  customer address ",
                         bindingResult.getErrorCount() );
												System.out.println("$#12400#"); return template.toString();
        }
        

        Language language = getSessionAttribute(Constants.LANGUAGE, request);
								System.out.println("$#12401#"); customerFacade.updateAddress( customer.getId(), store, address, language);
        
        Customer c = customerService.getById(customer.getId());
		System.out.println("$#12402#"); super.setSessionAttribute(Constants.CUSTOMER, c, request);
        
        model.addAttribute("success", "success");
        
								System.out.println("$#12403#"); return template.toString();

    }
    
    
	@ModelAttribute("countries")
	protected List<Country> getCountries(final HttpServletRequest request){
	    
        Language language = (Language) request.getAttribute( "LANGUAGE" );
        try
        {
												System.out.println("$#12404#"); if ( language == null )
            {
                language = (Language) request.getAttribute( "LANGUAGE" );
            }

												System.out.println("$#12405#"); if ( language == null )
            {
                language = languageService.getByCode( Constants.DEFAULT_LANGUAGE );
            }
            
            List<Country> countryList=countryService.getCountries( language );
												System.out.println("$#12406#"); return countryList;
        }
        catch ( ServiceException e )
        {
            LOGGER.error( "Error while fetching country list ", e );

        }
        return Collections.emptyList();
    }

    //@ModelAttribute("zones")
    //public List<Zone> getZones(final HttpServletRequest request){
    //    return zoneService.list();
    //}
 




}
