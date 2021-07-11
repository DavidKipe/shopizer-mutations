package com.salesmanager.shop.admin.controller.content;

import com.salesmanager.core.business.services.content.ContentService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.content.Content;
import com.salesmanager.core.model.content.ContentDescription;
import com.salesmanager.core.model.content.ContentType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.admin.controller.ControllerConstants;
import com.salesmanager.shop.admin.model.web.Menu;
import com.salesmanager.shop.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

@Controller
public class ContentBoxesController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContentBoxesController.class);
	
	@Inject
	private ContentService contentService;
	
	@Inject
	LanguageService languageService;
	
	@ModelAttribute("boxPositions") 
    public Set<Map.Entry<String, String>> boxPositions() { 
        final Map<String, String> map = new HashMap<String, String>(); 

        map.put("LEFT", "LEFT");
        map.put("RIGHT", "RIGHT");


								System.out.println("$#5084#"); return (map.entrySet());
    } 


	
	
	@PreAuthorize("hasRole('CONTENT')")
	@RequestMapping(value="/admin/content/boxes/list.html", method=RequestMethod.GET)
	public String listContentBoxes(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		System.out.println("$#5085#"); setMenu(model,request);

		model.addAttribute("boxes", true);
		System.out.println("$#5086#"); return ControllerConstants.Tiles.Content.contentPages;
		
		
	}
	
	@PreAuthorize("hasRole('CONTENT')")
	@RequestMapping(value="/admin/content/boxes/create.html", method=RequestMethod.GET)
	public String createBox(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		model.addAttribute("boxes", true);
		System.out.println("$#5087#"); setMenu(model,request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		Content content = new Content();
		System.out.println("$#5088#"); content.setMerchantStore(store);
		System.out.println("$#5089#"); content.setContentType(ContentType.BOX);
		
		
		List<Language> languages = store.getLanguages();
		
		
		for(Language l : languages) {
			
			ContentDescription description = new ContentDescription();
			System.out.println("$#5090#"); description.setLanguage(l);
			content.getDescriptions().add(description);
		}
		
		//add positions
		List<String> positions = new ArrayList<String>();
		positions.add("LEFT");
		positions.add("RIGHT");
		
		model.addAttribute("positions",positions);
		model.addAttribute("content",content);
		

		System.out.println("$#5091#"); return ControllerConstants.Tiles.Content.contentPagesDetails;
		
		
	}
	
	@PreAuthorize("hasRole('CONTENT')")
	@RequestMapping(value="/admin/content/boxes/details.html", method=RequestMethod.GET)
	public String getContentDetails(@RequestParam("id") Long id, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		model.addAttribute("boxes", true);
		System.out.println("$#5092#"); setMenu(model,request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		Content content = contentService.getById(id);
		

		List<String> positions = new ArrayList<String>();
		positions.add("LEFT");
		positions.add("RIGHT");
		
		model.addAttribute("positions",positions);
		
		System.out.println("$#5093#"); if(content==null) {
			LOGGER.error("Content entity null for id " + id);
			System.out.println("$#5094#"); return "redirect:/admin/content/boxes/listContent.html";
		}
		
		System.out.println("$#5095#"); if(content.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
			LOGGER.error("Content id " + id + " does not belong to merchant " + store.getId());
			System.out.println("$#5096#"); return "redirect:/admin/content/boxes/listContent.html";
		}
		
		System.out.println("$#5097#"); if(!content.getContentType().name().equals(ContentType.BOX.name())) {
			LOGGER.error("This controller does not handle content type " + content.getContentType().name());
			System.out.println("$#5098#"); return "redirect:/admin/content/boxes/listContent.html";
		}
		
		List<Language> languages = store.getLanguages();
		
		List<ContentDescription> descriptions = new ArrayList<ContentDescription>();
		for(Language l : languages) {
			for(ContentDescription description : content.getDescriptions()) {
				System.out.println("$#5099#"); if(description.getLanguage().getCode().equals(l.getCode())) {
					descriptions.add(description);
				}
			}
		}
		System.out.println("$#5100#"); content.setDescriptions(descriptions);
		
		model.addAttribute("content",content);
		

		System.out.println("$#5101#"); return ControllerConstants.Tiles.Content.contentPagesDetails;
		
		
	}
	


	
	
	@PreAuthorize("hasRole('CONTENT')")
	@RequestMapping(value="/admin/content/boxes/save.html", method=RequestMethod.POST)
	public String saveContent(@Valid @ModelAttribute Content content, BindingResult result, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		model.addAttribute("boxes", true);
		System.out.println("$#5102#"); setMenu(model,request);
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		List<String> positions = new ArrayList<String>();
		positions.add("LEFT");
		positions.add("RIGHT");
		
		model.addAttribute("positions",positions);
		
		System.out.println("$#5103#"); if (result.hasErrors()) {
			System.out.println("$#5104#"); return ControllerConstants.Tiles.Content.contentPagesDetails;
		}
		
		Map<String,Language> langs = languageService.getLanguagesMap();
		
		List<ContentDescription> descriptions = content.getDescriptions();
		for(ContentDescription description : descriptions) {
			Language l = langs.get(description.getLanguage().getCode());
			System.out.println("$#5105#"); description.setLanguage(l);
			System.out.println("$#5106#"); description.setContent(content);
		}
		
		System.out.println("$#5107#"); content.setContentType(ContentType.BOX);
		System.out.println("$#5108#"); content.setMerchantStore(store);
		System.out.println("$#5109#"); contentService.saveOrUpdate(content);
		
		
		model.addAttribute("content",content);
		model.addAttribute("success","success");
		System.out.println("$#5110#"); return ControllerConstants.Tiles.Content.contentPagesDetails;
		
		
	}
	
	
	
	private void setMenu(Model model, HttpServletRequest request) throws Exception {
		
		//display menu
		Map<String,String> activeMenus = new HashMap<String,String>();
		activeMenus.put("content", "content");
		activeMenus.put("content-boxes", "content-boxes");
		
		@SuppressWarnings("unchecked")
		Map<String, Menu> menus = (Map<String, Menu>)request.getAttribute("MENUMAP");
		
		Menu currentMenu = (Menu)menus.get("content");
		model.addAttribute("currentMenu",currentMenu);
		model.addAttribute("activeMenus",activeMenus);
		//
		
	}
	

}
