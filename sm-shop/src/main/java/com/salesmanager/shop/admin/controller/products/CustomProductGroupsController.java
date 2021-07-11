package com.salesmanager.shop.admin.controller.products;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesmanager.core.business.services.catalog.category.CategoryService;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.relationship.ProductRelationshipService;
import com.salesmanager.core.business.utils.ajax.AjaxPageableResponse;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.description.ProductDescription;
import com.salesmanager.core.model.catalog.product.relationship.ProductRelationship;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.admin.controller.ControllerConstants;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


@Controller
public class CustomProductGroupsController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomProductGroupsController.class);
	
	@Inject
	CategoryService categoryService;
	
	@Inject
	ProductService productService;
	
	@Inject
	ProductRelationshipService productRelationshipService;
	
	@Inject
	LabelUtils messages;
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/groups/list.html", method=RequestMethod.GET)
	public String displayProductGroups(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		System.out.println("$#5973#"); setMenu(model,request);
		
		Language language = (Language)request.getAttribute("LANGUAGE");
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		ProductRelationship group = new ProductRelationship();
		
		
		model.addAttribute("group", group);

		System.out.println("$#5974#"); return ControllerConstants.Tiles.Product.customGroups;
		
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/groups/paging.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> pageCustomGroups(HttpServletRequest request, HttpServletResponse response) {
		
		
		AjaxResponse resp = new AjaxResponse();
		
		try {

			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			

			List<ProductRelationship> relationships = productRelationshipService.getGroups(store);
			
			for(ProductRelationship relationship : relationships) {
				
				System.out.println("$#5975#"); if(!"FEATURED_ITEM".equals(relationship.getCode())) {//do not add featured items

					Map entry = new HashMap();
					entry.put("code", relationship.getCode());
					entry.put("active", relationship.isActive());
	
					System.out.println("$#5976#"); resp.addDataEntry(entry);
				
				}
				
			}
			

			System.out.println("$#5977#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_SUCCESS);
		
		} catch (Exception e) {
			LOGGER.error("Error while paging products", e);
			System.out.println("$#5978#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#5979#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#5980#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		System.out.println("$#5981#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);


	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/groups/save.html", method=RequestMethod.POST)
	public String saveCustomProductGroup(@ModelAttribute("group") ProductRelationship group, BindingResult result, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		
		System.out.println("$#5982#"); setMenu(model,request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		
		//check if group already exist
		
		
		System.out.println("$#5983#"); if(StringUtils.isBlank(group.getCode())) {
			FieldError fieldError = new FieldError("group","code",group.getCode(),false,null,null,messages.getMessage("message.group.required",locale));
			System.out.println("$#5984#"); result.addError(fieldError);
			System.out.println("$#5985#"); return ControllerConstants.Tiles.Product.customGroups;
		}
		
		//String msg = messages.getMessage("message.group.alerady.exists",locale);
		//String[] messages = {msg};
		
		String[] messages = {"message.group.alerady.exists"};
		
		List<ProductRelationship> groups = productRelationshipService.getGroups(store);
		for(ProductRelationship grp : groups) {
			System.out.println("$#5986#"); if(grp.getCode().equalsIgnoreCase(group.getCode())) {
				String[] args = {group.getCode()};
				FieldError fieldError = new FieldError("group","code",group.getCode(),false,messages,args,null);
				System.out.println("$#5987#"); result.addError(fieldError);
			}
		}
		
		System.out.println("$#5988#"); if(result.hasErrors()) {
			System.out.println("$#5989#"); return ControllerConstants.Tiles.Product.customGroups;
		}

		System.out.println("$#5990#"); group.setActive(true);
		System.out.println("$#5991#"); group.setStore(store);
		
		System.out.println("$#5992#"); productRelationshipService.addGroup(store,group.getCode());

		
		model.addAttribute("success","success");
		
		System.out.println("$#5993#"); return ControllerConstants.Tiles.Product.customGroups;
		
	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/groups/remove.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> removeCustomProductGroup(HttpServletRequest request, HttpServletResponse response) {
		
		String groupCode = request.getParameter("code");

		AjaxResponse resp = new AjaxResponse();


		try {
			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			System.out.println("$#5994#"); productRelationshipService.deleteGroup(store, groupCode);
			System.out.println("$#5995#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);

		} catch (Exception e) {
			LOGGER.error("Error while deleting a group", e);
			System.out.println("$#5996#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
		}

		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#5997#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		System.out.println("$#5998#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);

	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/groups/update.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> activateProductGroup(HttpServletRequest request, HttpServletResponse response) {
		String values = request.getParameter("_oldValues");
		String active = request.getParameter("active");
		

		AjaxResponse resp = new AjaxResponse();

		try {
			
			ObjectMapper mapper = new ObjectMapper();
			@SuppressWarnings("rawtypes")
			Map conf = mapper.readValue(values, Map.class);
			String groupCode = (String)conf.get("code");

			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			
			//get groups
			List<ProductRelationship> groups = productRelationshipService.getGroups(store);
			
			for(ProductRelationship relation : groups) {
				System.out.println("$#5999#"); if(relation.getCode().equals(groupCode)) {
					System.out.println("$#6000#"); if("true".equals(active)) {
						System.out.println("$#6001#"); relation.setActive(true);
					} else {
						System.out.println("$#6002#"); relation.setActive(false);
					}
					System.out.println("$#6003#"); productRelationshipService.saveOrUpdate(relation);
				}
			}
			System.out.println("$#6004#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);

		} catch (Exception e) {
			LOGGER.error("Error while updateing groups", e);
			System.out.println("$#6005#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
		}
		
		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#6006#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		System.out.println("$#6007#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/group/edit.html", method=RequestMethod.GET)
	public String displayCustomProductGroup(@RequestParam("id") String groupCode, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		System.out.println("$#6008#"); setMenu(model,request);
		
		Language language = (Language)request.getAttribute("LANGUAGE");
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		List<Category> categories = categoryService.listByStore(store,language);//for categories
		List<com.salesmanager.shop.admin.model.catalog.Category> readableCategories = CategoryUtils.readableCategoryListConverter(categories, language);
		
		model.addAttribute("group", groupCode);
		model.addAttribute("categories", readableCategories);
		System.out.println("$#6009#"); return ControllerConstants.Tiles.Product.customGroupsDetails;
		
	}
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/group/details/paging.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> pageProducts(HttpServletRequest request, HttpServletResponse response) {
		
		String code = request.getParameter("code");
		AjaxResponse resp = new AjaxResponse();
		
		try {
			

			
			Language language = (Language)request.getAttribute("LANGUAGE");
			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			

			List<ProductRelationship> relationships = productRelationshipService.getByGroup(store, code, language);
			
			for(ProductRelationship relationship : relationships) {
				
				Product product = relationship.getRelatedProduct();
				Map entry = new HashMap();
				entry.put("relationshipId", relationship.getId());
				entry.put("productId", product.getId());
				
				ProductDescription description = product.getDescriptions().iterator().next();
				Set<ProductDescription> descriptions = product.getDescriptions();
				for(ProductDescription desc : descriptions) {
					System.out.println("$#6010#"); if(desc.getLanguage().getId().intValue()==language.getId().intValue()) {
						description = desc;
					}
				}
				
				entry.put("name", description.getName());
				entry.put("sku", product.getSku());
				entry.put("available", product.isAvailable());
				System.out.println("$#6011#"); resp.addDataEntry(entry);
				
			}
			

			System.out.println("$#6012#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_SUCCESS);
		
		} catch (Exception e) {
			LOGGER.error("Error while paging products", e);
			System.out.println("$#6013#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6014#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#6015#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		System.out.println("$#6016#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);


	}
	
	
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/group/details/addItem.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> addItem(HttpServletRequest request, HttpServletResponse response) {
		
		String code = request.getParameter("code");
		String productId = request.getParameter("productId");
		AjaxResponse resp = new AjaxResponse();
		
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#6017#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		try {
			

			Long lProductId = Long.parseLong(productId);

			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			
			Product product = productService.getById(lProductId);
			
			System.out.println("$#6018#"); if(product==null) {
				System.out.println("$#6019#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#6020#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#6021#"); if(product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#6022#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#6023#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}


			ProductRelationship relationship = new ProductRelationship();
			System.out.println("$#6024#"); relationship.setActive(true);
			System.out.println("$#6025#"); relationship.setCode(code);
			System.out.println("$#6026#"); relationship.setStore(store);
			System.out.println("$#6027#"); relationship.setRelatedProduct(product);
			
			System.out.println("$#6028#"); productRelationshipService.saveOrUpdate(relationship);
			

			System.out.println("$#6029#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_SUCCESS);
		
		} catch (Exception e) {
			LOGGER.error("Error while paging products", e);
			System.out.println("$#6030#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6031#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#6032#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		
	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/group/details/removeItem.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> removeItem(HttpServletRequest request, HttpServletResponse response) {
		
		String code = request.getParameter("code");
		String productId = request.getParameter("productId");
		AjaxResponse resp = new AjaxResponse();
		
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#6033#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		try {
			

			Long lproductId = Long.parseLong(productId);

			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			
			Product product = productService.getById(lproductId);
			
			System.out.println("$#6034#"); if(product==null) {
				System.out.println("$#6035#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#6036#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#6037#"); if(product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#6038#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#6039#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			
			ProductRelationship relationship = null;
			List<ProductRelationship> relationships = productRelationshipService.getByGroup(store, code);
			
			for(ProductRelationship r : relationships) {
				System.out.println("$#6040#"); if(r.getRelatedProduct().getId().longValue()==lproductId.longValue()) {
					relationship = r;
					break;
				}
			}
			
			System.out.println("$#6041#"); if(relationship==null) {
				System.out.println("$#6042#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#6043#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#6044#"); if(relationship.getStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#6045#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#6046#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}


			
			
			System.out.println("$#6047#"); productRelationshipService.delete(relationship);
			

			System.out.println("$#6048#"); resp.setStatus(AjaxPageableResponse.RESPONSE_OPERATION_COMPLETED);
		
		} catch (Exception e) {
			LOGGER.error("Error while paging products", e);
			System.out.println("$#6049#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6050#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#6051#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		
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
