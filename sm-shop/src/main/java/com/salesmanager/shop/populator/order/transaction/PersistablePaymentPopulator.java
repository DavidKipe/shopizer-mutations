package com.salesmanager.shop.populator.order.transaction;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.payments.Payment;
import com.salesmanager.core.model.payments.PaymentType;
import com.salesmanager.core.model.payments.TransactionType;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.order.transaction.PersistablePayment;

public class PersistablePaymentPopulator extends AbstractDataPopulator<PersistablePayment, Payment> {
	
	
	PricingService pricingService;



	@Override
	public Payment populate(PersistablePayment source, Payment target, MerchantStore store, Language language)
			throws ConversionException {
		
		Validate.notNull(source,"PersistablePayment cannot be null");
		Validate.notNull(pricingService,"pricingService must be set");
		System.out.println("$#10839#"); if(target == null) {
			target = new Payment();
		}
		
		try {
		
			System.out.println("$#10840#"); target.setAmount(pricingService.getAmount(source.getAmount()));
			System.out.println("$#10841#"); target.setModuleName(source.getPaymentModule());
			System.out.println("$#10842#"); target.setPaymentType(PaymentType.valueOf(source.getPaymentType()));
			System.out.println("$#10843#"); target.setTransactionType(TransactionType.valueOf(source.getTransactionType()));
			
			Map<String,String> metadata = new HashMap<String,String>();
			metadata.put("paymentToken", source.getPaymentToken());
			System.out.println("$#10844#"); target.setPaymentMetaData(metadata);
			
			System.out.println("$#10845#"); return target;
		
		} catch(Exception e) {
			throw new ConversionException(e);
		}
	}

	@Override
	protected Payment createTarget() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public PricingService getPricingService() {
		System.out.println("$#10846#"); return pricingService;
	}

	public void setPricingService(PricingService pricingService) {
		this.pricingService = pricingService;
	}

}
