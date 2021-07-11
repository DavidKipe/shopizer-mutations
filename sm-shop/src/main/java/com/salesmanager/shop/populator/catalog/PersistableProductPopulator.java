package com.salesmanager.shop.populator.catalog;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.salesmanager.core.business.constants.Constants;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.catalog.category.CategoryService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductOptionService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductOptionValueService;
import com.salesmanager.core.business.services.catalog.product.manufacturer.ManufacturerService;
import com.salesmanager.core.business.services.catalog.product.type.ProductTypeService;
import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.tax.TaxClassService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.catalog.product.availability.ProductAvailability;
import com.salesmanager.core.model.catalog.product.description.ProductDescription;
import com.salesmanager.core.model.catalog.product.image.ProductImage;
import com.salesmanager.core.model.catalog.product.manufacturer.Manufacturer;
import com.salesmanager.core.model.catalog.product.price.ProductPrice;
import com.salesmanager.core.model.catalog.product.price.ProductPriceDescription;
import com.salesmanager.core.model.catalog.product.type.ProductType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.catalog.PersistableProductAttributeMapper;
import com.salesmanager.shop.model.catalog.product.PersistableImage;
import com.salesmanager.shop.model.catalog.product.PersistableProduct;
import com.salesmanager.shop.model.catalog.product.ProductPriceEntity;
import com.salesmanager.shop.utils.DateUtil;


@Component
public class PersistableProductPopulator extends
		AbstractDataPopulator<PersistableProduct, Product> {
	
	@Inject
	private CategoryService categoryService;
	@Inject
	private ManufacturerService manufacturerService;
	@Inject
	private TaxClassService taxClassService;
	@Inject
	private LanguageService languageService;
	@Inject
	private ProductOptionService productOptionService;
	@Inject
	private ProductOptionValueService productOptionValueService;
	@Inject
	private CustomerService customerService;
	@Autowired
	private PersistableProductAttributeMapper persistableProductAttributeMapper;
	
	@Autowired
	private ProductTypeService productTypeService;

	



	@Override
	public Product populate(PersistableProduct source,
			Product target, MerchantStore store, Language language)
			throws ConversionException {
	  
					System.out.println("$#9524#"); Validate.notNull(target,"Product must not be null");

		try {

			System.out.println("$#9525#"); target.setSku(source.getSku());
			System.out.println("$#9526#"); target.setAvailable(source.isAvailable());
			System.out.println("$#9527#"); target.setPreOrder(source.isPreOrder());
			System.out.println("$#9528#"); target.setRefSku(source.getRefSku());
			System.out.println("$#9529#"); if(source.getId() != null && source.getId().longValue()==0) {
				System.out.println("$#9531#"); target.setId(null);
			} else {
				System.out.println("$#9532#"); target.setId(source.getId());
			}
			
			System.out.println("$#9533#"); target.setCondition(source.getCondition());
			
			
			//PRODUCT TYPE
			System.out.println("$#9534#"); if(!StringUtils.isBlank(source.getType())) {
				ProductType type = productTypeService.getByCode(source.getType(), store, language);
				System.out.println("$#9535#"); if(type == null) {
					throw new ConversionException("Product type [" + source.getType() + "] does not exist");
				}

				System.out.println("$#9536#"); target.setType(type);
			}
			
			
			//RENTAL
			System.out.println("$#9537#"); target.setRentalDuration(source.getRentalDuration());
			System.out.println("$#9538#"); target.setRentalStatus(source.getRentalStatus());
			System.out.println("$#9539#"); target.setRentalPeriod(source.getRentalPeriod());
			
			/** end RENTAL **/
			
			System.out.println("$#9540#"); if(source.getOwner()!=null && source.getOwner().getId()!=null) {
				com.salesmanager.core.model.customer.Customer owner = customerService.getById(source.getOwner().getId());
				System.out.println("$#9542#"); target.setOwner(owner);
			}
			
			System.out.println("$#9543#"); if(!StringUtils.isBlank(source.getDateAvailable())) {
				System.out.println("$#9544#"); target.setDateAvailable(DateUtil.getDate(source.getDateAvailable()));
			}


			
			System.out.println("$#9545#"); target.setMerchantStore(store);
			
			List<Language> languages = new ArrayList<Language>();
			Set<ProductDescription> descriptions = new HashSet<ProductDescription>();
			System.out.println("$#9546#"); if(!CollectionUtils.isEmpty(source.getDescriptions())) {
				for(com.salesmanager.shop.model.catalog.product.ProductDescription description : source.getDescriptions()) {
					
				  ProductDescription productDescription = new ProductDescription();
				  Language lang = languageService.getByCode(description.getLanguage());
															System.out.println("$#9547#"); if(lang==null) {
	                    throw new ConversionException("Language code " + description.getLanguage() + " is invalid, use ISO code (en, fr ...)");
	               }
							System.out.println("$#9548#"); if(!CollectionUtils.isEmpty(target.getDescriptions())) {
				      for(ProductDescription desc : target.getDescriptions()) {
												System.out.println("$#9549#"); if(desc.getLanguage().getCode().equals(description.getLanguage())) {
				          productDescription = desc;
				          break;
				        }
				      }
				    }

					System.out.println("$#9550#"); productDescription.setProduct(target);
					System.out.println("$#9551#"); productDescription.setDescription(description.getDescription());

					System.out.println("$#9552#"); productDescription.setProductHighlight(description.getHighlights());

					System.out.println("$#9553#"); productDescription.setName(description.getName());
					System.out.println("$#9554#"); productDescription.setSeUrl(description.getFriendlyUrl());
					System.out.println("$#9555#"); productDescription.setMetatagKeywords(description.getKeyWords());
					System.out.println("$#9556#"); productDescription.setMetatagDescription(description.getMetaDescription());
					System.out.println("$#9557#"); productDescription.setTitle(description.getTitle());
					
					languages.add(lang);
					System.out.println("$#9558#"); productDescription.setLanguage(lang);
					descriptions.add(productDescription);
				}
			}
			
			System.out.println("$#9560#"); System.out.println("$#9559#"); if(descriptions.size()>0) {
				System.out.println("$#9561#"); target.setDescriptions(descriptions);
			}

			System.out.println("$#9562#"); if(source.getProductSpecifications()!=null) {
							System.out.println("$#9563#"); target.setProductHeight(source.getProductSpecifications().getHeight());
							System.out.println("$#9564#"); target.setProductLength(source.getProductSpecifications().getLength());
							System.out.println("$#9565#"); target.setProductWeight(source.getProductSpecifications().getWeight());
							System.out.println("$#9566#"); target.setProductWidth(source.getProductSpecifications().getWidth());
    			
    			
														System.out.println("$#9567#"); if(source.getProductSpecifications().getManufacturer()!=null) {
                   
                   Manufacturer manuf = null;
																			System.out.println("$#9568#"); if(!StringUtils.isBlank(source.getProductSpecifications().getManufacturer())) {
                       manuf = manufacturerService.getByCode(store, source.getProductSpecifications().getManufacturer());
                   } 
                   
																			System.out.println("$#9569#"); if(manuf==null) {
                       throw new ConversionException("Invalid manufacturer id");
                   }
																			System.out.println("$#9570#"); if(manuf!=null) {
																							System.out.println("$#9571#"); if(manuf.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
                           throw new ConversionException("Invalid manufacturer id");
                       }
																							System.out.println("$#9572#"); target.setManufacturer(manuf);
                   }
               }
    			
			}
			System.out.println("$#9573#"); target.setSortOrder(source.getSortOrder());
			System.out.println("$#9574#"); target.setProductVirtual(source.isProductVirtual());
			System.out.println("$#9575#"); target.setProductShipeable(source.isProductShipeable());
			System.out.println("$#9576#"); if(source.getRating() != null) {
				System.out.println("$#9577#"); target.setProductReviewAvg(new BigDecimal(source.getRating()));
			}
			System.out.println("$#9578#"); target.setProductReviewCount(source.getRatingCount());
			
			System.out.println("$#9579#"); if(CollectionUtils.isNotEmpty(source.getProductPrices())) {

				//get product availability
			  
			    //create new ProductAvailability
			    ProductAvailability productAvailability = new ProductAvailability(target, store);

			    //todo now support for specific regions
							System.out.println("$#9580#"); productAvailability.setRegion(Constants.ALL_REGIONS);

				System.out.println("$#9581#"); productAvailability.setProductQuantity(source.getQuantity());
				System.out.println("$#9582#"); productAvailability.setProductQuantityOrderMin(1);
				System.out.println("$#9583#"); productAvailability.setProductQuantityOrderMax(1);
				System.out.println("$#9584#"); productAvailability.setAvailable(Boolean.valueOf(target.isAvailable()));
				
				for(com.salesmanager.shop.model.catalog.product.PersistableProductPrice priceEntity : source.getProductPrices()) {
					
					ProductPrice price = new ProductPrice();
					System.out.println("$#9585#"); price.setProductAvailability(productAvailability);
					System.out.println("$#9586#"); price.setDefaultPrice(priceEntity.isDefaultPrice());
					System.out.println("$#9587#"); price.setProductPriceAmount(priceEntity.getOriginalPrice());
					System.out.println("$#9588#"); price.setCode(priceEntity.getCode());
					System.out.println("$#9589#"); price.setProductPriceSpecialAmount(priceEntity.getDiscountedPrice());
					System.out.println("$#9590#"); if(priceEntity.getDiscountStartDate()!=null) {
						Date startDate = DateUtil.getDate(priceEntity.getDiscountStartDate());
						System.out.println("$#9591#"); price.setProductPriceSpecialStartDate(startDate);
					}
					System.out.println("$#9592#"); if(priceEntity.getDiscountEndDate()!=null) {
						Date endDate = DateUtil.getDate(priceEntity.getDiscountEndDate());
						System.out.println("$#9593#"); price.setProductPriceSpecialEndDate(endDate);
					}
					productAvailability.getPrices().add(price);
					target.getAvailabilities().add(productAvailability);
					for(Language lang : languages) {
						ProductPriceDescription ppd = new ProductPriceDescription();
						System.out.println("$#9594#"); ppd.setProductPrice(price);
						System.out.println("$#9595#"); ppd.setLanguage(lang);
						System.out.println("$#9596#"); ppd.setName(ProductPriceDescription.DEFAULT_PRICE_DESCRIPTION);
						
						//price appender
						Optional<com.salesmanager.shop.model.catalog.product.ProductPriceDescription> description = priceEntity.getDescriptions().stream().filter(d -> d.getLanguage()!= null && d.getLanguage().equals(lang.getCode())).findFirst();
						System.out.println("$#9600#"); if(description.isPresent()) {
							System.out.println("$#9601#"); ppd.setPriceAppender(description.get().getPriceAppender());
						}
						price.getDescriptions().add(ppd);
					}
				}

			} else { //create 
			  
			    ProductAvailability productAvailability = null;
			    ProductPrice defaultPrice = null;
							System.out.println("$#9602#"); if(!CollectionUtils.isEmpty(target.getAvailabilities())) {
			      for(ProductAvailability avail : target.getAvailabilities()) {
    			        Set<ProductPrice> prices = avail.getPrices();
    			        for(ProductPrice p : prices) {
																	System.out.println("$#9603#"); if(p.isDefaultPrice()) {
																			System.out.println("$#9604#"); if(productAvailability == null) {
    			              productAvailability = avail;
    			              defaultPrice = p;
    			              break;
    			            }
																			System.out.println("$#9605#"); p.setDefaultPrice(false);
    			          }
    			        }
			      }
			    }
				
							System.out.println("$#9606#"); if(productAvailability == null) {
			      productAvailability = new ProductAvailability(target, store);
			      target.getAvailabilities().add(productAvailability);
			    }

				System.out.println("$#9607#"); productAvailability.setProductQuantity(source.getQuantity());
				System.out.println("$#9608#"); productAvailability.setProductQuantityOrderMin(1);
				System.out.println("$#9609#"); productAvailability.setProductQuantityOrderMax(1);
				System.out.println("$#9610#"); productAvailability.setRegion(Constants.ALL_REGIONS);
				System.out.println("$#9611#"); productAvailability.setAvailable(Boolean.valueOf(target.isAvailable()));


				System.out.println("$#9612#"); if(defaultPrice != null) {
						System.out.println("$#9613#"); defaultPrice.setProductPriceAmount(source.getPrice());
				} else {
				    defaultPrice = new ProductPrice();
								System.out.println("$#9614#"); defaultPrice.setDefaultPrice(true);
								System.out.println("$#9615#"); defaultPrice.setProductPriceAmount(source.getPrice());
								System.out.println("$#9616#"); defaultPrice.setCode(ProductPriceEntity.DEFAULT_PRICE_CODE);
								System.out.println("$#9617#"); defaultPrice.setProductAvailability(productAvailability);
	                productAvailability.getPrices().add(defaultPrice);
	                for(Language lang : languages) {
	                
                      ProductPriceDescription ppd = new ProductPriceDescription();
																						System.out.println("$#9618#"); ppd.setProductPrice(defaultPrice);
																						System.out.println("$#9619#"); ppd.setLanguage(lang);
																						System.out.println("$#9620#"); ppd.setName(ProductPriceDescription.DEFAULT_PRICE_DESCRIPTION);
                      defaultPrice.getDescriptions().add(ppd);
                    }
				}

				
				
			}

			//image
			System.out.println("$#9621#"); if(source.getImages()!=null) {
				for(PersistableImage img : source.getImages()) {
					ByteArrayInputStream in = new ByteArrayInputStream(img.getBytes());
					ProductImage productImage = new ProductImage();
					System.out.println("$#9622#"); productImage.setProduct(target);
					System.out.println("$#9623#"); productImage.setProductImage(img.getName());
					System.out.println("$#9624#"); productImage.setImage(in);
					target.getImages().add(productImage);
				}
			}
			
			//attributes
			System.out.println("$#9625#"); if(source.getAttributes()!=null) {
				for(com.salesmanager.shop.model.catalog.product.attribute.PersistableProductAttribute attr : source.getAttributes()) {
					ProductAttribute attribute = persistableProductAttributeMapper.convert(attr, store, language);
					
					System.out.println("$#9626#"); attribute.setProduct(target);
					target.getAttributes().add(attribute);

				}
			}

			
			//categories
			System.out.println("$#9627#"); if(!CollectionUtils.isEmpty(source.getCategories())) {
				for(com.salesmanager.shop.model.catalog.category.Category categ : source.getCategories()) {
					
					Category c = null;
					System.out.println("$#9628#"); if(!StringUtils.isBlank(categ.getCode())) {
						c = categoryService.getByCode(store, categ.getCode());
					} else {
						System.out.println("$#9629#"); Validate.notNull(categ.getId(), "Category id nust not be null");
						c = categoryService.getById(categ.getId(), store.getId());
					}
					
					System.out.println("$#9630#"); if(c==null) {
						throw new ConversionException("Category id " + categ.getId() + " does not exist");
					}
					System.out.println("$#9631#"); if(c.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
						throw new ConversionException("Invalid category id");
					}
					target.getCategories().add(c);
				}
			}
			System.out.println("$#9632#"); return target;
		
		} catch (Exception e) {
			throw new ConversionException(e);
		}
	}



	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	public CategoryService getCategoryService() {
		System.out.println("$#9633#"); return categoryService;
	}

	public void setManufacturerService(ManufacturerService manufacturerService) {
		this.manufacturerService = manufacturerService;
	}

	public ManufacturerService getManufacturerService() {
		System.out.println("$#9634#"); return manufacturerService;
	}

	public void setTaxClassService(TaxClassService taxClassService) {
		this.taxClassService = taxClassService;
	}

	public TaxClassService getTaxClassService() {
		System.out.println("$#9635#"); return taxClassService;
	}


	public LanguageService getLanguageService() {
		System.out.println("$#9636#"); return languageService;
	}

	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}

	public ProductOptionService getProductOptionService() {
		System.out.println("$#9637#"); return productOptionService;
	}

	public void setProductOptionService(ProductOptionService productOptionService) {
		this.productOptionService = productOptionService;
	}

	public ProductOptionValueService getProductOptionValueService() {
		System.out.println("$#9638#"); return productOptionValueService;
	}

	public void setProductOptionValueService(
			ProductOptionValueService productOptionValueService) {
		this.productOptionValueService = productOptionValueService;
	}


	@Override
	protected Product createTarget() {
		return null;
	}



	public CustomerService getCustomerService() {
		System.out.println("$#9639#"); return customerService;
	}



	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

}
