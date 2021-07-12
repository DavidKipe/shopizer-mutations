package com.salesmanager.core.business.services.content;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.modules.cms.content.StaticContentFileManager;
import com.salesmanager.core.business.repositories.content.ContentRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.content.Content;
import com.salesmanager.core.model.content.ContentDescription;
import com.salesmanager.core.model.content.ContentType;
import com.salesmanager.core.model.content.FileContentType;
import com.salesmanager.core.model.content.InputContentFile;
import com.salesmanager.core.model.content.OutputContentFile;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;

@Service("contentService")
public class ContentServiceImpl extends SalesManagerEntityServiceImpl<Long, Content> implements ContentService {

	private static final Logger LOG = LoggerFactory.getLogger(ContentServiceImpl.class);

	private final ContentRepository contentRepository;

	@Inject
	StaticContentFileManager contentFileManager;

	@Inject
	public ContentServiceImpl(ContentRepository contentRepository) {
		super(contentRepository);

		this.contentRepository = contentRepository;
	}

	@Override
	public List<Content> listByType(ContentType contentType, MerchantStore store, Language language)
			throws ServiceException {

		System.out.println("$#2085#"); return contentRepository.findByType(contentType, store.getId(), language.getId());
	}

	@Override
	public void delete(Content content) throws ServiceException {

		Content c = this.getById(content.getId());
		System.out.println("$#2086#"); super.delete(c);

	}

	@Override
	public Content getByLanguage(Long id, Language language) throws ServiceException {
		System.out.println("$#2087#"); return contentRepository.findByIdAndLanguage(id, language.getId());
	}

	@Override
	public List<Content> listByType(List<ContentType> contentType, MerchantStore store, Language language)
			throws ServiceException {

		/*
		 * List<String> contentTypes = new ArrayList<String>(); for (int i = 0;
		 * i < contentType.size(); i++) {
		 * contentTypes.add(contentType.get(i).name()); }
		 */

		System.out.println("$#2088#"); return contentRepository.findByTypes(contentType, store.getId(), language.getId());
	}

	@Override
	public List<ContentDescription> listNameByType(List<ContentType> contentType, MerchantStore store,
			Language language) throws ServiceException {

		System.out.println("$#2089#"); return contentRepository.listNameByType(contentType, store, language);
	}

	@Override
	public List<Content> listByType(List<ContentType> contentType, MerchantStore store) throws ServiceException {

		System.out.println("$#2090#"); return contentRepository.findByTypes(contentType, store.getId());
	}

	@Override
	public Content getByCode(String code, MerchantStore store) throws ServiceException {

		System.out.println("$#2091#"); return contentRepository.findByCode(code, store.getId());

	}

	@Override
	public Content getById(Long id) {
		System.out.println("$#2092#"); return contentRepository.findOne(id);
	}

	@Override
	public void saveOrUpdate(final Content content) throws ServiceException {

		// save or update (persist and attach entities
		System.out.println("$#2094#"); System.out.println("$#2093#"); if (content.getId() != null && content.getId() > 0) {
			System.out.println("$#2096#"); super.update(content);
		} else {
			System.out.println("$#2097#"); super.save(content);
		}

	}

	@Override
	public Content getByCode(String code, MerchantStore store, Language language) throws ServiceException {
		System.out.println("$#2098#"); return contentRepository.findByCode(code, store.getId(), language.getId());
	}

	/**
	 * Method responsible for adding content file for given merchant store in
	 * underlying Infinispan tree cache. It will take {@link InputContentFile}
	 * and will store file for given merchant store according to its type. it
	 * can save an image or any type of file (pdf, css, js ...)
	 * 
	 * @param merchantStoreCode
	 *            Merchant store
	 * @param contentFile
	 *            {@link InputContentFile} being stored
	 * @throws ServiceException
	 *             service exception
	 */
	@Override
	public void addContentFile(String merchantStoreCode, InputContentFile contentFile) throws ServiceException {
		System.out.println("$#2099#"); Assert.notNull(merchantStoreCode, "Merchant store Id can not be null");
		System.out.println("$#2100#"); Assert.notNull(contentFile, "InputContentFile image can not be null");
		System.out.println("$#2101#"); Assert.notNull(contentFile.getFileName(), "InputContentFile.fileName can not be null");
		System.out.println("$#2102#"); Assert.notNull(contentFile.getFileContentType(), "InputContentFile.fileContentType can not be null");

		String mimeType = URLConnection.guessContentTypeFromName(contentFile.getFileName());
		System.out.println("$#2103#"); contentFile.setMimeType(mimeType);

		System.out.println("$#2104#"); if (contentFile.getFileContentType().name().equals(FileContentType.IMAGE.name())
				|| contentFile.getFileContentType().name().equals(FileContentType.STATIC_FILE.name())) {
			System.out.println("$#2106#"); addFile(merchantStoreCode, contentFile);
		} else if(contentFile.getFileContentType().name().equals(FileContentType.API_IMAGE.name())) { System.out.println("$#2107#");
			System.out.println("$#2108#"); contentFile.setFileContentType(FileContentType.IMAGE);
			System.out.println("$#2109#"); addImage(merchantStoreCode, contentFile);
		} else if(contentFile.getFileContentType().name().equals(FileContentType.API_FILE.name())) { System.out.println("$#2110#");
			System.out.println("$#2111#"); contentFile.setFileContentType(FileContentType.STATIC_FILE);
			System.out.println("$#2112#"); addFile(merchantStoreCode, contentFile);
		} else {
			System.out.println("$#2107#"); // manual correction for else-if mutation coverage
			System.out.println("$#2110#"); // manual correction for else-if mutation coverage
			System.out.println("$#2113#"); addImage(merchantStoreCode, contentFile);
		}

	}

	@Override
	public void addLogo(String merchantStoreCode, InputContentFile cmsContentImage) throws ServiceException {

		System.out.println("$#2114#"); Assert.notNull(merchantStoreCode, "Merchant store Id can not be null");
		System.out.println("$#2115#"); Assert.notNull(cmsContentImage, "CMSContent image can not be null");

		System.out.println("$#2116#"); cmsContentImage.setFileContentType(FileContentType.LOGO);
		System.out.println("$#2117#"); addImage(merchantStoreCode, cmsContentImage);

	}

	@Override
	public void addOptionImage(String merchantStoreCode, InputContentFile cmsContentImage) throws ServiceException {

		System.out.println("$#2118#"); Assert.notNull(merchantStoreCode, "Merchant store Id can not be null");
		System.out.println("$#2119#"); Assert.notNull(cmsContentImage, "CMSContent image can not be null");
		System.out.println("$#2120#"); cmsContentImage.setFileContentType(FileContentType.PROPERTY);
		System.out.println("$#2121#"); addImage(merchantStoreCode, cmsContentImage);

	}

	private void addImage(String merchantStoreCode, InputContentFile contentImage) throws ServiceException {

		try {
			LOG.info("Adding content image for merchant id {}", merchantStoreCode);

			String p = null;
			Optional<String> path = Optional.ofNullable(p);
			System.out.println("$#2122#"); contentFileManager.addFile(merchantStoreCode, path, contentImage);

		} catch (Exception e) {
			LOG.error("Error while trying to convert input stream to buffered image", e);
			throw new ServiceException(e);

		} finally {

			try {
				System.out.println("$#2123#"); if (contentImage.getFile() != null) {
					System.out.println("$#2124#"); contentImage.getFile().close();
				}
			} catch (Exception ignore) {
			}

		}

	}

	private void addFile(final String merchantStoreCode, InputContentFile contentImage) throws ServiceException {

		try {
			LOG.info("Adding content file for merchant id {}", merchantStoreCode);
			// staticContentFileManager.addFile(merchantStoreCode,
			// contentImage);

			String p = null;
			Optional<String> path = Optional.ofNullable(p);

			System.out.println("$#2125#"); contentFileManager.addFile(merchantStoreCode, path, contentImage);

		} catch (Exception e) {
			LOG.error("Error while trying to convert input stream to buffered image", e);
			throw new ServiceException(e);

		} finally {

			try {
				System.out.println("$#2126#"); if (contentImage.getFile() != null) {
					System.out.println("$#2127#"); contentImage.getFile().close();
				}
			} catch (Exception ignore) {
			}
		}

	}

	/**
	 * Method responsible for adding list of content images for given merchant
	 * store in underlying Infinispan tree cache. It will take list of
	 * {@link CMSContentImage} and will store them for given merchant store.
	 * 
	 * @param merchantStoreCode
	 *            Merchant store
	 * @param contentImagesList
	 *            list of {@link CMSContentImage} being stored
	 * @throws ServiceException
	 *             service exception
	 */
	@Override
	public void addContentFiles(String merchantStoreCode, List<InputContentFile> contentFilesList)
			throws ServiceException {

		System.out.println("$#2128#"); Assert.notNull(merchantStoreCode, "Merchant store ID can not be null");
		System.out.println("$#2129#"); Assert.notEmpty(contentFilesList, "File list can not be empty");
		LOG.info("Adding total {} images for given merchant", contentFilesList.size());

		String p = null;
		Optional<String> path = Optional.ofNullable(p);

		LOG.info("Adding content images for merchant....");
		System.out.println("$#2130#"); contentFileManager.addFiles(merchantStoreCode, path, contentFilesList);
		// staticContentFileManager.addFiles(merchantStoreCode,
		// contentFilesList);

		try {
			for (InputContentFile file : contentFilesList) {
				System.out.println("$#2131#"); if (file.getFile() != null) {
					System.out.println("$#2132#"); file.getFile().close();
				}
			}
		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}

	/**
	 * Method to remove given content image.Images are stored in underlying
	 * system based on there name. Name will be used to search given image for
	 * removal
	 * 
	 * @param contentImage
	 * @param merchantStoreCode
	 *            merchant store
	 * @throws ServiceException
	 */
	@Override
	public void removeFile(String merchantStoreCode, FileContentType fileContentType, String fileName)
			throws ServiceException {
		System.out.println("$#2133#"); Assert.notNull(merchantStoreCode, "Merchant Store Id can not be null");
		System.out.println("$#2134#"); Assert.notNull(fileContentType, "Content file type can not be null");
		System.out.println("$#2135#"); Assert.notNull(fileName, "Content Image type can not be null");

		String p = null;
		Optional<String> path = Optional.ofNullable(p);

		System.out.println("$#2136#"); contentFileManager.removeFile(merchantStoreCode, fileContentType, fileName, path);

	}

	@Override
	public void removeFile(String storeCode, String fileName) throws ServiceException {

		String fileType = "IMAGE";
		String mimetype = URLConnection.guessContentTypeFromName(fileName);
		String type = mimetype.split("/")[0];
		System.out.println("$#2137#"); if (!type.equals("image"))
			fileType = "STATIC_FILE";

		String p = null;
		Optional<String> path = Optional.ofNullable(p);

		System.out.println("$#2138#"); contentFileManager.removeFile(storeCode, FileContentType.valueOf(fileType), fileName, path);

	}

	/**
	 * Method to remove all images for a given merchant.It will take merchant
	 * store as an input and will remove all images associated with given
	 * merchant store.
	 * 
	 * @param merchantStoreCode
	 * @throws ServiceException
	 */
	@Override
	public void removeFiles(String merchantStoreCode) throws ServiceException {
		System.out.println("$#2139#"); Assert.notNull(merchantStoreCode, "Merchant Store Id can not be null");

		String p = null;
		Optional<String> path = Optional.ofNullable(p);

		System.out.println("$#2140#"); contentFileManager.removeFiles(merchantStoreCode, path);
	}

	/**
	 * Implementation for getContentImage method defined in
	 * {@link ContentService} interface. Methods will return Content image with
	 * given image name for the Merchant store or will return null if no image
	 * with given name found for requested Merchant Store in Infinispan tree
	 * cache.
	 * 
	 * @param store
	 *            Merchant merchantStoreCode
	 * @param imageName
	 *            name of requested image
	 * @return {@link OutputContentImage}
	 * @throws ServiceException
	 */
	@Override
	public OutputContentFile getContentFile(String merchantStoreCode, FileContentType fileContentType, String fileName)
			throws ServiceException {
		System.out.println("$#2141#"); Assert.notNull(merchantStoreCode, "Merchant store ID can not be null");
		System.out.println("$#2142#"); Assert.notNull(fileName, "File name can not be null");

		String p = null;
		Optional<String> path = Optional.ofNullable(p);

		System.out.println("$#2143#"); if (fileContentType.name().equals(FileContentType.IMAGE.name())
				|| fileContentType.name().equals(FileContentType.STATIC_FILE.name())) {
			System.out.println("$#2145#"); return contentFileManager.getFile(merchantStoreCode, path, fileContentType, fileName);
		} else {
			System.out.println("$#2146#"); return contentFileManager.getFile(merchantStoreCode, path, fileContentType, fileName);
		}

	}

	/**
	 * Implementation for getContentImages method defined in
	 * {@link ContentService} interface. Methods will return list of all Content
	 * image associated with given Merchant store or will return empty list if
	 * no image is associated with given Merchant Store in Infinispan tree
	 * cache.
	 * 
	 * @param merchantStoreId
	 *            Merchant store
	 * @return list of {@link OutputContentImage}
	 * @throws ServiceException
	 */
	@Override
	public List<OutputContentFile> getContentFiles(String merchantStoreCode, FileContentType fileContentType)
			throws ServiceException {
		System.out.println("$#2147#"); Assert.notNull(merchantStoreCode, "Merchant store Id can not be null");
		// return staticContentFileManager.getFiles(merchantStoreCode,
		// fileContentType);
		String p = null;
		Optional<String> path = Optional.ofNullable(p);
		System.out.println("$#2148#"); return contentFileManager.getFiles(merchantStoreCode, path, fileContentType);
	}

	/**
	 * Returns the image names for a given merchant and store
	 * 
	 * @param merchantStoreCode
	 * @param imageContentType
	 * @return images name list
	 * @throws ServiceException
	 */
	@Override
	public List<String> getContentFilesNames(String merchantStoreCode, FileContentType fileContentType)
			throws ServiceException {
		System.out.println("$#2149#"); Assert.notNull(merchantStoreCode, "Merchant store Id can not be null");

		String p = null;
		Optional<String> path = Optional.ofNullable(p);

		System.out.println("$#2150#"); return contentFileManager.getFileNames(merchantStoreCode, path, fileContentType);

		/*
		 * if(fileContentType.name().equals(FileContentType.IMAGE.name()) ||
		 * fileContentType.name().equals(FileContentType.STATIC_FILE.name())) {
		 * return contentFileManager.getFileNames(merchantStoreCode,
		 * fileContentType); } else { return
		 * contentFileManager.getFileNames(merchantStoreCode, fileContentType);
		 * }
		 */
	}

	@Override
	public ContentDescription getBySeUrl(MerchantStore store, String seUrl) {
		System.out.println("$#2151#"); return contentRepository.getBySeUrl(store, seUrl);
	}

	@Override
	public List<Content> getByCodeLike(ContentType type, String codeLike, MerchantStore store, Language language) {
		System.out.println("$#2152#"); return contentRepository.findByCodeLike(type, '%' + codeLike + '%', store.getId(), language.getId());
	}

	@Override
	public Content getById(Long id, MerchantStore store, Language language) throws ServiceException {

		Content content = contentRepository.findOne(id);

		System.out.println("$#2153#"); if (content != null) {
			System.out.println("$#2154#"); if (content.getMerchantStore().getId().intValue() != store.getId().intValue()) {
				return null;
			}
		}

		System.out.println("$#2155#"); return content;
	}

	@Override
	public void addFolder(MerchantStore store, Optional<String> path, String folderName) throws ServiceException {
		System.out.println("$#2156#"); Validate.notNull(store, "MerchantStore cannot be null");
		System.out.println("$#2157#"); Validate.notNull(folderName, "Folder name cannot be null");
		
		System.out.println("$#2158#"); if(path.isPresent()) {
			System.out.println("$#2159#"); if(!this.isValidLinuxDirectory(path.get())) {
				throw new ServiceException("Path format [" + path.get() + "] not a valid directory format");
			}
		}
		System.out.println("$#2160#"); contentFileManager.addFolder(store.getCode(), folderName, path);


	}

	@Override
	public List<String> listFolders(MerchantStore store, Optional<String> path) throws ServiceException {
		System.out.println("$#2161#"); Validate.notNull(store, "MerchantStore cannot be null");
		
		System.out.println("$#2162#"); return contentFileManager.listFolders(store.getCode(), path);
	}

	@Override
	public void removeFolder(MerchantStore store, Optional<String> path, String folderName) throws ServiceException {
		System.out.println("$#2163#"); Validate.notNull(store, "MerchantStore cannot be null");
		System.out.println("$#2164#"); Validate.notNull(folderName, "Folder name cannot be null");
		
		System.out.println("$#2165#"); contentFileManager.removeFolder(store.getCode(), folderName, path);

	}
	
	public boolean isValidLinuxDirectory(String path) {
	    Pattern linuxDirectoryPattern = Pattern.compile("^/|(/[a-zA-Z0-9_-]+)+$");
						System.out.println("$#2167#"); System.out.println("$#2166#"); return path != null && !path.trim().isEmpty() && linuxDirectoryPattern.matcher( path ).matches();
	}

	@Override
	public void renameFile(String merchantStoreCode, FileContentType fileContentType, Optional<String> path,
			String originalName, String newName) throws ServiceException{

		OutputContentFile file = contentFileManager.getFile(merchantStoreCode, path, fileContentType, originalName);
		
		System.out.println("$#2170#"); if(file == null) {
			throw new ServiceException("File name [" + originalName + "] not found for merchant [" + merchantStoreCode +"]");
		}
		
		ByteArrayOutputStream os = file.getFile();
		InputStream is = new ByteArrayInputStream(os.toByteArray());
		
		//remove file
		System.out.println("$#2171#"); contentFileManager.removeFile(merchantStoreCode, fileContentType, originalName, path);
		
		//recreate file
		InputContentFile inputFile = new InputContentFile();
		System.out.println("$#2172#"); inputFile.setFileContentType(fileContentType);
		System.out.println("$#2173#"); inputFile.setFileName(newName);
		System.out.println("$#2174#"); inputFile.setMimeType(file.getMimeType());
		System.out.println("$#2175#"); inputFile.setFile(is);
		
		System.out.println("$#2176#"); contentFileManager.addFile(merchantStoreCode, path, inputFile);
	
	}

}
