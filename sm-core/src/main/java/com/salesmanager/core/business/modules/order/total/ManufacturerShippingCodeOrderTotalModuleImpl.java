package com.salesmanager.core.business.modules.order.total;

import java.math.BigDecimal;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.price.FinalPrice;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.OrderSummary;
import com.salesmanager.core.model.order.OrderTotal;
import com.salesmanager.core.model.order.OrderTotalType;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.core.modules.order.total.OrderTotalPostProcessorModule;


/**
 * Add variation to the OrderTotal
 * This has to be defined in shopizer-core-ordertotal-processors
 * @author carlsamson
 *
 */
@Component
public class ManufacturerShippingCodeOrderTotalModuleImpl implements OrderTotalPostProcessorModule {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ManufacturerShippingCodeOrderTotalModuleImpl.class);
	
	private String name;
	private String code;
	
	//private StatelessKnowledgeSession orderTotalMethodDecision;//injected from xml file
	
	//private KnowledgeBase kbase;//injected from xml file
	
	//@Inject
	//KieContainer kieManufacturerBasedPricingContainer;
	

	PricingService pricingService;
	

	
	public PricingService getPricingService() {
		System.out.println("$#1431#"); return pricingService;
	}

	public void setPricingService(PricingService pricingService) {
		this.pricingService = pricingService;
	}

	@Override
	public OrderTotal caculateProductPiceVariation(final OrderSummary summary, ShoppingCartItem shoppingCartItem, Product product, Customer customer, MerchantStore store)
			throws Exception {

		
					System.out.println("$#1432#"); Validate.notNull(product,"product must not be null");
		System.out.println("$#1433#"); Validate.notNull(product.getManufacturer(),"product manufacturer must not be null");
		
		//requires shipping summary, otherwise return null
		System.out.println("$#1434#"); if(summary.getShippingSummary()==null) {
			return null;
		}

		OrderTotalInputParameters inputParameters = new OrderTotalInputParameters();
		System.out.println("$#1435#"); inputParameters.setItemManufacturerCode(product.getManufacturer().getCode());
		
		
		System.out.println("$#1436#"); inputParameters.setShippingMethod(summary.getShippingSummary().getShippingOptionCode());
		
		LOGGER.debug("Setting input parameters " + inputParameters.toString());
		
/*        KieSession kieSession = kieManufacturerBasedPricingContainer.newKieSession();
        kieSession.insert(inputParameters);
        kieSession.fireAllRules();*/
		
		
		//orderTotalMethodDecision.execute(inputParameters);
		
		
		LOGGER.debug("Applied discount " + inputParameters.getDiscount());
		
		OrderTotal orderTotal = null;
		System.out.println("$#1437#"); if(inputParameters.getDiscount() != null) {
				orderTotal = new OrderTotal();
				System.out.println("$#1438#"); orderTotal.setOrderTotalCode(Constants.OT_DISCOUNT_TITLE);
				System.out.println("$#1439#"); orderTotal.setOrderTotalType(OrderTotalType.SUBTOTAL);
				System.out.println("$#1440#"); orderTotal.setTitle(Constants.OT_SUBTOTAL_MODULE_CODE);
				
				//calculate discount that will be added as a negative value
				FinalPrice productPrice = pricingService.calculateProductPrice(product);
				
				Double discount = inputParameters.getDiscount();
				BigDecimal reduction = productPrice.getFinalPrice().multiply(new BigDecimal(discount));
				reduction = reduction.multiply(new BigDecimal(shoppingCartItem.getQuantity()));
				
				System.out.println("$#1441#"); orderTotal.setValue(reduction);
		}
			
		
		
		System.out.println("$#1442#"); return orderTotal;


	}
	
/*	public KnowledgeBase getKbase() {
		return kbase;
	}


	public void setKbase(KnowledgeBase kbase) {
		this.kbase = kbase;
	}

	public StatelessKnowledgeSession getOrderTotalMethodDecision() {
		return orderTotalMethodDecision;
	}

	public void setOrderTotalMethodDecision(StatelessKnowledgeSession orderTotalMethodDecision) {
		this.orderTotalMethodDecision = orderTotalMethodDecision;
	}*/

	@Override
	public String getName() {
		System.out.println("$#1443#"); return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getCode() {
		System.out.println("$#1444#"); return code;
	}

	@Override
	public void setCode(String code) {
		this.code = code;
	}



}
