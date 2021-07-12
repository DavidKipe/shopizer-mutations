package com.salesmanager.shop.utils;

import com.salesmanager.core.business.modules.email.Email;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.services.system.EmailService;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.order.OrderTotal;
import com.salesmanager.core.model.order.orderproduct.OrderProduct;
import com.salesmanager.core.model.order.orderstatus.OrderStatusHistory;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.shop.constants.ApplicationConstants;
import com.salesmanager.shop.constants.EmailConstants;
import com.salesmanager.shop.model.customer.PersistableCustomer;
import com.salesmanager.shop.model.shop.ContactForm;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Date;
import java.util.Locale;
import java.util.Map;


@Component
public class EmailTemplatesUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailTemplatesUtils.class);
	
	@Inject
	private EmailService emailService;

	@Inject
	private LabelUtils messages;
	
	@Inject
	private CountryService countryService;
	
	@Inject
	private ProductService productService;
	
	@Inject
	private ZoneService zoneService;
	
	@Inject
	private PricingService pricingService;
	
	@Inject
	@Qualifier("img")
	private ImageFilePath imageUtils;
	
	@Inject
	private EmailUtils emailUtils;
	
	@Inject
	private FilePathUtils filePathUtils;
	
	private final static String LINE_BREAK = "<br/>";
	private final static String TABLE = "<table width=\"100%\">";
	private final static String CLOSING_TABLE = "</table>";
	private final static String TR = "<tr>";
	private final static String TR_BORDER = "<tr class=\"border\">";
	private final static String CLOSING_TR = "</tr>";
	private final static String TD = "<td valign=\"top\">";
	private final static String CLOSING_TD = "</td>";
	

	/**
	 * Sends an email to the customer after a completed order
	 * @param customer
	 * @param order
	 * @param customerLocale
	 * @param language
	 * @param merchantStore
	 * @param contextPath
	 */
	@Async
	public void sendOrderEmail(String toEmail, Customer customer, Order order, Locale customerLocale, Language language, MerchantStore merchantStore, String contextPath) {
			   /** issue with putting that elsewhere **/ 
		       LOGGER.info( "Sending welcome email to customer" );
		       try {
		    	   
		    	   Map<String,Zone> zones = zoneService.getZones(language);
		    	   
		    	   Map<String,Country> countries = countryService.getCountriesMap(language);
		    	   
		    	   //format Billing address
		    	   StringBuilder billing = new StringBuilder();
										System.out.println("$#15638#"); if(StringUtils.isBlank(order.getBilling().getCompany())) {
		    		   billing.append(order.getBilling().getFirstName()).append(" ")
		    		   .append(order.getBilling().getLastName()).append(LINE_BREAK);
		    	   } else {
		    		   billing.append(order.getBilling().getCompany()).append(LINE_BREAK);
		    	   }
		    	   billing.append(order.getBilling().getAddress()).append(LINE_BREAK);
		    	   billing.append(order.getBilling().getCity()).append(", ");
		    	   
										System.out.println("$#15639#"); if(order.getBilling().getZone()!=null) {
		    		   Zone zone = zones.get(order.getBilling().getZone().getCode());
											System.out.println("$#15640#"); if(zone!=null) {
		    			   billing.append(zone.getName());
		    		   }
		    		   billing.append(LINE_BREAK);
										} else if(!StringUtils.isBlank(order.getBilling().getState())) { System.out.println("$#15641#");
		    		   billing.append(order.getBilling().getState()).append(LINE_BREAK); 
		    	   } else {
											System.out.println("$#15641#"); // manual correction for else-if mutation coverage
			       }
		    	   Country country = countries.get(order.getBilling().getCountry().getIsoCode());
										System.out.println("$#15642#"); if(country!=null) {
		    		   billing.append(country.getName()).append(" ");
		    	   }
		    	   billing.append(order.getBilling().getPostalCode());
		    	   
		    	   
		    	   //format shipping address
		    	   StringBuilder shipping = null;
										System.out.println("$#15643#"); if(order.getDelivery()!=null && !StringUtils.isBlank(order.getDelivery().getFirstName())) {
		    		   shipping = new StringBuilder();
											System.out.println("$#15645#"); if(StringUtils.isBlank(order.getDelivery().getCompany())) {
			    		   shipping.append(order.getDelivery().getFirstName()).append(" ")
			    		   .append(order.getDelivery().getLastName()).append(LINE_BREAK);
			    	   } else {
			    		   shipping.append(order.getDelivery().getCompany()).append(LINE_BREAK);
			    	   }
			    	   shipping.append(order.getDelivery().getAddress()).append(LINE_BREAK);
			    	   shipping.append(order.getDelivery().getCity()).append(", ");
			    	   
											System.out.println("$#15646#"); if(order.getDelivery().getZone()!=null) {
			    		   Zone zone = zones.get(order.getDelivery().getZone().getCode());
												System.out.println("$#15647#"); if(zone!=null) {
			    			   shipping.append(zone.getName());
			    		   }
			    		   shipping.append(LINE_BREAK);
											} else if(!StringUtils.isBlank(order.getDelivery().getState())) { System.out.println("$#15648#");
			    		   shipping.append(order.getDelivery().getState()).append(LINE_BREAK); 
			    	   } else {
												System.out.println("$#15648#"); // manual correction for else-if mutation coverage
				       }
			    	   Country deliveryCountry = countries.get(order.getDelivery().getCountry().getIsoCode());
											System.out.println("$#15649#"); if(country!=null) {
			    		   shipping.append(deliveryCountry.getName()).append(" ");
			    	   }
			    	   shipping.append(order.getDelivery().getPostalCode());
		    	   }
		    	   
										System.out.println("$#15650#"); if(shipping==null && StringUtils.isNotBlank(order.getShippingModuleCode())) {
		    		   //TODO IF HAS NO SHIPPING
		    		   shipping = billing;
		    	   }
		    	   
		    	   //format order
		    	   //String storeUri = FilePathUtils.buildStoreUri(merchantStore, contextPath);
		    	   StringBuilder orderTable = new StringBuilder();
		    	   orderTable.append(TABLE);
		    	   for(OrderProduct product : order.getOrderProducts()) {
		    		   //Product productModel = productService.getByCode(product.getSku(), language);
		    		   orderTable.append(TR);
			    		   orderTable.append(TD).append(product.getProductName()).append(" - ").append(product.getSku()).append(CLOSING_TD);
		    		   	   orderTable.append(TD).append(messages.getMessage("label.quantity", customerLocale)).append(": ").append(product.getProductQuantity()).append(CLOSING_TD);
	    		   		   orderTable.append(TD).append(pricingService.getDisplayAmount(product.getOneTimeCharge(), merchantStore)).append(CLOSING_TD);
    		   		   orderTable.append(CLOSING_TR);
		    	   }

		    	   //order totals
		    	   for(OrderTotal total : order.getOrderTotal()) {
		    		   orderTable.append(TR_BORDER);
		    		   		//orderTable.append(TD);
		    		   		//orderTable.append(CLOSING_TD);
		    		   		orderTable.append(TD);
		    		   		orderTable.append(CLOSING_TD);
		    		   		orderTable.append(TD);
		    		   		orderTable.append("<strong>");
														System.out.println("$#15652#"); if(total.getModule().equals("tax")) {
		    		   				orderTable.append(total.getText()).append(": ");

		    		   			} else {
		    		   				//if(total.getModule().equals("total") || total.getModule().equals("subtotal")) {
		    		   				//}
		    		   				orderTable.append(messages.getMessage(total.getOrderTotalCode(), customerLocale)).append(": ");
		    		   				//if(total.getModule().equals("total") || total.getModule().equals("subtotal")) {
		    		   					
		    		   				//}
		    		   			}
		    		   		orderTable.append("</strong>");
		    		   		orderTable.append(CLOSING_TD);
		    		   		orderTable.append(TD);
		    		   			orderTable.append("<strong>");

		    		   			orderTable.append(pricingService.getDisplayAmount(total.getValue(), merchantStore));

	    		   				orderTable.append("</strong>");
		    		   		orderTable.append(CLOSING_TD);
		    		   orderTable.append(CLOSING_TR);
		    	   }
		    	   orderTable.append(CLOSING_TABLE);

		           Map<String, String> templateTokens = emailUtils.createEmailObjectsMap(contextPath, merchantStore, messages, customerLocale);
		           templateTokens.put(EmailConstants.LABEL_HI, messages.getMessage("label.generic.hi", customerLocale));
		           templateTokens.put(EmailConstants.EMAIL_CUSTOMER_FIRSTNAME, order.getBilling().getFirstName());
		           templateTokens.put(EmailConstants.EMAIL_CUSTOMER_LASTNAME, order.getBilling().getLastName());
		           
		           String[] params = {String.valueOf(order.getId())};
		           String[] dt = {DateUtil.formatDate(order.getDatePurchased())};
		           templateTokens.put(EmailConstants.EMAIL_ORDER_NUMBER, messages.getMessage("email.order.confirmation", params, customerLocale));
		           templateTokens.put(EmailConstants.EMAIL_ORDER_DATE, messages.getMessage("email.order.ordered", dt, customerLocale));
		           templateTokens.put(EmailConstants.EMAIL_ORDER_THANKS, messages.getMessage("email.order.thanks",customerLocale));
		           templateTokens.put(EmailConstants.ADDRESS_BILLING, billing.toString());
		           
		           templateTokens.put(EmailConstants.ORDER_PRODUCTS_DETAILS, orderTable.toString());
		           templateTokens.put(EmailConstants.EMAIL_ORDER_DETAILS_TITLE, messages.getMessage("label.order.details",customerLocale));
		           templateTokens.put(EmailConstants.ADDRESS_BILLING_TITLE, messages.getMessage("label.customer.billinginformation",customerLocale));
		           templateTokens.put(EmailConstants.PAYMENT_METHOD_TITLE, messages.getMessage("label.order.paymentmode",customerLocale));
		           templateTokens.put(EmailConstants.PAYMENT_METHOD_DETAILS, messages.getMessage(new StringBuilder().append("payment.type.").append(order.getPaymentType().name()).toString(),customerLocale,order.getPaymentType().name()));
		           
													System.out.println("$#15653#"); if(StringUtils.isNotBlank(order.getShippingModuleCode())) {
		        	   //templateTokens.put(EmailConstants.SHIPPING_METHOD_DETAILS, messages.getMessage(new StringBuilder().append("module.shipping.").append(order.getShippingModuleCode()).toString(),customerLocale,order.getShippingModuleCode()));
		        	   templateTokens.put(EmailConstants.SHIPPING_METHOD_DETAILS, messages.getMessage(new StringBuilder().append("module.shipping.").append(order.getShippingModuleCode()).toString(),new String[]{merchantStore.getStorename()},customerLocale));
		        	   templateTokens.put(EmailConstants.ADDRESS_SHIPPING_TITLE, messages.getMessage("label.order.shippingmethod",customerLocale));
		        	   templateTokens.put(EmailConstants.ADDRESS_DELIVERY_TITLE, messages.getMessage("label.customer.shippinginformation",customerLocale));
		        	   templateTokens.put(EmailConstants.SHIPPING_METHOD_TITLE, messages.getMessage("label.customer.shippinginformation",customerLocale));
		        	   templateTokens.put(EmailConstants.ADDRESS_DELIVERY, shipping.toString());
		           } else {
		        	   templateTokens.put(EmailConstants.SHIPPING_METHOD_DETAILS, "");
		        	   templateTokens.put(EmailConstants.ADDRESS_SHIPPING_TITLE, "");
		        	   templateTokens.put(EmailConstants.ADDRESS_DELIVERY_TITLE, "");
		        	   templateTokens.put(EmailConstants.SHIPPING_METHOD_TITLE, "");
		        	   templateTokens.put(EmailConstants.ADDRESS_DELIVERY, "");
		           }
		           
			       String status = messages.getMessage("label.order." + order.getStatus().name(), customerLocale, order.getStatus().name());
			       String[] statusMessage = {DateUtil.formatDate(order.getDatePurchased()),status};
		           templateTokens.put(EmailConstants.ORDER_STATUS, messages.getMessage("email.order.status", statusMessage, customerLocale));
		           

		           String[] title = {merchantStore.getStorename(), String.valueOf(order.getId())};
		           Email email = new Email();
													System.out.println("$#15654#"); email.setFrom(merchantStore.getStorename());
													System.out.println("$#15655#"); email.setFromEmail(merchantStore.getStoreEmailAddress());
													System.out.println("$#15656#"); email.setSubject(messages.getMessage("email.order.title", title, customerLocale));
													System.out.println("$#15657#"); email.setTo(toEmail);
													System.out.println("$#15658#"); email.setTemplateName(EmailConstants.EMAIL_ORDER_TPL);
													System.out.println("$#15659#"); email.setTemplateTokens(templateTokens);

		           LOGGER.debug( "Sending email to {} for order id {} ",customer.getEmailAddress(), order.getId() );
													System.out.println("$#15660#"); emailService.sendHtmlEmail(merchantStore, email);

		       } catch (Exception e) {
		           LOGGER.error("Error occured while sending order confirmation email ",e);
		       }
			
		}
	
	/**
	 * Sends an email to the customer after registration
	 * @param request
	 * @param customer
	 * @param merchantStore
	 * @param customerLocale
	 */
	@Async
	public void sendRegistrationEmail(
		PersistableCustomer customer, MerchantStore merchantStore,
			Locale customerLocale, String contextPath) {
		   /** issue with putting that elsewhere **/ 
	       LOGGER.info( "Sending welcome email to customer" );
	       try {

	           Map<String, String> templateTokens = emailUtils.createEmailObjectsMap(contextPath, merchantStore, messages, customerLocale);
	           templateTokens.put(EmailConstants.LABEL_HI, messages.getMessage("label.generic.hi", customerLocale));
	           templateTokens.put(EmailConstants.EMAIL_CUSTOMER_FIRSTNAME, customer.getBilling().getFirstName());
	           templateTokens.put(EmailConstants.EMAIL_CUSTOMER_LASTNAME, customer.getBilling().getLastName());
	           String[] greetingMessage = {merchantStore.getStorename(),filePathUtils.buildCustomerUri(merchantStore,contextPath),merchantStore.getStoreEmailAddress()};
	           templateTokens.put(EmailConstants.EMAIL_CUSTOMER_GREETING, messages.getMessage("email.customer.greeting", greetingMessage, customerLocale));
	           templateTokens.put(EmailConstants.EMAIL_USERNAME_LABEL, messages.getMessage("label.generic.username",customerLocale));
	           templateTokens.put(EmailConstants.EMAIL_PASSWORD_LABEL, messages.getMessage("label.generic.password",customerLocale));
	           templateTokens.put(EmailConstants.CUSTOMER_ACCESS_LABEL, messages.getMessage("label.customer.accessportal",customerLocale));
	           templateTokens.put(EmailConstants.ACCESS_NOW_LABEL, messages.getMessage("label.customer.accessnow",customerLocale));
	           templateTokens.put(EmailConstants.EMAIL_USER_NAME, customer.getUserName());
	           templateTokens.put(EmailConstants.EMAIL_CUSTOMER_PASSWORD, customer.getPassword());

	           //shop url
	           String customerUrl = filePathUtils.buildStoreUri(merchantStore, contextPath);
	           templateTokens.put(EmailConstants.CUSTOMER_ACCESS_URL, customerUrl);

	           Email email = new Email();
												System.out.println("$#15661#"); email.setFrom(merchantStore.getStorename());
												System.out.println("$#15662#"); email.setFromEmail(merchantStore.getStoreEmailAddress());
												System.out.println("$#15663#"); email.setSubject(messages.getMessage("email.newuser.title",customerLocale));
												System.out.println("$#15664#"); email.setTo(customer.getEmailAddress());
												System.out.println("$#15665#"); email.setTemplateName(EmailConstants.EMAIL_CUSTOMER_TPL);
												System.out.println("$#15666#"); email.setTemplateTokens(templateTokens);

	           LOGGER.debug( "Sending email to {} on their  registered email id {} ",customer.getBilling().getFirstName(),customer.getEmailAddress() );
												System.out.println("$#15667#"); emailService.sendHtmlEmail(merchantStore, email);

	       } catch (Exception e) {
	           LOGGER.error("Error occured while sending welcome email ",e);
	       }
		
	}
	
	@Async
	public void sendContactEmail(
			ContactForm contact, MerchantStore merchantStore,
				Locale storeLocale, String contextPath) {
			   /** issue with putting that elsewhere **/ 
		       LOGGER.info( "Sending email to store owner" );
		       try {

		           Map<String, String> templateTokens = emailUtils.createEmailObjectsMap(contextPath, merchantStore, messages, storeLocale);
		           
		           templateTokens.put(EmailConstants.EMAIL_CONTACT_NAME, contact.getName());
		           templateTokens.put(EmailConstants.EMAIL_CONTACT_EMAIL, contact.getEmail());
		           templateTokens.put(EmailConstants.EMAIL_CONTACT_CONTENT, contact.getComment());
		           
		           String[] contactSubject = {contact.getSubject()};
		           
		           templateTokens.put(EmailConstants.EMAIL_CUSTOMER_CONTACT, messages.getMessage("email.contact",contactSubject, storeLocale));
		           templateTokens.put(EmailConstants.EMAIL_CONTACT_NAME_LABEL, messages.getMessage("label.entity.name",storeLocale));
		           templateTokens.put(EmailConstants.EMAIL_CONTACT_EMAIL_LABEL, messages.getMessage("label.generic.email",storeLocale));



		           Email email = new Email();
													System.out.println("$#15668#"); email.setFrom(contact.getName());
		           //since shopizer sends email to store email, sender is store email
													System.out.println("$#15669#"); email.setFromEmail(merchantStore.getStoreEmailAddress());
													System.out.println("$#15670#"); email.setSubject(messages.getMessage("email.contact.title",storeLocale));
		           //contact has to be delivered to store owner, receiver is store email
													System.out.println("$#15671#"); email.setTo(merchantStore.getStoreEmailAddress());
													System.out.println("$#15672#"); email.setTemplateName(EmailConstants.EMAIL_CONTACT_TMPL);
													System.out.println("$#15673#"); email.setTemplateTokens(templateTokens);

		           LOGGER.debug( "Sending contact email");
													System.out.println("$#15674#"); emailService.sendHtmlEmail(merchantStore, email);

		       } catch (Exception e) {
		           LOGGER.error("Error occured while sending contact email ",e);
		       }
			
		}
	
	/**
	 * Send an email to the customer with last order status
	 * @param request
	 * @param customer
	 * @param order
	 * @param merchantStore
	 * @param customerLocale
	 */
	@Async
	public void sendUpdateOrderStatusEmail(
			Customer customer, Order order, OrderStatusHistory lastHistory, MerchantStore merchantStore,
			Locale customerLocale, String contextPath) {
		   /** issue with putting that elsewhere **/ 
	       LOGGER.info( "Sending order status email to customer" );
	       try {


				Map<String, String> templateTokens = emailUtils.createEmailObjectsMap(contextPath, merchantStore, messages, customerLocale);
				
		        templateTokens.put(EmailConstants.LABEL_HI, messages.getMessage("label.generic.hi", customerLocale));
		        templateTokens.put(EmailConstants.EMAIL_CUSTOMER_FIRSTNAME, customer.getBilling().getFirstName());
		        templateTokens.put(EmailConstants.EMAIL_CUSTOMER_LASTNAME, customer.getBilling().getLastName());
				
		        String[] statusMessageText = {String.valueOf(order.getId()),DateUtil.formatDate(order.getDatePurchased())};
		        String status = messages.getMessage("label.order." + order.getStatus().name(), customerLocale, order.getStatus().name());
		        String[] statusMessage = {DateUtil.formatDate(lastHistory.getDateAdded()),status};
		        
		        String comments = lastHistory.getComments();
										System.out.println("$#15675#"); if(StringUtils.isBlank(comments)) {
		        	comments = messages.getMessage("label.order." + order.getStatus().name(), customerLocale, order.getStatus().name());
		        }
		        
				templateTokens.put(EmailConstants.EMAIL_ORDER_STATUS_TEXT, messages.getMessage("email.order.statustext", statusMessageText, customerLocale));
				templateTokens.put(EmailConstants.EMAIL_ORDER_STATUS, messages.getMessage("email.order.status", statusMessage, customerLocale));
				templateTokens.put(EmailConstants.EMAIL_TEXT_STATUS_COMMENTS, comments);
				
				
				Email email = new Email();
				System.out.println("$#15676#"); email.setFrom(merchantStore.getStorename());
				System.out.println("$#15677#"); email.setFromEmail(merchantStore.getStoreEmailAddress());
				System.out.println("$#15678#"); email.setSubject(messages.getMessage("email.order.status.title",new String[]{String.valueOf(order.getId())},customerLocale));
				System.out.println("$#15679#"); email.setTo(customer.getEmailAddress());
				System.out.println("$#15680#"); email.setTemplateName(EmailConstants.ORDER_STATUS_TMPL);
				System.out.println("$#15681#"); email.setTemplateTokens(templateTokens);
	
	
				
				System.out.println("$#15682#"); emailService.sendHtmlEmail(merchantStore, email);

	       } catch (Exception e) {
	           LOGGER.error("Error occured while sending order download email ",e);
	       }
		
	}
	
	/**
	 * Send download email instructions to customer
	 * @param customer
	 * @param order
	 * @param merchantStore
	 * @param customerLocale
	 * @param contextPath
	 */
	@Async
	public void sendOrderDownloadEmail(
			Customer customer, Order order, MerchantStore merchantStore,
			Locale customerLocale, String contextPath) {
		   /** issue with putting that elsewhere **/ 
	       LOGGER.info( "Sending download email to customer" );
	       try {

	           Map<String, String> templateTokens = emailUtils.createEmailObjectsMap(contextPath, merchantStore, messages, customerLocale);
	           templateTokens.put(EmailConstants.LABEL_HI, messages.getMessage("label.generic.hi", customerLocale));
	           templateTokens.put(EmailConstants.EMAIL_CUSTOMER_FIRSTNAME, customer.getBilling().getFirstName());
	           templateTokens.put(EmailConstants.EMAIL_CUSTOMER_LASTNAME, customer.getBilling().getLastName());
	           String[] downloadMessage = {String.valueOf(ApplicationConstants.MAX_DOWNLOAD_DAYS), String.valueOf(order.getId()), filePathUtils.buildCustomerUri(merchantStore, contextPath), merchantStore.getStoreEmailAddress()};
	           templateTokens.put(EmailConstants.EMAIL_ORDER_DOWNLOAD, messages.getMessage("email.order.download.text", downloadMessage, customerLocale));
	           templateTokens.put(EmailConstants.CUSTOMER_ACCESS_LABEL, messages.getMessage("label.customer.accessportal",customerLocale));
	           templateTokens.put(EmailConstants.ACCESS_NOW_LABEL, messages.getMessage("label.customer.accessnow",customerLocale));

	           //shop url
	           String customerUrl = filePathUtils.buildStoreUri(merchantStore, contextPath);
	           templateTokens.put(EmailConstants.CUSTOMER_ACCESS_URL, customerUrl);

	           String[] orderInfo = {String.valueOf(order.getId())};
	           
	           Email email = new Email();
												System.out.println("$#15683#"); email.setFrom(merchantStore.getStorename());
												System.out.println("$#15684#"); email.setFromEmail(merchantStore.getStoreEmailAddress());
												System.out.println("$#15685#"); email.setSubject(messages.getMessage("email.order.download.title", orderInfo, customerLocale));
												System.out.println("$#15686#"); email.setTo(customer.getEmailAddress());
												System.out.println("$#15687#"); email.setTemplateName(EmailConstants.EMAIL_ORDER_DOWNLOAD_TPL);
												System.out.println("$#15688#"); email.setTemplateTokens(templateTokens);

	           LOGGER.debug( "Sending email to {} with download info",customer.getEmailAddress() );
												System.out.println("$#15689#"); emailService.sendHtmlEmail(merchantStore, email);

	       } catch (Exception e) {
	           LOGGER.error("Error occured while sending order download email ",e);
	       }
		
	}
	
	/**
	 * Sends a change password notification email to the Customer
	 * @param customer
	 * @param merchantStore
	 * @param customerLocale
	 * @param contextPath
	 */
	@Async
	public void changePasswordNotificationEmail(
			Customer customer, MerchantStore merchantStore,
			Locale customerLocale, String contextPath) {
	       LOGGER.debug( "Sending change password email" );
	       try {


				Map<String, String> templateTokens = emailUtils.createEmailObjectsMap(contextPath, merchantStore, messages, customerLocale);
				
		        templateTokens.put(EmailConstants.LABEL_HI, messages.getMessage("label.generic.hi", customerLocale));
		        templateTokens.put(EmailConstants.EMAIL_CUSTOMER_FIRSTNAME, customer.getBilling().getFirstName());
		        templateTokens.put(EmailConstants.EMAIL_CUSTOMER_LASTNAME, customer.getBilling().getLastName());
				
		        String[] date = {DateUtil.formatLongDate(new Date())};
		        
		        templateTokens.put(EmailConstants.EMAIL_NOTIFICATION_MESSAGE, messages.getMessage("label.notification.message.passwordchanged", date, customerLocale));
		        

				Email email = new Email();
				System.out.println("$#15690#"); email.setFrom(merchantStore.getStorename());
				System.out.println("$#15691#"); email.setFromEmail(merchantStore.getStoreEmailAddress());
				System.out.println("$#15692#"); email.setSubject(messages.getMessage("label.notification.title.passwordchanged",customerLocale));
				System.out.println("$#15693#"); email.setTo(customer.getEmailAddress());
				System.out.println("$#15694#"); email.setTemplateName(EmailConstants.EMAIL_NOTIFICATION_TMPL);
				System.out.println("$#15695#"); email.setTemplateTokens(templateTokens);
	
	
				
				System.out.println("$#15696#"); emailService.sendHtmlEmail(merchantStore, email);

	       } catch (Exception e) {
	           LOGGER.error("Error occured while sending change password email ",e);
	       }
		
	}

}
