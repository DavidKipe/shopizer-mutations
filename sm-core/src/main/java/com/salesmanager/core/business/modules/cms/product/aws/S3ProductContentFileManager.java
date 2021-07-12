package com.salesmanager.core.business.modules.cms.product.aws;

import java.io.ByteArrayOutputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
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
import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.modules.cms.impl.CMSManager;
import com.salesmanager.core.business.modules.cms.product.ProductAssetsManager;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.file.ProductImageSize;
import com.salesmanager.core.model.catalog.product.image.ProductImage;
import com.salesmanager.core.model.content.FileContentType;
import com.salesmanager.core.model.content.ImageContentFile;
import com.salesmanager.core.model.content.OutputContentFile;

/**
 * Product content file manager with AWS S3
 *
 * @author carlsamson
 *
 */
public class S3ProductContentFileManager
    implements ProductAssetsManager {

  /**
   *
   */
  private static final long serialVersionUID = 1L;



  private static final Logger LOGGER = LoggerFactory.getLogger(S3ProductContentFileManager.class);



  private static S3ProductContentFileManager fileManager = null;

  private static String DEFAULT_BUCKET_NAME = "shopizer-content";
  private static String DEFAULT_REGION_NAME = "us-east-1";
  private static final String ROOT_NAME = "products";

  private static final char UNIX_SEPARATOR = '/';
  private static final char WINDOWS_SEPARATOR = '\\';


  private final static String SMALL = "SMALL";
  private final static String LARGE = "LARGE";

  private CMSManager cmsManager;

  public static S3ProductContentFileManager getInstance() {

				System.out.println("$#195#"); if (fileManager == null) {
      fileManager = new S3ProductContentFileManager();
    }

				System.out.println("$#196#"); return fileManager;

  }

  @Override
  public List<OutputContentFile> getImages(String merchantStoreCode,
      FileContentType imageContentType) throws ServiceException {
    try {
      // get buckets
      String bucketName = bucketName();



      ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request()
          .withBucketName(bucketName).withPrefix(nodePath(merchantStoreCode));

      List<OutputContentFile> files = null;
      final AmazonS3 s3 = s3Client();
      ListObjectsV2Result results = s3.listObjectsV2(listObjectsRequest);
      List<S3ObjectSummary> objects = results.getObjectSummaries();
      for (S3ObjectSummary os : objects) {
								System.out.println("$#197#"); if (files == null) {
          files = new ArrayList<OutputContentFile>();
        }
        String mimetype = URLConnection.guessContentTypeFromName(os.getKey());
								System.out.println("$#198#"); if (!StringUtils.isBlank(mimetype)) {
          S3Object o = s3.getObject(bucketName, os.getKey());
          byte[] byteArray = IOUtils.toByteArray(o.getObjectContent());
          ByteArrayOutputStream baos = new ByteArrayOutputStream(byteArray.length);
										System.out.println("$#199#"); baos.write(byteArray, 0, byteArray.length);
          OutputContentFile ct = new OutputContentFile();
										System.out.println("$#200#"); ct.setFile(baos);
          files.add(ct);
        }
      }

						System.out.println("$#201#"); return files;
    } catch (final Exception e) {
      LOGGER.error("Error while getting files", e);
      throw new ServiceException(e);

    }
  }

  @Override
  public void removeImages(String merchantStoreCode) throws ServiceException {
    try {
      // get buckets
      String bucketName = bucketName();

      final AmazonS3 s3 = s3Client();
						System.out.println("$#202#"); s3.deleteObject(bucketName, nodePath(merchantStoreCode));

      LOGGER.info("Remove folder");
    } catch (final Exception e) {
      LOGGER.error("Error while removing folder", e);
      throw new ServiceException(e);

    }

  }

  @Override
  public void removeProductImage(ProductImage productImage) throws ServiceException {
    try {
      // get buckets
      String bucketName = bucketName();

      final AmazonS3 s3 = s3Client();
						System.out.println("$#203#"); s3.deleteObject(bucketName, nodePath(productImage.getProduct().getMerchantStore().getCode(),
          productImage.getProduct().getSku()) + productImage.getProductImage());

      LOGGER.info("Remove file");
    } catch (final Exception e) {
      LOGGER.error("Error while removing file", e);
      throw new ServiceException(e);

    }

  }

  @Override
  public void removeProductImages(Product product) throws ServiceException {
    try {
      // get buckets
      String bucketName = bucketName();

      final AmazonS3 s3 = s3Client();
						System.out.println("$#204#"); s3.deleteObject(bucketName, nodePath(product.getMerchantStore().getCode(), product.getSku()));

      LOGGER.info("Remove file");
    } catch (final Exception e) {
      LOGGER.error("Error while removing file", e);
      throw new ServiceException(e);

    }

  }

  @Override
  public OutputContentFile getProductImage(String merchantStoreCode, String productCode,
      String imageName) throws ServiceException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public OutputContentFile getProductImage(String merchantStoreCode, String productCode,
      String imageName, ProductImageSize size) throws ServiceException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public OutputContentFile getProductImage(ProductImage productImage) throws ServiceException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<OutputContentFile> getImages(Product product) throws ServiceException {
				System.out.println("$#205#"); return null;
  }

  @Override
  public void addProductImage(ProductImage productImage, ImageContentFile contentImage)
      throws ServiceException {


    try {
      // get buckets
      String bucketName = bucketName();
      final AmazonS3 s3 = s3Client();

      String nodePath = this.nodePath(productImage.getProduct().getMerchantStore().getCode(),
          productImage.getProduct().getSku(), contentImage);


      ObjectMetadata metadata = new ObjectMetadata();
						System.out.println("$#206#"); metadata.setContentType(contentImage.getMimeType());

      PutObjectRequest request = new PutObjectRequest(bucketName,
          nodePath + productImage.getProductImage(), contentImage.getFile(), metadata);
						System.out.println("$#207#"); request.setCannedAcl(CannedAccessControlList.PublicRead);


      s3.putObject(request);


      LOGGER.info("Product add file");

    } catch (final Exception e) {
      LOGGER.error("Error while removing file", e);
      throw new ServiceException(e);

    }


  }


  private Bucket getBucket(String bucket_name) {
    final AmazonS3 s3 = s3Client();
    Bucket named_bucket = null;
    List<Bucket> buckets = s3.listBuckets();
    for (Bucket b : buckets) {
						System.out.println("$#208#"); if (b.getName().equals(bucket_name)) {
        named_bucket = b;
      }
    }

				System.out.println("$#209#"); if (named_bucket == null) {
      named_bucket = createBucket(bucket_name);
    }

				System.out.println("$#210#"); return named_bucket;
  }

  private Bucket createBucket(String bucket_name) {
    final AmazonS3 s3 = s3Client();
    Bucket b = null;
				System.out.println("$#211#"); if (s3.doesBucketExistV2(bucket_name)) {
      System.out.format("Bucket %s already exists.\n", bucket_name);
      b = getBucket(bucket_name);
    } else {
      try {
        b = s3.createBucket(bucket_name);
      } catch (AmazonS3Exception e) {
								System.out.println("$#212#"); System.err.println(e.getErrorMessage());
      }
    }
				System.out.println("$#213#"); return b;
  }

  /**
   * Builds an amazon S3 client
   *
   * @return
   */
  private AmazonS3 s3Client() {

    AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(regionName()) // The first region to
                                                                            // try your request
                                                                            // against
        .build();

				System.out.println("$#214#"); return s3;
  }

  private String bucketName() {
    String bucketName = getCmsManager().getRootName();
				System.out.println("$#215#"); if (StringUtils.isBlank(bucketName)) {
      bucketName = DEFAULT_BUCKET_NAME;
    }
				System.out.println("$#216#"); return bucketName;
  }

  private String regionName() {
    String regionName = getCmsManager().getLocation();
				System.out.println("$#217#"); if (StringUtils.isBlank(regionName)) {
      regionName = DEFAULT_REGION_NAME;
    }
				System.out.println("$#218#"); return regionName;
  }

  private String nodePath(String store) {
				System.out.println("$#219#"); return new StringBuilder().append(ROOT_NAME).append(Constants.SLASH).append(store)
        .append(Constants.SLASH).toString();
  }

  private String nodePath(String store, String product) {

    StringBuilder sb = new StringBuilder();
    // node path
    String nodePath = nodePath(store);
    sb.append(nodePath);

    // product path
    sb.append(product).append(Constants.SLASH);
				System.out.println("$#220#"); return sb.toString();

  }

  private String nodePath(String store, String product, ImageContentFile contentImage) {

    StringBuilder sb = new StringBuilder();
    // node path
    String nodePath = nodePath(store, product);
    sb.append(nodePath);

    // small large
				System.out.println("$#221#"); if (contentImage.getFileContentType().name().equals(FileContentType.PRODUCT.name())) {
      sb.append(SMALL);
				} else if (contentImage.getFileContentType().name().equals(FileContentType.PRODUCTLG.name())) { System.out.println("$#222#");
      sb.append(LARGE);
    } else {
				  System.out.println("$#222#"); // manual correction for else-if mutation coverage
    }

				System.out.println("$#223#"); return sb.append(Constants.SLASH).toString();


  }

  public static String getName(String filename) {
				System.out.println("$#224#"); if (filename == null) {
						System.out.println("$#225#"); return null;
    }
    int index = indexOfLastSeparator(filename);
				System.out.println("$#227#"); System.out.println("$#226#"); return filename.substring(index + 1);
  }

  public static int indexOfLastSeparator(String filename) {
				System.out.println("$#228#"); if (filename == null) {
						System.out.println("$#229#"); return -1;
    }
    int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
    int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
				System.out.println("$#230#"); return Math.max(lastUnixPos, lastWindowsPos);
  }



  public CMSManager getCmsManager() {
				System.out.println("$#231#"); return cmsManager;
  }

  public void setCmsManager(CMSManager cmsManager) {
    this.cmsManager = cmsManager;
  }


}
