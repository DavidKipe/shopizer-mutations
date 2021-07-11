package com.salesmanager.shop.store.api.v1.order;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.helper.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.services.shoppingcart.ShoppingCartService;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.order.OrderCriteria;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shoppingcart.ShoppingCart;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.customer.PersistableCustomer;
import com.salesmanager.shop.model.customer.ReadableCustomer;
import com.salesmanager.shop.model.order.v0.ReadableOrder;
import com.salesmanager.shop.model.order.v0.ReadableOrderList;
import com.salesmanager.shop.model.order.v1.PersistableAnonymousOrder;
import com.salesmanager.shop.model.order.v1.PersistableOrder;
import com.salesmanager.shop.populator.customer.ReadableCustomerPopulator;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.controller.customer.facade.CustomerFacade;
import com.salesmanager.shop.store.controller.order.facade.OrderFacade;
import com.salesmanager.shop.utils.AuthorizationUtils;
import com.salesmanager.shop.utils.LocaleUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/v1")
@Api(tags = { "Order management resource (Order Management Api)" })
@SwaggerDefinition(tags = { @Tag(name = "Order management resource", description = "Manage orders") })
public class OrderApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderApi.class);

	@Inject
	private CustomerService customerService;

	@Inject
	private OrderFacade orderFacade;

	@Inject
	private ShoppingCartService shoppingCartService;

	@Autowired
	private CustomerFacade customerFacade;

	@Inject
	private AuthorizationUtils authorizationUtils;
	
	private static final String DEFAULT_ORDER_LIST_COUNT = "25";

	/**
	 * Get a list of orders for a given customer accept request parameter
	 * 'start' start index for count accept request parameter 'max' maximum
	 * number count, otherwise returns all Used for administrators
	 *
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = { "/private/orders/customers/{id}" }, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "string", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "string", defaultValue = "en") })
	public ReadableOrderList list(@PathVariable final Long id,
			@RequestParam(value = "start", required = false) Integer start,
			@RequestParam(value = "count", required = false) Integer count, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language, HttpServletResponse response) throws Exception {

		Customer customer = customerService.getById(id);

		System.out.println("$#11687#"); if (customer == null) {
			LOGGER.error("Customer is null for id " + id);
			System.out.println("$#11688#"); response.sendError(404, "Customer is null for id " + id);
			return null;
		}

		System.out.println("$#11689#"); if (start == null) {
			start = new Integer(0);
		}
		System.out.println("$#11690#"); if (count == null) {
			count = new Integer(100);
		}

		ReadableCustomer readableCustomer = new ReadableCustomer();
		ReadableCustomerPopulator customerPopulator = new ReadableCustomerPopulator();
		customerPopulator.populate(customer, readableCustomer, merchantStore, language);

		ReadableOrderList returnList = orderFacade.getReadableOrderList(merchantStore, customer, start, count,
				language);

		List<ReadableOrder> orders = returnList.getOrders();

		System.out.println("$#11691#"); if (!CollectionUtils.isEmpty(orders)) {
			for (ReadableOrder order : orders) {
				System.out.println("$#11692#"); order.setCustomer(readableCustomer);
			}
		}

		System.out.println("$#11693#"); return returnList;
	}

	/**
	 * List orders for authenticated customers
	 *
	 * @param start
	 * @param count
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = { "/auth/orders" }, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "string", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "string", defaultValue = "en") })
	public ReadableOrderList list(@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "count", required = false) Integer count, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language, HttpServletRequest request, HttpServletResponse response) throws Exception {

		Principal principal = request.getUserPrincipal();
		String userName = principal.getName();

		System.out.println("$#11694#"); System.out.println("who os the username ? " + userName);

		Customer customer = customerService.getByNick(userName);

		System.out.println("$#11695#"); if (customer == null) {
			System.out.println("$#11696#"); response.sendError(401, "Error while listing orders, customer not authorized");
			return null;
		}

		System.out.println("$#11697#"); if (page == null) {
			page = new Integer(0);
		}
		System.out.println("$#11698#"); if (count == null) {
			count = new Integer(100);
		}

		ReadableCustomer readableCustomer = new ReadableCustomer();
		ReadableCustomerPopulator customerPopulator = new ReadableCustomerPopulator();
		customerPopulator.populate(customer, readableCustomer, merchantStore, language);

		ReadableOrderList returnList = orderFacade.getReadableOrderList(merchantStore, customer, page, count, language);

		System.out.println("$#11699#"); if (returnList == null) {
			returnList = new ReadableOrderList();
		}

		List<ReadableOrder> orders = returnList.getOrders();
		System.out.println("$#11700#"); if (!CollectionUtils.isEmpty(orders)) {
			for (ReadableOrder order : orders) {
				System.out.println("$#11701#"); order.setCustomer(readableCustomer);
			}
		}
		System.out.println("$#11702#"); return returnList;
	}

	/**
	 * This method returns list of all the orders for a store.This is not
	 * bound to any specific stores and will get list of all the orders
	 * available for this instance
	 *
	 * @param start
	 * @param count
	 * @return List of orders
	 * @throws Exception
	 */
	@RequestMapping(value = { "/private/orders" }, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ReadableOrderList list(
			@RequestParam(value = "count", required = false, defaultValue = DEFAULT_ORDER_LIST_COUNT) Integer count,
			@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "id", required = false) Long id,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "phone", required = false) String phone,
			@RequestParam(value = "email", required = false) String email,
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {
		
		
		//long startTime = System.nanoTime();


		OrderCriteria orderCriteria = new OrderCriteria();
		System.out.println("$#11703#"); orderCriteria.setPageSize(count);
		System.out.println("$#11704#"); orderCriteria.setStartPage(page);

		System.out.println("$#11705#"); orderCriteria.setCustomerName(name);
		System.out.println("$#11706#"); orderCriteria.setCustomerPhone(phone);
		System.out.println("$#11707#"); orderCriteria.setStatus(status);
		System.out.println("$#11708#"); orderCriteria.setEmail(email);
		System.out.println("$#11709#"); orderCriteria.setId(id);


		String user = authorizationUtils.authenticatedUser();
		System.out.println("$#11710#"); authorizationUtils.authorizeUser(user, Stream.of(Constants.GROUP_SUPERADMIN, Constants.GROUP_ADMIN,
				Constants.GROUP_ADMIN_ORDER, Constants.GROUP_ADMIN_RETAIL).collect(Collectors.toList()), merchantStore);

		ReadableOrderList orders = orderFacade.getReadableOrderList(orderCriteria, merchantStore);
		
		/**
		long endTime = System.nanoTime();
		
		long timeElapsed = endTime - startTime;

		System.out.println("Execution time in milliseconds : " +
								timeElapsed / 1000000);
								**/
		
		System.out.println("$#11711#"); return orders;

	}

	/**
	 * Order details
	 * @param id
	 * @param merchantStore
	 * @param language
	 * @return
	 */
	@RequestMapping(value = { "/private/orders/{id}" }, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "string", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "string", defaultValue = "en") })
	public ReadableOrder get(
			@PathVariable final Long id, 
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {
		
		String user = authorizationUtils.authenticatedUser();
		System.out.println("$#11712#"); authorizationUtils.authorizeUser(user, Stream.of(Constants.GROUP_SUPERADMIN, Constants.GROUP_ADMIN,
				Constants.GROUP_ADMIN_ORDER, Constants.GROUP_ADMIN_RETAIL).collect(Collectors.toList()), merchantStore);


		ReadableOrder order = orderFacade.getReadableOrder(id, merchantStore, language);

		System.out.println("$#11713#"); return order;
	}

	/**
	 * Get a given order by id
	 *
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = { "/auth/orders/{id}" }, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "string", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "string", defaultValue = "en") })
	public ReadableOrder getOrder(@PathVariable final Long id, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Principal principal = request.getUserPrincipal();
		String userName = principal.getName();

		Customer customer = customerService.getByNick(userName);

		System.out.println("$#11714#"); if (customer == null) {
			System.out.println("$#11715#"); response.sendError(401, "Error while performing checkout customer not authorized");
			return null;
		}

		ReadableOrder order = orderFacade.getReadableOrder(id, merchantStore, language);

		System.out.println("$#11716#"); if (order == null) {
			LOGGER.error("Order is null for id " + id);
			System.out.println("$#11717#"); response.sendError(404, "Order is null for id " + id);
			return null;
		}

		System.out.println("$#11718#"); if (order.getCustomer() == null) {
			LOGGER.error("Order is null for customer " + principal);
			System.out.println("$#11719#"); response.sendError(404, "Order is null for customer " + principal);
			return null;
		}

		System.out.println("$#11720#"); if (order.getCustomer().getId() != null
				&& order.getCustomer().getId().longValue() != customer.getId().longValue()) {
			LOGGER.error("Order is null for customer " + principal);
			System.out.println("$#11722#"); response.sendError(404, "Order is null for customer " + principal);
			return null;
		}

		System.out.println("$#11723#"); return order;
	}

	/**
	 * Action for performing a checkout on a given shopping cart
	 *
	 * @param id
	 * @param order
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = { "/auth/cart/{code}/checkout" }, method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "string", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "string", defaultValue = "en") })
	public PersistableOrder checkout(@PathVariable final String code, @Valid @RequestBody PersistableOrder order,
			@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language, HttpServletRequest request,
			HttpServletResponse response, Locale locale) throws Exception {

		try {
			Principal principal = request.getUserPrincipal();
			String userName = principal.getName();

			Customer customer = customerService.getByNick(userName);

			System.out.println("$#11724#"); if (customer == null) {
				System.out.println("$#11725#"); response.sendError(401, "Error while performing checkout customer not authorized");
				return null;
			}

			ShoppingCart cart = shoppingCartService.getByCode(code, merchantStore);
			System.out.println("$#11726#"); if (cart == null) {
				throw new ResourceNotFoundException("Cart code " + code + " does not exist");
			}

			System.out.println("$#11727#"); order.setShoppingCartId(cart.getId());
			System.out.println("$#11728#"); order.setCustomerId(customer.getId());

			Order modelOrder = orderFacade.processOrder(order, customer, merchantStore, language, locale);
			Long orderId = modelOrder.getId();
			System.out.println("$#11729#"); order.setId(orderId);

			// hash payment token
			System.out.println("$#11730#"); order.getPayment().setPaymentToken("***");

			System.out.println("$#11731#"); return order;

		} catch (Exception e) {
			LOGGER.error("Error while processing checkout", e);
			try {
				System.out.println("$#11732#"); response.sendError(503, "Error while processing checkout " + e.getMessage());
			} catch (Exception ignore) {
			}
			return null;
		}
	}

	@RequestMapping(value = { "/cart/{code}/checkout" }, method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "string", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "string", defaultValue = "en") })
	public PersistableOrder checkout(
			@PathVariable final String code,
			@Valid @RequestBody PersistableAnonymousOrder order, 
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		System.out.println("$#11733#"); Validate.notNull(order.getCustomer(), "Customer must not be null");

		ShoppingCart cart;
		try {
			cart = shoppingCartService.getByCode(code, merchantStore);

			System.out.println("$#11734#"); if (cart == null) {
				throw new ResourceNotFoundException("Cart code " + code + " does not exist");
			}

			Customer customer = new Customer();
			customer = customerFacade.populateCustomerModel(customer, order.getCustomer(), merchantStore, language);

			System.out.println("$#11735#"); order.setShoppingCartId(cart.getId());

			Order modelOrder = orderFacade.processOrder(order, customer, merchantStore, language,
					LocaleUtils.getLocale(language));
			Long orderId = modelOrder.getId();
			System.out.println("$#11736#"); order.setId(orderId);
			// set customer id
			System.out.println("$#11737#"); order.getCustomer().setId(modelOrder.getCustomerId());

			// hash payment token
			System.out.println("$#11738#"); order.getPayment().setPaymentToken("***");
			System.out.println("$#11739#"); return order;

		} catch (Exception e) {
			throw new ServiceRuntimeException("Error during checkout " + e.getMessage(), e);
		}

	}
	
	@RequestMapping(value = { "/private/orders/{id}/customer" }, method = RequestMethod.PATCH)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiImplicitParams({ 
			@ApiImplicitParam(name = "store", dataType = "string", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "string", defaultValue = "en") })
	public void updateOrderCustomer(
			@PathVariable final Long id,
			@Valid @RequestBody PersistableCustomer orderCustomer, 
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {
		
		String user = authorizationUtils.authenticatedUser();
		System.out.println("$#11740#"); authorizationUtils.authorizeUser(user, Stream.of(Constants.GROUP_SUPERADMIN, Constants.GROUP_ADMIN,
				Constants.GROUP_ADMIN_ORDER, Constants.GROUP_ADMIN_RETAIL).collect(Collectors.toList()), merchantStore);

		
		System.out.println("$#11741#"); orderFacade.updateOrderCustomre(id, orderCustomer, merchantStore);
		return;
		
	}
}
