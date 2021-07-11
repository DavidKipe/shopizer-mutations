package com.salesmanager.core.business.configuration;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;


@Configuration
@EnableCaching
public class DataConfiguration {

	/**
	 * Datasource
	 */
    @Value("${db.driverClass}")
    private String driverClassName;
    
    @Value("${db.jdbcUrl}")
    private String url;
    
    @Value("${db.user}")
    private String user;
    
    @Value("${db.password}")
    private String password;

    
    /**
     * Other connection properties
     */
    
    @Value("${hibernate.hbm2ddl.auto}")
    private String hbm2ddl;
    
    @Value("${hibernate.dialect}")
    private String dialect;
    
    @Value("${db.show.sql}")
    private String showSql;
    
    @Value("${db.preferredTestQuery}")
    private String preferredTestQuery;
    
    @Value("${db.schema}")
    private String schema;
    
    @Value("${db.preferredTestQuery}")
    private String testQuery;
    
    @Value("${db.minPoolSize}")
    private int minPoolSize;
    
    @Value("${db.maxPoolSize}")
    private int maxPoolSize;

    @Bean
    public HikariDataSource dataSource() {
    	HikariDataSource dataSource = DataSourceBuilder.create().type(HikariDataSource.class)
    	.driverClassName(driverClassName)
    	.url(url)
    	.username(user)
    	.password(password)
    	.build();
    	
    	/** Datasource config **/
					System.out.println("$#0#"); dataSource.setIdleTimeout(minPoolSize);
					System.out.println("$#1#"); dataSource.setMaximumPoolSize(maxPoolSize);
					System.out.println("$#2#"); dataSource.setConnectionTestQuery(testQuery);
    	
					System.out.println("$#3#"); return dataSource;
    }

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		System.out.println("$#4#"); vendorAdapter.setGenerateDdl(true);

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		System.out.println("$#5#"); factory.setJpaVendorAdapter(vendorAdapter);
		System.out.println("$#6#"); factory.setPackagesToScan("com.salesmanager.core.model");
		System.out.println("$#7#"); factory.setJpaProperties(additionalProperties());
		System.out.println("$#8#"); factory.setDataSource(dataSource());
		System.out.println("$#9#"); return factory;
	}
	
    final Properties additionalProperties() {
        final Properties hibernateProperties = new Properties();
        
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", hbm2ddl);
        hibernateProperties.setProperty("hibernate.default_schema", schema);
        hibernateProperties.setProperty("hibernate.dialect", dialect);
        hibernateProperties.setProperty("hibernate.show_sql", showSql);
        hibernateProperties.setProperty("hibernate.cache.use_second_level_cache", "true");
        hibernateProperties.setProperty("hibernate.cache.use_query_cache", "true");
        hibernateProperties.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory");
        hibernateProperties.setProperty("hibernate.connection.CharSet", "utf8");
        hibernateProperties.setProperty("hibernate.connection.characterEncoding", "utf8");
        hibernateProperties.setProperty("hibernate.connection.useUnicode", "true");
        hibernateProperties.setProperty("hibernate.id.new_generator_mappings", "false");
        // hibernateProperties.setProperty("hibernate.globally_quoted_identifiers", "true");
								System.out.println("$#10#"); return hibernateProperties;
    }

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {

		JpaTransactionManager txManager = new JpaTransactionManager();
		System.out.println("$#11#"); txManager.setEntityManagerFactory(entityManagerFactory);
		System.out.println("$#12#"); return txManager;
	}

}
