package com.salesmanager.shop.populator.catalog;

import org.apache.commons.lang.Validate;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.catalog.product.price.FinalPrice;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.catalog.product.ReadableProductPrice;

public class ReadableFinalPricePopulator extends
		AbstractDataPopulator<FinalPrice, ReadableProductPrice> {
	
	
	private PricingService pricingService;

	public PricingService getPricingService() {
		System.out.println("$#9695#"); return pricingService;
	}

	public void setPricingService(PricingService pricingService) {
		this.pricingService = pricingService;
	}

	@Override
	public ReadableProductPrice populate(FinalPrice source,
			ReadableProductPrice target, MerchantStore store, Language language)
			throws ConversionException {
		System.out.println("$#9696#"); Validate.notNull(pricingService,"pricingService must be set");
		
		try {
			
			System.out.println("$#9697#"); target.setOriginalPrice(pricingService.getDisplayAmount(source.getOriginalPrice(), store));
			System.out.println("$#9698#"); if(source.isDiscounted()) {
				System.out.println("$#9699#"); target.setDiscounted(true);
				System.out.println("$#9700#"); target.setFinalPrice(pricingService.getDisplayAmount(source.getDiscountedPrice(), store));
			} else {
				System.out.println("$#9701#"); target.setFinalPrice(pricingService.getDisplayAmount(source.getFinalPrice(), store));
			}
			
		} catch(Exception e) {
			throw new ConversionException("Exception while converting to ReadableProductPrice",e);
		}
		
		
		
		System.out.println("$#9702#"); return target;
	}

	@Override
	protected ReadableProductPrice createTarget() {
		// TODO Auto-generated method stub
		return null;
	}

}
