package com.salesmanager.shop.store.facade.product;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductOptionSetService;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionSet;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.catalog.PersistableProductOptionSetMapper;
import com.salesmanager.shop.mapper.catalog.ReadableProductOptionSetMapper;
import com.salesmanager.shop.model.catalog.product.attribute.optionset.PersistableProductOptionSet;
import com.salesmanager.shop.model.catalog.product.attribute.optionset.ReadableProductOptionSet;
import com.salesmanager.shop.store.api.exception.OperationNotAllowedException;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.controller.product.facade.ProductOptionSetFacade;

@Service
public class ProductOptionSetFacadeImpl implements ProductOptionSetFacade {
	
	@Autowired
	private PersistableProductOptionSetMapper persistableProductOptionSetMapper;
	
	@Autowired
	private ReadableProductOptionSetMapper readableProductOptionSetMapper;
	
	@Autowired
	private ProductOptionSetService productOptionSetService;

	@Override
	public ReadableProductOptionSet get(Long id, MerchantStore store, Language language) {
		System.out.println("$#15032#"); Validate.notNull(store, "MerchantStore cannot be null");
		System.out.println("$#15033#"); Validate.notNull(language, "Language cannot be null");
		ProductOptionSet optionSet =  productOptionSetService.getById(store, id, language);
		System.out.println("$#15034#"); if(optionSet == null) {
			throw new ResourceNotFoundException("ProductOptionSet not found for id [" + id +"] and store [" + store.getCode() + "]");
		}
		
		System.out.println("$#15035#"); return readableProductOptionSetMapper.convert(optionSet, store, language);
	}

	@Override
	public List<ReadableProductOptionSet> list(MerchantStore store, Language language) {
		System.out.println("$#15036#"); Validate.notNull(store, "MerchantStore cannot be null");
		System.out.println("$#15037#"); Validate.notNull(language, "Language cannot be null");
		
		try {
			List<ProductOptionSet> optionSets = productOptionSetService.listByStore(store, language);
			System.out.println("$#15039#"); System.out.println("$#15038#"); return optionSets.stream().map(opt -> this.convert(opt, store, language)).collect(Collectors.toList());
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Exception while listing ProductOptionSet", e);
		}
		

	}
	
	private ReadableProductOptionSet convert(ProductOptionSet optionSet, MerchantStore store, Language language) {
		System.out.println("$#15040#"); return readableProductOptionSetMapper.convert(optionSet, store, language);
	}

	@Override
	public void create(PersistableProductOptionSet optionSet, MerchantStore store, Language language) {
		System.out.println("$#15041#"); Validate.notNull(store, "MerchantStore cannot be null");
		System.out.println("$#15042#"); Validate.notNull(language, "Language cannot be null");
		System.out.println("$#15043#"); Validate.notNull(optionSet, "PersistableProductOptionSet cannot be null");
		
		System.out.println("$#15044#"); if(this.exists(optionSet.getCode(), store)) {
			throw new OperationNotAllowedException("Option set with code [" + optionSet.getCode() + "] already exist");
		}
		
		ProductOptionSet opt = persistableProductOptionSetMapper.convert(optionSet, store, language);
		try {
			System.out.println("$#15045#"); opt.setStore(store);
			System.out.println("$#15046#"); productOptionSetService.create(opt);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Exception while creating ProductOptionSet", e);
		}

	}

	@Override
	public void update(Long id, PersistableProductOptionSet optionSet, MerchantStore store, Language language) {
		System.out.println("$#15047#"); Validate.notNull(store, "MerchantStore cannot be null");
		System.out.println("$#15048#"); Validate.notNull(language, "Language cannot be null");
		System.out.println("$#15049#"); Validate.notNull(optionSet, "PersistableProductOptionSet cannot be null");
		
		ProductOptionSet opt =  productOptionSetService.getById(store, id, language);
		System.out.println("$#15050#"); if(opt == null) {
			throw new ResourceNotFoundException("ProductOptionSet not found for id [" + id +"] and store [" + store.getCode() + "]");
		}
		
		System.out.println("$#15051#"); optionSet.setId(id);
		System.out.println("$#15052#"); optionSet.setCode(opt.getCode());
		ProductOptionSet model = persistableProductOptionSetMapper.convert(optionSet, store, language);
		try {
			System.out.println("$#15053#"); model.setStore(store);
			System.out.println("$#15054#"); productOptionSetService.save(model);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Exception while creating ProductOptionSet", e);
		}

	}

	@Override
	public void delete(Long id, MerchantStore store) {
		System.out.println("$#15055#"); Validate.notNull(store, "MerchantStore cannot be null");
		System.out.println("$#15056#"); Validate.notNull(id, "id cannot be null");
		ProductOptionSet opt =  productOptionSetService.getById(id);
		System.out.println("$#15057#"); if(opt == null) {
			throw new ResourceNotFoundException("ProductOptionSet not found for id [" + id +"] and store [" + store.getCode() + "]");
		}
		System.out.println("$#15058#"); if(!opt.getStore().getCode().equals(store.getCode())) {
			throw new ResourceNotFoundException("ProductOptionSet not found for id [" + id +"] and store [" + store.getCode() + "]");
		}
		try {
			System.out.println("$#15059#"); productOptionSetService.delete(opt);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Exception while deleting ProductOptionSet", e);
		}

	}

	@Override
	public boolean exists(String code, MerchantStore store) {
		System.out.println("$#15060#"); Validate.notNull(store, "MerchantStore cannot be null");
		System.out.println("$#15061#"); Validate.notNull(code, "code cannot be null");
		ProductOptionSet optionSet =  productOptionSetService.getCode(store, code);
		System.out.println("$#15062#"); if(optionSet != null) {
			System.out.println("$#15063#"); return true;
		}
		
		System.out.println("$#15064#"); return false;
	}

}
