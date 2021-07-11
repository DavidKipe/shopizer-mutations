package com.salesmanager.core.business.configuration.db;

import javax.inject.Inject;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import com.salesmanager.core.model.system.credentials.DbCredentials;

//@Configuration
//@VaultPropertySource("secret/db")
public class DbConfig {
	
    @Inject Environment env;

    @Bean
    public DbCredentials dbCredentials() {
    	DbCredentials dbCredentials = new DbCredentials();
					System.out.println("$#13#"); dbCredentials.setUserName(env.getProperty("user"));
					System.out.println("$#14#"); dbCredentials.setPassword(env.getProperty("password"));
								System.out.println("$#15#"); return dbCredentials;
    }

}
