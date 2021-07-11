package com.salesmanager.shop.admin.controller.products;

import com.salesmanager.core.business.services.catalog.category.CategoryService;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.image.ProductImageService;
import com.salesmanager.core.business.services.catalog.product.manufacturer.ManufacturerService;
import com.salesmanager.core.business.services.catalog.product.type.ProductTypeService;
import com.salesmanager.core.business.services.tax.TaxClassService;
import com.salesmanager.core.business.utils.CoreConfiguration;
import com.salesmanager.core.business.utils.ProductPriceUtils;
import com.salesmanager.core.business.utils.ajax.AjaxPageableResponse;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.catalog.category.CategoryDescription;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.catalog.product.availability.ProductAvailability;
import com.salesmanager.core.model.catalog.product.description.ProductDescription;
import com.salesmanager.core.model.catalog.product.image.ProductImage;
import com.salesmanager.core.model.catalog.product.image.ProductImageDescription;
import com.salesmanager.core.model.catalog.product.manufacturer.Manufacturer;
import com.salesmanager.core.model.catalog.product.price.ProductPrice;
import com.salesmanager.core.model.catalog.product.price.ProductPriceDescription;
import com.salesmanager.core.model.catalog.product.relationship.ProductRelationship;
import com.salesmanager.core.model.catalog.product.type.ProductType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.tax.taxclass.TaxClass;
import com.salesmanager.shop.admin.model.web.Menu;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.utils.CategoryUtils;
import com.salesmanager.shop.utils.DateUtil;
import com.salesmanager.shop.utils.LabelUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.*;

@Controller
public class ProductController {
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);
	

	
	@Inject
	private ProductService productService;
	
	@Inject
	private ManufacturerService manufacturerService;
	
	@Inject
	private ProductTypeService productTypeService;
	
	@Inject
	private ProductImageService productImageService;
	
	@Inject
	private TaxClassService taxClassService;
	
	@Inject
	private ProductPriceUtils priceUtil;

	@Inject
	LabelUtils messages;
	
	@Inject
	private CoreConfiguration configuration;
	
	@Inject
	CategoryService categoryService;

	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/editProduct.html", method=RequestMethod.GET)
	public String displayProductEdit(@RequestParam("id") long productId, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("$#6460#"); return displayProduct(productId,model,request,response);

	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/viewEditProduct.html", method=RequestMethod.GET)
	public String displayProductEdit(@RequestParam("sku") String sku, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Language language = (Language)request.getAttribute("LANGUAGE");
		Product dbProduct = productService.getByCode(sku, language);
		
		long productId = -1;//non existent
		System.out.println("$#6461#"); if(dbProduct!=null) {
			productId = dbProduct.getId();
		}
		
		System.out.println("$#6462#"); return displayProduct(productId,model,request,response);
	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/createProduct.html", method=RequestMethod.GET)
	public String displayProductCreate(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("$#6463#"); return displayProduct(null,model,request,response);

	}
	
	
	
	private String displayProduct(Long productId, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		

		//display menu
		System.out.println("$#6464#"); setMenu(model,request);
		
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		Language language = (Language)request.getAttribute("LANGUAGE");
		

		List<Manufacturer> manufacturers = manufacturerService.listByStore(store, language);
		
		List<ProductType> productTypes = productTypeService.list();
		
		List<TaxClass> taxClasses = taxClassService.listByStore(store);
		
		List<Language> languages = store.getLanguages();
		

		
		com.salesmanager.shop.admin.model.catalog.Product product = new com.salesmanager.shop.admin.model.catalog.Product();
		List<ProductDescription> descriptions = new ArrayList<ProductDescription>();

		System.out.println("$#6465#"); if(productId!=null && productId!=0) {//edit mode
			

			Product dbProduct = productService.getById(productId);
			
			System.out.println("$#6467#"); if(dbProduct==null || dbProduct.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#6469#"); return "redirect:/admin/products/products.html";
			}
			
			System.out.println("$#6470#"); product.setProduct(dbProduct);
			Set<ProductDescription> productDescriptions = dbProduct.getDescriptions();
			
			for(Language l : languages) {
				
				ProductDescription productDesc = null;
				for(ProductDescription desc : productDescriptions) {
					
					Language lang = desc.getLanguage();
					System.out.println("$#6471#"); if(lang.getCode().equals(l.getCode())) {
						productDesc = desc;
					}

				}
				
				System.out.println("$#6472#"); if(productDesc==null) {
					productDesc = new ProductDescription();
					System.out.println("$#6473#"); productDesc.setLanguage(l);
				}

				descriptions.add(productDesc);
				
			}
			
			for(ProductImage image : dbProduct.getImages()) {
				System.out.println("$#6474#"); if(image.isDefaultImage()) {
					System.out.println("$#6475#"); product.setProductImage(image);
					break;
				}

			}
			
			
			ProductAvailability productAvailability = null;
			ProductPrice productPrice = null;
			
			Set<ProductAvailability> availabilities = dbProduct.getAvailabilities();
			System.out.println("$#6477#"); System.out.println("$#6476#"); if(availabilities!=null && availabilities.size()>0) {
				
				for(ProductAvailability availability : availabilities) {
					System.out.println("$#6479#"); if(availability.getRegion().equals(com.salesmanager.core.business.constants.Constants.ALL_REGIONS)) {
						productAvailability = availability;
						Set<ProductPrice> prices = availability.getPrices();
						for(ProductPrice price : prices) {
							System.out.println("$#6480#"); if(price.isDefaultPrice()) {
								productPrice = price;
								System.out.println("$#6481#"); product.setProductPrice(priceUtil.getAdminFormatedAmount(store, productPrice.getProductPriceAmount()));
							}
						}
					}
				}
			}
			
			System.out.println("$#6482#"); if(productAvailability==null) {
				productAvailability = new ProductAvailability();
			}
			
			System.out.println("$#6483#"); if(productPrice==null) {
				productPrice = new ProductPrice();
			}
			
			System.out.println("$#6484#"); product.setAvailability(productAvailability);
			System.out.println("$#6485#"); product.setPrice(productPrice);
			System.out.println("$#6486#"); product.setDescriptions(descriptions);
			
			
			System.out.println("$#6487#"); product.setDateAvailable(DateUtil.formatDate(dbProduct.getDateAvailable()));


		} else {


			for(Language l : languages) {
				
				ProductDescription desc = new ProductDescription();
				System.out.println("$#6488#"); desc.setLanguage(l);
				descriptions.add(desc);
				
			}
			
			Product prod = new Product();
			
			System.out.println("$#6489#"); prod.setAvailable(true);
			
			ProductAvailability productAvailability = new ProductAvailability();
			ProductPrice price = new ProductPrice();
			System.out.println("$#6490#"); product.setPrice(price);
			System.out.println("$#6491#"); product.setAvailability(productAvailability);
			System.out.println("$#6492#"); product.setProduct(prod);
			System.out.println("$#6493#"); product.setDescriptions(descriptions);
			System.out.println("$#6494#"); product.setDateAvailable(DateUtil.formatDate(new Date()));


		}
		
		
		
		
		
		model.addAttribute("product",product);
		model.addAttribute("manufacturers", manufacturers);
		model.addAttribute("productTypes", productTypes);
		model.addAttribute("taxClasses", taxClasses);
		System.out.println("$#6495#"); return "admin-products-edit";
	}
	

	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/save.html", method=RequestMethod.POST)
	public String saveProduct(@Valid @ModelAttribute("product") com.salesmanager.shop.admin.model.catalog.Product  product, BindingResult result, Model model, HttpServletRequest request, Locale locale) throws Exception {
		

		Language language = (Language)request.getAttribute("LANGUAGE");
		
		//display menu
		System.out.println("$#6496#"); setMenu(model,request);
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		List<Manufacturer> manufacturers = manufacturerService.listByStore(store, language);
		
		List<ProductType> productTypes = productTypeService.list();
		
		List<TaxClass> taxClasses = taxClassService.listByStore(store);
		
		List<Language> languages = store.getLanguages();
		
		model.addAttribute("manufacturers", manufacturers);
		model.addAttribute("productTypes", productTypes);
		model.addAttribute("taxClasses", taxClasses);
		
		boolean productAlreadyExists = false;
		System.out.println("$#6497#"); if (!StringUtils.isBlank(product.getProduct().getSku()) && (product.getProduct().getId() == null || product.getProduct().getId().longValue() == 0)) {
			try {
				Product productByCode = productService.getByCode(product.getProduct().getSku(),language);
				productAlreadyExists = productByCode != null;

				System.out.println("$#6501#"); if(productAlreadyExists) throw new Exception();
				} catch (Exception e) {
				ObjectError error = new ObjectError("product.sku",messages.getMessage("message.sku.exists", locale));
				System.out.println("$#6502#"); result.addError(error);
			}
		}
		
		//validate price
		BigDecimal submitedPrice = null;
		try {
			submitedPrice = priceUtil.getAmount(product.getProductPrice());
		} catch (Exception e) {
			ObjectError error = new ObjectError("productPrice",messages.getMessage("NotEmpty.product.productPrice", locale));
			System.out.println("$#6503#"); result.addError(error);
		}
		Date date = new Date();
		System.out.println("$#6504#"); if(!StringUtils.isBlank(product.getDateAvailable())) {
			try {
				date = DateUtil.getDate(product.getDateAvailable());
				System.out.println("$#6505#"); product.getAvailability().setProductDateAvailable(date);
				System.out.println("$#6506#"); product.setDateAvailable(DateUtil.formatDate(date));
			} catch (Exception e) {
				ObjectError error = new ObjectError("dateAvailable",messages.getMessage("message.invalid.date", locale));
				System.out.println("$#6507#"); result.addError(error);
			}
		}
		

		
		//validate image
		System.out.println("$#6508#"); if(product.getImage()!=null && !product.getImage().isEmpty()) {
			
			try {
				
				String maxHeight = configuration.getProperty("PRODUCT_IMAGE_MAX_HEIGHT_SIZE");
				String maxWidth = configuration.getProperty("PRODUCT_IMAGE_MAX_WIDTH_SIZE");
				String maxSize = configuration.getProperty("PRODUCT_IMAGE_MAX_SIZE");
				
				
				BufferedImage image = ImageIO.read(product.getImage().getInputStream());
				
				
				System.out.println("$#6510#"); if(!StringUtils.isBlank(maxHeight)) {
					
					int maxImageHeight = Integer.parseInt(maxHeight);
					System.out.println("$#6512#"); System.out.println("$#6511#"); if(image.getHeight()>maxImageHeight) {
						ObjectError error = new ObjectError("image",messages.getMessage("message.image.height", locale) + " {"+maxHeight+"}");
						System.out.println("$#6513#"); result.addError(error);
					}
					
				}
				
				System.out.println("$#6514#"); if(!StringUtils.isBlank(maxWidth)) {
					
					int maxImageWidth = Integer.parseInt(maxWidth);
					System.out.println("$#6516#"); System.out.println("$#6515#"); if(image.getWidth()>maxImageWidth) {
						ObjectError error = new ObjectError("image",messages.getMessage("message.image.width", locale) + " {"+maxWidth+"}");
						System.out.println("$#6517#"); result.addError(error);
					}
					
				}
				
				System.out.println("$#6518#"); if(!StringUtils.isBlank(maxSize)) {
					
					int maxImageSize = Integer.parseInt(maxSize);
					System.out.println("$#6520#"); System.out.println("$#6519#"); if(product.getImage().getSize()>maxImageSize) {
						ObjectError error = new ObjectError("image",messages.getMessage("message.image.size", locale) + " {"+maxSize+"}");
						System.out.println("$#6521#"); result.addError(error);
					}
					
				}
				

				
			} catch (Exception e) {
				LOGGER.error("Cannot validate product image", e);
			}

		}
		
		
		
		System.out.println("$#6522#"); if (result.hasErrors()) {
			System.out.println("$#6523#"); return "admin-products-edit";
		}
		
		Product newProduct = product.getProduct();
		ProductAvailability newProductAvailability = null;
		ProductPrice newProductPrice = null;
		
		Set<ProductPriceDescription> productPriceDescriptions = null;
		
		//get tax class
		//TaxClass taxClass = newProduct.getTaxClass();
		//TaxClass dbTaxClass = taxClassService.getById(taxClass.getId());
		Set<ProductPrice> prices = new HashSet<ProductPrice>();
		Set<ProductAvailability> availabilities = new HashSet<ProductAvailability>();	

		System.out.println("$#6525#"); System.out.println("$#6524#"); if(product.getProduct().getId()!=null && product.getProduct().getId().longValue()>0) {
		
		
			//get actual product
			newProduct = productService.getById(product.getProduct().getId());
			System.out.println("$#6527#"); if(newProduct!=null && newProduct.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#6529#"); return "redirect:/admin/products/products.html";
			}
			
			//copy properties
			System.out.println("$#6530#"); newProduct.setSku(product.getProduct().getSku());
			System.out.println("$#6531#"); newProduct.setRefSku(product.getProduct().getRefSku());
			System.out.println("$#6532#"); newProduct.setAvailable(product.getProduct().isAvailable());
			System.out.println("$#6533#"); newProduct.setDateAvailable(date);
			System.out.println("$#6534#"); newProduct.setManufacturer(product.getProduct().getManufacturer());
			System.out.println("$#6535#"); newProduct.setType(product.getProduct().getType());
			System.out.println("$#6536#"); newProduct.setProductHeight(product.getProduct().getProductHeight());
			System.out.println("$#6537#"); newProduct.setProductLength(product.getProduct().getProductLength());
			System.out.println("$#6538#"); newProduct.setProductWeight(product.getProduct().getProductWeight());
			System.out.println("$#6539#"); newProduct.setProductWidth(product.getProduct().getProductWidth());
			System.out.println("$#6540#"); newProduct.setProductVirtual(product.getProduct().isProductVirtual());
			System.out.println("$#6541#"); newProduct.setProductShipeable(product.getProduct().isProductShipeable());
			System.out.println("$#6542#"); newProduct.setTaxClass(product.getProduct().getTaxClass());
			System.out.println("$#6543#"); newProduct.setSortOrder(product.getProduct().getSortOrder());

			Set<ProductAvailability> avails = newProduct.getAvailabilities();
			System.out.println("$#6545#"); System.out.println("$#6544#"); if(avails !=null && avails.size()>0) {
				
				for(ProductAvailability availability : avails) {
					System.out.println("$#6547#"); if(availability.getRegion().equals(com.salesmanager.core.business.constants.Constants.ALL_REGIONS)) {

						
						newProductAvailability = availability;
						Set<ProductPrice> productPrices = availability.getPrices();
						
						for(ProductPrice price : productPrices) {
							System.out.println("$#6548#"); if(price.isDefaultPrice()) {
								newProductPrice = price;
								System.out.println("$#6549#"); newProductPrice.setProductPriceAmount(submitedPrice);
								productPriceDescriptions = price.getDescriptions();
							} else {
								prices.add(price);
							}	
						}
					} else {
						availabilities.add(availability);
					}
				}
			}
			
			
			for(ProductImage image : newProduct.getImages()) {
				System.out.println("$#6550#"); if(image.isDefaultImage()) {
					System.out.println("$#6551#"); product.setProductImage(image);
				}
			}
		}
		
		System.out.println("$#6552#"); if(newProductPrice==null) {
			newProductPrice = new ProductPrice();
			System.out.println("$#6553#"); newProductPrice.setDefaultPrice(true);
			System.out.println("$#6554#"); newProductPrice.setProductPriceAmount(submitedPrice);
		}
		
		System.out.println("$#6555#"); if(product.getProductImage()!=null && product.getProductImage().getId() == null) {
			System.out.println("$#6557#"); product.setProductImage(null);
		}
		
		System.out.println("$#6558#"); if(productPriceDescriptions==null) {
			productPriceDescriptions = new HashSet<ProductPriceDescription>();
			for(ProductDescription description : product.getDescriptions()) {
				ProductPriceDescription ppd = new ProductPriceDescription();
				System.out.println("$#6559#"); ppd.setProductPrice(newProductPrice);
				System.out.println("$#6560#"); ppd.setLanguage(description.getLanguage());
				System.out.println("$#6561#"); ppd.setName(ProductPriceDescription.DEFAULT_PRICE_DESCRIPTION);
				productPriceDescriptions.add(ppd);
			}
			System.out.println("$#6562#"); newProductPrice.setDescriptions(productPriceDescriptions);
		}
		
		System.out.println("$#6563#"); newProduct.setMerchantStore(store);
		
		System.out.println("$#6564#"); if(newProductAvailability==null) {
			newProductAvailability = new ProductAvailability();
		}
		

		System.out.println("$#6565#"); newProductAvailability.setProductQuantity(product.getAvailability().getProductQuantity());
		System.out.println("$#6566#"); newProductAvailability.setProductQuantityOrderMin(product.getAvailability().getProductQuantityOrderMin());
		System.out.println("$#6567#"); newProductAvailability.setProductQuantityOrderMax(product.getAvailability().getProductQuantityOrderMax());
		System.out.println("$#6568#"); newProductAvailability.setProduct(newProduct);
		System.out.println("$#6569#"); newProductAvailability.setPrices(prices);
		availabilities.add(newProductAvailability);
			
		System.out.println("$#6570#"); newProductPrice.setProductAvailability(newProductAvailability);
		prices.add(newProductPrice);
			
		System.out.println("$#6571#"); newProduct.setAvailabilities(availabilities);

		Set<ProductDescription> descriptions = new HashSet<ProductDescription>();
		System.out.println("$#6573#"); System.out.println("$#6572#"); if(product.getDescriptions()!=null && product.getDescriptions().size()>0) {
			
			for(ProductDescription description : product.getDescriptions()) {
				System.out.println("$#6575#"); description.setProduct(newProduct);
				descriptions.add(description);
				
			}
		}
		
		System.out.println("$#6576#"); newProduct.setDescriptions(descriptions);
		System.out.println("$#6577#"); product.setDateAvailable(DateUtil.formatDate(date));

		
		
		System.out.println("$#6578#"); if(product.getImage()!=null && !product.getImage().isEmpty()) {
			

			
			String imageName = product.getImage().getOriginalFilename();
			

			
			ProductImage productImage = new ProductImage();
			System.out.println("$#6580#"); productImage.setDefaultImage(true);
			System.out.println("$#6581#"); productImage.setImage(product.getImage().getInputStream());
			System.out.println("$#6582#"); productImage.setProductImage(imageName);
			
			
			List<ProductImageDescription> imagesDescriptions = new ArrayList<ProductImageDescription>();

			for(Language l : languages) {
				
				ProductImageDescription imageDescription = new ProductImageDescription();
				System.out.println("$#6583#"); imageDescription.setName(imageName);
				System.out.println("$#6584#"); imageDescription.setLanguage(l);
				System.out.println("$#6585#"); imageDescription.setProductImage(productImage);
				imagesDescriptions.add(imageDescription);
				
			}
			
			System.out.println("$#6586#"); productImage.setDescriptions(imagesDescriptions);
			System.out.println("$#6587#"); productImage.setProduct(newProduct);
			
			newProduct.getImages().add(productImage);
			
			//productService.saveOrUpdate(newProduct);
			
			//product displayed
			System.out.println("$#6588#"); product.setProductImage(productImage);
			
			
		} //else {
			
			//productService.saveOrUpdate(newProduct);
			
		//}
		
		System.out.println("$#6589#"); productService.create(newProduct);
		model.addAttribute("success","success");
		
		System.out.println("$#6590#"); return "admin-products-edit";
	}
	
	
	/**
	 * Creates a duplicate product with the same inner object graph
	 * Will ignore SKU, reviews and images
	 * @param id
	 * @param result
	 * @param model
	 * @param request
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/product/duplicate.html", method=RequestMethod.POST)
	public String duplicateProduct(@ModelAttribute("productId") Long  id, BindingResult result, Model model, HttpServletRequest request, Locale locale) throws Exception {
		

		Language language = (Language)request.getAttribute("LANGUAGE");
		
		//display menu
		System.out.println("$#6591#"); setMenu(model,request);
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		List<Manufacturer> manufacturers = manufacturerService.listByStore(store, language);
		List<ProductType> productTypes = productTypeService.list();
		List<TaxClass> taxClasses = taxClassService.listByStore(store);

		model.addAttribute("manufacturers", manufacturers);
		model.addAttribute("productTypes", productTypes);
		model.addAttribute("taxClasses", taxClasses);
		
		Product dbProduct = productService.getById(id);
		Product newProduct = new Product();
		
		System.out.println("$#6592#"); if(dbProduct==null || dbProduct.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
			System.out.println("$#6594#"); return "redirect:/admin/products/products.html";
		}
		
		//Make a copy of the product
		com.salesmanager.shop.admin.model.catalog.Product product = new com.salesmanager.shop.admin.model.catalog.Product();
		
		Set<ProductAvailability> availabilities = new HashSet<ProductAvailability>();
		//availability - price
		for(ProductAvailability pAvailability : dbProduct.getAvailabilities()) {
			
			ProductAvailability availability = new ProductAvailability();
			System.out.println("$#6595#"); availability.setProductDateAvailable(pAvailability.getProductDateAvailable());
			System.out.println("$#6596#"); availability.setProductIsAlwaysFreeShipping(pAvailability.getProductIsAlwaysFreeShipping());
			System.out.println("$#6597#"); availability.setProductQuantity(pAvailability.getProductQuantity());
			System.out.println("$#6598#"); availability.setProductQuantityOrderMax(pAvailability.getProductQuantityOrderMax());
			System.out.println("$#6599#"); availability.setProductQuantityOrderMin(pAvailability.getProductQuantityOrderMin());
			System.out.println("$#6600#"); availability.setProductStatus(pAvailability.getProductStatus());
			System.out.println("$#6601#"); availability.setRegion(pAvailability.getRegion());
			System.out.println("$#6602#"); availability.setRegionVariant(pAvailability.getRegionVariant());
			System.out.println("$#6603#"); availability.setProduct(newProduct);


			
			Set<ProductPrice> prices = pAvailability.getPrices();
			for(ProductPrice pPrice : prices) {
				
				ProductPrice price = new ProductPrice();
				System.out.println("$#6604#"); price.setDefaultPrice(pPrice.isDefaultPrice());
				System.out.println("$#6605#"); price.setProductPriceAmount(pPrice.getProductPriceAmount());
				System.out.println("$#6606#"); price.setProductAvailability(availability);
				System.out.println("$#6607#"); price.setProductPriceSpecialAmount(pPrice.getProductPriceSpecialAmount());
				System.out.println("$#6608#"); price.setProductPriceSpecialEndDate(pPrice.getProductPriceSpecialEndDate());
				System.out.println("$#6609#"); price.setProductPriceSpecialStartDate(pPrice.getProductPriceSpecialStartDate());
				System.out.println("$#6610#"); price.setProductPriceType(pPrice.getProductPriceType());
				
				Set<ProductPriceDescription> priceDescriptions = new HashSet<ProductPriceDescription>();
				//price descriptions
				for(ProductPriceDescription pPriceDescription : pPrice.getDescriptions()) {
					
					ProductPriceDescription productPriceDescription = new ProductPriceDescription();
					System.out.println("$#6611#"); productPriceDescription.setAuditSection(pPriceDescription.getAuditSection());
					System.out.println("$#6612#"); productPriceDescription.setDescription(pPriceDescription.getDescription());
					System.out.println("$#6613#"); productPriceDescription.setName(pPriceDescription.getName());
					System.out.println("$#6614#"); productPriceDescription.setLanguage(pPriceDescription.getLanguage());
					System.out.println("$#6615#"); productPriceDescription.setProductPrice(price);
					priceDescriptions.add(productPriceDescription);
					
				}
				System.out.println("$#6616#"); price.setDescriptions(priceDescriptions);
				System.out.println("$#6617#"); if(price.isDefaultPrice()) {
					System.out.println("$#6618#"); product.setPrice(price);
					System.out.println("$#6619#"); product.setProductPrice(priceUtil.getAdminFormatedAmount(store, price.getProductPriceAmount()));
				}
				
				availability.getPrices().add(price);
			}
			
			

			System.out.println("$#6620#"); if(availability.getRegion().equals(com.salesmanager.core.business.constants.Constants.ALL_REGIONS)) {
				System.out.println("$#6621#"); product.setAvailability(availability);
			}
			
			availabilities.add(availability);
		}
		
		System.out.println("$#6622#"); newProduct.setAvailabilities(availabilities);
		
		
		
		//attributes
		Set<ProductAttribute> attributes = new HashSet<ProductAttribute>();
		for(ProductAttribute pAttribute : dbProduct.getAttributes()) {
			
			ProductAttribute attribute = new ProductAttribute();
			System.out.println("$#6623#"); attribute.setAttributeDefault(pAttribute.getAttributeDefault());
			System.out.println("$#6624#"); attribute.setAttributeDiscounted(pAttribute.getAttributeDiscounted());
			System.out.println("$#6625#"); attribute.setAttributeDisplayOnly(pAttribute.getAttributeDisplayOnly());
			System.out.println("$#6626#"); attribute.setAttributeRequired(pAttribute.getAttributeRequired());
			System.out.println("$#6627#"); attribute.setProductAttributePrice(pAttribute.getProductAttributePrice());
			System.out.println("$#6628#"); attribute.setProductAttributeIsFree(pAttribute.getProductAttributeIsFree());
			System.out.println("$#6629#"); attribute.setProductAttributeWeight(pAttribute.getProductAttributeWeight());
			System.out.println("$#6630#"); attribute.setProductOption(pAttribute.getProductOption());
			System.out.println("$#6631#"); attribute.setProductOptionSortOrder(pAttribute.getProductOptionSortOrder());
			System.out.println("$#6632#"); attribute.setProductOptionValue(pAttribute.getProductOptionValue());
			System.out.println("$#6633#"); attribute.setProduct(newProduct);
			attributes.add(attribute);
						
		}
		System.out.println("$#6634#"); newProduct.setAttributes(attributes);
		
		//relationships
		Set<ProductRelationship> relationships = new HashSet<ProductRelationship>();
		for(ProductRelationship pRelationship : dbProduct.getRelationships()) {
			
			ProductRelationship relationship = new ProductRelationship();
			System.out.println("$#6635#"); relationship.setActive(pRelationship.isActive());
			System.out.println("$#6636#"); relationship.setCode(pRelationship.getCode());
			System.out.println("$#6637#"); relationship.setRelatedProduct(pRelationship.getRelatedProduct());
			System.out.println("$#6638#"); relationship.setStore(store);
			System.out.println("$#6639#"); relationship.setProduct(newProduct);
			relationships.add(relationship);

		}
		
		System.out.println("$#6640#"); newProduct.setRelationships(relationships);
		
		//product description
		Set<ProductDescription> descsset = new HashSet<ProductDescription>();
		List<ProductDescription> desclist = new ArrayList<ProductDescription>();
		Set<ProductDescription> descriptions = dbProduct.getDescriptions();
		for(ProductDescription pDescription : descriptions) {
			
			ProductDescription description = new ProductDescription();
			System.out.println("$#6641#"); description.setAuditSection(pDescription.getAuditSection());
			System.out.println("$#6642#"); description.setName(pDescription.getName());
			System.out.println("$#6643#"); description.setDescription(pDescription.getDescription());
			System.out.println("$#6644#"); description.setLanguage(pDescription.getLanguage());
			System.out.println("$#6645#"); description.setMetatagDescription(pDescription.getMetatagDescription());
			System.out.println("$#6646#"); description.setMetatagKeywords(pDescription.getMetatagKeywords());
			System.out.println("$#6647#"); description.setMetatagTitle(pDescription.getMetatagTitle());
			System.out.println("$#6648#"); description.setProduct(newProduct);
			descsset.add(description);
			desclist.add(description);
		}
		System.out.println("$#6649#"); newProduct.setDescriptions(descsset);
		System.out.println("$#6650#"); product.setDescriptions(desclist);
		
		//product
		System.out.println("$#6651#"); newProduct.setAuditSection(dbProduct.getAuditSection());
		System.out.println("$#6652#"); newProduct.setAvailable(dbProduct.isAvailable());
		
		

		//copy
		// newProduct.setCategories(dbProduct.getCategories());
		System.out.println("$#6653#"); newProduct.setDateAvailable(dbProduct.getDateAvailable());
		System.out.println("$#6654#"); newProduct.setManufacturer(dbProduct.getManufacturer());
		System.out.println("$#6655#"); newProduct.setMerchantStore(store);
		System.out.println("$#6656#"); newProduct.setProductHeight(dbProduct.getProductHeight());
		System.out.println("$#6657#"); newProduct.setProductIsFree(dbProduct.getProductIsFree());
		System.out.println("$#6658#"); newProduct.setProductLength(dbProduct.getProductLength());
		System.out.println("$#6659#"); newProduct.setProductOrdered(dbProduct.getProductOrdered());
		System.out.println("$#6660#"); newProduct.setProductWeight(dbProduct.getProductWeight());
		System.out.println("$#6661#"); newProduct.setProductWidth(dbProduct.getProductWidth());
		System.out.println("$#6662#"); newProduct.setSortOrder(dbProduct.getSortOrder());
		System.out.println("$#6663#"); newProduct.setTaxClass(dbProduct.getTaxClass());
		System.out.println("$#6664#"); newProduct.setType(dbProduct.getType());
		System.out.println("$#6665#"); newProduct.setSku(UUID.randomUUID().toString().replace("-",""));
		System.out.println("$#6666#"); newProduct.setProductVirtual(dbProduct.isProductVirtual());
		System.out.println("$#6667#"); newProduct.setProductShipeable(dbProduct.isProductShipeable());
		
		System.out.println("$#6668#"); productService.update(newProduct);
		
		Set<Category> categories = dbProduct.getCategories();
		for(Category category : categories) {
			Category categoryCopy = categoryService.getById(category.getId(), store.getId());
			newProduct.getCategories().add(categoryCopy);
			System.out.println("$#6669#"); productService.update(newProduct);
		}
		
		System.out.println("$#6670#"); product.setProduct(newProduct);
		model.addAttribute("product", product);
		model.addAttribute("success","success");
		
		System.out.println("$#6671#"); return "redirect:/admin/products/editProduct.html?id=" + newProduct.getId();
	}

	
	/**
	 * Removes a product image based on the productimage id
	 * @param request
	 * @param response
	 * @param locale
	 * @return
	 */
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/product/removeImage.html")
	public @ResponseBody ResponseEntity<String> removeImage(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		String iid = request.getParameter("imageId");

		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		AjaxResponse resp = new AjaxResponse();

		
		try {
			
			Long id = Long.parseLong(iid);
			ProductImage productImage = productImageService.getById(id);
			System.out.println("$#6672#"); if(productImage==null || productImage.getProduct().getMerchantStore().getId().intValue()!=store.getId().intValue()) {

				System.out.println("$#6674#"); resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
				System.out.println("$#6675#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				
			} else {
				
				System.out.println("$#6676#"); productImageService.removeProductImage(productImage);
				System.out.println("$#6677#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);
				
			}
		
		
		} catch (Exception e) {
			LOGGER.error("Error while deleting product", e);
			System.out.println("$#6678#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6679#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#6680#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		System.out.println("$#6681#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	
	
	/**
	 * List all categories and let the merchant associate the product to a category
	 * @param productId
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/displayProductToCategories.html", method=RequestMethod.GET)
	public String displayAddProductToCategories(@RequestParam("id") long productId, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
	
		
		System.out.println("$#6682#"); setMenu(model,request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		Language language = (Language)request.getAttribute("LANGUAGE");
		
		
		//get the product and validate it belongs to the current merchant
		Product product = productService.getById(productId);
		
		System.out.println("$#6683#"); if(product==null) {
			System.out.println("$#6684#"); return "redirect:/admin/products/products.html";
		}
		
		System.out.println("$#6685#"); if(product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
			System.out.println("$#6686#"); return "redirect:/admin/products/products.html";
		}
		

		//get parent categories
		List<Category> categories = categoryService.listByStore(store,language);
		List<com.salesmanager.shop.admin.model.catalog.Category> readableCategories = CategoryUtils.readableCategoryListConverter(categories, language);
		
		model.addAttribute("product", product);
		model.addAttribute("categories", readableCategories);
		System.out.println("$#6687#"); return "catalogue-product-categories";
		
	}
	
	/**
	 * List all categories associated to a Product
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/product-categories/paging.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> pageProductCategories(HttpServletRequest request, HttpServletResponse response) {

		String sProductId = request.getParameter("productId");
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		
		AjaxResponse resp = new AjaxResponse();
		
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#6688#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		Long productId;
		Product product = null;
		
		try {
			productId = Long.parseLong(sProductId);
		} catch (Exception e) {
			System.out.println("$#6689#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6690#"); resp.setErrorString("Product id is not valid");
			String returnString = resp.toJSONString();
			System.out.println("$#6691#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		}

		
		try {

			product = productService.getById(productId);

			
			System.out.println("$#6692#"); if(product==null) {
				System.out.println("$#6693#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				System.out.println("$#6694#"); resp.setErrorString("Product id is not valid");
				String returnString = resp.toJSONString();
				System.out.println("$#6695#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#6696#"); if(product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				System.out.println("$#6697#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				System.out.println("$#6698#"); resp.setErrorString("Product id is not valid");
				String returnString = resp.toJSONString();
				System.out.println("$#6699#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			
			Language language = (Language)request.getAttribute("LANGUAGE");

			
			Set<Category> categories = product.getCategories();
			

			for(Category category : categories) {
				Map entry = new HashMap();
				entry.put("categoryId", category.getId());
				
				Set<CategoryDescription> descriptions = category.getDescriptions();
				String categoryName = category.getDescriptions().iterator().next().getName();
				for(CategoryDescription description : descriptions){
					System.out.println("$#6700#"); if(description.getLanguage().getCode().equals(language.getCode())) {
						categoryName = description.getName();
					}
				}
				entry.put("name", categoryName);
				System.out.println("$#6701#"); resp.addDataEntry(entry);
			}

			System.out.println("$#6702#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_SUCCESS);
		
		} catch (Exception e) {
			LOGGER.error("Error while paging products", e);
			System.out.println("$#6703#"); resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6704#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#6705#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);


	}
	
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/product-categories/remove.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> deleteProductFromCategory(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		String sCategoryid = request.getParameter("categoryId");
		String sProductId = request.getParameter("productId");
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		AjaxResponse resp = new AjaxResponse();
		
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#6706#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		
		try {
			
			Long categoryId = Long.parseLong(sCategoryid);
			Long productId = Long.parseLong(sProductId);
			
			Category category = categoryService.getById(categoryId, store.getId());
			Product product = productService.getById(productId);
			
			System.out.println("$#6707#"); if(category==null || category.getMerchantStore().getId()!=store.getId()) {

				System.out.println("$#6709#"); resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
				System.out.println("$#6710#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#6711#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			} 
			
			System.out.println("$#6712#"); if(product==null || product.getMerchantStore().getId()!=store.getId()) {

				System.out.println("$#6714#"); resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
				System.out.println("$#6715#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#6716#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			} 
			
			product.getCategories().remove(category);
			System.out.println("$#6717#"); productService.update(product);
			
			System.out.println("$#6718#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);

		
		
		} catch (Exception e) {
			LOGGER.error("Error while deleting category", e);
			System.out.println("$#6719#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#6720#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();

		System.out.println("$#6721#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	

	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/addProductToCategories.html", method=RequestMethod.POST)
	public String addProductToCategory(@RequestParam("productId") long productId, @RequestParam("id") long categoryId, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		System.out.println("$#6722#"); setMenu(model,request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		Language language = (Language)request.getAttribute("LANGUAGE");
		
		
		//get the product and validate it belongs to the current merchant
		Product product = productService.getById(productId);
		
		System.out.println("$#6723#"); if(product==null) {
			System.out.println("$#6724#"); return "redirect:/admin/products/products.html";
		}
		
		System.out.println("$#6725#"); if(product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
			System.out.println("$#6726#"); return "redirect:/admin/products/products.html";
		}
		

		//get parent categories
		List<Category> categories = categoryService.listByStore(store,language);
		
		Category category = categoryService.getById(categoryId, store.getId(), language.getId());
		
		System.out.println("$#6727#"); if(category==null) {
			System.out.println("$#6728#"); return "redirect:/admin/products/products.html";
		}
		
		System.out.println("$#6729#"); if(category.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
			System.out.println("$#6730#"); return "redirect:/admin/products/products.html";
		}
		
		product.getCategories().add(category);
		
		System.out.println("$#6731#"); productService.update(product);
		
		List<com.salesmanager.shop.admin.model.catalog.Category> readableCategories = CategoryUtils.readableCategoryListConverter(categories, language);
		
		model.addAttribute("product", product);
		model.addAttribute("categories", readableCategories);
		
		System.out.println("$#6732#"); return "catalogue-product-categories";
		
	}

	private void setMenu(Model model, HttpServletRequest request) throws Exception {
		
		//display menu
		Map<String,String> activeMenus = new HashMap<String,String>();
		activeMenus.put("catalogue", "catalogue");
		activeMenus.put("catalogue-products", "catalogue-products");
		
		@SuppressWarnings("unchecked")
		Map<String, Menu> menus = (Map<String, Menu>)request.getAttribute("MENUMAP");
		
		Menu currentMenu = (Menu)menus.get("catalogue");
		model.addAttribute("currentMenu",currentMenu);
		model.addAttribute("activeMenus",activeMenus);
		//	
	}
}