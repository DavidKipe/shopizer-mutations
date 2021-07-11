package com.salesmanager.shop.populator.customer;

import org.apache.commons.lang.StringUtils;

import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.customer.address.Address;
import com.salesmanager.core.business.utils.AbstractDataPopulator;

public class PersistableCustomerBillingAddressPopulator extends AbstractDataPopulator<Address, Customer>
{

    @Override
    public Customer populate( Address source, Customer target, MerchantStore store, Language language )
        throws ConversionException
    {
        
       
											System.out.println("$#10145#"); target.getBilling().setFirstName( source.getFirstName() );
											System.out.println("$#10146#"); target.getBilling().setLastName( source.getLastName() );
          
            // lets fill optional data now
           
											System.out.println("$#10147#"); if(StringUtils.isNotBlank( source.getAddress())){
															System.out.println("$#10148#"); target.getBilling().setAddress( source.getAddress() );
           }
           
											System.out.println("$#10149#"); if(StringUtils.isNotBlank( source.getCity())){
															System.out.println("$#10150#"); target.getBilling().setCity( source.getCity() );
           }
           
											System.out.println("$#10151#"); if(StringUtils.isNotBlank( source.getCompany())){
															System.out.println("$#10152#"); target.getBilling().setCompany( source.getCompany() );
           }
           
											System.out.println("$#10153#"); if(StringUtils.isNotBlank( source.getPhone())){
															System.out.println("$#10154#"); target.getBilling().setTelephone( source.getPhone());
           }
           
											System.out.println("$#10155#"); if(StringUtils.isNotBlank( source.getPostalCode())){
															System.out.println("$#10156#"); target.getBilling().setPostalCode( source.getPostalCode());
           }
           
											System.out.println("$#10157#"); if(StringUtils.isNotBlank( source.getStateProvince())){
															System.out.println("$#10158#"); target.getBilling().setState(source.getStateProvince());
           }
           
											System.out.println("$#10159#"); return target;
        
    }

    @Override
    protected Customer createTarget()
    {
         return null;
    }

   

}
