package com.salesmanager.shop.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class CorsFilter extends HandlerInterceptorAdapter {

		public CorsFilter() {
			
		}

		/**
		 * Allows public web services to work from remote hosts
		 */
	   public boolean preHandle(
	            HttpServletRequest request,
	            HttpServletResponse response,
	            Object handler) throws Exception {
		   
        	HttpServletResponse httpResponse = (HttpServletResponse) response;
        	
        	String origin = "*";
									System.out.println("$#8044#"); if(!StringUtils.isBlank(request.getHeader("origin"))) {
        		origin = request.getHeader("origin");
        	}
	
									System.out.println("$#8045#"); httpResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE, PATCH");
									System.out.println("$#8046#"); httpResponse.setHeader("Access-Control-Allow-Headers", "X-Auth-Token, Content-Type, Authorization, Cache-Control, X-Requested-With");
									System.out.println("$#8047#"); httpResponse.setHeader("Access-Control-Allow-Origin", origin);
	        
									System.out.println("$#8048#"); return true;
			
		}
}
