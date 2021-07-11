package com.salesmanager.shop.store.facade.tax;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.tax.TaxClassService;
import com.salesmanager.core.business.services.tax.TaxRateService;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.tax.taxclass.TaxClass;
import com.salesmanager.core.model.tax.taxrate.TaxRate;
import com.salesmanager.shop.mapper.tax.PersistableTaxClassMapper;
import com.salesmanager.shop.mapper.tax.PersistableTaxRateMapper;
import com.salesmanager.shop.mapper.tax.ReadableTaxClassMapper;
import com.salesmanager.shop.mapper.tax.ReadableTaxRateMapper;
import com.salesmanager.shop.model.entity.Entity;
import com.salesmanager.shop.model.entity.ReadableEntityList;
import com.salesmanager.shop.model.tax.PersistableTaxClass;
import com.salesmanager.shop.model.tax.PersistableTaxRate;
import com.salesmanager.shop.model.tax.ReadableTaxClass;
import com.salesmanager.shop.model.tax.ReadableTaxRate;
import com.salesmanager.shop.store.api.exception.OperationNotAllowedException;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.api.exception.UnauthorizedException;
import com.salesmanager.shop.store.controller.tax.facade.TaxFacade;

@Service
public class TaxFacadeImpl implements TaxFacade {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TaxFacadeImpl.class);
	
	
	@Autowired
	private TaxClassService taxClassService;
	
	@Autowired
	private TaxRateService taxRateService;
	
	@Autowired
	private PersistableTaxClassMapper persistableTaxClassMapper;
	
	@Autowired
	private ReadableTaxClassMapper readableTaxClassMapper;
	
	@Autowired
	private PersistableTaxRateMapper persistableTaxRateMapper;
	
	@Autowired
	private ReadableTaxRateMapper readableTaxRateMapper;

	@Override
	public Entity createTaxClass(PersistableTaxClass taxClass, MerchantStore store, Language language) {
		System.out.println("$#15097#"); Validate.notNull(taxClass,"TaxClass cannot be null");
		System.out.println("$#15098#"); Validate.notNull(store,"MerchantStore cannot be null");
		System.out.println("$#15099#"); Validate.notNull(store.getCode(),"MerchantStore code cannot be null");
		try {
			
			
			System.out.println("$#15100#"); if(this.existsTaxClass(taxClass.getCode(), store, language)) {
				throw new OperationNotAllowedException("Tax class [" + taxClass.getCode() + "] already exist for store [" + store.getCode() + "]");
			}

			System.out.println("$#15101#"); taxClass.setStore(store.getCode());
			TaxClass model = persistableTaxClassMapper.convert(taxClass, store, language);
			model = taxClassService.saveOrUpdate(model);;
			Entity id = new Entity();
			System.out.println("$#15102#"); id.setId(model.getId());
			System.out.println("$#15103#"); return id;

		} catch (ServiceException e) {
			LOGGER.error("Error while saving taxClass [" +  taxClass.getCode() + "] for store [" + store.getCode() + "]", e);
			throw new ServiceRuntimeException("Error while saving taxClass [" +  taxClass.getCode() + "] for store [" + store.getCode() + "]", e);
		}
		
	}

	@Override
	public void deleteTaxClass(Long id, MerchantStore store, Language language) {
		System.out.println("$#15104#"); Validate.notNull(id,"TaxClass id cannot be null");
		System.out.println("$#15105#"); Validate.notNull(store,"MerchantStore cannot be null");
		System.out.println("$#15106#"); Validate.notNull(store.getCode(),"MerchantStore code cannot be null");
		try {
			TaxClass model = taxClassService.getById(id);
			System.out.println("$#15107#"); if(model == null) {
				throw new ResourceNotFoundException("TaxClass not found [" + id + "] for store [" + store.getCode() + "]");
			} else {
				System.out.println("$#15108#"); if(!model.getMerchantStore().getCode().equals(store.getCode())) {
					throw new UnauthorizedException("MerchantStore [" + store.getCode() + "] cannot delete tax class [" + id + "]");
				}
			}
			System.out.println("$#15109#"); taxClassService.delete(model);
				
		} catch (ServiceException e) {
			LOGGER.error("Error while getting taxClasse [" + id + "] for store [" + store.getCode() + "]", e);
			throw new ServiceRuntimeException("Error while getting taxClasse [" + id + "] for store [" + store.getCode() + "]", e);
		}

	}

	@Override
	public ReadableEntityList<ReadableTaxClass> taxClasses(MerchantStore store, Language language) {
		System.out.println("$#15110#"); Validate.notNull(store,"MerchantStore cannot be null");
		System.out.println("$#15111#"); Validate.notNull(store.getCode(),"MerchantStore code cannot be null");
		try {
			List<TaxClass> models = taxClassService.listByStore(store);
			
			List<ReadableTaxClass> taxClasses = models.stream().map(t -> convertToReadableTaxClass(t, store, language)).collect(Collectors.toList());

			ReadableEntityList<ReadableTaxClass> list = new ReadableEntityList<ReadableTaxClass>();
			System.out.println("$#15113#"); list.setItems(taxClasses);
			System.out.println("$#15114#"); list.setNumber(taxClasses.size());
			System.out.println("$#15115#"); list.setTotalPages(1);
			System.out.println("$#15116#"); list.setRecordsTotal(taxClasses.size());
			
			System.out.println("$#15117#"); return list;
			
		} catch (ServiceException e) {
			LOGGER.error("Error while getting taxClasses for store [" + store.getCode() + "]", e);
			throw new ServiceRuntimeException("Error while getting taxClasses for store [" + store.getCode() + "]", e);
		}
	}
	
	private ReadableTaxClass convertToReadableTaxClass(TaxClass t, MerchantStore store, Language language) {
		System.out.println("$#15118#"); return readableTaxClassMapper.convert(t, store, language);
	}
	
	@Override
	public void updateTaxClass(Long id, PersistableTaxClass taxClass, MerchantStore store, Language language) {
		System.out.println("$#15119#"); Validate.notNull(taxClass,"TaxClass cannot be null");
		System.out.println("$#15120#"); Validate.notNull(store,"MerchantStore cannot be null");
		System.out.println("$#15121#"); Validate.notNull(store.getCode(),"MerchantStore code cannot be null");
		try {
			TaxClass model = taxClassService.getById(id);
			System.out.println("$#15122#"); if(model == null) {
				throw new ResourceNotFoundException("TaxClass not found [" + id + "] for store [" + store.getCode() + "]");
			} else {
				System.out.println("$#15123#"); if(!model.getMerchantStore().getCode().equals(store.getCode())) {
					throw new UnauthorizedException("MerchantStore [" + store.getCode() + "] cannot update tax class [" + taxClass.getCode() + "]");
				}
			}
			model = persistableTaxClassMapper.convert(taxClass, store, language);
			taxClassService.saveOrUpdate(model);

		} catch (ServiceException e) {
			LOGGER.error("Error while saving taxClass [" +  taxClass.getCode() + "] for store [" + store.getCode() + "]", e);
			throw new ServiceRuntimeException("Error while saving taxClass [" +  taxClass.getCode() + "] for store [" + store.getCode() + "]", e);
		}
	}

	@Override
	public ReadableTaxClass taxClass(String code, MerchantStore store, Language language) {
		
		System.out.println("$#15124#"); Validate.notNull(code,"TaxClass code cannot be null");
		System.out.println("$#15125#"); Validate.notNull(store,"MerchantStore cannot be null");
		System.out.println("$#15126#"); Validate.notNull(store.getCode(),"MerchantStore code cannot be null");
		
		try {
			TaxClass model = taxClassService.getByCode(code, store);
			System.out.println("$#15127#"); if(model == null) {
				throw new ResourceNotFoundException("TaxClass not found [" + code + "] for store [" + store.getCode() + "]");
			}
			System.out.println("$#15128#"); if(model != null) {
				System.out.println("$#15129#"); if(!model.getMerchantStore().getCode().equals(store.getCode())) {
					throw new UnauthorizedException("MerchantStore [" + store.getCode() + "] cannot get tax class [" + code + "]");
				}
			}
			System.out.println("$#15130#"); return readableTaxClassMapper.convert(model, store, language);
		} catch (ServiceException e) {
			LOGGER.error("Error while getting taxClass [" +  code + "] for store [" + store.getCode() + "]", e);
			throw new ServiceRuntimeException("Error while getting taxClass [" +  code + "] for store [" + store.getCode() + "]", e);
		}

	}
	
	@Override
	public boolean existsTaxClass(String code, MerchantStore store, Language language) {
		try {
			boolean exist = taxClassService.exists(code, store);
			System.out.println("$#15132#"); System.out.println("$#15131#"); return exist;
		} catch (ServiceException e) {
			LOGGER.error("Error while getting taxClass [" +  code + "] for store [" + store.getCode() + "]", e);
			throw new ServiceRuntimeException("Error while saving taxClass [" +  code + "] for store [" + store.getCode() + "]", e);
		}
	}
	
	
	//get by code
	private TaxRate taxRateByCode(String code, MerchantStore store, Language language) {
		
		System.out.println("$#15133#"); Validate.notNull(code,"TaxRate code cannot be null");
		System.out.println("$#15134#"); Validate.notNull(store,"MerchantStore cannot be null");
		System.out.println("$#15135#"); Validate.notNull(store.getCode(),"MerchantStore code cannot be null");
		
		try {
			TaxRate model = taxRateService.getByCode(code, store);
			System.out.println("$#15136#"); if(model == null) {
				throw new ResourceNotFoundException("TaxRate not found [" + code + "] for store [" + store.getCode() + "]");
			}
			System.out.println("$#15137#"); if(model != null) {
				System.out.println("$#15138#"); if(!model.getMerchantStore().getCode().equals(store.getCode())) {
					throw new UnauthorizedException("MerchantStore [" + store.getCode() + "] cannot get tax rate [" + code + "]");
				}
			}
			System.out.println("$#15139#"); return model;
		} catch (ServiceException e) {
			LOGGER.error("Error while getting taxRate [" +  code + "] for store [" + store.getCode() + "]", e);
			throw new ServiceRuntimeException("Error while getting taxRate [" +  code + "] for store [" + store.getCode() + "]", e);
		}
		
	}
	
	//get by id
	private TaxRate taxRateById(Long id, MerchantStore store, Language language) {
		
		System.out.println("$#15140#"); Validate.notNull(id,"TaxRate id cannot be null");
		System.out.println("$#15141#"); Validate.notNull(store,"MerchantStore cannot be null");
		System.out.println("$#15142#"); Validate.notNull(store.getCode(),"MerchantStore code cannot be null");
		
		try {
			TaxRate model = taxRateService.getById(id, store);
			System.out.println("$#15143#"); if(model == null) {
				throw new ResourceNotFoundException("TaxRate not found [" + id + "]");
			} 
			System.out.println("$#15144#"); return model;
		} catch (Exception e) {
			LOGGER.error("Error while getting taxRate [" +  id + "] for store [" + store.getCode() + "]", e);
			throw new ServiceRuntimeException("Error while getting taxRate [" +  id + "] for store [" + store.getCode() + "]", e);
		}
		
	}


	@Override
	public void deleteTaxRate(Long id, MerchantStore store, Language language) {
		TaxRate model = taxRateById(id, store, language);
		try {
			System.out.println("$#15145#"); taxRateService.delete(model);
		} catch (ServiceException e) {
			LOGGER.error("Error while deleting taxRate [" +  id + "] for store [" + store.getCode() + "]", e);
			throw new ServiceRuntimeException("Error deleting taxRate [" +  id + "] for store [" + store.getCode() + "]", e);
		}

	}

	@Override
	public ReadableTaxRate taxRate(Long id, MerchantStore store, Language language) {
		
		TaxRate model = taxRateById(id, store, language);
		System.out.println("$#15146#"); return readableTaxRateMapper.convert(model, store, language);
	}

	@Override
	public Entity createTaxRate(PersistableTaxRate taxRate, MerchantStore store, Language language) {
		
		System.out.println("$#15147#"); Validate.notNull(taxRate,"TaxRate cannot be null");
		System.out.println("$#15148#"); Validate.notNull(taxRate.getCode(),"TaxRate code cannot be null");
		System.out.println("$#15149#"); Validate.notNull(store,"MerchantStore cannot be null");
		System.out.println("$#15150#"); Validate.notNull(store.getCode(),"MerchantStore code cannot be null");
		

		
		try {
			
			TaxRate model = taxRateService.getByCode(taxRate.getCode(), store);
			System.out.println("$#15151#"); if(model!=null) {
				throw new OperationNotAllowedException("Tax rate [" + taxRate.getCode() + "] already exist for store [" + store.getCode() + "]");
			}

			
			model = persistableTaxRateMapper.convert(taxRate, store, language);
			
			model = taxRateService.saveOrUpdate(model);
			
			Entity id = new Entity();
			System.out.println("$#15152#"); id.setId(model.getId());
			System.out.println("$#15153#"); return id;
		} catch (ServiceException e) {
			LOGGER.error("Error while saving taxRate [" +  taxRate.getCode() + "] for store [" + store.getCode() + "]", e);
			throw new ServiceRuntimeException("Error while saving taxRate [" +  taxRate.getCode() + "] for store [" + store.getCode() + "]", e);
		}
		

	}

	@Override
	public void updateTaxRate(Long id, PersistableTaxRate taxRate, MerchantStore store, Language language) {
		
		System.out.println("$#15154#"); Validate.notNull(taxRate,"TaxRate cannot be null");
		System.out.println("$#15155#"); Validate.notNull(id,"TaxRate id cannot be null");
		System.out.println("$#15156#"); Validate.notNull(store,"MerchantStore cannot be null");
		System.out.println("$#15157#"); Validate.notNull(store.getCode(),"MerchantStore code cannot be null");

		TaxRate model = taxRateById(id, store, language);
		
		model = persistableTaxRateMapper.convert(taxRate, model, store, language);
		
		try {
			model = taxRateService.saveOrUpdate(model);

		} catch (ServiceException e) {
			LOGGER.error("Error while saving taxRate [" +  taxRate.getCode() + "] for store [" + store.getCode() + "]", e);
			throw new ServiceRuntimeException("Error while saving taxRate [" +  taxRate.getCode() + "] for store [" + store.getCode() + "]", e);
		}
		
		
	}

	@Override
	public boolean existsTaxRate(String code, MerchantStore store, Language language) {

		System.out.println("$#15158#"); Validate.notNull(code,"TaxRate code cannot be null");
		System.out.println("$#15159#"); Validate.notNull(store,"MerchantStore cannot be null");
		System.out.println("$#15160#"); Validate.notNull(store.getCode(),"MerchantStore code cannot be null");
		
		TaxRate rate = taxRateByCode(code, store, language);
		System.out.println("$#15161#"); if(rate == null) {
			System.out.println("$#15162#"); return false;
		}
		System.out.println("$#15163#"); return true;
	}

	@Override
	public ReadableEntityList<ReadableTaxRate> taxRates(MerchantStore store, Language language) {
		
		System.out.println("$#15164#"); Validate.notNull(store,"MerchantStore cannot be null");
		System.out.println("$#15165#"); Validate.notNull(store.getCode(),"MerchantStore code cannot be null");
		
		try {
			List<TaxRate> rates = taxRateService.listByStore(store, language);
			List<ReadableTaxRate> readableRates = rates.stream().map(r -> readableTaxRateMapper.convert(r, store, language)).collect(Collectors.toList());
			
			ReadableEntityList<ReadableTaxRate> returnRates = new ReadableEntityList<ReadableTaxRate>();
			System.out.println("$#15167#"); returnRates.setItems(readableRates);
			System.out.println("$#15168#"); returnRates.setTotalPages(1);
			System.out.println("$#15169#"); returnRates.setNumber(readableRates.size());
			System.out.println("$#15170#"); returnRates.setRecordsTotal(readableRates.size());
			
			System.out.println("$#15171#"); return returnRates;
		} catch (ServiceException e) {
			LOGGER.error("Error while getting taxRates for store [" + store.getCode() + "]", e);
			throw new ServiceRuntimeException("Error while getting taxRates for store [" + store.getCode() + "]", e);
		}

	}

}
