package com.salesmanager.core.business.services.catalog.product.file;

import java.util.Optional;

import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.modules.cms.content.StaticContentFileManager;
import com.salesmanager.core.business.repositories.catalog.product.file.DigitalProductRepository;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.file.DigitalProduct;
import com.salesmanager.core.model.content.FileContentType;
import com.salesmanager.core.model.content.InputContentFile;
import com.salesmanager.core.model.merchant.MerchantStore;

@Service("digitalProductService")
public class DigitalProductServiceImpl extends SalesManagerEntityServiceImpl<Long, DigitalProduct> 
	implements DigitalProductService {
	

	private DigitalProductRepository digitalProductRepository;
	
    @Inject
    StaticContentFileManager productDownloadsFileManager;
    
    @Inject
    ProductService productService;

	@Inject
	public DigitalProductServiceImpl(DigitalProductRepository digitalProductRepository) {
		super(digitalProductRepository);
		this.digitalProductRepository = digitalProductRepository;
	}
	
	@Override
	public void addProductFile(Product product, DigitalProduct digitalProduct, InputContentFile inputFile) throws ServiceException {
	
		System.out.println("$#1861#"); Assert.notNull(digitalProduct,"DigitalProduct cannot be null");
		System.out.println("$#1862#"); Assert.notNull(product,"Product cannot be null");
		System.out.println("$#1863#"); digitalProduct.setProduct(product);

		try {
			
			System.out.println("$#1864#"); Assert.notNull(inputFile.getFile(),"InputContentFile.file cannot be null");
			
			System.out.println("$#1865#"); Assert.notNull(product.getMerchantStore(),"Product.merchantStore cannot be null");
			System.out.println("$#1866#"); this.saveOrUpdate(digitalProduct);
			
		    String path = null;
		    
			
			System.out.println("$#1867#"); productDownloadsFileManager.addFile(product.getMerchantStore().getCode(), Optional.of(path), inputFile);
			
			System.out.println("$#1868#"); product.setProductVirtual(true);
			System.out.println("$#1869#"); productService.update(product);
		
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			try {

				System.out.println("$#1870#"); if(inputFile.getFile()!=null) {
					System.out.println("$#1871#"); inputFile.getFile().close();
				}

			} catch(Exception ignore) {}
		}
		
		
	}
	
	@Override
	public DigitalProduct getByProduct(MerchantStore store, Product product) throws ServiceException {
		System.out.println("$#1872#"); return digitalProductRepository.findByProduct(store.getId(), product.getId());
	}
	
	@Override
	public void delete(DigitalProduct digitalProduct) throws ServiceException {
		
		System.out.println("$#1873#"); Assert.notNull(digitalProduct,"DigitalProduct cannot be null");
		System.out.println("$#1874#"); Assert.notNull(digitalProduct.getProduct(),"DigitalProduct.product cannot be null");
		//refresh file
		digitalProduct = this.getById(digitalProduct.getId());
		System.out.println("$#1875#"); super.delete(digitalProduct);
		
		String path = null;

		System.out.println("$#1876#"); productDownloadsFileManager.removeFile(digitalProduct.getProduct().getMerchantStore().getCode(), FileContentType.PRODUCT, digitalProduct.getProductFileName(), Optional.of(path));
		System.out.println("$#1877#"); digitalProduct.getProduct().setProductVirtual(false);
		System.out.println("$#1878#"); productService.update(digitalProduct.getProduct());
	}
	
	
	@Override
	public void saveOrUpdate(DigitalProduct digitalProduct) throws ServiceException {
		
		System.out.println("$#1879#"); Assert.notNull(digitalProduct,"DigitalProduct cannot be null");
		System.out.println("$#1880#"); Assert.notNull(digitalProduct.getProduct(),"DigitalProduct.product cannot be null");
		System.out.println("$#1881#"); if(digitalProduct.getId()==null || digitalProduct.getId().longValue()==0) {
			System.out.println("$#1883#"); super.save(digitalProduct);
		} else {
			System.out.println("$#1884#"); super.create(digitalProduct);
		}
		
		System.out.println("$#1885#"); digitalProduct.getProduct().setProductVirtual(true);
		System.out.println("$#1886#"); productService.update(digitalProduct.getProduct());
		
		
	}
	

	

}
