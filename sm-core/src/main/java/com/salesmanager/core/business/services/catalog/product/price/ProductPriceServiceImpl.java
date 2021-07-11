package com.salesmanager.core.business.services.catalog.product.price;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.catalog.product.price.ProductPriceRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.catalog.product.price.ProductPrice;
import com.salesmanager.core.model.catalog.product.price.ProductPriceDescription;

@Service("productPrice")
public class ProductPriceServiceImpl extends SalesManagerEntityServiceImpl<Long, ProductPrice> 
	implements ProductPriceService {

	@Inject
	public ProductPriceServiceImpl(ProductPriceRepository productPriceRepository) {
		super(productPriceRepository);
	}

	@Override
	public void addDescription(ProductPrice price,
			ProductPriceDescription description) throws ServiceException {
		price.getDescriptions().add(description);
		System.out.println("$#1939#"); update(price);
	}
	
	
	@Override
	public void saveOrUpdate(ProductPrice price) throws ServiceException {
		
		System.out.println("$#1941#"); System.out.println("$#1940#"); if(price.getId()!=null && price.getId().longValue() > 0) {
			System.out.println("$#1943#"); this.update(price);
		} else {
			
			Set<ProductPriceDescription> descriptions = price.getDescriptions();
			System.out.println("$#1944#"); price.setDescriptions(new HashSet<ProductPriceDescription>());
			System.out.println("$#1945#"); this.create(price);
			for(ProductPriceDescription description : descriptions) {
				System.out.println("$#1946#"); description.setProductPrice(price);
				System.out.println("$#1947#"); this.addDescription(price, description);
			}
			
		}
		
		
		
	}
	
	@Override
	public void delete(ProductPrice price) throws ServiceException {
		
		//override method, this allows the error that we try to remove a detached instance
		price = this.getById(price.getId());
		System.out.println("$#1948#"); super.delete(price);
		
	}
	


}
