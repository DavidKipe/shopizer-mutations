package com.salesmanager.shop.store.facade.payment;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.payments.PaymentService;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.payments.PaymentMethod;
import com.salesmanager.core.model.system.IntegrationConfiguration;
import com.salesmanager.shop.model.configuration.PersistableConfiguration;
import com.salesmanager.shop.model.configuration.ReadableConfiguration;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.controller.configurations.ConfigurationsFacade;


@Service("paymentConfigurationFacade")
public class PaymentConfigurationFacadeImpl implements ConfigurationsFacade {
	
	
	@Autowired
	private PaymentService paymentService;

	@Override
	public List<ReadableConfiguration> configurations(MerchantStore store) {
		
		try {
			
			List<PaymentMethod> methods = paymentService.getAcceptedPaymentMethods(store);
			List<ReadableConfiguration> configurations = 
					methods.stream()
					.map(m -> configuration(m.getInformations(), store)).collect(Collectors.toList());
			System.out.println("$#14783#"); return configurations;
			
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Error while getting payment configurations",e);
		}

	}

	@Override
	public ReadableConfiguration configuration(String module, MerchantStore store) {
		
		try {
			
			ReadableConfiguration config = null;
			List<PaymentMethod> methods = paymentService.getAcceptedPaymentMethods(store);
			Optional<ReadableConfiguration> configuration = 
					methods.stream()
					.filter(m -> module.equals(m.getModule().getCode()))
					.map(m -> this.configuration(m.getInformations(), store))
					.findFirst();
			
			System.out.println("$#14787#"); if(configuration.isPresent()) {
				config = configuration.get();
			}
			
			System.out.println("$#14788#"); return config;
		
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Error while getting payment configuration [" + module + "]",e);
		}

	}

	@Override
	public void saveConfiguration(PersistableConfiguration configuration, MerchantStore store) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteConfiguration(String module, MerchantStore store) {
		// TODO Auto-generated method stub

	}

	
	private ReadableConfiguration configuration(IntegrationConfiguration source, MerchantStore store) {
		
		ReadableConfiguration config = new ReadableConfiguration();
		System.out.println("$#14789#"); config.setActive(source.isActive());
		System.out.println("$#14790#"); config.setCode(source.getModuleCode());
		System.out.println("$#14791#"); config.setKeys(source.getIntegrationKeys());
		System.out.println("$#14792#"); config.setIntegrationOptions(source.getIntegrationOptions());
		
		System.out.println("$#14793#"); return config;
	}


}
