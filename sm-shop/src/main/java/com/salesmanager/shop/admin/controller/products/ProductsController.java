package com.salesmanager.shop.admin.controller.products;

import com.salesmanager.core.business.services.catalog.category.CategoryService;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.utils.ajax.AjaxPageableResponse;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.ProductCriteria;
import com.salesmanager.core.model.catalog.product.ProductList;
import com.salesmanager.core.model.catalog.product.description.ProductDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.admin.model.web.Menu;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.utils.CategoryUtils;
import com.salesmanager.shop.utils.LabelUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.*;

@Controller
public class ProductsController {
	
	@Inject
	CategoryService categoryService;
	
	@Inject
	ProductService productService;
	
	@Inject
	LabelUtils messages;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductsController.class);
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/products.html", method=RequestMethod.GET)
	public String displayProducts(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		System.out.println("$#7036#"); setMenu(model,request);
		
		Language language = (Language)request.getAttribute("LANGUAGE");
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		List<Category> categories = categoryService.listByStore(store, language);
		
		List<com.salesmanager.shop.admin.model.catalog.Category> readableCategories = CategoryUtils.readableCategoryListConverter(categories, language);
		
		model.addAttribute("categories", readableCategories);
		
		System.out.println("$#7037#"); return "admin-products";
		
	}

	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/paging.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> pageProducts(HttpServletRequest request, HttpServletResponse response) {
		
		//TODO what if ROOT
		
		String categoryId = request.getParameter("categoryId");
		String sku = request.getParameter("sku");
		String available = request.getParameter("available");
		String searchTerm = request.getParameter("searchTerm");
		String name = request.getParameter("name");
		
		AjaxPageableResponse resp = new AjaxPageableResponse();
		
		try {
			
		
			int startRow = Integer.parseInt(request.getParameter("_startRow"));
			int endRow = Integer.parseInt(request.getParameter("_endRow"));
			
			Language language = (Language)request.getAttribute("LANGUAGE");
			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			
			ProductCriteria criteria = new ProductCriteria();
			
			System.out.println("$#7038#"); criteria.setStartIndex(startRow);
			System.out.println("$#7039#"); criteria.setMaxCount(endRow);
			
			
			System.out.println("$#7040#"); if(!StringUtils.isBlank(categoryId) && !categoryId.equals("-1")) {
				
				//get other filters
				Long lcategoryId = 0L;
				try {
					lcategoryId = Long.parseLong(categoryId);
				} catch (Exception e) {
					LOGGER.error("Product page cannot parse categoryId " + categoryId );
					System.out.println("$#7042#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
					String returnString = resp.toJSONString();
					System.out.println("$#7043#"); return new ResponseEntity<String>(returnString,HttpStatus.BAD_REQUEST);
				} 
				
				

				System.out.println("$#7045#"); System.out.println("$#7044#"); if(lcategoryId>0) {
				
					Category category = categoryService.getById(lcategoryId, store.getId());
	
					System.out.println("$#7046#"); if(category==null || category.getMerchantStore().getId()!=store.getId()) {
						System.out.println("$#7048#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
						String returnString = resp.toJSONString();
						System.out.println("$#7049#"); return new ResponseEntity<String>(returnString,HttpStatus.BAD_REQUEST);
					}
					
					//get all sub categories
					StringBuilder lineage = new StringBuilder();
					lineage.append(category.getLineage()).append(category.getId()).append("/");
					
					List<Category> categories = categoryService.getListByLineage(store, lineage.toString());
					
					List<Long> categoryIds = new ArrayList<Long>();
					
					for(Category cat : categories) {
						categoryIds.add(cat.getId());
					}
					categoryIds.add(category.getId());
					System.out.println("$#7050#"); criteria.setCategoryIds(categoryIds);
				
				}
				


				
			}
			
			System.out.println("$#7051#"); if(!StringUtils.isBlank(sku)) {
				System.out.println("$#7052#"); criteria.setCode(sku);
			}
			
			System.out.println("$#7053#"); if(!StringUtils.isBlank(name)) {
				System.out.println("$#7054#"); criteria.setProductName(name);
			}
			
			System.out.println("$#7055#"); if(!StringUtils.isBlank(available)) {
				System.out.println("$#7056#"); if(available.equals("true")) {
					System.out.println("$#7057#"); criteria.setAvailable(new Boolean(true));
				} else {
					System.out.println("$#7058#"); criteria.setAvailable(new Boolean(false));
				}
			}
			
			ProductList productList = productService.listByStore(store, language, criteria);
			System.out.println("$#7059#"); resp.setEndRow(productList.getTotalCount());
			System.out.println("$#7060#"); resp.setStartRow(startRow);
			List<Product> plist = productList.getProducts();
			
			System.out.println("$#7061#"); if(plist!=null) {
			
				for(Product product : plist) {
					
					Map entry = new HashMap();
					entry.put("productId", product.getId());
					
					ProductDescription description = product.getDescriptions().iterator().next();
					
					entry.put("name", description.getName());
					entry.put("sku", product.getSku());
					entry.put("available", product.isAvailable());
					System.out.println("$#7062#"); resp.addDataEntry(entry);
					
					
					
				}
			
			}

			System.out.println("$#7063#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_SUCCESS);
		
		} catch (Exception e) {
			LOGGER.error("Error while paging products", e);
			System.out.println("$#7064#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#7065#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#7066#"); return new ResponseEntity<String>(returnString,HttpStatus.OK);


	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/remove.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> deleteProduct(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		String sid = request.getParameter("productId");

		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		AjaxResponse resp = new AjaxResponse();

		
		try {
			
			Long id = Long.parseLong(sid);
			
			Product product = productService.getById(id);

			System.out.println("$#7067#"); if(product==null || product.getMerchantStore().getId()!=store.getId()) {

				System.out.println("$#7069#"); resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
				System.out.println("$#7070#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				
			} else {
				
				System.out.println("$#7071#"); productService.delete(product);
				System.out.println("$#7072#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);
				
			}
		
		
		} catch (Exception e) {
			LOGGER.error("Error while deleting product", e);
			System.out.println("$#7073#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#7074#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#7075#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		System.out.println("$#7076#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
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
