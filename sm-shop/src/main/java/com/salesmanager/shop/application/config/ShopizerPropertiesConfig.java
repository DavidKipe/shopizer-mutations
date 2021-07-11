package com.salesmanager.shop.application.config;


import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class ShopizerPropertiesConfig {

  @Bean
  public List<String> templates() {
				System.out.println("$#7942#"); return Arrays.asList("bootstrap", "generic", "exoticamobilia", "december");
  }

  @Bean(name = "shopizer-properties")
  public PropertiesFactoryBean mapper() {
    PropertiesFactoryBean bean = new PropertiesFactoryBean();
				System.out.println("$#7943#"); bean.setLocation(new ClassPathResource("shopizer-properties.properties"));
				System.out.println("$#7944#"); return bean;
  }
}
