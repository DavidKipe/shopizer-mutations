package com.salesmanager.shop.mapper.catalog;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import com.salesmanager.core.model.catalog.product.attribute.ProductOption;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.catalog.product.attribute.api.ReadableProductOptionEntity;
import com.salesmanager.shop.model.catalog.product.attribute.api.ReadableProductOptionFull;

@Component
public class ReadableProductOptionMapper implements Mapper<ProductOption, ReadableProductOptionEntity> {

  @Override
  public ReadableProductOptionEntity convert(ProductOption source, MerchantStore store,
      Language language) {
    ReadableProductOptionEntity destination = new ReadableProductOptionEntity();
				System.out.println("$#8451#"); return convert(source, destination, store, language);
  }


  @Override
  public ReadableProductOptionEntity convert(ProductOption source,
      ReadableProductOptionEntity destination, MerchantStore store, Language language) {
    ReadableProductOptionEntity readableProductOption = new ReadableProductOptionEntity();
				System.out.println("$#8452#"); if(language == null) {
      readableProductOption = new ReadableProductOptionFull();
      List<com.salesmanager.shop.model.catalog.product.attribute.ProductOptionDescription> descriptions = new ArrayList<com.salesmanager.shop.model.catalog.product.attribute.ProductOptionDescription>();
      for(ProductOptionDescription desc : source.getDescriptions()) {
          com.salesmanager.shop.model.catalog.product.attribute.ProductOptionDescription d = this.description(desc);
          descriptions.add(d);
      }
						System.out.println("$#8453#"); ((ReadableProductOptionFull)readableProductOption).setDescriptions(descriptions);
    } else {
      readableProductOption = new ReadableProductOptionEntity();
						System.out.println("$#8454#"); if(!CollectionUtils.isEmpty(source.getDescriptions())) {
        for(ProductOptionDescription desc : source.getDescriptions()) {
										System.out.println("$#8455#"); if(desc != null && desc.getLanguage()!= null && desc.getLanguage().getId() == language.getId()) {
            com.salesmanager.shop.model.catalog.product.attribute.ProductOptionDescription d = this.description(desc);
												System.out.println("$#8458#"); readableProductOption.setDescription(d);
          }
        }
      }
    }
    
				System.out.println("$#8459#"); readableProductOption.setCode(source.getCode());
				System.out.println("$#8460#"); readableProductOption.setId(source.getId());
				System.out.println("$#8461#"); readableProductOption.setType(source.getProductOptionType());
    
    
				System.out.println("$#8462#"); return readableProductOption;
  }



  com.salesmanager.shop.model.catalog.product.attribute.ProductOptionDescription description(ProductOptionDescription description) {
    com.salesmanager.shop.model.catalog.product.attribute.ProductOptionDescription desc = new com.salesmanager.shop.model.catalog.product.attribute.ProductOptionDescription();
				System.out.println("$#8463#"); desc.setDescription(description.getDescription());
				System.out.println("$#8464#"); desc.setName(description.getName());
				System.out.println("$#8465#"); desc.setId(description.getId());
				System.out.println("$#8466#"); desc.setLanguage(description.getLanguage().getCode());
				System.out.println("$#8467#"); return desc;
  }

}