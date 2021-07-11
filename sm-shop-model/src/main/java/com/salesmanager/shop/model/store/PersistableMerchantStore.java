package com.salesmanager.shop.model.store;

import java.util.List;

import com.salesmanager.shop.model.references.PersistableAddress;

public class PersistableMerchantStore extends MerchantStoreEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PersistableAddress address;
	//code of parent store (can be null if retailer)
	private String retailerStore;
	private List<String> supportedLanguages;

	public List<String> getSupportedLanguages() {
		System.out.println("$#9368#"); return supportedLanguages;
	}

	public void setSupportedLanguages(List<String> supportedLanguages) {
		this.supportedLanguages = supportedLanguages;
	}

	public PersistableAddress getAddress() {
		System.out.println("$#9369#"); return address;
	}

	public void setAddress(PersistableAddress address) {
		this.address = address;
	}

  public String getRetailerStore() {
				System.out.println("$#9370#"); return retailerStore;
  }

  public void setRetailerStore(String retailerStore) {
    this.retailerStore = retailerStore;
  }

}
