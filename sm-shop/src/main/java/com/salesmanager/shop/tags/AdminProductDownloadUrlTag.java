package com.salesmanager.shop.tags;

import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.salesmanager.core.model.catalog.product.file.DigitalProduct;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.utils.FilePathUtils;



public class AdminProductDownloadUrlTag extends RequestContextAwareTag {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6319855234657139862L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminProductDownloadUrlTag.class);

	private DigitalProduct digitalProduct;
	
	@Inject
	private FilePathUtils filePathUtils;




	public DigitalProduct getDigitalProduct() {
		System.out.println("$#15395#"); return digitalProduct;
	}

	public void setDigitalProduct(DigitalProduct digitalProduct) {
		this.digitalProduct = digitalProduct;
	}

	public int doStartTagInternal() throws JspException {
		try {
			
			System.out.println("$#15396#"); if (filePathUtils==null) {
	            WebApplicationContext wac = getRequestContext().getWebApplicationContext();
	            AutowireCapableBeanFactory factory = wac.getAutowireCapableBeanFactory();
													System.out.println("$#15397#"); factory.autowireBean(this);
	        }


			HttpServletRequest request = (HttpServletRequest) pageContext
					.getRequest();
			
			MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			
			HttpSession session = request.getSession();

			StringBuilder filePath = new StringBuilder();
			
			//TODO domain from merchant, else from global config, else from property (localhost)
			
			// example -> "/files/{storeCode}/{fileName}.{extension}"
			

			@SuppressWarnings("unchecked")
			Map<String,String> configurations = (Map<String, String>)session.getAttribute(Constants.STORE_CONFIGURATION);
			String scheme = Constants.HTTP_SCHEME;
			System.out.println("$#15398#"); if(configurations!=null) {
				scheme = (String)configurations.get("scheme");
			}
			

			
			filePath.append(scheme).append("://")
			.append(merchantStore.getDomainName())
			//.append("/")
			.append(request.getContextPath());
			
			filePath
				.append(filePathUtils.buildAdminDownloadProductFilePath(merchantStore, digitalProduct)).toString();

			

			System.out.println("$#15399#"); pageContext.getOut().print(filePath.toString());


			
		} catch (Exception ex) {
			LOGGER.error("Error while getting content url", ex);
		}
		return SKIP_BODY;
	}

	public int doEndTag() {
		System.out.println("$#15400#"); return EVAL_PAGE;
	}





	

}
