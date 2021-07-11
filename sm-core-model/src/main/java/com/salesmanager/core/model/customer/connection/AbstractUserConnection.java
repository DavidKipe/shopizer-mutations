package com.salesmanager.core.model.customer.connection;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;


@Deprecated
@MappedSuperclass
public abstract class AbstractUserConnection<P> implements RemoteUser,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String accessToken;
	private String displayName;
	private Long expireTime;
	private String imageUrl;
	private String profileUrl;
	private int userRank;
	private String refreshToken;
	private String secret;


	public String getAccessToken() {
		System.out.println("$#4153#"); return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getDisplayName() {
		System.out.println("$#4154#"); return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Long getExpireTime() {
		System.out.println("$#4155#"); return expireTime;
	}

	public void setExpireTime(Long expireTime) {
		this.expireTime = expireTime;
	}

	public String getImageUrl() {
		System.out.println("$#4156#"); return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getProfileUrl() {
		System.out.println("$#4157#"); return profileUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	public abstract String getProviderId();

	public abstract void setProviderId(String providerId);

	public abstract String getProviderUserId();

	public abstract void setProviderUserId(String providerUserId);

	public int getRank() {
		System.out.println("$#4158#"); return userRank;
	}

	public void setRank(int userRank) {
		this.userRank = userRank;
	}

	public String getRefreshToken() {
		System.out.println("$#4159#"); return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getSecret() {
		System.out.println("$#4160#"); return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public abstract String getUserId();

	public abstract void setUserId(String userId);

	protected abstract P getId();
}
