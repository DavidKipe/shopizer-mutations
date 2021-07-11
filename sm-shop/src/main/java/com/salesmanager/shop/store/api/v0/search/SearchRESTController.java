package com.salesmanager.shop.store.api.v0.search;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.salesmanager.core.business.services.merchant.MerchantStoreService;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.shop.store.controller.search.facade.SearchFacade;


/**
 * Searching and indexing products
 * @author c.samson
 *
 */

@Controller
@RequestMapping("/services")
public class SearchRESTController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchRESTController.class);
	
	@Inject
	private MerchantStoreService merchantStoreService;
	
	@Inject
	private SearchFacade searchFacade;
	
	@RequestMapping( value="/private/{store}/search/index", method=RequestMethod.GET)
	@ResponseBody
	public AjaxResponse indexProducts(@PathVariable String store, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		AjaxResponse resp = new AjaxResponse();
		
		try {
			
			MerchantStore merchantStore = merchantStoreService.getByCode(store);
			System.out.println("$#11466#"); if(merchantStore==null) {
				LOGGER.error("Merchant store is null for code " + store);
				System.out.println("$#11467#"); resp.setStatus(500);
				System.out.println("$#11468#"); resp.setErrorString("Merchant store is null for code " + store);
				System.out.println("$#11469#"); return resp;
			}

			LOGGER.debug("Index all data : " + store);
			System.out.println("$#11470#"); searchFacade.indexAllData(merchantStore);
			System.out.println("$#11471#"); response.setStatus(200);
			System.out.println("$#11472#"); resp.setStatus(200);
			
		} catch(Exception e) {
			System.out.println("$#11473#"); resp.setStatus(500);
			System.out.println("$#11474#"); resp.setErrorMessage(e);
			System.out.println("$#11475#"); response.sendError(503, "Exception while indexing all data for store " + store + " " + e.getMessage());
		}

		System.out.println("$#11476#"); return resp;
		
	}

}
