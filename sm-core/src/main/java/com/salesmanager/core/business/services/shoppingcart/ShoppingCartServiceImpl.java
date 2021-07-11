package com.salesmanager.core.business.services.shoppingcart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.shoppingcart.ShoppingCartAttributeRepository;
import com.salesmanager.core.business.repositories.shoppingcart.ShoppingCartItemRepository;
import com.salesmanager.core.business.repositories.shoppingcart.ShoppingCartRepository;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductAttributeService;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.catalog.product.price.FinalPrice;
import com.salesmanager.core.model.common.UserContext;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.shipping.ShippingProduct;
import com.salesmanager.core.model.shoppingcart.ShoppingCart;
import com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;

@Service("shoppingCartService")
public class ShoppingCartServiceImpl extends SalesManagerEntityServiceImpl<Long, ShoppingCart>
		implements ShoppingCartService {

	private ShoppingCartRepository shoppingCartRepository;

	@Inject
	private ProductService productService;

	@Inject
	private ShoppingCartItemRepository shoppingCartItemRepository;

	@Inject
	private ShoppingCartAttributeRepository shoppingCartAttributeItemRepository;

	@Inject
	private PricingService pricingService;

	@Inject
	private ProductAttributeService productAttributeService;


	private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingCartServiceImpl.class);

	@Inject
	public ShoppingCartServiceImpl(ShoppingCartRepository shoppingCartRepository) {
		super(shoppingCartRepository);
		this.shoppingCartRepository = shoppingCartRepository;

	}

	/**
	 * Retrieve a {@link ShoppingCart} cart for a given customer
	 */
	@Override
	@Transactional
	public ShoppingCart getShoppingCart(final Customer customer) throws ServiceException {

		try {

			List<ShoppingCart> shoppingCarts = shoppingCartRepository.findByCustomer(customer.getId());
			
			//elect valid shopping cart
			List<ShoppingCart> validCart = shoppingCarts.stream()
					.filter((cart) -> cart.getOrderId()==null)
					.collect(Collectors.toList());
			
			ShoppingCart shoppingCart = null;
			
			System.out.println("$#3105#"); if(!org.apache.commons.collections.CollectionUtils.isEmpty(validCart)) {
				shoppingCart = validCart.get(0);
				getPopulatedShoppingCart(shoppingCart);
				System.out.println("$#3106#"); if (shoppingCart != null && shoppingCart.isObsolete()) {
					System.out.println("$#3108#"); delete(shoppingCart);
					shoppingCart = null;
				}
			}
			
			System.out.println("$#3109#"); return shoppingCart;

		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}

	/**
	 * Save or update a {@link ShoppingCart} for a given customer
	 */
	@Override
	public void saveOrUpdate(ShoppingCart shoppingCart) throws ServiceException {

		Validate.notNull(shoppingCart, "ShoppingCart must not be null");
		Validate.notNull(shoppingCart.getMerchantStore(), "ShoppingCart.merchantStore must not be null");


		try {
			UserContext userContext = UserContext.getCurrentInstance();
			System.out.println("$#3110#"); if(userContext!=null) {
				System.out.println("$#3111#"); shoppingCart.setIpAddress(userContext.getIpAddress());
			}
		} catch(Exception s) {
			LOGGER.error("Cannot add ip address to shopping cart ", s);
		}


		System.out.println("$#3112#"); if (shoppingCart.getId() == null || shoppingCart.getId().longValue() == 0) {
			System.out.println("$#3114#"); super.create(shoppingCart);
		} else {
			System.out.println("$#3115#"); super.update(shoppingCart);
		}



	}

	/**
	 * Get a {@link ShoppingCart} for a given id and MerchantStore. Will update
	 * the shopping cart prices and items based on the actual inventory. This
	 * method will remove the shopping cart if no items are attached.
	 */
	@Override
	@Transactional
	public ShoppingCart getById(final Long id, final MerchantStore store) throws ServiceException {

		try {
			ShoppingCart shoppingCart = shoppingCartRepository.findById(store.getId(), id);
			System.out.println("$#3116#"); if (shoppingCart == null) {
				return null;
			}
			getPopulatedShoppingCart(shoppingCart);

			System.out.println("$#3117#"); if (shoppingCart.isObsolete()) {
				System.out.println("$#3118#"); delete(shoppingCart);
				return null;
			} else {
				System.out.println("$#3119#"); return shoppingCart;
			}

		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}

	/**
	 * Get a {@link ShoppingCart} for a given id. Will update the shopping cart
	 * prices and items based on the actual inventory. This method will remove
	 * the shopping cart if no items are attached.
	 */
	@Override
	@Transactional
	public ShoppingCart getById(final Long id) {

		try {
			ShoppingCart shoppingCart = shoppingCartRepository.findOne(id);
			System.out.println("$#3120#"); if (shoppingCart == null) {
				return null;
			}
			getPopulatedShoppingCart(shoppingCart);

			System.out.println("$#3121#"); if (shoppingCart.isObsolete()) {
				System.out.println("$#3122#"); delete(shoppingCart);
				return null;
			} else {
				System.out.println("$#3123#"); return shoppingCart;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("$#3124#"); e.printStackTrace();
		}
		return null;

	}

	/**
	 * Get a {@link ShoppingCart} for a given code. Will update the shopping
	 * cart prices and items based on the actual inventory. This method will
	 * remove the shopping cart if no items are attached.
	 */
	@Override
	@Transactional
	public ShoppingCart getByCode(final String code, final MerchantStore store) throws ServiceException {

		try {
			ShoppingCart shoppingCart = shoppingCartRepository.findByCode(store.getId(), code);
			System.out.println("$#3125#"); if (shoppingCart == null) {
				return null;
			}
			getPopulatedShoppingCart(shoppingCart);

			System.out.println("$#3126#"); if (shoppingCart.isObsolete()) {
				System.out.println("$#3127#"); delete(shoppingCart);
				return null;
			} else {
				System.out.println("$#3128#"); return shoppingCart;
			}

		} catch (javax.persistence.NoResultException nre) {
			return null;
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		} catch (Exception ee) {
			throw new ServiceException(ee);
		} catch (Throwable t) {
			throw new ServiceException(t);
		}

	}

	@Override
	@Transactional
	public void deleteCart(final ShoppingCart shoppingCart) throws ServiceException {
		ShoppingCart cart = this.getById(shoppingCart.getId());
		System.out.println("$#3129#"); if (cart != null) {
			System.out.println("$#3130#"); super.delete(cart);
		}
	}

/*	@Override
	@Transactional
	public ShoppingCart getByCustomer(final Customer customer) throws ServiceException {

		try {
			List<ShoppingCart> shoppingCart = shoppingCartRepository.findByCustomer(customer.getId());
			if (shoppingCart == null) {
				return null;
			}
			return getPopulatedShoppingCart(shoppingCart);

		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}*/

	@Transactional(noRollbackFor = { org.springframework.dao.EmptyResultDataAccessException.class })
	private ShoppingCart getPopulatedShoppingCart(final ShoppingCart shoppingCart) throws Exception {

		try {

			boolean cartIsObsolete = false;
			System.out.println("$#3131#"); if (shoppingCart != null) {

				Set<ShoppingCartItem> items = shoppingCart.getLineItems();
				System.out.println("$#3132#"); if (items == null || items.size() == 0) {
					System.out.println("$#3134#"); shoppingCart.setObsolete(true);
					System.out.println("$#3135#"); return shoppingCart;

				}

				// Set<ShoppingCartItem> shoppingCartItems = new
				// HashSet<ShoppingCartItem>();
				for (ShoppingCartItem item : items) {
					LOGGER.debug("Populate item " + item.getId());
					System.out.println("$#3136#"); getPopulatedItem(item);
					LOGGER.debug("Obsolete item ? " + item.isObsolete());
					System.out.println("$#3137#"); if (item.isObsolete()) {
						cartIsObsolete = true;
					}
				}

				// shoppingCart.setLineItems(shoppingCartItems);
				Set<ShoppingCartItem> refreshedItems = new HashSet<ShoppingCartItem>();
				for (ShoppingCartItem item : items) {
					refreshedItems.add(item);
				}

				//if (refreshCart) {
					System.out.println("$#3138#"); shoppingCart.setLineItems(refreshedItems);
								System.out.println("$#3139#"); update(shoppingCart);
				//}

				System.out.println("$#3140#"); if (cartIsObsolete) {
					System.out.println("$#3141#"); shoppingCart.setObsolete(true);
				}
				System.out.println("$#3142#"); return shoppingCart;
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new ServiceException(e);
		}

		System.out.println("$#3143#"); return shoppingCart;

	}

	@Override
	public ShoppingCartItem populateShoppingCartItem(final Product product) throws ServiceException {
		Validate.notNull(product, "Product should not be null");
		Validate.notNull(product.getMerchantStore(), "Product.merchantStore should not be null");

		ShoppingCartItem item = new ShoppingCartItem(product);

		// set item price
		FinalPrice price = pricingService.calculateProductPrice(product);
		System.out.println("$#3144#"); item.setItemPrice(price.getFinalPrice());
		System.out.println("$#3145#"); return item;

	}

	@Transactional
	private void getPopulatedItem(final ShoppingCartItem item) throws Exception {

		Product product = null;

		Long productId = item.getProductId();
		product = productService.getById(productId);

		System.out.println("$#3146#"); if (product == null) {
			System.out.println("$#3147#"); item.setObsolete(true);
			return;
		}

		System.out.println("$#3148#"); item.setProduct(product);

		System.out.println("$#3149#"); if (product.isProductVirtual()) {
			System.out.println("$#3150#"); item.setProductVirtual(true);
		}

		Set<ShoppingCartAttributeItem> cartAttributes = item.getAttributes();
		Set<ProductAttribute> productAttributes = product.getAttributes();
		List<ProductAttribute> attributesList = new ArrayList<ProductAttribute>();//attributes maintained
		List<ShoppingCartAttributeItem> removeAttributesList = new ArrayList<ShoppingCartAttributeItem>();//attributes to remove
		//DELETE ORPHEANS MANUALLY
		System.out.println("$#3153#"); System.out.println("$#3151#"); if ( (productAttributes != null && productAttributes.size() > 0) || (cartAttributes != null && cartAttributes.size() > 0)) {
						System.out.println("$#3157#"); if(cartAttributes!=null) {
    			for (ShoppingCartAttributeItem attribute : cartAttributes) {
    				long attributeId = attribute.getProductAttributeId().longValue();
    				boolean existingAttribute = false;
    				for (ProductAttribute productAttribute : productAttributes) {

									System.out.println("$#3158#"); if (productAttribute.getId().longValue() == attributeId) {
										System.out.println("$#3159#"); attribute.setProductAttribute(productAttribute);
    						attributesList.add(productAttribute);
    						existingAttribute = true;
    						break;
    					}
    				}

								System.out.println("$#3160#"); if(!existingAttribute) {
    					removeAttributesList.add(attribute);
    				}

    			}
		    }
		}

		//cleanup orphean item
		System.out.println("$#3161#"); if(CollectionUtils.isNotEmpty(removeAttributesList)) {
			for(ShoppingCartAttributeItem attr : removeAttributesList) {
				System.out.println("$#3162#"); shoppingCartAttributeItemRepository.delete(attr);
			}
		}

		//cleanup detached attributes
		System.out.println("$#3163#"); if(CollectionUtils.isEmpty(attributesList)) {
			System.out.println("$#3164#"); item.setAttributes(null);
		}



		// set item price
		FinalPrice price = pricingService.calculateProductPrice(product, attributesList);
		System.out.println("$#3165#"); item.setItemPrice(price.getFinalPrice());
		System.out.println("$#3166#"); item.setFinalPrice(price);

		BigDecimal subTotal = item.getItemPrice().multiply(new BigDecimal(item.getQuantity().intValue()));
		System.out.println("$#3167#"); item.setSubTotal(subTotal);

	}

	@Override
	public List<ShippingProduct> createShippingProduct(final ShoppingCart cart) throws ServiceException {
		/**
		 * Determines if products are virtual
		 */
		Set<ShoppingCartItem> items = cart.getLineItems();
		List<ShippingProduct> shippingProducts = null;
		for (ShoppingCartItem item : items) {
			Product product = item.getProduct();
			System.out.println("$#3168#"); if (!product.isProductVirtual() && product.isProductShipeable()) {
				System.out.println("$#3170#"); if (shippingProducts == null) {
					shippingProducts = new ArrayList<ShippingProduct>();
				}
				ShippingProduct shippingProduct = new ShippingProduct(product);
				System.out.println("$#3171#"); shippingProduct.setQuantity(item.getQuantity());
				System.out.println("$#3172#"); shippingProduct.setFinalPrice(item.getFinalPrice());
				shippingProducts.add(shippingProduct);
			}
		}

		System.out.println("$#3173#"); return shippingProducts;

	}

/*	@Override
	public boolean isFreeShoppingCart(final ShoppingCart cart) throws ServiceException {
		*//**
		 * Determines if products are free
		 *//*
		Set<ShoppingCartItem> items = cart.getLineItems();
		for (ShoppingCartItem item : items) {
			Product product = item.getProduct();
			FinalPrice finalPrice = pricingService.calculateProductPrice(product);
			if (finalPrice.getFinalPrice().longValue() > 0) {
				return false;
			}
		}

		return true;

	}*/

/*	@Override
	public boolean requiresShipping(final ShoppingCart cart) throws ServiceException {

		Validate.notNull(cart, "Shopping cart cannot be null");
		Validate.notNull(cart.getLineItems(), "ShoppingCart items cannot be null");
		boolean requiresShipping = false;
		for (ShoppingCartItem item : cart.getLineItems()) {
			Product product = item.getProduct();
			if (product.isProductShipeable()) {
				requiresShipping = true;
				break;
			}
		}

		return requiresShipping;

	}*/

	@Override
	public void removeShoppingCart(final ShoppingCart cart) throws ServiceException {
		System.out.println("$#3174#"); shoppingCartRepository.delete(cart);
	}

	@Override
	public ShoppingCart mergeShoppingCarts(final ShoppingCart userShoppingModel, final ShoppingCart sessionCart,
			final MerchantStore store) throws Exception {
		System.out.println("$#3175#"); if (sessionCart.getCustomerId() != null && sessionCart.getCustomerId() == userShoppingModel.getCustomerId()) {
			LOGGER.info("Session Shopping cart belongs to same logged in user");
			System.out.println("$#3177#"); if (CollectionUtils.isNotEmpty(userShoppingModel.getLineItems())
					&& CollectionUtils.isNotEmpty(sessionCart.getLineItems())) {
				System.out.println("$#3179#"); return userShoppingModel;
			}
		}

		LOGGER.info("Starting merging shopping carts");
		System.out.println("$#3180#"); if (CollectionUtils.isNotEmpty(sessionCart.getLineItems())) {
			Set<ShoppingCartItem> shoppingCartItemsSet = getShoppingCartItems(sessionCart, store, userShoppingModel);
			boolean duplicateFound = false;
			System.out.println("$#3181#"); if (CollectionUtils.isNotEmpty(shoppingCartItemsSet)) {
				for (ShoppingCartItem sessionShoppingCartItem : shoppingCartItemsSet) {
					System.out.println("$#3182#"); if (CollectionUtils.isNotEmpty(userShoppingModel.getLineItems())) {
						for (ShoppingCartItem cartItem : userShoppingModel.getLineItems()) {
							if (cartItem.getProduct().getId().longValue() == sessionShoppingCartItem.getProduct()
									.getId().longValue()) {
								System.out.println("$#3184#"); if (CollectionUtils.isNotEmpty(cartItem.getAttributes())) {
									System.out.println("$#3185#"); if (!duplicateFound) {
										LOGGER.info("Dupliate item found..updating exisitng product quantity");
										System.out.println("$#3186#"); System.out.println("$#3187#"); cartItem.setQuantity(
												cartItem.getQuantity() + sessionShoppingCartItem.getQuantity());
										duplicateFound = true;
										break;
									}
								}
							}
						}
					}
					System.out.println("$#3188#"); if (!duplicateFound) {
						LOGGER.info("New item found..adding item to Shopping cart");
						userShoppingModel.getLineItems().add(sessionShoppingCartItem);
					}
				}

			}

		}
		LOGGER.info("Shopping Cart merged successfully.....");
		System.out.println("$#3189#"); saveOrUpdate(userShoppingModel);
		System.out.println("$#3190#"); removeShoppingCart(sessionCart);

		System.out.println("$#3191#"); return userShoppingModel;
	}

	private Set<ShoppingCartItem> getShoppingCartItems(final ShoppingCart sessionCart, final MerchantStore store,
			final ShoppingCart cartModel) throws Exception {

		Set<ShoppingCartItem> shoppingCartItemsSet = null;
		System.out.println("$#3192#"); if (CollectionUtils.isNotEmpty(sessionCart.getLineItems())) {
			shoppingCartItemsSet = new HashSet<ShoppingCartItem>();
			for (ShoppingCartItem shoppingCartItem : sessionCart.getLineItems()) {
				Product product = productService.getById(shoppingCartItem.getProductId());
				System.out.println("$#3193#"); if (product == null) {
					throw new Exception("Item with id " + shoppingCartItem.getProductId() + " does not exist");
				}

				System.out.println("$#3194#"); if (product.getMerchantStore().getId().intValue() != store.getId().intValue()) {
					throw new Exception("Item with id " + shoppingCartItem.getProductId()
							+ " does not belong to merchant " + store.getId());
				}

				ShoppingCartItem item = populateShoppingCartItem(product);
				System.out.println("$#3195#"); item.setQuantity(shoppingCartItem.getQuantity());
				System.out.println("$#3196#"); item.setShoppingCart(cartModel);

				List<ShoppingCartAttributeItem> cartAttributes = new ArrayList<ShoppingCartAttributeItem>(
						shoppingCartItem.getAttributes());
				System.out.println("$#3197#"); if (CollectionUtils.isNotEmpty(cartAttributes)) {
					for (ShoppingCartAttributeItem shoppingCartAttributeItem : cartAttributes) {
						ProductAttribute productAttribute = productAttributeService
								.getById(shoppingCartAttributeItem.getId());
						if (productAttribute != null
								&& productAttribute.getProduct().getId().longValue() == product.getId().longValue()) {

							ShoppingCartAttributeItem attributeItem = new ShoppingCartAttributeItem(item,
									productAttribute);
							System.out.println("$#3201#"); System.out.println("$#3200#"); if (shoppingCartAttributeItem.getId() > 0) {
								System.out.println("$#3202#"); attributeItem.setId(shoppingCartAttributeItem.getId());
							}
							System.out.println("$#3203#"); item.addAttributes(attributeItem);

						}
					}
				}

				shoppingCartItemsSet.add(item);
			}

		}
		System.out.println("$#3204#"); return shoppingCartItemsSet;
	}

/*	@Override
	public boolean isFreeShoppingCart(List<ShoppingCartItem> items) throws ServiceException {
		ShoppingCart cart = new ShoppingCart();
		Set<ShoppingCartItem> cartItems = new HashSet<ShoppingCartItem>(items);
		cart.setLineItems(cartItems);
		return this.isFreeShoppingCart(cart);
	}*/

	@Override
	@Transactional
	public void deleteShoppingCartItem(Long id) {


		ShoppingCartItem item = shoppingCartItemRepository.findOne(id);
		System.out.println("$#3205#"); if(item != null) {


			System.out.println("$#3206#"); if(item.getAttributes() != null) {
				System.out.println("$#3207#"); item.getAttributes().stream().forEach(a -> {shoppingCartAttributeItemRepository.deleteById(a.getId());});
				System.out.println("$#3209#"); item.getAttributes().clear();
			}


			//refresh
			item = shoppingCartItemRepository.findOne(id);

			//delete
			System.out.println("$#3210#"); shoppingCartItemRepository.deleteById(id);


		}


	}

}
