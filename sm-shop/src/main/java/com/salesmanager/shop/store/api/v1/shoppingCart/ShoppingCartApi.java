package com.salesmanager.shop.store.api.v1.shoppingCart;

import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.shoppingcart.PersistableShoppingCartItem;
import com.salesmanager.shop.model.shoppingcart.ReadableShoppingCart;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.controller.shoppingCart.facade.ShoppingCartFacade;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.util.Arrays;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

@Controller
@RequestMapping("/api/v1")
public class ShoppingCartApi {

  @Inject private ShoppingCartFacade shoppingCartFacade;

  @Inject private CustomerService customerService;

  private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingCartApi.class);

  @ResponseStatus(HttpStatus.CREATED)
  @RequestMapping(value = "/cart", method = RequestMethod.POST)
  @ApiOperation(
      httpMethod = "POST",
      value = "Add product to shopping cart when no cart exists, this will create a new cart id",
      notes =
          "No customer ID in scope. Add to cart for non authenticated users, as simple as {\"product\":1232,\"quantity\":1}",
      produces = "application/json",
      response = ReadableShoppingCart.class)
  @ApiImplicitParams({
      @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
      @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en")
  })
  public @ResponseBody ReadableShoppingCart addToCart(
      @Valid @RequestBody PersistableShoppingCartItem shoppingCartItem,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language,
      HttpServletResponse response) {

    try {
      ReadableShoppingCart cart =
          shoppingCartFacade.addToCart(shoppingCartItem, merchantStore, language);

						System.out.println("$#12059#"); return cart;

    } catch (Exception e) {
      try {
						System.out.println("$#12060#"); if(e instanceof ResourceNotFoundException) {
        //response.sendError(204, "Error while adding product to cart id [" + shoppingCartItems.getProduct() + "] not found or not available");
      }
      LOGGER.error("Error while adding product to cart", e);

								System.out.println("$#12061#"); response.sendError(503, "Error while adding product to cart " + e.getMessage());
      } catch (Exception ignore) {
      }

      return null;
    }
  }

  @RequestMapping(value = "/cart/{code}", method = RequestMethod.PUT)
  @ApiOperation(
          httpMethod = "PUT",
          value = "Add to an existing shopping cart or modify an item quantity",
          notes =
                  "No customer ID in scope. Modify cart for non authenticated users, as simple as {\"product\":1232,\"quantity\":0} for instance will remove item 1234 from cart",
          produces = "application/json",
          response = ReadableShoppingCart.class)
  @ApiImplicitParams({
          @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
          @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en")
  })
  public ResponseEntity<ReadableShoppingCart> modifyCart(
          @PathVariable String code,
          @Valid @RequestBody PersistableShoppingCartItem shoppingCartItem,
          @ApiIgnore MerchantStore merchantStore,
          @ApiIgnore Language language,
          HttpServletResponse response) {

    try {
      ReadableShoppingCart cart =
              shoppingCartFacade.modifyCart(code, shoppingCartItem, merchantStore, language);

						System.out.println("$#12062#"); if(cart == null) {
								System.out.println("$#12063#"); return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }

						System.out.println("$#12064#"); return new ResponseEntity<>(cart, HttpStatus.CREATED);

    } catch (IllegalArgumentException e) {
      LOGGER.error("Cart or item not found " + code + " : " + shoppingCartItem.getProduct(), e);
						System.out.println("$#12065#"); return new ResponseEntity("Cart or Item not found " + code + " : " + shoppingCartItem.getProduct(), HttpStatus.NOT_FOUND);

    } catch (Exception ignore) {
						System.out.println("$#12066#"); return new ResponseEntity("Error while modifying cart " + code + " " + ignore.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping(value = "/cart/{code}/multi", consumes = {"application/json"}, produces = {"application/json"})
  @ApiOperation(
          httpMethod = "POST",
          value = "Add to an existing shopping cart or modify an item quantity",
          notes =
                  "No customer ID in scope. Modify cart for non authenticated users, as simple as {\"product\":1232,\"quantity\":0} for instance will remove item 1234 from cart",
          produces = "application/json",
          response = ReadableShoppingCart.class)
  @ApiImplicitParams({
          @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
          @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en")
  })
  public ResponseEntity<ReadableShoppingCart> modifyCart(
          @PathVariable String code,
          @Valid @RequestBody PersistableShoppingCartItem[] shoppingCartItems,
          @ApiIgnore MerchantStore merchantStore,
          @ApiIgnore Language language) {

    try {
      ReadableShoppingCart cart =
              shoppingCartFacade.modifyCartMulti(code, Arrays.asList(shoppingCartItems), merchantStore, language);

						System.out.println("$#12067#"); return new ResponseEntity<>(cart, HttpStatus.CREATED);

    } catch (IllegalArgumentException e) {
      LOGGER.error("Cart or item not found " + code + " : " + shoppingCartItems, e);
						System.out.println("$#12068#"); return new ResponseEntity("Cart or Item not found " + code + " : " + shoppingCartItems, HttpStatus.NOT_FOUND);

    } catch (Exception ignore) {
						System.out.println("$#12069#"); return new ResponseEntity("Error while modifying cart " + code + " " + ignore.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @ResponseStatus(HttpStatus.OK)
  @RequestMapping(value = "/cart/{code}", method = RequestMethod.GET)
  @ApiOperation(
      httpMethod = "GET",
      value = "Get a chopping cart by code",
      notes = "",
      produces = "application/json",
      response = ReadableShoppingCart.class)
  @ApiImplicitParams({
      @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
      @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en")
  })
  public @ResponseBody ReadableShoppingCart getByCode(
      @PathVariable String code,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language,
      HttpServletResponse response) {

    try {
      ReadableShoppingCart cart = shoppingCartFacade.getByCode(code, merchantStore, language);

						System.out.println("$#12070#"); if (cart == null) {
								System.out.println("$#12071#"); response.sendError(404, "No ShoppingCart found for customer code : " + code);
        return null;
      }

						System.out.println("$#12072#"); return cart;

    } catch (Exception e) {
      LOGGER.error("Error while getting cart", e);
      try {
								System.out.println("$#12073#"); response.sendError(503, "Error while getting cart " + e.getMessage());
      } catch (Exception ignore) {
      }

      return null;
    }
  }

  @ResponseStatus(HttpStatus.CREATED)
  @RequestMapping(value = "/customers/{id}/cart", method = RequestMethod.POST)
  @ApiOperation(
      httpMethod = "POST",
      value = "Add product to a specific customer shopping cart",
      notes = "",
      produces = "application/json",
      response = ReadableShoppingCart.class)
  @ApiImplicitParams({
      @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
      @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en")
  })
  public @ResponseBody ReadableShoppingCart addToCart(
      @PathVariable Long id,
      @Valid @RequestBody PersistableShoppingCartItem shoppingCartItem,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language,
      HttpServletResponse response) {

    try {
      // lookup customer
      Customer customer = customerService.getById(id);

						System.out.println("$#12074#"); if (customer == null) {
								System.out.println("$#12075#"); response.sendError(404, "No Customer found for ID : " + id);
        return null;
      }

      ReadableShoppingCart cart =
          shoppingCartFacade.addToCart(customer, shoppingCartItem, merchantStore, language);

						System.out.println("$#12076#"); return cart;

    } catch (Exception e) {
      LOGGER.error("Error while adding product to cart", e);
      try {
								System.out.println("$#12077#"); response.sendError(503, "Error while adding product to cart " + e.getMessage());
      } catch (Exception ignore) {
      }

      return null;
    }
  }

  @ResponseStatus(HttpStatus.OK)
  @RequestMapping(value = "/customers/{id}/cart", method = RequestMethod.GET)
  @ApiOperation(
      httpMethod = "GET",
      value = "Get a chopping cart by id",
      notes = "",
      produces = "application/json",
      response = ReadableShoppingCart.class)
  @ApiImplicitParams({
      @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
      @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en")
  })
  public @ResponseBody ReadableShoppingCart get(
      @PathVariable Long id,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language,
      HttpServletResponse response) {

    try {
      // lookup customer
      Customer customer = customerService.getById(id);

						System.out.println("$#12078#"); if (customer == null) {
								System.out.println("$#12079#"); response.sendError(404, "No Customer found for ID : " + id);
        return null;
      }

      ReadableShoppingCart cart = shoppingCartFacade.getCart(customer, merchantStore, language);

						System.out.println("$#12080#"); if (cart == null) {
								System.out.println("$#12081#"); response.sendError(404, "No ShoppingCart found for customer ID : " + id);
        return null;
      }

						System.out.println("$#12082#"); return cart;

    } catch (Exception e) {
      LOGGER.error("Error while getting cart", e);
      try {
								System.out.println("$#12083#"); response.sendError(503, "Error while getting cart " + e.getMessage());
      } catch (Exception ignore) {
      }

      return null;
    }
  }

  @DeleteMapping(
      value = "/cart/{code}/product/{id}",
      produces = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE})
  @ApiOperation(
      httpMethod = "DELETE",
      value = "Remove a product from a specific cart",
      notes = "If body set to true returns remaining cart in body, empty cart gives empty body. If body set to false no body ",
      produces = "application/json",
      response = ReadableShoppingCart.class)
  @ApiImplicitParams({
    @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
    @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en"),
    @ApiImplicitParam(name = "body", dataType = "boolean", defaultValue = "false"),
  })
  public ResponseEntity<ReadableShoppingCart> deleteCartItem(
      @PathVariable("code") String cartCode,
      @PathVariable("id") Long itemId,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language,
      @RequestParam(defaultValue = "false") boolean body) throws Exception{

      ReadableShoppingCart updatedCart =  shoppingCartFacade.removeShoppingCartItem(cartCode, itemId, merchantStore, language, body);
						System.out.println("$#12084#"); if(body) {
								System.out.println("$#12085#"); return new ResponseEntity<>(updatedCart, HttpStatus.OK);
      }
						System.out.println("$#12086#"); return new ResponseEntity<>(updatedCart, HttpStatus.NO_CONTENT);
  }
}
