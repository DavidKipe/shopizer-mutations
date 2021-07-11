package com.salesmanager.shop.store.api.v1.content;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.salesmanager.core.model.content.FileContentType;
import com.salesmanager.core.model.content.OutputContentFile;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.content.Content;
import com.salesmanager.shop.model.content.ContentFile;
import com.salesmanager.shop.model.content.ContentFolder;
import com.salesmanager.shop.store.api.exception.RestApiException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.controller.content.facade.ContentFacade;
import com.salesmanager.shop.utils.ImageFilePath;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Administration tool dedicated api
 * @author carlsamson
 *
 */
@RestController
@RequestMapping(value = "/api/v1")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ContentAdministrationApi {
	
	
	private static final String DEFAULT_PATH = "/";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContentAdministrationApi.class);

	@Inject
	private ContentFacade contentFacade;
	
	
	@Inject
	@Qualifier("img")
	private ImageFilePath imageUtils;
	
	/**
	 * Works with ng-file-man client
	 * 
	 * @param path
	 * @param merchantStore
	 * @param language
	 * @return
	 * @throws Exception
	 */
	@GetMapping(value = "/private/content/list", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public List<ImageFile> list(@RequestParam(value = "parentPath", required = false) String path,
			@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) throws Exception {

		String decodedPath = decodeContentPath(path);

		ContentFolder folder = contentFacade.getContentFolder(decodedPath, merchantStore);
		List<ImageFile> files = folder.getContent().stream().map(x -> convertToImageFile(merchantStore, x))
				.collect(Collectors.toList());

		System.out.println("$#11562#"); return files;
	}
	
	/**
	 * @param path
	 * @param merchantStore
	 * @param language
	 * @return
	 * @throws Exception
	 */
	@GetMapping(value = "/private/content/folder", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public ContentFolder folder(
			@RequestParam(value = "path", required = false) String path,
			@ApiIgnore MerchantStore merchantStore, 
			@ApiIgnore Language language) throws Exception {
		String decodedPath = decodeContentPath(path);
		System.out.println("$#11563#"); return contentFacade.getContentFolder(decodedPath, merchantStore);
	}
	
	
	/**
	 * works with file manager
	 * @param files
	 * @param merchantStore
	 * @param language
	 */
	@PostMapping(value = "/private/content/images/add", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public FileStatus upload(
			@RequestParam(value = "qqfile", required = true) MultipartFile qqfile,
			@RequestParam(value = "qquuid", required = true) String qquuid,
			@RequestParam(value = "qqfilename", required = true) String qqfilename,
			@RequestParam(value = "qqtotalfilesize", required = false) Long qqtotalfilesize,
			@RequestParam(value = "parentPath", required = false) String parentPath,
			@RequestParam(value = "qqpartindex", required = false) Integer qqpartindex,
			@RequestParam(value = "qqtotalparts", required = false) Integer qqtotalparts,
			@ApiIgnore MerchantStore merchantStore, 
			@ApiIgnore Language language) {

			ContentFile cf = new ContentFile();
			System.out.println("$#11564#"); cf.setContentType(qqfile.getContentType());
			System.out.println("$#11565#"); cf.setName(qqfilename);
			try {
				System.out.println("$#11566#"); cf.setFile(qqfile.getBytes());
				System.out.println("$#11567#"); contentFacade.addContentFile(cf, merchantStore.getCode());
				System.out.println("$#11568#"); return new FileStatus();
			} catch (IOException e) {
				//throw new ServiceRuntimeException("Error while getting file bytes");
				LOGGER.error("Error when uploadging file",e);
				FileStatus fs = new FileStatus();
				System.out.println("$#11569#"); fs.setError(e.getMessage());
				System.out.println("$#11570#"); fs.setSuccess(false);
				System.out.println("$#11571#"); return fs;
			}

	}
	
	@GetMapping(value = "/content/images/download")
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public @ResponseBody String download(
			@RequestParam(value = "path", required = true) String path,
			@ApiIgnore MerchantStore merchantStore, 
			@ApiIgnore Language language) {
		System.out.println("$#11572#"); String fileName = path.substring(path.lastIndexOf("/")+1, path.length());
		try {
	    
			//OutputContentFile file = contentFacade.download(merchantStore, FileContentType.IMAGE, fileName);
			//return file.getFile().toByteArray();
			System.out.println("$#11573#"); return "https://s3.ca-central-1.amazonaws.com/shopizer-carl/files/DEFAULT/85.jpg";
		} catch (Exception e) {
			//throw new ServiceRuntimeException("Error while getting file bytes");
			LOGGER.error("Error when renaming file",e);
			throw new ServiceRuntimeException("Error while downloading file [" + fileName + "]");
		}

	}
	
	@PostMapping(value = "/private/content/images/rename", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public FileStatus rename(
			@RequestParam(value = "path", required = true) String path,
			@RequestParam(value = "newName", required = true) String newName,
			@ApiIgnore MerchantStore merchantStore, 
			@ApiIgnore Language language) {

		try {
			
			System.out.println("$#11574#"); String fileName = path.substring(path.lastIndexOf("/")+1, path.length());
			System.out.println("$#11575#"); contentFacade.renameFile(merchantStore, FileContentType.IMAGE, fileName, newName);
			System.out.println("$#11576#"); return new FileStatus();
		} catch (Exception e) {
			//throw new ServiceRuntimeException("Error while getting file bytes");
			LOGGER.error("Error when renaming file",e);
			FileStatus fs = new FileStatus();
			System.out.println("$#11577#"); fs.setError(e.getMessage());
			System.out.println("$#11578#"); fs.setSuccess(false);
			System.out.println("$#11579#"); return fs;
		}

	}
	
	@DeleteMapping(value = "/private/content/images/remove", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public FileStatus remove(
			@RequestParam(value = "path", required = true) String path,
			@ApiIgnore MerchantStore merchantStore, 
			@ApiIgnore Language language) {

		try {
			
			System.out.println("$#11580#"); String fileName = path.substring(path.lastIndexOf("/")+1, path.length());
			System.out.println("$#11581#"); contentFacade.delete(merchantStore, fileName, FileContentType.IMAGE.name());
			System.out.println("$#11582#"); return new FileStatus();
		} catch (Exception e) {
			//throw new ServiceRuntimeException("Error while getting file bytes");
			LOGGER.error("Error when renaming file",e);
			FileStatus fs = new FileStatus();
			System.out.println("$#11583#"); fs.setError(e.getMessage());
			System.out.println("$#11584#"); fs.setSuccess(false);
			System.out.println("$#11585#"); return fs;
		}

	}


	private ImageFile convertToImageFile(MerchantStore store, Content content) {
		ImageFile f = new ImageFile();
		System.out.println("$#11586#"); f.setDir(false);
		System.out.println("$#11587#"); f.setId(imageUtils.buildStaticImageUtils(store, content.getName()));
		System.out.println("$#11588#"); f.setName(content.getName());
		System.out.println("$#11589#"); f.setUrl(imageUtils.buildStaticImageUtils(store, content.getName()));
		System.out.println("$#11590#"); f.setPath("image.png");
		System.out.println("$#11591#"); f.setSize(null);
		System.out.println("$#11592#"); return f;
	}

	private ImageFile convertToFolder(String folder) {
		ImageFile f = new ImageFile();
		System.out.println("$#11593#"); f.setDir(true);
		System.out.println("$#11594#"); f.setId(UUID.randomUUID().toString());
		System.out.println("$#11595#"); f.setName(DEFAULT_PATH + "images");
		System.out.println("$#11596#"); f.setUrl(DEFAULT_PATH + "images");
		System.out.println("$#11597#"); f.setPath(DEFAULT_PATH + "images");
		System.out.println("$#11598#"); return f;
	}
	
	private String decodeContentPath(String path) throws UnsupportedEncodingException {
		try {
			System.out.println("$#11600#"); System.out.println("$#11599#"); return StringUtils.isBlank(path) || path.contains("/images") ? "/" : URLDecoder.decode(path.replaceAll(",",""), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RestApiException(e);
		}

	}
	
	class FileStatus implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private boolean success = true;
		private String error = null;
		private boolean preventRetry = true;
		public boolean isSuccess() {
			System.out.println("$#11603#"); System.out.println("$#11602#"); return success;
		}
		public void setSuccess(boolean success) {
			this.success = success;
		}
		public String getError() {
			System.out.println("$#11604#"); return error;
		}
		public void setError(String error) {
			this.error = error;
		}
		public boolean isPreventRetry() {
			System.out.println("$#11606#"); System.out.println("$#11605#"); return preventRetry;
		}
		public void setPreventRetry(boolean preventRetry) {
			this.preventRetry = preventRetry;
		}
		
	}
	
	
	class ImageFile implements Serializable {

		public String getUrl() {
			System.out.println("$#11607#"); return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getName() {
			System.out.println("$#11608#"); return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getSize() {
			System.out.println("$#11609#"); return size;
		}

		public void setSize(String size) {
			this.size = size;
		}

		public boolean isDir() {
			System.out.println("$#11611#"); System.out.println("$#11610#"); return dir;
		}

		public void setDir(boolean dir) {
			this.dir = dir;
		}

		public String getPath() {
			System.out.println("$#11612#"); return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getId() {
			System.out.println("$#11613#"); return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String url;
		private String name;
		private String size;
		private boolean dir;
		private String path;
		private String id;
	}

}
