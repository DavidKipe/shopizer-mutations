package com.salesmanager.core.business.services.customer.attribute;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.customer.attribute.CustomerOptionSetRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.customer.attribute.CustomerOption;
import com.salesmanager.core.model.customer.attribute.CustomerOptionSet;
import com.salesmanager.core.model.customer.attribute.CustomerOptionValue;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;



@Service("customerOptionSetService")
public class CustomerOptionSetServiceImpl extends
		SalesManagerEntityServiceImpl<Long, CustomerOptionSet> implements CustomerOptionSetService {


	@Inject
	private CustomerOptionSetRepository customerOptionSetRepository;
	

	@Inject
	public CustomerOptionSetServiceImpl(
			CustomerOptionSetRepository customerOptionSetRepository) {
			super(customerOptionSetRepository);
			this.customerOptionSetRepository = customerOptionSetRepository;
	}
	

	@Override
	public List<CustomerOptionSet> listByOption(CustomerOption option, MerchantStore store) throws ServiceException {
		Validate.notNull(store,"merchant store cannot be null");
		Validate.notNull(option,"option cannot be null");
		
		System.out.println("$#2192#"); return customerOptionSetRepository.findByOptionId(store.getId(), option.getId());
	}
	
	@Override
	public void delete(CustomerOptionSet customerOptionSet) throws ServiceException {
		customerOptionSet = customerOptionSetRepository.findOne(customerOptionSet.getId());
		System.out.println("$#2193#"); super.delete(customerOptionSet);
	}
	
	@Override
	public List<CustomerOptionSet> listByStore(MerchantStore store, Language language) throws ServiceException {
		Validate.notNull(store,"merchant store cannot be null");

		
		System.out.println("$#2194#"); return customerOptionSetRepository.findByStore(store.getId(),language.getId());
	}


	@Override
	public void saveOrUpdate(CustomerOptionSet entity) throws ServiceException {
		Validate.notNull(entity,"customer option set cannot be null");

		System.out.println("$#2196#"); System.out.println("$#2195#"); if(entity.getId()>0) {
			System.out.println("$#2197#"); super.update(entity);
		} else {
			System.out.println("$#2198#"); super.create(entity);
		}
		
	}


	@Override
	public List<CustomerOptionSet> listByOptionValue(
			CustomerOptionValue optionValue, MerchantStore store)
			throws ServiceException {
		System.out.println("$#2199#"); return customerOptionSetRepository.findByOptionValueId(store.getId(), optionValue.getId());
	}


	




}
