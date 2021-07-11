/**
 * 
 */
package com.salesmanager.shop.populator.customer;

import org.apache.commons.lang.StringUtils;


import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.customer.address.Address;




/**
 * @author Admin
 *
 */
public class CustomerDeliveryAddressPopulator extends AbstractDataPopulator<Customer, Address>
{

    @Override
    public Address populate( Customer source, Address target, MerchantStore store, Language language )
        throws ConversionException
    {
        
								System.out.println("$#9987#"); if(source.getDelivery()!=null){
								System.out.println("$#9988#"); if(StringUtils.isNotBlank( source.getDelivery().getCity() )){
												System.out.println("$#9989#"); target.setCity(source.getDelivery().getCity());
        }
        
								System.out.println("$#9990#"); if(StringUtils.isNotBlank( source.getDelivery().getCompany() )){
												System.out.println("$#9991#"); target.setCompany(source.getDelivery().getCompany());
        }
        
								System.out.println("$#9992#"); if(StringUtils.isNotBlank( source.getDelivery().getAddress() )){
												System.out.println("$#9993#"); target.setAddress(source.getDelivery().getAddress());
        }
        
								System.out.println("$#9994#"); if(StringUtils.isNotBlank( source.getDelivery().getFirstName() )){
												System.out.println("$#9995#"); target.setFirstName(source.getDelivery().getFirstName());
        }
        
								System.out.println("$#9996#"); if(StringUtils.isNotBlank( source.getDelivery().getLastName() )){
												System.out.println("$#9997#"); target.setLastName(source.getDelivery().getLastName());
        }
        
								System.out.println("$#9998#"); if(StringUtils.isNotBlank( source.getDelivery().getPostalCode() )){
												System.out.println("$#9999#"); target.setPostalCode(source.getDelivery().getPostalCode());
        }
        
								System.out.println("$#10000#"); if(StringUtils.isNotBlank( source.getDelivery().getTelephone() )){
												System.out.println("$#10001#"); target.setPhone(source.getDelivery().getTelephone());
        }
      
								System.out.println("$#10002#"); target.setStateProvince(source.getDelivery().getState());
        
								System.out.println("$#10003#"); if(source.getDelivery().getTelephone()==null) {
												System.out.println("$#10004#"); target.setPhone(source.getDelivery().getTelephone());
        }
								System.out.println("$#10005#"); target.setAddress(source.getDelivery().getAddress());
								System.out.println("$#10006#"); if(source.getDelivery().getCountry()!=null) {
												System.out.println("$#10007#"); target.setCountry(source.getDelivery().getCountry().getIsoCode());
        }
								System.out.println("$#10008#"); if(source.getDelivery().getZone()!=null) {
												System.out.println("$#10009#"); target.setZone(source.getDelivery().getZone().getCode());
        }
        }
								System.out.println("$#10010#"); return target;
    }

    @Override
    protected Address createTarget()
    {
							System.out.println("$#10011#"); return new Address();
    }

}
