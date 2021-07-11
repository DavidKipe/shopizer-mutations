package com.salesmanager.shop.store.facade.items;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.relationship.ProductRelationshipService;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.ProductCriteria;
import com.salesmanager.core.model.catalog.product.relationship.ProductRelationship;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.catalog.product.ReadableProduct;
import com.salesmanager.shop.model.catalog.product.ReadableProductList;
import com.salesmanager.shop.model.catalog.product.group.ProductGroup;
import com.salesmanager.shop.populator.catalog.ReadableProductPopulator;
import com.salesmanager.shop.store.api.exception.OperationNotAllowedException;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.controller.items.facade.ProductItemsFacade;
import com.salesmanager.shop.utils.ImageFilePath;

@Component
public class ProductItemsFacadeImpl implements ProductItemsFacade {
	
	
	@Inject
	ProductService productService;
	
	@Inject
	PricingService pricingService;
	
	@Inject
	@Qualifier("img")
	private ImageFilePath imageUtils;
	
	@Inject
	private ProductRelationshipService productRelationshipService;

	@Override
	public ReadableProductList listItemsByManufacturer(MerchantStore store,
			Language language, Long manufacturerId, int startCount, int maxCount) throws Exception {
		
		
		ProductCriteria productCriteria = new ProductCriteria();
		System.out.println("$#14695#"); productCriteria.setMaxCount(maxCount);
		System.out.println("$#14696#"); productCriteria.setStartIndex(startCount);
		

		System.out.println("$#14697#"); productCriteria.setManufacturerId(manufacturerId);
		com.salesmanager.core.model.catalog.product.ProductList products = productService.listByStore(store, language, productCriteria);

		
		ReadableProductPopulator populator = new ReadableProductPopulator();
		System.out.println("$#14698#"); populator.setPricingService(pricingService);
		System.out.println("$#14699#"); populator.setimageUtils(imageUtils);
		
		
		ReadableProductList productList = new ReadableProductList();
		for(Product product : products.getProducts()) {

			//create new proxy product
			ReadableProduct readProduct = populator.populate(product, new ReadableProduct(), store, language);
			productList.getProducts().add(readProduct);
			
		}
		
		System.out.println("$#14700#"); productList.setTotalPages(products.getTotalCount());
		
		
		System.out.println("$#14701#"); return productList;
	}
	
	@Override
	public ReadableProductList listItemsByIds(MerchantStore store, Language language, List<Long> ids, int startCount,
			int maxCount) throws Exception {
		
		System.out.println("$#14702#"); if(CollectionUtils.isEmpty(ids)) {
			System.out.println("$#14703#"); return new ReadableProductList();
		}
		
		
		ProductCriteria productCriteria = new ProductCriteria();
		System.out.println("$#14704#"); productCriteria.setMaxCount(maxCount);
		System.out.println("$#14705#"); productCriteria.setStartIndex(startCount);
		System.out.println("$#14706#"); productCriteria.setProductIds(ids);
		

		com.salesmanager.core.model.catalog.product.ProductList products = productService.listByStore(store, language, productCriteria);

		
		ReadableProductPopulator populator = new ReadableProductPopulator();
		System.out.println("$#14707#"); populator.setPricingService(pricingService);
		System.out.println("$#14708#"); populator.setimageUtils(imageUtils);
		
		
		ReadableProductList productList = new ReadableProductList();
		for(Product product : products.getProducts()) {

			//create new proxy product
			ReadableProduct readProduct = populator.populate(product, new ReadableProduct(), store, language);
			productList.getProducts().add(readProduct);
			
		}
		
		System.out.println("$#14709#"); productList.setNumber(products.getTotalCount());
		System.out.println("$#14710#"); productList.setRecordsTotal(new Long(products.getTotalCount()));

		System.out.println("$#14711#"); return productList;
	}

	@Override
	public ReadableProductList listItemsByGroup(String group, MerchantStore store, Language language) throws Exception {


		//get product group
		List<ProductRelationship> groups = productRelationshipService.getByGroup(store, group, language);

		System.out.println("$#14712#"); if(group!=null) {
			List<Long> ids = new ArrayList<Long>();
			for(ProductRelationship relationship : groups) {
				Product product = relationship.getRelatedProduct();
				ids.add(product.getId());
			}
			
			ReadableProductList list = listItemsByIds(store, language, ids, 0, 0);
			List<ReadableProduct> prds = list.getProducts().stream().sorted(Comparator.comparing(ReadableProduct::getSortOrder)).collect(Collectors.toList());
			System.out.println("$#14713#"); list.setProducts(prds);
			System.out.println("$#14714#"); list.setTotalPages(1);//no paging
			System.out.println("$#14715#"); return list;
		}
		
		return null;
	}

	@Override
	public ReadableProductList addItemToGroup(Product product, String group, MerchantStore store, Language language) {
		
		Validate.notNull(product,"Product must not be null");
		Validate.notNull(group,"group must not be null");
		
		
		//check if product is already in group
		List<ProductRelationship> existList = null;
		try {
			existList = productRelationshipService.getByGroup(store, group).stream()
			.filter(prod -> prod.getRelatedProduct() != null && (product.getId().longValue() == prod.getRelatedProduct().getId()))
			.collect(Collectors.toList());
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("ExceptionWhile getting product group [" + group + "]", e);
		}
		
		System.out.println("$#14720#"); System.out.println("$#14719#"); if(existList.size()>0) {
			throw new OperationNotAllowedException("Product with id [" + product.getId() + "] is already in the group");
		}
		
		
		ProductRelationship relationship = new ProductRelationship();
		System.out.println("$#14721#"); relationship.setActive(true);
		System.out.println("$#14722#"); relationship.setCode(group);
		System.out.println("$#14723#"); relationship.setStore(store);
		System.out.println("$#14724#"); relationship.setRelatedProduct(product);

		try {
			System.out.println("$#14725#"); productRelationshipService.saveOrUpdate(relationship);
			System.out.println("$#14726#"); return listItemsByGroup(group,store,language);
		} catch (Exception e) {
			throw new ServiceRuntimeException("ExceptionWhile getting product group [" + group + "]", e);
		}
		
		
		
		
	}

	@Override
	public ReadableProductList removeItemFromGroup(Product product, String group, MerchantStore store,
			Language language) throws Exception {
		
		ProductRelationship relationship = null;
		List<ProductRelationship> relationships = productRelationshipService.getByType(store, product, group);

		for(ProductRelationship r : relationships) {
			System.out.println("$#14727#"); if(r.getRelatedProduct().getId().longValue()==product.getId().longValue()) {
				System.out.println("$#14728#"); productRelationshipService.delete(relationship);
			}
		}

		System.out.println("$#14729#"); return listItemsByGroup(group,store,language);
	}

	@Override
	public void deleteGroup(String group, MerchantStore store) {
		
		Validate.notNull(group, "Group cannot be null");
		Validate.notNull(store, "MerchantStore cannot be null");
		
		try {
			System.out.println("$#14730#"); productRelationshipService.deleteGroup(store, group);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Cannor delete product group",e);
		}
		
	}

	@Override
	public ProductGroup createProductGroup(ProductGroup group, MerchantStore store) {
		Validate.notNull(group,"ProductGroup cannot be null");
		Validate.notNull(group.getCode(),"ProductGroup code cannot be null");
		Validate.notNull(store,"MerchantStore cannot be null");
		try {
			System.out.println("$#14731#"); productRelationshipService.addGroup(store, group.getCode());
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Cannor delete product group",e);
		}
		System.out.println("$#14732#"); return group;
	}

	@Override
	public void updateProductGroup(String code, ProductGroup group, MerchantStore store) {
		try {
			List<ProductRelationship>  items = productRelationshipService.getGroupDefinition(store, code);
			System.out.println("$#14733#"); if(CollectionUtils.isEmpty(items)) {
				throw new ResourceNotFoundException("ProductGroup [" + code + "] not found");
			}
			
			System.out.println("$#14734#"); if(group.isActive()) {
				System.out.println("$#14735#"); productRelationshipService.activateGroup(store, code);
			} else {
				System.out.println("$#14736#"); productRelationshipService.deactivateGroup(store, code);
			}
			
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Exception while updating product [" + code + "]");
		}
		
	}

	@Override
	public List<ProductGroup> listProductGroups(MerchantStore store, Language language) {
		Validate.notNull(store,"MerchantStore cannot be null");
		
		List<ProductRelationship> relationships = productRelationshipService.getGroups(store);
		
		List<ProductGroup> groups = new ArrayList<ProductGroup>();
		
		for(ProductRelationship relationship : relationships) {
			
			System.out.println("$#14737#"); if(!"FEATURED_ITEM".equals(relationship.getCode())) {//do not add featured items
				ProductGroup g = new ProductGroup();
				System.out.println("$#14738#"); g.setActive(relationship.isActive());
				System.out.println("$#14739#"); g.setCode(relationship.getCode());
				groups.add(g);
			
			}
			
		}
		
		System.out.println("$#14740#"); return groups;
	}

}
