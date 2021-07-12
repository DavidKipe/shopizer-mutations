package com.salesmanager.shop.store.controller.order.facade;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductAttributeService;
import com.salesmanager.core.business.services.catalog.product.file.DigitalProductService;
import com.salesmanager.core.business.services.order.OrderService;
import com.salesmanager.core.business.services.payments.PaymentService;
import com.salesmanager.core.business.services.payments.TransactionService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.services.shipping.ShippingQuoteService;
import com.salesmanager.core.business.services.shipping.ShippingService;
import com.salesmanager.core.business.services.shoppingcart.ShoppingCartService;
import com.salesmanager.core.business.utils.CoreConfiguration;
import com.salesmanager.core.business.utils.CreditCardUtils;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.availability.ProductAvailability;
import com.salesmanager.core.model.common.Billing;
import com.salesmanager.core.model.common.Delivery;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.order.OrderCriteria;
import com.salesmanager.core.model.order.OrderList;
import com.salesmanager.core.model.order.OrderSummary;
import com.salesmanager.core.model.order.OrderTotalSummary;
import com.salesmanager.core.model.order.attributes.OrderAttribute;
import com.salesmanager.core.model.order.orderproduct.OrderProduct;
import com.salesmanager.core.model.order.orderstatus.OrderStatus;
import com.salesmanager.core.model.order.orderstatus.OrderStatusHistory;
import com.salesmanager.core.model.order.payment.CreditCard;
import com.salesmanager.core.model.payments.CreditCardPayment;
import com.salesmanager.core.model.payments.CreditCardType;
import com.salesmanager.core.model.payments.Payment;
import com.salesmanager.core.model.payments.PaymentType;
import com.salesmanager.core.model.payments.Transaction;
import com.salesmanager.core.model.payments.TransactionType;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shipping.ShippingProduct;
import com.salesmanager.core.model.shipping.ShippingQuote;
import com.salesmanager.core.model.shipping.ShippingSummary;
import com.salesmanager.core.model.shoppingcart.ShoppingCart;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.shop.model.customer.PersistableCustomer;
import com.salesmanager.shop.model.customer.ReadableCustomer;
import com.salesmanager.shop.model.customer.address.Address;
import com.salesmanager.shop.model.order.OrderEntity;
import com.salesmanager.shop.model.order.PersistableOrderProduct;
import com.salesmanager.shop.model.order.ReadableOrderProduct;
import com.salesmanager.shop.model.order.ShopOrder;
import com.salesmanager.shop.model.order.history.PersistableOrderStatusHistory;
import com.salesmanager.shop.model.order.history.ReadableOrderStatusHistory;
import com.salesmanager.shop.model.order.total.OrderTotal;
import com.salesmanager.shop.model.order.transaction.ReadableTransaction;
import com.salesmanager.shop.populator.customer.CustomerPopulator;
import com.salesmanager.shop.populator.customer.PersistableCustomerPopulator;
import com.salesmanager.shop.populator.order.OrderProductPopulator;
import com.salesmanager.shop.populator.order.PersistableOrderApiPopulator;
import com.salesmanager.shop.populator.order.ReadableOrderPopulator;
import com.salesmanager.shop.populator.order.ReadableOrderProductPopulator;
import com.salesmanager.shop.populator.order.ShoppingCartItemPopulator;
import com.salesmanager.shop.populator.order.transaction.PersistablePaymentPopulator;
import com.salesmanager.shop.populator.order.transaction.ReadableTransactionPopulator;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.controller.customer.facade.CustomerFacade;
import com.salesmanager.shop.store.controller.shoppingCart.facade.ShoppingCartFacade;
import com.salesmanager.shop.utils.DateUtil;
import com.salesmanager.shop.utils.EmailTemplatesUtils;
import com.salesmanager.shop.utils.ImageFilePath;
import com.salesmanager.shop.utils.LabelUtils;
import com.salesmanager.shop.utils.LocaleUtils;

@Service("orderFacade")
public class OrderFacadeImpl implements OrderFacade {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderFacadeImpl.class);

	@Inject
	private OrderService orderService;
	@Inject
	private ProductService productService;
	@Inject
	private ProductAttributeService productAttributeService;
	@Inject
	private ShoppingCartService shoppingCartService;
	@Inject
	private DigitalProductService digitalProductService;
	@Inject
	private ShippingService shippingService;
	@Inject
	private CustomerFacade customerFacade;
	@Inject
	private PricingService pricingService;
	@Inject
	private ShoppingCartFacade shoppingCartFacade;
	@Inject
	private ShippingQuoteService shippingQuoteService;
	@Inject
	private CoreConfiguration coreConfiguration;
	@Inject
	private PaymentService paymentService;
	@Inject
	private CountryService countryService;
	@Inject
	private ZoneService zoneService;

	@Autowired
	private PersistableOrderApiPopulator persistableOrderApiPopulator;

	@Autowired
	private ReadableOrderPopulator readableOrderPopulator;

	@Autowired
	private CustomerPopulator customerPopulator;
	
	@Autowired
	private TransactionService transactionService;

	@Inject
	private EmailTemplatesUtils emailTemplatesUtils;

	@Inject
	private LabelUtils messages;

	@Inject
	@Qualifier("img")
	private ImageFilePath imageUtils;

	@Override
	public ShopOrder initializeOrder(MerchantStore store, Customer customer, ShoppingCart shoppingCart,
			Language language) throws Exception {

		// assert not null shopping cart items

		ShopOrder order = new ShopOrder();

		OrderStatus orderStatus = OrderStatus.ORDERED;
		System.out.println("$#12802#"); order.setOrderStatus(orderStatus);

		System.out.println("$#12803#"); if (customer == null) {
			customer = this.initEmptyCustomer(store);
		}

		PersistableCustomer persistableCustomer = persistableCustomer(customer, store, language);
		System.out.println("$#12804#"); order.setCustomer(persistableCustomer);

		// keep list of shopping cart items for core price calculation
		List<ShoppingCartItem> items = new ArrayList<ShoppingCartItem>(shoppingCart.getLineItems());
		System.out.println("$#12805#"); order.setShoppingCartItems(items);

		System.out.println("$#12806#"); return order;
	}

	@Override
	public OrderTotalSummary calculateOrderTotal(MerchantStore store, ShopOrder order, Language language)
			throws Exception {

		Customer customer = customerFacade.getCustomerModel(order.getCustomer(), store, language);
		OrderTotalSummary summary = calculateOrderTotal(store, customer, order, language);
		System.out.println("$#12807#"); this.setOrderTotals(order, summary);
		System.out.println("$#12808#"); return summary;
	}

	@Override
	public OrderTotalSummary calculateOrderTotal(MerchantStore store,
			com.salesmanager.shop.model.order.v0.PersistableOrder order, Language language) throws Exception {

		List<PersistableOrderProduct> orderProducts = order.getOrderProductItems();

		ShoppingCartItemPopulator populator = new ShoppingCartItemPopulator();
		System.out.println("$#12809#"); populator.setProductAttributeService(productAttributeService);
		System.out.println("$#12810#"); populator.setProductService(productService);
		System.out.println("$#12811#"); populator.setShoppingCartService(shoppingCartService);

		List<ShoppingCartItem> items = new ArrayList<ShoppingCartItem>();
		for (PersistableOrderProduct orderProduct : orderProducts) {
			ShoppingCartItem item = populator.populate(orderProduct, new ShoppingCartItem(), store, language);
			items.add(item);
		}

		Customer customer = customer(order.getCustomer(), store, language);

		OrderTotalSummary summary = this.calculateOrderTotal(store, customer, order, language);

		System.out.println("$#12812#"); return summary;
	}

	private OrderTotalSummary calculateOrderTotal(MerchantStore store, Customer customer,
			com.salesmanager.shop.model.order.v0.PersistableOrder order, Language language) throws Exception {

		OrderTotalSummary orderTotalSummary = null;

		OrderSummary summary = new OrderSummary();

		System.out.println("$#12813#"); if (order instanceof ShopOrder) {
			ShopOrder o = (ShopOrder) order;
			System.out.println("$#12814#"); summary.setProducts(o.getShoppingCartItems());

			System.out.println("$#12815#"); if (o.getShippingSummary() != null) {
				System.out.println("$#12816#"); summary.setShippingSummary(o.getShippingSummary());
			}

			System.out.println("$#12817#"); if (!StringUtils.isBlank(o.getCartCode())) {

				ShoppingCart shoppingCart = shoppingCartFacade.getShoppingCartModel(o.getCartCode(), store);

				// promo code
				System.out.println("$#12818#"); if (!StringUtils.isBlank(shoppingCart.getPromoCode())) {
					Date promoDateAdded = shoppingCart.getPromoAdded();// promo
																		// valid
																		// 1 day
					Instant instant = promoDateAdded.toInstant();
					ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
					LocalDate date = zdt.toLocalDate();
					// date added < date + 1 day
					LocalDate tomorrow = LocalDate.now().plusDays(1);
					System.out.println("$#12819#"); if (date.isBefore(tomorrow)) {
						System.out.println("$#12820#"); summary.setPromoCode(shoppingCart.getPromoCode());
					} else {
						// clear promo
						System.out.println("$#12821#"); shoppingCart.setPromoCode(null);
						System.out.println("$#12822#"); shoppingCartService.saveOrUpdate(shoppingCart);
					}
				}

			}

			orderTotalSummary = orderService.caculateOrderTotal(summary, customer, store, language);
		} else {
			// need Set of ShoppingCartItem
			// PersistableOrder not implemented
			throw new Exception("calculateOrderTotal not yet implemented for PersistableOrder");
		}

		System.out.println("$#12823#"); return orderTotalSummary;

	}

	private PersistableCustomer persistableCustomer(Customer customer, MerchantStore store, Language language)
			throws Exception {

		PersistableCustomerPopulator customerPopulator = new PersistableCustomerPopulator();
		PersistableCustomer persistableCustomer = customerPopulator.populate(customer, new PersistableCustomer(), store,
				language);
		System.out.println("$#12824#"); return persistableCustomer;

	}

	private Customer customer(PersistableCustomer customer, MerchantStore store, Language language) throws Exception {

		Customer cust = customerPopulator.populate(customer, new Customer(), store, language);
		System.out.println("$#12825#"); return cust;

	}

	private void setOrderTotals(OrderEntity order, OrderTotalSummary summary) {

		List<OrderTotal> totals = new ArrayList<OrderTotal>();
		List<com.salesmanager.core.model.order.OrderTotal> orderTotals = summary.getTotals();
		for (com.salesmanager.core.model.order.OrderTotal t : orderTotals) {
			OrderTotal total = new OrderTotal();
			System.out.println("$#12826#"); total.setCode(t.getOrderTotalCode());
			System.out.println("$#12827#"); total.setTitle(t.getTitle());
			System.out.println("$#12828#"); total.setValue(t.getValue());
			totals.add(total);
		}

		System.out.println("$#12829#"); order.setTotals(totals);

	}

	/**
	 * Submitted object must be valided prior to the invocation of this method
	 */
	@Override
	public Order processOrder(ShopOrder order, Customer customer, MerchantStore store, Language language)
			throws ServiceException {

		System.out.println("$#12830#"); return processOrderModel(order, customer, null, store, language);

	}

	@Override
	public Order processOrder(ShopOrder order, Customer customer, Transaction transaction, MerchantStore store,
			Language language) throws ServiceException {

		System.out.println("$#12831#"); return processOrderModel(order, customer, transaction, store, language);

	}

	private Order processOrderModel(ShopOrder order, Customer customer, Transaction transaction, MerchantStore store,
			Language language) throws ServiceException {

		try {

			System.out.println("$#12832#"); if (order.isShipToBillingAdress()) {// customer shipping is billing
				PersistableCustomer orderCustomer = order.getCustomer();
				Address billing = orderCustomer.getBilling();
				System.out.println("$#12833#"); orderCustomer.setDelivery(billing);
			}

			Order modelOrder = new Order();
			System.out.println("$#12834#"); modelOrder.setDatePurchased(new Date());
			System.out.println("$#12835#"); modelOrder.setBilling(customer.getBilling());
			System.out.println("$#12836#"); modelOrder.setDelivery(customer.getDelivery());
			System.out.println("$#12837#"); modelOrder.setPaymentModuleCode(order.getPaymentModule());
			System.out.println("$#12838#"); modelOrder.setPaymentType(PaymentType.valueOf(order.getPaymentMethodType()));
			System.out.println("$#12839#"); modelOrder.setShippingModuleCode(order.getShippingModule());
			System.out.println("$#12840#"); modelOrder.setCustomerAgreement(order.isCustomerAgreed());
			System.out.println("$#12841#"); modelOrder.setLocale(LocaleUtils.getLocale(store));// set the store
																// locale based
																// on the
																// country for
																// order $
																// formatting

			List<ShoppingCartItem> shoppingCartItems = order.getShoppingCartItems();
			Set<OrderProduct> orderProducts = new LinkedHashSet<OrderProduct>();

			System.out.println("$#12842#"); if (!StringUtils.isBlank(order.getComments())) {
				OrderStatusHistory statusHistory = new OrderStatusHistory();
				System.out.println("$#12843#"); statusHistory.setStatus(OrderStatus.ORDERED);
				System.out.println("$#12844#"); statusHistory.setOrder(modelOrder);
				System.out.println("$#12845#"); statusHistory.setDateAdded(new Date());
				System.out.println("$#12846#"); statusHistory.setComments(order.getComments());
				modelOrder.getOrderHistory().add(statusHistory);
			}

			OrderProductPopulator orderProductPopulator = new OrderProductPopulator();
			System.out.println("$#12847#"); orderProductPopulator.setDigitalProductService(digitalProductService);
			System.out.println("$#12848#"); orderProductPopulator.setProductAttributeService(productAttributeService);
			System.out.println("$#12849#"); orderProductPopulator.setProductService(productService);
			String shoppingCartCode = null;

			for (ShoppingCartItem item : shoppingCartItems) {
				
				System.out.println("$#12850#"); if(shoppingCartCode == null && item.getShoppingCart()!=null) {
					shoppingCartCode = item.getShoppingCart().getShoppingCartCode();
				}

				/**
				 * Before processing order quantity of item must be > 0
				 */

				Product product = productService.getById(item.getProductId());
				System.out.println("$#12852#"); if (product == null) {
					throw new ServiceException(ServiceException.EXCEPTION_INVENTORY_MISMATCH);
				}

				LOGGER.debug("Validate inventory");
				for (ProductAvailability availability : product.getAvailabilities()) {
					System.out.println("$#12853#"); if (availability.getRegion().equals(Constants.ALL_REGIONS)) {
						int qty = availability.getProductQuantity();
						System.out.println("$#12855#"); System.out.println("$#12854#"); if (qty < item.getQuantity()) {
							throw new ServiceException(ServiceException.EXCEPTION_INVENTORY_MISMATCH);
						}
					}
				}

				OrderProduct orderProduct = new OrderProduct();
				orderProduct = orderProductPopulator.populate(item, orderProduct, store, language);
				System.out.println("$#12856#"); orderProduct.setOrder(modelOrder);
				orderProducts.add(orderProduct);
			}

			System.out.println("$#12857#"); modelOrder.setOrderProducts(orderProducts);

			OrderTotalSummary summary = order.getOrderTotalSummary();
			List<com.salesmanager.core.model.order.OrderTotal> totals = summary.getTotals();

			// re-order totals
			System.out.println("$#12858#"); Collections.sort(totals, new Comparator<com.salesmanager.core.model.order.OrderTotal>() {
				public int compare(com.salesmanager.core.model.order.OrderTotal x,
						com.salesmanager.core.model.order.OrderTotal y) {
					System.out.println("$#13184#"); if (x.getSortOrder() == y.getSortOrder())
						return 0;
					System.out.println("$#13187#"); System.out.println("$#13186#"); System.out.println("$#13185#"); return x.getSortOrder() < y.getSortOrder() ? -1 : 1;
				}

			});

			Set<com.salesmanager.core.model.order.OrderTotal> modelTotals = new LinkedHashSet<com.salesmanager.core.model.order.OrderTotal>();
			for (com.salesmanager.core.model.order.OrderTotal total : totals) {
				System.out.println("$#12859#"); total.setOrder(modelOrder);
				modelTotals.add(total);
			}

			System.out.println("$#12860#"); modelOrder.setOrderTotal(modelTotals);
			System.out.println("$#12861#"); modelOrder.setTotal(order.getOrderTotalSummary().getTotal());

			// order misc objects
			System.out.println("$#12862#"); modelOrder.setCurrency(store.getCurrency());
			System.out.println("$#12863#"); modelOrder.setMerchant(store);

			// customer object
			System.out.println("$#12864#"); orderCustomer(customer, modelOrder, language);

			// populate shipping information
			System.out.println("$#12865#"); if (!StringUtils.isBlank(order.getShippingModule())) {
				System.out.println("$#12866#"); modelOrder.setShippingModuleCode(order.getShippingModule());
			}

			String paymentType = order.getPaymentMethodType();
			Payment payment = new Payment();
			System.out.println("$#12867#"); payment.setPaymentType(PaymentType.valueOf(paymentType));
			System.out.println("$#12868#"); payment.setAmount(order.getOrderTotalSummary().getTotal());
			System.out.println("$#12869#"); payment.setModuleName(order.getPaymentModule());
			System.out.println("$#12870#"); payment.setCurrency(modelOrder.getCurrency());

			System.out.println("$#12871#"); if (order.getPayment() != null && order.getPayment().get("paymentToken") != null) {// set
																								// any
																								// tokenization
																								// payment
																								// token
				String paymentToken = order.getPayment().get("paymentToken");
				Map<String, String> paymentMetaData = new HashMap<String, String>();
				System.out.println("$#12873#"); payment.setPaymentMetaData(paymentMetaData);
				paymentMetaData.put("paymentToken", paymentToken);
			}

			System.out.println("$#12874#"); if (PaymentType.CREDITCARD.name().equals(paymentType)) {

				payment = new CreditCardPayment();
				System.out.println("$#12875#"); ((CreditCardPayment) payment).setCardOwner(order.getPayment().get("creditcard_card_holder"));
				((CreditCardPayment) payment)
						.setCredidCardValidationNumber(order.getPayment().get("creditcard_card_cvv"));
				System.out.println("$#12877#"); ((CreditCardPayment) payment).setCreditCardNumber(order.getPayment().get("creditcard_card_number"));
				((CreditCardPayment) payment)
						.setExpirationMonth(order.getPayment().get("creditcard_card_expirationmonth"));
				((CreditCardPayment) payment)
						.setExpirationYear(order.getPayment().get("creditcard_card_expirationyear"));

				Map<String, String> paymentMetaData = order.getPayment();
				System.out.println("$#12880#"); payment.setPaymentMetaData(paymentMetaData);
				System.out.println("$#12881#"); payment.setPaymentType(PaymentType.valueOf(paymentType));
				System.out.println("$#12882#"); payment.setAmount(order.getOrderTotalSummary().getTotal());
				System.out.println("$#12883#"); payment.setModuleName(order.getPaymentModule());
				System.out.println("$#12884#"); payment.setCurrency(modelOrder.getCurrency());

				CreditCardType creditCardType = null;
				String cardType = order.getPayment().get("creditcard_card_type");

				// supported credit cards
				System.out.println("$#12885#"); if (CreditCardType.AMEX.name().equalsIgnoreCase(cardType)) {
					creditCardType = CreditCardType.AMEX;
				} else if (CreditCardType.VISA.name().equalsIgnoreCase(cardType)) { System.out.println("$#12886#");
					creditCardType = CreditCardType.VISA;
				} else if (CreditCardType.MASTERCARD.name().equalsIgnoreCase(cardType)) { System.out.println("$#12887#");
					creditCardType = CreditCardType.MASTERCARD;
				} else if (CreditCardType.DINERS.name().equalsIgnoreCase(cardType)) { System.out.println("$#12888#");
					creditCardType = CreditCardType.DINERS;
				} else if (CreditCardType.DISCOVERY.name().equalsIgnoreCase(cardType)) { System.out.println("$#12889#");
					creditCardType = CreditCardType.DISCOVERY;
				} else {
					System.out.println("$#12886#"); // manual correction for else-if mutation coverage
					System.out.println("$#12887#"); // manual correction for else-if mutation coverage
					System.out.println("$#12888#"); // manual correction for else-if mutation coverage
					System.out.println("$#12889#"); // manual correction for else-if mutation coverage
				}

				System.out.println("$#12890#"); ((CreditCardPayment) payment).setCreditCard(creditCardType);

				System.out.println("$#12891#"); if (creditCardType != null) {

					CreditCard cc = new CreditCard();
					System.out.println("$#12892#"); cc.setCardType(creditCardType);
					System.out.println("$#12893#"); cc.setCcCvv(((CreditCardPayment) payment).getCredidCardValidationNumber());
					System.out.println("$#12894#"); cc.setCcOwner(((CreditCardPayment) payment).getCardOwner());
					System.out.println("$#12895#"); cc.setCcExpires(((CreditCardPayment) payment).getExpirationMonth() + "-"
							+ ((CreditCardPayment) payment).getExpirationYear());

					// hash credit card number
					System.out.println("$#12896#"); if (!StringUtils.isBlank(cc.getCcNumber())) {
						String maskedNumber = CreditCardUtils
								.maskCardNumber(order.getPayment().get("creditcard_card_number"));
						System.out.println("$#12897#"); cc.setCcNumber(maskedNumber);
						System.out.println("$#12898#"); modelOrder.setCreditCard(cc);
					}

				}

			}

			System.out.println("$#12899#"); if (PaymentType.PAYPAL.name().equals(paymentType)) {

				// check for previous transaction
				System.out.println("$#12900#"); if (transaction == null) {
					throw new ServiceException("payment.error");
				}

				payment = new com.salesmanager.core.model.payments.PaypalPayment();

				((com.salesmanager.core.model.payments.PaypalPayment) payment)
						.setPayerId(transaction.getTransactionDetails().get("PAYERID"));
				((com.salesmanager.core.model.payments.PaypalPayment) payment)
						.setPaymentToken(transaction.getTransactionDetails().get("TOKEN"));

			}

			System.out.println("$#12903#"); modelOrder.setShoppingCartCode(shoppingCartCode);
			System.out.println("$#12904#"); modelOrder.setPaymentModuleCode(order.getPaymentModule());
			System.out.println("$#12905#"); payment.setModuleName(order.getPaymentModule());

			System.out.println("$#12906#"); if (transaction != null) {
				orderService.processOrder(modelOrder, customer, order.getShoppingCartItems(), summary, payment, store);
			} else {
				orderService.processOrder(modelOrder, customer, order.getShoppingCartItems(), summary, payment,
						transaction, store);
			}

			System.out.println("$#12907#"); return modelOrder;

		} catch (ServiceException se) {// may be invalid credit card
			throw se;
		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}

	private void orderCustomer(Customer customer, Order order, Language language) throws Exception {

		// populate customer
		System.out.println("$#12908#"); order.setBilling(customer.getBilling());
		System.out.println("$#12909#"); order.setDelivery(customer.getDelivery());
		System.out.println("$#12910#"); order.setCustomerEmailAddress(customer.getEmailAddress());
		System.out.println("$#12911#"); order.setCustomerId(customer.getId());

	}

	@Override
	public Customer initEmptyCustomer(MerchantStore store) {

		Customer customer = new Customer();
		Billing billing = new Billing();
		System.out.println("$#12912#"); billing.setCountry(store.getCountry());
		System.out.println("$#12913#"); billing.setZone(store.getZone());
		System.out.println("$#12914#"); billing.setState(store.getStorestateprovince());
		/** empty postal code for initial quote **/
		// billing.setPostalCode(store.getStorepostalcode());
		System.out.println("$#12915#"); customer.setBilling(billing);

		Delivery delivery = new Delivery();
		System.out.println("$#12916#"); delivery.setCountry(store.getCountry());
		System.out.println("$#12917#"); delivery.setZone(store.getZone());
		System.out.println("$#12918#"); delivery.setState(store.getStorestateprovince());
		/** empty postal code for initial quote **/
		// delivery.setPostalCode(store.getStorepostalcode());
		System.out.println("$#12919#"); customer.setDelivery(delivery);

		System.out.println("$#12920#"); return customer;
	}

	@Override
	public void refreshOrder(ShopOrder order, MerchantStore store, Customer customer, ShoppingCart shoppingCart,
			Language language) throws Exception {
		System.out.println("$#12921#"); if (customer == null && order.getCustomer() != null) {
			System.out.println("$#12923#"); order.getCustomer().setId(0L);// reset customer id
		}

		System.out.println("$#12924#"); if (customer != null) {
			PersistableCustomer persistableCustomer = persistableCustomer(customer, store, language);
			System.out.println("$#12925#"); order.setCustomer(persistableCustomer);
		}

		List<ShoppingCartItem> items = new ArrayList<ShoppingCartItem>(shoppingCart.getLineItems());
		System.out.println("$#12926#"); order.setShoppingCartItems(items);

		return;
	}

	@Override
	public ShippingQuote getShippingQuote(PersistableCustomer persistableCustomer, ShoppingCart cart, ShopOrder order,
			MerchantStore store, Language language) throws Exception {

		// create shipping products
		List<ShippingProduct> shippingProducts = shoppingCartService.createShippingProduct(cart);

		System.out.println("$#12927#"); if (CollectionUtils.isEmpty(shippingProducts)) {
			return null;// products are virtual
		}

		Customer customer = customerFacade.getCustomerModel(persistableCustomer, store, language);

		Delivery delivery = new Delivery();

		// adjust shipping and billing
		System.out.println("$#12928#"); if (order.isShipToBillingAdress() && !order.isShipToDeliveryAddress()) {

			Billing billing = customer.getBilling();

			String postalCode = billing.getPostalCode();
			postalCode = validatePostalCode(postalCode);

			System.out.println("$#12930#"); delivery.setAddress(billing.getAddress());
			System.out.println("$#12931#"); delivery.setCompany(billing.getCompany());
			System.out.println("$#12932#"); delivery.setCity(billing.getCity());
			System.out.println("$#12933#"); delivery.setPostalCode(billing.getPostalCode());
			System.out.println("$#12934#"); delivery.setState(billing.getState());
			System.out.println("$#12935#"); delivery.setCountry(billing.getCountry());
			System.out.println("$#12936#"); delivery.setZone(billing.getZone());
		} else {
			delivery = customer.getDelivery();
		}

		ShippingQuote quote = shippingService.getShippingQuote(cart.getId(), store, delivery, shippingProducts,
				language);

		System.out.println("$#12937#"); return quote;

	}

	private String validatePostalCode(String postalCode) {

		String patternString = "__";// this one is set in the template
		System.out.println("$#12938#"); if (postalCode.contains(patternString)) {
			postalCode = null;
		}
		System.out.println("$#12939#"); return postalCode;
	}

	@Override
	public List<Country> getShipToCountry(MerchantStore store, Language language) throws Exception {

		List<Country> shippingCountriesList = shippingService.getShipToCountryList(store, language);
		System.out.println("$#12940#"); return shippingCountriesList;

	}

	/**
	 * ShippingSummary contains the subset of information of a ShippingQuote
	 */
	@Override
	public ShippingSummary getShippingSummary(ShippingQuote quote, MerchantStore store, Language language) {

		ShippingSummary summary = new ShippingSummary();
		System.out.println("$#12941#"); if (quote.getSelectedShippingOption() != null) {
			System.out.println("$#12942#"); summary.setShippingQuote(true);
			System.out.println("$#12943#"); summary.setFreeShipping(quote.isFreeShipping());
			System.out.println("$#12944#"); summary.setTaxOnShipping(quote.isApplyTaxOnShipping());
			System.out.println("$#12945#"); summary.setHandling(quote.getHandlingFees());
			System.out.println("$#12946#"); summary.setShipping(quote.getSelectedShippingOption().getOptionPrice());
			System.out.println("$#12947#"); summary.setShippingOption(quote.getSelectedShippingOption().getOptionName());
			System.out.println("$#12948#"); summary.setShippingModule(quote.getShippingModuleCode());
			System.out.println("$#12949#"); summary.setShippingOptionCode(quote.getSelectedShippingOption().getOptionCode());

			System.out.println("$#12950#"); if (quote.getDeliveryAddress() != null) {
				System.out.println("$#12951#"); summary.setDeliveryAddress(quote.getDeliveryAddress());
			}

		}

		System.out.println("$#12952#"); return summary;
	}

	@Override
	public void validateOrder(ShopOrder order, BindingResult bindingResult, Map<String, String> messagesResult,
			MerchantStore store, Locale locale) throws ServiceException {

		System.out.println("$#12953#"); Validate.notNull(messagesResult, "messagesResult should not be null");

		try {

			// Language language = (Language)request.getAttribute("LANGUAGE");

			// validate order shipping and billing
			System.out.println("$#12954#"); if (StringUtils.isBlank(order.getCustomer().getBilling().getFirstName())) {
				FieldError error = new FieldError("customer.billing.firstName", "customer.billing.firstName",
						messages.getMessage("NotEmpty.customer.firstName", locale));
				System.out.println("$#12955#"); bindingResult.addError(error);
				messagesResult.put("customer.billing.firstName",
						messages.getMessage("NotEmpty.customer.firstName", locale));
			}

			System.out.println("$#12956#"); if (StringUtils.isBlank(order.getCustomer().getBilling().getLastName())) {
				FieldError error = new FieldError("customer.billing.lastName", "customer.billing.lastName",
						messages.getMessage("NotEmpty.customer.lastName", locale));
				System.out.println("$#12957#"); bindingResult.addError(error);
				messagesResult.put("customer.billing.lastName",
						messages.getMessage("NotEmpty.customer.lastName", locale));
			}

			System.out.println("$#12958#"); if (StringUtils.isBlank(order.getCustomer().getEmailAddress())) {
				FieldError error = new FieldError("customer.emailAddress", "customer.emailAddress",
						messages.getMessage("NotEmpty.customer.emailAddress", locale));
				System.out.println("$#12959#"); bindingResult.addError(error);
				messagesResult.put("customer.emailAddress",
						messages.getMessage("NotEmpty.customer.emailAddress", locale));
			}

			System.out.println("$#12960#"); if (StringUtils.isBlank(order.getCustomer().getBilling().getAddress())) {
				FieldError error = new FieldError("customer.billing.address", "customer.billing.address",
						messages.getMessage("NotEmpty.customer.billing.address", locale));
				System.out.println("$#12961#"); bindingResult.addError(error);
				messagesResult.put("customer.billing.address",
						messages.getMessage("NotEmpty.customer.billing.address", locale));
			}

			System.out.println("$#12962#"); if (StringUtils.isBlank(order.getCustomer().getBilling().getCity())) {
				FieldError error = new FieldError("customer.billing.city", "customer.billing.city",
						messages.getMessage("NotEmpty.customer.billing.city", locale));
				System.out.println("$#12963#"); bindingResult.addError(error);
				messagesResult.put("customer.billing.city",
						messages.getMessage("NotEmpty.customer.billing.city", locale));
			}

			System.out.println("$#12964#"); if (StringUtils.isBlank(order.getCustomer().getBilling().getCountry())) {
				FieldError error = new FieldError("customer.billing.country", "customer.billing.country",
						messages.getMessage("NotEmpty.customer.billing.country", locale));
				System.out.println("$#12965#"); bindingResult.addError(error);
				messagesResult.put("customer.billing.country",
						messages.getMessage("NotEmpty.customer.billing.country", locale));
			}

			System.out.println("$#12966#"); if (StringUtils.isBlank(order.getCustomer().getBilling().getZone())
					&& StringUtils.isBlank(order.getCustomer().getBilling().getStateProvince())) {
				FieldError error = new FieldError("customer.billing.stateProvince", "customer.billing.stateProvince",
						messages.getMessage("NotEmpty.customer.billing.stateProvince", locale));
				System.out.println("$#12968#"); bindingResult.addError(error);
				messagesResult.put("customer.billing.stateProvince",
						messages.getMessage("NotEmpty.customer.billing.stateProvince", locale));
			}

			System.out.println("$#12969#"); if (StringUtils.isBlank(order.getCustomer().getBilling().getPhone())) {
				FieldError error = new FieldError("customer.billing.phone", "customer.billing.phone",
						messages.getMessage("NotEmpty.customer.billing.phone", locale));
				System.out.println("$#12970#"); bindingResult.addError(error);
				messagesResult.put("customer.billing.phone",
						messages.getMessage("NotEmpty.customer.billing.phone", locale));
			}

			System.out.println("$#12971#"); if (StringUtils.isBlank(order.getCustomer().getBilling().getPostalCode())) {
				FieldError error = new FieldError("customer.billing.postalCode", "customer.billing.postalCode",
						messages.getMessage("NotEmpty.customer.billing.postalCode", locale));
				System.out.println("$#12972#"); bindingResult.addError(error);
				messagesResult.put("customer.billing.postalCode",
						messages.getMessage("NotEmpty.customer.billing.postalCode", locale));
			}

			System.out.println("$#12973#"); if (!order.isShipToBillingAdress()) {

				System.out.println("$#12974#"); if (StringUtils.isBlank(order.getCustomer().getDelivery().getFirstName())) {
					FieldError error = new FieldError("customer.delivery.firstName", "customer.delivery.firstName",
							messages.getMessage("NotEmpty.customer.shipping.firstName", locale));
					System.out.println("$#12975#"); bindingResult.addError(error);
					messagesResult.put("customer.delivery.firstName",
							messages.getMessage("NotEmpty.customer.shipping.firstName", locale));
				}

				System.out.println("$#12976#"); if (StringUtils.isBlank(order.getCustomer().getDelivery().getLastName())) {
					FieldError error = new FieldError("customer.delivery.lastName", "customer.delivery.lastName",
							messages.getMessage("NotEmpty.customer.shipping.lastName", locale));
					System.out.println("$#12977#"); bindingResult.addError(error);
					messagesResult.put("customer.delivery.lastName",
							messages.getMessage("NotEmpty.customer.shipping.lastName", locale));
				}

				System.out.println("$#12978#"); if (StringUtils.isBlank(order.getCustomer().getDelivery().getAddress())) {
					FieldError error = new FieldError("customer.delivery.address", "customer.delivery.address",
							messages.getMessage("NotEmpty.customer.shipping.address", locale));
					System.out.println("$#12979#"); bindingResult.addError(error);
					messagesResult.put("customer.delivery.address",
							messages.getMessage("NotEmpty.customer.shipping.address", locale));
				}

				System.out.println("$#12980#"); if (StringUtils.isBlank(order.getCustomer().getDelivery().getCity())) {
					FieldError error = new FieldError("customer.delivery.city", "customer.delivery.city",
							messages.getMessage("NotEmpty.customer.shipping.city", locale));
					System.out.println("$#12981#"); bindingResult.addError(error);
					messagesResult.put("customer.delivery.city",
							messages.getMessage("NotEmpty.customer.shipping.city", locale));
				}

				System.out.println("$#12982#"); if (StringUtils.isBlank(order.getCustomer().getDelivery().getCountry())) {
					FieldError error = new FieldError("customer.delivery.country", "customer.delivery.country",
							messages.getMessage("NotEmpty.customer.shipping.country", locale));
					System.out.println("$#12983#"); bindingResult.addError(error);
					messagesResult.put("customer.delivery.country",
							messages.getMessage("NotEmpty.customer.shipping.country", locale));
				}

				System.out.println("$#12984#"); if (StringUtils.isBlank(order.getCustomer().getDelivery().getZone())
						&& StringUtils.isBlank(order.getCustomer().getDelivery().getStateProvince())) {
					FieldError error = new FieldError("customer.delivery.stateProvince",
							"customer.delivery.stateProvince",
							messages.getMessage("NotEmpty.customer.shipping.stateProvince", locale));
					System.out.println("$#12986#"); bindingResult.addError(error);
					messagesResult.put("customer.delivery.stateProvince",
							messages.getMessage("NotEmpty.customer.shipping.stateProvince", locale));
				}

				System.out.println("$#12987#"); if (StringUtils.isBlank(order.getCustomer().getDelivery().getPostalCode())) {
					FieldError error = new FieldError("customer.delivery.postalCode", "customer.delivery.postalCode",
							messages.getMessage("NotEmpty.customer.shipping.postalCode", locale));
					System.out.println("$#12988#"); bindingResult.addError(error);
					messagesResult.put("customer.delivery.postalCode",
							messages.getMessage("NotEmpty.customer.shipping.postalCode", locale));
				}

			}

			System.out.println("$#12989#"); if (bindingResult.hasErrors()) {
				return;

			}

			String paymentType = order.getPaymentMethodType();

			// validate payment
			System.out.println("$#12990#"); if (paymentType == null) {
				ServiceException serviceException = new ServiceException(ServiceException.EXCEPTION_VALIDATION,
						"payment.required");
				throw serviceException;
			}

			// validate shipping
			System.out.println("$#12991#"); if (shippingService.requiresShipping(order.getShoppingCartItems(), store)
					&& order.getSelectedShippingOption() == null) {
				ServiceException serviceException = new ServiceException(ServiceException.EXCEPTION_VALIDATION,
						"shipping.required");
				throw serviceException;
			}

			// pre-validate credit card
			System.out.println("$#12993#"); if (PaymentType.CREDITCARD.name().equals(paymentType)
					&& "true".equals(coreConfiguration.getProperty("VALIDATE_CREDIT_CARD"))) {
				String cco = order.getPayment().get("creditcard_card_holder");
				String cvv = order.getPayment().get("creditcard_card_cvv");
				String ccn = order.getPayment().get("creditcard_card_number");
				String ccm = order.getPayment().get("creditcard_card_expirationmonth");
				String ccd = order.getPayment().get("creditcard_card_expirationyear");

				System.out.println("$#12995#"); if (StringUtils.isBlank(cco) || StringUtils.isBlank(cvv) || StringUtils.isBlank(ccn)
						|| StringUtils.isBlank(ccm) || StringUtils.isBlank(ccd)) {
					ObjectError error = new ObjectError("creditcard",
							messages.getMessage("messages.error.creditcard", locale));
					System.out.println("$#13000#"); bindingResult.addError(error);
					messagesResult.put("creditcard", messages.getMessage("messages.error.creditcard", locale));
					return;
				}

				CreditCardType creditCardType = null;
				String cardType = order.getPayment().get("creditcard_card_type");

				System.out.println("$#13001#"); if (cardType.equalsIgnoreCase(CreditCardType.AMEX.name())) {
					creditCardType = CreditCardType.AMEX;
				} else if (cardType.equalsIgnoreCase(CreditCardType.VISA.name())) { System.out.println("$#13002#");
					creditCardType = CreditCardType.VISA;
				} else if (cardType.equalsIgnoreCase(CreditCardType.MASTERCARD.name())) { System.out.println("$#13003#");
					creditCardType = CreditCardType.MASTERCARD;
				} else if (cardType.equalsIgnoreCase(CreditCardType.DINERS.name())) { System.out.println("$#13004#");
					creditCardType = CreditCardType.DINERS;
				} else if (cardType.equalsIgnoreCase(CreditCardType.DISCOVERY.name())) { System.out.println("$#13005#");
					creditCardType = CreditCardType.DISCOVERY;
				} else {
					System.out.println("$#13002#"); // manual correction for else-if mutation coverage
					System.out.println("$#13003#"); // manual correction for else-if mutation coverage
					System.out.println("$#13004#"); // manual correction for else-if mutation coverage
					System.out.println("$#13005#"); // manual correction for else-if mutation coverage
				}

				System.out.println("$#13006#"); if (creditCardType == null) {
					ServiceException serviceException = new ServiceException(ServiceException.EXCEPTION_VALIDATION,
							"cc.type");
					throw serviceException;
				}

			}

		} catch (ServiceException se) {
			LOGGER.error("Error while commiting order", se);
			throw se;
		}

	}

	@Override
	public com.salesmanager.shop.model.order.v0.ReadableOrderList getReadableOrderList(MerchantStore store,
			Customer customer, int start, int maxCount, Language language) throws Exception {

		OrderCriteria criteria = new OrderCriteria();
		System.out.println("$#13007#"); criteria.setStartIndex(start);
		System.out.println("$#13008#"); criteria.setMaxCount(maxCount);
		System.out.println("$#13009#"); criteria.setCustomerId(customer.getId());

		System.out.println("$#13010#"); return this.getReadableOrderList(criteria, store, language);

	}

	@Override
	public com.salesmanager.shop.model.order.v0.ReadableOrderList getReadableOrderList(OrderCriteria criteria,
			MerchantStore store) {

		try {
			System.out.println("$#13011#"); criteria.setLegacyPagination(false);

			OrderList orderList = orderService.getOrders(criteria, store);

			List<Order> orders = orderList.getOrders();
			com.salesmanager.shop.model.order.v0.ReadableOrderList returnList = new com.salesmanager.shop.model.order.v0.ReadableOrderList();

			System.out.println("$#13012#"); if (CollectionUtils.isEmpty(orders)) {
				System.out.println("$#13013#"); returnList.setRecordsTotal(0);
				System.out.println("$#13014#"); return returnList;
			}

			List<com.salesmanager.shop.model.order.v0.ReadableOrder> readableOrders = new ArrayList<com.salesmanager.shop.model.order.v0.ReadableOrder>();
			for (Order order : orders) {
				com.salesmanager.shop.model.order.v0.ReadableOrder readableOrder = new com.salesmanager.shop.model.order.v0.ReadableOrder();
				readableOrderPopulator.populate(order, readableOrder, null, null);
				readableOrders.add(readableOrder);

			}
			System.out.println("$#13015#"); returnList.setOrders(readableOrders);

			System.out.println("$#13016#"); returnList.setRecordsTotal(orderList.getTotalCount());
			System.out.println("$#13017#"); returnList.setTotalPages(orderList.getTotalPages());
			System.out.println("$#13018#"); returnList.setNumber(orderList.getOrders().size());
			System.out.println("$#13019#"); returnList.setRecordsFiltered(orderList.getOrders().size());

			System.out.println("$#13020#"); return returnList;

		} catch (Exception e) {
			throw new ServiceRuntimeException("Error while getting orders", e);
		}

	}

	@Override
	public ShippingQuote getShippingQuote(Customer customer, ShoppingCart cart,
			com.salesmanager.shop.model.order.v0.PersistableOrder order, MerchantStore store, Language language)
			throws Exception {
		// create shipping products
		List<ShippingProduct> shippingProducts = shoppingCartService.createShippingProduct(cart);

		System.out.println("$#13021#"); if (CollectionUtils.isEmpty(shippingProducts)) {
			return null;// products are virtual
		}

		Delivery delivery = new Delivery();

		// adjust shipping and billing
		System.out.println("$#13022#"); if (order.isShipToBillingAdress()) {
			Billing billing = customer.getBilling();
			System.out.println("$#13023#"); delivery.setAddress(billing.getAddress());
			System.out.println("$#13024#"); delivery.setCity(billing.getCity());
			System.out.println("$#13025#"); delivery.setCompany(billing.getCompany());
			System.out.println("$#13026#"); delivery.setPostalCode(billing.getPostalCode());
			System.out.println("$#13027#"); delivery.setState(billing.getState());
			System.out.println("$#13028#"); delivery.setCountry(billing.getCountry());
			System.out.println("$#13029#"); delivery.setZone(billing.getZone());
		} else {
			delivery = customer.getDelivery();
		}

		ShippingQuote quote = shippingService.getShippingQuote(cart.getId(), store, delivery, shippingProducts,
				language);

		System.out.println("$#13030#"); return quote;
	}

	private com.salesmanager.shop.model.order.v0.ReadableOrderList populateOrderList(final OrderList orderList,
			final MerchantStore store, final Language language) {
		List<Order> orders = orderList.getOrders();
		com.salesmanager.shop.model.order.v0.ReadableOrderList returnList = new com.salesmanager.shop.model.order.v0.ReadableOrderList();
		System.out.println("$#13031#"); if (CollectionUtils.isEmpty(orders)) {
			LOGGER.info("Order list if empty..Returning empty list");
			System.out.println("$#13032#"); returnList.setRecordsTotal(0);
			// returnList.setMessage("No results for store code " + store);
			System.out.println("$#13033#"); return returnList;
		}

		// ReadableOrderPopulator orderPopulator = new ReadableOrderPopulator();
		Locale locale = LocaleUtils.getLocale(language);
		System.out.println("$#13034#"); readableOrderPopulator.setLocale(locale);

		List<com.salesmanager.shop.model.order.v0.ReadableOrder> readableOrders = new ArrayList<com.salesmanager.shop.model.order.v0.ReadableOrder>();
		for (Order order : orders) {
			com.salesmanager.shop.model.order.v0.ReadableOrder readableOrder = new com.salesmanager.shop.model.order.v0.ReadableOrder();
			try {
				readableOrderPopulator.populate(order, readableOrder, store, language);
				System.out.println("$#13035#"); setOrderProductList(order, locale, store, language, readableOrder);
			} catch (ConversionException ex) {
				LOGGER.error("Error while converting order to order data", ex);

			}
			readableOrders.add(readableOrder);

		}

		System.out.println("$#13036#"); returnList.setOrders(readableOrders);
		System.out.println("$#13037#"); return returnList;

	}

	private void setOrderProductList(final Order order, final Locale locale, final MerchantStore store,
			final Language language, final com.salesmanager.shop.model.order.v0.ReadableOrder readableOrder)
			throws ConversionException {
		List<ReadableOrderProduct> orderProducts = new ArrayList<ReadableOrderProduct>();
		for (OrderProduct p : order.getOrderProducts()) {
			ReadableOrderProductPopulator orderProductPopulator = new ReadableOrderProductPopulator();
			System.out.println("$#13038#"); orderProductPopulator.setLocale(locale);
			System.out.println("$#13039#"); orderProductPopulator.setProductService(productService);
			System.out.println("$#13040#"); orderProductPopulator.setPricingService(pricingService);
			System.out.println("$#13041#"); orderProductPopulator.setimageUtils(imageUtils);
			ReadableOrderProduct orderProduct = new ReadableOrderProduct();
			orderProductPopulator.populate(p, orderProduct, store, language);

			// image

			// attributes

			orderProducts.add(orderProduct);
		}

		System.out.println("$#13042#"); readableOrder.setProducts(orderProducts);
	}

	private com.salesmanager.shop.model.order.v0.ReadableOrderList getReadableOrderList(OrderCriteria criteria,
			MerchantStore store, Language language) throws Exception {

		OrderList orderList = orderService.listByStore(store, criteria);

		// ReadableOrderPopulator orderPopulator = new ReadableOrderPopulator();
		Locale locale = LocaleUtils.getLocale(language);
		System.out.println("$#13043#"); readableOrderPopulator.setLocale(locale);

		List<Order> orders = orderList.getOrders();
		com.salesmanager.shop.model.order.v0.ReadableOrderList returnList = new com.salesmanager.shop.model.order.v0.ReadableOrderList();

		System.out.println("$#13044#"); if (CollectionUtils.isEmpty(orders)) {
			System.out.println("$#13045#"); returnList.setRecordsTotal(0);
			// returnList.setMessage("No results for store code " + store);
			return null;
		}

		List<com.salesmanager.shop.model.order.v0.ReadableOrder> readableOrders = new ArrayList<com.salesmanager.shop.model.order.v0.ReadableOrder>();
		for (Order order : orders) {
			com.salesmanager.shop.model.order.v0.ReadableOrder readableOrder = new com.salesmanager.shop.model.order.v0.ReadableOrder();
			readableOrderPopulator.populate(order, readableOrder, store, language);
			readableOrders.add(readableOrder);

		}

		System.out.println("$#13046#"); returnList.setRecordsTotal(orderList.getTotalCount());
		System.out.println("$#13047#"); return this.populateOrderList(orderList, store, language);

	}

	@Override
	public com.salesmanager.shop.model.order.v0.ReadableOrderList getReadableOrderList(MerchantStore store, int start,
			int maxCount, Language language) throws Exception {

		OrderCriteria criteria = new OrderCriteria();
		System.out.println("$#13048#"); criteria.setStartIndex(start);
		System.out.println("$#13049#"); criteria.setMaxCount(maxCount);

		System.out.println("$#13050#"); return getReadableOrderList(criteria, store, language);
	}

	@Override
	public com.salesmanager.shop.model.order.v0.ReadableOrder getReadableOrder(Long orderId, MerchantStore store,
			Language language) {
		System.out.println("$#13051#"); Validate.notNull(store, "MerchantStore cannot be null");
		Order modelOrder = orderService.getOrder(orderId, store);
		System.out.println("$#13052#"); if (modelOrder == null) {
			throw new ResourceNotFoundException("Order not found with id " + orderId);
		}

		com.salesmanager.shop.model.order.v0.ReadableOrder readableOrder = new com.salesmanager.shop.model.order.v0.ReadableOrder();

		Long customerId = modelOrder.getCustomerId();
		System.out.println("$#13053#"); if (customerId != null) {
			ReadableCustomer readableCustomer = customerFacade.getCustomerById(customerId, store, language);
			System.out.println("$#13054#"); if (readableCustomer == null) {
				LOGGER.warn("Customer id " + customerId + " not found in order " + orderId);
			} else {
				System.out.println("$#13055#"); readableOrder.setCustomer(readableCustomer);
			}
		}

		try {
			readableOrderPopulator.populate(modelOrder, readableOrder, store, language);

			// order products
			List<ReadableOrderProduct> orderProducts = new ArrayList<ReadableOrderProduct>();
			for (OrderProduct p : modelOrder.getOrderProducts()) {
				ReadableOrderProductPopulator orderProductPopulator = new ReadableOrderProductPopulator();
				System.out.println("$#13056#"); orderProductPopulator.setProductService(productService);
				System.out.println("$#13057#"); orderProductPopulator.setPricingService(pricingService);
				System.out.println("$#13058#"); orderProductPopulator.setimageUtils(imageUtils);

				ReadableOrderProduct orderProduct = new ReadableOrderProduct();
				orderProductPopulator.populate(p, orderProduct, store, language);
				orderProducts.add(orderProduct);
			}

			System.out.println("$#13059#"); readableOrder.setProducts(orderProducts);
		} catch (Exception e) {
			throw new ServiceRuntimeException("Error while getting order [" + orderId + "]");
		}

		System.out.println("$#13060#"); return readableOrder;
	}

	@Override
	public ShippingQuote getShippingQuote(Customer customer, ShoppingCart cart, MerchantStore store, Language language)
			throws Exception {

		System.out.println("$#13061#"); Validate.notNull(customer, "Customer cannot be null");
		System.out.println("$#13062#"); Validate.notNull(cart, "cart cannot be null");

		// create shipping products
		List<ShippingProduct> shippingProducts = shoppingCartService.createShippingProduct(cart);

		System.out.println("$#13063#"); if (CollectionUtils.isEmpty(shippingProducts)) {
			return null;// products are virtual
		}

		Delivery delivery = new Delivery();

		// adjust shipping and billing
		System.out.println("$#13064#"); if (customer.getDelivery() == null || StringUtils.isBlank(customer.getDelivery().getPostalCode())) {
			Billing billing = customer.getBilling();
			System.out.println("$#13066#"); delivery.setAddress(billing.getAddress());
			System.out.println("$#13067#"); delivery.setCity(billing.getCity());
			System.out.println("$#13068#"); delivery.setCompany(billing.getCompany());
			System.out.println("$#13069#"); delivery.setPostalCode(billing.getPostalCode());
			System.out.println("$#13070#"); delivery.setState(billing.getState());
			System.out.println("$#13071#"); delivery.setCountry(billing.getCountry());
			System.out.println("$#13072#"); delivery.setZone(billing.getZone());
		} else {
			delivery = customer.getDelivery();
		}

		ShippingQuote quote = shippingService.getShippingQuote(cart.getId(), store, delivery, shippingProducts,
				language);
		System.out.println("$#13073#"); return quote;
	}

	@Override
	public Order processOrder(com.salesmanager.shop.model.order.v1.PersistableOrder order, Customer customer,
			MerchantStore store, Language language, Locale locale) throws ServiceException {

		try {

			Order modelOrder = new Order();
			persistableOrderApiPopulator.populate(order, modelOrder, store, language);

			Long shoppingCartId = order.getShoppingCartId();
			ShoppingCart cart = shoppingCartService.getById(shoppingCartId, store);

			System.out.println("$#13074#"); if (cart == null) {
				throw new ServiceException("Shopping cart with id " + shoppingCartId + " does not exist");
			}

			Set<ShoppingCartItem> shoppingCartItems = cart.getLineItems();

			List<ShoppingCartItem> items = new ArrayList<ShoppingCartItem>(shoppingCartItems);

			Set<OrderProduct> orderProducts = new LinkedHashSet<OrderProduct>();

			OrderProductPopulator orderProductPopulator = new OrderProductPopulator();
			System.out.println("$#13075#"); orderProductPopulator.setDigitalProductService(digitalProductService);
			System.out.println("$#13076#"); orderProductPopulator.setProductAttributeService(productAttributeService);
			System.out.println("$#13077#"); orderProductPopulator.setProductService(productService);

			for (ShoppingCartItem item : shoppingCartItems) {
				OrderProduct orderProduct = new OrderProduct();
				orderProduct = orderProductPopulator.populate(item, orderProduct, store, language);
				System.out.println("$#13078#"); orderProduct.setOrder(modelOrder);
				orderProducts.add(orderProduct);
			}

			System.out.println("$#13079#"); modelOrder.setOrderProducts(orderProducts);

			System.out.println("$#13081#"); System.out.println("$#13080#"); if (order.getAttributes() != null && order.getAttributes().size() > 0) {
				Set<OrderAttribute> attrs = new HashSet<OrderAttribute>();
				for (com.salesmanager.shop.model.order.OrderAttribute attribute : order.getAttributes()) {
					OrderAttribute attr = new OrderAttribute();
					System.out.println("$#13083#"); attr.setKey(attribute.getKey());
					System.out.println("$#13084#"); attr.setValue(attribute.getValue());
					System.out.println("$#13085#"); attr.setOrder(modelOrder);
					attrs.add(attr);
				}
				System.out.println("$#13086#"); modelOrder.setOrderAttributes(attrs);
			}

			// requires Shipping information (need a quote id calculated)
			ShippingSummary shippingSummary = null;

			// get shipping quote if asked for
			System.out.println("$#13088#"); System.out.println("$#13087#"); if (order.getShippingQuote() != null && order.getShippingQuote().longValue() > 0) {
				shippingSummary = shippingQuoteService.getShippingSummary(order.getShippingQuote(), store);
				System.out.println("$#13090#"); if (shippingSummary != null) {
					System.out.println("$#13091#"); modelOrder.setShippingModuleCode(shippingSummary.getShippingModule());
				}
			}

			// requires Order Totals, this needs recalculation and then compare
			// total with the amount sent as part
			// of process order request. If totals does not match, an error
			// should be thrown.

			OrderTotalSummary orderTotalSummary = null;

			OrderSummary orderSummary = new OrderSummary();
			System.out.println("$#13092#"); orderSummary.setShippingSummary(shippingSummary);
			List<ShoppingCartItem> itemsSet = new ArrayList<ShoppingCartItem>(cart.getLineItems());
			System.out.println("$#13093#"); orderSummary.setProducts(itemsSet);

			orderTotalSummary = orderService.caculateOrderTotal(orderSummary, customer, store, language);

			System.out.println("$#13094#"); if (order.getPayment().getAmount() == null) {
				throw new ConversionException("Requires Payment.amount");
			}

			String submitedAmount = order.getPayment().getAmount();

			BigDecimal calculatedAmount = orderTotalSummary.getTotal();
			String strCalculatedTotal = pricingService.getStringAmount(calculatedAmount, store);

			// compare both prices
			System.out.println("$#13095#"); if (!submitedAmount.equals(strCalculatedTotal)) {
				throw new ConversionException("Payment.amount does not match what the system has calculated "
						+ strCalculatedTotal + " please recalculate the order and submit again");
			}

			System.out.println("$#13096#"); modelOrder.setTotal(calculatedAmount);
			List<com.salesmanager.core.model.order.OrderTotal> totals = orderTotalSummary.getTotals();
			Set<com.salesmanager.core.model.order.OrderTotal> set = new HashSet<com.salesmanager.core.model.order.OrderTotal>();

			System.out.println("$#13097#"); if (!CollectionUtils.isEmpty(totals)) {
				for (com.salesmanager.core.model.order.OrderTotal total : totals) {
					System.out.println("$#13098#"); total.setOrder(modelOrder);
					set.add(total);
				}
			}
			System.out.println("$#13099#"); modelOrder.setOrderTotal(set);

			PersistablePaymentPopulator paymentPopulator = new PersistablePaymentPopulator();
			System.out.println("$#13100#"); paymentPopulator.setPricingService(pricingService);
			Payment paymentModel = new Payment();
			paymentPopulator.populate(order.getPayment(), paymentModel, store, language);

			System.out.println("$#13101#"); modelOrder.setShoppingCartCode(cart.getShoppingCartCode());
			modelOrder = orderService.processOrder(modelOrder, customer, items, orderTotalSummary, paymentModel, store);
			
			// update cart
			try {
				System.out.println("$#13102#"); cart.setOrderId(modelOrder.getId());
				System.out.println("$#13103#"); shoppingCartFacade.saveOrUpdateShoppingCart(cart);
			} catch (Exception e) {
				LOGGER.error("Cannot delete cart " + cart.getId(), e);
			}

			System.out.println("$#13104#"); if ("true".equals(coreConfiguration.getProperty("ORDER_EMAIL_API"))) {
				// send email
				try {

					// send order confirmation email to customer
					System.out.println("$#13105#"); emailTemplatesUtils.sendOrderEmail(customer.getEmailAddress(), customer, modelOrder, locale,
							language, store, coreConfiguration.getProperty("CONTEXT_PATH"));

					System.out.println("$#13106#"); if (orderService.hasDownloadFiles(modelOrder)) {
						System.out.println("$#13107#"); emailTemplatesUtils.sendOrderDownloadEmail(customer, modelOrder, store, locale,
								coreConfiguration.getProperty("CONTEXT_PATH"));
					}

					// send order confirmation email to merchant
					System.out.println("$#13108#"); emailTemplatesUtils.sendOrderEmail(store.getStoreEmailAddress(), customer, modelOrder, locale,
							language, store, coreConfiguration.getProperty("CONTEXT_PATH"));

				} catch (Exception e) {
					LOGGER.error("Cannot send order confirmation email", e);
				}
			}

			System.out.println("$#13109#"); return modelOrder;

		} catch (Exception e) {

			throw new ServiceException(e);

		}

	}

	@Override
	public com.salesmanager.shop.model.order.v0.ReadableOrderList getCapturableOrderList(MerchantStore store,
			Date startDate, Date endDate, Language language) throws Exception {

		// get all transactions for the given date
		List<Order> orders = orderService.getCapturableOrders(store, startDate, endDate);

		// ReadableOrderPopulator orderPopulator = new ReadableOrderPopulator();
		Locale locale = LocaleUtils.getLocale(language);
		System.out.println("$#13110#"); readableOrderPopulator.setLocale(locale);

		com.salesmanager.shop.model.order.v0.ReadableOrderList returnList = new com.salesmanager.shop.model.order.v0.ReadableOrderList();

		System.out.println("$#13111#"); if (CollectionUtils.isEmpty(orders)) {
			System.out.println("$#13112#"); returnList.setRecordsTotal(0);
			// returnList.setMessage("No results for store code " + store);
			return null;
		}

		List<com.salesmanager.shop.model.order.v0.ReadableOrder> readableOrders = new ArrayList<com.salesmanager.shop.model.order.v0.ReadableOrder>();
		for (Order order : orders) {
			com.salesmanager.shop.model.order.v0.ReadableOrder readableOrder = new com.salesmanager.shop.model.order.v0.ReadableOrder();
			readableOrderPopulator.populate(order, readableOrder, store, language);
			readableOrders.add(readableOrder);

		}

		System.out.println("$#13113#"); returnList.setRecordsTotal(orders.size());
		System.out.println("$#13114#"); returnList.setOrders(readableOrders);

		System.out.println("$#13115#"); return returnList;
	}

	@Override
	public ReadableTransaction captureOrder(MerchantStore store, Order order, Customer customer, Language language)
			throws Exception {
		Transaction transactionModel = paymentService.processCapturePayment(order, customer, store);

		ReadableTransaction transaction = new ReadableTransaction();
		ReadableTransactionPopulator trxPopulator = new ReadableTransactionPopulator();
		System.out.println("$#13116#"); trxPopulator.setOrderService(orderService);
		System.out.println("$#13117#"); trxPopulator.setPricingService(pricingService);

		trxPopulator.populate(transactionModel, transaction, store, language);

		System.out.println("$#13118#"); return transaction;

	}

	@Override
	public List<ReadableOrderStatusHistory> getReadableOrderHistory(Long orderId, MerchantStore store,
			Language language) {

		Order order = orderService.getOrder(orderId, store);
		System.out.println("$#13119#"); if (order == null) {
			throw new ResourceNotFoundException(
					"Order id [" + orderId + "] not found for merchand [" + store.getId() + "]");
		}

		Set<OrderStatusHistory> historyList = order.getOrderHistory();
		System.out.println("$#13120#"); List<ReadableOrderStatusHistory> returnList = historyList.stream().map(f -> mapToReadbleOrderStatusHistory(f))
				.collect(Collectors.toList());
		System.out.println("$#13121#"); return returnList;
	}

	ReadableOrderStatusHistory mapToReadbleOrderStatusHistory(OrderStatusHistory source) {
		ReadableOrderStatusHistory readable = new ReadableOrderStatusHistory();
		System.out.println("$#13122#"); readable.setComments(source.getComments());
		System.out.println("$#13123#"); readable.setDate(DateUtil.formatLongDate(source.getDateAdded()));
		System.out.println("$#13124#"); readable.setId(source.getId());
		System.out.println("$#13125#"); readable.setOrderId(source.getOrder().getId());
		System.out.println("$#13126#"); readable.setOrderStatus(source.getStatus().name());

		System.out.println("$#13127#"); return readable;
	}

	@Override
	public void createOrderStatus(PersistableOrderStatusHistory status, Long id, MerchantStore store) {
		System.out.println("$#13128#"); Validate.notNull(status, "OrderStatusHistory must not be null");
		System.out.println("$#13129#"); Validate.notNull(id, "Order id must not be null");
		System.out.println("$#13130#"); Validate.notNull(store, "MerchantStore must not be null");

		// retrieve original order
		Order order = orderService.getOrder(id, store);
		System.out.println("$#13131#"); if (order == null) {
			throw new ResourceNotFoundException(
					"Order with id [" + id + "] does not exist for merchant [" + store.getCode() + "]");
		}

		try {
			OrderStatusHistory history = new OrderStatusHistory();
			System.out.println("$#13132#"); history.setComments(status.getComments());
			System.out.println("$#13133#"); history.setDateAdded(DateUtil.getDate(status.getDate()));
			System.out.println("$#13134#"); history.setOrder(order);
			System.out.println("$#13135#"); history.setStatus(status.getStatus());

			System.out.println("$#13136#"); orderService.addOrderStatusHistory(order, history);

		} catch (Exception e) {
			throw new ServiceRuntimeException("An error occured while converting orderstatushistory", e);
		}

	}

	@Override
	public void updateOrderCustomre(Long orderId, PersistableCustomer customer, MerchantStore store) {
		// TODO Auto-generated method stub
		
		try {
		
		//get order by order id
		Order modelOrder = orderService.getOrder(orderId, store);
		
		System.out.println("$#13137#"); if(modelOrder == null) {
			throw new ResourceNotFoundException("Order id [" + orderId + "] not found for store [" + store.getCode() + "]");
		}
		
		//set customer information
		System.out.println("$#13138#"); modelOrder.setCustomerEmailAddress(customer.getEmailAddress());
		System.out.println("$#13139#"); modelOrder.setBilling(this.convertBilling(customer.getBilling()));
		System.out.println("$#13140#"); modelOrder.setDelivery(this.convertDelivery(customer.getDelivery()));
		
		System.out.println("$#13141#"); orderService.saveOrUpdate(modelOrder);
		
		} catch(Exception e) {
			throw new ServiceRuntimeException("An error occured while updating order customer", e);
		}

	}
	
	private Billing convertBilling(Address source) throws ServiceException {
		Billing target = new Billing();
								System.out.println("$#13142#"); target.setCity(source.getCity());
								System.out.println("$#13143#"); target.setCompany(source.getCompany());
								System.out.println("$#13144#"); target.setFirstName(source.getFirstName());
								System.out.println("$#13145#"); target.setLastName(source.getLastName());
								System.out.println("$#13146#"); target.setPostalCode(source.getPostalCode());
								System.out.println("$#13147#"); target.setTelephone(source.getPhone());
								System.out.println("$#13148#"); target.setAddress(source.getAddress());
								System.out.println("$#13149#"); if(source.getCountry()!=null) {
									System.out.println("$#13150#"); target.setCountry(countryService.getByCode(source.getCountry()));
        }
        
								System.out.println("$#13151#"); if(source.getZone()!=null) {
												System.out.println("$#13152#"); target.setZone(zoneService.getByCode(source.getZone()));
        }
								System.out.println("$#13153#"); target.setState(source.getBilstateOther());
        
								System.out.println("$#13154#"); return target;
	}
	
	private Delivery convertDelivery(Address source) throws ServiceException {
		Delivery target = new Delivery();
								System.out.println("$#13155#"); target.setCity(source.getCity());
								System.out.println("$#13156#"); target.setCompany(source.getCompany());
								System.out.println("$#13157#"); target.setFirstName(source.getFirstName());
								System.out.println("$#13158#"); target.setLastName(source.getLastName());
								System.out.println("$#13159#"); target.setPostalCode(source.getPostalCode());
								System.out.println("$#13160#"); target.setTelephone(source.getPhone());
								System.out.println("$#13161#"); target.setAddress(source.getAddress());
								System.out.println("$#13162#"); if(source.getCountry()!=null) {
									System.out.println("$#13163#"); target.setCountry(countryService.getByCode(source.getCountry()));
        }
        
								System.out.println("$#13164#"); if(source.getZone()!=null) {
												System.out.println("$#13165#"); target.setZone(zoneService.getByCode(source.getZone()));
        }
								System.out.println("$#13166#"); target.setState(source.getBilstateOther());
        
								System.out.println("$#13167#"); return target;
	}

	@Override
	public TransactionType nextTransaction(Long orderId, MerchantStore store) {

		try {
			
			Order modelOrder = orderService.getOrder(orderId, store);

			System.out.println("$#13168#"); if(modelOrder == null) {
				throw new ResourceNotFoundException("Order id [" + orderId + "] not found for store [" + store.getCode() + "]");
			}
			
			Transaction last = transactionService.lastTransaction(modelOrder, store);
			
			System.out.println("$#13169#"); if(last.getTransactionType().name().equals(TransactionType.AUTHORIZE.name())) {
				System.out.println("$#13170#"); return TransactionType.CAPTURE;
			} else if(last.getTransactionType().name().equals(TransactionType.AUTHORIZECAPTURE.name())) { System.out.println("$#13171#");
				System.out.println("$#13172#"); return TransactionType.REFUND;
			} else if(last.getTransactionType().name().equals(TransactionType.CAPTURE.name())) { System.out.println("$#13173#");
				System.out.println("$#13174#"); return TransactionType.REFUND;
			} else if(last.getTransactionType().name().equals(TransactionType.REFUND.name())) { System.out.println("$#13175#");
				System.out.println("$#13176#"); return TransactionType.OK;
			} else {
				System.out.println("$#13171#"); // manual correction for else-if mutation coverage
				System.out.println("$#13173#"); // manual correction for else-if mutation coverage
				System.out.println("$#13175#"); // manual correction for else-if mutation coverage
				System.out.println("$#13177#"); return TransactionType.OK;
			}

			
		} catch(Exception e) {
			throw new ServiceRuntimeException("Error while getting last transaction for order [" + orderId + "]",e);
		}
		

	}

	@Override
	public List<ReadableTransaction> listTransactions(Long orderId, MerchantStore store) {
		System.out.println("$#13178#"); Validate.notNull(orderId, "orderId must not be null");
		System.out.println("$#13179#"); Validate.notNull(store, "MerchantStore must not be null");
		List<ReadableTransaction> trx = new ArrayList<ReadableTransaction>();
		try {
			Order modelOrder = orderService.getOrder(orderId, store);
			
			System.out.println("$#13180#"); if(modelOrder == null) {
				throw new ResourceNotFoundException("Order id [" + orderId + "] not found for store [" + store.getCode() + "]");
			}
			
			List<Transaction> transactions = transactionService.listTransactions(modelOrder);
			
			ReadableTransaction transaction = null;
			ReadableTransactionPopulator trxPopulator = null;
			
			for(Transaction tr : transactions) {
				transaction = new ReadableTransaction();
				trxPopulator = new ReadableTransactionPopulator();
				
				System.out.println("$#13181#"); trxPopulator.setOrderService(orderService);
				System.out.println("$#13182#"); trxPopulator.setPricingService(pricingService);
				
				trxPopulator.populate(tr, transaction, store, store.getDefaultLanguage());
				trx.add(transaction);
			}

			System.out.println("$#13183#"); return trx;

		} catch(Exception e) {
			LOGGER.error("Error while getting transactions for order [" + orderId + "] and store code [" + store.getCode() + "]");
			throw new ServiceRuntimeException("Error while getting transactions for order [" + orderId + "] and store code [" + store.getCode() + "]");
		}

	}

}
