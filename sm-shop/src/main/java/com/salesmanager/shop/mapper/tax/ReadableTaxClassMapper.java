package com.salesmanager.shop.mapper.tax;

import org.springframework.stereotype.Component;

import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.tax.taxclass.TaxClass;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.tax.ReadableTaxClass;

@Component
public class ReadableTaxClassMapper implements Mapper<TaxClass, ReadableTaxClass> {

	@Override
	public ReadableTaxClass convert(TaxClass source, MerchantStore store, Language language) {
		ReadableTaxClass taxClass = new ReadableTaxClass();
		System.out.println("$#8682#"); taxClass.setId(source.getId());
		System.out.println("$#8683#"); taxClass.setCode(source.getCode());
		System.out.println("$#8684#"); taxClass.setName(source.getTitle());
		System.out.println("$#8685#"); taxClass.setStore(store.getCode());
		System.out.println("$#8686#"); return taxClass;
	}

	@Override
	public ReadableTaxClass convert(TaxClass source, ReadableTaxClass destination, MerchantStore store,
			Language language) {
		System.out.println("$#8687#"); destination.setId(source.getId());
		System.out.println("$#8688#"); destination.setCode(source.getCode());
		System.out.println("$#8689#"); destination.setName(source.getTitle());
		System.out.println("$#8690#"); destination.setStore(store.getCode());
		System.out.println("$#8691#"); return destination;
	}

}
