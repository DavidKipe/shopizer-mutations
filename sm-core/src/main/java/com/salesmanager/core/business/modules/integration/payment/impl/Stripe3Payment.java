package com.salesmanager.core.business.modules.integration.payment.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

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
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCaptureParams;
import com.stripe.param.PaymentIntentCreateParams;

// import com.stripe.exception.APIConnectionException;

public class Stripe3Payment implements PaymentModule {

	private static final Logger LOGGER = LoggerFactory.getLogger(Stripe3Payment.class);

	@Inject
	private ProductPriceUtils productPriceUtils;

	private final static String AUTHORIZATION = "Authorization";
	private final static String TRANSACTION = "Transaction";


	@Override
	public Transaction initTransaction(MerchantStore store, Customer customer,
									   BigDecimal amount, Payment payment,
									   IntegrationConfiguration configuration, IntegrationModule module)
			throws IntegrationException {


		String strAmount = String.valueOf(amount);
		strAmount = strAmount.replace(".","");

		Transaction transaction = new Transaction();
		try {


			String apiKey = configuration.getIntegrationKeys().get("secretKey");

			System.out.println("$#728#"); if (StringUtils.isBlank(apiKey)) {
				IntegrationException te = new IntegrationException(
						"Can't process Stripe, missing payment.metaData");
				System.out.println("$#729#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
				System.out.println("$#730#"); te.setMessageCode("message.payment.error");
				System.out.println("$#731#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
				throw te;
			}

			Stripe.apiKey = apiKey;

			PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
					.setCurrency(store.getCurrency().getCode())
					.setAmount(Long.parseLong(strAmount))
					.setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.MANUAL)
					.build();

			// Create a PaymentIntent with the order amount and currency
			PaymentIntent intent = PaymentIntent.create(createParams);

			intent.getClientSecret();

			System.out.println("$#732#"); transaction.setAmount(amount);
			//transaction.setOrder(order);
			System.out.println("$#733#"); transaction.setTransactionDate(new Date());
			System.out.println("$#734#"); transaction.setTransactionType(TransactionType.AUTHORIZE);
			System.out.println("$#735#"); transaction.setPaymentType(PaymentType.CREDITCARD);
			transaction.getTransactionDetails().put("TRANSACTIONID", intent.getId());
			transaction.getTransactionDetails().put("TRNAPPROVED", intent.getStatus());
			transaction.getTransactionDetails().put("TRNORDERNUMBER", intent.getId());
			transaction.getTransactionDetails().put("INTENTSECRET", intent.getClientSecret());
			transaction.getTransactionDetails().put("MESSAGETEXT", null);

		} catch (Exception e) {
			throw buildException(e);
		}

		System.out.println("$#736#"); return transaction;
	}

	@Override
	public void validateModuleConfiguration(
			IntegrationConfiguration integrationConfiguration,
			MerchantStore store) throws IntegrationException {
		
		
		List<String> errorFields = null;
		
		
		Map<String,String> keys = integrationConfiguration.getIntegrationKeys();
		
		//validate integrationKeys['secretKey']
		System.out.println("$#737#"); if(keys==null || StringUtils.isBlank(keys.get("secretKey"))) {
			errorFields = new ArrayList<String>();
			errorFields.add("secretKey");
		}
		
		//validate integrationKeys['publishableKey']
		System.out.println("$#739#"); if(keys==null || StringUtils.isBlank(keys.get("publishableKey"))) {
			System.out.println("$#741#"); if(errorFields==null) {
				errorFields = new ArrayList<String>();
			}
			errorFields.add("publishableKey");
		}
		
		
		System.out.println("$#742#"); if(errorFields!=null) {
			IntegrationException ex = new IntegrationException(IntegrationException.ERROR_VALIDATION_SAVE);
			System.out.println("$#743#"); ex.setErrorFields(errorFields);
			throw ex;
		}
	}


	/* -----------------------------------------  */

	@Override
	public Transaction authorize(MerchantStore store, Customer customer,
			List<ShoppingCartItem> items, BigDecimal amount, Payment payment,
			IntegrationConfiguration configuration, IntegrationModule module)
			throws IntegrationException {
		
		Transaction transaction = new Transaction();
		try {
			

			String apiKey = configuration.getIntegrationKeys().get("secretKey");

			System.out.println("$#744#"); if(payment.getPaymentMetaData()==null || StringUtils.isBlank(apiKey)) {
				IntegrationException te = new IntegrationException(
						"Can't process Stripe, missing payment.metaData");
				System.out.println("$#746#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
				System.out.println("$#747#"); te.setMessageCode("message.payment.error");
				System.out.println("$#748#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
				throw te;
			}
			
			/**
			 * This is the PaymentIntent ID created on the Frontend, that we will now store.
			 */
			String token = payment.getPaymentMetaData().get("stripe_token");
			
			System.out.println("$#749#"); if(StringUtils.isBlank(token)) {
				IntegrationException te = new IntegrationException(
						"Can't process Stripe, missing stripe token");
				System.out.println("$#750#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
				System.out.println("$#751#"); te.setMessageCode("message.payment.error");
				System.out.println("$#752#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
				throw te;
			}

			Stripe.apiKey = apiKey;
			
			PaymentIntent paymentIntent = PaymentIntent.retrieve(
					token
			);
			
			System.out.println("$#753#"); transaction.setAmount(amount);
			//transaction.setOrder(order);
			System.out.println("$#754#"); transaction.setTransactionDate(new Date());
			System.out.println("$#755#"); transaction.setTransactionType(TransactionType.AUTHORIZE);
			System.out.println("$#756#"); transaction.setPaymentType(PaymentType.CREDITCARD);
			transaction.getTransactionDetails().put("TRANSACTIONID", token);
			transaction.getTransactionDetails().put("TRNAPPROVED", paymentIntent.getStatus());
			transaction.getTransactionDetails().put("TRNORDERNUMBER", paymentIntent.getId());  // <---- We store the PI id here
			transaction.getTransactionDetails().put("MESSAGETEXT", null);
			
		} catch (Exception e) {
			
			throw buildException(e);

		} 
		
		System.out.println("$#757#"); return transaction;

		
	}

	@Override
	public Transaction capture(MerchantStore store, Customer customer,
			Order order, Transaction capturableTransaction,
			IntegrationConfiguration configuration, IntegrationModule module)
			throws IntegrationException {

		Transaction transaction = new Transaction();
		try {

			String apiKey = configuration.getIntegrationKeys().get("secretKey");

			System.out.println("$#758#"); if(StringUtils.isBlank(apiKey)) {
				IntegrationException te = new IntegrationException(
						"Can't process Stripe, missing payment.metaData");
				System.out.println("$#759#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
				System.out.println("$#760#"); te.setMessageCode("message.payment.error");
				System.out.println("$#761#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
				throw te;
			}

			String chargeId = capturableTransaction.getTransactionDetails().get("TRNORDERNUMBER");       // <---- We retrieve the PI id here

			System.out.println("$#762#"); if(StringUtils.isBlank(chargeId)) {
				IntegrationException te = new IntegrationException(
						"Can't process Stripe capture, missing TRNORDERNUMBER");
				System.out.println("$#763#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
				System.out.println("$#764#"); te.setMessageCode("message.payment.error");
				System.out.println("$#765#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
				throw te;
			}

			String amnt = productPriceUtils.getAdminFormatedAmount(store, order.getTotal());
			String strAmount = String.valueOf(amnt);
			strAmount = strAmount.replace(".","");

			Stripe.apiKey = apiKey;


			PaymentIntent paymentIntent = PaymentIntent.retrieve(
					chargeId
			);

			System.out.println("$#766#");
			PaymentIntentCaptureParams params =
					PaymentIntentCaptureParams.builder()
							.setAmountToCapture(Long.parseLong(strAmount))
							.setStatementDescriptor(
									store.getStorename().length() > 22 ?
											store.getStorename().substring(0, 22) :
											store.getStorename()
							)
					.build();

			paymentIntent = paymentIntent.capture(params);

			System.out.println("$#768#"); transaction.setAmount(order.getTotal());
			System.out.println("$#769#"); transaction.setOrder(order);
			System.out.println("$#770#"); transaction.setTransactionDate(new Date());
			System.out.println("$#771#"); transaction.setTransactionType(TransactionType.CAPTURE);
			System.out.println("$#772#"); transaction.setPaymentType(PaymentType.CREDITCARD);
			transaction.getTransactionDetails().put("TRANSACTIONID", capturableTransaction.getTransactionDetails().get("TRANSACTIONID"));
			transaction.getTransactionDetails().put("TRNAPPROVED", paymentIntent.getStatus());
			transaction.getTransactionDetails().put("TRNORDERNUMBER", paymentIntent.getId());
			transaction.getTransactionDetails().put("MESSAGETEXT", null);

			System.out.println("$#773#"); return transaction;

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

		System.out.println("$#774#"); if(payment.getPaymentMetaData()==null || StringUtils.isBlank(apiKey)) {
			IntegrationException te = new IntegrationException(
					"Can't process Stripe, missing payment.metaData");
			System.out.println("$#776#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			System.out.println("$#777#"); te.setMessageCode("message.payment.error");
			System.out.println("$#778#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			throw te;
		}
		
		String token = payment.getPaymentMetaData().get("stripe_token");
		System.out.println("$#779#"); if(StringUtils.isBlank(token)) { //possibly from api
		  token = payment.getPaymentMetaData().get("paymentToken");
		}
		
		System.out.println("$#780#"); if(StringUtils.isBlank(token)) {
			IntegrationException te = new IntegrationException(
					"Can't process Stripe, missing stripe token");
			System.out.println("$#781#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			System.out.println("$#782#"); te.setMessageCode("message.payment.error");
			System.out.println("$#783#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
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
			
			/*Map<String, Object> chargeParams = new HashMap<String, Object>();
			chargeParams.put("amount", strAmount);
			chargeParams.put("capture", true);
			chargeParams.put("currency", store.getCurrency().getCode());
			chargeParams.put("source", token); // obtained with Stripe.js
			chargeParams.put("description", new StringBuilder().append(TRANSACTION).append(" - ").append(store.getStorename()).toString());
			*/

			Stripe.apiKey = apiKey;
			

			PaymentIntent paymentIntent = PaymentIntent.retrieve(
					token
		  	);

			System.out.println("$#784#");
			PaymentIntentCaptureParams params =
					PaymentIntentCaptureParams.builder()
							.setAmountToCapture(Long.parseLong(strAmount))
							.setStatementDescriptor(
									store.getStorename().length() > 22 ?
											store.getStorename().substring(0, 22) :
											store.getStorename()
							)
					.build();

			paymentIntent = paymentIntent.capture(params);
	
			//Map<String,String> metadata = ch.getMetadata();
			
			
			System.out.println("$#786#"); transaction.setAmount(amount);
			//transaction.setOrder(order);
			System.out.println("$#787#"); transaction.setTransactionDate(new Date());
			System.out.println("$#788#"); transaction.setTransactionType(TransactionType.AUTHORIZECAPTURE);
			System.out.println("$#789#"); transaction.setPaymentType(PaymentType.CREDITCARD);
			transaction.getTransactionDetails().put("TRANSACTIONID", token);
			transaction.getTransactionDetails().put("TRNAPPROVED", paymentIntent.getStatus());
			transaction.getTransactionDetails().put("TRNORDERNUMBER", paymentIntent.getId());
			transaction.getTransactionDetails().put("MESSAGETEXT", null);
			
		} catch (Exception e) {

			System.out.println("$#790#"); e.printStackTrace();

			throw buildException(e);
	
		} 
		
		System.out.println("$#791#"); return transaction;
		
	}

	@Override
	public Transaction refund(boolean partial, MerchantStore store, Transaction transaction,
			Order order, BigDecimal amount,
			IntegrationConfiguration configuration, IntegrationModule module)
			throws IntegrationException {
		
		
		
		String apiKey = configuration.getIntegrationKeys().get("secretKey");

		System.out.println("$#792#"); if(StringUtils.isBlank(apiKey)) {
			IntegrationException te = new IntegrationException(
					"Can't process Stripe, missing payment.metaData");
			System.out.println("$#793#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			System.out.println("$#794#"); te.setMessageCode("message.payment.error");
			System.out.println("$#795#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
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

			PaymentIntent paymentIntent = PaymentIntent.retrieve(
					trnID
			);

			Map<String, Object> params = new HashMap<>();
			params.put("payment_intent", paymentIntent.getId());
			params.put("amount", strAmount);
			Refund re = Refund.create(params);

			transaction = new Transaction();
			System.out.println("$#796#"); transaction.setAmount(order.getTotal());
			System.out.println("$#797#"); transaction.setOrder(order);
			System.out.println("$#798#"); transaction.setTransactionDate(new Date());
			System.out.println("$#799#"); transaction.setTransactionType(TransactionType.CAPTURE);
			System.out.println("$#800#"); transaction.setPaymentType(PaymentType.CREDITCARD);
			transaction.getTransactionDetails().put("TRANSACTIONID", transaction.getTransactionDetails().get("TRANSACTIONID"));
			transaction.getTransactionDetails().put("TRNAPPROVED", re.getReason());
			transaction.getTransactionDetails().put("TRNORDERNUMBER", re.getId());
			transaction.getTransactionDetails().put("MESSAGETEXT", null);

			System.out.println("$#801#"); return transaction;

			
		} catch(Exception e) {
			
			throw buildException(e);

		} 
		
		
		
	}
	
	private IntegrationException buildException(Exception ex) {
		
		
	System.out.println("$#802#"); if(ex instanceof CardException) {
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
			
			System.out.println("$#803#"); if("card_declined".equals(declineCode)) {
				IntegrationException te = new IntegrationException(
						"Can't process stripe message " + e.getMessage());
				System.out.println("$#804#"); te.setExceptionType(IntegrationException.EXCEPTION_PAYMENT_DECLINED);
				System.out.println("$#805#"); te.setMessageCode("message.payment.declined");
				System.out.println("$#806#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
				System.out.println("$#807#"); return te;
			}
			
			System.out.println("$#808#"); if("invalid_number".equals(declineCode)) {
				IntegrationException te = new IntegrationException(
						"Can't process stripe message " + e.getMessage());
				System.out.println("$#809#"); te.setExceptionType(IntegrationException.EXCEPTION_VALIDATION);
				System.out.println("$#810#"); te.setMessageCode("messages.error.creditcard.number");
				System.out.println("$#811#"); te.setErrorCode(IntegrationException.EXCEPTION_VALIDATION);
				System.out.println("$#812#"); return te;
			}
			
			System.out.println("$#813#"); if("invalid_expiry_month".equals(declineCode)) {
				IntegrationException te = new IntegrationException(
						"Can't process stripe message " + e.getMessage());
				System.out.println("$#814#"); te.setExceptionType(IntegrationException.EXCEPTION_VALIDATION);
				System.out.println("$#815#"); te.setMessageCode("messages.error.creditcard.dateformat");
				System.out.println("$#816#"); te.setErrorCode(IntegrationException.EXCEPTION_VALIDATION);
				System.out.println("$#817#"); return te;
			}
			
			System.out.println("$#818#"); if("invalid_expiry_year".equals(declineCode)) {
				IntegrationException te = new IntegrationException(
						"Can't process stripe message " + e.getMessage());
				System.out.println("$#819#"); te.setExceptionType(IntegrationException.EXCEPTION_VALIDATION);
				System.out.println("$#820#"); te.setMessageCode("messages.error.creditcard.dateformat");
				System.out.println("$#821#"); te.setErrorCode(IntegrationException.EXCEPTION_VALIDATION);
				System.out.println("$#822#"); return te;
			}
			
			System.out.println("$#823#"); if("invalid_cvc".equals(declineCode)) {
				IntegrationException te = new IntegrationException(
						"Can't process stripe message " + e.getMessage());
				System.out.println("$#824#"); te.setExceptionType(IntegrationException.EXCEPTION_VALIDATION);
				System.out.println("$#825#"); te.setMessageCode("messages.error.creditcard.cvc");
				System.out.println("$#826#"); te.setErrorCode(IntegrationException.EXCEPTION_VALIDATION);
				System.out.println("$#827#"); return te;
			}
			
			System.out.println("$#828#"); if("incorrect_number".equals(declineCode)) {
				IntegrationException te = new IntegrationException(
						"Can't process stripe message " + e.getMessage());
				System.out.println("$#829#"); te.setExceptionType(IntegrationException.EXCEPTION_VALIDATION);
				System.out.println("$#830#"); te.setMessageCode("messages.error.creditcard.number");
				System.out.println("$#831#"); te.setErrorCode(IntegrationException.EXCEPTION_VALIDATION);
				System.out.println("$#832#"); return te;
			}
			
			System.out.println("$#833#"); if("incorrect_cvc".equals(declineCode)) {
				IntegrationException te = new IntegrationException(
						"Can't process stripe message " + e.getMessage());
				System.out.println("$#834#"); te.setExceptionType(IntegrationException.EXCEPTION_VALIDATION);
				System.out.println("$#835#"); te.setMessageCode("messages.error.creditcard.cvc");
				System.out.println("$#836#"); te.setErrorCode(IntegrationException.EXCEPTION_VALIDATION);
				System.out.println("$#837#"); return te;
			}
			
			//nothing good so create generic error
			IntegrationException te = new IntegrationException(
					"Can't process stripe card  " + e.getMessage());
			System.out.println("$#838#"); te.setExceptionType(IntegrationException.EXCEPTION_VALIDATION);
			System.out.println("$#839#"); te.setMessageCode("messages.error.creditcard.number");
			System.out.println("$#840#"); te.setErrorCode(IntegrationException.EXCEPTION_VALIDATION);
			System.out.println("$#841#"); return te;
		

		  
	} else if (ex instanceof InvalidRequestException) { System.out.println("$#842#");
		LOGGER.error("InvalidRequest error with stripe", ex.getMessage());
		InvalidRequestException e =(InvalidRequestException)ex;
		IntegrationException te = new IntegrationException(
				"Can't process Stripe, missing invalid payment parameters");
		System.out.println("$#843#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
		System.out.println("$#844#"); te.setMessageCode("messages.error.creditcard.number");
		System.out.println("$#845#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
		System.out.println("$#846#"); return te;
		
	} else if (ex instanceof AuthenticationException) { System.out.println("$#847#");
		LOGGER.error("Authentication error with stripe", ex.getMessage());
		AuthenticationException e = (AuthenticationException)ex;
		  // Authentication with Stripe's API failed
		  // (maybe you changed API keys recently)
		IntegrationException te = new IntegrationException(
				"Can't process Stripe, missing invalid payment parameters");
		System.out.println("$#848#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
		System.out.println("$#849#"); te.setMessageCode("message.payment.error");
		System.out.println("$#850#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
		System.out.println("$#851#"); return te;
		
	} /*else if (ex instanceof APIConnectionException) {
		LOGGER.error("API connection error with stripe", ex.getMessage());
		APIConnectionException e = (APIConnectionException)ex;
		  // Network communication with Stripe failed
		IntegrationException te = new IntegrationException(
				"Can't process Stripe, missing invalid payment parameters");
		te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
		te.setMessageCode("message.payment.error");
		te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
		return te;
	} */else if (ex instanceof StripeException) { System.out.println("$#852#");
		LOGGER.error("Error with stripe", ex.getMessage());
		StripeException e = (StripeException)ex;
		  // Display a very generic error to the user, and maybe send
		  // yourself an email
		IntegrationException te = new IntegrationException(
				"Can't process Stripe authorize, missing invalid payment parameters");
		System.out.println("$#853#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
		System.out.println("$#854#"); te.setMessageCode("message.payment.error");
		System.out.println("$#855#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
		System.out.println("$#856#"); return te;
		
		

	} else if (ex instanceof Exception) { System.out.println("$#857#");
		LOGGER.error("Stripe module error", ex.getMessage());
		System.out.println("$#858#"); if(ex instanceof IntegrationException) {
			System.out.println("$#859#"); return (IntegrationException)ex;
		} else {
			IntegrationException te = new IntegrationException(
					"Can't process Stripe authorize, exception", ex);
			System.out.println("$#860#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			System.out.println("$#861#"); te.setMessageCode("message.payment.error");
			System.out.println("$#862#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			System.out.println("$#863#"); return te;
		}


	} else {
		System.out.println("$#842#"); // manual correction for else-if mutation coverage
		System.out.println("$#847#"); // manual correction for else-if mutation coverage
		System.out.println("$#852#"); // manual correction for else-if mutation coverage
		System.out.println("$#857#"); // manual correction for else-if mutation coverage
		LOGGER.error("Stripe module error", ex.getMessage());
		IntegrationException te = new IntegrationException(
				"Can't process Stripe authorize, exception", ex);
		System.out.println("$#864#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
		System.out.println("$#865#"); te.setMessageCode("message.payment.error");
		System.out.println("$#866#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
		System.out.println("$#867#"); return te;
	}

	}
	
	



}
