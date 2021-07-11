package com.salesmanager.core.model.shipping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

/**
 * Object saved in the database maintaining various shipping options
 * @author casams1
 *
 */
public class ShippingConfiguration implements JSONAware {
	
	//enums
	private ShippingType shippingType = ShippingType.NATIONAL;
	private ShippingBasisType shippingBasisType = ShippingBasisType.SHIPPING;
	private ShippingOptionPriceType shippingOptionPriceType = ShippingOptionPriceType.ALL;
	private ShippingPackageType shippingPackageType = ShippingPackageType.ITEM;
	private ShippingDescription shippingDescription = ShippingDescription.SHORT_DESCRIPTION;
	private ShippingType freeShippingType = null;
	
	private int boxWidth = 0;
	private int boxHeight = 0;
	private int boxLength = 0;
	private double boxWeight = 0;
	private double maxWeight = 0;
	
	//free shipping
	private boolean freeShippingEnabled = false;
	private BigDecimal orderTotalFreeShipping = null;
	
	private List<Package> packages = new ArrayList<Package>();

	
	
	private BigDecimal handlingFees = null;
	private boolean taxOnShipping = false;
	
	
	//JSON bindings
	private String shipType;
	private String shipBaseType;
	private String shipOptionPriceType = ShippingOptionPriceType.ALL.name();
	private String shipPackageType;
	private String shipDescription;
	private String shipFreeType;
	
	//Transient
	private String orderTotalFreeShippingText = null;
	private String handlingFeesText = null;
	
	
	public String getShipType() {
		System.out.println("$#4564#"); return shipType;
	}


	public String getShipBaseType() {
		System.out.println("$#4565#"); return shipBaseType;
	}


	public String getShipOptionPriceType() {
		System.out.println("$#4566#"); return shipOptionPriceType;
	}



	public void setShippingOptionPriceType(ShippingOptionPriceType shippingOptionPriceType) {
		this.shippingOptionPriceType = shippingOptionPriceType;
		this.shipOptionPriceType = this.shippingOptionPriceType.name();
	}


	public ShippingOptionPriceType getShippingOptionPriceType() {
		System.out.println("$#4567#"); return shippingOptionPriceType;
	}


	public void setShippingBasisType(ShippingBasisType shippingBasisType) {
		this.shippingBasisType = shippingBasisType;
		this.shipBaseType = this.shippingBasisType.name();
	}


	public ShippingBasisType getShippingBasisType() {
		System.out.println("$#4568#"); return shippingBasisType;
	}


	public void setShippingType(ShippingType shippingType) {
		this.shippingType = shippingType;
		this.shipType = this.shippingType.name();
	}


	public ShippingType getShippingType() {
		System.out.println("$#4569#"); return shippingType;
	}
	
	public ShippingPackageType getShippingPackageType() {
		System.out.println("$#4570#"); return shippingPackageType;
	}


	public void setShippingPackageType(ShippingPackageType shippingPackageType) {
		this.shippingPackageType = shippingPackageType;
		this.shipPackageType = shippingPackageType.name();
	}
	
	
	public String getShipPackageType() {
		System.out.println("$#4571#"); return shipPackageType;
	}

	
	/** JSON bindding **/
	public void setShipType(String shipType) {
		this.shipType = shipType;
		ShippingType sType = ShippingType.NATIONAL;
		System.out.println("$#4572#"); if(shipType.equals(ShippingType.INTERNATIONAL.name())) {
			sType = ShippingType.INTERNATIONAL;
		}
		System.out.println("$#4573#"); setShippingType(sType);
	}


	public void setShipOptionPriceType(String shipOptionPriceType) {
		this.shipOptionPriceType = shipOptionPriceType;
		ShippingOptionPriceType sType = ShippingOptionPriceType.ALL;
		System.out.println("$#4574#"); if(shipOptionPriceType.equals(ShippingOptionPriceType.HIGHEST.name())) {
			sType = ShippingOptionPriceType.HIGHEST;
		}
		System.out.println("$#4575#"); if(shipOptionPriceType.equals(ShippingOptionPriceType.LEAST.name())) {
			sType = ShippingOptionPriceType.LEAST;
		}
		System.out.println("$#4576#"); setShippingOptionPriceType(sType);
	}


	public void setShipBaseType(String shipBaseType) {
		this.shipBaseType = shipBaseType;
		ShippingBasisType sType = ShippingBasisType.SHIPPING;
		System.out.println("$#4577#"); if(shipBaseType.equals(ShippingBasisType.BILLING.name())) {
			sType = ShippingBasisType.BILLING;
		}
		System.out.println("$#4578#"); setShippingBasisType(sType);
	}



	public void setShipPackageType(String shipPackageType) {
		this.shipPackageType = shipPackageType;
		ShippingPackageType sType = ShippingPackageType.ITEM;
		System.out.println("$#4579#"); if(shipPackageType.equals(ShippingPackageType.BOX.name())) {
			sType = ShippingPackageType.BOX;
		}
		System.out.println("$#4580#"); this.setShippingPackageType(sType);
	}
	
	public void setShipDescription(String shipDescription) {
		this.shipDescription = shipDescription;
		ShippingDescription sType = ShippingDescription.SHORT_DESCRIPTION;
		System.out.println("$#4581#"); if(shipDescription.equals(ShippingDescription.LONG_DESCRIPTION.name())) {
			sType = ShippingDescription.LONG_DESCRIPTION;
		}
		System.out.println("$#4582#"); this.setShippingDescription(sType);
	}
	
	public void setShipFreeType(String shipFreeType) {
		this.shipFreeType = shipFreeType;
		ShippingType sType = ShippingType.NATIONAL;
		System.out.println("$#4583#"); if(shipFreeType.equals(ShippingType.INTERNATIONAL.name())) {
			sType = ShippingType.INTERNATIONAL;
		}
		System.out.println("$#4584#"); setFreeShippingType(sType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String toJSONString() {
		JSONObject data = new JSONObject();
		data.put("shipBaseType", this.getShippingBasisType().name());
		data.put("shipOptionPriceType", this.getShippingOptionPriceType().name());
		data.put("shipType", this.getShippingType().name());
		data.put("shipPackageType", this.getShippingPackageType().name());
		System.out.println("$#4585#"); if(shipFreeType!=null) {
			data.put("shipFreeType", this.getFreeShippingType().name());
		}
		data.put("shipDescription", this.getShippingDescription().name());
		
		
		data.put("boxWidth", this.getBoxWidth());
		data.put("boxHeight", this.getBoxHeight());
		data.put("boxLength", this.getBoxLength());
		data.put("boxWeight", this.getBoxWeight());
		data.put("maxWeight", this.getMaxWeight());
		data.put("freeShippingEnabled", this.freeShippingEnabled);
		data.put("orderTotalFreeShipping", this.orderTotalFreeShipping);
		data.put("handlingFees", this.handlingFees);
		data.put("taxOnShipping", this.taxOnShipping);
		
		
		JSONArray jsonArray = new JSONArray();

		for(Package p : this.getPackages()) {
			jsonArray.add(transformPackage(p));
		}
		
		data.put("packages", jsonArray);
		
		
		System.out.println("$#4586#"); return data.toJSONString();
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject transformPackage(Package p) {
		JSONObject data = new JSONObject();
		data.put("boxWidth", p.getBoxWidth());
		data.put("boxHeight", p.getBoxHeight());
		data.put("boxLength", p.getBoxLength());
		data.put("boxWeight", p.getBoxWeight());
		data.put("maxWeight", p.getMaxWeight());
		data.put("treshold", p.getTreshold());
		data.put("code", p.getCode());
		data.put("shipPackageType", p.getShipPackageType().name());
		data.put("defaultPackaging", p.isDefaultPackaging());
		System.out.println("$#4587#"); return data;
	}


	public int getBoxWidth() {
		System.out.println("$#4588#"); return boxWidth;
	}


	public void setBoxWidth(int boxWidth) {
		this.boxWidth = boxWidth;
	}


	public int getBoxHeight() {
		System.out.println("$#4589#"); return boxHeight;
	}


	public void setBoxHeight(int boxHeight) {
		this.boxHeight = boxHeight;
	}


	public int getBoxLength() {
		System.out.println("$#4590#"); return boxLength;
	}


	public void setBoxLength(int boxLength) {
		this.boxLength = boxLength;
	}


	public double getBoxWeight() {
		System.out.println("$#4591#"); return boxWeight;
	}


	public void setBoxWeight(double boxWeight) {
		this.boxWeight = boxWeight;
	}


	public double getMaxWeight() {
		System.out.println("$#4592#"); return maxWeight;
	}


	public void setMaxWeight(double maxWeight) {
		this.maxWeight = maxWeight;
	}


	public boolean isFreeShippingEnabled() {
		System.out.println("$#4594#"); System.out.println("$#4593#"); return freeShippingEnabled;
	}


	public void setFreeShippingEnabled(boolean freeShippingEnabled) {
		this.freeShippingEnabled = freeShippingEnabled;
	}


	public BigDecimal getOrderTotalFreeShipping() {
		System.out.println("$#4595#"); return orderTotalFreeShipping;
	}


	public void setOrderTotalFreeShipping(BigDecimal orderTotalFreeShipping) {
		this.orderTotalFreeShipping = orderTotalFreeShipping;
	}


	public void setHandlingFees(BigDecimal handlingFees) {
		this.handlingFees = handlingFees;
	}


	public BigDecimal getHandlingFees() {
		System.out.println("$#4596#"); return handlingFees;
	}


	public void setTaxOnShipping(boolean taxOnShipping) {
		this.taxOnShipping = taxOnShipping;
	}


	public boolean isTaxOnShipping() {
		System.out.println("$#4598#"); System.out.println("$#4597#"); return taxOnShipping;
	}





	public String getShipDescription() {
		System.out.println("$#4599#"); return shipDescription;
	}


	public void setShippingDescription(ShippingDescription shippingDescription) {
		this.shippingDescription = shippingDescription;
	}


	public ShippingDescription getShippingDescription() {
		System.out.println("$#4600#"); return shippingDescription;
	}


	public void setFreeShippingType(ShippingType freeShippingType) {
		this.freeShippingType = freeShippingType;
	}


	public ShippingType getFreeShippingType() {
		System.out.println("$#4601#"); return freeShippingType;
	}



	public String getShipFreeType() {
		System.out.println("$#4602#"); return shipFreeType;
	}


	public void setOrderTotalFreeShippingText(String orderTotalFreeShippingText) {
		this.orderTotalFreeShippingText = orderTotalFreeShippingText;
	}


	public String getOrderTotalFreeShippingText() {
		System.out.println("$#4603#"); return orderTotalFreeShippingText;
	}


	public void setHandlingFeesText(String handlingFeesText) {
		this.handlingFeesText = handlingFeesText;
	}


	public String getHandlingFeesText() {
		System.out.println("$#4604#"); return handlingFeesText;
	}


	public List<Package> getPackages() {
		System.out.println("$#4605#"); return packages;
	}


	public void setPackages(List<Package> packages) {
		this.packages = packages;
	}











}


