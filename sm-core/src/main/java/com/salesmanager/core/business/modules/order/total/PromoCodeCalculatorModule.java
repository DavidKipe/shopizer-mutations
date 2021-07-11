package com.salesmanager.core.business.modules.order.total;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.salesmanager.core.business.configuration.DroolsBeanFactory;
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

@Component
public class PromoCodeCalculatorModule implements OrderTotalPostProcessorModule {
	
	
	@Autowired
	private DroolsBeanFactory droolsBeanFactory;
	
	@Autowired
	private PricingService pricingService;

	private String name;
	private String code;

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		System.out.println("$#1455#"); return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;

	}

	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		System.out.println("$#1456#"); return code;
	}

	@Override
	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public OrderTotal caculateProductPiceVariation(OrderSummary summary, ShoppingCartItem shoppingCartItem,
			Product product, Customer customer, MerchantStore store) throws Exception {
		
		System.out.println("$#1457#"); Validate.notNull(summary, "OrderTotalSummary must not be null");
		System.out.println("$#1458#"); Validate.notNull(store, "MerchantStore must not be null");
		
		System.out.println("$#1459#"); if(StringUtils.isBlank(summary.getPromoCode())) {
			return null;
		}
		
		KieSession kieSession=droolsBeanFactory.getKieSession(ResourceFactory.newClassPathResource("com/salesmanager/drools/rules/PromoCoupon.drl"));
		
		OrderTotalResponse resp = new OrderTotalResponse();
		
		OrderTotalInputParameters inputParameters = new OrderTotalInputParameters();
		System.out.println("$#1460#"); inputParameters.setPromoCode(summary.getPromoCode());
		System.out.println("$#1461#"); inputParameters.setDate(new Date());
		
        kieSession.insert(inputParameters);
								System.out.println("$#1462#"); kieSession.setGlobal("total",resp);
        kieSession.fireAllRules();

		System.out.println("$#1463#"); if(resp.getDiscount() != null) {
			
			OrderTotal orderTotal = null;
			System.out.println("$#1464#"); if(resp.getDiscount() != null) {
					orderTotal = new OrderTotal();
					System.out.println("$#1465#"); orderTotal.setOrderTotalCode(Constants.OT_DISCOUNT_TITLE);
					System.out.println("$#1466#"); orderTotal.setOrderTotalType(OrderTotalType.SUBTOTAL);
					System.out.println("$#1467#"); orderTotal.setTitle(Constants.OT_SUBTOTAL_MODULE_CODE);
					System.out.println("$#1468#"); orderTotal.setText(summary.getPromoCode());
					
					//calculate discount that will be added as a negative value
					FinalPrice productPrice = pricingService.calculateProductPrice(product);
					
					Double discount = resp.getDiscount();
					BigDecimal reduction = productPrice.getFinalPrice().multiply(new BigDecimal(discount));
					reduction = reduction.multiply(new BigDecimal(shoppingCartItem.getQuantity()));
					
					System.out.println("$#1469#"); orderTotal.setValue(reduction);//discount value
					
					//TODO check expiration
			}
				
			
			
			System.out.println("$#1470#"); return orderTotal;
			
		}
		
		
		
		return null;
	}

}
