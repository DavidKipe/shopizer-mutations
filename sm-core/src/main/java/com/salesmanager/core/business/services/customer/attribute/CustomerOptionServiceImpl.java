package com.salesmanager.core.business.services.customer.attribute;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.customer.attribute.CustomerOptionRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.customer.attribute.CustomerAttribute;
import com.salesmanager.core.model.customer.attribute.CustomerOption;
import com.salesmanager.core.model.customer.attribute.CustomerOptionSet;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;



@Service("customerOptionService")
public class CustomerOptionServiceImpl extends
		SalesManagerEntityServiceImpl<Long, CustomerOption> implements CustomerOptionService {

	
	private CustomerOptionRepository customerOptionRepository;
	
	@Inject
	private CustomerAttributeService customerAttributeService;
	
	@Inject
	private CustomerOptionSetService customerOptionSetService;
	

	@Inject
	public CustomerOptionServiceImpl(
			CustomerOptionRepository customerOptionRepository) {
			super(customerOptionRepository);
			this.customerOptionRepository = customerOptionRepository;
	}
	
	@Override
	public List<CustomerOption> listByStore(MerchantStore store, Language language) throws ServiceException {

		System.out.println("$#2182#"); return customerOptionRepository.findByStore(store.getId(), language.getId());

	}
	

	@Override
	public void saveOrUpdate(CustomerOption entity) throws ServiceException {
		
		
		//save or update (persist and attach entities
		System.out.println("$#2184#"); System.out.println("$#2183#"); if(entity.getId()!=null && entity.getId()>0) {
			System.out.println("$#2186#"); super.update(entity);
		} else {
			System.out.println("$#2187#"); super.save(entity);
		}
		
	}


	@Override
	public void delete(CustomerOption customerOption) throws ServiceException {
		
		//remove all attributes having this option
		List<CustomerAttribute> attributes = customerAttributeService.getByOptionId(customerOption.getMerchantStore(), customerOption.getId());
		
		for(CustomerAttribute attribute : attributes) {
			System.out.println("$#2188#"); customerAttributeService.delete(attribute);
		}
		
		CustomerOption option = this.getById(customerOption.getId());
		
		List<CustomerOptionSet> optionSets = customerOptionSetService.listByOption(customerOption, customerOption.getMerchantStore());
		
		for(CustomerOptionSet optionSet : optionSets) {
			System.out.println("$#2189#"); customerOptionSetService.delete(optionSet);
		}
		
		//remove option
		System.out.println("$#2190#"); super.delete(option);
		
	}
	
	@Override
	public CustomerOption getByCode(MerchantStore store, String optionCode) {
		System.out.println("$#2191#"); return customerOptionRepository.findByCode(store.getId(), optionCode);
	}
	

	




}
