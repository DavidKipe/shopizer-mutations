package com.salesmanager.shop.store.facade.product;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.salesmanager.shop.utils.LocaleUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.category.CategoryService;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.relationship.ProductRelationshipService;
import com.salesmanager.core.business.services.catalog.product.review.ProductReviewService;
import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.ProductCriteria;
import com.salesmanager.core.model.catalog.product.availability.ProductAvailability;
import com.salesmanager.core.model.catalog.product.manufacturer.Manufacturer;
import com.salesmanager.core.model.catalog.product.price.ProductPrice;
import com.salesmanager.core.model.catalog.product.relationship.ProductRelationship;
import com.salesmanager.core.model.catalog.product.relationship.ProductRelationshipType;
import com.salesmanager.core.model.catalog.product.review.ProductReview;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.constants.Constants;

import com.salesmanager.shop.model.catalog.product.LightPersistableProduct;
import com.salesmanager.shop.model.catalog.product.PersistableProduct;
import com.salesmanager.shop.model.catalog.product.PersistableProductReview;
import com.salesmanager.shop.model.catalog.product.ProductPriceEntity;
import com.salesmanager.shop.model.catalog.product.ProductSpecification;
import com.salesmanager.shop.model.catalog.product.ReadableProduct;
import com.salesmanager.shop.model.catalog.product.ReadableProductList;
import com.salesmanager.shop.model.catalog.product.ReadableProductReview;
import com.salesmanager.shop.populator.catalog.PersistableProductPopulator;
import com.salesmanager.shop.populator.catalog.PersistableProductReviewPopulator;
import com.salesmanager.shop.populator.catalog.ReadableProductPopulator;
import com.salesmanager.shop.populator.catalog.ReadableProductReviewPopulator;
import com.salesmanager.shop.store.api.exception.OperationNotAllowedException;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.controller.product.facade.ProductFacade;
import com.salesmanager.shop.utils.DateUtil;
import com.salesmanager.shop.utils.ImageFilePath;

@Service("productFacade")
@Profile({ "default", "cloud", "gcp", "aws", "mysql" })
public class ProductFacadeImpl implements ProductFacade {

	@Inject
	private CategoryService categoryService;

	@Inject
	private LanguageService languageService;

	@Inject
	private ProductService productService;

	@Inject
	private PricingService pricingService;

	@Inject
	private CustomerService customerService;

	@Inject
	private ProductReviewService productReviewService;

	@Inject
	private ProductRelationshipService productRelationshipService;

	@Inject
	private PersistableProductPopulator persistableProductPopulator;

	@Inject
	@Qualifier("img")
	private ImageFilePath imageUtils;

	@Override
	public PersistableProduct saveProduct(MerchantStore store, PersistableProduct product, Language language) {

		String manufacturer = Manufacturer.DEFAULT_MANUFACTURER;
		System.out.println("$#14794#"); if (product.getProductSpecifications() != null) {
			manufacturer = product.getProductSpecifications().getManufacturer();
		} else {
			ProductSpecification specifications = new ProductSpecification();
			System.out.println("$#14795#"); specifications.setManufacturer(manufacturer);
		}

		Product target = null;
		System.out.println("$#14797#"); System.out.println("$#14796#"); if (product.getId() != null && product.getId().longValue() > 0) {
			target = productService.getById(product.getId());
		} else {
			target = new Product();
		}

		try {
			persistableProductPopulator.populate(product, target, store, language);
			System.out.println("$#14800#"); System.out.println("$#14799#"); if (target.getId() != null && target.getId() > 0) {
				System.out.println("$#14802#"); productService.update(target);
			} else {
				System.out.println("$#14803#"); productService.create(target);
				System.out.println("$#14804#"); product.setId(target.getId());
			}

			System.out.println("$#14805#"); return product;
		} catch (Exception e) {
			throw new ServiceRuntimeException(e);
		}

	}

	public void updateProduct(MerchantStore store, PersistableProduct product, Language language) {

		System.out.println("$#14806#"); Validate.notNull(product, "Product must not be null");
		System.out.println("$#14807#"); Validate.notNull(product.getId(), "Product id must not be null");

		// get original product
		Product productModel = productService.getById(product.getId());

		// merge original product with persistable product

		/*
		 * String manufacturer = Manufacturer.DEFAULT_MANUFACTURER; if
		 * (product.getProductSpecifications() != null) { manufacturer =
		 * product.getProductSpecifications().getManufacturer(); } else {
		 * ProductSpecification specifications = new ProductSpecification();
		 * specifications.setManufacturer(manufacturer); }
		 *
		 * Product target = null; if (product.getId() != null &&
		 * product.getId().longValue() > 0) { target =
		 * productService.getById(product.getId()); } else { target = new
		 * Product(); }
		 *
		 *
		 * try { persistableProductPopulator.populate(product, target, store,
		 * language); productService.create(target);
		 * product.setId(target.getId()); return product; } catch (Exception e)
		 * { throw new ServiceRuntimeException(e); }
		 */

	}

	@Override
	public ReadableProduct getProduct(MerchantStore store, Long id, Language language) throws Exception {

		Product product = productService.findOne(id, store);
		System.out.println("$#14808#"); if (product == null) {
			throw new ResourceNotFoundException("Product [" + id + "] not found");
		}

		System.out.println("$#14809#"); if (product.getMerchantStore().getId() != store.getId()) {
			throw new ResourceNotFoundException("Product [" + id + "] not found for store [" + store.getId() + "]");
		}

		ReadableProduct readableProduct = new ReadableProduct();
		ReadableProductPopulator populator = new ReadableProductPopulator();
		System.out.println("$#14810#"); populator.setPricingService(pricingService);
		System.out.println("$#14811#"); populator.setimageUtils(imageUtils);
		readableProduct = populator.populate(product, readableProduct, store, language);

		System.out.println("$#14812#"); return readableProduct;
	}

	@Override
	public ReadableProduct getProduct(MerchantStore store, String sku, Language language) throws Exception {

		Product product = productService.getByCode(sku, language);

		System.out.println("$#14813#"); if (product == null) {
			return null;
		}

		ReadableProduct readableProduct = new ReadableProduct();

		ReadableProductPopulator populator = new ReadableProductPopulator();

		System.out.println("$#14814#"); populator.setPricingService(pricingService);
		System.out.println("$#14815#"); populator.setimageUtils(imageUtils);
		populator.populate(product, readableProduct, store, language);

		System.out.println("$#14816#"); return readableProduct;
	}

	@Override
	public ReadableProduct updateProductPrice(ReadableProduct product, ProductPriceEntity price, Language language)
			throws Exception {

		Product persistable = productService.getById(product.getId());

		System.out.println("$#14817#"); if (persistable == null) {
			throw new Exception("product is null for id " + product.getId());
		}

		java.util.Set<ProductAvailability> availabilities = persistable.getAvailabilities();
		for (ProductAvailability availability : availabilities) {
			ProductPrice productPrice = availability.defaultPrice();
			System.out.println("$#14818#"); productPrice.setProductPriceAmount(price.getOriginalPrice());
			System.out.println("$#14819#"); if (price.isDiscounted()) {
				System.out.println("$#14820#"); productPrice.setProductPriceSpecialAmount(price.getDiscountedPrice());
				System.out.println("$#14821#"); if (!StringUtils.isBlank(price.getDiscountStartDate())) {
					Date startDate = DateUtil.getDate(price.getDiscountStartDate());
					System.out.println("$#14822#"); productPrice.setProductPriceSpecialStartDate(startDate);
				}
				System.out.println("$#14823#"); if (!StringUtils.isBlank(price.getDiscountEndDate())) {
					Date endDate = DateUtil.getDate(price.getDiscountEndDate());
					System.out.println("$#14824#"); productPrice.setProductPriceSpecialEndDate(endDate);
				}
			}

		}

		System.out.println("$#14825#"); productService.update(persistable);

		ReadableProduct readableProduct = new ReadableProduct();

		ReadableProductPopulator populator = new ReadableProductPopulator();

		System.out.println("$#14826#"); populator.setPricingService(pricingService);
		System.out.println("$#14827#"); populator.setimageUtils(imageUtils);
		populator.populate(persistable, readableProduct, persistable.getMerchantStore(), language);

		System.out.println("$#14828#"); return readableProduct;
	}

	@Override
	public ReadableProduct updateProductQuantity(ReadableProduct product, int quantity, Language language)
			throws Exception {
		Product persistable = productService.getById(product.getId());

		System.out.println("$#14829#"); if (persistable == null) {
			throw new Exception("product is null for id " + product.getId());
		}

		java.util.Set<ProductAvailability> availabilities = persistable.getAvailabilities();
		for (ProductAvailability availability : availabilities) {
			System.out.println("$#14830#"); availability.setProductQuantity(quantity);
		}

		System.out.println("$#14831#"); productService.update(persistable);

		ReadableProduct readableProduct = new ReadableProduct();

		ReadableProductPopulator populator = new ReadableProductPopulator();

		System.out.println("$#14832#"); populator.setPricingService(pricingService);
		System.out.println("$#14833#"); populator.setimageUtils(imageUtils);
		populator.populate(persistable, readableProduct, persistable.getMerchantStore(), language);

		System.out.println("$#14834#"); return readableProduct;
	}

	@Override
	public void deleteProduct(Product product) throws Exception {
		System.out.println("$#14835#"); productService.delete(product);

	}

	@Override
	public ReadableProductList getProductListsByCriterias(MerchantStore store, Language language,
			ProductCriteria criterias) throws Exception {

		System.out.println("$#14836#"); Validate.notNull(criterias, "ProductCriteria must be set for this product");

		/** This is for category **/
		System.out.println("$#14837#"); if (CollectionUtils.isNotEmpty(criterias.getCategoryIds())) {

			System.out.println("$#14838#"); if (criterias.getCategoryIds().size() == 1) {

				com.salesmanager.core.model.catalog.category.Category category = categoryService
						.getById(criterias.getCategoryIds().get(0));

				System.out.println("$#14839#"); if (category != null) {
					String lineage = new StringBuilder().append(category.getLineage()).append(Constants.SLASH)
							.toString();

					List<com.salesmanager.core.model.catalog.category.Category> categories = categoryService
							.getListByLineage(store, lineage);

					List<Long> ids = new ArrayList<Long>();
					System.out.println("$#14841#"); System.out.println("$#14840#"); if (categories != null && categories.size() > 0) {
						for (com.salesmanager.core.model.catalog.category.Category c : categories) {
							ids.add(c.getId());
						}
					}
					ids.add(category.getId());
					System.out.println("$#14843#"); criterias.setCategoryIds(ids);
				}
			}
		}

		com.salesmanager.core.model.catalog.product.ProductList products = productService.listByStore(store, language,
				criterias);
		
		List<Product> prds = products.getProducts().stream().sorted(Comparator.comparing(Product::getSortOrder)).collect(Collectors.toList());
		System.out.println("$#14844#"); products.setProducts(prds);
		
		ReadableProductPopulator populator = new ReadableProductPopulator();
		System.out.println("$#14845#"); populator.setPricingService(pricingService);
		System.out.println("$#14846#"); populator.setimageUtils(imageUtils);

		ReadableProductList productList = new ReadableProductList();
		for (Product product : products.getProducts()) {

			// create new proxy product
			ReadableProduct readProduct = populator.populate(product, new ReadableProduct(), store, language);
			productList.getProducts().add(readProduct);

		}

		// productList.setTotalPages(products.getTotalCount());
		System.out.println("$#14847#"); productList.setRecordsTotal(products.getTotalCount());
		System.out.println("$#14850#"); System.out.println("$#14848#"); productList.setNumber(products.getTotalCount() >= criterias.getMaxCount() ? products.getTotalCount()
				: criterias.getMaxCount());

		System.out.println("$#14851#"); int lastPageNumber = (int) (Math.ceil(products.getTotalCount() / criterias.getPageSize()));
		System.out.println("$#14852#"); productList.setTotalPages(lastPageNumber);

		System.out.println("$#14853#"); return productList;
	}

	@Override
	public ReadableProduct addProductToCategory(Category category, Product product, Language language)
			throws Exception {

		System.out.println("$#14854#"); Validate.notNull(category, "Category cannot be null");
		System.out.println("$#14855#"); Validate.notNull(product, "Product cannot be null");

		// not alloweed if category already attached
		List<Category> assigned = product.getCategories().stream()
				.filter(cat -> cat.getId().longValue() == category.getId().longValue()).collect(Collectors.toList());

		System.out.println("$#14859#"); System.out.println("$#14858#"); if (assigned.size() > 0) {
			throw new OperationNotAllowedException("Category with id [" + category.getId()
					+ "] already attached to product [" + product.getId() + "]");
		}

		product.getCategories().add(category);

		System.out.println("$#14860#"); productService.update(product);

		ReadableProduct readableProduct = new ReadableProduct();

		ReadableProductPopulator populator = new ReadableProductPopulator();

		System.out.println("$#14861#"); populator.setPricingService(pricingService);
		System.out.println("$#14862#"); populator.setimageUtils(imageUtils);
		populator.populate(product, readableProduct, product.getMerchantStore(), language);

		System.out.println("$#14863#"); return readableProduct;

	}

	@Override
	public ReadableProduct removeProductFromCategory(Category category, Product product, Language language)
			throws Exception {

		System.out.println("$#14864#"); Validate.notNull(category, "Category cannot be null");
		System.out.println("$#14865#"); Validate.notNull(product, "Product cannot be null");

		product.getCategories().remove(category);
		System.out.println("$#14866#"); productService.update(product);

		ReadableProduct readableProduct = new ReadableProduct();

		ReadableProductPopulator populator = new ReadableProductPopulator();

		System.out.println("$#14867#"); populator.setPricingService(pricingService);
		System.out.println("$#14868#"); populator.setimageUtils(imageUtils);
		populator.populate(product, readableProduct, product.getMerchantStore(), language);

		System.out.println("$#14869#"); return readableProduct;
	}

	@Override
	public ReadableProduct getProductByCode(MerchantStore store, String uniqueCode, Language language)
			throws Exception {

		Product product = productService.getByCode(uniqueCode, language);

		ReadableProduct readableProduct = new ReadableProduct();

		ReadableProductPopulator populator = new ReadableProductPopulator();

		System.out.println("$#14870#"); populator.setPricingService(pricingService);
		System.out.println("$#14871#"); populator.setimageUtils(imageUtils);
		populator.populate(product, readableProduct, product.getMerchantStore(), language);

		System.out.println("$#14872#"); return readableProduct;
	}

	@Override
	public void saveOrUpdateReview(PersistableProductReview review, MerchantStore store, Language language)
			throws Exception {
		PersistableProductReviewPopulator populator = new PersistableProductReviewPopulator();
		System.out.println("$#14873#"); populator.setLanguageService(languageService);
		System.out.println("$#14874#"); populator.setCustomerService(customerService);
		System.out.println("$#14875#"); populator.setProductService(productService);

		com.salesmanager.core.model.catalog.product.review.ProductReview rev = new com.salesmanager.core.model.catalog.product.review.ProductReview();
		populator.populate(review, rev, store, language);

		System.out.println("$#14876#"); if (review.getId() == null) {
			System.out.println("$#14877#"); productReviewService.create(rev);
		} else {
			System.out.println("$#14878#"); productReviewService.update(rev);
		}

		System.out.println("$#14879#"); review.setId(rev.getId());

	}

	@Override
	public void deleteReview(ProductReview review, MerchantStore store, Language language) throws Exception {
		System.out.println("$#14880#"); productReviewService.delete(review);

	}

	@Override
	public List<ReadableProductReview> getProductReviews(Product product, MerchantStore store, Language language)
			throws Exception {

		List<ProductReview> reviews = productReviewService.getByProduct(product);

		ReadableProductReviewPopulator populator = new ReadableProductReviewPopulator();

		List<ReadableProductReview> productReviews = new ArrayList<ReadableProductReview>();

		for (ProductReview review : reviews) {
			ReadableProductReview readableReview = new ReadableProductReview();
			populator.populate(review, readableReview, store, language);
			productReviews.add(readableReview);
		}

		System.out.println("$#14881#"); return productReviews;
	}

	@Override
	public List<ReadableProduct> relatedItems(MerchantStore store, Product product, Language language)
			throws Exception {
		ReadableProductPopulator populator = new ReadableProductPopulator();
		System.out.println("$#14882#"); populator.setPricingService(pricingService);
		System.out.println("$#14883#"); populator.setimageUtils(imageUtils);

		List<ProductRelationship> relatedItems = productRelationshipService.getByType(store, product,
				ProductRelationshipType.RELATED_ITEM);
		System.out.println("$#14885#"); System.out.println("$#14884#"); if (relatedItems != null && relatedItems.size() > 0) {
			List<ReadableProduct> items = new ArrayList<ReadableProduct>();
			for (ProductRelationship relationship : relatedItems) {
				Product relatedProduct = relationship.getRelatedProduct();
				ReadableProduct proxyProduct = populator.populate(relatedProduct, new ReadableProduct(), store,
						language);
				items.add(proxyProduct);
			}
			System.out.println("$#14887#"); return items;
		}
		System.out.println("$#14888#"); return null;
	}

	@Override
	public void update(Long productId, LightPersistableProduct product, MerchantStore merchant, Language language) {
		// Get product
		Product modified = productService.findOne(productId, merchant);

		// Update product with minimal set
		System.out.println("$#14889#"); modified.setAvailable(product.isAvailable());

		for (ProductAvailability availability : modified.getAvailabilities()) {
			System.out.println("$#14890#"); availability.setProductQuantity(product.getQuantity());
			System.out.println("$#14891#"); if (!StringUtils.isBlank(product.getPrice())) {
				// set default price
				for (ProductPrice price : availability.getPrices()) {
					System.out.println("$#14892#"); if (price.isDefaultPrice()) {
						try {
							System.out.println("$#14893#"); price.setProductPriceAmount(pricingService.getAmount(product.getPrice()));
						} catch (ServiceException e) {
							throw new ServiceRuntimeException("Invalid product price format");
						}
					}
				}
			}
		}

		try {
			System.out.println("$#14894#"); productService.save(modified);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Cannot update product ", e);
		}

	}

	@Override
	public boolean exists(String sku, MerchantStore store) {
		boolean exists = false;
		Product product = productService.getByCode(sku, store.getDefaultLanguage());
		System.out.println("$#14895#"); if (product != null && product.getMerchantStore().getId().intValue() == store.getId().intValue()) {
			exists = true;
		}
		System.out.println("$#14898#"); System.out.println("$#14897#"); return exists;
	}

	@Override
	public Product getProduct(String sku, MerchantStore store) {
		System.out.println("$#14899#"); return productService.getByCode(sku, store.getDefaultLanguage());
	}

	@Override
	public void deleteProduct(Long id, MerchantStore store) {

		System.out.println("$#14900#"); Validate.notNull(id, "Product id cannot be null");
		System.out.println("$#14901#"); Validate.notNull(store, "store cannot be null");

		Product p = productService.getById(id);

		System.out.println("$#14902#"); if (p == null) {
			throw new ResourceNotFoundException("Product with id [" + id + " not found");
		}

		System.out.println("$#14903#"); if (p.getMerchantStore().getId().intValue() != store.getId().intValue()) {
			throw new ResourceNotFoundException(
					"Product with id [" + id + " not found for store [" + store.getCode() + "]");
		}

		try {
			System.out.println("$#14904#"); productService.delete(p);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Error while deleting ptoduct with id [" + id + "]", e);
		}

	}

	@Override
	public ReadableProduct getProductBySeUrl(MerchantStore store, String friendlyUrl, Language language) throws Exception {

		Product product = productService.getBySeUrl(store, friendlyUrl, LocaleUtils.getLocale(language));

		System.out.println("$#14905#"); if (product == null) {
			return null;
		}

		ReadableProduct readableProduct = new ReadableProduct();

		ReadableProductPopulator populator = new ReadableProductPopulator();

		System.out.println("$#14906#"); populator.setPricingService(pricingService);
		System.out.println("$#14907#"); populator.setimageUtils(imageUtils);
		populator.populate(product, readableProduct, store, language);

		System.out.println("$#14908#"); return readableProduct;
	}

}
