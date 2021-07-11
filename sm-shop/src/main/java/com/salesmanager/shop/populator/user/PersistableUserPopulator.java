package com.salesmanager.shop.populator.user;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.merchant.MerchantStoreService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.user.GroupService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.user.Group;
import com.salesmanager.core.model.user.User;
import com.salesmanager.shop.model.security.PersistableGroup;
import com.salesmanager.shop.model.user.PersistableUser;


@Component
public class PersistableUserPopulator extends AbstractDataPopulator<PersistableUser, User> {

  @Inject
  private LanguageService languageService;
  
  @Inject
  private GroupService groupService;
  
  @Inject
  private MerchantStoreService merchantStoreService;
  
  @Inject
  @Named("passwordEncoder")
  private PasswordEncoder passwordEncoder;
  
  @Override
  public User populate(PersistableUser source, User target, MerchantStore store, Language language)
      throws ConversionException {
				System.out.println("$#11167#"); Validate.notNull(source, "PersistableUser cannot be null");
				System.out.println("$#11168#"); Validate.notNull(store, "MerchantStore cannot be null");

				System.out.println("$#11169#"); if (target == null) {
      target = new User();
    }

				System.out.println("$#11170#"); target.setFirstName(source.getFirstName());
				System.out.println("$#11171#"); target.setLastName(source.getLastName());
				System.out.println("$#11172#"); target.setAdminEmail(source.getEmailAddress());
				System.out.println("$#11173#"); target.setAdminName(source.getUserName());
				System.out.println("$#11174#"); if(!StringUtils.isBlank(source.getPassword())) {
						System.out.println("$#11175#"); target.setAdminPassword(passwordEncoder.encode(source.getPassword()));
    }
    
				System.out.println("$#11176#"); if(!StringUtils.isBlank(source.getStore())) {
        try {
			MerchantStore userStore = merchantStoreService.getByCode(source.getStore());
			System.out.println("$#11177#"); target.setMerchantStore(userStore);
		} catch (ServiceException e) {
			throw new ConversionException("Error while reading MerchantStore store [" + source.getStore() + "]",e);
		}
    } else {
					System.out.println("$#11178#"); target.setMerchantStore(store);
    }
    
    
				System.out.println("$#11179#"); target.setActive(source.isActive());
    
    Language lang = null;
    try {
      lang = languageService.getByCode(source.getDefaultLanguage());
    } catch(Exception e) {
      throw new ConversionException("Cannot get language [" + source.getDefaultLanguage() + "]",e);
    }

    // set default language
				System.out.println("$#11180#"); target.setDefaultLanguage(lang);

    List<Group> userGroups = new ArrayList<Group>();
    List<String> names = new ArrayList<String>();
    for (PersistableGroup group : source.getGroups()) {
      names.add(group.getName());
    }
    try {
      List<Group> groups = groupService.listGroupByNames(names);
      for(Group g: groups) {
        userGroups.add(g);
      }
    } catch (Exception e1) {
      throw new ConversionException("Error while getting user groups",e1);
    }
    
				System.out.println("$#11181#"); target.setGroups(userGroups);

				System.out.println("$#11182#"); return target;
  }

  @Override
  protected User createTarget() {
    // TODO Auto-generated method stub
    return null;
  }

}
