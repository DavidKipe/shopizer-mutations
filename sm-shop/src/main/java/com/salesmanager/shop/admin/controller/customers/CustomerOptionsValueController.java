package com.salesmanager.shop.admin.controller.customers;

import com.salesmanager.core.business.services.customer.attribute.CustomerOptionValueService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.customer.attribute.CustomerOptionValue;
import com.salesmanager.core.model.customer.attribute.CustomerOptionValueDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.admin.controller.ControllerConstants;
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
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

@Controller
public class CustomerOptionsValueController {
	
	@Inject
	LanguageService languageService;
	

	@Inject
	private CustomerOptionValueService customerOptionValueService;
	
	@Inject
	LabelUtils messages;

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerOptionsValueController.class);
	
	/**
	 * Displays the list of customer options values
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("hasRole('CUSTOMER')")
	@RequestMapping(value="/admin/customers/options/values/list.html", method=RequestMethod.GET)
	public String displayOptionValues(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		System.out.println("$#5496#"); setMenu(model,request);
		System.out.println("$#5497#"); return ControllerConstants.Tiles.Customer.optionsValuesList;
		
		
		
	}
	
	/**
	 * Display an option value in edit mode
	 * @param id
	 * @param request
	 * @param response
	 * @param model
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("hasRole('CUSTOMER')")
	@RequestMapping(value="/admin/customers/options/values/edit.html", method=RequestMethod.GET)
	public String displayOptionValueEdit(@RequestParam("id") long id, HttpServletRequest request, HttpServletResponse response, Model model, Locale locale) throws Exception {
		System.out.println("$#5498#"); return displayOption(id,request,response,model,locale);
	}
	
	@PreAuthorize("hasRole('CUSTOMER')")
	@RequestMapping(value="/admin/customers/options/values/create.html", method=RequestMethod.GET)
	public String displayOptionValueCreate(HttpServletRequest request, HttpServletResponse response, Model model, Locale locale) throws Exception {
		System.out.println("$#5499#"); return displayOption(null,request,response,model,locale);
	}
	
	private String displayOption(Long id, HttpServletRequest request, HttpServletResponse response,Model model,Locale locale) throws Exception {

		System.out.println("$#5500#"); this.setMenu(model, request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		List<Language> languages = store.getLanguages();

		Set<CustomerOptionValueDescription> descriptions = new HashSet<CustomerOptionValueDescription>();
		CustomerOptionValue option = new CustomerOptionValue();
		
		System.out.println("$#5501#"); if(id!=null && id!=0) {//edit mode
			
			
			option = customerOptionValueService.getById(id);
			
			
			System.out.println("$#5503#"); if(option==null) {
				System.out.println("$#5504#"); return "redirect:/admin/customers/options/values/list.html";
			}
			
			System.out.println("$#5505#"); if(option.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#5506#"); return "redirect:/admin/customers/options/values/list.html";
			}
			
			Set<CustomerOptionValueDescription> optionDescriptions = option.getDescriptions();

			for(Language l : languages) {
			
				CustomerOptionValueDescription optionDescription = null;
				
				System.out.println("$#5507#"); if(optionDescriptions!=null) {
					for(CustomerOptionValueDescription description : optionDescriptions) {
						String code = description.getLanguage().getCode();
						System.out.println("$#5508#"); if(code.equals(l.getCode())) {
							optionDescription = description;
						}
					}
				}
				
				System.out.println("$#5509#"); if(optionDescription==null) {
					optionDescription = new CustomerOptionValueDescription();
					System.out.println("$#5510#"); optionDescription.setLanguage(l);
				}
				
				descriptions.add(optionDescription);
			}

		} else {
			
			for(Language l : languages) {
				CustomerOptionValueDescription desc = new CustomerOptionValueDescription();
				System.out.println("$#5511#"); desc.setLanguage(l);
				descriptions.add(desc);
			}
			
			System.out.println("$#5512#"); option.setDescriptions(descriptions);
		}
		

		
		model.addAttribute("optionValue", option);
		System.out.println("$#5513#"); return ControllerConstants.Tiles.Customer.optionsValueDetails;
		
		
	}
		
	
	@PreAuthorize("hasRole('CUSTOMER')")
	@RequestMapping(value="/admin/customers/options/values/save.html", method=RequestMethod.POST)
	public String saveOption(@Valid @ModelAttribute("optionValue") CustomerOptionValue optionValue, BindingResult result, Model model, HttpServletRequest request, Locale locale) throws Exception {
		

		//display menu
		System.out.println("$#5514#"); setMenu(model,request);
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		CustomerOptionValue dbEntity =	null;	

		System.out.println("$#5516#"); System.out.println("$#5515#"); if(optionValue.getId() != null && optionValue.getId() >0) { //edit entry
			
			//get from DB
			dbEntity = customerOptionValueService.getById(optionValue.getId());
			
			System.out.println("$#5518#"); if(dbEntity==null) {
				System.out.println("$#5519#"); return "redirect:/admin/customers/options/values/list.html";
			}
			
			System.out.println("$#5520#"); if(dbEntity.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#5521#"); return "redirect:/admin/customers/options/values/list.html";
			}
		}
		
		//validate if it contains an existing code
		CustomerOptionValue byCode = customerOptionValueService.getByCode(store, optionValue.getCode());
		System.out.println("$#5522#"); if(byCode!=null && optionValue.getId()==null) {
			ObjectError error = new ObjectError("code",messages.getMessage("message.code.exist", locale));
			System.out.println("$#5524#"); result.addError(error);
		}

			
		Map<String,Language> langs = languageService.getLanguagesMap();
			

		List<CustomerOptionValueDescription> descriptions = optionValue.getDescriptionsList();
		System.out.println("$#5526#"); System.out.println("$#5525#"); if(descriptions!=null && descriptions.size()>0) {
			
				Set<CustomerOptionValueDescription> descs = new HashSet<CustomerOptionValueDescription>();
					System.out.println("$#5528#"); optionValue.setDescriptions(descs);
					for(CustomerOptionValueDescription description : descriptions) {
						
						System.out.println("$#5529#"); if(StringUtils.isBlank(description.getName())) {
							ObjectError error = new ObjectError("name",messages.getMessage("message.name.required", locale));
							System.out.println("$#5530#"); result.addError(error);
						} else {
							String code = description.getLanguage().getCode();
							Language l = langs.get(code);
							System.out.println("$#5531#"); description.setLanguage(l);
							System.out.println("$#5532#"); description.setCustomerOptionValue(optionValue);
							descs.add(description);
						}	
					}

		} else {
			
			ObjectError error = new ObjectError("name",messages.getMessage("message.name.required", locale));
			System.out.println("$#5533#"); result.addError(error);
			
		}
			

		System.out.println("$#5534#"); optionValue.setMerchantStore(store);

		
		System.out.println("$#5535#"); if (result.hasErrors()) {
			System.out.println("$#5536#"); return ControllerConstants.Tiles.Customer.optionsValueDetails;
		}
		

		System.out.println("$#5537#"); customerOptionValueService.saveOrUpdate(optionValue);

		model.addAttribute("success","success");
		System.out.println("$#5538#"); return ControllerConstants.Tiles.Customer.optionsValueDetails;
	}

	
	
	@SuppressWarnings("unchecked")
	@PreAuthorize("hasRole('CUSTOMER')")
	@RequestMapping(value="/admin/customers/options/values/paging.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> pageOptions(HttpServletRequest request, HttpServletResponse response) {

		AjaxResponse resp = new AjaxResponse();

		
		try {
			
			
			Language language = (Language)request.getAttribute("LANGUAGE");	
		
			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			
			List<CustomerOptionValue> options = null;
					
	
			options = customerOptionValueService.listByStore(store, language);

			for(CustomerOptionValue option : options) {
				
				@SuppressWarnings("rawtypes")
				Map entry = new HashMap();
				entry.put("id", option.getId());
				entry.put("code", option.getCode());
				CustomerOptionValueDescription description = option.getDescriptions().iterator().next();
				
				entry.put("name", description.getName());
				System.out.println("$#5539#"); resp.addDataEntry(entry);
				
				
			}
			
			System.out.println("$#5540#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_SUCCESS);
			

		
		} catch (Exception e) {
			LOGGER.error("Error while paging options", e);
			System.out.println("$#5541#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
		}
		
		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#5542#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		System.out.println("$#5543#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		
		
	}
	
	@PreAuthorize("hasRole('CUSTOMER')")
	@RequestMapping(value="/admin/customers/options/values/remove.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> deleteOptionValue(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		String sid = request.getParameter("id");

		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		AjaxResponse resp = new AjaxResponse();

		
		try {
			
			Long id = Long.parseLong(sid);
			
			CustomerOptionValue entity = customerOptionValueService.getById(id);

			System.out.println("$#5544#"); if(entity==null || entity.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#5546#"); resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
				System.out.println("$#5547#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			} else {
				System.out.println("$#5548#"); customerOptionValueService.delete(entity);
				System.out.println("$#5549#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);
				
			}
		
		
		} catch (Exception e) {
			LOGGER.error("Error while deleting option", e);
			System.out.println("$#5550#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#5551#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#5552#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		System.out.println("$#5553#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	

	
	private void setMenu(Model model, HttpServletRequest request) throws Exception {
		
		//display menu
		Map<String,String> activeMenus = new HashMap<String,String>();
		activeMenus.put("customer", "customer");
		activeMenus.put("customer-options", "customer-options");
		
		@SuppressWarnings("unchecked")
		Map<String, Menu> menus = (Map<String, Menu>)request.getAttribute("MENUMAP");
		
		Menu currentMenu = (Menu)menus.get("customer");
		model.addAttribute("currentMenu",currentMenu);
		model.addAttribute("activeMenus",activeMenus);
		//
		
	}

}
