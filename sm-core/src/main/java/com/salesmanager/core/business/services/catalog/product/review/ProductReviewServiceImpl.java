package com.salesmanager.core.business.services.catalog.product.review;

import java.math.BigDecimal;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.lang.Validate;
import org.springframework.stereotype.Service;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.catalog.product.review.ProductReviewRepository;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.review.ProductReview;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.reference.language.Language;

@Service("productReviewService")
public class ProductReviewServiceImpl extends
		SalesManagerEntityServiceImpl<Long, ProductReview> implements
		ProductReviewService {


	private ProductReviewRepository productReviewRepository;
	
	@Inject
	private ProductService productService;
	
	@Inject
	public ProductReviewServiceImpl(
			ProductReviewRepository productReviewRepository) {
			super(productReviewRepository);
			this.productReviewRepository = productReviewRepository;
	}

	@Override
	public List<ProductReview> getByCustomer(Customer customer) {
		System.out.println("$#2047#"); return productReviewRepository.findByCustomer(customer.getId());
	}

	@Override
	public List<ProductReview> getByProduct(Product product) {
		System.out.println("$#2048#"); return productReviewRepository.findByProduct(product.getId());
	}
	
	@Override
	public ProductReview getByProductAndCustomer(Long productId, Long customerId) {
		System.out.println("$#2049#"); return productReviewRepository.findByProductAndCustomer(productId, customerId);
	}
	
	@Override
	public List<ProductReview> getByProduct(Product product, Language language) {
		System.out.println("$#2050#"); return productReviewRepository.findByProduct(product.getId(), language.getId());
	}
	
	private void saveOrUpdate(ProductReview review) throws ServiceException {
		

		System.out.println("$#2051#"); Validate.notNull(review,"ProductReview cannot be null");
		System.out.println("$#2052#"); Validate.notNull(review.getProduct(),"ProductReview.product cannot be null");
		System.out.println("$#2053#"); Validate.notNull(review.getCustomer(),"ProductReview.customer cannot be null");
		
		
		//refresh product
		Product product = productService.getById(review.getProduct().getId());
		
		//ajust product rating
		Integer count = 0;
		System.out.println("$#2054#"); if(product.getProductReviewCount()!=null) {
			count = product.getProductReviewCount();
		}
				
		
		

		BigDecimal averageRating = product.getProductReviewAvg();
		System.out.println("$#2055#"); if(averageRating==null) {
			averageRating = new BigDecimal(0);
		}
		//get reviews

		
		BigDecimal totalRating = averageRating.multiply(new BigDecimal(count));
		totalRating = totalRating.add(new BigDecimal(review.getReviewRating()));
		
		System.out.println("$#2056#"); count = count + 1;
		System.out.println("$#2057#"); double avg = totalRating.doubleValue() / count.intValue();
		
		System.out.println("$#2058#"); product.setProductReviewAvg(new BigDecimal(avg));
		System.out.println("$#2059#"); product.setProductReviewCount(count);
		System.out.println("$#2060#"); super.save(review);
		
		System.out.println("$#2061#"); productService.update(product);
		
		System.out.println("$#2062#"); review.setProduct(product);
		
	}
	
	public void update(ProductReview review) throws ServiceException {
		System.out.println("$#2063#"); this.saveOrUpdate(review);
	}
	
	public void create(ProductReview review) throws ServiceException {
		System.out.println("$#2064#"); this.saveOrUpdate(review);
	}

	/* (non-Javadoc)
	 * @see com.salesmanager.core.business.services.catalog.product.review.ProductReviewService#getByProductNoObjects(com.salesmanager.core.model.catalog.product.Product)
	 */
	@Override
	public List<ProductReview> getByProductNoCustomers(Product product) {
		System.out.println("$#2065#"); return productReviewRepository.findByProductNoCustomers(product.getId());
	}


}
