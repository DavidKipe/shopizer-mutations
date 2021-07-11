package com.salesmanager.core.business.services.catalog.product;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.catalog.product.ProductRepository;
import com.salesmanager.core.business.services.catalog.category.CategoryService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductAttributeService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductOptionService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductOptionValueService;
import com.salesmanager.core.business.services.catalog.product.availability.ProductAvailabilityService;
import com.salesmanager.core.business.services.catalog.product.image.ProductImageService;
import com.salesmanager.core.business.services.catalog.product.price.ProductPriceService;
import com.salesmanager.core.business.services.catalog.product.relationship.ProductRelationshipService;
import com.salesmanager.core.business.services.catalog.product.review.ProductReviewService;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.business.services.search.SearchService;
import com.salesmanager.core.business.utils.CatalogServiceHelper;
import com.salesmanager.core.business.utils.CoreConfiguration;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.ProductCriteria;
import com.salesmanager.core.model.catalog.product.ProductList;
import com.salesmanager.core.model.catalog.product.description.ProductDescription;
import com.salesmanager.core.model.catalog.product.image.ProductImage;
import com.salesmanager.core.model.catalog.product.relationship.ProductRelationship;
import com.salesmanager.core.model.catalog.product.review.ProductReview;
import com.salesmanager.core.model.content.FileContentType;
import com.salesmanager.core.model.content.ImageContentFile;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.tax.taxclass.TaxClass;

@Service("productService")
public class ProductServiceImpl extends SalesManagerEntityServiceImpl<Long, Product> implements ProductService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

	ProductRepository productRepository;

	@Inject
	CategoryService categoryService;

	@Inject
	ProductAvailabilityService productAvailabilityService;

	@Inject
	ProductPriceService productPriceService;

	@Inject
	ProductOptionService productOptionService;

	@Inject
	ProductOptionValueService productOptionValueService;

	@Inject
	ProductAttributeService productAttributeService;

	@Inject
	ProductRelationshipService productRelationshipService;

	@Inject
	SearchService searchService;

	@Inject
	ProductImageService productImageService;

	@Inject
	CoreConfiguration configuration;

	@Inject
	ProductReviewService productReviewService;

	@Inject
	public ProductServiceImpl(ProductRepository productRepository) {
		super(productRepository);
		this.productRepository = productRepository;
	}

	@Override
	public void addProductDescription(Product product, ProductDescription description)
			throws ServiceException {


		System.out.println("$#1958#"); if(product.getDescriptions()==null) {
			System.out.println("$#1959#"); product.setDescriptions(new HashSet<ProductDescription>());
		}

		product.getDescriptions().add(description);
		System.out.println("$#1960#"); description.setProduct(product);
		System.out.println("$#1961#"); update(product);
		System.out.println("$#1962#"); searchService.index(product.getMerchantStore(), product);
	}

	@Override
	public List<Product> getProducts(List<Long> categoryIds) throws ServiceException {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		Set ids = new HashSet(categoryIds);
		System.out.println("$#1963#"); return productRepository.getProductsListByCategories(ids);

	}

	@Override
	public List<Product> getProductsByIds(List<Long> productIds) throws ServiceException {
		Set<Long> idSet = productIds.stream().collect(Collectors.toSet());
		System.out.println("$#1964#"); return productRepository.getProductsListByIds(idSet);
	}

	public Product getById(Long productId) {
		System.out.println("$#1965#"); return productRepository.getById(productId);
	}

	@Override
	public Product getProductWithOnlyMerchantStoreById(Long productId) {
		System.out.println("$#1966#"); return productRepository.getProductWithOnlyMerchantStoreById(productId);
	}

	@Override
	public List<Product> getProducts(List<Long> categoryIds, Language language) throws ServiceException {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		Set<Long> ids = new HashSet(categoryIds);
		System.out.println("$#1967#"); return productRepository.getProductsListByCategories(ids, language);

	}



	@Override
	public ProductDescription getProductDescription(Product product, Language language) {
		for (ProductDescription description : product.getDescriptions()) {
			System.out.println("$#1968#"); if (description.getLanguage().equals(language)) {
				System.out.println("$#1969#"); return description;
			}
		}
		return null;
	}

	@Override
	public Product getBySeUrl(MerchantStore store, String seUrl, Locale locale) {
		System.out.println("$#1970#"); return productRepository.getByFriendlyUrl(store, seUrl, locale);
	}

	@Override
	public Product getProductForLocale(long productId, Language language, Locale locale)
			throws ServiceException {
		Product product =  productRepository.getProductForLocale(productId, language, locale);
		System.out.println("$#1971#"); if(product==null) {
			return null;
		}

		System.out.println("$#1972#"); CatalogServiceHelper.setToAvailability(product, locale);
		System.out.println("$#1973#"); CatalogServiceHelper.setToLanguage(product, language.getId());
		System.out.println("$#1974#"); return product;
	}

	@Override
	public List<Product> getProductsForLocale(Category category,
			Language language, Locale locale) throws ServiceException {

		System.out.println("$#1975#"); if(category==null) {
			throw new ServiceException("The category is null");
		}

		//Get the category list
		StringBuilder lineage = new StringBuilder().append(category.getLineage()).append(category.getId()).append("/");
		List<Category> categories = categoryService.getListByLineage(category.getMerchantStore(),lineage.toString());
		Set<Long> categoryIds = new HashSet<Long>();
		for(Category c : categories) {

			categoryIds.add(c.getId());

		}

		categoryIds.add(category.getId());

		//Get products
		List<Product> products = productRepository.getProductsForLocale(category.getMerchantStore(), categoryIds, language, locale);

		//Filter availability

		System.out.println("$#1976#"); return products;
	}

	@Override
	public ProductList listByStore(MerchantStore store,
			Language language, ProductCriteria criteria) {

		System.out.println("$#1977#"); return productRepository.listByStore(store, language, criteria);
	}

	@Override
	public List<Product> listByStore(MerchantStore store) {

		System.out.println("$#1978#"); return productRepository.listByStore(store);
	}

	@Override
	public List<Product> listByTaxClass(TaxClass taxClass) {
		System.out.println("$#1979#"); return productRepository.listByTaxClass(taxClass);
	}

	@Override
	public Product getByCode(String productCode, Language language) {
		System.out.println("$#1980#"); return productRepository.getByCode(productCode, language);
	}





	@Override
	public void delete(Product product) throws ServiceException {
		LOGGER.debug("Deleting product");
		Validate.notNull(product, "Product cannot be null");
		Validate.notNull(product.getMerchantStore(), "MerchantStore cannot be null in product");
		product = this.getById(product.getId());//Prevents detached entity error
		System.out.println("$#1981#"); product.setCategories(null);

		Set<ProductImage> images = product.getImages();

		for(ProductImage image : images) {
			System.out.println("$#1982#"); productImageService.removeProductImage(image);
		}

		System.out.println("$#1983#"); product.setImages(null);

		//delete reviews
		List<ProductReview> reviews = productReviewService.getByProductNoCustomers(product);
		for(ProductReview review : reviews) {
			System.out.println("$#1984#"); productReviewService.delete(review);
		}

		//related - featured
		List<ProductRelationship> relationships = productRelationshipService.listByProduct(product);
		for(ProductRelationship relationship : relationships) {
			System.out.println("$#1985#"); productRelationshipService.deleteRelationship(relationship);
		}

		System.out.println("$#1986#"); super.delete(product);
		System.out.println("$#1987#"); searchService.deleteIndex(product.getMerchantStore(), product);

	}

	@Override
	public void create(Product product) throws ServiceException {
		System.out.println("$#1988#"); saveOrUpdate(product);
		System.out.println("$#1989#"); searchService.index(product.getMerchantStore(), product);
	}

	@Override
	public void update(Product product) throws ServiceException {
		System.out.println("$#1990#"); saveOrUpdate(product);
		System.out.println("$#1991#"); searchService.index(product.getMerchantStore(), product);
	}


	private void saveOrUpdate(Product product) throws ServiceException {
		LOGGER.debug("Save or update product ");
		Validate.notNull(product,"product cannot be null");
		Validate.notNull(product.getAvailabilities(),"product must have at least one availability");
		Validate.notEmpty(product.getAvailabilities(),"product must have at least one availability");

		//take care of product images separately
	    Set<ProductImage> originalProductImages = new HashSet<ProductImage>(product.getImages());

		/** save product first **/

		System.out.println("$#1993#"); System.out.println("$#1992#"); if(product.getId()!=null && product.getId()>0) {
			System.out.println("$#1995#"); super.update(product);
		} else {
			System.out.println("$#1996#"); super.create(product);
		}

		/**
		 * Image creation needs extra service to save the file in the CMS
		 */
		List<Long> newImageIds = new ArrayList<Long>();
		Set<ProductImage> images = product.getImages();

		try {

			System.out.println("$#1998#"); System.out.println("$#1997#"); if(images!=null && images.size()>0) {
				for(ProductImage image : images) {
					System.out.println("$#2000#"); if(image.getImage()!=null && (image.getId()==null || image.getId()==0L)) {
						System.out.println("$#2003#"); image.setProduct(product);

				        InputStream inputStream = image.getImage();
				        ImageContentFile cmsContentImage = new ImageContentFile();
												System.out.println("$#2004#"); cmsContentImage.setFileName( image.getProductImage() );
												System.out.println("$#2005#"); cmsContentImage.setFile( inputStream );
												System.out.println("$#2006#"); cmsContentImage.setFileContentType(FileContentType.PRODUCT);

						System.out.println("$#2007#"); productImageService.addProductImage(product, image, cmsContentImage);
						newImageIds.add(image.getId());
					} else {
									System.out.println("$#2008#"); if(image.getId()!=null) {
										System.out.println("$#2009#"); productImageService.save(image);
    						newImageIds.add(image.getId());
					    }
					}
				}
			}

			//cleanup old and new images
			System.out.println("$#2010#"); if(originalProductImages!=null) {
				for(ProductImage image : originalProductImages) {

																		System.out.println("$#2011#"); if(image.getImage()!=null && image.getId()==null) {
																					System.out.println("$#2013#"); image.setProduct(product);

                     InputStream inputStream = image.getImage();
                     ImageContentFile cmsContentImage = new ImageContentFile();
																					System.out.println("$#2014#"); cmsContentImage.setFileName( image.getProductImage() );
																					System.out.println("$#2015#"); cmsContentImage.setFile( inputStream );
																					System.out.println("$#2016#"); cmsContentImage.setFileContentType(FileContentType.PRODUCT);

																					System.out.println("$#2017#"); productImageService.addProductImage(product, image, cmsContentImage);
                     newImageIds.add(image.getId());
                  } else {
																				System.out.println("$#2018#"); if(!newImageIds.contains(image.getId())) {
																								System.out.println("$#2019#"); productImageService.delete(image);
                    }
                  }
				}
			}

		} catch(Exception e) {
			LOGGER.error("Cannot save images " + e.getMessage());
		}



	}

  @Override
  public Product findOne(Long id, MerchantStore merchant) {
    Validate.notNull(merchant,"MerchantStore must not be null");
    Validate.notNull(id,"id must not be null");
				System.out.println("$#2020#"); return productRepository.getById(id, merchant);
  }


}
