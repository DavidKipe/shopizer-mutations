package com.salesmanager.shop.admin.controller.content;

import com.salesmanager.core.business.services.content.ContentService;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.content.FileContentType;
import com.salesmanager.core.model.content.InputContentFile;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.shop.admin.controller.ControllerConstants;
import com.salesmanager.shop.admin.model.content.ContentFiles;
import com.salesmanager.shop.admin.model.web.Menu;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.utils.ImageFilePath;
import org.apache.commons.collections4.CollectionUtils;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.util.*;

/**
 * Manage static content type image
 * - Add images
 * - Remove images
 * @author Carl Samson
 *
 */
@Controller
public class ContentImageController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContentImageController.class);
	
	@Inject
	private ContentService contentService;
	
	@Inject
	@Qualifier("img")
	private ImageFilePath imageUtils;
	
	/**
	 * Entry point for the file browser used from the javascript
	 * content editor
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("hasRole('CONTENT')")
	@RequestMapping(value={"/admin/content/fileBrowser.html"}, method=RequestMethod.GET)
	public String displayFileBrowser(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {


		System.out.println("$#5111#"); return ControllerConstants.Tiles.ContentImages.fileBrowser;
		
	}
	
	
	
	/**
	 * Get images for a given merchant store
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("hasRole('CONTENT')")
	@RequestMapping(value={"/admin/content/contentImages.html","/admin/content/contentManagement.html"}, method=RequestMethod.GET)
	public String getContentImages(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		System.out.println("$#5112#"); this.setMenu(model, request);
		System.out.println("$#5113#"); return ControllerConstants.Tiles.ContentImages.contentImages;
		
	}
	
	
	@SuppressWarnings({ "unchecked"})
	@PreAuthorize("hasRole('CONTENT')")
	@RequestMapping(value="/admin/content/images/paging.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> pageImages(HttpServletRequest request, HttpServletResponse response) {
		AjaxResponse resp = new AjaxResponse();

		try {
			

			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			
			List<String> imageNames = contentService.getContentFilesNames(store.getCode(),FileContentType.IMAGE);
			
			System.out.println("$#5114#"); if(imageNames!=null) {

				for(String name : imageNames) {

					@SuppressWarnings("rawtypes")
					Map entry = new HashMap();
					entry.put("picture", new StringBuilder().append(request.getContextPath()).append(imageUtils.buildStaticImageUtils(store, name)).toString());
					
					entry.put("name", name);
					entry.put("id", name);
					System.out.println("$#5115#"); resp.addDataEntry(entry);

				}
			
			}
			
			System.out.println("$#5116#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_SUCCESS);

		} catch (Exception e) {
			LOGGER.error("Error while paging content images", e);
			System.out.println("$#5117#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
		}
		
		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#5118#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		System.out.println("$#5119#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	
	/**
	 * Controller methods which allow Admin to add content images to underlying
	 * Infinispan cache.
	 * @param model model object
	 * @param request http request object
	 * @param response http response object
	 * @return view allowing user to add content images
	 * @throws Exception
	 */
	@PreAuthorize("hasRole('CONTENT')")
	@RequestMapping(value="/admin/content/createContentImages.html", method=RequestMethod.GET)
    public String displayContentImagesCreate(final Model model, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
      
					System.out.println("$#5120#"); return ControllerConstants.Tiles.ContentImages.addContentImages;

    }
	
	/**
	 * Method responsible for adding content images to underlying Infinispan cache.
	 * It will add given content image(s) for given merchant store in the cache.
	 * Following steps will be performed in order to add images
	 * <pre>
	 * 1. Validate form data
	 * 2. Get Merchant Store based on merchant Id.
	 * 3. Call {@link InputContentFile} to add image(s).
	 * </pre>
	 * 
	 * @param contentImages
	 * @param bindingResult
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("hasRole('CONTENT')")
	@RequestMapping(value="/admin/content/saveContentImages.html", method=RequestMethod.POST)
	public String saveContentImages(@ModelAttribute(value="contentFiles") @Valid final ContentFiles contentImages, final BindingResult bindingResult,final Model model, final HttpServletRequest request) throws Exception{
	    
		System.out.println("$#5121#"); this.setMenu(model, request);
					System.out.println("$#5122#"); if (bindingResult.hasErrors()) {
	        LOGGER.info( "Found {} Validation errors", bindingResult.getErrorCount());
								System.out.println("$#5123#"); return ControllerConstants.Tiles.ContentImages.addContentImages;
	       
        }
	    final List<InputContentFile> contentImagesList=new ArrayList<InputContentFile>();
        final MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
								System.out.println("$#5124#"); if(CollectionUtils.isNotEmpty( contentImages.getFile() )){
            LOGGER.info("Saving {} content images for merchant {}",contentImages.getFile().size(),store.getId());
            for(final MultipartFile multipartFile:contentImages.getFile()){
																System.out.println("$#5125#"); if(!multipartFile.isEmpty()){
                    ByteArrayInputStream inputStream = new ByteArrayInputStream( multipartFile.getBytes() );
                    InputContentFile cmsContentImage = new InputContentFile();
																				System.out.println("$#5126#"); cmsContentImage.setFileName(multipartFile.getOriginalFilename() );
																				System.out.println("$#5127#"); cmsContentImage.setMimeType( multipartFile.getContentType() );
																				System.out.println("$#5128#"); cmsContentImage.setFile( inputStream );
																				System.out.println("$#5129#"); cmsContentImage.setFileContentType(FileContentType.IMAGE);
                    contentImagesList.add( cmsContentImage);
                }
            }
            
												System.out.println("$#5130#"); if(CollectionUtils.isNotEmpty( contentImagesList )){
																System.out.println("$#5131#"); contentService.addContentFiles( store.getCode(), contentImagesList );
            }
            else{
                // show error message on UI
            }
        }
       
								System.out.println("$#5132#"); return ControllerConstants.Tiles.ContentImages.contentImages;
	}
	
	
	/**
	 * Removes a content image from the CMS
	 * @param request
	 * @param response
	 * @param locale
	 * @return
	 */
	@PreAuthorize("hasRole('CONTENT')")
	@RequestMapping(value="/admin/content/removeImage.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> removeImage(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		String imageName = request.getParameter("name");

		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		AjaxResponse resp = new AjaxResponse();

		
		try {
			

			
			System.out.println("$#5133#"); contentService.removeFile(store.getCode(), FileContentType.IMAGE, imageName);

		
		
		} catch (Exception e) {
			LOGGER.error("Error while deleting product", e);
			System.out.println("$#5134#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#5135#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#5136#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		System.out.println("$#5137#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	
	private void setMenu(Model model, HttpServletRequest request) throws Exception {
		
		//display menu
		Map<String,String> activeMenus = new HashMap<String,String>();
		activeMenus.put("content", "content");
		activeMenus.put("content-images", "content-images");
		
		@SuppressWarnings("unchecked")
		Map<String, Menu> menus = (Map<String, Menu>)request.getAttribute("MENUMAP");
		
		Menu currentMenu = (Menu)menus.get("content");
		model.addAttribute("currentMenu",currentMenu);
		model.addAttribute("activeMenus",activeMenus);
		//
		
	}

}
