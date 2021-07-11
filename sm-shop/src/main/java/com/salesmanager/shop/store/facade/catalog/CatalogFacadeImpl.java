package com.salesmanager.shop.store.facade.catalog;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jsoup.helper.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.catalog.CatalogEntryService;
import com.salesmanager.core.business.services.catalog.catalog.CatalogService;
import com.salesmanager.core.model.catalog.catalog.Catalog;
import com.salesmanager.core.model.catalog.catalog.CatalogCategoryEntry;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.catalog.PersistableCatalogCategoryEntryMapper;
import com.salesmanager.shop.mapper.catalog.PersistableCatalogMapper;
import com.salesmanager.shop.mapper.catalog.ReadableCatalogCategoryEntryMapper;
import com.salesmanager.shop.mapper.catalog.ReadableCatalogMapper;
import com.salesmanager.shop.model.catalog.catalog.PersistableCatalog;
import com.salesmanager.shop.model.catalog.catalog.PersistableCatalogCategoryEntry;
import com.salesmanager.shop.model.catalog.catalog.ReadableCatalog;
import com.salesmanager.shop.model.catalog.catalog.ReadableCatalogCategoryEntry;
import com.salesmanager.shop.model.catalog.catalog.ReadableCatalogCategoryEntryList;
import com.salesmanager.shop.model.catalog.catalog.ReadableCatalogList;
import com.salesmanager.shop.store.api.exception.OperationNotAllowedException;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.controller.catalog.facade.CatalogFacade;

@Service("catalogFacade")
public class CatalogFacadeImpl implements CatalogFacade {
	
	
	@Autowired
	private CatalogService catalogService;
	
	@Autowired
	private CatalogEntryService catalogEntryService;
	
	@Autowired
	private PersistableCatalogMapper persistableCatalogMapper;
	
	@Autowired
	private ReadableCatalogMapper readableCatalogMapper;
	
	@Autowired
	private PersistableCatalogCategoryEntryMapper persistableCatalogEntryMapper;
	
	@Autowired
	private ReadableCatalogCategoryEntryMapper readableCatalogEntryMapper;


	@Override
	public ReadableCatalog saveCatalog(PersistableCatalog catalog, MerchantStore store, Language language) {
		System.out.println("$#14348#"); Validate.notNull(catalog,"Catalog cannot be null");
		System.out.println("$#14349#"); Validate.notNull(store,"MerchantStore cannot be null");
		System.out.println("$#14350#"); Validate.notNull(language,"Language cannot be null");
		Catalog c = persistableCatalogMapper.convert(catalog, store, language);
		

		try {
			
			boolean existByCode = uniqueCatalog(catalog.getCode(), store);
			
			System.out.println("$#14351#"); if(existByCode) {
				throw new OperationNotAllowedException("Catalog [" + catalog.getCode() +"] already exists");
			}
			
			catalogService.saveOrUddate(c, store);
			
			c = catalogService.getByCode(c.getCode(), store);
			
			ReadableCatalog readable = readableCatalogMapper.convert(c, store, language);
			
			
			System.out.println("$#14352#"); return readable;
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Error while saving catalog",e);
		}

	}

	@Override
	public void deleteCatalog(Long catalogId, MerchantStore store, Language language) {
		System.out.println("$#14353#"); Validate.notNull(catalogId,"Catalog id cannot be null");
		System.out.println("$#14356#"); System.out.println("$#14354#"); Validate.isTrue(catalogId > 0, "Catalog id cannot be null");
		System.out.println("$#14357#"); Validate.notNull(store,"MerchantStore cannot be null");
		
		Catalog c = catalogService.getById(catalogId);
		
		System.out.println("$#14358#"); if(c == null) {
			throw new ResourceNotFoundException("Catalog with id [" + catalogId + "] not found");
		}
		
		System.out.println("$#14359#"); if(c.getMerchantStore() != null && !c.getMerchantStore().getCode().equals(store.getCode())) {
			throw new ResourceNotFoundException("Catalog with id [" + catalogId + "] not found for merchant [" + store.getCode()+ "]");
		}
		
		try {
			System.out.println("$#14361#"); catalogService.delete(c);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Error while deleting catalog id [" + catalogId + "]" ,e);
		}

	}

	@Override
	public ReadableCatalog getCatalog(String code, MerchantStore store, Language language) {
		System.out.println("$#14362#"); Validate.notNull(code,"Catalog code cannot be null");
		System.out.println("$#14363#"); Validate.notNull(store,"MerchantStore cannot be null");
		System.out.println("$#14364#"); Validate.notNull(language,"Language cannot be null");
		
		Catalog c = catalogService.getByCode(code, store);
		
		System.out.println("$#14365#"); if(c == null) {
			throw new ResourceNotFoundException("Catalog with code [" + code + "] not found");
		}

		System.out.println("$#14366#"); return readableCatalogMapper.convert(c, store, language);

	}

	@Override
	public void updateCatalog(Long catalogId, PersistableCatalog catalog, MerchantStore store, Language language) {
		System.out.println("$#14367#"); Validate.notNull(catalogId,"Catalog id cannot be null");
		System.out.println("$#14370#"); System.out.println("$#14368#"); Validate.isTrue(catalogId > 0, "Catalog id cannot be null");
		System.out.println("$#14371#"); Validate.notNull(store,"MerchantStore cannot be null");
		System.out.println("$#14372#"); Validate.notNull(language,"Language cannot be null");
		
		Catalog c = catalogService.getById(catalogId);
		
		System.out.println("$#14373#"); if(c == null) {
			throw new ResourceNotFoundException("Catalog with id [" + catalogId + "] not found");
		}
		
		System.out.println("$#14374#"); if(c.getMerchantStore() != null && !c.getMerchantStore().getCode().equals(store.getCode())) {
			throw new ResourceNotFoundException("Catalog with id [" + catalogId + "] not found for merchant [" + store.getCode()+ "]");
		}
		
		System.out.println("$#14376#"); c.setDefaultCatalog(catalog.isDefaultCatalog());
		System.out.println("$#14377#"); c.setVisible(catalog.isVisible());
		
		try {
			catalogService.saveOrUddate(c, store);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Error while saving catalog",e);
		}
	}

	@Override
	public ReadableCatalog getCatalog(Long id, MerchantStore store, Language language) {
		System.out.println("$#14378#"); Validate.notNull(id,"Catalog id cannot be null");
		System.out.println("$#14379#"); Validate.notNull(store,"MerchantStore cannot be null");
		
		Catalog c = catalogService.getById(id, store);
		
		System.out.println("$#14380#"); if(c == null) {
			throw new ResourceNotFoundException("Catalog with id [" + id + "] not found");
		}

		
		System.out.println("$#14381#"); return readableCatalogMapper.convert(c, store, language);
	}

	@Override
	public Catalog getCatalog(String code, MerchantStore store) {
		System.out.println("$#14382#"); Validate.notNull(code,"Catalog code cannot be null");
		System.out.println("$#14383#"); Validate.notNull(store,"MerchantStore cannot be null");

		System.out.println("$#14384#"); return catalogService.getByCode(code, store);
	}

	@Override
	public ReadableCatalogList listCatalogs(Optional<String> code, MerchantStore store, Language language, int page, int count) {
		System.out.println("$#14385#"); Validate.notNull(store,"MerchantStore cannot be null");
		
		String catalogCode = null;
		System.out.println("$#14386#"); if(code.isPresent()) {
			catalogCode = code.get();
		}
		
		ReadableCatalogList catalogList = new ReadableCatalogList();
		
		try {
			Page<Catalog> catalogs = catalogService.getCatalogs(store, language, catalogCode, page, count);
		
			System.out.println("$#14387#"); if(catalogs.getSize() == 0) {
				System.out.println("$#14388#"); return catalogList;
			}
			
			List<ReadableCatalog> readableList = catalogs.getContent().stream()
					.map(cat -> readableCatalogMapper.convert(cat, store, language))
					.collect(Collectors.toList());
			
			System.out.println("$#14390#"); catalogList.setCatalogs(readableList);
			System.out.println("$#14391#"); catalogList.setTotalPages(catalogs.getTotalPages());
			System.out.println("$#14392#"); catalogList.setNumber(catalogs.getNumber());
			System.out.println("$#14393#"); catalogList.setRecordsTotal(catalogs.getTotalElements());
		
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Cannot get catalogs for merchant [" + store.getCode() + "]");
		}

		System.out.println("$#14394#"); return catalogList;
	}

	@Override
	public ReadableCatalogCategoryEntryList listCatalogEntry(Optional<String> product, Long id, MerchantStore store, Language language, int page, int count) {
		System.out.println("$#14395#"); Validate.notNull(store,"MerchantStore cannot be null");
		String productCode = null;
		System.out.println("$#14396#"); if(product.isPresent()) {
			productCode = product.get();
		}
		
		Catalog catalog = catalogService.getById(id, store);
		
		System.out.println("$#14397#"); if(catalog == null) {
			throw new ResourceNotFoundException("Catalog with id [" + id + "] not found for store ["+ store.getCode() +"]");
		}
		
		ReadableCatalogCategoryEntryList catalogList = new ReadableCatalogCategoryEntryList();
		
		try {
			Page<CatalogCategoryEntry> entry = catalogEntryService.list(catalog, store, language, productCode, page, count);
		
			System.out.println("$#14398#"); if(entry.getSize() == 0) {
				System.out.println("$#14399#"); return catalogList;
			}
			
			List<ReadableCatalogCategoryEntry> readableList = entry.getContent().stream()
					.map(cat -> readableCatalogEntryMapper.convert(cat, store, language))
					.collect(Collectors.toList());
			
			System.out.println("$#14401#"); catalogList.setCatalogEntry(readableList);
			System.out.println("$#14402#"); catalogList.setTotalPages(entry.getTotalPages());
			System.out.println("$#14403#"); catalogList.setRecordsTotal(entry.getTotalElements());
			System.out.println("$#14404#"); catalogList.setNumber(entry.getNumber());
		
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Cannot get catalog entry for catalog [" + id + "] andr merchant [" + store.getCode() + "]");
		}

		System.out.println("$#14405#"); return catalogList;
	}

	@Override
	public ReadableCatalogCategoryEntry getCatalogEntry(Long id, MerchantStore store, Language language) {
		CatalogCategoryEntry entry = catalogEntryService.getById(id);
		System.out.println("$#14406#"); if(entry == null) {
			throw new ResourceNotFoundException("catalog entry [" + id + "] not found");
		}
		
		System.out.println("$#14407#"); if(entry.getCatalog().getMerchantStore().getId().intValue() != store.getId().intValue()) {
			throw new ResourceNotFoundException("catalog entry [" + id + "] not found");
		}
		
		ReadableCatalogCategoryEntry readable = readableCatalogEntryMapper.convert(entry, store, language);
		System.out.println("$#14408#"); return readable;
	}

	@Override
	public ReadableCatalogCategoryEntry addCatalogEntry(PersistableCatalogCategoryEntry entry, MerchantStore store, Language language) {
		
		System.out.println("$#14409#"); Validate.notNull(entry,"PersistableCatalogEntry cannot be null");
		System.out.println("$#14410#"); Validate.notNull(entry.getCatalog(),"CatalogEntry.catalog cannot be null");
		System.out.println("$#14411#"); Validate.notNull(store,"MerchantStore cannot be null");
		
		Catalog catalog = catalogService.getByCode(entry.getCatalog(), store);
		
		System.out.println("$#14412#"); if(catalog == null) {
			throw new ResourceNotFoundException("catalog [" + entry.getCatalog() + "] not found");
		}
		
		CatalogCategoryEntry catalogEntryModel = persistableCatalogEntryMapper.convert(entry, store, language);
		
		try {
			System.out.println("$#14413#"); catalogEntryService.add(catalogEntryModel, catalog);
			System.out.println("$#14414#"); return readableCatalogEntryMapper.convert(catalogEntryModel, store, language);
			
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Exception while creating catalogEntry",e);
		}

	}

	@Override
	public void removeCatalogEntry(Long catalogId, Long catalogEntryId, MerchantStore store, Language language) {
		CatalogCategoryEntry entry = catalogEntryService.getById(catalogEntryId);
		System.out.println("$#14415#"); if(entry == null) {
			throw new ResourceNotFoundException("catalog entry [" + catalogEntryId + "] not found");
		}
		
		System.out.println("$#14416#"); if(entry.getCatalog().getId().longValue() != catalogId.longValue()) {
			throw new ResourceNotFoundException("catalog entry [" + catalogEntryId + "] not found");
		}
		
		System.out.println("$#14417#"); if(entry.getCatalog().getMerchantStore().getId().intValue() != store.getId().intValue()) {
			throw new ResourceNotFoundException("catalog entry [" + catalogEntryId + "] not found");
		}
		
		try {
			System.out.println("$#14418#"); catalogEntryService.delete(entry);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Exception while deleting catalogEntry",e);
		}
		
	}

	@Override
	public boolean uniqueCatalog(String code, MerchantStore store) {
		System.out.println("$#14420#"); System.out.println("$#14419#"); return catalogService.existByCode(code, store);
	}

}
