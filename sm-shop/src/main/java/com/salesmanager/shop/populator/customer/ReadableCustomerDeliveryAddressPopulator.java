/**
 * 
 */
package com.salesmanager.shop.populator.customer;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.common.Delivery;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.shop.model.customer.ReadableDelivery;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * @author Carl Samson
 *
 */
public class ReadableCustomerDeliveryAddressPopulator extends AbstractDataPopulator<Delivery, ReadableDelivery>
{

    
	private CountryService countryService;
	private ZoneService zoneService;
	
	@Override
    public ReadableDelivery populate( Delivery source, ReadableDelivery target, MerchantStore store, Language language )
        throws ConversionException
    {

        
		System.out.println("$#10293#"); if(countryService==null) {
			throw new ConversionException("countryService must be set");
		}
		
		System.out.println("$#10294#"); if(zoneService==null) {
			throw new ConversionException("zoneService must be set");
		}
		

		System.out.println("$#10295#"); target.setLatitude(source.getLatitude());
		System.out.println("$#10296#"); target.setLongitude(source.getLongitude());

		
		System.out.println("$#10297#"); if(StringUtils.isNotBlank( source.getCity() )){
												System.out.println("$#10298#"); target.setCity(source.getCity());
        }
        
								System.out.println("$#10299#"); if(StringUtils.isNotBlank( source.getCompany() )){
												System.out.println("$#10300#"); target.setCompany(source.getCompany());
        }
        
								System.out.println("$#10301#"); if(StringUtils.isNotBlank( source.getAddress() )){
												System.out.println("$#10302#"); target.setAddress(source.getAddress());
        }
        
								System.out.println("$#10303#"); if(StringUtils.isNotBlank( source.getFirstName() )){
												System.out.println("$#10304#"); target.setFirstName(source.getFirstName());
        }
        
								System.out.println("$#10305#"); if(StringUtils.isNotBlank( source.getLastName() )){
												System.out.println("$#10306#"); target.setLastName(source.getLastName());
        }
        
								System.out.println("$#10307#"); if(StringUtils.isNotBlank( source.getPostalCode() )){
												System.out.println("$#10308#"); target.setPostalCode(source.getPostalCode());
        }
        
								System.out.println("$#10309#"); if(StringUtils.isNotBlank( source.getTelephone() )){
												System.out.println("$#10310#"); target.setPhone(source.getTelephone());
        }
      
								System.out.println("$#10311#"); target.setStateProvince(source.getState());
        
								System.out.println("$#10312#"); if(source.getTelephone()==null) {
												System.out.println("$#10313#"); target.setPhone(source.getTelephone());
        }
								System.out.println("$#10314#"); target.setAddress(source.getAddress());
								System.out.println("$#10315#"); if(source.getCountry()!=null) {
												System.out.println("$#10316#"); target.setCountry(source.getCountry().getIsoCode());
            
            //resolve country name
            try {
				Map<String,Country> countries = countryService.getCountriesMap(language);
				Country c =countries.get(source.getCountry().getIsoCode());
				System.out.println("$#10317#"); if(c!=null) {
					System.out.println("$#10318#"); target.setCountryName(c.getName());
				}
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				throw new ConversionException(e);
			}
        }
								System.out.println("$#10319#"); if(source.getZone()!=null) {
												System.out.println("$#10320#"); target.setZone(source.getZone().getCode());
            
            //resolve zone name
            try {
				Map<String,Zone> zones = zoneService.getZones(language);
				Zone z = zones.get(source.getZone().getCode());
				System.out.println("$#10321#"); if(z!=null) {
					System.out.println("$#10322#"); target.setProvinceName(z.getName());
				}
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				throw new ConversionException(e);
			}
        }
        
        

								System.out.println("$#10323#"); return target;
    }

	@Override
	protected ReadableDelivery createTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	public CountryService getCountryService() {
		System.out.println("$#10324#"); return countryService;
	}

	public void setCountryService(CountryService countryService) {
		this.countryService = countryService;
	}

	public ZoneService getZoneService() {
		System.out.println("$#10325#"); return zoneService;
	}

	public void setZoneService(ZoneService zoneService) {
		this.zoneService = zoneService;
	}


}
