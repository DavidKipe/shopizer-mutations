/**
 * 
 */
package com.salesmanager.shop.store.controller.shoppingCart;

import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.shoppingcart.ShoppingCartData;
import com.salesmanager.shop.store.controller.AbstractController;
import com.salesmanager.shop.store.controller.shoppingCart.facade.ShoppingCartFacade;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Umesh A
 *
 */
@Controller
@RequestMapping("/shop/cart")
public class MiniCartController extends AbstractController{

	private static final Logger LOG = LoggerFactory.getLogger(MiniCartController.class);
	
	@Inject
	private ShoppingCartFacade shoppingCartFacade;
	
	

	
	@RequestMapping(value={"/displayMiniCartByCode"},  method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ShoppingCartData displayMiniCart(final String shoppingCartCode, HttpServletRequest request, Model model){
		
		Language language = (Language)request.getAttribute(Constants.LANGUAGE);
		
		try {
			MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
		    Customer customer = getSessionAttribute(  Constants.CUSTOMER, request );
			ShoppingCartData cart =  shoppingCartFacade.getShoppingCartData(customer,merchantStore,shoppingCartCode, language);
			System.out.println("$#13987#"); if(cart!=null) {
				System.out.println("$#13988#"); request.getSession().setAttribute(Constants.SHOPPING_CART, cart.getCode());
			}
			else {
				System.out.println("$#13989#"); request.getSession().removeAttribute(Constants.SHOPPING_CART);//make sure there is no cart here
				cart = new ShoppingCartData();//create an empty cart
			}
			System.out.println("$#13990#"); return cart;
			
			
		} catch(Exception e) {
			LOG.error("Error while getting the shopping cart",e);
		}
		
		return null;

	}

	
	@RequestMapping(value={"/removeMiniShoppingCartItem"},   method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ShoppingCartData removeShoppingCartItem(Long lineItemId, final String shoppingCartCode, HttpServletRequest request, Model model) throws Exception {
		Language language = (Language)request.getAttribute(Constants.LANGUAGE);
		MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
		ShoppingCartData cart =  shoppingCartFacade.getShoppingCartData(null, merchantStore, shoppingCartCode, language);
		
		System.out.println("$#13991#"); if(cart==null) {
			return null;
		}
		
		ShoppingCartData shoppingCartData=shoppingCartFacade.removeCartItem(lineItemId, cart.getCode(), merchantStore,language);
		
					System.out.println("$#13992#"); if(shoppingCartData==null) {
            return null;
        }
		
		System.out.println("$#13993#"); if(CollectionUtils.isEmpty(shoppingCartData.getShoppingCartItems())) {
			System.out.println("$#13994#"); shoppingCartFacade.deleteShoppingCart(shoppingCartData.getId(), merchantStore);
			System.out.println("$#13995#"); request.getSession().removeAttribute(Constants.SHOPPING_CART);
			return null;
		}
		
		System.out.println("$#13996#"); request.getSession().setAttribute(Constants.SHOPPING_CART, cart.getCode());
		
		LOG.debug("removed item" + lineItemId + "from cart");
		System.out.println("$#13997#"); return shoppingCartData;
	}
	
	
}
