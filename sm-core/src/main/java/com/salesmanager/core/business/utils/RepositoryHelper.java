package com.salesmanager.core.business.utils;

import javax.persistence.Query;

import com.salesmanager.core.model.common.Criteria;
import com.salesmanager.core.model.common.GenericEntityList;

/**
 * Helper for Spring Data JPA
 * 
 * @author carlsamson
 *
 */
public class RepositoryHelper {

	@SuppressWarnings("rawtypes")
	public static Query paginateQuery(Query q, Number count, GenericEntityList entityList, Criteria criteria) {

		System.out.println("$#3691#"); if (entityList == null) {
			entityList = new GenericEntityList();
		}

		System.out.println("$#3692#"); if (criteria.isLegacyPagination()) {
			System.out.println("$#3694#"); System.out.println("$#3693#"); if (criteria.getMaxCount() > 0) {
				q.setFirstResult(criteria.getStartIndex());
				System.out.println("$#3696#"); System.out.println("$#3695#"); if (criteria.getMaxCount() < count.intValue()) {
					q.setMaxResults(criteria.getMaxCount());
				} else {
					q.setMaxResults(count.intValue());
				}
			}
		} else {
			System.out.println("$#3699#"); System.out.println("$#3698#"); System.out.println("$#3697#"); int firstResult = ((criteria.getStartPage()==0?criteria.getStartPage()+1:criteria.getStartPage()) - 1) * criteria.getPageSize();
			q.setFirstResult(firstResult);
			q.setMaxResults(criteria.getPageSize());
			System.out.println("$#3702#"); System.out.println("$#3701#"); int lastPageNumber = (int) ((count.intValue() / criteria.getPageSize()) + 1);
			System.out.println("$#3703#"); entityList.setTotalPages(lastPageNumber);
			System.out.println("$#3704#"); entityList.setTotalCount(count.intValue());
		}

		System.out.println("$#3705#"); return q;

	}

}
