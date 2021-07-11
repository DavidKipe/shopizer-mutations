package com.salesmanager.shop.populator.order;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.shop.model.customer.PersistableCustomer;
import com.salesmanager.shop.model.customer.ReadableCustomer;
import com.salesmanager.shop.model.customer.address.Address;
import com.salesmanager.shop.model.order.ReadableShopOrder;
import com.salesmanager.shop.model.order.ShopOrder;

public class ReadableShopOrderPopulator extends
		AbstractDataPopulator<ShopOrder, ReadableShopOrder> {

	@Override
	public ReadableShopOrder populate(ShopOrder source,
			ReadableShopOrder target, MerchantStore store, Language language)
			throws ConversionException {
		
		//not that much is required
		
		
		//customer
		
		try {
			
			ReadableCustomer customer = new ReadableCustomer();
			PersistableCustomer persistableCustomer = source.getCustomer();


			System.out.println("$#10805#"); customer.setEmailAddress(persistableCustomer.getEmailAddress());
			System.out.println("$#10806#"); if(persistableCustomer.getBilling()!=null) {
				Address address = new Address();
				System.out.println("$#10807#"); address.setCity(persistableCustomer.getBilling().getCity());
				System.out.println("$#10808#"); address.setCompany(persistableCustomer.getBilling().getCompany());
				System.out.println("$#10809#"); address.setFirstName(persistableCustomer.getBilling().getFirstName());
				System.out.println("$#10810#"); address.setLastName(persistableCustomer.getBilling().getLastName());
				System.out.println("$#10811#"); address.setPostalCode(persistableCustomer.getBilling().getPostalCode());
				System.out.println("$#10812#"); address.setPhone(persistableCustomer.getBilling().getPhone());
				System.out.println("$#10813#"); if(persistableCustomer.getBilling().getCountry()!=null) {
					System.out.println("$#10814#"); address.setCountry(persistableCustomer.getBilling().getCountry());
				}
				System.out.println("$#10815#"); if(persistableCustomer.getBilling().getZone()!=null) {
					System.out.println("$#10816#"); address.setZone(persistableCustomer.getBilling().getZone());
				}
				
				System.out.println("$#10817#"); customer.setBilling(address);
			}
			
			System.out.println("$#10818#"); if(persistableCustomer.getDelivery()!=null) {
				Address address = new Address();
				System.out.println("$#10819#"); address.setCity(persistableCustomer.getDelivery().getCity());
				System.out.println("$#10820#"); address.setCompany(persistableCustomer.getDelivery().getCompany());
				System.out.println("$#10821#"); address.setFirstName(persistableCustomer.getDelivery().getFirstName());
				System.out.println("$#10822#"); address.setLastName(persistableCustomer.getDelivery().getLastName());
				System.out.println("$#10823#"); address.setPostalCode(persistableCustomer.getDelivery().getPostalCode());
				System.out.println("$#10824#"); address.setPhone(persistableCustomer.getDelivery().getPhone());
				System.out.println("$#10825#"); if(persistableCustomer.getDelivery().getCountry()!=null) {
					System.out.println("$#10826#"); address.setCountry(persistableCustomer.getDelivery().getCountry());
				}
				System.out.println("$#10827#"); if(persistableCustomer.getDelivery().getZone()!=null) {
					System.out.println("$#10828#"); address.setZone(persistableCustomer.getDelivery().getZone());
				}
				
				System.out.println("$#10829#"); customer.setDelivery(address);
			}
			
			//TODO if ship to billing enabled, set delivery = billing
			
			
/*			if(persistableCustomer.getAttributes()!=null) {
				for(PersistableCustomerAttribute attribute : persistableCustomer.getAttributes()) {
					ReadableCustomerAttribute readableAttribute = new ReadableCustomerAttribute();
					readableAttribute.setId(attribute.getId());
					ReadableCustomerOption option = new ReadableCustomerOption();
					option.setId(attribute.getCustomerOption().getId());
					option.setCode(attribute.getCustomerOption());
					
					CustomerOptionDescription d = new CustomerOptionDescription();
					d.setDescription(attribute.getCustomerOption().getDescriptionsSettoList().get(0).getDescription());
					d.setName(attribute.getCustomerOption().getDescriptionsSettoList().get(0).getName());
					option.setDescription(d);
					
					readableAttribute.setCustomerOption(option);
					
					ReadableCustomerOptionValue optionValue = new ReadableCustomerOptionValue();
					optionValue.setId(attribute.getCustomerOptionValue().getId());
					CustomerOptionValueDescription vd = new CustomerOptionValueDescription();
					vd.setDescription(attribute.getCustomerOptionValue().getDescriptionsSettoList().get(0).getDescription());
					vd.setName(attribute.getCustomerOptionValue().getDescriptionsSettoList().get(0).getName());
					optionValue.setCode(attribute.getCustomerOptionValue().getCode());
					optionValue.setDescription(vd);
					
					
					readableAttribute.setCustomerOptionValue(optionValue);
					customer.getAttributes().add(readableAttribute);
				}
			}*/
			
			System.out.println("$#10830#"); target.setCustomer(customer);
		
		} catch (Exception e) {
			throw new ConversionException(e);
		}
		
		
		
		System.out.println("$#10831#"); return target;
	}

	@Override
	protected ReadableShopOrder createTarget() {
		return null;
	}

}
