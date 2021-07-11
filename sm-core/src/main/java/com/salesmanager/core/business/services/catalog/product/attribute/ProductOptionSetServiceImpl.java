package com.salesmanager.core.business.services.catalog.product.attribute;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.catalog.product.attribute.ProductOptionSetRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionSet;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;

@Service("productOptionSetService")
public class ProductOptionSetServiceImpl extends
		SalesManagerEntityServiceImpl<Long, ProductOptionSet> implements ProductOptionSetService {

	
	private ProductOptionSetRepository productOptionSetRepository;
	

	@Inject
	public ProductOptionSetServiceImpl(
			ProductOptionSetRepository productOptionSetRepository) {
			super(productOptionSetRepository);
			this.productOptionSetRepository = productOptionSetRepository;
	}


	@Override
	public List<ProductOptionSet> listByStore(MerchantStore store, Language language) throws ServiceException {
		System.out.println("$#1834#"); return productOptionSetRepository.findByStore(store.getId(), language.getId());
	}


	@Override
	public ProductOptionSet getById(MerchantStore store, Long optionSetId, Language lang) {
		System.out.println("$#1835#"); return productOptionSetRepository.findOne(store.getId(), optionSetId, lang.getId());
	}


	@Override
	public ProductOptionSet getCode(MerchantStore store, String code) {
		System.out.println("$#1836#"); return productOptionSetRepository.findByCode(store.getId(), code);
	}


	@Override
	public List<ProductOptionSet> getByProductType(Long productTypeId, MerchantStore store, Language lang) {
		System.out.println("$#1837#"); return productOptionSetRepository.findByProductType(productTypeId, store.getId(), lang.getId());
	}



}
