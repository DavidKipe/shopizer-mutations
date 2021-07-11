package com.salesmanager.core.business.services.common.generic;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.model.generic.SalesManagerEntity;

/**
 * @param <T> entity type
 */
public abstract class SalesManagerEntityServiceImpl<K extends Serializable & Comparable<K>, E extends SalesManagerEntity<K, ?>>
	implements SalesManagerEntityService<K, E> {
	
	/**
	 * Classe de l'entité, déterminé à partir des paramètres generics.
	 */
	private Class<E> objectClass;


    private JpaRepository<E, K> repository;

	@SuppressWarnings("unchecked")
	public SalesManagerEntityServiceImpl(JpaRepository<E, K> repository) {
		ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
		this.objectClass = (Class<E>) genericSuperclass.getActualTypeArguments()[1];
		this.repository = repository;
	}
	
	protected final Class<E> getObjectClass() {
		System.out.println("$#2076#"); return objectClass;
	}


	public E getById(K id) {
		System.out.println("$#2077#"); return repository.getOne(id);
	}

	
	public void save(E entity) throws ServiceException {
		repository.saveAndFlush(entity);
	}
	
	
	public void create(E entity) throws ServiceException {
		System.out.println("$#2078#"); save(entity);
	}

	
	
	public void update(E entity) throws ServiceException {
		System.out.println("$#2079#"); save(entity);
	}
	

	public void delete(E entity) throws ServiceException {
		System.out.println("$#2080#"); repository.delete(entity);
	}
	
	
	public void flush() {
		System.out.println("$#2081#"); repository.flush();
	}
	

	
	public List<E> list() {
		System.out.println("$#2082#"); return repository.findAll();
	}
	

	public Long count() {
		System.out.println("$#2083#"); return repository.count();
	}
	
	protected E saveAndFlush(E entity) {
		System.out.println("$#2084#"); return repository.saveAndFlush(entity);
	}

}