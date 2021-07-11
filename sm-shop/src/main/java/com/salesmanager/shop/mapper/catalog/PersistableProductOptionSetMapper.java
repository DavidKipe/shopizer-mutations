package com.salesmanager.shop.mapper.catalog;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductOptionService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductOptionValueService;
import com.salesmanager.core.business.services.catalog.product.type.ProductTypeService;
import com.salesmanager.core.model.catalog.product.attribute.ProductOption;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionSet;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValue;
import com.salesmanager.core.model.catalog.product.type.ProductType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.catalog.product.attribute.optionset.PersistableProductOptionSet;
import com.salesmanager.shop.store.api.exception.ConversionRuntimeException;

@Component
public class PersistableProductOptionSetMapper implements Mapper<PersistableProductOptionSet, ProductOptionSet> {

	@Autowired
	private ProductOptionService productOptionService;
	
	@Autowired
	private ProductOptionValueService productOptionValueService;
	
	@Autowired
	private ProductTypeService productTypeService;

	@Override
	public ProductOptionSet convert(PersistableProductOptionSet source, MerchantStore store, Language language) {
		
		
		ProductOptionSet optionSet = new ProductOptionSet();
		System.out.println("$#8300#"); return this.convert(source, optionSet, store, language);

	}

	private ProductOptionValue value(Long productOptionValue, MerchantStore store) {
		System.out.println("$#8301#"); return productOptionValueService.getById(store, productOptionValue);
	}
	
	@Override
	public ProductOptionSet convert(PersistableProductOptionSet source, ProductOptionSet destination,
			MerchantStore store, Language language) {
		System.out.println("$#8302#"); Validate.notNull(destination, "ProductOptionSet must not be null");
		
		System.out.println("$#8303#"); destination.setId(source.getId());
		System.out.println("$#8304#"); destination.setCode(source.getCode());
		System.out.println("$#8305#"); destination.setOptionDisplayOnly(source.isReadOnly());
		
		ProductOption option = productOptionService.getById(store, source.getOption());
		System.out.println("$#8306#"); destination.setOption(option);
		
		System.out.println("$#8307#"); if(!CollectionUtils.isEmpty(source.getOptionValues())) {
			List<ProductOptionValue> values = source.getOptionValues().stream().map(id -> value(id, store)).collect(Collectors.toList());
			System.out.println("$#8309#"); destination.setValues(values);
		}
		
		System.out.println("$#8310#"); if(!CollectionUtils.isEmpty(source.getProductTypes())) {
			try {
				List<ProductType> types = productTypeService.listProductTypes(source.getProductTypes(), store, language);
				Set<ProductType> typesSet = new HashSet<ProductType>(types);
				System.out.println("$#8311#"); destination.setProductTypes(typesSet);
			} catch (ServiceException e) {
				throw new ConversionRuntimeException("Error while mpping ProductOptionSet", e);
			}
		}
		
		System.out.println("$#8312#"); return destination;
	}

}
