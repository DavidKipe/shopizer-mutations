package com.salesmanager.shop.mapper.catalog;

import javax.inject.Inject;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductOptionService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductOptionValueService;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.catalog.product.attribute.ProductOption;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValue;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.catalog.product.attribute.PersistableProductAttribute;
import com.salesmanager.shop.store.api.exception.ConversionRuntimeException;

@Component
public class PersistableProductAttributeMapper implements Mapper<PersistableProductAttribute, ProductAttribute> {

	@Inject
	private ProductOptionService productOptionService;
	@Inject
	private ProductOptionValueService productOptionValueService;
	@Inject
	private ProductService productService;
	
	@Override
	public ProductAttribute convert(PersistableProductAttribute source, MerchantStore store, Language language) {
		ProductAttribute attribute = new ProductAttribute();
		System.out.println("$#8248#"); return convert(source,attribute,store,language);
	}

	@Override
	public ProductAttribute convert(PersistableProductAttribute source, ProductAttribute destination,
			MerchantStore store, Language language) {

		
		ProductOption productOption = null;
		
		System.out.println("$#8249#"); if(!StringUtils.isBlank(source.getOption().getCode())) {
			productOption = productOptionService.getByCode(store, source.getOption().getCode());
		} else {
			System.out.println("$#8250#"); Validate.notNull(source.getOption().getId(),"Product option id is null");
			productOption = productOptionService.getById(source.getOption().getId());
		}

		System.out.println("$#8251#"); if(productOption==null) {
			throw new ConversionRuntimeException("Product option id " + source.getOption().getId() + " does not exist");
		}
		
		ProductOptionValue productOptionValue = null;
		
		System.out.println("$#8252#"); if(!StringUtils.isBlank(source.getOptionValue().getCode())) {
			productOptionValue = productOptionValueService.getByCode(store, source.getOptionValue().getCode());
		} else {
			productOptionValue = productOptionValueService.getById(source.getOptionValue().getId());
		}
		
		System.out.println("$#8253#"); if(productOptionValue==null) {
			throw new ConversionRuntimeException("Product option value id " + source.getOptionValue().getId() + " does not exist");
		}
		
		System.out.println("$#8254#"); if(productOption.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
			throw new ConversionRuntimeException("Invalid product option id ");
		}
		
		System.out.println("$#8255#"); if(productOptionValue.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
			throw new ConversionRuntimeException("Invalid product option value id ");
		}
		
		System.out.println("$#8257#"); System.out.println("$#8256#"); if(source.getProductId() != null && source.getProductId().longValue() >0 ) {
			Product p = productService.getById(source.getProductId());
			System.out.println("$#8259#"); if(p == null) {
				throw new ConversionRuntimeException("Invalid product id ");
			}
			System.out.println("$#8260#"); destination.setProduct(p);
		}

		
		System.out.println("$#8262#"); System.out.println("$#8261#"); if(destination.getId()!=null && destination.getId().longValue()>0) {
			System.out.println("$#8264#"); destination.setId(destination.getId());
		} else {
			System.out.println("$#8265#"); destination.setId(null);
		}
		System.out.println("$#8266#"); destination.setProductOption(productOption);
		System.out.println("$#8267#"); destination.setProductOptionValue(productOptionValue);
		System.out.println("$#8268#"); destination.setProductAttributePrice(source.getProductAttributePrice());
		System.out.println("$#8269#"); destination.setProductAttributeWeight(source.getProductAttributeWeight());
		System.out.println("$#8270#"); destination.setProductAttributePrice(source.getProductAttributePrice());
		System.out.println("$#8271#"); destination.setAttributeDisplayOnly(source.isAttributeDisplayOnly());

		
		System.out.println("$#8272#"); return destination;
	}

}
