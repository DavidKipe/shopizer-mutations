package com.salesmanager.core.business.services.catalog.category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.catalog.category.CategoryDescriptionRepository;
import com.salesmanager.core.business.repositories.catalog.category.CategoryRepository;
import com.salesmanager.core.business.repositories.catalog.category.PageableCategoryRepository;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.catalog.category.CategoryDescription;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;

@Service("categoryService")
public class CategoryServiceImpl extends SalesManagerEntityServiceImpl<Long, Category> implements CategoryService {


  private CategoryRepository categoryRepository;

  @Inject
  private ProductService productService;
  
  @Inject
  private PageableCategoryRepository pageableCategoryRepository;
  
  @Inject
  private CategoryDescriptionRepository categoryDescriptionRepository;



  @Inject
  public CategoryServiceImpl(CategoryRepository categoryRepository) {
    super(categoryRepository);
    this.categoryRepository = categoryRepository;
  }

  public void create(Category category) throws ServiceException {

				System.out.println("$#1738#"); super.create(category);
    StringBuilder lineage = new StringBuilder();
    Category parent = category.getParent();
				System.out.println("$#1739#"); if (parent != null && parent.getId() != null && parent.getId().longValue() != 0) {
      //get parent category
      Category p = this.getById(parent.getId());

      lineage.append(p.getLineage()).append(category.getId()).append("/");
						System.out.println("$#1743#"); System.out.println("$#1742#"); category.setDepth(p.getDepth() + 1);
    } else {
      lineage.append("/").append(category.getId()).append("/");
						System.out.println("$#1744#"); category.setDepth(0);
    }
				System.out.println("$#1745#"); category.setLineage(lineage.toString());
				System.out.println("$#1746#"); super.update(category);


  }

  @Override
  public List<Object[]> countProductsByCategories(MerchantStore store, List<Long> categoryIds)
      throws ServiceException {

				System.out.println("$#1747#"); return categoryRepository.countProductsByCategories(store, categoryIds);

	}


	@Override
	public List<Category> listByCodes(MerchantStore store, List<String> codes, Language language) {
		System.out.println("$#1748#"); return categoryRepository.findByCodes(store.getId(), codes, language.getId());
	}

	@Override
	public List<Category> listByIds(MerchantStore store, List<Long> ids, Language language) {
		System.out.println("$#1749#"); return categoryRepository.findByIds(store.getId(), ids, language.getId());
	}

	@Override
	public Category getOneByLanguage(long categoryId, Language language) {
		System.out.println("$#1750#"); return categoryRepository.findByIdAndLanguage(categoryId, language.getId());
	}

	@Override
	public void saveOrUpdate(Category category) throws ServiceException {

		// save or update (persist and attach entities
		System.out.println("$#1752#"); System.out.println("$#1751#"); if (category.getId() != null && category.getId() > 0) {

			System.out.println("$#1754#"); super.update(category);

		} else {

			System.out.println("$#1755#"); this.create(category);

		}

	}

	@Override
	public List<Category> getListByLineage(MerchantStore store, String lineage) throws ServiceException {
		try {
			System.out.println("$#1756#"); return categoryRepository.findByLineage(store.getId(), lineage);
		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}

	@Override
	public List<Category> getListByLineage(String storeCode, String lineage) throws ServiceException {
		try {
			System.out.println("$#1757#"); return categoryRepository.findByLineage(storeCode, lineage);
		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}

	@Override
	public List<Category> listBySeUrl(MerchantStore store, String seUrl) throws ServiceException {

		try {
			System.out.println("$#1758#"); return categoryRepository.listByFriendlyUrl(store.getId(), seUrl);
		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}

	@Override
	public Category getBySeUrl(MerchantStore store, String seUrl) {
		System.out.println("$#1759#"); return categoryRepository.findByFriendlyUrl(store.getId(), seUrl);
	}

	@Override
	public Category getByCode(MerchantStore store, String code) throws ServiceException {

		try {
			System.out.println("$#1760#"); return categoryRepository.findByCode(store.getId(), code);
		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}

	@Override
	public Category getByCode(String storeCode, String code) throws ServiceException {

		try {
			System.out.println("$#1761#"); return categoryRepository.findByCode(storeCode, code);
		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}

	@Override
	public Category getById(Long id, int merchantId) {

		Category category = categoryRepository.findByIdAndStore(id, merchantId);
		
		System.out.println("$#1762#"); if(category == null) {
			return null;
		}

		List<CategoryDescription> descriptions = categoryDescriptionRepository.listByCategoryId(id);

		Set<CategoryDescription> desc = new HashSet<CategoryDescription>(descriptions);

		System.out.println("$#1763#"); category.setDescriptions(desc);

		System.out.println("$#1764#"); return category;

	}

	@Override
	public List<Category> listByParent(Category category) throws ServiceException {

		try {
			System.out.println("$#1765#"); return categoryRepository.listByStoreAndParent(null, category);
		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}

	@Override
	public List<Category> listByStoreAndParent(MerchantStore store, Category category) throws ServiceException {

		try {
			System.out.println("$#1766#"); return categoryRepository.listByStoreAndParent(store, category);
		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}

	@Override
	public List<Category> listByParent(Category category, Language language) {
		System.out.println("$#1767#"); Assert.notNull(category, "Category cannot be null");
		System.out.println("$#1768#"); Assert.notNull(language, "Language cannot be null");
		System.out.println("$#1769#"); Assert.notNull(category.getMerchantStore(), "category.merchantStore cannot be null");

		System.out.println("$#1770#"); return categoryRepository.findByParent(category.getId(), language.getId());
	}

	@Override
	public void addCategoryDescription(Category category, CategoryDescription description) throws ServiceException {

		try {
			category.getDescriptions().add(description);
			System.out.println("$#1771#"); description.setCategory(category);
			System.out.println("$#1772#"); update(category);
		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}

	// @Override
	public void delete(Category category) throws ServiceException {

		// get category with lineage (subcategories)
		StringBuilder lineage = new StringBuilder();
		lineage.append(category.getLineage()).append(category.getId()).append(Constants.SLASH);
		List<Category> categories = this.getListByLineage(category.getMerchantStore(), lineage.toString());

		Category dbCategory = getById(category.getId(), category.getMerchantStore().getId());

		System.out.println("$#1773#"); if (dbCategory != null && dbCategory.getId().longValue() == category.getId().longValue()) {

			categories.add(dbCategory);

			System.out.println("$#1775#"); Collections.reverse(categories);

			List<Long> categoryIds = new ArrayList<Long>();

			for (Category c : categories) {
				categoryIds.add(c.getId());
			}

			List<Product> products = productService.getProducts(categoryIds);
			// org.hibernate.Session session =
			// em.unwrap(org.hibernate.Session.class);// need to refresh the
			// session to update
			// all product
			// categories

			for (Product product : products) {
				// session.evict(product);// refresh product so we get all
				// product categories
				Product dbProduct = productService.getById(product.getId());
				Set<Category> productCategories = dbProduct.getCategories();
				System.out.println("$#1777#"); System.out.println("$#1776#"); if (productCategories.size() > 1) {
					for (Category c : categories) {
						productCategories.remove(c);
						System.out.println("$#1778#"); productService.update(dbProduct);
					}

					System.out.println("$#1779#"); if (product.getCategories() == null || product.getCategories().size() == 0) {
						System.out.println("$#1781#"); productService.delete(dbProduct);
					}

				} else {
					System.out.println("$#1782#"); productService.delete(dbProduct);
				}

			}

			Category categ = getById(category.getId(), category.getMerchantStore().getId());
			System.out.println("$#1783#"); categoryRepository.delete(categ);

		}

	}

	@Override
	public CategoryDescription getDescription(Category category, Language language) {

		for (CategoryDescription description : category.getDescriptions()) {
			System.out.println("$#1784#"); if (description.getLanguage().equals(language)) {
				System.out.println("$#1785#"); return description;
			}
		}
		return null;
	}

	@Override
	public void addChild(Category parent, Category child) throws ServiceException {

		System.out.println("$#1786#"); if (child == null || child.getMerchantStore() == null) {
			throw new ServiceException("Child category and merchant store should not be null");
		}

		try {

			System.out.println("$#1788#"); if (parent == null) {

				// assign to root
				System.out.println("$#1789#"); child.setParent(null);
				System.out.println("$#1790#"); child.setDepth(0);
				// child.setLineage(new
				// StringBuilder().append("/").append(child.getId()).append("/").toString());
				System.out.println("$#1791#"); child.setLineage(new StringBuilder().append("/").append(child.getId()).append("/").toString());

			} else {

				Category p = getById(parent.getId(), parent.getMerchantStore().getId());// parent

				String lineage = p.getLineage();
				int depth = p.getDepth();

				System.out.println("$#1792#"); child.setParent(p);
				System.out.println("$#1794#"); System.out.println("$#1793#"); child.setDepth(depth + 1);
				System.out.println("$#1795#"); child.setLineage(new StringBuilder().append(lineage).append(Constants.SLASH).append(child.getId())
						.append(Constants.SLASH).toString());

			}

			System.out.println("$#1796#"); update(child);
			StringBuilder childLineage = new StringBuilder();
			childLineage.append(child.getLineage()).append(child.getId()).append("/");
			List<Category> subCategories = getListByLineage(child.getMerchantStore(), childLineage.toString());

			// ajust all sub categories lineages
			System.out.println("$#1798#"); System.out.println("$#1797#"); if (subCategories != null && subCategories.size() > 0) {
				for (Category subCategory : subCategories) {
					System.out.println("$#1800#"); if (child.getId() != subCategory.getId()) {
						System.out.println("$#1801#"); addChild(child, subCategory);
					}
				}

			}
		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}

	@Override
	public List<Category> getListByDepth(MerchantStore store, int depth) {
		System.out.println("$#1802#"); return categoryRepository.findByDepth(store.getId(), depth);
	}

	@Override
	public List<Category> getListByDepthFilterByFeatured(MerchantStore store, int depth, Language language) {
		System.out.println("$#1803#"); return categoryRepository.findByDepthFilterByFeatured(store.getId(), depth, language.getId());
	}

	@Override
	public List<Category> getByName(MerchantStore store, String name, Language language) throws ServiceException {

		try {
			System.out.println("$#1804#"); return categoryRepository.findByName(store.getId(), name, language.getId());
		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}

	@Override
	public List<Category> listByStore(MerchantStore store) throws ServiceException {

		try {
			System.out.println("$#1805#"); return categoryRepository.findByStore(store.getId());
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public List<Category> listByStore(MerchantStore store, Language language) throws ServiceException {

		try {
			System.out.println("$#1806#"); return categoryRepository.findByStore(store.getId(), language.getId());
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public Category getById(MerchantStore store, Long id) throws ServiceException {
		System.out.println("$#1807#"); return categoryRepository.findById(id, store.getCode());
	}

	@Override
	public Category findById(Long category) {
		Optional<Category> cat = categoryRepository.findById(category);
		System.out.println("$#1808#"); if (cat.isPresent()) {
			System.out.println("$#1809#"); return cat.get();
		}
		return null;
	}

	@Override
	public Page<Category> getListByDepth(MerchantStore store, Language language, String name, int depth, int page,
			int count) {

		Pageable pageRequest = PageRequest.of(page, count);

		System.out.println("$#1810#"); return pageableCategoryRepository.listByStore(store.getId(), language.getId(), name, pageRequest);
	}

	@Override
	public List<Category> getListByDepth(MerchantStore store, int depth, Language language) {
		System.out.println("$#1811#"); return categoryRepository.find(store.getId(), depth, language.getId(), null);
	}

	@Override
	public int count(MerchantStore store) {
		System.out.println("$#1812#"); return categoryRepository.count(store.getId());
	}

	@Override
	public Category getById(Long categoryid, int merchantId, int language) {
		System.out.println("$#1813#"); return categoryRepository.findById(merchantId, categoryid, language);
	}

}
