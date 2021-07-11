package com.salesmanager.shop.populator.order;

import org.apache.commons.lang.Validate;

import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shipping.ShippingSummary;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.shop.model.customer.ReadableDelivery;
import com.salesmanager.shop.model.order.shipping.ReadableShippingSummary;

public class ReadableShippingSummaryPopulator extends
		AbstractDataPopulator<ShippingSummary, ReadableShippingSummary> {
	
	private PricingService pricingService;

	@Override
	public ReadableShippingSummary populate(ShippingSummary source,
			ReadableShippingSummary target, MerchantStore store,
			Language language) throws ConversionException {
		
		System.out.println("$#10779#"); Validate.notNull(pricingService,"PricingService must be set");
		System.out.println("$#10780#"); Validate.notNull(source,"ShippingSummary cannot be null");
	
		try {
			
			System.out.println("$#10781#"); target.setShippingQuote(source.isShippingQuote());
			System.out.println("$#10782#"); target.setFreeShipping(source.isFreeShipping());
			System.out.println("$#10783#"); target.setHandling(source.getHandling());
			System.out.println("$#10784#"); target.setShipping(source.getShipping());
			System.out.println("$#10785#"); target.setShippingModule(source.getShippingModule());
			System.out.println("$#10786#"); target.setShippingOption(source.getShippingOption());
			System.out.println("$#10787#"); target.setTaxOnShipping(source.isTaxOnShipping());
			System.out.println("$#10788#"); target.setHandlingText(pricingService.getDisplayAmount(source.getHandling(), store));
			System.out.println("$#10789#"); target.setShippingText(pricingService.getDisplayAmount(source.getShipping(), store));
			
			System.out.println("$#10790#"); if(source.getDeliveryAddress()!=null) {
			
				ReadableDelivery deliveryAddress = new ReadableDelivery();
				System.out.println("$#10791#"); deliveryAddress.setAddress(source.getDeliveryAddress().getAddress());
				System.out.println("$#10792#"); deliveryAddress.setPostalCode(source.getDeliveryAddress().getPostalCode());
				System.out.println("$#10793#"); deliveryAddress.setCity(source.getDeliveryAddress().getCity());
				System.out.println("$#10794#"); if(source.getDeliveryAddress().getZone()!=null) {
					System.out.println("$#10795#"); deliveryAddress.setZone(source.getDeliveryAddress().getZone().getCode());
				}
				System.out.println("$#10796#"); if(source.getDeliveryAddress().getCountry()!=null) {
					System.out.println("$#10797#"); deliveryAddress.setCountry(source.getDeliveryAddress().getCountry().getIsoCode());
				}
				System.out.println("$#10798#"); deliveryAddress.setLatitude(source.getDeliveryAddress().getLatitude());
				System.out.println("$#10799#"); deliveryAddress.setLongitude(source.getDeliveryAddress().getLongitude());
				System.out.println("$#10800#"); deliveryAddress.setStateProvince(source.getDeliveryAddress().getState());
				
				System.out.println("$#10801#"); target.setDelivery(deliveryAddress);
			}

			
		} catch(Exception e) {
			throw new ConversionException(e);
		}
		
		System.out.println("$#10802#"); return target;
		
		
	}

	@Override
	protected ReadableShippingSummary createTarget() {
		System.out.println("$#10803#"); return new
				ReadableShippingSummary();
	}

	public PricingService getPricingService() {
		System.out.println("$#10804#"); return pricingService;
	}

	public void setPricingService(PricingService pricingService) {
		this.pricingService = pricingService;
	}

}
