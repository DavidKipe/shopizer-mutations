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
import com.salesmanager.shop.model.catalog.manufacturer.Manufacturer;
import com.salesmanager.shop.utils.FilePathUtils;
import com.salesmanager.shop.utils.ImageFilePath;



public class ManufacturerImageUrlTag extends RequestContextAwareTag {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6319855234657139862L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ManufacturerImageUrlTag.class);


	private String imageName;
	private String imageType;
	private Manufacturer manufacturer;
	
	@Inject
	private FilePathUtils filePathUtils;


	@Inject
	@Qualifier("img")
	private ImageFilePath imageUtils;

	public int doStartTagInternal() throws JspException {
		try {
			
			System.out.println("$#15415#"); if (filePathUtils==null || imageUtils==null) {
	            WebApplicationContext wac = getRequestContext().getWebApplicationContext();
	            AutowireCapableBeanFactory factory = wac.getAutowireCapableBeanFactory();
													System.out.println("$#15417#"); factory.autowireBean(this);
	        }


			HttpServletRequest request = (HttpServletRequest) pageContext
					.getRequest();
			
			MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			
			StringBuilder imagePath = new StringBuilder();
			
			String baseUrl = filePathUtils.buildStoreUri(merchantStore, request);
			imagePath.append(baseUrl);
			
			System.out.println("$#15418#"); pageContext.getOut().print(imagePath.toString());


			
		} catch (Exception ex) {
			LOGGER.error("Error while getting content url", ex);
		}
		return SKIP_BODY;
	}

	public int doEndTag() {
		System.out.println("$#15419#"); return EVAL_PAGE;
	}


	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getImageName() {
		System.out.println("$#15420#"); return imageName;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public String getImageType() {
		System.out.println("$#15421#"); return imageType;
	}

	public Manufacturer getManufacturer() {
		System.out.println("$#15422#"); return manufacturer;
	}

	public void setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}

	

}
