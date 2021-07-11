package com.salesmanager.shop.populator.user;

import org.apache.commons.lang.Validate;
import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.user.Group;
import com.salesmanager.core.model.user.User;
import com.salesmanager.shop.model.security.ReadableGroup;
import com.salesmanager.shop.model.user.ReadableUser;
import com.salesmanager.shop.utils.DateUtil;

/**
 * Converts user model to readable user
 * 
 * @author carlsamson
 *
 */
public class ReadableUserPopulator extends AbstractDataPopulator<User, ReadableUser> {

  @Override
  public ReadableUser populate(User source, ReadableUser target, MerchantStore store,
      Language language) throws ConversionException {
				System.out.println("$#11183#"); Validate.notNull(source, "User cannot be null");

				System.out.println("$#11184#"); if (target == null) {
      target = new ReadableUser();
    }

				System.out.println("$#11185#"); target.setFirstName(source.getFirstName());
				System.out.println("$#11186#"); target.setLastName(source.getLastName());
				System.out.println("$#11187#"); target.setEmailAddress(source.getAdminEmail());
				System.out.println("$#11188#"); target.setUserName(source.getAdminName());
				System.out.println("$#11189#"); target.setActive(source.isActive());

				System.out.println("$#11190#"); if (source.getLastAccess() != null) {
						System.out.println("$#11191#"); target.setLastAccess(DateUtil.formatLongDate(source.getLastAccess()));
    }

    // set default language
				System.out.println("$#11192#"); target.setDefaultLanguage(Constants.DEFAULT_LANGUAGE);

				System.out.println("$#11193#"); if (source.getDefaultLanguage() != null)
						System.out.println("$#11194#"); target.setDefaultLanguage(source.getDefaultLanguage().getCode());
				System.out.println("$#11195#"); target.setMerchant(store.getCode());
				System.out.println("$#11196#"); target.setId(source.getId());


    for (Group group : source.getGroups()) {

      ReadableGroup g = new ReadableGroup();
						System.out.println("$#11197#"); g.setName(group.getGroupName());
						System.out.println("$#11198#"); g.setId(group.getId().longValue());
      target.getGroups().add(g);
    }

    /**
     * dates DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
     * myObjectMapper.setDateFormat(df);
     */


				System.out.println("$#11199#"); return target;
  }

  @Override
  protected ReadableUser createTarget() {
    // TODO Auto-generated method stub
    return null;
  }

}
