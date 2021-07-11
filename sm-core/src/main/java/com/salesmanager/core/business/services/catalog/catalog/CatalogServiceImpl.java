package com.salesmanager.core.business.services.catalog.catalog;

import javax.inject.Inject;

import org.jsoup.helper.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.catalog.catalog.CatalogRepository;
import com.salesmanager.core.business.repositories.catalog.catalog.PageableCatalogRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.catalog.catalog.Catalog;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;

@Service("catalogService")
public class CatalogServiceImpl 
extends SalesManagerEntityServiceImpl<Long, Catalog> 
implements CatalogService {
	
	
	private CatalogRepository catalogRepository;
	
	@Autowired
	private PageableCatalogRepository pageableCatalogRepository;

	@Inject
	public CatalogServiceImpl(CatalogRepository repository) {
		super(repository);
		this.catalogRepository = repository;
	}

	@Override
	public Catalog saveOrUddate(Catalog catalog, MerchantStore store) throws ServiceException {
		catalogRepository.save(catalog);
		System.out.println("$#1730#"); return catalog;
	}

	@Override
	public Page<Catalog> getCatalogs(MerchantStore store, Language language, String name, int page, int count)
			throws ServiceException {
		Pageable pageRequest = PageRequest.of(page, count);
		System.out.println("$#1731#"); return pageableCatalogRepository.listByStore(store.getId(), name, pageRequest);
	}

	@Override
	public void delete(Catalog catalog) throws ServiceException {
		System.out.println("$#1732#"); Validate.notNull(catalog,"Catalog must not be null");
		System.out.println("$#1733#"); catalogRepository.delete(catalog);
	}

	@Override
	public Catalog getById(Long catalogId, MerchantStore store) {
		System.out.println("$#1734#"); return catalogRepository.findById(catalogId, store.getId());
	}

	@Override
	public Catalog getByCode(String code, MerchantStore store) {
		System.out.println("$#1735#"); return catalogRepository.findByCode(code, store.getId());
	}

	@Override
	public boolean existByCode(String code, MerchantStore store) {
		System.out.println("$#1737#"); System.out.println("$#1736#"); return catalogRepository.existsByCode(code, store.getId());
	}
	
	

}
