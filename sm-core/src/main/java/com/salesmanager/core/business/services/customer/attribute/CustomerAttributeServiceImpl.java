package com.salesmanager.core.business.services.customer.attribute;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.customer.attribute.CustomerAttributeRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.customer.attribute.CustomerAttribute;
import com.salesmanager.core.model.merchant.MerchantStore;



@Service("customerAttributeService")
public class CustomerAttributeServiceImpl extends
		SalesManagerEntityServiceImpl<Long, CustomerAttribute> implements CustomerAttributeService {
	
	private CustomerAttributeRepository customerAttributeRepository;

	@Inject
	public CustomerAttributeServiceImpl(CustomerAttributeRepository customerAttributeRepository) {
		super(customerAttributeRepository);
		this.customerAttributeRepository = customerAttributeRepository;
	}
	




	@Override
	public void saveOrUpdate(CustomerAttribute customerAttribute)
			throws ServiceException {

			customerAttributeRepository.save(customerAttribute);

		
	}
	
	@Override
	public void delete(CustomerAttribute attribute) throws ServiceException {
		
		//override method, this allows the error that we try to remove a detached instance
		attribute = this.getById(attribute.getId());
		System.out.println("$#2177#"); super.delete(attribute);
		
	}
	


	@Override
	public CustomerAttribute getByCustomerOptionId(MerchantStore store, Long customerId, Long id) {
		System.out.println("$#2178#"); return customerAttributeRepository.findByOptionId(store.getId(), customerId, id);
	}



	@Override
	public List<CustomerAttribute> getByCustomer(MerchantStore store, Customer customer) {
		System.out.println("$#2179#"); return customerAttributeRepository.findByCustomerId(store.getId(), customer.getId());
	}


	@Override
	public List<CustomerAttribute> getByCustomerOptionValueId(MerchantStore store,
			Long id) {
		System.out.println("$#2180#"); return customerAttributeRepository.findByOptionValueId(store.getId(), id);
	}
	
	@Override
	public List<CustomerAttribute> getByOptionId(MerchantStore store,
			Long id) {
		System.out.println("$#2181#"); return customerAttributeRepository.findByOptionId(store.getId(), id);
	}

}
