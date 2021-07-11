package com.salesmanager.core.business.repositories.content;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.salesmanager.core.model.content.Content;
import com.salesmanager.core.model.content.ContentDescription;
import com.salesmanager.core.model.content.ContentType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;


public class ContentRepositoryImpl implements ContentRepositoryCustom {

	
    @PersistenceContext
    private EntityManager em;
    
	@Override
	public List<ContentDescription> listNameByType(List<ContentType> contentType, MerchantStore store, Language language) {
		


			StringBuilder qs = new StringBuilder();

			qs.append("select c from Content c ");
			qs.append("left join fetch c.descriptions cd join fetch c.merchantStore cm ");
			qs.append("where c.contentType in (:ct) ");
			qs.append("and cm.id =:cm ");
			qs.append("and cd.language.id =:cl ");
			qs.append("and c.visible=true ");
			qs.append("order by c.sortOrder");

			String hql = qs.toString();
			Query q = this.em.createQuery(hql);

	    	q.setParameter("ct", contentType);
	    	q.setParameter("cm", store.getId());
	    	q.setParameter("cl", language.getId());
	

			@SuppressWarnings("unchecked")
			List<Content> contents = q.getResultList();
			
			List<ContentDescription> descriptions = new ArrayList<ContentDescription>();
			for(Content c : contents) {
					String name = c.getDescription().getName();
					String url = c.getDescription().getSeUrl();
					ContentDescription contentDescription = new ContentDescription();
					System.out.println("$#1618#"); contentDescription.setName(name);
					System.out.println("$#1619#"); contentDescription.setSeUrl(url);
					System.out.println("$#1620#"); contentDescription.setContent(c);
					descriptions.add(contentDescription);
					
			}
			
			System.out.println("$#1621#"); return descriptions;

	}
	
	@Override
	public ContentDescription getBySeUrl(MerchantStore store,String seUrl) {

			StringBuilder qs = new StringBuilder();

			qs.append("select c from Content c ");
			qs.append("left join fetch c.descriptions cd join fetch c.merchantStore cm ");
			qs.append("where cm.id =:cm ");
			qs.append("and c.visible =true ");
			qs.append("and cd.seUrl =:se ");


			String hql = qs.toString();
			Query q = this.em.createQuery(hql);

	    	q.setParameter("cm", store.getId());
	    	q.setParameter("se", seUrl);
	

	    	Content content = (Content)q.getSingleResult();
			

			System.out.println("$#1622#"); if(content!=null) {
					System.out.println("$#1623#"); return content.getDescription();
			}
			
			@SuppressWarnings("unchecked")
			List<Content> results = q.getResultList();
									System.out.println("$#1624#"); if (results.isEmpty()) {
	        	return null;
									} else if (results.size() >= 1) { System.out.println("$#1625#"); System.out.println("$#1626#");
	        		content = results.get(0);
	        }
	        
			System.out.println("$#1627#"); if(content!=null) {
				System.out.println("$#1628#"); return content.getDescription();
			}
	        
			
			return null;

	}
    

}