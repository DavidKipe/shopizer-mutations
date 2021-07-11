package com.salesmanager.shop.store.controller.store;

import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.salesmanager.core.business.services.content.ContentService;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.content.Content;
import com.salesmanager.core.model.content.ContentDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.shop.ContactForm;
import com.salesmanager.shop.model.shop.PageInformation;
import com.salesmanager.shop.store.controller.AbstractController;
import com.salesmanager.shop.store.controller.ControllerConstants;
import com.salesmanager.shop.utils.CaptchaRequestUtils;
import com.salesmanager.shop.utils.EmailTemplatesUtils;
import com.salesmanager.shop.utils.LabelUtils;
import com.salesmanager.shop.utils.LocaleUtils;

@Controller
public class ContactController extends AbstractController {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ContactController.class);

	@Value("${config.googleMapsKey}")
	private String googleMapsKey;
	
    @Value("${config.recaptcha.siteKey}")
    private String siteKeyKey;

	@Inject
	private ContentService contentService;

	@Inject
	private LabelUtils messages;
	
	@Inject
	private EmailTemplatesUtils emailTemplatesUtils;
	
	@Inject
	private CaptchaRequestUtils captchaRequestUtils;

	
	private final static String CONTACT_LINK = "CONTACT";
	
	
	@RequestMapping("/shop/store/contactus.html")
	public String display(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
		
		model.addAttribute("googleMapsKey",googleMapsKey);
		
		System.out.println("$#14051#"); request.setAttribute(Constants.LINK_CODE, CONTACT_LINK);

		Language language = (Language)request.getAttribute("LANGUAGE");
		
		ContactForm contact = new ContactForm();
		model.addAttribute("contact", contact);
		
		model.addAttribute( "recapatcha_public_key", siteKeyKey);
		
		Content content = contentService.getByCode(Constants.CONTENT_CONTACT_US, store, language);
		ContentDescription contentDescription = null;
		System.out.println("$#14052#"); if(content!=null && content.isVisible()) {
			contentDescription = content.getDescription();
		}
		
		System.out.println("$#14054#"); if(contentDescription!=null) {

			//meta information
			PageInformation pageInformation = new PageInformation();
			System.out.println("$#14055#"); pageInformation.setPageDescription(contentDescription.getMetatagDescription());
			System.out.println("$#14056#"); pageInformation.setPageKeywords(contentDescription.getMetatagKeywords());
			System.out.println("$#14057#"); pageInformation.setPageTitle(contentDescription.getTitle());
			System.out.println("$#14058#"); pageInformation.setPageUrl(contentDescription.getName());
			
			System.out.println("$#14059#"); request.setAttribute(Constants.REQUEST_PAGE_INFORMATION, pageInformation);
			
			model.addAttribute("content",contentDescription);

		} 

		
		
		/** template **/
		StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Content.contactus).append(".").append(store.getStoreTemplate());
		System.out.println("$#14060#"); return template.toString();
		
		
	}

	
	@RequestMapping(value={"/shop/store/{storeCode}/contact"}, method=RequestMethod.POST)
	public @ResponseBody String sendEmail(@ModelAttribute(value="contact") ContactForm contact, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {

		AjaxResponse ajaxResponse = new AjaxResponse();
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);

		try {

	        
									System.out.println("$#14061#"); if(!StringUtils.isBlank(request.getParameter("g-recaptcha-response"))) {
	        	boolean validateCaptcha = captchaRequestUtils.checkCaptcha(request.getParameter("g-recaptcha-response"));
	        	
																System.out.println("$#14062#"); if ( !validateCaptcha )
                {
                    LOGGER.debug( "Captcha response does not matched" );
        			FieldError error = new FieldError("captchaChallengeField","captchaChallengeField",messages.getMessage("validaion.recaptcha.not.matched", locale));
											System.out.println("$#14063#"); bindingResult.addError(error);
                }
	        }
	        
	        
	        
									System.out.println("$#14064#"); if ( bindingResult.hasErrors() )
	        {
	            LOGGER.debug( "found {} validation error while validating in customer registration ",
	                         bindingResult.getErrorCount() );
													System.out.println("$#14065#"); ajaxResponse.setErrorString(bindingResult.getAllErrors().get(0).getDefaultMessage());
													System.out.println("$#14066#"); ajaxResponse.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
													System.out.println("$#14067#"); return ajaxResponse.toJSONString();

	        }
	        
									System.out.println("$#14068#"); emailTemplatesUtils.sendContactEmail(contact, store, LocaleUtils.getLocale(store.getDefaultLanguage()), request.getContextPath());
			
			System.out.println("$#14069#"); ajaxResponse.setStatus(AjaxResponse.RESPONSE_STATUS_SUCCESS);
		} catch(Exception e) {
			LOGGER.error("An error occured while trying to send an email",e);
			System.out.println("$#14070#"); ajaxResponse.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
		}
		
		System.out.println("$#14071#"); return ajaxResponse.toJSONString();
		
		
	}
	
	
}
