package com.salesmanager.shop.populator.shoppingCart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductAttributeService;
import com.salesmanager.core.business.services.shoppingcart.ShoppingCartCalculationService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.catalog.product.attribute.ProductOption;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionDescription;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValue;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.OrderSummary;
import com.salesmanager.core.model.order.OrderTotalSummary;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shoppingcart.ShoppingCart;
import com.salesmanager.shop.model.order.total.ReadableOrderTotal;
import com.salesmanager.shop.model.shoppingcart.ReadableShoppingCart;
import com.salesmanager.shop.model.shoppingcart.ReadableShoppingCartAttribute;
import com.salesmanager.shop.model.shoppingcart.ReadableShoppingCartAttributeOption;
import com.salesmanager.shop.model.shoppingcart.ReadableShoppingCartAttributeOptionValue;
import com.salesmanager.shop.model.shoppingcart.ReadableShoppingCartItem;
import com.salesmanager.shop.populator.catalog.ReadableProductPopulator;
import com.salesmanager.shop.utils.ImageFilePath;

public class ReadableShoppingCartPopulator extends AbstractDataPopulator<ShoppingCart, ReadableShoppingCart> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReadableShoppingCartPopulator.class);
	
	private PricingService pricingService;
    private ShoppingCartCalculationService shoppingCartCalculationService;
    private ProductAttributeService productAttributeService;
    
    private ImageFilePath imageUtils;
	
	@Override
	public ReadableShoppingCart populate(ShoppingCart source, ReadableShoppingCart target, MerchantStore store,
			Language language) throws ConversionException {
    	Validate.notNull(source, "Requires ShoppingCart");
    	Validate.notNull(language, "Requires Language not null");
    	Validate.notNull(store, "Requires MerchantStore not null");
    	Validate.notNull(pricingService, "Requires to set pricingService");
    	Validate.notNull(productAttributeService, "Requires to set productAttributeService");
    	Validate.notNull(shoppingCartCalculationService, "Requires to set shoppingCartCalculationService");
    	Validate.notNull(imageUtils, "Requires to set imageUtils");
    	
					System.out.println("$#10907#"); if(target == null) {
    		target = new ReadableShoppingCart();
    	}
					System.out.println("$#10908#"); target.setCode(source.getShoppingCartCode());
    	int cartQuantity = 0;
    	
					System.out.println("$#10909#"); target.setCustomer(source.getCustomerId());
    	
    	try {
    	
    		Set<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> items = source.getLineItems();

												System.out.println("$#10910#"); if(items!=null) {

                for(com.salesmanager.core.model.shoppingcart.ShoppingCartItem item : items) {


                	ReadableShoppingCartItem shoppingCartItem = new ReadableShoppingCartItem();

                	ReadableProductPopulator readableProductPopulator = new ReadableProductPopulator();
																	System.out.println("$#10911#"); readableProductPopulator.setPricingService(pricingService);
																	System.out.println("$#10912#"); readableProductPopulator.setimageUtils(imageUtils);
                	readableProductPopulator.populate(item.getProduct(), shoppingCartItem,  store, language);



																				System.out.println("$#10913#"); shoppingCartItem.setPrice(item.getItemPrice());
					System.out.println("$#10914#"); shoppingCartItem.setFinalPrice(pricingService.getDisplayAmount(item.getItemPrice(),store));
			
																				System.out.println("$#10915#"); shoppingCartItem.setQuantity(item.getQuantity());
                    
																				System.out.println("$#10916#"); cartQuantity = cartQuantity + item.getQuantity();
                    
                    BigDecimal subTotal = pricingService.calculatePriceQuantity(item.getItemPrice(), item.getQuantity());
                    
                    //calculate sub total (price * quantity)
																				System.out.println("$#10917#"); shoppingCartItem.setSubTotal(subTotal);

					System.out.println("$#10918#"); shoppingCartItem.setDisplaySubTotal(pricingService.getDisplayAmount(subTotal,store));


                    Set<com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem> attributes = item.getAttributes();
																				System.out.println("$#10919#"); if(attributes!=null) {
                        for(com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem attribute : attributes) {

                        	ProductAttribute productAttribute = productAttributeService.getById(attribute.getProductAttributeId());
                        	
																									System.out.println("$#10920#"); if(productAttribute==null) {
                        		LOGGER.warn("Product attribute with ID " + attribute.getId() + " not found, skipping cart attribute " + attribute.getId());
                        		continue;
                        	}
                        	
                        	ReadableShoppingCartAttribute cartAttribute = new ReadableShoppingCartAttribute();
                        	

																												System.out.println("$#10921#"); cartAttribute.setId(attribute.getId());
                            
                            ProductOption option = productAttribute.getProductOption();
                            ProductOptionValue optionValue = productAttribute.getProductOptionValue();


                            List<ProductOptionDescription> optionDescriptions = option.getDescriptionsSettoList();
                            List<ProductOptionValueDescription> optionValueDescriptions = optionValue.getDescriptionsSettoList();
                            
                            String optName = null;
                            String optValue = null;
																												System.out.println("$#10922#"); if(!CollectionUtils.isEmpty(optionDescriptions) && !CollectionUtils.isEmpty(optionValueDescriptions)) {
                            	
                            	optName = optionDescriptions.get(0).getName();
                            	optValue = optionValueDescriptions.get(0).getName();
                            	
                            	for(ProductOptionDescription optionDescription : optionDescriptions) {
																														System.out.println("$#10924#"); if(optionDescription.getLanguage() != null && optionDescription.getLanguage().getId().intValue() == language.getId().intValue()) {
                            			optName = optionDescription.getName();
                            			break;
                            		}
                            	}
                            	
                            	for(ProductOptionValueDescription optionValueDescription : optionValueDescriptions) {
																														System.out.println("$#10926#"); if(optionValueDescription.getLanguage() != null && optionValueDescription.getLanguage().getId().intValue() == language.getId().intValue()) {
                            			optValue = optionValueDescription.getName();
                            			break;
                            		}
                            	}

                            }
                            
																												System.out.println("$#10928#"); if(optName != null) {
                            	ReadableShoppingCartAttributeOption attributeOption = new ReadableShoppingCartAttributeOption();
																													System.out.println("$#10929#"); attributeOption.setCode(option.getCode());
																													System.out.println("$#10930#"); attributeOption.setId(option.getId());
																													System.out.println("$#10931#"); attributeOption.setName(optName);
																													System.out.println("$#10932#"); cartAttribute.setOption(attributeOption);
                            }
                            
																												System.out.println("$#10933#"); if(optValue != null) {
                            	ReadableShoppingCartAttributeOptionValue attributeOptionValue = new ReadableShoppingCartAttributeOptionValue();
																													System.out.println("$#10934#"); attributeOptionValue.setCode(optionValue.getCode());
																													System.out.println("$#10935#"); attributeOptionValue.setId(optionValue.getId());
																													System.out.println("$#10936#"); attributeOptionValue.setName(optValue);
																													System.out.println("$#10937#"); cartAttribute.setOptionValue(attributeOptionValue);
                            }
                            shoppingCartItem.getCartItemattributes().add(cartAttribute);  
                        }
                       
                    }
                    target.getProducts().add(shoppingCartItem);
                }
            }
            
            //Calculate totals using shoppingCartService
            //OrderSummary contains ShoppingCart items

            OrderSummary summary = new OrderSummary();
            List<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> productsList = new ArrayList<com.salesmanager.core.model.shoppingcart.ShoppingCartItem>();
            productsList.addAll(source.getLineItems());
												System.out.println("$#10938#"); summary.setProducts(productsList);
            
            //OrdetTotalSummary contains all calculations
            
            OrderTotalSummary orderSummary = shoppingCartCalculationService.calculate(source, store, language );

												System.out.println("$#10939#"); if(CollectionUtils.isNotEmpty(orderSummary.getTotals())) {
            	List<ReadableOrderTotal> totals = new ArrayList<ReadableOrderTotal>();
            	for(com.salesmanager.core.model.order.OrderTotal t : orderSummary.getTotals()) {
            		ReadableOrderTotal total = new ReadableOrderTotal();
														System.out.println("$#10940#"); total.setCode(t.getOrderTotalCode());
														System.out.println("$#10941#"); total.setValue(t.getValue());
														System.out.println("$#10942#"); total.setText(t.getText());
            		totals.add(total);
            	}
													System.out.println("$#10943#"); target.setTotals(totals);
            }
            
												System.out.println("$#10944#"); target.setSubtotal(orderSummary.getSubTotal());
												System.out.println("$#10945#"); target.setDisplaySubTotal(pricingService.getDisplayAmount(orderSummary.getSubTotal(), store));
           
            
												System.out.println("$#10946#"); target.setTotal(orderSummary.getTotal());
												System.out.println("$#10947#"); target.setDisplayTotal(pricingService.getDisplayAmount(orderSummary.getTotal(), store));

            
												System.out.println("$#10948#"); target.setQuantity(cartQuantity);
												System.out.println("$#10949#"); target.setId(source.getId());
            
												System.out.println("$#10950#"); if(source.getOrderId() != null) {
													System.out.println("$#10951#"); target.setOrder(source.getOrderId());
            }
            
            
    	} catch(Exception e) {
    		throw new ConversionException(e);
    	}

								System.out.println("$#10952#"); return target;
    	
 
	}

	@Override
	protected ReadableShoppingCart createTarget() {
		return null;
	}

	public PricingService getPricingService() {
		System.out.println("$#10953#"); return pricingService;
	}

	public void setPricingService(PricingService pricingService) {
		this.pricingService = pricingService;
	}

	public ShoppingCartCalculationService getShoppingCartCalculationService() {
		System.out.println("$#10954#"); return shoppingCartCalculationService;
	}

	public void setShoppingCartCalculationService(ShoppingCartCalculationService shoppingCartCalculationService) {
		this.shoppingCartCalculationService = shoppingCartCalculationService;
	}

	public ImageFilePath getImageUtils() {
		System.out.println("$#10955#"); return imageUtils;
	}

	public void setImageUtils(ImageFilePath imageUtils) {
		this.imageUtils = imageUtils;
	}

	public ProductAttributeService getProductAttributeService() {
		System.out.println("$#10956#"); return productAttributeService;
	}

	public void setProductAttributeService(ProductAttributeService productAttributeService) {
		this.productAttributeService = productAttributeService;
	}

}
