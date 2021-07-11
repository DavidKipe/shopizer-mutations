package com.salesmanager.shop.populator.order;

import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.OrderTotal;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.order.total.ReadableOrderTotal;
import com.salesmanager.shop.utils.LabelUtils;
import com.salesmanager.shop.utils.LocaleUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

public class ReadableOrderTotalPopulator extends
		AbstractDataPopulator<OrderTotal, ReadableOrderTotal> {
	
	
	private PricingService pricingService;


	private LabelUtils messages;




	@Override
	public ReadableOrderTotal populate(OrderTotal source,
			ReadableOrderTotal target, MerchantStore store, Language language)
			throws ConversionException {
		
			System.out.println("$#10762#"); Validate.notNull(pricingService,"PricingService must be set");
			System.out.println("$#10763#"); Validate.notNull(messages,"LabelUtils must be set");
			
			Locale locale = LocaleUtils.getLocale(language);
		
			try {
				
				System.out.println("$#10764#"); target.setCode(source.getOrderTotalCode());
				System.out.println("$#10765#"); target.setId(source.getId());
				System.out.println("$#10766#"); target.setModule(source.getModule());
				System.out.println("$#10767#"); target.setOrder(source.getSortOrder());
				

				System.out.println("$#10768#"); target.setTitle(messages.getMessage(source.getOrderTotalCode(), locale, source.getOrderTotalCode()));
				System.out.println("$#10769#"); target.setText(source.getText());
				
				System.out.println("$#10770#"); target.setValue(source.getValue());
				System.out.println("$#10771#"); target.setTotal(pricingService.getDisplayAmount(source.getValue(), store));
				
				System.out.println("$#10772#"); if(!StringUtils.isBlank(source.getOrderTotalCode())) {
					System.out.println("$#10773#"); if(Constants.OT_DISCOUNT_TITLE.equals(source.getOrderTotalCode())) {
						System.out.println("$#10774#"); target.setDiscounted(true);
					}
				}
				
			} catch(Exception e) {
				throw new ConversionException(e);
			}
			
			System.out.println("$#10775#"); return target;
		
	}

	@Override
	protected ReadableOrderTotal createTarget() {
		System.out.println("$#10776#"); return new ReadableOrderTotal();
	}
	
	public PricingService getPricingService() {
		System.out.println("$#10777#"); return pricingService;
	}

	public void setPricingService(PricingService pricingService) {
		this.pricingService = pricingService;
	}
	
	public LabelUtils getMessages() {
		System.out.println("$#10778#"); return messages;
	}

	public void setMessages(LabelUtils messages) {
		this.messages = messages;
	}

}