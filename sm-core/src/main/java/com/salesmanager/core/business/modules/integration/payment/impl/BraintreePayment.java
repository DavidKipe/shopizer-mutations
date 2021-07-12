package com.salesmanager.core.business.modules.integration.payment.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.ValidationError;
import com.braintreegateway.Result;
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

public class BraintreePayment implements PaymentModule {

	@Override
	public void validateModuleConfiguration(IntegrationConfiguration integrationConfiguration, MerchantStore store)
			throws IntegrationException {
		List<String> errorFields = null;
		
		
		Map<String,String> keys = integrationConfiguration.getIntegrationKeys();
		
		//validate integrationKeys['merchant_id']
		System.out.println("$#497#"); if(keys==null || StringUtils.isBlank(keys.get("merchant_id"))) {
			errorFields = new ArrayList<String>();
			errorFields.add("merchant_id");
		}
		
		//validate integrationKeys['public_key']
		System.out.println("$#499#"); if(keys==null || StringUtils.isBlank(keys.get("public_key"))) {
			System.out.println("$#501#"); if(errorFields==null) {
				errorFields = new ArrayList<String>();
			}
			errorFields.add("public_key");
		}
		
		//validate integrationKeys['private_key']
		System.out.println("$#502#"); if(keys==null || StringUtils.isBlank(keys.get("private_key"))) {
			System.out.println("$#504#"); if(errorFields==null) {
				errorFields = new ArrayList<String>();
			}
			errorFields.add("private_key");
		}
		
		//validate integrationKeys['tokenization_key']
		System.out.println("$#505#"); if(keys==null || StringUtils.isBlank(keys.get("tokenization_key"))) {
			System.out.println("$#507#"); if(errorFields==null) {
				errorFields = new ArrayList<String>();
			}
			errorFields.add("tokenization_key");
		}
		
		
		System.out.println("$#508#"); if(errorFields!=null) {
			IntegrationException ex = new IntegrationException(IntegrationException.ERROR_VALIDATION_SAVE);
			System.out.println("$#509#"); ex.setErrorFields(errorFields);
			throw ex;
			
		}

	}

	@Override
	public Transaction initTransaction(MerchantStore store, Customer customer, BigDecimal amount, Payment payment,
			IntegrationConfiguration configuration, IntegrationModule module) throws IntegrationException {

		System.out.println("$#510#"); Validate.notNull(configuration,"Configuration cannot be null");
		
		String merchantId = configuration.getIntegrationKeys().get("merchant_id");
		String publicKey = configuration.getIntegrationKeys().get("public_key");
		String privateKey = configuration.getIntegrationKeys().get("private_key");
		
		System.out.println("$#511#"); Validate.notNull(merchantId,"merchant_id cannot be null");
		System.out.println("$#512#"); Validate.notNull(publicKey,"public_key cannot be null");
		System.out.println("$#513#"); Validate.notNull(privateKey,"private_key cannot be null");
		
		Environment environment= Environment.PRODUCTION;
		System.out.println("$#514#"); if (configuration.getEnvironment().equals("TEST")) {// sandbox
			environment= Environment.SANDBOX;
		}
		
	    BraintreeGateway gateway = new BraintreeGateway(
	    		   environment,
	    		   merchantId,
	    		   publicKey,
	    		   privateKey
				);
		
		String clientToken = gateway.clientToken().generate();

		Transaction transaction = new Transaction();
		System.out.println("$#515#"); transaction.setAmount(amount);
		System.out.println("$#516#"); transaction.setDetails(clientToken);
		System.out.println("$#517#"); transaction.setPaymentType(payment.getPaymentType());
		System.out.println("$#518#"); transaction.setTransactionDate(new Date());
		System.out.println("$#519#"); transaction.setTransactionType(payment.getTransactionType());
		
		System.out.println("$#520#"); return transaction;
	}

	@Override
	public Transaction authorize(MerchantStore store, Customer customer, List<ShoppingCartItem> items,
			BigDecimal amount, Payment payment, IntegrationConfiguration configuration, IntegrationModule module)
			throws IntegrationException {


		System.out.println("$#521#"); Validate.notNull(configuration,"Configuration cannot be null");
		
		String merchantId = configuration.getIntegrationKeys().get("merchant_id");
		String publicKey = configuration.getIntegrationKeys().get("public_key");
		String privateKey = configuration.getIntegrationKeys().get("private_key");
		
		System.out.println("$#522#"); Validate.notNull(merchantId,"merchant_id cannot be null");
		System.out.println("$#523#"); Validate.notNull(publicKey,"public_key cannot be null");
		System.out.println("$#524#"); Validate.notNull(privateKey,"private_key cannot be null");
		
		String nonce = payment.getPaymentMetaData().get("paymentToken");
		
					System.out.println("$#525#"); if(StringUtils.isBlank(nonce)) {
			IntegrationException te = new IntegrationException(
						"Can't process Braintree, missing authorization nounce");
			System.out.println("$#526#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			System.out.println("$#527#"); te.setMessageCode("message.payment.error");
			System.out.println("$#528#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			throw te;
	    }
		
		Environment environment= Environment.PRODUCTION;
		System.out.println("$#529#"); if (configuration.getEnvironment().equals("TEST")) {// sandbox
			environment= Environment.SANDBOX;
		}
		
	    BraintreeGateway gateway = new BraintreeGateway(
	    		   environment,
	    		   merchantId,
	    		   publicKey,
	    		   privateKey
				);
	    
	   

        TransactionRequest request = new TransactionRequest()
            .amount(amount)
            .paymentMethodNonce(nonce);

        Result<com.braintreegateway.Transaction> result = gateway.transaction().sale(request);

        String authorizationId = null;
        
								System.out.println("$#530#"); if (result.isSuccess()) {
        	com.braintreegateway.Transaction transaction = result.getTarget();
        	authorizationId  = transaction.getId();
								} else if (result.getTransaction() != null) { System.out.println("$#531#");
        	com.braintreegateway.Transaction transaction = result.getTransaction();
        	authorizationId = transaction.getAuthorizedTransactionId();
        } else {
									System.out.println("$#531#"); // manual correction for else-if mutation coverage
            String errorString = "";
            for (ValidationError error : result.getErrors().getAllDeepValidationErrors()) {
               errorString += "Error: " + error.getCode() + ": " + error.getMessage() + "\n";
            }
            
			IntegrationException te = new IntegrationException(
					"Can't process Braintree authorization " + errorString);
			System.out.println("$#532#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			System.out.println("$#533#"); te.setMessageCode("message.payment.error");
			System.out.println("$#534#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			throw te;

        }
        
								System.out.println("$#535#"); if(StringUtils.isBlank(authorizationId)) {
			IntegrationException te = new IntegrationException(
					"Can't process Braintree, missing authorizationId");
			System.out.println("$#536#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			System.out.println("$#537#"); te.setMessageCode("message.payment.error");
			System.out.println("$#538#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			throw te;
        }
        
        Transaction trx = new Transaction();
								System.out.println("$#539#"); trx.setAmount(amount);
								System.out.println("$#540#"); trx.setTransactionDate(new Date());
								System.out.println("$#541#"); trx.setTransactionType(TransactionType.AUTHORIZE);
								System.out.println("$#542#"); trx.setPaymentType(PaymentType.CREDITCARD);
        trx.getTransactionDetails().put("TRANSACTIONID", authorizationId);
        trx.getTransactionDetails().put("TRNAPPROVED", null);
        trx.getTransactionDetails().put("TRNORDERNUMBER", authorizationId);
        trx.getTransactionDetails().put("MESSAGETEXT", null);
        
								System.out.println("$#543#"); return trx;
		
	}

	@Override
	public Transaction capture(MerchantStore store, Customer customer, Order order, Transaction capturableTransaction,
			IntegrationConfiguration configuration, IntegrationModule module) throws IntegrationException {
		System.out.println("$#544#"); Validate.notNull(configuration,"Configuration cannot be null");
		
		String merchantId = configuration.getIntegrationKeys().get("merchant_id");
		String publicKey = configuration.getIntegrationKeys().get("public_key");
		String privateKey = configuration.getIntegrationKeys().get("private_key");
		
		System.out.println("$#545#"); Validate.notNull(merchantId,"merchant_id cannot be null");
		System.out.println("$#546#"); Validate.notNull(publicKey,"public_key cannot be null");
		System.out.println("$#547#"); Validate.notNull(privateKey,"private_key cannot be null");
		
		String auth = capturableTransaction.getTransactionDetails().get("TRANSACTIONID");
		
					System.out.println("$#548#"); if(StringUtils.isBlank(auth)) {
			IntegrationException te = new IntegrationException(
						"Can't process Braintree, missing authorization id");
			System.out.println("$#549#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			System.out.println("$#550#"); te.setMessageCode("message.payment.error");
			System.out.println("$#551#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			throw te;
	    }
		
		Environment environment= Environment.PRODUCTION;
		System.out.println("$#552#"); if (configuration.getEnvironment().equals("TEST")) {// sandbox
			environment= Environment.SANDBOX;
		}
		
	    BraintreeGateway gateway = new BraintreeGateway(
	    		   environment,
	    		   merchantId,
	    		   publicKey,
	    		   privateKey
				);
	    
	   
	    BigDecimal amount = order.getTotal();

        Result<com.braintreegateway.Transaction> result = gateway.transaction().submitForSettlement(auth, amount);

        String trxId = null;
        
								System.out.println("$#553#"); if (result.isSuccess()) {
        	com.braintreegateway.Transaction settledTransaction = result.getTarget();
        	trxId = settledTransaction.getId();
        } else {
            String errorString = "";
            for (ValidationError error : result.getErrors().getAllDeepValidationErrors()) {
               errorString += "Error: " + error.getCode() + ": " + error.getMessage() + "\n";
            }
            
			IntegrationException te = new IntegrationException(
					"Can't process Braintree refund " + errorString);
			System.out.println("$#554#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			System.out.println("$#555#"); te.setMessageCode("message.payment.error");
			System.out.println("$#556#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			throw te;

        }
        
								System.out.println("$#557#"); if(StringUtils.isBlank(trxId)) {
			IntegrationException te = new IntegrationException(
					"Can't process Braintree, missing original transaction");
			System.out.println("$#558#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			System.out.println("$#559#"); te.setMessageCode("message.payment.error");
			System.out.println("$#560#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			throw te;
        }
        
        Transaction trx = new Transaction();
								System.out.println("$#561#"); trx.setAmount(amount);
								System.out.println("$#562#"); trx.setTransactionDate(new Date());
								System.out.println("$#563#"); trx.setTransactionType(TransactionType.CAPTURE);
								System.out.println("$#564#"); trx.setPaymentType(PaymentType.CREDITCARD);
        trx.getTransactionDetails().put("TRANSACTIONID", trxId);
        trx.getTransactionDetails().put("TRNAPPROVED", null);
        trx.getTransactionDetails().put("TRNORDERNUMBER", trxId);
        trx.getTransactionDetails().put("MESSAGETEXT", null);
        
								System.out.println("$#565#"); return trx;
		
		
	}

	@Override
	public Transaction authorizeAndCapture(MerchantStore store, Customer customer, List<ShoppingCartItem> items,
			BigDecimal amount, Payment payment, IntegrationConfiguration configuration, IntegrationModule module)
			throws IntegrationException {

		System.out.println("$#566#"); Validate.notNull(configuration,"Configuration cannot be null");
		
		String merchantId = configuration.getIntegrationKeys().get("merchant_id");
		String publicKey = configuration.getIntegrationKeys().get("public_key");
		String privateKey = configuration.getIntegrationKeys().get("private_key");
		
		System.out.println("$#567#"); Validate.notNull(merchantId,"merchant_id cannot be null");
		System.out.println("$#568#"); Validate.notNull(publicKey,"public_key cannot be null");
		System.out.println("$#569#"); Validate.notNull(privateKey,"private_key cannot be null");
		
		String nonce = payment.getPaymentMetaData().get("paymentToken");//paymentToken
		
					System.out.println("$#570#"); if(StringUtils.isBlank(nonce)) {
			IntegrationException te = new IntegrationException(
						"Can't process Braintree, missing authorization nounce");
			System.out.println("$#571#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			System.out.println("$#572#"); te.setMessageCode("message.payment.error");
			System.out.println("$#573#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			throw te;
	    }
		
		Environment environment= Environment.PRODUCTION;
		System.out.println("$#574#"); if (configuration.getEnvironment().equals("TEST")) {// sandbox
			environment= Environment.SANDBOX;
		}
		
	    BraintreeGateway gateway = new BraintreeGateway(
	    		   environment,
	    		   merchantId,
	    		   publicKey,
	    		   privateKey
				);
	    
	   

        TransactionRequest request = new TransactionRequest()
            .amount(amount)
            .paymentMethodNonce(nonce);

        Result<com.braintreegateway.Transaction> result = gateway.transaction().sale(request);

        String trxId = null;
        
								System.out.println("$#575#"); if (result.isSuccess()) {
        	com.braintreegateway.Transaction transaction = result.getTarget();
        	trxId  = transaction.getId();
        } else {
            String errorString = "";
            for (ValidationError error : result.getErrors().getAllDeepValidationErrors()) {
               errorString += "Error: " + error.getCode() + ": " + error.getMessage() + "\n";
            }
            
			IntegrationException te = new IntegrationException(
					"Can't process Braintree auth + capture " + errorString);
			System.out.println("$#576#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			System.out.println("$#577#"); te.setMessageCode("message.payment.error");
			System.out.println("$#578#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			throw te;

        }
        
								System.out.println("$#579#"); if(StringUtils.isBlank(trxId)) {
			IntegrationException te = new IntegrationException(
					"Can't process Braintree, missing trxId");
			System.out.println("$#580#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			System.out.println("$#581#"); te.setMessageCode("message.payment.error");
			System.out.println("$#582#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			throw te;
        }
        
        Transaction trx = new Transaction();
								System.out.println("$#583#"); trx.setAmount(amount);
								System.out.println("$#584#"); trx.setTransactionDate(new Date());
								System.out.println("$#585#"); trx.setTransactionType(TransactionType.AUTHORIZECAPTURE);
								System.out.println("$#586#"); trx.setPaymentType(PaymentType.CREDITCARD);
        trx.getTransactionDetails().put("TRANSACTIONID", trxId);
        trx.getTransactionDetails().put("TRNAPPROVED", null);
        trx.getTransactionDetails().put("TRNORDERNUMBER", trxId);
        trx.getTransactionDetails().put("MESSAGETEXT", null);
        
								System.out.println("$#587#"); return trx;
		
		
	}

	@Override
	public Transaction refund(boolean partial, MerchantStore store, Transaction transaction, Order order,
			BigDecimal amount, IntegrationConfiguration configuration, IntegrationModule module)
			throws IntegrationException {

		
		String merchantId = configuration.getIntegrationKeys().get("merchant_id");
		String publicKey = configuration.getIntegrationKeys().get("public_key");
		String privateKey = configuration.getIntegrationKeys().get("private_key");
		
		System.out.println("$#588#"); Validate.notNull(merchantId,"merchant_id cannot be null");
		System.out.println("$#589#"); Validate.notNull(publicKey,"public_key cannot be null");
		System.out.println("$#590#"); Validate.notNull(privateKey,"private_key cannot be null");
		
		String auth = transaction.getTransactionDetails().get("TRANSACTIONID");
		
					System.out.println("$#591#"); if(StringUtils.isBlank(auth)) {
			IntegrationException te = new IntegrationException(
						"Can't process Braintree refund, missing transaction id");
			System.out.println("$#592#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			System.out.println("$#593#"); te.setMessageCode("message.payment.error");
			System.out.println("$#594#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			throw te;
	    }
		
		Environment environment= Environment.PRODUCTION;
		System.out.println("$#595#"); if (configuration.getEnvironment().equals("TEST")) {// sandbox
			environment= Environment.SANDBOX;
		}
		
	    BraintreeGateway gateway = new BraintreeGateway(
	    		   environment,
	    		   merchantId,
	    		   publicKey,
	    		   privateKey
				);
	    

        Result<com.braintreegateway.Transaction> result = gateway.transaction().refund(auth, amount);

        String trxId = null;
        
								System.out.println("$#596#"); if (result.isSuccess()) {
        	com.braintreegateway.Transaction settledTransaction = result.getTarget();
        	trxId = settledTransaction.getId();
        } else {
            String errorString = "";
            for (ValidationError error : result.getErrors().getAllDeepValidationErrors()) {
               errorString += "Error: " + error.getCode() + ": " + error.getMessage() + "\n";
            }
            
			IntegrationException te = new IntegrationException(
					"Can't process Braintree refund " + errorString);
			System.out.println("$#597#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			System.out.println("$#598#"); te.setMessageCode("message.payment.error");
			System.out.println("$#599#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			throw te;

        }
        
								System.out.println("$#600#"); if(StringUtils.isBlank(trxId)) {
			IntegrationException te = new IntegrationException(
					"Can't process Braintree refund, missing original transaction");
			System.out.println("$#601#"); te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			System.out.println("$#602#"); te.setMessageCode("message.payment.error");
			System.out.println("$#603#"); te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			throw te;
        }
        
        Transaction trx = new Transaction();
								System.out.println("$#604#"); trx.setAmount(amount);
								System.out.println("$#605#"); trx.setTransactionDate(new Date());
								System.out.println("$#606#"); trx.setTransactionType(TransactionType.REFUND);
								System.out.println("$#607#"); trx.setPaymentType(PaymentType.CREDITCARD);
        trx.getTransactionDetails().put("TRANSACTIONID", trxId);
        trx.getTransactionDetails().put("TRNAPPROVED", null);
        trx.getTransactionDetails().put("TRNORDERNUMBER", trxId);
        trx.getTransactionDetails().put("MESSAGETEXT", null);
        
								System.out.println("$#608#"); return trx;
		
	}

}
