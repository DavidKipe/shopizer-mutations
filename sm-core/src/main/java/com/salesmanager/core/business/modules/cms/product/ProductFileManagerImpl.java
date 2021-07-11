package com.salesmanager.core.business.modules.cms.product;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.utils.CoreConfiguration;
import com.salesmanager.core.business.utils.ProductImageCropUtils;
import com.salesmanager.core.business.utils.ProductImageSizeUtils;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.file.ProductImageSize;
import com.salesmanager.core.model.catalog.product.image.ProductImage;
import com.salesmanager.core.model.content.FileContentType;
import com.salesmanager.core.model.content.ImageContentFile;
import com.salesmanager.core.model.content.OutputContentFile;


public class ProductFileManagerImpl extends ProductFileManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductFileManagerImpl.class);


  private ProductImagePut uploadImage;
  private ProductImageGet getImage;
  private ProductImageRemove removeImage;

  private CoreConfiguration configuration;

  private final static String PRODUCT_IMAGE_HEIGHT_SIZE = "PRODUCT_IMAGE_HEIGHT_SIZE";
  private final static String PRODUCT_IMAGE_WIDTH_SIZE = "PRODUCT_IMAGE_WIDTH_SIZE";
  private final static String CROP_UPLOADED_IMAGES = "CROP_UPLOADED_IMAGES";


  public CoreConfiguration getConfiguration() {
				System.out.println("$#315#"); return configuration;
  }


  public void setConfiguration(CoreConfiguration configuration) {
    this.configuration = configuration;
  }


  public ProductImageRemove getRemoveImage() {
				System.out.println("$#316#"); return removeImage;
  }


  public void setRemoveImage(ProductImageRemove removeImage) {
    this.removeImage = removeImage;
  }


  public void addProductImage(ProductImage productImage, ImageContentFile contentImage)
      throws ServiceException {


    try {

      /** copy to input stream **/
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      // Fake code simulating the copy
      // You can generally do better with nio if you need...
      // And please, unlike me, do something about the Exceptions :D
      byte[] buffer = new byte[1024];
      int len;
						System.out.println("$#318#"); System.out.println("$#317#"); while ((len = contentImage.getFile().read(buffer)) > -1) {
								System.out.println("$#319#"); baos.write(buffer, 0, len);
      }
						System.out.println("$#320#"); baos.flush();

      // Open new InputStreams using the recorded bytes
      // Can be repeated as many times as you wish
      InputStream is1 = new ByteArrayInputStream(baos.toByteArray());
      InputStream is2 = new ByteArrayInputStream(baos.toByteArray());

      BufferedImage bufferedImage = ImageIO.read(is2);


						System.out.println("$#321#"); if (bufferedImage == null) {
        LOGGER.error("Cannot read image format for " + productImage.getProductImage());
        throw new Exception("Cannot read image format " + productImage.getProductImage());
      }

      // contentImage.setBufferedImage(bufferedImage);
						System.out.println("$#322#"); contentImage.setFile(is1);


      // upload original -- L
						System.out.println("$#323#"); contentImage.setFileContentType(FileContentType.PRODUCTLG);
						System.out.println("$#324#"); uploadImage.addProductImage(productImage, contentImage);

      /*
       * //default large InputContentImage largeContentImage = new
       * InputContentImage(ImageContentType.PRODUCT);
       * largeContentImage.setFile(contentImage.getFile());
       * largeContentImage.setDefaultImage(productImage.isDefaultImage());
       * largeContentImage.setImageName(new
       * StringBuilder().append("L-").append(productImage.getProductImage()).toString());
       * 
       * 
       * uploadImage.uploadProductImage(configuration, productImage, largeContentImage);
       */

      /*
       * //default small InputContentImage smallContentImage = new
       * InputContentImage(ImageContentType.PRODUCT);
       * smallContentImage.setFile(contentImage.getFile());
       * smallContentImage.setDefaultImage(productImage.isDefaultImage());
       * smallContentImage.setImageName(new
       * StringBuilder().append("S-").append(productImage.getProductImage()).toString());
       * 
       * uploadImage.uploadProductImage(configuration, productImage, smallContentImage);
       */


      // get template properties file

      String slargeImageHeight = configuration.getProperty(PRODUCT_IMAGE_HEIGHT_SIZE);
      String slargeImageWidth = configuration.getProperty(PRODUCT_IMAGE_WIDTH_SIZE);

      // String ssmallImageHeight = configuration.getProperty("SMALL_IMAGE_HEIGHT_SIZE");
      // String ssmallImageWidth = configuration.getProperty("SMALL_IMAGE_WIDTH_SIZE");

      //Resizes
						System.out.println("$#325#"); if (!StringUtils.isBlank(slargeImageHeight) && !StringUtils.isBlank(slargeImageWidth)) { // &&
                                                                                               // !StringUtils.isBlank(ssmallImageHeight)
                                                                                               // &&
                                                                                               // !StringUtils.isBlank(ssmallImageWidth))
                                                                                               // {


        FileNameMap fileNameMap = URLConnection.getFileNameMap();

        String contentType = fileNameMap.getContentTypeFor(contentImage.getFileName());
        String extension = null;
								System.out.println("$#327#"); if (contentType != null) {
										System.out.println("$#328#"); extension = contentType.substring(contentType.indexOf('/') + 1, contentType.length());
        }

								System.out.println("$#329#"); if (extension == null) {
          extension = "jpeg";
        }


        int largeImageHeight = Integer.parseInt(slargeImageHeight);
        int largeImageWidth = Integer.parseInt(slargeImageWidth);

								System.out.println("$#332#"); System.out.println("$#330#"); if (largeImageHeight <= 0 || largeImageWidth <= 0) {
          String sizeMsg =
              "Image configuration set to an invalid value [PRODUCT_IMAGE_HEIGHT_SIZE] "
                  + largeImageHeight + " , [PRODUCT_IMAGE_WIDTH_SIZE] " + largeImageWidth;
          LOGGER.error(sizeMsg);
          throw new ServiceException(sizeMsg);
        }

								System.out.println("$#334#"); if (!StringUtils.isBlank(configuration.getProperty(CROP_UPLOADED_IMAGES))
            && configuration.getProperty(CROP_UPLOADED_IMAGES).equals(Constants.TRUE)) {
          // crop image
          ProductImageCropUtils utils =
              new ProductImageCropUtils(bufferedImage, largeImageWidth, largeImageHeight);
										System.out.println("$#336#"); if (utils.isCropeable()) {
            bufferedImage = utils.getCroppedImage();
          }
        }

        // do not keep a large image for now, just take care of the regular image and a small image

        // resize large
        // ByteArrayOutputStream output = new ByteArrayOutputStream();
        BufferedImage largeResizedImage =
            ProductImageSizeUtils.resizeWithRatio(bufferedImage, largeImageWidth, largeImageHeight);


        File tempLarge =
            File.createTempFile(new StringBuilder().append(productImage.getProduct().getId())
                .append("tmpLarge").toString(), "." + extension);
        ImageIO.write(largeResizedImage, extension, tempLarge);

        try(FileInputStream isLarge = new FileInputStream(tempLarge)) {


        // IOUtils.copy(isLarge, output);


        ImageContentFile largeContentImage = new ImageContentFile();
								System.out.println("$#337#"); largeContentImage.setFileContentType(FileContentType.PRODUCT);
								System.out.println("$#338#"); largeContentImage.setFileName(productImage.getProductImage());
								System.out.println("$#339#"); largeContentImage.setFile(isLarge);


        // largeContentImage.setBufferedImage(bufferedImage);

        // largeContentImage.setFile(output);
        // largeContentImage.setDefaultImage(false);
        // largeContentImage.setImageName(new
        // StringBuilder().append("L-").append(productImage.getProductImage()).toString());


								System.out.println("$#340#"); uploadImage.addProductImage(productImage, largeContentImage);

        // output.flush();
        // output.close();

        tempLarge.delete();

        // now upload original



        /*
         * //resize small BufferedImage smallResizedImage = ProductImageSizeUtils.resize(cropped,
         * smallImageWidth, smallImageHeight); File tempSmall = File.createTempFile(new
         * StringBuilder().append(productImage.getProduct().getId()).append("tmpSmall").toString(),
         * "." + extension ); ImageIO.write(smallResizedImage, extension, tempSmall);
         *
         * //byte[] is = IOUtils.toByteArray(new FileInputStream(tempSmall));
         *
         * FileInputStream isSmall = new FileInputStream(tempSmall);
         *
         * output = new ByteArrayOutputStream(); IOUtils.copy(isSmall, output);
         *
         *
         * smallContentImage = new InputContentImage(ImageContentType.PRODUCT);
         * smallContentImage.setFile(output); smallContentImage.setDefaultImage(false);
         * smallContentImage.setImageName(new
         * StringBuilder().append("S-").append(productImage.getProductImage()).toString());
         *
         * uploadImage.uploadProductImage(configuration, productImage, smallContentImage);
         *
         * output.flush(); output.close();
         *
         * tempSmall.delete();
         */


    }
      } else {
        // small will be the same as the original
								System.out.println("$#341#"); contentImage.setFileContentType(FileContentType.PRODUCT);
								System.out.println("$#342#"); uploadImage.addProductImage(productImage, contentImage);
      }



    } catch (Exception e) {
      throw new ServiceException(e);
    } finally {
      try {
								System.out.println("$#343#"); productImage.getImage().close();
      } catch (Exception ignore) {
      }
    }

  }


  public OutputContentFile getProductImage(ProductImage productImage) throws ServiceException {
    // will return original
				System.out.println("$#344#"); return getImage.getProductImage(productImage);
  }


  @Override
  public List<OutputContentFile> getImages(final String merchantStoreCode,
      FileContentType imageContentType) throws ServiceException {
    // will return original
				System.out.println("$#345#"); return getImage.getImages(merchantStoreCode, FileContentType.PRODUCT);
  }

  @Override
  public List<OutputContentFile> getImages(Product product) throws ServiceException {
				System.out.println("$#346#"); return getImage.getImages(product);
  }



  @Override
  public void removeProductImage(ProductImage productImage) throws ServiceException {

				System.out.println("$#347#"); this.removeImage.removeProductImage(productImage);

    /*
     * ProductImage large = new ProductImage(); large.setProduct(productImage.getProduct());
     * large.setProductImage("L" + productImage.getProductImage());
     * 
     * this.removeImage.removeProductImage(large);
     * 
     * ProductImage small = new ProductImage(); small.setProduct(productImage.getProduct());
     * small.setProductImage("S" + productImage.getProductImage());
     * 
     * this.removeImage.removeProductImage(small);
     */

  }


  @Override
  public void removeProductImages(Product product) throws ServiceException {

				System.out.println("$#348#"); this.removeImage.removeProductImages(product);

  }


  @Override
  public void removeImages(final String merchantStoreCode) throws ServiceException {

				System.out.println("$#349#"); this.removeImage.removeImages(merchantStoreCode);

  }


  public ProductImagePut getUploadImage() {
				System.out.println("$#350#"); return uploadImage;
  }


  public void setUploadImage(ProductImagePut uploadImage) {
    this.uploadImage = uploadImage;
  }



  public ProductImageGet getGetImage() {
				System.out.println("$#351#"); return getImage;
  }


  public void setGetImage(ProductImageGet getImage) {
    this.getImage = getImage;
  }


  @Override
  public OutputContentFile getProductImage(String merchantStoreCode, String productCode,
      String imageName) throws ServiceException {
				System.out.println("$#352#"); return getImage.getProductImage(merchantStoreCode, productCode, imageName);
  }



  @Override
  public OutputContentFile getProductImage(String merchantStoreCode, String productCode,
      String imageName, ProductImageSize size) throws ServiceException {
				System.out.println("$#353#"); return getImage.getProductImage(merchantStoreCode, productCode, imageName, size);
  }



}
