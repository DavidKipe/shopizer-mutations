package com.salesmanager.core.business.utils;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.catalog.product.attribute.ProductOption;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionDescription;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValue;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription;
import com.salesmanager.core.model.catalog.product.availability.ProductAvailability;


public class CatalogServiceHelper {
	
	/**
	 * Filters descriptions and set the appropriate language
	 * @param p
	 * @param language
	 */
	public static void setToLanguage(Product p, int language) {
		
		
	Set<ProductAttribute> attributes = p.getAttributes();
		System.out.println("$#3445#"); if(attributes!=null) {
			
			for(ProductAttribute attribute : attributes) {

				ProductOption po = attribute.getProductOption();
				Set<ProductOptionDescription> spod = po.getDescriptions();
				System.out.println("$#3446#"); if(spod!=null) {
					Set<ProductOptionDescription> podDescriptions = new HashSet<ProductOptionDescription>();
					for(ProductOptionDescription pod : spod) {
						//System.out.println("    ProductOptionDescription : " + pod.getProductOptionName());
						System.out.println("$#3447#"); if(pod.getLanguage().getId()==language) {
							podDescriptions.add(pod);
						}
					}
					System.out.println("$#3448#"); po.setDescriptions(podDescriptions);
				}
				
				ProductOptionValue pov = attribute.getProductOptionValue();
				
				
				Set<ProductOptionValueDescription> spovd = pov.getDescriptions();
				System.out.println("$#3449#"); if(spovd!=null) {
					Set<ProductOptionValueDescription> povdDescriptions = new HashSet();
					for(ProductOptionValueDescription povd : spovd) {
						System.out.println("$#3450#"); if(povd.getLanguage().getId()==language) {
							povdDescriptions.add(povd);
						}
					}
					System.out.println("$#3451#"); pov.setDescriptions(povdDescriptions);
				}
					
			}
		}
		
	}
	
	/**
	 * Overwrites the availability in order to return 1 price / region
	 * @param product
	 * @param locale
	 */
	public static void setToAvailability(Product product, Locale locale) {
		
		Set<ProductAvailability> availabilities =  product.getAvailabilities();
		
		ProductAvailability defaultAvailability = null;
		ProductAvailability localeAvailability = null;
		
		for(ProductAvailability availability : availabilities) {
			
			System.out.println("$#3452#"); if(availability.getRegion().equals(Constants.ALL_REGIONS)) {
				defaultAvailability = availability;
			} 
			System.out.println("$#3453#"); if(availability.getRegion().equals(locale.getCountry())) {
				localeAvailability = availability;
			}
			
		}
		
		System.out.println("$#3454#"); if(defaultAvailability!=null || localeAvailability!=null) {
			Set<ProductAvailability> productAvailabilities = new HashSet<ProductAvailability>();
			System.out.println("$#3456#"); if(defaultAvailability!=null) {
				productAvailabilities.add(defaultAvailability);
			}
			System.out.println("$#3457#"); if(localeAvailability!=null) {
				productAvailabilities.add(localeAvailability);
			}
			System.out.println("$#3458#"); product.setAvailabilities(productAvailabilities);
		}
		
	}

}
