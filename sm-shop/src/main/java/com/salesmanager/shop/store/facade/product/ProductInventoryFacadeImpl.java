package com.salesmanager.shop.store.facade.product;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.availability.ProductAvailabilityService;
import com.salesmanager.core.business.services.merchant.MerchantStoreService;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.availability.ProductAvailability;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.inventory.PersistableInventoryMapper;
import com.salesmanager.shop.mapper.inventory.ReadableInventoryMapper;
import com.salesmanager.shop.model.catalog.product.inventory.PersistableInventory;
import com.salesmanager.shop.model.catalog.product.inventory.ReadableInventory;
import com.salesmanager.shop.model.catalog.product.inventory.ReadableInventoryList;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.controller.product.facade.ProductInventoryFacade;

@Service("productInventoryFacade")
@Profile({"default", "cloud", "gcp", "aws", "mysql"})
public class ProductInventoryFacadeImpl implements ProductInventoryFacade {

  @Autowired
  private ProductAvailabilityService productAvailabilityService;

  @Autowired
  private ProductService productService;

  @Autowired
  private MerchantStoreService merchantStoreService;

  @Autowired
  private ReadableInventoryMapper readableInventoryMapper;

  @Autowired
  private PersistableInventoryMapper productInventoryMapper;

  @Override
  public ReadableInventoryList getInventory(Long productId, MerchantStore store, String child,
      Language language, int page, int count) {

    try {
      Product product = productService.getById(productId);

						System.out.println("$#14909#"); if (product == null) {
        throw new ResourceNotFoundException("Product with id [" + productId + "] not found");
      }

						System.out.println("$#14910#"); if (product.getMerchantStore().getId().intValue() != store.getId().intValue()) {
        throw new ResourceNotFoundException("Product with id [" + productId
            + "] not found for store id [" + store.getInvoiceTemplate() + "]");
      }

      ReadableInventoryList returnList = new ReadableInventoryList();

      Page<ProductAvailability> availabilities =
          productAvailabilityService.listByProduct(product, store, child, page, count);
						System.out.println("$#14911#"); returnList.setTotalPages(availabilities.getTotalPages());
						System.out.println("$#14912#"); returnList.setRecordsTotal(availabilities.getTotalElements());
						System.out.println("$#14913#"); returnList.setNumber(availabilities.getNumber());

      for (ProductAvailability availability : availabilities) {
        ReadableInventory inv = new ReadableInventory();
        inv = readableInventoryMapper.convert(availability, inv, store, language);
        returnList.getInventory().add(inv);
      }

						System.out.println("$#14914#"); return returnList;


    } catch (ServiceException e) {
      throw new ServiceRuntimeException("An error occured while getting inventory list", e);
    }

  }



  @Override
  public void delete(Long inventoryId, MerchantStore store) {
    try {
      ProductAvailability availability = productAvailabilityService.getById(inventoryId, store);
						System.out.println("$#14915#"); productAvailabilityService.delete(availability);
    } catch (ServiceException e) {
      throw new ServiceRuntimeException("Error while deleting inventory", e);
    }

  }

  @Override
  public ReadableInventory get(Long inventoryId, MerchantStore store, Language language) {
    try {

      ProductAvailability availability = productAvailabilityService.getById(inventoryId, store);
						System.out.println("$#14916#"); if (availability == null) {
        throw new ResourceNotFoundException("Inventory with id [" + inventoryId + "] not found");
      }
      ReadableInventory inv = new ReadableInventory();
      inv = readableInventoryMapper.convert(availability, inv, store, language);
						System.out.println("$#14917#"); return inv;


    } catch (ServiceException e) {
      throw new ServiceRuntimeException("Error while getting availability " + inventoryId, e);
    }
  }



  @Override
  public ReadableInventory get(Long productId, String child, Language language) {

    try {
      Product product = productService.getById(productId);
      MerchantStore store = merchantStoreService.getByCode(child);

						System.out.println("$#14918#"); if (product == null) {
        throw new ResourceNotFoundException("Product with id [" + productId + "] not found");
      }

						System.out.println("$#14919#"); if (store == null) {
        throw new ResourceNotFoundException("MerchantStore [" + child + "] not found");
      }

						System.out.println("$#14920#"); if (store.getParent() == null || store.getParent().getId().intValue() != product
          .getMerchantStore().getId().intValue()) {
        throw new ResourceNotFoundException(
            "MerchantStore [" + child + "] is not a store of retailer [" + store.getCode() + "]");
      }

      ProductAvailability availability;

      availability = productAvailabilityService.getByStore(product, store);
						System.out.println("$#14922#"); if (availability == null) {
        throw new ResourceNotFoundException("Inventory with not found");
      }
      ReadableInventory inv = new ReadableInventory();
      inv = readableInventoryMapper.convert(availability, inv, store, language);
						System.out.println("$#14923#"); return inv;
    } catch (ServiceException e) {
      throw new ServiceRuntimeException("Error while getting inventory", e);
    }

  }



  @Override
  public ReadableInventory add(Long productId, PersistableInventory inventory, MerchantStore store,
      Language language) {

    Validate.notNull(productId, "Product id cannot be null");
    Validate.notNull(inventory, "Inventory cannot be null");
    Validate.notNull(store, "MerchantStore cannot be null");

    Product product = productService.getById(productId);

				System.out.println("$#14924#"); if (product == null) {
      throw new ResourceNotFoundException("Product with id [" + productId + "] not found");
    }

    ProductAvailability availability =
        productInventoryMapper.convert(inventory, store, store.getDefaultLanguage());
				System.out.println("$#14925#"); availability.setProduct(product);
				System.out.println("$#14926#"); availability.setMerchantStore(store);
    // add product

    try {
						System.out.println("$#14927#"); productAvailabilityService.saveOrUpdate(availability);

      ReadableInventory returnObject = get(availability.getId(), store, language);

						System.out.println("$#14928#"); return returnObject;
    } catch (ServiceException e) {
      throw new ServiceRuntimeException("Cannot create Inventory", e);
    }

  }



  @Override
  public ReadableInventory get(Long productId, Long inventoryId, MerchantStore store,
      Language language) {
    try {
      Product product = productService.getById(productId);

						System.out.println("$#14929#"); if (product == null) {
        throw new ResourceNotFoundException("Product with id [" + productId + "] not found");
      }

						System.out.println("$#14930#"); if (product.getMerchantStore().getId().intValue() != store.getId().intValue()) {
        throw new ResourceNotFoundException(
            "Product with id [" + productId + "] not found for store [" + store.getCode() + "]");
      }


      ProductAvailability availability;

      availability = productAvailabilityService.getByInventoryId(productId, inventoryId, store);
						System.out.println("$#14931#"); if (availability == null) {
        throw new ResourceNotFoundException("Inventory with id [" + inventoryId + "] not found");
      }
      ReadableInventory inv = new ReadableInventory();
      inv = readableInventoryMapper.convert(availability, inv, store, language);
						System.out.println("$#14932#"); return inv;
    } catch (ServiceException e) {
      throw new ServiceRuntimeException("Error while getting inventory", e);
    }
  }



  @Override
  public void update(Long productId, PersistableInventory inventory, MerchantStore store,
      Language language) {

    Validate.notNull(productId, "Product id cannot be null");
    Validate.notNull(inventory, "Inventory cannot be null");
    Validate.notNull(store, "MerchantStore cannot be null");

    try {
      Product product = productService.getById(productId);

						System.out.println("$#14933#"); if (product == null) {
        throw new ResourceNotFoundException("Product with id [" + productId + "] not found");
      }

      ProductAvailability availability =
          productAvailabilityService.getById(inventory.getId(), store);

						System.out.println("$#14934#"); if (availability == null) {
        throw new ResourceNotFoundException(
            "Availability with id [" + inventory.getId() + "] not found");
      }
      
						System.out.println("$#14935#"); if(availability.getProduct().getId().longValue() != productId) {
          throw new ResourceNotFoundException(
                  "Availability with id [" + inventory.getId() + "] not found for product id [" + productId + "]");
      }
      
						System.out.println("$#14936#"); inventory.setProductId(product.getId());

      availability = productInventoryMapper.convert(inventory, availability, store, language);
						System.out.println("$#14937#"); availability.setProduct(product);
						System.out.println("$#14938#"); availability.setMerchantStore(store);
      // add product


						System.out.println("$#14939#"); productAvailabilityService.saveOrUpdate(availability);

    } catch (ServiceException e) {
      throw new ServiceRuntimeException("Cannot create Inventory", e);
    }

  }

}
