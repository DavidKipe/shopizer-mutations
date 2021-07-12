package com.salesmanager.shop.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesmanager.core.business.services.catalog.category.CategoryService;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.content.ContentService;
import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.services.merchant.MerchantStoreService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.system.MerchantConfigurationService;
import com.salesmanager.core.business.utils.CacheUtils;
import com.salesmanager.core.business.utils.CoreConfiguration;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.catalog.category.CategoryDescription;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.common.UserContext;
import com.salesmanager.core.model.content.Content;
import com.salesmanager.core.model.content.ContentDescription;
import com.salesmanager.core.model.content.ContentType;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.system.MerchantConfig;
import com.salesmanager.core.model.system.MerchantConfiguration;
import com.salesmanager.core.model.system.MerchantConfigurationType;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.catalog.category.ReadableCategory;
import com.salesmanager.shop.model.catalog.category.ReadableCategoryList;
import com.salesmanager.shop.model.customer.AnonymousCustomer;
import com.salesmanager.shop.model.customer.address.Address;
import com.salesmanager.shop.model.shop.Breadcrumb;
import com.salesmanager.shop.model.shop.BreadcrumbItem;
import com.salesmanager.shop.model.shop.BreadcrumbItemType;
import com.salesmanager.shop.model.shop.PageInformation;
import com.salesmanager.shop.populator.catalog.ReadableCategoryPopulator;
import com.salesmanager.shop.store.controller.category.facade.CategoryFacade;
import com.salesmanager.shop.utils.GeoLocationUtils;
import com.salesmanager.shop.utils.LabelUtils;
import com.salesmanager.shop.utils.LanguageUtils;
import com.salesmanager.shop.utils.WebApplicationCacheUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Servlet Filter implementation class StoreFilter
 */

public class StoreFilter extends HandlerInterceptorAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(StoreFilter.class);

	private final static String STORE_REQUEST_PARAMETER = "store";

	@Inject
	private ContentService contentService;

	@Inject
	private CategoryService categoryService;

	@Inject
	private ProductService productService;

	@Inject
	private MerchantStoreService merchantService;

	@Inject
	private CustomerService customerService;

	@Inject
	private MerchantConfigurationService merchantConfigurationService;

	@Inject
	private LanguageService languageService;

	@Inject
	private LabelUtils messages;

	@Inject
	private LanguageUtils languageUtils;

	@Inject
	private CacheUtils cache;

	@Inject
	private WebApplicationCacheUtils webApplicationCache;

	@Inject
	private CategoryFacade categoryFacade;

	@Inject
	private CoreConfiguration coreConfiguration;

	private final static String SERVICES_URL_PATTERN = "/services";
	private final static String REFERENCE_URL_PATTERN = "/reference";

	/**
	 * Default constructor.
	 */
	public StoreFilter() {

	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		System.out.println("$#8049#"); request.setCharacterEncoding("UTF-8");

		/**
		 * if url contains /services exit from here !
		 */
		System.out.println("$#8050#"); if (request.getRequestURL().toString().toLowerCase().contains(SERVICES_URL_PATTERN)
				|| request.getRequestURL().toString().toLowerCase().contains(REFERENCE_URL_PATTERN)) {
			System.out.println("$#8052#"); return true;
		}

		/*****
		 * where is my stuff
		 */
		// String currentPath = System.getProperty("user.dir");
		// System.out.println("*** user.dir ***" + currentPath);
		// LOGGER.debug("*** user.dir ***" + currentPath);

		try

		{

			/** merchant store **/
			MerchantStore store = (MerchantStore) request.getSession().getAttribute(Constants.MERCHANT_STORE);

			String storeCode = request.getParameter(STORE_REQUEST_PARAMETER);

			// remove link set from controllers for declaring active - inactive
			// links
			System.out.println("$#8053#"); request.removeAttribute(Constants.LINK_CODE);

			System.out.println("$#8054#"); if (!StringUtils.isBlank(storeCode)) {
				System.out.println("$#8055#"); if (store != null) {
					System.out.println("$#8056#"); if (!store.getCode().equals(storeCode)) {
						store = setMerchantStoreInSession(request, storeCode);
					}
				} else { // when url sm-shop/shop is being loaded for first time
							// store is null
					store = setMerchantStoreInSession(request, storeCode);
				}
			}

			System.out.println("$#8057#"); if (store == null) {
				store = setMerchantStoreInSession(request, MerchantStore.DEFAULT_STORE);
			}
			
			System.out.println("$#8058#"); if(StringUtils.isBlank(store.getStoreTemplate())) {
				System.out.println("$#8059#"); store.setStoreTemplate(Constants.DEFAULT_TEMPLATE);
			}
			System.out.println("$#8060#"); request.setAttribute(Constants.MERCHANT_STORE, store);
			
			
			/*
			//remote ip address
			String remoteAddress = "";
			try {
				
				if (request != null) {
					remoteAddress = request.getHeader("X-Forwarded-For");
					if (remoteAddress == null || "".equals(remoteAddress)) {
						remoteAddress = request.getRemoteAddr();
					}
				}
				remoteAddress = remoteAddress != null && remoteAddress.contains(",") ? remoteAddress.split(",")[0] : remoteAddress;
				LOGGER.info("remote ip addres {}", remoteAddress);
			} catch (Exception e) {
				LOGGER.error("Error while getting user remote address");
			}
			*/
			
			String ipAddress = GeoLocationUtils.getClientIpAddress(request);
			
			UserContext userContext = UserContext.create();
			System.out.println("$#8061#"); userContext.setIpAddress(ipAddress);

			/** customer **/
			Customer customer = (Customer) request.getSession().getAttribute(Constants.CUSTOMER);
			System.out.println("$#8062#"); if (customer != null) {
				System.out.println("$#8063#"); if (customer.getMerchantStore().getId().intValue() != store.getId().intValue()) {
					System.out.println("$#8064#"); request.getSession().removeAttribute(Constants.CUSTOMER);
				}
				System.out.println("$#8065#"); if (!customer.isAnonymous()) {
					System.out.println("$#8066#"); if (!request.isUserInRole("AUTH_CUSTOMER")) {
						System.out.println("$#8067#"); request.removeAttribute(Constants.CUSTOMER);
					}
				}
				
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				
				System.out.println("$#8068#"); request.setAttribute(Constants.CUSTOMER, customer);
			}

			System.out.println("$#8069#"); if (customer == null) {

				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				System.out.println("$#8070#"); if (auth != null && request.isUserInRole("AUTH_CUSTOMER")) {
					customer = customerService.getByNick(auth.getName());
					System.out.println("$#8072#"); if (customer != null) {
						System.out.println("$#8073#"); request.setAttribute(Constants.CUSTOMER, customer);
					}
				}
			}

			AnonymousCustomer anonymousCustomer = (AnonymousCustomer) request.getSession()
					.getAttribute(Constants.ANONYMOUS_CUSTOMER);
			System.out.println("$#8074#"); if (anonymousCustomer == null) {

				Address address = null;
				try {

					System.out.println("$#8075#"); if(!StringUtils.isBlank(ipAddress)) {
						com.salesmanager.core.model.common.Address geoAddress = customerService.getCustomerAddress(store,
								ipAddress);
						System.out.println("$#8076#"); if (geoAddress != null) {
							address = new Address();
							System.out.println("$#8077#"); address.setCountry(geoAddress.getCountry());
							System.out.println("$#8078#"); address.setCity(geoAddress.getCity());
							System.out.println("$#8079#"); address.setZone(geoAddress.getZone());
							/** no postal code **/
							// address.setPostalCode(geoAddress.getPostalCode());
						}
					}
				} catch (Exception ce) {
					LOGGER.error("Cannot get geo ip component ", ce);
				}

				System.out.println("$#8080#"); if (address == null) {
					address = new Address();
					System.out.println("$#8081#"); address.setCountry(store.getCountry().getIsoCode());
					System.out.println("$#8082#"); if (store.getZone() != null) {
						System.out.println("$#8083#"); address.setZone(store.getZone().getCode());
					} else {
						System.out.println("$#8084#"); address.setStateProvince(store.getStorestateprovince());
					}
					/** no postal code **/
					// address.setPostalCode(store.getStorepostalcode());
				}

				anonymousCustomer = new AnonymousCustomer();
				System.out.println("$#8085#"); anonymousCustomer.setBilling(address);
				System.out.println("$#8086#"); request.getSession().setAttribute(Constants.ANONYMOUS_CUSTOMER, anonymousCustomer);
			} else {
				System.out.println("$#8087#"); request.setAttribute(Constants.ANONYMOUS_CUSTOMER, anonymousCustomer);
			}

			/** language & locale **/
			Language language = languageUtils.getRequestLanguage(request, response);
			System.out.println("$#8088#"); request.setAttribute(Constants.LANGUAGE, language);

			Locale locale = languageService.toLocale(language, store);
			System.out.println("$#8089#"); request.setAttribute(Constants.LOCALE, locale);

			// Locale locale = LocaleContextHolder.getLocale();
			System.out.println("$#8090#"); LocaleContextHolder.setLocale(locale);

			/** Breadcrumbs **/
			System.out.println("$#8091#"); setBreadcrumb(request, locale);

			/**
			 * Get global objects Themes are built on a similar way displaying
			 * Header, Body and Footer Header and Footer are displayed on each
			 * page Some themes also contain side bars which may include similar
			 * emements
			 * 
			 * Elements from Header : - CMS links - Customer - Mini shopping
			 * cart - Store name / logo - Top categories - Search
			 * 
			 * Elements from Footer : - CMS links - Store address - Global
			 * payment information - Global shipping information
			 */

			// get from the cache first
			/**
			 * The cache for each object contains 2 objects, a Cache and a
			 * Missed-Cache Get objects from the cache If not null use those
			 * objects If null, get entry from missed-cache If missed-cache not
			 * null then nothing exist If missed-cache null, add missed-cache
			 * entry and load from the database If objects from database not
			 * null store in cache
			 */

			/******* CMS Objects ********/
			System.out.println("$#8092#"); this.getContentObjects(store, language, request);

			/******* CMS Page names **********/
			System.out.println("$#8093#"); this.getContentPageNames(store, language, request);

			/******* Top Categories ********/
			// this.getTopCategories(store, language, request);
			System.out.println("$#8094#"); this.setTopCategories(store, language, request);

			/******* Default metatags *******/

			/**
			 * Title Description Keywords
			 */

			PageInformation pageInformation = new PageInformation();
			System.out.println("$#8095#"); pageInformation.setPageTitle(store.getStorename());
			System.out.println("$#8096#"); pageInformation.setPageDescription(store.getStorename());
			System.out.println("$#8097#"); pageInformation.setPageKeywords(store.getStorename());

			@SuppressWarnings("unchecked")
			Map<String, ContentDescription> contents = (Map<String, ContentDescription>) request
					.getAttribute(Constants.REQUEST_CONTENT_OBJECTS);

			System.out.println("$#8098#"); if (contents != null) {
				// for(String key : contents.keySet()) {
				// List<ContentDescription> contentsList = contents.get(key);
				// for(Content content : contentsList) {
				// if(key.equals(Constants.CONTENT_LANDING_PAGE)) {

				// List<ContentDescription> descriptions =
				// content.getDescriptions();
				ContentDescription contentDescription = contents.get(Constants.CONTENT_LANDING_PAGE);
				System.out.println("$#8099#"); if (contentDescription != null) {
					// for(ContentDescription contentDescription : descriptions)
					// {
					// if(contentDescription.getLanguage().getCode().equals(language.getCode()))
					// {
					System.out.println("$#8100#"); pageInformation.setPageTitle(contentDescription.getName());
					System.out.println("$#8101#"); pageInformation.setPageDescription(contentDescription.getMetatagDescription());
					System.out.println("$#8102#"); pageInformation.setPageKeywords(contentDescription.getMetatagKeywords());
					// }
				}
				// }
				// }
				// }
			}

			System.out.println("$#8103#"); request.setAttribute(Constants.REQUEST_PAGE_INFORMATION, pageInformation);

			/******* Configuration objects *******/

			/**
			 * SHOP configuration type Should contain - Different configuration
			 * flags - Google analytics - Facebook page - Twitter handle - Show
			 * customer login - ...
			 */

			System.out.println("$#8104#"); this.getMerchantConfigurations(store, request);

			/******* Shopping Cart *********/

			String shoppingCarCode = (String) request.getSession().getAttribute(Constants.SHOPPING_CART);
			System.out.println("$#8105#"); if (shoppingCarCode != null) {
				System.out.println("$#8106#"); request.setAttribute(Constants.REQUEST_SHOPPING_CART, shoppingCarCode);
			}

		} catch (Exception e) {
			LOGGER.error("Error in StoreFilter", e);
		}

		System.out.println("$#8107#"); return true;

	}

	@SuppressWarnings("unchecked")
	private void getMerchantConfigurations(MerchantStore store, HttpServletRequest request) throws Exception {

		StringBuilder configKey = new StringBuilder();
		configKey.append(store.getId()).append("_").append(Constants.CONFIG_CACHE_KEY);

		StringBuilder configKeyMissed = new StringBuilder();
		configKeyMissed.append(configKey.toString()).append(Constants.MISSED_CACHE_KEY);

		Map<String, Object> configs = null;

		System.out.println("$#8108#"); if (store.isUseCache()) {

			// get from the cache
			configs = (Map<String, Object>) cache.getFromCache(configKey.toString());
			System.out.println("$#8109#"); if (configs == null) {
				// get from missed cache
				// Boolean missedContent =
				// (Boolean)cache.getFromCache(configKeyMissed.toString());

				// if( missedContent==null) {
				configs = this.getConfigurations(store);
				// put in cache

				System.out.println("$#8110#"); if (configs != null) {
					System.out.println("$#8111#"); cache.putInCache(configs, configKey.toString());
				} else {
					// put in missed cache
					// cache.putInCache(new Boolean(true),
					// configKeyMissed.toString());
				}
				// }
			}

		} else {
			configs = this.getConfigurations(store);
		}

		System.out.println("$#8113#"); System.out.println("$#8112#"); if (configs != null && configs.size() > 0) {
			System.out.println("$#8115#"); request.setAttribute(Constants.REQUEST_CONFIGS, configs);
		}

	}

	@SuppressWarnings("unchecked")
	private void getContentPageNames(MerchantStore store, Language language, HttpServletRequest request)
			throws Exception {

		/**
		 * CMS links Those links are implemented as pages (Content)
		 * ContentDescription will provide attributes name for the label to be
		 * displayed and seUrl for the friendly url page
		 */

		// build the key
		/**
		 * The cache is kept as a Map<String,Object> The key is
		 * <MERCHANT_ID>_CONTENTPAGELOCALE The value is a List of Content object
		 */

		StringBuilder contentKey = new StringBuilder();
		contentKey.append(store.getId()).append("_").append(Constants.CONTENT_PAGE_CACHE_KEY).append("-")
				.append(language.getCode());

		StringBuilder contentKeyMissed = new StringBuilder();
		contentKeyMissed.append(contentKey.toString()).append(Constants.MISSED_CACHE_KEY);

		Map<String, List<ContentDescription>> contents = null;

		System.out.println("$#8116#"); if (store.isUseCache()) {

			// get from the cache
			contents = (Map<String, List<ContentDescription>>) cache.getFromCache(contentKey.toString());

			System.out.println("$#8117#"); if (contents == null) {
				// get from missed cache
				// Boolean missedContent =
				// (Boolean)cache.getFromCache(contentKeyMissed.toString());

				// if(missedContent==null) {

				contents = this.getContentPagesNames(store, language);

				System.out.println("$#8118#"); if (contents != null) {
					// put in cache
					System.out.println("$#8119#"); cache.putInCache(contents, contentKey.toString());

				} else {
					// put in missed cache
					// cache.putInCache(new Boolean(true),
					// contentKeyMissed.toString());
				}
				// }
			}
		} else {
			contents = this.getContentPagesNames(store, language);
		}

		System.out.println("$#8121#"); System.out.println("$#8120#"); if (contents != null && contents.size() > 0) {
			List<ContentDescription> descriptions = contents.get(contentKey.toString());

			System.out.println("$#8123#"); if (descriptions != null) {
				System.out.println("$#8124#"); request.setAttribute(Constants.REQUEST_CONTENT_PAGE_OBJECTS, descriptions);
			}
		}
	}

	@SuppressWarnings({ "unchecked" })
	private void getContentObjects(MerchantStore store, Language language, HttpServletRequest request)
			throws Exception {

		/**
		 * CMS links Those links are implemented as pages (Content)
		 * ContentDescription will provide attributes name for the label to be
		 * displayed and seUrl for the friendly url page
		 */

		// build the key
		/**
		 * The cache is kept as a Map<String,Object> The key is
		 * CONTENT_<MERCHANT_ID>_<LOCALE> The value is a List of Content object
		 */

		StringBuilder contentKey = new StringBuilder();
		contentKey.append(store.getId()).append("_").append(Constants.CONTENT_CACHE_KEY).append("-")
				.append(language.getCode());

		StringBuilder contentKeyMissed = new StringBuilder();
		contentKeyMissed.append(contentKey.toString()).append(Constants.MISSED_CACHE_KEY);

		Map<String, List<Content>> contents = null;

		System.out.println("$#8125#"); if (store.isUseCache()) {

			// get from the cache
			contents = (Map<String, List<Content>>) cache.getFromCache(contentKey.toString());

			System.out.println("$#8126#"); if (contents == null) {

				// get from missed cache
				// Boolean missedContent =
				// (Boolean)cache.getFromCache(contentKeyMissed.toString());

				// if(missedContent==null) {

				contents = this.getContent(store, language);
				System.out.println("$#8128#"); System.out.println("$#8127#"); if (contents != null && contents.size() > 0) {
					// put in cache
					System.out.println("$#8130#"); cache.putInCache(contents, contentKey.toString());
				} else {
					// put in missed cache
					// cache.putInCache(new Boolean(true),
					// contentKeyMissed.toString());
				}
				// }

			}
		} else {

			contents = this.getContent(store, language);

		}

		System.out.println("$#8132#"); System.out.println("$#8131#"); if (contents != null && contents.size() > 0) {

			// request.setAttribute(Constants.REQUEST_CONTENT_OBJECTS,
			// contents);

			List<Content> contentByStore = contents.get(contentKey.toString());
			System.out.println("$#8134#"); if (!CollectionUtils.isEmpty(contentByStore)) {
				Map<String, ContentDescription> contentMap = new HashMap<String, ContentDescription>();
				for (Content content : contentByStore) {
					System.out.println("$#8135#"); if (content.isVisible()) {
						contentMap.put(content.getCode(), content.getDescription());
					}
				}
				System.out.println("$#8136#"); request.setAttribute(Constants.REQUEST_CONTENT_OBJECTS, contentMap);
			}

		}

	}

	@SuppressWarnings("unchecked")
	private void setTopCategories(MerchantStore store, Language language, HttpServletRequest request) throws Exception {

		StringBuilder categoriesKey = new StringBuilder();
		categoriesKey.append(store.getId()).append("_").append(Constants.CATEGORIES_CACHE_KEY).append("-")
				.append(language.getCode());

		StringBuilder categoriesKeyMissed = new StringBuilder();
		categoriesKeyMissed.append(categoriesKey.toString()).append(Constants.MISSED_CACHE_KEY);

		// language code - List of category
		Map<String, List<ReadableCategory>> objects = null;
		List<ReadableCategory> loadedCategories = null;

		System.out.println("$#8137#"); if (store.isUseCache()) {
			objects = (Map<String, List<ReadableCategory>>) webApplicationCache.getFromCache(categoriesKey.toString());

			System.out.println("$#8138#"); if (objects == null) {
				// load categories
				ReadableCategoryList categoryList = categoryFacade.getCategoryHierarchy(store, null, 0, language, null,
						0, 200);// null
				loadedCategories = categoryList.getCategories();

				// filter out invisible category
				loadedCategories.stream().filter(cat -> cat.isVisible() == true).collect(Collectors.toList());

				objects = new ConcurrentHashMap<String, List<ReadableCategory>>();
				objects.put(language.getCode(), loadedCategories);
				System.out.println("$#8141#"); webApplicationCache.putInCache(categoriesKey.toString(), objects);

			} else {
				loadedCategories = objects.get(language.getCode());
			}

		} else {

			ReadableCategoryList categoryList = categoryFacade.getCategoryHierarchy(store, null, 0, language, null, 0,
					200);// null // filter
			loadedCategories = categoryList.getCategories();
		}

		System.out.println("$#8142#"); if (loadedCategories != null) {
			System.out.println("$#8143#"); request.setAttribute(Constants.REQUEST_TOP_CATEGORIES, loadedCategories);
		}

	}

	private Map<String, List<ContentDescription>> getContentPagesNames(MerchantStore store, Language language)
			throws Exception {

		Map<String, List<ContentDescription>> contents = new ConcurrentHashMap<String, List<ContentDescription>>();

		// Get boxes and sections from the database
		List<ContentType> contentTypes = new ArrayList<ContentType>();
		contentTypes.add(ContentType.PAGE);

		List<ContentDescription> contentPages = contentService.listNameByType(contentTypes, store, language);

		System.out.println("$#8145#"); System.out.println("$#8144#"); if (contentPages != null && contentPages.size() > 0) {

			// create a Map<String,List<Content>
			for (ContentDescription content : contentPages) {

				Language lang = language;
				String key = new StringBuilder().append(store.getId()).append("_")
						.append(Constants.CONTENT_PAGE_CACHE_KEY).append("-").append(lang.getCode()).toString();
				List<ContentDescription> contentList = null;
				System.out.println("$#8147#"); if (contents == null || contents.size() == 0) {
					contents = new HashMap<String, List<ContentDescription>>();
				}
				System.out.println("$#8149#"); if (!contents.containsKey(key)) {
					contentList = new ArrayList<ContentDescription>();

					contents.put(key, contentList);
				} else {// get from key
					contentList = contents.get(key);
					System.out.println("$#8150#"); if (contentList == null) {
						LOGGER.error("Cannot find content key in cache " + key);
						continue;
					}
				}
				contentList.add(content);
			}
		}
		System.out.println("$#8151#"); return contents;
	}

	private Map<String, List<Content>> getContent(MerchantStore store, Language language) throws Exception {

		Map<String, List<Content>> contents = new ConcurrentHashMap<String, List<Content>>();

		// Get boxes and sections from the database
		List<ContentType> contentTypes = new ArrayList<ContentType>();
		contentTypes.add(ContentType.BOX);
		contentTypes.add(ContentType.SECTION);

		List<Content> contentPages = contentService.listByType(contentTypes, store, language);

		System.out.println("$#8153#"); System.out.println("$#8152#"); if (contentPages != null && contentPages.size() > 0) {

			// create a Map<String,List<Content>
			for (Content content : contentPages) {
				System.out.println("$#8155#"); if (content.isVisible()) {
					List<ContentDescription> descriptions = content.getDescriptions();
					for (ContentDescription contentDescription : descriptions) {
						Language lang = contentDescription.getLanguage();
						String key = new StringBuilder().append(store.getId()).append("_")
								.append(Constants.CONTENT_CACHE_KEY).append("-").append(lang.getCode()).toString();
						List<Content> contentList = null;
						System.out.println("$#8156#"); if (contents == null || contents.size() == 0) {
							contents = new HashMap<String, List<Content>>();
						}
						System.out.println("$#8158#"); if (!contents.containsKey(key)) {
							contentList = new ArrayList<Content>();

							contents.put(key, contentList);
						} else {// get from key
							contentList = contents.get(key);
							System.out.println("$#8159#"); if (contentList == null) {
								LOGGER.error("Cannot find content key in cache " + key);
								continue;
							}
						}
						contentList.add(content);
					}
				}
			}
		}
		System.out.println("$#8160#"); return contents;
	}

	/**
	 * 
	 * @param store
	 * @param language
	 * @return
	 * @throws Exception
	 */
	// private Map<String, List<Category>> getCategories(MerchantStore store,
	// Language language)
	// throws Exception {
	private Map<String, List<ReadableCategory>> getCategories(MerchantStore store, Language language) throws Exception {

		// Map<String, List<Category>> objects = new ConcurrentHashMap<String,
		// List<Category>>();
		Map<String, List<ReadableCategory>> objects = new ConcurrentHashMap<String, List<ReadableCategory>>();

		/**
		 * returns categories with required depth, 0 = root category, 1 = root +
		 * 1 layer child ...)
		 **/
		List<Category> categories = categoryService.getListByDepth(store, 0, language);

		ReadableCategoryPopulator readableCategoryPopulator = new ReadableCategoryPopulator();

		Map<String, ReadableCategory> subs = new ConcurrentHashMap<String, ReadableCategory>();

		System.out.println("$#8162#"); System.out.println("$#8161#"); if (categories != null && categories.size() > 0) {

			// create a Map<String,List<Content>
			for (Category category : categories) {
				System.out.println("$#8164#"); if (category.isVisible()) {
					// if(category.getDepth().intValue()==0) {
					// ReadableCategory readableCategory = new
					// ReadableCategory();
					// readableCategoryPopulator.populate(category,
					// readableCategory, store, language);

					Set<CategoryDescription> descriptions = category.getDescriptions();
					for (CategoryDescription description : descriptions) {

						Language lang = description.getLanguage();

						ReadableCategory readableCategory = new ReadableCategory();
						readableCategoryPopulator.populate(category, readableCategory, store, language);

						String key = new StringBuilder().append(store.getId()).append("_")
								.append(Constants.CATEGORIES_CACHE_KEY).append("-").append(lang.getCode()).toString();

						System.out.println("$#8165#"); if (category.getDepth().intValue() == 0) {

							// List<Category> cacheCategories = null;
							List<ReadableCategory> cacheCategories = null;
							System.out.println("$#8166#"); if (objects == null || objects.size() == 0) {
								// objects = new HashMap<String,
								// List<Category>>();
								objects = new HashMap<String, List<ReadableCategory>>();
							}
							System.out.println("$#8168#"); if (!objects.containsKey(key)) {
								// cacheCategories = new ArrayList<Category>();
								cacheCategories = new ArrayList<ReadableCategory>();

								objects.put(key, cacheCategories);
							} else {
								cacheCategories = objects.get(key.toString());
								System.out.println("$#8169#"); if (cacheCategories == null) {
									LOGGER.error("Cannot find categories key in cache " + key);
									continue;
								}
							}
							// cacheCategories.add(category);
							cacheCategories.add(readableCategory);

						} else {
							subs.put(lang.getCode(), readableCategory);
						}
					}
				}
			}

		}
		System.out.println("$#8170#"); return objects;
	}

	@SuppressWarnings("unused")
	private Map<String, Object> getConfigurations(MerchantStore store) {

		Map<String, Object> configs = new HashMap<String, Object>();
		try {

			List<MerchantConfiguration> merchantConfiguration = merchantConfigurationService
					.listByType(MerchantConfigurationType.CONFIG, store);

			// get social
			List<MerchantConfiguration> socialConfigs = merchantConfigurationService
					.listByType(MerchantConfigurationType.SOCIAL, store);

			System.out.println("$#8171#"); if (!CollectionUtils.isEmpty(socialConfigs)) {
				System.out.println("$#8172#"); if (CollectionUtils.isEmpty(merchantConfiguration)) {
					merchantConfiguration = new ArrayList<MerchantConfiguration>();
				}
				merchantConfiguration.addAll(socialConfigs);
			}

			System.out.println("$#8173#"); if (CollectionUtils.isEmpty(merchantConfiguration)) {
				System.out.println("$#8174#"); return configs;
			}

			for (MerchantConfiguration configuration : merchantConfiguration) {
				configs.put(configuration.getKey(), configuration.getValue());
			}

			configs.put(Constants.SHOP_SCHEME, coreConfiguration.getProperty(Constants.SHOP_SCHEME));
			configs.put(Constants.FACEBOOK_APP_ID, coreConfiguration.getProperty(Constants.FACEBOOK_APP_ID));

			// get MerchantConfig
			MerchantConfig merchantConfig = merchantConfigurationService.getMerchantConfig(store);
			System.out.println("$#8175#"); if (merchantConfig != null) {
				System.out.println("$#8176#"); if (configs == null) {
					configs = new HashMap<String, Object>();
				}

				ObjectMapper m = new ObjectMapper();
				@SuppressWarnings("unchecked")
				Map<String, Object> props = m.convertValue(merchantConfig, Map.class);

				for (String key : props.keySet()) {
					configs.put(key, props.get(key));
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception while getting configurations", e);
		}

		System.out.println("$#8177#"); return configs;

	}

	private void setBreadcrumb(HttpServletRequest request, Locale locale) {

		try {

			// breadcrumb
			Breadcrumb breadCrumb = (Breadcrumb) request.getSession().getAttribute(Constants.BREADCRUMB);
			Language language = (Language) request.getAttribute(Constants.LANGUAGE);
			System.out.println("$#8178#"); if (breadCrumb == null) {
				breadCrumb = new Breadcrumb();
				System.out.println("$#8179#"); breadCrumb.setLanguage(language);
				BreadcrumbItem item = this.getDefaultBreadcrumbItem(language, locale);
				breadCrumb.getBreadCrumbs().add(item);
			} else {

				// check language
				System.out.println("$#8180#"); if (language.getCode().equals(breadCrumb.getLanguage().getCode())) {

					// rebuild using the appropriate language
					List<BreadcrumbItem> items = new ArrayList<BreadcrumbItem>();
					for (BreadcrumbItem item : breadCrumb.getBreadCrumbs()) {

						System.out.println("$#8181#"); if (item.getItemType().name().equals(BreadcrumbItemType.HOME)) {
							BreadcrumbItem homeItem = this.getDefaultBreadcrumbItem(language, locale);
							System.out.println("$#8182#"); homeItem.setItemType(BreadcrumbItemType.HOME);
							System.out.println("$#8183#"); homeItem.setLabel(messages.getMessage(Constants.HOME_MENU_KEY, locale));
							System.out.println("$#8184#"); homeItem.setUrl(Constants.HOME_URL);
							items.add(homeItem);
						} else if (item.getItemType().name().equals(BreadcrumbItemType.PRODUCT)) { System.out.println("$#8185#");
							Product product = productService.getProductForLocale(item.getId(), language, locale);
							System.out.println("$#8186#"); if (product != null) {
								BreadcrumbItem productItem = new BreadcrumbItem();
								System.out.println("$#8187#"); productItem.setId(product.getId());
								System.out.println("$#8188#"); productItem.setItemType(BreadcrumbItemType.PRODUCT);
								System.out.println("$#8189#"); productItem.setLabel(product.getProductDescription().getName());
								System.out.println("$#8190#"); productItem.setUrl(product.getProductDescription().getSeUrl());
								items.add(productItem);
							}
						} else if (item.getItemType().name().equals(BreadcrumbItemType.CATEGORY)) { System.out.println("$#8191#");
							Category category = categoryService.getOneByLanguage(item.getId(), language);
							System.out.println("$#8192#"); if (category != null) {
								BreadcrumbItem categoryItem = new BreadcrumbItem();
								System.out.println("$#8193#"); categoryItem.setId(category.getId());
								System.out.println("$#8194#"); categoryItem.setItemType(BreadcrumbItemType.CATEGORY);
								System.out.println("$#8195#"); categoryItem.setLabel(category.getDescription().getName());
								System.out.println("$#8196#"); categoryItem.setUrl(category.getDescription().getSeUrl());
								items.add(categoryItem);
							}
						} else if (item.getItemType().name().equals(BreadcrumbItemType.PAGE)) { System.out.println("$#8197#");
							Content content = contentService.getByLanguage(item.getId(), language);
							System.out.println("$#8198#"); if (content != null) {
								BreadcrumbItem contentItem = new BreadcrumbItem();
								System.out.println("$#8199#"); contentItem.setId(content.getId());
								System.out.println("$#8200#"); contentItem.setItemType(BreadcrumbItemType.PAGE);
								System.out.println("$#8201#"); contentItem.setLabel(content.getDescription().getName());
								System.out.println("$#8202#"); contentItem.setUrl(content.getDescription().getSeUrl());
								items.add(contentItem);
							}
						} else {
							System.out.println("$#8185#"); // manual correction for else-if mutation coverage
							System.out.println("$#8191#"); // manual correction for else-if mutation coverage
							System.out.println("$#8197#"); // manual correction for else-if mutation coverage
						}

					}

					breadCrumb = new Breadcrumb();
					System.out.println("$#8203#"); breadCrumb.setLanguage(language);
					System.out.println("$#8204#"); breadCrumb.setBreadCrumbs(items);

				}

			}

			System.out.println("$#8205#"); request.getSession().setAttribute(Constants.BREADCRUMB, breadCrumb);
			System.out.println("$#8206#"); request.setAttribute(Constants.BREADCRUMB, breadCrumb);

		} catch (Exception e) {
			LOGGER.error("Error while building breadcrumbs", e);
		}

	}

	private BreadcrumbItem getDefaultBreadcrumbItem(Language language, Locale locale) {

		// set home page item
		BreadcrumbItem item = new BreadcrumbItem();
		System.out.println("$#8207#"); item.setItemType(BreadcrumbItemType.HOME);
		System.out.println("$#8208#"); item.setLabel(messages.getMessage(Constants.HOME_MENU_KEY, locale));
		System.out.println("$#8209#"); item.setUrl(Constants.HOME_URL);
		System.out.println("$#8210#"); return item;

	}

	/**
	 * Sets a MerchantStore with the given storeCode in the session.
	 * 
	 * @param request
	 * @param storeCode
	 *            The storeCode of the Merchant.
	 * @return the MerchantStore inserted in the session.
	 * @throws Exception
	 */
	private MerchantStore setMerchantStoreInSession(HttpServletRequest request, String storeCode) throws Exception {
		System.out.println("$#8211#"); if (storeCode == null || request == null)
			return null;
		MerchantStore store = merchantService.getByCode(storeCode);
		System.out.println("$#8213#"); if (store != null) {
			System.out.println("$#8214#"); request.getSession().setAttribute(Constants.MERCHANT_STORE, store);
		}
		System.out.println("$#8215#"); return store;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable ModelAndView modelAndView) throws Exception {
		
		System.out.println("$#8216#"); if (request.getRequestURL().toString().toLowerCase().contains(SERVICES_URL_PATTERN)
				|| request.getRequestURL().toString().toLowerCase().contains(REFERENCE_URL_PATTERN)) {
			return;
		}
		
		UserContext userContext = UserContext.getCurrentInstance();
		System.out.println("$#8218#"); if(userContext!=null) {
			System.out.println("$#8219#"); userContext.close();
		}

		
	}

}
