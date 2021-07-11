package com.salesmanager.shop.store.controller.product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductAttributeService;
import com.salesmanager.core.business.services.catalog.product.relationship.ProductRelationshipService;
import com.salesmanager.core.business.services.catalog.product.review.ProductReviewService;
import com.salesmanager.core.business.utils.CacheUtils;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionDescription;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValue;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription;
import com.salesmanager.core.model.catalog.product.price.FinalPrice;
import com.salesmanager.core.model.catalog.product.relationship.ProductRelationship;
import com.salesmanager.core.model.catalog.product.relationship.ProductRelationshipType;
import com.salesmanager.core.model.catalog.product.review.ProductReview;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.catalog.product.ReadableProduct;
import com.salesmanager.shop.model.catalog.product.ReadableProductPrice;
import com.salesmanager.shop.model.catalog.product.ReadableProductReview;
import com.salesmanager.shop.model.shop.Breadcrumb;
import com.salesmanager.shop.model.shop.PageInformation;
import com.salesmanager.shop.populator.catalog.ReadableFinalPricePopulator;
import com.salesmanager.shop.populator.catalog.ReadableProductPopulator;
import com.salesmanager.shop.populator.catalog.ReadableProductReviewPopulator;
import com.salesmanager.shop.store.controller.ControllerConstants;
import com.salesmanager.shop.store.model.catalog.Attribute;
import com.salesmanager.shop.store.model.catalog.AttributeValue;
import com.salesmanager.shop.utils.BreadcrumbsUtils;
import com.salesmanager.shop.utils.ImageFilePath;
import com.salesmanager.shop.utils.PageBuilderUtils;



/**
 * Populates the product details page
 * @author Carl Samson
 *
 */
@Controller
@RequestMapping("/shop/product")
public class ShopProductController {
	
	@Inject
	private ProductService productService;
	
	@Inject
	private ProductAttributeService productAttributeService;
	
	@Inject
	private ProductRelationshipService productRelationshipService;
	
	@Inject
	private PricingService pricingService;
	
	@Inject
	private ProductReviewService productReviewService;

	@Inject
	private CacheUtils cache;
	
	@Inject
	private BreadcrumbsUtils breadcrumbsUtils;
	
	@Inject
	@Qualifier("img")
	private ImageFilePath imageUtils;
	
	private static final Logger LOG = LoggerFactory.getLogger(ShopProductController.class);
	

	/**
	 * Display product details with reference to caller page
	 * @param friendlyUrl
	 * @param ref
	 * @param model
	 * @param request
	 * @param response
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/{friendlyUrl}.html/ref={ref}")
	public String displayProductWithReference(@PathVariable final String friendlyUrl, @PathVariable final String ref, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		System.out.println("$#13508#"); return display(ref, friendlyUrl, model, request, response, locale);
	}

	

	/**
	 * Display product details no reference
	 * @param friendlyUrl
	 * @param model
	 * @param request
	 * @param response
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/{friendlyUrl}.html")
	public String displayProduct(@PathVariable final String friendlyUrl, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		System.out.println("$#13509#"); return display(null, friendlyUrl, model, request, response, locale);
	}


	@SuppressWarnings("unchecked")
	public String display(final String reference, final String friendlyUrl, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		

		MerchantStore store = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
		Language language = (Language)request.getAttribute("LANGUAGE");
		
		Product product = productService.getBySeUrl(store, friendlyUrl, locale);
				
		System.out.println("$#13510#"); if(product==null) {
			System.out.println("$#13511#"); return PageBuilderUtils.build404(store);
		}
		
		ReadableProductPopulator populator = new ReadableProductPopulator();
		System.out.println("$#13512#"); populator.setPricingService(pricingService);
		System.out.println("$#13513#"); populator.setimageUtils(imageUtils);
		
		ReadableProduct productProxy = populator.populate(product, new ReadableProduct(), store, language);

		//meta information
		PageInformation pageInformation = new PageInformation();
		System.out.println("$#13514#"); pageInformation.setPageDescription(productProxy.getDescription().getMetaDescription());
		System.out.println("$#13515#"); pageInformation.setPageKeywords(productProxy.getDescription().getKeyWords());
		System.out.println("$#13516#"); pageInformation.setPageTitle(productProxy.getDescription().getTitle());
		System.out.println("$#13517#"); pageInformation.setPageUrl(productProxy.getDescription().getFriendlyUrl());
		
		System.out.println("$#13518#"); request.setAttribute(Constants.REQUEST_PAGE_INFORMATION, pageInformation);
		
		Breadcrumb breadCrumb = breadcrumbsUtils.buildProductBreadcrumb(reference, productProxy, store, language, request.getContextPath());
		System.out.println("$#13519#"); request.getSession().setAttribute(Constants.BREADCRUMB, breadCrumb);
		System.out.println("$#13520#"); request.setAttribute(Constants.BREADCRUMB, breadCrumb);
		

		
		StringBuilder relatedItemsCacheKey = new StringBuilder();
		relatedItemsCacheKey
		.append(store.getId())
		.append("_")
		.append(Constants.RELATEDITEMS_CACHE_KEY)
		.append("-")
		.append(language.getCode());
		
		StringBuilder relatedItemsMissed = new StringBuilder();
		relatedItemsMissed
		.append(relatedItemsCacheKey.toString())
		.append(Constants.MISSED_CACHE_KEY);
		
		Map<Long,List<ReadableProduct>> relatedItemsMap = null;
		List<ReadableProduct> relatedItems = null;
		
		System.out.println("$#13521#"); if(store.isUseCache()) {

			//get from the cache
			relatedItemsMap = (Map<Long,List<ReadableProduct>>) cache.getFromCache(relatedItemsCacheKey.toString());
			System.out.println("$#13522#"); if(relatedItemsMap==null) {
				//get from missed cache
				//Boolean missedContent = (Boolean)cache.getFromCache(relatedItemsMissed.toString());

				//if(missedContent==null) {
					relatedItems = relatedItems(store, product, language);
					System.out.println("$#13523#"); if(relatedItems!=null) {
						relatedItemsMap = new HashMap<Long,List<ReadableProduct>>();
						relatedItemsMap.put(product.getId(), relatedItems);
						System.out.println("$#13524#"); cache.putInCache(relatedItemsMap, relatedItemsCacheKey.toString());
					} else {
						//cache.putInCache(new Boolean(true), relatedItemsMissed.toString());
					}
				//}
			} else {
				relatedItems = relatedItemsMap.get(product.getId());
			}
		} else {
			relatedItems = relatedItems(store, product, language);
		}
		
		model.addAttribute("relatedProducts",relatedItems);	
		Set<ProductAttribute> attributes = product.getAttributes();
		

		
		//split read only and options
		Map<Long,Attribute> readOnlyAttributes = null;
		Map<Long,Attribute> selectableOptions = null;
		
		System.out.println("$#13525#"); if(!CollectionUtils.isEmpty(attributes)) {
						
			for(ProductAttribute attribute : attributes) {
				Attribute attr = null;
				AttributeValue attrValue = new AttributeValue();
				ProductOptionValue optionValue = attribute.getProductOptionValue();
				
				System.out.println("$#13526#"); if(attribute.getAttributeDisplayOnly()==true) {//read only attribute
					System.out.println("$#13527#"); if(readOnlyAttributes==null) {
						readOnlyAttributes = new TreeMap<Long,Attribute>();
					}
					attr = readOnlyAttributes.get(attribute.getProductOption().getId());
					System.out.println("$#13528#"); if(attr==null) {
						attr = createAttribute(attribute, language);
					}
					System.out.println("$#13529#"); if(attr!=null) {
						readOnlyAttributes.put(attribute.getProductOption().getId(), attr);
						System.out.println("$#13530#"); attr.setReadOnlyValue(attrValue);
					}
				} else {//selectable option
					System.out.println("$#13531#"); if(selectableOptions==null) {
						selectableOptions = new TreeMap<Long,Attribute>();
					}
					attr = selectableOptions.get(attribute.getProductOption().getId());
					System.out.println("$#13532#"); if(attr==null) {
						attr = createAttribute(attribute, language);
					}
					System.out.println("$#13533#"); if(attr!=null) {
						selectableOptions.put(attribute.getProductOption().getId(), attr);
					}
				}
				
				
				
				System.out.println("$#13534#"); attrValue.setDefaultAttribute(attribute.getAttributeDefault());
				System.out.println("$#13535#"); attrValue.setId(attribute.getId());//id of the attribute
				System.out.println("$#13536#"); attrValue.setLanguage(language.getCode());
				System.out.println("$#13538#"); System.out.println("$#13537#"); if(attribute.getProductAttributePrice()!=null && attribute.getProductAttributePrice().doubleValue()>0) {
					String formatedPrice = pricingService.getDisplayAmount(attribute.getProductAttributePrice(), store);
					System.out.println("$#13540#"); attrValue.setPrice(formatedPrice);
				}
				
				System.out.println("$#13541#"); if(!StringUtils.isBlank(attribute.getProductOptionValue().getProductOptionValueImage())) {
					System.out.println("$#13542#"); attrValue.setImage(imageUtils.buildProductPropertyImageUtils(store, attribute.getProductOptionValue().getProductOptionValueImage()));
				}
				System.out.println("$#13543#"); attrValue.setSortOrder(0);
				System.out.println("$#13544#"); if(attribute.getProductOptionSortOrder()!=null) {
					System.out.println("$#13545#"); attrValue.setSortOrder(attribute.getProductOptionSortOrder().intValue());
				}
				
				List<ProductOptionValueDescription> descriptions = optionValue.getDescriptionsSettoList();
				ProductOptionValueDescription description = null;
				System.out.println("$#13547#"); System.out.println("$#13546#"); if(descriptions!=null && descriptions.size()>0) {
					description = descriptions.get(0);
					System.out.println("$#13550#"); System.out.println("$#13549#"); if(descriptions.size()>1) {
						for(ProductOptionValueDescription optionValueDescription : descriptions) {
							System.out.println("$#13551#"); if(optionValueDescription.getLanguage().getId().intValue()==language.getId().intValue()) {
								description = optionValueDescription;
								break;
							}
						}
					}
				}
				System.out.println("$#13552#"); attrValue.setName(description.getName());
				System.out.println("$#13553#"); attrValue.setDescription(description.getDescription());
				List<AttributeValue> attrs = attr.getValues();
				System.out.println("$#13554#"); if(attrs==null) {
					attrs = new ArrayList<AttributeValue>();
					System.out.println("$#13555#"); attr.setValues(attrs);
				}
				attrs.add(attrValue);
			}
			
		}
		
		

		List<ProductReview> reviews = productReviewService.getByProduct(product, language);
		System.out.println("$#13556#"); if(!CollectionUtils.isEmpty(reviews)) {
			List<ReadableProductReview> revs = new ArrayList<ReadableProductReview>();
			ReadableProductReviewPopulator reviewPopulator = new ReadableProductReviewPopulator();
			for(ProductReview review : reviews) {
				ReadableProductReview rev = new ReadableProductReview();
				reviewPopulator.populate(review, rev, store, language);
				revs.add(rev);
			}
			model.addAttribute("reviews", revs);
		}
		
		List<Attribute> attributesList = null;
		System.out.println("$#13557#"); if(readOnlyAttributes!=null) {
			attributesList = new ArrayList<Attribute>(readOnlyAttributes.values());
		}
		
		List<Attribute> optionsList = null;
		System.out.println("$#13558#"); if(selectableOptions!=null) {
			optionsList = new ArrayList<Attribute>(selectableOptions.values());
			//order attributes by sort order
			for(Attribute attr : optionsList) {
				System.out.println("$#13559#"); Collections.sort(attr.getValues(), new Comparator<AttributeValue>(){
				     public int compare(AttributeValue o1, AttributeValue o2){
														System.out.println("$#13585#"); if(o1.getSortOrder()== o2.getSortOrder())
					             return 0;
														System.out.println("$#13588#"); System.out.println("$#13587#"); System.out.println("$#13586#"); return o1.getSortOrder() < o2.getSortOrder() ? -1 : 1;
				    	
				     }
				});
			}
		}
		
		model.addAttribute("attributes", attributesList);
		model.addAttribute("options", optionsList);
			
		model.addAttribute("product", productProxy);

		
		/** template **/
		StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Product.product).append(".").append(store.getStoreTemplate());

		System.out.println("$#13560#"); return template.toString();
	}
	
    @RequestMapping(value={"/{productId}/calculatePrice.json"}, method=RequestMethod.POST)
	public @ResponseBody
	ReadableProductPrice calculatePrice(@RequestParam(value="attributeIds[]") Long[] attributeIds, @PathVariable final Long productId, final HttpServletRequest request, final HttpServletResponse response, final Locale locale) throws Exception {

    	
    	MerchantStore store = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
		Language language = (Language)request.getAttribute("LANGUAGE");
		
		
		Product product = productService.getById(productId);
		
		@SuppressWarnings("unchecked")
		List<Long> ids = new ArrayList<Long>(Arrays.asList(attributeIds));
		List<ProductAttribute> attributes = productAttributeService.getByAttributeIds(store, product, ids);      
		
		for(ProductAttribute attribute : attributes) {
			System.out.println("$#13561#"); if(attribute.getProduct().getId().longValue()!=productId.longValue()) {
				return null;
			}
		}
		
		FinalPrice price = pricingService.calculateProductPrice(product, attributes);
    	ReadableProductPrice readablePrice = new ReadableProductPrice();
    	ReadableFinalPricePopulator populator = new ReadableFinalPricePopulator();
					System.out.println("$#13562#"); populator.setPricingService(pricingService);
    	populator.populate(price, readablePrice, store, language);
					System.out.println("$#13563#"); return readablePrice;
    	
    }
	
	private Attribute createAttribute(ProductAttribute productAttribute, Language language) {
		
		Attribute attribute = new Attribute();
		System.out.println("$#13564#"); attribute.setId(productAttribute.getProductOption().getId());//attribute of the option
		System.out.println("$#13565#"); attribute.setType(productAttribute.getProductOption().getProductOptionType());
		List<ProductOptionDescription> descriptions = productAttribute.getProductOption().getDescriptionsSettoList();
		ProductOptionDescription description = null;
		System.out.println("$#13567#"); System.out.println("$#13566#"); if(descriptions!=null && descriptions.size()>0) {
			description = descriptions.get(0);
			System.out.println("$#13570#"); System.out.println("$#13569#"); if(descriptions.size()>1) {
				for(ProductOptionDescription optionDescription : descriptions) {
					System.out.println("$#13571#"); if(optionDescription.getLanguage().getId().intValue()==language.getId().intValue()) {
						description = optionDescription;
						break;
					}
				}
			}
		}
		
		System.out.println("$#13572#"); if(description==null) {
			return null;
		}
		
		System.out.println("$#13573#"); attribute.setType(productAttribute.getProductOption().getProductOptionType());
		System.out.println("$#13574#"); attribute.setLanguage(language.getCode());
		System.out.println("$#13575#"); attribute.setName(description.getName());
		System.out.println("$#13576#"); attribute.setCode(productAttribute.getProductOption().getCode());

		
		System.out.println("$#13577#"); return attribute;
		
	}
	
	private List<ReadableProduct> relatedItems(MerchantStore store, Product product, Language language) throws Exception {
		
		
		ReadableProductPopulator populator = new ReadableProductPopulator();
		System.out.println("$#13578#"); populator.setPricingService(pricingService);
		System.out.println("$#13579#"); populator.setimageUtils(imageUtils);
		
		List<ProductRelationship> relatedItems = productRelationshipService.getByType(store, product, ProductRelationshipType.RELATED_ITEM);
		System.out.println("$#13581#"); System.out.println("$#13580#"); if(relatedItems!=null && relatedItems.size()>0) {
			List<ReadableProduct> items = new ArrayList<ReadableProduct>();
			for(ProductRelationship relationship : relatedItems) {
				Product relatedProduct = relationship.getRelatedProduct();
				ReadableProduct proxyProduct = populator.populate(relatedProduct, new ReadableProduct(), store, language);
				items.add(proxyProduct);
			}
			System.out.println("$#13583#"); return items;
		}
		
		System.out.println("$#13584#"); return null;
	}
	


}
