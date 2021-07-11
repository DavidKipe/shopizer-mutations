package com.salesmanager.shop.mapper.catalog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.jsoup.helper.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.salesmanager.core.model.catalog.product.attribute.ProductOption;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionDescription;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionSet;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValue;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription;
import com.salesmanager.core.model.catalog.product.type.ProductType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.catalog.product.attribute.ReadableProductOption;
import com.salesmanager.shop.model.catalog.product.attribute.ReadableProductOptionValue;
import com.salesmanager.shop.model.catalog.product.attribute.optionset.ReadableProductOptionSet;
import com.salesmanager.shop.model.catalog.product.type.ReadableProductType;

@Component
public class ReadableProductOptionSetMapper implements Mapper<ProductOptionSet, ReadableProductOptionSet> {
 
	
	@Autowired
	private ReadableProductTypeMapper readableProductTypeMapper;
	
	@Override
	public ReadableProductOptionSet convert(ProductOptionSet source, MerchantStore store, Language language) {
		ReadableProductOptionSet optionSource = new ReadableProductOptionSet();
		System.out.println("$#8468#"); return convert(source, optionSource, store, language);
	}

	@Override
	public ReadableProductOptionSet convert(ProductOptionSet source, ReadableProductOptionSet destination,
			MerchantStore store, Language language) {
		System.out.println("$#8469#"); Validate.notNull(source,"ProductOptionSet must not be null");
		System.out.println("$#8470#"); Validate.notNull(destination,"ReadableProductOptionSet must not be null");
		
		
		System.out.println("$#8471#"); destination.setId(source.getId());
		System.out.println("$#8472#"); destination.setCode(source.getCode());
		System.out.println("$#8473#"); destination.setReadOnly(source.isOptionDisplayOnly());
		
		System.out.println("$#8474#"); destination.setOption(this.option(source.getOption(), store, language));
		
		List<Long> ids = new ArrayList<Long>();

		System.out.println("$#8475#"); if(!CollectionUtils.isEmpty(source.getValues())) {
			List<ReadableProductOptionValue> values = source.getValues().stream().map(val -> optionValue(ids, val, store, language)).collect(Collectors.toList());
			System.out.println("$#8477#"); destination.setValues(values);
			destination.getValues().removeAll(Collections.singleton(null));
		}
		
		System.out.println("$#8478#"); if(!CollectionUtils.isEmpty(source.getProductTypes())) {
			List<ReadableProductType> types = source.getProductTypes().stream().map( t -> this.productType(t, store, language)).collect(Collectors.toList());
			System.out.println("$#8480#"); destination.setProductTypes(types);
		}

		
		System.out.println("$#8481#"); return destination;
	}
	
	private ReadableProductOption option (ProductOption option, MerchantStore store, Language lang) {

		ReadableProductOption opt = new ReadableProductOption();
		System.out.println("$#8482#"); opt.setCode(option.getCode());
		System.out.println("$#8483#"); opt.setId(option.getId());
		System.out.println("$#8484#"); opt.setLang(lang.getCode());
		System.out.println("$#8485#"); opt.setReadOnly(option.isReadOnly());
		System.out.println("$#8486#"); opt.setType(option.getProductOptionType());
		ProductOptionDescription desc = this.optionDescription(option.getDescriptions(), lang);
		System.out.println("$#8487#"); if(desc != null) {
			System.out.println("$#8488#"); opt.setName(desc.getName());
		}

		System.out.println("$#8489#"); return opt;
	}
	
	private ReadableProductOptionValue optionValue (List<Long> ids, ProductOptionValue optionValue, MerchantStore store, Language language) {
		
		System.out.println("$#8490#"); if(!ids.contains(optionValue.getId())) {
			ReadableProductOptionValue value = new ReadableProductOptionValue();
			System.out.println("$#8491#"); value.setCode(optionValue.getCode());
			System.out.println("$#8492#"); value.setId(optionValue.getId());
			ProductOptionValueDescription desc = optionValueDescription(optionValue.getDescriptions(), language);
			System.out.println("$#8493#"); if(desc!=null) {
				System.out.println("$#8494#"); value.setName(desc.getName());
			}
			ids.add(optionValue.getId());
			System.out.println("$#8495#"); return value;
		} else {
			return null;
		}
	}
	
	private ProductOptionDescription optionDescription(Set<ProductOptionDescription> descriptions, Language lang) {
		System.out.println("$#8498#"); System.out.println("$#8497#"); System.out.println("$#8496#"); return descriptions.stream().filter(desc-> desc.getLanguage().getCode().equals(lang.getCode())).findAny().orElse(null);
	}
	
	private ProductOptionValueDescription optionValueDescription(Set<ProductOptionValueDescription> descriptions, Language lang) {
		System.out.println("$#8501#"); System.out.println("$#8500#"); System.out.println("$#8499#"); return descriptions.stream().filter(desc-> desc.getLanguage().getCode().equals(lang.getCode())).findAny().orElse(null);
	}
	
	private ReadableProductType productType(ProductType type, MerchantStore store, Language language) {
		System.out.println("$#8502#"); return readableProductTypeMapper.convert(type, store, language);
	}

}
