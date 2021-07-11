package com.salesmanager.shop.mapper.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.catalog.category.CategoryDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.catalog.category.ReadableCategory;
import com.salesmanager.shop.model.catalog.category.ReadableCategoryFull;

@Component
public class ReadableCategoryMapper implements Mapper<Category, ReadableCategory> {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(ReadableCategoryMapper.class);

  @Override
  public ReadableCategory convert(Category source, MerchantStore store, Language language) {
    ReadableCategory target = category(language);
    
				System.out.println("$#8378#"); feedDescription(source, language, target);


    Optional<com.salesmanager.shop.model.catalog.category.Category> parentCategory =
        createParentCategory(source);
				System.out.println("$#8379#"); parentCategory.ifPresent(target::setParent);

				System.out.println("$#8380#"); Optional.ofNullable(source.getDepth()).ifPresent(target::setDepth);

				System.out.println("$#8381#"); target.setLineage(source.getLineage());
				System.out.println("$#8382#"); target.setStore(source.getMerchantStore().getCode());
				System.out.println("$#8383#"); target.setCode(source.getCode());
				System.out.println("$#8384#"); target.setId(source.getId());
				System.out.println("$#8385#"); target.setSortOrder(source.getSortOrder());
				System.out.println("$#8386#"); target.setVisible(source.isVisible());
				System.out.println("$#8387#"); target.setFeatured(source.isFeatured());
				System.out.println("$#8388#"); return target;
  }

  private void feedDescription(Category source, Language language, ReadableCategory target) {
    List<com.salesmanager.shop.model.catalog.category.CategoryDescription> descriptions = new ArrayList<com.salesmanager.shop.model.catalog.category.CategoryDescription>();
    for(CategoryDescription description : source.getDescriptions()) {
						System.out.println("$#8389#"); if (language == null) {
        descriptions.add(convertDescription(description));
      } else {
								System.out.println("$#8390#"); if(language.getId().intValue()==description.getLanguage().getId().intValue()) {
										System.out.println("$#8391#"); target.setDescription(convertDescription(description));
        }
      }
    }
    
    
				System.out.println("$#8392#"); if(target instanceof ReadableCategoryFull) {
						System.out.println("$#8393#"); ((ReadableCategoryFull)target).setDescriptions(descriptions);
    }

  }


  private com.salesmanager.shop.model.catalog.category.CategoryDescription convertDescription(
      CategoryDescription description) {
    final com.salesmanager.shop.model.catalog.category.CategoryDescription desc =
        new com.salesmanager.shop.model.catalog.category.CategoryDescription();

				System.out.println("$#8394#"); desc.setFriendlyUrl(description.getSeUrl());
				System.out.println("$#8395#"); desc.setName(description.getName());
				System.out.println("$#8396#"); desc.setId(description.getId());
				System.out.println("$#8397#"); desc.setDescription(description.getDescription());
				System.out.println("$#8398#"); desc.setKeyWords(description.getMetatagKeywords());
				System.out.println("$#8399#"); desc.setHighlights(description.getCategoryHighlight());
				System.out.println("$#8400#"); desc.setLanguage(description.getLanguage().getCode());
				System.out.println("$#8401#"); desc.setTitle(description.getMetatagTitle());
				System.out.println("$#8402#"); desc.setMetaDescription(description.getMetatagDescription());
				System.out.println("$#8403#"); return desc;
  }


  private Optional<com.salesmanager.shop.model.catalog.category.Category> createParentCategory(
      Category source) {
				System.out.println("$#8404#"); return Optional.ofNullable(source.getParent()).map(parentValue -> {
      final com.salesmanager.shop.model.catalog.category.Category parent =
          new com.salesmanager.shop.model.catalog.category.Category();
						System.out.println("$#8405#"); parent.setCode(source.getParent().getCode());
						System.out.println("$#8406#"); parent.setId(source.getParent().getId());
						System.out.println("$#8407#"); return parent;
    });
  }

  private ReadableCategory category(Language language) {

				System.out.println("$#8408#"); if (language == null) {
						System.out.println("$#8409#"); return new ReadableCategoryFull();
    } else {
						System.out.println("$#8410#"); return new ReadableCategory();
    }

  }

  @Override
  public ReadableCategory convert(Category source, ReadableCategory destination,
      MerchantStore store, Language language) {
				System.out.println("$#8411#"); return destination;
  }
}
