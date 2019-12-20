package com.zhuxn.inforelease.shiro.realm;

import com.zhuxn.inforelease.common.Constants;
import com.zhuxn.inforelease.shiro.UserType;
import com.zhuxn.inforelease.shiro.orm.entity.Permission;
import com.zhuxn.inforelease.shiro.orm.entity.Role;
import com.zhuxn.inforelease.shiro.orm.entity.User;
import com.zhuxn.inforelease.shiro.orm.mapper.PermissionMapper;
import com.zhuxn.inforelease.shiro.orm.mapper.RoleMapper;
import com.zhuxn.inforelease.shiro.orm.mapper.UserMapper;
import com.zhuxn.inforelease.shiro.overwrite.UserLoginToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 
 * @author guozc
 * 2018年4月17日
 */
@Component
public class UserDbRealm extends AuthorizingRealm {
	
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private RoleMapper roleMapper;
	@Autowired
	private PermissionMapper permissionMapper;
	@Override
	public String getName() {
		return UserType.NORMAL;
	}
	
	@Override
    protected Object getAuthorizationCacheKey(PrincipalCollection principals) {
        return this.getAuthenticationCacheKey(principals);
    }

    /**
     * @author guozc
     * 保证缓存唯一性
     */
    @Override
    protected Object getAuthenticationCacheKey(PrincipalCollection principals) {
        return principals.getPrimaryPrincipal();

    }
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		if (principals.getRealmNames().contains(UserType.NORMAL)) {
			User user = userMapper.getByUserName((String)principals.getPrimaryPrincipal());
			if (user != null) {
				List<Role> roles = roleMapper.listByUser(user.getId());
				if (!roles.isEmpty()) {
					for (Role r : roles) {
						r.setPermissions(permissionMapper.listByRole(r.getId()));
					}
					user.setRoles(roles);
				}
			}
			SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
			List<Role> roles = user.getRoles();
			if (roles != null) {
				for(Role role : roles){
					info.addRole(role.getCode());
					if (!role.getPermissions().isEmpty()) {
						for (Permission permission : role.getPermissions()) {
							info.addStringPermission(permission.getCode());
						}
					}
				}
			}
			SecurityUtils.getSubject().getSession().setAttribute(Constants.SEESION_USER_KEY, user);
			return info;
		}
		return null;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken authcToken) throws AuthenticationException {
		UserLoginToken token = (UserLoginToken) authcToken;
		User user= null;
		try {
			 user = userMapper.getByUserName((String)token.getPrincipal());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (user == null) {
			throw new UnknownAccountException();
		}
		SecurityUtils.getSubject().getSession().setAttribute(Constants.SEESION_USER_KEY, user);
		return new SimpleAuthenticationInfo(user.getUserName(), user.getPassword(), getName());
		
	}

}
