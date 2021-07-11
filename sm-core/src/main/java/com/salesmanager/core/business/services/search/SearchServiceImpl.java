package com.salesmanager.core.business.services.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.JsonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.utils.CoreConfiguration;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.description.ProductDescription;
import com.salesmanager.core.model.catalog.product.price.FinalPrice;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.search.IndexProduct;
import com.salesmanager.core.model.search.SearchEntry;
import com.salesmanager.core.model.search.SearchFacet;
import com.salesmanager.core.model.search.SearchKeywords;
import com.shopizer.search.services.Facet;
import com.shopizer.search.services.SearchHit;
import com.shopizer.search.services.SearchRequest;
import com.shopizer.search.services.SearchResponse;



@Service("productSearchService")
public class SearchServiceImpl implements com.salesmanager.core.business.services.search.SearchService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchServiceImpl.class);
	
	
	private final static String PRODUCT_INDEX_NAME = "product";
	private final static String UNDERSCORE = "_";
	private final static String INDEX_PRODUCTS = "INDEX_PRODUCTS";

	@Inject
	private com.shopizer.search.services.SearchService searchService;
	
	@Inject
	private PricingService pricingService;
	
	@Inject
	private CoreConfiguration configuration;
	

	public void initService() {
		System.out.println("$#2833#"); searchService.initService();
	}

	@Async
	@SuppressWarnings("rawtypes")
	public void index(MerchantStore store, Product product)
			throws ServiceException {
		
		/**
		 * When a product is saved or updated the indexing process occurs
		 * 
		 * A product entity will have to be transformed to a bean ProductIndex
		 * which contains the indices as described in product.json
		 * 
		 * {"product": {
						"properties" :  {
							"name" : {"type":"string","index":"analyzed"},
							"price" : {"type":"string","index":"not_analyzed"},
							"category" : {"type":"string","index":"not_analyzed"},
							"lang" : {"type":"string","index":"not_analyzed"},
							"available" : {"type":"string","index":"not_analyzed"},
							"description" : {"type":"string","index":"analyzed","index_analyzer":"english"}, 
							"tags" : {"type":"string","index":"not_analyzed"} 
						 } 
			            }
			}
		 *
		 * productService saveOrUpdate as well as create and update will invoke
		 * productSearchService.index	
		 * 
		 * A copy of properies between Product to IndexProduct
		 * Then IndexProduct will be transformed to a json representation by the invocation
		 * of .toJSONString on IndexProduct
		 * 
		 * Then index product
		 * searchService.index(json, "product_<LANGUAGE_CODE>_<MERCHANT_CODE>", "product");
		 * 
		 * example ...index(json,"product_en_default",product)
		 * 
		 */
		
		System.out.println("$#2834#"); if(configuration.getProperty(INDEX_PRODUCTS)==null || configuration.getProperty(INDEX_PRODUCTS).equals(Constants.FALSE)) {
			return;
		}
		
		FinalPrice price = pricingService.calculateProductPrice(product);

		
		Set<ProductDescription> descriptions = product.getDescriptions();
		for(ProductDescription description : descriptions) {
			
			StringBuilder collectionName = new StringBuilder();
			collectionName.append(PRODUCT_INDEX_NAME).append(UNDERSCORE).append(description.getLanguage().getCode()).append(UNDERSCORE).append(store.getCode().toLowerCase());
			
			IndexProduct index = new IndexProduct();

			System.out.println("$#2836#"); index.setId(String.valueOf(product.getId()));
			System.out.println("$#2837#"); index.setStore(store.getCode().toLowerCase());
			System.out.println("$#2838#"); index.setLang(description.getLanguage().getCode());
			System.out.println("$#2839#"); index.setAvailable(product.isAvailable());
			System.out.println("$#2840#"); index.setDescription(description.getDescription());
			System.out.println("$#2841#"); index.setName(description.getName());
			System.out.println("$#2842#"); if(product.getManufacturer()!=null) {
				System.out.println("$#2843#"); index.setManufacturer(String.valueOf(product.getManufacturer().getId()));
			}
			System.out.println("$#2844#"); if(price!=null) {
				System.out.println("$#2845#"); index.setPrice(price.getFinalPrice().doubleValue());
			}
			System.out.println("$#2846#"); index.setHighlight(description.getProductHighlight());
			System.out.println("$#2847#"); if(!StringUtils.isBlank(description.getMetatagKeywords())){
				String[] tags = description.getMetatagKeywords().split(",");
				@SuppressWarnings("unchecked")
				List<String> tagsList = new ArrayList(Arrays.asList(tags));
				System.out.println("$#2848#"); index.setTags(tagsList);
			}

			
			Set<Category> categories = product.getCategories();
			System.out.println("$#2849#"); if(!CollectionUtils.isEmpty(categories)) {
				List<String> categoryList = new ArrayList<String>();
				for(Category category : categories) {
					categoryList.add(category.getCode());
				}
				System.out.println("$#2850#"); index.setCategories(categoryList);
			}
			
			String jsonString = index.toJSONString();
			try {
				System.out.println("$#2851#"); searchService.index(jsonString, collectionName.toString());
			} catch (Exception e) {
				throw new ServiceException("Cannot index product id [" + product.getId() + "], " + e.getMessage() ,e);
			}
		}
	}


	public void deleteIndex(MerchantStore store, Product product) throws ServiceException {
		
		System.out.println("$#2852#"); if(configuration.getProperty(INDEX_PRODUCTS)==null || configuration.getProperty(INDEX_PRODUCTS).equals(Constants.FALSE)) {
			return;
		}
		
		Set<ProductDescription> descriptions = product.getDescriptions();
		for(ProductDescription description : descriptions) {
			
			StringBuilder collectionName = new StringBuilder();
			collectionName.append(PRODUCT_INDEX_NAME).append(UNDERSCORE).append(description.getLanguage().getCode()).append(UNDERSCORE).append(store.getCode().toLowerCase());

			try {
				System.out.println("$#2854#"); searchService.deleteObject(collectionName.toString(), String.valueOf(product.getId()));
			} catch (Exception e) {
				LOGGER.error("Cannot delete index for product id [" + product.getId() + "], ",e);
			}
		}
	
	}
	

	public SearchKeywords searchForKeywords(String collectionName, String word, int entriesCount) throws ServiceException {
		
     		
		try {

			SearchResponse response = searchService.searchAutoComplete(collectionName, word, entriesCount);
			
			SearchKeywords keywords = new SearchKeywords();
			System.out.println("$#2855#"); if(response!=null && response.getInlineSearchList() != null) {
					System.out.println("$#2857#"); keywords.setKeywords(Arrays.asList(response.getInlineSearchList()));
			}
			
			System.out.println("$#2858#"); return keywords;
			
		} catch (Exception e) {
			LOGGER.error("Error while searching keywords " + word,e);
			throw new ServiceException(e);
		}

		
	}
	

	public com.salesmanager.core.model.search.SearchResponse search(MerchantStore store, String languageCode, String term, int entriesCount, int startIndex) throws ServiceException {
		

		try {
			
			StringBuilder collectionName = new StringBuilder();
			collectionName.append(PRODUCT_INDEX_NAME).append(UNDERSCORE).append(languageCode).append(UNDERSCORE).append(store.getCode().toLowerCase());
			
			
			SearchRequest request = new SearchRequest();
			System.out.println("$#2859#"); request.addCollection(collectionName.toString());
			System.out.println("$#2860#"); request.setSize(entriesCount);
			System.out.println("$#2861#"); request.setStart(startIndex);
			System.out.println("$#2862#"); request.setMatch(term);
			
			SearchResponse response = searchService.search(request);
			
			com.salesmanager.core.model.search.SearchResponse resp = new com.salesmanager.core.model.search.SearchResponse();
			System.out.println("$#2863#"); resp.setTotalCount(0);
			
			System.out.println("$#2864#"); if(response != null) {
				System.out.println("$#2865#"); resp.setTotalCount(response.getCount());
				
				List<SearchEntry> entries = new ArrayList<SearchEntry>();
				
				Collection<SearchHit> hits = response.getSearchHits();
				
				System.out.println("$#2866#"); if(!CollectionUtils.isEmpty(hits)) {
					for(SearchHit hit : hits) {
						
						SearchEntry entry = new SearchEntry();
		
						//Map<String,Object> metaEntries = hit.getMetaEntries();
						Map<String,Object> metaEntries = hit.getItem();
						IndexProduct indexProduct = new IndexProduct();

						Object desc = metaEntries.get("description");
						System.out.println("$#2867#"); if(desc instanceof JsonNull == false) {
							System.out.println("$#2868#"); indexProduct.setDescription((String)metaEntries.get("description"));
						}
						
						Object hl = metaEntries.get("highlight");
						System.out.println("$#2869#"); if(hl instanceof JsonNull == false) {
							System.out.println("$#2870#"); indexProduct.setHighlight((String)metaEntries.get("highlight"));
						}
						System.out.println("$#2871#"); indexProduct.setId((String)metaEntries.get("id"));
						System.out.println("$#2872#"); indexProduct.setLang((String)metaEntries.get("lang"));
						
						Object nm = metaEntries.get("name");
						System.out.println("$#2873#"); if(nm instanceof JsonNull == false) {
							System.out.println("$#2874#"); indexProduct.setName(((String)metaEntries.get("name")));
						}
						
						Object mf = metaEntries.get("manufacturer");
						System.out.println("$#2875#"); if(mf instanceof JsonNull == false) {
							System.out.println("$#2876#"); indexProduct.setManufacturer(((String)metaEntries.get("manufacturer")));
						}
						System.out.println("$#2877#"); indexProduct.setPrice(Double.valueOf(((String)metaEntries.get("price"))));
						System.out.println("$#2878#"); indexProduct.setStore(((String)metaEntries.get("store")));
						System.out.println("$#2879#"); entry.setIndexProduct(indexProduct);
						entries.add(entry);
						
						/**
						 * no more support for highlighted
						 */

					}
					
					System.out.println("$#2880#"); resp.setEntries(entries);
					
					//Map<String,List<FacetEntry>> facets = response.getFacets();
					Map<String,Facet> facets = response.getFacets();
					System.out.println("$#2882#"); System.out.println("$#2881#"); if(facets!=null && facets.size() > 0) {
						Map<String,List<SearchFacet>> searchFacets = new HashMap<String,List<SearchFacet>>();
						for(String key : facets.keySet()) {
							
							Facet f = facets.get(key);
							List<com.shopizer.search.services.Entry> ent = f.getEntries();
							
							//List<FacetEntry> f = facets.get(key);
							
							List<SearchFacet> fs = searchFacets.get(key);
							System.out.println("$#2884#"); if(fs==null) {
								fs = new ArrayList<SearchFacet>();
								searchFacets.put(key, fs);
							}
		
							for(com.shopizer.search.services.Entry facetEntry : ent) {
							
								SearchFacet searchFacet = new SearchFacet();
								System.out.println("$#2885#"); searchFacet.setKey(facetEntry.getName());
								System.out.println("$#2886#"); searchFacet.setName(facetEntry.getName());
								System.out.println("$#2887#"); searchFacet.setCount(facetEntry.getCount());
								
								fs.add(searchFacet);
							
							}
							
						}
						
						System.out.println("$#2888#"); resp.setFacets(searchFacets);
					
					}
				
				}
			}
			
			
			
			System.out.println("$#2889#"); return resp;
			
			
		} catch (Exception e) {
			LOGGER.error("Error while searching keywords " + term,e);
			throw new ServiceException(e);
		}
		
	}
	
}

