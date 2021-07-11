package com.salesmanager.shop.populator.customer;


import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.customer.attribute.CustomerOptionSet;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.admin.model.customer.attribute.CustomerOption;
import com.salesmanager.shop.admin.model.customer.attribute.CustomerOptionValue;


import java.util.ArrayList;
import java.util.List;



/**
 * Used in the admin section
 * @author c.samson
 *
 */

public class CustomerOptionPopulator extends
		AbstractDataPopulator<com.salesmanager.core.model.customer.attribute.CustomerOption, com.salesmanager.shop.admin.model.customer.attribute.CustomerOption> {

	
	private CustomerOptionSet optionSet;
	
	public CustomerOptionSet getOptionSet() {
		System.out.println("$#10049#"); return optionSet;
	}

	public void setOptionSet(CustomerOptionSet optionSet) {
		this.optionSet = optionSet;
	}

	@Override
	public CustomerOption populate(
			com.salesmanager.core.model.customer.attribute.CustomerOption source,
			CustomerOption target, MerchantStore store, Language language) throws ConversionException {
		
		
		CustomerOption customerOption = target;
		System.out.println("$#10050#"); if(customerOption==null) {
			customerOption = new CustomerOption();
		} 
		
		System.out.println("$#10051#"); customerOption.setId(source.getId());
		System.out.println("$#10052#"); customerOption.setType(source.getCustomerOptionType());
		System.out.println("$#10053#"); customerOption.setName(source.getDescriptionsSettoList().get(0).getName());

		List<CustomerOptionValue> values = customerOption.getAvailableValues();
		System.out.println("$#10054#"); if(values==null) {
			values = new ArrayList<CustomerOptionValue>();
			System.out.println("$#10055#"); customerOption.setAvailableValues(values);
		}
		
		com.salesmanager.core.model.customer.attribute.CustomerOptionValue optionValue = optionSet.getCustomerOptionValue();
		CustomerOptionValue custOptValue = new CustomerOptionValue();
		System.out.println("$#10056#"); custOptValue.setId(optionValue.getId());
		System.out.println("$#10057#"); custOptValue.setLanguage(language.getCode());
		System.out.println("$#10058#"); custOptValue.setName(optionValue.getDescriptionsSettoList().get(0).getName());
		values.add(custOptValue);
		
		System.out.println("$#10059#"); return customerOption;

	}

    @Override
    protected CustomerOption createTarget()
    {
        // TODO Auto-generated method stub
        return null;
    }


}
