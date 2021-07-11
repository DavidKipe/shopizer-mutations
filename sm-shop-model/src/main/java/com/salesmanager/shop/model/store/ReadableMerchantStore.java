package com.salesmanager.shop.model.store;

import java.io.Serializable;
import java.util.List;

import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.content.ReadableImage;
import com.salesmanager.shop.model.entity.ReadableAudit;
import com.salesmanager.shop.model.entity.ReadableAuditable;
import com.salesmanager.shop.model.references.ReadableAddress;

public class ReadableMerchantStore extends MerchantStoreEntity implements ReadableAuditable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String currentUserLanguage;
	private ReadableAddress address;
	private ReadableImage logo;
	private ReadableAudit audit;
	private ReadableMerchantStore parent;

	private List<Language> supportedLanguages;

	public String getCurrentUserLanguage() {
		System.out.println("$#9372#"); return currentUserLanguage;
	}

	public void setCurrentUserLanguage(String currentUserLanguage) {
		this.currentUserLanguage = currentUserLanguage;
	}

	public ReadableAddress getAddress() {
		System.out.println("$#9373#"); return address;
	}

	public void setAddress(ReadableAddress address) {
		this.address = address;
	}

	public ReadableImage getLogo() {
		System.out.println("$#9374#"); return logo;
	}

	public void setLogo(ReadableImage logo) {
		this.logo = logo;
	}

	public void setReadableAudit(ReadableAudit audit) {
		this.audit = audit;

	}

	public ReadableAudit getReadableAudit() {
		System.out.println("$#9375#"); return this.audit;
	}

	public ReadableMerchantStore getParent() {
		System.out.println("$#9376#"); return parent;
	}

	public void setParent(ReadableMerchantStore parent) {
		this.parent = parent;
	}

	public List<Language> getSupportedLanguages() {
		System.out.println("$#9377#"); return supportedLanguages;
	}

	public void setSupportedLanguages(List<Language> supportedLanguages) {
		this.supportedLanguages = supportedLanguages;
	}

}
