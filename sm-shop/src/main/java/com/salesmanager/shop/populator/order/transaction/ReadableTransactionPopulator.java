package com.salesmanager.shop.populator.order.transaction;

import org.apache.commons.lang.Validate;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.services.order.OrderService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.payments.Transaction;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.order.transaction.ReadableTransaction;
import com.salesmanager.shop.utils.DateUtil;


public class ReadableTransactionPopulator extends AbstractDataPopulator<Transaction, ReadableTransaction> {

	
	private OrderService orderService;
	private PricingService pricingService;
	
	@Override
	public ReadableTransaction populate(Transaction source, ReadableTransaction target, MerchantStore store,
			Language language) throws ConversionException {

		
		System.out.println("$#10864#"); Validate.notNull(source,"PersistableTransaction must not be null");
		System.out.println("$#10865#"); Validate.notNull(orderService,"OrderService must not be null");
		System.out.println("$#10866#"); Validate.notNull(pricingService,"OrderService must not be null");
		
		System.out.println("$#10867#"); if(target == null) {
			target = new ReadableTransaction();
		}
		
		
		try {
			

			System.out.println("$#10868#"); target.setAmount(pricingService.getDisplayAmount(source.getAmount(), store));
			System.out.println("$#10869#"); target.setDetails(source.getDetails());
			System.out.println("$#10870#"); target.setPaymentType(source.getPaymentType());
			System.out.println("$#10871#"); target.setTransactionType(source.getTransactionType());
			System.out.println("$#10872#"); target.setTransactionDate(DateUtil.formatDate(source.getTransactionDate()));
			System.out.println("$#10873#"); target.setId(source.getId());
			
			System.out.println("$#10874#"); if(source.getOrder() != null) {
				System.out.println("$#10875#"); target.setOrderId(source.getOrder().getId());

			}
			
			System.out.println("$#10876#"); return target;
			
			
		
		} catch(Exception e) {
			throw new ConversionException(e);
		}
		
	}

	@Override
	protected ReadableTransaction createTarget() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public OrderService getOrderService() {
		System.out.println("$#10877#"); return orderService;
	}

	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

	public PricingService getPricingService() {
		System.out.println("$#10878#"); return pricingService;
	}

	public void setPricingService(PricingService pricingService) {
		this.pricingService = pricingService;
	}

}
