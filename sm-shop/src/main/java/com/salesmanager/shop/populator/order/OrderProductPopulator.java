package com.salesmanager.shop.populator.order;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductAttributeService;
import com.salesmanager.core.business.services.catalog.product.file.DigitalProductService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.catalog.product.file.DigitalProduct;
import com.salesmanager.core.model.catalog.product.price.FinalPrice;
import com.salesmanager.core.model.catalog.product.price.ProductPrice;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.orderproduct.OrderProduct;
import com.salesmanager.core.model.order.orderproduct.OrderProductAttribute;
import com.salesmanager.core.model.order.orderproduct.OrderProductDownload;
import com.salesmanager.core.model.order.orderproduct.OrderProductPrice;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.shop.constants.ApplicationConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.Validate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrderProductPopulator extends
		AbstractDataPopulator<ShoppingCartItem, OrderProduct> {
	
	private ProductService productService;
	private DigitalProductService digitalProductService;
	private ProductAttributeService productAttributeService;


	public ProductAttributeService getProductAttributeService() {
		System.out.println("$#10470#"); return productAttributeService;
	}

	public void setProductAttributeService(
			ProductAttributeService productAttributeService) {
		this.productAttributeService = productAttributeService;
	}

	public DigitalProductService getDigitalProductService() {
		System.out.println("$#10471#"); return digitalProductService;
	}

	public void setDigitalProductService(DigitalProductService digitalProductService) {
		this.digitalProductService = digitalProductService;
	}

	/**
	 * Converts a ShoppingCartItem carried in the ShoppingCart to an OrderProduct
	 * that will be saved in the system
	 */
	@Override
	public OrderProduct populate(ShoppingCartItem source, OrderProduct target,
			MerchantStore store, Language language) throws ConversionException {
		
		System.out.println("$#10472#"); Validate.notNull(productService,"productService must be set");
		System.out.println("$#10473#"); Validate.notNull(digitalProductService,"digitalProductService must be set");
		System.out.println("$#10474#"); Validate.notNull(productAttributeService,"productAttributeService must be set");

		
		try {
			Product modelProduct = productService.getById(source.getProductId());
			System.out.println("$#10475#"); if(modelProduct==null) {
				throw new ConversionException("Cannot get product with id (productId) " + source.getProductId());
			}
			
			System.out.println("$#10476#"); if(modelProduct.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				throw new ConversionException("Invalid product id " + source.getProductId());
			}

			DigitalProduct digitalProduct = digitalProductService.getByProduct(store, modelProduct);
			
			System.out.println("$#10477#"); if(digitalProduct!=null) {
				OrderProductDownload orderProductDownload = new OrderProductDownload();	
				System.out.println("$#10478#"); orderProductDownload.setOrderProductFilename(digitalProduct.getProductFileName());
				System.out.println("$#10479#"); orderProductDownload.setOrderProduct(target);
				System.out.println("$#10480#"); orderProductDownload.setDownloadCount(0);
				System.out.println("$#10481#"); orderProductDownload.setMaxdays(ApplicationConstants.MAX_DOWNLOAD_DAYS);
				target.getDownloads().add(orderProductDownload);
			}

			System.out.println("$#10482#"); target.setOneTimeCharge(source.getItemPrice());
			System.out.println("$#10483#"); target.setProductName(source.getProduct().getDescriptions().iterator().next().getName());
			System.out.println("$#10484#"); target.setProductQuantity(source.getQuantity());
			System.out.println("$#10485#"); target.setSku(source.getProduct().getSku());
			
			FinalPrice finalPrice = source.getFinalPrice();
			System.out.println("$#10486#"); if(finalPrice==null) {
				throw new ConversionException("Object final price not populated in shoppingCartItem (source)");
			}
			//Default price
			OrderProductPrice orderProductPrice = orderProductPrice(finalPrice);
			System.out.println("$#10487#"); orderProductPrice.setOrderProduct(target);
			
			Set<OrderProductPrice> prices = new HashSet<OrderProductPrice>();
			prices.add(orderProductPrice);

			//Other prices
			List<FinalPrice> otherPrices = finalPrice.getAdditionalPrices();
			System.out.println("$#10488#"); if(otherPrices!=null) {
				for(FinalPrice otherPrice : otherPrices) {
					OrderProductPrice other = orderProductPrice(otherPrice);
					System.out.println("$#10489#"); other.setOrderProduct(target);
					prices.add(other);
				}
			}
			
			System.out.println("$#10490#"); target.setPrices(prices);
			
			//OrderProductAttribute
			Set<ShoppingCartAttributeItem> attributeItems = source.getAttributes();
			System.out.println("$#10491#"); if(!CollectionUtils.isEmpty(attributeItems)) {
				Set<OrderProductAttribute> attributes = new HashSet<OrderProductAttribute>();
				for(ShoppingCartAttributeItem attribute : attributeItems) {
					OrderProductAttribute orderProductAttribute = new OrderProductAttribute();
					System.out.println("$#10492#"); orderProductAttribute.setOrderProduct(target);
					Long id = attribute.getProductAttributeId();
					ProductAttribute attr = productAttributeService.getById(id);
					System.out.println("$#10493#"); if(attr==null) {
						throw new ConversionException("Attribute id " + id + " does not exists");
					}
					
					System.out.println("$#10494#"); if(attr.getProduct().getMerchantStore().getId().intValue()!=store.getId().intValue()) {
						throw new ConversionException("Attribute id " + id + " invalid for this store");
					}
					
					System.out.println("$#10495#"); orderProductAttribute.setProductAttributeIsFree(attr.getProductAttributeIsFree());
					System.out.println("$#10496#"); orderProductAttribute.setProductAttributeName(attr.getProductOption().getDescriptionsSettoList().get(0).getName());
					System.out.println("$#10497#"); orderProductAttribute.setProductAttributeValueName(attr.getProductOptionValue().getDescriptionsSettoList().get(0).getName());
					System.out.println("$#10498#"); orderProductAttribute.setProductAttributePrice(attr.getProductAttributePrice());
					System.out.println("$#10499#"); orderProductAttribute.setProductAttributeWeight(attr.getProductAttributeWeight());
					System.out.println("$#10500#"); orderProductAttribute.setProductOptionId(attr.getProductOption().getId());
					System.out.println("$#10501#"); orderProductAttribute.setProductOptionValueId(attr.getProductOptionValue().getId());
					attributes.add(orderProductAttribute);
				}
				System.out.println("$#10502#"); target.setOrderAttributes(attributes);
			}

			
		} catch (Exception e) {
			throw new ConversionException(e);
		}
		
		
		System.out.println("$#10503#"); return target;
	}

	@Override
	protected OrderProduct createTarget() {
		return null;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public ProductService getProductService() {
		System.out.println("$#10504#"); return productService;
	}
	
	private OrderProductPrice orderProductPrice(FinalPrice price) {
		
		OrderProductPrice orderProductPrice = new OrderProductPrice();
		
		ProductPrice productPrice = price.getProductPrice();
		
		System.out.println("$#10505#"); orderProductPrice.setDefaultPrice(productPrice.isDefaultPrice());

		System.out.println("$#10506#"); orderProductPrice.setProductPrice(price.getFinalPrice());
		System.out.println("$#10507#"); orderProductPrice.setProductPriceCode(productPrice.getCode());
		System.out.println("$#10509#"); System.out.println("$#10508#"); if(productPrice.getDescriptions()!=null && productPrice.getDescriptions().size()>0) {
			System.out.println("$#10511#"); orderProductPrice.setProductPriceName(productPrice.getDescriptions().iterator().next().getName());
		}
		System.out.println("$#10512#"); if(price.isDiscounted()) {
			System.out.println("$#10513#"); orderProductPrice.setProductPriceSpecial(productPrice.getProductPriceSpecialAmount());
			System.out.println("$#10514#"); orderProductPrice.setProductPriceSpecialStartDate(productPrice.getProductPriceSpecialStartDate());
			System.out.println("$#10515#"); orderProductPrice.setProductPriceSpecialEndDate(productPrice.getProductPriceSpecialEndDate());
		}
		
		System.out.println("$#10516#"); return orderProductPrice;
	}


}
