/**
 *
 */
package com.salesmanager.shop.populator.shoppingCart;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.services.shoppingcart.ShoppingCartCalculationService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionDescription;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription;
import com.salesmanager.core.model.catalog.product.description.ProductDescription;
import com.salesmanager.core.model.catalog.product.image.ProductImage;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.OrderSummary;
import com.salesmanager.core.model.order.OrderTotalSummary;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shoppingcart.ShoppingCart;
import com.salesmanager.shop.model.order.total.OrderTotal;
import com.salesmanager.shop.model.shoppingcart.ShoppingCartAttribute;
import com.salesmanager.shop.model.shoppingcart.ShoppingCartData;
import com.salesmanager.shop.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.shop.utils.ImageFilePath;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author Umesh A
 *
 */


public class ShoppingCartDataPopulator extends AbstractDataPopulator<ShoppingCart,ShoppingCartData>
{

    private static final Logger LOG = LoggerFactory.getLogger(ShoppingCartDataPopulator.class);

    private PricingService pricingService;

    private  ShoppingCartCalculationService shoppingCartCalculationService;
    
    private ImageFilePath imageUtils;

			public ImageFilePath getimageUtils() {
				System.out.println("$#10957#"); return imageUtils;
			}
		
		
		
		
			public void setimageUtils(ImageFilePath imageUtils) {
				this.imageUtils = imageUtils;
			}



    @Override
    public ShoppingCartData createTarget()
    {

								System.out.println("$#10958#"); return new ShoppingCartData();
    }



    public ShoppingCartCalculationService getOrderService() {
								System.out.println("$#10959#"); return shoppingCartCalculationService;
    }



    public PricingService getPricingService() {
								System.out.println("$#10960#"); return pricingService;
    }


    @Override
    public ShoppingCartData populate(final ShoppingCart shoppingCart,
                                     final ShoppingCartData cart, final MerchantStore store, final Language language) {

    	Validate.notNull(shoppingCart, "Requires ShoppingCart");
    	Validate.notNull(language, "Requires Language not null");
    	int cartQuantity = 0;
								System.out.println("$#10961#"); cart.setCode(shoppingCart.getShoppingCartCode());
        Set<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> items = shoppingCart.getLineItems();
        List<ShoppingCartItem> shoppingCartItemsList=Collections.emptyList();
        try{
												System.out.println("$#10962#"); if(items!=null) {
                shoppingCartItemsList=new ArrayList<ShoppingCartItem>();
                for(com.salesmanager.core.model.shoppingcart.ShoppingCartItem item : items) {
                	
                    ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
																				System.out.println("$#10963#"); shoppingCartItem.setCode(cart.getCode());
																				System.out.println("$#10964#"); shoppingCartItem.setProductCode(item.getProduct().getSku());
																				System.out.println("$#10965#"); shoppingCartItem.setProductVirtual(item.isProductVirtual());

																				System.out.println("$#10966#"); shoppingCartItem.setProductId(item.getProductId());
																				System.out.println("$#10967#"); shoppingCartItem.setId(item.getId());
                    
                    String itemName = item.getProduct().getProductDescription().getName();
																				System.out.println("$#10968#"); if(!CollectionUtils.isEmpty(item.getProduct().getDescriptions())) {
                    	for(ProductDescription productDescription : item.getProduct().getDescriptions()) {
																						System.out.println("$#10969#"); if(language != null && language.getId().intValue() == productDescription.getLanguage().getId().intValue()) {
                    			itemName = productDescription.getName();
                    			break;
                    		}
                    	}
                    }
                    
																				System.out.println("$#10971#"); shoppingCartItem.setName(itemName);

																				System.out.println("$#10972#"); shoppingCartItem.setPrice(pricingService.getDisplayAmount(item.getItemPrice(),store));
																				System.out.println("$#10973#"); shoppingCartItem.setQuantity(item.getQuantity());
                    
                    
																				System.out.println("$#10974#"); cartQuantity = cartQuantity + item.getQuantity();
                    
																				System.out.println("$#10975#"); shoppingCartItem.setProductPrice(item.getItemPrice());
																				System.out.println("$#10976#"); shoppingCartItem.setSubTotal(pricingService.getDisplayAmount(item.getSubTotal(), store));
                    ProductImage image = item.getProduct().getProductImage();
																				System.out.println("$#10977#"); if(image!=null && imageUtils!=null) {
                        String imagePath = imageUtils.buildProductImageUtils(store, item.getProduct().getSku(), image.getProductImage());
																								System.out.println("$#10979#"); shoppingCartItem.setImage(imagePath);
                    }
                    Set<com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem> attributes = item.getAttributes();
																				System.out.println("$#10980#"); if(attributes!=null) {
                        List<ShoppingCartAttribute> cartAttributes = new ArrayList<ShoppingCartAttribute>();
                        for(com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem attribute : attributes) {
                            ShoppingCartAttribute cartAttribute = new ShoppingCartAttribute();
																												System.out.println("$#10981#"); cartAttribute.setId(attribute.getId());
																												System.out.println("$#10982#"); cartAttribute.setAttributeId(attribute.getProductAttributeId());
																												System.out.println("$#10983#"); cartAttribute.setOptionId(attribute.getProductAttribute().getProductOption().getId());
																												System.out.println("$#10984#"); cartAttribute.setOptionValueId(attribute.getProductAttribute().getProductOptionValue().getId());
                            List<ProductOptionDescription> optionDescriptions = attribute.getProductAttribute().getProductOption().getDescriptionsSettoList();
                            List<ProductOptionValueDescription> optionValueDescriptions = attribute.getProductAttribute().getProductOptionValue().getDescriptionsSettoList();
																												System.out.println("$#10985#"); if(!CollectionUtils.isEmpty(optionDescriptions) && !CollectionUtils.isEmpty(optionValueDescriptions)) {
                            	
                            	String optionName = optionDescriptions.get(0).getName();
                            	String optionValue = optionValueDescriptions.get(0).getName();
                            	
                            	for(ProductOptionDescription optionDescription : optionDescriptions) {
																														System.out.println("$#10987#"); if(optionDescription.getLanguage() != null && optionDescription.getLanguage().getId().intValue() == language.getId().intValue()) {
                            			optionName = optionDescription.getName();
                            			break;
                            		}
                            	}
                            	
                            	for(ProductOptionValueDescription optionValueDescription : optionValueDescriptions) {
																														System.out.println("$#10989#"); if(optionValueDescription.getLanguage() != null && optionValueDescription.getLanguage().getId().intValue() == language.getId().intValue()) {
                            			optionValue = optionValueDescription.getName();
                            			break;
                            		}
                            	}
																													System.out.println("$#10991#"); cartAttribute.setOptionName(optionName);
																													System.out.println("$#10992#"); cartAttribute.setOptionValue(optionValue);
                            	cartAttributes.add(cartAttribute);
                            }
                        }
																								System.out.println("$#10993#"); shoppingCartItem.setShoppingCartAttributes(cartAttributes);
                    }
                    shoppingCartItemsList.add(shoppingCartItem);
                }
            }
												System.out.println("$#10994#"); if(CollectionUtils.isNotEmpty(shoppingCartItemsList)){
																System.out.println("$#10995#"); cart.setShoppingCartItems(shoppingCartItemsList);
            }
            
												System.out.println("$#10996#"); if(shoppingCart.getOrderId() != null) {
													System.out.println("$#10997#"); cart.setOrderId(shoppingCart.getOrderId());
            }

            OrderSummary summary = new OrderSummary();
            List<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> productsList = new ArrayList<com.salesmanager.core.model.shoppingcart.ShoppingCartItem>();
            productsList.addAll(shoppingCart.getLineItems());
												System.out.println("$#11000#"); summary.setProducts(productsList.stream().filter(p -> p.getProduct().isAvailable()).collect(Collectors.toList()));
            OrderTotalSummary orderSummary = shoppingCartCalculationService.calculate(shoppingCart,store, language );

												System.out.println("$#11001#"); if(CollectionUtils.isNotEmpty(orderSummary.getTotals())) {
            	List<OrderTotal> totals = new ArrayList<OrderTotal>();
            	for(com.salesmanager.core.model.order.OrderTotal t : orderSummary.getTotals()) {
            		OrderTotal total = new OrderTotal();
														System.out.println("$#11002#"); total.setCode(t.getOrderTotalCode());
														System.out.println("$#11003#"); total.setText(t.getText());
														System.out.println("$#11004#"); total.setValue(t.getValue());
            		totals.add(total);
            	}
													System.out.println("$#11005#"); cart.setTotals(totals);
            }
            
												System.out.println("$#11006#"); cart.setSubTotal(pricingService.getDisplayAmount(orderSummary.getSubTotal(), store));
												System.out.println("$#11007#"); cart.setTotal(pricingService.getDisplayAmount(orderSummary.getTotal(), store));
												System.out.println("$#11008#"); cart.setQuantity(cartQuantity);
												System.out.println("$#11009#"); cart.setId(shoppingCart.getId());
        }
        catch(ServiceException ex){
            LOG.error( "Error while converting cart Model to cart Data.."+ex );
            throw new ConversionException( "Unable to create cart data", ex );
        }
								System.out.println("$#11010#"); return cart;


    };





    public void setPricingService(final PricingService pricingService) {
        this.pricingService = pricingService;
    }






    public void setShoppingCartCalculationService(final ShoppingCartCalculationService shoppingCartCalculationService) {
        this.shoppingCartCalculationService = shoppingCartCalculationService;
    }




}
