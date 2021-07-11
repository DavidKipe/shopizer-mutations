package com.salesmanager.shop.store.facade.category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang.Validate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.category.CategoryService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductAttributeService;
import com.salesmanager.core.business.services.merchant.MerchantStoreService;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.catalog.product.attribute.ProductOption;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionDescription;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValue;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.catalog.category.PersistableCategory;
import com.salesmanager.shop.model.catalog.category.ReadableCategory;
import com.salesmanager.shop.model.catalog.category.ReadableCategoryList;
import com.salesmanager.shop.model.catalog.product.attribute.ReadableProductVariant;
import com.salesmanager.shop.model.catalog.product.attribute.ReadableProductVariantValue;
import com.salesmanager.shop.model.entity.ListCriteria;
import com.salesmanager.shop.populator.catalog.PersistableCategoryPopulator;
import com.salesmanager.shop.populator.catalog.ReadableCategoryPopulator;
import com.salesmanager.shop.store.api.exception.OperationNotAllowedException;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.api.exception.UnauthorizedException;
import com.salesmanager.shop.store.controller.category.facade.CategoryFacade;

@Service(value = "categoryFacade")
public class CategoryFacadeImpl implements CategoryFacade {

	@Inject
	private CategoryService categoryService;

	@Inject
	private MerchantStoreService merchantStoreService;

	@Inject
	private PersistableCategoryPopulator persistableCatagoryPopulator;

	@Inject
	private Mapper<Category, ReadableCategory> categoryReadableCategoryConverter;

	@Inject
	private ProductAttributeService productAttributeService;

	private static final String FEATURED_CATEGORY = "featured";
	private static final String VISIBLE_CATEGORY = "visible";

	@Override
	public ReadableCategoryList getCategoryHierarchy(MerchantStore store, ListCriteria criteria, int depth,
			Language language, List<String> filter, int page, int count) {

		System.out.println("$#14421#"); Validate.notNull(store,"MerchantStore can not be null");


		//get parent store
		try {

			MerchantStore parent = merchantStoreService.getParent(store.getCode());


			List<Category> categories = null;
			ReadableCategoryList returnList = new ReadableCategoryList();
			System.out.println("$#14422#"); if (!CollectionUtils.isEmpty(filter) && filter.contains(FEATURED_CATEGORY)) {
				categories = categoryService.getListByDepthFilterByFeatured(parent, depth, language);
				System.out.println("$#14424#"); returnList.setRecordsTotal(categories.size());
				System.out.println("$#14425#"); returnList.setNumber(categories.size());
				System.out.println("$#14426#"); returnList.setTotalPages(1);
			} else {
				org.springframework.data.domain.Page<Category> pageable = categoryService.getListByDepth(parent, language,
						criteria != null ? criteria.getName() : null, depth, page, count);
				categories = pageable.getContent();
				System.out.println("$#14428#"); returnList.setRecordsTotal(pageable.getTotalElements());
				System.out.println("$#14429#"); returnList.setTotalPages(pageable.getTotalPages());
				System.out.println("$#14430#"); returnList.setNumber(categories.size());
			}



			List<ReadableCategory> readableCategories = null;
			System.out.println("$#14431#"); if (filter != null && filter.contains(VISIBLE_CATEGORY)) {
				readableCategories = categories.stream().filter(Category::isVisible)
						.map(cat -> categoryReadableCategoryConverter.convert(cat, store, language))
						.collect(Collectors.toList());
			} else {
				readableCategories = categories.stream()
						.map(cat -> categoryReadableCategoryConverter.convert(cat, store, language))
						.collect(Collectors.toList());
			}

			Map<Long, ReadableCategory> readableCategoryMap = readableCategories.stream()
					.collect(Collectors.toMap(ReadableCategory::getId, Function.identity()));

			readableCategories.stream()
					// .filter(ReadableCategory::isVisible)
					.filter(cat -> Objects.nonNull(cat.getParent()))
					.filter(cat -> readableCategoryMap.containsKey(cat.getParent().getId())).forEach(readableCategory -> {
						ReadableCategory parentCategory = readableCategoryMap.get(readableCategory.getParent().getId());
						System.out.println("$#14440#"); if (parentCategory != null) {
							parentCategory.getChildren().add(readableCategory);
						}
					});

			List<ReadableCategory> filteredList = readableCategoryMap.values().stream().filter(cat -> cat.getDepth() == 0)
					.sorted(Comparator.comparing(ReadableCategory::getSortOrder)).collect(Collectors.toList());

			System.out.println("$#14443#"); returnList.setCategories(filteredList);

			System.out.println("$#14444#"); return returnList;

		} catch (ServiceException e) {
			throw new ServiceRuntimeException(e);
		}

	}

	@Override
	public boolean existByCode(MerchantStore store, String code) {
		try {
			Category c = categoryService.getByCode(store, code);
			System.out.println("$#14446#"); System.out.println("$#14445#"); return c != null ? true : false;
		} catch (ServiceException e) {
			throw new ServiceRuntimeException(e);
		}
	}

	@Override
	public PersistableCategory saveCategory(MerchantStore store, PersistableCategory category) {
		try {

			Long categoryId = category.getId();
			System.out.println("$#14450#");
			Category target = Optional.ofNullable(categoryId)
					.filter(merchant -> store !=null)
					.filter(id -> id > 0)
					.map(categoryService::getById)
					.orElse(new Category());

			Category dbCategory = populateCategory(store, category, target);
			System.out.println("$#14452#"); saveCategory(store, dbCategory, null);

			// set category id
			System.out.println("$#14453#"); category.setId(dbCategory.getId());
			System.out.println("$#14454#"); return category;
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Error while updating category", e);
		}
	}

	private Category populateCategory(MerchantStore store, PersistableCategory category, Category target) {
		try {
			System.out.println("$#14455#"); return persistableCatagoryPopulator.populate(category, target, store, store.getDefaultLanguage());
		} catch (ConversionException e) {
			throw new ServiceRuntimeException(e);
		}
	}

	private void saveCategory(MerchantStore store, Category category, Category parent) throws ServiceException {

		/**
		 * c.children1
		 *
		 * <p>
		 * children1.children1 children1.children2
		 *
		 * <p>
		 * children1.children2.children1
		 */

		/** set lineage * */
		System.out.println("$#14456#"); if (parent != null) {
			System.out.println("$#14457#"); category.setParent(category);

			String lineage = parent.getLineage();
			int depth = parent.getDepth();

			System.out.println("$#14459#"); System.out.println("$#14458#"); category.setDepth(depth + 1);
			System.out.println("$#14460#"); category.setLineage(new StringBuilder().append(lineage).toString());// service
																										// will
																										// adjust
																										// lineage
		}

		System.out.println("$#14461#"); category.setMerchantStore(store);

		// remove children
		List<Category> children = category.getCategories();
		List<Category> saveAfter = children.stream().filter(c -> c.getId() == null || c.getId().longValue()==0).collect(Collectors.toList());
		System.out.println("$#14466#"); List<Category> saveNow = children.stream().filter(c -> c.getId() != null && c.getId().longValue()>0).collect(Collectors.toList());
		System.out.println("$#14469#"); category.setCategories(saveNow);

		/** set parent * */
		System.out.println("$#14470#"); if (parent != null) {
			System.out.println("$#14471#"); category.setParent(parent);
		}

		System.out.println("$#14472#"); categoryService.saveOrUpdate(category);

		System.out.println("$#14473#"); if (!CollectionUtils.isEmpty(saveAfter)) {
			parent = category;
			for(Category c: saveAfter) {
				System.out.println("$#14474#"); if(c.getId() == null || c.getId().longValue()==0) {
					for (Category sub : children) {
						System.out.println("$#14476#"); saveCategory(store, sub, parent);
					}
				}
			}
		}

/*		if (!CollectionUtils.isEmpty(children)) {
			parent = category;
			for (Category sub : children) {
				saveCategory(store, sub, parent);
			}
		}*/
	}

	@Override
	public ReadableCategory getById(MerchantStore store, Long id, Language language) {
		try {
			Category categoryModel = null;
			System.out.println("$#14477#"); if (language != null) {
				categoryModel = getCategoryById(id, language);
			} else {// all langs
				categoryModel = getById(store, id);
			}

			System.out.println("$#14478#"); if (categoryModel == null)
				throw new ResourceNotFoundException("Categori id [" + id + "] not found");

			StringBuilder lineage = new StringBuilder().append(categoryModel.getLineage());

			ReadableCategory readableCategory = categoryReadableCategoryConverter.convert(categoryModel, store,
					language);

			// get children
			List<Category> children = getListByLineage(store, lineage.toString());

			List<ReadableCategory> childrenCats = children.stream()
					.map(cat -> categoryReadableCategoryConverter.convert(cat, store, language))
					.collect(Collectors.toList());

			System.out.println("$#14480#"); addChildToParent(readableCategory, childrenCats);
			System.out.println("$#14481#"); return readableCategory;
		} catch (Exception e) {
			throw new ServiceRuntimeException(e);
		}
	}

	private void addChildToParent(ReadableCategory readableCategory, List<ReadableCategory> childrenCats) {
		Map<Long, ReadableCategory> categoryMap = childrenCats.stream()
				.collect(Collectors.toMap(ReadableCategory::getId, Function.identity()));
		categoryMap.put(readableCategory.getId(), readableCategory);

		// traverse map and add child to parent
		for (ReadableCategory readable : childrenCats) {

			System.out.println("$#14482#"); if (readable.getParent() != null) {

				ReadableCategory rc = categoryMap.get(readable.getParent().getId());
				System.out.println("$#14483#"); if (rc != null) {
					rc.getChildren().add(readable);
				}
			}
		}
	}

	private List<Category> getListByLineage(MerchantStore store, String lineage) {
		try {
			System.out.println("$#14484#"); return categoryService.getListByLineage(store, lineage);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException(String.format("Error while getting root category %s", e.getMessage()), e);
		}
	}

	private Category getCategoryById(Long id, Language language) {
		System.out.println("$#14485#"); return Optional.ofNullable(categoryService.getOneByLanguage(id, language))
				.orElseThrow(() -> new ResourceNotFoundException("Category id not found"));
	}

	@Override
	public void deleteCategory(Category category) {
		try {
			System.out.println("$#14487#"); categoryService.delete(category);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Error while deleting category", e);
		}
	}

	@Override
	public ReadableCategory getByCode(MerchantStore store, String code, Language language) throws Exception {

		System.out.println("$#14488#"); Validate.notNull(code, "category code must not be null");
		ReadableCategoryPopulator categoryPopulator = new ReadableCategoryPopulator();
		ReadableCategory readableCategory = new ReadableCategory();

		Category category = categoryService.getByCode(store, code);
		categoryPopulator.populate(category, readableCategory, store, language);

		System.out.println("$#14489#"); return readableCategory;
	}

	private Category getById(MerchantStore store, Long id) throws Exception {
		System.out.println("$#14490#"); Validate.notNull(id, "category id must not be null");
		System.out.println("$#14491#"); Validate.notNull(store, "MerchantStore must not be null");
		Category category = categoryService.getById(id, store.getId());
		System.out.println("$#14492#"); if (category == null) {
			throw new ResourceNotFoundException("Category with id [" + id + "] not found");
		}
		System.out.println("$#14493#"); if (category.getMerchantStore().getId().intValue() != store.getId().intValue()) {
			throw new UnauthorizedException("Unauthorized");
		}
		System.out.println("$#14494#"); return category;
	}

	@Override
	public void deleteCategory(Long categoryId, MerchantStore store) {
		Category category = getOne(categoryId, store.getId());
		System.out.println("$#14495#"); deleteCategory(category);
	}

	private Category getOne(Long categoryId, int storeId) {
		System.out.println("$#14496#"); return Optional.ofNullable(categoryService.getById(categoryId)).orElseThrow(
				() -> new ResourceNotFoundException(String.format("No Category found for ID : %s", categoryId)));
	}

	@Override
	public List<ReadableProductVariant> categoryProductVariants(Long categoryId, MerchantStore store,
			Language language) {
		Category category = categoryService.getById(categoryId, store.getId());

		List<ReadableProductVariant> variants = new ArrayList<ReadableProductVariant>();

		System.out.println("$#14498#"); if (category == null) {
			throw new ResourceNotFoundException("Category [" + categoryId + "] not found");
		}

		try {
			List<ProductAttribute> attributes = productAttributeService.getProductAttributesByCategoryLineage(store,
					category.getLineage(), language);

			/**
			 * Option NAME OptionValueName OptionValueName
			 **/
			Map<String, List<ProductOptionValue>> rawFacet = new HashMap<String, List<ProductOptionValue>>();
			Map<String, ProductOption> references = new HashMap<String, ProductOption>();
			for (ProductAttribute attr : attributes) {
				references.put(attr.getProductOption().getCode(), attr.getProductOption());
				List<ProductOptionValue> values = rawFacet.get(attr.getProductOption().getCode());
				System.out.println("$#14499#"); if (values == null) {
					values = new ArrayList<ProductOptionValue>();
					rawFacet.put(attr.getProductOption().getCode(), values);
				}
				values.add(attr.getProductOptionValue());
			}

			// for each reference set Option
			Iterator<Entry<String, ProductOption>> it = references.entrySet().iterator();
			while (it.hasNext()) {
				@SuppressWarnings("rawtypes")
				Map.Entry pair = (Map.Entry) it.next();
				ProductOption option = (ProductOption) pair.getValue();
				List<ProductOptionValue> values = rawFacet.get(option.getCode());

				ReadableProductVariant productVariant = new ReadableProductVariant();
				Optional<ProductOptionDescription>  optionDescription = option.getDescriptions().stream().filter(o -> o.getLanguage().getId() == language.getId()).findFirst();
				System.out.println("$#14502#"); if(optionDescription.isPresent()) {
					System.out.println("$#14503#"); productVariant.setName(optionDescription.get().getName());
					System.out.println("$#14504#"); productVariant.setId(optionDescription.get().getId());
					List<ReadableProductVariantValue> optionValues = new ArrayList<ReadableProductVariantValue>();
					for (ProductOptionValue value : values) {
						Optional<ProductOptionValueDescription>  optionValueDescription = value.getDescriptions().stream().filter(o -> o.getLanguage().getId() == language.getId()).findFirst();
						ReadableProductVariantValue v = new ReadableProductVariantValue();
						System.out.println("$#14507#"); v.setName(value.getDescriptionsSettoList().get(0).getName());
						System.out.println("$#14508#"); v.setDescription(value.getDescriptionsSettoList().get(0).getDescription());
						System.out.println("$#14509#"); if(optionValueDescription.isPresent()) {
							System.out.println("$#14510#"); v.setName(optionValueDescription.get().getName());
							System.out.println("$#14511#"); v.setDescription(optionValueDescription.get().getDescription());
						}
						System.out.println("$#14512#"); v.setOption(option.getId());
						System.out.println("$#14513#"); v.setValue(value.getId());
						optionValues.add(v);
					}
					System.out.println("$#14514#"); productVariant.setOptions(optionValues);
					variants.add(productVariant);
				}
			}

			System.out.println("$#14515#"); return variants;
		} catch (Exception e) {
			throw new ServiceRuntimeException("An error occured while retrieving ProductAttributes", e);
		}
	}

	@Override
	public void move(Long child, Long parent, MerchantStore store) {

		System.out.println("$#14516#"); Validate.notNull(child, "Child category must not be null");
		System.out.println("$#14517#"); Validate.notNull(parent, "Parent category must not be null");
		System.out.println("$#14518#"); Validate.notNull(store, "Merhant must not be null");
		
		
		try {

			Category c = categoryService.getById(child, store.getId());

			System.out.println("$#14519#"); if(c == null) {
				throw new ResourceNotFoundException("Category with id [" + child + "] for store [" + store.getCode() + "]");
			}
			
			System.out.println("$#14520#"); if(parent.longValue()==-1) {
				System.out.println("$#14521#"); categoryService.addChild(null, c);
				return;
				
			}

			Category p = categoryService.getById(parent, store.getId());

			System.out.println("$#14522#"); if(p == null) {
				throw new ResourceNotFoundException("Category with id [" + parent + "] for store [" + store.getCode() + "]");
			}

			System.out.println("$#14523#"); if (c.getParent() != null && c.getParent().getId() == parent) {
				return;
			}

			System.out.println("$#14525#"); if (c.getMerchantStore().getId().intValue() != store.getId().intValue()) {
				throw new OperationNotAllowedException(
						"Invalid identifiers for Merchant [" + c.getMerchantStore().getCode() + "]");
			}

			System.out.println("$#14526#"); if (p.getMerchantStore().getId().intValue() != store.getId().intValue()) {
				throw new OperationNotAllowedException(
						"Invalid identifiers for Merchant [" + c.getMerchantStore().getCode() + "]");
			}

			System.out.println("$#14527#"); p.getAuditSection().setModifiedBy("Api");
			System.out.println("$#14528#"); categoryService.addChild(p, c);
		} catch (ResourceNotFoundException re) {
			throw re;
		} catch (OperationNotAllowedException oe) {
			throw oe;
		} catch (Exception e) {
			throw new ServiceRuntimeException(e);
		}

	}

	@Override
	public Category getByCode(String code, MerchantStore store) {
		try {
			System.out.println("$#14529#"); return categoryService.getByCode(store, code);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Exception while reading category code [" + code + "]",e);
		}
	}

	@Override
	public void setVisible(PersistableCategory category, MerchantStore store) {
		System.out.println("$#14530#"); Validate.notNull(category, "Category must not be null");
		System.out.println("$#14531#"); Validate.notNull(store, "Store must not be null");
		try {
			Category c = this.getById(store, category.getId());
			System.out.println("$#14532#"); c.setVisible(category.isVisible());
			System.out.println("$#14533#"); categoryService.saveOrUpdate(c);
		} catch (Exception e) {
			throw new ServiceRuntimeException("Error while getting category [" + category.getId() + "]",e);
		}
	}
}
