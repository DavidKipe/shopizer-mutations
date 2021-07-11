package com.salesmanager.shop.admin.controller.orders;

import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.services.order.OrderService;
import com.salesmanager.core.business.services.payments.PaymentService;
import com.salesmanager.core.business.services.payments.TransactionService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.services.system.EmailService;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.order.orderstatus.OrderStatusHistory;
import com.salesmanager.core.model.payments.Transaction;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.modules.integration.IntegrationException;
import com.salesmanager.shop.admin.model.orders.Refund;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.utils.DateUtil;
import com.salesmanager.shop.utils.EmailTemplatesUtils;
import com.salesmanager.shop.utils.LabelUtils;
import com.salesmanager.shop.utils.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * Manage order details
 * @author Carl Samson
 */
@Controller
public class OrderActionsControler {
	
private static final Logger LOGGER = LoggerFactory.getLogger(OrderActionsControler.class);
	
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
	EmailTemplatesUtils emailTemplatesUtils;
	
	
	
	@PreAuthorize("hasRole('ORDER')")
	@RequestMapping(value="/admin/orders/captureOrder.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> captureOrder(HttpServletRequest request, HttpServletResponse response, Locale locale) {


		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		String sId = request.getParameter("id");
		
		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#5704#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		try {
			Long id = Long.parseLong(sId);
			
			Order order = orderService.getById(id);
			
			System.out.println("$#5705#"); if(order==null) {
				
				LOGGER.error("Order {0} does not exists", id);
				System.out.println("$#5706#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#5707#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#5708#"); if(order.getMerchant().getId().intValue()!=store.getId().intValue()) {
				
				LOGGER.error("Merchant store does not have order {0}",id);
				System.out.println("$#5709#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#5710#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			Customer customer = customerService.getById(order.getCustomerId());
			
			System.out.println("$#5711#"); if(customer==null) {
				System.out.println("$#5712#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				System.out.println("$#5713#"); resp.setStatusMessage(messages.getMessage("message.notexist.customer", locale));
				String returnString = resp.toJSONString();
				System.out.println("$#5714#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			paymentService.processCapturePayment(order, customer, store);

			System.out.println("$#5715#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);

		} catch (IntegrationException e) {
			LOGGER.error("Error while processing capture", e);
			System.out.println("$#5716#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#5717#"); resp.setErrorString(messages.getMessage(e.getMessageCode(),locale));
		} catch (Exception e) {
			LOGGER.error("Error while getting order", e);
			System.out.println("$#5718#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#5719#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#5720#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ORDER')")
	@RequestMapping(value="/admin/orders/refundOrder.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> refundOrder(@RequestBody Refund refund, HttpServletRequest request, HttpServletResponse response, Locale locale) {


		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		
		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#5721#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		BigDecimal submitedAmount = null;
		
		try {
			
			Order order = orderService.getById(refund.getOrderId());
			
			System.out.println("$#5722#"); if(order==null) {
				
				LOGGER.error("Order {0} does not exists", refund.getOrderId());
				System.out.println("$#5723#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#5724#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#5725#"); if(order.getMerchant().getId().intValue()!=store.getId().intValue()) {
				
				LOGGER.error("Merchant store does not have order {0}",refund.getOrderId());
				System.out.println("$#5726#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#5727#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
		
			//parse amount
			try {
				submitedAmount = new BigDecimal(refund.getAmount());
				System.out.println("$#5728#"); if(submitedAmount.doubleValue()==0) {
					System.out.println("$#5729#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
					System.out.println("$#5730#"); resp.setStatusMessage(messages.getMessage("message.invalid.amount", locale));
					String returnString = resp.toJSONString();
					System.out.println("$#5731#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
				}
				
			} catch (Exception e) {
				LOGGER.equals("invalid refundAmount " + refund.getAmount());
				System.out.println("$#5732#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#5733#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
				
				
				BigDecimal orderTotal = order.getTotal();
				System.out.println("$#5735#"); System.out.println("$#5734#"); if(submitedAmount.doubleValue()>orderTotal.doubleValue()) {
					System.out.println("$#5736#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
					System.out.println("$#5737#"); resp.setStatusMessage(messages.getMessage("message.invalid.amount", locale));
					String returnString = resp.toJSONString();
					System.out.println("$#5738#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
				}
				
				System.out.println("$#5740#"); System.out.println("$#5739#"); if(submitedAmount.doubleValue()<=0) {
					System.out.println("$#5741#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
					System.out.println("$#5742#"); resp.setStatusMessage(messages.getMessage("message.invalid.amount", locale));
					String returnString = resp.toJSONString();
					System.out.println("$#5743#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
				}
				
				Customer customer = customerService.getById(order.getCustomerId());
				
				System.out.println("$#5744#"); if(customer==null) {
					System.out.println("$#5745#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
					System.out.println("$#5746#"); resp.setStatusMessage(messages.getMessage("message.notexist.customer", locale));
					String returnString = resp.toJSONString();
					System.out.println("$#5747#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
				}
				
	
				paymentService.processRefund(order, customer, store, submitedAmount);

				System.out.println("$#5748#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);
		} catch (IntegrationException e) {
			LOGGER.error("Error while processing refund", e);
			System.out.println("$#5749#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#5750#"); resp.setErrorString(messages.getMessage(e.getMessageCode(),locale));
		} catch (Exception e) {
			LOGGER.error("Error while processing refund", e);
			System.out.println("$#5751#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#5752#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#5753#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ORDER')")
	@RequestMapping(value="/admin/orders/printInvoice.html", method=RequestMethod.GET)
	public void printInvoice(HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		
		String sId = request.getParameter("id");
		
		try {
			
		Long id = Long.parseLong(sId);
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		Order order = orderService.getById(id);
		
		System.out.println("$#5754#"); if(order.getMerchant().getId().intValue()!=store.getId().intValue()) {
			throw new Exception("Invalid order");
		}
		

		Language lang = store.getDefaultLanguage();
		
		

		ByteArrayOutputStream stream  = orderService.generateInvoice(store, order, lang);
		StringBuilder attachment = new StringBuilder();
		//attachment.append("attachment; filename=");
		attachment.append(order.getId());
		attachment.append(".pdf");
		
								System.out.println("$#5755#"); response.setHeader("Content-disposition", "attachment;filename=" + attachment.toString());

        //Set the mime type for the response
								System.out.println("$#5756#"); response.setContentType("application/pdf");

		
		System.out.println("$#5757#"); response.getOutputStream().write(stream.toByteArray());
		
		System.out.println("$#5758#"); response.flushBuffer();
			
			
		} catch(Exception e) {
			LOGGER.error("Error while printing a report",e);
		}
			
		
	}
	

	@SuppressWarnings("unchecked")
	@PreAuthorize("hasRole('ORDER')")
	@RequestMapping(value="/admin/orders/listTransactions.html", method=RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> listTransactions(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String sId = request.getParameter("id");

		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		
		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#5759#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		System.out.println("$#5760#"); if(sId==null) {
			System.out.println("$#5761#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			String returnString = resp.toJSONString();
			System.out.println("$#5762#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		}


		
		try {
			
			Long id = Long.parseLong(sId);
			

			Order dbOrder = orderService.getById(id);

			System.out.println("$#5763#"); if(dbOrder==null) {
				System.out.println("$#5764#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#5765#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			
			System.out.println("$#5766#"); if(dbOrder.getMerchant().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#5767#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#5768#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			

			
			List<Transaction> transactions = transactionService.listTransactions(dbOrder);
			
			System.out.println("$#5769#"); if(transactions!=null) {
				
				for(Transaction transaction : transactions) {
					@SuppressWarnings("rawtypes")
					Map entry = new HashMap();
					entry.put("transactionId", transaction.getId());
					entry.put("transactionDate", DateUtil.formatLongDate(transaction.getTransactionDate()));
					entry.put("transactionType", transaction.getTransactionType().name());
					entry.put("paymentType", transaction.getPaymentType().name());
					entry.put("transactionAmount", pricingService.getStringAmount(transaction.getAmount(), store));
					entry.put("transactionDetails", transaction.getTransactionDetails());
					System.out.println("$#5770#"); resp.addDataEntry(entry);
				}
				
				
			}
			
			
			System.out.println("$#5771#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);
			
		} catch(Exception e) {
			LOGGER.error("Cannot get transactions for order id " + sId, e);
			System.out.println("$#5772#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#5773#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#5774#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		
		
	}
	

	@PreAuthorize("hasRole('ORDER')")
	@RequestMapping(value="/admin/orders/sendInvoice.html", method=RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> sendInvoice(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String sId = request.getParameter("id");
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		
		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#5775#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		System.out.println("$#5776#"); if(sId==null) {
			System.out.println("$#5777#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			String returnString = resp.toJSONString();
			System.out.println("$#5778#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		}


		
		try {
			
			Long id = Long.parseLong(sId);
			

			Order dbOrder = orderService.getById(id);

			System.out.println("$#5779#"); if(dbOrder==null) {
				System.out.println("$#5780#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#5781#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			
			System.out.println("$#5782#"); if(dbOrder.getMerchant().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#5783#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#5784#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			//get customer
			Customer customer = customerService.getById(dbOrder.getCustomerId());
			
			System.out.println("$#5785#"); if(customer==null) {
				System.out.println("$#5786#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				System.out.println("$#5787#"); resp.setErrorString("Customer does not exist");
				String returnString = resp.toJSONString();
				System.out.println("$#5788#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			Locale customerLocale = LocaleUtils.getLocale(customer.getDefaultLanguage());
			
			System.out.println("$#5789#"); emailTemplatesUtils.sendOrderEmail(customer.getEmailAddress(), customer, dbOrder, customerLocale, customer.getDefaultLanguage(), store, request.getContextPath());
			
			
			System.out.println("$#5790#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);
			
		} catch(Exception e) {
			LOGGER.error("Cannot get transactions for order id " + sId, e);
			System.out.println("$#5791#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#5792#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#5793#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		
		
	}
	
	

	@PreAuthorize("hasRole('ORDER')")
	@RequestMapping(value="/admin/orders/updateStatus.html", method=RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> updateStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String sId = request.getParameter("id");
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		
		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#5794#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		System.out.println("$#5795#"); if(sId==null) {
			System.out.println("$#5796#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			String returnString = resp.toJSONString();
			System.out.println("$#5797#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		}


		
		try {
			
			Long id = Long.parseLong(sId);
			

			Order dbOrder = orderService.getById(id);

			System.out.println("$#5798#"); if(dbOrder==null) {
				System.out.println("$#5799#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#5800#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			
			System.out.println("$#5801#"); if(dbOrder.getMerchant().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#5802#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#5803#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			//get customer
			Customer customer = customerService.getById(dbOrder.getCustomerId());
			
			System.out.println("$#5804#"); if(customer==null) {
				System.out.println("$#5805#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				System.out.println("$#5806#"); resp.setErrorString("Customer does not exist");
				String returnString = resp.toJSONString();
				System.out.println("$#5807#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			Locale customerLocale = LocaleUtils.getLocale(customer.getDefaultLanguage());
			
			
			Set<OrderStatusHistory> orderStatus = dbOrder.getOrderHistory();
			OrderStatusHistory lastHistory = null;
			System.out.println("$#5808#"); if(orderStatus!=null) {
				int count = 1;
				for(OrderStatusHistory history : orderStatus) {
					System.out.println("$#5809#"); if(count==orderStatus.size()) {
						lastHistory = history;
						break;
					}
					System.out.println("$#5810#"); count++;
				}
			}
			
			System.out.println("$#5811#"); if(lastHistory==null) {
				System.out.println("$#5812#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				System.out.println("$#5813#"); resp.setErrorString("No history");
				String returnString = resp.toJSONString();
				System.out.println("$#5814#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			System.out.println("$#5815#"); emailTemplatesUtils.sendUpdateOrderStatusEmail(customer, dbOrder, lastHistory, store, customerLocale, request.getContextPath());

			
			System.out.println("$#5816#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);
			
		} catch(Exception e) {
			LOGGER.error("Cannot get transactions for order id " + sId, e);
			System.out.println("$#5817#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#5818#"); resp.setErrorString(e.getMessage());
			System.out.println("$#5819#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#5820#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		
		
	}
	
	@PreAuthorize("hasRole('ORDER')")
	@RequestMapping(value="/admin/orders/sendDownloadEmail.html", method=RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> sendDownloadEmail(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String sId = request.getParameter("id");
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		
		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#5821#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		System.out.println("$#5822#"); if(sId==null) {
			System.out.println("$#5823#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			String returnString = resp.toJSONString();
			System.out.println("$#5824#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		}


		
		try {
			
			Long id = Long.parseLong(sId);
			

			Order dbOrder = orderService.getById(id);

			System.out.println("$#5825#"); if(dbOrder==null) {
				System.out.println("$#5826#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#5827#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			
			System.out.println("$#5828#"); if(dbOrder.getMerchant().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#5829#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#5830#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			//get customer
			Customer customer = customerService.getById(dbOrder.getCustomerId());
			
			System.out.println("$#5831#"); if(customer==null) {
				System.out.println("$#5832#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				System.out.println("$#5833#"); resp.setErrorString("Customer does not exist");
				String returnString = resp.toJSONString();
				System.out.println("$#5834#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			Locale customerLocale = LocaleUtils.getLocale(customer.getDefaultLanguage());
			
			
			System.out.println("$#5835#"); emailTemplatesUtils.sendOrderDownloadEmail(customer, dbOrder, store, customerLocale, request.getContextPath());
			
			
			System.out.println("$#5836#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);
			
		} catch(Exception e) {
			LOGGER.error("Cannot get transactions for order id " + sId, e);
			System.out.println("$#5837#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#5838#"); resp.setErrorString(e.getMessage());
			System.out.println("$#5839#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#5840#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		
		
	}

	

}
