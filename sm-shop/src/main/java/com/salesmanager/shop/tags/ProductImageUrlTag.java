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

import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.utils.FilePathUtils;
import com.salesmanager.shop.utils.ImageFilePath;

public class ProductImageUrlTag extends RequestContextAwareTag {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6319855234657139862L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductImageUrlTag.class);


	private String imageName;
	private String imageType;
	private Product product;
	
	@Inject
	private FilePathUtils filePathUtils;

	@Inject
	@Qualifier("img")
	private ImageFilePath imageUtils;

	public int doStartTagInternal() throws JspException {
		try {


			System.out.println("$#15444#"); if (filePathUtils==null || imageUtils==null) {
	            WebApplicationContext wac = getRequestContext().getWebApplicationContext();
	            AutowireCapableBeanFactory factory = wac.getAutowireCapableBeanFactory();
													System.out.println("$#15446#"); factory.autowireBean(this);
	        }

			HttpServletRequest request = (HttpServletRequest) pageContext
					.getRequest();
			
			MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);

			StringBuilder imagePath = new StringBuilder();
			
			String baseUrl = filePathUtils.buildRelativeStoreUri(request, merchantStore);
			imagePath.append(baseUrl);
			
			imagePath

				.append(imageUtils.buildProductImageUtils(merchantStore, product, this.getImageName())).toString();

			System.out.println("$#15447#"); System.out.println("Printing image " + imagePath.toString());

			System.out.println("$#15448#"); pageContext.getOut().print(imagePath.toString());


			
		} catch (Exception ex) {
			LOGGER.error("Error while getting content url", ex);
		}
		return SKIP_BODY;
	}

	public int doEndTag() {
		System.out.println("$#15449#"); return EVAL_PAGE;
	}


	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getImageName() {
		System.out.println("$#15450#"); return imageName;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public String getImageType() {
		System.out.println("$#15451#"); return imageType;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Product getProduct() {
		System.out.println("$#15452#"); return product;
	}




	

}
