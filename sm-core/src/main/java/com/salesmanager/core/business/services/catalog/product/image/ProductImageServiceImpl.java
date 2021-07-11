package com.salesmanager.core.business.services.catalog.product.image;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.modules.cms.product.ProductFileManager;
import com.salesmanager.core.business.repositories.catalog.product.image.ProductImageRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.file.ProductImageSize;
import com.salesmanager.core.model.catalog.product.image.ProductImage;
import com.salesmanager.core.model.catalog.product.image.ProductImageDescription;
import com.salesmanager.core.model.content.FileContentType;
import com.salesmanager.core.model.content.ImageContentFile;
import com.salesmanager.core.model.content.OutputContentFile;

@Service("productImage")
public class ProductImageServiceImpl extends SalesManagerEntityServiceImpl<Long, ProductImage> 
	implements ProductImageService {
	
	private ProductImageRepository productImageRepository;

	@Inject
	public ProductImageServiceImpl(ProductImageRepository productImageRepository) {
		super(productImageRepository);
		this.productImageRepository = productImageRepository;
	}
	
	@Inject
	private ProductFileManager productFileManager;
	

	
	
	public ProductImage getById(Long id) {
		
		
		System.out.println("$#1887#"); return productImageRepository.findOne(id);
	}
	
	
	@Override
	public void addProductImages(Product product, List<ProductImage> productImages) throws ServiceException {
		
		try {
			for(ProductImage productImage : productImages) {
				
				System.out.println("$#1888#"); Assert.notNull(productImage.getImage());
				
		        InputStream inputStream = productImage.getImage();
		        ImageContentFile cmsContentImage = new ImageContentFile();
										System.out.println("$#1889#"); cmsContentImage.setFileName( productImage.getProductImage() );
										System.out.println("$#1890#"); cmsContentImage.setFile( inputStream );
										System.out.println("$#1891#"); cmsContentImage.setFileContentType(FileContentType.PRODUCT);
		        

		        
	
				System.out.println("$#1892#"); addProductImage(product,productImage,cmsContentImage);
			}
		
		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}
	
	
	@Override
	public void addProductImage(Product product, ProductImage productImage, ImageContentFile inputImage) throws ServiceException {
		
		
		
		
		System.out.println("$#1893#"); productImage.setProduct(product);

		try {
			
			System.out.println("$#1894#"); Assert.notNull(inputImage.getFile(),"ImageContentFile.file cannot be null");


			
			System.out.println("$#1895#"); productFileManager.addProductImage(productImage, inputImage);
	
			//insert ProductImage
			System.out.println("$#1896#"); this.saveOrUpdate(productImage);
			

		
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			try {

				System.out.println("$#1897#"); if(inputImage.getFile()!=null) {
					System.out.println("$#1898#"); inputImage.getFile().close();
				}

			} catch(Exception ignore) {
				
			}
		}
		
		
	}
	
	@Override
	public void saveOrUpdate(ProductImage productImage) throws ServiceException {
		
				
		System.out.println("$#1899#"); super.save(productImage);
		
	}
	
	public void addProductImageDescription(ProductImage productImage, ProductImageDescription description)
	throws ServiceException {

		
			System.out.println("$#1900#"); if(productImage.getDescriptions()==null) {
				System.out.println("$#1901#"); productImage.setDescriptions(new ArrayList<ProductImageDescription>());
			}
			
			productImage.getDescriptions().add(description);
			System.out.println("$#1902#"); description.setProductImage(productImage);
			System.out.println("$#1903#"); update(productImage);


	}
	
	//TODO get default product image

	
	@Override
	public OutputContentFile getProductImage(ProductImage productImage, ProductImageSize size) throws ServiceException {

		
		ProductImage pi = new ProductImage();
		String imageName = productImage.getProductImage();
		System.out.println("$#1904#"); if(size == ProductImageSize.LARGE) {
			imageName = "L-" + imageName;
		}
		
		System.out.println("$#1905#"); if(size == ProductImageSize.SMALL) {
			imageName = "S-" + imageName;
		}
		
		System.out.println("$#1906#"); pi.setProductImage(imageName);
		System.out.println("$#1907#"); pi.setProduct(productImage.getProduct());
		
		OutputContentFile outputImage = productFileManager.getProductImage(pi);
		
		System.out.println("$#1908#"); return outputImage;
		
	}
	
	@Override
	public OutputContentFile getProductImage(final String storeCode, final String productCode, final String fileName, final ProductImageSize size) throws ServiceException {
		OutputContentFile outputImage = productFileManager.getProductImage(storeCode, productCode, fileName, size);
		System.out.println("$#1909#"); return outputImage;
		
	}
	
	@Override
	public List<OutputContentFile> getProductImages(Product product) throws ServiceException {
		System.out.println("$#1910#"); return productFileManager.getImages(product);
	}
	
	@Override
	public void removeProductImage(ProductImage productImage) throws ServiceException {

		System.out.println("$#1911#"); if(!StringUtils.isBlank(productImage.getProductImage())) {
			System.out.println("$#1912#"); productFileManager.removeProductImage(productImage);//managed internally
		}
		
		ProductImage p = this.getById(productImage.getId());
		
		
		System.out.println("$#1913#"); this.delete(p);
		
	}
}
