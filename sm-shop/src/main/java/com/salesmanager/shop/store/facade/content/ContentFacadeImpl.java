package com.salesmanager.shop.store.facade.content;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.content.ContentService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.content.Content;
import com.salesmanager.core.model.content.ContentDescription;
import com.salesmanager.core.model.content.ContentType;
import com.salesmanager.core.model.content.FileContentType;
import com.salesmanager.core.model.content.InputContentFile;
import com.salesmanager.core.model.content.OutputContentFile;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.content.ContentDescriptionEntity;
import com.salesmanager.shop.model.content.ContentFile;
import com.salesmanager.shop.model.content.ContentFolder;
import com.salesmanager.shop.model.content.ContentImage;
import com.salesmanager.shop.model.content.PersistableContentEntity;
import com.salesmanager.shop.model.content.ReadableContentBox;
import com.salesmanager.shop.model.content.ReadableContentEntity;
import com.salesmanager.shop.model.content.ReadableContentFull;
import com.salesmanager.shop.model.content.ReadableContentPage;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.controller.content.facade.ContentFacade;
import com.salesmanager.shop.utils.FilePathUtils;
import com.salesmanager.shop.utils.ImageFilePath;

@Component("contentFacade")
public class ContentFacadeImpl implements ContentFacade {


	private static final Logger LOGGER = LoggerFactory.getLogger(ContentFacade.class);

	public static final String FILE_CONTENT_DELIMETER = "/";

	@Inject
	private ContentService contentService;

	@Inject
	private LanguageService languageService;

	@Inject
	@Qualifier("img")
	private ImageFilePath imageUtils;

	@Inject
	private FilePathUtils fileUtils;

	@Override
	public ContentFolder getContentFolder(String folder, MerchantStore store) throws Exception {
		try {
			List<String> imageNames = Optional
					.ofNullable(contentService.getContentFilesNames(store.getCode(), FileContentType.IMAGE))
					.orElseThrow(() -> new ResourceNotFoundException("No Folder found for path : " + folder));

			// images from CMS
			List<ContentImage> contentImages = imageNames.stream().map(name -> convertToContentImage(name, store))
					.collect(Collectors.toList());

			ContentFolder contentFolder = new ContentFolder();
			System.out.println("$#14536#"); if (!StringUtils.isBlank(folder)) {
				System.out.println("$#14537#"); contentFolder.setPath(URLEncoder.encode(folder, "UTF-8"));
			}
			contentFolder.getContent().addAll(contentImages);
			System.out.println("$#14538#"); return contentFolder;

		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Error while getting folder " + e.getMessage(), e);
		}
	}

	private ContentImage convertToContentImage(String name, MerchantStore store) {
		String path = absolutePath(store, null);
		ContentImage contentImage = new ContentImage();
		System.out.println("$#14539#"); contentImage.setName(name);
		System.out.println("$#14540#"); contentImage.setPath(path);
		System.out.println("$#14541#"); return contentImage;
	}

	@Override
	public String absolutePath(MerchantStore store, String file) {
		System.out.println("$#14542#"); return new StringBuilder().append(imageUtils.getContextPath())
				.append(imageUtils.buildStaticImageUtils(store, file)).toString();
	}

	@Override
	public void delete(MerchantStore store, String fileName, String fileType) {
		System.out.println("$#14543#"); Validate.notNull(store, "MerchantStore cannot be null");
		System.out.println("$#14544#"); Validate.notNull(fileName, "File name cannot be null");
		try {
			FileContentType t = FileContentType.valueOf(fileType);
			System.out.println("$#14545#"); contentService.removeFile(store.getCode(), t, fileName);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException(e);
		}
	}

	@Override
	public List<ReadableContentPage> getContentPage(MerchantStore store, Language language) {
		System.out.println("$#14546#"); Validate.notNull(store, "MerchantStore cannot be null");
		System.out.println("$#14547#"); Validate.notNull(language, "Language cannot be null");

		try {
			System.out.println("$#14548#"); return contentService.listByType(ContentType.PAGE, store, language).stream().filter(Content::isVisible)
					.map(content -> convertContentToReadableContentPage(store, language, content))
					.collect(Collectors.toList());
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Error while getting content " + e.getMessage(), e);
		}
	}

	private ReadableContentPage convertContentToReadableContentPage(MerchantStore store, Language language,
			Content content) {
		//ReadableContentPage page = new ReadableContentPage();
		Optional<ContentDescription> contentDescription = findAppropriateContentDescription(content.getDescriptions(),
				language);

		System.out.println("$#14550#"); if (contentDescription.isPresent()) {
			/*page.setName(contentDescription.get().getName());
			page.setPageContent(contentDescription.get().getDescription());*/
			System.out.println("$#14551#"); return this.contentDescriptionToReadableContent(store, content, contentDescription.get());
		}
/*		page.setId(content.getId());
		page.setSlug(contentDescription.get().getSeUrl());
		page.setDisplayedInMenu(content.isLinkToMenu());
		page.setTitle(contentDescription.get().getTitle());
		page.setMetaDetails(contentDescription.get().getMetatagDescription());
		page.setContentType(ContentType.PAGE.name());
		page.setCode(content.getCode());
		page.setPath(fileUtils.buildStaticFilePath(store.getCode(), contentDescription.get().getSeUrl()));
		return page;*/
		return null;
	}
	
	private ReadableContentPage contentDescriptionToReadableContent(MerchantStore store, Content content, ContentDescription contentDescription) {
		
		ReadableContentPage page = new ReadableContentPage();


		System.out.println("$#14552#"); page.setName(contentDescription.getName());
		System.out.println("$#14553#"); page.setPageContent(contentDescription.getDescription());

		System.out.println("$#14554#"); page.setId(content.getId());
		System.out.println("$#14555#"); page.setSlug(contentDescription.getSeUrl());
		System.out.println("$#14556#"); page.setDisplayedInMenu(content.isLinkToMenu());
		System.out.println("$#14557#"); page.setTitle(contentDescription.getTitle());
		System.out.println("$#14558#"); page.setMetaDetails(contentDescription.getMetatagDescription());
		System.out.println("$#14559#"); page.setContentType(ContentType.PAGE.name());
		System.out.println("$#14560#"); page.setCode(content.getCode());
		System.out.println("$#14561#"); page.setPath(fileUtils.buildStaticFilePath(store.getCode(), contentDescription.getSeUrl()));
		System.out.println("$#14562#"); return page;
		
		
	}

	private ReadableContentFull convertContentToReadableContentFull(MerchantStore store, Language language,
			Content content) {
		ReadableContentFull contentFull = new ReadableContentFull();

		try {
			List<ContentDescriptionEntity> descriptions = this.createContentDescriptionEntitys(store, content,
					language);

			System.out.println("$#14563#"); contentFull.setDescriptions(descriptions);
			System.out.println("$#14564#"); contentFull.setId(content.getId());
			System.out.println("$#14565#"); contentFull.setDisplayedInMenu(content.isLinkToMenu());
			System.out.println("$#14566#"); contentFull.setContentType(content.getContentType().name());
			System.out.println("$#14567#"); contentFull.setCode(content.getCode());
			System.out.println("$#14568#"); contentFull.setId(content.getId());
			System.out.println("$#14569#"); contentFull.setVisible(content.isVisible());

			System.out.println("$#14570#"); return contentFull;

		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Error while creating ReadableContentFull", e);
		}
	}

	private ReadableContentEntity convertContentToReadableContentEntity(MerchantStore store, Language language,
			Content content) {

		ReadableContentEntity contentEntity = new ReadableContentEntity();

		ContentDescriptionEntity description = this.create(content.getDescription());

		System.out.println("$#14571#"); contentEntity.setDescription(description);
		System.out.println("$#14572#"); contentEntity.setId(content.getId());
		System.out.println("$#14573#"); contentEntity.setDisplayedInMenu(content.isLinkToMenu());
		System.out.println("$#14574#"); contentEntity.setContentType(content.getContentType().name());
		System.out.println("$#14575#"); contentEntity.setCode(content.getCode());
		System.out.println("$#14576#"); contentEntity.setId(content.getId());
		System.out.println("$#14577#"); contentEntity.setVisible(content.isVisible());

		System.out.println("$#14578#"); return contentEntity;

	}

	private Content convertContentPageToContent(MerchantStore store, Language language,
			PersistableContentEntity content) throws ServiceException {
		Content contentModel = new Content();

		List<ContentDescription> descriptions = createContentDescription(store, contentModel, content);
		System.out.println("$#14579#"); descriptions.stream().forEach(c -> c.setContent(contentModel));

		System.out.println("$#14581#"); contentModel.setCode(content.getCode());
		System.out.println("$#14582#"); contentModel.setContentType(ContentType.PAGE);
		System.out.println("$#14583#"); contentModel.setMerchantStore(store);
		System.out.println("$#14584#"); contentModel.setLinkToMenu(content.isDisplayedInMenu());
		System.out.println("$#14585#"); contentModel.setVisible(true);// force visible
		System.out.println("$#14586#"); contentModel.setDescriptions(descriptions);
		System.out.println("$#14587#"); return contentModel;
	}

	private Content convertContentPageToContent(MerchantStore store, Language language, Content content,
			PersistableContentEntity contentPage) throws ServiceException {

		ContentType contentType = ContentType.valueOf(contentPage.getContentType());
		System.out.println("$#14588#"); if (contentType == null) {
			throw new ServiceRuntimeException("Invalid specified contentType [" + contentPage.getContentType() + "]");
		}

		List<ContentDescription> descriptions = createContentDescription(store, content, contentPage);
		System.out.println("$#14589#"); descriptions.stream().forEach(c -> c.setContent(content));

		System.out.println("$#14591#"); content.setDescriptions(descriptions);

		// ContentDescription contentDescription =
		// createContentDescription(store, contentPage, language);
		// setContentDescriptionToContentModel(content,contentDescription,language);

		// contentDescription.setContent(content);

		System.out.println("$#14593#"); System.out.println("$#14592#"); if (contentPage.getId() != null && contentPage.getId().longValue() > 0) {
			System.out.println("$#14595#"); content.setId(contentPage.getId());
		}
		System.out.println("$#14596#"); content.setVisible(contentPage.isVisible());
		System.out.println("$#14597#"); content.setLinkToMenu(contentPage.isDisplayedInMenu());
		System.out.println("$#14598#"); content.setContentType(ContentType.valueOf(contentPage.getContentType()));
		System.out.println("$#14599#"); content.setMerchantStore(store);

		System.out.println("$#14600#"); return content;
	}

	private List<ContentDescriptionEntity> createContentDescriptionEntitys(MerchantStore store, Content contentModel,
			Language language) throws ServiceException {

		List<ContentDescriptionEntity> descriptions = new ArrayList<ContentDescriptionEntity>();

		System.out.println("$#14601#"); if (!CollectionUtils.isEmpty(contentModel.getDescriptions())) {
			for (ContentDescription description : contentModel.getDescriptions()) {
				System.out.println("$#14602#"); if (language != null && !language.getId().equals(description.getLanguage().getId())) {
					continue;
				}

				ContentDescriptionEntity contentDescription = create(description);
				descriptions.add(contentDescription);
			}
		}

		System.out.println("$#14604#"); return descriptions;
	}

	private ContentDescriptionEntity create(ContentDescription description) {

		ContentDescriptionEntity contentDescription = new ContentDescriptionEntity();
		System.out.println("$#14605#"); contentDescription.setLanguage(description.getLanguage().getCode());
		System.out.println("$#14606#"); contentDescription.setTitle(description.getTitle());
		System.out.println("$#14607#"); contentDescription.setName(description.getName());
		System.out.println("$#14608#"); contentDescription.setFriendlyUrl(description.getSeUrl());
		System.out.println("$#14609#"); contentDescription.setDescription(description.getDescription());
		System.out.println("$#14611#"); System.out.println("$#14610#"); if (description.getId() != null && description.getId().longValue() > 0) {
			System.out.println("$#14613#"); contentDescription.setId(description.getId());
		}

		System.out.println("$#14614#"); return contentDescription;

	}

	private List<ContentDescription> createContentDescription(MerchantStore store, Content contentModel,
			PersistableContentEntity content) throws ServiceException {

		System.out.println("$#14615#"); if (contentModel != null) {

		}
		List<ContentDescription> descriptions = new ArrayList<ContentDescription>();
		for (ContentDescriptionEntity objectContent : content.getDescriptions()) {
			Language lang = languageService.getByCode(objectContent.getLanguage());
			ContentDescription contentDescription = new ContentDescription();
			System.out.println("$#14616#"); if (contentModel != null) {
				System.out.println("$#14617#"); setContentDescriptionToContentModel(contentModel, contentDescription, lang);
			}
			System.out.println("$#14618#"); contentDescription.setLanguage(lang);
			System.out.println("$#14619#"); contentDescription.setMetatagDescription(objectContent.getMetaDescription());
			System.out.println("$#14620#"); contentDescription.setTitle(objectContent.getTitle());
			System.out.println("$#14621#"); contentDescription.setName(objectContent.getName());
			System.out.println("$#14622#"); contentDescription.setSeUrl(objectContent.getFriendlyUrl());
			System.out.println("$#14623#"); contentDescription.setDescription(objectContent.getDescription());
			System.out.println("$#14624#"); contentDescription.setMetatagTitle(objectContent.getTitle());
			descriptions.add(contentDescription);
		}
		System.out.println("$#14625#"); return descriptions;
	}

	private void setContentDescriptionToContentModel(Content content, ContentDescription contentDescription,
			Language language) {

		Optional<ContentDescription> contentDescriptionModel = findAppropriateContentDescription(
				content.getDescriptions(), language);

		System.out.println("$#14626#"); if (contentDescriptionModel.isPresent()) {
			System.out.println("$#14627#"); contentDescription.setMetatagDescription(contentDescriptionModel.get().getMetatagDescription());
			System.out.println("$#14628#"); contentDescription.setDescription(contentDescriptionModel.get().getDescription());
			System.out.println("$#14629#"); contentDescription.setId(contentDescriptionModel.get().getId());
			System.out.println("$#14630#"); contentDescription.setAuditSection(contentDescriptionModel.get().getAuditSection());
			System.out.println("$#14631#"); contentDescription.setLanguage(contentDescriptionModel.get().getLanguage());
			System.out.println("$#14632#"); contentDescription.setTitle(contentDescriptionModel.get().getTitle());
			System.out.println("$#14633#"); contentDescription.setName(contentDescriptionModel.get().getName());
		} else {
			content.getDescriptions().add(contentDescription);
		}

	}

	@Override
	public ReadableContentPage getContentPage(String code, MerchantStore store, Language language) {

		System.out.println("$#14634#"); Validate.notNull(code, "Content code cannot be null");
		System.out.println("$#14635#"); Validate.notNull(store, "MerchantStore cannot be null");

		try {
			Content content = null;
			
			System.out.println("$#14636#"); if(language == null) {
				content = Optional.ofNullable(contentService.getByCode(code, store))
				.orElseThrow(() -> new ResourceNotFoundException("No page found : " + code));
			} else {
				content = Optional.ofNullable(contentService.getByCode(code, store, language))
				.orElseThrow(() -> new ResourceNotFoundException("No page found : " + code));
			}

			System.out.println("$#14639#"); return convertContentToReadableContentPage(store, language, content);

		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Error while getting page " + e.getMessage(), e);
		}
	}

	@Override
	public List<ReadableContentBox> getContentBoxes(ContentType type, String codePrefix, MerchantStore store,
			Language language) {

		System.out.println("$#14640#"); Validate.notNull(codePrefix, "content code prefix cannot be null");
		System.out.println("$#14641#"); Validate.notNull(store, "MerchantStore cannot be null");
		System.out.println("$#14642#"); Validate.notNull(language, "Language cannot be null");

		System.out.println("$#14643#"); return contentService.getByCodeLike(type, codePrefix, store, language).stream()
				.map(content -> convertContentToReadableContentBox(store, language, content))
				.collect(Collectors.toList());
	}

	@Override
	public void addContentFile(ContentFile file, String merchantStoreCode) {
		try {
			byte[] payload = file.getFile();
			String fileName = file.getName();

			try (InputStream targetStream = new ByteArrayInputStream(payload)) {

				String type = file.getContentType().split(FILE_CONTENT_DELIMETER)[0];
				FileContentType fileType = getFileContentType(type);

				InputContentFile cmsContent = new InputContentFile();
				System.out.println("$#14645#"); cmsContent.setFileName(fileName);
				System.out.println("$#14646#"); cmsContent.setMimeType(file.getContentType());
				System.out.println("$#14647#"); cmsContent.setFile(targetStream);
				System.out.println("$#14648#"); cmsContent.setFileContentType(fileType);

				System.out.println("$#14649#"); contentService.addContentFile(merchantStoreCode, cmsContent);
			}
		} catch (ServiceException | IOException e) {
			throw new ServiceRuntimeException(e);
		}
	}

	private FileContentType getFileContentType(String type) {
		FileContentType fileType = FileContentType.STATIC_FILE;
		System.out.println("$#14650#"); if (type.equals("image")) {//for now we consider this route from api only
			fileType = FileContentType.API_IMAGE;
		}
		System.out.println("$#14651#"); return fileType;
	}

	private ReadableContentBox convertContentToReadableContentBox(MerchantStore store, Language language,
			Content content) {
		ReadableContentBox box = new ReadableContentBox();
		Optional<ContentDescription> contentDescription = findAppropriateContentDescription(content.getDescriptions(),
				language);
		System.out.println("$#14652#"); if (contentDescription.isPresent()) {
			System.out.println("$#14653#"); box.setName(contentDescription.get().getName());
			System.out.println("$#14654#"); box.setBoxContent(contentDescription.get().getDescription());
		}
		String staticImageFilePath = imageUtils.buildStaticImageUtils(store, content.getCode() + ".jpg");
		System.out.println("$#14655#"); box.setImage(staticImageFilePath);
		System.out.println("$#14656#"); return box;
	}

	private Optional<ContentDescription> findAppropriateContentDescription(List<ContentDescription> contentDescriptions,
			Language language) {
		System.out.println("$#14657#"); return contentDescriptions.stream()
				.filter(description -> description.getLanguage().getCode().equals(language.getCode())).findFirst();
	}

	@Override
	public ReadableContentBox getContentBox(String code, MerchantStore store, Language language) {
		System.out.println("$#14660#"); Validate.notNull(code, "Content code cannot be null");
		System.out.println("$#14661#"); Validate.notNull(store, "MerchantStore cannot be null");
		System.out.println("$#14662#"); Validate.notNull(language, "Language cannot be null");

		try {
			Content content = Optional.ofNullable(contentService.getByCode(code, store, language))
					.orElseThrow(() -> new ResourceNotFoundException(
							"Resource not found [" + code + "] for store [" + store.getCode() + "]"));

			Optional<ContentDescription> contentDescription = findAppropriateContentDescription(
					content.getDescriptions(), language);

			ReadableContentBox box = new ReadableContentBox();
			System.out.println("$#14664#"); if (contentDescription.isPresent()) {
				System.out.println("$#14665#"); box.setName(contentDescription.get().getSeUrl());
				System.out.println("$#14666#"); box.setBoxContent("<![CDATA["
						+ contentDescription.get().getDescription().replaceAll("\r\n", "").replaceAll("\t", "")
						+ "]]>");
			}
			System.out.println("$#14667#"); return box;
		} catch (ServiceException e) {
			throw new ServiceRuntimeException(e);
		}
	}

	@Override
	public void saveContentPage(PersistableContentEntity page, MerchantStore merchantStore, Language language) {
		System.out.println("$#14668#"); Validate.notNull(page);
		System.out.println("$#14669#"); Validate.notNull(merchantStore);

		try {
			Content content = null;
			System.out.println("$#14671#"); System.out.println("$#14670#"); if (page.getId() != null && page.getId() > 0) {
				content = contentService.getById(page.getId());
			} else {
				System.out.println("$#14673#"); Validate.notNull(page.getCode(), "Content code must not be null");
				content = contentService.getByCode(page.getCode(), merchantStore);
			}
			System.out.println("$#14674#"); if (content != null) {
				content = convertContentPageToContent(merchantStore, language, content, page);
			} else {
				content = convertContentPageToContent(merchantStore, language, page);
			}
			System.out.println("$#14675#"); contentService.saveOrUpdate(content);
		} catch (Exception e) {
			throw new ServiceRuntimeException(e);
		}

	}

	@Override
	public void addContentFiles(List<ContentFile> files, String merchantStoreCode) {
		for (ContentFile file : files) {
			System.out.println("$#14676#"); addContentFile(file, merchantStoreCode);
		}

	}

	@Override
	public void delete(MerchantStore store, Long id) {
		System.out.println("$#14677#"); Validate.notNull(store, "MerchantStore not null");
		System.out.println("$#14678#"); Validate.notNull(id, "Content id must not be null");
		// select content first
		Content content = contentService.getById(id);
		System.out.println("$#14679#"); if (content != null) {
			System.out.println("$#14680#"); if (content.getMerchantStore().getId().intValue() != store.getId().intValue()) {
				throw new ResourceNotFoundException(
						"No content found with id [" + id + "] for store [" + store.getCode() + "]");
			}
		}

		try {
			System.out.println("$#14681#"); contentService.delete(content);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Exception while deleting content " + e.getMessage(), e);
		}

	}

	@Override
	public ReadableContentFull getContent(String code, MerchantStore store, Language language) {
		System.out.println("$#14682#"); Validate.notNull(store, "MerchantStore not null");
		System.out.println("$#14683#"); Validate.notNull(code, "Content code must not be null");

		try {
			Content content = contentService.getByCode(code, store);
			System.out.println("$#14684#"); if (content == null) {
				throw new ResourceNotFoundException(
						"No content found with code [" + code + "] for store [" + store.getCode() + "]");
			}

			System.out.println("$#14685#"); return this.convertContentToReadableContentFull(store, language, content);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Error while getting content [" + code + "]", e);
		}

	}

	@Override
	public List<ReadableContentEntity> getContents(Optional<String> type, MerchantStore store, Language language) {

		/**
		 * get all types
		 */
		List<ContentType> types = new ArrayList<ContentType>();
		types.add(ContentType.BOX);
		types.add(ContentType.PAGE);
		types.add(ContentType.SECTION);

		try {
			System.out.println("$#14686#"); return contentService.listByType(types, store, language).stream()
					.map(content -> convertContentToReadableContentEntity(store, language, content))
					.collect(Collectors.toList());

		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Exception while getting contents", e);
		}

	}

	@Override
	public ReadableContentPage getContentPageByName(String name, MerchantStore store, Language language) {
		System.out.println("$#14688#"); Validate.notNull(name, "Content name cannot be null");
		System.out.println("$#14689#"); Validate.notNull(store, "MerchantStore cannot be null");
		System.out.println("$#14690#"); Validate.notNull(language, "Language cannot be null");

		try {
	
			
			ContentDescription contentDescription = Optional.ofNullable(contentService.getBySeUrl(store, name))
					.orElseThrow(() -> new ResourceNotFoundException("No page found : " + name));

			System.out.println("$#14692#"); return this.contentDescriptionToReadableContent(store, contentDescription.getContent(), contentDescription);

		} catch (Exception e) {
			throw new ServiceRuntimeException("Error while getting page " + e.getMessage(), e);
		}
	}

	@Override
	public void renameFile(MerchantStore store, FileContentType fileType, String originalName, String newName) {
		Optional<String> path = Optional.ofNullable(null);
		try {
			System.out.println("$#14693#"); contentService.renameFile(store.getCode(), fileType, path, originalName, newName);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Error while renaming file " + e.getMessage(), e);
		}
		
	}

	@Override
	public OutputContentFile download(MerchantStore store, FileContentType fileType, String fileName) {
		
		try {
			System.out.println("$#14694#"); return contentService.getContentFile(store.getCode(), fileType, fileName);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Error while downloading file " + e.getMessage(), e);
		}
		

	}

}
