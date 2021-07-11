package com.salesmanager.core.business.modules.email;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class EmailConfig implements JSONAware {

	private String host;
	private String port;
	private String protocol;
	private String username;
	private String password;
	private boolean smtpAuth = false;
	private boolean starttls = false;
	
	private String emailTemplatesPath = null;
	
	@SuppressWarnings("unchecked")
	@Override
	public String toJSONString() {
		JSONObject data = new JSONObject();
		data.put("host", this.getHost());
		data.put("port", this.getPort());
		data.put("protocol", this.getProtocol());
		data.put("username", this.getUsername());
		data.put("smtpAuth", this.isSmtpAuth());
		data.put("starttls", this.isStarttls());
		data.put("password", this.getPassword());
		System.out.println("$#410#"); return data.toJSONString();
	}
	
	

	public boolean isSmtpAuth() {
		System.out.println("$#412#"); System.out.println("$#411#"); return smtpAuth;
	}
	public void setSmtpAuth(boolean smtpAuth) {
		this.smtpAuth = smtpAuth;
	}
	public boolean isStarttls() {
		System.out.println("$#414#"); System.out.println("$#413#"); return starttls;
	}
	public void setStarttls(boolean starttls) {
		this.starttls = starttls;
	}
	public void setEmailTemplatesPath(String emailTemplatesPath) {
		this.emailTemplatesPath = emailTemplatesPath;
	}
	public String getEmailTemplatesPath() {
		System.out.println("$#415#"); return emailTemplatesPath;
	}



	public String getHost() {
		System.out.println("$#416#"); return host;
	}



	public void setHost(String host) {
		this.host = host;
	}



	public String getPort() {
		System.out.println("$#417#"); return port;
	}



	public void setPort(String port) {
		this.port = port;
	}



	public String getProtocol() {
		System.out.println("$#418#"); return protocol;
	}



	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}



	public String getUsername() {
		System.out.println("$#419#"); return username;
	}



	public void setUsername(String username) {
		this.username = username;
	}



	public String getPassword() {
		System.out.println("$#420#"); return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}

}
