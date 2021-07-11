package com.salesmanager.shop.populator.customer;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.customer.review.CustomerReview;
import com.salesmanager.core.model.customer.review.CustomerReviewDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.customer.PersistableCustomerReview;
import com.salesmanager.shop.utils.DateUtil;

public class PersistableCustomerReviewPopulator extends AbstractDataPopulator<PersistableCustomerReview, CustomerReview> {

	private CustomerService customerService;
	
	private LanguageService languageService;
	
	public LanguageService getLanguageService() {
		System.out.println("$#10239#"); return languageService;
	}

	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}

	@Override
	public CustomerReview populate(PersistableCustomerReview source, CustomerReview target, MerchantStore store,
			Language language) throws ConversionException {
		
		System.out.println("$#10240#"); Validate.notNull(customerService,"customerService cannot be null");
		System.out.println("$#10241#"); Validate.notNull(languageService,"languageService cannot be null");
		System.out.println("$#10242#"); Validate.notNull(source.getRating(),"Rating cannot bot be null");
		
		try {
			
			System.out.println("$#10243#"); if(target==null) {
				target = new CustomerReview();
			}
			
			System.out.println("$#10244#"); if(source.getDate() == null) {
				String date = DateUtil.formatDate(new Date());
				System.out.println("$#10245#"); source.setDate(date);
			}
			System.out.println("$#10246#"); target.setReviewDate(DateUtil.getDate(source.getDate()));
			
			System.out.println("$#10247#"); if(source.getId() != null && source.getId().longValue()==0) {
				System.out.println("$#10249#"); source.setId(null);
			} else {
				System.out.println("$#10250#"); target.setId(source.getId());
			}
			
			
			Customer reviewer = customerService.getById(source.getCustomerId());
			Customer reviewed = customerService.getById(source.getReviewedCustomer());
			
			System.out.println("$#10251#"); target.setReviewRating(source.getRating());
			
			System.out.println("$#10252#"); target.setCustomer(reviewer);
			System.out.println("$#10253#"); target.setReviewedCustomer(reviewed);
			
			Language lang = languageService.getByCode(language.getCode());
			System.out.println("$#10254#"); if(lang ==null) {
				throw new ConversionException("Invalid language code, use iso codes (en, fr ...)");
			}
			
			CustomerReviewDescription description = new CustomerReviewDescription();
			System.out.println("$#10255#"); description.setDescription(source.getDescription());
			System.out.println("$#10256#"); description.setLanguage(lang);
			System.out.println("$#10257#"); description.setName("-");
			System.out.println("$#10258#"); description.setCustomerReview(target);
			
			Set<CustomerReviewDescription> descriptions = new HashSet<CustomerReviewDescription>();
			descriptions.add(description);
			
			System.out.println("$#10259#"); target.setDescriptions(descriptions);
			
		} catch (Exception e) {
			throw new ConversionException("Cannot populate CustomerReview", e);
		}
		
		
		System.out.println("$#10260#"); return target;
	}

	@Override
	protected CustomerReview createTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	public CustomerService getCustomerService() {
		System.out.println("$#10261#"); return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

}
