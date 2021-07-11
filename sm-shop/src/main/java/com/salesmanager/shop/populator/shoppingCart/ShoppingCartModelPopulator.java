/**
 * 
 */
package com.salesmanager.shop.populator.shoppingCart;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductAttributeService;
import com.salesmanager.core.business.services.shoppingcart.ShoppingCartService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shoppingcart.ShoppingCart;
import com.salesmanager.shop.model.shoppingcart.ShoppingCartAttribute;
import com.salesmanager.shop.model.shoppingcart.ShoppingCartData;
import com.salesmanager.shop.model.shoppingcart.ShoppingCartItem;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Umesh A
 */

@Service(value="shoppingCartModelPopulator")
public class ShoppingCartModelPopulator
    extends AbstractDataPopulator<ShoppingCartData,ShoppingCart>
{

	private static final Logger LOG = LoggerFactory.getLogger(ShoppingCartModelPopulator.class);

    private ShoppingCartService shoppingCartService;
    
    private Customer customer;

    public ShoppingCartService getShoppingCartService() {
		System.out.println("$#11011#"); return shoppingCartService;
	}


	public void setShoppingCartService(ShoppingCartService shoppingCartService) {
		this.shoppingCartService = shoppingCartService;
	}


	private ProductService productService;


    public ProductService getProductService() {
		System.out.println("$#11012#"); return productService;
	}


	public void setProductService(ProductService productService) {
		this.productService = productService;
	}


	private ProductAttributeService productAttributeService;
    
   
    public ProductAttributeService getProductAttributeService() {
		System.out.println("$#11013#"); return productAttributeService;
	}


	public void setProductAttributeService(
			ProductAttributeService productAttributeService) {
		this.productAttributeService = productAttributeService;
	}


	@Override
    public ShoppingCart populate(ShoppingCartData shoppingCart,ShoppingCart cartMdel,final MerchantStore store, Language language)
    {


        // if id >0 get the original from the database, override products
       try{
								System.out.println("$#11015#"); System.out.println("$#11014#"); if ( shoppingCart.getId() > 0  && StringUtils.isNotBlank( shoppingCart.getCode()))
        {
            cartMdel = shoppingCartService.getByCode( shoppingCart.getCode(), store );
												System.out.println("$#11017#"); if(cartMdel==null){
                cartMdel=new ShoppingCart();
																System.out.println("$#11018#"); cartMdel.setShoppingCartCode( shoppingCart.getCode() );
																System.out.println("$#11019#"); cartMdel.setMerchantStore( store );
																System.out.println("$#11020#"); if ( customer != null )
                {
																				System.out.println("$#11021#"); cartMdel.setCustomerId( customer.getId() );
                }
																System.out.println("$#11022#"); shoppingCartService.create( cartMdel );
            }
        }
        else
        {
												System.out.println("$#11023#"); cartMdel.setShoppingCartCode( shoppingCart.getCode() );
												System.out.println("$#11024#"); cartMdel.setMerchantStore( store );
												System.out.println("$#11025#"); if ( customer != null )
            {
																System.out.println("$#11026#"); cartMdel.setCustomerId( customer.getId() );
            }
												System.out.println("$#11027#"); shoppingCartService.create( cartMdel );
        }

        List<ShoppingCartItem> items = shoppingCart.getShoppingCartItems();
        Set<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> newItems =
            new HashSet<com.salesmanager.core.model.shoppingcart.ShoppingCartItem>();
								System.out.println("$#11029#"); System.out.println("$#11028#"); if ( items != null && items.size() > 0 )
        {
            for ( ShoppingCartItem item : items )
            {

                Set<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> cartItems = cartMdel.getLineItems();
																System.out.println("$#11032#"); System.out.println("$#11031#"); if ( cartItems != null && cartItems.size() > 0 )
                {

                    for ( com.salesmanager.core.model.shoppingcart.ShoppingCartItem dbItem : cartItems )
                    {
																								System.out.println("$#11034#"); if ( dbItem.getId().longValue() == item.getId() )
                        {
																												System.out.println("$#11035#"); dbItem.setQuantity( item.getQuantity() );
                            // compare attributes
                            Set<com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem> attributes =
                                dbItem.getAttributes();
                            Set<com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem> newAttributes =
                                new HashSet<com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem>();
                            List<ShoppingCartAttribute> cartAttributes = item.getShoppingCartAttributes();
																												System.out.println("$#11036#"); if ( !CollectionUtils.isEmpty( cartAttributes ) )
                            {
                                for ( ShoppingCartAttribute attribute : cartAttributes )
                                {
                                    for ( com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem dbAttribute : attributes )
                                    {
																																								System.out.println("$#11037#"); if ( dbAttribute.getId().longValue() == attribute.getId() )
                                        {
                                            newAttributes.add( dbAttribute );
                                        }
                                    }
                                }
                                
																																System.out.println("$#11038#"); dbItem.setAttributes( newAttributes );
                            }
                            else
                            {
																																System.out.println("$#11039#"); dbItem.removeAllAttributes();
                            }
                            newItems.add( dbItem );
                        }
                    }
                }
                else
                {// create new item
                    com.salesmanager.core.model.shoppingcart.ShoppingCartItem cartItem =
                        createCartItem( cartMdel, item, store );
                    Set<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> lineItems =
                        cartMdel.getLineItems();
																				System.out.println("$#11040#"); if ( lineItems == null )
                    {
                        lineItems = new HashSet<com.salesmanager.core.model.shoppingcart.ShoppingCartItem>();
																								System.out.println("$#11041#"); cartMdel.setLineItems( lineItems );
                    }
                    lineItems.add( cartItem );
																				System.out.println("$#11042#"); shoppingCartService.update( cartMdel );
                }
            }// end for
        }// end if
       }catch(ServiceException se){
           LOG.error( "Error while converting cart data to cart model.."+se );
           throw new ConversionException( "Unable to create cart model", se ); 
       }
       catch (Exception ex){
           LOG.error( "Error while converting cart data to cart model.."+ex );
           throw new ConversionException( "Unable to create cart model", ex );  
       }

								System.out.println("$#11043#"); return cartMdel;
    }

   
    private com.salesmanager.core.model.shoppingcart.ShoppingCartItem createCartItem( com.salesmanager.core.model.shoppingcart.ShoppingCart cart,
                                                                                               ShoppingCartItem shoppingCartItem,
                                                                                               MerchantStore store )
        throws Exception
    {

        Product product = productService.getById( shoppingCartItem.getProductId() );

								System.out.println("$#11044#"); if ( product == null )
        {
            throw new Exception( "Item with id " + shoppingCartItem.getProductId() + " does not exist" );
        }

								System.out.println("$#11045#"); if ( product.getMerchantStore().getId().intValue() != store.getId().intValue() )
        {
            throw new Exception( "Item with id " + shoppingCartItem.getProductId() + " does not belong to merchant "
                + store.getId() );
        }

        com.salesmanager.core.model.shoppingcart.ShoppingCartItem item =
            new com.salesmanager.core.model.shoppingcart.ShoppingCartItem( cart, product );
								System.out.println("$#11046#"); item.setQuantity( shoppingCartItem.getQuantity() );
								System.out.println("$#11047#"); item.setItemPrice( shoppingCartItem.getProductPrice() );
								System.out.println("$#11048#"); item.setShoppingCart( cart );

        // attributes
        List<ShoppingCartAttribute> cartAttributes = shoppingCartItem.getShoppingCartAttributes();
								System.out.println("$#11049#"); if ( !CollectionUtils.isEmpty( cartAttributes ) )
        {
            Set<com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem> newAttributes =
                new HashSet<com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem>();
            for ( ShoppingCartAttribute attribute : cartAttributes )
            {
                ProductAttribute productAttribute = productAttributeService.getById( attribute.getAttributeId() );
                if ( productAttribute != null
                    && productAttribute.getProduct().getId().longValue() == product.getId().longValue() )
                {
                    com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem attributeItem =
                        new com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem( item,
                                                                                                         productAttribute );
																				System.out.println("$#11053#"); System.out.println("$#11052#"); if ( attribute.getAttributeId() > 0 )
                    {
																								System.out.println("$#11054#"); attributeItem.setId( attribute.getId() );
                    }
																				System.out.println("$#11055#"); item.addAttributes( attributeItem );
                    //newAttributes.add( attributeItem );
                }

            }
            
            //item.setAttributes( newAttributes );
        }

								System.out.println("$#11056#"); return item;

    }




    @Override
    protected ShoppingCart createTarget()
    {
      
								System.out.println("$#11057#"); return new ShoppingCart();
    }


	public Customer getCustomer() {
		System.out.println("$#11058#"); return customer;
	}


	public void setCustomer(Customer customer) {
		this.customer = customer;
	}


   


   

   

}
