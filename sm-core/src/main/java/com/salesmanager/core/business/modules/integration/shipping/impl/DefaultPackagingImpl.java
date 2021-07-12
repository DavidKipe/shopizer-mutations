package com.salesmanager.core.business.modules.integration.shipping.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.shipping.ShippingService;
import com.salesmanager.core.business.services.system.MerchantLogService;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.shipping.PackageDetails;
import com.salesmanager.core.model.shipping.ShippingConfiguration;
import com.salesmanager.core.model.shipping.ShippingProduct;
import com.salesmanager.core.model.system.MerchantLog;
import com.salesmanager.core.modules.integration.shipping.model.Packaging;

public class DefaultPackagingImpl implements Packaging {

	
	@Inject
	private ShippingService shippingService;
	
	@Inject
	private MerchantLogService merchantLogService;
	
	/** default dimensions **/
	private final static Double defaultWeight = 1D;
	private final static Double defaultHeight = 4D;
	private final static Double defaultLength = 4D;
	private final static Double defaultWidth = 4D;
	
	@Override
	public List<PackageDetails> getBoxPackagesDetails(
			List<ShippingProduct> products, MerchantStore store)
			throws ServiceException {

		
		System.out.println("$#1054#"); if (products == null) {
			throw new ServiceException("Product list cannot be null !!");
		}

		double width = 0;
		double length = 0;
		double height = 0;
		double weight = 0;
		double maxweight = 0;

		//int treshold = 0;
		
		
		ShippingConfiguration shippingConfiguration = shippingService.getShippingConfiguration(store);
		System.out.println("$#1055#"); if(shippingConfiguration==null) {
			throw new ServiceException("ShippingConfiguration not found for merchant " + store.getCode());
		}
		
		width = new Double(shippingConfiguration.getBoxWidth()).doubleValue();
		length = new Double(shippingConfiguration.getBoxLength()).doubleValue();
		height = new Double(shippingConfiguration.getBoxHeight()).doubleValue();
		weight = new Double(shippingConfiguration.getBoxWeight()).doubleValue();
		maxweight = new Double(shippingConfiguration.getMaxWeight()).doubleValue();
		


		List<PackageDetails> boxes = new ArrayList<PackageDetails>();

		// maximum number of boxes
		int maxBox = 100;
		int iterCount = 0;

		List<Product> individualProducts = new ArrayList<Product>();

		// need to put items individually
		for(ShippingProduct shippingProduct : products){

			Product product = shippingProduct.getProduct();
			System.out.println("$#1056#"); if (product.isProductVirtual()) {
				continue;
			}

			int qty = shippingProduct.getQuantity();

			Set<ProductAttribute> attrs = shippingProduct.getProduct().getAttributes();

			// set attributes values
			BigDecimal w = product.getProductWeight();
			BigDecimal h = product.getProductHeight();
			BigDecimal l = product.getProductLength();
			BigDecimal wd = product.getProductWidth();
			System.out.println("$#1057#"); if(w==null) {
				w = new BigDecimal(defaultWeight);
			}
			System.out.println("$#1058#"); if(h==null) {
				h = new BigDecimal(defaultHeight);
			}
			System.out.println("$#1059#"); if(l==null) {
				l = new BigDecimal(defaultLength);
			}
			System.out.println("$#1060#"); if(wd==null) {
				wd = new BigDecimal(defaultWidth);
			}
			System.out.println("$#1062#"); System.out.println("$#1061#"); if (attrs != null && attrs.size() > 0) {
				for(ProductAttribute attribute : attrs) {
					System.out.println("$#1064#"); if(attribute.getProductAttributeWeight()!=null) {
						w = w.add(attribute.getProductAttributeWeight());
					}
				}
			}
			


			System.out.println("$#1066#"); System.out.println("$#1065#"); if (qty > 1) {

				System.out.println("$#1069#"); System.out.println("$#1068#"); System.out.println("$#1067#"); for (int i = 1; i <= qty; i++) {
					Product temp = new Product();
					System.out.println("$#1070#"); temp.setProductHeight(h);
					System.out.println("$#1071#"); temp.setProductLength(l);
					System.out.println("$#1072#"); temp.setProductWidth(wd);
					System.out.println("$#1073#"); temp.setProductWeight(w);
					System.out.println("$#1074#"); temp.setAttributes(product.getAttributes());
					System.out.println("$#1075#"); temp.setDescriptions(product.getDescriptions());
					individualProducts.add(temp);
				}
			} else {
				Product temp = new Product();
				System.out.println("$#1076#"); temp.setProductHeight(h);
				System.out.println("$#1077#"); temp.setProductLength(l);
				System.out.println("$#1078#"); temp.setProductWidth(wd);
				System.out.println("$#1079#"); temp.setProductWeight(w);
				System.out.println("$#1080#"); temp.setAttributes(product.getAttributes());
				System.out.println("$#1081#"); temp.setDescriptions(product.getDescriptions());
				individualProducts.add(temp);
			}
			System.out.println("$#1082#"); iterCount++;
		}

		System.out.println("$#1083#"); if (iterCount == 0) {
			System.out.println("$#1084#"); return null;
		}

		int productCount = individualProducts.size();

		List<PackingBox> boxesList = new ArrayList<PackingBox>();

		//start the creation of boxes
		PackingBox box = new PackingBox();
		// set box max volume
		System.out.println("$#1085#"); double maxVolume = width * length * height;

		System.out.println("$#1087#"); if (maxVolume == 0 || maxweight == 0) {
			
			System.out.println("$#1089#"); merchantLogService.save(new MerchantLog(store,"shipping","Check shipping box configuration, it has a volume of "
							+ maxVolume + " and a maximum weight of "
							+ maxweight
							+ ". Those values must be greater than 0."));
			
			throw new ServiceException("Product configuration exceeds box configuraton");
			

		}
		
		
		System.out.println("$#1090#"); box.setVolumeLeft(maxVolume);
		System.out.println("$#1091#"); box.setWeightLeft(maxweight);

		boxesList.add(box);//assign first box

		//int boxCount = 1;
		List<Product> assignedProducts = new ArrayList<Product>();

		// calculate the volume for the next object
		System.out.println("$#1093#"); System.out.println("$#1092#"); if (assignedProducts.size() > 0) {
			individualProducts.removeAll(assignedProducts);
			assignedProducts = new ArrayList<Product>();
		}

		boolean productAssigned = false;

		for(Product p : individualProducts) {

			//Set<ProductAttribute> attributes = p.getAttributes();
			productAssigned = false;

			double productWeight = p.getProductWeight().doubleValue();


			// validate if product fits in the box
			System.out.println("$#1095#"); System.out.println("$#1094#");
			System.out.println("$#1096#"); System.out.println("$#1098#");
			if (p.getProductWidth().doubleValue() > width
					|| p.getProductHeight().doubleValue() > height
					|| p.getProductLength().doubleValue() > length) {
				// log message to customer
				System.out.println("$#1100#"); merchantLogService.save(new MerchantLog(store,"shipping","Product "
						+ p.getSku()
						+ " has a demension larger than the box size specified. Will use per item calculation."));
				throw new ServiceException("Product configuration exceeds box configuraton");

			}

			System.out.println("$#1102#"); System.out.println("$#1101#"); if (productWeight > maxweight) {
				System.out.println("$#1103#"); merchantLogService.save(new MerchantLog(store,"shipping","Product "
						+ p.getSku()
						+ " has a weight larger than the box maximum weight specified. Will use per item calculation."));
				
				throw new ServiceException("Product configuration exceeds box configuraton");

			}

			System.out.println("$#1104#");
			double productVolume = (p.getProductWidth().doubleValue()
					* p.getProductHeight().doubleValue() * p
					.getProductLength().doubleValue());

			System.out.println("$#1106#"); if (productVolume == 0) {
				
				System.out.println("$#1107#"); merchantLogService.save(new MerchantLog(store,"shipping","Product "
						+ p.getSku()
						+ " has one of the dimension set to 0 and therefore cannot calculate the volume"));
				
				throw new ServiceException("Product configuration exceeds box configuraton");
				

			}
			
			System.out.println("$#1109#"); System.out.println("$#1108#"); if (productVolume > maxVolume) {
				
				throw new ServiceException("Product configuration exceeds box configuraton");
				
			}

			//List boxesList = boxesList;

			// try each box
			//Iterator boxIter = boxesList.iterator();
			for (PackingBox pbox : boxesList) {
				double volumeLeft = pbox.getVolumeLeft();
				double weightLeft = pbox.getWeightLeft();

				System.out.println("$#1112#"); System.out.println("$#1111#"); System.out.println("$#1110#"); System.out.println("$#1113#");
				if ((volumeLeft * .75) >= productVolume
						&& pbox.getWeightLeft() >= productWeight) {// fit the item
																	// in this
																	// box
					// fit in the current box
					System.out.println("$#1115#"); volumeLeft = volumeLeft - productVolume;
					System.out.println("$#1116#"); pbox.setVolumeLeft(volumeLeft);
					System.out.println("$#1117#"); weightLeft = weightLeft - productWeight;
					System.out.println("$#1118#"); pbox.setWeightLeft(weightLeft);

					assignedProducts.add(p);
					System.out.println("$#1119#"); productCount--;

					double w = pbox.getWeight();
					System.out.println("$#1120#"); w = w + productWeight;
					System.out.println("$#1121#"); pbox.setWeight(w);
					productAssigned = true;
					System.out.println("$#1122#"); maxBox--;
					break;

				}

			}

			System.out.println("$#1123#"); if (!productAssigned) {// create a new box

				box = new PackingBox();
				// set box max volume
				System.out.println("$#1124#"); box.setVolumeLeft(maxVolume);
				System.out.println("$#1125#"); box.setWeightLeft(maxweight);

				boxesList.add(box);

				System.out.println("$#1126#"); double volumeLeft = box.getVolumeLeft() - productVolume;
				System.out.println("$#1127#"); box.setVolumeLeft(volumeLeft);
				System.out.println("$#1128#"); double weightLeft = box.getWeightLeft() - productWeight;
				System.out.println("$#1129#"); box.setWeightLeft(weightLeft);
				assignedProducts.add(p);
				System.out.println("$#1130#"); productCount--;
				double w = box.getWeight();
				System.out.println("$#1131#"); w = w + productWeight;
				System.out.println("$#1132#"); box.setWeight(w);
				System.out.println("$#1133#"); maxBox--;
			}

		}

		// now prepare the shipping info

		// number of boxes

		//Iterator ubIt = usedBoxesList.iterator();

		System.out.println("$#1134#"); System.out.println("###################################");
		System.out.println("$#1135#"); System.out.println("Number of boxes " + boxesList.size());
		System.out.println("$#1136#"); System.out.println("###################################");

		for(PackingBox pb : boxesList) {
			PackageDetails details = new PackageDetails();
			System.out.println("$#1137#"); details.setShippingHeight(height);
			System.out.println("$#1138#"); details.setShippingLength(length);
			System.out.println("$#1140#"); System.out.println("$#1139#"); details.setShippingWeight(weight + box.getWeight());
			System.out.println("$#1141#"); details.setShippingWidth(width);
			System.out.println("$#1142#"); details.setItemName(store.getCode());
			boxes.add(details);
		}

		System.out.println("$#1143#"); return boxes;

	}

	@Override
	public List<PackageDetails> getItemPackagesDetails(
			List<ShippingProduct> products, MerchantStore store)
			throws ServiceException {
		
		
		List<PackageDetails> packages = new ArrayList<PackageDetails>();
		for(ShippingProduct shippingProduct : products) {
			Product product = shippingProduct.getProduct();

			System.out.println("$#1144#"); if (product.isProductVirtual()) {
				continue;
			}

			//BigDecimal weight = product.getProductWeight();
			Set<ProductAttribute> attributes = product.getAttributes();
			// set attributes values
			BigDecimal w = product.getProductWeight();
			BigDecimal h = product.getProductHeight();
			BigDecimal l = product.getProductLength();
			BigDecimal wd = product.getProductWidth();
			System.out.println("$#1145#"); if(w==null) {
				w = new BigDecimal(defaultWeight);
			}
			System.out.println("$#1146#"); if(h==null) {
				h = new BigDecimal(defaultHeight);
			}
			System.out.println("$#1147#"); if(l==null) {
				l = new BigDecimal(defaultLength);
			}
			System.out.println("$#1148#"); if(wd==null) {
				wd = new BigDecimal(defaultWidth);
			}
			System.out.println("$#1150#"); System.out.println("$#1149#"); if (attributes != null && attributes.size() > 0) {
				for(ProductAttribute attribute : attributes) {
					System.out.println("$#1152#"); if(attribute.getAttributeAdditionalWeight()!=null) {
						w = w.add(attribute.getProductAttributeWeight());
					}
				}
			}
			
			

			System.out.println("$#1153#"); if (shippingProduct.getQuantity() == 1) {
				PackageDetails detail = new PackageDetails();

	
				System.out.println("$#1154#"); detail.setShippingHeight(h
						.doubleValue());
				System.out.println("$#1155#"); detail.setShippingLength(l
						.doubleValue());
				System.out.println("$#1156#"); detail.setShippingWeight(w.doubleValue());
				System.out.println("$#1157#"); detail.setShippingWidth(wd.doubleValue());
				System.out.println("$#1158#"); detail.setShippingQuantity(shippingProduct.getQuantity());
				String description = "item";
				System.out.println("$#1160#"); System.out.println("$#1159#"); if(product.getDescriptions().size()>0) {
					description = product.getDescriptions().iterator().next().getName();
				}
				System.out.println("$#1161#"); detail.setItemName(description);
	
				packages.add(detail);
			} else if (shippingProduct.getQuantity() > 1) { System.out.println("$#1162#"); System.out.println("$#1163#");
				System.out.println("$#1165#"); System.out.println("$#1164#"); for (int i = 0; i < shippingProduct.getQuantity(); i++) {
					PackageDetails detail = new PackageDetails();
					System.out.println("$#1166#"); detail.setShippingHeight(h
							.doubleValue());
					System.out.println("$#1167#"); detail.setShippingLength(l
							.doubleValue());
					System.out.println("$#1168#"); detail.setShippingWeight(w.doubleValue());
					System.out.println("$#1169#"); detail.setShippingWidth(wd
							.doubleValue());
					System.out.println("$#1170#"); detail.setShippingQuantity(1);//issue seperate shipping
					String description = "item";
					System.out.println("$#1172#"); System.out.println("$#1171#"); if(product.getDescriptions().size()>0) {
						description = product.getDescriptions().iterator().next().getName();
					}
					System.out.println("$#1173#"); detail.setItemName(description);
					
					packages.add(detail);
				}
			} else {
				System.out.println("$#1162#"); System.out.println("$#1163#"); // manual correction for else-if mutation coverage
			}
		}
		
		System.out.println("$#1174#"); return packages;
		
		
		
	}


}


class PackingBox {

	private double volumeLeft;
	private double weightLeft;
	private double weight;

	public double getVolumeLeft() {
		System.out.println("$#1175#"); return volumeLeft;
	}

	public void setVolumeLeft(double volumeLeft) {
		this.volumeLeft = volumeLeft;
	}

	public double getWeight() {
		System.out.println("$#1176#"); return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getWeightLeft() {
		System.out.println("$#1177#"); return weightLeft;
	}

	public void setWeightLeft(double weightLeft) {
		this.weightLeft = weightLeft;
	}

}

