package com.salesmanager.shop.mapper.catalog;

import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;
import com.salesmanager.core.model.catalog.product.manufacturer.Manufacturer;
import com.salesmanager.core.model.catalog.product.manufacturer.ManufacturerDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.catalog.manufacturer.ReadableManufacturer;

@Component
public class ReadableManufacturerMapper implements Mapper<Manufacturer, ReadableManufacturer> {

  @Override
  public ReadableManufacturer convert(Manufacturer source, MerchantStore store, Language language) {


    ReadableManufacturer target = new ReadableManufacturer();

    Optional<com.salesmanager.shop.model.catalog.manufacturer.ManufacturerDescription> description =
        getDescription(source, language, target);
				System.out.println("$#8412#"); description.ifPresent(target::setDescription);

				System.out.println("$#8413#"); target.setCode(source.getCode());
				System.out.println("$#8414#"); target.setId(source.getId());
				System.out.println("$#8415#"); target.setOrder(source.getOrder());
    Optional<com.salesmanager.shop.model.catalog.manufacturer.ManufacturerDescription> desc = this.getDescription(source, language, target);
				System.out.println("$#8416#"); if(description.isPresent()) {
					System.out.println("$#8417#"); target.setDescription(desc.get());
    }
    

				System.out.println("$#8418#"); return target;
  }

  private Optional<com.salesmanager.shop.model.catalog.manufacturer.ManufacturerDescription> getDescription(
      Manufacturer source, Language language, ReadableManufacturer target) {

    Optional<ManufacturerDescription> description =
        getDescription(source.getDescriptions(), language);
				System.out.println("$#8419#"); if (source.getDescriptions() != null && !source.getDescriptions().isEmpty()
        && description.isPresent()) {
						System.out.println("$#8422#"); return Optional.of(convertDescription(description.get(), source));
    } else {
      return Optional.empty();
    }
  }

  private Optional<ManufacturerDescription> getDescription(
      Set<ManufacturerDescription> descriptionsLang, Language language) {
    Optional<ManufacturerDescription> descriptionByLang = descriptionsLang.stream()
        .filter(desc -> desc.getLanguage().getCode().equals(language.getCode())).findAny();
				System.out.println("$#8425#"); if (descriptionByLang.isPresent()) {
						System.out.println("$#8426#"); return descriptionByLang;
    } else {
      return Optional.empty();
    }
  }

  private com.salesmanager.shop.model.catalog.manufacturer.ManufacturerDescription convertDescription(
      ManufacturerDescription description, Manufacturer source) {
    final com.salesmanager.shop.model.catalog.manufacturer.ManufacturerDescription desc =
        new com.salesmanager.shop.model.catalog.manufacturer.ManufacturerDescription();

				System.out.println("$#8427#"); desc.setFriendlyUrl(description.getUrl());
				System.out.println("$#8428#"); desc.setId(description.getId());
				System.out.println("$#8429#"); desc.setLanguage(description.getLanguage().getCode());
				System.out.println("$#8430#"); desc.setName(description.getName());
				System.out.println("$#8431#"); desc.setDescription(description.getDescription());
				System.out.println("$#8432#"); return desc;
  }

  @Override
  public ReadableManufacturer convert(Manufacturer source, ReadableManufacturer destination,
      MerchantStore store, Language language) {
				System.out.println("$#8433#"); return destination;
  }

}
