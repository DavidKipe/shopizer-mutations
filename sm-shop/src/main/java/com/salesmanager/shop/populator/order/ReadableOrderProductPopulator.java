package com.salesmanager.shop.populator.order;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.image.ProductImage;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.orderproduct.OrderProduct;
import com.salesmanager.core.model.order.orderproduct.OrderProductAttribute;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.catalog.product.ReadableProduct;
import com.salesmanager.shop.model.order.ReadableOrderProduct;
import com.salesmanager.shop.model.order.ReadableOrderProductAttribute;
import com.salesmanager.shop.populator.catalog.ReadableProductPopulator;
import com.salesmanager.shop.utils.ImageFilePath;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ReadableOrderProductPopulator extends
		AbstractDataPopulator<OrderProduct, ReadableOrderProduct> {
	
	private ProductService productService;
	private PricingService pricingService;
	private ImageFilePath imageUtils;



	public ImageFilePath getimageUtils() {
		System.out.println("$#10724#"); return imageUtils;
	}

	public void setimageUtils(ImageFilePath imageUtils) {
		this.imageUtils = imageUtils;
	}

	@Override
	public ReadableOrderProduct populate(OrderProduct source,
			ReadableOrderProduct target, MerchantStore store, Language language)
			throws ConversionException {
		
		Validate.notNull(productService,"Requires ProductService");
		Validate.notNull(pricingService,"Requires PricingService");
		Validate.notNull(imageUtils,"Requires imageUtils");
		System.out.println("$#10725#"); target.setId(source.getId());
		System.out.println("$#10726#"); target.setOrderedQuantity(source.getProductQuantity());
		try {
			System.out.println("$#10727#"); target.setPrice(pricingService.getDisplayAmount(source.getOneTimeCharge(), store));
		} catch(Exception e) {
			throw new ConversionException("Cannot convert price",e);
		}
		System.out.println("$#10728#"); target.setProductName(source.getProductName());
		System.out.println("$#10729#"); target.setSku(source.getSku());
		
		//subtotal = price * quantity
		BigDecimal subTotal = source.getOneTimeCharge();
		subTotal = subTotal.multiply(new BigDecimal(source.getProductQuantity()));
		
		try {
			String subTotalPrice = pricingService.getDisplayAmount(subTotal, store);
			System.out.println("$#10730#"); target.setSubTotal(subTotalPrice);
		} catch(Exception e) {
			throw new ConversionException("Cannot format price",e);
		}
		
		System.out.println("$#10731#"); if(source.getOrderAttributes()!=null) {
			List<ReadableOrderProductAttribute> attributes = new ArrayList<ReadableOrderProductAttribute>();
			for(OrderProductAttribute attr : source.getOrderAttributes()) {
				ReadableOrderProductAttribute readableAttribute = new ReadableOrderProductAttribute();
				try {
					String price = pricingService.getDisplayAmount(attr.getProductAttributePrice(), store);
					System.out.println("$#10732#"); readableAttribute.setAttributePrice(price);
				} catch (ServiceException e) {
					throw new ConversionException("Cannot format price",e);
				}
				
				System.out.println("$#10733#"); readableAttribute.setAttributeName(attr.getProductAttributeName());
				System.out.println("$#10734#"); readableAttribute.setAttributeValue(attr.getProductAttributeValueName());
				attributes.add(readableAttribute);
			}
			System.out.println("$#10735#"); target.setAttributes(attributes);
		}
		

			String productSku = source.getSku();
			System.out.println("$#10736#"); if(!StringUtils.isBlank(productSku)) {
				Product product = productService.getByCode(productSku, language);
				System.out.println("$#10737#"); if(product!=null) {
					
					
					
					ReadableProductPopulator populator = new ReadableProductPopulator();
					System.out.println("$#10738#"); populator.setPricingService(pricingService);
					System.out.println("$#10739#"); populator.setimageUtils(imageUtils);
					
					ReadableProduct productProxy = populator.populate(product, new ReadableProduct(), store, language);
					System.out.println("$#10740#"); target.setProduct(productProxy);
					
					Set<ProductImage> images = product.getImages();
					ProductImage defaultImage = null;
					System.out.println("$#10741#"); if(images!=null) {
						for(ProductImage image : images) {
							System.out.println("$#10742#"); if(defaultImage==null) {
								defaultImage = image;
							}
							System.out.println("$#10743#"); if(image.isDefaultImage()) {
								defaultImage = image;
							}
						}
					}
					System.out.println("$#10744#"); if(defaultImage!=null) {
						System.out.println("$#10745#"); target.setImage(defaultImage.getProductImage());
					}
				}
			}
		
		
		System.out.println("$#10746#"); return target;
	}

	@Override
	protected ReadableOrderProduct createTarget() {

		return null;
	}

	public ProductService getProductService() {
		System.out.println("$#10747#"); return productService;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}
	
	public PricingService getPricingService() {
		System.out.println("$#10748#"); return pricingService;
	}

	public void setPricingService(PricingService pricingService) {
		this.pricingService = pricingService;
	}

}
