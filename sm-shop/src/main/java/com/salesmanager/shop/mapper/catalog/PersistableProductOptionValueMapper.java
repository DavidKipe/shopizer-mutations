package com.salesmanager.shop.mapper.catalog;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValue;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.catalog.product.attribute.api.PersistableProductOptionValueEntity;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;

@Component
public class PersistableProductOptionValueMapper
		implements Mapper<PersistableProductOptionValueEntity, ProductOptionValue> {

	@Autowired
	private LanguageService languageService;

	ProductOptionValueDescription description(
			com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValueDescription description)
			throws Exception {
		System.out.println("$#8313#"); Validate.notNull(description.getLanguage(), "description.language should not be null");
		ProductOptionValueDescription desc = new ProductOptionValueDescription();
		System.out.println("$#8314#"); desc.setId(null);
		System.out.println("$#8315#"); desc.setDescription(description.getDescription());
		System.out.println("$#8316#"); desc.setName(description.getName());
		System.out.println("$#8318#"); System.out.println("$#8317#"); if (description.getId() != null && description.getId().longValue() > 0) {
			System.out.println("$#8320#"); desc.setId(description.getId());
		}
		Language lang = languageService.getByCode(description.getLanguage());
		System.out.println("$#8321#"); desc.setLanguage(lang);
		System.out.println("$#8322#"); return desc;
	}

	@Override
	public ProductOptionValue convert(PersistableProductOptionValueEntity source, ProductOptionValue destination,
			MerchantStore store, Language language) {
		System.out.println("$#8323#"); if (destination == null) {
			destination = new ProductOptionValue();
		}

		try {

			System.out.println("$#8324#"); if (!CollectionUtils.isEmpty(source.getDescriptions())) {
				for (com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValueDescription desc : source
						.getDescriptions()) {
					ProductOptionValueDescription description = null;
					System.out.println("$#8325#"); if (!CollectionUtils.isEmpty(destination.getDescriptions())) {
						for (ProductOptionValueDescription d : destination.getDescriptions()) {
							System.out.println("$#8326#"); if (!StringUtils.isBlank(desc.getLanguage())
									&& desc.getLanguage().equals(d.getLanguage().getCode())) {
								
																			System.out.println("$#8328#"); d.setDescription(desc.getDescription());
																			System.out.println("$#8329#"); d.setName(desc.getName());
																			System.out.println("$#8330#"); d.setTitle(desc.getTitle());
				            	  description = d;
				            	  break;

							}
						}
					} //else {
													System.out.println("$#8331#"); if(description == null) {
				          description = description(desc);
														System.out.println("$#8332#"); description.setProductOptionValue(destination);
				          destination.getDescriptions().add(description);
			          }
						//description = description(desc);
						//description.setProductOptionValue(destination);
					//}
					//destination.getDescriptions().add(description);
				}
			}

			System.out.println("$#8333#"); destination.setCode(source.getCode());
			System.out.println("$#8334#"); destination.setMerchantStore(store);
			System.out.println("$#8335#"); destination.setProductOptionValueSortOrder(source.getSortOrder());


			System.out.println("$#8336#"); return destination;
		} catch (Exception e) {
			throw new ServiceRuntimeException("Error while converting product option", e);
		}
	}

	@Override
	public ProductOptionValue convert(PersistableProductOptionValueEntity source, MerchantStore store,
			Language language) {
		ProductOptionValue destination = new ProductOptionValue();
		System.out.println("$#8337#"); return convert(source, destination, store, language);
	}


}