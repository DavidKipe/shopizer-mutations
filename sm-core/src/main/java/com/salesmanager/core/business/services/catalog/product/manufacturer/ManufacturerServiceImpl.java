package com.salesmanager.core.business.services.catalog.product.manufacturer;


import java.util.HashSet;
import java.util.List;
import javax.inject.Inject;
import org.jsoup.helper.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.catalog.product.manufacturer.ManufacturerRepository;
import com.salesmanager.core.business.repositories.catalog.product.manufacturer.PageableManufacturerRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.catalog.product.manufacturer.Manufacturer;
import com.salesmanager.core.model.catalog.product.manufacturer.ManufacturerDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;



@Service("manufacturerService")
public class ManufacturerServiceImpl extends SalesManagerEntityServiceImpl<Long, Manufacturer>
    implements ManufacturerService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ManufacturerServiceImpl.class);

  @Inject
  private PageableManufacturerRepository pageableManufacturerRepository;
  
  private ManufacturerRepository manufacturerRepository;

  @Inject
  public ManufacturerServiceImpl(ManufacturerRepository manufacturerRepository) {
    super(manufacturerRepository);
    this.manufacturerRepository = manufacturerRepository;
  }

  @Override
  public void delete(Manufacturer manufacturer) throws ServiceException {
    manufacturer = this.getById(manufacturer.getId());
				System.out.println("$#1914#"); super.delete(manufacturer);
  }

  @Override
  public Long getCountManufAttachedProducts(Manufacturer manufacturer) throws ServiceException {
				System.out.println("$#1915#"); return manufacturerRepository.countByProduct(manufacturer.getId());
    // .getCountManufAttachedProducts( manufacturer );
  }


  @Override
  public List<Manufacturer> listByStore(MerchantStore store, Language language)
      throws ServiceException {
				System.out.println("$#1916#"); return manufacturerRepository.findByStoreAndLanguage(store.getId(), language.getId());
  }

  @Override
  public List<Manufacturer> listByStore(MerchantStore store) throws ServiceException {
				System.out.println("$#1917#"); return manufacturerRepository.findByStore(store.getId());
  }

  @Override
  public List<Manufacturer> listByProductsByCategoriesId(MerchantStore store, List<Long> ids,
      Language language) throws ServiceException {
				System.out.println("$#1918#"); return manufacturerRepository.findByCategoriesAndLanguage(ids, language.getId());
  }

  @Override
  public void addManufacturerDescription(Manufacturer manufacturer,
      ManufacturerDescription description) throws ServiceException {


				System.out.println("$#1919#"); if (manufacturer.getDescriptions() == null) {
						System.out.println("$#1920#"); manufacturer.setDescriptions(new HashSet<ManufacturerDescription>());
    }

    manufacturer.getDescriptions().add(description);
				System.out.println("$#1921#"); description.setManufacturer(manufacturer);
				System.out.println("$#1922#"); update(manufacturer);
  }

  @Override
  public void saveOrUpdate(Manufacturer manufacturer) throws ServiceException {

    LOGGER.debug("Creating Manufacturer");

				System.out.println("$#1924#"); System.out.println("$#1923#"); if (manufacturer.getId() != null && manufacturer.getId().longValue() > 0) {
						System.out.println("$#1926#"); super.update(manufacturer);

    } else {
						System.out.println("$#1927#"); super.create(manufacturer);

    }
  }

  @Override
  public Manufacturer getByCode(com.salesmanager.core.model.merchant.MerchantStore store,
      String code) {
				System.out.println("$#1928#"); return manufacturerRepository.findByCodeAndMerchandStore(code, store.getId());
  }
  
  @Override
  public Manufacturer getById(Long id) {
				System.out.println("$#1929#"); return manufacturerRepository.findOne(id);
  }

  @Override
  public List<Manufacturer> listByProductsInCategory(MerchantStore store, Category category,
      Language language) throws ServiceException {
				System.out.println("$#1930#"); Validate.notNull(store, "Store cannot be null");
				System.out.println("$#1931#"); Validate.notNull(category,"Category cannot be null");
				System.out.println("$#1932#"); Validate.notNull(language, "Language cannot be null");
				System.out.println("$#1933#"); return manufacturerRepository.findByProductInCategoryId(store.getId(), category.getLineage(), language.getId());
  }

  @Override
  public Page<Manufacturer> listByStore(MerchantStore store, Language language, int page, int count)
      throws ServiceException {

    Pageable pageRequest = PageRequest.of(page, count);
				System.out.println("$#1934#"); return pageableManufacturerRepository.findByStore(store.getId(), language.getId(), null, pageRequest);
  }

  @Override
  public int count(MerchantStore store) {
				System.out.println("$#1935#"); Validate.notNull(store, "Merchant must not be null");
				System.out.println("$#1936#"); return manufacturerRepository.count(store.getId());
  }

  @Override
  public Page<Manufacturer> listByStore(MerchantStore store, Language language, String name,
      int page, int count) throws ServiceException {

    Pageable pageRequest = PageRequest.of(page, count);
				System.out.println("$#1937#"); return pageableManufacturerRepository.findByStore(store.getId(), language.getId(), name, pageRequest);
  }

  @Override
  public Page<Manufacturer> listByStore(MerchantStore store, String name, int page, int count)
      throws ServiceException {

    Pageable pageRequest = PageRequest.of(page, count);
				System.out.println("$#1938#"); return pageableManufacturerRepository.findByStore(store.getId(), name, pageRequest);
  }
}
