package com.salesmanager.core.business.services.catalog.product.relationship;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.catalog.product.relationship.ProductRelationshipRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.relationship.ProductRelationship;
import com.salesmanager.core.model.catalog.product.relationship.ProductRelationshipType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;

@Service("productRelationshipService")
public class ProductRelationshipServiceImpl extends
		SalesManagerEntityServiceImpl<Long, ProductRelationship> implements
		ProductRelationshipService {

	
	private ProductRelationshipRepository productRelationshipRepository;
	
	@Inject
	public ProductRelationshipServiceImpl(
			ProductRelationshipRepository productRelationshipRepository) {
			super(productRelationshipRepository);
			this.productRelationshipRepository = productRelationshipRepository;
	}
	
	@Override
	public void saveOrUpdate(ProductRelationship relationship) throws ServiceException {
		
		System.out.println("$#2022#"); System.out.println("$#2021#"); if(relationship.getId()!=null && relationship.getId()>0) {
			
			System.out.println("$#2024#"); this.update(relationship);
			
		} else {
			System.out.println("$#2025#"); this.create(relationship);
		}
		
	}
	
	
	@Override
	public void addGroup(MerchantStore store, String groupName) throws ServiceException {
		ProductRelationship relationship = new ProductRelationship();
		System.out.println("$#2026#"); relationship.setCode(groupName);
		System.out.println("$#2027#"); relationship.setStore(store);
		System.out.println("$#2028#"); relationship.setActive(true);
		System.out.println("$#2029#"); this.save(relationship);
	}
	
	@Override
	public List<ProductRelationship> getGroups(MerchantStore store) {
		System.out.println("$#2030#"); return productRelationshipRepository.getGroups(store);
	}
	
	@Override
	public void deleteGroup(MerchantStore store, String groupName) throws ServiceException {
		List<ProductRelationship> entities = productRelationshipRepository.getByGroup(store, groupName);
		for(ProductRelationship relation : entities) {
			System.out.println("$#2031#"); this.delete(relation);
		}
	}
	
	@Override
	public void deactivateGroup(MerchantStore store, String groupName) throws ServiceException {
		List<ProductRelationship> entities = getGroupDefinition(store, groupName);
		for(ProductRelationship relation : entities) {
			System.out.println("$#2032#"); relation.setActive(false);
			System.out.println("$#2033#"); this.saveOrUpdate(relation);
		}
	}
	
	@Override
	public void activateGroup(MerchantStore store, String groupName) throws ServiceException {
		List<ProductRelationship> entities = getGroupDefinition(store, groupName);
		for(ProductRelationship relation : entities) {
			System.out.println("$#2034#"); relation.setActive(true);
			System.out.println("$#2035#"); this.saveOrUpdate(relation);
		}
	}
	
	public void deleteRelationship(ProductRelationship relationship)  throws ServiceException {
		
		//throws detached exception so need to query first
		relationship = this.getById(relationship.getId());
		System.out.println("$#2036#"); if(relationship != null) {
			System.out.println("$#2037#"); delete(relationship);
		}
		
		
		
	}
	
	@Override
	public List<ProductRelationship> listByProduct(Product product) throws ServiceException {

		System.out.println("$#2038#"); return productRelationshipRepository.listByProducts(product);

	}
	
	
	@Override
	public List<ProductRelationship> getByType(MerchantStore store, Product product, ProductRelationshipType type, Language language) throws ServiceException {

		System.out.println("$#2039#"); return productRelationshipRepository.getByType(store, type.name(), product, language);

	}
	
	@Override
	public List<ProductRelationship> getByType(MerchantStore store, ProductRelationshipType type, Language language) throws ServiceException {
		System.out.println("$#2040#"); return productRelationshipRepository.getByType(store, type.name(), language);
	}
	
	@Override
	public List<ProductRelationship> getByType(MerchantStore store, ProductRelationshipType type) throws ServiceException {

		System.out.println("$#2041#"); return productRelationshipRepository.getByType(store, type.name());

	}
	
	@Override
	public List<ProductRelationship> getByGroup(MerchantStore store, String groupName) throws ServiceException {

		System.out.println("$#2042#"); return productRelationshipRepository.getByType(store, groupName);

	}
	
	@Override
	public List<ProductRelationship> getByGroup(MerchantStore store, String groupName, Language language) throws ServiceException {

		System.out.println("$#2043#"); return productRelationshipRepository.getByType(store, groupName, language);

	}
	
	@Override
	public List<ProductRelationship> getByType(MerchantStore store, Product product, ProductRelationshipType type) throws ServiceException {
		

		System.out.println("$#2044#"); return productRelationshipRepository.getByType(store, type.name(), product);
				
		
	}

	@Override
	public List<ProductRelationship> getGroupDefinition(MerchantStore store, String name) {
		System.out.println("$#2045#"); return productRelationshipRepository.getByGroup(store, name);
	}

	@Override
	public List<ProductRelationship> getByType(MerchantStore store, Product product, String name)
			throws ServiceException {
		System.out.println("$#2046#"); return productRelationshipRepository.getByType(store, name, product);
	}



}
