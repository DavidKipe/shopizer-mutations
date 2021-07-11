package com.salesmanager.shop.store.controller.search.facade;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.category.CategoryService;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.search.SearchService;
import com.salesmanager.core.business.utils.CoreConfiguration;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.ProductCriteria;
import com.salesmanager.core.model.catalog.product.ProductList;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.search.IndexProduct;
import com.salesmanager.core.model.search.SearchEntry;
import com.salesmanager.core.model.search.SearchFacet;
import com.salesmanager.core.model.search.SearchKeywords;
import com.salesmanager.core.model.search.SearchResponse;
import com.salesmanager.shop.model.catalog.SearchProductList;
import com.salesmanager.shop.model.catalog.SearchProductRequest;
import com.salesmanager.shop.model.catalog.category.ReadableCategory;
import com.salesmanager.shop.model.catalog.product.ReadableProduct;
import com.salesmanager.shop.model.entity.ValueList;
import com.salesmanager.shop.populator.catalog.ReadableCategoryPopulator;
import com.salesmanager.shop.populator.catalog.ReadableProductPopulator;
import com.salesmanager.shop.store.api.exception.ConversionRuntimeException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.model.search.AutoCompleteRequest;
import com.salesmanager.shop.utils.ImageFilePath;

@Service("searchFacade")
public class SearchFacadeImpl implements SearchFacade {

	private static final Logger LOGGER = LoggerFactory.getLogger(SearchFacadeImpl.class);

	@Inject
	private SearchService searchService;

	@Inject
	private ProductService productService;

	@Inject
	private CategoryService categoryService;

	@Inject
	private PricingService pricingService;

	@Inject
	@Qualifier("img")
	private ImageFilePath imageUtils;

	private final static String CATEGORY_FACET_NAME = "categories";
	private final static String MANUFACTURER_FACET_NAME = "manufacturer";
	private final static int AUTOCOMPLETE_ENTRIES_COUNT = 15;

	/**
	 * Index all products from the catalogue Better stop the system, remove ES
	 * indexex manually restart ES and run this query
	 */
	@Override
	@Async
	public void indexAllData(MerchantStore store) throws Exception {
		List<Product> products = productService.listByStore(store);

		for (Product product : products) {
			System.out.println("$#13589#"); searchService.index(store, product);
		}

	}

	@Override
	public SearchProductList search(MerchantStore store, Language language, SearchProductRequest searchRequest) {
		SearchResponse response = search(store, language.getCode(), searchRequest.getQuery(), searchRequest.getCount(),
				searchRequest.getStart());
		System.out.println("$#13590#"); return convertToSearchProductList(response, store, searchRequest.getStart(), searchRequest.getCount(),
				language);
	}

	private SearchResponse search(MerchantStore store, String languageCode, String query, Integer count,
			Integer start) {
		try {
			LOGGER.debug("Search " + query);
			System.out.println("$#13591#"); return searchService.search(store, languageCode, query, count, start);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException(e);
		}
	}

	@Override
	public SearchProductList convertToSearchProductList(SearchResponse searchResponse, MerchantStore merchantStore,
			int start, int count, Language language) {

		SearchProductList returnList = new SearchProductList();
		List<SearchEntry> entries = searchResponse.getEntries();

		System.out.println("$#13592#"); if (CollectionUtils.isNotEmpty(entries)) {
			List<Long> ids = entries.stream().map(SearchEntry::getIndexProduct).map(IndexProduct::getId)
					.map(Long::parseLong).collect(Collectors.toList());

			ProductCriteria searchCriteria = new ProductCriteria();
			System.out.println("$#13593#"); searchCriteria.setMaxCount(count);
			System.out.println("$#13594#"); searchCriteria.setStartIndex(start);
			System.out.println("$#13595#"); searchCriteria.setProductIds(ids);
			System.out.println("$#13596#"); searchCriteria.setAvailable(true);

			ProductList productList = productService.listByStore(merchantStore, language, searchCriteria);

			List<ReadableProduct> readableProducts = productList.getProducts().stream()
					.map(product -> convertProductToReadableProduct(product, merchantStore, language))
					.collect(Collectors.toList());

			returnList.getProducts().addAll(readableProducts);
			System.out.println("$#13598#"); returnList.setProductCount(productList.getProducts().size());
		}

		// Facets
		Map<String, List<SearchFacet>> facets = Optional.ofNullable(searchResponse.getFacets())
				.orElse(Collections.emptyMap());

		List<ReadableCategory> categoryProxies = getCategoryFacets(merchantStore, language, facets);
		System.out.println("$#13599#"); returnList.setCategoryFacets(categoryProxies);

		List<SearchFacet> manufacturersFacets = facets.entrySet().stream()
				.filter(e -> MANUFACTURER_FACET_NAME.equals(e.getKey())).findFirst().map(Entry::getValue)
				.orElse(Collections.emptyList());

		System.out.println("$#13602#"); if (CollectionUtils.isNotEmpty(manufacturersFacets)) {
			// TODO add manufacturer facets
		}
		System.out.println("$#13603#"); return returnList;
	}

	private List<ReadableCategory> getCategoryFacets(MerchantStore merchantStore, Language language,
			Map<String, List<SearchFacet>> facets) {
		List<SearchFacet> categoriesFacets = facets.entrySet().stream()
				.filter(e -> CATEGORY_FACET_NAME.equals(e.getKey())).findFirst().map(Entry::getValue)
				.orElse(Collections.emptyList());

		System.out.println("$#13606#"); if (CollectionUtils.isNotEmpty(categoriesFacets)) {

			List<String> categoryCodes = categoriesFacets.stream().map(SearchFacet::getName)
					.collect(Collectors.toList());

			Map<String, Long> productCategoryCount = categoriesFacets.stream()
					.collect(Collectors.toMap(SearchFacet::getKey, SearchFacet::getCount));

			List<Category> categories = categoryService.listByCodes(merchantStore, categoryCodes, language);
			System.out.println("$#13608#"); System.out.println("$#13607#"); return categories.stream().map(category -> convertCategoryToReadableCategory(merchantStore, language,
					productCategoryCount, category)).collect(Collectors.toList());
		} else {
			return Collections.emptyList();
		}
	}

	private ReadableCategory convertCategoryToReadableCategory(MerchantStore merchantStore, Language language,
			Map<String, Long> productCategoryCount, Category category) {
		ReadableCategoryPopulator populator = new ReadableCategoryPopulator();
		try {
			ReadableCategory categoryProxy = populator.populate(category, new ReadableCategory(), merchantStore,
					language);
			Long total = productCategoryCount.get(categoryProxy.getCode());
			System.out.println("$#13609#"); if (total != null) {
				System.out.println("$#13610#"); categoryProxy.setProductCount(total.intValue());
			}
			System.out.println("$#13611#"); return categoryProxy;
		} catch (ConversionException e) {
			throw new ConversionRuntimeException(e);
		}
	}

	private ReadableProduct convertProductToReadableProduct(Product product, MerchantStore merchantStore,
			Language language) {

		ReadableProductPopulator populator = new ReadableProductPopulator();
		System.out.println("$#13612#"); populator.setPricingService(pricingService);
		System.out.println("$#13613#"); populator.setimageUtils(imageUtils);

		try {
			System.out.println("$#13614#"); return populator.populate(product, new ReadableProduct(), merchantStore, language);
		} catch (ConversionException e) {
			throw new ConversionRuntimeException(e);
		}
	}

	@Override
	public ValueList autocompleteRequest(String word, MerchantStore store, Language language) {
		AutoCompleteRequest req = new AutoCompleteRequest(store.getCode(), language.getCode());
		//String formattedQuery = String.format(coreConfiguration.getProperty("AUTOCOMPLETE_QUERY"), query);

		/**
		 * formatted toJSONString because of te specific field names required in
		 * the UI
		 **/

		SearchKeywords keywords = getSearchKeywords(req, word);
		ValueList returnList = new ValueList();
		System.out.println("$#13615#"); returnList.setValues(keywords.getKeywords());
		System.out.println("$#13616#"); return returnList;
	}

	private SearchKeywords getSearchKeywords(AutoCompleteRequest req, String word) {
		try {
			LOGGER.debug("Search auto comlete " + word);
			System.out.println("$#13617#"); return searchService.searchForKeywords(req.getCollectionName(), word, AUTOCOMPLETE_ENTRIES_COUNT);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException(e);
		}
	}
}
