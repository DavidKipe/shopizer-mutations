package com.salesmanager.shop.store.controller.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.services.order.OrderService;
import com.salesmanager.core.business.services.order.orderproduct.OrderProductDownloadService;
import com.salesmanager.core.business.services.payments.PaymentService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.services.shipping.ShippingService;
import com.salesmanager.core.business.services.shoppingcart.ShoppingCartService;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.price.FinalPrice;
import com.salesmanager.core.model.common.Billing;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.order.OrderTotal;
import com.salesmanager.core.model.order.OrderTotalSummary;
import com.salesmanager.core.model.order.orderproduct.OrderProductDownload;
import com.salesmanager.core.model.payments.PaymentMethod;
import com.salesmanager.core.model.payments.Transaction;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.core.model.shipping.ShippingMetaData;
import com.salesmanager.core.model.shipping.ShippingOption;
import com.salesmanager.core.model.shipping.ShippingQuote;
import com.salesmanager.core.model.shipping.ShippingSummary;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.shop.admin.model.userpassword.UserReset;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.customer.AnonymousCustomer;
import com.salesmanager.shop.model.customer.PersistableCustomer;
import com.salesmanager.shop.model.customer.ReadableDelivery;
import com.salesmanager.shop.model.order.ReadableShopOrder;
import com.salesmanager.shop.model.order.ShopOrder;
import com.salesmanager.shop.model.order.shipping.ReadableShippingSummary;
import com.salesmanager.shop.model.order.total.ReadableOrderTotal;
import com.salesmanager.shop.model.shoppingcart.ShoppingCartData;
import com.salesmanager.shop.populator.customer.ReadableCustomerDeliveryAddressPopulator;
import com.salesmanager.shop.populator.order.ReadableOrderTotalPopulator;
import com.salesmanager.shop.populator.order.ReadableShippingSummaryPopulator;
import com.salesmanager.shop.populator.order.ReadableShopOrderPopulator;
import com.salesmanager.shop.store.controller.AbstractController;
import com.salesmanager.shop.store.controller.ControllerConstants;
import com.salesmanager.shop.store.controller.customer.facade.CustomerFacade;
import com.salesmanager.shop.store.controller.order.facade.OrderFacade;
import com.salesmanager.shop.store.controller.shoppingCart.facade.ShoppingCartFacade;
import com.salesmanager.shop.utils.EmailTemplatesUtils;
import com.salesmanager.shop.utils.LabelUtils;


/**
 * Displays checkout form and deals with ajax user input
 * @author carlsamson
 *
 */
@Controller
@RequestMapping(Constants.SHOP_URI+"/order")
public class ShoppingOrderController extends AbstractController {
	
	private static final Logger LOGGER = LoggerFactory
	.getLogger(ShoppingOrderController.class);
	
	@Value("${config.googleMapsKey}")
	private String googleMapsKey;
	
	@Inject
	private ShoppingCartFacade shoppingCartFacade;
	
    @Inject
    private ShoppingCartService shoppingCartService;

	@Inject
	private PaymentService paymentService;
	
	@Inject
	private CustomerService customerService;
	
	@Inject
	private ShippingService shippingService;
	
	@Inject
	private LanguageService languageService;
	
	@Inject
	private OrderService orderService;
	
	@Inject
	private CountryService countryService;
	
	@Inject
	private ZoneService zoneService;
	
	@Inject
	private OrderFacade orderFacade;
	
	@Inject
	private CustomerFacade customerFacade;
	
	@Inject
	private LabelUtils messages;
	
	@Inject
	private PricingService pricingService;
	
	@Inject
	private ProductService productService;
	
	//@Inject
	//private PasswordEncoder passwordEncoder;

	@Inject
	private EmailTemplatesUtils emailTemplatesUtils;
	
	@Inject
	private OrderProductDownloadService orderProdctDownloadService;
	
	@SuppressWarnings("unused")
	@RequestMapping("/checkout.html")
	public String displayCheckout(@CookieValue("cart") String cookie, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {

		Language language = (Language)request.getAttribute("LANGUAGE");
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
		Customer customer = (Customer)request.getSession().getAttribute(Constants.CUSTOMER);

		model.addAttribute("googleMapsKey",googleMapsKey);
		
		/**
		 * Shopping cart
		 * 
		 * ShoppingCart should be in the HttpSession
		 * Otherwise the cart id is in the cookie
		 * Otherwise the customer is in the session and a cart exist in the DB
		 * Else -> Nothing to display
		 */
		
		//check if an existing order exist
		ShopOrder order = null;
		order = super.getSessionAttribute(Constants.ORDER, request);
	
		//Get the cart from the DB
		String shoppingCartCode  = (String)request.getSession().getAttribute(Constants.SHOPPING_CART);
		com.salesmanager.core.model.shoppingcart.ShoppingCart cart = null;
	
					System.out.println("$#13201#"); if(StringUtils.isBlank(shoppingCartCode)) {
				
			System.out.println("$#13202#"); if(cookie==null) {//session expired and cookie null, nothing to do
				System.out.println("$#13203#"); return "redirect:/shop/cart/shoppingCart.html";
			}
			String merchantCookie[] = cookie.split("_");
			String merchantStoreCode = merchantCookie[0];
			System.out.println("$#13204#"); if(!merchantStoreCode.equals(store.getCode())) {
				System.out.println("$#13205#"); return "redirect:/shop/cart/shoppingCart.html";
			}
			shoppingCartCode = merchantCookie[1];
	    	
	    } 
	    
	    cart = shoppingCartFacade.getShoppingCartModel(shoppingCartCode, store);
	    
	
					System.out.println("$#13206#"); if(cart==null && customer!=null) {
				cart=shoppingCartFacade.getShoppingCartModel(customer, store);
	    }
	    boolean allAvailables = true;
	    boolean requiresShipping = false;
	    boolean freeShoppingCart = true;
	    
	    //Filter items, delete unavailable
        Set<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> availables = new HashSet<ShoppingCartItem>();
        //Take out items no more available
        Set<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> items = cart.getLineItems();
        for(com.salesmanager.core.model.shoppingcart.ShoppingCartItem item : items) {
        	
        	Long id = item.getProduct().getId();
        	Product p = productService.getById(id);
									System.out.println("$#13208#"); if(p.isAvailable()) {
        		availables.add(item);
        	} else {
        		allAvailables = false;
        	}
			FinalPrice finalPrice = pricingService.calculateProductPrice(p);
			System.out.println("$#13210#"); System.out.println("$#13209#"); if (finalPrice.getFinalPrice().longValue() > 0) {
				freeShoppingCart = false;
			}
			System.out.println("$#13211#"); if (p.isProductShipeable()) {
				requiresShipping = true;
			}
        }
								System.out.println("$#13212#"); cart.setLineItems(availables);

								System.out.println("$#13213#"); if(!allAvailables) {
									System.out.println("$#13214#"); shoppingCartFacade.saveOrUpdateShoppingCart(cart);
        }
	    
					System.out.println("$#13215#"); super.setSessionAttribute(Constants.SHOPPING_CART, cart.getShoppingCartCode(), request);
	
					System.out.println("$#13216#"); if(shoppingCartCode==null && cart==null) {//error
				System.out.println("$#13218#"); return "redirect:/shop/cart/shoppingCart.html";
	    }
			
	
					System.out.println("$#13219#"); if(customer!=null) {
			System.out.println("$#13220#"); if(cart.getCustomerId()!=customer.getId().longValue()) {
					System.out.println("$#13221#"); return "redirect:/shop/shoppingCart.html";
			}
	     } else {
				customer = orderFacade.initEmptyCustomer(store);
				AnonymousCustomer anonymousCustomer = (AnonymousCustomer)request.getAttribute(Constants.ANONYMOUS_CUSTOMER);
				System.out.println("$#13222#"); if(anonymousCustomer!=null && anonymousCustomer.getBilling()!=null) {
					Billing billing = customer.getBilling();
					System.out.println("$#13224#"); billing.setCity(anonymousCustomer.getBilling().getCity());
					Map<String,Country> countriesMap = countryService.getCountriesMap(language);
					Country anonymousCountry = countriesMap.get(anonymousCustomer.getBilling().getCountry());
					System.out.println("$#13225#"); if(anonymousCountry!=null) {
						System.out.println("$#13226#"); billing.setCountry(anonymousCountry);
					}
					Map<String,Zone> zonesMap = zoneService.getZones(language);
					Zone anonymousZone = zonesMap.get(anonymousCustomer.getBilling().getZone());
					System.out.println("$#13227#"); if(anonymousZone!=null) {
						System.out.println("$#13228#"); billing.setZone(anonymousZone);
					}
					System.out.println("$#13229#"); if(anonymousCustomer.getBilling().getPostalCode()!=null) {
						System.out.println("$#13230#"); billing.setPostalCode(anonymousCustomer.getBilling().getPostalCode());
					}
					System.out.println("$#13231#"); customer.setBilling(billing);
				}
	     }
	

						System.out.println("$#13232#"); if(CollectionUtils.isEmpty(items)) {
				System.out.println("$#13233#"); return "redirect:/shop/shoppingCart.html";
	     }
		
						System.out.println("$#13234#"); if(order==null) {//TODO
			order = orderFacade.initializeOrder(store, customer, cart, language);
		  }

		/**
		 * hook for displaying or not delivery address configuration
		 */
		ShippingMetaData shippingMetaData = shippingService.getShippingMetaData(store);
		model.addAttribute("shippingMetaData",shippingMetaData);
		
		/** shipping **/
		ShippingQuote quote = null;
		System.out.println("$#13235#"); if(requiresShipping) {
			//System.out.println("** Berfore default shipping quote **");
			//Get all applicable shipping quotes
			quote = orderFacade.getShippingQuote(customer, cart, order, store, language);
			model.addAttribute("shippingQuote", quote);
		}

		System.out.println("$#13236#"); if(quote!=null) {
			String shippingReturnCode = quote.getShippingReturnCode();

			System.out.println("$#13237#"); if(StringUtils.isBlank(shippingReturnCode) || shippingReturnCode.equals(ShippingQuote.NO_POSTAL_CODE)) {
			
				System.out.println("$#13239#"); if(order.getShippingSummary()==null) {
					ShippingSummary summary = orderFacade.getShippingSummary(quote, store, language);
					System.out.println("$#13240#"); order.setShippingSummary(summary);
					System.out.println("$#13241#"); request.getSession().setAttribute(Constants.SHIPPING_SUMMARY, summary);//TODO DTO
				}
				System.out.println("$#13242#"); if(order.getSelectedShippingOption()==null) {
					System.out.println("$#13243#"); order.setSelectedShippingOption(quote.getSelectedShippingOption());
				}
				
				//save quotes in HttpSession
				List<ShippingOption> options = quote.getShippingOptions();
				System.out.println("$#13244#"); request.getSession().setAttribute(Constants.SHIPPING_OPTIONS, options);//TODO DTO
				
				System.out.println("$#13245#"); if(!CollectionUtils.isEmpty(options)) {
					
					for(ShippingOption shipOption : options) {
						
						StringBuilder moduleName = new StringBuilder();
						moduleName.append("module.shipping.").append(shipOption.getShippingModuleCode());
								
								
						String carrier = messages.getMessage(moduleName.toString(),locale);	
						String note = messages.getMessage(moduleName.append(".note").toString(), locale, "");
								
						System.out.println("$#13246#"); shipOption.setDescription(carrier);
						System.out.println("$#13247#"); shipOption.setNote(note);
						
						//option name
						System.out.println("$#13248#"); if(!StringUtils.isBlank(shipOption.getOptionCode())) {
							//try to get the translate
							StringBuilder optionCodeBuilder = new StringBuilder();
							try {
								
								optionCodeBuilder.append("module.shipping.").append(shipOption.getShippingModuleCode());
								String optionName = messages.getMessage(optionCodeBuilder.toString(),locale);
								System.out.println("$#13249#"); shipOption.setOptionName(optionName);
							} catch(Exception e) {//label not found
								LOGGER.warn("displayCheckout No shipping code found for " + optionCodeBuilder.toString());
							}
						}

					}
				
				}
			
			}
			
			System.out.println("$#13250#"); if(quote.getDeliveryAddress()!=null) {
				ReadableCustomerDeliveryAddressPopulator addressPopulator = new ReadableCustomerDeliveryAddressPopulator();
				System.out.println("$#13251#"); addressPopulator.setCountryService(countryService);
				System.out.println("$#13252#"); addressPopulator.setZoneService(zoneService);
				ReadableDelivery deliveryAddress = new ReadableDelivery();
				addressPopulator.populate(quote.getDeliveryAddress(), deliveryAddress,  store, language);
				model.addAttribute("deliveryAddress", deliveryAddress);
				System.out.println("$#13253#"); super.setSessionAttribute(Constants.KEY_SESSION_ADDRESS, deliveryAddress, request);
			}
			
			
			//get shipping countries
			List<Country> shippingCountriesList = orderFacade.getShipToCountry(store, language);
			model.addAttribute("countries", shippingCountriesList);
		} else {
			//get all countries
			List<Country> countries = countryService.getCountries(language);
			model.addAttribute("countries", countries);
		}
		
		System.out.println("$#13254#"); if(quote!=null && quote.getShippingReturnCode()!=null && quote.getShippingReturnCode().equals(ShippingQuote.NO_SHIPPING_MODULE_CONFIGURED)) {
			LOGGER.error("Shipping quote error " + quote.getShippingReturnCode());
			model.addAttribute("errorMessages", messages.getMessage(quote.getShippingReturnCode(), locale, quote.getShippingReturnCode()));
		}
		
		System.out.println("$#13257#"); if(quote!=null && !StringUtils.isBlank(quote.getQuoteError())) {
			LOGGER.error("Shipping quote error " + quote.getQuoteError());
			model.addAttribute("errorMessages", quote.getQuoteError());
		}
		
		System.out.println("$#13259#"); if(quote!=null && quote.getShippingReturnCode()!=null && quote.getShippingReturnCode().equals(ShippingQuote.NO_SHIPPING_TO_SELECTED_COUNTRY)) {
			LOGGER.error("Shipping quote error " + quote.getShippingReturnCode());
			model.addAttribute("errorMessages", quote.getShippingReturnCode());
		}
		/** end shipping **/

		//get payment methods
		List<PaymentMethod> paymentMethods = paymentService.getAcceptedPaymentMethods(store);

		//not free and no payment methods
		System.out.println("$#13262#"); if(CollectionUtils.isEmpty(paymentMethods) && !freeShoppingCart) {
			LOGGER.error("No payment method configured");
			model.addAttribute("errorMessages", messages.getMessage("payment.not.configured", locale,
					"No payments configured"));
		}
		
		System.out.println("$#13264#"); if(!CollectionUtils.isEmpty(paymentMethods)) {//select default payment method
			PaymentMethod defaultPaymentSelected = null;
			for(PaymentMethod paymentMethod : paymentMethods) {
				System.out.println("$#13265#"); if(paymentMethod.isDefaultSelected()) {
					defaultPaymentSelected = paymentMethod;
					break;
				}
			}
			
			System.out.println("$#13266#"); if(defaultPaymentSelected==null) {//forced default selection
				defaultPaymentSelected = paymentMethods.get(0);
				System.out.println("$#13267#"); defaultPaymentSelected.setDefaultSelected(true);
			}
			
			System.out.println("$#13268#"); order.setDefaultPaymentMethodCode(defaultPaymentSelected.getPaymentMethodCode());

		}
		
		//readable shopping cart items for order summary box
        ShoppingCartData shoppingCart = shoppingCartFacade.getShoppingCartData(cart, language);
        model.addAttribute( "cart", shoppingCart );
		
								System.out.println("$#13269#"); order.setCartCode(shoppingCart.getCode());


		//order total
		OrderTotalSummary orderTotalSummary = orderFacade.calculateOrderTotal(store, order, language);
		System.out.println("$#13270#"); order.setOrderTotalSummary(orderTotalSummary);
		//if order summary has to be re-used
		System.out.println("$#13271#"); super.setSessionAttribute(Constants.ORDER_SUMMARY, orderTotalSummary, request);

		//display hacks
		System.out.println("$#13272#"); if(!StringUtils.isBlank(googleMapsKey)) {
		  model.addAttribute("fieldDisabled","true");
		  model.addAttribute("cssClass","");
		} else {
		  model.addAttribute("fieldDisabled","false");
		  model.addAttribute("cssClass","required");
		}
		
		model.addAttribute("order",order);
		model.addAttribute("paymentMethods", paymentMethods);
		
		/** template **/
		StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Checkout.checkout).append(".").append(store.getStoreTemplate());
		System.out.println("$#13273#"); return template.toString();

		
	}
	
	
	@RequestMapping("/commitPreAuthorized.html")
	public String commitPreAuthorizedOrder(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
		Language language = (Language)request.getAttribute("LANGUAGE");
		ShopOrder order = super.getSessionAttribute(Constants.ORDER, request);
		System.out.println("$#13274#"); if(order==null) {
			StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Pages.timeout).append(".").append(store.getStoreTemplate());
			System.out.println("$#13275#"); return template.toString();
		}
		model.addAttribute("googleMapsKey",googleMapsKey);
	      //display hacks
								System.out.println("$#13276#"); if(!StringUtils.isBlank(googleMapsKey)) {
          model.addAttribute("disabled","true");
          model.addAttribute("cssClass","");
        } else {
          model.addAttribute("disabled","false");
          model.addAttribute("cssClass","required");
        }
		
		@SuppressWarnings("unchecked")
		Map<String, Object> configs = (Map<String, Object>) request.getAttribute(Constants.REQUEST_CONFIGS);
		
		System.out.println("$#13277#"); if(configs!=null && configs.containsKey(Constants.DEBUG_MODE)) {
			Boolean debugMode = (Boolean) configs.get(Constants.DEBUG_MODE);
			System.out.println("$#13279#"); if(debugMode) {
				try {
					ObjectMapper mapper = new ObjectMapper();
					String jsonInString = mapper.writeValueAsString(order);
					LOGGER.debug("Commit pre-authorized order -> " + jsonInString);
				} catch(Exception de) {
					LOGGER.error(de.getMessage());
				}
			}
		}

		
		try {
			
			OrderTotalSummary totalSummary = super.getSessionAttribute(Constants.ORDER_SUMMARY, request);
			
			System.out.println("$#13280#"); if(totalSummary==null) {
				totalSummary = orderFacade.calculateOrderTotal(store, order, language);
				System.out.println("$#13281#"); super.setSessionAttribute(Constants.ORDER_SUMMARY, totalSummary, request);
			}
			
			
			System.out.println("$#13282#"); order.setOrderTotalSummary(totalSummary);
			
			//already validated, proceed with commit
			Order orderModel = this.commitOrder(order, request, locale);
			System.out.println("$#13283#"); super.setSessionAttribute(Constants.ORDER_ID, orderModel.getId(), request);
			
			System.out.println("$#13284#"); return "redirect:/shop/order/confirmation.html";
			
		} catch(Exception e) {
			LOGGER.error("Error while commiting order",e);
			throw e;		
			
		}

	}
	
	
	private Order commitOrder(ShopOrder order, HttpServletRequest request, Locale locale) throws Exception, ServiceException {
		
		
			LOGGER.info("Entering comitOrder");
		
			MerchantStore store = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
			Language language = (Language)request.getAttribute("LANGUAGE");
			
			
			String userName = null;
			String password = null;
			
			PersistableCustomer customer = order.getCustomer();
			
	        /** set username and password to persistable object **/
			LOGGER.info("Set username and password to customer");
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			Customer authCustomer = null;
        	if(auth != null &&
	        		 request.isUserInRole("AUTH_CUSTOMER")) {
        		LOGGER.info("Customer authenticated");
        		authCustomer = customerFacade.getCustomerByUserName(auth.getName(), store);
        		//set id and authentication information
										System.out.println("$#13287#"); customer.setUserName(authCustomer.getNick());
        		//customer.setEncodedPassword(authCustomer.getPassword());
										System.out.println("$#13288#"); customer.setId(authCustomer.getId());
	        } else {
	        	//set customer id to null
										System.out.println("$#13289#"); customer.setId(null);
	        }
		
	        //if the customer is new, generate a password
        	LOGGER.info("New customer generate password");
									System.out.println("$#13290#"); if(customer.getId()==null || customer.getId()==0) {//new customer
	        	password = UserReset.generateRandomString();
	        	//String encodedPassword = passwordEncoder.encode(password);
	        	//customer.setEncodedPassword(encodedPassword);
	        }
	        
									System.out.println("$#13292#"); if(order.isShipToBillingAdress()) {
										System.out.println("$#13293#"); customer.setDelivery(customer.getBilling());
	        }
	        

	        LOGGER.info("Before creating new volatile");
			Customer modelCustomer = null;
			try {//set groups
				System.out.println("$#13294#"); if(authCustomer==null) {//not authenticated, create a new volatile user
					modelCustomer = customerFacade.getCustomerModel(customer, store, language);
					System.out.println("$#13295#"); customerFacade.setCustomerModelDefaultProperties(modelCustomer, store);
					userName = modelCustomer.getNick();
					LOGGER.debug( "About to persist volatile customer to database." );
					System.out.println("$#13296#"); if(modelCustomer.getDefaultLanguage() == null) {
						System.out.println("$#13297#"); modelCustomer.setDefaultLanguage(languageService.toLanguage(locale));
					}
											System.out.println("$#13298#"); customerService.saveOrUpdate( modelCustomer );
				} else {//use existing customer
					LOGGER.info("Populate customer model");
					modelCustomer = customerFacade.populateCustomerModel(authCustomer, customer, store, language);
				}
			} catch(Exception e) {
				throw new ServiceException(e);
			}
	        
           
			LOGGER.debug( "About to save transaction" );
	        Order modelOrder = null;
	        Transaction initialTransaction = (Transaction)super.getSessionAttribute(Constants.INIT_TRANSACTION_KEY, request);
									System.out.println("$#13299#"); if(initialTransaction!=null) {
	        	modelOrder=orderFacade.processOrder(order, modelCustomer, initialTransaction, store, language);
	        } else {
	        	modelOrder=orderFacade.processOrder(order, modelCustomer, store, language);
	        }
	        
	        //save order id in session
									System.out.println("$#13300#"); super.setSessionAttribute(Constants.ORDER_ID, modelOrder.getId(), request);
	        //set a unique token for confirmation
									System.out.println("$#13301#"); super.setSessionAttribute(Constants.ORDER_ID_TOKEN, modelOrder.getId(), request);
	        LOGGER.debug( "Transaction ended and order saved" );
	        
	        
	        LOGGER.debug( "Remove cart" );
			//get cart
			String cartCode = super.getSessionAttribute(Constants.SHOPPING_CART, request);
			System.out.println("$#13302#"); if(StringUtils.isNotBlank(cartCode)) {
				try {
					System.out.println("$#13303#"); shoppingCartFacade.setOrderId(cartCode, modelOrder.getId(), store);
				} catch(Exception e) {
					LOGGER.error("Cannot update cart " + cartCode, e);
					throw new ServiceException(e);
				}
			}

			
	        //cleanup the order objects
									System.out.println("$#13304#"); super.removeAttribute(Constants.ORDER, request);
									System.out.println("$#13305#"); super.removeAttribute(Constants.ORDER_SUMMARY, request);
									System.out.println("$#13306#"); super.removeAttribute(Constants.INIT_TRANSACTION_KEY, request);
									System.out.println("$#13307#"); super.removeAttribute(Constants.SHIPPING_OPTIONS, request);
									System.out.println("$#13308#"); super.removeAttribute(Constants.SHIPPING_SUMMARY, request);
									System.out.println("$#13309#"); super.removeAttribute(Constants.SHOPPING_CART, request);
	        
	        
	        
	        LOGGER.debug( "Refresh customer" );
	        try {
		        //refresh customer --
	        	modelCustomer = customerFacade.getCustomerByUserName(modelCustomer.getNick(), store);
		        
	        	//if has downloads, authenticate
	        	
	        	//check if any downloads exist for this order6
	    		List<OrderProductDownload> orderProductDownloads = orderProdctDownloadService.getByOrderId(modelOrder.getId());
							System.out.println("$#13310#"); if(CollectionUtils.isNotEmpty(orderProductDownloads)) {

		        	LOGGER.debug("Is user authenticated ? ",auth.isAuthenticated());
		        	if(auth != null &&
			        		 request.isUserInRole("AUTH_CUSTOMER")) {
			        	//already authenticated
			        } else {
				        //authenticate
												System.out.println("$#13313#"); customerFacade.authenticate(modelCustomer, userName, password);
												System.out.println("$#13314#"); super.setSessionAttribute(Constants.CUSTOMER, modelCustomer, request);
			        }
		        	//send new user registration template
					System.out.println("$#13315#"); if(order.getCustomer().getId()==null || order.getCustomer().getId().longValue()==0) {
						//send email for new customer
						System.out.println("$#13317#"); customer.setPassword(password);//set clear password for email
						System.out.println("$#13318#"); customer.setUserName(userName);
						System.out.println("$#13319#"); emailTemplatesUtils.sendRegistrationEmail( customer, store, locale, request.getContextPath() );
					}
	    		}
	    		
				//send order confirmation email to customer
				System.out.println("$#13320#"); emailTemplatesUtils.sendOrderEmail(modelCustomer.getEmailAddress(), modelCustomer, modelOrder, locale, language, store, request.getContextPath());
		        
										System.out.println("$#13321#"); if(orderService.hasDownloadFiles(modelOrder)) {
											System.out.println("$#13322#"); emailTemplatesUtils.sendOrderDownloadEmail(modelCustomer, modelOrder, store, locale, request.getContextPath());
		
		        }
	    		
				//send order confirmation email to merchant
				System.out.println("$#13323#"); emailTemplatesUtils.sendOrderEmail(store.getStoreEmailAddress(), modelCustomer, modelOrder, locale, language, store, request.getContextPath());
		        
	    		
	    		
	        } catch(Exception e) {
	        	LOGGER.error("Error while post processing order",e);
	        }


			
			
									System.out.println("$#13324#"); return modelOrder;
		
		
	}

	

	
	@SuppressWarnings("unchecked")
	@RequestMapping("/commitOrder.html")
	public String commitOrder(@CookieValue("cart") String cookie, @Valid @ModelAttribute(value="order") ShopOrder order, BindingResult bindingResult, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {

		MerchantStore store = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
		Language language = (Language)request.getAttribute("LANGUAGE");
		//validate if session has expired
		model.addAttribute("googleMapsKey",googleMapsKey);
	      //display hacks
								System.out.println("$#13325#"); if(!StringUtils.isBlank(googleMapsKey)) {
          model.addAttribute("disabled","true");
          model.addAttribute("cssClass","");
        } else {
          model.addAttribute("disabled","false");
          model.addAttribute("cssClass","required");
        }
		
		model.addAttribute("order", order);
		
		Map<String, Object> configs = (Map<String, Object>) request.getAttribute(Constants.REQUEST_CONFIGS);
		
		System.out.println("$#13326#"); if(configs!=null && configs.containsKey(Constants.DEBUG_MODE)) {
			Boolean debugMode = (Boolean) configs.get(Constants.DEBUG_MODE);
			System.out.println("$#13328#"); if(debugMode) {
				try {
					ObjectMapper mapper = new ObjectMapper();
					String jsonInString = mapper.writeValueAsString(order);
					LOGGER.debug("Commit order -> " + jsonInString);
				} catch(Exception de) {
					LOGGER.error(de.getMessage());
				}
			}
		}
			
		try {
				
				/**
				 * 
				 * Retrieve shopping cart and metadata 
				 * (information required to process order)
				 * 
				 * - Cart rerieved from cookie or from user session
				 * - Retrieves payment metadata
				 */
				ShippingMetaData shippingMetaData = shippingService.getShippingMetaData(store);
				model.addAttribute("shippingMetaData",shippingMetaData);
				//basic stuff
				String shoppingCartCode  = (String)request.getSession().getAttribute(Constants.SHOPPING_CART);
				System.out.println("$#13329#"); if(shoppingCartCode==null) {
					
					System.out.println("$#13330#"); if(cookie==null) {//session expired and cookie null, nothing to do
						StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Pages.timeout).append(".").append(store.getStoreTemplate());
						System.out.println("$#13331#"); return template.toString();
					}
					String merchantCookie[] = cookie.split("_");
					String merchantStoreCode = merchantCookie[0];
					System.out.println("$#13332#"); if(!merchantStoreCode.equals(store.getCode())) {
						StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Pages.timeout).append(".").append(store.getStoreTemplate());
						System.out.println("$#13333#"); return template.toString();
					}
					shoppingCartCode = merchantCookie[1];
				}
				com.salesmanager.core.model.shoppingcart.ShoppingCart cart = null;
			
							System.out.println("$#13334#"); if(StringUtils.isBlank(shoppingCartCode)) {
					StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Pages.timeout).append(".").append(store.getStoreTemplate());
					System.out.println("$#13335#"); return template.toString();
			    }
			    cart = shoppingCartFacade.getShoppingCartModel(shoppingCartCode, store);
			    
				//readable shopping cart items for order summary box
		        ShoppingCartData shoppingCart = shoppingCartFacade.getShoppingCartData(cart, language);
		        model.addAttribute( "cart", shoppingCart );
		        
		        boolean freeShoppingCart = true;

				Set<ShoppingCartItem> items = cart.getLineItems();
				List<ShoppingCartItem> cartItems = new ArrayList<ShoppingCartItem>(items);
				System.out.println("$#13336#"); order.setShoppingCartItems(cartItems);
				
		        for(com.salesmanager.core.model.shoppingcart.ShoppingCartItem item : items) {
		        	
		        	Long id = item.getProduct().getId();
		        	Product p = productService.getById(id);
					FinalPrice finalPrice = pricingService.calculateProductPrice(p);
					System.out.println("$#13338#"); System.out.println("$#13337#"); if (finalPrice.getFinalPrice().longValue() > 0) {
						freeShoppingCart = false;
					}
		        }

				//get payment methods
				List<PaymentMethod> paymentMethods = paymentService.getAcceptedPaymentMethods(store);
				

				//not free and no payment methods
				System.out.println("$#13339#"); if(CollectionUtils.isEmpty(paymentMethods) && !freeShoppingCart) {
					LOGGER.error("No payment method configured");
					model.addAttribute("errorMessages", "No payments configured");
				}
				
				System.out.println("$#13341#"); if(!CollectionUtils.isEmpty(paymentMethods)) {//select default payment method
					PaymentMethod defaultPaymentSelected = null;
					for(PaymentMethod paymentMethod : paymentMethods) {
						System.out.println("$#13342#"); if(paymentMethod.isDefaultSelected()) {
							defaultPaymentSelected = paymentMethod;
							break;
						}
					}
					
					System.out.println("$#13343#"); if(defaultPaymentSelected==null) {//forced default selection
						defaultPaymentSelected = paymentMethods.get(0);
						System.out.println("$#13344#"); defaultPaymentSelected.setDefaultSelected(true);
					}
					
					
				}
				
				/**
				 * Prepare failure data
				 * - Get another shipping quote
				 */
				
				ShippingQuote quote = orderFacade.getShippingQuote(order.getCustomer(), cart, order, store, language);
				
				
				System.out.println("$#13345#"); if(quote!=null) {
					

						//save quotes in HttpSession
						List<ShippingOption> options = quote.getShippingOptions();
						System.out.println("$#13346#"); request.getSession().setAttribute(Constants.SHIPPING_OPTIONS, options);
						
						System.out.println("$#13347#"); if(!CollectionUtils.isEmpty(options)) {
							
							for(ShippingOption shipOption : options) {
								
								LOGGER.info("Looking at shipping option " + shipOption.getOptionCode());
								
								StringBuilder moduleName = new StringBuilder();
								moduleName.append("module.shipping.").append(shipOption.getShippingModuleCode());
										
										
								String carrier = messages.getMessage(moduleName.toString(),new String[]{store.getStorename()},locale);		
										
								System.out.println("$#13348#"); shipOption.setDescription(carrier);
								
								//option name
								System.out.println("$#13349#"); if(!StringUtils.isBlank(shipOption.getOptionCode())) {
									//try to get the translate
									StringBuilder optionCodeBuilder = new StringBuilder();
									try {
										
										optionCodeBuilder.append("module.shipping.").append(shipOption.getShippingModuleCode()).append(".").append(shipOption.getOptionCode());
										String optionName = messages.getMessage(optionCodeBuilder.toString(),locale);
										System.out.println("$#13350#"); shipOption.setOptionName(optionName);
									} catch(Exception e) {//label not found
										LOGGER.warn("commitOrder No shipping code found for " + optionCodeBuilder.toString());
									}
								}

							}
						
						}
						
						System.out.println("$#13351#"); if(quote.getDeliveryAddress()!=null) {
							ReadableCustomerDeliveryAddressPopulator addressPopulator = new ReadableCustomerDeliveryAddressPopulator();
							System.out.println("$#13352#"); addressPopulator.setCountryService(countryService);
							System.out.println("$#13353#"); addressPopulator.setZoneService(zoneService);
							ReadableDelivery deliveryAddress = new ReadableDelivery();
							addressPopulator.populate(quote.getDeliveryAddress(), deliveryAddress,  store, language);
							model.addAttribute("deliveryAddress", deliveryAddress);
						}

				}
				
				model.addAttribute("shippingQuote", quote);
				model.addAttribute("paymentMethods", paymentMethods);
				
				System.out.println("$#13354#"); if(quote!=null) {
					List<Country> shippingCountriesList = orderFacade.getShipToCountry(store, language);
					model.addAttribute("countries", shippingCountriesList);
				} else {
					//get all countries
					List<Country> countries = countryService.getCountries(language);
					model.addAttribute("countries", countries);
				}
				
				//set shipping summary
				System.out.println("$#13355#"); if(order.getSelectedShippingOption()!=null) {
					ShippingSummary summary = (ShippingSummary)request.getSession().getAttribute(Constants.SHIPPING_SUMMARY);
					List<ShippingOption> options = (List<ShippingOption>)request.getSession().getAttribute(Constants.SHIPPING_OPTIONS);
					
					System.out.println("$#13356#"); if(summary==null) {
						summary = orderFacade.getShippingSummary(quote, store, language);
						System.out.println("$#13357#"); request.getSession().setAttribute(Constants.SHIPPING_SUMMARY, options);
					}
					
					System.out.println("$#13358#"); if(options==null) {
						options = quote.getShippingOptions();
						System.out.println("$#13359#"); request.getSession().setAttribute(Constants.SHIPPING_OPTIONS, options);
					}

					ReadableShippingSummary readableSummary = new ReadableShippingSummary();
					ReadableShippingSummaryPopulator readableSummaryPopulator = new ReadableShippingSummaryPopulator();
					System.out.println("$#13360#"); readableSummaryPopulator.setPricingService(pricingService);
					readableSummaryPopulator.populate(summary, readableSummary, store, language);
					
					
					System.out.println("$#13361#"); if(!CollectionUtils.isEmpty(options)) {
					
						//get submitted shipping option
						ShippingOption quoteOption = null;
						ShippingOption selectedOption = order.getSelectedShippingOption();

						//check if selectedOption exist
						for(ShippingOption shipOption : options) {
							System.out.println("$#13362#"); if(!StringUtils.isBlank(shipOption.getOptionId()) && shipOption.getOptionId().equals(selectedOption.getOptionId())) {
								quoteOption = shipOption;
							}
							
						}
						System.out.println("$#13364#"); if(quoteOption==null) {
							quoteOption = options.get(0);
						}
						
						System.out.println("$#13365#"); readableSummary.setSelectedShippingOption(quoteOption);
						System.out.println("$#13366#"); readableSummary.setShippingOptions(options);
						System.out.println("$#13367#"); summary.setShippingOption(quoteOption.getOptionId());
						System.out.println("$#13368#"); summary.setShipping(quoteOption.getOptionPrice());
					
					}

					System.out.println("$#13369#"); order.setShippingSummary(summary);
				}
				
				
				/**
				 * Calculate order total summary
				 */
				
				OrderTotalSummary totalSummary = super.getSessionAttribute(Constants.ORDER_SUMMARY, request);
				
				System.out.println("$#13370#"); if(totalSummary==null) {
					totalSummary = orderFacade.calculateOrderTotal(store, order, language);
					System.out.println("$#13371#"); super.setSessionAttribute(Constants.ORDER_SUMMARY, totalSummary, request);
				}
				
				
				System.out.println("$#13372#"); order.setOrderTotalSummary(totalSummary);
				
			
				System.out.println("$#13373#"); orderFacade.validateOrder(order, bindingResult, new HashMap<String,String>(), store, locale);
		        
										System.out.println("$#13374#"); if ( bindingResult.hasErrors() )
		        {
		            LOGGER.info( "found {} validation error while validating in customer registration ",
		                         bindingResult.getErrorCount() );
		            String message = null;
		            List<ObjectError> errors = bindingResult.getAllErrors();
														System.out.println("$#13375#"); if(!CollectionUtils.isEmpty(errors)) {
		            	for(ObjectError error : errors) {
		            		message = error.getDefaultMessage();
		            		break;
		            	}
		            }
        			model.addAttribute("errorMessages", message);
		            StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Checkout.checkout).append(".").append(store.getStoreTemplate());
								System.out.println("$#13376#"); return template.toString();
	
		        }
		        
		        @SuppressWarnings("unused")
				Order modelOrder = commitOrder(order, request, locale);

	        
			} catch(ServiceException se) {


            	LOGGER.error("Error while creating an order ", se);
            	
            	String defaultMessage = messages.getMessage("message.error", locale);
            	model.addAttribute("errorMessages", defaultMessage);
            	
													System.out.println("$#13377#"); if(se.getExceptionType()==ServiceException.EXCEPTION_VALIDATION) {
														System.out.println("$#13378#"); if(!StringUtils.isBlank(se.getMessageCode())) {
            			String messageLabel = messages.getMessage(se.getMessageCode(), locale, defaultMessage);
            			model.addAttribute("errorMessages", messageLabel);
            		}
													} else if(se.getExceptionType()==ServiceException.EXCEPTION_PAYMENT_DECLINED) { System.out.println("$#13379#");
            		String paymentDeclinedMessage = messages.getMessage("message.payment.declined", locale);
														System.out.println("$#13380#"); if(!StringUtils.isBlank(se.getMessageCode())) {
            			String messageLabel = messages.getMessage(se.getMessageCode(), locale, paymentDeclinedMessage);
            			model.addAttribute("errorMessages", messageLabel);
            		} else {
            			model.addAttribute("errorMessages", paymentDeclinedMessage);
            		}
            	} else {
														System.out.println("$#13379#"); // manual correction for else-if mutation coverage
			}
            	
            	
            	
            	StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Checkout.checkout).append(".").append(store.getStoreTemplate());
							System.out.println("$#13381#"); return template.toString();
				
			} catch(Exception e) {
				LOGGER.error("Error while commiting order",e);
				throw e;		
				
			}

	        //redirect to completd
									System.out.println("$#13382#"); return "redirect:/shop/order/confirmation.html";

		
	}
	
	

	
	/**
	 * Recalculates shipping and tax following a change in country or province
	 * @param order
	 * @param request
	 * @param response
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value={"/shippingQuotes.json"}, method=RequestMethod.POST)
	public @ResponseBody ReadableShopOrder calculateShipping(@ModelAttribute(value="order") ShopOrder order, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		
		Language language = (Language)request.getAttribute("LANGUAGE");
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
		String shoppingCartCode  = getSessionAttribute(Constants.SHOPPING_CART, request);

		Map<String, Object> configs = (Map<String, Object>) request.getAttribute(Constants.REQUEST_CONFIGS);
		
/*		if(configs!=null && configs.containsKey(Constants.DEBUG_MODE)) {
			Boolean debugMode = (Boolean) configs.get(Constants.DEBUG_MODE);
			if(debugMode) {
				try {
					ObjectMapper mapper = new ObjectMapper();
					String jsonInString = mapper.writeValueAsString(order);
					LOGGER.info("Calculate order -> shoppingCartCode[ " + shoppingCartCode + "] -> " + jsonInString);
				} catch(Exception de) {
					LOGGER.error(de.getMessage());
				}
			}
		}*/

		System.out.println("$#13383#"); Validate.notNull(shoppingCartCode,"shoppingCartCode does not exist in the session");
		
		ReadableShopOrder readableOrder = new ReadableShopOrder();
		try {

			//re-generate cart
			com.salesmanager.core.model.shoppingcart.ShoppingCart cart = shoppingCartFacade.getShoppingCartModel(shoppingCartCode, store);
			Set<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> cartItems = cart.getLineItems();	
			
			
			ReadableShopOrderPopulator populator = new ReadableShopOrderPopulator();
			populator.populate(order, readableOrder, store, language);
			
			boolean requiresShipping = false;;
	        for(com.salesmanager.core.model.shoppingcart.ShoppingCartItem item : cartItems) {
	        	
	        	Long id = item.getProduct().getId();
	        	Product p = productService.getById(id);
				System.out.println("$#13384#"); if (p.isProductShipeable()) {
					requiresShipping = true;
				}
	        }
			
			/** shipping **/
			ShippingQuote quote = null;
			System.out.println("$#13385#"); if(requiresShipping) {
				quote = orderFacade.getShippingQuote(order.getCustomer(), cart, order, store, language);
			}

			System.out.println("$#13386#"); if(quote!=null) {
				String shippingReturnCode = quote.getShippingReturnCode();
				System.out.println("$#13387#"); if(CollectionUtils.isNotEmpty(quote.getShippingOptions()) || ShippingQuote.NO_POSTAL_CODE.equals(shippingReturnCode)) {

					ShippingSummary summary = orderFacade.getShippingSummary(quote, store, language);
					System.out.println("$#13389#"); order.setShippingSummary(summary);//for total calculation
					
					
					ReadableShippingSummary readableSummary = new ReadableShippingSummary();
					ReadableShippingSummaryPopulator readableSummaryPopulator = new ReadableShippingSummaryPopulator();
					System.out.println("$#13390#"); readableSummaryPopulator.setPricingService(pricingService);
					readableSummaryPopulator.populate(summary, readableSummary, store, language);
					
					//additional informations
/*					if(quote.getQuoteInformations() != null && quote.getQuoteInformations().size() >0) {
						for(String k : quote.getQuoteInformations().keySet()) {
							Object o = quote.getQuoteInformations().get(k);
							try {
								readableSummary.getQuoteInformations().put(k, String.valueOf(o));
							} catch(Exception e) {
								LOGGER.error("Cannot cast value to string " + e.getMessage());
							}
						}
					}*/
					
					System.out.println("$#13391#"); if(quote.getDeliveryAddress()!=null) {
						ReadableCustomerDeliveryAddressPopulator addressPopulator = new ReadableCustomerDeliveryAddressPopulator();
						System.out.println("$#13392#"); addressPopulator.setCountryService(countryService);
						System.out.println("$#13393#"); addressPopulator.setZoneService(zoneService);
						ReadableDelivery deliveryAddress = new ReadableDelivery();
						addressPopulator.populate(quote.getDeliveryAddress(), deliveryAddress,  store, language);
						//model.addAttribute("deliveryAddress", deliveryAddress);
						System.out.println("$#13394#"); readableOrder.setDelivery(deliveryAddress);
						System.out.println("$#13395#"); super.setSessionAttribute(Constants.KEY_SESSION_ADDRESS, deliveryAddress, request);
					}
					
					
					//save quotes in HttpSession
					List<ShippingOption> options = quote.getShippingOptions();
					
					System.out.println("$#13396#"); if(!CollectionUtils.isEmpty(options)) {
					
						for(ShippingOption shipOption : options) {
							
							StringBuilder moduleName = new StringBuilder();
							moduleName.append("module.shipping.").append(shipOption.getShippingModuleCode());
											
							String carrier = messages.getMessage(moduleName.toString(),new String[]{store.getStorename()},locale);
							
							String note = messages.getMessage(moduleName.append(".note").toString(), locale, "");
							
									
							System.out.println("$#13397#"); shipOption.setDescription(carrier);
							System.out.println("$#13398#"); shipOption.setNote(note);
							
							//option name
							System.out.println("$#13399#"); if(!StringUtils.isBlank(shipOption.getOptionCode())) {
								//try to get the translate
								StringBuilder optionCodeBuilder = new StringBuilder();
								try {
									
									optionCodeBuilder.append("module.shipping.").append(shipOption.getShippingModuleCode());
									String optionName = messages.getMessage(optionCodeBuilder.toString(),locale);
									System.out.println("$#13400#"); shipOption.setOptionName(optionName);
								} catch(Exception e) {//label not found
									LOGGER.warn("calculateShipping No shipping code found for " + optionCodeBuilder.toString());
								}
							}

						}
					
					}
					
					System.out.println("$#13401#"); readableSummary.setSelectedShippingOption(quote.getSelectedShippingOption());

					
					System.out.println("$#13402#"); readableSummary.setShippingOptions(options);
					
					System.out.println("$#13403#"); readableOrder.setShippingSummary(readableSummary);//TODO add readable address
					System.out.println("$#13404#"); request.getSession().setAttribute(Constants.SHIPPING_SUMMARY, summary);
					System.out.println("$#13405#"); request.getSession().setAttribute(Constants.SHIPPING_OPTIONS, options);
					System.out.println("$#13406#"); request.getSession().setAttribute("SHIPPING_INFORMATIONS", readableSummary.getQuoteInformations());
					
					System.out.println("$#13407#"); if(configs!=null && configs.containsKey(Constants.DEBUG_MODE)) {
						Boolean debugMode = (Boolean) configs.get(Constants.DEBUG_MODE);
						System.out.println("$#13409#"); if(debugMode) {
							
							try {
								ObjectMapper mapper = new ObjectMapper();
								String jsonInString = mapper.writeValueAsString(readableOrder);
								LOGGER.debug("Readable order -> shoppingCartCode[ " + shoppingCartCode + "] -> " + jsonInString);
								System.out.println("$#13410#"); System.out.println("Readable order -> shoppingCartCode[ " + shoppingCartCode + "] -> " + jsonInString);
							} catch(Exception de) {
								LOGGER.error(de.getMessage());
							}
							

						}
					}
					
				
				}

				System.out.println("$#13411#"); if(quote.getShippingReturnCode()!=null && quote.getShippingReturnCode().equals(ShippingQuote.NO_SHIPPING_MODULE_CONFIGURED)) {
					LOGGER.error("Shipping quote error " + quote.getShippingReturnCode());
					System.out.println("$#13413#"); readableOrder.setErrorMessage(messages.getMessage("message.noshipping", locale));
				}
				
				System.out.println("$#13414#"); if(quote.getShippingReturnCode()!=null && quote.getShippingReturnCode().equals(ShippingQuote.NO_SHIPPING_TO_SELECTED_COUNTRY)) {
					System.out.println("$#13416#"); if(CollectionUtils.isEmpty(quote.getShippingOptions())) {//only if there are no other options
						LOGGER.error("Shipping quote error " + quote.getShippingReturnCode());
						System.out.println("$#13417#"); readableOrder.setErrorMessage(messages.getMessage("message.noshipping", locale));
					}
				}
				
				//if(quote.getShippingReturnCode()!=null && quote.getShippingReturnCode().equals(ShippingQuote.NO_POSTAL_CODE)) {
				//	LOGGER.error("Shipping quote error " + quote.getShippingReturnCode());
				//	readableOrder.setErrorMessage(messages.getMessage("message.noshipping", locale));
				//}
				
				System.out.println("$#13418#"); if(!StringUtils.isBlank(quote.getQuoteError())) {
					LOGGER.error("Shipping quote error " + quote.getQuoteError());
					System.out.println("$#13419#"); readableOrder.setErrorMessage(messages.getMessage("message.noshippingerror", locale));
				}
				
				
			}
			
			//set list of shopping cart items for core price calculation
			List<ShoppingCartItem> items = new ArrayList<ShoppingCartItem>(cart.getLineItems());
			System.out.println("$#13420#"); order.setShoppingCartItems(items);
			System.out.println("$#13421#"); order.setCartCode(cart.getShoppingCartCode());

			
			OrderTotalSummary orderTotalSummary = orderFacade.calculateOrderTotal(store, order, language);
			System.out.println("$#13422#"); super.setSessionAttribute(Constants.ORDER_SUMMARY, orderTotalSummary, request);
			
			
			ReadableOrderTotalPopulator totalPopulator = new ReadableOrderTotalPopulator();
			System.out.println("$#13423#"); totalPopulator.setMessages(messages);
			System.out.println("$#13424#"); totalPopulator.setPricingService(pricingService);

			List<ReadableOrderTotal> subtotals = new ArrayList<ReadableOrderTotal>();
			for(OrderTotal total : orderTotalSummary.getTotals()) {
				System.out.println("$#13425#"); if(!total.getOrderTotalCode().equals("order.total.total")) {
					ReadableOrderTotal t = new ReadableOrderTotal();
					totalPopulator.populate(total, t, store, language);
					subtotals.add(t);
				} else {//grand total
					ReadableOrderTotal ot = new ReadableOrderTotal();
					totalPopulator.populate(total, ot, store, language);
					System.out.println("$#13426#"); readableOrder.setGrandTotal(ot.getTotal());
				}
			}
			
			
			System.out.println("$#13427#"); readableOrder.setSubTotals(subtotals);
		
		} catch(Exception e) {
			LOGGER.error("Error while getting shipping quotes",e);
			System.out.println("$#13428#"); readableOrder.setErrorMessage(messages.getMessage("message.error", locale));
		}
		
		System.out.println("$#13429#"); return readableOrder;
	}

	/**
	 * Calculates the order total following price variation like changing a shipping option
	 * @param order
	 * @param request
	 * @param response
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/calculateOrderTotal.json"}, method=RequestMethod.POST)
	public @ResponseBody ReadableShopOrder calculateOrderTotal(@ModelAttribute(value="order") ShopOrder order, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		
		Language language = (Language)request.getAttribute("LANGUAGE");
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
		String shoppingCartCode  = getSessionAttribute(Constants.SHOPPING_CART, request);
		
		System.out.println("$#13430#"); Validate.notNull(shoppingCartCode,"shoppingCartCode does not exist in the session");
		
		ReadableShopOrder readableOrder = new ReadableShopOrder();
		try {

			//re-generate cart
			com.salesmanager.core.model.shoppingcart.ShoppingCart cart = shoppingCartFacade.getShoppingCartModel(shoppingCartCode, store);

			ReadableShopOrderPopulator populator = new ReadableShopOrderPopulator();
			populator.populate(order, readableOrder, store, language);
			
			ReadableDelivery readableDelivery = super.getSessionAttribute(Constants.KEY_SESSION_ADDRESS, request);

			System.out.println("$#13431#"); if(order.getSelectedShippingOption()!=null) {
						ShippingSummary summary = (ShippingSummary)request.getSession().getAttribute(Constants.SHIPPING_SUMMARY);
						@SuppressWarnings("unchecked")
						List<ShippingOption> options = (List<ShippingOption>)request.getSession().getAttribute(Constants.SHIPPING_OPTIONS);
						
						
						System.out.println("$#13432#"); order.setShippingSummary(summary);//for total calculation
						
						
						ReadableShippingSummary readableSummary = new ReadableShippingSummary();
						ReadableShippingSummaryPopulator readableSummaryPopulator = new ReadableShippingSummaryPopulator();
						System.out.println("$#13433#"); readableSummaryPopulator.setPricingService(pricingService);
						readableSummaryPopulator.populate(summary, readableSummary, store, language);
						
						//override summary
						System.out.println("$#13434#"); readableSummary.setDelivery(readableDelivery);
						
						System.out.println("$#13435#"); if(!CollectionUtils.isEmpty(options)) {
						
							//get submitted shipping option
							ShippingOption quoteOption = null;
							ShippingOption selectedOption = order.getSelectedShippingOption();

							
							
							//check if selectedOption exist
							for(ShippingOption shipOption : options) {
																
								StringBuilder moduleName = new StringBuilder();
								moduleName.append("module.shipping.").append(shipOption.getShippingModuleCode());
										
										
								String carrier = messages.getMessage(moduleName.toString(),locale);		
								String note = messages.getMessage(moduleName.append(".note").toString(), locale, "");
										
								System.out.println("$#13436#"); shipOption.setNote(note);
								
								System.out.println("$#13437#"); shipOption.setDescription(carrier);
								System.out.println("$#13438#"); if(!StringUtils.isBlank(shipOption.getOptionId()) && shipOption.getOptionId().equals(selectedOption.getOptionId())) {
									quoteOption = shipOption;
								}
								
								//option name
								System.out.println("$#13440#"); if(!StringUtils.isBlank(shipOption.getOptionCode())) {
									//try to get the translate
									StringBuilder optionCodeBuilder = new StringBuilder();
									try {
										
										//optionCodeBuilder.append("module.shipping.").append(shipOption.getShippingModuleCode()).append(".").append(shipOption.getOptionCode());
										optionCodeBuilder.append("module.shipping.").append(shipOption.getShippingModuleCode());
										String optionName = messages.getMessage(optionCodeBuilder.toString(),locale);
										System.out.println("$#13441#"); shipOption.setOptionName(optionName);
									} catch(Exception e) {//label not found
										LOGGER.warn("calculateOrderTotal No shipping code found for " + optionCodeBuilder.toString());
									}
								}
							}
							
							System.out.println("$#13442#"); if(quoteOption==null) {
								quoteOption = options.get(0);
							}
							
							
							System.out.println("$#13443#"); readableSummary.setSelectedShippingOption(quoteOption);
							System.out.println("$#13444#"); readableSummary.setShippingOptions(options);

							System.out.println("$#13445#"); summary.setShippingOption(quoteOption.getOptionId());
							System.out.println("$#13446#"); summary.setShippingOptionCode(quoteOption.getOptionCode());
							System.out.println("$#13447#"); summary.setShipping(quoteOption.getOptionPrice());
							System.out.println("$#13448#"); order.setShippingSummary(summary);//override with new summary
							
							
							@SuppressWarnings("unchecked")
							Map<String,String> informations = (Map<String,String>)request.getSession().getAttribute("SHIPPING_INFORMATIONS");
							System.out.println("$#13449#"); readableSummary.setQuoteInformations(informations);
						
						}

						
						System.out.println("$#13450#"); readableOrder.setShippingSummary(readableSummary);//TODO readable address format
						System.out.println("$#13451#"); readableOrder.setDelivery(readableDelivery);
			}
			
			//set list of shopping cart items for core price calculation
			List<ShoppingCartItem> items = new ArrayList<ShoppingCartItem>(cart.getLineItems());
			System.out.println("$#13452#"); order.setShoppingCartItems(items);
			System.out.println("$#13453#"); order.setCartCode(shoppingCartCode);
			
			//order total calculation
			OrderTotalSummary orderTotalSummary = orderFacade.calculateOrderTotal(store, order, language);
			System.out.println("$#13454#"); super.setSessionAttribute(Constants.ORDER_SUMMARY, orderTotalSummary, request);
			
			
			ReadableOrderTotalPopulator totalPopulator = new ReadableOrderTotalPopulator();
			System.out.println("$#13455#"); totalPopulator.setMessages(messages);
			System.out.println("$#13456#"); totalPopulator.setPricingService(pricingService);

			List<ReadableOrderTotal> subtotals = new ArrayList<ReadableOrderTotal>();
			for(OrderTotal total : orderTotalSummary.getTotals()) {
				System.out.println("$#13457#"); if(total.getOrderTotalCode() == null || !total.getOrderTotalCode().equals("order.total.total")) {
					ReadableOrderTotal t = new ReadableOrderTotal();
					totalPopulator.populate(total, t, store, language);
					subtotals.add(t);
				} else {//grand total
					ReadableOrderTotal ot = new ReadableOrderTotal();
					totalPopulator.populate(total, ot, store, language);
					System.out.println("$#13459#"); readableOrder.setGrandTotal(ot.getTotal());
				}
			}
			
			
			System.out.println("$#13460#"); readableOrder.setSubTotals(subtotals);
		
		} catch(Exception e) {
			LOGGER.error("Error while getting shipping quotes",e);
			System.out.println("$#13461#"); readableOrder.setErrorMessage(messages.getMessage("message.error", locale));
		}
		
		System.out.println("$#13462#"); return readableOrder;
	}
	


}
