package com.salesmanager.shop.admin.controller.products;

import com.salesmanager.core.business.services.catalog.product.attribute.ProductOptionService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.catalog.product.attribute.ProductOption;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
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
public class OptionsController {
	
	@Inject
	LanguageService languageService;
	
	@Inject
	ProductOptionService productOptionService;
	
	@Inject
	LabelUtils messages;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OptionsController.class);
	
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/options/options.html", method=RequestMethod.GET)
	public String displayOptions(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		System.out.println("$#6230#"); setMenu(model,request);



		
		System.out.println("$#6231#"); return "catalogue-options-list";
		
		
		
	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/options/editOption.html", method=RequestMethod.GET)
	public String displayOptionEdit(@RequestParam("id") long optionId, HttpServletRequest request, HttpServletResponse response, Model model, Locale locale) throws Exception {
		System.out.println("$#6232#"); return displayOption(optionId,request,response,model,locale);
	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/options/createOption.html", method=RequestMethod.GET)
	public String displayOption(HttpServletRequest request, HttpServletResponse response, Model model, Locale locale) throws Exception {
		System.out.println("$#6233#"); return displayOption(null,request,response,model,locale);
	}
	
	private String displayOption(Long optionId, HttpServletRequest request, HttpServletResponse response,Model model,Locale locale) throws Exception {

		
		System.out.println("$#6234#"); this.setMenu(model, request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		List<Language> languages = store.getLanguages();

		Set<ProductOptionDescription> descriptions = new HashSet<ProductOptionDescription>();
		
		ProductOption option = new ProductOption();
		
		System.out.println("$#6235#"); if(optionId!=null && optionId!=0) {//edit mode
			
			
			option = productOptionService.getById(store, optionId);
			
			
			System.out.println("$#6237#"); if(option==null) {
				System.out.println("$#6238#"); return "redirect:/admin/options/options.html";
			}
			
			Set<ProductOptionDescription> optionDescriptions = option.getDescriptions();
			
			
			
			for(Language l : languages) {
			
				ProductOptionDescription optionDescription = null;
				
				System.out.println("$#6239#"); if(optionDescriptions!=null) {
					
					for(ProductOptionDescription description : optionDescriptions) {
						
						String code = description.getLanguage().getCode();
						System.out.println("$#6240#"); if(code.equals(l.getCode())) {
							optionDescription = description;
						}
						
					}
					
				}
				
				System.out.println("$#6241#"); if(optionDescription==null) {
					optionDescription = new ProductOptionDescription();
					System.out.println("$#6242#"); optionDescription.setLanguage(l);
				}
				
				descriptions.add(optionDescription);
			
			}

		} else {
			
			for(Language l : languages) {
				
				ProductOptionDescription desc = new ProductOptionDescription();
				System.out.println("$#6243#"); desc.setLanguage(l);
				descriptions.add(desc);
				
			}
			
		}
		

		System.out.println("$#6244#"); option.setDescriptions(descriptions);
		model.addAttribute("option", option);
		System.out.println("$#6245#"); return "catalogue-options-details";
		
		
	}
		
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/options/save.html", method=RequestMethod.POST)
	public String saveOption(@Valid @ModelAttribute("option") ProductOption option, BindingResult result, Model model, HttpServletRequest request, Locale locale) throws Exception {
		

		//display menu
		System.out.println("$#6246#"); setMenu(model,request);
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		ProductOption dbEntity =	null;	

		System.out.println("$#6248#"); System.out.println("$#6247#"); if(option.getId() != null && option.getId() >0) { //edit entry
			//get from DB
			dbEntity = productOptionService.getById(option.getId());
			
			System.out.println("$#6250#"); if(dbEntity==null) {
				System.out.println("$#6251#"); return "redirect:/admin/options/options.html";
			}
		}
		
		//validate if it contains an existing code
		ProductOption byCode = productOptionService.getByCode(store, option.getCode());
		System.out.println("$#6252#"); if(byCode!=null) {
			ObjectError error = new ObjectError("code",messages.getMessage("message.code.exist", locale));
			System.out.println("$#6253#"); result.addError(error);
		}

			
		Map<String,Language> langs = languageService.getLanguagesMap();
			

		List<ProductOptionDescription> descriptions = option.getDescriptionsList();
		
		System.out.println("$#6254#"); if(descriptions!=null) {
				
				for(ProductOptionDescription description : descriptions) {
					
					String code = description.getLanguage().getCode();
					Language l = langs.get(code);
					System.out.println("$#6255#"); description.setLanguage(l);
					System.out.println("$#6256#"); description.setProductOption(option);
	
				}
				
		}
			
		System.out.println("$#6257#"); option.setDescriptions(new HashSet<ProductOptionDescription>(descriptions));
		System.out.println("$#6258#"); option.setMerchantStore(store);

		
		System.out.println("$#6259#"); if (result.hasErrors()) {
			System.out.println("$#6260#"); return "catalogue-options-details";
		}
		

		
		
		System.out.println("$#6261#"); productOptionService.saveOrUpdate(option);


		

		model.addAttribute("success","success");
		System.out.println("$#6262#"); return "catalogue-options-details";
	}

	
	
	@SuppressWarnings("unchecked")
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/options/paging.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> pageOptions(HttpServletRequest request, HttpServletResponse response) {
		
		String optionName = request.getParameter("name");


		AjaxResponse resp = new AjaxResponse();

		
		try {
			
			
			Language language = (Language)request.getAttribute("LANGUAGE");	
		
			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			
			List<ProductOption> options = null;
					
			System.out.println("$#6263#"); if(!StringUtils.isBlank(optionName)) {
				
				options = productOptionService.getByName(store, optionName, language);
				
			} else {
				
				options = productOptionService.listByStore(store, language);
				
			}
					
					

			for(ProductOption option : options) {
				
				@SuppressWarnings("rawtypes")
				Map entry = new HashMap();
				entry.put("optionId", option.getId());
				entry.put("display", option.isReadOnly());
				ProductOptionDescription description = option.getDescriptions().iterator().next();
				
				entry.put("name", description.getName());
				entry.put("type", option.getProductOptionType());//TODO resolve with option type label
				System.out.println("$#6264#"); resp.addDataEntry(entry);
				
				
			}
			
			System.out.println("$#6265#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_SUCCESS);
			

		
		} catch (Exception e) {
			LOGGER.error("Error while paging options", e);
			System.out.println("$#6266#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
		}
		
		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#6267#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		System.out.println("$#6268#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		
		
	}
	
	

	
	private void setMenu(Model model, HttpServletRequest request) throws Exception {
		
		//display menu
		Map<String,String> activeMenus = new HashMap<String,String>();
		activeMenus.put("catalogue", "catalogue");
		activeMenus.put("catalogue-options", "catalogue-options");
		
		@SuppressWarnings("unchecked")
		Map<String, Menu> menus = (Map<String, Menu>)request.getAttribute("MENUMAP");
		
		Menu currentMenu = (Menu)menus.get("catalogue");
		model.addAttribute("currentMenu",currentMenu);
		model.addAttribute("activeMenus",activeMenus);
		//
		
	}
	
	@RequestMapping(value="/admin/options/remove.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> deleteOption(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		String sid = request.getParameter("optionId");

		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		AjaxResponse resp = new AjaxResponse();

		
		try {
			
			Long id = Long.parseLong(sid);
			
			ProductOption entity = productOptionService.getById(id);

			System.out.println("$#6269#"); if(entity==null || entity.getMerchantStore().getId().intValue()!=store.getId().intValue()) {

				System.out.println("$#6271#"); resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
				System.out.println("$#6272#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				
			} else {
				
				System.out.println("$#6273#"); productOptionService.delete(entity);
				System.out.println("$#6274#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);
				
			}
		
		
		} catch (Exception e) {
			LOGGER.error("Error while deleting option", e);
			System.out.println("$#6275#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6276#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#6277#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		System.out.println("$#6278#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}

}
