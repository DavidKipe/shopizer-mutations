package com.salesmanager.shop.admin.controller.content;

import com.salesmanager.core.business.services.catalog.product.relationship.ProductRelationshipService;
import com.salesmanager.core.business.services.content.ContentService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.catalog.product.relationship.ProductRelationship;
import com.salesmanager.core.model.content.Content;
import com.salesmanager.core.model.content.ContentDescription;
import com.salesmanager.core.model.content.ContentType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.admin.controller.ControllerConstants;
import com.salesmanager.shop.admin.model.web.Menu;
import com.salesmanager.shop.constants.Constants;

import org.apache.commons.collections4.CollectionUtils;
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
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

@Controller
public class ContentPagesController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContentPagesController.class);
	
	@Inject
	private ContentService contentService;
	
	@Inject
	LanguageService languageService;
	
	@Inject
	ProductRelationshipService productRelationshipService;
	
	
	@PreAuthorize("hasRole('CONTENT')")
	@RequestMapping(value="/admin/content/pages/list.html", method=RequestMethod.GET)
	public String listContentPages(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		System.out.println("$#5138#"); setMenu(model,request);

		System.out.println("$#5139#"); return ControllerConstants.Tiles.Content.contentPages;
		
		
	}
	
	@PreAuthorize("hasRole('CONTENT')")
	@RequestMapping(value="/admin/content/pages/create.html", method=RequestMethod.GET)
	public String createPage(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		System.out.println("$#5140#"); setMenu(model,request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		Content content = new Content();
		System.out.println("$#5141#"); content.setMerchantStore(store);
		System.out.println("$#5142#"); content.setContentType(ContentType.PAGE);
		
		
		List<Language> languages = store.getLanguages();
		
		
		for(Language l : languages) {
			
			ContentDescription description = new ContentDescription();
			System.out.println("$#5143#"); description.setLanguage(l);
			content.getDescriptions().add(description);
		}
		
		List<ProductRelationship> relationships = productRelationshipService.getGroups(store);
		System.out.println("$#5144#"); if(!CollectionUtils.isEmpty(relationships)) {
			model.addAttribute("productGroups", relationships);
		}
		
		
		
		model.addAttribute("content",content);
		

		System.out.println("$#5145#"); return ControllerConstants.Tiles.Content.contentPagesDetails;
		
		
	}
	
	@PreAuthorize("hasRole('CONTENT')")
	@RequestMapping(value="/admin/content/pages/details.html", method=RequestMethod.GET)
	public String getContentDetails(@RequestParam("id") Long id, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		System.out.println("$#5146#"); setMenu(model,request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		Content content = contentService.getById(id);
		

		
		System.out.println("$#5147#"); if(content==null) {
			LOGGER.error("Content entity null for id " + id);
			System.out.println("$#5148#"); return "redirect:/admin/content/pages/listContent.html";
		}
		
		System.out.println("$#5149#"); if(content.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
			LOGGER.error("Content id " + id + " does not belong to merchant " + store.getId());
			System.out.println("$#5150#"); return "redirect:/admin/content/pages/listContent.html";
		}
		
		System.out.println("$#5151#"); if(!content.getContentType().name().equals(ContentType.PAGE.name())) {
			LOGGER.error("This controller does not handle content type " + content.getContentType().name());
			System.out.println("$#5152#"); return "redirect:/admin/content/pages/listContent.html";
		}
		
		List<Language> languages = store.getLanguages();
		
		List<ContentDescription> descriptions = new ArrayList<ContentDescription>();
		for(Language l : languages) {
			for(ContentDescription description : content.getDescriptions()) {
				System.out.println("$#5153#"); if(description.getLanguage().getCode().equals(l.getCode())) {
					descriptions.add(description);
				}
			}
		}
		System.out.println("$#5154#"); content.setDescriptions(descriptions);
		model.addAttribute("content",content);
		
		List<ProductRelationship> relationships = productRelationshipService.getGroups(store);
		System.out.println("$#5155#"); if(!CollectionUtils.isEmpty(relationships)) {
			model.addAttribute("productGroups", relationships);
		}
		
		System.out.println("$#5156#"); return ControllerConstants.Tiles.Content.contentPagesDetails;
		
		
	}
	
	
	
	@PreAuthorize("hasRole('CONTENT')")
	@RequestMapping(value="/admin/content/remove.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> removeContent(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		String id = request.getParameter("id");

		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#5157#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		
		try {
			
			//get the content first
			Long lid = Long.parseLong(id);
			
			Content dbContent = contentService.getById(lid);
			
			System.out.println("$#5158#"); if(dbContent==null) {
				LOGGER.error("Invalid content id ", id);
				System.out.println("$#5159#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#5160#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#5161#"); if(dbContent!=null && dbContent.getMerchantStore().getId().intValue()!= store.getId().intValue()) {
				System.out.println("$#5163#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#5164#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#5165#"); contentService.delete(dbContent);

			System.out.println("$#5166#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);
		
		} catch (Exception e) {
			LOGGER.error("Error while deleting product", e);
			System.out.println("$#5167#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#5168#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#5169#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	
	
	@SuppressWarnings({ "unchecked"})
	@PreAuthorize("hasRole('CONTENT')")
	@RequestMapping(value="/admin/content/page.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> pageStaticContent(@RequestParam("contentType") String contentType, HttpServletRequest request, HttpServletResponse response) {
		AjaxResponse resp = new AjaxResponse();

		try {
			

			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);

			Language language = (Language)request.getAttribute("LANGUAGE");
			
			

			
			ContentType cType = ContentType.PAGE;
			System.out.println("$#5170#"); if(ContentType.BOX.name().equals(contentType)) {
				cType = ContentType.BOX;
			} 
			List<Content> contentList = contentService.listByType(cType, store, language);
			
			System.out.println("$#5171#"); if(contentList!=null) {

				for(Content content : contentList) {
					
					List<ContentDescription> descriptions = content.getDescriptions();
					ContentDescription description = descriptions.get(0);
					for(ContentDescription desc : descriptions) {
						System.out.println("$#5172#"); if(desc.getLanguage().getCode().equals(language.getCode())) {
							description = desc;
							break;
						}
					}
					

					@SuppressWarnings("rawtypes")
					Map entry = new HashMap();
					entry.put("id", content.getId());
					entry.put("code", content.getCode());
					entry.put("name", description.getName());
					System.out.println("$#5173#"); resp.addDataEntry(entry);

				}
			
			}
			
			System.out.println("$#5174#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_SUCCESS);

		} catch (Exception e) {
			LOGGER.error("Error while paging content", e);
			System.out.println("$#5175#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
		}
		
		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#5176#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		System.out.println("$#5177#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	
	
	@PreAuthorize("hasRole('CONTENT')")
	@RequestMapping(value="/admin/content/pages/save.html", method=RequestMethod.POST)
	public String saveContent(@Valid @ModelAttribute Content content, BindingResult result, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		System.out.println("$#5178#"); setMenu(model,request);
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		System.out.println("$#5179#"); if (result.hasErrors()) {
			System.out.println("$#5180#"); return ControllerConstants.Tiles.Content.contentPagesDetails;
		}
		
		Map<String,Language> langs = languageService.getLanguagesMap();
		
		List<ContentDescription> descriptions = content.getDescriptions();
		for(ContentDescription description : descriptions) {
			Language l = langs.get(description.getLanguage().getCode());
			System.out.println("$#5181#"); description.setLanguage(l);
			System.out.println("$#5182#"); description.setContent(content);
		}
		
		System.out.println("$#5183#"); if(content.getSortOrder()==null) {
			System.out.println("$#5184#"); content.setSortOrder(0);
		}

		System.out.println("$#5185#"); content.setContentType(ContentType.PAGE);
		System.out.println("$#5186#"); content.setMerchantStore(store);

		System.out.println("$#5187#"); contentService.saveOrUpdate(content);
		
		List<ProductRelationship> relationships = productRelationshipService.getGroups(store);
		System.out.println("$#5188#"); if(!CollectionUtils.isEmpty(relationships)) {
			model.addAttribute("productGroups", relationships);
		}
		
		
		model.addAttribute("content",content);
		model.addAttribute("success","success");
		System.out.println("$#5189#"); return ControllerConstants.Tiles.Content.contentPagesDetails;
		
		
	}
	
	/**
	 * Check if the content code filled in by the
	 * user is unique
	 * @param request
	 * @param response
	 * @param locale
	 * @return
	 */
	@PreAuthorize("hasRole('CONTENT')")
	@RequestMapping(value="/admin/content/checkContentCode.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> checkContentCode(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		
		String code = request.getParameter("code");
		String id = request.getParameter("id");

		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);

		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#5190#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
					System.out.println("$#5191#"); if(StringUtils.isBlank(code)) {
				System.out.println("$#5192#"); resp.setStatus(AjaxResponse.CODE_ALREADY_EXIST);
				String returnString = resp.toJSONString();
				System.out.println("$#5193#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		   }
		
		try {
			
		Content content = contentService.getByCode(code, store);
		
		
		System.out.println("$#5194#"); if(!StringUtils.isBlank(id)) {
			try {
				Long lid = Long.parseLong(id);
				
				System.out.println("$#5195#"); if(content!=null && content.getCode().equals(code) && content.getId().longValue()==lid) {
					System.out.println("$#5198#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_SUCCESS);
					String returnString = resp.toJSONString();
					System.out.println("$#5199#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
				}
			} catch (Exception e) {
				System.out.println("$#5200#"); resp.setStatus(AjaxResponse.CODE_ALREADY_EXIST);
				String returnString = resp.toJSONString();
				System.out.println("$#5201#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}

		} else {
			System.out.println("$#5202#"); if(content!=null) {
				System.out.println("$#5203#"); resp.setStatus(AjaxResponse.CODE_ALREADY_EXIST);
				String returnString = resp.toJSONString();
				System.out.println("$#5204#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
		}

			

			System.out.println("$#5205#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);

		} catch (Exception e) {
			LOGGER.error("Error while getting content", e);
			System.out.println("$#5206#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#5207#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#5208#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	
	
	
	private void setMenu(Model model, HttpServletRequest request) throws Exception {
		
		//display menu
		Map<String,String> activeMenus = new HashMap<String,String>();
		activeMenus.put("content", "content");
		activeMenus.put("content-pages", "content-pages");
		
		@SuppressWarnings("unchecked")
		Map<String, Menu> menus = (Map<String, Menu>)request.getAttribute("MENUMAP");
		
		Menu currentMenu = (Menu)menus.get("content");
		model.addAttribute("currentMenu",currentMenu);
		model.addAttribute("activeMenus",activeMenus);
		//
		
	}
	

}
