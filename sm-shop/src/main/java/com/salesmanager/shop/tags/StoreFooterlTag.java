package com.salesmanager.shop.tags;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.shop.constants.Constants;

public class StoreFooterlTag extends TagSupport {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6319855234657139862L;
	private static final Logger LOGGER = LoggerFactory.getLogger(StoreFooterlTag.class);

	private final static String COPY = "\u00a9";


	public int doStartTag() throws JspException {
		try {



			HttpServletRequest request = (HttpServletRequest) pageContext
					.getRequest();
			
			MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);

			
			StringBuilder y = new StringBuilder();
			int currentYear = Calendar.getInstance().get(Calendar.YEAR);


			System.out.println("$#15498#"); if(merchantStore!=null) {
				Date dateBusiness=merchantStore.getInBusinessSince();
				System.out.println("$#15499#"); if(dateBusiness!=null) {
					Calendar c = Calendar.getInstance();
					System.out.println("$#15500#"); c.setTime(dateBusiness);
					int startBusiness = c.get(Calendar.YEAR);
					System.out.println("$#15502#"); System.out.println("$#15501#"); if(startBusiness<currentYear) {
						y.append(startBusiness).append("-");
					}
				}
			}

			y.append(currentYear);
			
			StringBuilder copy = new StringBuilder();
			copy.append(COPY).append(" ").append(merchantStore.getStorename()).append(" ").append(y.toString());

			System.out.println("$#15503#"); pageContext.getOut().print(copy.toString());


			
		} catch (Exception ex) {
			LOGGER.error("Error while getting content url", ex);
		}
		return SKIP_BODY;
	}

	public int doEndTag() {
		System.out.println("$#15504#"); return EVAL_PAGE;
	}








	

}
