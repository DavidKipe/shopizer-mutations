package com.salesmanager.shop.application.config;

import static org.springframework.http.MediaType.IMAGE_GIF;
import static org.springframework.http.MediaType.IMAGE_JPEG;
import static org.springframework.http.MediaType.IMAGE_PNG;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesView;
import org.springframework.web.servlet.view.tiles3.TilesViewResolver;

import com.salesmanager.core.business.configuration.CoreApplicationConfiguration;
import com.salesmanager.shop.filter.AdminFilter;
import com.salesmanager.shop.filter.CorsFilter;
import com.salesmanager.shop.filter.StoreFilter;
import com.salesmanager.shop.utils.LabelUtils;

@Configuration
@ComponentScan({"com.salesmanager.shop"})
@ServletComponentScan
@Import({CoreApplicationConfiguration.class}) // import sm-core configurations
@EnableWebSecurity
public class ShopApplicationConfiguration implements WebMvcConfigurer {

  protected final Log logger = LogFactory.getLog(getClass());

  @EventListener(ApplicationReadyEvent.class)
  public void applicationReadyCode() {
    String workingDir = System.getProperty("user.dir");
    logger.info("Current working directory : " + workingDir);
  }

  /** Configure TilesConfigurer. */
  @Bean
  public TilesConfigurer tilesConfigurer() {
    TilesConfigurer tilesConfigurer = new TilesConfigurer();
				System.out.println("$#7920#"); tilesConfigurer.setDefinitions(
        "/WEB-INF/tiles/tiles-admin.xml",
        "/WEB-INF/tiles/tiles-shop.xml");
				System.out.println("$#7921#"); tilesConfigurer.setCheckRefresh(true);
				System.out.println("$#7922#"); return tilesConfigurer;
  }

  /** Configure ViewResolvers to deliver preferred views. */
  @Bean
  public TilesViewResolver tilesViewResolver() {
    final TilesViewResolver resolver = new TilesViewResolver();
				System.out.println("$#7923#"); resolver.setViewClass(TilesView.class);
				System.out.println("$#7924#"); resolver.setOrder(0);
				System.out.println("$#7925#"); return resolver;
  }
  



  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(new MappingJackson2HttpMessageConverter());
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
				System.out.println("$#7926#"); registry.addViewController("/").setViewName("shop");
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // Changes the locale when a 'locale' request parameter is sent; e.g. /?locale=de
    registry.addInterceptor(localeChangeInterceptor());

    registry
        .addInterceptor(storeFilter())
        // store web front filter
        .addPathPatterns("/shop/**")
        // customer section filter
        .addPathPatterns("/customer/**");

    registry
        .addInterceptor(corsFilter())
        // public services cors filter
        .addPathPatterns("/services/**")
        // REST api
        .addPathPatterns("/api/**");

    // admin panel filter
    registry.addInterceptor(adminFilter()).addPathPatterns("/admin/**");
  }

  @Override
  public void configureViewResolvers(ViewResolverRegistry registry) {
    InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
				System.out.println("$#7927#"); internalResourceViewResolver.setPrefix("/WEB-INF/views/");
				System.out.println("$#7928#"); internalResourceViewResolver.setSuffix(".jsp");
				System.out.println("$#7929#"); registry.viewResolver(internalResourceViewResolver);
  }

  @Bean
  public ByteArrayHttpMessageConverter byteArrayHttpMessageConverter() {
    List<MediaType> supportedMediaTypes = Arrays.asList(IMAGE_JPEG, IMAGE_GIF, IMAGE_PNG);

    ByteArrayHttpMessageConverter byteArrayHttpMessageConverter =
        new ByteArrayHttpMessageConverter();
				System.out.println("$#7930#"); byteArrayHttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
				System.out.println("$#7931#"); return byteArrayHttpMessageConverter;
  }

  @Bean
  public LocaleChangeInterceptor localeChangeInterceptor() {
				System.out.println("$#7932#"); return new LocaleChangeInterceptor();
  }

  @Bean
  public StoreFilter storeFilter() {
				System.out.println("$#7933#"); return new StoreFilter();
  }

  @Bean
  public CorsFilter corsFilter() {
				System.out.println("$#7934#"); return new CorsFilter();
  }

  @Bean
  public AdminFilter adminFilter() {
				System.out.println("$#7935#"); return new AdminFilter();
  }

  @Bean
  public SessionLocaleResolver localeResolver() {
    SessionLocaleResolver slr = new SessionLocaleResolver();
				System.out.println("$#7936#"); slr.setDefaultLocale(Locale.ENGLISH);
				System.out.println("$#7937#"); return slr;
  }

  @Bean
  public ReloadableResourceBundleMessageSource messageSource() {
    ReloadableResourceBundleMessageSource messageSource =
        new ReloadableResourceBundleMessageSource();
				System.out.println("$#7938#"); messageSource.setBasenames(
        "classpath:bundles/shopizer",
        "classpath:bundles/messages",
        "classpath:bundles/shipping",
        "classpath:bundles/payment");

				System.out.println("$#7939#"); messageSource.setDefaultEncoding("UTF-8");
				System.out.println("$#7940#"); return messageSource;
  }

  @Bean
  public LabelUtils messages() {
				System.out.println("$#7941#"); return new LabelUtils();
  }

}
