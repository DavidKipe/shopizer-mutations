package com.salesmanager.shop.store.controller.customer;

import com.salesmanager.core.business.services.customer.attribute.CustomerOptionSetService;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.customer.attribute.CustomerAttribute;
import com.salesmanager.core.model.customer.attribute.CustomerOptionSet;
import com.salesmanager.core.model.customer.attribute.CustomerOptionType;
import com.salesmanager.core.model.customer.attribute.CustomerOptionValueDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.admin.model.customer.attribute.CustomerOption;
import com.salesmanager.shop.admin.model.customer.attribute.CustomerOptionValue;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.populator.customer.ReadableCustomerOptionPopulator;
import com.salesmanager.shop.store.controller.AbstractController;
import com.salesmanager.shop.store.controller.ControllerConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Entry point for logged in customers
 * @author Carl Samson
 *
 */
@Controller
@RequestMapping("/shop/customer")
public class CustomerDashboardController extends AbstractController {
	
	@Inject
    private AuthenticationManager customerAuthenticationManager;
	
	@Inject
	private CustomerOptionSetService customerOptionSetService;
	
	
	@PreAuthorize("hasRole('AUTH_CUSTOMER')")
	@RequestMapping(value="/dashboard.html", method=RequestMethod.GET)
	public String displayCustomerDashboard(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		

	    MerchantStore store = getSessionAttribute(Constants.MERCHANT_STORE, request);
	    Language language = (Language)request.getAttribute(Constants.LANGUAGE);
	    
		Customer customer = (Customer)request.getAttribute(Constants.CUSTOMER);
		System.out.println("$#12407#"); getCustomerOptions(model, customer, store, language);
        

		model.addAttribute("section","dashboard");
		
		
		/** template **/
		StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Customer.customer).append(".").append(store.getStoreTemplate());

		System.out.println("$#12408#"); return template.toString();
		
	}
	
	
	private void getCustomerOptions(Model model, Customer customer, MerchantStore store, Language language) throws Exception {

		Map<Long,CustomerOption> options = new HashMap<Long,CustomerOption>();
		//get options
		List<CustomerOptionSet> optionSet = customerOptionSetService.listByStore(store, language);
		System.out.println("$#12409#"); if(!CollectionUtils.isEmpty(optionSet)) {
			
			
			ReadableCustomerOptionPopulator optionPopulator = new ReadableCustomerOptionPopulator();
			
			Set<CustomerAttribute> customerAttributes = customer.getAttributes();
			
			for(CustomerOptionSet optSet : optionSet) {
				
				com.salesmanager.core.model.customer.attribute.CustomerOption custOption = optSet.getCustomerOption();
				System.out.println("$#12410#"); if(!custOption.isActive() || !custOption.isPublicOption()) {
					continue;
				}
				CustomerOption customerOption = options.get(custOption.getId());
				
				System.out.println("$#12412#"); optionPopulator.setOptionSet(optSet);
				
				
				
				System.out.println("$#12413#"); if(customerOption==null) {
					customerOption = new CustomerOption();
					System.out.println("$#12414#"); customerOption.setId(custOption.getId());
					System.out.println("$#12415#"); customerOption.setType(custOption.getCustomerOptionType());
					System.out.println("$#12416#"); customerOption.setName(custOption.getDescriptionsSettoList().get(0).getName());
					
				} 
				
				optionPopulator.populate(custOption, customerOption, store, language);
				options.put(customerOption.getId(), customerOption);

				System.out.println("$#12417#"); if(!CollectionUtils.isEmpty(customerAttributes)) {
					for(CustomerAttribute customerAttribute : customerAttributes) {
						System.out.println("$#12418#"); if(customerAttribute.getCustomerOption().getId().longValue()==customerOption.getId()){
							CustomerOptionValue selectedValue = new CustomerOptionValue();
							com.salesmanager.core.model.customer.attribute.CustomerOptionValue attributeValue = customerAttribute.getCustomerOptionValue();
							System.out.println("$#12419#"); selectedValue.setId(attributeValue.getId());
							CustomerOptionValueDescription optValue = attributeValue.getDescriptionsSettoList().get(0);
							System.out.println("$#12420#"); selectedValue.setName(optValue.getName());
							System.out.println("$#12421#"); customerOption.setDefaultValue(selectedValue);
							System.out.println("$#12422#"); if(customerOption.getType().equalsIgnoreCase(CustomerOptionType.Text.name())) {
								System.out.println("$#12423#"); selectedValue.setName(customerAttribute.getTextValue());
							} 
						}
					}
				}
			}
		}
		
		
		model.addAttribute("options", options.values());

		
	}
	
	

}
