package com.salesmanager.shop.admin.controller.categories;

import java.util.*;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.salesmanager.core.business.services.catalog.category.CategoryService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.catalog.category.CategoryDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.admin.model.web.Menu;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.utils.CategoryUtils;
import com.salesmanager.shop.utils.LabelUtils;



@Controller
public class CategoryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryController.class);

    @Inject
    LanguageService languageService;

    @Inject
    CategoryService categoryService;

    @Inject
    CountryService countryService;

    @Inject
    LabelUtils messages;

    @PreAuthorize("hasRole('PRODUCTS')")
    @RequestMapping(value="/admin/categories/editCategory.html", method=RequestMethod.GET)
    public String displayCategoryEdit(@RequestParam("id") long categoryId, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
								System.out.println("$#4918#"); return displayCategory(categoryId,model,request);

    }

    @PreAuthorize("hasRole('PRODUCTS')")
    @RequestMapping(value="/admin/categories/createCategory.html", method=RequestMethod.GET)
    public String displayCategoryCreate(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
								System.out.println("$#4919#"); return displayCategory(null,model,request);

    }
    private String displayCategory(Long categoryId, Model model, HttpServletRequest request) throws Exception {
        //display menu
								System.out.println("$#4920#"); setMenu(model,request);
        MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
        Language language = (Language)request.getAttribute("LANGUAGE");
        //get parent categories
        List<Category> categories = categoryService.listByStore(store,language);
        List<Language> languages = store.getLanguages();
        Optional<Category> category;
								System.out.println("$#4921#"); if(categoryId!=null && categoryId!=0) {//edit mode
            category =  Optional.ofNullable(categoryService.getById(categoryId, store.getId()));
												System.out.println("$#4923#"); if(!category.isPresent() || category.get().getMerchantStore().getId().intValue()!=store.getId().intValue()) {
																System.out.println("$#4925#"); return "catalogue-categories";
            }
        } else {
            category = Optional.of(new Category());
												System.out.println("$#4926#"); category.get().setVisible(true);
        }
        com.salesmanager.shop.admin.model.catalog.Category adminCategory = new com.salesmanager.shop.admin.model.catalog.Category();
        List<CategoryDescription> descriptions = new LinkedList<>();
        List<com.salesmanager.shop.admin.model.catalog.Category> readableCategories = CategoryUtils.readableCategoryListConverter(categories, language);

        for(Language l : languages) {
            CategoryDescription description = null;
            for(CategoryDescription desc : category.get().getDescriptions()) {
																System.out.println("$#4927#"); if(desc.getLanguage().getCode().equals(l.getCode())) {
                    description = desc;
                }
            }
												System.out.println("$#4928#"); if(description==null) {
                description = new CategoryDescription();
																System.out.println("$#4929#"); description.setLanguage(l);
            }
            descriptions.add(description);
        }

								System.out.println("$#4930#"); adminCategory.setDescriptions(descriptions);
								System.out.println("$#4931#"); adminCategory.setCategory(category.get());

        model.addAttribute("category", adminCategory);
        model.addAttribute("categories", readableCategories);

								System.out.println("$#4932#"); return "catalogue-categories-category";
    }


    @PreAuthorize("hasRole('PRODUCTS')")
    @RequestMapping(value="/admin/categories/save.html", method=RequestMethod.POST)
    public String saveCategory(@Valid @ModelAttribute("category") com.salesmanager.shop.admin.model.catalog.Category category, BindingResult result, Model model, HttpServletRequest request) throws Exception {

        model.addAttribute("category",category);
        Language language = (Language)request.getAttribute("LANGUAGE");
        //display menu
								System.out.println("$#4933#"); setMenu(model,request);
        MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
								System.out.println("$#4935#"); System.out.println("$#4934#"); if(category.getCategory().getId() != null && category.getCategory().getId() >0) { //edit entry
            //get from DB
            Optional<Category> currentCategory = Optional.ofNullable(categoryService.getById(category.getCategory().getId(), store.getId()));

												System.out.println("$#4937#"); if(!currentCategory.isPresent() || currentCategory.get().getMerchantStore().getId().intValue()!=store.getId().intValue()) {
																System.out.println("$#4939#"); return "catalogue-categories";
            }
        }
        Map<String,Language> langs = languageService.getLanguagesMap();
        List<CategoryDescription> descriptions = category.getDescriptions();
								System.out.println("$#4940#"); if(descriptions!=null) {
            Set<CategoryDescription> categoryDescriptions = new HashSet<>();
            for(CategoryDescription description : descriptions) {

                String code = description.getLanguage().getCode();
                Language l = langs.get(code);
																System.out.println("$#4941#"); description.setLanguage(l);
																System.out.println("$#4942#"); description.setCategory(category.getCategory());
                categoryDescriptions.add(description);

            }
												System.out.println("$#4943#"); category.getCategory().setDescriptions(categoryDescriptions);
        }
        //save to DB
								System.out.println("$#4944#"); category.getCategory().setMerchantStore(store);
        //}
								System.out.println("$#4945#"); if (result.hasErrors()) {
												System.out.println("$#4946#"); return "catalogue-categories-category";
        }
        //check parent
								System.out.println("$#4947#"); if(category.getCategory().getParent()!=null) {
												System.out.println("$#4948#"); if(category.getCategory().getParent().getId()==-1) {//this is a root category
																System.out.println("$#4949#"); category.getCategory().setParent(null);
																System.out.println("$#4950#"); category.getCategory().setLineage("/" + category.getCategory().getId() + "/");
																System.out.println("$#4951#"); category.getCategory().setDepth(0);
            }
        }
								System.out.println("$#4952#"); category.getCategory().getAuditSection().setModifiedBy(request.getRemoteUser());
								System.out.println("$#4953#"); categoryService.saveOrUpdate(category.getCategory());
        //ajust lineage and depth
								System.out.println("$#4954#"); if(category.getCategory().getParent()!=null && category.getCategory().getParent().getId()!=-1) {
            Category parent = new Category();
												System.out.println("$#4956#"); parent.setId(category.getCategory().getParent().getId());
												System.out.println("$#4957#"); parent.setMerchantStore(store);
												System.out.println("$#4958#"); categoryService.addChild(parent, category.getCategory());
        }
        //get parent categories
        List<Category> categories = categoryService.listByStore(store,language);
        List<com.salesmanager.shop.admin.model.catalog.Category> readableCategories = CategoryUtils.readableCategoryListConverter(categories, language);

        model.addAttribute("categories", readableCategories);
        model.addAttribute("success","success");
								System.out.println("$#4959#"); return "catalogue-categories-category";
    }


    //category list
    @PreAuthorize("hasRole('PRODUCTS')")
    @RequestMapping(value="/admin/categories/categories.html", method=RequestMethod.GET)
    public String displayCategories(Model model, HttpServletRequest request) throws Exception {
								System.out.println("$#4960#"); setMenu(model,request);
        //does nothing, ajax subsequent request
								System.out.println("$#4961#"); return "catalogue-categories";
    }

    @SuppressWarnings({ "unchecked"})
    @PreAuthorize("hasRole('PRODUCTS')")
    @RequestMapping(value="/admin/categories/paging.html", method=RequestMethod.POST)
    public @ResponseBody ResponseEntity<String> pageCategories(HttpServletRequest request) {
        String categoryName = request.getParameter("name");
        String categoryCode = request.getParameter("code");
        AjaxResponse resp = new AjaxResponse();
        try {
            Language language = (Language)request.getAttribute("LANGUAGE");
            MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
            List<Category> categories = null;
												System.out.println("$#4962#"); if(!StringUtils.isBlank(categoryName)) {
                categories = categoryService.getByName(store, categoryName, language);
												} else if(!StringUtils.isBlank(categoryCode)) { System.out.println("$#4963#");
                categoryService.listByCodes(store, new ArrayList<>(Collections.singletonList(categoryCode)), language);
            } else {
													System.out.println("$#4963#"); // manual correction for else-if mutation coverage
                categories = categoryService.listByStore(store, language);
            }
            for(Category category : categories) {
                @SuppressWarnings("rawtypes")
                Map entry = new HashMap();
                entry.put("categoryId", category.getId());
                CategoryDescription description = category.getDescriptions().iterator().next();

                entry.put("name", description.getName());
                entry.put("code", category.getCode());
                entry.put("visible", category.isVisible());
																System.out.println("$#4964#"); resp.addDataEntry(entry);
            }
												System.out.println("$#4965#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_SUCCESS);
        } catch (Exception e) {
            LOGGER.error("Error while paging categories", e);
												System.out.println("$#4966#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
        }
        String returnString = resp.toJSONString();
        final HttpHeaders httpHeaders= new HttpHeaders();
								System.out.println("$#4967#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON);

								System.out.println("$#4968#"); return new ResponseEntity<>(returnString, httpHeaders, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PRODUCTS')")
    @RequestMapping(value="/admin/categories/hierarchy.html", method=RequestMethod.GET)
    public String displayCategoryHierarchy(Model model, HttpServletRequest request) throws Exception {
								System.out.println("$#4969#"); setMenu(model,request);
        //get the list of categories
        Language language = (Language)request.getAttribute("LANGUAGE");
        MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);

        List<Category> categories = categoryService.listByStore(store, language);
        List<com.salesmanager.shop.admin.model.catalog.Category> readableCategories = CategoryUtils.readableCategoryListConverter(categories, language);

        model.addAttribute("categories", readableCategories);

								System.out.println("$#4970#"); return "catalogue-categories-hierarchy";
    }

    @PreAuthorize("hasRole('PRODUCTS')")
    @RequestMapping(value="/admin/categories/remove.html", method=RequestMethod.POST)
    public @ResponseBody ResponseEntity<String> deleteCategory(HttpServletRequest request, Locale locale) {
        String sid = request.getParameter("categoryId");
        MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
        AjaxResponse resp = new AjaxResponse();
        try {
            Long id = Long.parseLong(sid);
            Optional<Category> category = Optional.ofNullable(categoryService.getById(id, store.getId()));
												System.out.println("$#4971#"); if(category.isPresent() || category.get().getMerchantStore().getId().intValue() !=store.getId().intValue() ) {
																System.out.println("$#4973#"); resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
																System.out.println("$#4974#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
            } else {
																System.out.println("$#4975#"); categoryService.delete(category.get());
																System.out.println("$#4976#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);
            }
        } catch (Exception e) {
            LOGGER.error("Error while deleting category", e);
												System.out.println("$#4977#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
												System.out.println("$#4978#"); resp.setErrorMessage(e);
        }
        String returnString = resp.toJSONString();
        final HttpHeaders httpHeaders= new HttpHeaders();
								System.out.println("$#4979#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON);
								System.out.println("$#4980#"); return new ResponseEntity<>(returnString, httpHeaders, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PRODUCTS')")
    @RequestMapping(value="/admin/categories/moveCategory.html", method=RequestMethod.POST)
    public @ResponseBody ResponseEntity<String> moveCategory(HttpServletRequest request, Locale locale) {
        String parentid = request.getParameter("parentId");
        String childid = request.getParameter("childId");
        MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
        AjaxResponse resp = new AjaxResponse();
        final HttpHeaders httpHeaders= new HttpHeaders();
								System.out.println("$#4981#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            Long parentId = Long.parseLong(parentid);
            Long childId = Long.parseLong(childid);
            Optional<Category> child = Optional.ofNullable(categoryService.getById(childId, store.getId()));
            Optional<Category> parent = Optional.ofNullable(categoryService.getById(parentId, store.getId()));
												System.out.println("$#4982#"); if(child.isPresent()&& child.get().getParent().getId().equals(parentId)) {
																System.out.println("$#4984#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);
                String returnString = resp.toJSONString();
            }
												System.out.println("$#4985#"); if(parentId!=1) {
																System.out.println("$#4986#"); if(isValid(store, child, parent)) {
																				System.out.println("$#4987#"); return getResponseAjax(locale, resp, httpHeaders);
                }
            }
												System.out.println("$#4988#"); parent.get().getAuditSection().setModifiedBy(request.getRemoteUser());
												System.out.println("$#4989#"); categoryService.addChild(parent.get(), child.get());
												System.out.println("$#4990#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);

        } catch (Exception e) {
            LOGGER.error("Error while moving category", e);
												System.out.println("$#4991#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
												System.out.println("$#4992#"); resp.setErrorMessage(e);
        }

        String returnString = resp.toJSONString();
								System.out.println("$#4993#"); return new ResponseEntity<>(returnString, httpHeaders, HttpStatus.OK);
    }

    private boolean isValid(MerchantStore store, Optional<Category> child, Optional<Category> parent) {
								System.out.println("$#4995#"); System.out.println("$#4994#"); return !child.isPresent()|| !parent.isPresent()|| !child.get().getMerchantStore().getId().equals(store.getId()) || !parent.get().getMerchantStore().getId().equals(store.getId());
    }

    private ResponseEntity<String> getResponseAjax(Locale locale, AjaxResponse resp, HttpHeaders httpHeaders) {
								System.out.println("$#4999#"); resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
								System.out.println("$#5000#"); return getResponseAjax(resp, httpHeaders, AjaxResponse.RESPONSE_STATUS_FAIURE);
    }

    @PreAuthorize("hasRole('PRODUCTS')")
    @RequestMapping(value="/admin/categories/checkCategoryCode.html", method=RequestMethod.POST)
    public @ResponseBody ResponseEntity<String> checkCategoryCode(HttpServletRequest request) {
        String code = request.getParameter("code");
        String id = request.getParameter("id");
        MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
        AjaxResponse resp = new AjaxResponse();
        final HttpHeaders httpHeaders= new HttpHeaders();
								System.out.println("$#5001#"); httpHeaders.setContentType(MediaType.APPLICATION_JSON);
								System.out.println("$#5002#"); if(StringUtils.isBlank(code)) {
												System.out.println("$#5003#"); return getResponseAjax(resp, httpHeaders, AjaxResponse.CODE_ALREADY_EXIST);
        }
        try {
            Optional<Category> category =Optional.ofNullable(categoryService.getByCode(store, code));
												System.out.println("$#5004#"); if(category.isPresent() && StringUtils.isBlank(id)) {
																System.out.println("$#5006#"); return getResponseAjax(resp, httpHeaders, AjaxResponse.CODE_ALREADY_EXIST);
            }
												System.out.println("$#5007#"); if(category.isPresent() && !StringUtils.isBlank(id)) {
                try {
                    long lid = Long.parseLong(id);
																				System.out.println("$#5009#"); if(category.get().getCode().equals(code) && category.get().getId() ==lid) {
																								System.out.println("$#5011#"); return getResponseAjax(resp, httpHeaders, AjaxResponse.CODE_ALREADY_EXIST);
                    }
                } catch (Exception e) {
																				System.out.println("$#5012#"); return getResponseAjax(resp, httpHeaders, AjaxResponse.CODE_ALREADY_EXIST);
                }
            }
												System.out.println("$#5013#"); resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);
        } catch (Exception e) {
            LOGGER.error("Error while getting category", e);
												System.out.println("$#5014#"); resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
												System.out.println("$#5015#"); resp.setErrorMessage(e);
        }
        String returnString = resp.toJSONString();
								System.out.println("$#5016#"); return new ResponseEntity<>(returnString, httpHeaders, HttpStatus.OK);
    }

    private ResponseEntity<String> getResponseAjax(AjaxResponse resp, HttpHeaders httpHeaders, int codeAlreadyExist) {
								System.out.println("$#5017#"); resp.setStatus(codeAlreadyExist);
        String returnString = resp.toJSONString();
								System.out.println("$#5018#"); return new ResponseEntity<>(returnString, httpHeaders, HttpStatus.OK);
    }

    private void setMenu(Model model, HttpServletRequest request) {
        //display menu
        Map<String,String> activeMenus = new HashMap<>();
        activeMenus.put("catalogue", "catalogue");
        activeMenus.put("catalogue-categories", "catalogue-categories");
        @SuppressWarnings("unchecked")
        Map<String, Menu> menus = (Map<String, Menu>)request.getAttribute("MENUMAP");
        Menu currentMenu = menus.get("catalogue");
        model.addAttribute("currentMenu",currentMenu);
        model.addAttribute("activeMenus",activeMenus);
        //
    }

}
