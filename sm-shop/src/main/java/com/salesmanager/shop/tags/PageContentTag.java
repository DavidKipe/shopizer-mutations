package com.salesmanager.shop.tags;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.salesmanager.core.business.services.content.ContentService;
import com.salesmanager.core.model.content.Content;
import com.salesmanager.core.model.content.ContentDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.constants.Constants;


public class PageContentTag extends RequestContextAwareTag  {
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(PageContentTag.class);


	@Inject
	private ContentService contentService;
	
	private String contentCode;
	
	
	

	public String getContentCode() {
		System.out.println("$#15429#"); return contentCode;
	}


	public void setContentCode(String contentCode) {
		this.contentCode = contentCode;
	}


	@Override
	protected int doStartTagInternal() throws Exception {
		System.out.println("$#15430#"); if (contentService == null || contentService==null) {
			LOGGER.debug("Autowiring contentService");
            WebApplicationContext wac = getRequestContext().getWebApplicationContext();
            AutowireCapableBeanFactory factory = wac.getAutowireCapableBeanFactory();
												System.out.println("$#15432#"); factory.autowireBean(this);
        }
		
		HttpServletRequest request = (HttpServletRequest) pageContext
		.getRequest();
		
		Language language = (Language)request.getAttribute(Constants.LANGUAGE);

		MerchantStore store = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);

		Content content = contentService.getByCode(contentCode, store, language);
		
		String pageContent = "";
		System.out.println("$#15433#"); if(content!=null) {
			ContentDescription description = content.getDescription();
			System.out.println("$#15434#"); if(description != null) {
				pageContent = description.getDescription();
			}
		}
		
		
		System.out.println("$#15435#"); pageContext.getOut().print(pageContent);
		
		return SKIP_BODY;

	}


	public int doEndTag() {
		System.out.println("$#15436#"); return EVAL_PAGE;
	}


	

}
