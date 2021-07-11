package com.salesmanager.shop.admin.controller.merchant;


import com.salesmanager.core.business.services.content.ContentService;
import com.salesmanager.core.business.services.merchant.MerchantStoreService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.content.Content;
import com.salesmanager.core.model.content.ContentDescription;
import com.salesmanager.core.model.content.ContentType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.admin.model.merchant.StoreLanding;
import com.salesmanager.shop.admin.model.merchant.StoreLandingDescription;
import com.salesmanager.shop.admin.model.web.Menu;
import com.salesmanager.shop.constants.Constants;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class StoreLandingController {
	
	@Inject
	MerchantStoreService merchantStoreService;

	@Inject
	LanguageService languageService;
	
	@Inject
	ContentService contentService;
	
	@PreAuthorize("hasRole('STORE')")
	@RequestMapping(value="/admin/store/storeLanding.html", method=RequestMethod.GET)
	public String displayStoreLanding(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		System.out.println("$#5664#"); setMenu(model,request);

		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		List<Language> languages = store.getLanguages();
		
		Content content = contentService.getByCode("LANDING_PAGE", store);
		StoreLanding landing = new StoreLanding();
		
		List<StoreLandingDescription> descriptions = new ArrayList<StoreLandingDescription>();
		
		
		for(Language l : languages) {
			
			StoreLandingDescription landingDescription = null;
			System.out.println("$#5665#"); if(content!=null) {
				for(ContentDescription desc : content.getDescriptions()) {
					System.out.println("$#5666#"); if(desc.getLanguage().getCode().equals(l.getCode())) {
						landingDescription = new StoreLandingDescription();
						System.out.println("$#5667#"); landingDescription.setDescription(desc.getMetatagDescription());
						System.out.println("$#5668#"); landingDescription.setHomePageContent(desc.getDescription());
						System.out.println("$#5669#"); landingDescription.setKeywords(desc.getMetatagKeywords());
						System.out.println("$#5670#"); landingDescription.setTitle(desc.getName());//name is a not empty
						System.out.println("$#5671#"); landingDescription.setLanguage(desc.getLanguage());
						break;
					}
				}
			}
			
			System.out.println("$#5672#"); if(landingDescription==null) {
				landingDescription = new StoreLandingDescription();
				System.out.println("$#5673#"); landingDescription.setLanguage(l);
			}
			

			
			descriptions.add(landingDescription);
		}
		
		System.out.println("$#5674#"); landing.setDescriptions(descriptions);

		
		model.addAttribute("store", store);
		model.addAttribute("storeLanding", landing);

		
		System.out.println("$#5675#"); return "admin-store-landing";
	}
	
	@PreAuthorize("hasRole('STORE')")
	@RequestMapping(value="/admin/store/saveLanding.html", method=RequestMethod.POST)
	public String saveStoreLanding(@Valid @ModelAttribute("storeLanding") StoreLanding storeLanding, BindingResult result, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		System.out.println("$#5676#"); setMenu(model,request);
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);

		
		System.out.println("$#5677#"); if (result.hasErrors()) {
			System.out.println("$#5678#"); return "admin-store-landing";
		}
		
		//get original store
		Content content = contentService.getByCode("LANDING_PAGE", store);
		
		System.out.println("$#5679#"); if(content==null) {
			content = new Content();
			System.out.println("$#5680#"); content.setVisible(true);
			System.out.println("$#5681#"); content.setContentType(ContentType.SECTION);
			System.out.println("$#5682#"); content.setCode("LANDING_PAGE");
			System.out.println("$#5683#"); content.setMerchantStore(store);
		}
		

		//List<Language> languages = store.getLanguages();
			
		Map<String,Language> langs = languageService.getLanguagesMap();
		
		
		
/*		for(Language l : languages) {
			
			StoreLandingDescription landingDescription = null;
			for(ContentDescription desc : content.getDescriptions()) {
					if(desc.getLanguage().getCode().equals(l.getCode())) {
						landingDescription = new StoreLandingDescription();
						landingDescription.setDescription(desc.getMetatagDescription());
						landingDescription.setHomePageContent(desc.getDescription());
						landingDescription.setKeywords(desc.getMetatagKeywords());
						landingDescription.setTitle(desc.getName());//name is a not empty
						landingDescription.setLanguage(desc.getLanguage());
					}
			}
		
			
			if(landingDescription==null) {
				landingDescription = new StoreLandingDescription();
				landingDescription.setLanguage(l);
			}
			

			
			descriptions.add(landingDescription);
		}
		
		landing.setDescriptions(descriptions);*/
		
		
		
			

		List<StoreLandingDescription> descriptions = storeLanding.getDescriptions();
		List<ContentDescription> contentDescriptions = new ArrayList<ContentDescription>();
		System.out.println("$#5684#"); if(descriptions!=null) {
				
				for(StoreLandingDescription description : descriptions) {
					
					String code = description.getLanguage().getCode();
					Language l = langs.get(code);
					
					ContentDescription contentDescription = null;
					System.out.println("$#5686#"); System.out.println("$#5685#"); if(content.getDescriptions()!=null && content.getDescriptions().size()>0) {
						
						for(ContentDescription desc : content.getDescriptions()) {
							
							System.out.println("$#5688#"); if(desc.getLanguage().getCode().equals(l.getCode())) {
								contentDescription = desc;
								System.out.println("$#5689#"); desc.setMetatagDescription(description.getDescription());
								System.out.println("$#5690#"); desc.setName(description.getTitle());
								System.out.println("$#5691#"); desc.setTitle(description.getTitle());
								System.out.println("$#5692#"); desc.setDescription(description.getHomePageContent());
								System.out.println("$#5693#"); desc.setMetatagKeywords(description.getKeywords());
								
								
							}

						}
					}
					
					System.out.println("$#5694#"); if(contentDescription==null) {
						
						
						contentDescription = new ContentDescription();
						System.out.println("$#5695#"); contentDescription.setContent(content);
						System.out.println("$#5696#"); contentDescription.setLanguage(l);
						System.out.println("$#5697#"); contentDescription.setMetatagDescription(description.getDescription());
						System.out.println("$#5698#"); contentDescription.setName(description.getTitle());
						System.out.println("$#5699#"); contentDescription.setDescription(description.getHomePageContent());
						System.out.println("$#5700#"); contentDescription.setMetatagKeywords(description.getKeywords());

					}
					
					contentDescriptions.add(contentDescription);



				}
				
				System.out.println("$#5701#"); content.setDescriptions(contentDescriptions);
				
			}


		
		System.out.println("$#5702#"); contentService.saveOrUpdate(content);

		model.addAttribute("success","success");

		System.out.println("$#5703#"); return "admin-store-landing";
	}
	
	private void setMenu(Model model, HttpServletRequest request) throws Exception {

		//display menu
		Map<String,String> activeMenus = new HashMap<String,String>();
		activeMenus.put("store", "store");
		activeMenus.put("storeLanding", "storeLanding");

		
		@SuppressWarnings("unchecked")
		Map<String, Menu> menus = (Map<String, Menu>)request.getAttribute("MENUMAP");
		
		Menu currentMenu = (Menu)menus.get("store");
		model.addAttribute("currentMenu",currentMenu);
		model.addAttribute("activeMenus",activeMenus);
		//
		
	}

}
