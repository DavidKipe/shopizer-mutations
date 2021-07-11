package com.salesmanager.shop.mapper.catalog;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.catalog.product.attribute.ProductOption;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.catalog.product.attribute.api.PersistableProductOptionEntity;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;

@Component
public class PersistableProductOptionMapper implements Mapper<PersistableProductOptionEntity, ProductOption> {

  @Autowired
  private LanguageService languageService;



  ProductOptionDescription description(com.salesmanager.shop.model.catalog.product.attribute.ProductOptionDescription description) throws Exception {
				System.out.println("$#8273#"); Validate.notNull(description.getLanguage(),"description.language should not be null");
    ProductOptionDescription desc = new ProductOptionDescription();
				System.out.println("$#8274#"); desc.setId(null);
				System.out.println("$#8275#"); desc.setDescription(description.getDescription());
				System.out.println("$#8276#"); desc.setName(description.getName());
				System.out.println("$#8278#"); System.out.println("$#8277#"); if(description.getId() != null && description.getId().longValue()>0) {
						System.out.println("$#8280#"); desc.setId(description.getId());
    }
    Language lang = languageService.getByCode(description.getLanguage());
				System.out.println("$#8281#"); desc.setLanguage(lang);
				System.out.println("$#8282#"); return desc;
  }


  @Override
  public ProductOption convert(PersistableProductOptionEntity source, MerchantStore store,
      Language language) {
    ProductOption destination = new ProductOption();
				System.out.println("$#8283#"); return convert(source, destination, store, language);
  }


  @Override
  public ProductOption convert(PersistableProductOptionEntity source, ProductOption destination,
      MerchantStore store, Language language) {
				System.out.println("$#8284#"); if(destination == null) {
      destination = new ProductOption();
    }
    
    try {

						System.out.println("$#8285#"); if(!CollectionUtils.isEmpty(source.getDescriptions())) {
        for(com.salesmanager.shop.model.catalog.product.attribute.ProductOptionDescription desc : source.getDescriptions()) {
          ProductOptionDescription description = null;
										System.out.println("$#8286#"); if(!CollectionUtils.isEmpty(destination.getDescriptions())) {
            for(ProductOptionDescription d : destination.getDescriptions()) {
														System.out.println("$#8287#"); if(!StringUtils.isBlank(desc.getLanguage()) && desc.getLanguage().equals(d.getLanguage().getCode())) {
															System.out.println("$#8289#"); d.setDescription(desc.getDescription());
															System.out.println("$#8290#"); d.setName(desc.getName());
															System.out.println("$#8291#"); d.setTitle(desc.getTitle());
            	  description = d;
            	  break;
              } 
            }
          } 
										System.out.println("$#8292#"); if(description == null) {
	          description = description(desc);
											System.out.println("$#8293#"); description.setProductOption(destination);
	          destination.getDescriptions().add(description);
          }
        }
      }
      
						System.out.println("$#8294#"); destination.setCode(source.getCode());
						System.out.println("$#8295#"); destination.setMerchantStore(store);
						System.out.println("$#8296#"); destination.setProductOptionSortOrder(source.getOrder());
						System.out.println("$#8297#"); destination.setProductOptionType(source.getType());
						System.out.println("$#8298#"); destination.setReadOnly(source.isReadOnly());


						System.out.println("$#8299#"); return destination;
      } catch (Exception e) {
        throw new ServiceRuntimeException("Error while converting product option", e);
      }
  }

}