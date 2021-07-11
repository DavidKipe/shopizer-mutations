package com.salesmanager.core.model.shipping;

import java.util.List;

import com.salesmanager.core.model.reference.country.Country;

/**
 * Describes how shipping is configured for a given store
 * @author carlsamson
 *
 */
public class ShippingMetaData {
	
	private List<String> modules;
	private List<String> preProcessors;
	private List<String> postProcessors;
	private List<Country> shipToCountry;
	private boolean useDistanceModule;
	private boolean useAddressAutoComplete;
	
	
	
	public List<String> getModules() {
		System.out.println("$#4606#"); return modules;
	}
	public void setModules(List<String> modules) {
		this.modules = modules;
	}
	public List<String> getPreProcessors() {
		System.out.println("$#4607#"); return preProcessors;
	}
	public void setPreProcessors(List<String> preProcessors) {
		this.preProcessors = preProcessors;
	}
	public List<String> getPostProcessors() {
		System.out.println("$#4608#"); return postProcessors;
	}
	public void setPostProcessors(List<String> postProcessors) {
		this.postProcessors = postProcessors;
	}
	public List<Country> getShipToCountry() {
		System.out.println("$#4609#"); return shipToCountry;
	}
	public void setShipToCountry(List<Country> shipToCountry) {
		this.shipToCountry = shipToCountry;
	}
	public boolean isUseDistanceModule() {
		System.out.println("$#4611#"); System.out.println("$#4610#"); return useDistanceModule;
	}
	public void setUseDistanceModule(boolean useDistanceModule) {
		this.useDistanceModule = useDistanceModule;
	}
  public boolean isUseAddressAutoComplete() {
				System.out.println("$#4613#"); System.out.println("$#4612#"); return useAddressAutoComplete;
  }
  public void setUseAddressAutoComplete(boolean useAddressAutoComplete) {
    this.useAddressAutoComplete = useAddressAutoComplete;
  }

}
