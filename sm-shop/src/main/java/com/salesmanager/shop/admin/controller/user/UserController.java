package com.salesmanager.shop.admin.controller.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.modules.email.Email;
import com.salesmanager.core.business.services.merchant.MerchantStoreService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.system.EmailService;
import com.salesmanager.core.business.services.user.GroupService;
import com.salesmanager.core.business.services.user.UserService;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.user.Group;
import com.salesmanager.core.model.user.GroupType;
import com.salesmanager.core.model.user.User;
import com.salesmanager.shop.admin.controller.ControllerConstants;
import com.salesmanager.shop.admin.model.secutity.Password;
import com.salesmanager.shop.admin.model.userpassword.UserReset;
import com.salesmanager.shop.admin.model.web.Menu;
import com.salesmanager.shop.admin.security.SecurityQuestion;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.constants.EmailConstants;
import com.salesmanager.shop.utils.EmailUtils;
import com.salesmanager.shop.utils.FilePathUtils;
import com.salesmanager.shop.utils.LabelUtils;
import com.salesmanager.shop.utils.LocaleUtils;
import com.salesmanager.shop.utils.UserUtils;

@Controller
public class UserController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	
	@Inject
	private LanguageService languageService;
	
	@Inject
	private UserService userService;

	@Inject
	private GroupService groupService;

	@Inject
	private EmailService emailService;
	
	@Inject
	private MerchantStoreService merchantStoreService;
	
	@Inject
	LabelUtils messages;
	
	@Inject
	private FilePathUtils filePathUtils;
	
	@Inject
	private EmailUtils emailUtils;
	
	@Inject
	@Named("passwordEncoder")
	private PasswordEncoder passwordEncoder;
	
	private final static String QUESTION_1 = "question1";
	private final static String QUESTION_2 = "question2";
	private final static String QUESTION_3 = "question3";
	private final static String RESET_PASSWORD_TPL = "email_template_password_reset_user.ftl";	
	private final static String NEW_USER_TMPL = "email_template_new_user.ftl";
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value="/admin/users/list.html", method=RequestMethod.GET)
	public String displayUsers(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {

		//The users are retrieved from the paging method
		System.out.println("$#7573#"); setMenu(model,request);
		System.out.println("$#7574#"); return ControllerConstants.Tiles.User.users;
	}
	
	/**
	 * Displays a list of users that can be managed by admins
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/admin/users/paging.html", method = RequestMethod.POST)
	public @ResponseBody
	ResponseEntity<String> pageUsers(HttpServletRequest request,
			HttpServletResponse response) {

		AjaxResponse resp = new AjaxResponse();
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);

		String sCurrentUser = request.getRemoteUser();
		
		
		try {

			User currentUser = userService.getByUserName(sCurrentUser);
			List<User> users = null;
			System.out.println("$#7575#"); if(UserUtils.userInGroup(currentUser, Constants.GROUP_SUPERADMIN) ) {
				users = userService.listUser();
			} else {
				users = userService.listByStore(store);
			}
			 

			for (User user : users) {
				
				System.out.println("$#7576#"); if(!UserUtils.userInGroup(user, Constants.GROUP_SUPERADMIN)) {
					
					System.out.println("$#7577#"); if(!currentUser.equals(user.getAdminName())){

						@SuppressWarnings("rawtypes")
						Map entry = new HashMap();
						entry.put("userId", user.getId());
						entry.put("name", user.getFirstName() + " " + user.getLastName());
						entry.put("email", user.getAdminEmail());
						entry.put("active", user.isActive());
						System.out.println("$#7578#"); resp.addDataEntry(entry);
					
					}
				}
			}

			System.out.println("$#7579#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_SUCCESS);

		} catch (Exception e) {
			LOGGER.error("Error while paging products", e);
			System.out.println("$#7580#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
		}

		String returnString = resp.toJSONString();
		System.out.println("$#7581#"); return new ResponseEntity<String>(returnString,HttpStatus.OK);
	}

	@PreAuthorize("hasRole('AUTH')")
	@RequestMapping(value="/admin/users/password.html", method=RequestMethod.GET)
	public String displayChangePassword(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		System.out.println("$#7582#"); setMenu(model,request);
		String userName = request.getRemoteUser();
		User user = userService.getByUserName(userName);
		
		Password password = new Password();
		System.out.println("$#7583#"); password.setUser(user);
		
		model.addAttribute("password",password);
		model.addAttribute("user",user);
		System.out.println("$#7584#"); return ControllerConstants.Tiles.User.password;
	}
	
	
	@PreAuthorize("hasRole('AUTH')")
	@RequestMapping(value="/admin/users/savePassword.html", method=RequestMethod.POST)
	public String changePassword(@ModelAttribute("password") Password password, BindingResult result, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		System.out.println("$#7585#"); setMenu(model,request);
		String userName = request.getRemoteUser();
		User dbUser = userService.getByUserName(userName);
		

		System.out.println("$#7586#"); if(password.getUser().getId().longValue()!= dbUser.getId().longValue()) {
				System.out.println("$#7587#"); return "redirect:/admin/users/displayUser.html";
		}
		
		//validate password not empty
		System.out.println("$#7588#"); if(StringUtils.isBlank(password.getPassword())) {
			ObjectError error = new ObjectError("password",new StringBuilder().append(messages.getMessage("label.generic.password", locale)).append(" ").append(messages.getMessage("message.cannot.empty", locale)).toString());
			System.out.println("$#7589#"); result.addError(error);
			System.out.println("$#7590#"); return ControllerConstants.Tiles.User.password;
		}

		System.out.println("$#7591#"); if(!passwordEncoder.matches(password.getPassword(), dbUser.getAdminPassword())) {
			ObjectError error = new ObjectError("password",messages.getMessage("message.password.invalid", locale));
			System.out.println("$#7592#"); result.addError(error);
			System.out.println("$#7593#"); return ControllerConstants.Tiles.User.password;
		}
		

		System.out.println("$#7594#"); if(StringUtils.isBlank(password.getNewPassword())) {
			ObjectError error = new ObjectError("newPassword",new StringBuilder().append(messages.getMessage("label.generic.newpassword", locale)).append(" ").append(messages.getMessage("message.cannot.empty", locale)).toString());
			System.out.println("$#7595#"); result.addError(error);
		}
		
		System.out.println("$#7596#"); if(StringUtils.isBlank(password.getRepeatPassword())) {
			ObjectError error = new ObjectError("newPasswordAgain",new StringBuilder().append(messages.getMessage("label.generic.newpassword.repeat", locale)).append(" ").append(messages.getMessage("message.cannot.empty", locale)).toString());
			System.out.println("$#7597#"); result.addError(error);
		}
		
		System.out.println("$#7598#"); if(!password.getRepeatPassword().equals(password.getNewPassword())) {
			ObjectError error = new ObjectError("newPasswordAgain",messages.getMessage("message.password.different", locale));
			System.out.println("$#7599#"); result.addError(error);
		}
		
		System.out.println("$#7601#"); System.out.println("$#7600#"); if(password.getNewPassword().length()<6) {
			ObjectError error = new ObjectError("newPassword",messages.getMessage("message.password.length", locale));
			System.out.println("$#7602#"); result.addError(error);
		}
		
		System.out.println("$#7603#"); if (result.hasErrors()) {
			System.out.println("$#7604#"); return ControllerConstants.Tiles.User.password;
		}
		
		
		
		String pass = passwordEncoder.encode(password.getNewPassword());
		System.out.println("$#7605#"); dbUser.setAdminPassword(pass);
		System.out.println("$#7606#"); userService.update(dbUser);
		
		model.addAttribute("success","success");
		System.out.println("$#7607#"); return ControllerConstants.Tiles.User.password;
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value="/admin/users/createUser.html", method=RequestMethod.GET)
	public String displayUserCreate(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		System.out.println("$#7608#"); return displayUser(null,model,request,response,locale);
	}
	

	/**
	 * From user list
	 * @param id
	 * @param model
	 * @param request
	 * @param response
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("hasRole('AUTH')")
	@RequestMapping(value="/admin/users/displayStoreUser.html", method=RequestMethod.GET)
	public String displayUserEdit(@ModelAttribute("id") Long id, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {

		User dbUser = userService.getById(id);
		
		System.out.println("$#7609#"); if(dbUser==null) {
			LOGGER.info("User is null for id " + id);
			System.out.println("$#7610#"); return "redirect://admin/users/list.html";
		}
		
		
		System.out.println("$#7611#"); return displayUser(dbUser,model,request,response,locale);

	}
	
	/**
	 * From user profile
	 * @param model
	 * @param request
	 * @param response
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("hasRole('AUTH')")
	@RequestMapping(value="/admin/users/displayUser.html", method=RequestMethod.GET)
	public String displayUserEdit(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		
		
		String userName = request.getRemoteUser();
		User user = userService.getByUserName(userName);
		System.out.println("$#7612#"); return displayUser(user,model,request,response,locale);

	}
	
	private void populateUserObjects(User user, MerchantStore store, Model model, Locale locale) throws Exception {
		
		//get groups
		List<Group> groups = new ArrayList<Group>();
		List<Group> userGroups = groupService.listGroup(GroupType.ADMIN);
		for(Group group : userGroups) {
			System.out.println("$#7613#"); if(!group.getGroupName().equals(Constants.GROUP_SUPERADMIN)) {
				groups.add(group);
			}
		}
		
		
		List<MerchantStore> stores = new ArrayList<MerchantStore>();
		//stores.add(store);
		stores = merchantStoreService.list();
		
		
		//questions
		List<SecurityQuestion> questions = new ArrayList<SecurityQuestion>();
		
		SecurityQuestion question = new SecurityQuestion();
		System.out.println("$#7614#"); question.setId("1");
		System.out.println("$#7615#"); question.setLabel(messages.getMessage("security.question.1", locale));
		questions.add(question);
		
		question = new SecurityQuestion();
		System.out.println("$#7616#"); question.setId("2");
		System.out.println("$#7617#"); question.setLabel(messages.getMessage("security.question.2", locale));
		questions.add(question);
		
		question = new SecurityQuestion();
		System.out.println("$#7618#"); question.setId("3");
		System.out.println("$#7619#"); question.setLabel(messages.getMessage("security.question.3", locale));
		questions.add(question);
		
		question = new SecurityQuestion();
		System.out.println("$#7620#"); question.setId("4");
		System.out.println("$#7621#"); question.setLabel(messages.getMessage("security.question.4", locale));
		questions.add(question);
		
		question = new SecurityQuestion();
		System.out.println("$#7622#"); question.setId("5");
		System.out.println("$#7623#"); question.setLabel(messages.getMessage("security.question.5", locale));
		questions.add(question);
		
		question = new SecurityQuestion();
		System.out.println("$#7624#"); question.setId("6");
		System.out.println("$#7625#"); question.setLabel(messages.getMessage("security.question.6", locale));
		questions.add(question);
		
		question = new SecurityQuestion();
		System.out.println("$#7626#"); question.setId("7");
		System.out.println("$#7627#"); question.setLabel(messages.getMessage("security.question.7", locale));
		questions.add(question);
		
		question = new SecurityQuestion();
		System.out.println("$#7628#"); question.setId("8");
		System.out.println("$#7629#"); question.setLabel(messages.getMessage("security.question.8", locale));
		questions.add(question);
		
		question = new SecurityQuestion();
		System.out.println("$#7630#"); question.setId("9");
		System.out.println("$#7631#"); question.setLabel(messages.getMessage("security.question.9", locale));
		questions.add(question);
		
		model.addAttribute("questions", questions);
		model.addAttribute("stores", stores);
		model.addAttribute("languages", store.getLanguages());
		model.addAttribute("groups", groups);
		
		
	}
	
	
	
	private String displayUser(User user, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		

		//display menu
		System.out.println("$#7632#"); setMenu(model,request);
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);



		
		System.out.println("$#7633#"); if(user==null) {
			user = new User();
		} else {
			System.out.println("$#7634#"); user.setAdminPassword("TRANSIENT");
		}
		
		System.out.println("$#7635#"); this.populateUserObjects(user, store, model, locale);
		

		model.addAttribute("user", user);
		
		

		System.out.println("$#7636#"); return ControllerConstants.Tiles.User.profile;
	}
	
	@PreAuthorize("hasRole('AUTH')")
	@RequestMapping(value="/admin/users/checkUserCode.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> checkUserCode(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		String code = request.getParameter("code");
		String id = request.getParameter("id");

		AjaxResponse resp = new AjaxResponse();
		
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#7637#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		try {
			
			System.out.println("$#7638#"); if(StringUtils.isBlank(code)) {
				System.out.println("$#7639#"); resp.setStatus(AjaxResponse.CODE_ALREADY_EXIST);
				String returnString =  resp.toJSONString();
				System.out.println("$#7640#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			User user = userService.getByUserName(code);
		
		
			System.out.println("$#7641#"); if(!StringUtils.isBlank(id)&& user!=null) {
				try {
					Long lid = Long.parseLong(id);
					
					System.out.println("$#7643#"); if(user.getAdminName().equals(code) && user.getId()==lid) {
						System.out.println("$#7645#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_SUCCESS);
						String returnString =  resp.toJSONString();
						System.out.println("$#7646#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
					}
				} catch (Exception e) {
					System.out.println("$#7647#"); resp.setStatus(AjaxResponse.CODE_ALREADY_EXIST);
					String returnString =  resp.toJSONString();
					System.out.println("$#7648#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
				}
	
			}

			
			System.out.println("$#7649#"); if(StringUtils.isBlank(code)) {
				System.out.println("$#7650#"); resp.setStatus(AjaxResponse.CODE_ALREADY_EXIST);
				String returnString =  resp.toJSONString();
				System.out.println("$#7651#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}

			System.out.println("$#7652#"); if(user!=null) {
				System.out.println("$#7653#"); resp.setStatus(AjaxResponse.CODE_ALREADY_EXIST);
				String returnString =  resp.toJSONString();
				System.out.println("$#7654#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			

			System.out.println("$#7655#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);

		} catch (Exception e) {
			LOGGER.error("Error while getting user", e);
			System.out.println("$#7656#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#7657#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#7658#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);

	}
	
	@PreAuthorize("hasRole('AUTH')")
	@RequestMapping(value="/admin/users/save.html", method=RequestMethod.POST)
	public String saveUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model, HttpServletRequest request, Locale locale) throws Exception {


		System.out.println("$#7659#"); setMenu(model,request);
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);

		
		System.out.println("$#7660#"); this.populateUserObjects(user, store, model, locale);
		
		Language language = user.getDefaultLanguage();
		
		Language l = languageService.getById(language.getId());
		
		System.out.println("$#7661#"); user.setDefaultLanguage(l);
		
		Locale userLocale = LocaleUtils.getLocale(l);
		
		
		
		User dbUser = null;
		
		//edit mode, need to get original user important information
		System.out.println("$#7662#"); if(user.getId()!=null) {
			dbUser = userService.getByUserName(user.getAdminName());
			System.out.println("$#7663#"); if(dbUser==null) {
				System.out.println("$#7664#"); return "redirect:///admin/users/displayUser.html";
			}
		}

		List<Group> submitedGroups = user.getGroups();
		Set<Integer> ids = new HashSet<Integer>();
		for(Group group : submitedGroups) {
			ids.add(group.getId());
		}
		

		
		//validate security questions not empty
		System.out.println("$#7665#"); if(StringUtils.isBlank(user.getAnswer1())) {
			ObjectError error = new ObjectError("answer1",messages.getMessage("security.answer.question1.message", locale));
			System.out.println("$#7666#"); result.addError(error);
		}
		
		System.out.println("$#7667#"); if(StringUtils.isBlank(user.getAnswer2())) {
			ObjectError error = new ObjectError("answer2",messages.getMessage("security.answer.question2.message", locale));
			System.out.println("$#7668#"); result.addError(error);
		}
		
		System.out.println("$#7669#"); if(StringUtils.isBlank(user.getAnswer3())) {
			ObjectError error = new ObjectError("answer3",messages.getMessage("security.answer.question3.message", locale));
			System.out.println("$#7670#"); result.addError(error);
		}
		
		System.out.println("$#7671#"); if(user.getQuestion1().equals(user.getQuestion2()) || user.getQuestion1().equals(user.getQuestion3())
				|| user.getQuestion2().equals(user.getQuestion1()) || user.getQuestion1().equals(user.getQuestion3())
				|| user.getQuestion3().equals(user.getQuestion1()) || user.getQuestion1().equals(user.getQuestion2()))
		
		
		{
			ObjectError error = new ObjectError("question1",messages.getMessage("security.questions.differentmessages", locale));
			System.out.println("$#7677#"); result.addError(error);
		}
		
		
		Group superAdmin = null;
		
		System.out.println("$#7679#"); System.out.println("$#7678#"); if(user.getId()!=null && user.getId()>0) {
			System.out.println("$#7681#"); if(user.getId().longValue()!=dbUser.getId().longValue()) {
				System.out.println("$#7682#"); return "redirect:///admin/users/displayUser.html";
			}
			
			List<Group> groups = dbUser.getGroups();
			//boolean removeSuperAdmin = true;
			for(Group group : groups) {
				//can't revoke super admin
				System.out.println("$#7683#"); if(group.getGroupName().equals("SUPERADMIN")) {
					superAdmin = group;
				}
			}

		} else {
			
			System.out.println("$#7685#"); System.out.println("$#7684#"); if(user.getAdminPassword().length()<6) {
				ObjectError error = new ObjectError("adminPassword",messages.getMessage("message.password.length", locale));
				System.out.println("$#7686#"); result.addError(error);
			}
			
		}
		
		System.out.println("$#7687#"); if(superAdmin!=null) {
			ids.add(superAdmin.getId());
		}

		
		List<Group> newGroups = groupService.listGroupByIds(ids);

		//set actual user groups
		System.out.println("$#7688#"); user.setGroups(newGroups);
		
		System.out.println("$#7689#"); if (result.hasErrors()) {
			System.out.println("$#7690#"); return ControllerConstants.Tiles.User.profile;
		}
		
		String decodedPassword = user.getAdminPassword();
		System.out.println("$#7692#"); System.out.println("$#7691#"); if(user.getId()!=null && user.getId()>0) {
			System.out.println("$#7694#"); user.setAdminPassword(dbUser.getAdminPassword());
		} else {
			String encoded = passwordEncoder.encode(user.getAdminPassword());
			System.out.println("$#7695#"); user.setAdminPassword(encoded);
		}
		
		
		System.out.println("$#7696#"); if(user.getId()==null || user.getId().longValue()==0) {
			
			//save or update user
			System.out.println("$#7698#"); userService.saveOrUpdate(user);
			
			try {

				//creation of a user, send an email
				String userName = user.getFirstName();
				System.out.println("$#7699#"); if(StringUtils.isBlank(userName)) {
					userName = user.getAdminName();
				}
				String[] userNameArg = {userName};
				
				
				Map<String, String> templateTokens = emailUtils.createEmailObjectsMap(request.getContextPath(), store, messages, userLocale);
				templateTokens.put(EmailConstants.EMAIL_NEW_USER_TEXT, messages.getMessage("email.greeting", userNameArg, userLocale));
				templateTokens.put(EmailConstants.EMAIL_USER_FIRSTNAME, user.getFirstName());
				templateTokens.put(EmailConstants.EMAIL_USER_LASTNAME, user.getLastName());
				templateTokens.put(EmailConstants.EMAIL_ADMIN_USERNAME_LABEL, messages.getMessage("label.generic.username",userLocale));
				templateTokens.put(EmailConstants.EMAIL_ADMIN_NAME, user.getAdminName());
				templateTokens.put(EmailConstants.EMAIL_TEXT_NEW_USER_CREATED, messages.getMessage("email.newuser.text",userLocale));
				templateTokens.put(EmailConstants.EMAIL_ADMIN_PASSWORD_LABEL, messages.getMessage("label.generic.password",userLocale));
				templateTokens.put(EmailConstants.EMAIL_ADMIN_PASSWORD, decodedPassword);
				templateTokens.put(EmailConstants.EMAIL_ADMIN_URL_LABEL, messages.getMessage("label.adminurl",userLocale));
				templateTokens.put(EmailConstants.EMAIL_ADMIN_URL, filePathUtils.buildAdminUri(store, request));
	
				
				Email email = new Email();
				System.out.println("$#7700#"); email.setFrom(store.getStorename());
				System.out.println("$#7701#"); email.setFromEmail(store.getStoreEmailAddress());
				System.out.println("$#7702#"); email.setSubject(messages.getMessage("email.newuser.title",userLocale));
				System.out.println("$#7703#"); email.setTo(user.getAdminEmail());
				System.out.println("$#7704#"); email.setTemplateName(NEW_USER_TMPL);
				System.out.println("$#7705#"); email.setTemplateTokens(templateTokens);
	
	
				
				System.out.println("$#7706#"); emailService.sendHtmlEmail(store, email);
			
			} catch (Exception e) {
				LOGGER.error("Cannot send email to user",e);
			}
			
		} else {
			//save or update user
			System.out.println("$#7707#"); userService.saveOrUpdate(user);
		}

		model.addAttribute("success","success");
		System.out.println("$#7708#"); return ControllerConstants.Tiles.User.profile;
	}
	
	@PreAuthorize("hasRole('AUTH')")
	@RequestMapping(value="/admin/users/remove.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> removeUser(HttpServletRequest request, Locale locale) throws Exception {
		
		//do not remove super admin
		
		String sUserId = request.getParameter("userId");

		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#7709#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		String userName = request.getRemoteUser();
		User remoteUser = userService.getByUserName(userName);

		
		try {
			
			Long userId = Long.parseLong(sUserId);
			User user = userService.getById(userId);
			
			/**
			 * In order to remove a User the logged in ser must be ADMIN
			 * or SUPER_USER
			 */
			

			System.out.println("$#7710#"); if(user==null){
				System.out.println("$#7711#"); resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
				System.out.println("$#7712#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#7713#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#7714#"); if(!request.isUserInRole(Constants.GROUP_ADMIN)) {
				System.out.println("$#7715#"); resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
				System.out.println("$#7716#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#7717#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}

			
			//check if the user removed has group ADMIN
			boolean isAdmin = false;
			System.out.println("$#7718#"); if(UserUtils.userInGroup(remoteUser, Constants.GROUP_ADMIN) || UserUtils.userInGroup(remoteUser, Constants.GROUP_SUPERADMIN)) {
				isAdmin = true;
			}

			
			System.out.println("$#7720#"); if(!isAdmin) {
				System.out.println("$#7721#"); resp.setStatusMessage(messages.getMessage("message.security.caanotremovesuperadmin", locale));
				System.out.println("$#7722#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				System.out.println("$#7723#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
			
			System.out.println("$#7724#"); userService.delete(user);
			
			System.out.println("$#7725#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);

		
		
		} catch (Exception e) {
			LOGGER.error("Error while deleting user", e);
			System.out.println("$#7726#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#7727#"); resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		System.out.println("$#7728#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		
	}
	
	
	private void setMenu(Model model, HttpServletRequest request) throws Exception {
		
		//display menu
		Map<String,String> activeMenus = new HashMap<String,String>();
		activeMenus.put("profile", "profile");
		activeMenus.put("user", "create-user");
		
		@SuppressWarnings("unchecked")
		Map<String, Menu> menus = (Map<String, Menu>)request.getAttribute("MENUMAP");
		
		Menu currentMenu = (Menu)menus.get("profile");
		model.addAttribute("currentMenu",currentMenu);
		model.addAttribute("activeMenus",activeMenus);
		//
		
	}
	
	//password reset functionality  ---  Sajid Shajahan  
	@RequestMapping(value="/admin/users/resetPassword.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> resetPassword(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		
		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#7729#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
	    
		String userName = request.getParameter("username");
		
		
		
		/**
		 * Get User with userService.getByUserName
		 * Get 3 security questions from User.getQuestion1, user.getQuestion2, user.getQuestion3
		 */
		
		HttpSession session = request.getSession();
		System.out.println("$#7730#"); session.setAttribute("username_reset", userName);
		
		try {
				System.out.println("$#7731#"); if(!StringUtils.isBlank(userName)){
					
						User dbUser = userService.getByUserName(userName);
						
						System.out.println("$#7732#"); if(dbUser==null) {
							System.out.println("$#7733#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
							System.out.println("$#7734#"); resp.setStatusMessage(messages.getMessage("message.username.notfound", locale));
							String returnString = resp.toJSONString();
							System.out.println("$#7735#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
						}
					
						Map<String,String> entry = new HashMap<String,String>();
						entry.put(QUESTION_1, dbUser.getQuestion1());
						entry.put(QUESTION_2, dbUser.getQuestion2());
						entry.put(QUESTION_3, dbUser.getQuestion3());
						System.out.println("$#7736#"); resp.addDataEntry(entry);
						System.out.println("$#7737#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);
				
				}else
				{
						System.out.println("$#7738#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
						System.out.println("$#7739#"); resp.setStatusMessage(messages.getMessage("User.resetPassword.Error", locale));
				
				}
			} catch (Exception e) {
						System.out.println("$#7740#"); e.printStackTrace();
						System.out.println("$#7741#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
						System.out.println("$#7742#"); resp.setStatusMessage(messages.getMessage("User.resetPassword.Error", locale));
						String returnString = resp.toJSONString();
						System.out.println("$#7743#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
			}
	
		
		
		
		String returnString = resp.toJSONString();
		System.out.println("$#7744#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	//password reset functionality  ---  Sajid Shajahan
	@RequestMapping(value="/admin/users/resetPasswordSecurityQtn.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> resetPasswordSecurityQtn(@ModelAttribute(value="userReset") UserReset userReset,HttpServletRequest request, HttpServletResponse response, Locale locale) {
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		Language userLanguage = null; 
		Locale userLocale =  null; 
		AjaxResponse resp = new AjaxResponse();


		String answer1 = request.getParameter("answer1");
		String answer2 = request.getParameter("answer2");
		String answer3 = request.getParameter("answer3");
		
		try {
			
			HttpSession session = request.getSession();
			User dbUser = userService.getByUserName((String) session.getAttribute("username_reset"));
			
			System.out.println("$#7745#"); if(dbUser!= null){
				
				System.out.println("$#7746#"); if(dbUser.getAnswer1().equals(answer1.trim()) && dbUser.getAnswer2().equals(answer2.trim()) && dbUser.getAnswer3().equals(answer3.trim())){
					userLanguage = dbUser.getDefaultLanguage();	
					userLocale =  LocaleUtils.getLocale(userLanguage);
					
					String tempPass = userReset.generateRandomString();
					String pass = passwordEncoder.encode(tempPass);
					
					System.out.println("$#7749#"); dbUser.setAdminPassword(pass);
					System.out.println("$#7750#"); userService.update(dbUser);
					
					//send email
					
					try {
						String[] storeEmail = {store.getStoreEmailAddress()};						
						
						Map<String, String> templateTokens = emailUtils.createEmailObjectsMap(request.getContextPath(), store, messages, userLocale);
						templateTokens.put(EmailConstants.EMAIL_RESET_PASSWORD_TXT, messages.getMessage("email.user.resetpassword.text", userLocale));
						templateTokens.put(EmailConstants.EMAIL_CONTACT_OWNER, messages.getMessage("email.contactowner", storeEmail, userLocale));
						templateTokens.put(EmailConstants.EMAIL_PASSWORD_LABEL, messages.getMessage("label.generic.password",userLocale));
						templateTokens.put(EmailConstants.EMAIL_USER_PASSWORD, tempPass);

						Email email = new Email();
						System.out.println("$#7751#"); email.setFrom(store.getStorename());
						System.out.println("$#7752#"); email.setFromEmail(store.getStoreEmailAddress());
						System.out.println("$#7753#"); email.setSubject(messages.getMessage("label.generic.changepassword",userLocale));
						System.out.println("$#7754#"); email.setTo(dbUser.getAdminEmail() );
						System.out.println("$#7755#"); email.setTemplateName(RESET_PASSWORD_TPL);
						System.out.println("$#7756#"); email.setTemplateTokens(templateTokens);
						
						System.out.println("$#7757#"); emailService.sendHtmlEmail(store, email);
					
					} catch (Exception e) {
						LOGGER.error("Cannot send email to user",e);
					}
					
					System.out.println("$#7758#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);
					System.out.println("$#7759#"); resp.setStatusMessage(messages.getMessage("User.resetPassword.resetSuccess", locale));
				}
				else{
							System.out.println("$#7760#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
							System.out.println("$#7761#"); resp.setStatusMessage(messages.getMessage("User.resetPassword.wrongSecurityQtn", locale));
					  
				  }
			  }else{
						System.out.println("$#7762#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
						System.out.println("$#7763#"); resp.setStatusMessage(messages.getMessage("User.resetPassword.userNotFound", locale));
				  
			  }
			
		} catch (ServiceException e) {
			System.out.println("$#7764#"); e.printStackTrace();
			System.out.println("$#7765#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			System.out.println("$#7766#"); resp.setStatusMessage(messages.getMessage("User.resetPassword.Error", locale));
		}
		
		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders= new HttpHeaders();
					System.out.println("$#7767#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		System.out.println("$#7768#"); return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	
	}
