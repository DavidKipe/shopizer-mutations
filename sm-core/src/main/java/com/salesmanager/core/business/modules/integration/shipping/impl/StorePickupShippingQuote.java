package com.salesmanager.core.business.modules.integration.shipping.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang.Validate;

import com.salesmanager.core.business.services.system.MerchantConfigurationService;
import com.salesmanager.core.business.utils.ProductPriceUtils;
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
import com.salesmanager.core.modules.integration.IntegrationException;
import com.salesmanager.core.modules.integration.shipping.model.ShippingQuoteModule;
import com.salesmanager.core.modules.integration.shipping.model.ShippingQuotePrePostProcessModule;


/**
 * Store pick up shipping module
 * 
 * Requires a configuration of a message note to be printed to the client
 * and a price for calculation (should be configured to 0)
 * 
 * Calculates a ShippingQuote with a price set to the price configured
 * @author carlsamson
 *
 */
public class StorePickupShippingQuote implements ShippingQuoteModule, ShippingQuotePrePostProcessModule {
	
	
	public final static String MODULE_CODE = "storePickUp";

	@Inject
	private MerchantConfigurationService merchantConfigurationService;
	
	@Inject
	private ProductPriceUtils productPriceUtils;


	@Override
	public void validateModuleConfiguration(
			IntegrationConfiguration integrationConfiguration,
			MerchantStore store) throws IntegrationException {
		
		
		
		
		List<String> errorFields = null;
		
		//validate integrationKeys['account']
		Map<String,String> keys = integrationConfiguration.getIntegrationKeys();
		//if(keys==null || StringUtils.isBlank(keys.get("price"))) {
		System.out.println("$#1259#"); if(keys==null) {
			errorFields = new ArrayList<String>();
			errorFields.add("price");
		} else {
			//validate it can be parsed to BigDecimal
			try {
				BigDecimal price = new BigDecimal(keys.get("price"));
			} catch(Exception e) {
				errorFields = new ArrayList<String>();
				errorFields.add("price");
			}
		}
		
		//if(keys==null || StringUtils.isBlank(keys.get("note"))) {
		System.out.println("$#1260#"); if(keys==null) {
			errorFields = new ArrayList<String>();
			errorFields.add("note");
		}


		
		System.out.println("$#1261#"); if(errorFields!=null) {
			IntegrationException ex = new IntegrationException(IntegrationException.ERROR_VALIDATION_SAVE);
			System.out.println("$#1262#"); ex.setErrorFields(errorFields);
			throw ex;
			
		}

	}

	@Override
	public List<ShippingOption> getShippingQuotes(
			ShippingQuote shippingQuote,
			List<PackageDetails> packages, BigDecimal orderTotal,
			Delivery delivery, ShippingOrigin origin, MerchantStore store,
			IntegrationConfiguration configuration, IntegrationModule module,
			ShippingConfiguration shippingConfiguration, Locale locale)
			throws IntegrationException {

		// TODO Auto-generated method stub
		System.out.println("$#1263#"); return null;

	}

	@Override
	public CustomIntegrationConfiguration getCustomModuleConfiguration(
			MerchantStore store) throws IntegrationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void prePostProcessShippingQuotes(ShippingQuote quote,
			List<PackageDetails> packages, BigDecimal orderTotal,
			Delivery delivery, ShippingOrigin origin, MerchantStore store,
			IntegrationConfiguration globalShippingConfiguration,
			IntegrationModule currentModule,
			ShippingConfiguration shippingConfiguration,
			List<IntegrationModule> allModules, Locale locale)
			throws IntegrationException {
		
		System.out.println("$#1264#"); Validate.notNull(globalShippingConfiguration, "IntegrationConfiguration must not be null for StorePickUp");
		
		
		try {
			
			System.out.println("$#1265#"); if(!globalShippingConfiguration.isActive())
				return;

			String region = null;
			
			String price = globalShippingConfiguration.getIntegrationKeys().get("price");
	
	
			System.out.println("$#1266#"); if(delivery.getZone()!=null) {
				region = delivery.getZone().getCode();
			} else {
				region = delivery.getState();
			}
			
			ShippingOption shippingOption = new ShippingOption();
			System.out.println("$#1267#"); shippingOption.setShippingModuleCode(MODULE_CODE);
			System.out.println("$#1268#"); shippingOption.setOptionCode(MODULE_CODE);
			System.out.println("$#1269#"); shippingOption.setOptionId(new StringBuilder().append(MODULE_CODE).append("_").append(region).toString());
			
			System.out.println("$#1270#"); shippingOption.setOptionPrice(productPriceUtils.getAmount(price));
	
			System.out.println("$#1271#"); shippingOption.setOptionPriceText(productPriceUtils.getStoreFormatedAmountWithCurrency(store, productPriceUtils.getAmount(price)));
	
			List<ShippingOption> options = quote.getShippingOptions();
			
			System.out.println("$#1272#"); if(options == null) {
				options = new ArrayList<ShippingOption>();
				System.out.println("$#1273#"); quote.setShippingOptions(options);
			}

			options.add(shippingOption);
			
			System.out.println("$#1274#"); if(quote.getSelectedShippingOption()==null) {
				System.out.println("$#1275#"); quote.setSelectedShippingOption(shippingOption);
			}

		
		} catch (Exception e) {
			throw new IntegrationException(e);
		}
	
		
		
	}

	@Override
	public String getModuleCode() {
		// TODO Auto-generated method stub
		System.out.println("$#1276#"); return MODULE_CODE;
	}



}
