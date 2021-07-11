package com.salesmanager.shop.populator.order;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductAttributeService;
import com.salesmanager.core.business.services.catalog.product.file.DigitalProductService;
import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.currency.CurrencyService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.business.utils.CreditCardUtils;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.order.orderproduct.OrderProduct;
import com.salesmanager.core.model.order.orderstatus.OrderStatus;
import com.salesmanager.core.model.order.orderstatus.OrderStatusHistory;
import com.salesmanager.core.model.order.payment.CreditCard;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.currency.Currency;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.shop.model.customer.PersistableCustomer;
import com.salesmanager.shop.model.order.PersistableOrderProduct;
import com.salesmanager.shop.model.order.total.OrderTotal;
import com.salesmanager.shop.model.order.v0.PersistableOrder;
import com.salesmanager.shop.utils.LocaleUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class PersistableOrderPopulator extends
		AbstractDataPopulator<PersistableOrder, Order> {
	
	private CustomerService customerService;
	private CountryService countryService;
	private CurrencyService currencyService;


	private ZoneService zoneService;
	private ProductService productService;
	private DigitalProductService digitalProductService;
	private ProductAttributeService productAttributeService;

	@Override
	public Order populate(PersistableOrder source, Order target,
			MerchantStore store, Language language) throws ConversionException {
		
		
		System.out.println("$#10552#"); Validate.notNull(productService,"productService must be set");
		System.out.println("$#10553#"); Validate.notNull(digitalProductService,"digitalProductService must be set");
		System.out.println("$#10554#"); Validate.notNull(productAttributeService,"productAttributeService must be set");
		System.out.println("$#10555#"); Validate.notNull(customerService,"customerService must be set");
		System.out.println("$#10556#"); Validate.notNull(countryService,"countryService must be set");
		System.out.println("$#10557#"); Validate.notNull(zoneService,"zoneService must be set");
		System.out.println("$#10558#"); Validate.notNull(currencyService,"currencyService must be set");

		try {
			

			Map<String,Country> countriesMap = countryService.getCountriesMap(language);
			Map<String,Zone> zonesMap = zoneService.getZones(language);
			/** customer **/
			PersistableCustomer customer = source.getCustomer();
			System.out.println("$#10559#"); if(customer!=null) {
				System.out.println("$#10561#"); System.out.println("$#10560#"); if(customer.getId()!=null && customer.getId()>0) {
					Customer modelCustomer = customerService.getById(customer.getId());
					System.out.println("$#10563#"); if(modelCustomer==null) {
						throw new ConversionException("Customer id " + customer.getId() + " does not exists");
					}
					System.out.println("$#10564#"); if(modelCustomer.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
						throw new ConversionException("Customer id " + customer.getId() + " does not exists for store " + store.getCode());
					}
					System.out.println("$#10565#"); target.setCustomerId(modelCustomer.getId());
					System.out.println("$#10566#"); target.setBilling(modelCustomer.getBilling());
					System.out.println("$#10567#"); target.setDelivery(modelCustomer.getDelivery());
					System.out.println("$#10568#"); target.setCustomerEmailAddress(source.getCustomer().getEmailAddress());


					
				} 
			}
			
			System.out.println("$#10569#"); target.setLocale(LocaleUtils.getLocale(store));
			
			CreditCard creditCard = source.getCreditCard();
			System.out.println("$#10570#"); if(creditCard!=null) {
				String maskedNumber = CreditCardUtils.maskCardNumber(creditCard.getCcNumber());
				System.out.println("$#10571#"); creditCard.setCcNumber(maskedNumber);
				System.out.println("$#10572#"); target.setCreditCard(creditCard);
			}
			
			Currency currency = null;
			try {
				currency = currencyService.getByCode(source.getCurrency());
			} catch(Exception e) {
				throw new ConversionException("Currency not found for code " + source.getCurrency());
			}
			
			System.out.println("$#10573#"); if(currency==null) {
				throw new ConversionException("Currency not found for code " + source.getCurrency());
			}
			
			System.out.println("$#10574#"); target.setCurrency(currency);
			System.out.println("$#10575#"); target.setDatePurchased(source.getDatePurchased());
			//target.setCurrency(store.getCurrency());
			System.out.println("$#10576#"); target.setCurrencyValue(new BigDecimal(0));
			System.out.println("$#10577#"); target.setMerchant(store);
			System.out.println("$#10578#"); target.setStatus(source.getOrderStatus());
			System.out.println("$#10579#"); target.setPaymentModuleCode(source.getPaymentModule());
			System.out.println("$#10580#"); target.setPaymentType(source.getPaymentType());
			System.out.println("$#10581#"); target.setShippingModuleCode(source.getShippingModule());
			System.out.println("$#10582#"); target.setCustomerAgreement(source.isCustomerAgreed());
			System.out.println("$#10583#"); target.setConfirmedAddress(source.isConfirmedAddress());
			System.out.println("$#10584#"); if(source.getPreviousOrderStatus()!=null) {
				List<OrderStatus> orderStatusList = source.getPreviousOrderStatus();
				for(OrderStatus status : orderStatusList) {
					OrderStatusHistory statusHistory = new OrderStatusHistory();
					System.out.println("$#10585#"); statusHistory.setStatus(status);
					System.out.println("$#10586#"); statusHistory.setOrder(target);
					target.getOrderHistory().add(statusHistory);
				}
			}
			
			System.out.println("$#10587#"); if(!StringUtils.isBlank(source.getComments())) {
				OrderStatusHistory statusHistory = new OrderStatusHistory();
				System.out.println("$#10588#"); statusHistory.setStatus(null);
				System.out.println("$#10589#"); statusHistory.setOrder(target);
				System.out.println("$#10590#"); statusHistory.setComments(source.getComments());
				target.getOrderHistory().add(statusHistory);
			}
			
			List<PersistableOrderProduct> products = source.getOrderProductItems();
			System.out.println("$#10591#"); if(CollectionUtils.isEmpty(products)) {
				throw new ConversionException("Requires at least 1 PersistableOrderProduct");
			}
			com.salesmanager.shop.populator.order.PersistableOrderProductPopulator orderProductPopulator = new PersistableOrderProductPopulator();
			System.out.println("$#10592#"); orderProductPopulator.setProductAttributeService(productAttributeService);
			System.out.println("$#10593#"); orderProductPopulator.setProductService(productService);
			System.out.println("$#10594#"); orderProductPopulator.setDigitalProductService(digitalProductService);
			
			for(PersistableOrderProduct orderProduct : products) {
				OrderProduct modelOrderProduct = new OrderProduct();
				orderProductPopulator.populate(orderProduct, modelOrderProduct, store, language);
				target.getOrderProducts().add(modelOrderProduct);
			}
			
			List<OrderTotal> orderTotals = source.getTotals();
			System.out.println("$#10595#"); if(CollectionUtils.isNotEmpty(orderTotals)) {
				for(OrderTotal total : orderTotals) {
					com.salesmanager.core.model.order.OrderTotal totalModel = new com.salesmanager.core.model.order.OrderTotal();
					System.out.println("$#10596#"); totalModel.setModule(total.getModule());
					System.out.println("$#10597#"); totalModel.setOrder(target);
					System.out.println("$#10598#"); totalModel.setOrderTotalCode(total.getCode());
					System.out.println("$#10599#"); totalModel.setTitle(total.getTitle());
					System.out.println("$#10600#"); totalModel.setValue(total.getValue());
					target.getOrderTotal().add(totalModel);
				}
			}
			
		} catch (Exception e) {
			throw new ConversionException(e);
		}
		
		
		System.out.println("$#10601#"); return target;
	}

	@Override
	protected Order createTarget() {
		return null;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public ProductService getProductService() {
		System.out.println("$#10602#"); return productService;
	}

	public void setDigitalProductService(DigitalProductService digitalProductService) {
		this.digitalProductService = digitalProductService;
	}

	public DigitalProductService getDigitalProductService() {
		System.out.println("$#10603#"); return digitalProductService;
	}

	public void setProductAttributeService(ProductAttributeService productAttributeService) {
		this.productAttributeService = productAttributeService;
	}

	public ProductAttributeService getProductAttributeService() {
		System.out.println("$#10604#"); return productAttributeService;
	}
	
	public CustomerService getCustomerService() {
		System.out.println("$#10605#"); return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public CountryService getCountryService() {
		System.out.println("$#10606#"); return countryService;
	}

	public void setCountryService(CountryService countryService) {
		this.countryService = countryService;
	}

	public CurrencyService getCurrencyService() {
		System.out.println("$#10607#"); return currencyService;
	}

	public void setCurrencyService(CurrencyService currencyService) {
		this.currencyService = currencyService;
	}

	public ZoneService getZoneService() {
		System.out.println("$#10608#"); return zoneService;
	}

	public void setZoneService(ZoneService zoneService) {
		this.zoneService = zoneService;
	}

}
