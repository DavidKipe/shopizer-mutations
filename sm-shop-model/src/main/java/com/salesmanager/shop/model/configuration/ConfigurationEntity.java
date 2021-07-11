package com.salesmanager.shop.model.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.salesmanager.shop.model.entity.Entity;

public class ConfigurationEntity extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String key = null;
	private boolean active;
	private String value;
	private String type;
	private String code;
	private Map<String, String> keys = new HashMap<String, String>();
	private Map<String, List<String>> integrationOptions = new HashMap<String, List<String>>();
	
	
	
	public String getKey() {
		System.out.println("$#8978#"); return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public boolean isActive() {
		System.out.println("$#8980#"); System.out.println("$#8979#"); return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getValue() {
		System.out.println("$#8981#"); return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getType() {
		System.out.println("$#8982#"); return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Map<String, String> getKeys() {
		System.out.println("$#8983#"); return keys;
	}
	public void setKeys(Map<String, String> keys) {
		this.keys = keys;
	}
	public String getCode() {
		System.out.println("$#8984#"); return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Map<String, List<String>> getIntegrationOptions() {
		System.out.println("$#8985#"); return integrationOptions;
	}
	public void setIntegrationOptions(Map<String, List<String>> integrationOptions) {
		this.integrationOptions = integrationOptions;
	}

}
