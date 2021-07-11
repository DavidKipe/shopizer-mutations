package com.salesmanager.shop.admin.controller.tax;

import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.tax.TaxClassService;
import com.salesmanager.core.business.utils.ajax.AjaxPageableResponse;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.tax.taxclass.TaxClass;
import com.salesmanager.shop.admin.model.web.Menu;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.utils.LabelUtils;
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
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
public class TaxClassController {
	
	@Inject
	private TaxClassService taxClassService = null;
	
	@Inject
	private ProductService productService=null;
	
	@Inject
	LabelUtils messages;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TaxClassController.class);

	
	@PreAuthorize("hasRole('TAX')")
	@RequestMapping(value={"/admin/tax/taxclass/list.html"}, method=RequestMethod.GET)
	public String displayTaxClasses(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		System.out.println("$#7375#"); setMenu(model, request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		TaxClass taxClass = new TaxClass();
		System.out.println("$#7376#"); taxClass.setMerchantStore(store);
		
		model.addAttribute("taxClass", taxClass);
		
		System.out.println("$#7377#"); return com.salesmanager.shop.admin.controller.ControllerConstants.Tiles.Tax.taxClasses;
	}
	
	
	@PreAuthorize("hasRole('TAX')")
	@RequestMapping(value = "/admin/tax/taxclass/paging.html", method = RequestMethod.POST)
	public @ResponseBody
	ResponseEntity<String> pageTaxClasses(HttpServletRequest request,
			HttpServletResponse response, Locale locale) {
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);

		AjaxResponse resp = new AjaxResponse();
		try {

				List<TaxClass> taxClasses = taxClassService.listByStore(store);
				for(TaxClass tax : taxClasses) {
					System.out.println("$#7378#"); if(!tax.getCode().equals(TaxClass.DEFAULT_TAX_CLASS)) {
						Map<String,String> entry = new HashMap<String,String>();
						entry.put("taxClassId", String.valueOf(tax.getId()));
						entry.put("code", tax.getCode());
						entry.put("name", tax.getTitle());
						System.out.println("$#7379#"); resp.addDataEntry(entry);
					}
				}

				System.out.println("$#7380#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_SUCCESS);
		
		} catch (Exception e) {
			LOGGER.error("Error while paging permissions", e);
			System.out.println("$#7381#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#7382#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#7383#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		System.out.println("$#7384#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('TAX')")
	@RequestMapping(value="/admin/tax/taxclass/save.html", method=RequestMethod.POST)
	public String saveTaxClass(@Valid @ModelAttribute("taxClass") TaxClass taxClass, BindingResult result, Model model, HttpServletRequest request, Locale locale) throws Exception {
		
		
		System.out.println("$#7385#"); setMenu(model, request);
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);

		
		//requires code and name
		System.out.println("$#7386#"); if(taxClass.getCode().equals(TaxClass.DEFAULT_TAX_CLASS)) {
			ObjectError error = new ObjectError("code",messages.getMessage("message.taxclass.alreadyexist", locale));
			System.out.println("$#7387#"); result.addError(error);
		}
		

		
		//check if the code already exist
		TaxClass taxClassDb = taxClassService.getByCode(taxClass.getCode(),store);
		
		System.out.println("$#7388#"); if(taxClassDb!=null) {
			ObjectError error = new ObjectError("code",messages.getMessage("message.taxclass.alreadyexist", locale));
			System.out.println("$#7389#"); result.addError(error);
		}
		
		System.out.println("$#7390#"); if (result.hasErrors()) {
			System.out.println("$#7391#"); return com.salesmanager.shop.admin.controller.ControllerConstants.Tiles.Tax.taxClasses;
		}
		
		System.out.println("$#7392#"); taxClassService.create(taxClass);
		
		model.addAttribute("success","success");
		
		System.out.println("$#7393#"); return com.salesmanager.shop.admin.controller.ControllerConstants.Tiles.Tax.taxClasses;
		
	}
	
	
	@PreAuthorize("hasRole('TAX')")
	@RequestMapping(value="/admin/tax/taxclass/update.html", method=RequestMethod.POST)
	public String updateTaxClass(@Valid @ModelAttribute("taxClass") TaxClass taxClass, BindingResult result, Model model, HttpServletRequest request, Locale locale) throws Exception {
		
		
		System.out.println("$#7394#"); setMenu(model, request);
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);

		
		//requires code and name
		System.out.println("$#7395#"); if(taxClass.getCode().equals(TaxClass.DEFAULT_TAX_CLASS)) {
			ObjectError error = new ObjectError("code",messages.getMessage("message.taxclass.alreadyexist", locale));
			System.out.println("$#7396#"); result.addError(error);
		}
		

		
		//check if the code already exist
		TaxClass taxClassDb = taxClassService.getByCode(taxClass.getCode(),store);
		
		System.out.println("$#7397#"); if(taxClassDb!=null && taxClassDb.getId().longValue()!=taxClass.getId().longValue()) {
			ObjectError error = new ObjectError("code",messages.getMessage("message.taxclass.alreadyexist", locale));
			System.out.println("$#7399#"); result.addError(error);
		}
		
		System.out.println("$#7400#"); if (result.hasErrors()) {
			System.out.println("$#7401#"); return com.salesmanager.shop.admin.controller.ControllerConstants.Tiles.Tax.taxClass;
		}
		
		System.out.println("$#7402#"); taxClassService.update(taxClass);
		
		model.addAttribute("success","success");
		
		System.out.println("$#7403#"); return com.salesmanager.shop.admin.controller.ControllerConstants.Tiles.Tax.taxClass;
		
	}
	
	
	@PreAuthorize("hasRole('TAX')")
	@RequestMapping(value="/admin/tax/taxclass/remove.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> removeTaxClass(HttpServletRequest request, Locale locale) throws Exception {
		
		//do not remove super admin
		
		String taxClassId = request.getParameter("taxClassId");

		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#7404#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		try {
			

			/**
			 * In order to remove a User the logged in ser must be ADMIN
			 * or SUPER_USER
			 */
			

			System.out.println("$#7405#"); if(taxClassId==null){
				System.out.println("$#7406#"); resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
				System.out.println("$#7407#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#7408#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			long lTaxClassId;
			try {
				lTaxClassId = Long.parseLong(taxClassId);
			} catch (Exception e) {
				LOGGER.error("Invalid taxClassId " + taxClassId);
				System.out.println("$#7409#"); resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
				System.out.println("$#7410#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#7411#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			TaxClass taxClass = taxClassService.getById(lTaxClassId);
			
			System.out.println("$#7412#"); if(taxClass==null) {
				LOGGER.error("Invalid taxClassId " + taxClassId);
				System.out.println("$#7413#"); resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
				System.out.println("$#7414#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#7415#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			//look if the taxclass is used for products
			List<Product> products = productService.listByTaxClass(taxClass);

			System.out.println("$#7417#"); System.out.println("$#7416#"); if(products!=null && products.size()>0) {
				System.out.println("$#7419#"); resp.setStatusMessage(messages.getMessage("message.product.association", locale));
				System.out.println("$#7420#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#7421#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			
			System.out.println("$#7422#"); taxClassService.delete(taxClass);
			
			System.out.println("$#7423#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);

		
		
		} catch (Exception e) {
			LOGGER.error("Error while deleting tax class", e);
			System.out.println("$#7424#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#7425#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#7426#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		
	}
	
	@PreAuthorize("hasRole('TAX')")
	@RequestMapping(value="/admin/tax/taxclass/edit.html", method=RequestMethod.GET)
	public String editTaxClass(@ModelAttribute("id") String id, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		
		System.out.println("$#7427#"); setMenu(model,request);

		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);

		TaxClass taxClass = null;
		try {
			Long taxClassId = Long.parseLong(id);
			taxClass = taxClassService.getById(taxClassId);
		} catch (Exception e) {
			LOGGER.error("Cannot parse taxclassid " + id);
			System.out.println("$#7428#"); return "redirect:/admin/tax/taxclass/list.html";
		}
		
		System.out.println("$#7429#"); if(taxClass==null || taxClass.getMerchantStore().getId()!=store.getId()) {
			System.out.println("$#7431#"); return "redirect:/admin/tax/taxclass/list.html";
		}
		
		
		
		
		model.addAttribute("taxClass", taxClass);
		
		System.out.println("$#7432#"); return com.salesmanager.shop.admin.controller.ControllerConstants.Tiles.Tax.taxClass;
		
		
		
	}


	
	private void setMenu(Model model, HttpServletRequest request)
	throws Exception {

		// display menu
		Map<String, String> activeMenus = new HashMap<String, String>();
		activeMenus.put("tax", "tax");
		activeMenus.put("taxclass", "taxclass");
		
		@SuppressWarnings("unchecked")
		Map<String, Menu> menus = (Map<String, Menu>) request
				.getAttribute("MENUMAP");
		
		Menu currentMenu = (Menu) menus.get("tax");
		model.addAttribute("currentMenu", currentMenu);
		model.addAttribute("activeMenus", activeMenus);
		//

	}

}
