package com.salesmanager.shop.admin.controller.customers;

import com.salesmanager.core.business.services.customer.attribute.CustomerOptionService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.customer.attribute.CustomerOption;
import com.salesmanager.core.model.customer.attribute.CustomerOptionDescription;
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
public class CustomerOptionsController {
	
	@Inject
	private LanguageService languageService;
	
	@Inject
	private CustomerOptionService customerOptionService;
	
	@Inject
	private LabelUtils messages;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerOptionsController.class);
	
	
	@PreAuthorize("hasRole('CUSTOMER')")
	@RequestMapping(value="/admin/customers/options/list.html", method=RequestMethod.GET)
	public String displayOptions(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		System.out.println("$#5395#"); setMenu(model,request);
		System.out.println("$#5396#"); return ControllerConstants.Tiles.Customer.optionsList;
		

	}
	
	@PreAuthorize("hasRole('CUSTOMER')")
	@RequestMapping(value="/admin/customers/options/edit.html", method=RequestMethod.GET)
	public String displayOptionEdit(@RequestParam("id") long id, HttpServletRequest request, HttpServletResponse response, Model model, Locale locale) throws Exception {
		System.out.println("$#5397#"); return displayOption(id,request,response,model,locale);
	}
	
	@PreAuthorize("hasRole('CUSTOMER')")
	@RequestMapping(value="/admin/customers/options/create.html", method=RequestMethod.GET)
	public String displayOptionCreate(HttpServletRequest request, HttpServletResponse response, Model model, Locale locale) throws Exception {
		System.out.println("$#5398#"); return displayOption(null,request,response,model,locale);
	}
	
	private String displayOption(Long optionId, HttpServletRequest request, HttpServletResponse response,Model model,Locale locale) throws Exception {

		
		System.out.println("$#5399#"); this.setMenu(model, request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		List<Language> languages = store.getLanguages();

		Set<CustomerOptionDescription> descriptions = new HashSet<CustomerOptionDescription>();
		
		CustomerOption option = new CustomerOption();
		
		System.out.println("$#5400#"); if(optionId!=null && optionId!=0) {//edit mode
			
			
			option = customerOptionService.getById(optionId);
			
			
			System.out.println("$#5402#"); if(option==null) {
				System.out.println("$#5403#"); return "redirect:/admin/customers/options/list.html";
			}
			
			System.out.println("$#5404#"); if(option.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#5405#"); return "redirect:/admin/customers/options/list.html";
			}
			
			Set<CustomerOptionDescription> optionDescriptions = option.getDescriptions();
			
			
			
			for(Language l : languages) {
			
				CustomerOptionDescription optionDescription = null;
				
				System.out.println("$#5406#"); if(optionDescriptions!=null) {
					
					for(CustomerOptionDescription description : optionDescriptions) {
						
						String code = description.getLanguage().getCode();
						System.out.println("$#5407#"); if(code.equals(l.getCode())) {
							optionDescription = description;
						}
					}
					
				}
				
				System.out.println("$#5408#"); if(optionDescription==null) {
					optionDescription = new CustomerOptionDescription();
					System.out.println("$#5409#"); optionDescription.setLanguage(l);
				}
				descriptions.add(optionDescription);
			}

		} else {
			for(Language l : languages) {
				CustomerOptionDescription desc = new CustomerOptionDescription();
				System.out.println("$#5410#"); desc.setLanguage(l);
				descriptions.add(desc);
			}
		}
		

		System.out.println("$#5411#"); option.setDescriptions(descriptions);
		model.addAttribute("option", option);
		System.out.println("$#5412#"); return ControllerConstants.Tiles.Customer.optionDetails;
		
		
	}
		
	
	@PreAuthorize("hasRole('CUSTOMER')")
	@RequestMapping(value="/admin/customers/options/save.html", method=RequestMethod.POST)
	public String saveOption(@Valid @ModelAttribute("option") CustomerOption option, BindingResult result, Model model, HttpServletRequest request, Locale locale) throws Exception {
		

		//display menu
		System.out.println("$#5413#"); setMenu(model,request);
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		CustomerOption dbEntity =	null;	

		System.out.println("$#5415#"); System.out.println("$#5414#"); if(option.getId() != null && option.getId() >0) { //edit entry
			
			//get from DB
			dbEntity = customerOptionService.getById(option.getId());
			
			System.out.println("$#5417#"); if(dbEntity==null) {
				System.out.println("$#5418#"); return "redirect:/admin/options/options.html";
			}
		}
		
		//validate if it contains an existing code
		CustomerOption byCode = customerOptionService.getByCode(store, option.getCode());
		System.out.println("$#5419#"); if(byCode!=null && option.getId()==null) {
			ObjectError error = new ObjectError("code",messages.getMessage("message.code.exist", locale));
			System.out.println("$#5421#"); result.addError(error);
		}

			
		Map<String,Language> langs = languageService.getLanguagesMap();
			

		List<CustomerOptionDescription> descriptions = option.getDescriptionsList();
		
		System.out.println("$#5422#"); if(descriptions!=null) {
				
				for(CustomerOptionDescription description : descriptions) {
					
					System.out.println("$#5423#"); if(StringUtils.isBlank(description.getName())) {
						ObjectError error = new ObjectError("name",messages.getMessage("message.name.required", locale));
						System.out.println("$#5424#"); result.addError(error);
					} else {
					
						String code = description.getLanguage().getCode();
						Language l = langs.get(code);
						System.out.println("$#5425#"); description.setLanguage(l);
						System.out.println("$#5426#"); description.setCustomerOption(option);
					
					}
	
				}
				
		}
			
		System.out.println("$#5427#"); option.setDescriptions(new HashSet<CustomerOptionDescription>(descriptions));
		System.out.println("$#5428#"); option.setMerchantStore(store);

		
		System.out.println("$#5429#"); if (result.hasErrors()) {
			System.out.println("$#5430#"); return ControllerConstants.Tiles.Customer.optionDetails;
		}
		

		
		
		System.out.println("$#5431#"); customerOptionService.saveOrUpdate(option);


		

		model.addAttribute("success","success");
		System.out.println("$#5432#"); return ControllerConstants.Tiles.Customer.optionDetails;
	}

	
	
	@SuppressWarnings("unchecked")
	@PreAuthorize("hasRole('CUSTOMER')")
	@RequestMapping(value="/admin/customers/options/paging.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> pageOptions(HttpServletRequest request, HttpServletResponse response) {

		AjaxResponse resp = new AjaxResponse();

		
		try {
			
			
			Language language = (Language)request.getAttribute("LANGUAGE");	
		
			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			
			List<CustomerOption> options = null;
					

				
			options = customerOptionService.listByStore(store, language);
				

					
					

			for(CustomerOption option : options) {
				
				@SuppressWarnings("rawtypes")
				Map entry = new HashMap();
				entry.put("id", option.getId());
				
				CustomerOptionDescription description = option.getDescriptions().iterator().next();
				
				entry.put("name", description.getName());
				entry.put("type", option.getCustomerOptionType());
				entry.put("active", option.isActive());
				entry.put("public", option.isPublicOption());
				System.out.println("$#5433#"); resp.addDataEntry(entry);
				
				
			}
			
			System.out.println("$#5434#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_SUCCESS);
			

		
		} catch (Exception e) {
			LOGGER.error("Error while paging options", e);
			System.out.println("$#5435#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
		}
		
		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#5436#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		System.out.println("$#5437#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		
		
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
	
	@PreAuthorize("hasRole('CUSTOMER')")
	@RequestMapping(value="/admin/customers/options/remove.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> deleteOption(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		String sid = request.getParameter("id");

		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		AjaxResponse resp = new AjaxResponse();

		
		try {
			
			Long id = Long.parseLong(sid);
			
			CustomerOption entity = customerOptionService.getById(id);

			System.out.println("$#5438#"); if(entity==null || entity.getMerchantStore().getId().intValue()!=store.getId().intValue()) {

				System.out.println("$#5440#"); resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
				System.out.println("$#5441#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				
			} else {
				
				System.out.println("$#5442#"); customerOptionService.delete(entity);
				System.out.println("$#5443#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);
				
			}
		
		
		} catch (Exception e) {
			LOGGER.error("Error while deleting option", e);
			System.out.println("$#5444#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#5445#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#5446#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		System.out.println("$#5447#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}

}
