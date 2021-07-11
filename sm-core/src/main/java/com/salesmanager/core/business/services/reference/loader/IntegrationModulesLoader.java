package com.salesmanager.core.business.services.reference.loader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.model.system.IntegrationModule;
import com.salesmanager.core.model.system.ModuleConfig;

@Component
public class IntegrationModulesLoader {
	

	private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationModulesLoader.class);
	

	public List<IntegrationModule> loadIntegrationModules(String jsonFilePath) throws Exception {
		
		
		List<IntegrationModule> modules = new ArrayList<IntegrationModule>();
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			
            InputStream in =
                this.getClass().getClassLoader().getResourceAsStream(jsonFilePath);
			
            
            @SuppressWarnings("rawtypes")
			Map[] objects = mapper.readValue(in, Map[].class);
            
												System.out.println("$#2741#"); System.out.println("$#2740#"); for(int i = 0; i < objects.length; i++) {
            	
            	modules.add(this.loadModule(objects[i]));
            }
            
												System.out.println("$#2742#"); return modules;

  		} catch (Exception e) {
  			throw new ServiceException(e);
  		}
  		
  		

		
	
	
	
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IntegrationModule loadModule(Map object) throws Exception {
		
			ObjectMapper mapper = new ObjectMapper();
	    	IntegrationModule module = new IntegrationModule();
						System.out.println("$#2743#"); module.setModule((String)object.get("module"));
						System.out.println("$#2744#"); module.setCode((String)object.get("code"));
						System.out.println("$#2745#"); module.setImage((String)object.get("image"));
	    	
						System.out.println("$#2746#"); if(object.get("type")!=null) {
							System.out.println("$#2747#"); module.setType((String)object.get("type"));
	    	}
	    	
						System.out.println("$#2748#"); if(object.get("customModule")!=null) {
	    		Object o = object.get("customModule");
	    		Boolean b = false;
							System.out.println("$#2749#"); if(o instanceof Boolean) {
	    			b = (Boolean)object.get("customModule");
	    		} else {
	    			try {
	    				b = new Boolean((String)object.get("customModule"));
	    			} catch(Exception e) {
	    				LOGGER.error("Cannot cast " + o.getClass() + " tp a boolean value");
	    			}
	    		}
							System.out.println("$#2750#"); module.setCustomModule(b);
	    	}
	    	//module.setRegions(regions)
						System.out.println("$#2751#"); if(object.get("details")!=null) {
	    		
	    		Map<String,String> details = (Map<String,String>)object.get("details");
							System.out.println("$#2752#"); module.setDetails(details);
	    		
	    		//maintain the original json structure
	    		StringBuilder detailsStructure = new StringBuilder();
	    		int count = 0;
	    		detailsStructure.append("{");
	    		for(String key : details.keySet()) {
	    			String jsonKeyString = mapper.writeValueAsString(key);
	    			detailsStructure.append(jsonKeyString);
	    			detailsStructure.append(":");
	    			String jsonValueString = mapper.writeValueAsString(details.get(key));
	    			detailsStructure.append(jsonValueString);
											System.out.println("$#2755#"); System.out.println("$#2754#"); System.out.println("$#2753#"); if(count<(details.size()-1)) {
	        			detailsStructure.append(",");
	        		}
											System.out.println("$#2756#"); count++;
	    		}
	    		detailsStructure.append("}");
							System.out.println("$#2757#"); module.setConfigDetails(detailsStructure.toString());
	    		
	    	}
	    	
	    	
	    	List confs = (List)object.get("configuration");
	    	
	    	//convert to json
	    	
	    	
	    	
						System.out.println("$#2758#"); if(confs!=null) {
	    		StringBuilder configString = new StringBuilder();
	    		configString.append("[");
	    		Map<String,ModuleConfig> moduleConfigs = new HashMap<String,ModuleConfig>();
	        	int count=0;
	    		for(Object oo : confs) {
	        		
	        		Map values = (Map)oo;
	        		
	        		String env = (String)values.get("env");
	        		
	        		ModuleConfig config = new ModuleConfig();
											System.out.println("$#2759#"); config.setScheme((String)values.get("scheme"));
											System.out.println("$#2760#"); config.setHost((String)values.get("host"));
											System.out.println("$#2761#"); config.setPort((String)values.get("port"));
											System.out.println("$#2762#"); config.setUri((String)values.get("uri"));
											System.out.println("$#2763#"); config.setEnv((String)values.get("env"));
											System.out.println("$#2764#"); if((String)values.get("config1")!=null) {
												System.out.println("$#2765#"); config.setConfig1((String)values.get("config1"));
	        		}
											System.out.println("$#2766#"); if((String)values.get("config2")!=null) {
												System.out.println("$#2767#"); config.setConfig2((String)values.get("config2"));
	        		}
	        		
	        		String jsonConfigString = mapper.writeValueAsString(config);
	        		configString.append(jsonConfigString);
	        		
	        		moduleConfigs.put(env, config);
	        		
											System.out.println("$#2770#"); System.out.println("$#2769#"); System.out.println("$#2768#"); if(count<(confs.size()-1)) {
	        			configString.append(",");
	        		}
											System.out.println("$#2771#"); count++;
	        		
	        		
	        	}
	        	configString.append("]");
										System.out.println("$#2772#"); module.setConfiguration(configString.toString());
										System.out.println("$#2773#"); module.setModuleConfigs(moduleConfigs);
	    	}
	    	
	    	List<String> regions = (List<String>)object.get("regions");
						System.out.println("$#2774#"); if(regions!=null) {
	    		
	
	    		StringBuilder configString = new StringBuilder();
	    		configString.append("[");
	    		int count=0;
	    		for(String region : regions) {
	    			
	    			module.getRegionsSet().add(region);
	    			String jsonConfigString = mapper.writeValueAsString(region);
	    			configString.append(jsonConfigString);
	    			
											System.out.println("$#2777#"); System.out.println("$#2776#"); System.out.println("$#2775#"); if(count<(regions.size()-1)) {
	        			configString.append(",");
	        		}
											System.out.println("$#2778#"); count++;
	
	    		}
	    		configString.append("]");
							System.out.println("$#2779#"); module.setRegions(configString.toString());
	
	    	}
	    	
						System.out.println("$#2780#"); return module;
    	
		
	}

}
