package com.salesmanager.shop.populator;

import java.util.Date;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.salesmanager.core.model.common.audit.AuditSection;
import com.salesmanager.core.model.common.audit.Auditable;

/**
 * Create audit section
 * @author carlsamson
 *
 */
@Aspect
@Configuration
public class PersistableAuditAspect {
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PersistableAuditAspect.class);

    @AfterReturning(value = "execution(* populate(..))",
            returning = "result")
        public void afterReturning(JoinPoint joinPoint, Object result) {
    	
			try {
				System.out.println("$#10879#"); if(result instanceof Auditable) {
					Auditable entity = (Auditable)result;
					AuditSection audit = entity.getAuditSection();
					System.out.println("$#10880#"); if(entity.getAuditSection()==null) {
						audit = new AuditSection();
					}
					System.out.println("$#10881#"); audit.setDateModified(new Date());
					
					Authentication auth = SecurityContextHolder.getContext().getAuthentication();
					System.out.println("$#10882#"); if(auth!=null) {
						System.out.println("$#10883#"); if(auth instanceof UsernamePasswordAuthenticationToken) {//api only is captured
							com.salesmanager.shop.store.security.user.JWTUser user = (com.salesmanager.shop.store.security.user.JWTUser)auth.getPrincipal();
							System.out.println("$#10884#"); audit.setModifiedBy(user.getUsername());
						}
					}
					//TODO put in log audit log trail
					System.out.println("$#10885#"); entity.setAuditSection(audit);
				}
			} catch (Throwable e) {
				LOGGER.error("Error while setting audit values" + e.getMessage());
			}

        }


}
