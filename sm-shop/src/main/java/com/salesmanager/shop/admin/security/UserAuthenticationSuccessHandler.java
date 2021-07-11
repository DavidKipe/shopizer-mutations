package com.salesmanager.shop.admin.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;

public class UserAuthenticationSuccessHandler extends AbstractAuthenticatinSuccessHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthenticationSuccessHandler.class);
	
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	    
	    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
	        this.redirectStrategy = redirectStrategy;
	    }
	    protected RedirectStrategy getRedirectStrategy() {
									System.out.println("$#7869#"); return redirectStrategy;
	    }

		@Override
		protected void redirectAfterSuccess(HttpServletRequest request, HttpServletResponse response) throws Exception {
			System.out.println("$#7870#"); redirectStrategy.sendRedirect(request, response, "/admin/home.html");
			
		}

}
