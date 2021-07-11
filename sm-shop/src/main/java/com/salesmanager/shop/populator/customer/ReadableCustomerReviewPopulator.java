package com.salesmanager.shop.populator.customer;

import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.customer.review.CustomerReview;
import com.salesmanager.core.model.customer.review.CustomerReviewDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.customer.ReadableCustomer;
import com.salesmanager.shop.model.customer.ReadableCustomerReview;
import com.salesmanager.shop.utils.DateUtil;

public class ReadableCustomerReviewPopulator extends AbstractDataPopulator<CustomerReview, ReadableCustomerReview> {

	@Override
	public ReadableCustomerReview populate(CustomerReview source, ReadableCustomerReview target, MerchantStore store,
			Language language) throws ConversionException {

		try {
			
			System.out.println("$#10408#"); if(target==null) {
				target = new ReadableCustomerReview();
			}
			
			System.out.println("$#10409#"); if(source.getReviewDate() != null) {
				System.out.println("$#10410#"); target.setDate(DateUtil.formatDate(source.getReviewDate()));
			}
			

			ReadableCustomer reviewed = new ReadableCustomer();
			System.out.println("$#10411#"); reviewed.setId(source.getReviewedCustomer().getId());
			System.out.println("$#10412#"); reviewed.setFirstName(source.getReviewedCustomer().getBilling().getFirstName());
			System.out.println("$#10413#"); reviewed.setLastName(source.getReviewedCustomer().getBilling().getLastName());

			
			System.out.println("$#10414#"); target.setId(source.getId());
			System.out.println("$#10415#"); target.setCustomerId(source.getCustomer().getId());
			System.out.println("$#10416#"); target.setReviewedCustomer(reviewed);
			System.out.println("$#10417#"); target.setRating(source.getReviewRating());
			System.out.println("$#10418#"); target.setReviewedCustomer(reviewed);
			System.out.println("$#10419#"); target.setCustomerId(source.getCustomer().getId());
			
			Set<CustomerReviewDescription> descriptions = source.getDescriptions();
			System.out.println("$#10420#"); if(CollectionUtils.isNotEmpty(descriptions)) {
				CustomerReviewDescription description = null;
				System.out.println("$#10422#"); System.out.println("$#10421#"); if(descriptions.size()>1) {
					for(CustomerReviewDescription desc : descriptions) {
						System.out.println("$#10423#"); if(desc.getLanguage().getCode().equals(language.getCode())) {
							description = desc;
							break;
						}
					}
				} else {
					description = descriptions.iterator().next();
				}
				
				System.out.println("$#10424#"); if(description != null) {
					System.out.println("$#10425#"); target.setDescription(description.getDescription());
					System.out.println("$#10426#"); target.setLanguage(description.getLanguage().getCode());
				}

			}

			

			
		} catch (Exception e) {
			throw new ConversionException("Cannot populate ReadableCustomerReview", e);
		}
		
		
		System.out.println("$#10427#"); return target;

	}

	@Override
	protected ReadableCustomerReview createTarget() {
		// TODO Auto-generated method stub
		return null;
	}

}
