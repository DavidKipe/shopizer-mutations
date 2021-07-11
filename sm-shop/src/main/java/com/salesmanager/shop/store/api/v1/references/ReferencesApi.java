package com.salesmanager.shop.store.api.v1.references;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.currency.Currency;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.references.MeasureUnit;
import com.salesmanager.shop.model.references.ReadableCountry;
import com.salesmanager.shop.model.references.ReadableZone;
import com.salesmanager.shop.model.references.SizeReferences;
import com.salesmanager.shop.model.references.WeightUnit;
import com.salesmanager.shop.store.controller.country.facade.CountryFacade;
import com.salesmanager.shop.store.controller.currency.facade.CurrencyFacade;
import com.salesmanager.shop.store.controller.language.facade.LanguageFacade;
import com.salesmanager.shop.store.controller.store.facade.StoreFacade;
import com.salesmanager.shop.store.controller.zone.facade.ZoneFacade;
import com.salesmanager.shop.utils.LanguageUtils;

/**
 * Get system Language, Country and Currency objects
 *
 * @author c.samson
 */
@RestController
@RequestMapping("/api/v1")
public class ReferencesApi {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReferencesApi.class);


  @Inject private StoreFacade storeFacade;

  @Inject private LanguageUtils languageUtils;

  @Inject private LanguageFacade languageFacade;

  @Inject private CountryFacade countryFacade;

  @Inject private ZoneFacade zoneFacade;

  @Inject private CurrencyFacade currencyFacade;

  /**
   * Search languages by language code private/languages returns everything
   *
   * @return
   */
  @GetMapping("/languages")
  public List<Language> getLanguages() {
				System.out.println("$#12021#"); return languageFacade.getLanguages();
  }

  /**
   * Returns a country with zones (provinces, states) supports language set in parameter
   * ?lang=en|fr|ru...
   *
   * @param request
   * @return
   */
  @GetMapping("/country")
  public List<ReadableCountry> getCountry(HttpServletRequest request) {
    MerchantStore merchantStore = storeFacade.getByCode(request);
    Language lang = languageUtils.getRESTLanguage(request);
				System.out.println("$#12022#"); return countryFacade.getListCountryZones(lang, merchantStore);
  }

  @GetMapping("/zones")
  public List<ReadableZone> getZones(
      @RequestParam("code") String code, HttpServletRequest request) {
    MerchantStore merchantStore = storeFacade.getByCode(request);
    Language lang = languageUtils.getRESTLanguage(request);
				System.out.println("$#12023#"); return zoneFacade.getZones(code, lang, merchantStore);
  }

  /**
   * Currency
   *
   * @return
   */
  @GetMapping("/currency")
  public List<Currency> getCurrency() {
				System.out.println("$#12024#"); return currencyFacade.getList();
  }

  @GetMapping("/measures")
  public SizeReferences measures() {
    SizeReferences sizeReferences = new SizeReferences();
				System.out.println("$#12025#"); sizeReferences.setMeasures(Arrays.asList(MeasureUnit.values()));
				System.out.println("$#12026#"); sizeReferences.setWeights(Arrays.asList(WeightUnit.values()));
				System.out.println("$#12027#"); return sizeReferences;
  }
}
