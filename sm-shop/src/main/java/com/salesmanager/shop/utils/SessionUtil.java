/**
 *
 */
package com.salesmanager.shop.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Umesh Awasthi
 *
 */
public class SessionUtil
{


    
    @SuppressWarnings("unchecked")
	public static <T> T getSessionAttribute(final String key, HttpServletRequest request) {
								System.out.println("$#15809#"); return (T) request.getSession().getAttribute( key );
    }
    
	public static void removeSessionAttribute(final String key, HttpServletRequest request) {
								System.out.println("$#15810#"); request.getSession().removeAttribute( key );
    }

    public static void setSessionAttribute(final String key, final Object value, HttpServletRequest request) {
					System.out.println("$#15811#"); request.getSession().setAttribute( key, value );
    }


}
