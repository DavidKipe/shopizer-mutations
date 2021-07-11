package com.salesmanager.shop.populator.catalog;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.review.ProductReview;
import com.salesmanager.core.model.catalog.product.review.ProductReviewDescription;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.catalog.product.PersistableProductReview;
import com.salesmanager.shop.utils.DateUtil;



public class PersistableProductReviewPopulator extends
		AbstractDataPopulator<PersistableProductReview, ProductReview> {
	
	
	

	private CustomerService customerService;
	

	private ProductService productService;
	

	private LanguageService languageService;
	


	public LanguageService getLanguageService() {
		System.out.println("$#9640#"); return languageService;
	}

	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}

	@Override
	public ProductReview populate(PersistableProductReview source,
			ProductReview target, MerchantStore store, Language language)
			throws ConversionException {
		
		
		System.out.println("$#9641#"); Validate.notNull(customerService,"customerService cannot be null");
		System.out.println("$#9642#"); Validate.notNull(productService,"productService cannot be null");
		System.out.println("$#9643#"); Validate.notNull(languageService,"languageService cannot be null");
		System.out.println("$#9644#"); Validate.notNull(source.getRating(),"Rating cannot bot be null");
		
		try {
			
			System.out.println("$#9645#"); if(target==null) {
				target = new ProductReview();
			}
			
			Customer customer = customerService.getById(source.getCustomerId());
			
			//check if customer belongs to store
			System.out.println("$#9646#"); if(customer ==null || customer.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				throw new ConversionException("Invalid customer id for the given store");
			}
			
			System.out.println("$#9648#"); if(source.getDate() == null) {
				String date = DateUtil.formatDate(new Date());
				System.out.println("$#9649#"); source.setDate(date);
			}
			System.out.println("$#9650#"); target.setReviewDate(DateUtil.getDate(source.getDate()));
			System.out.println("$#9651#"); target.setCustomer(customer);
			System.out.println("$#9652#"); target.setReviewRating(source.getRating());
			
			Product product = productService.getById(source.getProductId());
			
			//check if product belongs to store
			System.out.println("$#9653#"); if(product ==null || product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				throw new ConversionException("Invalid product id for the given store");
			}
			
			System.out.println("$#9655#"); target.setProduct(product);
			
			Language lang = languageService.getByCode(language.getCode());
			System.out.println("$#9656#"); if(lang ==null) {
				throw new ConversionException("Invalid language code, use iso codes (en, fr ...)");
			}
			
			ProductReviewDescription description = new ProductReviewDescription();
			System.out.println("$#9657#"); description.setDescription(source.getDescription());
			System.out.println("$#9658#"); description.setLanguage(lang);
			System.out.println("$#9659#"); description.setName("-");
			System.out.println("$#9660#"); description.setProductReview(target);
			
			Set<ProductReviewDescription> descriptions = new HashSet<ProductReviewDescription>();
			descriptions.add(description);
			
			System.out.println("$#9661#"); target.setDescriptions(descriptions);
			
			

			
			
			System.out.println("$#9662#"); return target;
			
		} catch (Exception e) {
			throw new ConversionException("Cannot populate ProductReview", e);
		}
		
	}

	@Override
	protected ProductReview createTarget() {
		return null;
	}
	
	public CustomerService getCustomerService() {
		System.out.println("$#9663#"); return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}
	
	public ProductService getProductService() {
		System.out.println("$#9664#"); return productService;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}


}
