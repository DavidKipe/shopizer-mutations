package com.salesmanager.shop.populator.order.transaction;

import org.apache.commons.lang.Validate;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.services.order.OrderService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.payments.PaymentType;
import com.salesmanager.core.model.payments.Transaction;
import com.salesmanager.core.model.payments.TransactionType;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.order.transaction.PersistableTransaction;
import com.shopizer.search.utils.DateUtil;

public class PersistableTransactionPopulator extends AbstractDataPopulator<PersistableTransaction, Transaction> {

	private OrderService orderService;
	private PricingService pricingService;
	
	@Override
	public Transaction populate(PersistableTransaction source, Transaction target, MerchantStore store,
			Language language) throws ConversionException {
		
		System.out.println("$#10847#"); Validate.notNull(source,"PersistableTransaction must not be null");
		System.out.println("$#10848#"); Validate.notNull(orderService,"OrderService must not be null");
		System.out.println("$#10849#"); Validate.notNull(pricingService,"OrderService must not be null");
		
		System.out.println("$#10850#"); if(target == null) {
			target = new Transaction();
		}
		
		
		try {
			

			System.out.println("$#10851#"); target.setAmount(pricingService.getAmount(source.getAmount()));
			System.out.println("$#10852#"); target.setDetails(source.getDetails());
			System.out.println("$#10853#"); target.setPaymentType(PaymentType.valueOf(source.getPaymentType()));
			System.out.println("$#10854#"); target.setTransactionType(TransactionType.valueOf(source.getTransactionType()));
			System.out.println("$#10855#"); target.setTransactionDate(DateUtil.formatDate(source.getTransactionDate()));
			
			System.out.println("$#10857#"); System.out.println("$#10856#"); if(source.getOrderId()!=null && source.getOrderId().longValue() > 0) {
				Order order = orderService.getById(source.getOrderId());
/*				if(source.getCustomerId() == null) {
					throw new ConversionException("Cannot add a transaction for an Order without specyfing the customer");
				}*/
				
				System.out.println("$#10859#"); if(order == null) {
					throw new ConversionException("Order with id " + source.getOrderId() + "does not exist");
				}
				System.out.println("$#10860#"); target.setOrder(order);
			}
			
			System.out.println("$#10861#"); return target;
			
			
		
		} catch(Exception e) {
			throw new ConversionException(e);
		}

	}

	@Override
	protected Transaction createTarget() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public OrderService getOrderService() {
		System.out.println("$#10862#"); return orderService;
	}

	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

	public PricingService getPricingService() {
		System.out.println("$#10863#"); return pricingService;
	}

	public void setPricingService(PricingService pricingService) {
		this.pricingService = pricingService;
	}

}
