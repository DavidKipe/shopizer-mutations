package com.salesmanager.shop.mapper.inventory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.helper.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.model.catalog.product.availability.ProductAvailability;
import com.salesmanager.core.model.catalog.product.price.ProductPrice;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.catalog.product.ReadableProductPrice;
import com.salesmanager.shop.model.catalog.product.inventory.ReadableInventory;
import com.salesmanager.shop.model.store.ReadableMerchantStore;
import com.salesmanager.shop.populator.catalog.ReadableProductPricePopulator;
import com.salesmanager.shop.populator.store.ReadableMerchantStorePopulator;
import com.salesmanager.shop.store.api.exception.ConversionRuntimeException;
import com.salesmanager.shop.utils.DateUtil;

@Component
public class ReadableInventoryMapper implements Mapper<ProductAvailability, ReadableInventory> {

  @Autowired
  private CountryService countryService;

  @Autowired
  private ZoneService zoneService;
  
  @Autowired
  private PricingService pricingService;

  @Override
  public ReadableInventory convert(ProductAvailability source, MerchantStore store,
      Language language) {
    ReadableInventory availability = new ReadableInventory();
				System.out.println("$#8589#"); return this.convert(source, availability, store, language);
  }

  @Override
  public ReadableInventory convert(ProductAvailability source, ReadableInventory destination,
      MerchantStore store, Language language) {
				System.out.println("$#8590#"); Validate.notNull(destination, "Destination Product availability cannot be null");
				System.out.println("$#8591#"); Validate.notNull(source, "Source Product availability cannot be null");

    try {
						System.out.println("$#8592#"); destination.setQuantity(
          source.getProductQuantity() != null ? source.getProductQuantity().intValue() : 0);
						System.out.println("$#8595#"); destination.setProductQuantityOrderMax(source.getProductQuantityOrderMax() != null
          ? source.getProductQuantityOrderMax().intValue() : 0);
						System.out.println("$#8597#"); destination.setProductQuantityOrderMin(source.getProductQuantityOrderMin() != null
          ? source.getProductQuantityOrderMin().intValue() : 0);
						System.out.println("$#8598#"); destination.setOwner(source.getOwner());
						System.out.println("$#8599#"); destination.setId(source.getId());
						System.out.println("$#8600#"); destination.setRegion(source.getRegion());
						System.out.println("$#8601#"); destination.setRegionVariant(source.getRegionVariant());
						System.out.println("$#8602#"); destination.setStore(store(store, language));
						System.out.println("$#8603#"); if(source.getAvailable()!=null) {
								System.out.println("$#8604#"); if(source.getProductDateAvailable()!=null) {
          boolean isAfter = LocalDate.parse(DateUtil.getPresentDate())
              .isAfter(LocalDate.parse(DateUtil.formatDate(source.getProductDateAvailable())));
										System.out.println("$#8605#"); if(isAfter && source.getAvailable().booleanValue()) {
												System.out.println("$#8607#"); destination.setAvailable(true);
          }
										System.out.println("$#8608#"); destination.setDateAvailable(DateUtil.formatDate(source.getProductDateAvailable()));
        } else {
										System.out.println("$#8609#"); destination.setAvailable(source.getAvailable().booleanValue());
        }
      }
      
						System.out.println("$#8610#"); if(source.getAuditSection()!=null) {
								System.out.println("$#8611#"); if(source.getAuditSection().getDateCreated()!=null) {
										System.out.println("$#8612#"); destination.setCreationDate(DateUtil.formatDate(source.getAuditSection().getDateCreated()));
        }
      }
      
      List<ReadableProductPrice> prices = prices(source, store, language);
						System.out.println("$#8613#"); destination.setPrices(prices);


      
    } catch (Exception e) {
      throw new ConversionRuntimeException("Error while converting Inventory", e);
    }



				System.out.println("$#8614#"); return destination;
  }

  private ReadableMerchantStore store(MerchantStore store, Language language)
      throws ConversionException {
				System.out.println("$#8615#"); if(language == null) {
      language = store.getDefaultLanguage();
    }
    ReadableMerchantStorePopulator populator = new ReadableMerchantStorePopulator();
				System.out.println("$#8616#"); populator.setCountryService(countryService);
				System.out.println("$#8617#"); populator.setZoneService(zoneService);
				System.out.println("$#8618#"); return populator.populate(store, new ReadableMerchantStore(), store, language);
  }
  
  private List<ReadableProductPrice> prices(ProductAvailability source, MerchantStore store, Language language) throws ConversionException {
    
    ReadableProductPricePopulator populator = null;
    List<ReadableProductPrice> prices = new ArrayList<ReadableProductPrice>();
    
    for(ProductPrice price : source.getPrices()) {
        
      populator = new ReadableProductPricePopulator();
						System.out.println("$#8619#"); populator.setPricingService(pricingService);
      ReadableProductPrice p = populator.populate(price, new ReadableProductPrice(), store, language);
      prices.add(p);
    
    }
				System.out.println("$#8620#"); return prices;
  }


}
