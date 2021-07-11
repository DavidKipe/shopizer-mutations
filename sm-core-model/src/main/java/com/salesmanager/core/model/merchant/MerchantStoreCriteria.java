package com.salesmanager.core.model.merchant;

import com.salesmanager.core.model.common.Criteria;

public class MerchantStoreCriteria extends Criteria {
	
	private boolean retailers = false;
	private boolean stores = false;

	public boolean isRetailers() {
		System.out.println("$#4273#"); System.out.println("$#4272#"); return retailers;
	}

	public void setRetailers(boolean retailers) {
		this.retailers = retailers;
	}

	public boolean isStores() {
		System.out.println("$#4275#"); System.out.println("$#4274#"); return stores;
	}

	public void setStores(boolean stores) {
		this.stores = stores;
	}
	
	


}
