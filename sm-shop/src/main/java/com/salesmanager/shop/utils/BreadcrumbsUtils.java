package com.salesmanager.shop.utils;

import com.salesmanager.core.business.services.catalog.category.CategoryService;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.catalog.category.ReadableCategory;
import com.salesmanager.shop.model.catalog.product.ReadableProduct;
import com.salesmanager.shop.model.shop.Breadcrumb;
import com.salesmanager.shop.model.shop.BreadcrumbItem;
import com.salesmanager.shop.model.shop.BreadcrumbItemType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


@Component
public class BreadcrumbsUtils {
	
	@Inject
	private LabelUtils messages;
	
	@Inject
	private CategoryService categoryService;
	
	@Inject
	private FilePathUtils filePathUtils;
	
	
	public Breadcrumb buildCategoryBreadcrumb(ReadableCategory categoryClicked, MerchantStore store, Language language, String contextPath) throws Exception {
		
		/** Rebuild breadcrumb **/
		BreadcrumbItem home = new BreadcrumbItem();
		System.out.println("$#15546#"); home.setItemType(BreadcrumbItemType.HOME);
		System.out.println("$#15547#"); home.setLabel(messages.getMessage(Constants.HOME_MENU_KEY, LocaleUtils.getLocale(language)));
		System.out.println("$#15548#"); home.setUrl(filePathUtils.buildStoreUri(store, contextPath) + Constants.SHOP_URI);

		Breadcrumb breadCrumb = new Breadcrumb();
		System.out.println("$#15549#"); breadCrumb.setLanguage(language);
		
		List<BreadcrumbItem> items = new ArrayList<BreadcrumbItem>();
		items.add(home);
		

			List<String> categoryIds = parseCategoryLineage(categoryClicked.getLineage());
			List<Long> ids = new ArrayList<Long>();
			for(String c : categoryIds) {
				ids.add(Long.parseLong(c));
			}
			
			ids.add(categoryClicked.getId());
			
			
			List<Category> categories = categoryService.listByIds(store, ids, language);
			
			//category path - use lineage
			for(Category c : categories) {
				BreadcrumbItem categoryBreadcrump = new BreadcrumbItem();
				System.out.println("$#15550#"); categoryBreadcrump.setItemType(BreadcrumbItemType.CATEGORY);
				System.out.println("$#15551#"); categoryBreadcrump.setLabel(c.getDescription().getName());
				System.out.println("$#15552#"); categoryBreadcrump.setUrl(filePathUtils.buildCategoryUrl(store, contextPath, c.getDescription().getSeUrl()));
				items.add(categoryBreadcrump);
			}
			
			System.out.println("$#15553#"); breadCrumb.setUrlRefContent(buildBreadCrumb(ids));

		System.out.println("$#15554#"); breadCrumb.setBreadCrumbs(items);
		System.out.println("$#15555#"); breadCrumb.setItemType(BreadcrumbItemType.CATEGORY);
		
		
		System.out.println("$#15556#"); return breadCrumb;
	}
	
	
	public Breadcrumb buildProductBreadcrumb(String refContent, ReadableProduct productClicked, MerchantStore store, Language language, String contextPath) throws Exception {
		
		/** Rebuild breadcrumb **/
		BreadcrumbItem home = new BreadcrumbItem();
		System.out.println("$#15557#"); home.setItemType(BreadcrumbItemType.HOME);
		System.out.println("$#15558#"); home.setLabel(messages.getMessage(Constants.HOME_MENU_KEY, LocaleUtils.getLocale(language)));
		System.out.println("$#15559#"); home.setUrl(filePathUtils.buildStoreUri(store, contextPath) + Constants.SHOP_URI);

		Breadcrumb breadCrumb = new Breadcrumb();
		System.out.println("$#15560#"); breadCrumb.setLanguage(language);
		
		List<BreadcrumbItem> items = new ArrayList<BreadcrumbItem>();
		items.add(home);
		
		System.out.println("$#15561#"); if(!StringUtils.isBlank(refContent)) {

			List<String> categoryIds = parseBreadCrumb(refContent);
			List<Long> ids = new ArrayList<Long>();
			for(String c : categoryIds) {
				ids.add(Long.parseLong(c));
			}
			
			
			List<Category> categories = categoryService.listByIds(store, ids, language);
			
			//category path - use lineage
			for(Category c : categories) {
				BreadcrumbItem categoryBreadcrump = new BreadcrumbItem();
				System.out.println("$#15562#"); categoryBreadcrump.setItemType(BreadcrumbItemType.CATEGORY);
				System.out.println("$#15563#"); categoryBreadcrump.setLabel(c.getDescription().getName());
				System.out.println("$#15564#"); categoryBreadcrump.setUrl(filePathUtils.buildCategoryUrl(store, contextPath, c.getDescription().getSeUrl()));
				items.add(categoryBreadcrump);
			}
			

			System.out.println("$#15565#"); breadCrumb.setUrlRefContent(buildBreadCrumb(ids));
		} 
		
		BreadcrumbItem productBreadcrump = new BreadcrumbItem();
		System.out.println("$#15566#"); productBreadcrump.setItemType(BreadcrumbItemType.PRODUCT);
		System.out.println("$#15567#"); productBreadcrump.setLabel(productClicked.getDescription().getName());
		System.out.println("$#15568#"); productBreadcrump.setUrl(filePathUtils.buildProductUrl(store, contextPath, productClicked.getDescription().getFriendlyUrl()));
		items.add(productBreadcrump);
		
		
		


		System.out.println("$#15569#"); breadCrumb.setBreadCrumbs(items);
		System.out.println("$#15570#"); breadCrumb.setItemType(BreadcrumbItemType.CATEGORY);
		
		
		System.out.println("$#15571#"); return breadCrumb;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	private List<String> parseBreadCrumb(String refContent) throws Exception {
		
		/** c:1,2,3 **/
		String[] categoryComa = refContent.split(":");
		String[] categoryIds = categoryComa[1].split(",");
		String last = Arrays.asList(categoryIds).stream().reduce((first, second) -> second).get();
		System.out.println("$#15573#"); return Arrays.asList(last);
		
		
	}
	

	private List<String> parseCategoryLineage(String lineage) throws Exception {
		
		String[] categoryPath = lineage.split(Constants.CATEGORY_LINEAGE_DELIMITER);
		List<String> returnList = new LinkedList<String>();
		for(String c : categoryPath) {
			System.out.println("$#15574#"); if(!StringUtils.isBlank(c)) {
				returnList.add(c);
			}
		}
		System.out.println("$#15575#"); return returnList;

	}
	
	private String buildBreadCrumb(List<Long> ids) throws Exception {
		
		System.out.println("$#15576#"); if(CollectionUtils.isEmpty(ids)) {
			System.out.println("$#15577#"); return null;
		}
			StringBuilder sb = new StringBuilder();
			sb.append("c:");
			int count = 1;
			for(Long c : ids) {
				sb.append(c);
				System.out.println("$#15579#"); System.out.println("$#15578#"); if(count < ids.size()) {
					sb.append(",");
				}
				System.out.println("$#15580#"); count++;
			}
		
		
		System.out.println("$#15581#"); return sb.toString();
		
	}

}
