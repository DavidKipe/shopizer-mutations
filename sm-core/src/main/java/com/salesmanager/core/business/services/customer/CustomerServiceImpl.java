package com.salesmanager.core.business.services.customer;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.customer.CustomerRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.business.services.customer.attribute.CustomerAttributeService;
import com.salesmanager.core.model.common.Address;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.customer.CustomerCriteria;
import com.salesmanager.core.model.customer.CustomerList;
import com.salesmanager.core.model.customer.attribute.CustomerAttribute;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.modules.utils.GeoLocation;



@Service("customerService")
public class CustomerServiceImpl extends SalesManagerEntityServiceImpl<Long, Customer> implements CustomerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);
	
	private CustomerRepository customerRepository;
	
	@Inject
	private CustomerAttributeService customerAttributeService;
	
	@Inject
	private GeoLocation geoLocation;

	
	@Inject
	public CustomerServiceImpl(CustomerRepository customerRepository) {
		super(customerRepository);
		this.customerRepository = customerRepository;
	}

	@Override
	public List<Customer> getByName(String firstName) {
		System.out.println("$#2210#"); return customerRepository.findByName(firstName);
	}
	
	@Override
	public Customer getById(Long id) {
			System.out.println("$#2211#"); return customerRepository.findOne(id);
	}
	
	@Override
	public Customer getByNick(String nick) {
		System.out.println("$#2212#"); return customerRepository.findByNick(nick);
	}
	
	@Override
	public Customer getByNick(String nick, int storeId) {
		System.out.println("$#2213#"); return customerRepository.findByNick(nick, storeId);
	}
	
	@Override
	public List<Customer> getListByStore(MerchantStore store) {
		System.out.println("$#2214#"); return customerRepository.findByStore(store.getId());
	}
	
	@Override
	public CustomerList getListByStore(MerchantStore store, CustomerCriteria criteria) {
		System.out.println("$#2215#"); return customerRepository.listByStore(store,criteria);
	}
	
	@Override
	public Address getCustomerAddress(MerchantStore store, String ipAddress) throws ServiceException {
		
		try {
			System.out.println("$#2216#"); return geoLocation.getAddress(ipAddress);
		} catch(Exception e) {
			throw new ServiceException(e);
		}
		
	}

	@Override	
	public void saveOrUpdate(Customer customer) throws ServiceException {

		LOGGER.debug("Creating Customer");
		
		System.out.println("$#2218#"); System.out.println("$#2217#"); if(customer.getId()!=null && customer.getId()>0) {
			System.out.println("$#2220#"); super.update(customer);
		} else {			
		
			System.out.println("$#2221#"); super.create(customer);

		}
	}

	public void delete(Customer customer) throws ServiceException {
		customer = getById(customer.getId());
		
		//delete attributes
		List<CustomerAttribute> attributes =customerAttributeService.getByCustomer(customer.getMerchantStore(), customer);
		System.out.println("$#2222#"); if(attributes!=null) {
			for(CustomerAttribute attribute : attributes) {
				System.out.println("$#2223#"); customerAttributeService.delete(attribute);
			}
		}
		System.out.println("$#2224#"); customerRepository.delete(customer);

	}
	

}
