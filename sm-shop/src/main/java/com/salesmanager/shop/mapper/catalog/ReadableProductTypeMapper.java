package com.salesmanager.shop.mapper.catalog;

import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;

import com.salesmanager.core.model.catalog.product.type.ProductType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.catalog.product.type.ReadableProductType;

@Component
public class ReadableProductTypeMapper implements Mapper<ProductType, ReadableProductType> {

	@Override
	public ReadableProductType convert(ProductType source, MerchantStore store, Language language) {
		ReadableProductType type = new ReadableProductType();
		System.out.println("$#8524#"); return this.convert(source, type, store, language);
	}

	@Override
	public ReadableProductType convert(ProductType source, ReadableProductType destination, MerchantStore store,
			Language language) {
		Validate.notNull(source, "ProductType cannot be null");
		Validate.notNull(destination, "ReadableProductType cannot be null");
		System.out.println("$#8525#"); destination.setId(source.getId());
		System.out.println("$#8526#"); destination.setCode(source.getCode());
		System.out.println("$#8527#"); destination.setName(source.getCode());
		System.out.println("$#8528#"); destination.setAllowAddToCart(source.isAllowAddToCart());
		System.out.println("$#8529#"); return destination;
	}

}
