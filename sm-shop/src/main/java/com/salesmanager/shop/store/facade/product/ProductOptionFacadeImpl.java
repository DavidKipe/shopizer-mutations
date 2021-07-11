package com.salesmanager.shop.store.facade.product;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.helper.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductAttributeService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductOptionService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductOptionValueService;
import com.salesmanager.core.business.services.content.ContentService;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.catalog.product.attribute.ProductOption;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValue;
import com.salesmanager.core.model.content.FileContentType;
import com.salesmanager.core.model.content.InputContentFile;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.catalog.PersistableProductAttributeMapper;
import com.salesmanager.shop.mapper.catalog.PersistableProductOptionMapper;
import com.salesmanager.shop.mapper.catalog.PersistableProductOptionValueMapper;
import com.salesmanager.shop.mapper.catalog.ReadableProductAttributeMapper;
import com.salesmanager.shop.mapper.catalog.ReadableProductOptionMapper;
import com.salesmanager.shop.mapper.catalog.ReadableProductOptionValueMapper;
import com.salesmanager.shop.model.catalog.product.attribute.PersistableProductAttribute;
import com.salesmanager.shop.model.catalog.product.attribute.api.PersistableProductOptionEntity;
import com.salesmanager.shop.model.catalog.product.attribute.api.PersistableProductOptionValueEntity;
import com.salesmanager.shop.model.catalog.product.attribute.api.ReadableProductAttributeEntity;
import com.salesmanager.shop.model.catalog.product.attribute.api.ReadableProductAttributeList;
import com.salesmanager.shop.model.catalog.product.attribute.api.ReadableProductOptionEntity;
import com.salesmanager.shop.model.catalog.product.attribute.api.ReadableProductOptionList;
import com.salesmanager.shop.model.catalog.product.attribute.api.ReadableProductOptionValueEntity;
import com.salesmanager.shop.model.catalog.product.attribute.api.ReadableProductOptionValueList;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.controller.product.facade.ProductOptionFacade;

@Service
public class ProductOptionFacadeImpl implements ProductOptionFacade {

	@Autowired
	private ProductOptionService productOptionService;

	@Autowired
	private ProductOptionValueService productOptionValueService;

	@Autowired
	private ReadableProductOptionMapper readableMapper;

	@Autowired
	private PersistableProductOptionMapper persistableeMapper;

	@Autowired
	private PersistableProductOptionValueMapper persistableOptionValueMapper;

	@Autowired
	private ReadableProductOptionValueMapper readableOptionValueMapper;

	@Autowired
	private ContentService contentService;

	@Autowired
	private ProductAttributeService productAttributeService;

	@Autowired
	private PersistableProductAttributeMapper persistableProductAttributeMapper;

	@Autowired
	private ReadableProductAttributeMapper readableProductAttributeMapper;

	@Autowired
	private ProductService productService;

	@Override
	public ReadableProductOptionEntity saveOption(PersistableProductOptionEntity option, MerchantStore store,
			Language language) {
		System.out.println("$#14940#"); Validate.notNull(option, "ProductOption cannot be null");
		System.out.println("$#14941#"); Validate.notNull(store, "MerchantStore cannot be null");

		ProductOption optionModel = new ProductOption();
		System.out.println("$#14943#"); System.out.println("$#14942#"); if (option.getId() != null && option.getId().longValue() > 0) {
			optionModel = productOptionService.getById(store, option.getId());
			System.out.println("$#14945#"); if (optionModel == null) {
				throw new ResourceNotFoundException(
						"ProductOption not found for if [" + option.getId() + "] and store [" + store.getCode() + "]");
			}
		}

		optionModel = persistableeMapper.convert(option, optionModel, store, language);
		try {
			System.out.println("$#14946#"); productOptionService.saveOrUpdate(optionModel);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("An exception occured while saving ProductOption", e);
		}

		optionModel = productOptionService.getById(store, optionModel.getId());
		ReadableProductOptionEntity readable = readableMapper.convert(optionModel, store, language);
		System.out.println("$#14947#"); return readable;

	}

	@Override
	public void deleteOption(Long optionId, MerchantStore store) {
		ProductOption optionModel = productOptionService.getById(store, optionId);
		System.out.println("$#14948#"); if (optionModel == null) {
			throw new ResourceNotFoundException(
					"ProductOption not found for [" + optionId + "] and store [" + store.getCode() + "]");
		}
		try {
			System.out.println("$#14949#"); productOptionService.delete(optionModel);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("An exception occured while deleting ProductOption [" + optionId + "]",
					e);
		}
	}

	@Override
	public void deleteOptionValue(Long optionValueId, MerchantStore store) {
		ProductOptionValue optionModel = productOptionValueService.getById(store, optionValueId);
		System.out.println("$#14950#"); if (optionModel == null) {
			throw new ResourceNotFoundException(
					"ProductOptionValue not found for  [" + optionValueId + "] and store [" + store.getCode() + "]");
		}
		try {
			System.out.println("$#14951#"); productOptionValueService.delete(optionModel);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException(
					"An exception occured while deleting ProductOptionValue [" + optionValueId + "]", e);
		}

	}

	@Override
	public ReadableProductOptionValueList optionValues(MerchantStore store, Language language, String name, int page,
			int count) {
		System.out.println("$#14952#"); Validate.notNull(store, "MerchantStore should not be null");

		Page<ProductOptionValue> options = productOptionValueService.getByMerchant(store, null, name, page, count);
		ReadableProductOptionValueList valueList = new ReadableProductOptionValueList();
		System.out.println("$#14953#"); valueList.setTotalPages(options.getTotalPages());
		System.out.println("$#14954#"); valueList.setRecordsTotal(options.getTotalElements());
		System.out.println("$#14955#"); valueList.setNumber(options.getNumber());

		List<ReadableProductOptionValueEntity> values = options.getContent().stream()
				.map(option -> readableOptionValueMapper.convert(option, store, null)).collect(Collectors.toList());

		System.out.println("$#14957#"); valueList.setOptionValues(values);

		System.out.println("$#14958#"); return valueList;
	}

	@Override
	public ReadableProductOptionList options(MerchantStore store, Language language, String name, int page, int count) {
		System.out.println("$#14959#"); Validate.notNull(store, "MerchantStore should not be null");

		Page<ProductOption> options = productOptionService.getByMerchant(store, null, name, page, count);
		ReadableProductOptionList valueList = new ReadableProductOptionList();
		System.out.println("$#14960#"); valueList.setTotalPages(options.getTotalPages());
		System.out.println("$#14961#"); valueList.setRecordsTotal(options.getTotalElements());
		System.out.println("$#14962#"); valueList.setNumber(options.getNumber());

		List<ReadableProductOptionEntity> values = options.getContent().stream()
				.map(option -> readableMapper.convert(option, store, null)).collect(Collectors.toList());

		System.out.println("$#14964#"); valueList.setOptions(values);

		System.out.println("$#14965#"); return valueList;
	}

	@Override
	public ReadableProductOptionEntity getOption(Long optionId, MerchantStore store, Language language) {

		System.out.println("$#14966#"); Validate.notNull(optionId, "Option id cannot be null");
		System.out.println("$#14967#"); Validate.notNull(store, "Store cannot be null");

		ProductOption option = productOptionService.getById(store, optionId);

		System.out.println("$#14968#"); if (option == null) {
			throw new ResourceNotFoundException("Option id [" + optionId + "] not found");
		}

		System.out.println("$#14969#"); return readableMapper.convert(option, store, language);
	}

	@Override
	public boolean optionExists(String code, MerchantStore store) {
		System.out.println("$#14970#"); Validate.notNull(code, "Option code must not be null");
		System.out.println("$#14971#"); Validate.notNull(store, "Store code must not be null");
		boolean exists = false;
		ProductOption option = productOptionService.getByCode(store, code);
		System.out.println("$#14972#"); if (option != null) {
			exists = true;
		}
		System.out.println("$#14974#"); System.out.println("$#14973#"); return exists;
	}

	@Override
	public boolean optionValueExists(String code, MerchantStore store) {
		System.out.println("$#14975#"); Validate.notNull(code, "Option value code must not be null");
		System.out.println("$#14976#"); Validate.notNull(store, "Store code must not be null");
		boolean exists = false;
		ProductOptionValue optionValue = productOptionValueService.getByCode(store, code);
		System.out.println("$#14977#"); if (optionValue != null) {
			exists = true;
		}
		System.out.println("$#14979#"); System.out.println("$#14978#"); return exists;
	}

	@Override
	public ReadableProductOptionValueEntity saveOptionValue(PersistableProductOptionValueEntity optionValue,
			MerchantStore store, Language language) {
		System.out.println("$#14980#"); Validate.notNull(optionValue, "Option value code must not be null");
		System.out.println("$#14981#"); Validate.notNull(store, "Store code must not be null");

		ProductOptionValue value = new ProductOptionValue();
		System.out.println("$#14983#"); System.out.println("$#14982#"); if (optionValue.getId() != null && optionValue.getId().longValue() > 0) {
			value = productOptionValueService.getById(store, optionValue.getId());
			System.out.println("$#14985#"); if (value == null) {
				throw new ResourceNotFoundException("ProductOptionValue [" + optionValue.getId()
						+ "] does not exists for store [" + store.getCode() + "]");
			}
		}

		value = persistableOptionValueMapper.convert(optionValue, value, store, language);


		try {
			System.out.println("$#14986#"); productOptionValueService.saveOrUpdate(value);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Exception while saving option value", e);
		}

		ProductOptionValue optValue = productOptionValueService.getById(store, value.getId());

		// convert to readable
		ReadableProductOptionValueEntity readableProductOptionValue = new ReadableProductOptionValueEntity();
		readableProductOptionValue = readableOptionValueMapper.convert(optValue, readableProductOptionValue, store,
				language);

		System.out.println("$#14987#"); return readableProductOptionValue;
	}

	@Override
	public ReadableProductOptionValueEntity getOptionValue(Long optionValueId, MerchantStore store, Language language) {

		System.out.println("$#14988#"); Validate.notNull(optionValueId, "OptionValue id cannot be null");
		System.out.println("$#14989#"); Validate.notNull(store, "Store cannot be null");

		ProductOptionValue optionValue = productOptionValueService.getById(store, optionValueId);

		System.out.println("$#14990#"); if (optionValue == null) {
			throw new ResourceNotFoundException("OptionValue id [" + optionValueId + "] not found");
		}

		System.out.println("$#14991#"); return readableOptionValueMapper.convert(optionValue, store, language);
	}

	@Override
	public ReadableProductAttributeEntity saveAttribute(Long productId, PersistableProductAttribute attribute,
			MerchantStore store, Language language) {
		System.out.println("$#14992#"); Validate.notNull(productId, "Product id cannot be null");
		System.out.println("$#14993#"); Validate.notNull(attribute, "ProductAttribute cannot be null");
		System.out.println("$#14994#"); Validate.notNull(store, "Store cannot be null");

		System.out.println("$#14995#"); attribute.setProductId(productId);
		ProductAttribute attr = new ProductAttribute();
		System.out.println("$#14997#"); System.out.println("$#14996#"); if (attribute.getId() != null && attribute.getId().longValue() > 0) {
			attr = productAttributeService.getById(attribute.getId());
			System.out.println("$#14999#"); if (attr == null) {
				throw new ResourceNotFoundException("Product attribute [" + attribute.getId() + "] not found");
			}

			System.out.println("$#15000#"); if (productId != attr.getProduct().getId().longValue()) {
				throw new ResourceNotFoundException(
						"Product attribute [" + attribute.getId() + "] not found for product [" + productId + "]");
			}
		}

		attr = persistableProductAttributeMapper.convert(attribute, attr, store, language);

		try {
			System.out.println("$#15001#"); productAttributeService.saveOrUpdate(attr);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Exception while saving ProductAttribute", e);
		}

		// refresh
		attr = productAttributeService.getById(attr.getId());
		ReadableProductAttributeEntity readable = readableProductAttributeMapper.convert(attr, store, language);

		System.out.println("$#15002#"); return readable;
	}

	@Override
	public ReadableProductAttributeEntity getAttribute(Long productId, Long attributeId, MerchantStore store,
			Language language) {

		ProductAttribute attr = productAttributeService.getById(attributeId);

		System.out.println("$#15003#"); if (attr == null) {
			throw new ResourceNotFoundException(
					"ProductAttribute not found for [" + attributeId + "] and store [" + store.getCode() + "]");
		}

		System.out.println("$#15004#"); if (attr.getProduct().getId().longValue() != productId) {
			throw new ResourceNotFoundException(
					"ProductAttribute not found for [" + attributeId + "] and product [" + productId + "]");
		}

		System.out.println("$#15005#"); if (attr.getProduct().getMerchantStore().getId().intValue() != store.getId().intValue()) {
			throw new ResourceNotFoundException("ProductAttribute not found for [" + attributeId + "] and product ["
					+ productId + "] and store [" + store.getCode() + "]");
		}

		ReadableProductAttributeEntity readable = readableProductAttributeMapper.convert(attr, store, language);

		System.out.println("$#15006#"); return readable;
	}

	@Override
	public ReadableProductAttributeList getAttributesList(Long productId, MerchantStore store, Language language) {

		try {

			Product product = productService.getById(productId);

			System.out.println("$#15007#"); if (product == null) {
				throw new ResourceNotFoundException("Productnot found for id [" + productId + "]");
			}

			System.out.println("$#15008#"); if (product.getMerchantStore().getId().intValue() != store.getId().intValue()) {
				throw new ResourceNotFoundException(
						"Productnot found id [" + productId + "] for store [" + store.getCode() + "]");
			}

			List<ProductAttribute> attributes = productAttributeService.getByProductId(store, product, language);
			ReadableProductAttributeList attrList = new ReadableProductAttributeList();
			System.out.println("$#15009#"); attrList.setRecordsTotal(attributes.size());
			System.out.println("$#15010#"); attrList.setNumber(attributes.size());

			List<ReadableProductAttributeEntity> values = attributes.stream()
					.map(attribute -> readableProductAttributeMapper.convert(attribute, store, language))
					.collect(Collectors.toList());

			System.out.println("$#15012#"); attrList.setAttributes(values);

			System.out.println("$#15013#"); return attrList;

		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Error while getting attributes", e);
		}

	}

	@Override
	public void deleteAttribute(Long productId, Long attributeId, MerchantStore store) {
		try {

			ProductAttribute attr = productAttributeService.getById(attributeId);
			System.out.println("$#15014#"); if (attr == null) {
				throw new ResourceNotFoundException(
						"ProductAttribute not found for [" + attributeId + "] and store [" + store.getCode() + "]");
			}

			System.out.println("$#15015#"); if (attr.getProduct().getId().longValue() != productId) {
				throw new ResourceNotFoundException(
						"ProductAttribute not found for [" + attributeId + "] and product [" + productId + "]");
			}

			System.out.println("$#15016#"); if (attr.getProduct().getMerchantStore().getId().intValue() != store.getId().intValue()) {
				throw new ResourceNotFoundException("ProductAttribute not found for [" + attributeId + "] and product ["
						+ productId + "] and store [" + store.getCode() + "]");
			}

			System.out.println("$#15017#"); productAttributeService.delete(attr);

		} catch (ServiceException e) {
			throw new ServiceRuntimeException(
					"An exception occured while deleting ProductAttribute [" + attributeId + "]", e);
		}

	}



	@Override
	public void addOptionValueImage(MultipartFile image, Long optionValueId,
			MerchantStore store, Language language) {
		
		
		System.out.println("$#15018#"); Validate.notNull(optionValueId,"OptionValueId must not be null");
		System.out.println("$#15019#"); Validate.notNull(image,"Image must not be null");
		//get option value
		ProductOptionValue value = productOptionValueService.getById(store, optionValueId);
		System.out.println("$#15020#"); if(value == null) {
			throw new ResourceNotFoundException("Product option value [" + optionValueId + "] not found");
		}
		
		try {
			String imageName = image.getOriginalFilename();
			InputStream inputStream = image.getInputStream();
			InputContentFile cmsContentImage = new InputContentFile();
			System.out.println("$#15021#"); cmsContentImage.setFileName(imageName);
			System.out.println("$#15022#"); cmsContentImage.setMimeType(image.getContentType());
			System.out.println("$#15023#"); cmsContentImage.setFile(inputStream);

			System.out.println("$#15024#"); contentService.addOptionImage(store.getCode(), cmsContentImage);
			System.out.println("$#15025#"); value.setProductOptionValueImage(imageName);
			System.out.println("$#15026#"); productOptionValueService.saveOrUpdate(value);
		} catch (Exception e) {
			throw new ServiceRuntimeException("Exception while adding option value image", e);
		}


		
		
		return;
	}

	@Override
	public void removeOptionValueImage(Long optionValueId, MerchantStore store,
			Language language) {
		System.out.println("$#15027#"); Validate.notNull(optionValueId,"OptionValueId must not be null");
		ProductOptionValue value = productOptionValueService.getById(store, optionValueId);
		System.out.println("$#15028#"); if(value == null) {
			throw new ResourceNotFoundException("Product option value [" + optionValueId + "] not found");
		}
		
		try {

			System.out.println("$#15029#"); contentService.removeFile(store.getCode(), FileContentType.PROPERTY, value.getProductOptionValueImage());
			System.out.println("$#15030#"); value.setProductOptionValueImage(null);
			System.out.println("$#15031#"); productOptionValueService.saveOrUpdate(value);
		} catch (Exception e) {
			throw new ServiceRuntimeException("Exception while removing option value image", e);
		}


		
		
		return;
	}

}