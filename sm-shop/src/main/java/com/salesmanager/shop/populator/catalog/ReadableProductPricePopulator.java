package com.salesmanager.shop.populator.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.Validate;
import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.catalog.product.price.FinalPrice;
import com.salesmanager.core.model.catalog.product.price.ProductPrice;
import com.salesmanager.core.model.catalog.product.price.ProductPriceDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.catalog.manufacturer.ReadableManufacturerFull;
import com.salesmanager.shop.model.catalog.product.ReadableProductPrice;
import com.salesmanager.shop.model.catalog.product.ReadableProductPriceFull;



public class ReadableProductPricePopulator extends
		AbstractDataPopulator<ProductPrice, ReadableProductPrice> {
	
	
	private PricingService pricingService;

	public PricingService getPricingService() {
		System.out.println("$#9929#"); return pricingService;
	}

	public void setPricingService(PricingService pricingService) {
		this.pricingService = pricingService;
	}

	@Override
	public ReadableProductPrice populate(ProductPrice source,
			ReadableProductPrice target, MerchantStore store, Language language)
			throws ConversionException {
		System.out.println("$#9930#"); Validate.notNull(pricingService,"pricingService must be set");
		System.out.println("$#9931#"); Validate.notNull(source.getProductAvailability(),"productPrice.availability cannot be null");
		System.out.println("$#9932#"); Validate.notNull(source.getProductAvailability().getProduct(),"productPrice.availability.product cannot be null");
		
		try {
		  
						System.out.println("$#9933#"); if(language == null) {
		      target = new ReadableProductPriceFull();
		    }
		    
						System.out.println("$#9935#"); System.out.println("$#9934#"); if(source.getId() != null && source.getId() > 0) {
								System.out.println("$#9937#"); target.setId(source.getId());
		    }
			
			FinalPrice finalPrice = pricingService.calculateProductPrice(source.getProductAvailability().getProduct());
			
			System.out.println("$#9938#"); target.setOriginalPrice(pricingService.getDisplayAmount(source.getProductPriceAmount(), store));
			System.out.println("$#9939#"); if(finalPrice.isDiscounted()) {
				System.out.println("$#9940#"); target.setDiscounted(true);
				System.out.println("$#9941#"); target.setFinalPrice(pricingService.getDisplayAmount(source.getProductPriceSpecialAmount(), store));
			} else {
				System.out.println("$#9942#"); target.setFinalPrice(pricingService.getDisplayAmount(finalPrice.getOriginalPrice(), store));
			}
			
						System.out.println("$#9944#"); System.out.println("$#9943#"); if(source.getDescriptions()!=null && source.getDescriptions().size()>0) {
		       List<com.salesmanager.shop.model.catalog.product.ProductPriceDescription> fulldescriptions = new ArrayList<com.salesmanager.shop.model.catalog.product.ProductPriceDescription>();
	            
               Set<ProductPriceDescription> descriptions = source.getDescriptions();
               ProductPriceDescription description = null;
               for(ProductPriceDescription desc : descriptions) {
																			System.out.println("$#9946#"); if(language != null && desc.getLanguage().getCode().equals(language.getCode())) {
                       description = desc;
                       break;
                   } else {
                     fulldescriptions.add(populateDescription(desc));
                   }
               }

               
															System.out.println("$#9948#"); if (description != null) {
                   com.salesmanager.shop.model.catalog.product.ProductPriceDescription d = populateDescription(description);
																			System.out.println("$#9949#"); target.setDescription(d);
               }
               
															System.out.println("$#9950#"); if(target instanceof ReadableProductPriceFull) {
																	System.out.println("$#9951#"); ((ReadableProductPriceFull)target).setDescriptions(fulldescriptions);
               }
		    }


		} catch(Exception e) {
			throw new ConversionException("Exception while converting to ReadableProductPrice",e);
		}
		
		
		
		System.out.println("$#9952#"); return target;
	}

	@Override
	protected ReadableProductPrice createTarget() {
		// TODO Auto-generated method stub
		return null;
	}
	
	com.salesmanager.shop.model.catalog.product.ProductPriceDescription populateDescription(
	      ProductPriceDescription description) {
					System.out.println("$#9953#"); if (description == null) {
	      return null;
	    }
	    com.salesmanager.shop.model.catalog.product.ProductPriceDescription d =
	        new com.salesmanager.shop.model.catalog.product.ProductPriceDescription();
					System.out.println("$#9954#"); d.setName(description.getName());
					System.out.println("$#9955#"); d.setDescription(description.getDescription());
					System.out.println("$#9956#"); d.setId(description.getId());
					System.out.println("$#9957#"); d.setTitle(description.getTitle());
					System.out.println("$#9958#"); if (description.getLanguage() != null) {
							System.out.println("$#9959#"); d.setLanguage(description.getLanguage().getCode());
	    }
					System.out.println("$#9960#"); return d;
	 }

}
