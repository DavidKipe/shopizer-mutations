package com.salesmanager.shop.admin.controller.customers;

import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.services.customer.attribute.CustomerAttributeService;
import com.salesmanager.core.business.services.customer.attribute.CustomerOptionService;
import com.salesmanager.core.business.services.customer.attribute.CustomerOptionSetService;
import com.salesmanager.core.business.services.customer.attribute.CustomerOptionValueService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.services.system.EmailService;
import com.salesmanager.core.business.services.user.GroupService;
import com.salesmanager.core.business.utils.ajax.AjaxPageableResponse;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.customer.CustomerCriteria;
import com.salesmanager.core.model.customer.CustomerList;
import com.salesmanager.core.model.customer.attribute.CustomerAttribute;
import com.salesmanager.core.model.customer.attribute.CustomerOptionSet;
import com.salesmanager.core.model.customer.attribute.CustomerOptionType;
import com.salesmanager.core.model.customer.attribute.CustomerOptionValueDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.core.model.user.Group;
import com.salesmanager.core.model.user.GroupType;
import com.salesmanager.shop.admin.model.customer.attribute.CustomerOption;
import com.salesmanager.shop.admin.model.customer.attribute.CustomerOptionValue;
import com.salesmanager.shop.admin.model.web.Menu;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.populator.customer.ReadableCustomerOptionPopulator;
import com.salesmanager.shop.store.controller.customer.facade.CustomerFacade;
import com.salesmanager.shop.utils.EmailUtils;
import com.salesmanager.shop.utils.LabelUtils;
import com.salesmanager.shop.utils.LocaleUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;
import java.util.regex.Pattern;



@Controller
public class CustomerController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);
	
	private static final String CUSTOMER_ID_PARAMETER = "customer";
	
	
	
	@Inject
	private LabelUtils messages;
	
	@Inject
	private GroupService groupService;
	
	@Inject
	private CustomerService customerService;
	
	@Inject
	private CustomerOptionService customerOptionService;
	
	@Inject
	private CustomerOptionValueService customerOptionValueService;
	
	@Inject
	private CustomerOptionSetService customerOptionSetService;
	
	@Inject
	private CountryService countryService;
	
	@Inject
	private ZoneService zoneService;
	
	@Inject
	private LanguageService languageService;
	
	@Inject
	private CustomerAttributeService customerAttributeService;
	
	@Inject
	@Named("passwordEncoder")
	private PasswordEncoder passwordEncoder;
	
	@Inject
	private EmailService emailService;
	
	@Inject
	private EmailUtils emailUtils;
	
	@Inject
	private CustomerFacade customerFacade;
	
	
	/**
	 * Customer details
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("hasRole('CUSTOMER')")
	@RequestMapping(value="/admin/customers/customer.html", method=RequestMethod.GET)
	public String displayCustomer(Long id, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
			
		//display menu
		System.out.println("$#5234#"); this.setMenu(model, request);
		
		//get groups
		List<Group> groups = new ArrayList<Group>();
		List<Group> userGroups = groupService.listGroup(GroupType.CUSTOMER);
		for(Group group : userGroups) {
			groups.add(group);
		}
		
		model.addAttribute("groups",groups);
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		List<Language> languages = languageService.getLanguages();

		model.addAttribute("languages",languages);
		
		Customer customer = null;
		
		//if request.attribute contains id then get this customer from customerService
		System.out.println("$#5235#"); if(id!=null && id!=0) {//edit mode
			
			//get from DB
			customer = customerService.getById(id);
			System.out.println("$#5237#"); if(customer==null) {
				System.out.println("$#5238#"); return "redirect:/admin/customers/list.html";
			}
			System.out.println("$#5239#"); if(customer.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#5240#"); return "redirect:/admin/customers/list.html";
			}
			
		} else {
			 customer = new Customer();
		}
		//get list of countries (see merchant controller)
		Language language = (Language)request.getAttribute("LANGUAGE");				
		//get countries
		List<Country> countries = countryService.getCountries(language);
		
		//get list of zones
		List<Zone> zones = zoneService.list();
		
		System.out.println("$#5241#"); this.getCustomerOptions(model, customer, store, language);

		model.addAttribute("zones", zones);
		model.addAttribute("countries", countries);
		model.addAttribute("customer", customer);
		System.out.println("$#5242#"); return "admin-customer";
		
	}
	
	private void getCustomerOptions(Model model, Customer customer, MerchantStore store, Language language) throws Exception {

		Map<Long,CustomerOption> options = new HashMap<Long,CustomerOption>();
		//get options
		List<CustomerOptionSet> optionSet = customerOptionSetService.listByStore(store, language);
		System.out.println("$#5243#"); if(!CollectionUtils.isEmpty(optionSet)) {
			
			
			ReadableCustomerOptionPopulator optionPopulator = new ReadableCustomerOptionPopulator();
			
			Set<CustomerAttribute> customerAttributes = customer.getAttributes();
			
			for(CustomerOptionSet optSet : optionSet) {
				
				com.salesmanager.core.model.customer.attribute.CustomerOption custOption = optSet.getCustomerOption();
				System.out.println("$#5244#"); if(!custOption.isActive()) {
					continue;
				}
				CustomerOption customerOption = options.get(custOption.getId());
				
				System.out.println("$#5245#"); optionPopulator.setOptionSet(optSet);
				
				
				
				System.out.println("$#5246#"); if(customerOption==null) {
					customerOption = new CustomerOption();
					System.out.println("$#5247#"); customerOption.setId(custOption.getId());
					System.out.println("$#5248#"); customerOption.setType(custOption.getCustomerOptionType());
					System.out.println("$#5249#"); customerOption.setName(custOption.getDescriptionsSettoList().get(0).getName());
					
				} 
				
				optionPopulator.populate(custOption, customerOption, store, language);
				options.put(customerOption.getId(), customerOption);

				System.out.println("$#5250#"); if(!CollectionUtils.isEmpty(customerAttributes)) {
					for(CustomerAttribute customerAttribute : customerAttributes) {
						System.out.println("$#5251#"); if(customerAttribute.getCustomerOption().getId().longValue()==customerOption.getId()){
							CustomerOptionValue selectedValue = new CustomerOptionValue();
							com.salesmanager.core.model.customer.attribute.CustomerOptionValue attributeValue = customerAttribute.getCustomerOptionValue();
							System.out.println("$#5252#"); selectedValue.setId(attributeValue.getId());
							CustomerOptionValueDescription optValue = attributeValue.getDescriptionsSettoList().get(0);
							System.out.println("$#5253#"); selectedValue.setName(optValue.getName());
							System.out.println("$#5254#"); customerOption.setDefaultValue(selectedValue);
							System.out.println("$#5255#"); if(customerOption.getType().equalsIgnoreCase(CustomerOptionType.Text.name())) {
								System.out.println("$#5256#"); selectedValue.setName(customerAttribute.getTextValue());
							} 
						}
					}
				}
			}
		}
		
		
		model.addAttribute("options", options.values());

		
	}
	
	@PreAuthorize("hasRole('CUSTOMER')")
	@RequestMapping(value="/admin/customers/save.html", method=RequestMethod.POST)
	public String saveCustomer(@Valid @ModelAttribute("customer") Customer customer, BindingResult result, Model model, HttpServletRequest request, Locale locale) throws Exception{
	
		System.out.println("$#5257#"); this.setMenu(model, request);
		
		String email_regEx = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";
		Pattern pattern = Pattern.compile(email_regEx);
		
		Language language = (Language)request.getAttribute("LANGUAGE");
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		List<Language> languages = languageService.getLanguages();
		
		model.addAttribute("languages",languages);
		
		//get groups
		List<Group> groups = new ArrayList<Group>();
		List<Group> userGroups = groupService.listGroup(GroupType.CUSTOMER);
		for(Group group : userGroups) {
			groups.add(group);
		}
		
		model.addAttribute("groups",groups);
		
		System.out.println("$#5258#"); this.getCustomerOptions(model, customer, store, language);
		
		//get countries
		List<Country> countries = countryService.getCountries(language);

		
		System.out.println("$#5259#"); if(!StringUtils.isBlank(customer.getEmailAddress() ) ){
			 java.util.regex.Matcher matcher = pattern.matcher(customer.getEmailAddress());
			 
				System.out.println("$#5260#"); if(!matcher.find()) {
				ObjectError error = new ObjectError("customerEmailAddress",messages.getMessage("Email.customer.EmailAddress", locale));
				System.out.println("$#5261#"); result.addError(error);
			 }
		}else{
			ObjectError error = new ObjectError("customerEmailAddress",messages.getMessage("NotEmpty.customer.EmailAddress", locale));
			System.out.println("$#5262#"); result.addError(error);
		}
		

		 
		System.out.println("$#5263#"); if( StringUtils.isBlank(customer.getBilling().getFirstName() ) ){
			 ObjectError error = new ObjectError("billingFirstName", messages.getMessage("NotEmpty.customer.billingFirstName", locale));
				System.out.println("$#5264#"); result.addError(error);
		}
		
		System.out.println("$#5265#"); if( StringUtils.isBlank(customer.getBilling().getLastName() ) ){
			 ObjectError error = new ObjectError("billingLastName", messages.getMessage("NotEmpty.customer.billingLastName", locale));
				System.out.println("$#5266#"); result.addError(error);
		}
		
		System.out.println("$#5267#"); if( StringUtils.isBlank(customer.getBilling().getAddress() ) ){
			 ObjectError error = new ObjectError("billingAddress", messages.getMessage("NotEmpty.customer.billingStreetAddress", locale));
				System.out.println("$#5268#"); result.addError(error);
		}
		 
		System.out.println("$#5269#"); if( StringUtils.isBlank(customer.getBilling().getCity() ) ){
			 ObjectError error = new ObjectError("billingCity",messages.getMessage("NotEmpty.customer.billingCity", locale));
				System.out.println("$#5270#"); result.addError(error);
		}
		 
		System.out.println("$#5271#"); if( customer.getShowBillingStateList().equalsIgnoreCase("yes" ) && customer.getBilling().getZone().getCode() == null ){
			 ObjectError error = new ObjectError("billingState",messages.getMessage("NotEmpty.customer.billingState", locale));
				System.out.println("$#5273#"); result.addError(error);
			 
		}else if( customer.getShowBillingStateList().equalsIgnoreCase("no" ) && customer.getBilling().getState() == null ){ System.out.println("$#5274#");
				 ObjectError error = new ObjectError("billingState",messages.getMessage("NotEmpty.customer.billingState", locale));
					System.out.println("$#5276#"); result.addError(error);
			
		}
		 
		System.out.println("$#5277#"); if( StringUtils.isBlank(customer.getBilling().getPostalCode() ) ){
			 ObjectError error = new ObjectError("billingPostalCode", messages.getMessage("NotEmpty.customer.billingPostCode", locale));
				System.out.println("$#5278#"); result.addError(error);
		}
		
		//check if error from the @valid
		System.out.println("$#5279#"); if (result.hasErrors()) {
			model.addAttribute("countries", countries);
			System.out.println("$#5280#"); return "admin-customer";
		}
				
		Customer newCustomer = new Customer();

		System.out.println("$#5282#"); System.out.println("$#5281#"); if( customer.getId()!=null && customer.getId().longValue()>0 ) {
			newCustomer = customerService.getById( customer.getId() );
			
			System.out.println("$#5284#"); if(newCustomer==null) {
				System.out.println("$#5285#"); return "redirect:/admin/customers/list.html";
			}
			
			System.out.println("$#5286#"); if(newCustomer.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#5287#"); return "redirect:/admin/customers/list.html";
			}
			
			
			
		}else{
			//  new customer set marchant_Id
			MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			System.out.println("$#5288#"); newCustomer.setMerchantStore(merchantStore);
		}
		
		List<Group> submitedGroups = customer.getGroups();
		Set<Integer> ids = new HashSet<Integer>();
		for(Group group : submitedGroups) {
			ids.add(Integer.parseInt(group.getGroupName()));
		}
		
		List<Group> newGroups = groupService.listGroupByIds(ids);
		System.out.println("$#5289#"); newCustomer.setGroups(newGroups);
		

		System.out.println("$#5290#"); newCustomer.setEmailAddress(customer.getEmailAddress() );
		
		//get Customer country/zone 		
		Country deliveryCountry = countryService.getByCode( customer.getDelivery().getCountry().getIsoCode()); 
		Country billingCountry  = countryService.getByCode( customer.getBilling().getCountry().getIsoCode()) ;

		Zone deliveryZone = customer.getDelivery().getZone();
		Zone billingZone  = customer.getBilling().getZone();
		

		
		System.out.println("$#5291#"); if ("yes".equalsIgnoreCase(customer.getShowDeliveryStateList())) {
			System.out.println("$#5292#"); if(customer.getDelivery().getZone()!=null) {
				deliveryZone = zoneService.getByCode(customer.getDelivery().getZone().getCode());
				System.out.println("$#5293#"); customer.getDelivery().setState( null );
			}
			
		}else if ("no".equalsIgnoreCase(customer.getShowDeliveryStateList())){ System.out.println("$#5294#");
			System.out.println("$#5295#"); if(customer.getDelivery().getState()!=null) {
				deliveryZone = null ;
				System.out.println("$#5296#"); customer.getDelivery().setState( customer.getDelivery().getState() );
			}
		}
	
		System.out.println("$#5297#"); if ("yes".equalsIgnoreCase(customer.getShowBillingStateList())) {
			System.out.println("$#5298#"); if(customer.getBilling().getZone()!=null) {
				billingZone = zoneService.getByCode(customer.getBilling().getZone().getCode());
				System.out.println("$#5299#"); customer.getBilling().setState( null );
			}
			
		}else if ("no".equalsIgnoreCase(customer.getShowBillingStateList())){ System.out.println("$#5300#");
			System.out.println("$#5301#"); if(customer.getBilling().getState()!=null) {
				billingZone = null ;
				System.out.println("$#5302#"); customer.getBilling().setState( customer.getBilling().getState() );
			}
		}
				

		
		System.out.println("$#5303#"); newCustomer.setDefaultLanguage(customer.getDefaultLanguage() );
		
		System.out.println("$#5304#"); customer.getDelivery().setZone(  deliveryZone);
		System.out.println("$#5305#"); customer.getDelivery().setCountry(deliveryCountry );
		System.out.println("$#5306#"); newCustomer.setDelivery( customer.getDelivery() );
		
		System.out.println("$#5307#"); customer.getBilling().setZone(  billingZone);
		System.out.println("$#5308#"); customer.getBilling().setCountry(billingCountry );
		System.out.println("$#5309#"); newCustomer.setBilling( customer.getBilling()  );
		
		System.out.println("$#5310#"); customerService.saveOrUpdate(newCustomer);
		
		model.addAttribute("customer", newCustomer);
		model.addAttribute("countries", countries);
		model.addAttribute("success","success");
		
		System.out.println("$#5311#"); return "admin-customer";
		
	}
	
	/**
	 * Deserves shop and admin
	 * @param request
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("hasRole('CUSTOMER')")
	@RequestMapping(value={"/admin/customers/attributes/save.html"}, method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> saveCustomerAttributes(HttpServletRequest request, Locale locale) throws Exception {
		

		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#5312#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		//1=1&2=on&3=eeee&4=on&customer=1

		@SuppressWarnings("rawtypes")
		Enumeration parameterNames = request.getParameterNames();
		
		Customer customer = null;
		
		System.out.println("$#5313#"); while(parameterNames.hasMoreElements()) {

			String parameterName = (String)parameterNames.nextElement();
			String parameterValue = request.getParameter(parameterName);
			System.out.println("$#5314#"); if(CUSTOMER_ID_PARAMETER.equals(parameterName)) {
				customer = customerService.getById(new Long(parameterValue));
				break;
			}
		}
		
		System.out.println("$#5315#"); if(customer==null) {
			LOGGER.error("Customer id [customer] is not defined in the parameters");
			System.out.println("$#5316#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			String returnString = resp.toJSONString();
			System.out.println("$#5317#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		}
		
		System.out.println("$#5318#"); if(customer.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
			LOGGER.error("Customer id does not belong to current store");
			System.out.println("$#5319#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			String returnString = resp.toJSONString();
			System.out.println("$#5320#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		}
		
		List<CustomerAttribute> customerAttributes = customerAttributeService.getByCustomer(store, customer);
		Map<Long,CustomerAttribute> customerAttributesMap = new HashMap<Long,CustomerAttribute>();
		
		for(CustomerAttribute attr : customerAttributes) {
			customerAttributesMap.put(attr.getCustomerOption().getId(), attr);
		}

		parameterNames = request.getParameterNames();
		
		System.out.println("$#5321#"); while(parameterNames.hasMoreElements()) {
			
			String parameterName = (String)parameterNames.nextElement();
			String parameterValue = request.getParameter(parameterName);
			try {
				
				String[] parameterKey = parameterName.split("-");
				com.salesmanager.core.model.customer.attribute.CustomerOption customerOption = null;
				com.salesmanager.core.model.customer.attribute.CustomerOptionValue customerOptionValue = null;

				
				System.out.println("$#5322#"); if(CUSTOMER_ID_PARAMETER.equals(parameterName)) {
					continue;
				}
				
					System.out.println("$#5324#"); System.out.println("$#5323#"); if(parameterKey.length>1) {
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
					System.out.println("$#5325#"); if(attribute==null) {
						attribute = new CustomerAttribute();
						System.out.println("$#5326#"); attribute.setCustomer(customer);
						System.out.println("$#5327#"); attribute.setCustomerOption(customerOption);
					} else {
						customerAttributes.remove(attribute);
					}
					
					System.out.println("$#5328#"); if(customerOption.getCustomerOptionType().equals(CustomerOptionType.Text.name())) {
						System.out.println("$#5329#"); if(!StringUtils.isBlank(parameterValue)) {
							System.out.println("$#5330#"); attribute.setCustomerOptionValue(customerOptionValue);
							System.out.println("$#5331#"); attribute.setTextValue(parameterValue);
						} else {
							System.out.println("$#5332#"); attribute.setTextValue(null);
						}
					} else {
						System.out.println("$#5333#"); attribute.setCustomerOptionValue(customerOptionValue);
					}
					
					
					System.out.println("$#5335#"); System.out.println("$#5334#"); if(attribute.getId()!=null && attribute.getId().longValue()>0) {
						System.out.println("$#5337#"); if(attribute.getCustomerOptionValue()==null){
							System.out.println("$#5338#"); customerAttributeService.delete(attribute);
						} else {
							System.out.println("$#5339#"); customerAttributeService.update(attribute);
						}
					} else {
						System.out.println("$#5340#"); customerAttributeService.save(attribute);
					}
					


			} catch (Exception e) {
				LOGGER.error("Cannot get parameter information " + parameterName,e);
			}
			
		}
		
		//and now the remaining to be removed
		for(CustomerAttribute attr : customerAttributes) {
			System.out.println("$#5341#"); customerAttributeService.delete(attr);
		}
		
		System.out.println("$#5342#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_SUCCESS);
		String returnString = resp.toJSONString();
		System.out.println("$#5343#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		

	}


	
	/**
	 * List of customers
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/admin/customers/list.html", method=RequestMethod.GET)
	public String displayCustomers(Model model,HttpServletRequest request) throws Exception {
		
		
		System.out.println("$#5344#"); this.setMenu(model, request);
	
		System.out.println("$#5345#"); return "admin-customers";
		
		
		
	}
	
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/admin/customers/page.html", method=RequestMethod.POST)
	public @ResponseBody
	ResponseEntity<String>  pageCustomers(HttpServletRequest request,HttpServletResponse response) {


		AjaxPageableResponse resp = new AjaxPageableResponse();
		
		//Language language = (Language)request.getAttribute("LANGUAGE");
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		
		try {
			

			
			//Map<String,Country> countriesMap = countryService.getCountriesMap(language);
			
			
			int startRow = Integer.parseInt(request.getParameter("_startRow"));
			int endRow = Integer.parseInt(request.getParameter("_endRow"));
			String	email = request.getParameter("email");
			String name = request.getParameter("name");
			String firstName = request.getParameter("firstName");
			String lastName = request.getParameter("lastName");
			String	country = request.getParameter("country");
			
			
			CustomerCriteria criteria = new CustomerCriteria();
			System.out.println("$#5346#"); criteria.setStartIndex(startRow);
			System.out.println("$#5347#"); criteria.setMaxCount(endRow);
			
			System.out.println("$#5348#"); if(!StringUtils.isBlank(email)) {
				System.out.println("$#5349#"); criteria.setEmail(email);
			}
			
			System.out.println("$#5350#"); if(!StringUtils.isBlank(name)) {
				System.out.println("$#5351#"); criteria.setName(name);
			}
			
			System.out.println("$#5352#"); if(!StringUtils.isBlank(country)) {
				System.out.println("$#5353#"); criteria.setCountry(country);
			}
			
			System.out.println("$#5354#"); if(!StringUtils.isBlank(firstName)) {
				System.out.println("$#5355#"); criteria.setFirstName(firstName);
			}
			
			System.out.println("$#5356#"); if(!StringUtils.isBlank(lastName)) {
				System.out.println("$#5357#"); criteria.setLastName(lastName);
			}
			

			CustomerList customerList = customerService.getListByStore(store,criteria);
			
			System.out.println("$#5358#"); if(customerList.getCustomers()!=null) {
			
				for(Customer customer : customerList.getCustomers()) {
					@SuppressWarnings("rawtypes")
					Map entry = new HashMap();
					entry.put("id", customer.getId());
					entry.put("firstName", customer.getBilling().getFirstName());
					entry.put("lastName", customer.getBilling().getLastName());
					entry.put("email", customer.getEmailAddress());
					entry.put("country", customer.getBilling().getCountry().getIsoCode());
					System.out.println("$#5359#"); resp.addDataEntry(entry);
					
				}
			
			}
			
		} catch (Exception e) {
			LOGGER.error("Error while paging orders", e);
			System.out.println("$#5360#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
		}
		
		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#5361#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		System.out.println("$#5362#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		
	
	}
	
	
	@PreAuthorize("hasRole('CUSTOMER')")
	@RequestMapping(value="/admin/customers/resetPassword.html", method=RequestMethod.POST)
	public @ResponseBody
	ResponseEntity<String> resetPassword(HttpServletRequest request,HttpServletResponse response) {
		
		String customerId = request.getParameter("customerId");
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#5363#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		
		
		try {
			
			Long id = Long.parseLong(customerId);
			
			Customer customer = customerService.getById(id);
			
			System.out.println("$#5364#"); if(customer==null) {
				System.out.println("$#5365#"); resp.setErrorString("Customer does not exist");
				System.out.println("$#5366#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#5367#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#5368#"); if(customer.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#5369#"); resp.setErrorString("Invalid customer id");
				System.out.println("$#5370#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#5371#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			Language userLanguage = customer.getDefaultLanguage();
			
			System.out.println("$#5372#"); customerFacade.resetPassword(customer, store, userLanguage);
			
			System.out.println("$#5373#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_SUCCESS);
			
		} catch (Exception e) {
			LOGGER.error("An exception occured while changing password",e);
			System.out.println("$#5374#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
		}
		
		
		String returnString = resp.toJSONString();
		System.out.println("$#5375#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		
		
	}
	
	
	@PreAuthorize("hasRole('CUSTOMER')")
	@RequestMapping(value="/admin/customers/setCredentials.html", method=RequestMethod.POST)
	public @ResponseBody
	ResponseEntity<String> setCredentials(HttpServletRequest request,HttpServletResponse response) {
		
		String customerId = request.getParameter("customerId");
		String userName = request.getParameter("userName");
		String password = request.getParameter("password");
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#5376#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		
		
		try {
			
			Long id = Long.parseLong(customerId);
			
			Customer customer = customerService.getById(id);
			
			System.out.println("$#5377#"); if(customer==null) {
				System.out.println("$#5378#"); resp.setErrorString("Customer does not exist");
				System.out.println("$#5379#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#5380#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#5381#"); if(customer.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#5382#"); resp.setErrorString("Invalid customer id");
				System.out.println("$#5383#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#5384#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#5385#"); if(StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
				System.out.println("$#5387#"); resp.setErrorString("Invalid username or password");
				System.out.println("$#5388#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#5389#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			Language userLanguage = customer.getDefaultLanguage();
			
			Locale customerLocale = LocaleUtils.getLocale(userLanguage);

			String encodedPassword = passwordEncoder.encode(password);
			
			System.out.println("$#5390#"); customer.setPassword(encodedPassword);
			System.out.println("$#5391#"); customer.setNick(userName);
			
			System.out.println("$#5392#"); customerService.saveOrUpdate(customer);
			
			//send email
			
/*			try {

				//creation of a user, send an email
				String[] storeEmail = {store.getStoreEmailAddress()};
				
				
				Map<String, String> templateTokens = emailUtils.createEmailObjectsMap(request.getContextPath(), store, messages, customerLocale);
				templateTokens.put(EmailConstants.LABEL_HI, messages.getMessage("label.generic.hi", customerLocale));
		        templateTokens.put(EmailConstants.EMAIL_CUSTOMER_FIRSTNAME, customer.getBilling().getFirstName());
		        templateTokens.put(EmailConstants.EMAIL_CUSTOMER_LASTNAME, customer.getBilling().getLastName());
				templateTokens.put(EmailConstants.EMAIL_RESET_PASSWORD_TXT, messages.getMessage("email.customer.resetpassword.text", customerLocale));
				templateTokens.put(EmailConstants.EMAIL_CONTACT_OWNER, messages.getMessage("email.contactowner", storeEmail, customerLocale));
				templateTokens.put(EmailConstants.EMAIL_PASSWORD_LABEL, messages.getMessage("label.generic.password",customerLocale));
				templateTokens.put(EmailConstants.EMAIL_CUSTOMER_PASSWORD, password);


				Email email = new Email();
				email.setFrom(store.getStorename());
				email.setFromEmail(store.getStoreEmailAddress());
				email.setSubject(messages.getMessage("label.generic.changepassword",customerLocale));
				email.setTo(customer.getEmailAddress());
				email.setTemplateName(RESET_PASSWORD_TPL);
				email.setTemplateTokens(templateTokens);
	
	
				
				emailService.sendHtmlEmail(store, email);
				resp.setStatus(AjaxResponse.RESPONSE_STATUS_SUCCESS);
			
			} catch (Exception e) {
				LOGGER.error("Cannot send email to user",e);
				resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			}*/
			
			
			
			
		} catch (Exception e) {
			LOGGER.error("An exception occured while changing password",e);
			System.out.println("$#5393#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
		}
		
		
		String returnString = resp.toJSONString();
		System.out.println("$#5394#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		
		
	}
	
	private void setMenu(Model model, HttpServletRequest request) throws Exception {
		
		//display menu
		Map<String,String> activeMenus = new HashMap<String,String>();
		activeMenus.put("customer", "customer");
		activeMenus.put("customer-list", "customer-list");
		
		@SuppressWarnings("unchecked")
		Map<String, Menu> menus = (Map<String, Menu>)request.getAttribute("MENUMAP");
		
		Menu currentMenu = (Menu)menus.get("customer");
		model.addAttribute("currentMenu",currentMenu);
		model.addAttribute("activeMenus",activeMenus);


		//
		
	}
	
	

}
