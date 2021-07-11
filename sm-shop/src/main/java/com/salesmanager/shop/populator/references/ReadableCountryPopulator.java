package com.salesmanager.shop.populator.references;

import org.apache.commons.collections4.CollectionUtils;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.core.model.reference.zone.ZoneDescription;
import com.salesmanager.shop.model.references.ReadableCountry;
import com.salesmanager.shop.model.references.ReadableZone;

public class ReadableCountryPopulator extends AbstractDataPopulator<Country, ReadableCountry> {

	@Override
	public ReadableCountry populate(Country source, ReadableCountry target, MerchantStore store, Language language)
			throws ConversionException {
		
		System.out.println("$#10886#"); if(target==null) {
			target = new ReadableCountry();
		}
		
		System.out.println("$#10887#"); target.setId(new Long(source.getId()));
		System.out.println("$#10888#"); target.setCode(source.getIsoCode());
		System.out.println("$#10889#"); target.setSupported(source.getSupported());
		System.out.println("$#10890#"); if(!CollectionUtils.isEmpty(source.getDescriptions())) {
			System.out.println("$#10891#"); target.setName(source.getDescriptions().iterator().next().getName());
	    }
		
		System.out.println("$#10892#"); if(!CollectionUtils.isEmpty(source.getZones())) {
			for(Zone z : source.getZones()) {
				ReadableZone readableZone = new ReadableZone();
				System.out.println("$#10893#"); readableZone.setCountryCode(target.getCode());
				System.out.println("$#10894#"); readableZone.setId(z.getId());
				System.out.println("$#10895#"); if(!CollectionUtils.isEmpty(z.getDescriptions())) {
					for(ZoneDescription d : z.getDescriptions()) {
						System.out.println("$#10896#"); if(d.getLanguage().getId() == language.getId()) {
							System.out.println("$#10897#"); readableZone.setName(d.getName());
							continue;
						}
					}
				}
				target.getZones().add(readableZone);
			}
		}
		
		System.out.println("$#10898#"); return target;
	}

	@Override
	protected ReadableCountry createTarget() {
		// TODO Auto-generated method stub
		return null;
	}

}
