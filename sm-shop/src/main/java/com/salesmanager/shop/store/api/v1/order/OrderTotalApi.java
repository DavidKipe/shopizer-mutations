package com.salesmanager.shop.store.api.v1.order;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.services.order.OrderService;
import com.salesmanager.core.business.services.shipping.ShippingQuoteService;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.OrderSummary;
import com.salesmanager.core.model.order.OrderTotalSummary;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shipping.ShippingSummary;
import com.salesmanager.core.model.shoppingcart.ShoppingCart;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.shop.model.order.ReadableOrderTotalSummary;
import com.salesmanager.shop.populator.order.ReadableOrderSummaryPopulator;
import com.salesmanager.shop.store.controller.shoppingCart.facade.ShoppingCartFacade;
import com.salesmanager.shop.utils.LabelUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@RequestMapping("/api/v1")
public class OrderTotalApi {

  @Inject private ShoppingCartFacade shoppingCartFacade;

  @Inject private LabelUtils messages;

  @Inject private PricingService pricingService;

  @Inject private CustomerService customerService;

  @Inject private ShippingQuoteService shippingQuoteService;

  @Inject private OrderService orderService;

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderTotalApi.class);

  /**
   * This service calculates order total for a given shopping cart This method takes in
   * consideration any applicable sales tax An optional request parameter accepts a quote id that
   * was received using shipping api
   *
   * @param quote
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @RequestMapping(
      value = {"/auth/cart/{id}/total"},
      method = RequestMethod.GET)
  @ResponseBody
  @ApiImplicitParams({
      @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
      @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en")
  })
  public ReadableOrderTotalSummary payment(
      @PathVariable final Long id,
      @RequestParam(value = "quote", required = false) Long quote,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language,
      HttpServletRequest request,
      HttpServletResponse response) {

    try {
      Principal principal = request.getUserPrincipal();
      String userName = principal.getName();

      Customer customer = customerService.getByNick(userName);

						System.out.println("$#11806#"); if (customer == null) {
								System.out.println("$#11807#"); response.sendError(503, "Error while getting user details to calculate shipping quote");
      }

      ShoppingCart shoppingCart = shoppingCartFacade.getShoppingCartModel(id, merchantStore);

						System.out.println("$#11808#"); if (shoppingCart == null) {
								System.out.println("$#11809#"); response.sendError(404, "Cart id " + id + " does not exist");
        return null;
      }

						System.out.println("$#11810#"); if (shoppingCart.getCustomerId() == null) {
								System.out.println("$#11811#"); response.sendError(
            404, "Cart id " + id + " does not exist for exist for user " + userName);
        return null;
      }

						System.out.println("$#11812#"); if (shoppingCart.getCustomerId().longValue() != customer.getId().longValue()) {
								System.out.println("$#11813#"); response.sendError(
            404, "Cart id " + id + " does not exist for exist for user " + userName);
        return null;
      }

      ShippingSummary shippingSummary = null;

      // get shipping quote if asked for
						System.out.println("$#11814#"); if (quote != null) {
        shippingSummary = shippingQuoteService.getShippingSummary(quote, merchantStore);
      }

      OrderTotalSummary orderTotalSummary = null;

      OrderSummary orderSummary = new OrderSummary();
						System.out.println("$#11815#"); orderSummary.setShippingSummary(shippingSummary);
      List<ShoppingCartItem> itemsSet =
          new ArrayList<ShoppingCartItem>(shoppingCart.getLineItems());
						System.out.println("$#11816#"); orderSummary.setProducts(itemsSet);

      orderTotalSummary =
          orderService.caculateOrderTotal(orderSummary, customer, merchantStore, language);

      ReadableOrderTotalSummary returnSummary = new ReadableOrderTotalSummary();
      ReadableOrderSummaryPopulator populator = new ReadableOrderSummaryPopulator();
						System.out.println("$#11817#"); populator.setMessages(messages);
						System.out.println("$#11818#"); populator.setPricingService(pricingService);
      populator.populate(orderTotalSummary, returnSummary, merchantStore, language);

						System.out.println("$#11819#"); return returnSummary;

    } catch (Exception e) {
      LOGGER.error("Error while calculating order summary", e);
      try {
								System.out.println("$#11820#"); response.sendError(503, "Error while calculating order summary " + e.getMessage());
      } catch (Exception ignore) {
      }
      return null;
    }
  }

  /**
   * Public api
   * @param id
   * @param quote
   * @param merchantStore
   * @param language
   * @param response
   * @return
   */
  @RequestMapping(
      value = {"/cart/{id}/total"},
      method = RequestMethod.GET)
  @ResponseBody
  @ApiImplicitParams({
      @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
      @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en")
  })
  public ReadableOrderTotalSummary calculatePayment(
      @PathVariable final Long id,
      @RequestParam(value = "quote", required = false) Long quote,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language,
      HttpServletResponse response) {

    try {
      ShoppingCart shoppingCart = shoppingCartFacade.getShoppingCartModel(id, merchantStore);

						System.out.println("$#11821#"); if (shoppingCart == null) {

								System.out.println("$#11822#"); response.sendError(404, "Cart code " + id + " does not exist");

        return null;
      }

      ShippingSummary shippingSummary = null;

      // get shipping quote if asked for
						System.out.println("$#11823#"); if (quote != null) {
        shippingSummary = shippingQuoteService.getShippingSummary(quote, merchantStore);
      }

      OrderTotalSummary orderTotalSummary = null;

      OrderSummary orderSummary = new OrderSummary();
						System.out.println("$#11824#"); orderSummary.setShippingSummary(shippingSummary);
      List<ShoppingCartItem> itemsSet =
          new ArrayList<ShoppingCartItem>(shoppingCart.getLineItems());
						System.out.println("$#11825#"); orderSummary.setProducts(itemsSet);

      orderTotalSummary = orderService.caculateOrderTotal(orderSummary, merchantStore, language);

      ReadableOrderTotalSummary returnSummary = new ReadableOrderTotalSummary();
      ReadableOrderSummaryPopulator populator = new ReadableOrderSummaryPopulator();
						System.out.println("$#11826#"); populator.setMessages(messages);
						System.out.println("$#11827#"); populator.setPricingService(pricingService);
      populator.populate(orderTotalSummary, returnSummary, merchantStore, language);

						System.out.println("$#11828#"); return returnSummary;

    } catch (Exception e) {
      LOGGER.error("Error while calculating order summary", e);
      try {
								System.out.println("$#11829#"); response.sendError(503, "Error while calculating order summary " + e.getMessage());
      } catch (Exception ignore) {
      }
      return null;
    }
  }
}
