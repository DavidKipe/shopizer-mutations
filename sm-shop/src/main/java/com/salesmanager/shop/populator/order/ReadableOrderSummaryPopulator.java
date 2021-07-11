package com.salesmanager.shop.populator.order;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.OrderTotal;
import com.salesmanager.core.model.order.OrderTotalSummary;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.order.ReadableOrderTotalSummary;
import com.salesmanager.shop.model.order.total.ReadableOrderTotal;
import com.salesmanager.shop.utils.LabelUtils;

public class ReadableOrderSummaryPopulator extends AbstractDataPopulator<OrderTotalSummary, ReadableOrderTotalSummary> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ReadableOrderSummaryPopulator.class);
	
	private PricingService pricingService;
	
	private LabelUtils messages;
	


	@Override
	public ReadableOrderTotalSummary populate(OrderTotalSummary source, ReadableOrderTotalSummary target,
			MerchantStore store, Language language) throws ConversionException {
		
		Validate.notNull(pricingService,"PricingService must be set");
		Validate.notNull(messages,"LabelUtils must be set");
		
		System.out.println("$#10749#"); if(target==null) {
			target = new ReadableOrderTotalSummary();
		}
		
		try {
		
			System.out.println("$#10750#"); if(source.getSubTotal() != null) {
				System.out.println("$#10751#"); target.setSubTotal(pricingService.getDisplayAmount(source.getSubTotal(), store));
			}
			System.out.println("$#10752#"); if(source.getTaxTotal()!=null) {
				System.out.println("$#10753#"); target.setTaxTotal(pricingService.getDisplayAmount(source.getTaxTotal(), store));
			}
			System.out.println("$#10754#"); if(source.getTotal() != null) {
				System.out.println("$#10755#"); target.setTotal(pricingService.getDisplayAmount(source.getTotal(), store));
			}
			
			System.out.println("$#10756#"); if(!CollectionUtils.isEmpty(source.getTotals())) {
				ReadableOrderTotalPopulator orderTotalPopulator = new ReadableOrderTotalPopulator();
				System.out.println("$#10757#"); orderTotalPopulator.setMessages(messages);
				System.out.println("$#10758#"); orderTotalPopulator.setPricingService(pricingService);
				for(OrderTotal orderTotal : source.getTotals()) {
					ReadableOrderTotal t = new ReadableOrderTotal();
					orderTotalPopulator.populate(orderTotal, t, store, language);
					target.getTotals().add(t);
				}
			}
			
		
		} catch(Exception e) {
			LOGGER.error("Error during amount formatting " + e.getMessage());
			throw new ConversionException(e);
		}
		
		System.out.println("$#10759#"); return target;
		
	}

	@Override
	protected ReadableOrderTotalSummary createTarget() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public PricingService getPricingService() {
		System.out.println("$#10760#"); return pricingService;
	}

	public void setPricingService(PricingService pricingService) {
		this.pricingService = pricingService;
	}
	
	public LabelUtils getMessages() {
		System.out.println("$#10761#"); return messages;
	}

	public void setMessages(LabelUtils messages) {
		this.messages = messages;
	}

}
