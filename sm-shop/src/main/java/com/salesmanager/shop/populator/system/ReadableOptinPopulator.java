package com.salesmanager.shop.populator.system;

import org.apache.commons.lang.Validate;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.system.optin.Optin;
import com.salesmanager.shop.model.system.ReadableOptin;

public class ReadableOptinPopulator extends AbstractDataPopulator<Optin, ReadableOptin> {

	@Override
	public ReadableOptin populate(Optin source, ReadableOptin target, MerchantStore store, Language language)
			throws ConversionException {
		System.out.println("$#11156#"); Validate.notNull(store,"MerchantStore cannot be null");
		System.out.println("$#11157#"); Validate.notNull(source,"Optin cannot be null");
		
		System.out.println("$#11158#"); if(target==null) {
			target = new ReadableOptin();
		}
		
		System.out.println("$#11159#"); target.setCode(source.getCode());
		System.out.println("$#11160#"); target.setDescription(source.getDescription());
		System.out.println("$#11161#"); target.setEndDate(source.getEndDate());
		System.out.println("$#11162#"); target.setId(source.getId());
		System.out.println("$#11163#"); target.setOptinType(source.getOptinType().name());
		System.out.println("$#11164#"); target.setStartDate(source.getStartDate());
		System.out.println("$#11165#"); target.setStore(store.getCode());

		System.out.println("$#11166#"); return target;
	}

	@Override
	protected ReadableOptin createTarget() {
		// TODO Auto-generated method stub
		return null;
	}

}
