package com.salesmanager.shop.populator.order;

import org.apache.commons.lang3.Validate;

import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductAttributeService;
import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.core.business.services.shoppingcart.ShoppingCartService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.shop.model.order.PersistableOrderProduct;

public class ShoppingCartItemPopulator extends
		AbstractDataPopulator<PersistableOrderProduct, ShoppingCartItem> {


	private ProductService productService;
	private ProductAttributeService productAttributeService;
	private ShoppingCartService shoppingCartService;

	@Override
	public ShoppingCartItem populate(PersistableOrderProduct source, /** TODO: Fix, target not used possible future bug ! **/ShoppingCartItem target,
									 MerchantStore store, Language language)
			throws ConversionException {
		Validate.notNull(productService, "Requires to set productService");
		Validate.notNull(productAttributeService, "Requires to set productAttributeService");
		Validate.notNull(shoppingCartService, "Requires to set shoppingCartService");

		Product product = productService.getById(source.getProduct().getId());
		System.out.println("$#10832#"); if(source.getAttributes()!=null) {

			for(com.salesmanager.shop.model.catalog.product.attribute.ProductAttribute attr : source.getAttributes()) {
				ProductAttribute attribute = productAttributeService.getById(attr.getId());
				System.out.println("$#10833#"); if(attribute==null) {
					throw new ConversionException("ProductAttribute with id " + attr.getId() + " is null");
				}
				System.out.println("$#10834#"); if(attribute.getProduct().getId().longValue()!=source.getProduct().getId().longValue()) {
					throw new ConversionException("ProductAttribute with id " + attr.getId() + " is not assigned to Product id " + source.getProduct().getId());
				}
				product.getAttributes().add(attribute);
			}
		}

		try {
			System.out.println("$#10835#"); return shoppingCartService.populateShoppingCartItem(product);
		} catch (ServiceException e) {
			throw new ConversionException(e);
		}

	}

	@Override
	protected ShoppingCartItem createTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setProductAttributeService(ProductAttributeService productAttributeService) {
		this.productAttributeService = productAttributeService;
	}

	public ProductAttributeService getProductAttributeService() {
		System.out.println("$#10836#"); return productAttributeService;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public ProductService getProductService() {
		System.out.println("$#10837#"); return productService;
	}

	public void setShoppingCartService(ShoppingCartService shoppingCartService) {
		this.shoppingCartService = shoppingCartService;
	}

	public ShoppingCartService getShoppingCartService() {
		System.out.println("$#10838#"); return shoppingCartService;
	}

}
