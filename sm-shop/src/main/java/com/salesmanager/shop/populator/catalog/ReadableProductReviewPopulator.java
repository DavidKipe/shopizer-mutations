package com.salesmanager.shop.populator.catalog;

import java.util.Set;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.catalog.product.review.ProductReview;
import com.salesmanager.core.model.catalog.product.review.ProductReviewDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.catalog.product.ReadableProductReview;
import com.salesmanager.shop.model.customer.ReadableCustomer;
import com.salesmanager.shop.populator.customer.ReadableCustomerPopulator;
import com.salesmanager.shop.utils.DateUtil;

public class ReadableProductReviewPopulator extends
		AbstractDataPopulator<ProductReview, ReadableProductReview> {

	@Override
	public ReadableProductReview populate(ProductReview source,
			ReadableProductReview target, MerchantStore store, Language language)
			throws ConversionException {

		
		try {
			ReadableCustomerPopulator populator = new ReadableCustomerPopulator();
			ReadableCustomer customer = new ReadableCustomer();
			populator.populate(source.getCustomer(), customer, store, language);

			System.out.println("$#9961#"); target.setId(source.getId());
			System.out.println("$#9962#"); target.setDate(DateUtil.formatDate(source.getReviewDate()));
			System.out.println("$#9963#"); target.setCustomer(customer);
			System.out.println("$#9964#"); target.setRating(source.getReviewRating());
			System.out.println("$#9965#"); target.setProductId(source.getProduct().getId());
			
			Set<ProductReviewDescription> descriptions = source.getDescriptions();
			System.out.println("$#9966#"); if(descriptions!=null) {
				for(ProductReviewDescription description : descriptions) {
					System.out.println("$#9968#"); target.setDescription(description.getDescription());
					System.out.println("$#9969#"); target.setLanguage(description.getLanguage().getCode());
					break;
				}
			}

			System.out.println("$#9970#"); return target;
			
		} catch (Exception e) {
			throw new ConversionException("Cannot populate ProductReview", e);
		}
		
		
		
	}

	@Override
	protected ReadableProductReview createTarget() {
		return null;
	}

}
