package com.salesmanager.core.business.services.catalog.product.attribute;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.catalog.product.attribute.PageableProductOptionValueRepository;
import com.salesmanager.core.business.repositories.catalog.product.attribute.ProductOptionValueRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValue;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;

@Service("productOptionValueService")
public class ProductOptionValueServiceImpl extends
		SalesManagerEntityServiceImpl<Long, ProductOptionValue> implements
		ProductOptionValueService {

	@Inject
	private ProductAttributeService productAttributeService;
	
	@Autowired
	private PageableProductOptionValueRepository pageableProductOptionValueRepository;
	
	private ProductOptionValueRepository productOptionValueRepository;
	
	@Inject
	public ProductOptionValueServiceImpl(
			ProductOptionValueRepository productOptionValueRepository) {
			super(productOptionValueRepository);
			this.productOptionValueRepository = productOptionValueRepository;
	}
	
	
	@Override
	public List<ProductOptionValue> listByStore(MerchantStore store, Language language) throws ServiceException {
		
		System.out.println("$#1838#"); return productOptionValueRepository.findByStoreId(store.getId(), language.getId());
	}
	
	@Override
	public List<ProductOptionValue> listByStoreNoReadOnly(MerchantStore store, Language language) throws ServiceException {
		
		System.out.println("$#1839#"); return productOptionValueRepository.findByReadOnly(store.getId(), language.getId(), false);
	}

	@Override
	public List<ProductOptionValue> getByName(MerchantStore store, String name, Language language) throws ServiceException {
		
		try {
			System.out.println("$#1840#"); return productOptionValueRepository.findByName(store.getId(), name, language.getId());
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		
		
	}
	
	@Override
	public void saveOrUpdate(ProductOptionValue entity) throws ServiceException {
		
		
		//save or update (persist and attach entities
		System.out.println("$#1842#"); System.out.println("$#1841#"); if(entity.getId()!=null && entity.getId()>0) {

			System.out.println("$#1844#"); super.update(entity);
			
		} else {
			
			System.out.println("$#1845#"); super.save(entity);
			
		}
		
	}
	
	
	public void delete(ProductOptionValue entity) throws ServiceException {
		
		//remove all attributes having this option
		List<ProductAttribute> attributes = productAttributeService.getByOptionValueId(entity.getMerchantStore(), entity.getId());
		
		for(ProductAttribute attribute : attributes) {
			System.out.println("$#1846#"); productAttributeService.delete(attribute);
		}
		
		ProductOptionValue option = getById(entity.getId());
		
		//remove option
		System.out.println("$#1847#"); super.delete(option);
		
	}
	
	@Override
	public ProductOptionValue getByCode(MerchantStore store, String optionValueCode) {
		System.out.println("$#1848#"); return productOptionValueRepository.findByCode(store.getId(), optionValueCode);
	}


	@Override
	public ProductOptionValue getById(MerchantStore store, Long optionValueId) {
		System.out.println("$#1849#"); return productOptionValueRepository.findOne(store.getId(), optionValueId);
	}


	@Override
	public Page<ProductOptionValue> getByMerchant(MerchantStore store, Language language, String name, int page,
			int count) {
	    Validate.notNull(store, "MerchantStore cannot be null");
	    Pageable p = PageRequest.of(page, count);
					System.out.println("$#1850#"); return pageableProductOptionValueRepository.listOptionValues(store.getId(), name, p);
	}



}
