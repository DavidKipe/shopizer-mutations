package com.salesmanager.core.model.common.audit;

import java.util.Date;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class AuditListener {

  @PrePersist
  public void onSave(Object o) {
				System.out.println("$#4016#"); if (o instanceof Auditable) {
      Auditable audit = (Auditable) o;
      AuditSection auditSection = audit.getAuditSection();

						System.out.println("$#4017#"); auditSection.setDateModified(new Date());
						System.out.println("$#4018#"); if (auditSection.getDateCreated() == null) {
								System.out.println("$#4019#"); auditSection.setDateCreated(new Date());
      }
						System.out.println("$#4020#"); audit.setAuditSection(auditSection);
    }
  }

  @PreUpdate
  public void onUpdate(Object o) {
				System.out.println("$#4021#"); if (o instanceof Auditable) {
      Auditable audit = (Auditable) o;
      AuditSection auditSection = audit.getAuditSection();

						System.out.println("$#4022#"); auditSection.setDateModified(new Date());
						System.out.println("$#4023#"); if (auditSection.getDateCreated() == null) {
								System.out.println("$#4024#"); auditSection.setDateCreated(new Date());
      }
						System.out.println("$#4025#"); audit.setAuditSection(auditSection);
    }
  }
}
