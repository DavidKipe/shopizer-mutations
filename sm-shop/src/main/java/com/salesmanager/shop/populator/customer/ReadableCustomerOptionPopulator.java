package com.salesmanager.shop.populator.customer;

import java.util.ArrayList;
import java.util.List;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.customer.attribute.CustomerOptionSet;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.admin.model.customer.attribute.CustomerOption;
import com.salesmanager.shop.admin.model.customer.attribute.CustomerOptionValue;



public class ReadableCustomerOptionPopulator extends
		AbstractDataPopulator<com.salesmanager.core.model.customer.attribute.CustomerOption, com.salesmanager.shop.admin.model.customer.attribute.CustomerOption> {

	
	private CustomerOptionSet optionSet;
	
	public CustomerOptionSet getOptionSet() {
		System.out.println("$#10327#"); return optionSet;
	}

	public void setOptionSet(CustomerOptionSet optionSet) {
		this.optionSet = optionSet;
	}
	

	@Override
	public CustomerOption populate(
			com.salesmanager.core.model.customer.attribute.CustomerOption source,
			CustomerOption target, MerchantStore store, Language language) throws ConversionException {
		
		
		CustomerOption customerOption = target;
		System.out.println("$#10328#"); if(customerOption==null) {
			customerOption = new CustomerOption();
		} 
		
		System.out.println("$#10329#"); customerOption.setId(source.getId());
		System.out.println("$#10330#"); customerOption.setType(source.getCustomerOptionType());
		System.out.println("$#10331#"); customerOption.setName(source.getDescriptionsSettoList().get(0).getName());

		List<CustomerOptionValue> values = customerOption.getAvailableValues();
		System.out.println("$#10332#"); if(values==null) {
			values = new ArrayList<CustomerOptionValue>();
			System.out.println("$#10333#"); customerOption.setAvailableValues(values);
		}
		
		com.salesmanager.core.model.customer.attribute.CustomerOptionValue optionValue = optionSet.getCustomerOptionValue();
		CustomerOptionValue custOptValue = new CustomerOptionValue();
		System.out.println("$#10334#"); custOptValue.setId(optionValue.getId());
		System.out.println("$#10335#"); custOptValue.setLanguage(language.getCode());
		System.out.println("$#10336#"); custOptValue.setName(optionValue.getDescriptionsSettoList().get(0).getName());
		values.add(custOptValue);
		
		System.out.println("$#10337#"); return customerOption;

	}

    @Override
    protected CustomerOption createTarget()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
