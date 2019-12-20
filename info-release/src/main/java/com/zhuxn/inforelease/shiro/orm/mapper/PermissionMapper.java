package com.zhuxn.inforelease.shiro.orm.mapper;


import com.zhuxn.inforelease.shiro.orm.entity.Permission;

import java.util.List;

public interface PermissionMapper {
	/**
	 * @author guozc
	 */
	List<Permission> listByRole(int roleId);
	/**
	 * @author guozc
	 * 查询系统权限
	 */
	List<Permission> selectList();
	
}
