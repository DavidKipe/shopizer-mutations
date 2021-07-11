package com.salesmanager.shop.tags;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.utils.ProductPriceUtils;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.currency.Currency;
import com.salesmanager.shop.constants.Constants;


public class ShopProductPriceFormatTag extends RequestContextAwareTag  {
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ShopProductPriceFormatTag.class);

	@Inject
	private PricingService pricingService;
	
	@Inject
	private ProductPriceUtils productPriceUtils;
	
	
	
	private BigDecimal value;
	


	private Currency currency;


	


	public Currency getCurrency() {
		System.out.println("$#15463#"); return currency;
	}


	public void setCurrency(Currency currency) {
		this.currency = currency;
	}


	@Override
	protected int doStartTagInternal() throws Exception {
		System.out.println("$#15464#"); if (pricingService == null || productPriceUtils==null) {
			LOGGER.debug("Autowiring productPriceUtils");
            WebApplicationContext wac = getRequestContext().getWebApplicationContext();
            AutowireCapableBeanFactory factory = wac.getAutowireCapableBeanFactory();
												System.out.println("$#15466#"); factory.autowireBean(this);
        }
		
		HttpServletRequest request = (HttpServletRequest) pageContext
		.getRequest();

		MerchantStore store = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);

		String formatedPrice = null;
		
		System.out.println("$#15467#"); if(this.getCurrency()!=null) {
			formatedPrice = productPriceUtils.getFormatedAmountWithCurrency(this.getCurrency(), this.getValue());
		} else {
			formatedPrice = pricingService.getDisplayAmount(this.getValue(), store);
		}
		
		System.out.println("$#15468#"); pageContext.getOut().print(formatedPrice);
		
		return SKIP_BODY;

	}


	public int doEndTag() {
		System.out.println("$#15469#"); return EVAL_PAGE;
	}


	public void setValue(BigDecimal value) {
		this.value = value;
	}


	public BigDecimal getValue() {
		System.out.println("$#15470#"); return value;
	}




	

}
