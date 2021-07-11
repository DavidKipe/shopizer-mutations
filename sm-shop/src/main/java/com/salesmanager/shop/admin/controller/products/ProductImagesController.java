package com.salesmanager.shop.admin.controller.products;

import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.image.ProductImageService;
import com.salesmanager.core.business.utils.ajax.AjaxPageableResponse;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.image.ProductImage;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.shop.admin.controller.ControllerConstants;
import com.salesmanager.shop.admin.model.content.ProductImages;
import com.salesmanager.shop.admin.model.web.Menu;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.utils.ImageFilePath;
import com.salesmanager.shop.utils.LabelUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

@Controller
public class ProductImagesController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductImagesController.class);
	
	

	@Inject
	private ProductService productService;
	

	@Inject
	private ProductImageService productImageService;
	
	@Inject
	private LabelUtils messages;
	
	@Inject
	@Qualifier("img")
	private ImageFilePath imageUtils;
	

	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/images/list.html", method=RequestMethod.GET)
	public String displayProductImages(@RequestParam("id") long productId, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		System.out.println("$#6733#"); setMenu(model,request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		Product product = productService.getById(productId);
		
		System.out.println("$#6734#"); if(product==null) {
			System.out.println("$#6735#"); return "redirect:/admin/products/products.html";
		}
		
		System.out.println("$#6736#"); if(product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
			System.out.println("$#6737#"); return "redirect:/admin/products/products.html";
		}
		
		model.addAttribute("product",product);
		System.out.println("$#6738#"); return ControllerConstants.Tiles.Product.productImages;
		
	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/images/url/list.html", method=RequestMethod.GET)
	public String displayProductImagesUrl(@RequestParam("id") long productId, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		System.out.println("$#6739#"); setMenu(model,request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		Product product = productService.getById(productId);
		
		System.out.println("$#6740#"); if(product==null) {
			System.out.println("$#6741#"); return "redirect:/admin/products/products.html";
		}
		
		System.out.println("$#6742#"); if(product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
			System.out.println("$#6743#"); return "redirect:/admin/products/products.html";
		}
		
        Map< String, String > mediaTypes = new HashMap<String, String>();  
        mediaTypes.put("0", "IMAGE");  
        mediaTypes.put("1", "VIDEO");   
		
		ProductImage productImage = new ProductImage();
		
		model.addAttribute("productImage", productImage);
		model.addAttribute("product",product);
		model.addAttribute("mediaTypes",mediaTypes);
		System.out.println("$#6744#"); return ControllerConstants.Tiles.Product.productImagesUrl;
		
	}
	
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/images/page.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> pageProductImages(HttpServletRequest request, HttpServletResponse response) {

		String sProductId = request.getParameter("productId");
		
		
		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#6745#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		Long productId;
		Product product = null;
		
		try {
			productId = Long.parseLong(sProductId);
		} catch (Exception e) {
			System.out.println("$#6746#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6747#"); resp.setErrorString("Product id is not valid");
			String returnString = resp.toJSONString();
			System.out.println("$#6748#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		}

		
		try {
			
			
			product = productService.getById(productId);

			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			
			System.out.println("$#6749#"); if(product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#6750#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				System.out.println("$#6751#"); resp.setErrorString("Merchant id is not valid");
				String returnString = resp.toJSONString();
				System.out.println("$#6752#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}

			Set<ProductImage> images = product.getImages();
			
			System.out.println("$#6753#"); if(images!=null) {
				
				for(ProductImage image : images) {
					
						String imagePath = imageUtils.buildProductImageUtils(store, product, image.getProductImage());
						
						Map entry = new HashMap();
						//entry.put("picture", new StringBuilder().append(request.getContextPath()).append(imagePath).toString());
						entry.put("picture", imagePath);
						entry.put("name", image.getProductImage());
						entry.put("id",image.getId());
						entry.put("defaultImageCheckmark", image.isDefaultImage() ? "/resources/img/admin/checkmark_checked.png" : "/resources/img/admin/checkmark_unchecked.png");
						
						System.out.println("$#6755#"); resp.addDataEntry(entry);
					
				}
			}

			System.out.println("$#6756#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_SUCCESS);
		
		} catch (Exception e) {
			LOGGER.error("Error while paging products", e);
			System.out.println("$#6757#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6758#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#6759#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);


	}
	
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/images/url/page.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> pageProductImagesUrl(HttpServletRequest request, HttpServletResponse response) {

		String sProductId = request.getParameter("productId");
		
		
		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#6760#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		Long productId;
		Product product = null;
		
		try {
			productId = Long.parseLong(sProductId);
		} catch (Exception e) {
			System.out.println("$#6761#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6762#"); resp.setErrorString("Product id is not valid");
			String returnString = resp.toJSONString();
			System.out.println("$#6763#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		}

		
		try {
			
			
			product = productService.getById(productId);

			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);

			System.out.println("$#6764#"); if(product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#6765#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				System.out.println("$#6766#"); resp.setErrorString("Merchant id is not valid");
				String returnString = resp.toJSONString();
				System.out.println("$#6767#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			Set<ProductImage> images = product.getImages();
			
			System.out.println("$#6768#"); if(images!=null) {
				
				for(ProductImage image : images) {
					
					System.out.println("$#6769#"); if(!StringUtils.isBlank(image.getProductImageUrl())) {

						Map entry = new HashMap();
						entry.put("image", image.getProductImageUrl());
						entry.put("url", image.getProductImageUrl());
						entry.put("default", image.isDefaultImage());
						entry.put("id",image.getId());
						
						System.out.println("$#6770#"); resp.addDataEntry(entry);
					
					}
				}

			}



			System.out.println("$#6771#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_SUCCESS);
		
		} catch (Exception e) {
			LOGGER.error("Error while paging products", e);
			System.out.println("$#6772#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6773#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#6774#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);


	}


	
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/images/save.html", method=RequestMethod.POST)
	public String saveProductImages(@ModelAttribute(value="productImages") @Valid final ProductImages productImages, final BindingResult bindingResult,final Model model, final HttpServletRequest request,Locale locale) throws Exception{
	    
	    
		System.out.println("$#6775#"); this.setMenu(model, request);

		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);

		Product product = productService.getById(productImages.getProductId());
		model.addAttribute("product",product);
		System.out.println("$#6776#"); if(product==null) {
			FieldError error = new FieldError("productImages","image",messages.getMessage("message.error", locale));
			System.out.println("$#6777#"); bindingResult.addError(error);
			System.out.println("$#6778#"); return ControllerConstants.Tiles.Product.productImages;
		}
		
		System.out.println("$#6779#"); if(product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
			FieldError error = new FieldError("productImages","image",messages.getMessage("message.error", locale));
			System.out.println("$#6780#"); bindingResult.addError(error);
		}
		
		System.out.println("$#6781#"); if (bindingResult.hasErrors()) {
	        LOGGER.info( "Found {} Validation errors", bindingResult.getErrorCount());
								System.out.println("$#6782#"); return ControllerConstants.Tiles.Product.productImages;
	       
        }
		
	    final List<ProductImage> contentImagesList=new ArrayList<ProductImage>();
								System.out.println("$#6783#"); if(CollectionUtils.isNotEmpty( productImages.getFile() )){
            LOGGER.info("Saving {} content images for merchant {}",productImages.getFile().size(),store.getId());
            for(final MultipartFile multipartFile:productImages.getFile()){
																System.out.println("$#6784#"); if(!multipartFile.isEmpty()){
                	ProductImage productImage = new ProductImage();

																	System.out.println("$#6785#"); productImage.setImage(multipartFile.getInputStream());
																				System.out.println("$#6786#"); productImage.setProductImage(multipartFile.getOriginalFilename() );
																				System.out.println("$#6787#"); productImage.setProduct(product);
																				System.out.println("$#6788#"); productImage.setDefaultImage(false);//default image is uploaded in the product details
                    
                    contentImagesList.add( productImage);
                }
            }
            
												System.out.println("$#6789#"); if(CollectionUtils.isNotEmpty( contentImagesList )){
													System.out.println("$#6790#"); productImageService.addProductImages(product, contentImagesList);
            }
            
        }
		
        
        //reload
        product = productService.getById(productImages.getProductId());
        model.addAttribute("product",product);
        model.addAttribute("success","success");
        
								System.out.println("$#6791#"); return ControllerConstants.Tiles.Product.productImages;
	}
	


	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/images/url/save.html", method=RequestMethod.POST)
	public String saveProductImagesUrl(@ModelAttribute(value="productImage") @Valid final ProductImage productImage, final BindingResult bindingResult,final Model model, final HttpServletRequest request,Locale locale) throws Exception{
	    
	    
		System.out.println("$#6792#"); this.setMenu(model, request);

		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
        Map< String, String > mediaTypes = new HashMap<String, String>();  
        mediaTypes.put("0", "IMAGE");  
        mediaTypes.put("1", "VIDEO");   

		model.addAttribute("productImage", productImage);
		model.addAttribute("mediaTypes",mediaTypes);

		Product product = productService.getById(productImage.getId());
		model.addAttribute("product",product);
		System.out.println("$#6793#"); if(product==null) {
			FieldError error = new FieldError("productImages","image",messages.getMessage("message.error", locale));
			System.out.println("$#6794#"); bindingResult.addError(error);
			System.out.println("$#6795#"); return ControllerConstants.Tiles.Product.productImagesUrl;
		}
		
		System.out.println("$#6796#"); if(product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
			FieldError error = new FieldError("productImages","image",messages.getMessage("message.error", locale));
			System.out.println("$#6797#"); bindingResult.addError(error);
		}
		
		model.addAttribute("product",product);
		
		System.out.println("$#6798#"); if (bindingResult.hasErrors()) {
	        LOGGER.info( "Found {} Validation errors", bindingResult.getErrorCount());
								System.out.println("$#6799#"); return ControllerConstants.Tiles.Product.productImagesUrl;
        }
		
		System.out.println("$#6800#"); productImage.setProduct(product);
		System.out.println("$#6801#"); productImage.setId(null);
		
		System.out.println("$#6802#"); productImageService.saveOrUpdate(productImage);
        model.addAttribute("product",product);
        model.addAttribute("success","success");
        
								System.out.println("$#6803#"); return ControllerConstants.Tiles.Product.productImagesUrl;
	}

	
	

		
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/images/remove.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> deleteImage(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		String sImageId = request.getParameter("id");

		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#6804#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		
		try {

				
			Long imageId = Long.parseLong(sImageId);

			
			ProductImage productImage = productImageService.getById(imageId);
			System.out.println("$#6805#"); if(productImage==null) {
				System.out.println("$#6806#"); resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
				System.out.println("$#6807#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#6808#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#6809#"); if(productImage.getProduct().getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#6810#"); resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
				System.out.println("$#6811#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#6812#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#6813#"); productImageService.removeProductImage(productImage);

			System.out.println("$#6814#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);

		
		
		} catch (Exception e) {
			LOGGER.error("Error while deleting product price", e);
			System.out.println("$#6815#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6816#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#6817#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/images/defaultImage.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> setDefaultImage(final HttpServletRequest request, 
												final HttpServletResponse response, 
												final Locale locale) {
		final String sImageId = request.getParameter("id");
		final MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		final AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#6818#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		try {
			final Long imageId = Long.parseLong(sImageId);
			final ProductImage productImage = productImageService.getById(imageId);
			
			System.out.println("$#6819#"); if (productImage == null) {
				System.out.println("$#6820#"); resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
				System.out.println("$#6821#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#6822#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#6823#"); if (productImage.getProduct().getMerchantStore().getId().intValue() != store.getId().intValue()) {
				System.out.println("$#6824#"); resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
				System.out.println("$#6825#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#6826#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#6827#"); productImage.setDefaultImage(true);
			System.out.println("$#6828#"); productImageService.saveOrUpdate(productImage);
			
			final Set<ProductImage> images = productService.getById(productImage.getProduct().getId()).getImages();
			for (final ProductImage image : images) {
				System.out.println("$#6829#"); if (image.getId() != productImage.getId()) {
					System.out.println("$#6830#"); image.setDefaultImage(false);
					System.out.println("$#6831#"); productImageService.saveOrUpdate(image);
				}
			}
			
			System.out.println("$#6832#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);
		} catch (final Exception e) {
			LOGGER.error("Error while set default image", e);
			System.out.println("$#6833#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6834#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#6835#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
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
