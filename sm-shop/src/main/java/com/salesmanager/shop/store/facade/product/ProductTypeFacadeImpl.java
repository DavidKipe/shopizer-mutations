package com.salesmanager.shop.store.facade.product;

import java.util.stream.Collectors;

import org.jsoup.helper.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.product.type.ProductTypeService;
import com.salesmanager.core.model.catalog.product.type.ProductType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.catalog.PersistableProductTypeMapper;
import com.salesmanager.shop.mapper.catalog.ReadableProductTypeMapper;
import com.salesmanager.shop.model.catalog.product.type.PersistableProductType;
import com.salesmanager.shop.model.catalog.product.type.ReadableProductType;
import com.salesmanager.shop.model.catalog.product.type.ReadableProductTypeList;
import com.salesmanager.shop.store.api.exception.OperationNotAllowedException;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.controller.product.facade.ProductTypeFacade;

@Service("productTypeFacade")
public class ProductTypeFacadeImpl implements ProductTypeFacade {

	@Autowired
	private ProductTypeService productTypeService;
	
	@Autowired
	private ReadableProductTypeMapper readableProductTypeMapper;
	
	@Autowired
	private PersistableProductTypeMapper persistableProductTypeMapper;

	@Override
	public ReadableProductTypeList getByMerchant(MerchantStore store, Language language,  int count, int page) {

		System.out.println("$#15065#"); Validate.notNull(store, "MerchantStore cannot be null");
		ReadableProductTypeList returnList = new ReadableProductTypeList();

		try {
			
			Page<ProductType> types = productTypeService.getByMerchant(store, language, page, count);

			System.out.println("$#15066#"); if(types != null) {
				System.out.println("$#15068#"); System.out.println("$#15067#"); returnList.setList(types.getContent().stream().map(t -> readableProductTypeMapper.convert(t, store, language)).collect(Collectors.toList()));
				System.out.println("$#15069#"); returnList.setTotalPages(types.getTotalPages());
				System.out.println("$#15070#"); returnList.setRecordsTotal(types.getTotalElements());
				System.out.println("$#15071#"); returnList.setRecordsFiltered(types.getSize());
			}

			System.out.println("$#15072#"); return returnList;
		} catch (Exception e) {
			throw new ServiceRuntimeException(
					"An exception occured while getting product types for merchant[ " + store.getCode() + "]", e);
		}

	}

	@Override
	public ReadableProductType get(MerchantStore store, Long id, Language language) {
		
		System.out.println("$#15073#"); Validate.notNull(store, "MerchantStore cannot be null");
		System.out.println("$#15074#"); Validate.notNull(id, "ProductType code cannot be empty");
		try {
			
			ProductType type = productTypeService.getById(id, store, language);
			ReadableProductType readableType = readableProductTypeMapper.convert(type, store, language);
			System.out.println("$#15075#"); if(readableType == null) {
				throw new ResourceNotFoundException("Product type [" + id + "] not found for store [" + store.getCode() + "]");
			}
			
			System.out.println("$#15076#"); return readableType;
			
		} catch(Exception e) {
			throw new ServiceRuntimeException(
					"An exception occured while getting product type [" + id + "] not found for store [" + store.getCode() + "]", e);
		}

	}

	@Override
	public Long save(PersistableProductType type, MerchantStore store, Language language) {
		
		System.out.println("$#15077#"); Validate.notNull(type,"ProductType cannot be null");
		System.out.println("$#15078#"); Validate.notNull(store,"MerchantStore cannot be null");
		System.out.println("$#15079#"); Validate.notNull(type.getCode(),"ProductType code cannot be empty");
		
		try {
						
			System.out.println("$#15080#"); if(this.exists(type.getCode(), store, language)) {
				throw new OperationNotAllowedException(
						"Product type [" + type.getCode() + "] already exist for store [" + store.getCode() + "]");
			}
			
			ProductType model = persistableProductTypeMapper.convert(type, store, language);
			System.out.println("$#15081#"); model.setMerchantStore(store);
			productTypeService.saveOrUpdate(model);
			System.out.println("$#15082#"); return model.getId();

		} catch(Exception e) {
			throw new ServiceRuntimeException(
					"An exception occured while saving product type",e);
		}

	}

	@Override
	public void update(PersistableProductType type, Long id, MerchantStore store, Language language) {
		System.out.println("$#15083#"); Validate.notNull(type,"ProductType cannot be null");
		System.out.println("$#15084#"); Validate.notNull(store,"MerchantStore cannot be null");
		System.out.println("$#15085#"); Validate.notNull(id,"id cannot be empty");
		
		try {
			
			ProductType t = productTypeService.getById(id, store, language);		
			System.out.println("$#15086#"); if(t == null) {
				throw new ResourceNotFoundException(
						"Product type [" + type.getCode() + "] does not exist for store [" + store.getCode() + "]");
			}
			
			System.out.println("$#15087#"); type.setId(t.getId());
			System.out.println("$#15088#"); type.setCode(t.getCode());
			
			ProductType model = persistableProductTypeMapper.convert(type, store, language);
			productTypeService.saveOrUpdate(model);

		} catch(Exception e) {
			throw new ServiceRuntimeException(
					"An exception occured while saving product type",e);
		}

	}

	@Override
	public void delete(Long id, MerchantStore store, Language language) {
		System.out.println("$#15089#"); Validate.notNull(store,"MerchantStore cannot be null");
		System.out.println("$#15090#"); Validate.notNull(id,"id cannot be empty");
		
		try {
			
			ProductType t = productTypeService.getById(id, store, language);		
			System.out.println("$#15091#"); if(t == null) {
				throw new ResourceNotFoundException(
						"Product type [" + id + "] does not exist for store [" + store.getCode() + "]");
			}
			
			System.out.println("$#15092#"); productTypeService.delete(t);


		} catch(Exception e) {
			throw new ServiceRuntimeException(
					"An exception occured while saving product type",e);
		}

	}

	@Override
	public boolean exists(String code, MerchantStore store, Language language) {
		ProductType t;
		try {
			t = productTypeService.getByCode(code, store, language);
	    } catch (ServiceException e) {
			throw new RuntimeException("An exception occured while getting product type [" + code + "] for merchant store [" + store.getCode() +"]",e);
		}			
		System.out.println("$#15093#"); if(t != null) {
			System.out.println("$#15094#"); return true;
		}
		System.out.println("$#15095#"); return false;
	}


}
