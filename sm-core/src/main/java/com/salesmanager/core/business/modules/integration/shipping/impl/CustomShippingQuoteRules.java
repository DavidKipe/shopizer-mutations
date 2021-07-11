package com.salesmanager.core.business.modules.integration.shipping.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.salesmanager.core.business.configuration.DroolsBeanFactory;
import com.salesmanager.core.model.common.Delivery;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.shipping.PackageDetails;
import com.salesmanager.core.model.shipping.ShippingConfiguration;
import com.salesmanager.core.model.shipping.ShippingOption;
import com.salesmanager.core.model.shipping.ShippingOrigin;
import com.salesmanager.core.model.shipping.ShippingQuote;
import com.salesmanager.core.model.system.CustomIntegrationConfiguration;
import com.salesmanager.core.model.system.IntegrationConfiguration;
import com.salesmanager.core.model.system.IntegrationModule;
import com.salesmanager.core.modules.constants.Constants;
import com.salesmanager.core.modules.integration.IntegrationException;
import com.salesmanager.core.modules.integration.shipping.model.ShippingQuoteModule;


public class CustomShippingQuoteRules implements ShippingQuoteModule {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomShippingQuoteRules.class);
	
	@Inject
	private DroolsBeanFactory droolsBeanFactory;

	public final static String MODULE_CODE = "customQuotesRules";

	@Override
	public void validateModuleConfiguration(
			IntegrationConfiguration integrationConfiguration,
			MerchantStore store) throws IntegrationException {
		// Not used

	}

	@Override
	public CustomIntegrationConfiguration getCustomModuleConfiguration(
			MerchantStore store) throws IntegrationException {
		// Not used
		return null;
	}

	@Override
	public List<ShippingOption> getShippingQuotes(ShippingQuote quote,
			List<PackageDetails> packages, BigDecimal orderTotal,
			Delivery delivery, ShippingOrigin origin, MerchantStore store,
			IntegrationConfiguration configuration, IntegrationModule module,
			ShippingConfiguration shippingConfiguration, Locale locale)
			throws IntegrationException {

		
		
		Validate.notNull(delivery, "Delivery cannot be null");
		Validate.notNull(delivery.getCountry(), "Delivery.country cannot be null");
		Validate.notNull(packages, "packages cannot be null");
		Validate.notEmpty(packages, "packages cannot be empty");
		
		//requires the postal code
		System.out.println("$#1002#"); if(StringUtils.isBlank(delivery.getPostalCode())) {
			System.out.println("$#1003#"); return null;
		}

		Double distance = null;
		
		System.out.println("$#1004#"); if(quote!=null) {
			//look if distance has been calculated
			System.out.println("$#1005#"); if(quote.getQuoteInformations()!=null) {
				System.out.println("$#1006#"); if(quote.getQuoteInformations().containsKey(Constants.DISTANCE_KEY)) {
					distance = (Double)quote.getQuoteInformations().get(Constants.DISTANCE_KEY);
				}
			}
		}
		
		//calculate volume (L x W x H)
		Double volume = null;
		Double weight = 0D;
		Double size = null;
		//calculate weight
		for(PackageDetails pack : packages) {
			System.out.println("$#1007#"); weight = weight + pack.getShippingWeight();
			System.out.println("$#1008#"); Double tmpVolume = pack.getShippingHeight() * pack.getShippingLength() * pack.getShippingWidth();
			System.out.println("$#1011#"); System.out.println("$#1010#"); if(volume == null || tmpVolume.doubleValue() > volume.doubleValue()) { //take the largest volume
				volume = tmpVolume;
			} 
			//largest size
			List<Double> sizeList = new ArrayList<Double>();
			sizeList.add(pack.getShippingHeight());
			sizeList.add(pack.getShippingWeight());
			sizeList.add(pack.getShippingLength());
			Double maxSize = (Double)Collections.max(sizeList);
			System.out.println("$#1014#"); System.out.println("$#1013#"); if(size==null || maxSize.doubleValue() > size.doubleValue()) {
				size = maxSize.doubleValue();
			}
		}
		
		//Build a ShippingInputParameters
		ShippingInputParameters inputParameters = new ShippingInputParameters();
		
		System.out.println("$#1016#"); inputParameters.setWeight((long)weight.doubleValue());
		System.out.println("$#1017#"); inputParameters.setCountry(delivery.getCountry().getIsoCode());
		System.out.println("$#1018#"); inputParameters.setProvince("*");
		System.out.println("$#1019#"); inputParameters.setModuleName(module.getCode());
		
		System.out.println("$#1020#"); if(delivery.getZone().getCode()!=null) {
			System.out.println("$#1021#"); inputParameters.setProvince(delivery.getZone().getCode());
		}
		
		System.out.println("$#1022#"); if(distance!=null) {
			double ddistance = distance.doubleValue();
			long ldistance = (long)ddistance;
			System.out.println("$#1023#"); inputParameters.setDistance(ldistance);
		}
		
		System.out.println("$#1024#"); if(volume!=null) {
			System.out.println("$#1025#"); inputParameters.setVolume((long)volume.doubleValue());
		}
		
		List<ShippingOption> options = quote.getShippingOptions();
		
		System.out.println("$#1026#"); if(options == null) {
			options = new ArrayList<ShippingOption>();
			System.out.println("$#1027#"); quote.setShippingOptions(options);
		}
		
		
		
		LOGGER.debug("Setting input parameters " + inputParameters.toString());
		
		
		KieSession kieSession=droolsBeanFactory.getKieSession(ResourceFactory.newClassPathResource("com/salesmanager/drools/rules/PriceByDistance.drl"));
		
		DecisionResponse resp = new DecisionResponse();
		
        kieSession.insert(inputParameters);
								System.out.println("$#1028#"); kieSession.setGlobal("decision",resp);
        kieSession.fireAllRules();
        //System.out.println(resp.getCustomPrice());

		System.out.println("$#1029#"); if(resp.getCustomPrice() != null) {

			ShippingOption shippingOption = new ShippingOption();
			
			
			System.out.println("$#1030#"); shippingOption.setOptionPrice(new BigDecimal(resp.getCustomPrice()));
			System.out.println("$#1031#"); shippingOption.setShippingModuleCode(MODULE_CODE);
			System.out.println("$#1032#"); shippingOption.setOptionCode(MODULE_CODE);
			System.out.println("$#1033#"); shippingOption.setOptionId(MODULE_CODE);

			options.add(shippingOption);
		}

		
		System.out.println("$#1034#"); return options;
		
		
	}

/*	public StatelessKnowledgeSession getShippingPriceRule() {
		return shippingPriceRule;
	}

	public void setShippingPriceRule(StatelessKnowledgeSession shippingPriceRule) {
		this.shippingPriceRule = shippingPriceRule;
	}

	public KnowledgeBase getKbase() {
		return kbase;
	}

	public void setKbase(KnowledgeBase kbase) {
		this.kbase = kbase;
	}*/

}
