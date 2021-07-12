package com.salesmanager.shop.store.controller.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.salesmanager.core.business.modules.integration.payment.impl.PayPalExpressCheckoutPayment;
import com.salesmanager.core.business.modules.integration.payment.impl.Stripe3Payment;
import com.salesmanager.core.business.services.payments.PaymentService;
import com.salesmanager.core.business.services.payments.TransactionService;
import com.salesmanager.core.business.utils.CoreConfiguration;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.OrderTotalSummary;
import com.salesmanager.core.model.payments.PaypalPayment;
import com.salesmanager.core.model.payments.Transaction;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shipping.ShippingSummary;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.core.model.system.IntegrationConfiguration;
import com.salesmanager.core.model.system.IntegrationModule;
import com.salesmanager.core.modules.integration.payment.model.PaymentModule;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.order.ShopOrder;
import com.salesmanager.shop.store.controller.AbstractController;
import com.salesmanager.shop.store.controller.order.facade.OrderFacade;
import com.salesmanager.shop.store.controller.shoppingCart.facade.ShoppingCartFacade;

/**
 * Initialization of different payment services
 * @author carlsamson
 *
 */
@Controller
@RequestMapping(Constants.SHOP_URI)
public class ShoppingOrderPaymentController extends AbstractController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingOrderPaymentController.class);

	private final static String INIT_ACTION = "init";

	@Inject
	private ShoppingCartFacade shoppingCartFacade;

	@Inject
	private PaymentService paymentService;

	@Inject
	private OrderFacade orderFacade;

	@Inject
	private TransactionService transactionService;

	@Inject
	private CoreConfiguration coreConfiguration;

	/**
	 * Recalculates shipping and tax following a change in country or province
	 * 
	 * @param order
	 * @param request
	 * @param response
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = { "/order/payment/{action}/{paymentmethod}.html" }, method = RequestMethod.POST)
	public @ResponseBody String paymentAction(@Valid @ModelAttribute(value = "order") ShopOrder order,
			@PathVariable String action, @PathVariable String paymentmethod, HttpServletRequest request,
			HttpServletResponse response, Locale locale) throws Exception {

		Language language = (Language) request.getAttribute("LANGUAGE");
		MerchantStore store = (MerchantStore) request.getAttribute(Constants.MERCHANT_STORE);
		String shoppingCartCode = getSessionAttribute(Constants.SHOPPING_CART, request);

		Validate.notNull(shoppingCartCode, "shoppingCartCode does not exist in the session");
		AjaxResponse ajaxResponse = new AjaxResponse();

		try {

			com.salesmanager.core.model.shoppingcart.ShoppingCart cart = shoppingCartFacade
					.getShoppingCartModel(shoppingCartCode, store);

			Set<ShoppingCartItem> items = cart.getLineItems();
			List<ShoppingCartItem> cartItems = new ArrayList<ShoppingCartItem>(items);
			System.out.println("$#13473#"); order.setShoppingCartItems(cartItems);

			// validate order first
			Map<String, String> messages = new TreeMap<String, String>();
			System.out.println("$#13474#"); orderFacade.validateOrder(order, new BeanPropertyBindingResult(order, "order"), messages, store, locale);

			System.out.println("$#13475#"); if (CollectionUtils.isNotEmpty(messages.values())) {
				for (String key : messages.keySet()) {
					String value = messages.get(key);
					System.out.println("$#13476#"); ajaxResponse.addValidationMessage(key, value);
				}
				System.out.println("$#13477#"); ajaxResponse.setStatus(AjaxResponse.RESPONSE_STATUS_VALIDATION_FAILED);
				System.out.println("$#13478#"); return ajaxResponse.toJSONString();
			}

			IntegrationConfiguration config = paymentService.getPaymentConfiguration(order.getPaymentModule(), store);
			IntegrationModule integrationModule = paymentService.getPaymentMethodByCode(store,
					order.getPaymentModule());

			// OrderTotalSummary orderTotalSummary =
			// orderFacade.calculateOrderTotal(store, order, language);
			OrderTotalSummary orderTotalSummary = super.getSessionAttribute(Constants.ORDER_SUMMARY, request);
			System.out.println("$#13479#"); if (orderTotalSummary == null) {
				orderTotalSummary = orderFacade.calculateOrderTotal(store, order, language);
				System.out.println("$#13480#"); super.setSessionAttribute(Constants.ORDER_SUMMARY, orderTotalSummary, request);
			}

			ShippingSummary summary = (ShippingSummary) request.getSession().getAttribute("SHIPPING_SUMMARY");

			System.out.println("$#13481#"); if (summary != null) {
				System.out.println("$#13482#"); order.setShippingSummary(summary);
			}

			System.out.println("$#13483#"); if (action.equals(INIT_ACTION)) {
				System.out.println("$#13484#"); if (paymentmethod.equals("PAYPAL")) {
					try {
						PaymentModule module = paymentService.getPaymentModule("paypal-express-checkout");
						PayPalExpressCheckoutPayment p = (PayPalExpressCheckoutPayment) module;
						PaypalPayment payment = new PaypalPayment();
						System.out.println("$#13485#"); payment.setCurrency(store.getCurrency());
						Transaction transaction = p.initPaypalTransaction(store, cartItems, orderTotalSummary, payment,
								config, integrationModule);
						System.out.println("$#13486#"); transactionService.create(transaction);

						System.out.println("$#13487#"); super.setSessionAttribute(Constants.INIT_TRANSACTION_KEY, transaction, request);

						StringBuilder urlAppender = new StringBuilder();

						urlAppender.append(coreConfiguration.getProperty("PAYPAL_EXPRESSCHECKOUT_REGULAR"));

						urlAppender.append(transaction.getTransactionDetails().get("TOKEN"));

						if (config.getEnvironment()
								.equals(com.salesmanager.core.business.constants.Constants.PRODUCTION_ENVIRONMENT)) {
							StringBuilder url = new StringBuilder()
									.append(coreConfiguration.getProperty("PAYPAL_EXPRESSCHECKOUT_PRODUCTION"))
									.append(urlAppender.toString());
							System.out.println("$#13489#"); ajaxResponse.addEntry("url", url.toString());
						} else {
							StringBuilder url = new StringBuilder()
									.append(coreConfiguration.getProperty("PAYPAL_EXPRESSCHECKOUT_SANDBOX"))
									.append(urlAppender.toString());
							System.out.println("$#13490#"); ajaxResponse.addEntry("url", url.toString());
						}

						// keep order in session when user comes back from pp
						System.out.println("$#13491#"); super.setSessionAttribute(Constants.ORDER, order, request);
						System.out.println("$#13492#"); ajaxResponse.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);

					} catch (Exception e) {
						System.out.println("$#13493#"); ajaxResponse.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
					}
				} else if (paymentmethod.equals("stripe3")) { System.out.println("$#13494#");

					try {

						PaymentModule module = paymentService.getPaymentModule(paymentmethod);
						Stripe3Payment p = (Stripe3Payment) module;

						PaypalPayment payment = new PaypalPayment();
						System.out.println("$#13495#"); payment.setCurrency(store.getCurrency());
						Transaction transaction = p.initTransaction(store, null, orderTotalSummary.getTotal(), null,
								config, integrationModule);

						System.out.println("$#13496#"); transactionService.create(transaction);

						System.out.println("$#13497#"); super.setSessionAttribute(Constants.INIT_TRANSACTION_KEY, transaction, request);
						// keep order in session when user comes back from pp
						System.out.println("$#13498#"); super.setSessionAttribute(Constants.ORDER, order, request);

						System.out.println("$#13499#"); ajaxResponse.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);
						System.out.println("$#13500#"); ajaxResponse.setDataMap(transaction.getTransactionDetails());

					} catch (Exception e) {
						System.out.println("$#13501#"); ajaxResponse.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
					}
				} else {
					System.out.println("$#13494#"); // manual correction for else-if mutation coverage
				}
			}

		} catch (Exception e) {
			LOGGER.error("Error while performing payment action " + action + " for payment method " + paymentmethod, e);
			System.out.println("$#13502#"); ajaxResponse.setErrorMessage(e);
			System.out.println("$#13503#"); ajaxResponse.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);

		}

		System.out.println("$#13504#"); return ajaxResponse.toJSONString();
	}

	// cancel - success paypal order
	@RequestMapping(value = { "/paypal/checkout.html/{code}" }, method = RequestMethod.GET)
	public String returnPayPalPayment(@PathVariable String code, HttpServletRequest request,
			HttpServletResponse response, Locale locale) throws Exception {
		System.out.println("$#13505#"); if (Constants.SUCCESS.equals(code)) {
			System.out.println("$#13506#"); return "redirect:" + Constants.SHOP_URI + "/order/commitPreAuthorized.html";
		} else {// process as cancel
			System.out.println("$#13507#"); return "redirect:" + Constants.SHOP_URI + "/order/checkout.html";
		}
	}

}
