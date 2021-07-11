/**
 * 
 */
package com.salesmanager.core.business.modules.cms.content;

import java.util.List;
import java.util.Optional;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.modules.cms.content.infinispan.CmsStaticContentFileManagerImpl;
import com.salesmanager.core.business.modules.cms.impl.CMSManager;
import com.salesmanager.core.model.content.FileContentType;
import com.salesmanager.core.model.content.InputContentFile;
import com.salesmanager.core.model.content.OutputContentFile;

/**
 * @author Umesh Awasthi
 *
 */
public class StaticContentFileManagerImpl extends StaticContentFileManager {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FilePut uploadFile;
	private FileGet getFile;
	private FileRemove removeFile;
	private FolderRemove removeFolder;
	private FolderPut addFolder;
	private FolderList listFolder;

	@Override
	public void addFile(final String merchantStoreCode, Optional<String> path, final InputContentFile inputContentFile)
			throws ServiceException {
		System.out.println("$#155#"); uploadFile.addFile(merchantStoreCode, path, inputContentFile);

	}

	/**
	 * Implementation for add static data files. This method will called
	 * respected add files method of underlying CMSStaticContentManager. For CMS
	 * Content files {@link CmsStaticContentFileManagerImpl} will take care of
	 * adding given content images with Infinispan cache.
	 * 
	 * @param merchantStoreCode
	 *            merchant store.
	 * @param inputStaticContentDataList
	 *            Input content images
	 * @throws ServiceException
	 */
	@Override
	public void addFiles(final String merchantStoreCode, Optional<String> path, final List<InputContentFile> inputStaticContentDataList)
			throws ServiceException {
		System.out.println("$#156#"); uploadFile.addFiles(merchantStoreCode, path, inputStaticContentDataList);
	}

	@Override
	public void removeFile(final String merchantStoreCode, final FileContentType staticContentType,
			final String fileName, Optional<String> path) throws ServiceException {
		System.out.println("$#157#"); removeFile.removeFile(merchantStoreCode, staticContentType, fileName, path);

	}

	@Override
	public OutputContentFile getFile(String merchantStoreCode, Optional<String> path, FileContentType fileContentType, String contentName)
			throws ServiceException {
		System.out.println("$#158#"); return getFile.getFile(merchantStoreCode, path, fileContentType, contentName);
	}

	@Override
	public List<String> getFileNames(String merchantStoreCode, Optional<String> path, FileContentType fileContentType)
			throws ServiceException {
		System.out.println("$#159#"); return getFile.getFileNames(merchantStoreCode, path, fileContentType);
	}

	@Override
	public List<OutputContentFile> getFiles(String merchantStoreCode, Optional<String> path, FileContentType fileContentType)
			throws ServiceException {
		System.out.println("$#160#"); return getFile.getFiles(merchantStoreCode, path, fileContentType);
	}

	@Override
	public void removeFiles(String merchantStoreCode, Optional<String> path) throws ServiceException {
		System.out.println("$#161#"); removeFile.removeFiles(merchantStoreCode, path);
	}

	public void setRemoveFile(FileRemove removeFile) {
		this.removeFile = removeFile;
	}

	public FileRemove getRemoveFile() {
		System.out.println("$#162#"); return removeFile;
	}

	public void setGetFile(FileGet getFile) {
		this.getFile = getFile;
	}

	public FileGet getGetFile() {
		System.out.println("$#163#"); return getFile;
	}

	public void setUploadFile(FilePut uploadFile) {
		this.uploadFile = uploadFile;
	}

	public FilePut getUploadFile() {
		System.out.println("$#164#"); return uploadFile;
	}

	@Override
	public void removeFolder(String merchantStoreCode, String folderName, Optional<String> path) throws ServiceException {
		System.out.println("$#165#"); this.removeFolder.removeFolder(merchantStoreCode, folderName, path);

	}

	@Override
	public void addFolder(String merchantStoreCode, String folderName, Optional<String> path) throws ServiceException {
		System.out.println("$#166#"); addFolder.addFolder(merchantStoreCode, folderName, path);
	}

	public FolderRemove getRemoveFolder() {
		System.out.println("$#167#"); return removeFolder;
	}

	public void setRemoveFolder(FolderRemove removeFolder) {
		this.removeFolder = removeFolder;
	}

	public FolderPut getAddFolder() {
		System.out.println("$#168#"); return addFolder;
	}

	public void setAddFolder(FolderPut addFolder) {
		this.addFolder = addFolder;
	}

	@Override
	public List<String> listFolders(String merchantStoreCode, Optional<String> path) throws ServiceException {
		System.out.println("$#169#"); return this.listFolder.listFolders(merchantStoreCode, path);
	}

	public FolderList getListFolder() {
		System.out.println("$#170#"); return listFolder;
	}

	public void setListFolder(FolderList listFolder) {
		this.listFolder = listFolder;
	}

	@Override
	public CMSManager getCmsManager() {
		// TODO Auto-generated method stub
		return null;
	}

}
