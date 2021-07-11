package com.salesmanager.core.model.customer.connection;

import java.io.Serializable;

import javax.persistence.Embeddable;

/**
 * Identity key for storing spring social objects
 * @author carlsamson
 *
 */
@Deprecated
@Embeddable
public class UserConnectionPK implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String userId;
	private String providerId;
	private String providerUserId;

	public String getUserId() {
		System.out.println("$#4168#"); return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getProviderId() {
		System.out.println("$#4169#"); return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public String getProviderUserId() {
		System.out.println("$#4170#"); return providerUserId;
	}

	public void setProviderUserId(String providerUserId) {
		this.providerUserId = providerUserId;
	}

	public boolean equals(Object o) {
		System.out.println("$#4171#"); if (o instanceof UserConnectionPK) {
			UserConnectionPK other = (UserConnectionPK) o;
			System.out.println("$#4172#"); return other.getProviderId().equals(getProviderId())
					&& other.getProviderUserId().equals(getProviderUserId())
					&& other.getUserId().equals(getUserId());
		} else {
			System.out.println("$#4176#"); return false;
		}
	}

	public int hashCode() {
		System.out.println("$#4178#"); System.out.println("$#4177#"); System.out.println("$#4179#"); return getUserId().hashCode() + getProviderId().hashCode()
				+ getProviderUserId().hashCode();
	}

}
