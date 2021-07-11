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
import com.salesmanager.core.model.shipping.ShippingOrigin;
import com.salesmanager.core.model.shipping.ShippingQuote;
import com.salesmanager.core.model.system.IntegrationConfiguration;
import com.salesmanager.core.model.system.IntegrationModule;
import com.salesmanager.core.modules.constants.Constants;
import com.salesmanager.core.modules.integration.IntegrationException;
import com.salesmanager.core.modules.integration.shipping.model.ShippingQuotePrePostProcessModule;

/**
 * Decides which shipping method is going to be used based on a decision table
 * @author carlsamson
 *
 */
public class ShippingDecisionPreProcessorImpl implements ShippingQuotePrePostProcessModule {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ShippingDecisionPreProcessorImpl.class);
	
	private final static String MODULE_CODE = "shippingDecisionModule";
	
	@Inject
	private DroolsBeanFactory droolsBeanFactory;
	
	//private StatelessKnowledgeSession shippingMethodDecision;
	
	//private KnowledgeBase kbase;
	
	//@Inject
	//KieContainer kieShippingDecisionContainer;
	
	@Override
	public void prePostProcessShippingQuotes(
			ShippingQuote quote,
			List<PackageDetails> packages, 
			BigDecimal orderTotal,
			Delivery delivery, 
			ShippingOrigin origin, 
			MerchantStore store,
			IntegrationConfiguration globalShippingConfiguration,
			IntegrationModule currentModule,
			ShippingConfiguration shippingConfiguration,
			List<IntegrationModule> allModules, 
			Locale locale)
			throws IntegrationException {
		
		
		Validate.notNull(delivery, "Delivery cannot be null");
		Validate.notNull(currentModule, "IntegrationModule cannot be null");
		Validate.notNull(delivery.getCountry(), "Delivery.country cannot be null");
		Validate.notNull(allModules, "List<IntegrationModule> cannot be null");
		Validate.notNull(packages, "packages cannot be null");
		Validate.notEmpty(packages, "packages cannot be empty");
		
		Double distance = null;
		
		System.out.println("$#1197#"); if(quote!=null) {
			//look if distance has been calculated
			System.out.println("$#1198#"); if(quote.getQuoteInformations()!=null) {
				System.out.println("$#1199#"); if(quote.getQuoteInformations().containsKey(Constants.DISTANCE_KEY)) {
					distance = (Double)quote.getQuoteInformations().get(Constants.DISTANCE_KEY);
				}
			}
		}
		
		//calculate volume (L x W x H)
		Double volume = null;
		Double weight = 0D;
		Double size = null;
		//calculate weight, volume and largest size
		for(PackageDetails pack : packages) {
			System.out.println("$#1200#"); weight = weight + pack.getShippingWeight();
			System.out.println("$#1201#"); Double tmpVolume = pack.getShippingHeight() * pack.getShippingLength() * pack.getShippingWidth();
			System.out.println("$#1204#"); System.out.println("$#1203#"); if(volume == null || tmpVolume.doubleValue() > volume.doubleValue()) { //take the largest volume
				volume = tmpVolume;
			} 
			//largest size
			List<Double> sizeList = new ArrayList<Double>();
			sizeList.add(pack.getShippingHeight());
			sizeList.add(pack.getShippingLength());
			sizeList.add(pack.getShippingWidth());
			Double maxSize = (Double)Collections.max(sizeList);
			System.out.println("$#1207#"); System.out.println("$#1206#"); if(size==null || maxSize.doubleValue() > size.doubleValue()) {
				size = maxSize.doubleValue();
			}
		}
		
		//Build a ShippingInputParameters
		ShippingInputParameters inputParameters = new ShippingInputParameters();
		
		System.out.println("$#1209#"); inputParameters.setWeight((long)weight.doubleValue());
		System.out.println("$#1210#"); inputParameters.setCountry(delivery.getCountry().getIsoCode());
		System.out.println("$#1211#"); if(delivery.getZone()!=null && delivery.getZone().getCode()!=null) {
			System.out.println("$#1213#"); inputParameters.setProvince(delivery.getZone().getCode());
		} else {
			System.out.println("$#1214#"); inputParameters.setProvince(delivery.getState());
		}
		//inputParameters.setModuleName(currentModule.getCode());
		
		
		System.out.println("$#1215#"); if(size!=null) {
			System.out.println("$#1216#"); inputParameters.setSize((long)size.doubleValue());
		}
		
		System.out.println("$#1217#"); if(distance!=null) {
			double ddistance = distance.doubleValue();
			long ldistance = (long)ddistance;
			System.out.println("$#1218#"); inputParameters.setDistance(ldistance);
		}
		
		System.out.println("$#1219#"); if(volume!=null) {
			System.out.println("$#1220#"); inputParameters.setVolume((long)volume.doubleValue());
		}
		
		LOGGER.debug("Setting input parameters " + inputParameters.toString());
		System.out.println("$#1221#"); System.out.println(inputParameters.toString());
		
		
		/**
		 * New code
		 */
		
		KieSession kieSession=droolsBeanFactory.getKieSession(ResourceFactory.newClassPathResource("com/salesmanager/drools/rules/ShippingDecision.drl"));
		
		DecisionResponse resp = new DecisionResponse();
		
        kieSession.insert(inputParameters);
								System.out.println("$#1222#"); kieSession.setGlobal("decision",resp);
        kieSession.fireAllRules();
        //System.out.println(resp.getModuleName());
								System.out.println("$#1223#"); inputParameters.setModuleName(resp.getModuleName());

		LOGGER.debug("Using shipping nodule " + inputParameters.getModuleName());
		
		System.out.println("$#1224#"); if(!StringUtils.isBlank(inputParameters.getModuleName())) {
			for(IntegrationModule toBeUsed : allModules) {
				System.out.println("$#1225#"); if(toBeUsed.getCode().equals(inputParameters.getModuleName())) {
					System.out.println("$#1226#"); quote.setCurrentShippingModule(toBeUsed);
					break;
				}
			}
		}
		
	}


	@Override
	public String getModuleCode() {
		System.out.println("$#1227#"); return MODULE_CODE;
	}
	







}
