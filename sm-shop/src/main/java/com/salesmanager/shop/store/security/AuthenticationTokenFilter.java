package com.salesmanager.shop.store.security;

import java.io.IOException;
import java.util.Enumeration;
import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import com.salesmanager.core.model.common.UserContext;
import com.salesmanager.shop.store.security.common.CustomAuthenticationManager;
import com.salesmanager.shop.utils.GeoLocationUtils;


public class AuthenticationTokenFilter extends OncePerRequestFilter {


	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationTokenFilter.class);

    
    @Value("${authToken.header}")
    private String tokenHeader;
    
    private final static String BEARER_TOKEN ="Bearer ";
    
    private final static String FACEBOOK_TOKEN ="FB ";

    
    @Inject
    private CustomAuthenticationManager jwtCustomCustomerAuthenticationManager;
    
    @Inject
    private CustomAuthenticationManager jwtCustomAdminAuthenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        

    	String origin = "*";
					System.out.println("$#15246#"); if(!StringUtils.isBlank(request.getHeader("origin"))) {
    		origin = request.getHeader("origin");
    	}
    	//in flight
					System.out.println("$#15247#"); response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE, PATCH");
					System.out.println("$#15248#"); response.setHeader("Access-Control-Allow-Origin", origin);
					System.out.println("$#15249#"); response.setHeader("Access-Control-Allow-Headers", "X-Auth-Token, Content-Type, Authorization, Cache-Control, X-Requested-With");
					System.out.println("$#15250#"); response.setHeader("Access-Control-Allow-Credentials", "true");

    	try {
    		
    		String ipAddress = GeoLocationUtils.getClientIpAddress(request);
    		
    		UserContext userContext = UserContext.create();
						System.out.println("$#15251#"); userContext.setIpAddress(ipAddress);
    		
    	} catch(Exception s) {
    		LOGGER.error("Error while getting ip address ", s);
    	}


					System.out.println("$#15252#"); if(request.getRequestURL().toString().contains("/api/v1/auth")) {
    		//setHeader(request,response);   	
	    	final String requestHeader = request.getHeader(this.tokenHeader);//token
	    	
	    	try {
										System.out.println("$#15253#"); if (requestHeader != null && requestHeader.startsWith(BEARER_TOKEN)) {//Bearer
		        	
											System.out.println("$#15255#"); jwtCustomCustomerAuthenticationManager.authenticateRequest(request, response);
	
										} else if(requestHeader != null && requestHeader.startsWith(FACEBOOK_TOKEN)) { System.out.println("$#15256#");
		        	//Facebook
		        	//facebookCustomerAuthenticationManager.authenticateRequest(request, response);
		        } else {
											System.out.println("$#15256#"); // manual correction for else-if mutation coverage
		        	LOGGER.warn("couldn't find any authorization token, will ignore the header");
		        }
	        
	    	} catch(Exception e) {
	    		throw new ServletException(e);
	    	}
    	}
    	
					System.out.println("$#15258#"); if(request.getRequestURL().toString().contains("/api/v1/private")) {
    		
    		//setHeader(request,response);  
    		
    		Enumeration<String> headers = request.getHeaderNames();
						System.out.println("$#15259#"); while(headers.hasMoreElements()) {
    			LOGGER.debug(headers.nextElement());
    		}

	    	final String requestHeader = request.getHeader(this.tokenHeader);//token
	    	
	    	try {
										System.out.println("$#15260#"); if (requestHeader != null && requestHeader.startsWith(BEARER_TOKEN)) {//Bearer
		        	
											System.out.println("$#15262#"); jwtCustomAdminAuthenticationManager.authenticateRequest(request, response);
	
		        } else {
		        	LOGGER.warn("couldn't find any authorization token, will ignore the header, might be a preflight check");
		        }
	        
	    	} catch(Exception e) {
	    		throw new ServletException(e);
	    	}
    	}

								System.out.println("$#15263#"); chain.doFilter(request, response);
								System.out.println("$#15264#"); postFilter(request, response, chain);
    }
    
    
    private void postFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
    	
    	try {
    		
    		UserContext userContext = UserContext.getCurrentInstance();
						System.out.println("$#15265#"); if(userContext!=null) {
							System.out.println("$#15266#"); userContext.close();
    		}
    		
    	} catch(Exception s) {
    		LOGGER.error("Error while getting ip address ", s);
    	}
    	
    }


}
