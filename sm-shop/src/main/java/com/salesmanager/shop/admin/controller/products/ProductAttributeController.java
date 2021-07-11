package com.salesmanager.shop.admin.controller.products;

import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductAttributeService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductOptionService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductOptionValueService;
import com.salesmanager.core.business.utils.ProductPriceUtils;
import com.salesmanager.core.business.utils.ajax.AjaxPageableResponse;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.attribute.*;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.admin.model.web.Menu;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.utils.LabelUtils;
import org.apache.commons.lang3.RandomStringUtils;
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
public class ProductAttributeController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductAttributeController.class);
	
	private final static String TEXT_OPTION = "text";
	
	@Inject
	private ProductAttributeService productAttributeService;
	
	@Inject
	private ProductService productService;
	
	@Inject
	private ProductPriceUtils priceUtil;
	
	@Inject
	ProductOptionService productOptionService;
	
	@Inject
	ProductOptionValueService productOptionValueService;
	
	@Inject
	LabelUtils messages;
	

	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/attributes/list.html", method=RequestMethod.GET)
	public String displayProductAttributes(@RequestParam("id") long productId, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		System.out.println("$#6348#"); setMenu(model,request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		Product product = productService.getById(productId);
		
		System.out.println("$#6349#"); if(product==null) {
			System.out.println("$#6350#"); return "redirect:/admin/products/products.html";
		}
		
		System.out.println("$#6351#"); if(product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
			System.out.println("$#6352#"); return "redirect:/admin/products/products.html";
		}
		
		model.addAttribute("product",product);
		System.out.println("$#6353#"); return "admin-products-attributes";
		
	}
	
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/attributes/page.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> pageAttributes(HttpServletRequest request, HttpServletResponse response) {

		//String attribute = request.getParameter("attribute");
		String sProductId = request.getParameter("productId");
		
		
		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#6354#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		Long productId;
		Product product = null;
		
		try {
			productId = Long.parseLong(sProductId);
		} catch (Exception e) {
			System.out.println("$#6355#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6356#"); resp.setErrorString("Product id is not valid");
			String returnString = resp.toJSONString();
			System.out.println("$#6357#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		}

		
		try {
			
			
			product = productService.getById(productId);
			


			Language language = (Language)request.getAttribute("LANGUAGE");
			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			
			//List<ProductAttribute> attributes = productAttributeService.getByProductId(store, product, language);
			
			for(ProductAttribute attr : product.getAttributes()) {
				
				Map entry = new HashMap();
				entry.put("attributeId", attr.getId());
				
				List<ProductOptionDescription> optionsDescriptions = attr.getProductOption().getDescriptionsSettoList();
			    ProductOptionDescription optionDescription = attr.getProductOption().getDescriptionsSettoList().get(0);
				for(ProductOptionDescription desc : optionsDescriptions) {
					System.out.println("$#6358#"); if(desc.getLanguage().getId().intValue()==language.getId().intValue()) {
						optionDescription = desc;
					}
				}
				
				List<ProductOptionValueDescription> optionValuesDescriptions = attr.getProductOptionValue().getDescriptionsSettoList();
			    ProductOptionValueDescription optionValueDescription = attr.getProductOptionValue().getDescriptionsSettoList().get(0);
				for(ProductOptionValueDescription desc : optionValuesDescriptions) {
					System.out.println("$#6359#"); if(desc.getLanguage().getId().intValue()==language.getId().intValue()) {
						optionValueDescription = desc;
					}
				}
				entry.put("attribute", optionDescription.getName());
				entry.put("display", attr.getAttributeDisplayOnly());
				entry.put("value", optionValueDescription.getName());
				entry.put("order", attr.getProductOptionSortOrder());
				entry.put("price", priceUtil.getAdminFormatedAmountWithCurrency(store,attr.getProductAttributePrice()));

				System.out.println("$#6360#"); resp.addDataEntry(entry);
				
				
				
			}

			System.out.println("$#6361#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_SUCCESS);
		
		} catch (Exception e) {
			LOGGER.error("Error while paging products", e);
			System.out.println("$#6362#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6363#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#6364#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);


	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/attributes/editAttribute.html", method=RequestMethod.GET)
	public String displayAttributeEdit(@RequestParam("productId") Long productId, @RequestParam("id") Long id, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("$#6365#"); return displayAttribute(productId, id,model,request,response);

	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/attribute/createAttribute.html", method=RequestMethod.GET)
	public String displayAttributeCreate(@RequestParam("productId") Long productId, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("$#6366#"); return displayAttribute(productId, null,model,request,response);

	}
	
	private String displayAttribute(Long productId, Long id, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		

		//display menu
		System.out.println("$#6367#"); setMenu(model,request);
		
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		Language language = (Language)request.getAttribute("LANGUAGE");
		
		//get product
		Product product =  productService.getById(productId);
		System.out.println("$#6368#"); if(product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
			System.out.println("$#6369#"); return "redirect:/admin/products/products.html";
		}
		
		List<Language> languages = store.getLanguages();
		
		ProductAttribute attribute = null;
		
		//get Options
		List<ProductOption> options = productOptionService.listByStore(store, language);
		//get OptionsValues
		List<ProductOptionValue> optionsValues = productOptionValueService.listByStoreNoReadOnly(store, language);
		
		System.out.println("$#6370#"); if(id!=null && id.intValue()!=0) {//edit mode
			
			attribute = productAttributeService.getById(id);
			System.out.println("$#6372#"); attribute.setAttributePrice(priceUtil.getAdminFormatedAmount(store, attribute.getProductAttributePrice()));
			System.out.println("$#6373#"); attribute.setAttributeAdditionalWeight(String.valueOf(attribute.getProductAttributeWeight().intValue()));
			System.out.println("$#6374#"); attribute.setAttributeSortOrder(String.valueOf(attribute.getProductOptionSortOrder()));
			
		} else {
			
			attribute = new ProductAttribute();
			System.out.println("$#6375#"); attribute.setProduct(product);
			ProductOptionValue value = new ProductOptionValue();
			Set<ProductOptionValueDescription> descriptions = new HashSet<ProductOptionValueDescription>();
			for(Language l : languages) {
				
				ProductOptionValueDescription desc = new ProductOptionValueDescription();
				System.out.println("$#6376#"); desc.setLanguage(l);
				descriptions.add(desc);
				
				
			}
			
			System.out.println("$#6377#"); value.setDescriptions(descriptions);
			System.out.println("$#6378#"); attribute.setProductOptionValue(value);
		}
		
		model.addAttribute("optionsValues",optionsValues);
		model.addAttribute("options",options);
		model.addAttribute("attribute",attribute);
		model.addAttribute("product",product);
		System.out.println("$#6379#"); return "admin-products-attribute-details";
	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/attributes/attribute/save.html", method=RequestMethod.POST)
	public String saveAttribute(@Valid @ModelAttribute("attribute") ProductAttribute attribute, BindingResult result, Model model, HttpServletRequest request, Locale locale) throws Exception {
		

		//display menu
		System.out.println("$#6380#"); setMenu(model,request);
		
		Product product = productService.getById(attribute.getProduct().getId());
		
		model.addAttribute("product",product);
		
		
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		Language language = (Language)request.getAttribute("LANGUAGE");
		
		
		//get Options
		List<ProductOption> options = productOptionService.listByStore(store, language);
		//get OptionsValues
		List<ProductOptionValue> optionsValues = productOptionValueService.listByStoreNoReadOnly(store, language);
		
		model.addAttribute("optionsValues",optionsValues);
		model.addAttribute("options",options);
		
		ProductAttribute dbEntity =	null;	

		System.out.println("$#6382#"); System.out.println("$#6381#"); if(attribute.getId() != null && attribute.getId() >0) { //edit entry
			
			//get from DB
			dbEntity = productAttributeService.getById(attribute.getId());
			
			System.out.println("$#6384#"); if(dbEntity==null) {
				System.out.println("$#6385#"); return "redirect:/admin/products/attributes/list.html";
			}
			
			System.out.println("$#6386#"); if(dbEntity.getProductOption().getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#6387#"); return "redirect:/admin/products/attributes/list.html";
			}
		}
		
		//validate price
		BigDecimal submitedPrice = null;
		try {
			submitedPrice = priceUtil.getAmount(attribute.getAttributePrice());
			System.out.println("$#6388#"); attribute.setProductAttributePrice(submitedPrice);
		} catch (Exception e) {
			ObjectError error = new ObjectError("attributePrice",messages.getMessage("NotEmpty.product.productPrice", locale));
			System.out.println("$#6389#"); result.addError(error);
		}
		
		//validate sort order
		try {
			Integer sortOrder = Integer.parseInt(attribute.getAttributeSortOrder());
			System.out.println("$#6390#"); attribute.setProductOptionSortOrder(sortOrder);
		} catch(Exception e) {
			ObjectError error = new ObjectError("attributeSortOrder",messages.getMessage("message.number.invalid", locale));
			System.out.println("$#6391#"); result.addError(error);
		}
		
		//validate weight
		try {
			Integer weight = Integer.parseInt(attribute.getAttributeAdditionalWeight());
			System.out.println("$#6392#"); attribute.setProductAttributeWeight(new BigDecimal(weight));
		} catch(Exception e) {
			ObjectError error = new ObjectError("attributeAdditionalWeight",messages.getMessage("message.number.invalid", locale));
			System.out.println("$#6393#"); result.addError(error);
		}	
		
		System.out.println("$#6394#"); if(attribute.getProductOption()==null) {
			ObjectError error = new ObjectError("productOption.id",messages.getMessage("message.productoption.required", locale));
			System.out.println("$#6395#"); result.addError(error);
			System.out.println("$#6396#"); return "admin-products-attribute-details";
		}

		
		//check type
		ProductOption option = attribute.getProductOption();
		option = productOptionService.getById(option.getId());
		System.out.println("$#6397#"); attribute.setProductOption(option);
		
		System.out.println("$#6398#"); if(option.getProductOptionType().equals(TEXT_OPTION)) {
			
			System.out.println("$#6399#"); if(dbEntity!=null && dbEntity.getProductOption().getProductOptionType().equals(TEXT_OPTION)) {//bcz it is overwrited by hidden product option value list
				System.out.println("$#6401#"); if(dbEntity.getProductOptionValue()!=null) {
					ProductOptionValue optVal = dbEntity.getProductOptionValue();
					List<ProductOptionValueDescription> descriptions = attribute.getProductOptionValue().getDescriptionsList();
					Set<ProductOptionValueDescription> descriptionsSet = new HashSet<ProductOptionValueDescription>();
					for(ProductOptionValueDescription description : descriptions) {
						System.out.println("$#6402#"); description.setProductOptionValue(optVal);
						System.out.println("$#6405#"); System.out.println("$#6403#"); description.setName(description.getDescription().length()<15 ? description.getDescription() : description.getDescription().substring(0,15));
						descriptionsSet.add(description);
					}
					System.out.println("$#6406#"); optVal.setDescriptions(descriptionsSet);
					System.out.println("$#6407#"); optVal.setProductOptionDisplayOnly(true);
					System.out.println("$#6408#"); productOptionValueService.saveOrUpdate(optVal);
					System.out.println("$#6409#"); attribute.setProductOptionValue(optVal);
				}
			} else {//create a new value
			
				//create new option value
				List<ProductOptionValueDescription> descriptions = attribute.getProductOptionValue().getDescriptionsList();
				Set<ProductOptionValueDescription> newDescriptions = new HashSet<ProductOptionValueDescription>();
				ProductOptionValue newValue = new ProductOptionValue();
				for(ProductOptionValueDescription description : descriptions) {
					ProductOptionValueDescription optionValueDescription = new ProductOptionValueDescription();
					System.out.println("$#6410#"); optionValueDescription.setAuditSection(description.getAuditSection());
					System.out.println("$#6411#"); optionValueDescription.setLanguage(description.getLanguage());
					System.out.println("$#6414#"); System.out.println("$#6412#"); optionValueDescription.setName(description.getDescription().length()<15 ? description.getDescription() : description.getDescription().substring(0,15));
					System.out.println("$#6415#"); optionValueDescription.setLanguage(description.getLanguage());
					System.out.println("$#6416#"); optionValueDescription.setDescription(description.getDescription());
					System.out.println("$#6417#"); optionValueDescription.setProductOptionValue(newValue);
					newDescriptions.add(optionValueDescription);
				}
				
				//code generation
				String code = RandomStringUtils.randomAlphanumeric(10).toUpperCase();
				System.out.println("$#6418#"); newValue.setCode(code);
				System.out.println("$#6419#"); newValue.setMerchantStore(store);
				System.out.println("$#6420#"); newValue.setProductOptionValueSortOrder(attribute.getProductOptionValue().getProductOptionValueSortOrder());
				System.out.println("$#6421#"); newValue.setDescriptions(newDescriptions);
				System.out.println("$#6422#"); newValue.setProductOptionDisplayOnly(true);
				System.out.println("$#6423#"); productOptionValueService.save(newValue);
				System.out.println("$#6424#"); attribute.setProductOptionValue(newValue);
				System.out.println("$#6425#"); attribute.setAttributeDisplayOnly(true);
			
			}
			
		}
		

		
		System.out.println("$#6426#"); if(attribute.getProductOptionValue().getId()==null) {
			ObjectError error = new ObjectError("productOptionValue.id",messages.getMessage("message.productoptionvalue.required", locale));
			System.out.println("$#6427#"); result.addError(error);
		}
		
		model.addAttribute("attribute",attribute);

		
		System.out.println("$#6428#"); if (result.hasErrors()) {
			System.out.println("$#6429#"); return "admin-products-attribute-details";
		}
		
		System.out.println("$#6430#"); productAttributeService.saveOrUpdate(attribute);

		model.addAttribute("success","success");
		System.out.println("$#6431#"); return "admin-products-attribute-details";
	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/attributes/attribute/remove.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> deleteProductPrice(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		String sAttributeid = request.getParameter("attributeId");

		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#6432#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		
		try {
			
			Long attributeId = Long.parseLong(sAttributeid);
			ProductAttribute attribute = productAttributeService.getById(attributeId);
			

			System.out.println("$#6433#"); if(attribute==null || attribute.getProduct().getMerchantStore().getId().intValue()!=store.getId()) {

				System.out.println("$#6435#"); resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
				System.out.println("$#6436#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#6437#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			} 
			

			System.out.println("$#6438#"); productAttributeService.delete(attribute);
			
			
			System.out.println("$#6439#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);

		
		
		} catch (Exception e) {
			LOGGER.error("Error while deleting product price", e);
			System.out.println("$#6440#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6441#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#6442#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/attributes/getAttributeType.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> checkAttributeType(HttpServletRequest request, HttpServletResponse response, Locale locale) {

		String sOptionId = request.getParameter("optionId");

		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);

		
		
		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#6443#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		
		Long prodoptionId;
		ProductOption productOption = null;
		
		try {
			prodoptionId = Long.parseLong(sOptionId);
		} catch (Exception e) {
			System.out.println("$#6444#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6445#"); resp.setErrorString("Product Option id is not valid");
			String returnString = resp.toJSONString();
			System.out.println("$#6446#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		}

		
		try {
			
			
			productOption = productOptionService.getById(prodoptionId);
			
			System.out.println("$#6447#"); if(productOption==null) {
				System.out.println("$#6448#"); resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
				System.out.println("$#6449#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#6450#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#6451#"); if(productOption.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#6452#"); resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
				System.out.println("$#6453#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#6454#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			



			Map entry = new HashMap();
			

			
			entry.put("type", productOption.getProductOptionType());
			System.out.println("$#6455#"); resp.addDataEntry(entry);
			System.out.println("$#6456#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_SUCCESS);
		
		} catch (Exception e) {
			LOGGER.error("Error while paging products", e);
			System.out.println("$#6457#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6458#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#6459#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);

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
