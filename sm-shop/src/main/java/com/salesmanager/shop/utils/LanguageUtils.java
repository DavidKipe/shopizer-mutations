package com.salesmanager.shop.utils;

import java.util.Locale;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;

@Component
public class LanguageUtils {

  protected final Log logger = LogFactory.getLog(getClass());

  private static final String ALL_LANGUALES = "_all";

  @Inject
  LanguageService languageService;

  public Language getServiceLanguage(String lang) {
    Language l = null;
				System.out.println("$#15746#"); if (!StringUtils.isBlank(lang)) {
      try {
        l = languageService.getByCode(lang);
      } catch (ServiceException e) {
        logger.error("Cannot retrieve language " + lang, e);
      }
    }

				System.out.println("$#15747#"); if (l == null) {
      l = languageService.defaultLanguage();
    }

				System.out.println("$#15748#"); return l;
  }

  /**
   * Determines request language based on store rules
   * 
   * @param request
   * @return
   */
  public Language getRequestLanguage(HttpServletRequest request, HttpServletResponse response) {

    Locale locale = null;

    Language language = (Language) request.getSession().getAttribute(Constants.LANGUAGE);
    MerchantStore store =
        (MerchantStore) request.getSession().getAttribute(Constants.MERCHANT_STORE);
    


				System.out.println("$#15749#"); if (language == null) {
      try {

        locale = LocaleContextHolder.getLocale();// should be browser locale



								System.out.println("$#15750#"); if (store != null) {
          language = store.getDefaultLanguage();
										System.out.println("$#15751#"); if (language != null) {
            locale = languageService.toLocale(language, store);
												System.out.println("$#15752#"); if (locale != null) {
														System.out.println("$#15753#"); LocaleContextHolder.setLocale(locale);
            }
												System.out.println("$#15754#"); request.getSession().setAttribute(Constants.LANGUAGE, language);
          }

										System.out.println("$#15755#"); if (language == null) {
            language = languageService.toLanguage(locale);
												System.out.println("$#15756#"); request.getSession().setAttribute(Constants.LANGUAGE, language);
          }

        }

      } catch (Exception e) {
								System.out.println("$#15757#"); if (language == null) {
          try {
            language = languageService.getByCode(Constants.DEFAULT_LANGUAGE);
          } catch (Exception ignore) {
          }
        }
      }
    } else {


      Locale localeFromContext = LocaleContextHolder.getLocale();// should be browser locale
						System.out.println("$#15758#"); if (!language.getCode().equals(localeFromContext.getLanguage())) {
        // get locale context
        language = languageService.toLanguage(localeFromContext);
      }

    }

				System.out.println("$#15759#"); if (language != null) {
      locale = languageService.toLocale(language, store);
    } else {
      language = languageService.toLanguage(locale);
    }

    LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
				System.out.println("$#15760#"); if (localeResolver != null) {
						System.out.println("$#15761#"); localeResolver.setLocale(request, response, locale);
    }
				System.out.println("$#15762#"); response.setLocale(locale);
				System.out.println("$#15763#"); request.getSession().setAttribute(Constants.LANGUAGE, language);

				System.out.println("$#15764#"); return language;
  }

  /**
   * Should be used by rest web services
   * 
   * @param request
   * @param store
   * @return
   * @throws Exception
   */
  public Language getRESTLanguage(HttpServletRequest request) {

    Validate.notNull(request, "HttpServletRequest must not be null");

    try {
      Language language = null;

      String lang = request.getParameter(Constants.LANG);

						System.out.println("$#15765#"); if (StringUtils.isBlank(lang)) {
								System.out.println("$#15766#"); if (language == null) {
          language = languageService.defaultLanguage();
        }
      } else {
								System.out.println("$#15767#"); if(!ALL_LANGUALES.equals(lang)) {
          language = languageService.getByCode(lang);
										System.out.println("$#15768#"); if (language == null) {
            language = languageService.defaultLanguage();
          }
        }
      }

						System.out.println("$#15769#"); return language;

    } catch (ServiceException e) {
      throw new ServiceRuntimeException(e);
    }
  }

}
