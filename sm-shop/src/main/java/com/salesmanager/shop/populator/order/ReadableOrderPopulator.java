package com.salesmanager.shop.populator.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.order.OrderTotal;
import com.salesmanager.core.model.order.OrderTotalType;
import com.salesmanager.core.model.order.attributes.OrderAttribute;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.customer.ReadableBilling;
import com.salesmanager.shop.model.customer.ReadableDelivery;
import com.salesmanager.shop.model.customer.address.Address;
import com.salesmanager.shop.model.order.v0.ReadableOrder;
import com.salesmanager.shop.model.store.ReadableMerchantStore;

import org.springframework.beans.factory.annotation.Qualifier;
import com.salesmanager.shop.populator.store.ReadableMerchantStorePopulator;
import com.salesmanager.shop.utils.ImageFilePath;

@Component
public class ReadableOrderPopulator extends
		AbstractDataPopulator<Order, ReadableOrder> {
	
	@Autowired
	private CountryService countryService;
	@Autowired
	private ZoneService zoneService;
	
	@Autowired
	@Qualifier("img")
	private ImageFilePath filePath;


	@Override
	public ReadableOrder populate(Order source, ReadableOrder target,
			MerchantStore store, Language language) throws ConversionException {
		
		
		
		System.out.println("$#10643#"); target.setId(source.getId());
		System.out.println("$#10644#"); target.setDatePurchased(source.getDatePurchased());
		System.out.println("$#10645#"); target.setOrderStatus(source.getStatus());
		System.out.println("$#10646#"); target.setCurrency(source.getCurrency().getCode());
		System.out.println("$#10647#"); target.setCurrencyModel(source.getCurrency());
		
		System.out.println("$#10648#"); target.setPaymentType(source.getPaymentType());
		System.out.println("$#10649#"); target.setPaymentModule(source.getPaymentModuleCode());
		System.out.println("$#10650#"); target.setShippingModule(source.getShippingModuleCode());
		
		System.out.println("$#10651#"); if(source.getMerchant()!=null) {
			ReadableMerchantStorePopulator merchantPopulator = new ReadableMerchantStorePopulator();
			System.out.println("$#10652#"); merchantPopulator.setCountryService(countryService);
			System.out.println("$#10653#"); merchantPopulator.setFilePath(filePath);
			System.out.println("$#10654#"); merchantPopulator.setZoneService(zoneService);
			ReadableMerchantStore readableStore = merchantPopulator.populate(source.getMerchant(), null, store, source.getMerchant().getDefaultLanguage());
			System.out.println("$#10655#"); target.setStore(readableStore);
		}
		
		
		System.out.println("$#10656#"); if(source.getCustomerAgreement()!=null) {
			System.out.println("$#10657#"); target.setCustomerAgreed(source.getCustomerAgreement());
		}
		System.out.println("$#10658#"); if(source.getConfirmedAddress()!=null) {
			System.out.println("$#10659#"); target.setConfirmedAddress(source.getConfirmedAddress());
		}
		
		com.salesmanager.shop.model.order.total.OrderTotal taxTotal = null;
		com.salesmanager.shop.model.order.total.OrderTotal shippingTotal = null;
		
		
		System.out.println("$#10660#"); if(source.getBilling()!=null) {
			ReadableBilling address = new ReadableBilling();
			System.out.println("$#10661#"); address.setEmail(source.getCustomerEmailAddress());
			System.out.println("$#10662#"); address.setCity(source.getBilling().getCity());
			System.out.println("$#10663#"); address.setAddress(source.getBilling().getAddress());
			System.out.println("$#10664#"); address.setCompany(source.getBilling().getCompany());
			System.out.println("$#10665#"); address.setFirstName(source.getBilling().getFirstName());
			System.out.println("$#10666#"); address.setLastName(source.getBilling().getLastName());
			System.out.println("$#10667#"); address.setPostalCode(source.getBilling().getPostalCode());
			System.out.println("$#10668#"); address.setPhone(source.getBilling().getTelephone());
			System.out.println("$#10669#"); if(source.getBilling().getCountry()!=null) {
				System.out.println("$#10670#"); address.setCountry(source.getBilling().getCountry().getIsoCode());
			}
			System.out.println("$#10671#"); if(source.getBilling().getZone()!=null) {
				System.out.println("$#10672#"); address.setZone(source.getBilling().getZone().getCode());
			}
			
			System.out.println("$#10673#"); target.setBilling(address);
		}
		
		System.out.println("$#10675#"); System.out.println("$#10674#"); if(source.getOrderAttributes()!=null && source.getOrderAttributes().size()>0) {
			for(OrderAttribute attr : source.getOrderAttributes()) {
				com.salesmanager.shop.model.order.OrderAttribute a = new com.salesmanager.shop.model.order.OrderAttribute();
				System.out.println("$#10677#"); a.setKey(attr.getKey());
				System.out.println("$#10678#"); a.setValue(attr.getValue());
				target.getAttributes().add(a);
			}
		}
		
		System.out.println("$#10679#"); if(source.getDelivery()!=null) {
			ReadableDelivery address = new ReadableDelivery();
			System.out.println("$#10680#"); address.setCity(source.getDelivery().getCity());
			System.out.println("$#10681#"); address.setAddress(source.getDelivery().getAddress());
			System.out.println("$#10682#"); address.setCompany(source.getDelivery().getCompany());
			System.out.println("$#10683#"); address.setFirstName(source.getDelivery().getFirstName());
			System.out.println("$#10684#"); address.setLastName(source.getDelivery().getLastName());
			System.out.println("$#10685#"); address.setPostalCode(source.getDelivery().getPostalCode());
			System.out.println("$#10686#"); address.setPhone(source.getDelivery().getTelephone());
			System.out.println("$#10687#"); if(source.getDelivery().getCountry()!=null) {
				System.out.println("$#10688#"); address.setCountry(source.getDelivery().getCountry().getIsoCode());
			}
			System.out.println("$#10689#"); if(source.getDelivery().getZone()!=null) {
				System.out.println("$#10690#"); address.setZone(source.getDelivery().getZone().getCode());
			}
			
			System.out.println("$#10691#"); target.setDelivery(address);
		}
		
		List<com.salesmanager.shop.model.order.total.OrderTotal> totals = new ArrayList<com.salesmanager.shop.model.order.total.OrderTotal>();
		for(OrderTotal t : source.getOrderTotal()) {
			System.out.println("$#10692#"); if(t.getOrderTotalType()==null) {
				continue;
			}
			System.out.println("$#10693#"); if(t.getOrderTotalType().name().equals(OrderTotalType.TOTAL.name())) {
				com.salesmanager.shop.model.order.total.OrderTotal totalTotal = createTotal(t);
				System.out.println("$#10694#"); target.setTotal(totalTotal);
				totals.add(totalTotal);
			}
			else if(t.getOrderTotalType().name().equals(OrderTotalType.TAX.name())) { System.out.println("$#10695#");
				com.salesmanager.shop.model.order.total.OrderTotal totalTotal = createTotal(t);
				System.out.println("$#10696#"); if(taxTotal==null) {
					taxTotal = totalTotal;
				} else {
					BigDecimal v = taxTotal.getValue();
					v = v.add(totalTotal.getValue());
					System.out.println("$#10697#"); taxTotal.setValue(v);
				}
				System.out.println("$#10698#"); target.setTax(totalTotal);
				totals.add(totalTotal);
			}
			else if(t.getOrderTotalType().name().equals(OrderTotalType.SHIPPING.name())) { System.out.println("$#10699#");
				com.salesmanager.shop.model.order.total.OrderTotal totalTotal = createTotal(t);
				System.out.println("$#10700#"); if(shippingTotal==null) {
					shippingTotal = totalTotal;
				} else {
					BigDecimal v = shippingTotal.getValue();
					v = v.add(totalTotal.getValue());
					System.out.println("$#10701#"); shippingTotal.setValue(v);
				}
				System.out.println("$#10702#"); target.setShipping(totalTotal);
				totals.add(totalTotal);
			}
			else if(t.getOrderTotalType().name().equals(OrderTotalType.HANDLING.name())) { System.out.println("$#10703#");
				com.salesmanager.shop.model.order.total.OrderTotal totalTotal = createTotal(t);
				System.out.println("$#10704#"); if(shippingTotal==null) {
					shippingTotal = totalTotal;
				} else {
					BigDecimal v = shippingTotal.getValue();
					v = v.add(totalTotal.getValue());
					System.out.println("$#10705#"); shippingTotal.setValue(v);
				}
				System.out.println("$#10706#"); target.setShipping(totalTotal);
				totals.add(totalTotal);
			}
			else if(t.getOrderTotalType().name().equals(OrderTotalType.SUBTOTAL.name())) { System.out.println("$#10707#");
				com.salesmanager.shop.model.order.total.OrderTotal subTotal = createTotal(t);
				totals.add(subTotal);
				
			}
			else {
				com.salesmanager.shop.model.order.total.OrderTotal otherTotal = createTotal(t);
				totals.add(otherTotal);
			}
		}
		
		System.out.println("$#10708#"); target.setTotals(totals);
		
		System.out.println("$#10709#"); return target;
	}
	
	private com.salesmanager.shop.model.order.total.OrderTotal createTotal(OrderTotal t) {
		com.salesmanager.shop.model.order.total.OrderTotal totalTotal = new com.salesmanager.shop.model.order.total.OrderTotal();
		System.out.println("$#10710#"); totalTotal.setCode(t.getOrderTotalCode());
		System.out.println("$#10711#"); totalTotal.setId(t.getId());
		System.out.println("$#10712#"); totalTotal.setModule(t.getModule());
		System.out.println("$#10713#"); totalTotal.setOrder(t.getSortOrder());
		System.out.println("$#10714#"); totalTotal.setValue(t.getValue());
		System.out.println("$#10715#"); return totalTotal;
	}

	@Override
	protected ReadableOrder createTarget() {

		return null;
	}

}
