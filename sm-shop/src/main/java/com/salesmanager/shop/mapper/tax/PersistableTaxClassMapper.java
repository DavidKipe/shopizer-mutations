package com.salesmanager.shop.mapper.tax;

import org.apache.commons.lang.Validate;
import org.springframework.stereotype.Component;

import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.tax.taxclass.TaxClass;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.tax.PersistableTaxClass;

@Component
public class PersistableTaxClassMapper implements Mapper<PersistableTaxClass, TaxClass> {

	@Override
	public TaxClass convert(PersistableTaxClass source, MerchantStore store, Language language) {
		System.out.println("$#8632#"); Validate.notNull(source, "PersistableTaxClass cannot be null");
		System.out.println("$#8633#"); Validate.notNull(store, "MerchantStore cannot be null");
		TaxClass taxClass = new TaxClass();
		System.out.println("$#8634#"); taxClass.setMerchantStore(store);
		System.out.println("$#8635#"); taxClass.setTitle(source.getName());
		System.out.println("$#8636#"); taxClass.setId(source.getId());
		System.out.println("$#8637#"); return this.convert(source, taxClass, store, language);
	}

	@Override
	public TaxClass convert(PersistableTaxClass source, TaxClass destination, MerchantStore store, Language language) {
		System.out.println("$#8638#"); Validate.notNull(source, "PersistableTaxClass cannot be null");
		System.out.println("$#8639#"); Validate.notNull(destination, "TaxClass cannot be null");
		System.out.println("$#8640#"); Validate.notNull(store, "MerchantStore cannot be null");
		
		System.out.println("$#8641#"); destination.setCode(source.getCode());
		System.out.println("$#8643#"); System.out.println("$#8642#"); if(source.getId()!=null && source.getId().longValue() > 0) {
			System.out.println("$#8645#"); destination.setId(source.getId());
		}
		System.out.println("$#8646#"); destination.setMerchantStore(store);
		System.out.println("$#8647#"); destination.setTitle(source.getName());
		
		System.out.println("$#8648#"); return destination;
	}

}
