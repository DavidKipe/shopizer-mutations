package com.salesmanager.shop.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.catalog.category.CategoryDescription;
import com.salesmanager.core.model.reference.language.Language;

public class CategoryUtils {
  
  
  public static com.salesmanager.shop.admin.model.catalog.Category readableCategoryConverter(Category category, Language language) {
    com.salesmanager.shop.admin.model.catalog.Category readableCategory = new com.salesmanager.shop.admin.model.catalog.Category();
				System.out.println("$#15589#"); readableCategory.setCategory(category);
    
    List<CategoryDescription> descriptions = new ArrayList<CategoryDescription>(category.getDescriptions());
    
    //descriptions
    //.stream();
    //.filter(desc -> desc.getLanguage().getCode().equals(language.getCode()));
    
    
				System.out.println("$#15590#"); readableCategory.setDescriptions(descriptions);
				System.out.println("$#15591#"); return readableCategory;
  }
  
  public static List<com.salesmanager.shop.admin.model.catalog.Category> readableCategoryListConverter(List<Category> categories, Language language) {
    
    List<com.salesmanager.shop.admin.model.catalog.Category> readableCategories = 
        categories.stream()
         .map(cat -> readableCategoryConverter(cat, language))
         .collect(Collectors.toList());
    
				System.out.println("$#15593#"); return readableCategories;
    
  }

}
