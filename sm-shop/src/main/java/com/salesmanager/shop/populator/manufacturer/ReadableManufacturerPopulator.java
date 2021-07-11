package com.salesmanager.shop.populator.manufacturer;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.catalog.product.manufacturer.ManufacturerDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.catalog.manufacturer.ReadableManufacturer;
import com.salesmanager.shop.model.catalog.manufacturer.ReadableManufacturerFull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ReadableManufacturerPopulator extends
    AbstractDataPopulator<com.salesmanager.core.model.catalog.product.manufacturer.Manufacturer, ReadableManufacturer> {



  @Override
  public ReadableManufacturer populate(
      com.salesmanager.core.model.catalog.product.manufacturer.Manufacturer source,
      ReadableManufacturer target, MerchantStore store, Language language)
      throws ConversionException {


				System.out.println("$#10448#"); if (language == null) {
      target = new ReadableManufacturerFull();
    }
				System.out.println("$#10449#"); target.setOrder(source.getOrder());
				System.out.println("$#10450#"); target.setId(source.getId());
				System.out.println("$#10451#"); target.setCode(source.getCode());
				System.out.println("$#10453#"); System.out.println("$#10452#"); if (source.getDescriptions() != null && source.getDescriptions().size() > 0) {

      List<com.salesmanager.shop.model.catalog.manufacturer.ManufacturerDescription> fulldescriptions =
          new ArrayList<com.salesmanager.shop.model.catalog.manufacturer.ManufacturerDescription>();

      Set<ManufacturerDescription> descriptions = source.getDescriptions();
      ManufacturerDescription description = null;
      for (ManufacturerDescription desc : descriptions) {
								System.out.println("$#10455#"); if (language != null && desc.getLanguage().getCode().equals(language.getCode())) {
          description = desc;
          break;
        } else {
          fulldescriptions.add(populateDescription(desc));
        }
      }



						System.out.println("$#10457#"); if (description != null) {
        com.salesmanager.shop.model.catalog.manufacturer.ManufacturerDescription d =
            populateDescription(description);
								System.out.println("$#10458#"); target.setDescription(d);
      }

						System.out.println("$#10459#"); if (target instanceof ReadableManufacturerFull) {
								System.out.println("$#10460#"); ((ReadableManufacturerFull) target).setDescriptions(fulldescriptions);
      }

    }



				System.out.println("$#10461#"); return target;
  }

  @Override
  protected ReadableManufacturer createTarget() {
    return null;
  }

  com.salesmanager.shop.model.catalog.manufacturer.ManufacturerDescription populateDescription(
      ManufacturerDescription description) {
				System.out.println("$#10462#"); if (description == null) {
      return null;
    }
    com.salesmanager.shop.model.catalog.manufacturer.ManufacturerDescription d =
        new com.salesmanager.shop.model.catalog.manufacturer.ManufacturerDescription();
				System.out.println("$#10463#"); d.setName(description.getName());
				System.out.println("$#10464#"); d.setDescription(description.getDescription());
				System.out.println("$#10465#"); d.setId(description.getId());
				System.out.println("$#10466#"); d.setTitle(description.getTitle());
				System.out.println("$#10467#"); if (description.getLanguage() != null) {
						System.out.println("$#10468#"); d.setLanguage(description.getLanguage().getCode());
    }
				System.out.println("$#10469#"); return d;
  }

}
