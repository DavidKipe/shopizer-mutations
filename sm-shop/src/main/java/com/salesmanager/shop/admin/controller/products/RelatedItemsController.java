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
import com.salesmanager.shop.admin.controller.ControllerConstants;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Controller
public class RelatedItemsController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RelatedItemsController.class);
	
	@Inject
	CategoryService categoryService;
	
	@Inject
	ProductService productService;
	
	@Inject
	ProductRelationshipService productRelationshipService;
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/catalogue/related/list.html", method=RequestMethod.GET)
	public String displayRelatedItems(@RequestParam("id") long productId, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		System.out.println("$#7077#"); setMenu(model,request);
		
		Language language = (Language)request.getAttribute("LANGUAGE");
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		//get the product and validate it belongs to the current merchant
		Product product = productService.getById(productId);
		
		System.out.println("$#7078#"); if(product==null) {
			System.out.println("$#7079#"); return "redirect:/admin/products/products.html";
		}
		
		System.out.println("$#7080#"); if(product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
			System.out.println("$#7081#"); return "redirect:/admin/products/products.html";
		}
		
		
		List<Category> categories = categoryService.listByStore(store,language);
		List<com.salesmanager.shop.admin.model.catalog.Category> readableCategories = CategoryUtils.readableCategoryListConverter(categories, language);
        
		
		model.addAttribute("categories", readableCategories);
		model.addAttribute("product", product);
		System.out.println("$#7082#"); return ControllerConstants.Tiles.Product.relatedItems;
		
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/catalogue/related/paging.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> pageRelatedItems(HttpServletRequest request, HttpServletResponse response) {
		
		String sProductId = request.getParameter("productId");
		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#7083#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		try {
			

			
			Long productId = Long.parseLong(sProductId);
			Product product = productService.getById(productId);
			
			Language language = (Language)request.getAttribute("LANGUAGE");
			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			
			
			System.out.println("$#7084#"); if(product==null || product.getMerchantStore().getId().intValue()!= store.getId().intValue()) {
				System.out.println("$#7086#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				System.out.println("$#7087#"); resp.setErrorString("Product id is not valid");
				String returnString = resp.toJSONString();
				System.out.println("$#7088#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			

			List<ProductRelationship> relationships = productRelationshipService.getByType(store, product, ProductRelationshipType.RELATED_ITEM, language);
			
			for(ProductRelationship relationship : relationships) {
				
				Product relatedProduct = relationship.getRelatedProduct();
				Map entry = new HashMap();
				entry.put("relationshipId", relationship.getId());
				entry.put("productId", relatedProduct.getId());
				
				ProductDescription description = relatedProduct.getDescriptions().iterator().next();
				Set<ProductDescription> descriptions = relatedProduct.getDescriptions();
				for(ProductDescription desc : descriptions) {
					System.out.println("$#7089#"); if(desc.getLanguage().getId().intValue()==language.getId().intValue()) {
						description = desc;
					}
				}
				

				entry.put("name", description.getName());
				entry.put("sku", relatedProduct.getSku());
				entry.put("available", relatedProduct.isAvailable());
				System.out.println("$#7090#"); resp.addDataEntry(entry);
				
			}
			

			System.out.println("$#7091#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_SUCCESS);
		
		} catch (Exception e) {
			LOGGER.error("Error while paging products", e);
			System.out.println("$#7092#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#7093#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#7094#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);


	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/catalogue/related/addItem.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> addItem(HttpServletRequest request, HttpServletResponse response) {
		
		String productId = request.getParameter("productId");
		String baseProductId = request.getParameter("baseProductId");
		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#7095#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		try {
			

			Long lProductId = Long.parseLong(productId);
			Long lBaseProductId = Long.parseLong(baseProductId);

			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			
			Product product = productService.getById(lProductId);
			
			System.out.println("$#7096#"); if(product==null) {
				System.out.println("$#7097#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#7098#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#7099#"); if(product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#7100#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#7101#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			Product baseProduct = productService.getById(lBaseProductId);
			
			System.out.println("$#7102#"); if(baseProduct==null) {
				System.out.println("$#7103#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#7104#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#7105#"); if(baseProduct.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#7106#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#7107#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}


			ProductRelationship relationship = new ProductRelationship();
			System.out.println("$#7108#"); relationship.setActive(true);
			System.out.println("$#7109#"); relationship.setProduct(baseProduct);
			System.out.println("$#7110#"); relationship.setCode(ProductRelationshipType.RELATED_ITEM.name());
			System.out.println("$#7111#"); relationship.setStore(store);
			System.out.println("$#7112#"); relationship.setRelatedProduct(product);
			
			System.out.println("$#7113#"); productRelationshipService.saveOrUpdate(relationship);
			

			System.out.println("$#7114#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_SUCCESS);
		
		} catch (Exception e) {
			LOGGER.error("Error while paging products", e);
			System.out.println("$#7115#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#7116#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#7117#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		
	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/catalogue/related/removeItem.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> removeItem(HttpServletRequest request, HttpServletResponse response) {
		
		String productId = request.getParameter("productId");
		String baseProductId = request.getParameter("baseProductId");
		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#7118#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		try {
			

			Long lproductId = Long.parseLong(productId);
			Long lBaseProductId = Long.parseLong(baseProductId);

			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			
			Product product = productService.getById(lproductId);
			
			System.out.println("$#7119#"); if(product==null) {
				System.out.println("$#7120#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#7121#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#7122#"); if(product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#7123#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#7124#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			Product baseProduct = productService.getById(lBaseProductId);
			
			System.out.println("$#7125#"); if(baseProduct==null) {
				System.out.println("$#7126#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#7127#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#7128#"); if(baseProduct.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#7129#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#7130#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			ProductRelationship relationship = null;
			List<ProductRelationship> relationships = productRelationshipService.getByType(store, baseProduct, ProductRelationshipType.RELATED_ITEM);
			
			for(ProductRelationship r : relationships) {
				System.out.println("$#7131#"); if(r.getRelatedProduct().getId().longValue()==lproductId.longValue()) {
					relationship = r;
					break;
				}
			}
			
			System.out.println("$#7132#"); if(relationship==null) {
				System.out.println("$#7133#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#7134#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#7135#"); if(relationship.getStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#7136#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#7137#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}


			
			
			System.out.println("$#7138#"); productRelationshipService.delete(relationship);
			

			System.out.println("$#7139#"); resp.setStatus(AjaxPageableResponse.RESPONSE_OPERATION_COMPLETED);
		
		} catch (Exception e) {
			LOGGER.error("Error while paging products", e);
			System.out.println("$#7140#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#7141#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#7142#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		
	}
	
	
	private void setMenu(Model model, HttpServletRequest request) throws Exception {
		
		//display menu
		Map<String,String> activeMenus = new HashMap<String,String>();
		activeMenus.put("catalogue", "catalogue");
		activeMenus.put("catalogue-products", "catalogue-products");
		
		@SuppressWarnings("unchecked")
		Map<String, Menu> menus = (Map<String, Menu>)request.getAttribute("MENUMAP");
		
		Menu currentMenu = (Menu)menus.get("catalogue");
		model.addAttribute("currentMenu",currentMenu);
		model.addAttribute("activeMenus",activeMenus);
		//
		
	}

}
