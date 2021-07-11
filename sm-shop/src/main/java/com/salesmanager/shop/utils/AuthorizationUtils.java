package com.salesmanager.shop.utils;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.store.api.exception.UnauthorizedException;
import com.salesmanager.shop.store.controller.user.facade.UserFacade;

/**
 * Performs authorization check for REST Api
 * - check if user is in role
 * - check if user can perform actions on marchant
 * @author carlsamson
 *
 */
@Component
public class AuthorizationUtils {
	
	@Inject
	private UserFacade userFacade;
	
	public String authenticatedUser() {
		String authenticatedUser = userFacade.authenticatedUser();
		System.out.println("$#15531#"); if (authenticatedUser == null) {
			throw new UnauthorizedException();
		}
		System.out.println("$#15532#"); return authenticatedUser;
	}
	
	public void authorizeUser(String authenticatedUser, List<String> roles, MerchantStore store) {
		System.out.println("$#15533#"); userFacade.authorizedGroup(authenticatedUser, roles);
		System.out.println("$#15534#"); if (!userFacade.userInRoles(authenticatedUser, Arrays.asList(Constants.GROUP_SUPERADMIN))) {
			System.out.println("$#15535#"); if (!userFacade.authorizedStore(authenticatedUser, store.getCode())) {
				throw new UnauthorizedException("Operation unauthorized for user [" + authenticatedUser
						+ "] and store [" + store.getCode() + "]");
			}
		}
	}

}
