package com.salesmanager.core.model.system;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class MerchantConfig implements Serializable, JSONAware {
	

	/**
	 * TODO
	 * Add a generic key value in order to allow the creation of configuration
	 * on the fly from the client application and read from a key value map
	 */
	
	private static final long serialVersionUID = 1L;
	private boolean displayCustomerSection =false;
	private boolean displayContactUs =false;
	private boolean displayStoreAddress = false;
	private boolean displayAddToCartOnFeaturedItems = false;
	private boolean displayCustomerAgreement = false;
	private boolean displayPagesMenu = true;
	private boolean allowPurchaseItems = true;
	private boolean displaySearchBox = true;
	private boolean testMode = false;
	private boolean debugMode = false;
	
	/** Store default search json config **/
	private Map<String,Boolean> useDefaultSearchConfig= new HashMap<String,Boolean>();//language code | true or false
	private Map<String,String> defaultSearchConfigPath= new HashMap<String,String>();//language code | file path

	@SuppressWarnings("unchecked")
	@Override
	public String toJSONString() {
		JSONObject data = new JSONObject();
		data.put("displayCustomerSection", this.isDisplayCustomerSection());
		data.put("displayContactUs", this.isDisplayContactUs());
		data.put("displayStoreAddress", this.isDisplayStoreAddress());
		data.put("displayAddToCartOnFeaturedItems", this.isDisplayAddToCartOnFeaturedItems());
		data.put("displayPagesMenu", this.isDisplayPagesMenu());
		data.put("displayCustomerAgreement", this.isDisplayCustomerAgreement());
		data.put("allowPurchaseItems", this.isAllowPurchaseItems());
		data.put("displaySearchBox", this.displaySearchBox);
		data.put("testMode", this.isTestMode());
		data.put("debugMode", this.isDebugMode());
		
		System.out.println("$#4741#"); if(useDefaultSearchConfig!=null) {
			JSONObject obj = new JSONObject();
			for(String key : useDefaultSearchConfig.keySet()) {
				Boolean val = (Boolean)useDefaultSearchConfig.get(key);
				System.out.println("$#4742#"); if(val!=null) {
					obj.put(key,val);
				}
			}
			data.put("useDefaultSearchConfig", obj);
		}
		
		System.out.println("$#4743#"); if(defaultSearchConfigPath!=null) {
			JSONObject obj = new JSONObject();
			for(String key : defaultSearchConfigPath.keySet()) {
				String val = (String)defaultSearchConfigPath.get(key);
				System.out.println("$#4744#"); if(!StringUtils.isBlank(val)) {
					obj.put(key, val);
				}
			}
			data.put("defaultSearchConfigPath", obj);
		}
		
		
		System.out.println("$#4745#"); return data.toJSONString();
	}

	public void setDisplayCustomerSection(boolean displayCustomerSection) {
		this.displayCustomerSection = displayCustomerSection;
	}

	public boolean isDisplayCustomerSection() {
		System.out.println("$#4747#"); System.out.println("$#4746#"); return displayCustomerSection;
	}

	public void setDisplayContactUs(boolean displayContactUs) {
		this.displayContactUs = displayContactUs;
	}

	public boolean isDisplayContactUs() {
		System.out.println("$#4749#"); System.out.println("$#4748#"); return displayContactUs;
	}

	public boolean isDisplayStoreAddress() {
		System.out.println("$#4751#"); System.out.println("$#4750#"); return displayStoreAddress;
	}

	public void setDisplayStoreAddress(boolean displayStoreAddress) {
		this.displayStoreAddress = displayStoreAddress;
	}

	public void setUseDefaultSearchConfig(Map<String,Boolean> useDefaultSearchConfig) {
		this.useDefaultSearchConfig = useDefaultSearchConfig;
	}

	public Map<String,Boolean> getUseDefaultSearchConfig() {
		System.out.println("$#4752#"); return useDefaultSearchConfig;
	}

	public void setDefaultSearchConfigPath(Map<String,String> defaultSearchConfigPath) {
		this.defaultSearchConfigPath = defaultSearchConfigPath;
	}

	public Map<String,String> getDefaultSearchConfigPath() {
		System.out.println("$#4753#"); return defaultSearchConfigPath;
	}

	public void setDisplayAddToCartOnFeaturedItems(
			boolean displayAddToCartOnFeaturedItems) {
		this.displayAddToCartOnFeaturedItems = displayAddToCartOnFeaturedItems;
	}

	public boolean isDisplayAddToCartOnFeaturedItems() {
		System.out.println("$#4755#"); System.out.println("$#4754#"); return displayAddToCartOnFeaturedItems;
	}

	public boolean isDisplayCustomerAgreement() {
		System.out.println("$#4757#"); System.out.println("$#4756#"); return displayCustomerAgreement;
	}

	public void setDisplayCustomerAgreement(boolean displayCustomerAgreement) {
		this.displayCustomerAgreement = displayCustomerAgreement;
	}

	public boolean isAllowPurchaseItems() {
		System.out.println("$#4759#"); System.out.println("$#4758#"); return allowPurchaseItems;
	}

	public void setAllowPurchaseItems(boolean allowPurchaseItems) {
		this.allowPurchaseItems = allowPurchaseItems;
	}

	public boolean isDisplaySearchBox() {
		System.out.println("$#4761#"); System.out.println("$#4760#"); return displaySearchBox;
	}

	public void setDisplaySearchBox(boolean displaySearchBox) {
		this.displaySearchBox = displaySearchBox;
	}

	public boolean isTestMode() {
		System.out.println("$#4763#"); System.out.println("$#4762#"); return testMode;
	}

	public void setTestMode(boolean testMode) {
		this.testMode = testMode;
	}

	public boolean isDebugMode() {
		System.out.println("$#4765#"); System.out.println("$#4764#"); return debugMode;
	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	public boolean isDisplayPagesMenu() {
		System.out.println("$#4767#"); System.out.println("$#4766#"); return displayPagesMenu;
	}

	public void setDisplayPagesMenu(boolean displayPagesMenu) {
		this.displayPagesMenu = displayPagesMenu;
	}

}
