package com.salesmanager.shop.populator.customer;

import org.apache.commons.lang.StringUtils;

import com.salesmanager.core.model.common.Delivery;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.customer.address.Address;
import com.salesmanager.core.business.utils.AbstractDataPopulator;

public class PersistableCustomerShippingAddressPopulator extends AbstractDataPopulator<Address, Customer>
{

    @Override
    public Customer populate( Address source, Customer target, MerchantStore store, Language language )
        throws ConversionException
    {
        
       
										System.out.println("$#10262#"); if( target.getDelivery() == null){
              
              Delivery delivery=new Delivery();
														System.out.println("$#10263#"); delivery.setFirstName( source.getFirstName()) ;
														System.out.println("$#10264#"); delivery.setLastName( source.getLastName() );
              
														System.out.println("$#10265#"); if(StringUtils.isNotBlank( source.getAddress())){
																		System.out.println("$#10266#"); delivery.setAddress( source.getAddress() );
              }
              
														System.out.println("$#10267#"); if(StringUtils.isNotBlank( source.getCity())){
																		System.out.println("$#10268#"); delivery.setCity( source.getCity() );
              }
              
														System.out.println("$#10269#"); if(StringUtils.isNotBlank( source.getCompany())){
																		System.out.println("$#10270#"); delivery.setCompany( source.getCompany() );
              }
              
														System.out.println("$#10271#"); if(StringUtils.isNotBlank( source.getPhone())){
																		System.out.println("$#10272#"); delivery.setTelephone( source.getPhone());
              }
              
														System.out.println("$#10273#"); if(StringUtils.isNotBlank( source.getPostalCode())){
																		System.out.println("$#10274#"); delivery.setPostalCode( source.getPostalCode());
              }
              
														System.out.println("$#10275#"); if(StringUtils.isNotBlank( source.getStateProvince())){
																		System.out.println("$#10276#"); delivery.setPostalCode( source.getStateProvince());
              }
              
														System.out.println("$#10277#"); target.setDelivery( delivery );
          }
          else{
											System.out.println("$#10278#"); target.getDelivery().setFirstName( source.getFirstName() );
											System.out.println("$#10279#"); target.getDelivery().setLastName( source.getLastName() );
          
            // lets fill optional data now
           
											System.out.println("$#10280#"); if(StringUtils.isNotBlank( source.getAddress())){
															System.out.println("$#10281#"); target.getDelivery().setAddress( source.getAddress() );
           }
           
											System.out.println("$#10282#"); if(StringUtils.isNotBlank( source.getCity())){
															System.out.println("$#10283#"); target.getDelivery().setCity( source.getCity() );
           }
           
											System.out.println("$#10284#"); if(StringUtils.isNotBlank( source.getCompany())){
															System.out.println("$#10285#"); target.getDelivery().setCompany( source.getCompany() );
           }
           
											System.out.println("$#10286#"); if(StringUtils.isNotBlank( source.getPhone())){
															System.out.println("$#10287#"); target.getDelivery().setTelephone( source.getPhone());
           }
           
											System.out.println("$#10288#"); if(StringUtils.isNotBlank( source.getPostalCode())){
															System.out.println("$#10289#"); target.getDelivery().setPostalCode( source.getPostalCode());
           }
           
											System.out.println("$#10290#"); if(StringUtils.isNotBlank( source.getStateProvince())){
															System.out.println("$#10291#"); target.getDelivery().setPostalCode( source.getStateProvince());
           }
          }
           
											System.out.println("$#10292#"); return target;
        
    }

    @Override
    protected Customer createTarget()
    {
         return null;
    }

   

}
