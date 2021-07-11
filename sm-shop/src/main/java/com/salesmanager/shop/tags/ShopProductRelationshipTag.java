package com.salesmanager.shop.tags;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.services.catalog.product.relationship.ProductRelationshipService;
import com.salesmanager.core.business.utils.CacheUtils;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.relationship.ProductRelationship;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.catalog.product.ReadableProduct;
import com.salesmanager.shop.populator.catalog.ReadableProductPopulator;
import com.salesmanager.shop.utils.ImageFilePath;



public class ShopProductRelationshipTag extends RequestContextAwareTag  {
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ShopProductRelationshipTag.class);

	@Inject
	private ProductRelationshipService productRelationshipService;
	
	@Inject
	private PricingService pricingService;
	
	@Inject
	private CacheUtils cache;
	
	@Inject
	@Qualifier("img")
	private ImageFilePath imageUtils;
	
	
	private String groupName;



	public String getGroupName() {
		System.out.println("$#15471#"); return groupName;
	}


	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}


	@SuppressWarnings("unchecked")
	@Override
	protected int doStartTagInternal() throws Exception {
		System.out.println("$#15472#"); if (productRelationshipService == null || pricingService==null || imageUtils==null) {
			LOGGER.debug("Autowiring ProductRelationshipService");
            WebApplicationContext wac = getRequestContext().getWebApplicationContext();
            AutowireCapableBeanFactory factory = wac.getAutowireCapableBeanFactory();
												System.out.println("$#15475#"); factory.autowireBean(this);
        }
		
		HttpServletRequest request = (HttpServletRequest) pageContext
		.getRequest();

		MerchantStore store = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
		
		Language language = (Language)request.getAttribute(Constants.LANGUAGE);

		StringBuilder groupKey = new StringBuilder();
		groupKey
		.append(store.getId())
		.append("_")
		.append(Constants.PRODUCTS_GROUP_CACHE_KEY)
		.append("-")
		.append(this.getGroupName())
		.append("_")
		.append(language.getCode());
		
		StringBuilder groupKeyMissed = new StringBuilder();
		groupKeyMissed
		.append(groupKey.toString())
		.append(Constants.MISSED_CACHE_KEY);
		
		List<ReadableProduct> objects = null;
		
		System.out.println("$#15476#"); if(store.isUseCache()) {
		
			//get from the cache
			objects = (List<ReadableProduct>) cache.getFromCache(groupKey.toString());
			Boolean missedContent = null;

			System.out.println("$#15477#"); if(objects==null && missedContent==null) {
				objects = getProducts(request);

				//put in cache
				System.out.println("$#15479#"); cache.putInCache(objects, groupKey.toString());
					
			} else {
				//put in missed cache
				//cache.putInCache(new Boolean(true), groupKeyMissed.toString());
			}
		
		} else {
			objects = getProducts(request);
		}
		System.out.println("$#15481#"); System.out.println("$#15480#"); if(objects!=null && objects.size()>0) {
			System.out.println("$#15483#"); request.setAttribute(this.getGroupName(), objects);
		}
		
		return SKIP_BODY;

	}


	public int doEndTag() {
		System.out.println("$#15484#"); return EVAL_PAGE;
	}
	
	private List<ReadableProduct> getProducts(HttpServletRequest request) throws Exception {
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
		Language language = (Language)request.getAttribute(Constants.LANGUAGE);

		List<ProductRelationship> relationships = productRelationshipService.getByGroup(store, this.getGroupName(), language);
		
		ReadableProductPopulator populator = new ReadableProductPopulator();
		System.out.println("$#15485#"); populator.setPricingService(pricingService);
		System.out.println("$#15486#"); populator.setimageUtils(imageUtils);
		
		List<ReadableProduct> products = new ArrayList<ReadableProduct>();
		for(ProductRelationship relationship : relationships) {
			
			Product product = relationship.getRelatedProduct();
			
			ReadableProduct proxyProduct = populator.populate(product, new ReadableProduct(), store, language);
			products.add(proxyProduct);

		}
		
		System.out.println("$#15487#"); return products;
		
	}

	

}
