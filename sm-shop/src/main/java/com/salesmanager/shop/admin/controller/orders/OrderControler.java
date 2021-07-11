package com.salesmanager.shop.admin.controller.orders;

import com.salesmanager.core.business.modules.email.Email;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.services.order.OrderService;
import com.salesmanager.core.business.services.order.orderproduct.OrderProductDownloadService;
import com.salesmanager.core.business.services.payments.PaymentService;
import com.salesmanager.core.business.services.payments.TransactionService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.services.system.EmailService;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.order.OrderTotal;
import com.salesmanager.core.model.order.orderproduct.OrderProduct;
import com.salesmanager.core.model.order.orderproduct.OrderProductDownload;
import com.salesmanager.core.model.order.orderstatus.OrderStatusHistory;
import com.salesmanager.core.model.payments.PaymentType;
import com.salesmanager.core.model.payments.Transaction;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.shop.admin.controller.ControllerConstants;
import com.salesmanager.shop.admin.model.web.Menu;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.constants.EmailConstants;
import com.salesmanager.shop.utils.DateUtil;
import com.salesmanager.shop.utils.EmailUtils;
import com.salesmanager.shop.utils.LabelUtils;
import com.salesmanager.shop.utils.LocaleUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Manage order details
 * @author Carl Samson
 *
 */
@Controller
public class OrderControler {
	
private static final Logger LOGGER = LoggerFactory.getLogger(OrderControler.class);
	
	@Inject
	private LabelUtils messages;
	
	@Inject
	private OrderService orderService;
	
	@Inject
	CountryService countryService;
	
	@Inject
	ZoneService zoneService;
	
	@Inject
	PaymentService paymentService;
	
	@Inject
	CustomerService customerService;
	
	@Inject
	PricingService pricingService;
	
	@Inject
	TransactionService transactionService;
	
	@Inject
	EmailService emailService;
	
	@Inject
	private EmailUtils emailUtils;
	
	@Inject
	OrderProductDownloadService orderProdctDownloadService;
	
	private final static String ORDER_STATUS_TMPL = "email_template_order_status.ftl";
	

	@PreAuthorize("hasRole('ORDER')")
	@RequestMapping(value="/admin/orders/editOrder.html", method=RequestMethod.GET)
	public String displayOrderEdit(@RequestParam("id") long orderId, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		System.out.println("$#5841#"); return displayOrder(orderId,model,request,response);

	}

	@PreAuthorize("hasRole('ORDER')")
	private String displayOrder(Long orderId, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		//display menu
		System.out.println("$#5842#"); setMenu(model,request);
		   
		com.salesmanager.shop.admin.model.orders.Order order = new com.salesmanager.shop.admin.model.orders.Order();
		Language language = (Language)request.getAttribute("LANGUAGE");
		List<Country> countries = countryService.getCountries(language);
		System.out.println("$#5843#"); if(orderId!=null && orderId!=0) {		//edit mode
			
			
			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			
			
			
			Set<OrderProduct> orderProducts = null;
			Set<OrderTotal> orderTotal = null;
			Set<OrderStatusHistory> orderHistory = null;
		
			Order dbOrder = orderService.getById(orderId);

			System.out.println("$#5845#"); if(dbOrder==null) {
				System.out.println("$#5846#"); return "redirect:/admin/orders/list.html";
			}
			
			
			System.out.println("$#5847#"); if(dbOrder.getMerchant().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#5848#"); return "redirect:/admin/orders/list.html";
			}
			
			
			System.out.println("$#5849#"); order.setId( orderId );
		
			System.out.println("$#5850#"); if( dbOrder.getDatePurchased() !=null ){
				System.out.println("$#5851#"); order.setDatePurchased(DateUtil.formatDate(dbOrder.getDatePurchased()));
			}
			
			Long customerId = dbOrder.getCustomerId();
			
			System.out.println("$#5853#"); System.out.println("$#5852#"); if(customerId!=null && customerId>0) {
			
				try {
					
					Customer customer = customerService.getById(customerId);
					System.out.println("$#5855#"); if(customer!=null) {
						model.addAttribute("customer",customer);
					}
					
					
				} catch(Exception e) {
					LOGGER.error("Error while getting customer for customerId " + customerId, e);
				}
			
			}
			
			System.out.println("$#5856#"); order.setOrder( dbOrder );
			System.out.println("$#5857#"); order.setBilling( dbOrder.getBilling() );
			System.out.println("$#5858#"); order.setDelivery(dbOrder.getDelivery() );
			

			orderProducts = dbOrder.getOrderProducts();
			orderTotal = dbOrder.getOrderTotal();
			orderHistory = dbOrder.getOrderHistory();
			
			//get capturable
			System.out.println("$#5859#"); if(dbOrder.getPaymentType().name() != PaymentType.MONEYORDER.name()) {
				Transaction capturableTransaction = transactionService.getCapturableTransaction(dbOrder);
				System.out.println("$#5860#"); if(capturableTransaction!=null) {
					model.addAttribute("capturableTransaction",capturableTransaction);
				}
			}
			
			
			//get refundable
			System.out.println("$#5861#"); if(dbOrder.getPaymentType().name() != PaymentType.MONEYORDER.name()) {
				Transaction refundableTransaction = transactionService.getRefundableTransaction(dbOrder);
				System.out.println("$#5862#"); if(refundableTransaction!=null) {
						model.addAttribute("capturableTransaction",null);//remove capturable
						model.addAttribute("refundableTransaction",refundableTransaction);
				}
			}

			
			List<OrderProductDownload> orderProductDownloads = orderProdctDownloadService.getByOrderId(order.getId());
			System.out.println("$#5863#"); if(CollectionUtils.isNotEmpty(orderProductDownloads)) {
				model.addAttribute("downloads",orderProductDownloads);
			}
			
		}	
		
		model.addAttribute("countries", countries);
		model.addAttribute("order",order);
		System.out.println("$#5864#"); return  ControllerConstants.Tiles.Order.ordersEdit;
	}
	

	@PreAuthorize("hasRole('ORDER')")
	@RequestMapping(value="/admin/orders/save.html", method=RequestMethod.POST)
	public String saveOrder(@Valid @ModelAttribute("order") com.salesmanager.shop.admin.model.orders.Order entityOrder, BindingResult result, Model model, HttpServletRequest request, Locale locale) throws Exception {
		
		String email_regEx = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";
		Pattern pattern = Pattern.compile(email_regEx);
		
		Language language = (Language)request.getAttribute("LANGUAGE");
		List<Country> countries = countryService.getCountries(language);
		model.addAttribute("countries", countries);
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		//set the id if fails
		System.out.println("$#5865#"); entityOrder.setId(entityOrder.getOrder().getId());
		
		model.addAttribute("order", entityOrder);
		
		Set<OrderProduct> orderProducts = new HashSet<OrderProduct>();
		Set<OrderTotal> orderTotal = new HashSet<OrderTotal>();
		Set<OrderStatusHistory> orderHistory = new HashSet<OrderStatusHistory>();
		
		Date date = new Date();
		System.out.println("$#5866#"); if(!StringUtils.isBlank(entityOrder.getDatePurchased() ) ){
			try {
				date = DateUtil.getDate(entityOrder.getDatePurchased());
			} catch (Exception e) {
				ObjectError error = new ObjectError("datePurchased",messages.getMessage("message.invalid.date", locale));
				System.out.println("$#5867#"); result.addError(error);
			}
			
		} else{
			date = null;
		}
		 

		System.out.println("$#5868#"); if(!StringUtils.isBlank(entityOrder.getOrder().getCustomerEmailAddress() ) ){
			 java.util.regex.Matcher matcher = pattern.matcher(entityOrder.getOrder().getCustomerEmailAddress());
			 
				System.out.println("$#5869#"); if(!matcher.find()) {
				ObjectError error = new ObjectError("customerEmailAddress",messages.getMessage("Email.order.customerEmailAddress", locale));
				System.out.println("$#5870#"); result.addError(error);
			 }
		}else{
			ObjectError error = new ObjectError("customerEmailAddress",messages.getMessage("NotEmpty.order.customerEmailAddress", locale));
			System.out.println("$#5871#"); result.addError(error);
		}

		 
		System.out.println("$#5872#"); if( StringUtils.isBlank(entityOrder.getOrder().getBilling().getFirstName() ) ){
			 ObjectError error = new ObjectError("billingFirstName", messages.getMessage("NotEmpty.order.billingFirstName", locale));
				System.out.println("$#5873#"); result.addError(error);
		}
		
		System.out.println("$#5874#"); if( StringUtils.isBlank(entityOrder.getOrder().getBilling().getFirstName() ) ){
			 ObjectError error = new ObjectError("billingLastName", messages.getMessage("NotEmpty.order.billingLastName", locale));
				System.out.println("$#5875#"); result.addError(error);
		}
		 
		System.out.println("$#5876#"); if( StringUtils.isBlank(entityOrder.getOrder().getBilling().getAddress() ) ){
			 ObjectError error = new ObjectError("billingAddress", messages.getMessage("NotEmpty.order.billingStreetAddress", locale));
				System.out.println("$#5877#"); result.addError(error);
		}
		 
		System.out.println("$#5878#"); if( StringUtils.isBlank(entityOrder.getOrder().getBilling().getCity() ) ){
			 ObjectError error = new ObjectError("billingCity",messages.getMessage("NotEmpty.order.billingCity", locale));
				System.out.println("$#5879#"); result.addError(error);
		}
		 
		System.out.println("$#5880#"); if( entityOrder.getOrder().getBilling().getZone()==null){
			System.out.println("$#5881#"); if( StringUtils.isBlank(entityOrder.getOrder().getBilling().getState())){
				 ObjectError error = new ObjectError("billingState",messages.getMessage("NotEmpty.order.billingState", locale));
					System.out.println("$#5882#"); result.addError(error);
			}
		}
		 
		System.out.println("$#5883#"); if( StringUtils.isBlank(entityOrder.getOrder().getBilling().getPostalCode() ) ){
			 ObjectError error = new ObjectError("billingPostalCode", messages.getMessage("NotEmpty.order.billingPostCode", locale));
				System.out.println("$#5884#"); result.addError(error);
		}
		
		com.salesmanager.core.model.order.Order newOrder = orderService.getById(entityOrder.getOrder().getId() );
		
		
		//get capturable
		System.out.println("$#5885#"); if(newOrder.getPaymentType().name() != PaymentType.MONEYORDER.name()) {
			Transaction capturableTransaction = transactionService.getCapturableTransaction(newOrder);
			System.out.println("$#5886#"); if(capturableTransaction!=null) {
				model.addAttribute("capturableTransaction",capturableTransaction);
			}
		}
		
		
		//get refundable
		System.out.println("$#5887#"); if(newOrder.getPaymentType().name() != PaymentType.MONEYORDER.name()) {
			Transaction refundableTransaction = transactionService.getRefundableTransaction(newOrder);
			System.out.println("$#5888#"); if(refundableTransaction!=null) {
					model.addAttribute("capturableTransaction",null);//remove capturable
					model.addAttribute("refundableTransaction",refundableTransaction);
			}
		}
	
	
		System.out.println("$#5889#"); if (result.hasErrors()) {
			//  somehow we lose data, so reset Order detail info.
			System.out.println("$#5890#"); entityOrder.getOrder().setOrderProducts( orderProducts);
			System.out.println("$#5891#"); entityOrder.getOrder().setOrderTotal(orderTotal);
			System.out.println("$#5892#"); entityOrder.getOrder().setOrderHistory(orderHistory);
			
			System.out.println("$#5893#"); return ControllerConstants.Tiles.Order.ordersEdit;
		/*	"admin-orders-edit";  */
		}
		
		OrderStatusHistory orderStatusHistory = new OrderStatusHistory();		



		
		Country deliveryCountry = countryService.getByCode( entityOrder.getOrder().getDelivery().getCountry().getIsoCode()); 
		Country billingCountry  = countryService.getByCode( entityOrder.getOrder().getBilling().getCountry().getIsoCode()) ;
		Zone billingZone = null;
		Zone deliveryZone = null;
		System.out.println("$#5894#"); if(entityOrder.getOrder().getBilling().getZone()!=null) {
			billingZone = zoneService.getByCode(entityOrder.getOrder().getBilling().getZone().getCode());
		}
		
		System.out.println("$#5895#"); if(entityOrder.getOrder().getDelivery().getZone()!=null) {
			deliveryZone = zoneService.getByCode(entityOrder.getOrder().getDelivery().getZone().getCode());
		}

		System.out.println("$#5896#"); newOrder.setCustomerEmailAddress(entityOrder.getOrder().getCustomerEmailAddress() );
		System.out.println("$#5897#"); newOrder.setStatus(entityOrder.getOrder().getStatus() );
		
		System.out.println("$#5898#"); newOrder.setDatePurchased(date);
		System.out.println("$#5899#"); newOrder.setLastModified( new Date() );
		
		System.out.println("$#5900#"); if(!StringUtils.isBlank(entityOrder.getOrderHistoryComment() ) ) {
			System.out.println("$#5901#"); orderStatusHistory.setComments( entityOrder.getOrderHistoryComment() );
			System.out.println("$#5902#"); orderStatusHistory.setCustomerNotified(1);
			System.out.println("$#5903#"); orderStatusHistory.setStatus(entityOrder.getOrder().getStatus());
			System.out.println("$#5904#"); orderStatusHistory.setDateAdded(new Date() );
			System.out.println("$#5905#"); orderStatusHistory.setOrder(newOrder);
			newOrder.getOrderHistory().add( orderStatusHistory );
			System.out.println("$#5906#"); entityOrder.setOrderHistoryComment( "" );
		}		
		
		System.out.println("$#5907#"); newOrder.setDelivery( entityOrder.getOrder().getDelivery() );
		System.out.println("$#5908#"); newOrder.setBilling( entityOrder.getOrder().getBilling() );
		System.out.println("$#5909#"); newOrder.setCustomerAgreement(entityOrder.getOrder().getCustomerAgreement());
		
		System.out.println("$#5910#"); newOrder.getDelivery().setCountry(deliveryCountry );
		System.out.println("$#5911#"); newOrder.getBilling().setCountry(billingCountry );
		
		System.out.println("$#5912#"); if(billingZone!=null) {
			System.out.println("$#5913#"); newOrder.getBilling().setZone(billingZone);
		}
		
		System.out.println("$#5914#"); if(deliveryZone!=null) {
			System.out.println("$#5915#"); newOrder.getDelivery().setZone(deliveryZone);
		}
		
		System.out.println("$#5916#"); orderService.saveOrUpdate(newOrder);
		System.out.println("$#5917#"); entityOrder.setOrder(newOrder);
		System.out.println("$#5918#"); entityOrder.setBilling(newOrder.getBilling());
		System.out.println("$#5919#"); entityOrder.setDelivery(newOrder.getDelivery());
		model.addAttribute("order", entityOrder);
		
		Long customerId = newOrder.getCustomerId();
		
		System.out.println("$#5921#"); System.out.println("$#5920#"); if(customerId!=null && customerId>0) {
		
			try {
				
				Customer customer = customerService.getById(customerId);
				System.out.println("$#5923#"); if(customer!=null) {
					model.addAttribute("customer",customer);
				}
				
				
			} catch(Exception e) {
				LOGGER.error("Error while getting customer for customerId " + customerId, e);
			}
		
		}

		List<OrderProductDownload> orderProductDownloads = orderProdctDownloadService.getByOrderId(newOrder.getId());
		System.out.println("$#5924#"); if(CollectionUtils.isNotEmpty(orderProductDownloads)) {
			model.addAttribute("downloads",orderProductDownloads);
		}
		
		
		/** 
		 * send email if admin posted orderHistoryComment
		 * 
		 * **/
		
		System.out.println("$#5925#"); if(StringUtils.isBlank(entityOrder.getOrderHistoryComment())) {
		
			try {
				
				Customer customer = customerService.getById(newOrder.getCustomerId());
				Language lang = store.getDefaultLanguage();
				System.out.println("$#5926#"); if(customer!=null) {
					lang = customer.getDefaultLanguage();
				}
				
				Locale customerLocale = LocaleUtils.getLocale(lang);

				StringBuilder customerName = new StringBuilder();
				customerName.append(newOrder.getBilling().getFirstName()).append(" ").append(newOrder.getBilling().getLastName());
				
				
				Map<String, String> templateTokens = emailUtils.createEmailObjectsMap(request.getContextPath(), store, messages, customerLocale);
				templateTokens.put(EmailConstants.EMAIL_CUSTOMER_NAME, customerName.toString());
				templateTokens.put(EmailConstants.EMAIL_TEXT_ORDER_NUMBER, messages.getMessage("email.order.confirmation", new String[]{String.valueOf(newOrder.getId())}, customerLocale));
				templateTokens.put(EmailConstants.EMAIL_TEXT_DATE_ORDERED, messages.getMessage("email.order.ordered", new String[]{entityOrder.getDatePurchased()}, customerLocale));
				templateTokens.put(EmailConstants.EMAIL_TEXT_STATUS_COMMENTS, messages.getMessage("email.order.comments", new String[]{entityOrder.getOrderHistoryComment()}, customerLocale));
				templateTokens.put(EmailConstants.EMAIL_TEXT_DATE_UPDATED, messages.getMessage("email.order.updated", new String[]{DateUtil.formatDate(new Date())}, customerLocale));

				
				Email email = new Email();
				System.out.println("$#5927#"); email.setFrom(store.getStorename());
				System.out.println("$#5928#"); email.setFromEmail(store.getStoreEmailAddress());
				System.out.println("$#5929#"); email.setSubject(messages.getMessage("email.order.status.title",new String[]{String.valueOf(newOrder.getId())},customerLocale));
				System.out.println("$#5930#"); email.setTo(entityOrder.getOrder().getCustomerEmailAddress());
				System.out.println("$#5931#"); email.setTemplateName(ORDER_STATUS_TMPL);
				System.out.println("$#5932#"); email.setTemplateTokens(templateTokens);
	
	
				
				System.out.println("$#5933#"); emailService.sendHtmlEmail(store, email);
			
			} catch (Exception e) {
				LOGGER.error("Cannot send email to customer",e);
			}
			
		}
		
		model.addAttribute("success","success");

		
		System.out.println("$#5934#"); return  ControllerConstants.Tiles.Order.ordersEdit;
	    /*	"admin-orders-edit";  */
	}

	private void setMenu(Model model, HttpServletRequest request) throws Exception {
	
		//display menu
		Map<String,String> activeMenus = new HashMap<String,String>();
		activeMenus.put("order", "order");
		activeMenus.put("order-list", "order-list");
		
		@SuppressWarnings("unchecked")
		Map<String, Menu> menus = (Map<String, Menu>)request.getAttribute("MENUMAP");

		model.addAttribute("activeMenus",activeMenus);
		
		Menu currentMenu = (Menu)menus.get("order");
		model.addAttribute("currentMenu",currentMenu);
		model.addAttribute("activeMenus",activeMenus);
		//
		
	}

}
