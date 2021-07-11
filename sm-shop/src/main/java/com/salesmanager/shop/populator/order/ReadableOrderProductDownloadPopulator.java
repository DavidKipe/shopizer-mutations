package com.salesmanager.shop.populator.order;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.orderproduct.OrderProductDownload;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.shop.model.order.ReadableOrderProductDownload;

public class ReadableOrderProductDownloadPopulator extends
		AbstractDataPopulator<OrderProductDownload, ReadableOrderProductDownload> {

	@Override
	public ReadableOrderProductDownload populate(OrderProductDownload source,
			ReadableOrderProductDownload target, MerchantStore store,
			Language language) throws ConversionException {
		try {
			
			System.out.println("$#10716#"); target.setProductName(source.getOrderProduct().getProductName());
			System.out.println("$#10717#"); target.setDownloadCount(source.getDownloadCount());
			System.out.println("$#10718#"); target.setDownloadExpiryDays(source.getMaxdays());
			System.out.println("$#10719#"); target.setId(source.getId());
			System.out.println("$#10720#"); target.setFileName(source.getOrderProductFilename());
			System.out.println("$#10721#"); target.setOrderId(source.getOrderProduct().getOrder().getId());
			
			System.out.println("$#10722#"); return target;
			
		} catch(Exception e) {
			throw new ConversionException(e);
		}
	}

	@Override
	protected ReadableOrderProductDownload createTarget() {
		System.out.println("$#10723#"); return new ReadableOrderProductDownload();
	}
	

}
