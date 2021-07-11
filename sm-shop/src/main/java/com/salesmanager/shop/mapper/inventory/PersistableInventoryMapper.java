package com.salesmanager.shop.mapper.inventory;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.catalog.product.availability.ProductAvailability;
import com.salesmanager.core.model.catalog.product.price.ProductPrice;
import com.salesmanager.core.model.catalog.product.price.ProductPriceDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.catalog.product.PersistableProductPrice;
import com.salesmanager.shop.model.catalog.product.inventory.PersistableInventory;
import com.salesmanager.shop.store.api.exception.ConversionRuntimeException;
import com.salesmanager.shop.utils.DateUtil;

@Component
public class PersistableInventoryMapper implements Mapper<PersistableInventory, ProductAvailability> {
  
  
  @Autowired
  private LanguageService languageService;

  @Override
  public ProductAvailability convert(PersistableInventory source, MerchantStore store,
      Language language) {
    ProductAvailability availability = new ProductAvailability();
				System.out.println("$#8530#"); availability.setMerchantStore(store);
				System.out.println("$#8531#"); return convert(source, availability, store, language);
    
  }

  @Override
  public ProductAvailability convert(PersistableInventory source, ProductAvailability destination,
      MerchantStore store, Language language) {
				System.out.println("$#8532#"); Validate.notNull(destination, "Product availability cannot be null");
    
    try {

				System.out.println("$#8533#"); if(destination == null) {
      destination = new ProductAvailability();
    }


				System.out.println("$#8534#"); destination.setProductQuantity(source.getQuantity());
				System.out.println("$#8535#"); destination.setProductQuantityOrderMin(source.getProductQuantityOrderMax());
				System.out.println("$#8536#"); destination.setProductQuantityOrderMax(source.getProductQuantityOrderMin());
				System.out.println("$#8537#"); destination.setAvailable(source.isAvailable());
				System.out.println("$#8538#"); destination.setOwner(source.getOwner());
				System.out.println("$#8539#"); if(!StringUtils.isBlank(source.getRegion())) {
						System.out.println("$#8540#"); destination.setRegion(source.getRegion());
    } else {
						System.out.println("$#8541#"); destination.setRegion(Constants.ALL_REGIONS);
    }
    
				System.out.println("$#8542#"); destination.setRegionVariant(source.getRegionVariant());
				System.out.println("$#8543#"); if(!StringUtils.isBlank(source.getDateAvailable())) {
						System.out.println("$#8544#"); destination.setProductDateAvailable(DateUtil.getDate(source.getDateAvailable()));
    }

    for(PersistableProductPrice priceEntity : source.getPrices()) {
      
      ProductPrice price = new ProductPrice();
						System.out.println("$#8545#"); price.setId(null);
						System.out.println("$#8547#"); System.out.println("$#8546#"); if(priceEntity.getId()!=null && priceEntity.getId().longValue()>0) {
							System.out.println("$#8549#"); price.setId(priceEntity.getId());
      }
      Set<ProductPrice> prices = new HashSet<ProductPrice>();
						System.out.println("$#8550#"); if(destination.getPrices()!=null) {
        for(ProductPrice pp : destination.getPrices()) {
										System.out.println("$#8552#"); System.out.println("$#8551#"); if(priceEntity.getId()!=null && priceEntity.getId().longValue()>0 && priceEntity.getId().longValue() == pp.getId().longValue()) {
            price = pp;
												System.out.println("$#8555#"); price.setId(pp.getId());
          }
        }
      }

						System.out.println("$#8556#"); price.setProductAvailability(destination);
						System.out.println("$#8557#"); price.setDefaultPrice(priceEntity.isDefaultPrice());
						System.out.println("$#8558#"); price.setProductPriceAmount(priceEntity.getOriginalPrice());
						System.out.println("$#8559#"); price.setDefaultPrice(priceEntity.isDefaultPrice());
						System.out.println("$#8560#"); price.setCode(priceEntity.getCode());
						System.out.println("$#8561#"); price.setProductPriceSpecialAmount(priceEntity.getDiscountedPrice());
						System.out.println("$#8562#"); if(priceEntity.getDiscountStartDate()!=null) {
          Date startDate = DateUtil.getDate(priceEntity.getDiscountStartDate());
										System.out.println("$#8563#"); price.setProductPriceSpecialStartDate(startDate);
      }
						System.out.println("$#8564#"); if(priceEntity.getDiscountEndDate()!=null) {
          Date endDate = DateUtil.getDate(priceEntity.getDiscountEndDate());
										System.out.println("$#8565#"); price.setProductPriceSpecialEndDate(endDate);
      }
      //destination.getPrices().add(price);
      
						System.out.println("$#8566#"); price.setProductAvailability(destination);
      
      java.util.List<com.salesmanager.shop.model.catalog.product.ProductPriceDescription> descriptions = priceEntity.getDescriptions();
						System.out.println("$#8567#"); if(descriptions != null) {
        Set<ProductPriceDescription> descs = new HashSet<ProductPriceDescription>();
        for(com.salesmanager.shop.model.catalog.product.ProductPriceDescription desc : descriptions) {
          ProductPriceDescription description = null;
										System.out.println("$#8568#"); if(!CollectionUtils.isEmpty(price.getDescriptions())) {
            for(ProductPriceDescription d : price.getDescriptions()) {
														System.out.println("$#8570#"); System.out.println("$#8569#"); if(desc.getId() != null && desc.getId().longValue() > 0 && desc.getId().longValue() == d.getId().longValue()) {
																System.out.println("$#8573#"); desc.setId(d.getId());
              }
            }
          }
          description = getDescription(desc);
										System.out.println("$#8574#"); description.setProductPrice(price);
          descs.add(description);
        }
								System.out.println("$#8575#"); price.setDescriptions(descs);
      }
      prices.add(price);
						System.out.println("$#8576#"); destination.setPrices(prices);
    }
    
				System.out.println("$#8577#"); return destination;
    
    } catch(Exception e) {
      throw new ConversionRuntimeException(e);
    }

  }
  
  private ProductPriceDescription getDescription(com.salesmanager.shop.model.catalog.product.ProductPriceDescription desc) throws ConversionException {
    ProductPriceDescription target = new ProductPriceDescription();
				System.out.println("$#8578#"); target.setDescription(desc.getDescription());
				System.out.println("$#8579#"); target.setName(desc.getName());
				System.out.println("$#8580#"); target.setTitle(desc.getTitle());
				System.out.println("$#8581#"); target.setId(null);
				System.out.println("$#8583#"); System.out.println("$#8582#"); if(desc.getId()!=null && desc.getId().longValue()>0) {
						System.out.println("$#8585#"); target.setId(desc.getId());
    }

    Language lang;
    try {
      lang = languageService.getByCode(desc.getLanguage());
						System.out.println("$#8586#"); target.setLanguage(lang);
						System.out.println("$#8587#"); if(lang==null) {
        throw new ConversionException("Language is null for code " + desc.getLanguage() + " use language ISO code [en, fr ...]");
    }
    } catch (ServiceException e) {
      throw new ConversionException(e);
    }

				System.out.println("$#8588#"); return target;

  }

}
