package com.salesmanager.shop.populator.customer;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.customer.PersistableCustomer;
import com.salesmanager.shop.model.customer.address.Address;

public class PersistableCustomerPopulator extends
		AbstractDataPopulator<Customer, PersistableCustomer> {

	@Override
	public PersistableCustomer populate(Customer source,
			PersistableCustomer target, MerchantStore store, Language language)
			throws ConversionException {

		
		try {
			

			System.out.println("$#10190#"); if(source.getBilling()!=null) {
				Address address = new Address();
				System.out.println("$#10191#"); address.setCity(source.getBilling().getCity());
				System.out.println("$#10192#"); address.setCompany(source.getBilling().getCompany());
				System.out.println("$#10193#"); address.setFirstName(source.getBilling().getFirstName());
				System.out.println("$#10194#"); address.setLastName(source.getBilling().getLastName());
				System.out.println("$#10195#"); address.setPostalCode(source.getBilling().getPostalCode());
				System.out.println("$#10196#"); address.setPhone(source.getBilling().getTelephone());
				System.out.println("$#10197#"); if(source.getBilling().getTelephone()==null) {
					System.out.println("$#10198#"); address.setPhone(source.getBilling().getTelephone());
				}
				System.out.println("$#10199#"); address.setAddress(source.getBilling().getAddress());
				System.out.println("$#10200#"); if(source.getBilling().getCountry()!=null) {
					System.out.println("$#10201#"); address.setCountry(source.getBilling().getCountry().getIsoCode());
				}
				System.out.println("$#10202#"); if(source.getBilling().getZone()!=null) {
					System.out.println("$#10203#"); address.setZone(source.getBilling().getZone().getCode());
				}
				System.out.println("$#10204#"); if(source.getBilling().getState()!=null) {
					System.out.println("$#10205#"); address.setStateProvince(source.getBilling().getState());
				}
				
				System.out.println("$#10206#"); target.setBilling(address);
			}
			
			System.out.println("$#10207#"); target.setProvider(source.getProvider());
			
			System.out.println("$#10208#"); if(source.getCustomerReviewAvg() != null) {
				System.out.println("$#10209#"); target.setRating(source.getCustomerReviewAvg().doubleValue());
			}
			
			System.out.println("$#10210#"); if(source.getCustomerReviewCount() != null) {
				System.out.println("$#10211#"); target.setRatingCount(source.getCustomerReviewCount().intValue());
			}
			
			System.out.println("$#10212#"); if(source.getDelivery()!=null) {
				Address address = new Address();
				System.out.println("$#10213#"); address.setAddress(source.getDelivery().getAddress());
				System.out.println("$#10214#"); address.setCity(source.getDelivery().getCity());
				System.out.println("$#10215#"); address.setCompany(source.getDelivery().getCompany());
				System.out.println("$#10216#"); address.setFirstName(source.getDelivery().getFirstName());
				System.out.println("$#10217#"); address.setLastName(source.getDelivery().getLastName());
				System.out.println("$#10218#"); address.setPostalCode(source.getDelivery().getPostalCode());
				System.out.println("$#10219#"); address.setPhone(source.getDelivery().getTelephone());
				System.out.println("$#10220#"); if(source.getDelivery().getCountry()!=null) {
					System.out.println("$#10221#"); address.setCountry(source.getDelivery().getCountry().getIsoCode());
				}
				System.out.println("$#10222#"); if(source.getDelivery().getZone()!=null) {
					System.out.println("$#10223#"); address.setZone(source.getDelivery().getZone().getCode());
				}
				System.out.println("$#10224#"); if(source.getDelivery().getState()!=null) {
					System.out.println("$#10225#"); address.setStateProvince(source.getDelivery().getState());
				}
				
				System.out.println("$#10226#"); target.setDelivery(address);
			}
			
			System.out.println("$#10227#"); target.setId(source.getId());
			System.out.println("$#10228#"); target.setEmailAddress(source.getEmailAddress());
			System.out.println("$#10229#"); if(source.getGender()!=null) {
				System.out.println("$#10230#"); target.setGender(source.getGender().name());
			}
			System.out.println("$#10231#"); if(source.getDefaultLanguage()!=null) {
				System.out.println("$#10232#"); target.setLanguage(source.getDefaultLanguage().getCode());
			}
			System.out.println("$#10233#"); target.setUserName(source.getNick());
			System.out.println("$#10234#"); target.setStoreCode(store.getCode());
			System.out.println("$#10235#"); if(source.getDefaultLanguage()!=null) {
				System.out.println("$#10236#"); target.setLanguage(source.getDefaultLanguage().getCode());
			} else {
				System.out.println("$#10237#"); target.setLanguage(store.getDefaultLanguage().getCode());
			}
			
			
			
		} catch (Exception e) {
			throw new ConversionException(e);
		}
			
		System.out.println("$#10238#"); return target;
		
	}

	@Override
	protected PersistableCustomer createTarget() {
		// TODO Auto-generated method stub
		return null;
	}

}
