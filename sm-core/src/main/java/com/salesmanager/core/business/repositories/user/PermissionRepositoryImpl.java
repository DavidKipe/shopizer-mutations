package com.salesmanager.core.business.repositories.user;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


import javax.persistence.Query;

import com.salesmanager.core.model.user.Permission;
import com.salesmanager.core.model.user.PermissionCriteria;
import com.salesmanager.core.model.user.PermissionList;


public class PermissionRepositoryImpl implements PermissionRepositoryCustom {

	
    @PersistenceContext
    private EntityManager em;
    
	@Override
	public PermissionList listByCriteria(PermissionCriteria criteria) {
		PermissionList permissionList = new PermissionList();

        
		StringBuilder countBuilderSelect = new StringBuilder();
		countBuilderSelect.append("select count(p) from Permission as p");
		
		StringBuilder countBuilderWhere = new StringBuilder();
		
		
		System.out.println("$#1698#"); System.out.println("$#1697#"); if(criteria.getGroupIds()!=null && criteria.getGroupIds().size()>0) {
			countBuilderSelect.append(" INNER JOIN p.groups grous");
			countBuilderWhere.append(" where grous.id in (:cid)");
		}
		
	
		Query countQ = em.createQuery(
				countBuilderSelect.toString() + countBuilderWhere.toString());

		System.out.println("$#1701#"); System.out.println("$#1700#"); if(criteria.getGroupIds()!=null && criteria.getGroupIds().size()>0) {
			countQ.setParameter("cid", criteria.getGroupIds());
		}
		

		Number count = (Number) countQ.getSingleResult ();

		System.out.println("$#1703#"); permissionList.setTotalCount(count.intValue());

		System.out.println("$#1704#"); if(count.intValue()==0) {
			System.out.println("$#1705#");
			return permissionList;
		}

		
		StringBuilder qs = new StringBuilder();
		qs.append("select p from Permission as p ");
		qs.append("join fetch p.groups grous ");
		
		System.out.println("$#1707#"); System.out.println("$#1706#"); if(criteria.getGroupIds()!=null && criteria.getGroupIds().size()>0) {
			qs.append(" where grous.id in (:cid)");
		}
		
		qs.append(" order by p.id asc ");
		
    	String hql = qs.toString();
		Query q = em.createQuery(hql);


					System.out.println("$#1710#"); System.out.println("$#1709#"); if(criteria.getGroupIds()!=null && criteria.getGroupIds().size()>0) {
    		q.setParameter("cid", criteria.getGroupIds());
    	}
    	
					System.out.println("$#1713#"); System.out.println("$#1712#"); if(criteria.getMaxCount()>0) {
    		
    		
	    	q.setFirstResult(criteria.getStartIndex());
						System.out.println("$#1715#"); System.out.println("$#1714#"); if(criteria.getMaxCount()<count.intValue()) {
	    		q.setMaxResults(criteria.getMaxCount());
							System.out.println("$#1716#"); permissionList.setTotalCount(criteria.getMaxCount());
	    	}
	    	else {
	    		q.setMaxResults(count.intValue());
							System.out.println("$#1717#"); permissionList.setTotalCount(count.intValue());
	    	}
    	}
    	
    	@SuppressWarnings("unchecked")
		List<Permission> permissions =  q.getResultList();
					System.out.println("$#1718#"); permissionList.setPermissions(permissions);
    	
					System.out.println("$#1719#"); return permissionList;
	}   

}
