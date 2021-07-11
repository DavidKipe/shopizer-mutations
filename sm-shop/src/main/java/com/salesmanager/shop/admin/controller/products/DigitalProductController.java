package com.salesmanager.shop.admin.controller.products;

import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.file.DigitalProductService;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.file.DigitalProduct;
import com.salesmanager.core.model.content.FileContentType;
import com.salesmanager.core.model.content.InputContentFile;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.shop.admin.controller.ControllerConstants;
import com.salesmanager.shop.admin.model.digital.ProductFiles;
import com.salesmanager.shop.admin.model.web.Menu;
import com.salesmanager.shop.constants.Constants;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.util.*;

@Controller
public class DigitalProductController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DigitalProductController.class);
	
	@Inject
	private ProductService productService;
	
	@Inject
	private DigitalProductService digitalProductService;
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value={"/admin/products/digitalProduct.html"}, method=RequestMethod.GET)
	public String getDigitalProduct(@RequestParam("id") long productId, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		System.out.println("$#6052#"); this.setMenu(model, request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);

		Product product = productService.getById(productId);
		
		System.out.println("$#6053#"); if(product==null || product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
			System.out.println("$#6055#"); return "redirect:/admin/products/products.html";
		}
		
		model.addAttribute("product", product);

		DigitalProduct digitalProduct = digitalProductService.getByProduct(store, product);

		model.addAttribute("digitalProduct", digitalProduct);
		System.out.println("$#6056#"); return ControllerConstants.Tiles.Product.digitalProduct;
		
	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/product/saveDigitalProduct.html", method=RequestMethod.POST)
	public String saveFile(@ModelAttribute(value="productFiles") @Valid final ProductFiles productFiles, final BindingResult bindingResult,final Model model, final HttpServletRequest request) throws Exception{
	    
		System.out.println("$#6057#"); this.setMenu(model, request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);

		Product product = productService.getById(productFiles.getProduct().getId());
		DigitalProduct digitalProduct = new DigitalProduct();
		System.out.println("$#6058#"); if(product==null || product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
			System.out.println("$#6060#"); return "redirect:/admin/products/products.html";
		}
		
		System.out.println("$#6061#"); digitalProduct.setProduct(product);
		model.addAttribute("product", product);
		model.addAttribute("digitalProduct", digitalProduct);
	    
		System.out.println("$#6062#"); if (bindingResult.hasErrors()) {
	        LOGGER.info( "Found {} Validation errors", bindingResult.getErrorCount());
									System.out.println("$#6063#"); return ControllerConstants.Tiles.Product.digitalProduct;
        }

	    
	    final List<InputContentFile> contentFilesList=new ArrayList<InputContentFile>();
								System.out.println("$#6064#"); if(CollectionUtils.isNotEmpty( productFiles.getFile() )){
            LOGGER.info("Saving {} product files for merchant {}",productFiles.getFile().size(),store.getId());
            for(final MultipartFile multipartFile:productFiles.getFile()){
																System.out.println("$#6065#"); if(!multipartFile.isEmpty()){
                    ByteArrayInputStream inputStream = new ByteArrayInputStream( multipartFile.getBytes() );
                    InputContentFile cmsContentImage = new InputContentFile();
																				System.out.println("$#6066#"); cmsContentImage.setFileName(multipartFile.getOriginalFilename() );
																				System.out.println("$#6067#"); cmsContentImage.setFileContentType( FileContentType.PRODUCT_DIGITAL );
																				System.out.println("$#6068#"); cmsContentImage.setFile( inputStream );
                    contentFilesList.add( cmsContentImage);
                }
            }
            
												System.out.println("$#6069#"); if(CollectionUtils.isNotEmpty( contentFilesList )){

													System.out.println("$#6070#"); digitalProduct.setProductFileName(contentFilesList.get(0).getFileName());
													System.out.println("$#6071#"); digitalProductService.addProductFile(product, digitalProduct, contentFilesList.get(0));
            	
            	//refresh digital product
            	digitalProduct = digitalProductService.getByProduct(store, product);
   
            }
        }
        
        
        model.addAttribute("success","success");
								System.out.println("$#6072#"); return ControllerConstants.Tiles.Product.digitalProduct;
	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/product/removeDigitalProduct.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> removeFile(@RequestParam("fileId") long fileId, HttpServletRequest request, HttpServletResponse response, Locale locale) {

		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		AjaxResponse resp = new AjaxResponse();
		
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#6073#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		
		try {
			
			DigitalProduct digitalProduct = digitalProductService.getById(fileId);
			
			//validate store
			System.out.println("$#6074#"); if(digitalProduct==null) {
				System.out.println("$#6075#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#6076#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			Product product = digitalProduct.getProduct();
			System.out.println("$#6077#"); if(product.getMerchantStore().getId().intValue()!= store.getId().intValue()) {
				System.out.println("$#6078#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#6079#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#6080#"); digitalProductService.delete(digitalProduct);
			System.out.println("$#6081#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);
		
		
		} catch (Exception e) {
			LOGGER.error("Error while deleting product", e);
			System.out.println("$#6082#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6083#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#6084#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
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
