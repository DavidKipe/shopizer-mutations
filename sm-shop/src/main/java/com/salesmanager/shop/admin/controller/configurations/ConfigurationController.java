package com.salesmanager.shop.admin.controller.configurations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.salesmanager.core.business.modules.email.EmailConfig;
import com.salesmanager.core.business.services.system.EmailService;
import com.salesmanager.core.business.services.system.MerchantConfigurationService;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.system.MerchantConfiguration;
import com.salesmanager.core.model.system.MerchantConfigurationType;
import com.salesmanager.shop.admin.controller.ControllerConstants;
import com.salesmanager.shop.admin.model.web.ConfigListWrapper;
import com.salesmanager.shop.admin.model.web.Menu;
import com.salesmanager.shop.constants.Constants;




@Controller
public class ConfigurationController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationController.class);
	
	private static final String P_MASK = "*****";
	
	@Inject
	private MerchantConfigurationService merchantConfigurationService;
	
	@Inject
	private EmailService emailService;

	@Inject
	Environment env;
	

	@PreAuthorize("hasRole('AUTH')")
	@RequestMapping(value="/admin/configuration/accounts.html", method=RequestMethod.GET)
	public String displayAccountsConfguration(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		System.out.println("$#5028#"); setConfigurationMenu(model, request);
		List<MerchantConfiguration> configs = new ArrayList<MerchantConfiguration>();
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		MerchantConfiguration merchantFBConfiguration = merchantConfigurationService.getMerchantConfiguration(Constants.KEY_FACEBOOK_PAGE_URL,store);
		System.out.println("$#5029#"); if(null == merchantFBConfiguration)
		{
			merchantFBConfiguration = new MerchantConfiguration();
			System.out.println("$#5030#"); merchantFBConfiguration.setKey(Constants.KEY_FACEBOOK_PAGE_URL);
			System.out.println("$#5031#"); merchantFBConfiguration.setMerchantConfigurationType(MerchantConfigurationType.SOCIAL);
		}
		configs.add(merchantFBConfiguration);
		
		MerchantConfiguration merchantGoogleAnalyticsConfiguration = merchantConfigurationService.getMerchantConfiguration(Constants.KEY_GOOGLE_ANALYTICS_URL,store);
		System.out.println("$#5032#"); if(null == merchantGoogleAnalyticsConfiguration)
		{
			merchantGoogleAnalyticsConfiguration = new MerchantConfiguration();
			System.out.println("$#5033#"); merchantGoogleAnalyticsConfiguration.setKey(Constants.KEY_GOOGLE_ANALYTICS_URL);
			System.out.println("$#5034#"); merchantGoogleAnalyticsConfiguration.setMerchantConfigurationType(MerchantConfigurationType.SOCIAL);
		}
		configs.add(merchantGoogleAnalyticsConfiguration);
		
		MerchantConfiguration merchantInstagramConfiguration = merchantConfigurationService.getMerchantConfiguration(Constants.KEY_INSTAGRAM_URL,store);
		System.out.println("$#5035#"); if(null == merchantInstagramConfiguration)
		{
			merchantInstagramConfiguration = new MerchantConfiguration();
			System.out.println("$#5036#"); merchantInstagramConfiguration.setKey(Constants.KEY_INSTAGRAM_URL);
			System.out.println("$#5037#"); merchantInstagramConfiguration.setMerchantConfigurationType(MerchantConfigurationType.SOCIAL);
		}
		configs.add(merchantInstagramConfiguration);
		
		MerchantConfiguration merchantPinterestConfiguration = merchantConfigurationService.getMerchantConfiguration(Constants.KEY_PINTEREST_PAGE_URL,store);
		System.out.println("$#5038#"); if(null == merchantPinterestConfiguration)
		{
			merchantPinterestConfiguration = new MerchantConfiguration();
			System.out.println("$#5039#"); merchantPinterestConfiguration.setKey(Constants.KEY_PINTEREST_PAGE_URL);
			System.out.println("$#5040#"); merchantPinterestConfiguration.setMerchantConfigurationType(MerchantConfigurationType.SOCIAL);
		}
		configs.add(merchantPinterestConfiguration);
		
		/**
		MerchantConfiguration merchantGoogleApiConfiguration = merchantConfigurationService.getMerchantConfiguration(Constants.KEY_GOOGLE_API_KEY,store);
		if(null == merchantGoogleApiConfiguration)
		{
			merchantGoogleApiConfiguration = new MerchantConfiguration();
			merchantGoogleApiConfiguration.setKey(Constants.KEY_GOOGLE_API_KEY);
			merchantGoogleApiConfiguration.setMerchantConfigurationType(MerchantConfigurationType.CONFIG);
		}
		configs.add(merchantGoogleApiConfiguration);
		**/
		
		MerchantConfiguration twitterConfiguration = merchantConfigurationService.getMerchantConfiguration(Constants.KEY_TWITTER_HANDLE,store);
		System.out.println("$#5041#"); if(null == twitterConfiguration)
		{
			twitterConfiguration = new MerchantConfiguration();
			System.out.println("$#5042#"); twitterConfiguration.setKey(Constants.KEY_TWITTER_HANDLE);
			System.out.println("$#5043#"); twitterConfiguration.setMerchantConfigurationType(MerchantConfigurationType.SOCIAL);
		}
		configs.add(twitterConfiguration);
		
		ConfigListWrapper configWrapper = new ConfigListWrapper();
		System.out.println("$#5044#"); configWrapper.setMerchantConfigs(configs);
		model.addAttribute("configuration",configWrapper);
		
		System.out.println("$#5045#"); return com.salesmanager.shop.admin.controller.ControllerConstants.Tiles.Configuration.accounts;
	}
	
	@PreAuthorize("hasRole('AUTH')")
	@RequestMapping(value="/admin/configuration/saveConfiguration.html", method=RequestMethod.POST)
	public String saveConfigurations(@ModelAttribute("configuration") ConfigListWrapper configWrapper, BindingResult result, Model model, HttpServletRequest request, Locale locale) throws Exception
	{
		System.out.println("$#5046#"); setConfigurationMenu(model, request);
		List<MerchantConfiguration> configs = configWrapper.getMerchantConfigs();
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		for(MerchantConfiguration mConfigs : configs)
		{
			System.out.println("$#5047#"); mConfigs.setMerchantStore(store);
			System.out.println("$#5048#"); if(!StringUtils.isBlank(mConfigs.getValue())) {
				System.out.println("$#5049#"); mConfigs.setMerchantConfigurationType(MerchantConfigurationType.SOCIAL);
				System.out.println("$#5050#"); merchantConfigurationService.saveOrUpdate(mConfigs);
			} else {//remove if submited blank and exists
				MerchantConfiguration config = merchantConfigurationService.getMerchantConfiguration(mConfigs.getKey(), store);
				System.out.println("$#5051#"); if(config!=null) {
					System.out.println("$#5052#"); merchantConfigurationService.delete(config);
				}
			}
		}	
		model.addAttribute("success","success");
		model.addAttribute("configuration",configWrapper);
		System.out.println("$#5053#"); return com.salesmanager.shop.admin.controller.ControllerConstants.Tiles.Configuration.accounts;
		
	}
	
	@PreAuthorize("hasRole('AUTH')")
	@RequestMapping(value="/admin/configuration/email.html", method=RequestMethod.GET)
	public String displayEmailSettings(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("$#5054#"); setEmailConfigurationMenu(model, request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		EmailConfig emailConfig = emailService.getEmailConfiguration(store);
		System.out.println("$#5055#"); if(emailConfig == null){
			emailConfig = new EmailConfig();
			//TODO: Need to check below properties. When there are no record available in MerchantConfguration table with EMAIL_CONFIG key, 
			// instead of showing blank fields in setup screen, show default configured values from email.properties
			System.out.println("$#5056#"); emailConfig.setProtocol(env.getProperty("mailSender.protocol"));
			System.out.println("$#5057#"); emailConfig.setHost(env.getProperty("mailSender.host"));
			System.out.println("$#5058#"); emailConfig.setPort(env.getProperty("mailSender.port}"));
			System.out.println("$#5059#"); emailConfig.setUsername(env.getProperty("mailSender.username"));
			//emailConfig.setPassword(env.getProperty("mailSender.password"));
			System.out.println("$#5060#"); emailConfig.setPassword(P_MASK);
			System.out.println("$#5061#"); emailConfig.setSmtpAuth(Boolean.parseBoolean(env.getProperty("mailSender.mail.smtp.auth")));
			System.out.println("$#5062#"); emailConfig.setStarttls(Boolean.parseBoolean(env.getProperty("mail.smtp.starttls.enable")));
		}
		
		model.addAttribute("configuration", emailConfig);
		System.out.println("$#5063#"); return ControllerConstants.Tiles.Configuration.email;
	}
	
	@PreAuthorize("hasRole('AUTH')")
	@RequestMapping(value="/admin/configuration/saveEmailConfiguration.html", method=RequestMethod.POST)
	public String saveEmailSettings(@ModelAttribute("configuration") EmailConfig config, BindingResult result, Model model, HttpServletRequest request, Locale locale) throws Exception {
		System.out.println("$#5064#"); setEmailConfigurationMenu(model, request);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		EmailConfig emailConfig = emailService.getEmailConfiguration(store);
		System.out.println("$#5065#"); if(emailConfig == null){
			emailConfig = new EmailConfig();
		}
		
		// populte EmailConfig model from UI values
		System.out.println("$#5066#"); emailConfig.setProtocol(config.getProtocol());
		System.out.println("$#5067#"); emailConfig.setHost(config.getHost());
		System.out.println("$#5068#"); emailConfig.setPort(config.getPort());
		System.out.println("$#5069#"); emailConfig.setUsername(config.getUsername());
		System.out.println("$#5070#"); emailConfig.setPassword(emailConfig.getPassword());
		System.out.println("$#5071#"); if(!StringUtils.isBlank(config.getPassword())) {
			System.out.println("$#5072#"); if(!config.getPassword().equals(P_MASK)) {
				System.out.println("$#5073#"); emailConfig.setPassword(config.getPassword());
			}
		}
		System.out.println("$#5074#"); emailConfig.setSmtpAuth(config.isSmtpAuth());
		System.out.println("$#5075#"); emailConfig.setStarttls(config.isStarttls());
		
		System.out.println("$#5076#"); emailService.saveEmailConfiguration(emailConfig, store);
		
		model.addAttribute("configuration", emailConfig);
		model.addAttribute("success","success");
		System.out.println("$#5077#"); return ControllerConstants.Tiles.Configuration.email;
	}
	
	private void setConfigurationMenu(Model model, HttpServletRequest request) throws Exception {
		
		Map<String,String> activeMenus = new HashMap<String,String>();
		activeMenus.put("configuration", "configuration");
		activeMenus.put("accounts-conf", "accounts-conf");
		
		@SuppressWarnings("unchecked")
		Map<String, Menu> menus = (Map<String, Menu>)request.getAttribute("MENUMAP");
		
		Menu currentMenu = (Menu)menus.get("configuration");
		model.addAttribute("currentMenu",currentMenu);
		model.addAttribute("activeMenus",activeMenus);
	}
	
	private void setEmailConfigurationMenu(Model model, HttpServletRequest request) throws Exception {
		
		Map<String,String> activeMenus = new HashMap<String,String>();
		activeMenus.put("configuration", "configuration");
		activeMenus.put("email-conf", "email-conf");
		
		@SuppressWarnings("unchecked")
		Map<String, Menu> menus = (Map<String, Menu>)request.getAttribute("MENUMAP");
		
		Menu currentMenu = (Menu)menus.get("configuration");
		model.addAttribute("currentMenu",currentMenu);
		model.addAttribute("activeMenus",activeMenus);
	}
}
