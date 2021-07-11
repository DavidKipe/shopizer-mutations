package com.salesmanager.shop.populator.catalog;

import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;
import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.catalog.category.CategoryService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.catalog.category.CategoryDescription;
import com.salesmanager.shop.model.catalog.category.PersistableCategory;

@Component
public class PersistableCategoryPopulator extends
		AbstractDataPopulator<PersistableCategory, Category> {

	@Inject
	private CategoryService categoryService;
	@Inject
	private LanguageService languageService;


	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	public CategoryService getCategoryService() {
		System.out.println("$#9445#"); return categoryService;
	}

	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}

	public LanguageService getLanguageService() {
		System.out.println("$#9446#"); return languageService;
	}


	@Override
	public Category populate(PersistableCategory source, Category target,
			MerchantStore store, Language language)
			throws ConversionException {

		try {

		Validate.notNull(target, "Category target cannot be null");


/*		Validate.notNull(categoryService, "Requires to set CategoryService");
		Validate.notNull(languageService, "Requires to set LanguageService");*/

		System.out.println("$#9447#"); target.setMerchantStore(store);
		System.out.println("$#9448#"); target.setCode(source.getCode());
		System.out.println("$#9449#"); target.setSortOrder(source.getSortOrder());
		System.out.println("$#9450#"); target.setVisible(source.isVisible());
		System.out.println("$#9451#"); target.setFeatured(source.isFeatured());

		//children
		System.out.println("$#9452#"); if(!CollectionUtils.isEmpty(source.getChildren())) {
		  //no modifications to children category
		} else {
				System.out.println("$#9453#"); target.getCategories().clear();
		}

		//get parent

		System.out.println("$#9454#"); if(source.getParent()==null || (StringUtils.isBlank(source.getParent().getCode())) || source.getParent().getId()==null) {
			System.out.println("$#9457#"); target.setParent(null);
			System.out.println("$#9458#"); target.setDepth(0);
			System.out.println("$#9459#"); target.setLineage(new StringBuilder().append("/").append(source.getId()).append("/").toString());
		} else {
			Category parent = null;
			System.out.println("$#9460#"); if(!StringUtils.isBlank(source.getParent().getCode())) {
				 parent = categoryService.getByCode(store.getCode(), source.getParent().getCode());
			} else if(source.getParent().getId()!=null) { System.out.println("$#9461#");
				 parent = categoryService.getById(source.getParent().getId(), store.getId());
			} else {
				throw new ConversionException("Category parent needs at least an id or a code for reference");
			}
			System.out.println("$#9462#"); if(parent !=null && parent.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				throw new ConversionException("Store id does not belong to specified parent id");
			}

			System.out.println("$#9464#"); if(parent!=null) {
				System.out.println("$#9465#"); target.setParent(parent);

				String lineage = parent.getLineage();
				int depth = parent.getDepth();

				System.out.println("$#9467#"); System.out.println("$#9466#"); target.setDepth(depth+1);
				System.out.println("$#9468#"); target.setLineage(new StringBuilder().append(lineage).append(parent.getId()).append("/").toString());
			}

		}


		System.out.println("$#9469#"); if(!CollectionUtils.isEmpty(source.getChildren())) {

			for(PersistableCategory cat : source.getChildren()) {

				Category persistCategory = this.populate(cat, new Category(), store, language);
				target.getCategories().add(persistCategory);

			}

		}


		System.out.println("$#9470#"); if(!CollectionUtils.isEmpty(source.getDescriptions())) {
			Set<com.salesmanager.core.model.catalog.category.CategoryDescription> descriptions = new HashSet<com.salesmanager.core.model.catalog.category.CategoryDescription>();
			System.out.println("$#9471#"); if(CollectionUtils.isNotEmpty(target.getDescriptions())) {
    			for(com.salesmanager.core.model.catalog.category.CategoryDescription description : target.getDescriptions()) {
    			    for(CategoryDescription d : source.getDescriptions()) {
															System.out.println("$#9472#"); if(StringUtils.isBlank(d.getLanguage())) {
    			          throw new ConversionException("Source category description has no language");
    			        }
															System.out.println("$#9473#"); if(d.getLanguage().equals(description.getLanguage().getCode())) {
																System.out.println("$#9474#"); description.setCategory(target);
            				description = buildDescription(d, description);
            				descriptions.add(description);
    			        }
    			    }
    			}

			} else {
			  for(CategoryDescription d : source.getDescriptions()) {
                com.salesmanager.core.model.catalog.category.CategoryDescription t = new com.salesmanager.core.model.catalog.category.CategoryDescription();

			    this.buildDescription(d, t);
							System.out.println("$#9475#"); t.setCategory(target);
			    descriptions.add(t);

			  }

			}
			System.out.println("$#9476#"); target.setDescriptions(descriptions);
		}


		System.out.println("$#9477#"); return target;


		} catch(Exception e) {
			throw new ConversionException(e);
		}

	}

	private com.salesmanager.core.model.catalog.category.CategoryDescription buildDescription(com.salesmanager.shop.model.catalog.category.CategoryDescription source, com.salesmanager.core.model.catalog.category.CategoryDescription target) throws Exception {
      //com.salesmanager.core.model.catalog.category.CategoryDescription desc = new com.salesmanager.core.model.catalog.category.CategoryDescription();
			System.out.println("$#9478#"); target.setCategoryHighlight(source.getHighlights());
						System.out.println("$#9479#"); target.setDescription(source.getDescription());
						System.out.println("$#9480#"); target.setName(source.getName());
						System.out.println("$#9481#"); target.setMetatagDescription(source.getMetaDescription());
						System.out.println("$#9482#"); target.setMetatagTitle(source.getTitle());
						System.out.println("$#9483#"); target.setSeUrl(source.getFriendlyUrl());
      Language lang = languageService.getByCode(source.getLanguage());
						System.out.println("$#9484#"); if(lang==null) {
          throw new ConversionException("Language is null for code " + source.getLanguage() + " use language ISO code [en, fr ...]");
      }
      //description.setId(description.getId());
						System.out.println("$#9485#"); target.setLanguage(lang);
						System.out.println("$#9486#"); return target;
	}


	@Override
	protected Category createTarget() {
		// TODO Auto-generated method stub
		return null;
	}

}
