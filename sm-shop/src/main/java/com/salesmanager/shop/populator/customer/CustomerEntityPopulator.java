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
import com.salesmanager.shop.model.customer.CustomerEntity;
import com.salesmanager.shop.model.customer.address.Address;



/**
 * <p>
 * CustomerEntityPopulator will help to populate {@link CustomerEntity} from {@link Customer} CustomerEntity will be
 * used to show data on the UI side.
 * </p>
 *
 * @author Umesh Awasthi
 * @version 1.2
 */
public class CustomerEntityPopulator
    extends AbstractDataPopulator<Customer, CustomerEntity>
{

    @Override
    public CustomerEntity populate( final Customer source, final CustomerEntity target,
                                    final MerchantStore merchantStore, final Language language )
        throws ConversionException
    {
        try
        {

            
												System.out.println("$#10012#"); target.setId( source.getId() );
												System.out.println("$#10013#"); if(StringUtils.isNotBlank( source.getEmailAddress() )){
																System.out.println("$#10014#"); target.setEmailAddress( source.getEmailAddress() );
            }
            
          

												System.out.println("$#10015#"); if ( source.getBilling() != null )
            {
                Address address = new Address();
																System.out.println("$#10016#"); address.setCity( source.getBilling().getCity() );
																System.out.println("$#10017#"); address.setAddress(source.getBilling().getAddress());
																System.out.println("$#10018#"); address.setCompany( source.getBilling().getCompany() );
																System.out.println("$#10019#"); address.setFirstName( source.getBilling().getFirstName() );
																System.out.println("$#10020#"); address.setLastName( source.getBilling().getLastName() );
																System.out.println("$#10021#"); address.setPostalCode( source.getBilling().getPostalCode() );
																System.out.println("$#10022#"); address.setPhone( source.getBilling().getTelephone() );
																System.out.println("$#10023#"); if ( source.getBilling().getCountry() != null )
                {
																				System.out.println("$#10024#"); address.setCountry( source.getBilling().getCountry().getIsoCode() );
                }
																System.out.println("$#10025#"); if ( source.getBilling().getZone() != null )
                {
																				System.out.println("$#10026#"); address.setZone( source.getBilling().getZone().getCode() );
                }
                
																System.out.println("$#10027#"); address.setStateProvince(source.getBilling().getState());

																System.out.println("$#10028#"); target.setBilling( address );
            }
            
						System.out.println("$#10029#"); if(source.getCustomerReviewAvg() != null) {
							System.out.println("$#10030#"); target.setRating(source.getCustomerReviewAvg().doubleValue());
    		}
    		
						System.out.println("$#10031#"); if(source.getCustomerReviewCount() != null) {
							System.out.println("$#10032#"); target.setRatingCount(source.getCustomerReviewCount().intValue());
    		}

												System.out.println("$#10033#"); if ( source.getDelivery() != null )
            {
                Address address = new Address();
																System.out.println("$#10034#"); address.setCity( source.getDelivery().getCity() );
																System.out.println("$#10035#"); address.setAddress(source.getDelivery().getAddress());
																System.out.println("$#10036#"); address.setCompany( source.getDelivery().getCompany() );
																System.out.println("$#10037#"); address.setFirstName( source.getDelivery().getFirstName() );
																System.out.println("$#10038#"); address.setLastName( source.getDelivery().getLastName() );
																System.out.println("$#10039#"); address.setPostalCode( source.getDelivery().getPostalCode() );
																System.out.println("$#10040#"); address.setPhone( source.getDelivery().getTelephone() );
																System.out.println("$#10041#"); if ( source.getDelivery().getCountry() != null )
                {
																				System.out.println("$#10042#"); address.setCountry( source.getDelivery().getCountry().getIsoCode() );
                }
																System.out.println("$#10043#"); if ( source.getDelivery().getZone() != null )
                {
																				System.out.println("$#10044#"); address.setZone( source.getDelivery().getZone().getCode() );
                }
                
																System.out.println("$#10045#"); address.setStateProvince(source.getDelivery().getState());

																System.out.println("$#10046#"); target.setDelivery( address );
            }

        }
        catch ( Exception e )
        {
            throw new ConversionException( e );
        }

								System.out.println("$#10047#"); return target;
    }

    @Override
    protected CustomerEntity createTarget()
    {
								System.out.println("$#10048#"); return new CustomerEntity();
    }

}
