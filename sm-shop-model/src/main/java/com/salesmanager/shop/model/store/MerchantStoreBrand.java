package com.salesmanager.shop.model.store;

import java.util.ArrayList;
import java.util.List;

public class MerchantStoreBrand {
  

  private List<MerchantConfigEntity> socialNetworks = new ArrayList<MerchantConfigEntity>();

  public List<MerchantConfigEntity> getSocialNetworks() {
				System.out.println("$#9350#"); return socialNetworks;
  }
  public void setSocialNetworks(List<MerchantConfigEntity> socialNetworks) {
    this.socialNetworks = socialNetworks;
  }

}
