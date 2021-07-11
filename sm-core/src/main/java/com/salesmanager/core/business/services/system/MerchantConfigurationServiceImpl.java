package com.salesmanager.core.business.services.system;

import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.system.MerchantConfigurationRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.system.MerchantConfig;
import com.salesmanager.core.model.system.MerchantConfiguration;
import com.salesmanager.core.model.system.MerchantConfigurationType;

@Service("merchantConfigurationService")
public class MerchantConfigurationServiceImpl extends
		SalesManagerEntityServiceImpl<Long, MerchantConfiguration> implements
		MerchantConfigurationService {

	private MerchantConfigurationRepository merchantConfigurationRepository;
	
	@Inject
	public MerchantConfigurationServiceImpl(
			MerchantConfigurationRepository merchantConfigurationRepository) {
			super(merchantConfigurationRepository);
			this.merchantConfigurationRepository = merchantConfigurationRepository;
	}
	

	@Override
	public MerchantConfiguration getMerchantConfiguration(String key, MerchantStore store) throws ServiceException {
		System.out.println("$#3220#"); return merchantConfigurationRepository.findByMerchantStoreAndKey(store.getId(), key);
	}
	
	@Override
	public List<MerchantConfiguration> listByStore(MerchantStore store) throws ServiceException {
		System.out.println("$#3221#"); return merchantConfigurationRepository.findByMerchantStore(store.getId());
	}
	
	@Override
	public List<MerchantConfiguration> listByType(MerchantConfigurationType type, MerchantStore store) throws ServiceException {
		System.out.println("$#3222#"); return merchantConfigurationRepository.findByMerchantStoreAndType(store.getId(), type);
	}
	
	@Override
	public void saveOrUpdate(MerchantConfiguration entity) throws ServiceException {
		

		
		System.out.println("$#3224#"); System.out.println("$#3223#"); if(entity.getId()!=null && entity.getId()>0) {
			System.out.println("$#3226#"); super.update(entity);
		} else {
			System.out.println("$#3227#"); super.create(entity);

		}
	}
	
	
	@Override
	public void delete(MerchantConfiguration merchantConfiguration) throws ServiceException {
		MerchantConfiguration config = merchantConfigurationRepository.getOne(merchantConfiguration.getId());
		System.out.println("$#3228#"); if(config!=null) {
			System.out.println("$#3229#"); super.delete(config);
		}
	}
	
	@Override
	public MerchantConfig getMerchantConfig(MerchantStore store) throws ServiceException {

		MerchantConfiguration configuration = merchantConfigurationRepository.findByMerchantStoreAndKey(store.getId(), MerchantConfigurationType.CONFIG.name());
		
		MerchantConfig config = null;
		System.out.println("$#3230#"); if(configuration!=null) {
			String value = configuration.getValue();
			
			ObjectMapper mapper = new ObjectMapper();
			try {
				config = mapper.readValue(value, MerchantConfig.class);
			} catch(Exception e) {
				throw new ServiceException("Cannot parse json string " + value);
			}
		}
		System.out.println("$#3231#"); return config;
		
	}
	
	@Override
	public void saveMerchantConfig(MerchantConfig config, MerchantStore store) throws ServiceException {
		
		MerchantConfiguration configuration = merchantConfigurationRepository.findByMerchantStoreAndKey(store.getId(), MerchantConfigurationType.CONFIG.name());

		System.out.println("$#3232#"); if(configuration==null) {
			configuration = new MerchantConfiguration();
			System.out.println("$#3233#"); configuration.setMerchantStore(store);
			System.out.println("$#3234#"); configuration.setKey(MerchantConfigurationType.CONFIG.name());
			System.out.println("$#3235#"); configuration.setMerchantConfigurationType(MerchantConfigurationType.CONFIG);
		}
		
		String value = config.toJSONString();
		System.out.println("$#3236#"); configuration.setValue(value);
		System.out.println("$#3238#"); System.out.println("$#3237#"); if(configuration.getId()!=null && configuration.getId()>0) {
			System.out.println("$#3240#"); super.update(configuration);
		} else {
			System.out.println("$#3241#"); super.create(configuration);

		}
		
	}
	


}
