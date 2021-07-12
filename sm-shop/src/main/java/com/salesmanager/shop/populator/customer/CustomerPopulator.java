package com.salesmanager.shop.populator.customer;


import java.math.BigDecimal;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.customer.attribute.CustomerOptionService;
import com.salesmanager.core.business.services.customer.attribute.CustomerOptionValueService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.common.Billing;
import com.salesmanager.core.model.common.Delivery;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.customer.attribute.CustomerAttribute;
import com.salesmanager.core.model.customer.attribute.CustomerOption;
import com.salesmanager.core.model.customer.attribute.CustomerOptionValue;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.shop.model.customer.PersistableCustomer;
import com.salesmanager.shop.model.customer.address.Address;
import com.salesmanager.shop.model.customer.attribute.PersistableCustomerAttribute;

@Component
public class CustomerPopulator extends
		AbstractDataPopulator<PersistableCustomer, Customer> {
	
	protected static final Logger LOG=LoggerFactory.getLogger( CustomerPopulator.class );
    @Autowired
	private CountryService countryService;
    @Autowired
    private ZoneService zoneService;
    @Autowired
    private LanguageService languageService;
    @Autowired
	private CustomerOptionService customerOptionService;
    @Autowired
    private CustomerOptionValueService customerOptionValueService;
    @Autowired
    private PasswordEncoder passwordEncoder;



	/**
	 * Creates a Customer entity ready to be saved
	 */
	@Override
	public Customer populate(PersistableCustomer source, Customer target,
			MerchantStore store, Language language) throws ConversionException {

		try {
			
			System.out.println("$#10061#"); System.out.println("$#10060#"); if(source.getId() !=null && source.getId()>0){
							System.out.println("$#10063#"); target.setId( source.getId() );
			}

			System.out.println("$#10064#"); if(!StringUtils.isBlank(source.getPassword())) {
					System.out.println("$#10065#"); target.setPassword(passwordEncoder.encode(source.getPassword()));
					System.out.println("$#10066#"); target.setAnonymous(false);
			}

			System.out.println("$#10067#"); target.setBilling(new Billing());
			System.out.println("$#10068#"); if (!StringUtils.isEmpty(source.getFirstName())) {
				System.out.println("$#10069#"); target.getBilling().setFirstName(
						source.getFirstName()
				);
			}
			System.out.println("$#10070#"); if (!StringUtils.isEmpty(source.getLastName())) {
				System.out.println("$#10071#"); target.getBilling().setLastName(
						source.getLastName()
				);
			}

						System.out.println("$#10072#"); target.setProvider(source.getProvider());

			System.out.println("$#10073#"); target.setEmailAddress(source.getEmailAddress());
			System.out.println("$#10074#"); target.setNick(source.getUserName());
			System.out.println("$#10075#"); if(source.getGender()!=null && target.getGender()==null) {
				System.out.println("$#10077#"); target.setGender( com.salesmanager.core.model.customer.CustomerGender.valueOf( source.getGender() ) );
			}
			System.out.println("$#10078#"); if(target.getGender()==null) {
				System.out.println("$#10079#"); target.setGender( com.salesmanager.core.model.customer.CustomerGender.M);
			}

			Map<String,Country> countries = countryService.getCountriesMap(language);
			Map<String,Zone> zones = zoneService.getZones(language);
			
			System.out.println("$#10080#"); target.setMerchantStore( store );

			Address sourceBilling = source.getBilling();
			System.out.println("$#10081#"); if(sourceBilling!=null) {
				Billing billing = target.getBilling();
				System.out.println("$#10082#"); billing.setAddress(sourceBilling.getAddress());
				System.out.println("$#10083#"); billing.setCity(sourceBilling.getCity());
				System.out.println("$#10084#"); billing.setCompany(sourceBilling.getCompany());
				//billing.setCountry(country);
				System.out.println("$#10085#"); if (!StringUtils.isEmpty(sourceBilling.getFirstName())) { // manual correction for 'if' without '{' problem
					System.out.println("$#10086#");
					billing.setFirstName(sourceBilling.getFirstName());
				}
				System.out.println("$#10087#"); if (!StringUtils.isEmpty(sourceBilling.getLastName())) { // manual correction for 'if' without '{' problem
					System.out.println("$#10088#");
					billing.setLastName(sourceBilling.getLastName());
				}
				System.out.println("$#10089#"); billing.setTelephone(sourceBilling.getPhone());
				System.out.println("$#10090#"); billing.setPostalCode(sourceBilling.getPostalCode());
				System.out.println("$#10091#"); billing.setState(sourceBilling.getStateProvince());
				Country billingCountry = null;
				System.out.println("$#10092#"); if(!StringUtils.isBlank(sourceBilling.getCountry())) {
					billingCountry = countries.get(sourceBilling.getCountry());
					System.out.println("$#10093#"); if(billingCountry==null) {
						throw new ConversionException("Unsuported country code " + sourceBilling.getCountry());
					}
					System.out.println("$#10094#"); billing.setCountry(billingCountry);
				}
				
				System.out.println("$#10095#"); if(billingCountry!=null && !StringUtils.isBlank(sourceBilling.getZone())) {
					Zone zone = zoneService.getByCode(sourceBilling.getZone());
					System.out.println("$#10097#"); if(zone==null) {
						throw new ConversionException("Unsuported zone code " + sourceBilling.getZone());
					}
					Zone zoneDescription = zones.get(zone.getCode());
					System.out.println("$#10098#"); billing.setZone(zoneDescription);
				}
				// target.setBilling(billing);

			}
			System.out.println("$#10099#"); if(target.getBilling() ==null && source.getBilling()!=null){
			    LOG.info( "Setting default values for billing" );
			    Billing billing = new Billing();
			    Country billingCountry = null;
							System.out.println("$#10101#"); if(StringUtils.isNotBlank( source.getBilling().getCountry() )) {
                    billingCountry = countries.get(source.getBilling().getCountry());
																				System.out.println("$#10102#"); if(billingCountry==null) {
                        throw new ConversionException("Unsuported country code " + sourceBilling.getCountry());
                    }
																				System.out.println("$#10103#"); billing.setCountry(billingCountry);
																				System.out.println("$#10104#"); target.setBilling( billing );
                }
			}
			Address sourceShipping = source.getDelivery();
			System.out.println("$#10105#"); if(sourceShipping!=null) {
				Delivery delivery = new Delivery();
				System.out.println("$#10106#"); delivery.setAddress(sourceShipping.getAddress());
				System.out.println("$#10107#"); delivery.setCity(sourceShipping.getCity());
				System.out.println("$#10108#"); delivery.setCompany(sourceShipping.getCompany());
				System.out.println("$#10109#"); delivery.setFirstName(sourceShipping.getFirstName());
				System.out.println("$#10110#"); delivery.setLastName(sourceShipping.getLastName());
				System.out.println("$#10111#"); delivery.setTelephone(sourceShipping.getPhone());
				System.out.println("$#10112#"); delivery.setPostalCode(sourceShipping.getPostalCode());
				System.out.println("$#10113#"); delivery.setState(sourceShipping.getStateProvince());
				Country deliveryCountry = null;
				
				
				
				System.out.println("$#10114#"); if(!StringUtils.isBlank(sourceShipping.getCountry())) {
					deliveryCountry = countries.get(sourceShipping.getCountry());
					System.out.println("$#10115#"); if(deliveryCountry==null) {
						throw new ConversionException("Unsuported country code " + sourceShipping.getCountry());
					}
					System.out.println("$#10116#"); delivery.setCountry(deliveryCountry);
				}
				
				System.out.println("$#10117#"); if(deliveryCountry!=null && !StringUtils.isBlank(sourceShipping.getZone())) {
					Zone zone = zoneService.getByCode(sourceShipping.getZone());
					System.out.println("$#10119#"); if(zone==null) {
						throw new ConversionException("Unsuported zone code " + sourceShipping.getZone());
					}
					Zone zoneDescription = zones.get(zone.getCode());
					System.out.println("$#10120#"); delivery.setZone(zoneDescription);
				}
				System.out.println("$#10121#"); target.setDelivery(delivery);
			}
			
			System.out.println("$#10122#"); if(source.getRating() != null) {
				System.out.println("$#10123#"); target.setCustomerReviewAvg(new BigDecimal(source.getRating().doubleValue()));
			}
			
			System.out.println("$#10124#"); target.setCustomerReviewCount(source.getRatingCount());

			
			System.out.println("$#10125#"); if(target.getDelivery() ==null && source.getDelivery()!=null){
			    LOG.info( "Setting default value for delivery" );
			    Delivery delivery = new Delivery();
			    Country deliveryCountry = null;
																System.out.println("$#10127#"); if(StringUtils.isNotBlank( source.getDelivery().getCountry() )) {
                    deliveryCountry = countries.get(source.getDelivery().getCountry());
																				System.out.println("$#10128#"); if(deliveryCountry==null) {
                        throw new ConversionException("Unsuported country code " + sourceShipping.getCountry());
                    }
																				System.out.println("$#10129#"); delivery.setCountry(deliveryCountry);
																				System.out.println("$#10130#"); target.setDelivery( delivery );
                }
			}
			
			System.out.println("$#10131#"); if(source.getAttributes()!=null) {
				for(PersistableCustomerAttribute attr : source.getAttributes()) {

					CustomerOption customerOption = customerOptionService.getById(attr.getCustomerOption().getId());
					System.out.println("$#10132#"); if(customerOption==null) {
						throw new ConversionException("Customer option id " + attr.getCustomerOption().getId() + " does not exist");
					}
					
					CustomerOptionValue customerOptionValue = customerOptionValueService.getById(attr.getCustomerOptionValue().getId());
					System.out.println("$#10133#"); if(customerOptionValue==null) {
						throw new ConversionException("Customer option value id " + attr.getCustomerOptionValue().getId() + " does not exist");
					}
					
					System.out.println("$#10134#"); if(customerOption.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
						throw new ConversionException("Invalid customer option id ");
					}
					
					System.out.println("$#10135#"); if(customerOptionValue.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
						throw new ConversionException("Invalid customer option value id ");
					}
					
					CustomerAttribute attribute = new CustomerAttribute();
					System.out.println("$#10136#"); attribute.setCustomer(target);
					System.out.println("$#10137#"); attribute.setCustomerOption(customerOption);
					System.out.println("$#10138#"); attribute.setCustomerOptionValue(customerOptionValue);
					System.out.println("$#10139#"); attribute.setTextValue(attr.getTextValue());
					
					target.getAttributes().add(attribute);
					
				}
			}
			
			System.out.println("$#10140#"); if(target.getDefaultLanguage()==null) {
				
				Language lang = source.getLanguage() == null ?
						language : languageService.getByCode(source.getLanguage());

				
				System.out.println("$#10142#"); target.setDefaultLanguage(lang);
			}

		
		} catch (Exception e) {
			throw new ConversionException(e);
		}
		
		
		
		
		System.out.println("$#10143#"); return target;
	}

	@Override
	protected Customer createTarget() {
		System.out.println("$#10144#"); return new Customer();
	}


}
