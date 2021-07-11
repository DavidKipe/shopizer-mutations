package com.salesmanager.core.business.modules.integration.payment.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.salesmanager.core.business.utils.ProductPriceUtils;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.payments.Payment;
import com.salesmanager.core.model.payments.PaymentType;
import com.salesmanager.core.model.payments.Transaction;
import com.salesmanager.core.model.payments.TransactionType;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.core.model.system.IntegrationConfiguration;
import com.salesmanager.core.model.system.IntegrationModule;
import com.salesmanager.core.modules.integration.IntegrationException;
import com.salesmanager.core.modules.integration.payment.model.PaymentModule;
import com.stripe.Stripe;
// import com.stripe.exception.APIConnectionException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Refund;

public class StripePayment implements PaymentModule {
	
	@Inject
	private ProductPriceUtils productPriceUtils;

	
	private final static String AUTHORIZATION = "Authorization";
	private final static String TRANSACTION = "Transaction";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StripePayment.class);
	
	@Override
	public void validateModuleConfiguration(
			IntegrationConfiguration integrationConfiguration,
			MerchantStore store) throws IntegrationException {
		
		
		List<String> errorFields = null;
		
		
		Map<String,String> keys = integrationConfiguration.getIntegrationKeys();
		
		//validate integrationKeys['secretKey']
		System.out.println("$#868#"); if(keys==null || StringUtils.isBlank(keys.get("secretKey"))) {
			errorFields = new ArrayList<String>();
			errorFields.add("secretKey");
		}
		
		//validate integrationKeys['publishableKey']
		System.out.println("$#870#"); if(keys==null || StringUtils.isBlank(keys.get("publishableKey"))) {
			System.out.println("$#872#"); if(errorFields==null) {
				errorFields = new ArrayList<String>();
			}
			errorFields.add("publishableKey");
		}
		
		
		System.out.println("$#873#"); if(errorFields!=null) {
			IntegrationException ex = new IntegrationException(IntegrationException.ERROR_VALIDATION_SAVE);
			System.out.println("$#874#"); ex.setErrorFields(errorFields);
			throw ex;
			
		}
		
		
		
	}


	@Override
	public Transaction initTransaction(MerchantStore store, Customer customer,
			BigDecimal amount, Payment payment,
			IntegrationConfiguration configuration, IntegrationModule module)
			throws IntegrationException {
						System.out.println("$#875#"); Validate.notNull(configuration,"Configuration cannot be null");
      String publicKey = configuration.getIntegrationKeys().get("publishableKey");
						System.out.println("$#876#"); Validate.notNull(publicKey,"Publishable key not found in configuration");

      Transaction transaction = new Transaction();
						System.out.println("$#877#"); transaction.setAmount(amount);
						System.out.println("$#878#"); transaction.setDetails(publicKey);
						System.out.println("$#879#"); transaction.setPaymentType(payment.getPaymentType());
						System.out.println("$#880#"); transaction.setTransactionDate(new Date());
						System.out.println("$#881#"); transaction.setTransactionType(payment.getTransactionType());
      
						System.out.println("$#882#"); return transaction;
	}

	@Override
	public Transaction authorize(MerchantStore store, Customer customer,
			List<ShoppingCartItem> items, BigDecimal amount, Payment payment,
			IntegrationConfiguration configuration, IntegrationModule module)
			throws IntegrationException {

		Transaction transaction = new Transaction();
		try {
			

			String apiKey = configuration.getIntegrationKeys().get("secretKey");

			System.out.println("$#883#"); if(payment.getPaymentMetaData()==null || StringUtils.isBlank(apiKey)) {
				IntegrationException te = new IntegrationException(
						"Can't process Stripe, missing payment.metaData");
				System.out.println("$#885#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
				System.out.println("$#886#"); te.setMessageCode("message.payment.error");
				System.out.println("$#887#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
				throw te;
			}
			
			/**
			 * this is send by stripe from tokenization ui
			 */
			String token = payment.getPaymentMetaData().get("stripe_token");
			
			System.out.println("$#888#"); if(StringUtils.isBlank(token)) {
				IntegrationException te = new IntegrationException(
						"Can't process Stripe, missing stripe token");
				System.out.println("$#889#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
				System.out.println("$#890#"); te.setMessageCode("message.payment.error");
				System.out.println("$#891#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
				throw te;
			}
			

			String amnt = productPriceUtils.getAdminFormatedAmount(store, amount);
			
			//stripe does not support floating point
			//so amnt * 100 or remove floating point
			//553.47 = 55347
			
			String strAmount = String.valueOf(amnt);
			strAmount = strAmount.replace(".","");
			
			Map<String, Object> chargeParams = new HashMap<String, Object>();
			chargeParams.put("amount", strAmount);
			chargeParams.put("capture", false);
			chargeParams.put("currency", store.getCurrency().getCode());
			chargeParams.put("source", token); // obtained with Stripe.js
			chargeParams.put("description", new StringBuilder().append(TRANSACTION).append(" - ").append(store.getStorename()).toString());
			
			Stripe.apiKey = apiKey;
			
			
			Charge ch = Charge.create(chargeParams);

			//Map<String,String> metadata = ch.getMetadata();
			
			
			System.out.println("$#892#"); transaction.setAmount(amount);
			//transaction.setOrder(order);
			System.out.println("$#893#"); transaction.setTransactionDate(new Date());
			System.out.println("$#894#"); transaction.setTransactionType(TransactionType.AUTHORIZE);
			System.out.println("$#895#"); transaction.setPaymentType(PaymentType.CREDITCARD);
			transaction.getTransactionDetails().put("TRANSACTIONID", token);
			transaction.getTransactionDetails().put("TRNAPPROVED", ch.getStatus());
			transaction.getTransactionDetails().put("TRNORDERNUMBER", ch.getId());
			transaction.getTransactionDetails().put("MESSAGETEXT", null);
			
		} catch (Exception e) {
			
			throw buildException(e);

		} 
		
		System.out.println("$#896#"); return transaction;

		
	}

	@Override
	public Transaction capture(MerchantStore store, Customer customer,
			Order order, Transaction capturableTransaction,
			IntegrationConfiguration configuration, IntegrationModule module)
			throws IntegrationException {


			Transaction transaction = new Transaction();
			try {
				
				
				String apiKey = configuration.getIntegrationKeys().get("secretKey");

				System.out.println("$#897#"); if(StringUtils.isBlank(apiKey)) {
					IntegrationException te = new IntegrationException(
							"Can't process Stripe, missing payment.metaData");
					System.out.println("$#898#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
					System.out.println("$#899#"); te.setMessageCode("message.payment.error");
					System.out.println("$#900#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
					throw te;
				}
				
				String chargeId = capturableTransaction.getTransactionDetails().get("TRNORDERNUMBER");
				
				System.out.println("$#901#"); if(StringUtils.isBlank(chargeId)) {
					IntegrationException te = new IntegrationException(
							"Can't process Stripe capture, missing TRNORDERNUMBER");
					System.out.println("$#902#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
					System.out.println("$#903#"); te.setMessageCode("message.payment.error");
					System.out.println("$#904#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
					throw te;
				}
				

				Stripe.apiKey = apiKey;
				
				Charge ch = Charge.retrieve(chargeId);
				ch.capture();
				
				
				System.out.println("$#905#"); transaction.setAmount(order.getTotal());
				System.out.println("$#906#"); transaction.setOrder(order);
				System.out.println("$#907#"); transaction.setTransactionDate(new Date());
				System.out.println("$#908#"); transaction.setTransactionType(TransactionType.CAPTURE);
				System.out.println("$#909#"); transaction.setPaymentType(PaymentType.CREDITCARD);
				transaction.getTransactionDetails().put("TRANSACTIONID", capturableTransaction.getTransactionDetails().get("TRANSACTIONID"));
				transaction.getTransactionDetails().put("TRNAPPROVED", ch.getStatus());
				transaction.getTransactionDetails().put("TRNORDERNUMBER", ch.getId());
				transaction.getTransactionDetails().put("MESSAGETEXT", null);
				
				//authorize a preauth 


				System.out.println("$#910#"); return transaction;
				
			} catch (Exception e) {
				
				throw buildException(e);

			}  

	}

	@Override
	public Transaction authorizeAndCapture(MerchantStore store, Customer customer,
			List<ShoppingCartItem> items, BigDecimal amount, Payment payment,
			IntegrationConfiguration configuration, IntegrationModule module)
			throws IntegrationException {

		String apiKey = configuration.getIntegrationKeys().get("secretKey");

		System.out.println("$#911#"); if(payment.getPaymentMetaData()==null || StringUtils.isBlank(apiKey)) {
			IntegrationException te = new IntegrationException(
					"Can't process Stripe, missing payment.metaData");
			System.out.println("$#913#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			System.out.println("$#914#"); te.setMessageCode("message.payment.error");
			System.out.println("$#915#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			throw te;
		}
		
		String token = payment.getPaymentMetaData().get("stripe_token");
		System.out.println("$#916#"); if(StringUtils.isBlank(token)) { //possibly from api
		  token = payment.getPaymentMetaData().get("paymentToken");
		}
		
		System.out.println("$#917#"); if(StringUtils.isBlank(token)) {
			IntegrationException te = new IntegrationException(
					"Can't process Stripe, missing stripe token");
			System.out.println("$#918#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			System.out.println("$#919#"); te.setMessageCode("message.payment.error");
			System.out.println("$#920#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			throw te;
		}
		


		Transaction transaction = new Transaction();
		try {
			
			String amnt = productPriceUtils.getAdminFormatedAmount(store, amount);
			
			//stripe does not support floating point
			//so amnt * 100 or remove floating point
			//553.47 = 55347
			
		
			String strAmount = String.valueOf(amnt);
			strAmount = strAmount.replace(".","");
			
			Map<String, Object> chargeParams = new HashMap<String, Object>();
			chargeParams.put("amount", strAmount);
			chargeParams.put("capture", true);
			chargeParams.put("currency", store.getCurrency().getCode());
			chargeParams.put("source", token); // obtained with Stripe.js
			chargeParams.put("description", new StringBuilder().append(TRANSACTION).append(" - ").append(store.getStorename()).toString());
			
			Stripe.apiKey = apiKey;
			
			
			Charge ch = Charge.create(chargeParams);
	
			//Map<String,String> metadata = ch.getMetadata();
			
			
			System.out.println("$#921#"); transaction.setAmount(amount);
			//transaction.setOrder(order);
			System.out.println("$#922#"); transaction.setTransactionDate(new Date());
			System.out.println("$#923#"); transaction.setTransactionType(TransactionType.AUTHORIZECAPTURE);
			System.out.println("$#924#"); transaction.setPaymentType(PaymentType.CREDITCARD);
			transaction.getTransactionDetails().put("TRANSACTIONID", token);
			transaction.getTransactionDetails().put("TRNAPPROVED", ch.getStatus());
			transaction.getTransactionDetails().put("TRNORDERNUMBER", ch.getId());
			transaction.getTransactionDetails().put("MESSAGETEXT", null);
			
		} catch (Exception e) {
			
			throw buildException(e);
	
		} 
		
		System.out.println("$#925#"); return transaction;
		
	}

	@Override
	public Transaction refund(boolean partial, MerchantStore store, Transaction transaction,
			Order order, BigDecimal amount,
			IntegrationConfiguration configuration, IntegrationModule module)
			throws IntegrationException {
		

		
		String apiKey = configuration.getIntegrationKeys().get("secretKey");

		System.out.println("$#926#"); if(StringUtils.isBlank(apiKey)) {
			IntegrationException te = new IntegrationException(
					"Can't process Stripe, missing payment.metaData");
			System.out.println("$#927#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			System.out.println("$#928#"); te.setMessageCode("message.payment.error");
			System.out.println("$#929#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			throw te;
		}

		try {
		

			String trnID = transaction.getTransactionDetails().get("TRNORDERNUMBER");
			
			String amnt = productPriceUtils.getAdminFormatedAmount(store, amount);
			
			Stripe.apiKey = apiKey;
			
			//stripe does not support floating point
			//so amnt * 100 or remove floating point
			//553.47 = 55347
			
			String strAmount = String.valueOf(amnt);
			strAmount = strAmount.replace(".","");

			Charge ch = Charge.retrieve(trnID);

			Map<String, Object> params = new HashMap<>();
			params.put("charge", ch.getId());
			params.put("amount", strAmount);
			Refund re = Refund.create(params);

			transaction = new Transaction();
			System.out.println("$#930#"); transaction.setAmount(order.getTotal());
			System.out.println("$#931#"); transaction.setOrder(order);
			System.out.println("$#932#"); transaction.setTransactionDate(new Date());
			System.out.println("$#933#"); transaction.setTransactionType(TransactionType.CAPTURE);
			System.out.println("$#934#"); transaction.setPaymentType(PaymentType.CREDITCARD);
			transaction.getTransactionDetails().put("TRANSACTIONID", transaction.getTransactionDetails().get("TRANSACTIONID"));
			transaction.getTransactionDetails().put("TRNAPPROVED", re.getReason());
			transaction.getTransactionDetails().put("TRNORDERNUMBER", re.getId());
			transaction.getTransactionDetails().put("MESSAGETEXT", null);

			System.out.println("$#935#"); return transaction;

			
		} catch(Exception e) {
			
			throw buildException(e);

		} 
		
		
		
	}
	
	private IntegrationException buildException(Exception ex) {
		
		
	System.out.println("$#936#"); if(ex instanceof CardException) {
		  CardException e = (CardException)ex;
		  // Since it's a decline, CardException will be caught
		  //System.out.println("Status is: " + e.getCode());
		  //System.out.println("Message is: " + e.getMessage());
		  
		  
			/**
			 * 
				invalid_number 	The card number is not a valid credit card number.
				invalid_expiry_month 	The card's expiration month is invalid.
				invalid_expiry_year 	The card's expiration year is invalid.
				invalid_cvc 	The card's security code is invalid.
				incorrect_number 	The card number is incorrect.
				expired_card 	The card has expired.
				incorrect_cvc 	The card's security code is incorrect.
				incorrect_zip 	The card's zip code failed validation.
				card_declined 	The card was declined.
				missing 	There is no card on a customer that is being charged.
				processing_error 	An error occurred while processing the card.
				rate_limit 	An error occurred due to requests hitting the API too quickly. Please let us know if you're consistently running into this error.
			 */
		
			
			String declineCode = e.getDeclineCode();
			
			System.out.println("$#937#"); if("card_declined".equals(declineCode)) {
				IntegrationException te = new IntegrationException(
						"Can't process stripe message " + e.getMessage());
				System.out.println("$#938#"); te.setExceptionType(IntegrationException.EXCEPTION_PAYMENT_DECLINED);
				System.out.println("$#939#"); te.setMessageCode("message.payment.declined");
				System.out.println("$#940#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
				System.out.println("$#941#"); return te;
			}
			
			System.out.println("$#942#"); if("invalid_number".equals(declineCode)) {
				IntegrationException te = new IntegrationException(
						"Can't process stripe message " + e.getMessage());
				System.out.println("$#943#"); te.setExceptionType(IntegrationException.EXCEPTION_VALIDATION);
				System.out.println("$#944#"); te.setMessageCode("messages.error.creditcard.number");
				System.out.println("$#945#"); te.setErrorCode(IntegrationException.EXCEPTION_VALIDATION);
				System.out.println("$#946#"); return te;
			}
			
			System.out.println("$#947#"); if("invalid_expiry_month".equals(declineCode)) {
				IntegrationException te = new IntegrationException(
						"Can't process stripe message " + e.getMessage());
				System.out.println("$#948#"); te.setExceptionType(IntegrationException.EXCEPTION_VALIDATION);
				System.out.println("$#949#"); te.setMessageCode("messages.error.creditcard.dateformat");
				System.out.println("$#950#"); te.setErrorCode(IntegrationException.EXCEPTION_VALIDATION);
				System.out.println("$#951#"); return te;
			}
			
			System.out.println("$#952#"); if("invalid_expiry_year".equals(declineCode)) {
				IntegrationException te = new IntegrationException(
						"Can't process stripe message " + e.getMessage());
				System.out.println("$#953#"); te.setExceptionType(IntegrationException.EXCEPTION_VALIDATION);
				System.out.println("$#954#"); te.setMessageCode("messages.error.creditcard.dateformat");
				System.out.println("$#955#"); te.setErrorCode(IntegrationException.EXCEPTION_VALIDATION);
				System.out.println("$#956#"); return te;
			}
			
			System.out.println("$#957#"); if("invalid_cvc".equals(declineCode)) {
				IntegrationException te = new IntegrationException(
						"Can't process stripe message " + e.getMessage());
				System.out.println("$#958#"); te.setExceptionType(IntegrationException.EXCEPTION_VALIDATION);
				System.out.println("$#959#"); te.setMessageCode("messages.error.creditcard.cvc");
				System.out.println("$#960#"); te.setErrorCode(IntegrationException.EXCEPTION_VALIDATION);
				System.out.println("$#961#"); return te;
			}
			
			System.out.println("$#962#"); if("incorrect_number".equals(declineCode)) {
				IntegrationException te = new IntegrationException(
						"Can't process stripe message " + e.getMessage());
				System.out.println("$#963#"); te.setExceptionType(IntegrationException.EXCEPTION_VALIDATION);
				System.out.println("$#964#"); te.setMessageCode("messages.error.creditcard.number");
				System.out.println("$#965#"); te.setErrorCode(IntegrationException.EXCEPTION_VALIDATION);
				System.out.println("$#966#"); return te;
			}
			
			System.out.println("$#967#"); if("incorrect_cvc".equals(declineCode)) {
				IntegrationException te = new IntegrationException(
						"Can't process stripe message " + e.getMessage());
				System.out.println("$#968#"); te.setExceptionType(IntegrationException.EXCEPTION_VALIDATION);
				System.out.println("$#969#"); te.setMessageCode("messages.error.creditcard.cvc");
				System.out.println("$#970#"); te.setErrorCode(IntegrationException.EXCEPTION_VALIDATION);
				System.out.println("$#971#"); return te;
			}
			
			//nothing good so create generic error
			IntegrationException te = new IntegrationException(
					"Can't process stripe card  " + e.getMessage());
			System.out.println("$#972#"); te.setExceptionType(IntegrationException.EXCEPTION_VALIDATION);
			System.out.println("$#973#"); te.setMessageCode("messages.error.creditcard.number");
			System.out.println("$#974#"); te.setErrorCode(IntegrationException.EXCEPTION_VALIDATION);
			System.out.println("$#975#"); return te;
		

		  
	} else if (ex instanceof InvalidRequestException) { System.out.println("$#976#");
		LOGGER.error("InvalidRequest error with stripe", ex.getMessage());
		InvalidRequestException e =(InvalidRequestException)ex;
		IntegrationException te = new IntegrationException(
				"Can't process Stripe, missing invalid payment parameters");
		System.out.println("$#977#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
		System.out.println("$#978#"); te.setMessageCode("messages.error.creditcard.number");
		System.out.println("$#979#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
		System.out.println("$#980#"); return te;
		
	} else if (ex instanceof AuthenticationException) { System.out.println("$#981#");
		LOGGER.error("Authentication error with stripe", ex.getMessage());
		AuthenticationException e = (AuthenticationException)ex;
		  // Authentication with Stripe's API failed
		  // (maybe you changed API keys recently)
		IntegrationException te = new IntegrationException(
				"Can't process Stripe, missing invalid payment parameters");
		System.out.println("$#982#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
		System.out.println("$#983#"); te.setMessageCode("message.payment.error");
		System.out.println("$#984#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
		System.out.println("$#985#"); return te;
		
	} /*else if (ex instanceof APIConnectionException) { // DEPRECATED THIS EXCEPTION TYPE
		LOGGER.error("API connection error with stripe", ex.getMessage());
		APIConnectionException e = (APIConnectionException)ex;
		  // Network communication with Stripe failed
		IntegrationException te = new IntegrationException(
				"Can't process Stripe, missing invalid payment parameters");
		te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
		te.setMessageCode("message.payment.error");
		te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
		return te;
	} */else if (ex instanceof StripeException) { System.out.println("$#986#");
		LOGGER.error("Error with stripe", ex.getMessage());
		StripeException e = (StripeException)ex;
		  // Display a very generic error to the user, and maybe send
		  // yourself an email
		IntegrationException te = new IntegrationException(
				"Can't process Stripe authorize, missing invalid payment parameters");
		System.out.println("$#987#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
		System.out.println("$#988#"); te.setMessageCode("message.payment.error");
		System.out.println("$#989#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
		System.out.println("$#990#"); return te;
		
		

	} else if (ex instanceof Exception) { System.out.println("$#991#");
		LOGGER.error("Stripe module error", ex.getMessage());
		System.out.println("$#992#"); if(ex instanceof IntegrationException) {
			System.out.println("$#993#"); return (IntegrationException)ex;
		} else {
			IntegrationException te = new IntegrationException(
					"Can't process Stripe authorize, exception", ex);
			System.out.println("$#994#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			System.out.println("$#995#"); te.setMessageCode("message.payment.error");
			System.out.println("$#996#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			System.out.println("$#997#"); return te;
		}


	} else {
		LOGGER.error("Stripe module error", ex.getMessage());
		IntegrationException te = new IntegrationException(
				"Can't process Stripe authorize, exception", ex);
		System.out.println("$#998#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
		System.out.println("$#999#"); te.setMessageCode("message.payment.error");
		System.out.println("$#1000#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
		System.out.println("$#1001#"); return te;
	}

	}
	
	



}
