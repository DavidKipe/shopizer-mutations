package com.salesmanager.core.business.services.order;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.modules.order.InvoiceModule;
import com.salesmanager.core.business.repositories.order.OrderRepository;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.services.order.ordertotal.OrderTotalService;
import com.salesmanager.core.business.services.payments.PaymentService;
import com.salesmanager.core.business.services.payments.TransactionService;
import com.salesmanager.core.business.services.shipping.ShippingService;
import com.salesmanager.core.business.services.shoppingcart.ShoppingCartService;
import com.salesmanager.core.business.services.tax.TaxService;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.availability.ProductAvailability;
import com.salesmanager.core.model.catalog.product.price.FinalPrice;
import com.salesmanager.core.model.common.UserContext;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.order.OrderCriteria;
import com.salesmanager.core.model.order.OrderList;
import com.salesmanager.core.model.order.OrderSummary;
import com.salesmanager.core.model.order.OrderSummaryType;
import com.salesmanager.core.model.order.OrderTotal;
import com.salesmanager.core.model.order.OrderTotalSummary;
import com.salesmanager.core.model.order.OrderTotalType;
import com.salesmanager.core.model.order.OrderTotalVariation;
import com.salesmanager.core.model.order.OrderValueType;
import com.salesmanager.core.model.order.orderproduct.OrderProduct;
import com.salesmanager.core.model.order.orderstatus.OrderStatus;
import com.salesmanager.core.model.order.orderstatus.OrderStatusHistory;
import com.salesmanager.core.model.payments.Payment;
import com.salesmanager.core.model.payments.Transaction;
import com.salesmanager.core.model.payments.TransactionType;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shipping.ShippingConfiguration;
import com.salesmanager.core.model.shoppingcart.ShoppingCart;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.core.model.tax.TaxItem;

@Service("orderService")
public class OrderServiceImpl  extends SalesManagerEntityServiceImpl<Long, Order> implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Inject
    private InvoiceModule invoiceModule;

    @Inject
    private ShippingService shippingService;
    
    @Inject
    private PaymentService paymentService;
    
    @Inject
    private ProductService productService;

    @Inject
    private TaxService taxService;
    
    @Inject
    private CustomerService customerService;
    
    @Inject
    private ShoppingCartService shoppingCartService;
    
    @Inject
    private TransactionService transactionService;
    
    @Inject
    private OrderTotalService orderTotalService;

    private final OrderRepository orderRepository;

    @Inject
    public OrderServiceImpl(OrderRepository orderRepository) {
        super(orderRepository);
        this.orderRepository = orderRepository;
    }

    @Override
    public void addOrderStatusHistory(Order order, OrderStatusHistory history) throws ServiceException {
        order.getOrderHistory().add(history);
								System.out.println("$#2281#"); history.setOrder(order);
								System.out.println("$#2282#"); update(order);
    }
    
    @Override
    public Order processOrder(Order order, Customer customer, List<ShoppingCartItem> items, OrderTotalSummary summary, Payment payment, MerchantStore store) throws ServiceException {
    	
					System.out.println("$#2283#"); return process(order, customer, items, summary, payment, null, store);
    }
    
    @Override
    public Order processOrder(Order order, Customer customer, List<ShoppingCartItem> items, OrderTotalSummary summary, Payment payment, Transaction transaction, MerchantStore store) throws ServiceException {
    	
					System.out.println("$#2284#"); return process(order, customer, items, summary, payment, transaction, store);
    }
    
	private Order process(Order order, Customer customer, List<ShoppingCartItem> items, OrderTotalSummary summary, Payment payment, Transaction transaction, MerchantStore store) throws ServiceException {
    	
    	
    	Validate.notNull(order, "Order cannot be null");
    	Validate.notNull(customer, "Customer cannot be null (even if anonymous order)");
    	Validate.notEmpty(items, "ShoppingCart items cannot be null");
    	Validate.notNull(payment, "Payment cannot be null");
    	Validate.notNull(store, "MerchantStore cannot be null");
    	Validate.notNull(summary, "Order total Summary cannot be null");
    	
    	UserContext context = UserContext.getCurrentInstance();
					System.out.println("$#2285#"); if(context != null) {
    		String ipAddress = context.getIpAddress();
						System.out.println("$#2286#"); if(!StringUtils.isBlank(ipAddress)) {
							System.out.println("$#2287#"); order.setIpAddress(ipAddress);
    		}
    	}

    	
    	//first process payment
    	Transaction processTransaction = paymentService.processPayment(customer, store, payment, items, order);
    	
					System.out.println("$#2288#"); if(order.getOrderHistory()==null || order.getOrderHistory().size()==0 || order.getStatus()==null) {
    		OrderStatus status = order.getStatus();
						System.out.println("$#2291#"); if(status==null) {
    			status = OrderStatus.ORDERED;
							System.out.println("$#2292#"); order.setStatus(status);
    		}
    		Set<OrderStatusHistory> statusHistorySet = new HashSet<OrderStatusHistory>();
    		OrderStatusHistory statusHistory = new OrderStatusHistory();
						System.out.println("$#2293#"); statusHistory.setStatus(status);
						System.out.println("$#2294#"); statusHistory.setDateAdded(new Date());
						System.out.println("$#2295#"); statusHistory.setOrder(order);
    		statusHistorySet.add(statusHistory);
						System.out.println("$#2296#"); order.setOrderHistory(statusHistorySet);
    		
    	}
    	
								System.out.println("$#2297#"); if(customer.getId()==null || customer.getId()==0) {
										System.out.println("$#2299#"); customerService.create(customer);
        }
      
								System.out.println("$#2300#"); order.setCustomerId(customer.getId());
								System.out.println("$#2301#"); this.create(order);

					System.out.println("$#2302#"); if(transaction!=null) {
						System.out.println("$#2303#"); transaction.setOrder(order);
						System.out.println("$#2304#"); if(transaction.getId()==null || transaction.getId()==0) {
							System.out.println("$#2306#"); transactionService.create(transaction);
    		} else {
							System.out.println("$#2307#"); transactionService.update(transaction);
    		}
    	}
    	
					System.out.println("$#2308#"); if(processTransaction!=null) {
						System.out.println("$#2309#"); processTransaction.setOrder(order);
						System.out.println("$#2310#"); if(processTransaction.getId()==null || processTransaction.getId()==0) {
							System.out.println("$#2312#"); transactionService.create(processTransaction);
    		} else {
							System.out.println("$#2313#"); transactionService.update(processTransaction);
    		}
    	}

        /**
         * decrement inventory
         */
    	LOGGER.debug( "Update inventory" );
        Set<OrderProduct> products = order.getOrderProducts();
        for(OrderProduct orderProduct : products) {
            orderProduct.getProductQuantity();
            Product p = productService.getByCode(orderProduct.getSku(), store.getDefaultLanguage());
												System.out.println("$#2314#"); if(p == null)
                throw new ServiceException(ServiceException.EXCEPTION_INVENTORY_MISMATCH);
            for(ProductAvailability availability : p.getAvailabilities()) {
                int qty = availability.getProductQuantity();
																System.out.println("$#2316#"); System.out.println("$#2315#"); if(qty < orderProduct.getProductQuantity()) {
                    throw new ServiceException(ServiceException.EXCEPTION_INVENTORY_MISMATCH);
                }
																System.out.println("$#2317#"); qty = qty - orderProduct.getProductQuantity();
																System.out.println("$#2318#"); availability.setProductQuantity(qty);
            }
												System.out.println("$#2319#"); productService.update(p);
        }


        
					System.out.println("$#2320#"); return order;
    }

    private OrderTotalSummary caculateOrder(OrderSummary summary, Customer customer, final MerchantStore store, final Language language) throws Exception {

        OrderTotalSummary totalSummary = new OrderTotalSummary();
        List<OrderTotal> orderTotals = new ArrayList<OrderTotal>();
        Map<String,OrderTotal> otherPricesTotals = new HashMap<String,OrderTotal>();

        ShippingConfiguration shippingConfiguration = null;

        BigDecimal grandTotal = new BigDecimal(0);
        grandTotal.setScale(2, RoundingMode.HALF_UP);

        //price by item
        /**
         * qty * price
         * subtotal
         */
        BigDecimal subTotal = new BigDecimal(0);
        subTotal.setScale(2, RoundingMode.HALF_UP);
        for(ShoppingCartItem item : summary.getProducts()) {

            BigDecimal st = item.getItemPrice().multiply(new BigDecimal(item.getQuantity()));
												System.out.println("$#2321#"); item.setSubTotal(st);
            subTotal = subTotal.add(st);
            //Other prices
            FinalPrice finalPrice = item.getFinalPrice();
												System.out.println("$#2322#"); if(finalPrice!=null) {
                List<FinalPrice> otherPrices = finalPrice.getAdditionalPrices();
																System.out.println("$#2323#"); if(otherPrices!=null) {
                    for(FinalPrice price : otherPrices) {
																								System.out.println("$#2324#"); if(!price.isDefaultPrice()) {
                            OrderTotal itemSubTotal = otherPricesTotals.get(price.getProductPrice().getCode());

																												System.out.println("$#2325#"); if(itemSubTotal==null) {
                                itemSubTotal = new OrderTotal();
																																System.out.println("$#2326#"); itemSubTotal.setModule(Constants.OT_ITEM_PRICE_MODULE_CODE);
																																System.out.println("$#2327#"); itemSubTotal.setTitle(Constants.OT_ITEM_PRICE_MODULE_CODE);
																																System.out.println("$#2328#"); itemSubTotal.setOrderTotalCode(price.getProductPrice().getCode());
																																System.out.println("$#2329#"); itemSubTotal.setOrderTotalType(OrderTotalType.PRODUCT);
																																System.out.println("$#2330#"); itemSubTotal.setSortOrder(0);
                                otherPricesTotals.put(price.getProductPrice().getCode(), itemSubTotal);
                            }

                            BigDecimal orderTotalValue = itemSubTotal.getValue();
																												System.out.println("$#2331#"); if(orderTotalValue==null) {
                                orderTotalValue = new BigDecimal(0);
                                orderTotalValue.setScale(2, RoundingMode.HALF_UP);
                            }

                            orderTotalValue = orderTotalValue.add(price.getFinalPrice());
																												System.out.println("$#2332#"); itemSubTotal.setValue(orderTotalValue);
																												System.out.println("$#2333#"); if(price.getProductPrice().getProductPriceType().name().equals(OrderValueType.ONE_TIME)) {
                                subTotal = subTotal.add(price.getFinalPrice());
                            }
                        }
                    }
                }
            }
        }
        
        //only in order page, otherwise invokes too many processing
        if(
        		OrderSummaryType.ORDERTOTAL.name().equals(summary.getOrderSummaryType().name()) ||
        		OrderSummaryType.SHOPPINGCART.name().equals(summary.getOrderSummaryType().name())
        		
        		) {

	        //Post processing order total variation modules for sub total calculation - drools, custom modules
	        //may affect the sub total
	        OrderTotalVariation orderTotalVariation = orderTotalService.findOrderTotalVariation(summary, customer, store, language);
	        
	        int currentCount = 10;
	        
									System.out.println("$#2336#"); if(CollectionUtils.isNotEmpty(orderTotalVariation.getVariations())) {
	        	for(OrderTotal variation : orderTotalVariation.getVariations()) {
											System.out.println("$#2338#"); System.out.println("$#2337#"); variation.setSortOrder(currentCount++);
	        		orderTotals.add(variation);
	        		subTotal = subTotal.subtract(variation.getValue());
	        	}
	        }
        
        }


								System.out.println("$#2339#"); totalSummary.setSubTotal(subTotal);
        grandTotal=grandTotal.add(subTotal);

        OrderTotal orderTotalSubTotal = new OrderTotal();
								System.out.println("$#2340#"); orderTotalSubTotal.setModule(Constants.OT_SUBTOTAL_MODULE_CODE);
								System.out.println("$#2341#"); orderTotalSubTotal.setOrderTotalType(OrderTotalType.SUBTOTAL);
								System.out.println("$#2342#"); orderTotalSubTotal.setOrderTotalCode("order.total.subtotal");
								System.out.println("$#2343#"); orderTotalSubTotal.setTitle(Constants.OT_SUBTOTAL_MODULE_CODE);
        //orderTotalSubTotal.setText("order.total.subtotal");
								System.out.println("$#2344#"); orderTotalSubTotal.setSortOrder(5);
								System.out.println("$#2345#"); orderTotalSubTotal.setValue(subTotal);
        
        orderTotals.add(orderTotalSubTotal);


        //shipping
								System.out.println("$#2346#"); if(summary.getShippingSummary()!=null) {


	            OrderTotal shippingSubTotal = new OrderTotal();
													System.out.println("$#2347#"); shippingSubTotal.setModule(Constants.OT_SHIPPING_MODULE_CODE);
													System.out.println("$#2348#"); shippingSubTotal.setOrderTotalType(OrderTotalType.SHIPPING);
													System.out.println("$#2349#"); shippingSubTotal.setOrderTotalCode("order.total.shipping");
													System.out.println("$#2350#"); shippingSubTotal.setTitle(Constants.OT_SHIPPING_MODULE_CODE);
	            //shippingSubTotal.setText("order.total.shipping");
													System.out.println("$#2351#"); shippingSubTotal.setSortOrder(100);
	
	            orderTotals.add(shippingSubTotal);

												System.out.println("$#2352#"); if(!summary.getShippingSummary().isFreeShipping()) {
																System.out.println("$#2353#"); shippingSubTotal.setValue(summary.getShippingSummary().getShipping());
                grandTotal=grandTotal.add(summary.getShippingSummary().getShipping());
            } else {
																System.out.println("$#2354#"); shippingSubTotal.setValue(new BigDecimal(0));
                grandTotal=grandTotal.add(new BigDecimal(0));
            }

            //check handling fees
            shippingConfiguration = shippingService.getShippingConfiguration(store);
												System.out.println("$#2356#"); System.out.println("$#2355#"); if(summary.getShippingSummary().getHandling()!=null && summary.getShippingSummary().getHandling().doubleValue()>0) {
																System.out.println("$#2359#"); System.out.println("$#2358#"); if(shippingConfiguration.getHandlingFees()!=null && shippingConfiguration.getHandlingFees().doubleValue()>0) {
                    OrderTotal handlingubTotal = new OrderTotal();
																				System.out.println("$#2361#"); handlingubTotal.setModule(Constants.OT_HANDLING_MODULE_CODE);
																				System.out.println("$#2362#"); handlingubTotal.setOrderTotalType(OrderTotalType.HANDLING);
																				System.out.println("$#2363#"); handlingubTotal.setOrderTotalCode("order.total.handling");
																				System.out.println("$#2364#"); handlingubTotal.setTitle(Constants.OT_HANDLING_MODULE_CODE);
                    //handlingubTotal.setText("order.total.handling");
																				System.out.println("$#2365#"); handlingubTotal.setSortOrder(120);
																				System.out.println("$#2366#"); handlingubTotal.setValue(summary.getShippingSummary().getHandling());
                    orderTotals.add(handlingubTotal);
                    grandTotal=grandTotal.add(summary.getShippingSummary().getHandling());
                }
            }
        }

        //tax
        List<TaxItem> taxes = taxService.calculateTax(summary, customer, store, language);
								System.out.println("$#2368#"); System.out.println("$#2367#"); if(taxes!=null && taxes.size()>0) {
        	BigDecimal totalTaxes = new BigDecimal(0);
        	totalTaxes.setScale(2, RoundingMode.HALF_UP);
            int taxCount = 200;
            for(TaxItem tax : taxes) {

                OrderTotal taxLine = new OrderTotal();
																System.out.println("$#2370#"); taxLine.setModule(Constants.OT_TAX_MODULE_CODE);
																System.out.println("$#2371#"); taxLine.setOrderTotalType(OrderTotalType.TAX);
																System.out.println("$#2372#"); taxLine.setOrderTotalCode(tax.getLabel());
																System.out.println("$#2373#"); taxLine.setSortOrder(taxCount);
																System.out.println("$#2374#"); taxLine.setTitle(Constants.OT_TAX_MODULE_CODE);
																System.out.println("$#2375#"); taxLine.setText(tax.getLabel());
																System.out.println("$#2376#"); taxLine.setValue(tax.getItemPrice());

                totalTaxes = totalTaxes.add(tax.getItemPrice());
                orderTotals.add(taxLine);
                //grandTotal=grandTotal.add(tax.getItemPrice());

																System.out.println("$#2377#"); taxCount ++;

            }
            grandTotal = grandTotal.add(totalTaxes);
												System.out.println("$#2378#"); totalSummary.setTaxTotal(totalTaxes);
        }

        // grand total
        OrderTotal orderTotal = new OrderTotal();
								System.out.println("$#2379#"); orderTotal.setModule(Constants.OT_TOTAL_MODULE_CODE);
								System.out.println("$#2380#"); orderTotal.setOrderTotalType(OrderTotalType.TOTAL);
								System.out.println("$#2381#"); orderTotal.setOrderTotalCode("order.total.total");
								System.out.println("$#2382#"); orderTotal.setTitle(Constants.OT_TOTAL_MODULE_CODE);
        //orderTotal.setText("order.total.total");
								System.out.println("$#2383#"); orderTotal.setSortOrder(500);
								System.out.println("$#2384#"); orderTotal.setValue(grandTotal);
        orderTotals.add(orderTotal);

								System.out.println("$#2385#"); totalSummary.setTotal(grandTotal);
								System.out.println("$#2386#"); totalSummary.setTotals(orderTotals);
								System.out.println("$#2387#"); return totalSummary;

    }


    @Override
    public OrderTotalSummary caculateOrderTotal(final OrderSummary orderSummary, final Customer customer, final MerchantStore store, final Language language) throws ServiceException {
        Validate.notNull(orderSummary,"Order summary cannot be null");
        Validate.notNull(orderSummary.getProducts(),"Order summary.products cannot be null");
        Validate.notNull(store,"MerchantStore cannot be null");
        Validate.notNull(customer,"Customer cannot be null");

        try {
												System.out.println("$#2388#"); return caculateOrder(orderSummary, customer, store, language);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

    }



    @Override
    public OrderTotalSummary caculateOrderTotal(final OrderSummary orderSummary, final MerchantStore store, final Language language) throws ServiceException {
        Validate.notNull(orderSummary,"Order summary cannot be null");
        Validate.notNull(orderSummary.getProducts(),"Order summary.products cannot be null");
        Validate.notNull(store,"MerchantStore cannot be null");

        try {
												System.out.println("$#2389#"); return caculateOrder(orderSummary, null, store, language);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

    }

    private OrderTotalSummary caculateShoppingCart( ShoppingCart shoppingCart, final Customer customer, final MerchantStore store, final Language language) throws Exception {


    	OrderSummary orderSummary = new OrderSummary();
					System.out.println("$#2390#"); orderSummary.setOrderSummaryType(OrderSummaryType.SHOPPINGCART);
    	
					System.out.println("$#2391#"); if(!StringUtils.isBlank(shoppingCart.getPromoCode())) {
    		Date promoDateAdded = shoppingCart.getPromoAdded();//promo valid 1 day
    		Instant instant = promoDateAdded.toInstant();
    		ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
    		LocalDate date = zdt.toLocalDate();
    		//date added < date + 1 day
    		LocalDate tomorrow = LocalDate.now().plusDays(1);
						System.out.println("$#2392#"); if(date.isBefore(tomorrow)) {
							System.out.println("$#2393#"); orderSummary.setPromoCode(shoppingCart.getPromoCode());
    		} else {
    			//clear promo
							System.out.println("$#2394#"); shoppingCart.setPromoCode(null);
							System.out.println("$#2395#"); shoppingCartService.saveOrUpdate(shoppingCart);
    		}
    	}    	
    	
    	List<ShoppingCartItem> itemList = new ArrayList<ShoppingCartItem>(shoppingCart.getLineItems());
    	//filter out unavailable
    	itemList = itemList.stream().filter(p -> p.getProduct().isAvailable()).collect(Collectors.toList());
					System.out.println("$#2398#"); orderSummary.setProducts(itemList);
    	
    	
					System.out.println("$#2399#"); return caculateOrder(orderSummary, customer, store, language);

    }


    /**
     * <p>Method will be used to calculate Shopping cart total as well will update price for each
     * line items.
     * </p>
     * @param shoppingCart
     * @param customer
     * @param store
     * @param language
     * @return {@link OrderTotalSummary}
     * @throws ServiceException
     * 
     */
    @Override
    public OrderTotalSummary calculateShoppingCartTotal(
                                                        final ShoppingCart shoppingCart, final Customer customer, final MerchantStore store,
                                                        final Language language) throws ServiceException {
        Validate.notNull(shoppingCart,"Order summary cannot be null");
        Validate.notNull(customer,"Customery cannot be null");
        Validate.notNull(store,"MerchantStore cannot be null.");
        try {
												System.out.println("$#2400#"); return caculateShoppingCart(shoppingCart, customer, store, language);
        } catch (Exception e) {
            LOGGER.error( "Error while calculating shopping cart total" +e );
            throw new ServiceException(e);
        }

    }




    /**
     * <p>Method will be used to calculate Shopping cart total as well will update price for each
     * line items.
     * </p>
     * @param shoppingCart
     * @param store
     * @param language
     * @return {@link OrderTotalSummary}
     * @throws ServiceException
     * 
     */
    @Override
    public OrderTotalSummary calculateShoppingCartTotal(
                                                        final ShoppingCart shoppingCart, final MerchantStore store, final Language language)
                                                                        throws ServiceException {
        Validate.notNull(shoppingCart,"Order summary cannot be null");
        Validate.notNull(store,"MerchantStore cannot be null");

        try {
												System.out.println("$#2401#"); return caculateShoppingCart(shoppingCart, null, store, language);
        } catch (Exception e) {
            LOGGER.error( "Error while calculating shopping cart total" +e );
            throw new ServiceException(e);
        }
    }

    @Override
    public void delete(final Order order) throws ServiceException {


								System.out.println("$#2402#"); super.delete(order);
    }


    @Override
    public ByteArrayOutputStream generateInvoice(final MerchantStore store, final Order order, final Language language) throws ServiceException {

        Validate.notNull(order.getOrderProducts(),"Order products cannot be null");
        Validate.notNull(order.getOrderTotal(),"Order totals cannot be null");

        try {
            ByteArrayOutputStream stream = invoiceModule.createInvoice(store, order, language);
												System.out.println("$#2403#"); return stream;
        } catch(Exception e) {
            throw new ServiceException(e);
        }



    }

    @Override
    public Order getOrder(final Long orderId, MerchantStore store ) {
    	Validate.notNull(orderId, "Order id cannot be null");
    	Validate.notNull(store, "Store cannot be null");
								System.out.println("$#2404#"); return orderRepository.findOne(orderId, store.getId());
    }


    /** legacy **/
    @Override
    public OrderList listByStore(final MerchantStore store, final OrderCriteria criteria) {
								System.out.println("$#2405#"); return orderRepository.listByStore(store, criteria);
    }

    @Override
    public OrderList getOrders(final OrderCriteria criteria, MerchantStore store) {
								System.out.println("$#2406#"); return orderRepository.listOrders(store, criteria);
    }


    @Override
    public void saveOrUpdate(final Order order) throws ServiceException {

								System.out.println("$#2408#"); System.out.println("$#2407#"); if(order.getId()!=null && order.getId()>0) {
            LOGGER.debug("Updating Order");
												System.out.println("$#2410#"); super.update(order);

        } else {
            LOGGER.debug("Creating Order");
												System.out.println("$#2411#"); super.create(order);

        }
    }

	@Override
	public boolean hasDownloadFiles(Order order) throws ServiceException {
		
		Validate.notNull(order,"Order cannot be null");
		Validate.notNull(order.getOrderProducts(),"Order products cannot be null");
		Validate.notEmpty(order.getOrderProducts(),"Order products cannot be empty");
		
		boolean hasDownloads = false;
		for(OrderProduct orderProduct : order.getOrderProducts()) {
			
			System.out.println("$#2412#"); if(CollectionUtils.isNotEmpty(orderProduct.getDownloads())) {
				hasDownloads = true;
				break;
			}
		}
		
		System.out.println("$#2414#"); System.out.println("$#2413#"); return hasDownloads;
	}

	@Override
	public List<Order> getCapturableOrders(MerchantStore store, Date startDate, Date endDate) throws ServiceException {
		
		List<Transaction> transactions = transactionService.listTransactions(startDate, endDate);
		
		List<Order> returnOrders = null;

		System.out.println("$#2415#"); if(!CollectionUtils.isEmpty(transactions)) {
			
			returnOrders = new ArrayList<Order>();
			
			//order id
			Map<Long,Order> preAuthOrders = new HashMap<Long,Order> ();
			//order id
			Map<Long,List<Transaction>> processingTransactions = new HashMap<Long,List<Transaction>> ();
			
			for(Transaction trx : transactions) {
				Order order = trx.getOrder();
				System.out.println("$#2416#"); if(TransactionType.AUTHORIZE.name().equals(trx.getTransactionType().name())) {
					preAuthOrders.put(order.getId(), order);
				}
				
				//put transaction
				List<Transaction> listTransactions = null;
				System.out.println("$#2417#"); if(processingTransactions.containsKey(order.getId())) {
					listTransactions = processingTransactions.get(order.getId());
				} else {
					listTransactions = new ArrayList<Transaction>();
					processingTransactions.put(order.getId(), listTransactions);
				}
				listTransactions.add(trx);
			}
			
			//should have when captured
			/**
			 * Order id  Transaction type
			 * 1          AUTHORIZE
			 * 1          CAPTURE 
			 */
			
			//should have when not captured
			/**
			 * Order id  Transaction type
			 * 2          AUTHORIZE
			 */
			
			for(Long orderId : processingTransactions.keySet()) {
				
				List<Transaction> trx = processingTransactions.get(orderId);
				System.out.println("$#2418#"); if(CollectionUtils.isNotEmpty(trx)) {
					
					boolean capturable = true;
					for(Transaction t : trx) {
						
						System.out.println("$#2419#"); if(TransactionType.CAPTURE.name().equals(t.getTransactionType().name())) {
							capturable = false;
						} else if(TransactionType.AUTHORIZECAPTURE.name().equals(t.getTransactionType().name())) { System.out.println("$#2420#");
							capturable = false;
						} else if(TransactionType.REFUND.name().equals(t.getTransactionType().name())) { System.out.println("$#2421#");
							capturable = false;
						} else {
							System.out.println("$#2420#"); // manual correction for else-if mutation coverage
							System.out.println("$#2421#"); // manual correction for else-if mutation coverage
						}
						
					}
					
					System.out.println("$#2422#"); if(capturable) {
						Order o = preAuthOrders.get(orderId);
						returnOrders.add(o);
					}
					
				}
				
				
			}
		}

		System.out.println("$#2423#"); return returnOrders;
	}



}
