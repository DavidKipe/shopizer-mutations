package com.salesmanager.shop.mapper.optin;

import org.springframework.stereotype.Component;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.system.optin.Optin;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.system.ReadableOptin;

@Component
public class ReadableOptinMapper implements Mapper<Optin, ReadableOptin> {


  @Override
  public ReadableOptin convert(Optin source, MerchantStore store, Language language) {
    ReadableOptin optinEntity = new ReadableOptin();
				System.out.println("$#8627#"); optinEntity.setCode(source.getCode());
				System.out.println("$#8628#"); optinEntity.setDescription(source.getDescription());
				System.out.println("$#8629#"); optinEntity.setOptinType(source.getOptinType().name());
				System.out.println("$#8630#"); return optinEntity;
  }

  @Override
  public ReadableOptin convert(Optin source, ReadableOptin destination, MerchantStore store,
      Language language) {
				System.out.println("$#8631#"); return destination;
  }
}
