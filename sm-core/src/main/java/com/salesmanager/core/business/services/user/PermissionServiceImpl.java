package com.salesmanager.core.business.services.user;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.user.PermissionRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.user.Group;
import com.salesmanager.core.model.user.Permission;
import com.salesmanager.core.model.user.PermissionCriteria;
import com.salesmanager.core.model.user.PermissionList;



@Service("permissionService")
public class PermissionServiceImpl extends
		SalesManagerEntityServiceImpl<Integer, Permission> implements
		PermissionService {

	private PermissionRepository permissionRepository;


	@Inject
	public PermissionServiceImpl(PermissionRepository permissionRepository) {
		super(permissionRepository);
		this.permissionRepository = permissionRepository;

	}

	@Override
	public List<Permission> getByName() {
		// TODO Auto-generated method stub
		System.out.println("$#3358#"); return null;
	}


	@Override
	public Permission getById(Integer permissionId) {
		System.out.println("$#3359#"); return permissionRepository.findOne(permissionId);

	}


	@Override
	public void deletePermission(Permission permission) throws ServiceException {
		permission = this.getById(permission.getId());//Prevents detached entity error
		System.out.println("$#3360#"); permission.setGroups(null);
		
		System.out.println("$#3361#"); this.delete(permission);
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public List<Permission> getPermissions(List<Integer> groupIds)
			throws ServiceException {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Set ids = new HashSet(groupIds);
		System.out.println("$#3362#"); return permissionRepository.findByGroups(ids);
	}

	@Override
	public PermissionList listByCriteria(PermissionCriteria criteria)
			throws ServiceException {
		System.out.println("$#3363#"); return permissionRepository.listByCriteria(criteria);
	}

	@Override
	public void removePermission(Permission permission,Group group) throws ServiceException {
		permission = this.getById(permission.getId());//Prevents detached entity error
	
		permission.getGroups().remove(group);
		

	}

	@Override
	public List<Permission> listPermission() throws ServiceException {
		System.out.println("$#3364#"); return permissionRepository.findAll();
	}



}
