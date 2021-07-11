package com.salesmanager.shop.admin.controller.products;

import com.salesmanager.core.business.services.catalog.category.CategoryService;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.relationship.ProductRelationshipService;
import com.salesmanager.core.business.utils.ajax.AjaxPageableResponse;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.description.ProductDescription;
import com.salesmanager.core.model.catalog.product.relationship.ProductRelationship;
import com.salesmanager.core.model.catalog.product.relationship.ProductRelationshipType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.admin.model.web.Menu;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.utils.CategoryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Controller
public class FeaturedItemsController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FeaturedItemsController.class);
	
	@Inject
	CategoryService categoryService;
	
	@Inject
	ProductService productService;
	
	@Inject
	ProductRelationshipService productRelationshipService;
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/catalogue/featured/list.html", method=RequestMethod.GET)
	public String displayFeaturedItems(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		System.out.println("$#6085#"); setMenu(model,request);
		
		Language language = (Language)request.getAttribute("LANGUAGE");
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		List<Category> categories = categoryService.listByStore(store,language);
		List<com.salesmanager.shop.admin.model.catalog.Category> readableCategories = CategoryUtils.readableCategoryListConverter(categories, language);
		
		model.addAttribute("categories", readableCategories);
		System.out.println("$#6086#"); return "admin-catalogue-featured";
		
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/catalogue/featured/paging.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> pageProducts(HttpServletRequest request, HttpServletResponse response) {
		
		
		AjaxResponse resp = new AjaxResponse();
		
		try {
			

			
			Language language = (Language)request.getAttribute("LANGUAGE");
			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			

			List<ProductRelationship> relationships = productRelationshipService.getByType(store, ProductRelationshipType.FEATURED_ITEM, language);
			
			for(ProductRelationship relationship : relationships) {
				
				Product product = relationship.getRelatedProduct();
				Map entry = new HashMap();
				entry.put("relationshipId", relationship.getId());
				entry.put("productId", product.getId());
				
				ProductDescription description = product.getDescriptions().iterator().next();
				Set<ProductDescription> descriptions = product.getDescriptions();
				for(ProductDescription desc : descriptions) {
					System.out.println("$#6087#"); if(desc.getLanguage().getId().intValue()==language.getId().intValue()) {
						description = desc;
					}
				}
				
				entry.put("name", description.getName());
				entry.put("sku", product.getSku());
				entry.put("available", product.isAvailable());
				System.out.println("$#6088#"); resp.addDataEntry(entry);
				
			}
			

			System.out.println("$#6089#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_SUCCESS);
		
		} catch (Exception e) {
			LOGGER.error("Error while paging products", e);
			System.out.println("$#6090#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6091#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#6092#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		System.out.println("$#6093#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);


	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/catalogue/featured/addItem.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> addItem(HttpServletRequest request, HttpServletResponse response) {
		
		String productId = request.getParameter("productId");
		AjaxResponse resp = new AjaxResponse();
		
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#6094#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		
		try {
			

			Long lProductId = Long.parseLong(productId);

			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			
			Product product = productService.getById(lProductId);
			
			System.out.println("$#6095#"); if(product==null) {
				System.out.println("$#6096#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#6097#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#6098#"); if(product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#6099#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#6100#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}


			ProductRelationship relationship = new ProductRelationship();
			System.out.println("$#6101#"); relationship.setActive(true);
			System.out.println("$#6102#"); relationship.setCode(ProductRelationshipType.FEATURED_ITEM.name());
			System.out.println("$#6103#"); relationship.setStore(store);
			System.out.println("$#6104#"); relationship.setRelatedProduct(product);
			
			System.out.println("$#6105#"); productRelationshipService.saveOrUpdate(relationship);
			

			System.out.println("$#6106#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_SUCCESS);
		
		} catch (Exception e) {
			LOGGER.error("Error while paging products", e);
			System.out.println("$#6107#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6108#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#6109#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		
	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/catalogue/featured/removeItem.html&removeEntity=FEATURED", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> removeItem(HttpServletRequest request, HttpServletResponse response) {
		
		String productId = request.getParameter("productId");
		AjaxResponse resp = new AjaxResponse();
		
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#6110#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		try {
			

			Long lproductId = Long.parseLong(productId);

			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			
			Product product = productService.getById(lproductId);
			
			System.out.println("$#6111#"); if(product==null) {
				System.out.println("$#6112#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#6113#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#6114#"); if(product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#6115#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#6116#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			
			ProductRelationship relationship = null;
			List<ProductRelationship> relationships = productRelationshipService.getByType(store, ProductRelationshipType.FEATURED_ITEM);
			
			for(ProductRelationship r : relationships) {
				System.out.println("$#6117#"); if(r.getRelatedProduct().getId().longValue()==lproductId.longValue()) {
					relationship = r;
				}
			}
			
			System.out.println("$#6118#"); if(relationship==null) {
				System.out.println("$#6119#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#6120#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#6121#"); if(relationship.getStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#6122#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#6123#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}


			
			
			System.out.println("$#6124#"); productRelationshipService.delete(relationship);
			

			System.out.println("$#6125#"); resp.setStatus(AjaxPageableResponse.RESPONSE_OPERATION_COMPLETED);
		
		} catch (Exception e) {
			LOGGER.error("Error while paging products", e);
			System.out.println("$#6126#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6127#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#6128#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		
	}
	
	
	private void setMenu(Model model, HttpServletRequest request) throws Exception {
		
		//display menu
		Map<String,String> activeMenus = new HashMap<String,String>();
		activeMenus.put("catalogue", "catalogue");
		activeMenus.put("catalogue-products-group", "catalogue-products-group");
		
		@SuppressWarnings("unchecked")
		Map<String, Menu> menus = (Map<String, Menu>)request.getAttribute("MENUMAP");
		
		Menu currentMenu = (Menu)menus.get("catalogue");
		model.addAttribute("currentMenu",currentMenu);
		model.addAttribute("activeMenus",activeMenus);
		//
		
	}

}
