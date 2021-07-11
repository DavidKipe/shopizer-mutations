/**
 * 
 */
package com.salesmanager.shop.populator.customer;


import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.customer.address.Address;

/**
 * @author csamson
 *
 */
public class CustomerBillingAddressPopulator extends AbstractDataPopulator<Customer, Address>
{

    @Override
    public Address populate( Customer source, Address target, MerchantStore store, Language language )
        throws ConversionException
    {
        
								System.out.println("$#9971#"); target.setCity(source.getBilling().getCity());
								System.out.println("$#9972#"); target.setCompany(source.getBilling().getCompany());
								System.out.println("$#9973#"); target.setFirstName(source.getBilling().getFirstName());
								System.out.println("$#9974#"); target.setLastName(source.getBilling().getLastName());
								System.out.println("$#9975#"); target.setPostalCode(source.getBilling().getPostalCode());
								System.out.println("$#9976#"); target.setPhone(source.getBilling().getTelephone());
								System.out.println("$#9977#"); if(source.getBilling().getTelephone()==null) {
												System.out.println("$#9978#"); target.setPhone(source.getBilling().getTelephone());
        }
								System.out.println("$#9979#"); target.setAddress(source.getBilling().getAddress());
								System.out.println("$#9980#"); if(source.getBilling().getCountry()!=null) {
												System.out.println("$#9981#"); target.setCountry(source.getBilling().getCountry().getIsoCode());
        }
								System.out.println("$#9982#"); if(source.getBilling().getZone()!=null) {
												System.out.println("$#9983#"); target.setZone(source.getBilling().getZone().getCode());
        }
								System.out.println("$#9984#"); target.setStateProvince(source.getBilling().getState());
        
								System.out.println("$#9985#"); return target;
    }

    @Override
    protected Address createTarget()
    {
							System.out.println("$#9986#"); return new Address();
    }

}
