package com.salesmanager.shop.mapper.tax;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.Validate;
import org.springframework.stereotype.Component;

import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.tax.taxrate.TaxRate;
import com.salesmanager.core.model.tax.taxrate.TaxRateDescription;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.tax.ReadableTaxRate;
import com.salesmanager.shop.model.tax.ReadableTaxRateDescription;

@Component
public class ReadableTaxRateMapper implements Mapper<TaxRate, ReadableTaxRate> {

	@Override
	public ReadableTaxRate convert(TaxRate source, MerchantStore store, Language language) {
		ReadableTaxRate taxRate = new ReadableTaxRate();
		System.out.println("$#8692#"); return this.convert(source, taxRate, store, language);

	}

	@Override
	public ReadableTaxRate convert(TaxRate source, ReadableTaxRate destination, MerchantStore store,
			Language language) {
		System.out.println("$#8693#"); Validate.notNull(destination, "destination TaxRate cannot be null");
		System.out.println("$#8694#"); Validate.notNull(source, "source TaxRate cannot be null");
		System.out.println("$#8695#"); destination.setId(source.getId());
		System.out.println("$#8696#"); destination.setCountry(source.getCountry().getIsoCode());
		System.out.println("$#8697#"); destination.setZone(source.getZone().getCode());
		System.out.println("$#8698#"); destination.setRate(source.getTaxRate().toString());
		System.out.println("$#8699#"); destination.setCode(source.getCode());
		System.out.println("$#8700#"); destination.setPriority(source.getTaxPriority());
		Optional<ReadableTaxRateDescription> description = this.convertDescription(source.getDescriptions(), language);
		System.out.println("$#8701#"); if(description.isPresent()) {
			System.out.println("$#8702#"); destination.setDescription(description.get());
		}
		System.out.println("$#8703#"); return destination;
	}
	
	private Optional<ReadableTaxRateDescription> convertDescription(List<TaxRateDescription> descriptions, Language language) {
		System.out.println("$#8704#"); Validate.notEmpty(descriptions,"List of TaxRateDescriptions should not be empty");
		
	    Optional<TaxRateDescription> description = descriptions.stream()
	            .filter(desc -> desc.getLanguage().getCode().equals(language.getCode())).findAny();
									System.out.println("$#8707#"); if (description.isPresent()) {
											System.out.println("$#8708#"); return Optional.of(convertDescription(description.get()));
	        } else {
	          return Optional.empty();
	        }
		
		
	}
	
	private ReadableTaxRateDescription convertDescription(TaxRateDescription desc) {
		ReadableTaxRateDescription d = new ReadableTaxRateDescription();
		System.out.println("$#8709#"); d.setDescription(desc.getDescription());
		System.out.println("$#8710#"); d.setName(desc.getName());
		System.out.println("$#8711#"); d.setLanguage(desc.getLanguage().getCode());
		System.out.println("$#8712#"); d.setDescription(desc.getDescription());
		System.out.println("$#8713#"); d.setId(desc.getId());
		System.out.println("$#8714#"); d.setTitle(desc.getTitle());
		System.out.println("$#8715#"); return d;
	}



}
