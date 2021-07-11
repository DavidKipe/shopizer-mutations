package com.salesmanager.shop.store.controller.customer;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.services.shoppingcart.ShoppingCartCalculationService;
import com.salesmanager.core.business.services.system.EmailService;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.core.model.shoppingcart.ShoppingCart;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.customer.AnonymousCustomer;
import com.salesmanager.shop.model.customer.CustomerEntity;
import com.salesmanager.shop.model.customer.SecuredShopPersistableCustomer;
import com.salesmanager.shop.model.shoppingcart.ShoppingCartData;
import com.salesmanager.shop.populator.shoppingCart.ShoppingCartDataPopulator;
import com.salesmanager.shop.store.controller.AbstractController;
import com.salesmanager.shop.store.controller.ControllerConstants;
import com.salesmanager.shop.store.controller.customer.facade.CustomerFacade;
import com.salesmanager.shop.utils.CaptchaRequestUtils;
import com.salesmanager.shop.utils.EmailTemplatesUtils;
import com.salesmanager.shop.utils.ImageFilePath;
import com.salesmanager.shop.utils.LabelUtils;


//import com.salesmanager.core.business.customer.CustomerRegistrationException;

/**
 * Registration of a new customer
 * @author Carl Samson
 *
 */


// http://stackoverflow.com/questions/17444258/how-to-use-new-passwordencoder-from-spring-security
@Controller
@RequestMapping("/shop/customer")
public class CustomerRegistrationController extends AbstractController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerRegistrationController.class);


	@Inject
	private LanguageService languageService;


	@Inject
	private CountryService countryService;

	
	@Inject
	private ZoneService zoneService;


	@Inject
	EmailService emailService;

	@Inject
	private LabelUtils messages;
	
	@Inject
	private CustomerFacade customerFacade;

	
	@Inject
	private EmailTemplatesUtils emailTemplatesUtils;
	
	@Inject
	private CaptchaRequestUtils captchaRequestUtils;
	
	@Inject
	@Qualifier("img")
	private ImageFilePath imageUtils;
	
    @Inject
    private ShoppingCartCalculationService shoppingCartCalculationService;
    
    @Inject
    private PricingService pricingService;
	
    @Value("${config.recaptcha.siteKey}")
    private String siteKeyKey;



	@RequestMapping(value="/registration.html", method=RequestMethod.GET)
	public String displayRegistration(final Model model, final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		MerchantStore store = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);

		model.addAttribute( "recapatcha_public_key", siteKeyKey);
		
		SecuredShopPersistableCustomer customer = new SecuredShopPersistableCustomer();
		AnonymousCustomer anonymousCustomer = (AnonymousCustomer)request.getAttribute(Constants.ANONYMOUS_CUSTOMER);
		System.out.println("$#12509#"); if(anonymousCustomer!=null) {
			System.out.println("$#12510#"); customer.setBilling(anonymousCustomer.getBilling());
		}
		
		model.addAttribute("customer", customer);

		/** template **/
		StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Customer.register).append(".").append(store.getStoreTemplate());

		System.out.println("$#12511#"); return template.toString();


	}

    @RequestMapping( value = "/register.html", method = RequestMethod.POST )
    public String registerCustomer( @Valid
    @ModelAttribute("customer") SecuredShopPersistableCustomer customer, BindingResult bindingResult, Model model,
                                    HttpServletRequest request, HttpServletResponse response, final Locale locale )
        throws Exception
    {
        MerchantStore merchantStore = (MerchantStore) request.getAttribute( Constants.MERCHANT_STORE );
        Language language = super.getLanguage(request);

        String userName = null;
        String password = null;
        
        model.addAttribute( "recapatcha_public_key", siteKeyKey);
        
								System.out.println("$#12512#"); if(!StringUtils.isBlank(request.getParameter("g-recaptcha-response"))) {
        	boolean validateCaptcha = captchaRequestUtils.checkCaptcha(request.getParameter("g-recaptcha-response"));
        	
												System.out.println("$#12513#"); if ( !validateCaptcha )
            {
                LOGGER.debug( "Captcha response does not matched" );
    			FieldError error = new FieldError("captchaChallengeField","captchaChallengeField",messages.getMessage("validaion.recaptcha.not.matched", locale));
							System.out.println("$#12514#"); bindingResult.addError(error);
            }
        }
        

								System.out.println("$#12515#"); if ( StringUtils.isNotBlank( customer.getUserName() ) )
        {
												System.out.println("$#12516#"); if ( customerFacade.checkIfUserExists( customer.getUserName(), merchantStore ) )
            {
                LOGGER.debug( "Customer with username {} already exists for this store ", customer.getUserName() );
            	FieldError error = new FieldError("userName","userName",messages.getMessage("registration.username.already.exists", locale));
													System.out.println("$#12517#"); bindingResult.addError(error);
            }
            userName = customer.getUserName();
        }
        
        
								System.out.println("$#12518#"); if ( StringUtils.isNotBlank( customer.getPassword() ) &&  StringUtils.isNotBlank( customer.getCheckPassword() ))
        {
												System.out.println("$#12520#"); if (! customer.getPassword().equals(customer.getCheckPassword()) )
            {
            	FieldError error = new FieldError("password","password",messages.getMessage("message.password.checkpassword.identical", locale));
													System.out.println("$#12521#"); bindingResult.addError(error);

            }
            password = customer.getPassword();
        }

								System.out.println("$#12522#"); if ( bindingResult.hasErrors() )
        {
            LOGGER.debug( "found {} validation error while validating in customer registration ",
                         bindingResult.getErrorCount() );
            StringBuilder template =
                new StringBuilder().append( ControllerConstants.Tiles.Customer.register ).append( "." ).append( merchantStore.getStoreTemplate() );
												System.out.println("$#12523#"); return template.toString();

        }

        @SuppressWarnings( "unused" )
        CustomerEntity customerData = null;
        try
        {
            //set user clear password
									System.out.println("$#12524#"); customer.setPassword(password);
        	customerData = customerFacade.registerCustomer( customer, merchantStore, language );
        }

        catch ( Exception e )
        {
            LOGGER.error( "Error while registering customer.. ", e);
        	ObjectError error = new ObjectError("registration",messages.getMessage("registration.failed", locale));
									System.out.println("$#12525#"); bindingResult.addError(error);
            StringBuilder template =
                            new StringBuilder().append( ControllerConstants.Tiles.Customer.register ).append( "." ).append( merchantStore.getStoreTemplate() );
												System.out.println("$#12526#"); return template.toString();
        }
        
        
        try {
              
	        /**
	         * Send registration email
	         */
									System.out.println("$#12527#"); emailTemplatesUtils.sendRegistrationEmail( customer, merchantStore, locale, request.getContextPath() );

        } catch(Exception e) {
    	   
        	LOGGER.error("Cannot send email to customer ",e);
        	
        }
        
        /**
         * Login user
         */
        
        try {
        	
	        //refresh customer
	        Customer c = customerFacade.getCustomerByUserName(customer.getUserName(), merchantStore);
	        //authenticate
									System.out.println("$#12528#"); customerFacade.authenticate(c, userName, password);
									System.out.println("$#12529#"); super.setSessionAttribute(Constants.CUSTOMER, c, request);
	        
	        StringBuilder cookieValue = new StringBuilder();
            cookieValue.append(merchantStore.getCode()).append("_").append(c.getNick());
	        
            //set username in the cookie
            Cookie cookie = new Cookie(Constants.COOKIE_NAME_USER, cookieValue.toString());
												System.out.println("$#12530#"); cookie.setMaxAge(60 * 24 * 3600);
												System.out.println("$#12531#"); cookie.setPath(Constants.SLASH);
												System.out.println("$#12532#"); response.addCookie(cookie);
            
            
            String sessionShoppingCartCode= (String)request.getSession().getAttribute( Constants.SHOPPING_CART );
												System.out.println("$#12533#"); if(!StringUtils.isBlank(sessionShoppingCartCode)) {
	            ShoppingCart shoppingCart = customerFacade.mergeCart( c, sessionShoppingCartCode, merchantStore, language );
	            ShoppingCartData shoppingCartData=this.populateShoppingCartData(shoppingCart, merchantStore, language);
													System.out.println("$#12534#"); if(shoppingCartData !=null) {
																	System.out.println("$#12535#"); request.getSession().setAttribute(Constants.SHOPPING_CART, shoppingCartData.getCode());
	            }

	            //set username in the cookie
	            Cookie c1 = new Cookie(Constants.COOKIE_NAME_CART, shoppingCartData.getCode());
													System.out.println("$#12536#"); c1.setMaxAge(60 * 24 * 3600);
													System.out.println("$#12537#"); c1.setPath(Constants.SLASH);
													System.out.println("$#12538#"); response.addCookie(c1);
	            
            }

									System.out.println("$#12539#"); return "redirect:/shop/customer/dashboard.html";
        
        
        } catch(Exception e) {
        	LOGGER.error("Cannot authenticate user ",e);
        	ObjectError error = new ObjectError("registration",messages.getMessage("registration.failed", locale));
									System.out.println("$#12540#"); bindingResult.addError(error);
        }
        
        
        StringBuilder template =
                new StringBuilder().append( ControllerConstants.Tiles.Customer.register ).append( "." ).append( merchantStore.getStoreTemplate() );
								System.out.println("$#12541#"); return template.toString();

    }
	
	
	@ModelAttribute("countryList")
	public List<Country> getCountries(final HttpServletRequest request){
	    
        Language language = (Language) request.getAttribute( "LANGUAGE" );
        try
        {
												System.out.println("$#12542#"); if ( language == null )
            {
                language = (Language) request.getAttribute( "LANGUAGE" );
            }

												System.out.println("$#12543#"); if ( language == null )
            {
                language = languageService.getByCode( Constants.DEFAULT_LANGUAGE );
            }
            
            List<Country> countryList=countryService.getCountries( language );
												System.out.println("$#12544#"); return countryList;
        }
        catch ( ServiceException e )
        {
            LOGGER.error( "Error while fetching country list ", e );

        }
        return Collections.emptyList();
    }
	
	@ModelAttribute("zoneList")
    public List<Zone> getZones(final HttpServletRequest request){
					System.out.println("$#12545#"); return zoneService.list();
	}
	
	
	

	
	
    private ShoppingCartData populateShoppingCartData(final ShoppingCart cartModel , final MerchantStore store, final Language language){

        ShoppingCartDataPopulator shoppingCartDataPopulator = new ShoppingCartDataPopulator();
								System.out.println("$#12546#"); shoppingCartDataPopulator.setShoppingCartCalculationService( shoppingCartCalculationService );
								System.out.println("$#12547#"); shoppingCartDataPopulator.setPricingService( pricingService );
        
        try
        {
												System.out.println("$#12548#"); return shoppingCartDataPopulator.populate(  cartModel ,  store,  language);
        }
        catch ( ConversionException ce )
        {
           LOGGER.error( "Error in converting shopping cart to shopping cart data", ce );

        }
        return null;
    }
	


}
