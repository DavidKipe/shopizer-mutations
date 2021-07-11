package com.salesmanager.shop.model.catalog.product;

import java.io.Serializable;
import java.math.BigDecimal;
import com.salesmanager.shop.model.references.DimensionUnitOfMeasure;
import com.salesmanager.shop.model.references.WeightUnitOfMeasure;

/**
 * Specs weight dimension model and manufacturer
 * @author carlsamson
 *
 */
public class ProductSpecification implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  
  private BigDecimal height;
  private BigDecimal weight;
  private BigDecimal length;
  private BigDecimal width;
  private String model;
  private String manufacturer; //manufacturer code
  
  public String getManufacturer() {
				System.out.println("$#8918#"); return manufacturer;
  }
  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }
  public String getModel() {
				System.out.println("$#8919#"); return model;
  }
  public void setModel(String model) {
    this.model = model;
  }
  private com.salesmanager.shop.model.references.DimensionUnitOfMeasure dimensionUnitOfMeasure;
  private com.salesmanager.shop.model.references.WeightUnitOfMeasure weightUnitOfMeasure;
  
  public BigDecimal getHeight() {
				System.out.println("$#8920#"); return height;
  }
  public void setHeight(BigDecimal height) {
    this.height = height;
  }
  public BigDecimal getWeight() {
				System.out.println("$#8921#"); return weight;
  }
  public void setWeight(BigDecimal weight) {
    this.weight = weight;
  }
  public BigDecimal getLength() {
				System.out.println("$#8922#"); return length;
  }
  public void setLength(BigDecimal length) {
    this.length = length;
  }
  public BigDecimal getWidth() {
				System.out.println("$#8923#"); return width;
  }
  public void setWidth(BigDecimal width) {
    this.width = width;
  }
  public DimensionUnitOfMeasure getDimensionUnitOfMeasure() {
				System.out.println("$#8924#"); return dimensionUnitOfMeasure;
  }
  public void setDimensionUnitOfMeasure(DimensionUnitOfMeasure dimensionUnitOfMeasure) {
    this.dimensionUnitOfMeasure = dimensionUnitOfMeasure;
  }
  public WeightUnitOfMeasure getWeightUnitOfMeasure() {
				System.out.println("$#8925#"); return weightUnitOfMeasure;
  }
  public void setWeightUnitOfMeasure(WeightUnitOfMeasure weightUnitOfMeasure) {
    this.weightUnitOfMeasure = weightUnitOfMeasure;
  }
  public static long getSerialversionuid() {
				System.out.println("$#8926#"); return serialVersionUID;
  }

  
  
}
