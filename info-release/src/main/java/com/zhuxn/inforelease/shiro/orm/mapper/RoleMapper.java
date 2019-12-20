package com.zhuxn.inforelease.shiro.orm.mapper;


import com.zhuxn.inforelease.shiro.orm.entity.Role;

import java.util.List;

/**
 * @author guozc
 */
public interface RoleMapper {
	/**
	 * @author guozc
	 */
	List<Role> listByUser(int userId);
	/**
	 * @author guozc
	 * 通过id查询
	 */
	Role selectById(int id);
	/**
	 * @author guozc
	 * 添加角色
	 */
	int insert(Role role);
	/**
	 * @author guozc
	 * 通过code跟name判断是否已存在相同角色
	 */
	Role selectByNameAndCode(String code, String nam);
	/**
	 * @author guozc
	 * 查询用户列表
	 */
	List<Role> selectList();
}
