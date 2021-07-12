package com.salesmanager.core.business.services.payments;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.order.OrderService;
import com.salesmanager.core.business.services.reference.loader.ConfigurationModulesLoader;
import com.salesmanager.core.business.services.system.MerchantConfigurationService;
import com.salesmanager.core.business.services.system.ModuleConfigurationService;
import com.salesmanager.core.business.utils.CoreConfiguration;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.order.OrderTotal;
import com.salesmanager.core.model.order.OrderTotalType;
import com.salesmanager.core.model.order.orderstatus.OrderStatus;
import com.salesmanager.core.model.order.orderstatus.OrderStatusHistory;
import com.salesmanager.core.model.payments.CreditCardPayment;
import com.salesmanager.core.model.payments.CreditCardType;
import com.salesmanager.core.model.payments.Payment;
import com.salesmanager.core.model.payments.PaymentMethod;
import com.salesmanager.core.model.payments.PaymentType;
import com.salesmanager.core.model.payments.Transaction;
import com.salesmanager.core.model.payments.TransactionType;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.core.model.system.IntegrationConfiguration;
import com.salesmanager.core.model.system.IntegrationModule;
import com.salesmanager.core.model.system.MerchantConfiguration;
import com.salesmanager.core.modules.integration.IntegrationException;
import com.salesmanager.core.modules.integration.payment.model.PaymentModule;
import com.salesmanager.core.modules.utils.Encryption;


@Service("paymentService")
public class PaymentServiceImpl implements PaymentService {
	

	@Inject
	private MerchantConfigurationService merchantConfigurationService;
	
	@Inject
	private ModuleConfigurationService moduleConfigurationService;
	
	@Inject
	private TransactionService transactionService;
	
	@Inject
	private OrderService orderService;
	
	@Inject
	private CoreConfiguration coreConfiguration;
	
	@Inject
	@Resource(name="paymentModules")
	private Map<String,PaymentModule> paymentModules;
	
	@Inject
	private Encryption encryption;
	
	@Override
	public List<IntegrationModule> getPaymentMethods(MerchantStore store) throws ServiceException {
		
		List<IntegrationModule> modules =  moduleConfigurationService.getIntegrationModules(Constants.PAYMENT_MODULES);
		List<IntegrationModule> returnModules = new ArrayList<IntegrationModule>();
		
		for(IntegrationModule module : modules) {
			System.out.println("$#2431#"); if(module.getRegionsSet().contains(store.getCountry().getIsoCode())
					|| module.getRegionsSet().contains("*")) {
				
				returnModules.add(module);
			}
		}
		
		System.out.println("$#2433#"); return returnModules;
	}
	
	@Override
	public List<PaymentMethod> getAcceptedPaymentMethods(MerchantStore store) throws ServiceException {
		
		Map<String,IntegrationConfiguration> modules =  this.getPaymentModulesConfigured(store);

		List<PaymentMethod> returnModules = new ArrayList<PaymentMethod>();
		
		for(String module : modules.keySet()) {
			IntegrationConfiguration config = modules.get(module);
			System.out.println("$#2434#"); if(config.isActive()) {
				
				IntegrationModule md = this.getPaymentMethodByCode(store, config.getModuleCode());
				System.out.println("$#2435#"); if(md==null) {
					continue;
				}
				PaymentMethod paymentMethod = new PaymentMethod();
				
				System.out.println("$#2436#"); paymentMethod.setDefaultSelected(config.isDefaultSelected());
				System.out.println("$#2437#"); paymentMethod.setPaymentMethodCode(config.getModuleCode());
				System.out.println("$#2438#"); paymentMethod.setModule(md);
				System.out.println("$#2439#"); paymentMethod.setInformations(config);

				PaymentType type = PaymentType.fromString(md.getType());

				System.out.println("$#2440#"); paymentMethod.setPaymentType(type);
				returnModules.add(paymentMethod);
			}
		}
		
		System.out.println("$#2441#"); return returnModules;
		
		
	}
	
	@Override
	public IntegrationModule getPaymentMethodByType(MerchantStore store, String type) throws ServiceException {
		List<IntegrationModule> modules =  getPaymentMethods(store);

		for(IntegrationModule module : modules) {
			System.out.println("$#2442#"); if(module.getModule().equals(type)) {
				
				System.out.println("$#2443#"); return module;
			}
		}
		
		return null;
	}
	
	@Override
	public IntegrationModule getPaymentMethodByCode(MerchantStore store,
			String code) throws ServiceException {
		List<IntegrationModule> modules =  getPaymentMethods(store);

		for(IntegrationModule module : modules) {
			System.out.println("$#2444#"); if(module.getCode().equals(code)) {
				
				System.out.println("$#2445#"); return module;
			}
		}
		
		return null;
	}
	
	@Override
	public IntegrationConfiguration getPaymentConfiguration(String moduleCode, MerchantStore store) throws ServiceException {

		Validate.notNull(moduleCode,"Module code must not be null");
		Validate.notNull(store,"Store must not be null");
		
		String mod = moduleCode.toLowerCase();
		
		Map<String,IntegrationConfiguration> configuredModules = getPaymentModulesConfigured(store);
		System.out.println("$#2446#"); if(configuredModules!=null) {
			for(String key : configuredModules.keySet()) {
				System.out.println("$#2447#"); if(key.equals(mod)) {
					System.out.println("$#2448#"); return configuredModules.get(key);
				}
			}
		}
		
		return null;
		
	}
	

	
	@Override
	public Map<String,IntegrationConfiguration> getPaymentModulesConfigured(MerchantStore store) throws ServiceException {
		
		try {
		
			Map<String,IntegrationConfiguration> modules = new HashMap<String,IntegrationConfiguration>();
			MerchantConfiguration merchantConfiguration = merchantConfigurationService.getMerchantConfiguration(Constants.PAYMENT_MODULES, store);
			System.out.println("$#2449#"); if(merchantConfiguration!=null) {
				
				System.out.println("$#2450#"); if(!StringUtils.isBlank(merchantConfiguration.getValue())) {
					
					String decrypted = encryption.decrypt(merchantConfiguration.getValue());
					modules = ConfigurationModulesLoader.loadIntegrationConfigurations(decrypted);
					
					
				}
			}
			System.out.println("$#2451#"); return modules;
		
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	@Override
	public void savePaymentModuleConfiguration(IntegrationConfiguration configuration, MerchantStore store) throws ServiceException {
		
		//validate entries
		try {
			
			String moduleCode = configuration.getModuleCode();
			PaymentModule module = (PaymentModule)paymentModules.get(moduleCode);
			System.out.println("$#2452#"); if(module==null) {
				throw new ServiceException("Payment module " + moduleCode + " does not exist");
			}
			System.out.println("$#2453#"); module.validateModuleConfiguration(configuration, store);
			
		} catch (IntegrationException ie) {
			throw ie;
		}
		
		try {
			Map<String,IntegrationConfiguration> modules = new HashMap<String,IntegrationConfiguration>();
			MerchantConfiguration merchantConfiguration = merchantConfigurationService.getMerchantConfiguration(Constants.PAYMENT_MODULES, store);
			System.out.println("$#2454#"); if(merchantConfiguration!=null) {
				System.out.println("$#2455#"); if(!StringUtils.isBlank(merchantConfiguration.getValue())) {
					
					String decrypted = encryption.decrypt(merchantConfiguration.getValue());
					
					modules = ConfigurationModulesLoader.loadIntegrationConfigurations(decrypted);
				}
			} else {
				merchantConfiguration = new MerchantConfiguration();
				System.out.println("$#2456#"); merchantConfiguration.setMerchantStore(store);
				System.out.println("$#2457#"); merchantConfiguration.setKey(Constants.PAYMENT_MODULES);
			}
			modules.put(configuration.getModuleCode(), configuration);
			
			String configs =  ConfigurationModulesLoader.toJSONString(modules);
			
			String encrypted = encryption.encrypt(configs);
			System.out.println("$#2458#"); merchantConfiguration.setValue(encrypted);
			
			System.out.println("$#2459#"); merchantConfigurationService.saveOrUpdate(merchantConfiguration);
			
		} catch (Exception e) {
			throw new ServiceException(e);
		}
   }
	
	@Override
	public void removePaymentModuleConfiguration(String moduleCode, MerchantStore store) throws ServiceException {
		
		

		try {
			Map<String,IntegrationConfiguration> modules = new HashMap<String,IntegrationConfiguration>();
			MerchantConfiguration merchantConfiguration = merchantConfigurationService.getMerchantConfiguration(Constants.PAYMENT_MODULES, store);
			System.out.println("$#2460#"); if(merchantConfiguration!=null) {

				System.out.println("$#2461#"); if(!StringUtils.isBlank(merchantConfiguration.getValue())) {
					
					String decrypted = encryption.decrypt(merchantConfiguration.getValue());
					modules = ConfigurationModulesLoader.loadIntegrationConfigurations(decrypted);
				}
				
				modules.remove(moduleCode);
				String configs =  ConfigurationModulesLoader.toJSONString(modules);
				
				String encrypted = encryption.encrypt(configs);
				System.out.println("$#2462#"); merchantConfiguration.setValue(encrypted);
				
				System.out.println("$#2463#"); merchantConfigurationService.saveOrUpdate(merchantConfiguration);
				
				
			} 
			
			MerchantConfiguration configuration = merchantConfigurationService.getMerchantConfiguration(moduleCode, store);
			
			System.out.println("$#2464#"); if(configuration!=null) {//custom module

				System.out.println("$#2465#"); merchantConfigurationService.delete(configuration);
			}

			
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	
	}
	

	


	@Override
	public Transaction processPayment(Customer customer,
			MerchantStore store, Payment payment, List<ShoppingCartItem> items, Order order)
			throws ServiceException {


		Validate.notNull(customer);
		Validate.notNull(store);
		Validate.notNull(payment);
		Validate.notNull(order);
		Validate.notNull(order.getTotal());
		
		System.out.println("$#2466#"); payment.setCurrency(store.getCurrency());
		
		BigDecimal amount = order.getTotal();

		//must have a shipping module configured
		Map<String, IntegrationConfiguration> modules = this.getPaymentModulesConfigured(store);
		System.out.println("$#2467#"); if(modules==null){
			throw new ServiceException("No payment module configured");
		}
		
		IntegrationConfiguration configuration = modules.get(payment.getModuleName());
		
		System.out.println("$#2468#"); if(configuration==null) {
			throw new ServiceException("Payment module " + payment.getModuleName() + " is not configured");
		}
		
		System.out.println("$#2469#"); if(!configuration.isActive()) {
			throw new ServiceException("Payment module " + payment.getModuleName() + " is not active");
		}
		
		String sTransactionType = configuration.getIntegrationKeys().get("transaction");
		System.out.println("$#2470#"); if(sTransactionType==null) {
			sTransactionType = TransactionType.AUTHORIZECAPTURE.name();
		}
		

		System.out.println("$#2471#"); if(sTransactionType.equals(TransactionType.AUTHORIZE.name())) {
			System.out.println("$#2472#"); payment.setTransactionType(TransactionType.AUTHORIZE);
		} else {
			System.out.println("$#2473#"); payment.setTransactionType(TransactionType.AUTHORIZECAPTURE);
		} 
		

		PaymentModule module = this.paymentModules.get(payment.getModuleName());
		
		System.out.println("$#2474#"); if(module==null) {
			throw new ServiceException("Payment module " + payment.getModuleName() + " does not exist");
		}
		
		System.out.println("$#2475#"); if(payment instanceof CreditCardPayment && "true".equals(coreConfiguration.getProperty("VALIDATE_CREDIT_CARD"))) {
			CreditCardPayment creditCardPayment = (CreditCardPayment)payment;
			System.out.println("$#2477#"); validateCreditCard(creditCardPayment.getCreditCardNumber(),creditCardPayment.getCreditCard(),creditCardPayment.getExpirationMonth(),creditCardPayment.getExpirationYear());
		}
		
		IntegrationModule integrationModule = getPaymentMethodByCode(store,payment.getModuleName());
		TransactionType transactionType = TransactionType.valueOf(sTransactionType);
		System.out.println("$#2478#"); if(transactionType==null) {
			transactionType = payment.getTransactionType();
			System.out.println("$#2479#"); if(transactionType.equals(TransactionType.CAPTURE.name())) {
				throw new ServiceException("This method does not allow to process capture transaction. Use processCapturePayment");
			}
		}
		
		Transaction transaction = null;
		System.out.println("$#2480#"); if(transactionType == TransactionType.AUTHORIZE)  {
			transaction = module.authorize(store, customer, items, amount, payment, configuration, integrationModule);
		} else if(transactionType == TransactionType.AUTHORIZECAPTURE)  { System.out.println("$#2481#");
			transaction = module.authorizeAndCapture(store, customer, items, amount, payment, configuration, integrationModule);
		} else if(transactionType == TransactionType.INIT)  { System.out.println("$#2482#");
			transaction = module.initTransaction(store, customer, amount, payment, configuration, integrationModule);
		} else {
			System.out.println("$#2481#"); // manual correction for else-if mutation coverage
			System.out.println("$#2482#"); // manual correction for else-if mutation coverage
		}


		System.out.println("$#2483#"); if(transactionType != TransactionType.INIT) {
			System.out.println("$#2484#"); transactionService.create(transaction);
		}
		
		System.out.println("$#2485#"); if(transactionType == TransactionType.AUTHORIZECAPTURE)  {
			System.out.println("$#2486#"); order.setStatus(OrderStatus.ORDERED);
			System.out.println("$#2487#"); if(payment.getPaymentType().name()!=PaymentType.MONEYORDER.name()) {
				System.out.println("$#2488#"); order.setStatus(OrderStatus.PROCESSED);
			}
		}

		System.out.println("$#2489#"); return transaction;

		

	}
	
	@Override
	public PaymentModule getPaymentModule(String paymentModuleCode) throws ServiceException {
		System.out.println("$#2490#"); return paymentModules.get(paymentModuleCode);
	}
	
	@Override
	public Transaction processCapturePayment(Order order, Customer customer,
			MerchantStore store)
			throws ServiceException {


		Validate.notNull(customer);
		Validate.notNull(store);
		Validate.notNull(order);

		

		//must have a shipping module configured
		Map<String, IntegrationConfiguration> modules = this.getPaymentModulesConfigured(store);
		System.out.println("$#2491#"); if(modules==null){
			throw new ServiceException("No payment module configured");
		}
		
		IntegrationConfiguration configuration = modules.get(order.getPaymentModuleCode());
		
		System.out.println("$#2492#"); if(configuration==null) {
			throw new ServiceException("Payment module " + order.getPaymentModuleCode() + " is not configured");
		}
		
		System.out.println("$#2493#"); if(!configuration.isActive()) {
			throw new ServiceException("Payment module " + order.getPaymentModuleCode() + " is not active");
		}
		
		
		PaymentModule module = this.paymentModules.get(order.getPaymentModuleCode());
		
		System.out.println("$#2494#"); if(module==null) {
			throw new ServiceException("Payment module " + order.getPaymentModuleCode() + " does not exist");
		}
		

		IntegrationModule integrationModule = getPaymentMethodByCode(store,order.getPaymentModuleCode());
		
		//TransactionType transactionType = payment.getTransactionType();

			//get the previous transaction
		Transaction trx = transactionService.getCapturableTransaction(order);
		System.out.println("$#2495#"); if(trx==null) {
			throw new ServiceException("No capturable transaction for order id " + order.getId());
		}
		Transaction transaction = module.capture(store, customer, order, trx, configuration, integrationModule);
		System.out.println("$#2496#"); transaction.setOrder(order);
		
		

		System.out.println("$#2497#"); transactionService.create(transaction);
		
		
		OrderStatusHistory orderHistory = new OrderStatusHistory();
		System.out.println("$#2498#"); orderHistory.setOrder(order);
		System.out.println("$#2499#"); orderHistory.setStatus(OrderStatus.PROCESSED);
		System.out.println("$#2500#"); orderHistory.setDateAdded(new Date());
		
		System.out.println("$#2501#"); orderService.addOrderStatusHistory(order, orderHistory);
		
		System.out.println("$#2502#"); order.setStatus(OrderStatus.PROCESSED);
		System.out.println("$#2503#"); orderService.saveOrUpdate(order);

		System.out.println("$#2504#"); return transaction;

		

	}

	@Override
	public Transaction processRefund(Order order, Customer customer,
			MerchantStore store, BigDecimal amount)
			throws ServiceException {
		
		
		Validate.notNull(customer);
		Validate.notNull(store);
		Validate.notNull(amount);
		Validate.notNull(order);
		Validate.notNull(order.getOrderTotal());
		
		
		BigDecimal orderTotal = order.getTotal();
		
		System.out.println("$#2506#"); System.out.println("$#2505#"); if(amount.doubleValue()>orderTotal.doubleValue()) {
			throw new ServiceException("Invalid amount, the refunded amount is greater than the total allowed");
		}

		
		String module = order.getPaymentModuleCode();
		Map<String, IntegrationConfiguration> modules = this.getPaymentModulesConfigured(store);
		System.out.println("$#2507#"); if(modules==null){
			throw new ServiceException("No payment module configured");
		}
		
		IntegrationConfiguration configuration = modules.get(module);
		
		System.out.println("$#2508#"); if(configuration==null) {
			throw new ServiceException("Payment module " + module + " is not configured");
		}
		
		PaymentModule paymentModule = this.paymentModules.get(module);
		
		System.out.println("$#2509#"); if(paymentModule==null) {
			throw new ServiceException("Payment module " + paymentModule + " does not exist");
		}
		
		boolean partial = false;
		System.out.println("$#2510#"); if(amount.doubleValue()!=order.getTotal().doubleValue()) {
			partial = true;
		}
		
		IntegrationModule integrationModule = getPaymentMethodByCode(store,module);
		
		//get the associated transaction
		Transaction refundable = transactionService.getRefundableTransaction(order);
		
		System.out.println("$#2511#"); if(refundable==null) {
			throw new ServiceException("No refundable transaction for this order");
		}
		
		Transaction transaction = paymentModule.refund(partial, store, refundable, order, amount, configuration, integrationModule);
		System.out.println("$#2512#"); transaction.setOrder(order);
		System.out.println("$#2513#"); transactionService.create(transaction);
		
        OrderTotal refund = new OrderTotal();
								System.out.println("$#2514#"); refund.setModule(Constants.OT_REFUND_MODULE_CODE);
								System.out.println("$#2515#"); refund.setText(Constants.OT_REFUND_MODULE_CODE);
								System.out.println("$#2516#"); refund.setTitle(Constants.OT_REFUND_MODULE_CODE);
								System.out.println("$#2517#"); refund.setOrderTotalCode(Constants.OT_REFUND_MODULE_CODE);
								System.out.println("$#2518#"); refund.setOrderTotalType(OrderTotalType.REFUND);
								System.out.println("$#2519#"); refund.setValue(amount);
								System.out.println("$#2520#"); refund.setSortOrder(100);
								System.out.println("$#2521#"); refund.setOrder(order);
        
        order.getOrderTotal().add(refund);
        
		//update order total
		orderTotal = orderTotal.subtract(amount);
        
        //update ordertotal refund
        Set<OrderTotal> totals = order.getOrderTotal();
        for(OrderTotal total : totals) {
									System.out.println("$#2522#"); if(total.getModule().equals(Constants.OT_TOTAL_MODULE_CODE)) {
										System.out.println("$#2523#"); total.setValue(orderTotal);
        	}
        }

		

		System.out.println("$#2524#"); order.setTotal(orderTotal);
		System.out.println("$#2525#"); order.setStatus(OrderStatus.REFUNDED);
		
		
		
		OrderStatusHistory orderHistory = new OrderStatusHistory();
		System.out.println("$#2526#"); orderHistory.setOrder(order);
		System.out.println("$#2527#"); orderHistory.setStatus(OrderStatus.REFUNDED);
		System.out.println("$#2528#"); orderHistory.setDateAdded(new Date());
        order.getOrderHistory().add(orderHistory);
        
								System.out.println("$#2529#"); orderService.saveOrUpdate(order);

		System.out.println("$#2530#"); return transaction;
	}
	
	@Override
	public void validateCreditCard(String number, CreditCardType creditCard, String month, String date)
	throws ServiceException {

		try {
			Integer.parseInt(month);
			Integer.parseInt(date);
		} catch (NumberFormatException nfe) {
			ServiceException ex = new ServiceException(ServiceException.EXCEPTION_VALIDATION,"Invalid date format","messages.error.creditcard.dateformat");
			throw ex;
		}
		
		System.out.println("$#2531#"); if (StringUtils.isBlank(number)) {
			ServiceException ex = new ServiceException(ServiceException.EXCEPTION_VALIDATION,"Invalid card number","messages.error.creditcard.number");
			throw ex;
		}
		
		Matcher m = Pattern.compile("[^\\d\\s.-]").matcher(number);
		
		System.out.println("$#2532#"); if (m.find()) {
			ServiceException ex = new ServiceException(ServiceException.EXCEPTION_VALIDATION,"Invalid card number","messages.error.creditcard.number");
			throw ex;
		}
		
		Matcher matcher = Pattern.compile("[\\s.-]").matcher(number);
		
		number = matcher.replaceAll("");
		System.out.println("$#2533#"); validateCreditCardDate(Integer.parseInt(month), Integer.parseInt(date));
		System.out.println("$#2534#"); validateCreditCardNumber(number, creditCard);
	}

	private void validateCreditCardDate(int m, int y) throws ServiceException {
		java.util.Calendar cal = new java.util.GregorianCalendar();
		System.out.println("$#2535#"); int monthNow = cal.get(java.util.Calendar.MONTH) + 1;
		int yearNow = cal.get(java.util.Calendar.YEAR);
		System.out.println("$#2537#"); System.out.println("$#2536#"); if (yearNow > y) {
			ServiceException ex = new ServiceException(ServiceException.EXCEPTION_VALIDATION,"Invalid date format","messages.error.creditcard.dateformat");
			throw ex;
		}
		// OK, change implementation
		System.out.println("$#2539#"); System.out.println("$#2538#"); if (yearNow == y && monthNow > m) {
			ServiceException ex = new ServiceException(ServiceException.EXCEPTION_VALIDATION,"Invalid date format","messages.error.creditcard.dateformat");
			throw ex;
		}
	
	}
	
	@Deprecated
	/**
	 * Use commons validator CreditCardValidator
	 * @param number
	 * @param creditCard
	 * @throws ServiceException
	 */
	private void validateCreditCardNumber(String number, CreditCardType creditCard)
	throws ServiceException {

		//TODO implement
		System.out.println("$#2541#"); if(CreditCardType.MASTERCARD.equals(creditCard.name())) {
			System.out.println("$#2542#");
			System.out.println("$#2543#");
			System.out.println("$#2545#");
			if (number.length() != 16
					|| Integer.parseInt(number.substring(0, 2)) < 51
					|| Integer.parseInt(number.substring(0, 2)) > 55) {
				ServiceException ex = new ServiceException(ServiceException.EXCEPTION_VALIDATION,"Invalid card number","messages.error.creditcard.number");
				throw ex;
			}
		}
		
		System.out.println("$#2547#"); if(CreditCardType.VISA.equals(creditCard.name())) {
			System.out.println("$#2548#"); if ((number.length() != 13 && number.length() != 16)
					|| Integer.parseInt(number.substring(0, 1)) != 4) {
				ServiceException ex = new ServiceException(ServiceException.EXCEPTION_VALIDATION,"Invalid card number","messages.error.creditcard.number");
				throw ex;
			}
		}
		
		System.out.println("$#2551#"); if(CreditCardType.AMEX.equals(creditCard.name())) {
			System.out.println("$#2552#"); if (number.length() != 15
					|| (Integer.parseInt(number.substring(0, 2)) != 34 && Integer
							.parseInt(number.substring(0, 2)) != 37)) {
				ServiceException ex = new ServiceException(ServiceException.EXCEPTION_VALIDATION,"Invalid card number","messages.error.creditcard.number");
				throw ex;
			}
		}
		
		System.out.println("$#2555#"); if(CreditCardType.DINERS.equals(creditCard.name())) {
			System.out.println("$#2556#");
			System.out.println("$#2559#");
			System.out.println("$#2561#");
			if (number.length() != 14
					|| ((Integer.parseInt(number.substring(0, 2)) != 36 && Integer
							.parseInt(number.substring(0, 2)) != 38)
							&& Integer.parseInt(number.substring(0, 3)) < 300 || Integer
							.parseInt(number.substring(0, 3)) > 305)) {
				ServiceException ex = new ServiceException(ServiceException.EXCEPTION_VALIDATION,"Invalid card number","messages.error.creditcard.number");
				throw ex;
			}
		}
		
		System.out.println("$#2563#"); if(CreditCardType.DISCOVERY.equals(creditCard.name())) {
			System.out.println("$#2564#"); if (number.length() != 16
					|| Integer.parseInt(number.substring(0, 5)) != 6011) {
				ServiceException ex = new ServiceException(ServiceException.EXCEPTION_VALIDATION,"Invalid card number","messages.error.creditcard.number");
				throw ex;
			}
		}

		System.out.println("$#2566#"); luhnValidate(number);
	}

	// The Luhn algorithm is basically a CRC type
	// system for checking the validity of an entry.
	// All major credit cards use numbers that will
	// pass the Luhn check. Also, all of them are based
	// on MOD 10.
	@Deprecated
	private void luhnValidate(String numberString)
			throws ServiceException {
		char[] charArray = numberString.toCharArray();
		int[] number = new int[charArray.length];
		int total = 0;
	
		System.out.println("$#2568#"); System.out.println("$#2567#"); for (int i = 0; i < charArray.length; i++) {
			number[i] = Character.getNumericValue(charArray[i]);
		}
	
		System.out.println("$#2572#"); System.out.println("$#2571#"); System.out.println("$#2570#"); System.out.println("$#2569#"); for (int i = number.length - 2; i > -1; i -= 2) {
			System.out.println("$#2573#"); number[i] *= 2;
	
			System.out.println("$#2575#"); System.out.println("$#2574#"); if (number[i] > 9) { // manual correction for 'if' without '{' problem
				System.out.println("$#2576#");
				number[i] -= 9;
			}
		}
	
		System.out.println("$#2579#"); System.out.println("$#2578#"); System.out.println("$#2577#"); for (int i = 0; i < number.length; i++) {
			System.out.println("$#2580#");
			total += number[i];
		}
	
		System.out.println("$#2582#"); System.out.println("$#2581#"); if (total % 10 != 0) {
			ServiceException ex = new ServiceException(ServiceException.EXCEPTION_VALIDATION,"Invalid card number","messages.error.creditcard.number");
			throw ex;
		}
	
	}

	@Override
	public Transaction initTransaction(Order order, Customer customer, Payment payment, MerchantStore store) throws ServiceException {
		
		Validate.notNull(store);
		Validate.notNull(payment);
		Validate.notNull(order);
		Validate.notNull(order.getTotal());
		
		System.out.println("$#2583#"); payment.setCurrency(store.getCurrency());
		
		BigDecimal amount = order.getTotal();

		//must have a shipping module configured
		Map<String, IntegrationConfiguration> modules = this.getPaymentModulesConfigured(store);
		System.out.println("$#2584#"); if(modules==null){
			throw new ServiceException("No payment module configured");
		}
		
		IntegrationConfiguration configuration = modules.get(payment.getModuleName());
		
		System.out.println("$#2585#"); if(configuration==null) {
			throw new ServiceException("Payment module " + payment.getModuleName() + " is not configured");
		}
		
		System.out.println("$#2586#"); if(!configuration.isActive()) {
			throw new ServiceException("Payment module " + payment.getModuleName() + " is not active");
		}
		
		PaymentModule module = this.paymentModules.get(order.getPaymentModuleCode());
		
		System.out.println("$#2587#"); if(module==null) {
			throw new ServiceException("Payment module " + order.getPaymentModuleCode() + " does not exist");
		}
		
		IntegrationModule integrationModule = getPaymentMethodByCode(store,payment.getModuleName());
		
		Transaction transaction = module.initTransaction(store, customer, amount, payment, configuration, integrationModule);

		System.out.println("$#2588#"); return transaction;
	}

	@Override
	public Transaction initTransaction(Customer customer, Payment payment, MerchantStore store) throws ServiceException {

		Validate.notNull(store);
		Validate.notNull(payment);
		Validate.notNull(payment.getAmount());
		
		System.out.println("$#2589#"); payment.setCurrency(store.getCurrency());
		
		BigDecimal amount = payment.getAmount();

		//must have a shipping module configured
		Map<String, IntegrationConfiguration> modules = this.getPaymentModulesConfigured(store);
		System.out.println("$#2590#"); if(modules==null){
			throw new ServiceException("No payment module configured");
		}
		
		IntegrationConfiguration configuration = modules.get(payment.getModuleName());
		
		System.out.println("$#2591#"); if(configuration==null) {
			throw new ServiceException("Payment module " + payment.getModuleName() + " is not configured");
		}
		
		System.out.println("$#2592#"); if(!configuration.isActive()) {
			throw new ServiceException("Payment module " + payment.getModuleName() + " is not active");
		}
		
		PaymentModule module = this.paymentModules.get(payment.getModuleName());
		
		System.out.println("$#2593#"); if(module==null) {
			throw new ServiceException("Payment module " + payment.getModuleName() + " does not exist");
		}
		
		IntegrationModule integrationModule = getPaymentMethodByCode(store,payment.getModuleName());
		
		Transaction transaction = module.initTransaction(store, customer, amount, payment, configuration, integrationModule);
		
		System.out.println("$#2594#"); transactionService.save(transaction);

		System.out.println("$#2595#"); return transaction;
	}


	


}
