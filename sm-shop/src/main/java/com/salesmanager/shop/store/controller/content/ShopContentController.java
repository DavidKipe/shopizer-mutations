package com.salesmanager.shop.store.controller.content;

import java.util.Locale;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.salesmanager.core.business.services.content.ContentService;
import com.salesmanager.core.model.content.Content;
import com.salesmanager.core.model.content.ContentDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.shop.PageInformation;
import com.salesmanager.shop.store.controller.ControllerConstants;

@Controller
public class ShopContentController {
	
	
	@Inject
	private ContentService contentService;

	
	@RequestMapping("/shop/pages/{friendlyUrl}.html")
	public String displayContent(@PathVariable final String friendlyUrl, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);

		ContentDescription contentDescription = contentService.getBySeUrl(store, friendlyUrl);
		
		Content content = null;
		
		System.out.println("$#12307#"); if(contentDescription!=null) {
			
			content = contentDescription.getContent();
			
			System.out.println("$#12308#"); if(!content.isVisible()) {
				System.out.println("$#12309#"); return "redirect:/shop";
			}
			
			//meta information
			PageInformation pageInformation = new PageInformation();
			System.out.println("$#12310#"); pageInformation.setPageDescription(contentDescription.getMetatagDescription());
			System.out.println("$#12311#"); pageInformation.setPageKeywords(contentDescription.getMetatagKeywords());
			System.out.println("$#12312#"); pageInformation.setPageTitle(contentDescription.getTitle());
			System.out.println("$#12313#"); pageInformation.setPageUrl(contentDescription.getName());
			
			System.out.println("$#12314#"); request.setAttribute(Constants.REQUEST_PAGE_INFORMATION, pageInformation);
			
			
			
			
		}
		
		//TODO breadcrumbs
		System.out.println("$#12315#"); request.setAttribute(Constants.LINK_CODE, contentDescription.getSeUrl());
		model.addAttribute("content",contentDescription);

		System.out.println("$#12316#"); if(!StringUtils.isBlank(content.getProductGroup())) {
			model.addAttribute("productGroup",content.getProductGroup());
		}
		
		/** template **/
		StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Content.content).append(".").append(store.getStoreTemplate());

		System.out.println("$#12317#"); return template.toString();
		
		
	}
	
}