package com.salesmanager.core.business.utils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.validator.routines.BigDecimalValidator;
import org.apache.commons.validator.routines.CurrencyValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.catalog.product.availability.ProductAvailability;
import com.salesmanager.core.model.catalog.product.price.FinalPrice;
import com.salesmanager.core.model.catalog.product.price.ProductPrice;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.orderproduct.OrderProduct;


/**
 * This class determines the price that is displayed in the catalogue for a given item. 
 * It does not calculate the total price for a given item
 * @author casams1
 *
 */
@Component("priceUtil")
public class ProductPriceUtils {
	
	private final static char DECIMALCOUNT = '2';
	private final static char DECIMALPOINT = '.';
	private final static char THOUSANDPOINT = ',';
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductPriceUtils.class);

	
	
	/**
	 * Get the price without discount
	 * @param store
	 * @param product
	 * @param locale
	 * @return
	 */
	public BigDecimal getPrice(MerchantStore store, Product product, Locale locale) {
		
		BigDecimal defaultPrice = new BigDecimal(0);

		Set<ProductAvailability> availabilities = product.getAvailabilities();
		for(ProductAvailability availability : availabilities) {
			
			Set<ProductPrice> prices = availability.getPrices();
			for(ProductPrice price : prices) {
				
				System.out.println("$#3568#"); if(price.isDefaultPrice()) {
					defaultPrice = price.getProductPriceAmount();
				}
			}
		}
		
		System.out.println("$#3569#"); return defaultPrice;
	}
	
	/**
	 * This method calculates the final price taking into account
	 * all attributes included having a specified default attribute with an attribute price gt 0
	 * in the product object. The calculation is based
	 * on the default price.
	 * Attributes may be null
	 * @param Product
	 * @param List<ProductAttribute>
	 * @return FinalPrice
	 */
	public FinalPrice getFinalProductPrice(Product product, List<ProductAttribute> attributes) {


		FinalPrice finalPrice = calculateFinalPrice(product);
		
		//attributes
		BigDecimal attributePrice = null;
		System.out.println("$#3571#"); System.out.println("$#3570#"); if(attributes!=null && attributes.size()>0) {
			for(ProductAttribute attribute : attributes) {
					System.out.println("$#3574#"); System.out.println("$#3573#"); if(attribute.getProductAttributePrice()!=null && attribute.getProductAttributePrice().doubleValue()>0) {
						System.out.println("$#3576#"); if(attributePrice==null) {
							attributePrice = new BigDecimal(0);
						}
						attributePrice = attributePrice.add(attribute.getProductAttributePrice());
					}
			}
			
			System.out.println("$#3578#"); System.out.println("$#3577#"); if(attributePrice!=null && attributePrice.doubleValue()>0) {
				BigDecimal fp = finalPrice.getFinalPrice();
				fp = fp.add(attributePrice);
				System.out.println("$#3580#"); finalPrice.setFinalPrice(fp);
				
				BigDecimal op = finalPrice.getOriginalPrice();
				op = op.add(attributePrice);
				System.out.println("$#3581#"); finalPrice.setOriginalPrice(op);
				
				BigDecimal dp = finalPrice.getDiscountedPrice();
				System.out.println("$#3582#"); if(dp!=null) {
					dp = dp.add(attributePrice);
					System.out.println("$#3583#"); finalPrice.setDiscountedPrice(dp);
				}
				
			}
		}
		

		System.out.println("$#3584#"); return finalPrice;

	}

	
	/**
	 * This is the final price calculated from all configured prices
	 * and all possibles discounts. This price does not calculate the attributes
	 * or other prices than the default one
	 * @param store
	 * @param product
	 * @param locale
	 * @return
	 */
	public FinalPrice getFinalPrice(Product product) {



		FinalPrice finalPrice = calculateFinalPrice(product);
		
		//attributes
		BigDecimal attributePrice = null;
		System.out.println("$#3586#"); System.out.println("$#3585#"); if(product.getAttributes()!=null && product.getAttributes().size()>0) {
			for(ProductAttribute attribute : product.getAttributes()) {
					System.out.println("$#3588#"); if(attribute.getAttributeDefault()) {
						System.out.println("$#3590#"); System.out.println("$#3589#"); if(attribute.getProductAttributePrice()!=null && attribute.getProductAttributePrice().doubleValue()>0) {
							System.out.println("$#3592#"); if(attributePrice==null) {
								attributePrice = new BigDecimal(0);
							}
							attributePrice = attributePrice.add(attribute.getProductAttributePrice());
						}
					}
			}
			
			System.out.println("$#3594#"); System.out.println("$#3593#"); if(attributePrice!=null && attributePrice.doubleValue()>0) {
				BigDecimal fp = finalPrice.getFinalPrice();
				fp = fp.add(attributePrice);
				System.out.println("$#3596#"); finalPrice.setFinalPrice(fp);
				
				BigDecimal op = finalPrice.getOriginalPrice();
				op = op.add(attributePrice);
				System.out.println("$#3597#"); finalPrice.setOriginalPrice(op);
			}
		}

		System.out.println("$#3598#"); return finalPrice;

	}
	

	

	/**
	 * This is the format that will be displayed
	 * in the admin input text fields when editing
	 * an entity having a BigDecimal to be displayed
	 * as a raw amount 1,299.99
	 * The admin user will also be force to input
	 * the amount using that format	
	 * @param store
	 * @param amount
	 * @return
	 * @throws Exception
	 */
	public String getAdminFormatedAmount(MerchantStore store, BigDecimal amount) throws Exception {
			
		System.out.println("$#3599#"); if(amount==null) {
			return "";
		}
		
		NumberFormat nf = null;

			
		nf = NumberFormat.getInstance(Constants.DEFAULT_LOCALE);

		System.out.println("$#3600#"); nf.setMaximumFractionDigits(Integer.parseInt(Character
					.toString(DECIMALCOUNT)));
		System.out.println("$#3601#"); nf.setMinimumFractionDigits(Integer.parseInt(Character
					.toString(DECIMALCOUNT)));

		System.out.println("$#3602#"); return nf.format(amount);
	}
	
	
	/**
	 * This method has to be used to format store front amounts
	 * It will display national format amount ex:
	 * $1,345.99
	 * Rs.1.345.99
	 * or international format
	 * USD1,345.79
	 * INR1,345.79
	 * @param store
	 * @param amount
	 * @return String
	 * @throws Exception
	 */
	public String getStoreFormatedAmountWithCurrency(MerchantStore store, BigDecimal amount) throws Exception {
		System.out.println("$#3603#"); if(amount==null) {
			return "";
		}
		
		
		
		Currency currency = Constants.DEFAULT_CURRENCY;
		Locale locale = Constants.DEFAULT_LOCALE; 
		
		try {

			currency = store.getCurrency().getCurrency();
			locale = new Locale(store.getDefaultLanguage().getCode(),store.getCountry().getIsoCode());
		} catch (Exception e) {
			LOGGER.error("Cannot create currency or locale instance for store " + store.getCode());
		}

		
		NumberFormat currencyInstance = null;
		
		
		System.out.println("$#3604#"); if(store.isCurrencyFormatNational()) {
			currencyInstance = NumberFormat.getCurrencyInstance(locale);//national
		} else {
			currencyInstance = NumberFormat.getCurrencyInstance();//international
		}
					System.out.println("$#3605#"); currencyInstance.setCurrency(currency);
		
	    
					System.out.println("$#3606#"); return currencyInstance.format(amount.doubleValue());
		

    }
	
	
	public String getFormatedAmountWithCurrency(Locale locale, com.salesmanager.core.model.reference.currency.Currency currency, BigDecimal amount) throws Exception {
		System.out.println("$#3607#"); if(amount==null) {
			return "";
		}

		Currency curr = currency.getCurrency();


		
		NumberFormat currencyInstance = null;

		currencyInstance = NumberFormat.getCurrencyInstance(locale);
		System.out.println("$#3608#"); currencyInstance.setCurrency(curr);
					System.out.println("$#3609#"); return currencyInstance.format(amount.doubleValue());
		

    }
	

	
	/**
	 * This method will return the required formated amount
	 * with the appropriate currency
	 * @param store
	 * @param amount
	 * @return
	 * @throws Exception
	 */
	public String getAdminFormatedAmountWithCurrency(MerchantStore store, BigDecimal amount) throws Exception {
		System.out.println("$#3610#"); if(amount==null) {
			return "";
		}
		
		
		
		
		NumberFormat nf = null;

		
		Currency currency = store.getCurrency().getCurrency();
		nf = NumberFormat.getInstance(Constants.DEFAULT_LOCALE);
		System.out.println("$#3611#"); nf.setMaximumFractionDigits(Integer.parseInt(Character
				.toString(DECIMALCOUNT)));
		System.out.println("$#3612#"); nf.setMinimumFractionDigits(Integer.parseInt(Character
				.toString(DECIMALCOUNT)));
		System.out.println("$#3613#"); nf.setCurrency(currency);


		System.out.println("$#3614#"); return nf.format(amount);
	}
	
	/**
	 * Returns a formatted amount using Shopizer Currency
	 * requires internal java.util.Currency populated
	 * @param currency
	 * @param amount
	 * @return
	 * @throws Exception
	 */
	public String getFormatedAmountWithCurrency(com.salesmanager.core.model.reference.currency.Currency currency, BigDecimal amount) throws Exception {
		System.out.println("$#3615#"); if(amount==null) {
			return "";
		}
		
		Validate.notNull(currency.getCurrency(),"Currency must be populated with java.util.Currency");
		
		NumberFormat nf = null;

		
		Currency curr = currency.getCurrency();
		nf = NumberFormat.getInstance(Constants.DEFAULT_LOCALE);
		System.out.println("$#3616#"); nf.setMaximumFractionDigits(Integer.parseInt(Character
				.toString(DECIMALCOUNT)));
		System.out.println("$#3617#"); nf.setMinimumFractionDigits(Integer.parseInt(Character
				.toString(DECIMALCOUNT)));
		System.out.println("$#3618#"); nf.setCurrency(curr);


		String stringNumber = nf.format(amount);
		
		System.out.println("$#3619#"); return stringNumber;
	}

	/**
	 * This amount will be displayed to the end user
	 * @param store
	 * @param amount
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	public String getFormatedAmountWithCurrency(MerchantStore store, BigDecimal amount, Locale locale)
				throws Exception {
		
			NumberFormat nf = null;

			Currency currency = store.getCurrency().getCurrency();
			
			nf = NumberFormat.getInstance(locale);
			System.out.println("$#3620#"); nf.setCurrency(currency);
			System.out.println("$#3621#"); nf.setMaximumFractionDigits(Integer.parseInt(Character
					.toString(DECIMALCOUNT)));
			System.out.println("$#3622#"); nf.setMinimumFractionDigits(Integer.parseInt(Character
					.toString(DECIMALCOUNT)));
	

	
			System.out.println("$#3623#"); return nf.format(amount);

	}
	
	/**
	 * Transformation of an amount of money submited by the admin
	 * user to be inserted as a BigDecimal in the database
	 * @param amount
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getAmount(String amount) throws Exception {

		// validations
		/**
		 * 1) remove decimal and thousand
		 * 
		 * String.replaceAll(decimalPoint, ""); String.replaceAll(thousandPoint,
		 * "");
		 * 
		 * Should be able to parse to Integer
		 */
		StringBuffer newAmount = new StringBuffer();
		System.out.println("$#3625#"); System.out.println("$#3624#"); for (int i = 0; i < amount.length(); i++) {
			System.out.println("$#3626#"); if (amount.charAt(i) != DECIMALPOINT
					&& amount.charAt(i) != THOUSANDPOINT) {
				newAmount.append(amount.charAt(i));
			}
		}

		try {
			Integer.parseInt(newAmount.toString());
		} catch (Exception e) {
			throw new Exception("Cannot parse " + amount);
		}

		System.out.println("$#3628#"); if (!amount.contains(Character.toString(DECIMALPOINT))
				&& !amount.contains(Character.toString(THOUSANDPOINT))
				&& !amount.contains(" ")) {

			System.out.println("$#3631#"); if (matchPositiveInteger(amount)) {
				BigDecimalValidator validator = CurrencyValidator.getInstance();
				BigDecimal bdamount = validator.validate(amount, Locale.US);
				System.out.println("$#3632#"); if (bdamount == null) {
					throw new Exception("Cannot parse " + amount);
				} else {
					System.out.println("$#3633#"); return bdamount;
				}
			} else {
				throw new Exception("Not a positive integer "
						+ amount);
			}

		} else {
			//TODO should not go this path in this current release
			StringBuffer pat = new StringBuffer();

			System.out.println("$#3634#"); if (!StringUtils.isBlank(Character.toString(THOUSANDPOINT))) {
				pat.append("\\d{1,3}(" + THOUSANDPOINT + "?\\d{3})*");
			}

			pat.append("(\\" + DECIMALPOINT + "\\d{1," + DECIMALCOUNT + "})");

			Pattern pattern = Pattern.compile(pat.toString());
			Matcher matcher = pattern.matcher(amount);

			System.out.println("$#3635#"); if (matcher.matches()) {

				Locale locale = Constants.DEFAULT_LOCALE;
				//TODO validate amount using old test case
				if (DECIMALPOINT == ',') {
					locale = Locale.GERMAN;
				}

				BigDecimalValidator validator = CurrencyValidator.getInstance();
				BigDecimal bdamount = validator.validate(amount, locale);

				System.out.println("$#3636#"); return bdamount;
			} else {
				throw new Exception("Cannot parse " + amount);
			}
		}

	}
	
	public BigDecimal getOrderProductTotalPrice(MerchantStore store, OrderProduct orderProduct) {
		
		BigDecimal finalPrice = orderProduct.getOneTimeCharge();
		finalPrice = finalPrice.multiply(new BigDecimal(orderProduct.getProductQuantity()));
		System.out.println("$#3637#"); return finalPrice;
	}
	
	/**
	 * Determines if a ProductPrice has a discount
	 * @param productPrice
	 * @return
	 */
	public boolean hasDiscount(ProductPrice productPrice) {
		
		
		Date today = new Date();

		//calculate discount price
		boolean hasDiscount = false;
		System.out.println("$#3638#"); if(productPrice.getProductPriceSpecialStartDate()!=null
				|| productPrice.getProductPriceSpecialEndDate()!=null) {
			
			
			System.out.println("$#3640#"); if(productPrice.getProductPriceSpecialStartDate()!=null) {
				System.out.println("$#3641#"); if(productPrice.getProductPriceSpecialStartDate().before(today)) {
					System.out.println("$#3642#"); if(productPrice.getProductPriceSpecialEndDate()!=null) {
							System.out.println("$#3643#"); if(productPrice.getProductPriceSpecialEndDate().after(today)) {
								hasDiscount = true;
							}
					} 
				}
			}
		}
		
		System.out.println("$#3645#"); System.out.println("$#3644#"); return hasDiscount;
		
		
		
	}
	
	private boolean matchPositiveInteger(String amount) {

		Pattern pattern = Pattern.compile("^[+]?\\d*$");
		Matcher matcher = pattern.matcher(amount);
		System.out.println("$#3646#"); if (matcher.matches()) {
			System.out.println("$#3647#"); return true;

		} else {
			System.out.println("$#3648#"); return false;
		}
	}
	
	private FinalPrice calculateFinalPrice(Product product) {

		FinalPrice finalPrice = null;;
		List<FinalPrice> otherPrices = null;
		

		Set<ProductAvailability> availabilities = product.getAvailabilities();
		for(ProductAvailability availability : availabilities) {
			System.out.println("$#3649#"); if(!StringUtils.isEmpty(availability.getRegion()) && availability.getRegion().equals(Constants.ALL_REGIONS)) {//TODO REL 2.1 accept a region
				Set<ProductPrice> prices = availability.getPrices();
				for(ProductPrice price : prices) {
					
					FinalPrice p = finalPrice(price);
					System.out.println("$#3651#"); if(price.isDefaultPrice()) {
						finalPrice = p;
					} else {
						System.out.println("$#3652#"); if(otherPrices==null) {
							otherPrices = new ArrayList<FinalPrice>();
						}
						otherPrices.add(p);
					}
				}
			}
		}

		
		System.out.println("$#3653#"); if(finalPrice!=null) {
			System.out.println("$#3654#"); finalPrice.setAdditionalPrices(otherPrices);
		} else {
			System.out.println("$#3655#"); if(otherPrices!=null) {
				finalPrice = otherPrices.get(0);
			}
		}
		
		System.out.println("$#3656#"); return finalPrice;
		
		
	}
	
	private FinalPrice finalPrice(ProductPrice price) {
		
		FinalPrice finalPrice = new FinalPrice();
		BigDecimal fPrice = price.getProductPriceAmount();
		BigDecimal oPrice = price.getProductPriceAmount();

		Date today = new Date();
		//calculate discount price
		boolean hasDiscount = false;
		System.out.println("$#3657#"); if(price.getProductPriceSpecialStartDate()!=null
				|| price.getProductPriceSpecialEndDate()!=null) {
			
			
			System.out.println("$#3659#"); if(price.getProductPriceSpecialStartDate()!=null) {
				System.out.println("$#3660#"); if(price.getProductPriceSpecialStartDate().before(today)) {
					System.out.println("$#3661#"); if(price.getProductPriceSpecialEndDate()!=null) {
							System.out.println("$#3662#"); if(price.getProductPriceSpecialEndDate().after(today)) {
								hasDiscount = true;
								fPrice = price.getProductPriceSpecialAmount();
								System.out.println("$#3663#"); finalPrice.setDiscountEndDate(price.getProductPriceSpecialEndDate());
							}
					} 
						
				}
			}
			
			
			System.out.println("$#3664#"); if(!hasDiscount && price.getProductPriceSpecialStartDate()==null && price.getProductPriceSpecialEndDate()!=null) {
				System.out.println("$#3667#"); if(price.getProductPriceSpecialEndDate().after(today)) {
					hasDiscount = true;
					fPrice = price.getProductPriceSpecialAmount();
					System.out.println("$#3668#"); finalPrice.setDiscountEndDate(price.getProductPriceSpecialEndDate());
				}
			}
		} else {
			System.out.println("$#3670#"); System.out.println("$#3669#"); if(price.getProductPriceSpecialAmount()!=null && price.getProductPriceSpecialAmount().doubleValue()>0) {
				hasDiscount = true;
				fPrice = price.getProductPriceSpecialAmount();
				System.out.println("$#3672#"); finalPrice.setDiscountEndDate(price.getProductPriceSpecialEndDate());
			}
		}
		
		System.out.println("$#3673#"); finalPrice.setProductPrice(price);
		System.out.println("$#3674#"); finalPrice.setFinalPrice(fPrice);
		System.out.println("$#3675#"); finalPrice.setOriginalPrice(oPrice);
		
		
		System.out.println("$#3676#"); if(price.isDefaultPrice()) {
			System.out.println("$#3677#"); finalPrice.setDefaultPrice(true);
		}
		System.out.println("$#3678#"); if(hasDiscount) {
			System.out.println("$#3679#"); discountPrice(finalPrice);
		}

		
		System.out.println("$#3680#"); return finalPrice;
	}
	
	private void discountPrice(FinalPrice finalPrice) {
		
		System.out.println("$#3681#"); finalPrice.setDiscounted(true);
		
		System.out.println("$#3682#"); double arith = finalPrice.getProductPrice().getProductPriceSpecialAmount().doubleValue() / finalPrice.getProductPrice().getProductPriceAmount().doubleValue();
		System.out.println("$#3684#"); System.out.println("$#3683#"); double fsdiscount = 100 - (arith * 100);
		Float percentagediscount = new Float(fsdiscount);
		int percent = percentagediscount.intValue();
		System.out.println("$#3685#"); finalPrice.setDiscountPercent(percent);
		
		//calculate percent
		BigDecimal price = finalPrice.getOriginalPrice();
		System.out.println("$#3686#"); finalPrice.setDiscountedPrice(finalPrice.getProductPrice().getProductPriceSpecialAmount());
	}



}
