package com.salesmanager.core.model.catalog.category;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;

import javax.validation.constraints.NotEmpty;

import com.salesmanager.core.constants.SchemaConstant;
import com.salesmanager.core.model.common.audit.AuditSection;
import com.salesmanager.core.model.common.audit.Auditable;
import com.salesmanager.core.model.generic.SalesManagerEntity;
import com.salesmanager.core.model.merchant.MerchantStore;

@Entity
@EntityListeners(value = com.salesmanager.core.model.common.audit.AuditListener.class)
@Table(name = "CATEGORY", schema= SchemaConstant.SALESMANAGER_SCHEMA,uniqueConstraints=
    @UniqueConstraint(columnNames = {"MERCHANT_ID", "CODE"}) )


public class Category extends SalesManagerEntity<Long, Category> implements Auditable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "CATEGORY_ID", unique=true, nullable=false)
    @TableGenerator(name = "TABLE_GEN", table = "SM_SEQUENCER", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT", pkColumnValue = "CATEGORY_SEQ_NEXT_VAL")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN")
    private Long id;

    @Embedded
    private AuditSection auditSection = new AuditSection();

    @Valid
    @OneToMany(mappedBy="category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CategoryDescription> descriptions = new HashSet<CategoryDescription>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="MERCHANT_ID", nullable=false)
    private MerchantStore merchantStore;
    
    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    private Category parent;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
    private List<Category> categories = new ArrayList<Category>();
    
    @Column(name = "CATEGORY_IMAGE", length=100)
    private String categoryImage;

    @Column(name = "SORT_ORDER")
    private Integer sortOrder = 0;

    @Column(name = "CATEGORY_STATUS")
    private boolean categoryStatus;

    @Column(name = "VISIBLE")
    private boolean visible;

    @Column(name = "DEPTH")
    private Integer depth;

    @Column(name = "LINEAGE")
    private String lineage;
    
    @Column(name="FEATURED")
    private boolean featured;
    
    @NotEmpty
    @Column(name="CODE", length=100, nullable=false)
    private String code;

    public String getCode() {
								System.out.println("$#3737#"); return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Category() {
    }
    
    public Category(MerchantStore store) {
        this.merchantStore = store;
        this.id = 0L;
    }
    
    @Override
    public Long getId() {
								System.out.println("$#3738#"); return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
    
    @Override
    public AuditSection getAuditSection() {
								System.out.println("$#3739#"); return auditSection;
    }
    
    @Override
    public void setAuditSection(AuditSection auditSection) {
        this.auditSection = auditSection;
    }


    public String getCategoryImage() {
								System.out.println("$#3740#"); return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public Integer getSortOrder() {
								System.out.println("$#3741#"); return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isCategoryStatus() {
								System.out.println("$#3743#"); System.out.println("$#3742#"); return categoryStatus;
    }

    public void setCategoryStatus(boolean categoryStatus) {
        this.categoryStatus = categoryStatus;
    }

    public boolean isVisible() {
								System.out.println("$#3745#"); System.out.println("$#3744#"); return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Integer getDepth() {
								System.out.println("$#3746#"); return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public String getLineage() {
								System.out.println("$#3747#"); return lineage;
    }

    public void setLineage(String lineage) {
        this.lineage = lineage;
    }

    public Category getParent() {
								System.out.println("$#3748#"); return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }
    



    public MerchantStore getMerchantStore() {
								System.out.println("$#3749#"); return merchantStore;
    }

    public void setMerchantStore(MerchantStore merchantStore) {
        this.merchantStore = merchantStore;
    }

    public List<Category> getCategories() {
								System.out.println("$#3750#"); return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
    
    public CategoryDescription getDescription() {
								System.out.println("$#3752#"); System.out.println("$#3751#"); if(descriptions!=null && descriptions.size()>0) {
												System.out.println("$#3754#"); return descriptions.iterator().next();
        }
        
        return null;
    }

    public boolean isFeatured() {
								System.out.println("$#3756#"); System.out.println("$#3755#"); return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public Set<CategoryDescription> getDescriptions() {
						System.out.println("$#3757#"); return descriptions;
    }

    public void setDescriptions(Set<CategoryDescription> descriptions) {
      this.descriptions = descriptions;
    }

}