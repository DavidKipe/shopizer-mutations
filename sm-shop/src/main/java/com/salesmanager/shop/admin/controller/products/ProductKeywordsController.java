package com.salesmanager.shop.admin.controller.products;

import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.utils.ajax.AjaxPageableResponse;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.description.ProductDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.admin.controller.ControllerConstants;
import com.salesmanager.shop.admin.model.catalog.Keyword;
import com.salesmanager.shop.admin.model.web.Menu;
import com.salesmanager.shop.constants.Constants;
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
import javax.validation.Valid;
import java.util.*;

@Controller
public class ProductKeywordsController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductKeywordsController.class);
	
	@Inject
	private ProductService productService;
	
	@Inject
	LabelUtils messages;
	

	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value={"/admin/products/product/keywords.html"}, method=RequestMethod.GET)
	public String displayKeywords(@RequestParam("id") long productId, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		System.out.println("$#6836#"); this.setMenu(model, request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);

		Product product = productService.getById(productId);
		
		System.out.println("$#6837#"); if(product==null || product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
			System.out.println("$#6839#"); return "redirect:/admin/products/products.html";
		}
		
		model.addAttribute("store", store);
		model.addAttribute("product", product);
		model.addAttribute("productKeyword", new Keyword());

		System.out.println("$#6840#"); return ControllerConstants.Tiles.Product.productKeywords;
		
	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/product/addKeyword.html", method=RequestMethod.POST)
	public String addKeyword(@Valid @ModelAttribute("productKeyword") Keyword keyword, final BindingResult bindingResult,final Model model, final HttpServletRequest request, Locale locale) throws Exception{
		System.out.println("$#6841#"); this.setMenu(model, request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);

		
		Product product = productService.getById(keyword.getProductId());
		
		model.addAttribute("store", store);
		model.addAttribute("product", product);
		model.addAttribute("productKeyword", new Keyword());
		
		System.out.println("$#6842#"); if(product==null || product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
			System.out.println("$#6844#"); return "redirect:/admin/products/products.html";
		}
		
		Set<ProductDescription> descriptions = product.getDescriptions();
		ProductDescription productDescription = null;
		for(ProductDescription description : descriptions) {
			
			System.out.println("$#6845#"); if(description.getLanguage().getCode().equals(keyword.getLanguageCode())) {
				productDescription = description;
				break;
			}
			
		}
		
		System.out.println("$#6846#"); if(productDescription==null) {
			FieldError error = new FieldError("keyword","keyword",messages.getMessage("message.product.language", locale));
			System.out.println("$#6847#"); bindingResult.addError(error);
			System.out.println("$#6848#"); return ControllerConstants.Tiles.Product.productKeywords;
		}
		
		
		String keywords = productDescription.getMetatagKeywords();
		List<String> keyWordsList = null;
		System.out.println("$#6849#"); if(!StringUtils.isBlank(keywords)) {
			String[] splits = keywords.split(",");
			keyWordsList = new ArrayList(Arrays.asList(splits));
		}
		
		System.out.println("$#6850#"); if(keyWordsList==null) {
			keyWordsList = new ArrayList<String>();
		}
		keyWordsList.add(keyword.getKeyword());
		
		StringBuilder kwString = new StringBuilder();
		for(String s : keyWordsList) {
			kwString.append(s).append(",");
		}
		
		System.out.println("$#6851#"); productDescription.setMetatagKeywords(kwString.toString());
		Set<ProductDescription> updatedDescriptions = new HashSet<ProductDescription>();
		for(ProductDescription description : descriptions) {
			
			System.out.println("$#6852#"); if(!description.getLanguage().getCode().equals(keyword.getLanguageCode())) {
				updatedDescriptions.add(description);
			}
		}
		
		updatedDescriptions.add(productDescription);
		System.out.println("$#6853#"); product.setDescriptions(updatedDescriptions);
		
		System.out.println("$#6854#"); productService.update(product);
		model.addAttribute("success","success");

		
								System.out.println("$#6855#"); return ControllerConstants.Tiles.Product.productKeywords;
	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/product/removeKeyword.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> removeKeyword(@RequestParam("id") long productId, HttpServletRequest request, HttpServletResponse response, Locale locale) {

		
		String code = request.getParameter("code");
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#6856#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		
		try {
			
			//parse code i,lang (0,en)
			String ids[] = code.split(",");
			
			String languageCode = ids[1];
			
			int index = Integer.parseInt(ids[0]);
			
			Product product = productService.getById(productId);

			
			System.out.println("$#6857#"); if(product==null) {
				System.out.println("$#6858#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				System.out.println("$#6859#"); resp.setErrorString("Product id is not valid");
				String returnString = resp.toJSONString();
				System.out.println("$#6860#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#6861#"); if(product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#6862#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				System.out.println("$#6863#"); resp.setErrorString("Product id is not valid");
				String returnString = resp.toJSONString();
				System.out.println("$#6864#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			Set<ProductDescription> descriptions = product.getDescriptions();
			Set<ProductDescription> editedDescriptions = new HashSet<ProductDescription>();
			for(ProductDescription description : descriptions) {

				Language lang = description.getLanguage();
				System.out.println("$#6865#"); if(!lang.getCode().equals(languageCode)){
					editedDescriptions.add(description);
					continue;
				}

				List<String> keyWordsList = new ArrayList<String>();
	
				
				String keywords = description.getMetatagKeywords();
				System.out.println("$#6866#"); if(!StringUtils.isBlank(keywords)) {
					String splitKeywords[] = keywords.split(",");
					System.out.println("$#6868#"); System.out.println("$#6867#"); for(int i = 0; i < splitKeywords.length; i++) {
						
						System.out.println("$#6869#"); if(i!=index) {
							keyWordsList.add(splitKeywords[i]);
						}
						
						
					}
				}
				

				
				
				StringBuilder kwString = new StringBuilder();
				for(String s : keyWordsList) {
					kwString.append(s).append(",");
				}
				
				System.out.println("$#6870#"); description.setMetatagKeywords(kwString.toString());
				editedDescriptions.add(description);
				
			}
			
			System.out.println("$#6871#"); product.setDescriptions(editedDescriptions);
			System.out.println("$#6872#"); productService.update(product);
			System.out.println("$#6873#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);
		
		} catch (Exception e) {
			LOGGER.error("Error while deleting product", e);
			System.out.println("$#6874#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6875#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#6876#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	
	
	@SuppressWarnings("unchecked")
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/product/keywords/paging.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> pageKeywords(HttpServletRequest request, HttpServletResponse response) {
		
		String sProductId = request.getParameter("id");
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);

		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#6877#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		Long productId;
		Product product = null;
		
		try {
			productId = Long.parseLong(sProductId);
		} catch (Exception e) {
			System.out.println("$#6878#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6879#"); resp.setErrorString("Product id is not valid");
			String returnString = resp.toJSONString();
			System.out.println("$#6880#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		}

		
		try {

			product = productService.getById(productId);

			
			System.out.println("$#6881#"); if(product==null) {
				System.out.println("$#6882#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				System.out.println("$#6883#"); resp.setErrorString("Product id is not valid");
				String returnString = resp.toJSONString();
				System.out.println("$#6884#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#6885#"); if(product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#6886#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				System.out.println("$#6887#"); resp.setErrorString("Product id is not valid");
				String returnString = resp.toJSONString();
				System.out.println("$#6888#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			
			@SuppressWarnings("rawtypes")
			

			Set<ProductDescription> descriptions = product.getDescriptions();

			for(ProductDescription description : descriptions) {
				
				
				Language lang = description.getLanguage();
				
				
				String keywords = description.getMetatagKeywords();
				System.out.println("$#6889#"); if(!StringUtils.isBlank(keywords)) {
					
					String splitKeywords[] = keywords.split(",");
					System.out.println("$#6891#"); System.out.println("$#6890#"); for(int i = 0; i < splitKeywords.length; i++) {
						Map entry = new HashMap();
						entry.put("language", lang.getCode());
						String keyword = splitKeywords[i];
						StringBuilder code = new StringBuilder();
						code.append(i).append(",").append(lang.getCode());
						
						entry.put("code", code.toString());
						entry.put("keyword", keyword);
						System.out.println("$#6892#"); resp.addDataEntry(entry);
	
						
					}
					
				}
				
			}

			

			System.out.println("$#6893#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_SUCCESS);
		
		} catch (Exception e) {
			LOGGER.error("Error while paging products", e);
			System.out.println("$#6894#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6895#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#6896#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
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
