package com.salesmanager.shop.admin.controller.products;

import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.review.ProductReviewService;
import com.salesmanager.core.business.utils.ajax.AjaxPageableResponse;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.review.ProductReview;
import com.salesmanager.core.model.catalog.product.review.ProductReviewDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.admin.controller.ControllerConstants;
import com.salesmanager.shop.admin.model.web.Menu;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.utils.LabelUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
public class ProductReviewController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductReviewController.class);
	
	@Inject
	private ProductService productService;
	
	@Inject
	private ProductReviewService productReviewService;
	
	@Inject
	LabelUtils messages;
	
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/reviews.html", method=RequestMethod.GET)
	public String displayProductReviews(@RequestParam("id") long productId,Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		
		System.out.println("$#7001#"); setMenu(model, request);
		

		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		Product product = productService.getProductWithOnlyMerchantStoreById(productId);
		
		System.out.println("$#7002#"); if(product==null) {
			System.out.println("$#7003#"); return "redirect:/admin/products/products.html";
		}
		
		System.out.println("$#7004#"); if(product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
			System.out.println("$#7005#"); return "redirect:/admin/products/products.html";
		}
		
		
		model.addAttribute("product", product);
		
		System.out.println("$#7006#"); return ControllerConstants.Tiles.Product.productReviews;

	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/reviews/paging.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> pageProductReviews(HttpServletRequest request, HttpServletResponse response) {

		String sProductId = request.getParameter("productId");
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		
		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#7007#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		Long productId;
		Product product = null;
		
		try {
			productId = Long.parseLong(sProductId);
		} catch (Exception e) {
			System.out.println("$#7008#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#7009#"); resp.setErrorString("Product id is not valid");
			String returnString = resp.toJSONString();
			System.out.println("$#7010#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		}

		
		try {

			product = productService.getProductWithOnlyMerchantStoreById(productId);

			
			System.out.println("$#7011#"); if(product==null) {
				System.out.println("$#7012#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				System.out.println("$#7013#"); resp.setErrorString("Product id is not valid");
				String returnString = resp.toJSONString();
				System.out.println("$#7014#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#7015#"); if(product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#7016#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				System.out.println("$#7017#"); resp.setErrorString("Product id is not valid");
				String returnString = resp.toJSONString();
				System.out.println("$#7018#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			
			Language language = (Language)request.getAttribute("LANGUAGE");

			
			List<ProductReview> reviews = productReviewService.getByProduct(product);
			


			for(ProductReview review : reviews) {
				Map entry = new HashMap();
				entry.put("reviewId", review.getId());
				entry.put("rating", review.getReviewRating().intValue());
				Set<ProductReviewDescription> descriptions = review.getDescriptions();
				String reviewDesc= "";
				System.out.println("$#7019#"); if(!CollectionUtils.isEmpty(descriptions)) {
					reviewDesc = descriptions.iterator().next().getDescription();
				}
				//for(ProductReviewDescription description : descriptions){
				//	if(description.getLanguage().getCode().equals(language.getCode())) {
				//		reviewDesc = description.getDescription();
				//	}
				//}
				entry.put("description", reviewDesc);
				System.out.println("$#7020#"); resp.addDataEntry(entry);
			}

			System.out.println("$#7021#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_SUCCESS);
		
		} catch (Exception e) {
			LOGGER.error("Error while paging products", e);
			System.out.println("$#7022#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#7023#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#7024#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);


	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/reviews/remove.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> deleteProductReview(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		String sReviewid = request.getParameter("reviewId");

		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#7025#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		
		try {
			
			Long reviewId = Long.parseLong(sReviewid);

			
			ProductReview review = productReviewService.getById(reviewId);
			

			System.out.println("$#7026#"); if(review==null || review.getProduct().getMerchantStore().getId().intValue()!=store.getId()) {

				System.out.println("$#7028#"); resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
				System.out.println("$#7029#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#7030#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			} 
			

			System.out.println("$#7031#"); productReviewService.delete(review);
			
			
			System.out.println("$#7032#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);

		
		
		} catch (Exception e) {
			LOGGER.error("Error while deleting review", e);
			System.out.println("$#7033#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#7034#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#7035#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	
	
	private void setMenu(Model model, HttpServletRequest request) throws Exception {
		
		//display menu
		Map<String,String> activeMenus = new HashMap<String,String>();
		activeMenus.put("catalogue", "catalogue");
		activeMenus.put("catalogue-products", "catalogue-products");
		
		@SuppressWarnings("unchecked")
		Map<String, Menu> menus = (Map<String, Menu>)request.getAttribute("MENUMAP");
		
		Menu currentMenu = (Menu)menus.get("catalogue");
		model.addAttribute("currentMenu",currentMenu);
		model.addAttribute("activeMenus",activeMenus);
		//
		
	}
	
	

}
