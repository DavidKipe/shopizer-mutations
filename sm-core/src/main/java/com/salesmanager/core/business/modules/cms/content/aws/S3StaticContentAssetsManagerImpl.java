package com.salesmanager.core.business.modules.cms.content.aws;

import java.io.ByteArrayOutputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.modules.cms.content.ContentAssetsManager;
import com.salesmanager.core.business.modules.cms.impl.CMSManager;
import com.salesmanager.core.model.content.FileContentType;
import com.salesmanager.core.model.content.InputContentFile;
import com.salesmanager.core.model.content.OutputContentFile;

/**
 * Static content management with S3
 * 
 * @author carlsamson
 *
 */
public class S3StaticContentAssetsManagerImpl implements ContentAssetsManager {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(S3StaticContentAssetsManagerImpl.class);

	private static S3StaticContentAssetsManagerImpl fileManager = null;

	private CMSManager cmsManager;

	public static S3StaticContentAssetsManagerImpl getInstance() {

		System.out.println("$#39#"); if (fileManager == null) {
			fileManager = new S3StaticContentAssetsManagerImpl();
		}

		System.out.println("$#40#"); return fileManager;

	}

	@Override
	public OutputContentFile getFile(String merchantStoreCode, Optional<String> folderPath, FileContentType fileContentType, String contentName)
			throws ServiceException {
		try {
			// get buckets
			String bucketName = bucketName();

			final AmazonS3 s3 = s3Client();

			S3Object o = s3.getObject(bucketName, nodePath(merchantStoreCode, fileContentType) + contentName);

			LOGGER.info("Content getFile");
			System.out.println("$#41#"); return getOutputContentFile(IOUtils.toByteArray(o.getObjectContent()));
		} catch (final Exception e) {
			LOGGER.error("Error while getting file", e);
			throw new ServiceException(e);

		}
	}

	@Override
	public List<String> getFileNames(String merchantStoreCode, Optional<String> folderPath, FileContentType fileContentType)
			throws ServiceException {
		try {
			// get buckets
			String bucketName = bucketName();

			ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request().withBucketName(bucketName)
					.withPrefix(nodePath(merchantStoreCode, fileContentType));

			List<String> fileNames = null;

			final AmazonS3 s3 = s3Client();
			ListObjectsV2Result results = s3.listObjectsV2(listObjectsRequest);
			List<S3ObjectSummary> objects = results.getObjectSummaries();
			for (S3ObjectSummary os : objects) {
				System.out.println("$#42#"); if (isInsideSubFolder(os.getKey())) {
					continue;
				}
				System.out.println("$#43#"); if (fileNames == null) {
					fileNames = new ArrayList<String>();
				}
				String mimetype = URLConnection.guessContentTypeFromName(os.getKey());
				System.out.println("$#44#"); if (!StringUtils.isBlank(mimetype)) {
					fileNames.add(getName(os.getKey()));
				}
			}

			LOGGER.info("Content get file names");
			System.out.println("$#45#"); return fileNames;
		} catch (final Exception e) {
			LOGGER.error("Error while getting file names", e);
			throw new ServiceException(e);

		}
	}

	@Override
	public List<OutputContentFile> getFiles(String merchantStoreCode, Optional<String> folderPath, FileContentType fileContentType)
			throws ServiceException {
		try {
			// get buckets
			String bucketName = bucketName();

			ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request().withBucketName(bucketName)
					.withPrefix(nodePath(merchantStoreCode, fileContentType));

			List<OutputContentFile> files = null;
			final AmazonS3 s3 = s3Client();
			ListObjectsV2Result results = s3.listObjectsV2(listObjectsRequest);
			List<S3ObjectSummary> objects = results.getObjectSummaries();
			for (S3ObjectSummary os : objects) {
				System.out.println("$#46#"); if (files == null) {
					files = new ArrayList<OutputContentFile>();
				}
				String mimetype = URLConnection.guessContentTypeFromName(os.getKey());
				System.out.println("$#47#"); if (!StringUtils.isBlank(mimetype)) {
					S3Object o = s3.getObject(bucketName, os.getKey());
					byte[] byteArray = IOUtils.toByteArray(o.getObjectContent());
					ByteArrayOutputStream baos = new ByteArrayOutputStream(byteArray.length);
					System.out.println("$#48#"); baos.write(byteArray, 0, byteArray.length);
					OutputContentFile ct = new OutputContentFile();
					System.out.println("$#49#"); ct.setFile(baos);
					files.add(ct);
				}
			}

			LOGGER.info("Content getFiles");
			System.out.println("$#50#"); return files;
		} catch (final Exception e) {
			LOGGER.error("Error while getting files", e);
			throw new ServiceException(e);

		}
	}

	@Override
	public void addFile(String merchantStoreCode, Optional<String> folderPath, InputContentFile inputStaticContentData) throws ServiceException {

		try {
			// get buckets
			String bucketName = bucketName();

			String nodePath = nodePath(merchantStoreCode, inputStaticContentData.getFileContentType());

			final AmazonS3 s3 = s3Client();

			ObjectMetadata metadata = new ObjectMetadata();
			System.out.println("$#51#"); metadata.setContentType(inputStaticContentData.getMimeType());
			PutObjectRequest request = new PutObjectRequest(bucketName, nodePath + inputStaticContentData.getFileName(),
					inputStaticContentData.getFile(), metadata);
			System.out.println("$#52#"); request.setCannedAcl(CannedAccessControlList.PublicRead);

			s3.putObject(request);

			LOGGER.info("Content add file");
		} catch (final Exception e) {
			LOGGER.error("Error while adding file", e);
			throw new ServiceException(e);

		}

	}

	@Override
	public void addFiles(String merchantStoreCode, Optional<String> folderPath, List<InputContentFile> inputStaticContentDataList)
			throws ServiceException {

		System.out.println("$#53#"); if (CollectionUtils.isNotEmpty(inputStaticContentDataList)) {
			for (InputContentFile inputFile : inputStaticContentDataList) {
				System.out.println("$#54#"); this.addFile(merchantStoreCode, folderPath, inputFile);
			}

		}

	}

	@Override
	public void removeFile(String merchantStoreCode, FileContentType staticContentType, String fileName, Optional<String> folderPath)
			throws ServiceException {

		try {
			// get buckets
			String bucketName = bucketName();

			final AmazonS3 s3 = s3Client();
			System.out.println("$#55#"); s3.deleteObject(bucketName, nodePath(merchantStoreCode, staticContentType) + fileName);

			LOGGER.info("Remove file");
		} catch (final Exception e) {
			LOGGER.error("Error while removing file", e);
			throw new ServiceException(e);

		}

	}

	@Override
	public void removeFiles(String merchantStoreCode, Optional<String> folderPath) throws ServiceException {

		try {
			// get buckets
			String bucketName = bucketName();

			final AmazonS3 s3 = s3Client();
			System.out.println("$#56#"); s3.deleteObject(bucketName, nodePath(merchantStoreCode));

			LOGGER.info("Remove folder");
		} catch (final Exception e) {
			LOGGER.error("Error while removing folder", e);
			throw new ServiceException(e);

		}

	}

	private Bucket getBucket(String bucket_name) {
		final AmazonS3 s3 = s3Client();
		Bucket named_bucket = null;
		List<Bucket> buckets = s3.listBuckets();
		for (Bucket b : buckets) {
			System.out.println("$#57#"); if (b.getName().equals(bucket_name)) {
				named_bucket = b;
			}
		}

		System.out.println("$#58#"); if (named_bucket == null) {
			named_bucket = createBucket(bucket_name);
		}

		System.out.println("$#59#"); return named_bucket;
	}

	private Bucket createBucket(String bucket_name) {
		final AmazonS3 s3 = s3Client();
		Bucket b = null;
		System.out.println("$#60#"); if (s3.doesBucketExistV2(bucket_name)) {
			System.out.format("Bucket %s already exists.\n", bucket_name);
			b = getBucket(bucket_name);
		} else {
			try {
				b = s3.createBucket(bucket_name);
			} catch (AmazonS3Exception e) {
				System.out.println("$#61#"); System.err.println(e.getErrorMessage());
			}
		}
		System.out.println("$#62#"); return b;
	}

	/**
	 * Builds an amazon S3 client
	 * 
	 * @return
	 */
	private AmazonS3 s3Client() {
		String region = regionName();
		LOGGER.debug("AWS CMS Using region " + region);
		AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(region) // The
																			// first
																			// region
																			// to
																			// try
																			// your
																			// request
																			// against
				.build();

		System.out.println("$#63#"); return s3;
	}

	private String regionName() {
		String regionName = getCmsManager().getLocation();
		System.out.println("$#64#"); if (StringUtils.isBlank(regionName)) {
			regionName = DEFAULT_REGION_NAME;
		}
		System.out.println("$#65#"); return regionName;
	}

	public CMSManager getCmsManager() {
		System.out.println("$#66#"); return cmsManager;
	}

	public void setCmsManager(CMSManager cmsManager) {
		this.cmsManager = cmsManager;
	}

	@Override
	public void addFolder(String merchantStoreCode, String folderName, Optional<String> folderPath) throws ServiceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeFolder(String merchantStoreCode, String folderName, Optional<String> folderPath) throws ServiceException {
		// TODO Auto-generated method stub

	}


	@Override
	public List<String> listFolders(String merchantStoreCode, Optional<String> path) throws ServiceException {
		// TODO Auto-generated method stub
		System.out.println("$#67#"); return null;
	}

}
