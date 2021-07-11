package com.salesmanager.shop.admin.controller.merchant;

import com.salesmanager.core.business.modules.email.Email;
import com.salesmanager.core.business.services.merchant.MerchantStoreService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.currency.CurrencyService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.services.system.EmailService;
import com.salesmanager.core.business.services.user.UserService;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.currency.Currency;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.core.model.user.User;
import com.salesmanager.shop.admin.controller.ControllerConstants;
import com.salesmanager.shop.admin.model.reference.Size;
import com.salesmanager.shop.admin.model.reference.Weight;
import com.salesmanager.shop.admin.model.web.Menu;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.constants.EmailConstants;
import com.salesmanager.shop.utils.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

@Controller
public class MerchantStoreController {

	private static final Logger LOGGER = LoggerFactory.getLogger(MerchantStoreController.class);

	@Inject
	private MerchantStoreService merchantStoreService;

	@Inject
	private CountryService countryService;

	@Inject
	private ZoneService zoneService;

	@Inject
	private LanguageService languageService;

	@Inject
	private CurrencyService currencyService;

	@Inject
	private UserService userService;

	@Inject
	private LabelUtils messages;

	@Inject
	private EmailService emailService;

	@Inject
	private EmailUtils emailUtils;

	@Inject
	private FilePathUtils filePathUtils;

	private final static String NEW_STORE_TMPL = "email_template_new_store.ftl";

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/admin/store/list.html", method = RequestMethod.GET)
	public String displayStores(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale)
			throws Exception {

		System.out.println("$#5554#"); setMenu(model, request);
		System.out.println("$#5555#"); return ControllerConstants.Tiles.Store.stores;
	}

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/admin/store/paging.html", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> pageStores(HttpServletRequest request, HttpServletResponse response) {

		AjaxResponse resp = new AjaxResponse();

		try {

			List<MerchantStore> stores = merchantStoreService.findAllStoreCodeNameEmail();

			for (MerchantStore store : stores) {

				System.out.println("$#5556#"); if (!store.getCode().equals(MerchantStore.DEFAULT_STORE)) {
					Map<String, String> entry = new HashMap<String, String>();
					entry.put("storeId", String.valueOf(store.getId()));
					entry.put("code", store.getCode());
					entry.put("name", store.getStorename());
					entry.put("email", store.getStoreEmailAddress());
					System.out.println("$#5557#"); resp.addDataEntry(entry);
				}

			}

			System.out.println("$#5558#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_SUCCESS);

		} catch (Exception e) {
			LOGGER.error("Error while paging products", e);
			System.out.println("$#5559#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
		}

		String returnString = resp.toJSONString();

		final HttpHeaders httpHeaders = new HttpHeaders();
		System.out.println("$#5560#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		System.out.println("$#5561#"); return new ResponseEntity<String>(returnString, httpHeaders, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('STORE')")
	@RequestMapping(value = "/admin/store/storeCreate.html", method = RequestMethod.GET)
	public String displayMerchantStoreCreate(Model model, HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws Exception {

		System.out.println("$#5562#"); setMenu(model, request);

		MerchantStore store = new MerchantStore();

		MerchantStore sessionStore = (MerchantStore) request.getAttribute(Constants.ADMIN_STORE);
		System.out.println("$#5563#"); store.setCurrency(sessionStore.getCurrency());
		System.out.println("$#5564#"); store.setCountry(sessionStore.getCountry());
		System.out.println("$#5565#"); store.setZone(sessionStore.getZone());
		System.out.println("$#5566#"); store.setStorestateprovince(sessionStore.getStorestateprovince());
		System.out.println("$#5567#"); store.setLanguages(sessionStore.getLanguages());
		System.out.println("$#5568#"); store.setDomainName(sessionStore.getDomainName());

		System.out.println("$#5569#"); return displayMerchantStore(store, model, request, response, locale);
	}

	@PreAuthorize("hasRole('STORE')")
	@RequestMapping(value = "/admin/store/store.html", method = RequestMethod.GET)
	public String displayMerchantStore(Model model, HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws Exception {

		System.out.println("$#5570#"); setMenu(model, request);
		MerchantStore store = (MerchantStore) request.getAttribute(Constants.ADMIN_STORE);
		System.out.println("$#5571#"); return displayMerchantStore(store, model, request, response, locale);
	}

	@PreAuthorize("hasRole('STORE')")
	@RequestMapping(value = "/admin/store/editStore.html", method = RequestMethod.GET)
	public String displayMerchantStore(@ModelAttribute("id") Integer id, Model model, HttpServletRequest request,
			HttpServletResponse response, Locale locale) throws Exception {

		System.out.println("$#5572#"); setMenu(model, request);
		MerchantStore store = merchantStoreService.getById(id);
		System.out.println("$#5573#"); return displayMerchantStore(store, model, request, response, locale);
	}

	private String displayMerchantStore(MerchantStore store, Model model, HttpServletRequest request,
			HttpServletResponse response, Locale locale) throws Exception {

		System.out.println("$#5574#"); setMenu(model, request);
		Language language = (Language) request.getAttribute("LANGUAGE");
		List<Language> languages = languageService.getLanguages();
		List<Currency> currencies = currencyService.list();
		System.out.println("$#5575#"); if (CollectionUtils.isNotEmpty(currencies)) {
					System.out.println("$#5576#"); Collections.sort(currencies, new Comparator<Currency>() {
			      @Override
			      public int compare(final Currency object1, final Currency object2) {
													System.out.println("$#5638#"); return object1.getName().compareTo(object2.getName());
			      }
			  });
			}
		Date dt = store.getInBusinessSince();
		System.out.println("$#5577#"); if (dt != null) {
			System.out.println("$#5578#"); store.setDateBusinessSince(DateUtil.formatDate(dt));
		} else {
			System.out.println("$#5579#"); store.setDateBusinessSince(DateUtil.formatDate(new Date()));
		}

		// get countries
		List<Country> countries = countryService.getCountries(language);

		List<Weight> weights = new ArrayList<Weight>();
		weights.add(new Weight("LB", messages.getMessage("label.generic.weightunit.LB", locale)));
		weights.add(new Weight("KG", messages.getMessage("label.generic.weightunit.KG", locale)));

		List<Size> sizes = new ArrayList<Size>();
		sizes.add(new Size("CM", messages.getMessage("label.generic.sizeunit.CM", locale)));
		sizes.add(new Size("IN", messages.getMessage("label.generic.sizeunit.IN", locale)));

		// display menu

		model.addAttribute("countries", countries);
		model.addAttribute("languages", languages);
		model.addAttribute("currencies", currencies);

		model.addAttribute("weights", weights);
		model.addAttribute("sizes", sizes);
		model.addAttribute("store", store);

		System.out.println("$#5580#"); return "admin-store";

	}

	@PreAuthorize("hasRole('STORE')")
	@RequestMapping(value = "/admin/store/save.html", method = RequestMethod.POST)
	public String saveMerchantStore(@Valid @ModelAttribute("store") MerchantStore store, BindingResult result,
			Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {

		System.out.println("$#5581#"); setMenu(model, request);
		MerchantStore sessionStore = (MerchantStore) request.getAttribute(Constants.ADMIN_STORE);

		System.out.println("$#5582#"); if (store.getId() != null) {
			System.out.println("$#5583#"); if (store.getId().intValue() != sessionStore.getId().intValue()) {
				System.out.println("$#5584#"); return "redirect:/admin/store/store.html";
			}
		}

		Date date = new Date();
		System.out.println("$#5585#"); if (!StringUtils.isBlank(store.getDateBusinessSince())) {
			try {
				date = DateUtil.getDate(store.getDateBusinessSince());
				System.out.println("$#5586#"); store.setInBusinessSince(date);
			} catch (Exception e) {
				ObjectError error = new ObjectError("dateBusinessSince",
						messages.getMessage("message.invalid.date", locale));
				System.out.println("$#5587#"); result.addError(error);
			}
		}

		List<Currency> currencies = currencyService.list();

		Language language = (Language) request.getAttribute("LANGUAGE");
		List<Language> languages = languageService.getLanguages();

		// get countries
		List<Country> countries = countryService.getCountries(language);

		List<Weight> weights = new ArrayList<Weight>();
		weights.add(new Weight("LB", messages.getMessage("label.generic.weightunit.LB", locale)));
		weights.add(new Weight("KG", messages.getMessage("label.generic.weightunit.KG", locale)));

		List<Size> sizes = new ArrayList<Size>();
		sizes.add(new Size("CM", messages.getMessage("label.generic.sizeunit.CM", locale)));
		sizes.add(new Size("IN", messages.getMessage("label.generic.sizeunit.IN", locale)));

		model.addAttribute("weights", weights);
		model.addAttribute("sizes", sizes);

		model.addAttribute("countries", countries);
		model.addAttribute("languages", languages);
		model.addAttribute("currencies", currencies);

		Country c = store.getCountry();
		List<Zone> zonesList = zoneService.getZones(c, language);

		System.out.println("$#5588#"); if ((zonesList == null || zonesList.size() == 0) && StringUtils.isBlank(store.getStorestateprovince())) {

			ObjectError error = new ObjectError("zone.code", messages.getMessage("merchant.zone.invalid", locale));
			System.out.println("$#5591#"); result.addError(error);

		}

		System.out.println("$#5592#"); if (result.hasErrors()) {
			System.out.println("$#5593#"); return "admin-store";
		}

		// get country
		Country country = store.getCountry();
		country = countryService.getByCode(country.getIsoCode());
		Zone zone = store.getZone();
		System.out.println("$#5594#"); if (zone != null) {
			zone = zoneService.getByCode(zone.getCode());
		}
		Currency currency = store.getCurrency();
		currency = currencyService.getById(currency.getId());

		List<Language> supportedLanguages = store.getLanguages();
		List<Language> supportedLanguagesList = new ArrayList<Language>();
		Map<String, Language> languagesMap = languageService.getLanguagesMap();
		for (Language lang : supportedLanguages) {

			Language l = languagesMap.get(lang.getCode());
			System.out.println("$#5595#"); if (l != null) {
				supportedLanguagesList.add(l);
			}

		}

		Language defaultLanguage = store.getDefaultLanguage();
		defaultLanguage = languageService.getById(defaultLanguage.getId());
		System.out.println("$#5596#"); if (defaultLanguage != null) {
			System.out.println("$#5597#"); store.setDefaultLanguage(defaultLanguage);
		}

		Locale storeLocale = LocaleUtils.getLocale(defaultLanguage);

		System.out.println("$#5598#"); store.setStoreTemplate(sessionStore.getStoreTemplate());
		System.out.println("$#5599#"); store.setCountry(country);
		System.out.println("$#5600#"); store.setZone(zone);
		System.out.println("$#5601#"); store.setCurrency(currency);
		System.out.println("$#5602#"); store.setDefaultLanguage(defaultLanguage);
		System.out.println("$#5603#"); store.setLanguages(supportedLanguagesList);
		System.out.println("$#5604#"); store.setLanguages(supportedLanguagesList);

		System.out.println("$#5605#"); merchantStoreService.saveOrUpdate(store);

		System.out.println("$#5606#"); if (!store.getCode().equals(sessionStore.getCode())) {// create store
			// send email

			try {

				Map<String, String> templateTokens = emailUtils.createEmailObjectsMap(request.getContextPath(), store,
						messages, storeLocale);
				templateTokens.put(EmailConstants.EMAIL_NEW_STORE_TEXT,
						messages.getMessage("email.newstore.text", storeLocale));
				templateTokens.put(EmailConstants.EMAIL_STORE_NAME,
						messages.getMessage("email.newstore.name", new String[] { store.getStorename() }, storeLocale));
				templateTokens.put(EmailConstants.EMAIL_ADMIN_STORE_INFO_LABEL,
						messages.getMessage("email.newstore.info", storeLocale));

				templateTokens.put(EmailConstants.EMAIL_ADMIN_URL_LABEL,
						messages.getMessage("label.adminurl", storeLocale));
				templateTokens.put(EmailConstants.EMAIL_ADMIN_URL, filePathUtils.buildAdminUri(store, request));

				Email email = new Email();
				System.out.println("$#5607#"); email.setFrom(store.getStorename());
				System.out.println("$#5608#"); email.setFromEmail(store.getStoreEmailAddress());
				System.out.println("$#5609#"); email.setSubject(messages.getMessage("email.newstore.title", storeLocale));
				System.out.println("$#5610#"); email.setTo(store.getStoreEmailAddress());
				System.out.println("$#5611#"); email.setTemplateName(NEW_STORE_TMPL);
				System.out.println("$#5612#"); email.setTemplateTokens(templateTokens);

				System.out.println("$#5613#"); emailService.sendHtmlEmail(store, email);

			} catch (Exception e) {
				LOGGER.error("Cannot send email to user", e);
			}

		}

		sessionStore = merchantStoreService.getByCode(sessionStore.getCode());

		// update session store
		System.out.println("$#5614#"); request.getSession().setAttribute(Constants.ADMIN_STORE, sessionStore);

		model.addAttribute("success", "success");
		model.addAttribute("store", store);

		System.out.println("$#5615#"); return "admin-store";
	}

	@PreAuthorize("hasRole('AUTH')")
	@RequestMapping(value = "/admin/store/checkStoreCode.html", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> checkStoreCode(HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		String code = request.getParameter("code");

		AjaxResponse resp = new AjaxResponse();

		final HttpHeaders httpHeaders = new HttpHeaders();
		System.out.println("$#5616#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		try {

			System.out.println("$#5617#"); if (StringUtils.isBlank(code)) {
				System.out.println("$#5618#"); resp.setStatus(AjaxResponse.CODE_ALREADY_EXIST);
				System.out.println("$#5619#"); return new ResponseEntity<String>(resp.toJSONString(), httpHeaders, HttpStatus.OK);
			}

			MerchantStore store = merchantStoreService.getByCode(code);

			System.out.println("$#5620#"); if (store != null) {
				System.out.println("$#5621#"); resp.setStatus(AjaxResponse.CODE_ALREADY_EXIST);
				System.out.println("$#5622#"); return new ResponseEntity<String>(resp.toJSONString(), httpHeaders, HttpStatus.OK);
			}

			System.out.println("$#5623#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);

		} catch (Exception e) {
			LOGGER.error("Error while getting user", e);
			System.out.println("$#5624#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#5625#"); resp.setErrorMessage(e);
		}

		String returnString = resp.toJSONString();

		System.out.println("$#5626#"); return new ResponseEntity<String>(returnString, httpHeaders, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/admin/store/remove.html", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> removeMerchantStore(HttpServletRequest request, Locale locale)
			throws Exception {

		String sMerchantStoreId = request.getParameter("storeId");

		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders = new HttpHeaders();
		System.out.println("$#5627#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		try {

			Integer storeId = Integer.parseInt(sMerchantStoreId);
			MerchantStore store = merchantStoreService.getById(storeId);

			User user = userService.getByUserName(request.getRemoteUser());

			/**
			 * In order to remove a Store the logged in ser must be SUPERADMIN
			 */

			// check if the user removed has group SUPERADMIN
			boolean isSuperAdmin = false;
			System.out.println("$#5628#"); if (UserUtils.userInGroup(user, Constants.GROUP_SUPERADMIN)) {
				isSuperAdmin = true;
			}

			System.out.println("$#5629#"); if (!isSuperAdmin) {
				System.out.println("$#5630#"); resp.setStatusMessage(messages.getMessage("message.security.caanotremovesuperadmin", locale));
				System.out.println("$#5631#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#5632#"); return new ResponseEntity<String>(returnString, httpHeaders, HttpStatus.OK);
			}

			System.out.println("$#5633#"); merchantStoreService.delete(store);

			System.out.println("$#5634#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);

		} catch (Exception e) {
			LOGGER.error("Error while deleting product price", e);
			System.out.println("$#5635#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#5636#"); resp.setErrorMessage(e);
		}

		String returnString = resp.toJSONString();

		System.out.println("$#5637#"); return new ResponseEntity<String>(returnString, httpHeaders, HttpStatus.OK);

	}

	private void setMenu(Model model, HttpServletRequest request) throws Exception {

		// display menu
		Map<String, String> activeMenus = new HashMap<String, String>();
		activeMenus.put("store", "store");
		activeMenus.put("storeDetails", "storeDetails");

		@SuppressWarnings("unchecked")
		Map<String, Menu> menus = (Map<String, Menu>) request.getAttribute("MENUMAP");

		Menu currentMenu = (Menu) menus.get("store");
		model.addAttribute("currentMenu", currentMenu);
		model.addAttribute("activeMenus", activeMenus);
		//

	}

}
