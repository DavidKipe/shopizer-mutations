package com.salesmanager.shop.admin.controller.products;

import com.salesmanager.core.business.services.catalog.product.manufacturer.ManufacturerService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.utils.CoreConfiguration;
import com.salesmanager.core.business.utils.ajax.AjaxPageableResponse;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.catalog.product.manufacturer.Manufacturer;
import com.salesmanager.core.model.catalog.product.manufacturer.ManufacturerDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.admin.controller.ControllerConstants;
import com.salesmanager.shop.admin.controller.customers.CustomerController;
import com.salesmanager.shop.admin.model.web.Menu;
import com.salesmanager.shop.constants.Constants;
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

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.util.*;

@Controller
public class ManufacturerController {
	
	@Inject
	private LanguageService languageService;
	
	@Inject
	private ManufacturerService manufacturerService;
	
	@Inject
	LabelUtils messages;
	
	@Inject
	private CoreConfiguration configuration;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/catalogue/manufacturer/list.html", method=RequestMethod.GET)
	public String getManufacturers(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
	
		System.out.println("$#6129#"); this.setMenu(model, request);
		
		System.out.println("$#6130#"); return ControllerConstants.Tiles.Product.manufacturerList;
	}
	
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/catalogue/manufacturer/create.html", method=RequestMethod.GET)
	public String createManufacturer(  Model model,  HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		System.out.println("$#6131#"); return displayManufacturer(null,model,request,response);
	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/catalogue/manufacturer/edit.html", method=RequestMethod.GET)
	public String editManufacturer(@RequestParam("id") long manufacturerId, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		System.out.println("$#6132#"); return displayManufacturer(manufacturerId,model,request,response);
	}
	
	private String displayManufacturer(Long manufacturerId, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		//display menu
		System.out.println("$#6133#"); setMenu(model,request);
		
		//List<Language> languages = languageService.getLanguages();
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		List<Language> languages = store.getLanguages();
		
		
		com.salesmanager.shop.admin.model.catalog.Manufacturer manufacturer = new com.salesmanager.shop.admin.model.catalog.Manufacturer();
		List<ManufacturerDescription> descriptions = new ArrayList<ManufacturerDescription>();

		
		System.out.println("$#6134#"); if( manufacturerId!=null && manufacturerId.longValue()!=0) {	//edit mode

			Manufacturer dbManufacturer = new Manufacturer();
			dbManufacturer = manufacturerService.getById( manufacturerId );
			
			System.out.println("$#6136#"); if(dbManufacturer==null) {
				System.out.println("$#6137#"); return ControllerConstants.Tiles.Product.manufacturerList;
			}
			
			System.out.println("$#6138#"); if(dbManufacturer.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#6139#"); return ControllerConstants.Tiles.Product.manufacturerList;
			}
			
			Set<ManufacturerDescription> manufacturerDescriptions = dbManufacturer.getDescriptions();

			
			for(Language l : languages) {
				
				ManufacturerDescription manufDescription = null;
				System.out.println("$#6140#"); if(manufacturerDescriptions!=null) {
					
					for(ManufacturerDescription desc : manufacturerDescriptions) {				
						String code = desc.getLanguage().getCode();
						System.out.println("$#6141#"); if(code.equals(l.getCode())) {
							manufDescription = desc;
						}

					}
					
				}
				
				System.out.println("$#6142#"); if(manufDescription==null) {
					manufDescription = new ManufacturerDescription();
					System.out.println("$#6143#"); manufDescription.setLanguage(l);
				}
				
				manufacturer.getDescriptions().add(manufDescription);
				
			}
			
			System.out.println("$#6144#"); manufacturer.setManufacturer( dbManufacturer );
		
			System.out.println("$#6145#"); manufacturer.setCode(dbManufacturer.getCode());
			System.out.println("$#6146#"); manufacturer.setOrder( dbManufacturer.getOrder() );
			
		} else {	// Create mode

			Manufacturer manufacturerTmp = new Manufacturer();
			System.out.println("$#6147#"); manufacturer.setManufacturer( manufacturerTmp );
			
			for(Language l : languages) {// for each store language
				
				ManufacturerDescription manufacturerDesc = new ManufacturerDescription();
				System.out.println("$#6148#"); manufacturerDesc.setLanguage(l);
				descriptions.add(  manufacturerDesc );
				System.out.println("$#6149#"); manufacturer.setDescriptions(descriptions);
				
			}
		}

		model.addAttribute("languages",languages);
		model.addAttribute("manufacturer", manufacturer);
		
		System.out.println("$#6150#"); return ControllerConstants.Tiles.Product.manufacturerDetails;
	}
		
	@PreAuthorize("hasRole('PRODUCTS')")  
	@RequestMapping(value="/admin/catalogue/manufacturer/save.html", method=RequestMethod.POST)
	public String saveManufacturer( @Valid @ModelAttribute("manufacturer") com.salesmanager.shop.admin.model.catalog.Manufacturer manufacturer, BindingResult result, Model model,  HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {

		System.out.println("$#6151#"); this.setMenu(model, request);
		//save or edit a manufacturer

		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		List<Language> languages = languageService.getLanguages();

		System.out.println("$#6153#"); System.out.println("$#6152#"); if(manufacturer.getDescriptions()!=null && manufacturer.getDescriptions().size()>0) {

			for(ManufacturerDescription description : manufacturer.getDescriptions()) {

				//validate Url Clicked
/*				if ( description.getUrlClicked() != null && !description.getUrlClicked().toString().isEmpty()) {
					try{
						Integer.parseInt( description.getUrlClicked().toString() );

					} catch (Exception e) {

						ObjectError error = new ObjectError("descriptions[${counter.index}].urlClicked","URL Clicked must be a number");
						result.addError(error);
					}
				}*/
			}
		}


	//validate image
		System.out.println("$#6155#"); if(manufacturer.getImage()!=null && !manufacturer.getImage().isEmpty()) {

			try {

				String maxHeight = configuration.getProperty("PRODUCT_IMAGE_MAX_HEIGHT_SIZE");
				String maxWidth = configuration.getProperty("PRODUCT_IMAGE_MAX_WIDTH_SIZE");
				String maxSize = configuration.getProperty("PRODUCT_IMAGE_MAX_SIZE");

				BufferedImage image = ImageIO.read(manufacturer.getImage().getInputStream());

				System.out.println("$#6157#"); if(!StringUtils.isBlank(maxHeight)) {

					int maxImageHeight = Integer.parseInt(maxHeight);
					System.out.println("$#6159#"); System.out.println("$#6158#"); if(image.getHeight()>maxImageHeight) {
						ObjectError error = new ObjectError("image",messages.getMessage("message.image.height", locale) + " {"+maxHeight+"}");
						System.out.println("$#6160#"); result.addError(error);
					}
				}

				System.out.println("$#6161#"); if(!StringUtils.isBlank(maxWidth)) {

					int maxImageWidth = Integer.parseInt(maxWidth);
					System.out.println("$#6163#"); System.out.println("$#6162#"); if(image.getWidth()>maxImageWidth) {
						ObjectError error = new ObjectError("image",messages.getMessage("message.image.width", locale) + " {"+maxWidth+"}");
						System.out.println("$#6164#"); result.addError(error);
					}
				}

				System.out.println("$#6165#"); if(!StringUtils.isBlank(maxSize)) {

					int maxImageSize = Integer.parseInt(maxSize);
					System.out.println("$#6167#"); System.out.println("$#6166#"); if(manufacturer.getImage().getSize()>maxImageSize) {
						ObjectError error = new ObjectError("image",messages.getMessage("message.image.size", locale) + " {"+maxSize+"}");
						System.out.println("$#6168#"); result.addError(error);
					}
				}

			} catch (Exception e) {
				LOGGER.error("Cannot validate manufacturer image", e);
			}

		}

		System.out.println("$#6169#"); if (result.hasErrors()) {
			model.addAttribute("languages",languages);
			System.out.println("$#6170#"); return ControllerConstants.Tiles.Product.manufacturerDetails;
		}

		Manufacturer newManufacturer = manufacturer.getManufacturer();

		System.out.println("$#6172#"); System.out.println("$#6171#"); if ( manufacturer.getManufacturer().getId() !=null && manufacturer.getManufacturer().getId()  > 0 ){

			newManufacturer = manufacturerService.getById( manufacturer.getManufacturer().getId() );

			System.out.println("$#6174#"); if(newManufacturer.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#6175#"); return ControllerConstants.Tiles.Product.manufacturerList;
			}

		}

//		for(ManufacturerImage image : manufacturer.getImages()) {
//			if(image.isDefaultImage()) {
//				manufacturer.setProductImage(image);
//			}
//		}

		Set<ManufacturerDescription> descriptions = new HashSet<ManufacturerDescription>();
		System.out.println("$#6177#"); System.out.println("$#6176#"); if(manufacturer.getDescriptions()!=null && manufacturer.getDescriptions().size()>0) {
			
			for(ManufacturerDescription desc : manufacturer.getDescriptions()) {
				
				System.out.println("$#6179#"); desc.setManufacturer(newManufacturer);
				descriptions.add(desc);
			}
		}
		System.out.println("$#6180#"); newManufacturer.setDescriptions(descriptions );
		System.out.println("$#6181#"); newManufacturer.setOrder( manufacturer.getOrder() );
		System.out.println("$#6182#"); newManufacturer.setMerchantStore(store);
		System.out.println("$#6183#"); newManufacturer.setCode(manufacturer.getCode());


//		if(manufacturer.getManufacturerImage()!=null && manufacturer.getManufacturerImage().getId() == null) {
//			newManufacturer.setProductImage(null);
//		}



		System.out.println("$#6184#"); if(manufacturer.getImage()!=null && !manufacturer.getImage().isEmpty()) {
//
//			String imageName = manufacturer.getImage().getOriginalFilename();
//
//			ManufacturerImage manufacturerImage = new ManufacturerImage();
//			manufacturerImage.setDefaultImage(true);
//			manufacturerImage.setImage(manufacturer.getImage().getInputStream());
//			manufacturerImage.setManufacturerImage(imageName);
//
//			List<ManufacturerImageDescription> imagesDescriptions = new ArrayList<ManufacturerImageDescription>();
//
//			for(Language l : languages) {
//
//				ManufacturerImageDescription imageDescription = new ManufacturerImageDescription();
//				imageDescription.setName(imageName);
//				imageDescription.setLanguage(l);
//				imageDescription.setManufacturerImage(productImage);
//				imagesDescriptions.add(imageDescription);
//
//			}
//
//			manufacturerImage.setDescriptions(imagesDescriptions);
//			manufacturerImage.setProduct(newManufacturer);
//
//			newManufacturer.getImages().add(manufacturerImage);
//
//			manufacturerService.saveOrUpdate(newManufacturer);
//
//			//manufacturer displayed
//			manufacturer.setProductImage(manufacturerImage);


		} else {

			System.out.println("$#6186#"); manufacturerService.saveOrUpdate(newManufacturer);
		}

		model.addAttribute("manufacturer", manufacturer);
		model.addAttribute("languages",languages);
		model.addAttribute("success","success");

		System.out.println("$#6187#"); return ControllerConstants.Tiles.Product.manufacturerDetails;

	}
	
	
	@SuppressWarnings("unchecked")
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/catalogue/manufacturer/paging.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> pageManufacturers(HttpServletRequest request, HttpServletResponse response) {
		
		AjaxResponse resp = new AjaxResponse();
		try {
			
			Language language = (Language)request.getAttribute("LANGUAGE");	
			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			
			List<Manufacturer> manufacturers = null;				
			manufacturers = manufacturerService.listByStore(store, language);
			
				
			for(Manufacturer manufacturer : manufacturers) {
				
				@SuppressWarnings("rawtypes")
				Map entry = new HashMap();
				entry.put("id", manufacturer.getId());
				
				ManufacturerDescription description = manufacturer.getDescriptions().iterator().next();
				
				entry.put("name", description.getName());
				entry.put("code", manufacturer.getCode());
				entry.put("order", manufacturer.getOrder());
				System.out.println("$#6188#"); resp.addDataEntry(entry);
				
			}
			
			System.out.println("$#6189#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_SUCCESS);
		
		} catch (Exception e) {
			LOGGER.error("Error while paging Manufacturers", e);
			System.out.println("$#6190#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
		}
		
		System.out.println("$#6191#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_SUCCESS);
		
		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#6192#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		System.out.println("$#6193#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		
	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/catalogue/manufacturer/remove.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> deleteManufacturer(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		Long sid =  Long.valueOf(request.getParameter("id") );
	
	
		AjaxResponse resp = new AjaxResponse();
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#6194#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		try{
			Manufacturer delManufacturer = manufacturerService.getById( sid  );				
			System.out.println("$#6195#"); if(delManufacturer==null || delManufacturer.getMerchantStore().getId().intValue() != store.getId().intValue()) {
				System.out.println("$#6197#"); resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
				System.out.println("$#6198#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#6199#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			} 
			
			int count = manufacturerService.getCountManufAttachedProducts( delManufacturer ).intValue();
			//IF already attached to products it can't be deleted
			System.out.println("$#6201#"); System.out.println("$#6200#"); if ( count > 0 ){
				System.out.println("$#6202#"); resp.setStatusMessage(messages.getMessage("message.product.association", locale));
				System.out.println("$#6203#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#6204#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}	

			System.out.println("$#6205#"); manufacturerService.delete( delManufacturer );
			
			System.out.println("$#6206#"); resp.setStatusMessage(messages.getMessage("message.success", locale));
			System.out.println("$#6207#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);
			
		} catch (Exception e) {
			
			System.out.println("$#6208#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			LOGGER.error("Cannot delete manufacturer.", e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#6209#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		
	}
	
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/manufacturer/checkCode.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> checkCode(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		String code = request.getParameter("code");
		String id = request.getParameter("id");


		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#6210#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		
		AjaxResponse resp = new AjaxResponse();
		
		System.out.println("$#6211#"); if(StringUtils.isBlank(code)) {
			System.out.println("$#6212#"); resp.setStatus(AjaxResponse.CODE_ALREADY_EXIST);
			String returnString = resp.toJSONString();
			System.out.println("$#6213#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		}

		
		try {
			
		Manufacturer manufacturer = manufacturerService.getByCode(store, code);
		
		System.out.println("$#6214#"); if(manufacturer!=null && StringUtils.isBlank(id)) {
			System.out.println("$#6216#"); resp.setStatus(AjaxResponse.CODE_ALREADY_EXIST);
			String returnString = resp.toJSONString();
			System.out.println("$#6217#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		}
		
		
		System.out.println("$#6218#"); if(manufacturer!=null && !StringUtils.isBlank(id)) {
			try {
				Long lid = Long.parseLong(id);
				
				System.out.println("$#6220#"); if(manufacturer.getCode().equals(code) && manufacturer.getId().longValue()==lid) {
					System.out.println("$#6222#"); resp.setStatus(AjaxResponse.CODE_ALREADY_EXIST);
					String returnString = resp.toJSONString();
					System.out.println("$#6223#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
				}
			} catch (Exception e) {
				System.out.println("$#6224#"); resp.setStatus(AjaxResponse.CODE_ALREADY_EXIST);
				String returnString = resp.toJSONString();
				System.out.println("$#6225#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}

		}
		
		
		
		

	
		
			


			System.out.println("$#6226#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);

		} catch (Exception e) {
			LOGGER.error("Error while getting manufacturer", e);
			System.out.println("$#6227#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6228#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#6229#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	
	
	
	private void setMenu(Model model, HttpServletRequest request) throws Exception {		
		//display menu
		Map<String,String> activeMenus = new HashMap<String,String>();
		activeMenus.put("catalogue", "catalogue");
		activeMenus.put("manufacturer-list", "manufacturer-list");
		
		@SuppressWarnings("unchecked")
		Map<String, Menu> menus = (Map<String, Menu>)request.getAttribute("MENUMAP");
		
		Menu currentMenu = (Menu)menus.get("catalogue");
		model.addAttribute("currentMenu",currentMenu);
		model.addAttribute("activeMenus",activeMenus);
	}

}
