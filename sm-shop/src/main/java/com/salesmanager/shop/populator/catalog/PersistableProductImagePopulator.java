package com.salesmanager.shop.populator.catalog;

import org.apache.commons.lang.Validate;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.image.ProductImage;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.catalog.product.PersistableImage;

public class PersistableProductImagePopulator extends AbstractDataPopulator<PersistableImage, ProductImage> {

	
	private Product product;
	
	@Override
	public ProductImage populate(PersistableImage source, ProductImage target, MerchantStore store, Language language)
			throws ConversionException {
		
		System.out.println("$#9487#"); Validate.notNull(product,"Must set a product setProduct(Product)");
		System.out.println("$#9488#"); Validate.notNull(product.getId(),"Product must have an id not null");
		System.out.println("$#9489#"); Validate.notNull(source.getContentType(),"Content type must be set on persistable image");

		
		System.out.println("$#9490#"); target.setDefaultImage(source.isDefaultImage());
		System.out.println("$#9491#"); target.setImageType(source.getImageType());
		System.out.println("$#9492#"); target.setProductImage(source.getName());
		System.out.println("$#9493#"); if(source.getImageUrl() != null) {
			System.out.println("$#9494#"); target.setProductImageUrl(source.getImageUrl());
		}
		System.out.println("$#9495#"); target.setProduct(product);
		
		System.out.println("$#9496#"); return target;
	}

	@Override
	protected ProductImage createTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	public Product getProduct() {
		System.out.println("$#9497#"); return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

}
