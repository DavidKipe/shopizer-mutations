package com.salesmanager.core.model.customer.connection;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.salesmanager.core.constants.SchemaConstant;

@Deprecated
@MappedSuperclass
@Table(name="USERCONNECTION", schema=SchemaConstant.SALESMANAGER_SCHEMA, uniqueConstraints = { @UniqueConstraint(columnNames = { "userId",
		"providerId", "userRank" }) })
public abstract class AbstractUserConnectionWithCompositeKey extends
		AbstractUserConnection<UserConnectionPK> {

	@Id
	private UserConnectionPK primaryKey = new UserConnectionPK();

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getProviderId() {
		System.out.println("$#4161#"); return primaryKey.getProviderId();
	}

	@Override
	public void setProviderId(String providerId) {
		System.out.println("$#4162#"); primaryKey.setProviderId(providerId);
	}

	@Override
	public String getProviderUserId() {
		System.out.println("$#4163#"); return primaryKey.getProviderUserId();
	}

	@Override
	public void setProviderUserId(String providerUserId) {
		System.out.println("$#4164#"); primaryKey.setProviderUserId(providerUserId);
	}

	@Override
	public String getUserId() {
		System.out.println("$#4165#"); return primaryKey.getUserId();
	}

	@Override
	public void setUserId(String userId) {
		System.out.println("$#4166#"); primaryKey.setUserId(userId);
	}

	@Override
	protected UserConnectionPK getId() {
		System.out.println("$#4167#"); return primaryKey;
	}

}
