package com.salesmanager.core.business.services.shipping;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.shipping.ShippingQuoteRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.shipping.Quote;
import com.salesmanager.core.model.shipping.ShippingSummary;

@Service("shippingQuoteService")
public class ShippingQuoteServiceImpl extends SalesManagerEntityServiceImpl<Long, Quote> implements ShippingQuoteService {

	
	private static final Logger LOGGER = LoggerFactory.getLogger(ShippingQuoteServiceImpl.class);
	
	private ShippingQuoteRepository shippingQuoteRepository;
	
	@Inject
	private ShippingService shippingService;
	
	@Inject
	public ShippingQuoteServiceImpl(ShippingQuoteRepository repository) {
		super(repository);
		this.shippingQuoteRepository = repository;
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Quote> findByOrder(Order order) throws ServiceException {
		System.out.println("$#2891#"); Validate.notNull(order,"Order cannot be null");
		System.out.println("$#2892#"); return this.shippingQuoteRepository.findByOrder(order.getId());
	}

	@Override
	public ShippingSummary getShippingSummary(Long quoteId, MerchantStore store) throws ServiceException {
		
		System.out.println("$#2893#"); Validate.notNull(quoteId,"quoteId must not be null");
		
		Quote q = shippingQuoteRepository.getOne(quoteId);

		
		ShippingSummary quote = null;
		
		System.out.println("$#2894#"); if(q != null) {
			
			quote = new ShippingSummary();
			System.out.println("$#2895#"); quote.setDeliveryAddress(q.getDelivery());
			System.out.println("$#2896#"); quote.setShipping(q.getPrice());
			System.out.println("$#2897#"); quote.setShippingModule(q.getModule());
			System.out.println("$#2898#"); quote.setShippingOption(q.getOptionName());
			System.out.println("$#2899#"); quote.setShippingOptionCode(q.getOptionCode());
			System.out.println("$#2900#"); quote.setHandling(q.getHandling());
			
			System.out.println("$#2901#"); if(shippingService.hasTaxOnShipping(store)) {
				System.out.println("$#2902#"); quote.setTaxOnShipping(true);
			}
			
			
			
		}
		
		
		System.out.println("$#2903#"); return quote;
		
	}


}
