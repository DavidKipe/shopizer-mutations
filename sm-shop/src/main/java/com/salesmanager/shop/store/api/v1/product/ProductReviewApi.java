package com.salesmanager.shop.store.api.v1.product;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.review.ProductReviewService;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.review.ProductReview;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.catalog.product.PersistableProductReview;
import com.salesmanager.shop.model.catalog.product.ReadableProductReview;
import com.salesmanager.shop.store.controller.product.facade.ProductFacade;
import com.salesmanager.shop.store.controller.store.facade.StoreFacade;
import com.salesmanager.shop.utils.LanguageUtils;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@RequestMapping("/api/v1")
public class ProductReviewApi {

  @Inject private ProductFacade productFacade;

  @Inject private StoreFacade storeFacade;

  @Inject private LanguageUtils languageUtils;

  @Inject private ProductService productService;

  @Inject private ProductReviewService productReviewService;

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductReviewApi.class);

  @RequestMapping(
      value = {
        "/private/products/{id}/reviews",
        "/auth/products/{id}/reviews",
        "/auth/products/{id}/reviews",
        "/auth/products/{id}/reviews"
      },
      method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  @ApiImplicitParams({
      @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
      @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en")
  })
  public PersistableProductReview create(
      @PathVariable final Long id,
      @Valid @RequestBody PersistableProductReview review,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language,
      HttpServletRequest request,
      HttpServletResponse response) {

    try {
      // rating already exist
      ProductReview prodReview =
          productReviewService.getByProductAndCustomer(
              review.getProductId(), review.getCustomerId());
						System.out.println("$#11974#"); if (prodReview != null) {
								System.out.println("$#11975#"); response.sendError(500, "A review already exist for this customer and product");
        return null;
      }

      // rating maximum 5
						System.out.println("$#11977#"); System.out.println("$#11976#"); if (review.getRating() > Constants.MAX_REVIEW_RATING_SCORE) {
								System.out.println("$#11978#"); response.sendError(503, "Maximum rating score is " + Constants.MAX_REVIEW_RATING_SCORE);
        return null;
      }

						System.out.println("$#11979#"); review.setProductId(id);

						System.out.println("$#11980#"); productFacade.saveOrUpdateReview(review, merchantStore, language);

						System.out.println("$#11981#"); return review;

    } catch (Exception e) {
      LOGGER.error("Error while saving product review", e);
      try {
								System.out.println("$#11982#"); response.sendError(503, "Error while saving product review" + e.getMessage());
      } catch (Exception ignore) {
      }

      return null;
    }
  }

  @RequestMapping(value = "/products/{id}/reviews", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  @ApiImplicitParams({
      @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
      @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en")
  })
  public List<ReadableProductReview> getAll(
      @PathVariable final Long id,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language,
      HttpServletResponse response) {

    try {
      // product exist
      Product product = productService.getById(id);

						System.out.println("$#11983#"); if (product == null) {
								System.out.println("$#11984#"); response.sendError(404, "Product id " + id + " does not exists");
								System.out.println("$#11985#"); return null;
      }

      List<ReadableProductReview> reviews =
          productFacade.getProductReviews(product, merchantStore, language);

						System.out.println("$#11986#"); return reviews;

    } catch (Exception e) {
      LOGGER.error("Error while getting product reviews", e);
      try {
								System.out.println("$#11987#"); response.sendError(503, "Error while getting product reviews" + e.getMessage());
      } catch (Exception ignore) {
      }

						System.out.println("$#11988#"); return null;
    }
  }

  @RequestMapping(
      value = {
        "/private/products/{id}/reviews/{reviewid}",
        "/auth/products/{id}/reviews/{reviewid}"
      },
      method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  @ApiImplicitParams({
      @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
      @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en")
  })
  public PersistableProductReview update(
      @PathVariable final Long id,
      @PathVariable final Long reviewId,
      @Valid @RequestBody PersistableProductReview review,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language,
      HttpServletRequest request,
      HttpServletResponse response) {

    try {
      ProductReview prodReview = productReviewService.getById(reviewId);
						System.out.println("$#11989#"); if (prodReview == null) {
								System.out.println("$#11990#"); response.sendError(404, "Product review with id " + reviewId + " does not exist");
        return null;
      }

						System.out.println("$#11991#"); if (prodReview.getCustomer().getId().longValue() != review.getCustomerId().longValue()) {
								System.out.println("$#11992#"); response.sendError(404, "Product review with id " + reviewId + " does not exist");
        return null;
      }

      // rating maximum 5
						System.out.println("$#11994#"); System.out.println("$#11993#"); if (review.getRating() > Constants.MAX_REVIEW_RATING_SCORE) {
								System.out.println("$#11995#"); response.sendError(503, "Maximum rating score is " + Constants.MAX_REVIEW_RATING_SCORE);
        return null;
      }

						System.out.println("$#11996#"); review.setProductId(id);

						System.out.println("$#11997#"); productFacade.saveOrUpdateReview(review, merchantStore, language);

						System.out.println("$#11998#"); return review;

    } catch (Exception e) {
      LOGGER.error("Error while saving product review", e);
      try {
								System.out.println("$#11999#"); response.sendError(503, "Error while saving product review" + e.getMessage());
      } catch (Exception ignore) {
      }

      return null;
    }
  }

  @RequestMapping(
      value = {
        "/private/products/{id}/reviews/{reviewid}",
        "/auth/products/{id}/reviews/{reviewid}"
      },
      method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  @ApiImplicitParams({
      @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
      @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en")
  })
  public void delete(
      @PathVariable final Long id,
      @PathVariable final Long reviewId,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language,
      HttpServletResponse response) {

    try {
      ProductReview prodReview = productReviewService.getById(reviewId);
						System.out.println("$#12000#"); if (prodReview == null) {
								System.out.println("$#12001#"); response.sendError(404, "Product review with id " + reviewId + " does not exist");
        return;
      }

						System.out.println("$#12002#"); if (prodReview.getProduct().getId().longValue() != id.longValue()) {
								System.out.println("$#12003#"); response.sendError(404, "Product review with id " + reviewId + " does not exist");
        return;
      }

						System.out.println("$#12004#"); productFacade.deleteReview(prodReview, merchantStore, language);

    } catch (Exception e) {
      LOGGER.error("Error while deleting product review", e);
      try {
								System.out.println("$#12005#"); response.sendError(503, "Error while deleting product review" + e.getMessage());
      } catch (Exception ignore) {
      }

      return;
    }
  }
}
