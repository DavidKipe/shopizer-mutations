package com.salesmanager.core.model.catalog.product.image;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import com.salesmanager.core.constants.SchemaConstant;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.generic.SalesManagerEntity;

@Entity
@Table(name = "PRODUCT_IMAGE", schema=SchemaConstant.SALESMANAGER_SCHEMA)
public class ProductImage extends SalesManagerEntity<Long, ProductImage> {
	private static final long serialVersionUID = 247514890386076337L;
	
	@Id
	@Column(name = "PRODUCT_IMAGE_ID")
	@TableGenerator(name = "TABLE_GEN", table = "SM_SEQUENCER", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT", pkColumnValue = "PRODUCT_IMG_SEQ_NEXT_VAL")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN")
	private Long id;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "productImage", cascade = CascadeType.ALL)
	private List<ProductImageDescription> descriptions = new ArrayList<ProductImageDescription>();

	
	@Column(name = "PRODUCT_IMAGE")
	private String productImage;
	
	@Column(name = "DEFAULT_IMAGE")
	private boolean defaultImage = true;
	
	/**
	 * default to 0 for images managed by the system
	 */
	@Column(name = "IMAGE_TYPE")
	private int imageType;
	
	/**
	 * Refers to images not accessible through the system. It may also be a video.
	 */
	@Column(name = "PRODUCT_IMAGE_URL")
	private String productImageUrl;
	

	@Column(name = "IMAGE_CROP")
	private boolean imageCrop;
	
	@ManyToOne(targetEntity = Product.class)
	@JoinColumn(name = "PRODUCT_ID", nullable = false)
	private Product product;
	
	@Transient
	private InputStream image = null;
	
	//private MultiPartFile image

	public ProductImage(){
	}

	public String getProductImage() {
		System.out.println("$#3875#"); return productImage;
	}

	public void setProductImage(String productImage) {
		this.productImage = productImage;
	}

	public boolean isDefaultImage() {
		System.out.println("$#3877#"); System.out.println("$#3876#"); return defaultImage;
	}

	public void setDefaultImage(boolean defaultImage) {
		this.defaultImage = defaultImage;
	}

	public int getImageType() {
		System.out.println("$#3878#"); return imageType;
	}

	public void setImageType(int imageType) {
		this.imageType = imageType;
	}

	public boolean isImageCrop() {
		System.out.println("$#3880#"); System.out.println("$#3879#"); return imageCrop;
	}

	public void setImageCrop(boolean imageCrop) {
		this.imageCrop = imageCrop;
	}

	@Override
	public Long getId() {
		System.out.println("$#3881#"); return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Product getProduct() {
		System.out.println("$#3882#"); return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public void setDescriptions(List<ProductImageDescription> descriptions) {
		this.descriptions = descriptions;
	}

	public List<ProductImageDescription> getDescriptions() {
		System.out.println("$#3883#"); return descriptions;
	}

	public InputStream getImage() {
		System.out.println("$#3884#"); return image;
	}

	public void setImage(InputStream image) {
		this.image = image;
	}

	public String getProductImageUrl() {
		System.out.println("$#3885#"); return productImageUrl;
	}

	public void setProductImageUrl(String productImageUrl) {
		this.productImageUrl = productImageUrl;
	}


}
