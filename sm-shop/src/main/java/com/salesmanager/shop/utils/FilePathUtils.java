package com.salesmanager.shop.utils;

import static com.salesmanager.shop.constants.ApplicationConstants.SHOP_SCHEME;
import static com.salesmanager.shop.constants.Constants.ADMIN_URI;
import static com.salesmanager.shop.constants.Constants.BLANK;
import static com.salesmanager.shop.constants.Constants.CATEGORY_URI;
import static com.salesmanager.shop.constants.Constants.DEFAULT_DOMAIN_NAME;
import static com.salesmanager.shop.constants.Constants.FILES_URI;
import static com.salesmanager.shop.constants.Constants.HTTP_SCHEME;
import static com.salesmanager.shop.constants.Constants.ORDER_DOWNLOAD_URI;
import static com.salesmanager.shop.constants.Constants.SHOP_URI;
import static com.salesmanager.shop.constants.Constants.SLASH;
import static com.salesmanager.shop.constants.Constants.STATIC_URI;
import static com.salesmanager.shop.constants.Constants.URL_EXTENSION;

import java.util.Properties;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.salesmanager.core.business.utils.CoreConfiguration;
import com.salesmanager.core.model.catalog.product.file.DigitalProduct;
import com.salesmanager.core.model.content.FileContentType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.order.ReadableOrderProductDownload;

@Component
public class FilePathUtils {

	private static final String DOWNLOADS = "/downloads/";
	private static final String DOUBLE_SLASH = "://";
	private static final String CONTEXT_PATH = "CONTEXT_PATH";
	private static final String HTTP_VALUE = "http";

	@Inject private CoreConfiguration coreConfiguration;

	@Inject
	@Qualifier("img")
	private ImageFilePath imageUtils;

	@Resource(name = "shopizer-properties")
	public Properties properties = new Properties();

	/**
	 * Builds a static content content file path that can be used by image servlet utility for getting
	 * the physical image
	 * Example: /files/<storeCode>/
	 */
	public String buildStaticFilePath(String storeCode, String fileName) {
		String path = FILES_URI + SLASH + storeCode + SLASH;
		System.out.println("$#15711#"); if (StringUtils.isNotBlank(fileName)) {
			System.out.println("$#15712#"); return path + fileName;
		}
		System.out.println("$#15713#"); return path;
	}

	public String buildStaticFilePath(MerchantStore store) {
		System.out.println("$#15714#"); return STATIC_URI + FILES_URI + SLASH + store.getCode() + SLASH;
	}

	/**
	 * Example: /admin/files/downloads/<storeCode>/<product>
	 */
	public String buildAdminDownloadProductFilePath(
			MerchantStore store, DigitalProduct digitalProduct) {
		System.out.println("$#15715#"); return ADMIN_URI
				+ FILES_URI
				+ DOWNLOADS
				+ store.getCode()
				+ SLASH
				+ digitalProduct.getProductFileName();
	}

	/**
	 * Example: /shop/order/download/<orderId>.html
	 */
	public String buildOrderDownloadProductFilePath(
			MerchantStore store, ReadableOrderProductDownload digitalProduct, Long orderId) {
		System.out.println("$#15716#"); return SHOP_URI
				+ ORDER_DOWNLOAD_URI
				+ SLASH
				+ orderId
				+ SLASH
				+ digitalProduct.getId()
				+ URL_EXTENSION;
	}

	/**
	 * Example: /<baseImagePath>/files/<storeCode>/STATIC_FILE/<fileName>
	 * Or example: /<shopScheme>://<domainName>/<contextPath>/files/<storeCode>/
	 */
	public String buildStaticFileAbsolutePath(MerchantStore store, String fileName) {
		System.out.println("$#15717#"); if (StringUtils.isNotBlank(imageUtils.getBasePath())
				&& imageUtils.getBasePath().startsWith(HTTP_SCHEME)) {
			System.out.println("$#15719#"); return imageUtils.getBasePath()
					+ FILES_URI
					+ SLASH
					+ store.getCode()
					+ SLASH
					+ FileContentType.STATIC_FILE
					+ SLASH
					+ fileName;
		} else {
			String scheme = coreConfiguration.getProperty("SHOP_SCHEME", HTTP_SCHEME);
			System.out.println("$#15720#"); return scheme
					+ DOUBLE_SLASH
					+ store.getDomainName()
					+ coreConfiguration.getProperty("CONTEXT_PATH")
					+ buildStaticFilePath(store.getCode(), fileName);
		}
	}

	/**
	 * Example: http[s]://<scheme>/<domainName>/<contextPath>
	 */
	public String buildStoreUri(MerchantStore store, HttpServletRequest request) {
		System.out.println("$#15721#"); return buildBaseUrl(request, store);
	}
	
	
	/**
	 *\/<contextPath>
	 */
	public String buildStoreUri(MerchantStore store, String contextPath) {
		System.out.println("$#15722#"); return normalizePath(contextPath);
	}

	public String buildRelativeStoreUri(HttpServletRequest request, MerchantStore store) {
		System.out.println("$#15723#"); return "" + normalizePath(request.getContextPath());
	}

	/**
	 * Access to the customer section
	 */
	public String buildCustomerUri(MerchantStore store, String contextPath) {
		System.out.println("$#15724#"); return buildStoreUri(store, contextPath);
	}

	public String buildAdminUri(MerchantStore store, HttpServletRequest request) {
		String baseUrl = buildBaseUrl(request, store);
		System.out.println("$#15725#"); return baseUrl + ADMIN_URI;
	}

	public String buildCategoryUrl(MerchantStore store, String contextPath, String url) {
		System.out.println("$#15726#"); return buildStoreUri(store, contextPath)
				+ SHOP_URI
				+ CATEGORY_URI
				+ SLASH
				+ url
				+ URL_EXTENSION;
	}

	public String buildProductUrl(MerchantStore store, String contextPath, String url) {
		System.out.println("$#15727#"); return buildStoreUri(store, contextPath)
				+ SHOP_URI
				+ Constants.PRODUCT_URI
				+ SLASH
				+ url
				+ URL_EXTENSION;
	}

	public String getContextPath() {
		System.out.println("$#15728#"); return properties.getProperty(CONTEXT_PATH);
	}

	private String normalizePath(String path) {
		System.out.println("$#15729#"); if (SLASH.equals(path)) {
			return BLANK;
		} else {
			System.out.println("$#15730#"); return path;
		}
	}

	private String getDomainName(String domainName) {
		System.out.println("$#15731#"); if (StringUtils.isBlank(domainName)) {
			System.out.println("$#15732#"); return DEFAULT_DOMAIN_NAME;
		} else {
			System.out.println("$#15733#"); return domainName;
		}
	}

	private String buildBaseUrl(HttpServletRequest request, MerchantStore store) {
		String contextPath = normalizePath(request.getContextPath());
		String scheme = coreConfiguration.getProperty(SHOP_SCHEME, HTTP_VALUE);
		String domainName = getDomainName(store.getDomainName());
		System.out.println("$#15734#"); return scheme
				+ DOUBLE_SLASH
				+ domainName
				+ contextPath;
	}
}
