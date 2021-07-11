package com.salesmanager.shop.mapper.optin;

import org.springframework.stereotype.Component;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.system.optin.Optin;
import com.salesmanager.core.model.system.optin.OptinType;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.system.PersistableOptin;

@Component
public class PersistableOptinMapper implements Mapper<PersistableOptin, Optin> {


  @Override
  public Optin convert(PersistableOptin source, MerchantStore store, Language language) {
    Optin optinEntity = new Optin();
				System.out.println("$#8621#"); optinEntity.setCode(source.getCode());
				System.out.println("$#8622#"); optinEntity.setDescription(source.getDescription());
				System.out.println("$#8623#"); optinEntity.setOptinType(OptinType.valueOf(source.getOptinType()));
				System.out.println("$#8624#"); optinEntity.setMerchant(store);
				System.out.println("$#8625#"); return optinEntity;
  }

  @Override
  public Optin convert(PersistableOptin source, Optin destination, MerchantStore store,
      Language language) {
				System.out.println("$#8626#"); return destination;
  }
}
