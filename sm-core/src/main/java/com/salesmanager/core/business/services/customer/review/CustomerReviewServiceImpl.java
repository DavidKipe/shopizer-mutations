package com.salesmanager.core.business.services.customer.review;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.Validate;
import org.springframework.stereotype.Service;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.customer.review.CustomerReviewRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.customer.review.CustomerReview;

@Service("customerReviewService")
public class CustomerReviewServiceImpl extends
	SalesManagerEntityServiceImpl<Long, CustomerReview> implements CustomerReviewService {
	
	private CustomerReviewRepository customerReviewRepository;
	
	@Inject
	private CustomerService customerService;
	
	@Inject
	public CustomerReviewServiceImpl(
			CustomerReviewRepository customerReviewRepository) {
			super(customerReviewRepository);
			this.customerReviewRepository = customerReviewRepository;
	}
	
	
	private void saveOrUpdate(CustomerReview review) throws ServiceException {
		

		System.out.println("$#2229#"); Validate.notNull(review,"CustomerReview cannot be null");
		System.out.println("$#2230#"); Validate.notNull(review.getCustomer(),"CustomerReview.customer cannot be null");
		System.out.println("$#2231#"); Validate.notNull(review.getReviewedCustomer(),"CustomerReview.reviewedCustomer cannot be null");
		
		
		//refresh customer
		Customer customer = customerService.getById(review.getReviewedCustomer().getId());
		
		//ajust product rating
		Integer count = 0;
		System.out.println("$#2232#"); if(customer.getCustomerReviewCount()!=null) {
			count = customer.getCustomerReviewCount();
		}
				
		
		

		BigDecimal averageRating = customer.getCustomerReviewAvg();
		System.out.println("$#2233#"); if(averageRating==null) {
			averageRating = new BigDecimal(0);
		}
		//get reviews

		
		BigDecimal totalRating = averageRating.multiply(new BigDecimal(count));
		totalRating = totalRating.add(new BigDecimal(review.getReviewRating()));
		
		System.out.println("$#2234#"); count = count + 1;
		System.out.println("$#2235#"); double avg = totalRating.doubleValue() / count.intValue();
		
		System.out.println("$#2236#"); customer.setCustomerReviewAvg(new BigDecimal(avg));
		System.out.println("$#2237#"); customer.setCustomerReviewCount(count);
		System.out.println("$#2238#"); super.save(review);
		
		System.out.println("$#2239#"); customerService.update(customer);
		
		System.out.println("$#2240#"); review.setReviewedCustomer(customer);

		
	}
	
	public void update(CustomerReview review) throws ServiceException {
		System.out.println("$#2241#"); this.saveOrUpdate(review);
	}
	
	public void create(CustomerReview review) throws ServiceException {
		System.out.println("$#2242#"); this.saveOrUpdate(review);
	}
	
	

	@Override
	public List<CustomerReview> getByCustomer(Customer customer) {
		System.out.println("$#2243#"); Validate.notNull(customer,"Customer cannot be null");
		System.out.println("$#2244#"); return customerReviewRepository.findByReviewer(customer.getId());
	}

	@Override
	public List<CustomerReview> getByReviewedCustomer(Customer customer) {
		System.out.println("$#2245#"); Validate.notNull(customer,"Customer cannot be null");
		System.out.println("$#2246#"); return customerReviewRepository.findByReviewed(customer.getId());
	}


	@Override
	public CustomerReview getByReviewerAndReviewed(Long reviewer, Long reviewed) {
		System.out.println("$#2247#"); Validate.notNull(reviewer,"Reviewer customer cannot be null");
		System.out.println("$#2248#"); Validate.notNull(reviewed,"Reviewer customer cannot be null");
		System.out.println("$#2249#"); return customerReviewRepository.findByRevieweAndReviewed(reviewer, reviewed);
	}

}
