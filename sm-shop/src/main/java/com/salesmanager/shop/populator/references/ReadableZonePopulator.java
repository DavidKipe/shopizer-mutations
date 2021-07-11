package com.salesmanager.shop.populator.references;

import org.apache.commons.collections4.CollectionUtils;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.core.model.reference.zone.ZoneDescription;
import com.salesmanager.shop.model.references.ReadableZone;

public class ReadableZonePopulator extends AbstractDataPopulator<Zone, ReadableZone> {

	@Override
	public ReadableZone populate(Zone source, ReadableZone target, MerchantStore store, Language language)
			throws ConversionException {
		System.out.println("$#10899#"); if(target==null) {
			target = new ReadableZone();
		}
		
		System.out.println("$#10900#"); target.setId(source.getId());
		System.out.println("$#10901#"); target.setCode(source.getCode());
		System.out.println("$#10902#"); target.setCountryCode(source.getCountry().getIsoCode());
		
		System.out.println("$#10903#"); if(!CollectionUtils.isEmpty(source.getDescriptions())) {
			for(ZoneDescription d : source.getDescriptions()) {
				System.out.println("$#10904#"); if(d.getLanguage().getId() == language.getId()) {
					System.out.println("$#10905#"); target.setName(d.getName());
					continue;
				}
			}
		}
		
		System.out.println("$#10906#"); return target;
		
	}

	@Override
	protected ReadableZone createTarget() {
		// TODO Auto-generated method stub
		return null;
	}

}
