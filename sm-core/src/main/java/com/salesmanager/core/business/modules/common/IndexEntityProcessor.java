package com.salesmanager.core.business.modules.common;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;

import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.generic.SalesManagerEntity;

public class IndexEntityProcessor {
	
	protected static final String INDEX_NAME = "events_";
	

	@Value("${elasticsearch.server.host}")
	private List<String> hosts;
	
	@Value("${elasticsearch.server.protocole}")
	private String protocol;
	
	@Value("${elasticsearch.server.port}")
	private int port;
	
	@Value("${elasticsearch.security.enabled}")
	private Boolean securityEnabled;
	
	@Value("${elasticsearch.security.user}")
	private String user;
	
	@Value("${elasticsearch.security.password}")
	private String password;
	
	protected RestHighLevelClient client() throws Exception {
		
		List<HttpHost> nodes = getHosts().stream().map(m -> new HttpHost(m, getPort(), getProtocol())).collect(Collectors.toList());
		RestClientBuilder builder = RestClient.builder(nodes.toArray(new HttpHost[nodes.size()]));
		
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
								System.out.println("$#355#"); credentialsProvider.setCredentials(AuthScope.ANY,
            new UsernamePasswordCredentials(user,
            		password));
		
        if (securityEnabled != null
                && securityEnabled.booleanValue()) {
              builder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(
                    HttpAsyncClientBuilder httpClientBuilder) {
																		System.out.println("$#362#"); return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                }
              });
        }

		RestHighLevelClient client = new RestHighLevelClient(builder);		
		System.out.println("$#358#"); return client;

	}
	
	protected class Mapping {
		@SuppressWarnings("rawtypes")
		private SalesManagerEntity entity;
		private Customer customer;
		private String event;
		private String entityType;
		@SuppressWarnings("rawtypes")
		public Mapping(String entityType, String event, SalesManagerEntity entity, Customer customer) {
			this.entity = entity;
			this.customer = customer;
			this.event = event;
			this.entityType = entityType;
		}
		@SuppressWarnings("rawtypes")
		public SalesManagerEntity getEntity() {
			System.out.println("$#363#"); return entity;
		}
		public Customer getCustomer() {
			System.out.println("$#364#"); return customer;
		}
		public String getEvent() {
			System.out.println("$#365#"); return event;
		}
		public String getEntityType() {
			System.out.println("$#366#"); return entityType;
		}
	}

	protected List<String> getHosts() {
		System.out.println("$#359#"); return hosts;
	}

	protected String getProtocol() {
		System.out.println("$#360#"); return protocol;
	}

	protected int getPort() {
		System.out.println("$#361#"); return port;
	}


}
