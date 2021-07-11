package com.salesmanager.core.business.services.tax;

import java.util.List;

import javax.inject.Inject;

import org.jsoup.helper.Validate;
import org.springframework.stereotype.Service;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.tax.TaxClassRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.tax.taxclass.TaxClass;

@Service("taxClassService")
public class TaxClassServiceImpl extends SalesManagerEntityServiceImpl<Long, TaxClass>
		implements TaxClassService {

	private TaxClassRepository taxClassRepository;
	
	@Inject
	public TaxClassServiceImpl(TaxClassRepository taxClassRepository) {
		super(taxClassRepository);
		
		this.taxClassRepository = taxClassRepository;
	}
	
	@Override
	public List<TaxClass> listByStore(MerchantStore store) throws ServiceException {	
		System.out.println("$#3267#"); return taxClassRepository.findByStore(store.getId());
	}
	
	@Override
	public TaxClass getByCode(String code) throws ServiceException {
		System.out.println("$#3268#"); return taxClassRepository.findByCode(code);
	}
	
	@Override
	public TaxClass getByCode(String code, MerchantStore store) throws ServiceException {
		System.out.println("$#3269#"); return taxClassRepository.findByStoreAndCode(store.getId(), code);
	}
	
	@Override
	public void delete(TaxClass taxClass) throws ServiceException {
		
		TaxClass t = getById(taxClass.getId());
		System.out.println("$#3270#"); super.delete(t);
		
	}
	
	@Override
	public TaxClass getById(Long id) {
		System.out.println("$#3271#"); return taxClassRepository.getOne(id);
	}

	@Override
	public boolean exists(String code, MerchantStore store) throws ServiceException {
		System.out.println("$#3272#"); Validate.notNull(code, "TaxClass code cannot be empty");
		System.out.println("$#3273#"); Validate.notNull(store, "MerchantStore cannot be null");
		
		System.out.println("$#3275#"); System.out.println("$#3274#"); return taxClassRepository.findByStoreAndCode(store.getId(), code) != null ? true:false;

	}
	
	@Override
	public TaxClass saveOrUpdate(TaxClass taxClass) throws ServiceException {
		System.out.println("$#3277#"); System.out.println("$#3276#"); if(taxClass.getId()!=null && taxClass.getId().longValue() > 0) {
			System.out.println("$#3279#"); this.update(taxClass);
		} else {
			taxClass = super.saveAndFlush(taxClass);
		}
		System.out.println("$#3280#"); return taxClass;
	}

	

}
