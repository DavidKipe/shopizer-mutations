package com.salesmanager.shop.populator.catalog;

import org.apache.commons.lang.Validate;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.catalog.category.CategoryDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.catalog.category.ReadableCategory;

public class ReadableCategoryPopulator extends
        AbstractDataPopulator<Category, ReadableCategory> {

    @Override
    public ReadableCategory populate(final Category source,
            final ReadableCategory target,
            final MerchantStore store,
            final Language language) throws ConversionException {

								System.out.println("$#9665#"); Validate.notNull(source, "Category must not be null");

								System.out.println("$#9666#"); target.setLineage(source.getLineage());
								System.out.println("$#9668#"); System.out.println("$#9667#"); if (source.getDescriptions() != null && source.getDescriptions().size() > 0) {

            CategoryDescription description = source.getDescription();
												System.out.println("$#9671#"); System.out.println("$#9670#"); if (source.getDescriptions().size() > 1) {
                for (final CategoryDescription desc : source.getDescriptions()) {
																				System.out.println("$#9672#"); if (desc.getLanguage().getCode().equals(language.getCode())) {
                        description = desc;
                        break;
                    }
                }
            }

												System.out.println("$#9673#"); if (description != null) {
                final com.salesmanager.shop.model.catalog.category.CategoryDescription desc = new com.salesmanager.shop.model.catalog.category.CategoryDescription();
																System.out.println("$#9674#"); desc.setFriendlyUrl(description.getSeUrl());
																System.out.println("$#9675#"); desc.setName(description.getName());
																System.out.println("$#9676#"); desc.setId(source.getId());
																System.out.println("$#9677#"); desc.setDescription(description.getDescription());
																System.out.println("$#9678#"); desc.setKeyWords(description.getMetatagKeywords());
																System.out.println("$#9679#"); desc.setHighlights(description.getCategoryHighlight());
																System.out.println("$#9680#"); desc.setTitle(description.getMetatagTitle());
																System.out.println("$#9681#"); desc.setMetaDescription(description.getMetatagDescription());

																System.out.println("$#9682#"); target.setDescription(desc);
            }

        }

								System.out.println("$#9683#"); if (source.getParent() != null) {
            final com.salesmanager.shop.model.catalog.category.Category parent = new com.salesmanager.shop.model.catalog.category.Category();
												System.out.println("$#9684#"); parent.setCode(source.getParent().getCode());
												System.out.println("$#9685#"); parent.setId(source.getParent().getId());
												System.out.println("$#9686#"); target.setParent(parent);
        }

								System.out.println("$#9687#"); target.setCode(source.getCode());
								System.out.println("$#9688#"); target.setId(source.getId());
								System.out.println("$#9689#"); if (source.getDepth() != null) {
												System.out.println("$#9690#"); target.setDepth(source.getDepth());
        }
								System.out.println("$#9691#"); target.setSortOrder(source.getSortOrder());
								System.out.println("$#9692#"); target.setVisible(source.isVisible());
								System.out.println("$#9693#"); target.setFeatured(source.isFeatured());

								System.out.println("$#9694#"); return target;

    }

    @Override
    protected ReadableCategory createTarget() {
        return null;
    }

}
