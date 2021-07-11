/**
 *
 */
package com.salesmanager.shop.store.controller.shoppingCart.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.NoResultException;

import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductAttributeService;
import com.salesmanager.core.business.services.shoppingcart.ShoppingCartCalculationService;
import com.salesmanager.core.business.services.shoppingcart.ShoppingCartService;
import com.salesmanager.core.business.utils.ProductPriceUtils;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.catalog.product.availability.ProductAvailability;
import com.salesmanager.core.model.catalog.product.price.FinalPrice;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shoppingcart.ShoppingCart;
import com.salesmanager.shop.model.shoppingcart.CartModificationException;
import com.salesmanager.shop.model.shoppingcart.PersistableShoppingCartItem;
import com.salesmanager.shop.model.shoppingcart.ReadableShoppingCart;
import com.salesmanager.shop.model.shoppingcart.ShoppingCartAttribute;
import com.salesmanager.shop.model.shoppingcart.ShoppingCartData;
import com.salesmanager.shop.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.shop.populator.shoppingCart.ReadableShoppingCartPopulator;
import com.salesmanager.shop.populator.shoppingCart.ShoppingCartDataPopulator;
import com.salesmanager.shop.utils.DateUtil;
import com.salesmanager.shop.utils.ImageFilePath;

/**
 * @author Umesh Awasthi
 * @version 1.0
 * @since 1.0
 */
@Service( value = "shoppingCartFacade" )
public class ShoppingCartFacadeImpl
    implements ShoppingCartFacade
{


    private static final Logger LOG = LoggerFactory.getLogger(ShoppingCartFacadeImpl.class);

    @Inject
    private ShoppingCartService shoppingCartService;

    @Inject
    ShoppingCartCalculationService shoppingCartCalculationService;

    @Inject
    private ProductPriceUtils productPriceUtils;

    @Inject
    private ProductService productService;

    @Inject
    private PricingService pricingService;

    @Inject
    private ProductAttributeService productAttributeService;

	@Inject
	@Qualifier("img")
	private ImageFilePath imageUtils;

    public void deleteShoppingCart(final Long id, final MerchantStore store) throws Exception {
    	ShoppingCart cart = shoppingCartService.getById(id, store);
					System.out.println("$#13737#"); if(cart!=null) {
						System.out.println("$#13738#"); shoppingCartService.deleteCart(cart);
    	}
    }

    @Override
    public void deleteShoppingCart(final String code, final MerchantStore store) throws Exception {
    	ShoppingCart cart = shoppingCartService.getByCode(code, store);
					System.out.println("$#13739#"); if(cart!=null) {
						System.out.println("$#13740#"); shoppingCartService.deleteCart(cart);
    	}
    }

    @Override
    public ShoppingCartData addItemsToShoppingCart( final ShoppingCartData shoppingCartData,
                                                    final ShoppingCartItem item, final MerchantStore store, final Language language,final Customer customer )
        throws Exception
    {

        ShoppingCart cartModel = null;

        /**
         * Sometimes a user logs in and a shopping cart is present in db (shoppingCartData
         * but ui has no cookie with shopping cart code so the cart code will have
         * to be added to the item in order to process add to cart normally
         */
								System.out.println("$#13741#"); if(shoppingCartData != null && StringUtils.isBlank(item.getCode())) {
									System.out.println("$#13743#"); item.setCode(shoppingCartData.getCode());
        }


								System.out.println("$#13744#"); if ( !StringUtils.isBlank( item.getCode() ) )
        {
            // get it from the db
            cartModel = getShoppingCartModel( item.getCode(), store );
												System.out.println("$#13745#"); if ( cartModel == null )
            {
                cartModel = createCartModel( shoppingCartData.getCode(), store,customer );
            }

        }

								System.out.println("$#13746#"); if ( cartModel == null )
        {

            final String shoppingCartCode =
                StringUtils.isNotBlank( shoppingCartData.getCode() ) ? shoppingCartData.getCode() : null;
            cartModel = createCartModel( shoppingCartCode, store,customer );

        }
        com.salesmanager.core.model.shoppingcart.ShoppingCartItem shoppingCartItem =
            createCartItem( cartModel, item, store );


        boolean duplicateFound = false;
								System.out.println("$#13748#"); if(CollectionUtils.isEmpty(item.getShoppingCartAttributes())) {//increment quantity
        	//get duplicate item from the cart
        	Set<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> cartModelItems = cartModel.getLineItems();
        	for(com.salesmanager.core.model.shoppingcart.ShoppingCartItem cartItem : cartModelItems) {
										System.out.println("$#13749#"); if(cartItem.getProduct().getId().longValue()==shoppingCartItem.getProduct().getId().longValue()) {
											System.out.println("$#13750#"); if(CollectionUtils.isEmpty(cartItem.getAttributes())) {
												System.out.println("$#13751#"); if(!duplicateFound) {
													System.out.println("$#13752#"); if(!shoppingCartItem.isProductVirtual()) {
														System.out.println("$#13754#"); System.out.println("$#13753#"); cartItem.setQuantity(cartItem.getQuantity() + shoppingCartItem.getQuantity());
        					}
        					duplicateFound = true;
        					break;
        				}
        			}
        		}
        	}
        }

								System.out.println("$#13755#"); if(!duplicateFound) {
        	//shoppingCartItem.getAttributes().stream().forEach(a -> {a.setProductAttributeId(productAttributeId);});
        	cartModel.getLineItems().add( shoppingCartItem );
        }

        /** Update cart in database with line items **/
								System.out.println("$#13756#"); shoppingCartService.saveOrUpdate( cartModel );

        //refresh cart
        cartModel = shoppingCartService.getById(cartModel.getId(), store);

        shoppingCartCalculationService.calculate( cartModel, store, language );

        ShoppingCartDataPopulator shoppingCartDataPopulator = new ShoppingCartDataPopulator();
								System.out.println("$#13757#"); shoppingCartDataPopulator.setShoppingCartCalculationService( shoppingCartCalculationService );
								System.out.println("$#13758#"); shoppingCartDataPopulator.setPricingService( pricingService );
								System.out.println("$#13759#"); shoppingCartDataPopulator.setimageUtils(imageUtils);


								System.out.println("$#13760#"); return shoppingCartDataPopulator.populate( cartModel, store, language );
    }

    private com.salesmanager.core.model.shoppingcart.ShoppingCartItem createCartItem( final ShoppingCart cartModel,
                                                                                               final ShoppingCartItem shoppingCartItem,
                                                                                               final MerchantStore store )
        throws Exception
    {

        Product product = productService.getById( shoppingCartItem.getProductId() );

								System.out.println("$#13761#"); if ( product == null )
        {
            throw new Exception( "Item with id " + shoppingCartItem.getProductId() + " does not exist" );
        }

								System.out.println("$#13762#"); if ( product.getMerchantStore().getId().intValue() != store.getId().intValue() )
        {
            throw new Exception( "Item with id " + shoppingCartItem.getProductId() + " does not belong to merchant "
                + store.getId() );
        }

		/**
		 * Check if product quantity is 0
		 * Check if product is available
		 * Check if date available <= now
		 */

        Set<ProductAvailability> availabilities = product.getAvailabilities();
								System.out.println("$#13763#"); if(availabilities == null) {

        	throw new Exception( "Item with id " + product.getId() + " is not properly configured" );

        }

        for(ProductAvailability availability : availabilities) {
									System.out.println("$#13764#"); if(availability.getProductQuantity() == null || availability.getProductQuantity().intValue() ==0) {
                throw new Exception( "Item with id " + product.getId() + " is not available");
        	}
        }

								System.out.println("$#13766#"); if(!product.isAvailable()) {
        	throw new Exception( "Item with id " + product.getId() + " is not available");
        }

								System.out.println("$#13767#"); if(!DateUtil.dateBeforeEqualsDate(product.getDateAvailable(), new Date())) {
        	throw new Exception( "Item with id " + product.getId() + " is not available");
        }


        com.salesmanager.core.model.shoppingcart.ShoppingCartItem item =
            shoppingCartService.populateShoppingCartItem( product );

								System.out.println("$#13768#"); item.setQuantity( shoppingCartItem.getQuantity() );
								System.out.println("$#13769#"); item.setShoppingCart( cartModel );

        // attributes
        List<ShoppingCartAttribute> cartAttributes = shoppingCartItem.getShoppingCartAttributes();
								System.out.println("$#13770#"); if ( !CollectionUtils.isEmpty( cartAttributes ) )
        {
            for ( ShoppingCartAttribute attribute : cartAttributes )
            {
                ProductAttribute productAttribute = productAttributeService.getById( attribute.getAttributeId() );
                if ( productAttribute != null
                    && productAttribute.getProduct().getId().longValue() == product.getId().longValue() )
                {
                    com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem attributeItem =
                        new com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem( item,
                                                                                                         productAttribute );

																				System.out.println("$#13773#"); item.addAttributes( attributeItem );
                }
            }
        }
								System.out.println("$#13774#"); return item;

    }


    //used for api
	private com.salesmanager.core.model.shoppingcart.ShoppingCartItem createCartItem(ShoppingCart cartModel,
			 PersistableShoppingCartItem shoppingCartItem, MerchantStore store) throws Exception {

		Product product = productService.getById(shoppingCartItem.getProduct());

		System.out.println("$#13775#"); if (product == null) {
						System.out.println("$#13776#"); System.out.println("----------------------- Item with id " + shoppingCartItem.getProduct() + " does not exist");
			throw new ResourceNotFoundException("Item with id " + shoppingCartItem.getProduct() + " does not exist");
		}

		System.out.println("$#13777#"); if (product.getMerchantStore().getId().intValue() != store.getId().intValue()) {
			throw new ResourceNotFoundException("Item with id " + shoppingCartItem.getProduct() + " does not belong to merchant "
					+ store.getId());
		}

		/**
		 * Check if product quantity is 0
		 * Check if product is available
		 * Check if date available <= now
		 */

        Set<ProductAvailability> availabilities = product.getAvailabilities();
								System.out.println("$#13778#"); if(availabilities == null) {

        	throw new Exception( "Item with id " + product.getId() + " is not properly configured" );

        }

        for(ProductAvailability availability : availabilities) {
									System.out.println("$#13779#"); if(availability.getProductQuantity() == null || availability.getProductQuantity().intValue() ==0) {
                throw new Exception( "Item with id " + product.getId() + " is not available");
        	}
        }

								System.out.println("$#13781#"); if(!product.isAvailable()) {
        	throw new Exception( "Item with id " + product.getId() + " is not available");
        }

								System.out.println("$#13782#"); if(!DateUtil.dateBeforeEqualsDate(product.getDateAvailable(), new Date())) {
        	throw new Exception( "Item with id " + product.getId() + " is not available");
        }


		com.salesmanager.core.model.shoppingcart.ShoppingCartItem item = shoppingCartService
				.populateShoppingCartItem(product);

		System.out.println("$#13783#"); item.setQuantity(shoppingCartItem.getQuantity());
		System.out.println("$#13784#"); item.setShoppingCart(cartModel);

		//set attributes
		List<com.salesmanager.shop.model.catalog.product.attribute.ProductAttribute> attributes = shoppingCartItem.getAttributes();
		System.out.println("$#13785#"); if (!CollectionUtils.isEmpty(attributes)) {
			for(com.salesmanager.shop.model.catalog.product.attribute.ProductAttribute attribute : attributes) {

				ProductAttribute productAttribute = productAttributeService.getById(attribute.getId());

				if (productAttribute != null
						&& productAttribute.getProduct().getId().longValue() == product.getId().longValue()) {

					com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem attributeItem = new com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem(
							item, productAttribute);

					System.out.println("$#13788#"); item.addAttributes(attributeItem);
				}
			}
		}

		System.out.println("$#13789#"); return item;

	}

    //used for api
    private List<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> createCartItems(ShoppingCart cartModel,
                                                                                     List<PersistableShoppingCartItem> shoppingCartItems,
                                                                                           MerchantStore store) throws Exception {

        List<Long> productIds = shoppingCartItems.stream().map(s -> Long.valueOf(s.getProduct())).collect(Collectors.toList());

        List<Product> products = productService.getProductsByIds(productIds);

								System.out.println("$#13791#"); if (products == null || products.size() != shoppingCartItems.size()) {
            LOG.warn("----------------------- Items with in id-list " + productIds + " does not exist");
            throw new ResourceNotFoundException("Item with id " + productIds + " does not exist");
        }

        List<Product> wrongStoreProducts = products.stream().filter(p -> p.getMerchantStore().getId() != store.getId()).collect(Collectors.toList());
								System.out.println("$#13796#"); System.out.println("$#13795#"); if (wrongStoreProducts.size() > 0) {
            throw new ResourceNotFoundException("One or more of the items with id's " + wrongStoreProducts.stream().map(s -> Long.valueOf(s.getId())).collect(Collectors.toList()) + " does not belong to merchant "
                    + store.getId());
        }

        List<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> items = new ArrayList<>();

        for (Product p: products) {
            com.salesmanager.core.model.shoppingcart.ShoppingCartItem item = shoppingCartService.populateShoppingCartItem(p);
            Optional<PersistableShoppingCartItem> oShoppingCartItem = shoppingCartItems.stream().filter(i -> i.getProduct() == p.getId()).findFirst();
												System.out.println("$#13800#"); if(!oShoppingCartItem.isPresent()) {
                // Should never happen if not something is updated in realtime or user has item in local storage and add it long time after to cart!
                LOG.warn("Missing shoppingCartItem for product " + p.getSku() + " ( " + p.getId() + " )");
                continue;
            }
            PersistableShoppingCartItem shoppingCartItem = oShoppingCartItem.get();
												System.out.println("$#13801#"); item.setQuantity(shoppingCartItem.getQuantity());
												System.out.println("$#13802#"); item.setShoppingCart(cartModel);

            /**
             * Check if product is available
             * Check if product quantity is 0
             * Check if date available <= now
             */
												System.out.println("$#13803#"); if(!p.isAvailable()) {
                throw new Exception( "Item with id " + p.getId() + " is not available");
            }

            Set<ProductAvailability> availabilities = p.getAvailabilities();
												System.out.println("$#13804#"); if(availabilities == null) {
                throw new Exception( "Item with id " + p.getId() + " is not properly configured" );
            }

            for(ProductAvailability availability : availabilities) {
																System.out.println("$#13805#"); if(availability.getProductQuantity() == null || availability.getProductQuantity().intValue() ==0) {
                    throw new Exception( "Item with id " + p.getId() + " is not available");
                }
            }

												System.out.println("$#13807#"); if(!DateUtil.dateBeforeEqualsDate(p.getDateAvailable(), new Date())) {
                throw new Exception( "Item with id " + p.getId() + " is not available");
            }
            // end qty & availablility checks

            //set attributes
            List<com.salesmanager.shop.model.catalog.product.attribute.ProductAttribute> attributes = shoppingCartItem.getAttributes();
												System.out.println("$#13808#"); if (!CollectionUtils.isEmpty(attributes)) {
                for(com.salesmanager.shop.model.catalog.product.attribute.ProductAttribute attribute : attributes) {

                    ProductAttribute productAttribute = productAttributeService.getById(attribute.getId());

                    if (productAttribute != null
                            && productAttribute.getProduct().getId().longValue() == p.getId().longValue()) {

                        com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem attributeItem = new com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem(
                                item, productAttribute);

																								System.out.println("$#13811#"); item.addAttributes(attributeItem);
                    }
                }
            }
            items.add(item);
        }

								System.out.println("$#13812#"); return items;
    }


    @Override
    public ShoppingCart createCartModel( final String shoppingCartCode, final MerchantStore store,final Customer customer )
        throws Exception
    {
        final Long CustomerId = customer != null ? customer.getId() : null;
        ShoppingCart cartModel = new ShoppingCart();
								System.out.println("$#13814#"); if ( StringUtils.isNotBlank( shoppingCartCode ) )
        {
												System.out.println("$#13815#"); cartModel.setShoppingCartCode( shoppingCartCode );
        }
        else
        {
												System.out.println("$#13816#"); cartModel.setShoppingCartCode( uniqueShoppingCartCode() );
        }

								System.out.println("$#13817#"); cartModel.setMerchantStore( store );
								System.out.println("$#13818#"); if ( CustomerId != null )
        {
												System.out.println("$#13819#"); cartModel.setCustomerId( CustomerId );
        }
								System.out.println("$#13820#"); shoppingCartService.create( cartModel );
								System.out.println("$#13821#"); return cartModel;
    }





    private com.salesmanager.core.model.shoppingcart.ShoppingCartItem getEntryToUpdate( final long entryId,
                                                                                                 final ShoppingCart cartModel )
    {
								System.out.println("$#13822#"); if ( CollectionUtils.isNotEmpty( cartModel.getLineItems() ) )
        {
            for ( com.salesmanager.core.model.shoppingcart.ShoppingCartItem shoppingCartItem : cartModel.getLineItems() )
            {
																System.out.println("$#13823#"); if ( shoppingCartItem.getId().longValue() == entryId )
                {
                    LOG.info( "Found line item  for given entry id: " + entryId );
																				System.out.println("$#13824#"); return shoppingCartItem;

                }
            }
        }
        LOG.info( "Unable to find any entry for given Id: " + entryId );
        return null;
    }

    private Object getKeyValue( final String key )
    {
        ServletRequestAttributes reqAttr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
								System.out.println("$#13825#"); return reqAttr.getRequest().getAttribute( key );
    }

    @Override
    public ShoppingCartData getShoppingCartData(final Customer customer, final MerchantStore store,
                                                 final String shoppingCartId, Language language)
        throws Exception
    {

        ShoppingCart cart = null;
        try
        {
												System.out.println("$#13826#"); if ( customer != null )
            {
                LOG.info( "Reteriving customer shopping cart..." );
                cart = shoppingCartService.getShoppingCart( customer );

            }

            else
            {
																System.out.println("$#13827#"); if ( StringUtils.isNotBlank( shoppingCartId ) && cart == null )
                {
                    cart = shoppingCartService.getByCode( shoppingCartId, store );
                }

            }

        }
        catch ( ServiceException ex )
        {
            LOG.error( "Error while retriving cart from customer", ex );
        }
        catch( NoResultException nre) {
        	//nothing
        }

								System.out.println("$#13829#"); if ( cart == null )
        {
            return null;
        }

        //if cart has been completed return null
								System.out.println("$#13831#"); System.out.println("$#13830#"); if(cart.getOrderId() != null && cart.getOrderId().longValue() > 0) {
												System.out.println("$#13833#"); if ( StringUtils.isNotBlank( shoppingCartId ) && !(shoppingCartId.equals(cart.getShoppingCartCode())))
            {
                cart = shoppingCartService.getByCode( shoppingCartId, store );
            } else {
            	return null;
            }
        }

        LOG.info( "Cart model found." );

        ShoppingCartDataPopulator shoppingCartDataPopulator = new ShoppingCartDataPopulator();
								System.out.println("$#13835#"); shoppingCartDataPopulator.setShoppingCartCalculationService( shoppingCartCalculationService );
								System.out.println("$#13836#"); shoppingCartDataPopulator.setPricingService( pricingService );
								System.out.println("$#13837#"); shoppingCartDataPopulator.setimageUtils(imageUtils);

        //Language language = (Language) getKeyValue( Constants.LANGUAGE );
        MerchantStore merchantStore = (MerchantStore) getKeyValue( Constants.MERCHANT_STORE );

        ShoppingCartData shoppingCartData = shoppingCartDataPopulator.populate( cart, merchantStore, language );

/*        List<ShoppingCartItem> unavailables = new ArrayList<ShoppingCartItem>();
        List<ShoppingCartItem> availables = new ArrayList<ShoppingCartItem>();
        //Take out items no more available
        List<ShoppingCartItem> items = shoppingCartData.getShoppingCartItems();
        for(ShoppingCartItem item : items) {
        	String code = item.getProductCode();
        	Product p =productService.getByCode(code, language);
        	if(!p.isAvailable()) {
        		unavailables.add(item);
        	} else {
        		availables.add(item);
        	}

        }
        shoppingCartData.setShoppingCartItems(availables);
        shoppingCartData.setUnavailables(unavailables);*/

								System.out.println("$#13838#"); return shoppingCartData;

    }

    //@Override
    public ShoppingCartData getShoppingCartData( ShoppingCart shoppingCartModel, Language language)
        throws Exception
    {

    	Validate.notNull(shoppingCartModel, "Shopping Cart cannot be null");


        ShoppingCartDataPopulator shoppingCartDataPopulator = new ShoppingCartDataPopulator();
								System.out.println("$#13839#"); shoppingCartDataPopulator.setShoppingCartCalculationService( shoppingCartCalculationService );
								System.out.println("$#13840#"); shoppingCartDataPopulator.setPricingService( pricingService );
								System.out.println("$#13841#"); shoppingCartDataPopulator.setimageUtils(imageUtils);
        //Language language = (Language) getKeyValue( Constants.LANGUAGE );
        MerchantStore merchantStore = (MerchantStore) getKeyValue( Constants.MERCHANT_STORE );
								System.out.println("$#13842#"); return shoppingCartDataPopulator.populate( shoppingCartModel, merchantStore, language );
    }

	@Override
    public ShoppingCartData removeCartItem( final Long itemID, final String cartId ,final MerchantStore store,final Language language )
        throws Exception
    {
								System.out.println("$#13843#"); if ( StringUtils.isNotBlank( cartId ) )
        {

            ShoppingCart cartModel = getCartModel( cartId,store );
												System.out.println("$#13844#"); if ( cartModel != null )
            {
																System.out.println("$#13845#"); if ( CollectionUtils.isNotEmpty( cartModel.getLineItems() ) )
                {
                    Set<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> shoppingCartItemSet =
                        new HashSet<com.salesmanager.core.model.shoppingcart.ShoppingCartItem>();
                    for ( com.salesmanager.core.model.shoppingcart.ShoppingCartItem shoppingCartItem : cartModel.getLineItems() )
                    {
																								System.out.println("$#13846#"); if(shoppingCartItem.getId().longValue() == itemID.longValue() )
                        {
																						System.out.println("$#13847#"); shoppingCartService.deleteShoppingCartItem(itemID);
                        } else {
                            shoppingCartItemSet.add(shoppingCartItem);
                        }
                    }

																				System.out.println("$#13848#"); cartModel.setLineItems(shoppingCartItemSet);


                    ShoppingCartDataPopulator shoppingCartDataPopulator = new ShoppingCartDataPopulator();
																				System.out.println("$#13849#"); shoppingCartDataPopulator.setShoppingCartCalculationService( shoppingCartCalculationService );
																				System.out.println("$#13850#"); shoppingCartDataPopulator.setPricingService( pricingService );
																				System.out.println("$#13851#"); shoppingCartDataPopulator.setimageUtils(imageUtils);
																				System.out.println("$#13852#"); return shoppingCartDataPopulator.populate( cartModel, store, language );


                }
            }
        }
        return null;
    }

    @Override
    public ShoppingCartData updateCartItem( final Long itemID, final String cartId, final long newQuantity,final MerchantStore store, final Language language )
        throws Exception
    {
								System.out.println("$#13854#"); System.out.println("$#13853#"); if ( newQuantity < 1 )
        {
            throw new CartModificationException( "Quantity must not be less than one" );
        }
								System.out.println("$#13855#"); if ( StringUtils.isNotBlank( cartId ) )
        {
            ShoppingCart cartModel = getCartModel( cartId,store );
												System.out.println("$#13856#"); if ( cartModel != null )
            {
                com.salesmanager.core.model.shoppingcart.ShoppingCartItem entryToUpdate =
                    getEntryToUpdate( itemID.longValue(), cartModel );

																System.out.println("$#13857#"); if ( entryToUpdate == null )
                {
                    throw new CartModificationException( "Unknown entry number." );
                }

                entryToUpdate.getProduct();

                LOG.info( "Updating cart entry quantity to" + newQuantity );
																System.out.println("$#13858#"); entryToUpdate.setQuantity( (int) newQuantity );
                List<ProductAttribute> productAttributes = new ArrayList<ProductAttribute>();
                productAttributes.addAll( entryToUpdate.getProduct().getAttributes() );
                final FinalPrice finalPrice =
                    productPriceUtils.getFinalProductPrice( entryToUpdate.getProduct(), productAttributes );
																System.out.println("$#13859#"); entryToUpdate.setItemPrice( finalPrice.getFinalPrice() );
																System.out.println("$#13860#"); shoppingCartService.saveOrUpdate( cartModel );

                LOG.info( "Cart entry updated with desired quantity" );
                ShoppingCartDataPopulator shoppingCartDataPopulator = new ShoppingCartDataPopulator();
																System.out.println("$#13861#"); shoppingCartDataPopulator.setShoppingCartCalculationService( shoppingCartCalculationService );
																System.out.println("$#13862#"); shoppingCartDataPopulator.setPricingService( pricingService );
																System.out.println("$#13863#"); shoppingCartDataPopulator.setimageUtils(imageUtils);
																System.out.println("$#13864#"); return shoppingCartDataPopulator.populate( cartModel, store, language );

            }
        }
        return null;
    }


    //TODO promoCode request parameter
	@Override
    public ShoppingCartData updateCartItems( Optional<String> promoCode, final List<ShoppingCartItem> shoppingCartItems, final MerchantStore store, final Language language )
            throws Exception
        {

    		Validate.notEmpty(shoppingCartItems,"shoppingCartItems null or empty");
    		ShoppingCart cartModel = null;
    		Set<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> cartItems = new HashSet<com.salesmanager.core.model.shoppingcart.ShoppingCartItem>();
    		for(ShoppingCartItem item : shoppingCartItems) {

							System.out.println("$#13866#"); System.out.println("$#13865#"); if(item.getQuantity()<1) {
    				throw new CartModificationException( "Quantity must not be less than one" );
    			}

							System.out.println("$#13867#"); if(cartModel==null) {
    				cartModel = getCartModel( item.getCode(), store );
    			}

                com.salesmanager.core.model.shoppingcart.ShoppingCartItem entryToUpdate =
                        getEntryToUpdate( item.getId(), cartModel );

																System.out.println("$#13868#"); if ( entryToUpdate == null ) {
                        throw new CartModificationException( "Unknown entry number." );
                }

                entryToUpdate.getProduct();

                LOG.info( "Updating cart entry quantity to" + item.getQuantity() );
																System.out.println("$#13869#"); entryToUpdate.setQuantity( (int) item.getQuantity() );

                List<ProductAttribute> productAttributes = new ArrayList<ProductAttribute>();
                productAttributes.addAll( entryToUpdate.getProduct().getAttributes() );

                final FinalPrice finalPrice =
                        productPriceUtils.getFinalProductPrice( entryToUpdate.getProduct(), productAttributes );
																System.out.println("$#13870#"); entryToUpdate.setItemPrice( finalPrice.getFinalPrice() );


                cartItems.add(entryToUpdate);

    		}

						System.out.println("$#13871#"); cartModel.setPromoCode(null);
						System.out.println("$#13872#"); if(promoCode.isPresent()) {
							System.out.println("$#13873#"); cartModel.setPromoCode(promoCode.get());
							System.out.println("$#13874#"); cartModel.setPromoAdded(new Date());
    		}

						System.out.println("$#13875#"); cartModel.setLineItems(cartItems);
						System.out.println("$#13876#"); shoppingCartService.saveOrUpdate( cartModel );


            LOG.info( "Cart entry updated with desired quantity" );
            ShoppingCartDataPopulator shoppingCartDataPopulator = new ShoppingCartDataPopulator();
												System.out.println("$#13877#"); shoppingCartDataPopulator.setShoppingCartCalculationService( shoppingCartCalculationService );
												System.out.println("$#13878#"); shoppingCartDataPopulator.setPricingService( pricingService );
												System.out.println("$#13879#"); shoppingCartDataPopulator.setimageUtils(imageUtils);
												System.out.println("$#13880#"); return shoppingCartDataPopulator.populate( cartModel, store, language );

        }


    private ShoppingCart getCartModel( final String cartId,final MerchantStore store )
    {
								System.out.println("$#13881#"); if ( StringUtils.isNotBlank( cartId ) )
        {
           try
            {
																System.out.println("$#13882#"); return shoppingCartService.getByCode( cartId, store );
            }
            catch ( ServiceException e )
            {
                LOG.error( "unable to find any cart asscoiated with this Id: " + cartId );
                LOG.error( "error while fetching cart model...", e );
                return null;
            }
            catch( NoResultException nre) {
           	//nothing
            }

        }
        return null;
    }

	@Override
	public ShoppingCartData getShoppingCartData(String code, MerchantStore store, Language language) {
		try {
			ShoppingCart cartModel = shoppingCartService.getByCode( code, store );
			System.out.println("$#13883#"); if(cartModel!=null) {

				ShoppingCartData cart = getShoppingCartData(cartModel, language);
				System.out.println("$#13884#"); return cart;
			}
		} catch( NoResultException nre) {
	        	//nothing

		} catch(Exception e) {
			LOG.error("Cannot retrieve cart code " + code,e);
		}


		return null;
	}

	@Override
	public ShoppingCart getShoppingCartModel(String shoppingCartCode,
			MerchantStore store) throws Exception {
		System.out.println("$#13885#"); return shoppingCartService.getByCode( shoppingCartCode, store );
	}

	@Override
	public ShoppingCart getShoppingCartModel(Customer customer,
			MerchantStore store) throws Exception {
		System.out.println("$#13886#"); return shoppingCartService.getShoppingCart(customer);
	}

	@Override
	public void saveOrUpdateShoppingCart(ShoppingCart cart) throws Exception {
		System.out.println("$#13887#"); shoppingCartService.saveOrUpdate(cart);

	}

	@Override
	public ReadableShoppingCart getCart(Customer customer, MerchantStore store, Language language) throws Exception {

		Validate.notNull(customer,"Customer cannot be null");
		Validate.notNull(customer.getId(),"Customer.id cannot be null or empty");

		//Check if customer has an existing shopping cart
//<<<<<<< HEAD
//		ShoppingCart cartModel = shoppingCartService.getByCustomer(customer);
//
//=======
		ShoppingCart cartModel = shoppingCartService.getShoppingCart(customer);

//>>>>>>> a4f3b1d8db7306e0d96181047259e705b3edcf85
		System.out.println("$#13888#"); if(cartModel == null) {
			return null;
		}

        shoppingCartCalculationService.calculate( cartModel, store, language );

        ReadableShoppingCartPopulator readableShoppingCart = new ReadableShoppingCartPopulator();

								System.out.println("$#13889#"); readableShoppingCart.setImageUtils(imageUtils);
								System.out.println("$#13890#"); readableShoppingCart.setPricingService(pricingService);
								System.out.println("$#13891#"); readableShoppingCart.setProductAttributeService(productAttributeService);
								System.out.println("$#13892#"); readableShoppingCart.setShoppingCartCalculationService(shoppingCartCalculationService);

        ReadableShoppingCart readableCart = new  ReadableShoppingCart();

        readableShoppingCart.populate(cartModel, readableCart,  store, language);


		System.out.println("$#13893#"); return readableCart;
	}

	@Override
	public ReadableShoppingCart addToCart(PersistableShoppingCartItem item, MerchantStore store,
			Language language) throws Exception {

		Validate.notNull(item,"PersistableShoppingCartItem cannot be null");

		//if cart does not exist create a new one

		ShoppingCart cartModel = new ShoppingCart();
		System.out.println("$#13894#"); cartModel.setMerchantStore(store);
		System.out.println("$#13895#"); cartModel.setShoppingCartCode(uniqueShoppingCartCode());


		System.out.println("$#13896#"); return readableShoppingCart(cartModel,item,store,language);
	}



	@Override
	public @Nullable ReadableShoppingCart removeShoppingCartItem(String cartCode, Long productId,
                                                MerchantStore merchant, Language language, boolean returnCart) throws Exception {
	    Validate.notNull(cartCode, "Shopping cart code must not be null");
	    Validate.notNull(productId, "product id must not be null");
	    Validate.notNull(merchant, "MerchantStore must not be null");


	    //get cart
	    ShoppingCart cart = getCartModel(cartCode, merchant);

					System.out.println("$#13897#"); if(cart == null) {
	      throw new ResourceNotFoundException("Cart code [ " + cartCode + " ] not found");
	    }

	    Set<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> items = new HashSet<com.salesmanager.core.model.shoppingcart.ShoppingCartItem>();
	    com.salesmanager.core.model.shoppingcart.ShoppingCartItem itemToDelete = null;
	    for ( com.salesmanager.core.model.shoppingcart.ShoppingCartItem shoppingCartItem : cart.getLineItems() )
        {
												System.out.println("$#13898#"); if ( shoppingCartItem.getProduct().getId().longValue() == productId.longValue() )
            {
                //get cart item
                itemToDelete =
                    getEntryToUpdate( shoppingCartItem.getId(), cart );


                //break;

            } else {
              items.add(shoppingCartItem);
            }
        }
	    //delete item
					System.out.println("$#13899#"); if(itemToDelete!=null) {
							System.out.println("$#13900#"); shoppingCartService.deleteShoppingCartItem(itemToDelete.getId());
	    }

        //remaining items
					System.out.println("$#13902#"); System.out.println("$#13901#"); if(items.size()>0) {
						System.out.println("$#13903#"); cart.setLineItems(items);
	    } else {
						System.out.println("$#13904#"); cart.getLineItems().clear();
	    }

								System.out.println("$#13905#"); shoppingCartService.saveOrUpdate(cart);//update cart with remaining items
					System.out.println("$#13908#"); System.out.println("$#13907#"); System.out.println("$#13906#"); if(items.size()>0 & returnCart) {
										System.out.println("$#13910#"); return this.getByCode(cartCode, merchant, language);
        }
        return null;
	}

	private ReadableShoppingCart readableShoppingCart(ShoppingCart cartModel, PersistableShoppingCartItem item, MerchantStore store,
			Language language) throws Exception {


		com.salesmanager.core.model.shoppingcart.ShoppingCartItem itemModel = createCartItem(cartModel, item, store);

		//need to check if the item is already in the cart
        boolean duplicateFound = false;
        //only if item has no attributes
								System.out.println("$#13911#"); if(CollectionUtils.isEmpty(item.getAttributes())) {//increment quantity
        	//get duplicate item from the cart
        	Set<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> cartModelItems = cartModel.getLineItems();
        	for(com.salesmanager.core.model.shoppingcart.ShoppingCartItem cartItem : cartModelItems) {
										System.out.println("$#13912#"); if(cartItem.getProduct().getId().longValue()==item.getProduct().longValue()) {
											System.out.println("$#13913#"); if(CollectionUtils.isEmpty(cartItem.getAttributes())) {
												System.out.println("$#13914#"); if(!duplicateFound) {
													System.out.println("$#13915#"); if(!itemModel.isProductVirtual()) {
														System.out.println("$#13917#"); System.out.println("$#13916#"); cartItem.setQuantity(cartItem.getQuantity() + item.getQuantity());
        					}
        					duplicateFound = true;
        					break;
        				}
        			}
        		}
        	}
        }

								System.out.println("$#13918#"); if(!duplicateFound) {
        	cartModel.getLineItems().add( itemModel );
        }

								System.out.println("$#13919#"); saveShoppingCart( cartModel );

        //refresh cart
        cartModel = shoppingCartService.getById(cartModel.getId(), store);

        shoppingCartCalculationService.calculate( cartModel, store, language );

        ReadableShoppingCartPopulator readableShoppingCart = new ReadableShoppingCartPopulator();

								System.out.println("$#13920#"); readableShoppingCart.setImageUtils(imageUtils);
								System.out.println("$#13921#"); readableShoppingCart.setPricingService(pricingService);
								System.out.println("$#13922#"); readableShoppingCart.setProductAttributeService(productAttributeService);
								System.out.println("$#13923#"); readableShoppingCart.setShoppingCartCalculationService(shoppingCartCalculationService);

        ReadableShoppingCart readableCart = new  ReadableShoppingCart();

        readableShoppingCart.populate(cartModel, readableCart,  store, language);


		System.out.println("$#13924#"); return readableCart;

	}


	private ReadableShoppingCart modifyCart(ShoppingCart cartModel, PersistableShoppingCartItem item, MerchantStore store,
			Language language) throws Exception {


		com.salesmanager.core.model.shoppingcart.ShoppingCartItem itemModel = createCartItem(cartModel, item, store);

        boolean itemModified = false;
        //check if existing product
       	Set<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> items = cartModel.getLineItems();
								System.out.println("$#13925#"); if(!CollectionUtils.isEmpty(items)) {
       		Set<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> newItems = new HashSet<com.salesmanager.core.model.shoppingcart.ShoppingCartItem>();
       		Set<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> removeItems = new HashSet<com.salesmanager.core.model.shoppingcart.ShoppingCartItem>();
	    	for(com.salesmanager.core.model.shoppingcart.ShoppingCartItem anItem : items) {//take care of existing product
							System.out.println("$#13926#"); if(itemModel.getProduct().getId().longValue() == anItem.getProduct().getId()) {
								System.out.println("$#13927#"); if(item.getQuantity()==0) {
	    			    //left aside item to be removed
	    				//don't add it to new list of item
	    				removeItems.add(anItem);
	    			} else {
	    				//new quantity
									System.out.println("$#13928#"); anItem.setQuantity(item.getQuantity());
	    				newItems.add(anItem);
	    			}
	    			itemModified = true;
	    		} else {
	    			newItems.add(anItem);
	    		}
	    	}

						System.out.println("$#13929#"); if(!removeItems.isEmpty()) {
	    		for(com.salesmanager.core.model.shoppingcart.ShoppingCartItem emptyItem : removeItems) {
								System.out.println("$#13930#"); shoppingCartService.deleteShoppingCartItem(emptyItem.getId());
	    		}

	    	}

						System.out.println("$#13931#"); if(!itemModified) {
	    	  newItems.add(itemModel);
	    	}

						System.out.println("$#13932#"); if(newItems.isEmpty()) {
	    		newItems = null;
	    	}

						System.out.println("$#13933#"); cartModel.setLineItems(newItems);
       	} else {
           	//new item
													System.out.println("$#13935#"); System.out.println("$#13934#"); if(item.getQuantity() > 0) {
                cartModel.getLineItems().add( itemModel );
             }
       	}

       	//if cart items are null just return cart with no items

								System.out.println("$#13936#"); saveShoppingCart( cartModel );

        //refresh cart
        cartModel = shoppingCartService.getById(cartModel.getId(), store);

								System.out.println("$#13937#"); if(cartModel==null) {
        	return null;
        }

        shoppingCartCalculationService.calculate( cartModel, store, language );

        ReadableShoppingCartPopulator readableShoppingCart = new ReadableShoppingCartPopulator();

								System.out.println("$#13938#"); readableShoppingCart.setImageUtils(imageUtils);
								System.out.println("$#13939#"); readableShoppingCart.setPricingService(pricingService);
								System.out.println("$#13940#"); readableShoppingCart.setProductAttributeService(productAttributeService);
								System.out.println("$#13941#"); readableShoppingCart.setShoppingCartCalculationService(shoppingCartCalculationService);

        ReadableShoppingCart readableCart = new  ReadableShoppingCart();

        readableShoppingCart.populate(cartModel, readableCart,  store, language);


		System.out.println("$#13942#"); return readableCart;

	}


    /**
     * Update cart based on the Items coming in with cartItems,
     * Items not in incoming will not be affected,
     * Items with Qty set to 0 will be removed from cart
     *
     * @param cartModel
     * @param cartItems
     * @param store
     * @param language
     * @return
     * @throws Exception
     */
    private ReadableShoppingCart modifyCartMulti(ShoppingCart cartModel, List<PersistableShoppingCartItem> cartItems, MerchantStore store,
                                                 Language language) throws Exception {


        List<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> inCartItemList = createCartItems(cartModel, cartItems, store);

        int itemUpdatedCnt = 0;

        Set<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> existingItems = cartModel.getLineItems();
        // loop over incoming items since they drive changes
        for (com.salesmanager.core.model.shoppingcart.ShoppingCartItem newItemValue : inCartItemList) {

            // check that item exist in persisted cart
            Optional<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> oOldItem =
                    existingItems.stream().filter(i -> i.getProductId().intValue() == newItemValue.getProductId().intValue()).findFirst();

												System.out.println("$#13945#"); if (oOldItem.isPresent()) {
                // update of existing cartItem
                com.salesmanager.core.model.shoppingcart.ShoppingCartItem oldCartItem = oOldItem.get();
																System.out.println("$#13946#"); if (oldCartItem.getQuantity().intValue() == newItemValue.getQuantity()) {
                    // this is unchanged
                    continue;
                }
																System.out.println("$#13947#"); if (newItemValue.getQuantity() == 0) {
                    // remove from cart
																				System.out.println("$#13948#"); shoppingCartService.deleteShoppingCartItem(oldCartItem.getId());
                    cartModel.getLineItems().remove(oldCartItem);
																				System.out.println("$#13949#"); ++itemUpdatedCnt;
                    continue;
                }
                // update qty
																System.out.println("$#13950#"); oldCartItem.setQuantity(newItemValue.getQuantity());
																System.out.println("$#13951#"); ++itemUpdatedCnt;
            } else {
                // addition of new item
                cartModel.getLineItems().add(newItemValue);
																System.out.println("$#13952#"); ++itemUpdatedCnt;
            }
        }
        // at the moment we expect that some change have been done
								System.out.println("$#13953#"); saveShoppingCart(cartModel);

        //refresh cart
        cartModel = shoppingCartService.getById(cartModel.getId(), store);

								System.out.println("$#13954#"); if (cartModel == null) {
            return null;
        }

        shoppingCartCalculationService.calculate(cartModel, store, language);

        ReadableShoppingCartPopulator readableShoppingCart = new ReadableShoppingCartPopulator();

								System.out.println("$#13955#"); readableShoppingCart.setImageUtils(imageUtils);
								System.out.println("$#13956#"); readableShoppingCart.setPricingService(pricingService);
								System.out.println("$#13957#"); readableShoppingCart.setProductAttributeService(productAttributeService);
								System.out.println("$#13958#"); readableShoppingCart.setShoppingCartCalculationService(shoppingCartCalculationService);

        ReadableShoppingCart readableCart = new ReadableShoppingCart();

        readableShoppingCart.populate(cartModel, readableCart, store, language);


								System.out.println("$#13959#"); return readableCart;

    }

	@Override
	public ReadableShoppingCart addToCart(Customer customer, PersistableShoppingCartItem item, MerchantStore store,
			Language language) throws Exception {

		Validate.notNull(customer,"Customer cannot be null");
		Validate.notNull(customer.getId(),"Customer.id cannot be null or empty");

		//Check if customer has an existing shopping cart
//<<<<<<< HEAD
//		ShoppingCart cartModel = shoppingCartService.getByCustomer(customer);
//
//=======
		ShoppingCart cartModel = shoppingCartService.getShoppingCart(customer);

//>>>>>>> a4f3b1d8db7306e0d96181047259e705b3edcf85
		//if cart does not exist create a new one
		System.out.println("$#13960#"); if(cartModel==null) {
			cartModel = new ShoppingCart();
			System.out.println("$#13961#"); cartModel.setCustomerId(customer.getId());
			System.out.println("$#13962#"); cartModel.setMerchantStore(store);
			System.out.println("$#13963#"); cartModel.setShoppingCartCode(uniqueShoppingCartCode());
		}

		System.out.println("$#13964#"); return readableShoppingCart(cartModel,item,store,language);
	}

    @Override
    public ReadableShoppingCart modifyCart(String cartCode, PersistableShoppingCartItem item, MerchantStore store,
                                                Language language) throws Exception {

        Validate.notNull(cartCode, "String cart code cannot be null");
        Validate.notNull(item, "PersistableShoppingCartItem cannot be null");

        ShoppingCart cartModel = this.getCartModel(cartCode, store);
								System.out.println("$#13965#"); if (cartModel == null) {
            return null;
            // throw new IllegalArgumentException("Cart code not valid");
        }

								System.out.println("$#13966#"); return modifyCart(cartModel, item, store, language);
    }

    @Override
    public ReadableShoppingCart modifyCartMulti(String cartCode, List<PersistableShoppingCartItem> items, MerchantStore store, Language language) throws Exception {
        Validate.notNull(cartCode, "String cart code cannot be null");
        Validate.notNull(items, "PersistableShoppingCartItem cannot be null");

        ShoppingCart cartModel = this.getCartModel(cartCode, store);
								System.out.println("$#13967#"); if (cartModel == null) {
            throw new IllegalArgumentException("Cart code not valid");
        }

								System.out.println("$#13968#"); return modifyCartMulti(cartModel, items, store, language);
    }

    private void saveShoppingCart(ShoppingCart shoppingCart) throws Exception {
		System.out.println("$#13969#"); shoppingCartService.save(shoppingCart);
	}

	private String uniqueShoppingCartCode() {
		System.out.println("$#13970#"); return UUID.randomUUID().toString().replaceAll( "-", "" );
	}

	@Override
	public ReadableShoppingCart getById(Long shoppingCartId, MerchantStore store, Language language) throws Exception {

		ShoppingCart cart = shoppingCartService.getById(shoppingCartId);

		ReadableShoppingCart readableCart = null;

		System.out.println("$#13971#"); if(cart != null) {

	        ReadableShoppingCartPopulator readableShoppingCart = new ReadableShoppingCartPopulator();

									System.out.println("$#13972#"); readableShoppingCart.setImageUtils(imageUtils);
									System.out.println("$#13973#"); readableShoppingCart.setPricingService(pricingService);
									System.out.println("$#13974#"); readableShoppingCart.setProductAttributeService(productAttributeService);
									System.out.println("$#13975#"); readableShoppingCart.setShoppingCartCalculationService(shoppingCartCalculationService);

	        readableShoppingCart.populate(cart, readableCart,  store, language);


		}

		System.out.println("$#13976#"); return readableCart;
	}

	@Override
	public ShoppingCart getShoppingCartModel(Long id, MerchantStore store) throws Exception {
		System.out.println("$#13977#"); return shoppingCartService.getById(id);
	}

	@Override
	public ReadableShoppingCart getByCode(String code, MerchantStore store, Language language) throws Exception {

		ShoppingCart cart = shoppingCartService.getByCode(code, store);

		ReadableShoppingCart readableCart = null;

		System.out.println("$#13978#"); if(cart != null) {

	        ReadableShoppingCartPopulator readableShoppingCart = new ReadableShoppingCartPopulator();

									System.out.println("$#13979#"); readableShoppingCart.setImageUtils(imageUtils);
									System.out.println("$#13980#"); readableShoppingCart.setPricingService(pricingService);
									System.out.println("$#13981#"); readableShoppingCart.setProductAttributeService(productAttributeService);
									System.out.println("$#13982#"); readableShoppingCart.setShoppingCartCalculationService(shoppingCartCalculationService);

	        readableCart = readableShoppingCart.populate(cart, null,  store, language);


		}

		System.out.println("$#13983#"); return readableCart;

	}

	@Override
	public void setOrderId(String code, Long orderId, MerchantStore store) throws Exception {
		ShoppingCart cart = this.getShoppingCartModel(code, store);
		System.out.println("$#13984#"); if(cart == null) {
			LOG.warn("Shopping cart with code [" + code + "] not found, expected to find a cart to set order id [" + orderId + "]");
		} else {
			System.out.println("$#13985#"); cart.setOrderId(orderId);
		}
		System.out.println("$#13986#"); saveOrUpdateShoppingCart(cart);

	}



}
