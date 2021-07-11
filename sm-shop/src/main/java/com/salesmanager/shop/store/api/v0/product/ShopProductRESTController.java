package com.salesmanager.shop.store.api.v0.product;

import com.salesmanager.core.business.services.catalog.category.CategoryService;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductOptionService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductOptionValueService;
import com.salesmanager.core.business.services.catalog.product.manufacturer.ManufacturerService;
import com.salesmanager.core.business.services.catalog.product.review.ProductReviewService;
import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.services.merchant.MerchantStoreService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.tax.TaxClassService;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.ProductCriteria;
import com.salesmanager.core.model.catalog.product.review.ProductReview;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.catalog.manufacturer.PersistableManufacturer;
import com.salesmanager.shop.model.catalog.product.*;
import com.salesmanager.shop.model.catalog.product.attribute.PersistableProductOption;
import com.salesmanager.shop.model.catalog.product.attribute.PersistableProductOptionValue;
import com.salesmanager.shop.populator.catalog.PersistableProductOptionPopulator;
import com.salesmanager.shop.populator.catalog.PersistableProductOptionValuePopulator;
import com.salesmanager.shop.populator.catalog.PersistableProductReviewPopulator;
import com.salesmanager.shop.populator.catalog.ReadableProductPopulator;
import com.salesmanager.shop.populator.manufacturer.PersistableManufacturerPopulator;
import com.salesmanager.shop.store.controller.items.facade.ProductItemsFacade;
import com.salesmanager.shop.store.controller.product.facade.ProductFacade;
import com.salesmanager.shop.store.model.filter.QueryFilter;
import com.salesmanager.shop.store.model.filter.QueryFilterType;
import com.salesmanager.shop.utils.ImageFilePath;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * API to create, read, update and delete a Product
 * API to create Manufacturer
 * @author Carl Samson
 *
 */
@Controller
@RequestMapping("/api/v0")
public class ShopProductRESTController {
	
	@Inject
	private MerchantStoreService merchantStoreService;
	
	@Inject
	private CategoryService categoryService;
	
	@Inject
	private CustomerService customerService;
	
	@Inject
	private ProductService productService;
	
	@Inject
	private ProductFacade productFacade;

	
	@Inject
	private ProductReviewService productReviewService;
	
	@Inject
	private PricingService pricingService;

	@Inject
	private ProductOptionService productOptionService;
	
	@Inject
	private ProductOptionValueService productOptionValueService;
	
	@Inject
	private TaxClassService taxClassService;
	
	@Inject
	private ManufacturerService manufacturerService;
	
	@Inject
	private LanguageService languageService;
	
	@Inject
	@Qualifier("img")
	private ImageFilePath imageUtils;
	

	
	private static final Logger LOGGER = LoggerFactory.getLogger(ShopProductRESTController.class);
	
	
	/**
	 * Create new product for a given MerchantStore
	 */
	@RequestMapping( value="/private/{store}/product", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public PersistableProduct createProduct(@PathVariable final String store, @Valid @RequestBody PersistableProduct product, HttpServletRequest request, HttpServletResponse response) throws Exception {

		
		try {
			
			MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
			System.out.println("$#11341#"); if(merchantStore!=null) {
				System.out.println("$#11342#"); if(!merchantStore.getCode().equals(store)) {
					merchantStore = null;
				}
			}
			
			System.out.println("$#11343#"); if(merchantStore== null) {
				merchantStore = merchantStoreService.getByCode(store);
			}
			
			System.out.println("$#11344#"); if(merchantStore==null) {
				LOGGER.error("Merchant store is null for code " + store);
				System.out.println("$#11345#"); response.sendError(503, "Merchant store is null for code " + store);
				return null;
			}
			
			productFacade.saveProduct(merchantStore, product, merchantStore.getDefaultLanguage());
			
			System.out.println("$#11346#"); return product;
			
		} catch (Exception e) {
			LOGGER.error("Error while saving product",e);
			try {
				System.out.println("$#11347#"); response.sendError(503, "Error while saving product " + e.getMessage());
			} catch (Exception ignore) {
			}
			
			return null;
		}
		
	}
	

	@RequestMapping( value="/private/{store}/product/{id}", method=RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteProduct(@PathVariable final String store, @PathVariable Long id, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Product product = productService.getById(id);
		System.out.println("$#11348#"); if(product != null && product.getMerchantStore().getCode().equalsIgnoreCase(store)){
			System.out.println("$#11350#"); productService.delete(product);
		}else{
			System.out.println("$#11351#"); response.sendError(404, "No Product found for ID : " + id);
		}
	}
	
	/**
	 * Method for creating a manufacturer
	 * @param store
	 * @param manufacturer
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping( value="/private/{store}/manufacturer", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public PersistableManufacturer createManufacturer(@PathVariable final String store, @Valid @RequestBody PersistableManufacturer manufacturer, HttpServletRequest request, HttpServletResponse response) throws Exception {

		
		try {
			
			MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
			System.out.println("$#11352#"); if(merchantStore!=null) {
				System.out.println("$#11353#"); if(!merchantStore.getCode().equals(store)) {
					merchantStore = null;
				}
			}
			
			System.out.println("$#11354#"); if(merchantStore== null) {
				merchantStore = merchantStoreService.getByCode(store);
			}
			
			System.out.println("$#11355#"); if(merchantStore==null) {
				LOGGER.error("Merchant store is null for code " + store);
				System.out.println("$#11356#"); response.sendError(503, "Merchant store is null for code " + store);
				return null;
			}

			PersistableManufacturerPopulator populator = new PersistableManufacturerPopulator();
			System.out.println("$#11357#"); populator.setLanguageService(languageService);
			
			com.salesmanager.core.model.catalog.product.manufacturer.Manufacturer manuf = new com.salesmanager.core.model.catalog.product.manufacturer.Manufacturer();
			
			populator.populate(manufacturer, manuf, merchantStore, merchantStore.getDefaultLanguage());
		
			System.out.println("$#11358#"); manufacturerService.save(manuf);
			
			System.out.println("$#11359#"); manufacturer.setId(manuf.getId());
			
			System.out.println("$#11360#"); return manufacturer;
			
		} catch (Exception e) {
			LOGGER.error("Error while saving product",e);
			try {
				System.out.println("$#11361#"); response.sendError(503, "Error while saving product " + e.getMessage());
			} catch (Exception ignore) {
			}
			
			return null;
		}
		
	}
	
	
	@RequestMapping( value="/private/{store}/product/optionValue", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public PersistableProductOptionValue createProductOptionValue(@PathVariable final String store, @Valid @RequestBody PersistableProductOptionValue optionValue, HttpServletRequest request, HttpServletResponse response) throws Exception {

		
		try {
			
			MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
			System.out.println("$#11362#"); if(merchantStore!=null) {
				System.out.println("$#11363#"); if(!merchantStore.getCode().equals(store)) {
					merchantStore = null;
				}
			}
			
			System.out.println("$#11364#"); if(merchantStore== null) {
				merchantStore = merchantStoreService.getByCode(store);
			}
			
			System.out.println("$#11365#"); if(merchantStore==null) {
				LOGGER.error("Merchant store is null for code " + store);
				System.out.println("$#11366#"); response.sendError(503, "Merchant store is null for code " + store);
				return null;
			}

			PersistableProductOptionValuePopulator populator = new PersistableProductOptionValuePopulator();
			System.out.println("$#11367#"); populator.setLanguageService(languageService);
			
			com.salesmanager.core.model.catalog.product.attribute.ProductOptionValue optValue = new com.salesmanager.core.model.catalog.product.attribute.ProductOptionValue();
			populator.populate(optionValue, optValue, merchantStore, merchantStore.getDefaultLanguage());
		
			System.out.println("$#11368#"); productOptionValueService.save(optValue);
			
			System.out.println("$#11369#"); optionValue.setId(optValue.getId());
			
			System.out.println("$#11370#"); return optionValue;
			
		} catch (Exception e) {
			LOGGER.error("Error while saving product option value",e);
			try {
				System.out.println("$#11371#"); response.sendError(503, "Error while saving product option value" + e.getMessage());
			} catch (Exception ignore) {
			}
			
			return null;
		}
		
	}
	
	
	@RequestMapping( value="/private/{store}/product/option", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public PersistableProductOption createProductOption(@PathVariable final String store, @Valid @RequestBody PersistableProductOption option, HttpServletRequest request, HttpServletResponse response) throws Exception {

		
		try {
			
			MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
			System.out.println("$#11372#"); if(merchantStore!=null) {
				System.out.println("$#11373#"); if(!merchantStore.getCode().equals(store)) {
					merchantStore = null;
				}
			}
			
			System.out.println("$#11374#"); if(merchantStore== null) {
				merchantStore = merchantStoreService.getByCode(store);
			}
			
			System.out.println("$#11375#"); if(merchantStore==null) {
				LOGGER.error("Merchant store is null for code " + store);
				System.out.println("$#11376#"); response.sendError(503, "Merchant store is null for code " + store);
				return null;
			}

			PersistableProductOptionPopulator populator = new PersistableProductOptionPopulator();
			System.out.println("$#11377#"); populator.setLanguageService(languageService);
			
			com.salesmanager.core.model.catalog.product.attribute.ProductOption opt = new com.salesmanager.core.model.catalog.product.attribute.ProductOption();
			populator.populate(option, opt, merchantStore, merchantStore.getDefaultLanguage());
		
			System.out.println("$#11378#"); productOptionService.save(opt);
			
			System.out.println("$#11379#"); option.setId(opt.getId());
			
			System.out.println("$#11380#"); return option;
			
		} catch (Exception e) {
			LOGGER.error("Error while saving product option",e);
			try {
				System.out.println("$#11381#"); response.sendError(503, "Error while saving product option" + e.getMessage());
			} catch (Exception ignore) {
			}
			
			return null;
		}
	}
	
	
	@RequestMapping( value="/private/{store}/product/review", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public PersistableProductReview createProductReview(@PathVariable final String store, @Valid @RequestBody PersistableProductReview review, HttpServletRequest request, HttpServletResponse response) throws Exception {

		
		try {
			
			MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
			System.out.println("$#11382#"); if(merchantStore!=null) {
				System.out.println("$#11383#"); if(!merchantStore.getCode().equals(store)) {
					merchantStore = null;
				}
			}
			
			System.out.println("$#11384#"); if(merchantStore== null) {
				merchantStore = merchantStoreService.getByCode(store);
			}
			
			System.out.println("$#11385#"); if(merchantStore==null) {
				LOGGER.error("Merchant store is null for code " + store);
				System.out.println("$#11386#"); response.sendError(500, "Merchant store is null for code " + store);
				return null;
			}
			
			
			//rating already exist
			ProductReview prodReview = productReviewService.getByProductAndCustomer(review.getProductId(), review.getCustomerId());
			System.out.println("$#11387#"); if(prodReview!=null) {
				System.out.println("$#11388#"); response.sendError(500, "A review already exist for this customer and product");
				return null;
			}
			
			//rating maximum 5
			System.out.println("$#11390#"); System.out.println("$#11389#"); if(review.getRating()>Constants.MAX_REVIEW_RATING_SCORE) {
				System.out.println("$#11391#"); response.sendError(503, "Maximum rating score is " + Constants.MAX_REVIEW_RATING_SCORE);
				return null;
			}
			
			

			PersistableProductReviewPopulator populator = new PersistableProductReviewPopulator();
			System.out.println("$#11392#"); populator.setLanguageService(languageService);
			System.out.println("$#11393#"); populator.setCustomerService(customerService);
			System.out.println("$#11394#"); populator.setProductService(productService);
			
			com.salesmanager.core.model.catalog.product.review.ProductReview rev = new com.salesmanager.core.model.catalog.product.review.ProductReview();
			populator.populate(review, rev, merchantStore, merchantStore.getDefaultLanguage());
		
			System.out.println("$#11395#"); productReviewService.create(rev);

			
			System.out.println("$#11396#"); review.setId(rev.getId());
			
			System.out.println("$#11397#"); return review;
			
		} catch (Exception e) {
			LOGGER.error("Error while saving product review",e);
			try {
				System.out.println("$#11398#"); response.sendError(503, "Error while saving product review" + e.getMessage());
			} catch (Exception ignore) {
			}
			
			return null;
		}
	}
	

	@RequestMapping("/public/products/{store}")
	@ResponseBody
	public ReadableProductList getProducts(@PathVariable String store, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		/** default routine **/
		
		MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
		System.out.println("$#11399#"); if(merchantStore!=null) {
			System.out.println("$#11400#"); if(!merchantStore.getCode().equals(store)) {
				merchantStore = null;
			}
		}
		
		System.out.println("$#11401#"); if(merchantStore== null) {
			merchantStore = merchantStoreService.getByCode(store);
		}
		
		System.out.println("$#11402#"); if(merchantStore==null) {
			LOGGER.error("Merchant store is null for code " + store);
			System.out.println("$#11403#"); response.sendError(503, "Merchant store is null for code " + store);
			return null;
		}
		
		Language l = merchantStore.getDefaultLanguage();
		
		String lang = l.getCode();
		
		System.out.println("$#11404#"); if(!StringUtils.isBlank(request.getParameter(Constants.LANG))) {
			
			lang = request.getParameter(Constants.LANG);
			
		}
		
		
		/** end default routine **/
		
		

		
		System.out.println("$#11405#"); return this.getProducts(0, 10000, store, lang, null, null, request, response);
	}
	
/*	*//**
	 * Will get products for a given category
	 * supports language by setting land as a query parameter
	 * supports paging by adding start and max as query parameters
	 * @param store
	 * @param language
	 * @param category
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 *//*
	@RequestMapping("/public/products/page/{start}/{max}/{store}/{language}/{category}.html")
	@ResponseBody
	public ReadableProductList getProducts(@PathVariable String store, @PathVariable final String category, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		*//** default routine **//*
		
		MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
		if(merchantStore!=null) {
			if(!merchantStore.getCode().equals(store)) {
				merchantStore = null;
			}
		}
		
		if(merchantStore== null) {
			merchantStore = merchantStoreService.getByCode(store);
		}
		
		if(merchantStore==null) {
			LOGGER.error("Merchant store is null for code " + store);
			response.sendError(503, "Merchant store is null for code " + store);
			return null;
		}
		
		Language language = merchantStore.getDefaultLanguage();
		
		String lang = language.getCode();
		
		if(!StringUtils.isBlank(request.getParameter(Constants.LANG))) {
			
			lang = request.getParameter(Constants.LANG);
			
		}
		
		
		*//** end default routine **//*
		
		
		//start
		int iStart = 0;
		if(!StringUtils.isBlank(request.getParameter(Constants.START))) {
			
			String start = request.getParameter(Constants.START);
			
			try {
				iStart = Integer.parseInt(start);
			} catch(Exception e) {
				LOGGER.error("Cannot parse start parameter " + start);
			}

		}
		
		//max
		int iMax = 0;
		if(!StringUtils.isBlank(request.getParameter(Constants.MAX))) {
			
			String max = request.getParameter(Constants.MAX);
			
			try {
				iMax = Integer.parseInt(max);
			} catch(Exception e) {
				LOGGER.error("Cannot parse max parameter " + max);
			}

		}

		
		return this.getProducts(iStart, iMax, store, lang, category, null, request, response);
	}*/
	
	
	/**
	 * An entry point for filtering by another entity such as Manufacturer
	 * filter=BRAND&filter-value=123
	 * @param start
	 * @param max
	 * @param store
	 * @param language
	 * @param category
	 * @param filterType
	 * @param filterValue
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/products/public/page/{start}/{max}/{store}/{language}/{category}.html/filter={filterType}/filter-value={filterValue}")
	@ResponseBody
	public ReadableProductList getProductsFilteredByType(@PathVariable int start, @PathVariable int max, @PathVariable String store, @PathVariable final String language, @PathVariable final String category, @PathVariable final String filterType, @PathVariable final String filterValue, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		List<QueryFilter> queryFilters = null;
		try {
			System.out.println("$#11406#"); if(filterType.equals(QueryFilterType.BRAND.name())) {//the only one implemented so far
				QueryFilter filter = new QueryFilter();
				System.out.println("$#11407#"); filter.setFilterType(QueryFilterType.BRAND);
				System.out.println("$#11408#"); filter.setFilterId(Long.parseLong(filterValue));
				System.out.println("$#11409#"); if(queryFilters==null) {
					queryFilters = new ArrayList<QueryFilter>();
				}
				queryFilters.add(filter);
			}
		} catch(Exception e) {
			LOGGER.error("Invalid filter or filter-value " + filterType + " - " + filterValue,e);
		}
		
		System.out.println("$#11410#"); return this.getProducts(start, max, store, language, category, queryFilters, request, response);
	}
	
	
	private ReadableProductList getProducts(final int start, final int max, final String store, final String language, final String category, final List<QueryFilter> filters, HttpServletRequest request, HttpServletResponse response) throws Exception {

		
		try {

			
			/**
			 * How to Spring MVC Rest web service - ajax / jquery
			 * http://codetutr.com/2013/04/09/spring-mvc-easy-rest-based-json-services-with-responsebody/
			 */
			
			MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
			
			
			Map<String,Language> langs = languageService.getLanguagesMap();
			
			System.out.println("$#11411#"); if(merchantStore!=null) {
				System.out.println("$#11412#"); if(!merchantStore.getCode().equals(store)) {
					merchantStore = null; //reset for the current request
				}
			}
			
			System.out.println("$#11413#"); if(merchantStore== null) {
				merchantStore = merchantStoreService.getByCode(store);
			}
			
			System.out.println("$#11414#"); if(merchantStore==null) {
				LOGGER.error("Merchant store is null for code " + store);
				System.out.println("$#11415#"); response.sendError(503, "Merchant store is null for code " + store);//TODO localized message
				return null;
			}
			


			Language lang = langs.get(language);
			System.out.println("$#11416#"); if(lang==null) {
				lang = langs.get(Constants.DEFAULT_LANGUAGE);
			}
			
			ProductCriteria productCriteria = new ProductCriteria();
			System.out.println("$#11417#"); productCriteria.setMaxCount(max);
			System.out.println("$#11418#"); productCriteria.setStartIndex(start);
			
			//get the category by code
			System.out.println("$#11419#"); if(!StringUtils.isBlank(category)) {
				Category cat = categoryService.getBySeUrl(merchantStore, category);
				
				System.out.println("$#11420#"); if(cat==null) {
					LOGGER.error("Category " + category + " is null");
					System.out.println("$#11421#"); response.sendError(503, "Category is null");//TODO localized message
					return null;
				}
				
				
				String lineage = new StringBuilder().append(cat.getLineage()).append(cat.getId()).append("/").toString();
				
				List<Category> categories = categoryService.getListByLineage(store, lineage);
				
				List<Long> ids = new ArrayList<Long>();
				System.out.println("$#11423#"); System.out.println("$#11422#"); if(categories!=null && categories.size()>0) {
					for(Category c : categories) {
						ids.add(c.getId());
					}
				} 
				ids.add(cat.getId());
				
				
				System.out.println("$#11425#"); productCriteria.setCategoryIds(ids);
			}
			
			System.out.println("$#11426#"); if(filters!=null) {
				for(QueryFilter filter : filters) {
					System.out.println("$#11427#"); if(filter.getFilterType().name().equals(QueryFilterType.BRAND.name())) {//the only filter implemented
						System.out.println("$#11428#"); productCriteria.setManufacturerId(filter.getFilterId());
					}
				}
			}

			com.salesmanager.core.model.catalog.product.ProductList products = productService.listByStore(merchantStore, lang, productCriteria);

			
			ReadableProductPopulator populator = new ReadableProductPopulator();
			System.out.println("$#11429#"); populator.setPricingService(pricingService);
			System.out.println("$#11430#"); populator.setimageUtils(imageUtils);
			
			
			ReadableProductList productList = new ReadableProductList();
			for(Product product : products.getProducts()) {

				//create new proxy product
				ReadableProduct readProduct = populator.populate(product, new ReadableProduct(), merchantStore, lang);
				productList.getProducts().add(readProduct);
				
			}
			
			System.out.println("$#11431#"); productList.setTotalPages(products.getTotalCount());
			
			
			System.out.println("$#11432#"); return productList;
			
		
		} catch (Exception e) {
			LOGGER.error("Error while getting products",e);
			System.out.println("$#11433#"); response.sendError(503, "An error occured while retrieving products " + e.getMessage());
		}
		
		return null;

	}
	
	
	@RequestMapping(value = "/public/{store}/product/{id}", method=RequestMethod.GET)
	@ResponseBody
	public ReadableProduct getProduct(@PathVariable String store, @PathVariable final Long id, @RequestParam String lang, HttpServletRequest request, HttpServletResponse response) throws Exception {

		
		/** bcz of the filter **/
		MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
		System.out.println("$#11434#"); if(merchantStore!=null) {
			System.out.println("$#11435#"); if(!merchantStore.getCode().equals(store)) {
				merchantStore = null;
			}
		}

		System.out.println("$#11436#"); if(store!=null) {
			merchantStore = merchantStoreService.getByCode(store);
		}
		
		System.out.println("$#11437#"); if(merchantStore==null) {
			LOGGER.error("Merchant store is null for code " + store);
			System.out.println("$#11438#"); response.sendError(503, "Merchant store is null for code " + store);
			return null;
		}

		Language language = null;
		
		System.out.println("$#11439#"); if(!StringUtils.isBlank(lang)) {
			language = languageService.getByCode(lang);
		}
		
		System.out.println("$#11440#"); if(language==null) {
			language = merchantStore.getDefaultLanguage();
		}
		
		ReadableProduct product = productFacade.getProduct(merchantStore, id, language);
		
		System.out.println("$#11441#"); if(product==null) {
			System.out.println("$#11442#"); response.sendError(404, "Product not fount for id " + id);
			return null;
		}
		
		System.out.println("$#11443#"); return product;
		
	}

	
	/**
	 * Update the price of an item
	 * ?lang=en|fr otherwise default store language
	 */
	@RequestMapping( value="/private/{store}/product/price/{sku}", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public ReadableProduct updateProductPrice(@PathVariable final String store, @Valid @RequestBody ProductPriceEntity price, @PathVariable final String sku, HttpServletRequest request, HttpServletResponse response) throws Exception {

		
		try {
			
			MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
			System.out.println("$#11444#"); if(merchantStore!=null) {
				System.out.println("$#11445#"); if(!merchantStore.getCode().equals(store)) {
					merchantStore = null;
				}
			}
			
			String lang = request.getParameter("lang");
			Language language = null;
			
			System.out.println("$#11446#"); if(merchantStore== null) {
				merchantStore = merchantStoreService.getByCode(store);
			}
			
			System.out.println("$#11447#"); if(merchantStore==null) {
				LOGGER.error("Merchant store is null for code " + store);
				System.out.println("$#11448#"); response.sendError(503, "Merchant store is null for code " + store);
				return null;
			}
			
			System.out.println("$#11449#"); if(StringUtils.isBlank(lang)) {
				language = merchantStore.getDefaultLanguage();
			} else {
				language = languageService.getByCode(lang);
			}
			
			System.out.println("$#11450#"); if(language==null) {
				language = merchantStore.getDefaultLanguage();
			}
			
			ReadableProduct product = productFacade.getProduct(merchantStore, sku, language);
			
			System.out.println("$#11451#"); if(product==null) {
				LOGGER.error("Product is null for sku " +sku);
				System.out.println("$#11452#"); response.sendError(503, "Product is null for sku " +sku);
				return null;
			}
			
			product = productFacade.updateProductPrice(product, price, language);
			
			System.out.println("$#11453#"); return product;

			
		} catch (Exception e) {
			LOGGER.error("Error while saving product",e);
			try {
				System.out.println("$#11454#"); response.sendError(503, "Error while updating product " + e.getMessage());
			} catch (Exception ignore) {
			}
			
			return null;
		}
		
	}

	/**
	 * Update the quantity of an item
	 * ?lang=en|fr otherwise default store language
	 */
	@RequestMapping( value="/private/{store}/product/quantity/{sku}/{qty}", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public ReadableProduct updateProductQuantity(@PathVariable final String store, @PathVariable final String sku, @PathVariable final int qty, HttpServletRequest request, HttpServletResponse response) throws Exception {

		
		try {
			
			MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
			System.out.println("$#11455#"); if(merchantStore!=null) {
				System.out.println("$#11456#"); if(!merchantStore.getCode().equals(store)) {
					merchantStore = null;
				}
			}
			
			String lang = request.getParameter("lang");
			Language language = null;
			
			System.out.println("$#11457#"); if(merchantStore== null) {
				merchantStore = merchantStoreService.getByCode(store);
			}
			
			System.out.println("$#11458#"); if(merchantStore==null) {
				LOGGER.error("Merchant store is null for code " + store);
				System.out.println("$#11459#"); response.sendError(503, "Merchant store is null for code " + store);
				return null;
			}
			
			System.out.println("$#11460#"); if(StringUtils.isBlank(lang)) {
				language = merchantStore.getDefaultLanguage();
			} else {
				language = languageService.getByCode(lang);
			}
			
			System.out.println("$#11461#"); if(language==null) {
				language = merchantStore.getDefaultLanguage();
			}
			
			ReadableProduct product = productFacade.getProduct(merchantStore, sku, language);
			
			System.out.println("$#11462#"); if(product==null) {
				LOGGER.error("Product is null for sku " +sku);
				System.out.println("$#11463#"); response.sendError(503, "Product is null for sku " +sku);
				return null;
			}
			
			product = productFacade.updateProductQuantity(product, qty, language);
			
			System.out.println("$#11464#"); return product;

			
		} catch (Exception e) {
			LOGGER.error("Error while saving product",e);
			try {
				System.out.println("$#11465#"); response.sendError(503, "Error while updating product " + e.getMessage());
			} catch (Exception ignore) {
			}
			
			return null;
		}
		
	}	

}
