package com.salesmanager.shop.tags;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.utils.FilePathUtils;
import com.salesmanager.shop.utils.ImageFilePath;


public class ContentImageUrlTag extends RequestContextAwareTag {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6319855234657139862L;
	private static final Logger LOGGER = LoggerFactory.getLogger(ContentImageUrlTag.class);

	private MerchantStore merchantStore;
	private String imageName;
	private String imageType;
	
	@Inject
	private FilePathUtils filePathUtils;
	
	@Inject
	@Qualifier("img")
	private ImageFilePath imageUtils;


	public int doStartTagInternal() throws JspException {
		try {


			System.out.println("$#15406#"); if (filePathUtils==null || imageUtils==null) {
	            WebApplicationContext wac = getRequestContext().getWebApplicationContext();
	            AutowireCapableBeanFactory factory = wac.getAutowireCapableBeanFactory();
													System.out.println("$#15408#"); factory.autowireBean(this);
	        }
			
			HttpServletRequest request = (HttpServletRequest) pageContext
					.getRequest();
			
			MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			System.out.println("$#15409#"); if(this.getMerchantStore()!=null) {
				merchantStore = this.getMerchantStore();
			}

			String img = imageUtils.buildStaticImageUtils(merchantStore,this.getImageType(),this.getImageName());

			System.out.println("$#15410#"); pageContext.getOut().print(img);


			
		} catch (Exception ex) {
			LOGGER.error("Error while getting content url", ex);
		}
		return SKIP_BODY;
	}

	public int doEndTag() {
		System.out.println("$#15411#"); return EVAL_PAGE;
	}

	public void setMerchantStore(MerchantStore merchantStore) {
		this.merchantStore = merchantStore;
	}

	public MerchantStore getMerchantStore() {
		System.out.println("$#15412#"); return merchantStore;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getImageName() {
		System.out.println("$#15413#"); return imageName;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public String getImageType() {
		System.out.println("$#15414#"); return imageType;
	}



	

}
