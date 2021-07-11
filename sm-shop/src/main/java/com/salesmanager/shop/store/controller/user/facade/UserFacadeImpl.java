package com.salesmanager.shop.store.controller.user.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.merchant.MerchantStoreService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.user.PermissionService;
import com.salesmanager.core.business.services.user.UserService;
import com.salesmanager.core.model.common.Criteria;
import com.salesmanager.core.model.common.GenericEntityList;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.user.Group;
import com.salesmanager.core.model.user.Permission;
import com.salesmanager.core.model.user.User;
import com.salesmanager.core.model.user.UserCriteria;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.security.PersistableGroup;
import com.salesmanager.shop.model.security.ReadableGroup;
import com.salesmanager.shop.model.security.ReadablePermission;
import com.salesmanager.shop.model.user.PersistableUser;
import com.salesmanager.shop.model.user.ReadableUser;
import com.salesmanager.shop.model.user.ReadableUserList;
import com.salesmanager.shop.model.user.UserPassword;
import com.salesmanager.shop.populator.user.PersistableUserPopulator;
import com.salesmanager.shop.populator.user.ReadableUserPopulator;
import com.salesmanager.shop.store.api.exception.ConversionRuntimeException;
import com.salesmanager.shop.store.api.exception.OperationNotAllowedException;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.api.exception.UnauthorizedException;
import com.salesmanager.shop.store.controller.security.facade.SecurityFacade;

@Service("userFacade")
public class UserFacadeImpl implements UserFacade {
	
	private static final String PRIVATE_PATH = "/private/";

	@Inject
	private MerchantStoreService merchantStoreService;

	@Inject
	private UserService userService;

	@Inject
	private PermissionService permissionService;

	@Inject
	private LanguageService languageService;

	@Inject
	private PersistableUserPopulator persistableUserPopulator;

	@Inject
	private SecurityFacade securityFacade;

	private static final Logger LOGGER = LoggerFactory.getLogger(UserFacadeImpl.class);

	@Override
	public ReadableUser findByUserName(String userName, String storeCode, Language lang) {
		ReadableUser user = findByUserName(userName, lang);
		System.out.println("$#14206#"); if (user == null) {
			throw new ResourceNotFoundException("User [" + userName + "] not found");
		}

		System.out.println("$#14207#"); return user;

	}

	private ReadableUser findByUserName(String userName, Language lang) {
		User user = getByUserName(userName);
		System.out.println("$#14208#"); if (user == null) {
			throw new ResourceNotFoundException("User [" + userName + "] not found");
		}
		System.out.println("$#14209#"); return convertUserToReadableUser(lang, user);
	}

	private ReadableUser convertUserToReadableUser(Language lang, User user) {
		ReadableUserPopulator populator = new ReadableUserPopulator();
		try {
			ReadableUser readableUser = new ReadableUser();
			readableUser = populator.populate(user, readableUser, user.getMerchantStore(), lang);

			List<Integer> groupIds = readableUser.getGroups().stream().map(ReadableGroup::getId).map(Long::intValue)
					.collect(Collectors.toList());
			List<ReadablePermission> permissions = findPermissionsByGroups(groupIds);
			System.out.println("$#14210#"); readableUser.setPermissions(permissions);

			System.out.println("$#14211#"); return readableUser;
		} catch (ConversionException e) {
			throw new ConversionRuntimeException(e);
		}
	}

	private User converPersistabletUserToUser(MerchantStore store, Language lang, User userModel,
			PersistableUser user) {
		try {
			System.out.println("$#14212#"); return persistableUserPopulator.populate(user, userModel, store, lang);
		} catch (ConversionException e) {
			throw new ConversionRuntimeException(e);
		}
	}

	private User getByUserName(String userName) {
		try {
			System.out.println("$#14213#"); return userService.getByUserName(userName);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException(e);
		}
	}

	private User getByUserName(String userName, String storeCode) {
		try {
			System.out.println("$#14214#"); return userService.getByUserName(userName, storeCode);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException(e);
		}
	}

	private User getByUserId(Long id, String storeCode) {
		try {
			System.out.println("$#14215#"); return userService.findByStore(id, storeCode);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException(e);
		}
	}

	private User getByUserId(Long id) {
		try {
			System.out.println("$#14216#"); return userService.getById(id);
		} catch (Exception e) {
			throw new ServiceRuntimeException(e);
		}
	}

	@Override
	public List<ReadablePermission> findPermissionsByGroups(List<Integer> ids) {
		System.out.println("$#14218#"); System.out.println("$#14217#"); return getPermissionsByIds(ids).stream().map(permission -> convertPermissionToReadablePermission(permission))
				.collect(Collectors.toList());
	}

	private ReadablePermission convertPermissionToReadablePermission(Permission permission) {
		ReadablePermission readablePermission = new ReadablePermission();
		System.out.println("$#14219#"); readablePermission.setId(permission.getId());
		System.out.println("$#14220#"); readablePermission.setName(permission.getPermissionName());
		System.out.println("$#14221#"); return readablePermission;
	}

	private List<Permission> getPermissionsByIds(List<Integer> ids) {
		try {
			System.out.println("$#14222#"); return permissionService.getPermissions(ids);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException(e);
		}
	}

	@Deprecated
	@Override
	public boolean authorizedStore(String userName, String merchantStoreCode) {

		try {
			
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			Set<String> roles = authentication.getAuthorities().stream()
			     .map(r -> r.getAuthority()).collect(Collectors.toSet());
			
			
			
			ReadableUser readableUser = findByUserName(userName, languageService.defaultLanguage());

			// unless superadmin
			for (ReadableGroup group : readableUser.getGroups()) {
				System.out.println("$#14224#"); if (Constants.GROUP_SUPERADMIN.equals(group.getName())) {
					System.out.println("$#14225#"); return true;
				}
			}

			boolean authorized = false;
			User user = userService.findByStore(readableUser.getId(), merchantStoreCode);
			System.out.println("$#14226#"); if (user != null) {
				authorized = true;
			} else {
				user = userService.getByUserName(userName);
			}
			
			System.out.println("$#14227#"); if(user != null && !authorized) {

				//get parent
				MerchantStore store = merchantStoreService.getParent(merchantStoreCode);

				//user can be in parent
				MerchantStore st = user.getMerchantStore();
				System.out.println("$#14229#"); if(store != null &&  st.getCode().equals(store.getCode())) {
					authorized = true;
				}
			}

			System.out.println("$#14232#"); System.out.println("$#14231#"); return authorized;
		} catch (Exception e) {
			throw new ServiceRuntimeException("Cannot authorize user " + userName + " for store " + merchantStoreCode,
					e.getMessage());
		}
	}

	@Override
	public void authorizedGroup(String userName, List<String> groupName) {

		ReadableUser readableUser = findByUserName(userName, languageService.defaultLanguage());

		// unless superadmin
		for (ReadableGroup group : readableUser.getGroups()) {
			System.out.println("$#14233#"); if (groupName.contains(group.getName())) {
				return;
			}
		}

		throw new UnauthorizedException("User " + userName + " not authorized");

	}

	@Override
	public String authenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		System.out.println("$#14234#"); if(authentication == null) {
			throw new UnauthorizedException("User Not authorized");
		}
		
		System.out.println("$#14235#"); if (!(authentication instanceof AnonymousAuthenticationToken)) {
			String currentUserName = authentication.getName();
			System.out.println("$#14236#"); return currentUserName;
		}
		System.out.println("$#14237#"); return null;
	}

	@Override
	public ReadableUser create(PersistableUser user, MerchantStore store) {

		System.out.println("$#14238#"); Validate.notNull(store, "MerchantStore must not be null");
		System.out.println("$#14239#"); Validate.notNull(user, "User must not be null");
		System.out.println("$#14240#"); Validate.notNull(user.getUserName(), "Username must not be null");

		try {

			// check if user exists
			User tempUser = userService.getByUserName(user.getUserName(), store.getCode());
			System.out.println("$#14241#"); if (tempUser != null) {
				throw new ServiceRuntimeException(
						"User [" + user.getUserName() + "] already exists for store [" + store.getCode() + "]");
			}

			User userModel = new User();
			userModel = converPersistabletUserToUser(store, languageService.defaultLanguage(), userModel, user);
			System.out.println("$#14242#"); if (CollectionUtils.isEmpty(userModel.getGroups())) {
				throw new ServiceRuntimeException("No valid group groups associated with user " + user.getUserName());
			}
			System.out.println("$#14243#"); userService.saveOrUpdate(userModel);
			// now build returned object
			User createdUser = userService.getById(userModel.getId());
			System.out.println("$#14244#"); return convertUserToReadableUser(createdUser.getDefaultLanguage(), createdUser);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException(
					"Cannot create user " + user.getUserName() + " for store " + store.getCode(), e);
		}
	}

	@Override
	public ReadableUserList getByCriteria(Language language, String drawParam, Criteria criteria) {
		try {
			ReadableUserList readableUserList = new ReadableUserList();
			GenericEntityList<User> userList = userService.listByCriteria(criteria);
			for (User user : userList.getList()) {
				ReadableUser readableUser = this.convertUserToReadableUser(language, user);
				readableUserList.getData().add(readableUser);
			}
			System.out.println("$#14245#"); readableUserList.setRecordsTotal(userList.getTotalCount());
			System.out.println("$#14246#"); readableUserList.setNumber(userList.getList().size());
			System.out.println("$#14247#"); readableUserList.setTotalPages(userList.getTotalPages());
			//readableUserList.setTotalPages(readableUserList.getData().size());
			System.out.println("$#14248#"); readableUserList.setRecordsFiltered(userList.getTotalCount());

			System.out.println("$#14249#"); return readableUserList;
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Cannot get users by criteria user", e);
		}
	}

	@Override
	public void delete(Long id, String merchant) {
		System.out.println("$#14250#"); Validate.notNull(id, "User id cannot be null");

		try {
			User user = userService.findByStore(id, merchant);
			System.out.println("$#14251#"); if (user == null) {
				throw new ServiceRuntimeException("Cannot find user [" + id + "]");
			}

			// cannot delete superadmin
			System.out.println("$#14252#"); if (user.getGroups().contains(Constants.GROUP_SUPERADMIN)) {
				throw new ServiceRuntimeException("Cannot delete superadmin user [" + id + "]");
			}

			System.out.println("$#14253#"); userService.delete(user);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Cannot find user [" + id + "]", e);
		}

	}

	@Override
	public ReadableUser update(Long id, String authenticatedUser, MerchantStore store, PersistableUser user) {
		System.out.println("$#14254#"); Validate.notNull(user, "User cannot be null");
		System.out.println("$#14255#"); Validate.notNull(store, "store cannot be null");

		try {
			User userModel = userService.getById(id);
			System.out.println("$#14256#"); if (userModel == null) {
				throw new ServiceRuntimeException("Cannot find user [" + user.getUserName() + "]");
			}
			System.out.println("$#14257#"); if (userModel.getId().longValue() != id.longValue()) {
				throw new ServiceRuntimeException(
						"Cannot find user [" + user.getUserName() + "] id or name does not match");
			}
			User auth = userService.getByUserName(authenticatedUser);
			System.out.println("$#14258#"); if (auth == null) {
				throw new ServiceRuntimeException("Cannot find user [" + authenticatedUser + "]");
			}
			User adminName = getByUserName(user.getUserName());
			System.out.println("$#14259#"); if (adminName != null) {
				System.out.println("$#14260#"); if (adminName.getId().longValue() != userModel.getId().longValue()) {
					throw new ServiceRuntimeException(
							"User id [" + userModel.getId() + "] does not match [" + user.getUserName() + "]");
				}
			}
			boolean isActive = userModel.isActive();
			List<Group> originalGroups = userModel.getGroups();
			Group superadmin = originalGroups.stream()
					.filter(group -> Constants.GROUP_SUPERADMIN.equals(group.getGroupName())).findAny().orElse(null);
			

			
			//changing store ? 
			/**
			 * Can't change self store
			 * Only admin and superadmin can change another user store
			 */
			
			//i'm i editing my own profile ?
			System.out.println("$#14263#"); if(authenticatedUser.equals(adminName)) {
				
				System.out.println("$#14264#"); if(!userModel.getMerchantStore().getCode().equals(store.getCode())) {
					throw new OperationNotAllowedException("User [" + adminName + "] cannot change owning store");
				}
				
			} else {
				//i am an admin or super admin
				Group adminOrSuperadmin = originalGroups.stream()
						.filter(group -> (
								Constants.GROUP_SUPERADMIN.equals(group.getGroupName()) || Constants.ADMIN_USER.equals(group.getGroupName())|| Constants.ADMIN_STORE.equals(group.getGroupName()
										))).findAny().orElse(null);
				
				System.out.println("$#14269#"); if(!userModel.getMerchantStore().getCode().equals(store.getCode()) && adminOrSuperadmin == null) {
					throw new OperationNotAllowedException("User [" + adminName + "] cannot change owning store");
				}
				
			}

			userModel = converPersistabletUserToUser(store, languageService.defaultLanguage(), userModel, user);

			// if superadmin set original permissions, prevent removing super
			// admin
			System.out.println("$#14271#"); if (superadmin != null) {
				System.out.println("$#14272#"); userModel.setGroups(originalGroups);
			}

			Group adminGroup = auth.getGroups().stream()
					.filter((group) -> Constants.GROUP_SUPERADMIN.equals(group.getGroupName())
							|| Constants.GROUP_SUPERADMIN.equals(group.getGroupName()))
					.findAny().orElse(null);

			System.out.println("$#14276#"); if (adminGroup == null) {
				System.out.println("$#14277#"); userModel.setGroups(originalGroups);
				System.out.println("$#14278#"); userModel.setActive(isActive);
			}

			System.out.println("$#14279#"); user.setPassword(userModel.getAdminPassword());
			System.out.println("$#14280#"); userService.update(userModel);
			System.out.println("$#14281#"); return this.convertUserToReadableUser(languageService.defaultLanguage(), userModel);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Cannot update user [" + user.getUserName() + "]", e);
		}

	}


	@Override
	public void changePassword(Long userId, String authenticatedUser, UserPassword changePassword) {

		System.out.println("$#14282#"); Validate.notNull(changePassword, "Change password request must not be null");
		System.out.println("$#14283#"); Validate.notNull(changePassword.getPassword(), "Original password request must not be null");
		System.out.println("$#14284#"); Validate.notNull(changePassword.getChangePassword(), "New password request must not be null");

		/**
		 * Only admin and superadmin can change other user password
		 */
		User auth = null;
		try {
			auth = userService.getByUserName(authenticatedUser);

			System.out.println("$#14285#"); if (auth == null) {
				throw new ServiceRuntimeException("Cannot find user [" + authenticatedUser + "]");
			}

			User userModel = userService.getById(userId);
			System.out.println("$#14286#"); if (userModel == null) {
				throw new ServiceRuntimeException("Cannot find user [" + userId + "]");
			}

			/**
			 * need to validate if actual password match
			 */

			System.out.println("$#14287#"); if (!securityFacade.matchPassword(userModel.getAdminPassword(), changePassword.getPassword())) {
				throw new ServiceRuntimeException("Actual password does not match for user [" + userId + "]");
			}

			/**
			 * Validate new password
			 */
			System.out.println("$#14288#"); if (!securityFacade.validateUserPassword(changePassword.getChangePassword())) {
				throw new ServiceRuntimeException("New password does not apply to format policy");
			}

			String newPasswordEncoded = securityFacade.encodePassword(changePassword.getChangePassword());
			System.out.println("$#14289#"); userModel.setAdminPassword(newPasswordEncoded);

			System.out.println("$#14290#"); userService.update(userModel);

		} catch (ServiceException e) {
			LOGGER.error("Error updating password");
			throw new ServiceRuntimeException(e);
		}

	}

	@Override
	public ReadableUserList listByCriteria(UserCriteria criteria, int page, int count, Language language) {
		try {
			ReadableUserList readableUserList = new ReadableUserList();
			// filtering by userName is not in this implementation
			
			
			Page<User> userList = null;
			
			Optional<String> storeCode = Optional.ofNullable(criteria.getStoreCode());
			System.out.println("$#14291#"); if(storeCode.isPresent()) {
				//get store
				MerchantStore store = merchantStoreService.getByCode(storeCode.get());
				System.out.println("$#14292#"); if(store.isRetailer()) {
					//get group stores
					List<MerchantStore> stores = merchantStoreService.findAllStoreNames(store.getCode());
					List<Integer> intList = stores.stream().map(s -> s.getId()).collect(Collectors.toList());
					System.out.println("$#14294#"); criteria.setStoreIds(intList);
					//search over store list
					System.out.println("$#14295#"); criteria.setStoreCode(null);
				}
			} 
			
			
			userList = userService.listByCriteria(criteria, page, count);
			List<ReadableUser> readableUsers = new ArrayList<ReadableUser>();
			System.out.println("$#14296#"); if(userList != null) {
				readableUsers = userList.getContent().stream()
						.map(user -> convertUserToReadableUser(language, user)).collect(Collectors.toList());
				
				System.out.println("$#14298#"); readableUserList.setRecordsTotal(userList.getTotalElements());
				System.out.println("$#14299#"); readableUserList.setTotalPages(userList.getTotalPages());
				System.out.println("$#14300#"); readableUserList.setNumber(userList.getSize());
				System.out.println("$#14301#"); readableUserList.setRecordsFiltered(userList.getSize());
			}

			System.out.println("$#14302#"); readableUserList.setData(readableUsers);
			
/*			System.out.println(userList.getNumber());
			System.out.println(userList.getNumberOfElements());
			System.out.println(userList.getSize());
			System.out.println(userList.getTotalElements());
			System.out.println(userList.getTotalPages());
			*/
			


			System.out.println("$#14303#"); return readableUserList;
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Cannot get users by criteria user", e);
		}
	}

	@Override
	public void authorizedGroups(String authenticatedUser, PersistableUser user) {
		System.out.println("$#14304#"); Validate.notNull(authenticatedUser, "Required authenticated user");
		System.out.println("$#14305#"); Validate.notNull(user, "Required persistable user");
		
		
		try {
			User currentUser = userService.getByUserName(authenticatedUser);
			
			boolean isSuperAdmin = false;
			
			for(Group g : currentUser.getGroups()) {
				System.out.println("$#14306#"); if(g.getGroupName().equals("SUPERADMIN")) {
					isSuperAdmin = true;
					break;
				}
					
			}
			
			for(PersistableGroup g : user.getGroups()) {
				System.out.println("$#14307#"); if(g.getName().equals("SUPERADMIN")) {
					System.out.println("$#14308#"); if(!isSuperAdmin) {
						throw new UnauthorizedException("Superadmin group not allowed");
					}
				}
			}
			
			
			
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Error while looking for authorization",e);
		}
		

	}

	@Override
	public boolean userInRoles(String userName, List<String> groupNames) {
		
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		List<String> roles = authentication.getAuthorities().stream()
			 .filter(x -> groupNames.contains(x.getAuthority()))
		     .map(r -> r.getAuthority()).collect(Collectors.toList());
		
		
		System.out.println("$#14314#"); System.out.println("$#14313#"); System.out.println("$#14312#"); return roles.size() > 0;

	}

	@Override
	public void updateEnabled(MerchantStore store, PersistableUser user) {
		System.out.println("$#14315#"); Validate.notNull(user, "User cannot be null");
		System.out.println("$#14316#"); Validate.notNull(store, "MerchantStore cannot be null");
		System.out.println("$#14317#"); Validate.notNull(user.getId(), "User.id cannot be null");
		
		try {
			User modelUser = userService.findByStore(user.getId(), store.getCode());
			
			System.out.println("$#14318#"); if(modelUser == null) {
				throw new ResourceNotFoundException("User with id [" + user.getId() + "] not found for store [" + store.getCode() + "]");
			}
			
			System.out.println("$#14319#"); modelUser.setActive(user.isActive());
			System.out.println("$#14320#"); userService.saveOrUpdate(modelUser);
			
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Error while updating user enable flag",e);
		}
		
	}

	@Override
	public boolean authorizeStore(MerchantStore store, String path) {
		
		System.out.println("$#14321#"); Validate.notNull(store, "MerchantStore cannot be null");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		

		System.out.println("$#14322#"); if(!StringUtils.isBlank(path) && path.contains(PRIVATE_PATH)) {

			try {
				
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				String currentPrincipalName = authentication.getName();
				
				System.out.println("$#14324#"); System.out.println("Principal " + currentPrincipalName);
				
				ReadableUser readableUser = findByUserName(currentPrincipalName, languageService.defaultLanguage());
				
				System.out.println("$#14325#"); if(readableUser==null) {
					System.out.println("$#14326#"); return false;
				}
				
				
				//current user match;
				String merchant = readableUser.getMerchant();
				
				System.out.println("$#14327#"); if(store.getCode().equalsIgnoreCase(merchant)) {
					System.out.println("$#14328#"); return true;
				}
				
				Set<String> roles = authentication.getAuthorities().stream()
				     .map(r -> r.getAuthority()).collect(Collectors.toSet());

				//is superadmin
				for (ReadableGroup group : readableUser.getGroups()) {
					System.out.println("$#14330#"); if (Constants.GROUP_SUPERADMIN.equals(group.getName())) {
						System.out.println("$#14331#"); return true;
					}
				}
				
				
				boolean authorized = false;

				//user store can be parent and requested store is child 
				//get parent
				//TODO CACHE
				MerchantStore parent = merchantStoreService.getParent(merchant);
	
				//user can be in parent
				System.out.println("$#14332#"); if(parent != null &&  parent.getCode().equals(store.getCode())) {
					authorized = true;
				}
				
				//else false
				System.out.println("$#14335#"); System.out.println("$#14334#"); return authorized;
			} catch (Exception e) {
				throw new ServiceRuntimeException("Cannot authorize user " + authentication.getPrincipal().toString() + " for store " + store.getCode(),
						e.getMessage());
			}
		
		}

		
		
		System.out.println("$#14336#"); return true;
	}

	@Override
	public ReadableUser findById(Long id, MerchantStore store, Language lang) {
		System.out.println("$#14337#"); Validate.notNull(store, "MerchantStore cannot be null");
		
		User user = userService.getById(id, store);
		System.out.println("$#14338#"); if (user == null) {
			throw new ResourceNotFoundException("User [" + id + "] not found");
		}


		System.out.println("$#14339#"); return convertUserToReadableUser(lang, user);

	}

	@Override
	public ReadableUser findByUserName(String userName) {
		System.out.println("$#14340#"); Validate.notNull(userName, "userName cannot be null");
		User user;
		try {
			user = userService.getByUserName(userName);
			System.out.println("$#14341#"); if (user == null) {
				throw new ResourceNotFoundException("User [" + userName + "] not found");
			}
			
			System.out.println("$#14342#"); return this.convertUserToReadableUser(user.getDefaultLanguage(), user);
			
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Error while getting user [" + userName+ "]",
					e);
		}

	}


}
