package com.salesmanager.shop.populator.customer;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.customer.attribute.CustomerAttribute;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.user.Group;
import com.salesmanager.shop.model.customer.ReadableCustomer;
import com.salesmanager.shop.model.customer.address.Address;
import com.salesmanager.shop.model.customer.attribute.CustomerOptionDescription;
import com.salesmanager.shop.model.customer.attribute.CustomerOptionValueDescription;
import com.salesmanager.shop.model.customer.attribute.ReadableCustomerAttribute;
import com.salesmanager.shop.model.customer.attribute.ReadableCustomerOption;
import com.salesmanager.shop.model.customer.attribute.ReadableCustomerOptionValue;
import com.salesmanager.shop.model.security.ReadableGroup;
import org.apache.commons.lang3.StringUtils;

public class ReadableCustomerPopulator extends
		AbstractDataPopulator<Customer, ReadableCustomer> {

	

	@Override
	public ReadableCustomer populate(Customer source, ReadableCustomer target,
			MerchantStore store, Language language) throws ConversionException {

		try {
			
			System.out.println("$#10338#"); if(target == null) {
				target = new ReadableCustomer();
			}
			
			System.out.println("$#10340#"); System.out.println("$#10339#"); if(source.getId()!=null && source.getId()>0) {
				System.out.println("$#10342#"); target.setId(source.getId());
			}
			System.out.println("$#10343#"); target.setEmailAddress(source.getEmailAddress());

			System.out.println("$#10344#"); if (StringUtils.isNotEmpty(source.getNick())) {
				System.out.println("$#10345#"); target.setUserName(source.getNick());
			}

			System.out.println("$#10346#"); if (source.getDefaultLanguage()!= null) {
				System.out.println("$#10347#"); target.setLanguage(source.getDefaultLanguage().getCode());
			}

			System.out.println("$#10348#"); if (source.getGender()!= null) {
				System.out.println("$#10349#"); target.setGender(source.getGender().name());
			}

			System.out.println("$#10350#"); if (StringUtils.isNotEmpty(source.getProvider())) {
				System.out.println("$#10351#"); target.setProvider(source.getProvider());
			}

			System.out.println("$#10352#"); if(source.getBilling()!=null) {
				Address address = new Address();
				System.out.println("$#10353#"); address.setAddress(source.getBilling().getAddress());
				System.out.println("$#10354#"); address.setCity(source.getBilling().getCity());
				System.out.println("$#10355#"); address.setCompany(source.getBilling().getCompany());
				System.out.println("$#10356#"); address.setFirstName(source.getBilling().getFirstName());
				System.out.println("$#10357#"); address.setLastName(source.getBilling().getLastName());
				System.out.println("$#10358#"); address.setPostalCode(source.getBilling().getPostalCode());
				System.out.println("$#10359#"); address.setPhone(source.getBilling().getTelephone());
				System.out.println("$#10360#"); if(source.getBilling().getCountry()!=null) {
					System.out.println("$#10361#"); address.setCountry(source.getBilling().getCountry().getIsoCode());
				}
				System.out.println("$#10362#"); if(source.getBilling().getZone()!=null) {
					System.out.println("$#10363#"); address.setZone(source.getBilling().getZone().getCode());
				}
				System.out.println("$#10364#"); if(source.getBilling().getState()!=null) {
					System.out.println("$#10365#"); address.setStateProvince(source.getBilling().getState());
				}

				System.out.println("$#10366#"); target.setFirstName(address.getFirstName());
				System.out.println("$#10367#"); target.setLastName(address.getLastName());

				System.out.println("$#10368#"); target.setBilling(address);
			}

			System.out.println("$#10369#"); if(source.getCustomerReviewAvg() != null) {
				System.out.println("$#10370#"); target.setRating(source.getCustomerReviewAvg().doubleValue());
			}

			System.out.println("$#10371#"); if(source.getCustomerReviewCount() != null) {
				System.out.println("$#10372#"); target.setRatingCount(source.getCustomerReviewCount().intValue());
			}

			System.out.println("$#10373#"); if(source.getDelivery()!=null) {
				Address address = new Address();
				System.out.println("$#10374#"); address.setCity(source.getDelivery().getCity());
				System.out.println("$#10375#"); address.setAddress(source.getDelivery().getAddress());
				System.out.println("$#10376#"); address.setCompany(source.getDelivery().getCompany());
				System.out.println("$#10377#"); address.setFirstName(source.getDelivery().getFirstName());
				System.out.println("$#10378#"); address.setLastName(source.getDelivery().getLastName());
				System.out.println("$#10379#"); address.setPostalCode(source.getDelivery().getPostalCode());
				System.out.println("$#10380#"); address.setPhone(source.getDelivery().getTelephone());
				System.out.println("$#10381#"); if(source.getDelivery().getCountry()!=null) {
					System.out.println("$#10382#"); address.setCountry(source.getDelivery().getCountry().getIsoCode());
				}
				System.out.println("$#10383#"); if(source.getDelivery().getZone()!=null) {
					System.out.println("$#10384#"); address.setZone(source.getDelivery().getZone().getCode());
				}
				System.out.println("$#10385#"); if(source.getDelivery().getState()!=null) {
					System.out.println("$#10386#"); address.setStateProvince(source.getDelivery().getState());
				}

				System.out.println("$#10387#"); target.setDelivery(address);
			}

			System.out.println("$#10388#"); if(source.getAttributes()!=null) {
				for(CustomerAttribute attribute : source.getAttributes()) {
					ReadableCustomerAttribute readableAttribute = new ReadableCustomerAttribute();
					System.out.println("$#10389#"); readableAttribute.setId(attribute.getId());
					System.out.println("$#10390#"); readableAttribute.setTextValue(attribute.getTextValue());
					ReadableCustomerOption option = new ReadableCustomerOption();
					System.out.println("$#10391#"); option.setId(attribute.getCustomerOption().getId());
					System.out.println("$#10392#"); option.setCode(attribute.getCustomerOption().getCode());

					CustomerOptionDescription d = new CustomerOptionDescription();
					System.out.println("$#10393#"); d.setDescription(attribute.getCustomerOption().getDescriptionsSettoList().get(0).getDescription());
					System.out.println("$#10394#"); d.setName(attribute.getCustomerOption().getDescriptionsSettoList().get(0).getName());
					System.out.println("$#10395#"); option.setDescription(d);

					System.out.println("$#10396#"); readableAttribute.setCustomerOption(option);

					ReadableCustomerOptionValue optionValue = new ReadableCustomerOptionValue();
					System.out.println("$#10397#"); optionValue.setId(attribute.getCustomerOptionValue().getId());
					CustomerOptionValueDescription vd = new CustomerOptionValueDescription();
					System.out.println("$#10398#"); vd.setDescription(attribute.getCustomerOptionValue().getDescriptionsSettoList().get(0).getDescription());
					System.out.println("$#10399#"); vd.setName(attribute.getCustomerOptionValue().getDescriptionsSettoList().get(0).getName());
					System.out.println("$#10400#"); optionValue.setCode(attribute.getCustomerOptionValue().getCode());
					System.out.println("$#10401#"); optionValue.setDescription(vd);


					System.out.println("$#10402#"); readableAttribute.setCustomerOptionValue(optionValue);
					target.getAttributes().add(readableAttribute);
				}

				System.out.println("$#10403#"); if(source.getGroups() != null) {
					for(Group group : source.getGroups()) {
						ReadableGroup readableGroup = new ReadableGroup();
						System.out.println("$#10404#"); readableGroup.setId(group.getId().longValue());
						System.out.println("$#10405#"); readableGroup.setName(group.getGroupName());
						System.out.println("$#10406#"); readableGroup.setType(group.getGroupType().name());
						target.getGroups().add(
								readableGroup
						);
					}
				}
			}
		
		} catch (Exception e) {
			throw new ConversionException(e);
		}
		
		System.out.println("$#10407#"); return target;
	}

	@Override
	protected ReadableCustomer createTarget() {
		return null;
	}

}
