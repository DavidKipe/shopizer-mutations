package com.salesmanager.shop.admin.controller.products;

import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.price.ProductPriceService;
import com.salesmanager.core.business.utils.ProductPriceUtils;
import com.salesmanager.core.business.utils.ajax.AjaxPageableResponse;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.availability.ProductAvailability;
import com.salesmanager.core.model.catalog.product.price.ProductPrice;
import com.salesmanager.core.model.catalog.product.price.ProductPriceDescription;
import com.salesmanager.core.model.catalog.product.price.ProductPriceType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.admin.controller.ControllerConstants;
import com.salesmanager.shop.admin.model.web.Menu;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.utils.DateUtil;
import com.salesmanager.shop.utils.LabelUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;

@Controller
public class ProductPriceController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductPriceController.class);
	
	@Inject
	private ProductService productService;
	
	@Inject
	private ProductPriceService productPriceService;
	
	@Inject
	private ProductPriceUtils priceUtil;
	
	@Inject
	LabelUtils messages;
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/prices.html", method=RequestMethod.GET)
	public String getProductPrices(@RequestParam("id") long productId,Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		System.out.println("$#6897#"); setMenu(model,request);
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);

		//get the product and validate it belongs to the current merchant
		Product product = productService.getById(productId);
		
		System.out.println("$#6898#"); if(product==null) {
			System.out.println("$#6899#"); return "redirect:/admin/products/products.html";
		}
		
		System.out.println("$#6900#"); if(product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
			System.out.println("$#6901#"); return "redirect:/admin/products/products.html";
		}
		
		ProductAvailability productAvailability = null;
		for(ProductAvailability availability : product.getAvailabilities()) {
			System.out.println("$#6902#"); if(availability.getRegion().equals(com.salesmanager.core.business.constants.Constants.ALL_REGIONS)) {
				productAvailability = availability;
			}
		}

		model.addAttribute("product",product);
		model.addAttribute("availability",productAvailability);

		System.out.println("$#6903#"); return ControllerConstants.Tiles.Product.productPrices;
		
	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/prices/paging.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> pagePrices(HttpServletRequest request, HttpServletResponse response) {

		String sProductId = request.getParameter("productId");
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		Language language = (Language)request.getAttribute("LANGUAGE");
		
		
		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#6904#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		Long productId;
		Product product = null;
		
		try {
			productId = Long.parseLong(sProductId);
		} catch (Exception e) {
			System.out.println("$#6905#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6906#"); resp.setErrorString("Product id is not valid");
			String returnString = resp.toJSONString();
			System.out.println("$#6907#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		}

		
		try {

			product = productService.getById(productId);

			
			System.out.println("$#6908#"); if(product==null) {
				System.out.println("$#6909#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				System.out.println("$#6910#"); resp.setErrorString("Product id is not valid");
				String returnString = resp.toJSONString();
				System.out.println("$#6911#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#6912#"); if(product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#6913#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				System.out.println("$#6914#"); resp.setErrorString("Product id is not valid");
				String returnString = resp.toJSONString();
				System.out.println("$#6915#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			ProductAvailability defaultAvailability = null;
			
			Set<ProductAvailability> availabilities = product.getAvailabilities();

			//get default availability
			for(ProductAvailability availability : availabilities) {
				System.out.println("$#6916#"); if(availability.getRegion().equals(com.salesmanager.core.business.constants.Constants.ALL_REGIONS)) {
					defaultAvailability = availability;
					break;
				}
			}
			
			System.out.println("$#6917#"); if(defaultAvailability==null) {
				System.out.println("$#6918#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				System.out.println("$#6919#"); resp.setErrorString("Product id is not valid");
				String returnString = resp.toJSONString();
				System.out.println("$#6920#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			Set<ProductPrice> prices = defaultAvailability.getPrices();
			
			
			for(ProductPrice price : prices) {
				Map entry = new HashMap();
				entry.put("priceId", price.getId());
				
				
				String priceName = "";
				Set<ProductPriceDescription> descriptions = price.getDescriptions();
				System.out.println("$#6921#"); if(descriptions!=null) {
					for(ProductPriceDescription description : descriptions) {
						System.out.println("$#6922#"); if(description.getLanguage().getCode().equals(language.getCode())) {
							priceName = description.getName(); 
						}
					}
				}
				

				entry.put("name", priceName);
				entry.put("price", priceUtil.getAdminFormatedAmountWithCurrency(store,price.getProductPriceAmount()));
				entry.put("specialPrice", priceUtil.getAdminFormatedAmountWithCurrency(store,price.getProductPriceSpecialAmount()));
				
				String discount = "";
				System.out.println("$#6923#"); if(priceUtil.hasDiscount(price)) {
					discount = priceUtil.getAdminFormatedAmountWithCurrency(store,price.getProductPriceAmount());
				}
				entry.put("special", discount);
				
				System.out.println("$#6924#"); resp.addDataEntry(entry);
			}

			System.out.println("$#6925#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_SUCCESS);
		
		} catch (Exception e) {
			LOGGER.error("Error while paging products", e);
			System.out.println("$#6926#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6927#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#6928#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	
	
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/price/edit.html", method=RequestMethod.GET)
	public String editProductPrice(@RequestParam("id") long productPriceId, @RequestParam("productId") long productId,Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		Product product = productService.getById(productId);
		
		System.out.println("$#6929#"); if(product==null) {
			System.out.println("$#6930#"); return "redirect:/admin/products/products.html";
		}
		
		System.out.println("$#6931#"); if(product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
			System.out.println("$#6932#"); return "redirect:/admin/products/products.html";
		}
		
		
		System.out.println("$#6933#"); setMenu(model,request);
		System.out.println("$#6934#"); return displayProductPrice(product, productPriceId, model, request, response);
		
	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/price/create.html", method=RequestMethod.GET)
	public String displayCreateProductPrice(@RequestParam("productId") long productId,@RequestParam("availabilityId") long avilabilityId, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		Product product = productService.getById(productId);
		System.out.println("$#6935#"); if(product==null) {
			System.out.println("$#6936#"); return "redirect:/admin/products/products.html";
		}
		
		System.out.println("$#6937#"); if(product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
			System.out.println("$#6938#"); return "redirect:/admin/products/products.html";
		}
		
		System.out.println("$#6939#"); setMenu(model,request);
		System.out.println("$#6940#"); return displayProductPrice(product, null, model, request, response);


		
	}
	
	private String displayProductPrice(Product product, Long productPriceId, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

	
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);

		com.salesmanager.shop.admin.model.catalog.ProductPrice pprice = new com.salesmanager.shop.admin.model.catalog.ProductPrice();
		
		ProductPrice productPrice = null;
		ProductAvailability productAvailability = null;
		
		System.out.println("$#6941#"); if(productPriceId!=null) {
		
			Set<ProductAvailability> availabilities = product.getAvailabilities();
	
			//get default availability
			for(ProductAvailability availability : availabilities) {
				System.out.println("$#6942#"); if(availability.getRegion().equals(com.salesmanager.core.business.constants.Constants.ALL_REGIONS)) {//TODO to be updated when multiple regions is implemented
					productAvailability = availability;
					Set<ProductPrice> prices = availability.getPrices();
					for(ProductPrice price : prices) {
						System.out.println("$#6943#"); if(price.getId().longValue()==productPriceId.longValue()) {
							productPrice = price;
							System.out.println("$#6944#"); if(price.getProductPriceSpecialStartDate()!=null) {
								System.out.println("$#6945#"); pprice.setProductPriceSpecialStartDate(DateUtil.formatDate(price.getProductPriceSpecialStartDate()));
							}
							System.out.println("$#6946#"); if(price.getProductPriceSpecialEndDate()!=null) {
								System.out.println("$#6947#"); pprice.setProductPriceSpecialEndDate(DateUtil.formatDate(price.getProductPriceSpecialEndDate()));
							}
							System.out.println("$#6948#"); pprice.setPriceText(priceUtil.getAdminFormatedAmount(store, price.getProductPriceAmount()));
							System.out.println("$#6949#"); if(price.getProductPriceSpecialAmount()!=null) {
								System.out.println("$#6950#"); pprice.setSpecialPriceText(priceUtil.getAdminFormatedAmount(store, price.getProductPriceSpecialAmount()));
							}
							break;
						}
					}
				}
			}
		
		}	
		
		System.out.println("$#6951#"); if(productPrice==null) {
			productPrice = new ProductPrice();
			System.out.println("$#6952#"); productPrice.setProductPriceType(ProductPriceType.ONE_TIME);
		}
		
		//descriptions
		List<Language> languages = store.getLanguages();
		
		Set<ProductPriceDescription> productPriceDescriptions = productPrice.getDescriptions();
		List<ProductPriceDescription> descriptions = new ArrayList<ProductPriceDescription>();
		for(Language l : languages) {
			ProductPriceDescription productPriceDesc = null;
			for(ProductPriceDescription desc : productPriceDescriptions) {
				Language lang = desc.getLanguage();
				System.out.println("$#6953#"); if(lang.getCode().equals(l.getCode())) {
					productPriceDesc = desc;
				}
			}
			
			System.out.println("$#6954#"); if(productPriceDesc==null) {
				productPriceDesc = new ProductPriceDescription();
				System.out.println("$#6955#"); productPriceDesc.setLanguage(l);
				productPriceDescriptions.add(productPriceDesc);
			}	
			descriptions.add(productPriceDesc);
		}
		
		
		System.out.println("$#6956#"); if(productAvailability==null) {
			Set<ProductAvailability> availabilities = product.getAvailabilities();
			for(ProductAvailability availability : availabilities) {
				System.out.println("$#6957#"); if(availability.getRegion().equals(com.salesmanager.core.business.constants.Constants.ALL_REGIONS)) {//TODO to be updated when multiple regions is implemented
					productAvailability = availability;
					break;
				}
			}
		}
		
		System.out.println("$#6958#"); pprice.setDescriptions(descriptions);
		System.out.println("$#6959#"); pprice.setProductAvailability(productAvailability);
		System.out.println("$#6960#"); pprice.setPrice(productPrice);
		System.out.println("$#6961#"); pprice.setProduct(product);
		

		model.addAttribute("product",product);
		//model.addAttribute("descriptions",descriptions);
		model.addAttribute("price",pprice);
		//model.addAttribute("availability",productAvailability);
		
		System.out.println("$#6962#"); return ControllerConstants.Tiles.Product.productPrice;
	}
	
	
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/price/save.html", method=RequestMethod.POST)
	public String saveProductPrice(@Valid @ModelAttribute("price") com.salesmanager.shop.admin.model.catalog.ProductPrice price, BindingResult result, Model model, HttpServletRequest request, Locale locale) throws Exception {
		
		//dates after save
		
		System.out.println("$#6963#"); setMenu(model,request);
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		Product product = price.getProduct();
		Product dbProduct = productService.getById(product.getId());
		System.out.println("$#6964#"); if(store.getId().intValue()!=dbProduct.getMerchantStore().getId().intValue()) {
			System.out.println("$#6965#"); return "redirect:/admin/products/products.html";
		}
		
		model.addAttribute("product",dbProduct);
		
		//validate price
		BigDecimal submitedPrice = null;
		try {
			submitedPrice = priceUtil.getAmount(price.getPriceText());
		} catch (Exception e) {
			ObjectError error = new ObjectError("productPrice",messages.getMessage("NotEmpty.product.productPrice", locale));
			System.out.println("$#6966#"); result.addError(error);
		}
		
		//validate discount price
		BigDecimal submitedDiscountPrice = null;
		
		System.out.println("$#6967#"); if(!StringUtils.isBlank(price.getSpecialPriceText())) {
			try {
				submitedDiscountPrice = priceUtil.getAmount(price.getSpecialPriceText());
			} catch (Exception e) {
				ObjectError error = new ObjectError("productSpecialPrice",messages.getMessage("NotEmpty.product.productPrice", locale));
				System.out.println("$#6968#"); result.addError(error);
			}
		}
		
		//validate start date
		System.out.println("$#6969#"); if(!StringUtils.isBlank(price.getProductPriceSpecialStartDate())) {
			try {
				Date startDate = DateUtil.getDate(price.getProductPriceSpecialStartDate());
				System.out.println("$#6970#"); price.getPrice().setProductPriceSpecialStartDate(startDate);
			} catch (Exception e) {
				ObjectError error = new ObjectError("productPriceSpecialStartDate",messages.getMessage("message.invalid.date", locale));
				System.out.println("$#6971#"); result.addError(error);
			}
		}
		
		System.out.println("$#6972#"); if(!StringUtils.isBlank(price.getProductPriceSpecialEndDate())) {
			try {
				Date endDate = DateUtil.getDate(price.getProductPriceSpecialEndDate());
				System.out.println("$#6973#"); price.getPrice().setProductPriceSpecialEndDate(endDate);
			} catch (Exception e) {
				ObjectError error = new ObjectError("productPriceSpecialEndDate",messages.getMessage("message.invalid.date", locale));
				System.out.println("$#6974#"); result.addError(error);
			}
		}
		
		
		System.out.println("$#6975#"); if (result.hasErrors()) {
			System.out.println("$#6976#"); return ControllerConstants.Tiles.Product.productPrice;
		}
		

		System.out.println("$#6977#"); price.getPrice().setProductPriceAmount(submitedPrice);
		System.out.println("$#6978#"); if(!StringUtils.isBlank(price.getSpecialPriceText())) {
			System.out.println("$#6979#"); price.getPrice().setProductPriceSpecialAmount(submitedDiscountPrice);
		}
		
		ProductAvailability productAvailability = null;
		
		Set<ProductAvailability> availabilities = dbProduct.getAvailabilities();
		for(ProductAvailability availability : availabilities) {
			
			System.out.println("$#6980#"); if(availability.getId().longValue()==price.getProductAvailability().getId().longValue()) {
				productAvailability = availability;
				break;
			}
			
			
		}
		
		
		
		
		Set<ProductPriceDescription> descriptions = new HashSet<ProductPriceDescription>();
		System.out.println("$#6982#"); System.out.println("$#6981#"); if(price.getDescriptions()!=null && price.getDescriptions().size()>0) {
			
			for(ProductPriceDescription description : price.getDescriptions()) {
				System.out.println("$#6984#"); description.setProductPrice(price.getPrice());
				descriptions.add(description);
				System.out.println("$#6985#"); description.setProductPrice(price.getPrice());
			}
		}
		
		System.out.println("$#6986#"); price.getPrice().setDescriptions(descriptions);
		System.out.println("$#6987#"); price.getPrice().setProductAvailability(productAvailability);
		
		System.out.println("$#6988#"); productPriceService.saveOrUpdate(price.getPrice());
		model.addAttribute("success","success");
		
		System.out.println("$#6989#"); return ControllerConstants.Tiles.Product.productPrice;
		
	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/price/remove.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> deleteProductPrice(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		String sPriceid = request.getParameter("priceId");

		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#6990#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		
		try {
			
			Long priceId = Long.parseLong(sPriceid);
			ProductPrice price = productPriceService.getById(priceId);
			

			System.out.println("$#6991#"); if(price==null || price.getProductAvailability().getProduct().getMerchantStore().getId().intValue()!=store.getId()) {

				System.out.println("$#6993#"); resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
				System.out.println("$#6994#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#6995#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			} 
			
			System.out.println("$#6996#"); productPriceService.delete(price);
			
			
			System.out.println("$#6997#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);

		
		
		} catch (Exception e) {
			LOGGER.error("Error while deleting product price", e);
			System.out.println("$#6998#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6999#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#7000#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
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
