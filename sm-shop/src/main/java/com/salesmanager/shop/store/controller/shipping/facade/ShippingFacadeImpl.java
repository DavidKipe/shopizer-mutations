package com.salesmanager.shop.store.controller.shipping.facade;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.jsoup.helper.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.services.shipping.ShippingOriginService;
import com.salesmanager.core.business.services.shipping.ShippingService;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.core.model.shipping.PackageDetails;
import com.salesmanager.core.model.shipping.ShippingConfiguration;
import com.salesmanager.core.model.shipping.ShippingOrigin;
import com.salesmanager.core.model.shipping.ShippingPackageType;
import com.salesmanager.core.model.shipping.ShippingType;
import com.salesmanager.shop.model.references.PersistableAddress;
import com.salesmanager.shop.model.references.ReadableAddress;
import com.salesmanager.shop.model.shipping.ExpeditionConfiguration;
import com.salesmanager.shop.store.api.exception.OperationNotAllowedException;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;

@Service("shippingFacade")
public class ShippingFacadeImpl implements ShippingFacade {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ShippingFacadeImpl.class);

	@Autowired
	ShippingOriginService shippingOriginService;
	
	@Autowired
	ShippingService shippingService;
	
	@Autowired
	CountryService countryService;
	
	@Autowired
	ZoneService zoneService;


	@Override
	public ExpeditionConfiguration getExpeditionConfiguration(MerchantStore store, Language language) {
		ExpeditionConfiguration expeditionConfiguration = new ExpeditionConfiguration();
		try {
			
			ShippingConfiguration config = getDbConfig(store);
			System.out.println("$#13636#"); if(config!=null) {
				System.out.println("$#13639#"); expeditionConfiguration.setIternationalShipping(config.getShipType()!=null && config.getShipType().equals(ShippingType.INTERNATIONAL.name())?true:false);
				System.out.println("$#13640#"); expeditionConfiguration.setTaxOnShipping(config.isTaxOnShipping());
			}
			
			List<String> countries = shippingService.getSupportedCountries(store);

			System.out.println("$#13641#"); if(!CollectionUtils.isEmpty(countries)) {
				
				List<String> countryCode = countries.stream()
						.sorted(Comparator.comparing(n->n.toString()))
						.collect(Collectors.toList());
				
				System.out.println("$#13643#"); expeditionConfiguration.setShipToCountry(countryCode);
			}

		} catch (ServiceException e) {
			LOGGER.error("Error while getting expedition configuration", e);
			throw new ServiceRuntimeException("Error while getting Expedition configuration for store[" + store.getCode() + "]", e);
		}
		System.out.println("$#13644#"); return expeditionConfiguration;
	}

	@Override
	public void saveExpeditionConfiguration(ExpeditionConfiguration expedition, MerchantStore store) {
		System.out.println("$#13645#"); Validate.notNull(expedition, "ExpeditionConfiguration cannot be null");
		try {
			
			//get original configuration
			ShippingConfiguration config = getDbConfig(store);
			System.out.println("$#13646#"); config.setTaxOnShipping(expedition.isTaxOnShipping());
			System.out.println("$#13648#"); config.setShippingType(expedition.isIternationalShipping()?ShippingType.INTERNATIONAL:ShippingType.NATIONAL);
			System.out.println("$#13649#"); this.saveShippingConfiguration(config, store);
			
			System.out.println("$#13650#"); shippingService.setSupportedCountries(store, expedition.getShipToCountry());


		} catch (ServiceException e) {
			LOGGER.error("Error while getting expedition configuration", e);
			throw new ServiceRuntimeException("Error while getting Expedition configuration for store[" + store.getCode() + "]", e);
		}

	}
	
	private void saveShippingConfiguration(ShippingConfiguration config, MerchantStore store) throws ServiceRuntimeException {
		try {
			System.out.println("$#13651#"); shippingService.saveShippingConfiguration(config, store);
		} catch (ServiceException e) {
			LOGGER.error("Error while saving shipping configuration", e);
			throw new ServiceRuntimeException("Error while saving shipping configuration for store [" + store.getCode() + "]", e);
		}
	}

	@Override
	public ReadableAddress getShippingOrigin(MerchantStore store) {
		
		ShippingOrigin o = shippingOriginService.getByStore(store);
		
		System.out.println("$#13652#"); if(o == null) {
			throw new ResourceNotFoundException("Shipping origin does not exists for store [" + store.getCode() + "]");
		}
		
		ReadableAddress address = new ReadableAddress();
		System.out.println("$#13653#"); address.setAddress(o.getAddress());
		System.out.println("$#13654#"); address.setActive(o.isActive());
		System.out.println("$#13655#"); address.setCity(o.getCity());
		System.out.println("$#13656#"); address.setPostalCode(o.getPostalCode());
		System.out.println("$#13657#"); if(o.getCountry()!=null) {
			System.out.println("$#13658#"); address.setCountry(o.getCountry().getIsoCode());
		}
		Zone z = o.getZone();
		System.out.println("$#13659#"); if(z != null) {
			System.out.println("$#13660#"); address.setStateProvince(z.getCode());
		} else {
			System.out.println("$#13661#"); address.setStateProvince(o.getState());
		}

		System.out.println("$#13662#"); return address;
	}

	@Override
	public void saveShippingOrigin(PersistableAddress address, MerchantStore store) {
		System.out.println("$#13663#"); Validate.notNull(address, "PersistableAddress cannot be null");
		try {
			ShippingOrigin o = shippingOriginService.getByStore(store);
			System.out.println("$#13664#"); if(o == null) {
				o = new ShippingOrigin();
			}
			
			System.out.println("$#13665#"); o.setAddress(address.getAddress());
			System.out.println("$#13666#"); o.setCity(address.getCity());
			System.out.println("$#13667#"); o.setCountry(countryService.getByCode(address.getCountry()));
			System.out.println("$#13668#"); o.setMerchantStore(store);
			System.out.println("$#13669#"); o.setActive(address.isActive());
			System.out.println("$#13670#"); o.setPostalCode(address.getPostalCode());
			
			Zone zone = zoneService.getByCode(address.getStateProvince());
			System.out.println("$#13671#"); if(zone == null) {
				System.out.println("$#13672#"); o.setState(address.getStateProvince());
			} else {
				System.out.println("$#13673#"); o.setZone(zone);
			}
			
			System.out.println("$#13674#"); shippingOriginService.save(o);
			
		} catch (ServiceException e) {
			LOGGER.error("Error while getting shipping origin for country [" + address.getCountry() + "]",e);
			throw new ServiceRuntimeException("Error while getting shipping origin for country [" + address.getCountry() + "]",e);
		}


	}

	private ShippingConfiguration getDbConfig(MerchantStore store) {

		try {
			//get original configuration
			ShippingConfiguration config = shippingService.getShippingConfiguration(store);
			System.out.println("$#13675#"); if(config==null) {
				config = new ShippingConfiguration();
				System.out.println("$#13676#"); config.setShippingType(ShippingType.INTERNATIONAL);
			}

			System.out.println("$#13677#"); return config;
		} catch (ServiceException e) {
			LOGGER.error("Error while getting expedition configuration", e);
			throw new ServiceRuntimeException("Error while getting Expedition configuration for store[" + store.getCode() + "]", e);
		}
		
	}

	@Override
	public void createPackage(PackageDetails packaging, MerchantStore store) {
		System.out.println("$#13678#"); Validate.notNull(store, "MerchantStore cannot be null");
		System.out.println("$#13679#"); Validate.notNull(packaging, "PackageDetails cannot be null");
		ShippingConfiguration config = getDbConfig(store);
		
		System.out.println("$#13680#"); if(this.packageExists(config, packaging)) {
			throw new OperationNotAllowedException("Package with unique code [" + packaging.getCode() + "] already exist");
		}
		
		com.salesmanager.core.model.shipping.Package pack = toPackage(packaging);
		
		
		//need to check if code exists
		config.getPackages().add(pack);
		System.out.println("$#13681#"); this.saveShippingConfiguration(config, store);
	
	}
	
	private boolean packageExists(ShippingConfiguration configuration, PackageDetails packageDetails) {
		
		System.out.println("$#13682#"); Validate.notNull(configuration,"ShippingConfiguration cannot be null");
		System.out.println("$#13683#"); Validate.notNull(packageDetails, "PackageDetails cannot be null");
		System.out.println("$#13684#"); Validate.notEmpty(packageDetails.getCode(), "PackageDetails code cannot be empty");
		
		List<com.salesmanager.core.model.shipping.Package> packages = configuration.getPackages().stream().filter(p -> p.getCode().equalsIgnoreCase(packageDetails.getCode())).collect(Collectors.toList());
		
		System.out.println("$#13687#"); if(packages.isEmpty()) {
			System.out.println("$#13688#"); return false;
		} else {
			System.out.println("$#13689#"); return true;
		}
		
		
	}
	
	private com.salesmanager.core.model.shipping.Package packageDetails(ShippingConfiguration configuration, String code) {
		
		System.out.println("$#13690#"); Validate.notNull(configuration,"ShippingConfiguration cannot be null");
		System.out.println("$#13691#"); Validate.notNull(code, "PackageDetails code cannot be null");

		List<com.salesmanager.core.model.shipping.Package> packages = configuration.getPackages().stream().filter(p -> p.getCode().equalsIgnoreCase(code)).collect(Collectors.toList());
		
		System.out.println("$#13694#"); if(!packages.isEmpty()) {
			System.out.println("$#13695#"); return packages.get(0);
		} else {
			return null;
		}

		
	}

	@Override
	public PackageDetails getPackage(String code, MerchantStore store) {
		System.out.println("$#13696#"); Validate.notNull(store, "MerchantStore cannot be null");
		System.out.println("$#13697#"); Validate.notEmpty(code,"Packaging unique code cannot be empty");
		
		ShippingConfiguration config = getDbConfig(store);
		
		com.salesmanager.core.model.shipping.Package p = this.packageDetails(config, code);
		
		System.out.println("$#13698#"); if(p == null) {
			throw new ResourceNotFoundException("Package with unique code [" + code + "] not found");
		}
		
		System.out.println("$#13699#"); return toPackageDetails(p);
	}

	@Override
	public List<PackageDetails> listPackages(MerchantStore store) {
		System.out.println("$#13700#"); Validate.notNull(store, "MerchantStore cannot be null");
		ShippingConfiguration config = getDbConfig(store);
		
		System.out.println("$#13702#"); System.out.println("$#13701#"); return config.getPackages().stream().map(p -> this.toPackageDetails(p)).collect(Collectors.toList());

	}

	@Override
	public void updatePackage(String code, PackageDetails packaging, MerchantStore store) {
		System.out.println("$#13703#"); Validate.notNull(store, "MerchantStore cannot be null");
		System.out.println("$#13704#"); Validate.notNull(packaging, "PackageDetails cannot be null");
		System.out.println("$#13705#"); Validate.notEmpty(code,"Packaging unique code cannot be empty");
		
		ShippingConfiguration config = getDbConfig(store);
		
		com.salesmanager.core.model.shipping.Package p = this.packageDetails(config, code);
		
		System.out.println("$#13706#"); if(p == null) {
			throw new ResourceNotFoundException("Package with unique code [" + packaging.getCode() + "] not found");
		}
		
		com.salesmanager.core.model.shipping.Package pack = toPackage(packaging);
		System.out.println("$#13707#"); pack.setCode(code);
		
		//need to check if code exists
		List<com.salesmanager.core.model.shipping.Package> packs = config.getPackages().stream().filter(pa -> !pa.getCode().equals(code)).collect(Collectors.toList());
		packs.add(pack);
		
		System.out.println("$#13710#"); config.setPackages(packs);
		System.out.println("$#13711#"); this.saveShippingConfiguration(config, store);
		
	}

	@Override
	public void deletePackage(String code, MerchantStore store) {
		
		System.out.println("$#13712#"); Validate.notNull(store, "MerchantStore cannot be null");
		System.out.println("$#13713#"); Validate.notEmpty(code,"Packaging unique code cannot be empty");
		
		ShippingConfiguration config = getDbConfig(store);
		
		List<com.salesmanager.core.model.shipping.Package> packages = config.getPackages();
		
		List<com.salesmanager.core.model.shipping.Package> packList = config.getPackages().stream().filter(p -> p.getCode().equalsIgnoreCase(code)).collect(Collectors.toList());
		
		System.out.println("$#13716#"); if(!packList.isEmpty()) {
			packages.removeAll(packList);
			System.out.println("$#13717#"); config.setPackages(packages);
			System.out.println("$#13718#"); this.saveShippingConfiguration(config, store);
		} 
		
	}
	
	private PackageDetails toPackageDetails(com.salesmanager.core.model.shipping.Package pack) {
		PackageDetails details = new PackageDetails();
		System.out.println("$#13719#"); details.setCode(pack.getCode());
		System.out.println("$#13720#"); details.setShippingHeight(pack.getBoxHeight());
		System.out.println("$#13721#"); details.setShippingLength(pack.getBoxLength());
		System.out.println("$#13722#"); details.setShippingMaxWeight(pack.getMaxWeight());
		//details.setShippingQuantity(pack.getShippingQuantity());
		System.out.println("$#13723#"); details.setShippingWeight(pack.getBoxWeight());
		System.out.println("$#13724#"); details.setShippingWidth(pack.getBoxWidth());
		System.out.println("$#13725#"); details.setTreshold(pack.getTreshold());
		System.out.println("$#13726#"); details.setType(pack.getShipPackageType().name());
		System.out.println("$#13727#"); return details;
	}
	
	private com.salesmanager.core.model.shipping.Package toPackage(PackageDetails pack) {
		com.salesmanager.core.model.shipping.Package details = new com.salesmanager.core.model.shipping.Package();
		System.out.println("$#13728#"); details.setCode(pack.getCode());
		System.out.println("$#13729#"); details.setBoxHeight(pack.getShippingHeight());
		System.out.println("$#13730#"); details.setBoxLength(pack.getShippingLength());
		System.out.println("$#13731#"); details.setMaxWeight(pack.getShippingMaxWeight());
		//details.setShippingQuantity(pack.getShippingQuantity());
		System.out.println("$#13732#"); details.setBoxWeight(pack.getShippingWeight());
		System.out.println("$#13733#"); details.setBoxWidth(pack.getShippingWidth());
		System.out.println("$#13734#"); details.setTreshold(pack.getTreshold());
		System.out.println("$#13735#"); details.setShipPackageType(ShippingPackageType.valueOf(pack.getType()));
		System.out.println("$#13736#"); return details;
	}

}
