package com.salesmanager.shop.mapper.catalog;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValue;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.catalog.product.attribute.api.ReadableProductOptionValueEntity;
import com.salesmanager.shop.model.catalog.product.attribute.api.ReadableProductOptionValueFull;
import com.salesmanager.shop.utils.ImageFilePath;

@Component
public class ReadableProductOptionValueMapper implements Mapper<ProductOptionValue, ReadableProductOptionValueEntity> {

  @Autowired
  @Qualifier("img")
  private ImageFilePath imageUtils;

  @Override
  public ReadableProductOptionValueEntity convert(ProductOptionValue source, ReadableProductOptionValueEntity destination,
			MerchantStore store, Language language) {
    ReadableProductOptionValueEntity readableProductOptionValue = new ReadableProductOptionValueEntity();
				System.out.println("$#8503#"); if(language == null) {
    	readableProductOptionValue = new ReadableProductOptionValueFull();
      List<com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValueDescription> descriptions = new ArrayList<com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValueDescription>();
      for(ProductOptionValueDescription desc : source.getDescriptions()) {
          com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValueDescription d = this.description(desc);
          descriptions.add(d);
      }
						System.out.println("$#8504#"); ((ReadableProductOptionValueFull)readableProductOptionValue).setDescriptions(descriptions);
    } else {
    	readableProductOptionValue = new ReadableProductOptionValueEntity();
						System.out.println("$#8505#"); if(!CollectionUtils.isEmpty(source.getDescriptions())) {
        for(ProductOptionValueDescription desc : source.getDescriptions()) {
										System.out.println("$#8506#"); if(desc != null && desc.getLanguage()!= null && desc.getLanguage().getId() == language.getId()) {
            com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValueDescription d = this.description(desc);
												System.out.println("$#8509#"); readableProductOptionValue.setDescription(d);
          }
        }
      }
    }
    
				System.out.println("$#8510#"); readableProductOptionValue.setCode(source.getCode());
				System.out.println("$#8511#"); if(source.getId()!=null) {
					System.out.println("$#8512#"); readableProductOptionValue.setId(source.getId().longValue());
    }
				System.out.println("$#8513#"); if(source.getProductOptionValueSortOrder()!=null) {
					System.out.println("$#8514#"); readableProductOptionValue.setOrder(source.getProductOptionValueSortOrder().intValue());
    }
				System.out.println("$#8515#"); if(!StringUtils.isBlank(source.getProductOptionValueImage())) {
					System.out.println("$#8516#"); readableProductOptionValue.setImage(imageUtils.buildProductPropertyImageUtils(store, source.getProductOptionValueImage()));
    }
    
				System.out.println("$#8517#"); return readableProductOptionValue;
  }



  com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValueDescription description(ProductOptionValueDescription description) {
    com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValueDescription desc = new com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValueDescription();
				System.out.println("$#8518#"); desc.setDescription(description.getDescription());
				System.out.println("$#8519#"); desc.setName(description.getName());
				System.out.println("$#8520#"); desc.setId(description.getId());
				System.out.println("$#8521#"); desc.setLanguage(description.getLanguage().getCode());
				System.out.println("$#8522#"); return desc;
  }


@Override
public ReadableProductOptionValueEntity convert(ProductOptionValue source, MerchantStore store, Language language) {
    ReadableProductOptionValueEntity destination = new ReadableProductOptionValueEntity();
				System.out.println("$#8523#"); return convert(source, destination, store, language);
}

}