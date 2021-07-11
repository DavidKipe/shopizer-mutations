package com.salesmanager.shop.mapper.catalog;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.catalog.product.attribute.api.ReadableProductAttributeEntity;
import com.salesmanager.shop.model.catalog.product.attribute.api.ReadableProductOptionEntity;
import com.salesmanager.shop.model.catalog.product.attribute.api.ReadableProductOptionValueEntity;
import com.salesmanager.shop.store.api.exception.ConversionRuntimeException;

@Component
public class ReadableProductAttributeMapper implements Mapper<ProductAttribute, ReadableProductAttributeEntity> {

	@Autowired
	private ReadableProductOptionMapper readableProductOptionMapper;
	
	@Autowired
	private ReadableProductOptionValueMapper readableProductOptionValueMapper;

	@Autowired
	private PricingService pricingService;
	

	@Override
	public ReadableProductAttributeEntity convert(ProductAttribute source, MerchantStore store, Language language) {
		ReadableProductAttributeEntity productAttribute = new ReadableProductAttributeEntity();
		System.out.println("$#8434#"); return convert(source, productAttribute, store, language);
	}

	@Override
	public ReadableProductAttributeEntity convert(ProductAttribute source, ReadableProductAttributeEntity destination,
			MerchantStore store, Language language) {

		ReadableProductAttributeEntity attr = new ReadableProductAttributeEntity();
		System.out.println("$#8435#"); if(destination !=null) {
			attr = destination;
		}
		try {
			System.out.println("$#8436#"); attr.setId(source.getId());//attribute of the option
	
			System.out.println("$#8438#"); System.out.println("$#8437#"); if(source.getProductAttributePrice()!=null && source.getProductAttributePrice().doubleValue()>0) {
				String formatedPrice;
				formatedPrice = pricingService.getDisplayAmount(source.getProductAttributePrice(), store);
				System.out.println("$#8440#"); attr.setProductAttributePrice(formatedPrice);
			}
			
			System.out.println("$#8441#"); attr.setProductAttributeWeight(source.getAttributeAdditionalWeight());
			System.out.println("$#8442#"); attr.setAttributeDisplayOnly(source.getAttributeDisplayOnly());
			System.out.println("$#8443#"); attr.setAttributeDefault(source.getAttributeDefault());
			System.out.println("$#8444#"); if(!StringUtils.isBlank(source.getAttributeSortOrder())) {
				System.out.println("$#8445#"); attr.setSortOrder(Integer.parseInt(source.getAttributeSortOrder()));
			}
			
			System.out.println("$#8446#"); if(source.getProductOption()!=null) {
				ReadableProductOptionEntity option = readableProductOptionMapper.convert(source.getProductOption(), store, language);
				System.out.println("$#8447#"); attr.setOption(option);
			}
			
			System.out.println("$#8448#"); if(source.getProductOptionValue()!=null) {
				ReadableProductOptionValueEntity optionValue = readableProductOptionValueMapper.convert(source.getProductOptionValue(), store, language);
				System.out.println("$#8449#"); attr.setOptionValue(optionValue);
			}
		
		} catch (Exception e) {
			throw new ConversionRuntimeException("Exception while product attribute conversion",e);
		}
		
		
		System.out.println("$#8450#"); return attr;
	}

}
