/**
 *
 */
package com.salesmanager.shop.store.controller.customer.facade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.modules.email.Email;
import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.services.customer.optin.CustomerOptinService;
import com.salesmanager.core.business.services.customer.review.CustomerReviewService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.services.shoppingcart.ShoppingCartService;
import com.salesmanager.core.business.services.system.EmailService;
import com.salesmanager.core.business.services.system.optin.OptinService;
import com.salesmanager.core.business.services.user.GroupService;
import com.salesmanager.core.business.services.user.PermissionService;
import com.salesmanager.core.business.utils.CoreConfiguration;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.customer.CustomerCriteria;
import com.salesmanager.core.model.customer.CustomerList;
import com.salesmanager.core.model.customer.review.CustomerReview;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.core.model.shoppingcart.ShoppingCart;
import com.salesmanager.core.model.system.optin.CustomerOptin;
import com.salesmanager.core.model.system.optin.Optin;
import com.salesmanager.core.model.system.optin.OptinType;
import com.salesmanager.core.model.user.Group;
import com.salesmanager.core.model.user.GroupType;
import com.salesmanager.core.model.user.Permission;
import com.salesmanager.shop.admin.model.userpassword.UserReset;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.constants.EmailConstants;
import com.salesmanager.shop.model.customer.CustomerEntity;
import com.salesmanager.shop.model.customer.PersistableCustomer;
import com.salesmanager.shop.model.customer.PersistableCustomerReview;
import com.salesmanager.shop.model.customer.ReadableCustomer;
import com.salesmanager.shop.model.customer.ReadableCustomerReview;
import com.salesmanager.shop.model.customer.UserAlreadyExistException;
import com.salesmanager.shop.model.customer.address.Address;
import com.salesmanager.shop.model.customer.optin.PersistableCustomerOptin;
import com.salesmanager.shop.populator.customer.CustomerBillingAddressPopulator;
import com.salesmanager.shop.populator.customer.CustomerDeliveryAddressPopulator;
import com.salesmanager.shop.populator.customer.CustomerEntityPopulator;
import com.salesmanager.shop.populator.customer.CustomerPopulator;
import com.salesmanager.shop.populator.customer.PersistableCustomerBillingAddressPopulator;
import com.salesmanager.shop.populator.customer.PersistableCustomerReviewPopulator;
import com.salesmanager.shop.populator.customer.PersistableCustomerShippingAddressPopulator;
import com.salesmanager.shop.populator.customer.ReadableCustomerList;
import com.salesmanager.shop.populator.customer.ReadableCustomerPopulator;
import com.salesmanager.shop.populator.customer.ReadableCustomerReviewPopulator;
import com.salesmanager.shop.store.api.exception.ConversionRuntimeException;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.utils.EmailTemplatesUtils;
import com.salesmanager.shop.utils.EmailUtils;
import com.salesmanager.shop.utils.ImageFilePath;
import com.salesmanager.shop.utils.LabelUtils;
import com.salesmanager.shop.utils.LocaleUtils;


/**
 * Customer Facade work as an abstraction layer between Controller and Service layer. It work as an
 * entry point to service layer.
 * 
 * @author Umesh Awasthi
 * @version 2.2.1, 2.8.0
 * @modified Carl Samson
 *
 */

@Service("customerFacade")
public class CustomerFacadeImpl implements CustomerFacade {

  private static final Logger LOG = LoggerFactory.getLogger(CustomerFacadeImpl.class);
  private final static int USERNAME_LENGTH = 6;

  private final static String RESET_PASSWORD_TPL = "email_template_password_reset_customer.ftl";

  public final static String ROLE_PREFIX = "ROLE_";// Spring Security 4


  @Inject
  private CustomerService customerService;

  @Inject
  private OptinService optinService;

  @Inject
  private CustomerOptinService customerOptinService;

  @Inject
  private ShoppingCartService shoppingCartService;

  @Inject
  private LanguageService languageService;

  @Inject
  private LabelUtils messages;

  @Inject
  private CountryService countryService;

  @Inject
  private GroupService groupService;

  @Inject
  private PermissionService permissionService;

  @Inject
  private ZoneService zoneService;

  @Inject
  private PasswordEncoder passwordEncoder;

  @Inject
  private EmailService emailService;

  @Inject
  private EmailTemplatesUtils emailTemplatesUtils;

  @Inject
  private AuthenticationManager customerAuthenticationManager;

  @Inject
  private CustomerReviewService customerReviewService;

  @Inject
  private CoreConfiguration coreConfiguration;
  
  @Autowired
  private CustomerPopulator customerPopulator;

  @Inject
  private EmailUtils emailUtils;

  @Inject
  @Qualifier("img")
  private ImageFilePath imageUtils;

  /**
   * Method used to fetch customer based on the username and storecode. Customer username is unique
   * to each store.
   *
   * @param userName
   * @param store
   * @throws ConversionException
   */
  @Override
  public CustomerEntity getCustomerDataByUserName(final String userName, final MerchantStore store,
      final Language language) throws Exception {
    LOG.info("Fetching customer with userName" + userName);
    Customer customer = customerService.getByNick(userName);

				System.out.println("$#12549#"); if (customer != null) {
      LOG.info("Found customer, converting to CustomerEntity");
      try {
        CustomerEntityPopulator customerEntityPopulator = new CustomerEntityPopulator();
								System.out.println("$#12550#"); return customerEntityPopulator.populate(customer, store, language); // store, language

      } catch (ConversionException ex) {
        LOG.error("Error while converting Customer to CustomerEntity", ex);
        throw new Exception(ex);
      }
    }

    return null;

  }


  /*
   * (non-Javadoc)
   * 
   * @see com.salesmanager.web.shop.controller.customer.facade#mergeCart(final Customer
   * customerModel, final String sessionShoppingCartId ,final MerchantStore store,final Language
   * language)
   */
  @Override
  public ShoppingCart mergeCart(final Customer customerModel, final String sessionShoppingCartId,
      final MerchantStore store, final Language language) throws Exception {

    LOG.debug("Starting merge cart process");
				System.out.println("$#12551#"); if (customerModel != null) {
      ShoppingCart customerCart = shoppingCartService.getShoppingCart(customerModel);
						System.out.println("$#12552#"); if (StringUtils.isNotBlank(sessionShoppingCartId)) {
        ShoppingCart sessionShoppingCart =
            shoppingCartService.getByCode(sessionShoppingCartId, store);
								System.out.println("$#12553#"); if (sessionShoppingCart != null) {
										System.out.println("$#12554#"); if (customerCart == null) {
												System.out.println("$#12555#"); if (sessionShoppingCart.getCustomerId() == null) {// saved shopping cart does not belong
                                                              // to a customer
              LOG.debug("Not able to find any shoppingCart with current customer");
              // give it to the customer
														System.out.println("$#12556#"); sessionShoppingCart.setCustomerId(customerModel.getId());
														System.out.println("$#12557#"); shoppingCartService.saveOrUpdate(sessionShoppingCart);
              customerCart = shoppingCartService.getById(sessionShoppingCart.getId(), store);
														System.out.println("$#12558#"); return customerCart;
              // return populateShoppingCartData(customerCart,store,language);
            } else {
              return null;
            }
          } else {
												System.out.println("$#12559#"); if (sessionShoppingCart.getCustomerId() == null) {// saved shopping cart does not belong
                                                              // to a customer
              // assign it to logged in user
              LOG.debug("Customer shopping cart as well session cart is available, merging carts");
              customerCart =
                  shoppingCartService.mergeShoppingCarts(customerCart, sessionShoppingCart, store);
              customerCart = shoppingCartService.getById(customerCart.getId(), store);
														System.out.println("$#12560#"); return customerCart;
              // return populateShoppingCartData(customerCart,store,language);
            } else {
              if (sessionShoppingCart.getCustomerId().longValue() == customerModel.getId()
                  .longValue()) {
                if (!customerCart.getShoppingCartCode()
                    .equals(sessionShoppingCart.getShoppingCartCode())) {
                  // merge carts
                  LOG.info("Customer shopping cart as well session cart is available");
                  customerCart = shoppingCartService.mergeShoppingCarts(customerCart,
                      sessionShoppingCart, store);
                  customerCart = shoppingCartService.getById(customerCart.getId(), store);
																		System.out.println("$#12563#"); return customerCart;
                  // return populateShoppingCartData(customerCart,store,language);
                } else {
																		System.out.println("$#12564#"); return customerCart;
                  // return populateShoppingCartData(sessionShoppingCart,store,language);
                }
              } else {
                // the saved cart belongs to another user
                return null;
              }
            }


          }
        }
      } else {
								System.out.println("$#12565#"); if (customerCart != null) {
          // return populateShoppingCartData(customerCart,store,language);
										System.out.println("$#12566#"); return customerCart;
        }
        return null;

      }
    }
    LOG.info(
        "Seems some issue with system, unable to find any customer after successful authentication");
    return null;

  }



  @Override
  public Customer getCustomerByUserName(String userName, MerchantStore store) throws Exception {
				System.out.println("$#12567#"); return customerService.getByNick(userName, store.getId());
  }
  
  @Override
  public ReadableCustomer getByUserName(String userName, MerchantStore merchantStore, Language language) {
				System.out.println("$#12568#"); Validate.notNull(userName,"Username cannot be null");
				System.out.println("$#12569#"); Validate.notNull(merchantStore,"MerchantStore cannot be null");

    Customer customerModel = getCustomerByNickAndStoreId(userName, merchantStore);
				System.out.println("$#12570#"); return convertCustomerToReadableCustomer(customerModel, merchantStore, language);
  }

  private Customer getCustomerByNickAndStoreId(String userName, MerchantStore merchantStore) {
				System.out.println("$#12571#"); return Optional.ofNullable(customerService.getByNick(userName, merchantStore.getId()))
        .orElseThrow(() -> new ResourceNotFoundException("No Customer found for ID : " + userName));
  }


  /**
   * <p>
   * Method to check if given user exists for given username under given store. System treat
   * username as unique for a given store, customer is not allowed to use same username twice for a
   * given store, however it can be used for different stores.
   * </p>
   * 
   * @param userName Customer slected userName
   * @param store store for which customer want to register
   * @return boolean flag indicating if user exists for given store or not
   * @throws Exception
   * 
   */
  @Override
  public boolean checkIfUserExists(final String userName, final MerchantStore store)
      throws Exception {
				System.out.println("$#12573#"); if (StringUtils.isNotBlank(userName) && store != null) {
      Customer customer = customerService.getByNick(userName, store.getId());
						System.out.println("$#12575#"); if (customer != null) {
        LOG.info("Customer with userName {} already exists for store {} ", userName,
            store.getStorename());
								System.out.println("$#12576#"); return true;
      }

      LOG.info("No customer found with userName {} for store {} ", userName, store.getStorename());
						System.out.println("$#12577#"); return false;

    }
    LOG.info("Either userName is empty or we have not found any value for store");
				System.out.println("$#12578#"); return false;
  }


  @Override
  public PersistableCustomer registerCustomer(final PersistableCustomer customer,
      final MerchantStore merchantStore, Language language) throws Exception {
    LOG.info("Starting customer registration process..");

				System.out.println("$#12579#"); if (userExist(customer.getUserName())) {
      throw new UserAlreadyExistException("User already exist");
    }

    Customer customerModel = getCustomerModel(customer, merchantStore, language);
				System.out.println("$#12580#"); if (customerModel == null) {
      LOG.equals("Unable to create customer in system");
      // throw new CustomerRegistrationException( "Unable to register customer" );
      throw new Exception("Unable to register customer");
    }

    LOG.info("About to persist customer to database.");
				System.out.println("$#12581#"); customerService.saveOrUpdate(customerModel);

    LOG.info("Returning customer data to controller..");
    // return customerEntityPoulator(customerModel,merchantStore);
				System.out.println("$#12582#"); customer.setId(customerModel.getId());
				System.out.println("$#12583#"); return customer;
  }

  @Override
  public Customer getCustomerModel(final PersistableCustomer customer,
      final MerchantStore merchantStore, Language language) throws Exception {

    LOG.info("Starting to populate customer model from customer data");
    Customer customerModel = null;

    customerModel = customerPopulator.populate(customer, merchantStore, language);
    // we are creating or resetting a customer
				System.out.println("$#12584#"); if (StringUtils.isBlank(customerModel.getPassword())
        && !StringUtils.isBlank(customer.getPassword())) {
						System.out.println("$#12586#"); customerModel.setPassword(customer.getPassword());
    }
    // set groups
				System.out.println("$#12587#"); if (!StringUtils.isBlank(customerModel.getPassword())
        && !StringUtils.isBlank(customerModel.getNick())) {
						System.out.println("$#12589#"); customerModel.setPassword(passwordEncoder.encode(customer.getPassword()));
						System.out.println("$#12590#"); setCustomerModelDefaultProperties(customerModel, merchantStore);
    }


				System.out.println("$#12591#"); return customerModel;

  }



  @Override
  public void setCustomerModelDefaultProperties(Customer customer, MerchantStore store)
      throws Exception {
				System.out.println("$#12592#"); Validate.notNull(customer, "Customer object cannot be null");
				System.out.println("$#12593#"); if (customer.getId() == null || customer.getId() == 0) {
						System.out.println("$#12595#"); if (StringUtils.isBlank(customer.getNick())) {
        String userName = UserReset.generateRandomString(USERNAME_LENGTH);
								System.out.println("$#12596#"); customer.setNick(userName);
      }
						System.out.println("$#12597#"); if (StringUtils.isBlank(customer.getPassword())) {
        String password = UserReset.generateRandomString();
        String encodedPassword = passwordEncoder.encode(password);
								System.out.println("$#12598#"); customer.setPassword(encodedPassword);
      }
    }

				System.out.println("$#12599#"); if (CollectionUtils.isEmpty(customer.getGroups())) {
      List<Group> groups = getListOfGroups(GroupType.CUSTOMER);
      for (Group group : groups) {
								System.out.println("$#12600#"); if (group.getGroupName().equals(Constants.GROUP_CUSTOMER)) {
          customer.getGroups().add(group);
        }
      }

    }

  }



  public void authenticate(Customer customer, String userName, String password) throws Exception {

				System.out.println("$#12601#"); Validate.notNull(customer, "Customer cannot be null");

    Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
    GrantedAuthority role =
        new SimpleGrantedAuthority(ROLE_PREFIX + Constants.PERMISSION_CUSTOMER_AUTHENTICATED);// required
                                                                                              // to
                                                                                              // login
    authorities.add(role);
    List<Integer> groupsId = new ArrayList<Integer>();
    List<Group> groups = customer.getGroups();
				System.out.println("$#12602#"); if (groups != null) {
      for (Group group : groups) {
        groupsId.add(group.getId());

      }
						System.out.println("$#12604#"); System.out.println("$#12603#"); if (groupsId != null && groupsId.size() > 0) {
        List<Permission> permissions = permissionService.getPermissions(groupsId);
        for (Permission permission : permissions) {
          GrantedAuthority auth = new SimpleGrantedAuthority(permission.getPermissionName());
          authorities.add(auth);
        }
      }
    }

    Authentication authenticationToken =
        new UsernamePasswordAuthenticationToken(userName, password, authorities);

    Authentication authentication = customerAuthenticationManager.authenticate(authenticationToken);

				System.out.println("$#12606#"); SecurityContextHolder.getContext().setAuthentication(authentication);

  }


  @Override
  public Address getAddress(Long userId, final MerchantStore merchantStore,
      boolean isBillingAddress) throws Exception {
    LOG.info("Fetching customer for id {} ", userId);
    Address address = null;
    final Customer customerModel = customerService.getById(userId);

				System.out.println("$#12607#"); if (customerModel == null) {
      LOG.error("Customer with ID {} does not exists..", userId);
      // throw new CustomerNotFoundException( "customer with given id does not exists" );
      throw new Exception("customer with given id does not exists");
    }

				System.out.println("$#12608#"); if (isBillingAddress) {
      LOG.info("getting billing address..");
      CustomerBillingAddressPopulator billingAddressPopulator =
          new CustomerBillingAddressPopulator();
      address = billingAddressPopulator.populate(customerModel, merchantStore,
          merchantStore.getDefaultLanguage());
						System.out.println("$#12609#"); address.setBillingAddress(true);
						System.out.println("$#12610#"); return address;
    }

    LOG.info("getting Delivery address..");
    CustomerDeliveryAddressPopulator deliveryAddressPopulator =
        new CustomerDeliveryAddressPopulator();
				System.out.println("$#12611#"); return deliveryAddressPopulator.populate(customerModel, merchantStore,
        merchantStore.getDefaultLanguage());

  }


  @Override
  public void updateAddress(Long userId, MerchantStore merchantStore, Address address,
      final Language language) throws Exception {

    Customer customerModel = customerService.getById(userId);
    Map<String, Country> countriesMap = countryService.getCountriesMap(language);
    Country country = countriesMap.get(address.getCountry());

				System.out.println("$#12612#"); if (customerModel == null) {
      LOG.error("Customer with ID {} does not exists..", userId);
      // throw new CustomerNotFoundException( "customer with given id does not exists" );
      throw new Exception("customer with given id does not exists");

    }
				System.out.println("$#12613#"); if (address.isBillingAddress()) {
      LOG.info("updating customer billing address..");
      PersistableCustomerBillingAddressPopulator billingAddressPopulator =
          new PersistableCustomerBillingAddressPopulator();
      customerModel = billingAddressPopulator.populate(address, customerModel, merchantStore,
          merchantStore.getDefaultLanguage());
						System.out.println("$#12614#"); customerModel.getBilling().setCountry(country);
						System.out.println("$#12615#"); if (StringUtils.isNotBlank(address.getZone())) {
        Zone zone = zoneService.getByCode(address.getZone());
								System.out.println("$#12616#"); if (zone == null) {
          throw new ConversionException("Unsuported zone code " + address.getZone());
        }
								System.out.println("$#12617#"); customerModel.getBilling().setZone(zone);
								System.out.println("$#12618#"); customerModel.getBilling().setState(null);

      } else {
								System.out.println("$#12619#"); customerModel.getBilling().setZone(null);
      }

    } else {
      LOG.info("updating customer shipping address..");
      PersistableCustomerShippingAddressPopulator shippingAddressPopulator =
          new PersistableCustomerShippingAddressPopulator();
      customerModel = shippingAddressPopulator.populate(address, customerModel, merchantStore,
          merchantStore.getDefaultLanguage());
						System.out.println("$#12620#"); customerModel.getDelivery().setCountry(country);
						System.out.println("$#12621#"); if (StringUtils.isNotBlank(address.getZone())) {
        Zone zone = zoneService.getByCode(address.getZone());
								System.out.println("$#12622#"); if (zone == null) {
          throw new ConversionException("Unsuported zone code " + address.getZone());
        }

								System.out.println("$#12623#"); customerModel.getDelivery().setZone(zone);
								System.out.println("$#12624#"); customerModel.getDelivery().setState(null);

      } else {
								System.out.println("$#12625#"); customerModel.getDelivery().setZone(null);
      }

    }


    // same update address with customer model
				System.out.println("$#12626#"); this.customerService.saveOrUpdate(customerModel);

  }

  @Override
  public ReadableCustomer getCustomerById(final Long id, final MerchantStore merchantStore,
      final Language language) {

    Customer customerModel = Optional.ofNullable(customerService.getById(id))
        .orElseThrow(() -> new ResourceNotFoundException("No Customer found for ID : " + id));

				System.out.println("$#12628#"); return convertCustomerToReadableCustomer(customerModel, merchantStore, language);
  }


  @Override
  public Customer populateCustomerModel(Customer customerModel, PersistableCustomer customer,
      MerchantStore merchantStore, Language language) throws Exception {
      LOG.info("Starting to populate customer model from customer data");


    customerModel = customerPopulator.populate(customer, customerModel, merchantStore, language);

    LOG.info("About to persist customer to database.");
				System.out.println("$#12629#"); customerService.saveOrUpdate(customerModel);
				System.out.println("$#12630#"); return customerModel;
  }


  @Override
  public ReadableCustomer create(PersistableCustomer customer, MerchantStore store, Language language) {

	System.out.println("$#12631#"); Validate.notNull(customer, "Customer cannot be null");
	System.out.println("$#12632#"); Validate.notNull(customer.getEmailAddress(), "Customer email address is required");

	//set customer user name
	System.out.println("$#12633#"); customer.setUserName(customer.getEmailAddress());
				System.out.println("$#12634#"); if (userExist(customer.getUserName())) {
      throw new ServiceRuntimeException("User already exist");
    }
    //end user exists

    Customer customerToPopulate = convertPersistableCustomerToCustomer(customer, store);
    try {
		System.out.println("$#12635#"); setCustomerModelDefaultProperties(customerToPopulate, store);
	} catch (Exception e) {
		throw new ServiceRuntimeException("Cannot set default customer properties",e);
	}
				System.out.println("$#12636#"); saveCustomer(customerToPopulate);
				System.out.println("$#12637#"); customer.setId(customerToPopulate.getId());

				System.out.println("$#12638#"); notifyNewCustomer(customer, store, customerToPopulate.getDefaultLanguage());
    //convert to readable
				System.out.println("$#12639#"); return convertCustomerToReadableCustomer(customerToPopulate, store, language);
    

  }

  private void saveCustomer(Customer customerToPopulate) {
    try{
						System.out.println("$#12640#"); customerService.save(customerToPopulate);
    } catch (ServiceException exception) {
      throw new ServiceRuntimeException(exception);
    }

  }

  private boolean userExist(String userName) {
				System.out.println("$#12642#"); System.out.println("$#12641#"); return Optional.ofNullable(customerService.getByNick(userName))
        .isPresent();
  }

  private List<Group> getListOfGroups(GroupType groupType) {
    try{
						System.out.println("$#12643#"); return groupService.listGroup(groupType);
    } catch (ServiceException e) {
      throw new ServiceRuntimeException(e);
    }

  }

  private Customer convertPersistableCustomerToCustomer(PersistableCustomer customer, MerchantStore store) {

    Customer cust = new Customer();

    try{
      customerPopulator.populate(customer, cust, store, store.getDefaultLanguage());
    } catch (ConversionException e) {
      throw new ConversionRuntimeException(e);
    }


    List<Group> groups = getListOfGroups(GroupType.CUSTOMER);
				System.out.println("$#12644#"); cust.setGroups(groups);

    String password = customer.getPassword();
				System.out.println("$#12645#"); if (StringUtils.isBlank(password)) {
      password = UserReset.generateRandomString();
						System.out.println("$#12646#"); customer.setPassword(password);
    }


				System.out.println("$#12647#"); return cust;

  }

  @Async
  private void notifyNewCustomer(PersistableCustomer customer, MerchantStore store, Language lang) {
		System.out.println("$#12648#"); System.out.println("Customer notification");
		long startTime = System.nanoTime();
	Locale customerLocale = LocaleUtils.getLocale(lang);
    String shopSchema = coreConfiguration.getProperty("SHOP_SCHEME");
				System.out.println("$#12649#"); emailTemplatesUtils.sendRegistrationEmail(customer, store, customerLocale, shopSchema);
    long endTime = System.nanoTime();
				System.out.println("$#12651#"); System.out.println("$#12650#"); long duration = (endTime - startTime)/1000;
				System.out.println("$#12652#"); System.out.println("End Notification " + duration);
  }


  @Override
  public PersistableCustomer update(PersistableCustomer customer, MerchantStore store) {

				System.out.println("$#12653#"); if (customer.getId() == null || customer.getId() == 0) {
      throw new ServiceRuntimeException("Can't update a customer with null id");
    }

    Customer cust = customerService.getById(customer.getId());

    try{
      customerPopulator.populate(customer, cust, store, store.getDefaultLanguage());
    } catch (ConversionException e) {
      throw new ConversionRuntimeException(e);
    }

    String password = customer.getPassword();
				System.out.println("$#12655#"); if (StringUtils.isBlank(password)) {
      password = UserReset.generateRandomString();
						System.out.println("$#12656#"); customer.setPassword(password);
    }

				System.out.println("$#12657#"); saveCustomer(cust);
				System.out.println("$#12658#"); customer.setId(cust.getId());

				System.out.println("$#12659#"); return customer;
  }


  @Override
  public PersistableCustomerReview saveOrUpdateCustomerReview(PersistableCustomerReview reviewTO, MerchantStore store,
      Language language) {
    CustomerReview review = convertPersistableCustomerReviewToCustomerReview(reviewTO, store, language);
				System.out.println("$#12660#"); createReview(review);
				System.out.println("$#12661#"); reviewTO.setId(review.getId());
				System.out.println("$#12662#"); return reviewTO;
  }

  private void createReview(CustomerReview review) {
    try{
						System.out.println("$#12663#"); customerReviewService.create(review);
    } catch (ServiceException e) {
      throw new ServiceRuntimeException(e);
    }

  }

  private CustomerReview convertPersistableCustomerReviewToCustomerReview(
      PersistableCustomerReview review, MerchantStore store, Language language) {
    PersistableCustomerReviewPopulator populator = new PersistableCustomerReviewPopulator();
				System.out.println("$#12664#"); populator.setCustomerService(customerService);
				System.out.println("$#12665#"); populator.setLanguageService(languageService);
    try{
						System.out.println("$#12666#"); return populator.populate(review, new CustomerReview(), store, language);
    } catch (ConversionException e) {
      throw new ConversionRuntimeException(e);
    }
  }


  @Override
  public List<ReadableCustomerReview> getAllCustomerReviewsByReviewed(Long customerId,
      MerchantStore store, Language language) {

    //customer exist
    Customer customer = getCustomerById(customerId);
				System.out.println("$#12667#"); Validate.notNull(customer, "Reviewed customer cannot be null");

				System.out.println("$#12668#"); return customerReviewService.getByReviewedCustomer(customer)
        .stream()
        .map(
            customerReview ->
                convertCustomerReviewToReadableCustomerReview(customerReview, store, language))
        .collect(Collectors.toList());
  }

  private ReadableCustomerReview convertCustomerReviewToReadableCustomerReview(
      CustomerReview customerReview, MerchantStore store, Language language) {
    try{
      ReadableCustomerReviewPopulator populator = new ReadableCustomerReviewPopulator();
						System.out.println("$#12670#"); return populator.populate(customerReview, new ReadableCustomerReview(), store, language);
    } catch (ConversionException e){
      throw new ConversionRuntimeException(e);
    }
  }

  private Customer getCustomerById(Long customerId) {
				System.out.println("$#12671#"); return Optional.ofNullable(customerService.getById(customerId))
          .orElseThrow(() -> new ResourceNotFoundException("Customer id " + customerId + " does not exists"));
  }


  @Override
  public void deleteCustomerReview(Long customerId, Long reviewId, MerchantStore store, Language language) {

    CustomerReview customerReview = getCustomerReviewById(reviewId);

				System.out.println("$#12673#"); if(!customerReview.getReviewedCustomer().getId().equals(customerId)) {
      throw new ResourceNotFoundException("Customer review with id " + reviewId + " does not exist for this customer");
    }
				System.out.println("$#12674#"); deleteCustomerReview(customerReview);
  }

  private CustomerReview getCustomerReviewById(Long reviewId) {
				System.out.println("$#12675#"); return Optional.ofNullable(customerReviewService.getById(reviewId))
        .orElseThrow(() -> new ResourceNotFoundException("Customer review with id " + reviewId + " does not exist"));
  }

  private void deleteCustomerReview(CustomerReview review) {
    try{
						System.out.println("$#12677#"); customerReviewService.delete(review);
    } catch (ServiceException e) {
      throw new ServiceRuntimeException(e);
    }
  }


  @Override
  public void optinCustomer(PersistableCustomerOptin optin, MerchantStore store) {
    // check if customer optin exists
    Optin optinDef = getOptinByCode(store);

    CustomerOptin customerOptin = getCustomerOptinByEmailAddress(optin.getEmail(), store, OptinType.NEWSLETTER);

				System.out.println("$#12678#"); if (customerOptin != null) {
      // exists update
						System.out.println("$#12679#"); customerOptin.setEmail(optin.getEmail());
						System.out.println("$#12680#"); customerOptin.setFirstName(optin.getFirstName());
						System.out.println("$#12681#"); customerOptin.setLastName(optin.getLastName());
    } else {
      customerOptin = new com.salesmanager.core.model.system.optin.CustomerOptin();
						System.out.println("$#12682#"); customerOptin.setEmail(optin.getEmail());
						System.out.println("$#12683#"); customerOptin.setFirstName(optin.getFirstName());
						System.out.println("$#12684#"); customerOptin.setLastName(optin.getLastName());
						System.out.println("$#12685#"); customerOptin.setOptinDate(new Date());
						System.out.println("$#12686#"); customerOptin.setOptin(optinDef);
						System.out.println("$#12687#"); customerOptin.setMerchantStore(store);
    }
				System.out.println("$#12688#"); saveCustomerOption(customerOptin);
  }

  private void saveCustomerOption(CustomerOptin customerOptin) {
    try {
						System.out.println("$#12689#"); customerOptinService.save(customerOptin);
    } catch (ServiceException e) {
      throw new ServiceRuntimeException(e);
    }
  }

  private Optin getOptinByCode(MerchantStore store) {
    try{
						System.out.println("$#12690#"); return Optional.ofNullable(optinService.getOptinByCode(store, OptinType.NEWSLETTER.name()))
          .orElseThrow(() -> new ResourceNotFoundException("Optin newsletter does not exist"));
    } catch (ServiceException e){
      throw new ServiceRuntimeException(e);
    }
  }

  private CustomerOptin getCustomerOptinByEmailAddress(String optinEmail,
      MerchantStore store, OptinType optinType) {
    try{
						System.out.println("$#12692#"); return customerOptinService.findByEmailAddress(store, optinEmail, optinType.name());
    } catch (ServiceException e){
      throw new ServiceRuntimeException(e);
    }

  }


  @Override
  public void resetPassword(Customer customer, MerchantStore store, Language language)
      throws Exception {


    String password = UserReset.generateRandomString();
    String encodedPassword = passwordEncoder.encode(password);

				System.out.println("$#12693#"); customer.setPassword(encodedPassword);
				System.out.println("$#12694#"); customerService.saveOrUpdate(customer);

    Locale locale = languageService.toLocale(language, store);

    // send email

    try {

      // creation of a user, send an email
      String[] storeEmail = {store.getStoreEmailAddress()};


      Map<String, String> templateTokens =
          emailUtils.createEmailObjectsMap(imageUtils.getContextPath(), store, messages, locale);
      templateTokens.put(EmailConstants.LABEL_HI, messages.getMessage("label.generic.hi", locale));
      templateTokens.put(EmailConstants.EMAIL_CUSTOMER_FIRSTNAME,
          customer.getBilling().getFirstName());
      templateTokens.put(EmailConstants.EMAIL_CUSTOMER_LASTNAME,
          customer.getBilling().getLastName());
      templateTokens.put(EmailConstants.EMAIL_RESET_PASSWORD_TXT,
          messages.getMessage("email.customer.resetpassword.text", locale));
      templateTokens.put(EmailConstants.EMAIL_CONTACT_OWNER,
          messages.getMessage("email.contactowner", storeEmail, locale));
      templateTokens.put(EmailConstants.EMAIL_PASSWORD_LABEL,
          messages.getMessage("label.generic.password", locale));
      templateTokens.put(EmailConstants.EMAIL_CUSTOMER_PASSWORD, password);


      Email email = new Email();
						System.out.println("$#12695#"); email.setFrom(store.getStorename());
						System.out.println("$#12696#"); email.setFromEmail(store.getStoreEmailAddress());
						System.out.println("$#12697#"); email.setSubject(messages.getMessage("label.generic.changepassword", locale));
						System.out.println("$#12698#"); email.setTo(customer.getEmailAddress());
						System.out.println("$#12699#"); email.setTemplateName(RESET_PASSWORD_TPL);
						System.out.println("$#12700#"); email.setTemplateTokens(templateTokens);



						System.out.println("$#12701#"); emailService.sendHtmlEmail(store, email);

    } catch (Exception e) {
      LOG.error("Cannot send email to customer", e);
    }


  }

  @Override
  public ReadableCustomer getCustomerByNick(String userName, MerchantStore merchantStore,
      Language language) {
    Customer customer = getByNick(userName);
				System.out.println("$#12702#"); return convertCustomerToReadableCustomer(customer, merchantStore, language);
  }

  @Override
  public void deleteByNick(String userName) {
    Customer customer = getByNick(userName);
				System.out.println("$#12703#"); delete(customer);
  }

  private Customer getByNick(String userName) {
				System.out.println("$#12704#"); return Optional.ofNullable(customerService.getByNick(userName))
        .orElseThrow(() -> new ResourceNotFoundException("No Customer found for ID : " + userName));
  }

  @Override
  public void delete(Customer entity) {
    try{
						System.out.println("$#12706#"); customerService.delete(entity);
    } catch (ServiceException e) {
      throw new ServiceRuntimeException(e);
    }
  }

  @Override
  public ReadableCustomerList getListByStore(MerchantStore store, CustomerCriteria criteria,
      Language language) {
    CustomerList customerList = customerService.getListByStore(store, criteria);
				System.out.println("$#12707#"); return convertCustomerListToReadableCustomerList(customerList, store, language);
  }

  private ReadableCustomerList convertCustomerListToReadableCustomerList(
      CustomerList customerList, MerchantStore store, Language language) {
    List<ReadableCustomer> readableCustomers = customerList.getCustomers()
        .stream()
        .map(customer -> convertCustomerToReadableCustomer(customer, store, language))
        .collect(Collectors.toList());

    ReadableCustomerList readableCustomerList = new ReadableCustomerList();
				System.out.println("$#12709#"); readableCustomerList.setCustomers(readableCustomers);
				System.out.println("$#12710#"); readableCustomerList.setTotalPages(customerList.getTotalCount());
				System.out.println("$#12711#"); return readableCustomerList;
  }

  private ReadableCustomer convertCustomerToReadableCustomer(Customer customer, MerchantStore merchantStore, Language language) {
    ReadableCustomerPopulator populator = new ReadableCustomerPopulator();
    try{
						System.out.println("$#12712#"); return populator.populate(customer, new ReadableCustomer(), merchantStore, language);
    } catch (ConversionException e) {
      throw new ConversionRuntimeException(e);
    }
  }

  @Override
  public PersistableCustomerReview createCustomerReview(
      Long customerId,
      PersistableCustomerReview review,
      MerchantStore merchantStore,
      Language language) {

    // rating already exist
    Optional<CustomerReview> customerReview =
        Optional.ofNullable(
            customerReviewService.getByReviewerAndReviewed(review.getCustomerId(), customerId));

				System.out.println("$#12713#"); if(customerReview.isPresent()) {
      throw new ServiceRuntimeException("A review already exist for this customer and product");
    }

    // rating maximum 5
				System.out.println("$#12715#"); System.out.println("$#12714#"); if (review.getRating() > Constants.MAX_REVIEW_RATING_SCORE) {
      throw new ServiceRuntimeException("Maximum rating score is " + Constants.MAX_REVIEW_RATING_SCORE);
    }

				System.out.println("$#12716#"); review.setReviewedCustomer(customerId);

    saveOrUpdateCustomerReview(review, merchantStore, language);

				System.out.println("$#12717#"); return review;
  }

  @Override
  public PersistableCustomerReview updateCustomerReview(Long id, Long reviewId, PersistableCustomerReview review,
      MerchantStore store, Language language) {

    CustomerReview customerReview = getCustomerReviewById(reviewId);

				System.out.println("$#12718#"); if(! customerReview.getReviewedCustomer().getId().equals(id)) {
      throw new ResourceNotFoundException("Customer review with id " + reviewId + " does not exist for this customer");
    }

    //rating maximum 5
				System.out.println("$#12720#"); System.out.println("$#12719#"); if(review.getRating()>Constants.MAX_REVIEW_RATING_SCORE) {
      throw new ServiceRuntimeException("Maximum rating score is " + Constants.MAX_REVIEW_RATING_SCORE);
    }

				System.out.println("$#12721#"); review.setReviewedCustomer(id);
				System.out.println("$#12722#"); return review;
  }


  @Override
  public void deleteById(Long id) {
    Customer customer = getCustomerById(id);
				System.out.println("$#12723#"); delete(customer);
    
  }


  @Override
  public void updateAddress(PersistableCustomer customer, MerchantStore store) {
				System.out.println("$#12724#"); Validate.notNull(customer.getBilling(), "Billing address can not be null");
				System.out.println("$#12725#"); Validate.notNull(customer.getBilling().getAddress(), "Billing address can not be null");
				System.out.println("$#12726#"); Validate.notNull(customer.getBilling().getCity(), "Billing city can not be null");
				System.out.println("$#12727#"); Validate.notNull(customer.getBilling().getPostalCode(), "Billing postal code can not be null");
				System.out.println("$#12728#"); Validate.notNull(customer.getBilling().getCountryCode(), "Billing country can not be null");

				System.out.println("$#12729#"); customer.getBilling().setBillingAddress(true);
    
				System.out.println("$#12730#"); if(customer.getDelivery() == null) {
						System.out.println("$#12731#"); customer.setDelivery(customer.getBilling());
						System.out.println("$#12732#"); customer.getDelivery().setBillingAddress(false);
    } else {
						System.out.println("$#12733#"); Validate.notNull(customer.getDelivery(), "Delivery address can not be null");
						System.out.println("$#12734#"); Validate.notNull(customer.getDelivery().getAddress(), "Delivery address can not be null");
						System.out.println("$#12735#"); Validate.notNull(customer.getDelivery().getCity(), "Delivery city can not be null");
						System.out.println("$#12736#"); Validate.notNull(customer.getDelivery().getPostalCode(), "Delivery postal code can not be null");
						System.out.println("$#12737#"); Validate.notNull(customer.getDelivery().getCountryCode(), "Delivery country can not be null");

      
    }
    
    try {
      //update billing
						System.out.println("$#12738#"); updateAddress(customer.getId(), store, customer.getBilling(), store.getDefaultLanguage());
      //update delivery
						System.out.println("$#12739#"); updateAddress(customer.getId(), store, customer.getDelivery(), store.getDefaultLanguage());
    } catch (Exception e) {
      throw new ServiceRuntimeException("Error while updating customer address");
    }
    

  }


  @Override
  public void updateAddress(String userName, PersistableCustomer customer, MerchantStore store) {
    
    ReadableCustomer customerModel = getByUserName(userName, store, store.getDefaultLanguage());
				System.out.println("$#12740#"); customer.setId(customerModel.getId());
				System.out.println("$#12741#"); customer.setUserName(userName);
				System.out.println("$#12742#"); updateAddress(customer, store);
    
  }


  @Override
  public PersistableCustomer update(String userName, PersistableCustomer customer,
      MerchantStore store) {
    ReadableCustomer customerModel = getByUserName(userName, store, store.getDefaultLanguage());
				System.out.println("$#12743#"); customer.setId(customerModel.getId());
				System.out.println("$#12744#"); customer.setUserName(userName);
				System.out.println("$#12745#"); return this.update(customer, store);
  }


  @Override
  public boolean passwordMatch(String rawPassword, Customer customer) {
				System.out.println("$#12747#"); System.out.println("$#12746#"); return passwordEncoder.matches(rawPassword, customer.getPassword());
  }


  @Override
  public void changePassword(Customer customer, String newPassword) {
    String encoded = passwordEncoder.encode(newPassword);
				System.out.println("$#12748#"); customer.setPassword(encoded);
    try {
						System.out.println("$#12749#"); customerService.update(customer);
    } catch (ServiceException e) {
      throw new ServiceRuntimeException("Exception while changing password", e);
    }
    
  }
}
