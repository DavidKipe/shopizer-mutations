package com.salesmanager.shop.populator.order;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductAttributeService;
import com.salesmanager.core.business.services.catalog.product.file.DigitalProductService;
import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.services.reference.currency.CurrencyService;
import com.salesmanager.core.business.services.shoppingcart.ShoppingCartService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.common.Billing;
import com.salesmanager.core.model.common.Delivery;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.order.OrderChannel;
import com.salesmanager.core.model.order.attributes.OrderAttribute;
import com.salesmanager.core.model.order.orderstatus.OrderStatus;
import com.salesmanager.core.model.order.orderstatus.OrderStatusHistory;
import com.salesmanager.core.model.payments.PaymentType;
import com.salesmanager.core.model.reference.currency.Currency;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.customer.PersistableCustomer;
import com.salesmanager.shop.model.order.v1.PersistableAnonymousOrder;
import com.salesmanager.shop.model.order.v1.PersistableOrder;
import com.salesmanager.shop.populator.customer.CustomerPopulator;
import com.salesmanager.shop.utils.LocaleUtils;

@Component
public class PersistableOrderApiPopulator extends AbstractDataPopulator<PersistableOrder, Order> {

	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private CustomerService customerService;
/*	@Autowired
	private ShoppingCartService shoppingCartService;
	@Autowired
	private ProductService productService;
	@Autowired
	private ProductAttributeService productAttributeService;
	@Autowired
	private DigitalProductService digitalProductService;*/
	@Autowired
	private CustomerPopulator customerPopulator;
	
	

	


	@Override
	public Order populate(PersistableOrder source, Order target, MerchantStore store, Language language)
			throws ConversionException {
		

/*		Validate.notNull(currencyService,"currencyService must be set");
		Validate.notNull(customerService,"customerService must be set");
		Validate.notNull(shoppingCartService,"shoppingCartService must be set");
		Validate.notNull(productService,"productService must be set");
		Validate.notNull(productAttributeService,"productAttributeService must be set");
		Validate.notNull(digitalProductService,"digitalProductService must be set");*/
		System.out.println("$#10517#"); Validate.notNull(source.getPayment(),"Payment cannot be null");
		
		try {
			
			System.out.println("$#10518#"); if(target == null) {
				target = new Order();
			}
		
			//target.setLocale(LocaleUtils.getLocale(store));

			System.out.println("$#10519#"); target.setLocale(LocaleUtils.getLocale(store));
			
			
			Currency currency = null;
			try {
				currency = currencyService.getByCode(source.getCurrency());
			} catch(Exception e) {
				throw new ConversionException("Currency not found for code " + source.getCurrency());
			}
			
			System.out.println("$#10520#"); if(currency==null) {
				throw new ConversionException("Currency not found for code " + source.getCurrency());
			}
			
			//Customer
			Customer customer = null;
			System.out.println("$#10522#"); System.out.println("$#10521#"); if(source.getCustomerId() != null && source.getCustomerId().longValue() >0) {
			  Long customerId = source.getCustomerId();
			  customer = customerService.getById(customerId);

					System.out.println("$#10524#"); if(customer == null) {
				throw new ConversionException("Curstomer with id " + source.getCustomerId() + " does not exist");
			  }
					System.out.println("$#10525#"); target.setCustomerId(customerId);
			
			} else {
					System.out.println("$#10526#"); if(source instanceof PersistableAnonymousOrder) {
			    PersistableCustomer persistableCustomer = ((PersistableAnonymousOrder)source).getCustomer();
			    customer = new Customer();
			    customer = customerPopulator.populate(persistableCustomer, customer, store, language);
			  } else {
			    throw new ConversionException("Curstomer details or id not set in request");
			  } 
			}
			
			
			System.out.println("$#10527#"); target.setCustomerEmailAddress(customer.getEmailAddress());
			
			Delivery delivery = customer.getDelivery();
			System.out.println("$#10528#"); target.setDelivery(delivery);
			
			Billing billing = customer.getBilling();
			System.out.println("$#10529#"); target.setBilling(billing);
			
			System.out.println("$#10531#"); System.out.println("$#10530#"); if(source.getAttributes() != null && source.getAttributes().size() > 0) {
				Set<OrderAttribute> attrs = new HashSet<OrderAttribute>();
				for(com.salesmanager.shop.model.order.OrderAttribute attribute : source.getAttributes()) {
					OrderAttribute attr = new OrderAttribute();
					System.out.println("$#10533#"); attr.setKey(attribute.getKey());
					System.out.println("$#10534#"); attr.setValue(attribute.getValue());
					System.out.println("$#10535#"); attr.setOrder(target);
					attrs.add(attr);
				}
				System.out.println("$#10536#"); target.setOrderAttributes(attrs);
			}

			System.out.println("$#10537#"); target.setDatePurchased(new Date());
			System.out.println("$#10538#"); target.setCurrency(currency);
			System.out.println("$#10539#"); target.setCurrencyValue(new BigDecimal(0));
			System.out.println("$#10540#"); target.setMerchant(store);
			System.out.println("$#10541#"); target.setChannel(OrderChannel.API);
			//need this
			System.out.println("$#10542#"); target.setStatus(OrderStatus.ORDERED);
			System.out.println("$#10543#"); target.setPaymentModuleCode(source.getPayment().getPaymentModule());
			System.out.println("$#10544#"); target.setPaymentType(PaymentType.valueOf(source.getPayment().getPaymentType()));
			
			System.out.println("$#10545#"); target.setCustomerAgreement(source.isCustomerAgreement());
			System.out.println("$#10546#"); target.setConfirmedAddress(true);//force this to true, cannot perform this activity from the API

			
			System.out.println("$#10547#"); if(!StringUtils.isBlank(source.getComments())) {
				OrderStatusHistory statusHistory = new OrderStatusHistory();
				System.out.println("$#10548#"); statusHistory.setStatus(null);
				System.out.println("$#10549#"); statusHistory.setOrder(target);
				System.out.println("$#10550#"); statusHistory.setComments(source.getComments());
				target.getOrderHistory().add(statusHistory);
			}
			
			System.out.println("$#10551#"); return target;
		
		} catch(Exception e) {
			throw new ConversionException(e);
		}
	}

	@Override
	protected Order createTarget() {
		// TODO Auto-generated method stub
		return null;
	}


/*	public CurrencyService getCurrencyService() {
		return currencyService;
	}

	public void setCurrencyService(CurrencyService currencyService) {
		this.currencyService = currencyService;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public ShoppingCartService getShoppingCartService() {
		return shoppingCartService;
	}

	public void setShoppingCartService(ShoppingCartService shoppingCartService) {
		this.shoppingCartService = shoppingCartService;
	}

	public ProductService getProductService() {
		return productService;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public ProductAttributeService getProductAttributeService() {
		return productAttributeService;
	}

	public void setProductAttributeService(ProductAttributeService productAttributeService) {
		this.productAttributeService = productAttributeService;
	}

	public DigitalProductService getDigitalProductService() {
		return digitalProductService;
	}

	public void setDigitalProductService(DigitalProductService digitalProductService) {
		this.digitalProductService = digitalProductService;
	}*/



}
