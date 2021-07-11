package com.salesmanager.core.business.services.reference.loader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.model.system.IntegrationConfiguration;

/**
 * Loads all modules in the database
 * @author c.samson
 *
 */
public class ConfigurationModulesLoader {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationModulesLoader.class);
	

	
	public static String toJSONString(Map<String,IntegrationConfiguration> configurations) throws Exception {
		
		StringBuilder jsonModules = new StringBuilder();
		jsonModules.append("[");
		int count = 0;
		for(Object key : configurations.keySet()) {
			
			String k = (String)key;
			IntegrationConfiguration c = (IntegrationConfiguration)configurations.get(k);
			
			String jsonString = c.toJSONString();
			jsonModules.append(jsonString);
			
			System.out.println("$#2722#"); count ++;
			System.out.println("$#2724#"); System.out.println("$#2723#"); if(count<configurations.size()) {
				jsonModules.append(",");
			}
		}
		jsonModules.append("]");
		System.out.println("$#2725#"); return jsonModules.toString();
		
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String,IntegrationConfiguration> loadIntegrationConfigurations(String value) throws Exception {
		
		
		Map<String,IntegrationConfiguration> modules = new HashMap<String,IntegrationConfiguration>();
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			

            Map[] objects = mapper.readValue(value, Map[].class);
            
												System.out.println("$#2727#"); System.out.println("$#2726#"); for(int i = 0; i < objects.length; i++) {
            	
            	
            	Map object = objects[i];
            	
            	IntegrationConfiguration configuration = new IntegrationConfiguration();
            	
            	String moduleCode = (String)object.get("moduleCode");
													System.out.println("$#2728#"); if(object.get("active")!=null) {
														System.out.println("$#2729#"); configuration.setActive((Boolean)object.get("active"));
            	}
													System.out.println("$#2730#"); if(object.get("defaultSelected")!=null) {
														System.out.println("$#2731#"); configuration.setDefaultSelected((Boolean)object.get("defaultSelected"));
            	}
													System.out.println("$#2732#"); if(object.get("environment")!=null) {
														System.out.println("$#2733#"); configuration.setEnvironment((String)object.get("environment"));
            	}
													System.out.println("$#2734#"); configuration.setModuleCode(moduleCode);
            	
            	modules.put(moduleCode, configuration);

													System.out.println("$#2735#"); if(object.get("integrationKeys")!=null) {
            		Map<String,String> confs = (Map<String,String> )object.get("integrationKeys");
														System.out.println("$#2736#"); configuration.setIntegrationKeys(confs);
            	}
            	
													System.out.println("$#2737#"); if(object.get("integrationKeys")!=null) {
            		Map<String,List<String>> options = (Map<String,List<String>> )object.get("integrationOptions");
														System.out.println("$#2738#"); configuration.setIntegrationOptions(options);
            	}

            	
            }
            
												System.out.println("$#2739#"); return modules;

  		} catch (Exception e) {
  			throw new ServiceException(e);
  		}
  		

	
	}

}
