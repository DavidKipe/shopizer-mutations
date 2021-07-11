package com.salesmanager.shop.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.salesmanager.shop.constants.Constants;


public class ActiveLinkTag extends TagSupport {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(ActiveLinkTag.class);

	private final static String ACTIVE = "active";
	
	private String linkCode = null;
	private String activeReturnCode = null;
	private String inactiveReturnCode = null;
		

	public int doStartTag() throws JspException {
		try {



			HttpServletRequest request = (HttpServletRequest) pageContext
					.getRequest();

			String requestLinkCode = (String)request.getAttribute(Constants.LINK_CODE);
			
			System.out.println("$#15381#"); if(StringUtils.isBlank(requestLinkCode)) {
				System.out.println("$#15382#"); if(!StringUtils.isBlank(inactiveReturnCode)) {
					System.out.println("$#15383#"); pageContext.getOut().print(inactiveReturnCode);
				} else {
					System.out.println("$#15384#"); pageContext.getOut().print("");
				}
			} else {
				System.out.println("$#15385#"); if(requestLinkCode.equalsIgnoreCase(linkCode)) {
					System.out.println("$#15386#"); if(!StringUtils.isBlank(activeReturnCode)) {
						System.out.println("$#15387#"); pageContext.getOut().print(activeReturnCode);
					} else {
						System.out.println("$#15388#"); pageContext.getOut().print(ACTIVE);
					}
				} else {
					System.out.println("$#15389#"); if(!StringUtils.isBlank(inactiveReturnCode)) {
						System.out.println("$#15390#"); pageContext.getOut().print(inactiveReturnCode);
					} else {
						System.out.println("$#15391#"); pageContext.getOut().print("");
					}
				}
			}


			
		} catch (Exception ex) {
			LOGGER.error("Error while creating active link", ex);
		}
		return SKIP_BODY;
	}

	public int doEndTag() {
		System.out.println("$#15392#"); return EVAL_PAGE;
	}

	public String getLinkCode() {
		System.out.println("$#15393#"); return linkCode;
	}

	public void setLinkCode(String linkCode) {
		this.linkCode = linkCode;
	}

	public String getActiveReturnCode() {
		System.out.println("$#15394#"); return activeReturnCode;
	}

	public void setActiveReturnCode(String activeReturnCode) {
		this.activeReturnCode = activeReturnCode;
	}








	

}
