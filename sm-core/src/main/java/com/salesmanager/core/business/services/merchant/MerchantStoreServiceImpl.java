package com.salesmanager.core.business.services.merchant;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.jsoup.helper.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.merchant.MerchantRepository;
import com.salesmanager.core.business.repositories.merchant.PageableMerchantRepository;
import com.salesmanager.core.business.services.catalog.product.type.ProductTypeService;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.common.GenericEntityList;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.merchant.MerchantStoreCriteria;

@Service("merchantService")
public class MerchantStoreServiceImpl extends SalesManagerEntityServiceImpl<Integer, MerchantStore>
		implements MerchantStoreService {

	@Inject
	protected ProductTypeService productTypeService;

	@Autowired
	private PageableMerchantRepository pageableMerchantRepository;

	private MerchantRepository merchantRepository;

	@Inject
	public MerchantStoreServiceImpl(MerchantRepository merchantRepository) {
		super(merchantRepository);
		this.merchantRepository = merchantRepository;
	}

	@Override
	@CacheEvict(value="store", allEntries=true)
	public void saveOrUpdate(MerchantStore store) throws ServiceException {
		System.out.println("$#2250#"); super.save(store);
	}

	@Override
	@Cacheable("store")
	public MerchantStore getByCode(String code) throws ServiceException {
		System.out.println("$#2251#"); return merchantRepository.findByCode(code);
	}

	@Override
	public boolean existByCode(String code) {
		System.out.println("$#2253#"); System.out.println("$#2252#"); return merchantRepository.existsByCode(code);
	}

	@Override
	public GenericEntityList<MerchantStore> getByCriteria(MerchantStoreCriteria criteria) throws ServiceException {
		System.out.println("$#2254#"); return merchantRepository.listByCriteria(criteria);
	}

	@Override
	public Page<MerchantStore> listChildren(String code, int page, int count) throws ServiceException {
		Pageable pageRequest = PageRequest.of(page, count);
		System.out.println("$#2255#"); return pageableMerchantRepository.listByStore(code, pageRequest);
	}

	@Override
	public Page<MerchantStore> listAll(Optional<String> storeName, int page, int count) throws ServiceException {
		String store = null;
		System.out.println("$#2256#"); if (storeName != null && storeName.isPresent()) {
			store = storeName.get();
		}
		Pageable pageRequest = PageRequest.of(page, count);
		System.out.println("$#2258#"); return pageableMerchantRepository.listAll(store, pageRequest);

	}

	@Override
	public List<MerchantStore> findAllStoreCodeNameEmail() throws ServiceException {
		System.out.println("$#2259#"); return merchantRepository.findAllStoreCodeNameEmail();
	}

	@Override
	public Page<MerchantStore> listAllRetailers(Optional<String> storeName, int page, int count)
			throws ServiceException {
		String store = null;
		System.out.println("$#2260#"); if (storeName != null && storeName.isPresent()) {
			store = storeName.get();
		}
		Pageable pageRequest = PageRequest.of(page, count);
		System.out.println("$#2262#"); return pageableMerchantRepository.listAllRetailers(store, pageRequest);

	}

	@Override
	public List<MerchantStore> findAllStoreNames() throws ServiceException {
		System.out.println("$#2263#"); return merchantRepository.findAllStoreNames();
	}

	@Override
	public MerchantStore getParent(String code) throws ServiceException {
		System.out.println("$#2264#"); Validate.notNull(code, "MerchantStore code cannot be null");

		
		//get it
		MerchantStore storeModel = this.getByCode(code);
		
		System.out.println("$#2265#"); if(storeModel == null) {
			throw new ServiceException("Store with code [" + code + "] is not found");
		}
		
		System.out.println("$#2266#"); if(storeModel.isRetailer() != null && storeModel.isRetailer() && storeModel.getParent() == null) {
			System.out.println("$#2269#"); return storeModel;
		}
		
		System.out.println("$#2270#"); if(storeModel.getParent() == null) {
			System.out.println("$#2271#"); return storeModel;
		}
	
		System.out.println("$#2272#"); return merchantRepository.getById(storeModel.getParent().getId());
	}


	@Override
	public List<MerchantStore> findAllStoreNames(String code) throws ServiceException {
		System.out.println("$#2273#"); return merchantRepository.findAllStoreNames(code);
	}

	/**
	 * Store might be alone (known as retailer)
	 * A retailer can have multiple child attached
	 * 
	 * This method from a store code is able to retrieve parent and childs.
	 * Method can also filter on storeName
	 */
	@Override
	public Page<MerchantStore> listByGroup(Optional<String> storeName, String code, int page, int count) throws ServiceException {
		
		String name = null;
		System.out.println("$#2274#"); if (storeName != null && storeName.isPresent()) {
			name = storeName.get();
		}

		
		MerchantStore store = getByCode(code);//if exist
		Optional<Integer> id = Optional.ofNullable(store.getId());

		
		Pageable pageRequest = PageRequest.of(page, count);
		
		
		Page<MerchantStore> stores = pageableMerchantRepository.listByGroup(code, id.get(), name, pageRequest);
		System.out.println("$#2276#"); return stores;
		
		
	}

	@Override
	public boolean isStoreInGroup(String code) throws ServiceException{
		
		MerchantStore store = getByCode(code);//if exist
		Optional<Integer> id = Optional.ofNullable(store.getId());
		
		List<MerchantStore> stores = merchantRepository.listByGroup(code, id.get());
		
		
		System.out.println("$#2279#"); System.out.println("$#2278#"); System.out.println("$#2277#"); return stores.size() > 0;
	}


}
