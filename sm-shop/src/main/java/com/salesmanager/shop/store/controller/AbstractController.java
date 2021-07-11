/**
 *
 */
package com.salesmanager.shop.store.controller;

import javax.servlet.http.HttpServletRequest;

import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.store.model.paging.PaginationData;

/**
 * @author Umesh A
 *
 */
public abstract class AbstractController {


    /**
     * Method which will help to retrieving values from Session
     * based on the key being passed to the method.
     * @param key
     * @return value stored in session corresponding to the key
     */
    @SuppressWarnings( "unchecked" )
    protected <T> T getSessionAttribute(final String key, HttpServletRequest request) {
											System.out.println("$#12196#"); return (T) com.salesmanager.shop.utils.SessionUtil.getSessionAttribute(key, request);

	}
    
    protected void setSessionAttribute(final String key, final Object value, HttpServletRequest request) {
					System.out.println("$#12197#"); com.salesmanager.shop.utils.SessionUtil.setSessionAttribute(key, value, request);
	}
    
    
    protected void removeAttribute(final String key, HttpServletRequest request) {
					System.out.println("$#12198#"); com.salesmanager.shop.utils.SessionUtil.removeSessionAttribute(key, request);
	}
    
    protected Language getLanguage(HttpServletRequest request) {
					System.out.println("$#12199#"); return (Language)request.getAttribute(Constants.LANGUAGE);
    }

    protected PaginationData createPaginaionData( final int pageNumber, final int pageSize )
    {
        final PaginationData paginaionData = new PaginationData(pageSize,pageNumber);
       
								System.out.println("$#12200#"); return paginaionData;
    }
    
    protected PaginationData calculatePaginaionData( final PaginationData paginationData, final int pageSize, final int resultCount){
        
    	int currentPage = paginationData.getCurrentPage();


					System.out.println("$#12201#"); int count = Math.min((currentPage * pageSize), resultCount);
					System.out.println("$#12202#"); paginationData.setCountByPage(count);

					System.out.println("$#12203#"); paginationData.setTotalCount( resultCount );
								System.out.println("$#12204#"); return paginationData;
    }
}
