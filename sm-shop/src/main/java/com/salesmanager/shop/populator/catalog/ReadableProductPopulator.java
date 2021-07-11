package com.salesmanager.shop.populator.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionDescription;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValue;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription;
import com.salesmanager.core.model.catalog.product.availability.ProductAvailability;
import com.salesmanager.core.model.catalog.product.description.ProductDescription;
import com.salesmanager.core.model.catalog.product.image.ProductImage;
import com.salesmanager.core.model.catalog.product.manufacturer.ManufacturerDescription;
import com.salesmanager.core.model.catalog.product.price.FinalPrice;
import com.salesmanager.core.model.catalog.product.price.ProductPrice;
import com.salesmanager.core.model.catalog.product.price.ProductPriceDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.catalog.category.ReadableCategory;
import com.salesmanager.shop.model.catalog.manufacturer.ReadableManufacturer;
import com.salesmanager.shop.model.catalog.product.ProductSpecification;
import com.salesmanager.shop.model.catalog.product.ReadableImage;
import com.salesmanager.shop.model.catalog.product.ReadableProduct;
import com.salesmanager.shop.model.catalog.product.ReadableProductFull;
import com.salesmanager.shop.model.catalog.product.ReadableProductPrice;
import com.salesmanager.shop.model.catalog.product.RentalOwner;
import com.salesmanager.shop.model.catalog.product.attribute.ReadableProductAttribute;
import com.salesmanager.shop.model.catalog.product.attribute.ReadableProductAttributeValue;
import com.salesmanager.shop.model.catalog.product.attribute.ReadableProductOption;
import com.salesmanager.shop.model.catalog.product.attribute.api.ReadableProductOptionValueEntity;
import com.salesmanager.shop.model.catalog.product.type.ReadableProductType;
import com.salesmanager.shop.utils.DateUtil;
import com.salesmanager.shop.utils.ImageFilePath;



public class ReadableProductPopulator extends
		AbstractDataPopulator<Product, ReadableProduct> {
	
	private PricingService pricingService;
	
	private ImageFilePath imageUtils;

	public ImageFilePath getimageUtils() {
		System.out.println("$#9703#"); return imageUtils;
	}

	public void setimageUtils(ImageFilePath imageUtils) {
		this.imageUtils = imageUtils;
	}

	public PricingService getPricingService() {
		System.out.println("$#9704#"); return pricingService;
	}

	public void setPricingService(PricingService pricingService) {
		this.pricingService = pricingService;
	}

	@Override
	public ReadableProduct populate(Product source,
			ReadableProduct target, MerchantStore store, Language language)
			throws ConversionException {
		System.out.println("$#9705#"); Validate.notNull(pricingService, "Requires to set PricingService");
		System.out.println("$#9706#"); Validate.notNull(imageUtils, "Requires to set imageUtils");

		
		try {
		  
	        List<com.salesmanager.shop.model.catalog.product.ProductDescription> fulldescriptions = new ArrayList<com.salesmanager.shop.model.catalog.product.ProductDescription>();
									System.out.println("$#9707#"); if(language == null) {
	          target = new ReadableProductFull();
	        }

									System.out.println("$#9708#"); if(target==null) {
	        	target = new ReadableProduct();
	        }

	        ProductDescription description = source.getProductDescription();
	        
									System.out.println("$#9710#"); System.out.println("$#9709#"); if(source.getDescriptions()!=null && source.getDescriptions().size()>0) {
	          for(ProductDescription desc : source.getDescriptions()) {
																System.out.println("$#9712#"); if(language != null && desc.getLanguage()!=null && desc.getLanguage().getId().intValue() == language.getId().intValue()) {
                    description = desc;
                    break;
                } else {
                  fulldescriptions.add(populateDescription(desc));
                }
              }
	        }
	        
							System.out.println("$#9715#"); if(target instanceof ReadableProductFull) {
												System.out.println("$#9716#"); ((ReadableProductFull)target).setDescriptions(fulldescriptions);
		      }
		     
										System.out.println("$#9717#"); if(language == null) {
			          language = store.getDefaultLanguage();
			    }

		   final Language lang = language;
	
			System.out.println("$#9718#"); target.setId(source.getId());
			System.out.println("$#9719#"); target.setAvailable(source.isAvailable());
			System.out.println("$#9720#"); target.setProductShipeable(source.isProductShipeable());
			
			ProductSpecification specifications = new ProductSpecification();
			System.out.println("$#9721#"); specifications.setHeight(source.getProductHeight());
			System.out.println("$#9722#"); specifications.setLength(source.getProductLength());
			System.out.println("$#9723#"); specifications.setWeight(source.getProductWeight());
			System.out.println("$#9724#"); specifications.setWidth(source.getProductWidth());
			System.out.println("$#9725#"); target.setProductSpecifications(specifications);
			

			System.out.println("$#9726#"); target.setPreOrder(source.isPreOrder());
			System.out.println("$#9727#"); target.setRefSku(source.getRefSku());
			System.out.println("$#9728#"); target.setSortOrder(source.getSortOrder());
			
			
			System.out.println("$#9729#"); target.setCondition(source.getCondition());
			
			System.out.println("$#9730#"); if(source.getType() != null) {
				ReadableProductType readableType = new ReadableProductType();
				System.out.println("$#9731#"); readableType.setCode(source.getType().getCode());
				System.out.println("$#9732#"); readableType.setName(source.getType().getCode());
				System.out.println("$#9733#"); target.setType(readableType);
			}
			
			
			//RENTAL
			System.out.println("$#9734#"); if(source.getRentalDuration()!=null) {
				System.out.println("$#9735#"); target.setRentalDuration(source.getRentalDuration());
			}
			System.out.println("$#9736#"); if(source.getRentalPeriod()!=null) {
				System.out.println("$#9737#"); target.setRentalPeriod(source.getRentalPeriod());
			}
			System.out.println("$#9738#"); target.setRentalStatus(source.getRentalStatus());
			
			/**
			 * END RENTAL
			 */
			
			System.out.println("$#9739#"); if(source.getOwner() != null) {
				RentalOwner owner = new RentalOwner();
				System.out.println("$#9740#"); owner.setId(source.getOwner().getId());
				System.out.println("$#9741#"); owner.setEmailAddress(source.getOwner().getEmailAddress());
				System.out.println("$#9742#"); owner.setFirstName(source.getOwner().getBilling().getFirstName());
				System.out.println("$#9743#"); owner.setLastName(source.getOwner().getBilling().getLastName());
				com.salesmanager.shop.model.customer.address.Address address = new com.salesmanager.shop.model.customer.address.Address();
				System.out.println("$#9744#"); address.setAddress(source.getOwner().getBilling().getAddress());
				System.out.println("$#9745#"); address.setBillingAddress(true);
				System.out.println("$#9746#"); address.setCity(source.getOwner().getBilling().getCity());
				System.out.println("$#9747#"); address.setCompany(source.getOwner().getBilling().getCompany());
				System.out.println("$#9748#"); address.setCountry(source.getOwner().getBilling().getCountry().getIsoCode());
				System.out.println("$#9749#"); address.setZone(source.getOwner().getBilling().getZone().getCode());
				System.out.println("$#9750#"); address.setLatitude(source.getOwner().getBilling().getLatitude());
				System.out.println("$#9751#"); address.setLongitude(source.getOwner().getBilling().getLongitude());
				System.out.println("$#9752#"); address.setPhone(source.getOwner().getBilling().getTelephone());
				System.out.println("$#9753#"); address.setPostalCode(source.getOwner().getBilling().getPostalCode());
				System.out.println("$#9754#"); owner.setAddress(address);
				System.out.println("$#9755#"); target.setOwner(owner);
			}
			
			
			System.out.println("$#9756#"); if(source.getDateAvailable() != null) {
				System.out.println("$#9757#"); target.setDateAvailable(DateUtil.formatDate(source.getDateAvailable()));
			}
			
			System.out.println("$#9758#"); if(source.getAuditSection()!=null) {
					System.out.println("$#9759#"); target.setCreationDate(DateUtil.formatDate(source.getAuditSection().getDateCreated()));
			}
			
			System.out.println("$#9760#"); if(source.getProductReviewAvg()!=null) {
				double avg = source.getProductReviewAvg().doubleValue();
				System.out.println("$#9762#"); System.out.println("$#9761#"); double rating = Math.round(avg * 2) / 2.0f;
				System.out.println("$#9763#"); target.setRating(rating);
			}
			System.out.println("$#9764#"); target.setProductVirtual(source.getProductVirtual());
			System.out.println("$#9765#"); if(source.getProductReviewCount()!=null) {
				System.out.println("$#9766#"); target.setRatingCount(source.getProductReviewCount().intValue());
			}
			System.out.println("$#9767#"); if(description!=null) {
			    com.salesmanager.shop.model.catalog.product.ProductDescription tragetDescription = populateDescription(description);
				System.out.println("$#9768#"); target.setDescription(tragetDescription);
				
			}
			
			System.out.println("$#9769#"); if(source.getManufacturer()!=null) {
				ManufacturerDescription manufacturer = source.getManufacturer().getDescriptions().iterator().next(); 
				ReadableManufacturer manufacturerEntity = new ReadableManufacturer();
				com.salesmanager.shop.model.catalog.manufacturer.ManufacturerDescription d = new com.salesmanager.shop.model.catalog.manufacturer.ManufacturerDescription(); 
				System.out.println("$#9770#"); d.setName(manufacturer.getName());
				System.out.println("$#9771#"); manufacturerEntity.setDescription(d);
				System.out.println("$#9772#"); manufacturerEntity.setId(source.getManufacturer().getId());
				System.out.println("$#9773#"); manufacturerEntity.setOrder(source.getManufacturer().getOrder());
				System.out.println("$#9774#"); manufacturerEntity.setCode(source.getManufacturer().getCode());
				System.out.println("$#9775#"); target.setManufacturer(manufacturerEntity);
			}
			
			System.out.println("$#9776#"); if(source.getType() != null) {
			  ReadableProductType type = new ReadableProductType();
					System.out.println("$#9777#"); type.setId(source.getType().getId());
					System.out.println("$#9778#"); type.setCode(source.getType().getCode());
					System.out.println("$#9779#"); type.setName(source.getType().getCode());//need name
					System.out.println("$#9780#"); target.setType(type);
			}
			
			Set<ProductImage> images = source.getImages();
			System.out.println("$#9782#"); System.out.println("$#9781#"); if(images!=null && images.size()>0) {
				List<ReadableImage> imageList = new ArrayList<ReadableImage>();
				
				String contextPath = imageUtils.getContextPath();
				
				for(ProductImage img : images) {
					ReadableImage prdImage = new ReadableImage();
					System.out.println("$#9784#"); prdImage.setImageName(img.getProductImage());
					System.out.println("$#9785#"); prdImage.setDefaultImage(img.isDefaultImage());

					StringBuilder imgPath = new StringBuilder();
					imgPath.append(contextPath).append(imageUtils.buildProductImageUtils(store, source.getSku(), img.getProductImage()));

					System.out.println("$#9786#"); prdImage.setImageUrl(imgPath.toString());
					System.out.println("$#9787#"); prdImage.setId(img.getId());
					System.out.println("$#9788#"); prdImage.setImageType(img.getImageType());
					System.out.println("$#9789#"); if(img.getProductImageUrl()!=null){
						System.out.println("$#9790#"); prdImage.setExternalUrl(img.getProductImageUrl());
					}
					System.out.println("$#9791#"); if(img.getImageType()==1 && img.getProductImageUrl()!=null) {//video
						System.out.println("$#9793#"); prdImage.setVideoUrl(img.getProductImageUrl());
					}
					
					System.out.println("$#9794#"); if(prdImage.isDefaultImage()) {
						System.out.println("$#9795#"); target.setImage(prdImage);
					}
					
					imageList.add(prdImage);
				}
				target
				.setImages(imageList);
			}
			
			System.out.println("$#9797#"); if(!CollectionUtils.isEmpty(source.getCategories())) {
				
				ReadableCategoryPopulator categoryPopulator = new ReadableCategoryPopulator();
				List<ReadableCategory> categoryList = new ArrayList<ReadableCategory>();
				
				for(Category category : source.getCategories()) {
					
					ReadableCategory readableCategory = new ReadableCategory();
					categoryPopulator.populate(category, readableCategory, store, language);
					categoryList.add(readableCategory);
					
				}
				
				System.out.println("$#9798#"); target.setCategories(categoryList);
				
			}
			
			System.out.println("$#9799#"); if(!CollectionUtils.isEmpty(source.getAttributes())) {
			
				Set<ProductAttribute> attributes = source.getAttributes();
				

				//split read only and options
				Map<Long,ReadableProductAttribute> readOnlyAttributes = null;
				Map<Long,ReadableProductOption> selectableOptions = null;
				
				System.out.println("$#9800#"); if(!CollectionUtils.isEmpty(attributes)) {
								
					for(ProductAttribute attribute : attributes) {
							ReadableProductOption opt = null;
							ReadableProductAttribute attr = null;
							ReadableProductOptionValueEntity optValue = new ReadableProductOptionValueEntity();
							ReadableProductAttributeValue attrValue = new ReadableProductAttributeValue();
							
							ProductOptionValue optionValue = attribute.getProductOptionValue();
							
							System.out.println("$#9801#"); if(attribute.getAttributeDisplayOnly()) {//read only attribute
								System.out.println("$#9802#"); if(readOnlyAttributes==null) {
									readOnlyAttributes = new TreeMap<Long,ReadableProductAttribute>();
								}
								attr = readOnlyAttributes.get(attribute.getProductOption().getId());
								System.out.println("$#9803#"); if(attr==null) {
									attr = createAttribute(attribute, language);
								}
								System.out.println("$#9804#"); if(attr!=null) {
									readOnlyAttributes.put(attribute.getProductOption().getId(), attr);
								}
								
								
								System.out.println("$#9805#"); attrValue.setDefaultValue(attribute.getAttributeDefault());
								System.out.println("$#9806#"); if(attribute.getProductOptionValue()!=null) {
										System.out.println("$#9807#"); attrValue.setId(attribute.getProductOptionValue().getId());//id of the option value
								} else {
										System.out.println("$#9808#"); attrValue.setId(attribute.getId());
								}
								System.out.println("$#9809#"); attrValue.setLang(language.getCode());


								System.out.println("$#9810#"); attrValue.setSortOrder(0);
								System.out.println("$#9811#"); if(attribute.getProductOptionSortOrder()!=null) {
									System.out.println("$#9812#"); attrValue.setSortOrder(attribute.getProductOptionSortOrder().intValue());
								}
								
								List<ProductOptionValueDescription> podescriptions = optionValue.getDescriptionsSettoList();
								ProductOptionValueDescription podescription = null;
								System.out.println("$#9814#"); System.out.println("$#9813#"); if(podescriptions!=null && podescriptions.size()>0) {
									podescription = podescriptions.get(0);
									System.out.println("$#9817#"); System.out.println("$#9816#"); if(podescriptions.size()>1) {
										for(ProductOptionValueDescription optionValueDescription : podescriptions) {
											System.out.println("$#9818#"); if(optionValueDescription.getLanguage().getId().intValue()==language.getId().intValue()) {
												podescription = optionValueDescription;
												break;
											}
										}
									}
								}
								System.out.println("$#9819#"); attrValue.setName(podescription.getName());
								System.out.println("$#9820#"); attrValue.setDescription(podescription.getDescription());
								
								System.out.println("$#9821#"); if(attr!=null) {
									attr.getAttributeValues().add(attrValue);
								}
								
								
							} else {//selectable option
								
								System.out.println("$#9822#"); if(selectableOptions==null) {
									selectableOptions = new TreeMap<Long,ReadableProductOption>();
								}
								opt = selectableOptions.get(attribute.getProductOption().getId());
								System.out.println("$#9823#"); if(opt==null) {
									opt = createOption(attribute, language);
								}
								System.out.println("$#9824#"); if(opt!=null) {
									selectableOptions.put(attribute.getProductOption().getId(), opt);
								}
								
								System.out.println("$#9825#"); optValue.setDefaultValue(attribute.getAttributeDefault());
								System.out.println("$#9826#"); optValue.setId(attribute.getProductOptionValue().getId());
								System.out.println("$#9827#"); optValue.setCode(attribute.getProductOptionValue().getCode());
								com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValueDescription valueDescription = new com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValueDescription();
								System.out.println("$#9828#"); valueDescription.setLanguage(language.getCode());
								//optValue.setLang(language.getCode());
								System.out.println("$#9830#"); System.out.println("$#9829#"); if(attribute.getProductAttributePrice()!=null && attribute.getProductAttributePrice().doubleValue()>0) {
									String formatedPrice = pricingService.getDisplayAmount(attribute.getProductAttributePrice(), store);
									System.out.println("$#9832#"); optValue.setPrice(formatedPrice);
								}
								
								System.out.println("$#9833#"); if(!StringUtils.isBlank(attribute.getProductOptionValue().getProductOptionValueImage())) {
									System.out.println("$#9834#"); optValue.setImage(imageUtils.buildProductPropertyImageUtils(store, attribute.getProductOptionValue().getProductOptionValueImage()));
								}
								System.out.println("$#9835#"); optValue.setSortOrder(0);
								System.out.println("$#9836#"); if(attribute.getProductOptionSortOrder()!=null) {
									System.out.println("$#9837#"); optValue.setSortOrder(attribute.getProductOptionSortOrder().intValue());
								}
								
								List<ProductOptionValueDescription> podescriptions = optionValue.getDescriptionsSettoList();
								ProductOptionValueDescription podescription = null;
								System.out.println("$#9839#"); System.out.println("$#9838#"); if(podescriptions!=null && podescriptions.size()>0) {
									podescription = podescriptions.get(0);
									System.out.println("$#9842#"); System.out.println("$#9841#"); if(podescriptions.size()>1) {
										for(ProductOptionValueDescription optionValueDescription : podescriptions) {
											System.out.println("$#9843#"); if(optionValueDescription.getLanguage().getId().intValue()==language.getId().intValue()) {
												podescription = optionValueDescription;
												break;
											}
										}
									}
								}
								System.out.println("$#9844#"); valueDescription.setName(podescription.getName());
								System.out.println("$#9845#"); valueDescription.setDescription(podescription.getDescription());
								System.out.println("$#9846#"); optValue.setDescription(valueDescription);
								
								System.out.println("$#9847#"); if(opt!=null) {
									opt.getOptionValues().add(optValue);
								}
							}

						}
						
					}
				
				System.out.println("$#9848#"); if(selectableOptions != null) {
					List<ReadableProductOption> options = new ArrayList<ReadableProductOption>(selectableOptions.values());
					System.out.println("$#9849#"); target.setOptions(options);
				}

			
			}
			

			
			//remove products from invisible category -> set visible = false
/*			Set<Category> categories = source.getCategories();
			boolean isVisible = true;
			if(!CollectionUtils.isEmpty(categories)) {
				for(Category c : categories) {
					if(c.isVisible()) {
						isVisible = true;
						break;
					} else {
						isVisible = false;
					}
				}
			}*/
			
			//target.setVisible(isVisible);
			
			//availability
			ProductAvailability availability = null;
			for(ProductAvailability a : source.getAvailabilities()) {
				//TODO validate region
				//if(availability.getRegion().equals(Constants.ALL_REGIONS)) {//TODO REL 2.1 accept a region
					availability = a;
					System.out.println("$#9851#"); target.setQuantity(availability.getProductQuantity() == null ? 1:availability.getProductQuantity());
					System.out.println("$#9853#"); target.setQuantityOrderMaximum(availability.getProductQuantityOrderMax() == null ? 1:availability.getProductQuantityOrderMax());
					System.out.println("$#9855#"); target.setQuantityOrderMinimum(availability.getProductQuantityOrderMin()==null ? 1:availability.getProductQuantityOrderMin());
					System.out.println("$#9857#"); System.out.println("$#9856#"); if(availability.getProductQuantity().intValue() > 0 && target.isAvailable()) {
							System.out.println("$#9859#"); target.setCanBePurchased(true);
					}
				//}
			}
			
	
			System.out.println("$#9860#"); target.setSku(source.getSku());
	
			FinalPrice price = pricingService.calculateProductPrice(source);
			
			System.out.println("$#9861#"); if(price != null) {

				System.out.println("$#9862#"); target.setFinalPrice(pricingService.getDisplayAmount(price.getFinalPrice(), store));
				System.out.println("$#9863#"); target.setPrice(price.getFinalPrice());
				System.out.println("$#9864#"); target.setOriginalPrice(pricingService.getDisplayAmount(price.getOriginalPrice(), store));
				
				System.out.println("$#9865#"); if(price.isDiscounted()) {
					System.out.println("$#9866#"); target.setDiscounted(true);
				}
				
				//price appender
				System.out.println("$#9867#"); if(availability != null) {
					Set<ProductPrice> prices = availability.getPrices();
					System.out.println("$#9868#"); if(!CollectionUtils.isEmpty(prices)) {
						ReadableProductPrice readableProductPrice = new ReadableProductPrice();
						System.out.println("$#9869#"); readableProductPrice.setDiscounted(target.isDiscounted());
						System.out.println("$#9870#"); readableProductPrice.setFinalPrice(target.getFinalPrice());
						System.out.println("$#9871#"); readableProductPrice.setOriginalPrice(target.getOriginalPrice());
						
						Optional<ProductPrice> pr = prices.stream().filter(p -> p.getCode().equals(ProductPrice.DEFAULT_PRICE_CODE))
								.findFirst();
						
						System.out.println("$#9874#"); target.setProductPrice(readableProductPrice);
						
						System.out.println("$#9875#"); if(pr.isPresent()) {
							System.out.println("$#9876#"); readableProductPrice.setId(pr.get().getId());
							Optional<ProductPriceDescription> d = pr.get().getDescriptions().stream().filter(desc -> desc.getLanguage().getCode().equals(lang.getCode())).findFirst();
							System.out.println("$#9879#"); if(d.isPresent()) {
								com.salesmanager.shop.model.catalog.product.ProductPriceDescription priceDescription = new com.salesmanager.shop.model.catalog.product.ProductPriceDescription();
								System.out.println("$#9880#"); priceDescription.setLanguage(language.getCode());
								System.out.println("$#9881#"); priceDescription.setId(d.get().getId());
								System.out.println("$#9882#"); priceDescription.setPriceAppender(d.get().getPriceAppender());
								System.out.println("$#9883#"); readableProductPrice.setDescription(priceDescription);
							}
						}

					}
				}
			
			}
	



							System.out.println("$#9884#"); if(target instanceof ReadableProductFull) {
												System.out.println("$#9885#"); ((ReadableProductFull)target).setDescriptions(fulldescriptions);
		      }

			
			System.out.println("$#9886#"); return target;
		
		} catch (Exception e) {
			throw new ConversionException(e);
		}
	}
	

	
	private ReadableProductOption createOption(ProductAttribute productAttribute, Language language) {
		

		ReadableProductOption option = new ReadableProductOption();
		System.out.println("$#9887#"); option.setId(productAttribute.getProductOption().getId());//attribute of the option
		System.out.println("$#9888#"); option.setType(productAttribute.getProductOption().getProductOptionType());
		System.out.println("$#9889#"); option.setCode(productAttribute.getProductOption().getCode());
		List<ProductOptionDescription> descriptions = productAttribute.getProductOption().getDescriptionsSettoList();
		ProductOptionDescription description = null;
		System.out.println("$#9891#"); System.out.println("$#9890#"); if(descriptions!=null && descriptions.size()>0) {
			description = descriptions.get(0);
			System.out.println("$#9894#"); System.out.println("$#9893#"); if(descriptions.size()>1) {
				for(ProductOptionDescription optionDescription : descriptions) {
					System.out.println("$#9895#"); if(optionDescription.getLanguage().getCode().equals(language.getCode())) {
						description = optionDescription;
						break;
					}
				}
			}
		}
		
		System.out.println("$#9896#"); if(description==null) {
			return null;
		}

		System.out.println("$#9897#"); option.setLang(language.getCode());
		System.out.println("$#9898#"); option.setName(description.getName());
		System.out.println("$#9899#"); option.setCode(productAttribute.getProductOption().getCode());

		
		System.out.println("$#9900#"); return option;
		
	}
	
	private ReadableProductAttribute createAttribute(ProductAttribute productAttribute, Language language) {
		

		ReadableProductAttribute attr = new ReadableProductAttribute();
		System.out.println("$#9901#"); attr.setId(productAttribute.getProductOption().getId());//attribute of the option
		System.out.println("$#9902#"); attr.setType(productAttribute.getProductOption().getProductOptionType());
		List<ProductOptionDescription> descriptions = productAttribute.getProductOption().getDescriptionsSettoList();
		ProductOptionDescription description = null;
		System.out.println("$#9904#"); System.out.println("$#9903#"); if(descriptions!=null && descriptions.size()>0) {
			description = descriptions.get(0);
			System.out.println("$#9907#"); System.out.println("$#9906#"); if(descriptions.size()>1) {
				for(ProductOptionDescription optionDescription : descriptions) {
					System.out.println("$#9908#"); if(optionDescription.getLanguage().getId().intValue()==language.getId().intValue()) {
						description = optionDescription;
						break;
					}
				}
			}
		}
		
		System.out.println("$#9909#"); if(description==null) {
			return null;
		}

		System.out.println("$#9910#"); attr.setLang(language.getCode());
		System.out.println("$#9911#"); attr.setName(description.getName());
		System.out.println("$#9912#"); attr.setCode(productAttribute.getProductOption().getCode());

		
		System.out.println("$#9913#"); return attr;
		
	}




	@Override
	protected ReadableProduct createTarget() {
		// TODO Auto-generated method stub
		return null;
	}
	
    com.salesmanager.shop.model.catalog.product.ProductDescription populateDescription(ProductDescription description) {
						System.out.println("$#9914#"); if(description == null) {
        return null;
      }
     
      com.salesmanager.shop.model.catalog.product.ProductDescription tragetDescription = new com.salesmanager.shop.model.catalog.product.ProductDescription();
						System.out.println("$#9915#"); tragetDescription.setFriendlyUrl(description.getSeUrl());
						System.out.println("$#9916#"); tragetDescription.setName(description.getName());
						System.out.println("$#9917#"); tragetDescription.setId(description.getId());
						System.out.println("$#9918#"); if(!StringUtils.isBlank(description.getMetatagTitle())) {
										System.out.println("$#9919#"); tragetDescription.setTitle(description.getMetatagTitle());
      } else {
										System.out.println("$#9920#"); tragetDescription.setTitle(description.getName());
      }
						System.out.println("$#9921#"); tragetDescription.setMetaDescription(description.getMetatagDescription());
						System.out.println("$#9922#"); tragetDescription.setDescription(description.getDescription());
						System.out.println("$#9923#"); tragetDescription.setHighlights(description.getProductHighlight());
						System.out.println("$#9924#"); tragetDescription.setLanguage(description.getLanguage().getCode());
						System.out.println("$#9925#"); tragetDescription.setKeyWords(description.getMetatagKeywords());

						System.out.println("$#9926#"); if(description.getLanguage() != null) {
								System.out.println("$#9927#"); tragetDescription.setLanguage(description.getLanguage().getCode());
      }
						System.out.println("$#9928#"); return tragetDescription;
    }

}
