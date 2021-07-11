package com.salesmanager.core.business.services.system;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.system.ModuleConfigurationRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.business.services.reference.loader.IntegrationModulesLoader;
import com.salesmanager.core.business.utils.CacheUtils;
import com.salesmanager.core.model.system.IntegrationModule;
import com.salesmanager.core.model.system.ModuleConfig;

@Service("moduleConfigurationService")
public class ModuleConfigurationServiceImpl extends
		SalesManagerEntityServiceImpl<Long, IntegrationModule> implements
		ModuleConfigurationService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ModuleConfigurationServiceImpl.class);
	
	@Inject
	private IntegrationModulesLoader integrationModulesLoader;
	

	
	private ModuleConfigurationRepository moduleConfigurationRepository;
	
	@Inject
	private CacheUtils cache;
	
	@Inject
	public ModuleConfigurationServiceImpl(
			ModuleConfigurationRepository moduleConfigurationRepository) {
			super(moduleConfigurationRepository);
			this.moduleConfigurationRepository = moduleConfigurationRepository;
	}
	
	@Override
	public IntegrationModule getByCode(String moduleCode) {
		System.out.println("$#3242#"); return moduleConfigurationRepository.findByCode(moduleCode);
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<IntegrationModule> getIntegrationModules(String module) {
		
		
		List<IntegrationModule> modules = null;
		try {
			
			//CacheUtils cacheUtils = CacheUtils.getInstance();
			modules = (List<IntegrationModule>) cache.getFromCache("INTEGRATION_M)" + module);
			System.out.println("$#3243#"); if(modules==null) {
				modules = moduleConfigurationRepository.findByModule(module);
				//set json objects
				for(IntegrationModule mod : modules) {
					
					String regions = mod.getRegions();
					System.out.println("$#3244#"); if(regions!=null) {
						Object objRegions=JSONValue.parse(regions); 
						JSONArray arrayRegions=(JSONArray)objRegions;
						Iterator i = arrayRegions.iterator();
						while(i.hasNext()) {
							mod.getRegionsSet().add((String)i.next());
						}
					}
					
					
					String details = mod.getConfigDetails();
					System.out.println("$#3245#"); if(details!=null) {
						
						//Map objects = mapper.readValue(config, Map.class);

						Map<String,String> objDetails= (Map<String, String>) JSONValue.parse(details); 
						System.out.println("$#3246#"); mod.setDetails(objDetails);

						
					}
					
					
					String configs = mod.getConfiguration();
					System.out.println("$#3247#"); if(configs!=null) {
						
						//Map objects = mapper.readValue(config, Map.class);

						Object objConfigs=JSONValue.parse(configs); 
						JSONArray arrayConfigs=(JSONArray)objConfigs;
						
						Map<String,ModuleConfig> moduleConfigs = new HashMap<String,ModuleConfig>();
						
						Iterator i = arrayConfigs.iterator();
						while(i.hasNext()) {
							
							Map values = (Map)i.next();
							String env = (String)values.get("env");
		            		ModuleConfig config = new ModuleConfig();
																System.out.println("$#3248#"); config.setScheme((String)values.get("scheme"));
																System.out.println("$#3249#"); config.setHost((String)values.get("host"));
																System.out.println("$#3250#"); config.setPort((String)values.get("port"));
																System.out.println("$#3251#"); config.setUri((String)values.get("uri"));
																System.out.println("$#3252#"); config.setEnv((String)values.get("env"));
																System.out.println("$#3253#"); if((String)values.get("config1")!=null) {
																	System.out.println("$#3254#"); config.setConfig1((String)values.get("config1"));
		            		}
																System.out.println("$#3255#"); if((String)values.get("config2")!=null) {
																	System.out.println("$#3256#"); config.setConfig1((String)values.get("config2"));
		            		}
		            		
		            		moduleConfigs.put(env, config);
		            		
		            		
							
						}
						
						System.out.println("$#3257#"); mod.setModuleConfigs(moduleConfigs);
						

					}


				}
				System.out.println("$#3258#"); cache.putInCache(modules, "INTEGRATION_M)" + module);
			}

		} catch (Exception e) {
			LOGGER.error("getIntegrationModules()", e);
		}
		System.out.println("$#3259#"); return modules;
		
		
	}

	@Override
	public void createOrUpdateModule(String json) throws ServiceException {
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			
			
			@SuppressWarnings("rawtypes")
			Map object = mapper.readValue(json, Map.class);
			
			IntegrationModule module = integrationModulesLoader.loadModule(object);
			
												System.out.println("$#3260#"); if(module!=null) {
            	IntegrationModule m = this.getByCode(module.getCode());
													System.out.println("$#3261#"); if(m!=null) {
														System.out.println("$#3262#"); this.delete(m);
            	}
													System.out.println("$#3263#"); this.create(module);
            }



  		} catch (Exception e) {
  			throw new ServiceException(e);
  		} 
		
		
		
		
	}
	
	

	



}
