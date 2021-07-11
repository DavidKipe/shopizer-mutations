package com.salesmanager.shop.store.api.v1.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.image.ProductImageService;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.image.ProductImage;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.api.exception.UnauthorizedException;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@RequestMapping("/api/v1")
public class ProductImageApi {

  @Inject private ProductImageService productImageService;


  @Inject private ProductService productService;

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductImageApi.class);

  /**
   * To be used with MultipartFile
   *
   * @param id
   * @param uploadfiles
   * @param request
   * @param response
   * @throws Exception
   */
  @ResponseStatus(HttpStatus.CREATED)
  @RequestMapping(
      value = {"/private/products/{id}/images", "/auth/products/{id}/images"},
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
      method = RequestMethod.POST)
  @ApiImplicitParams({
    @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
    @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en")
  })
  public void uploadImages(
      @PathVariable Long id,
      @RequestParam(value="file",required = true) MultipartFile[] files,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language) {

    try {

      // get the product
      Product product = productService.getById(id);
						System.out.println("$#11918#"); if (product == null) {
        throw new ResourceNotFoundException("Product not found");
      }
      
      //security validation
      //product belongs to merchant store
						System.out.println("$#11919#"); if(product.getMerchantStore().getId().intValue() != merchantStore.getId().intValue()) {
        throw new UnauthorizedException("Resource not authorized for this merchant");
      }

      boolean hasDefaultImage = false;
      Set<ProductImage> images = product.getImages();
						System.out.println("$#11920#"); if (!CollectionUtils.isEmpty(images)) {
        for (ProductImage image : images) {
										System.out.println("$#11921#"); if (image.isDefaultImage()) {
            hasDefaultImage = true;
            break;
          }
        }
      }

      List<ProductImage> contentImagesList = new ArrayList<ProductImage>();

      for (MultipartFile multipartFile : files) {
								System.out.println("$#11922#"); if (!multipartFile.isEmpty()) {
          ProductImage productImage = new ProductImage();
										System.out.println("$#11923#"); productImage.setImage(multipartFile.getInputStream());
										System.out.println("$#11924#"); productImage.setProductImage(multipartFile.getOriginalFilename());
										System.out.println("$#11925#"); productImage.setProduct(product);

										System.out.println("$#11926#"); if (!hasDefaultImage) {
												System.out.println("$#11927#"); productImage.setDefaultImage(true);
            hasDefaultImage = true;
          }

          contentImagesList.add(productImage);
        }
      }

						System.out.println("$#11928#"); if (CollectionUtils.isNotEmpty(contentImagesList)) {
								System.out.println("$#11929#"); productImageService.addProductImages(product, contentImagesList);
      }

    } catch (Exception e) {
      LOGGER.error("Error while creating ProductImage", e);
      throw new ServiceRuntimeException("Error while creating image");
    }
  }


  @ResponseStatus(HttpStatus.OK)
  @RequestMapping(
      value = {"/private/products/images/{id}", "/auth/products/images/{id}"},
      method = RequestMethod.DELETE)
  public void deleteImage(
      @PathVariable Long id, HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    try {
      ProductImage productImage = productImageService.getById(id);

						System.out.println("$#11930#"); if (productImage != null) {
								System.out.println("$#11931#"); productImageService.delete(productImage);
      } else {
								System.out.println("$#11932#"); response.sendError(404, "No ProductImage found for ID : " + id);
      }

    } catch (Exception e) {
      LOGGER.error("Error while deleting ProductImage", e);
      try {
								System.out.println("$#11933#"); response.sendError(503, "Error while deleting ProductImage " + e.getMessage());
      } catch (Exception ignore) {
      }
    }
  }
}
