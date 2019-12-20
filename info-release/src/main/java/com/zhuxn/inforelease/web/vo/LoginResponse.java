package com.zhuxn.inforelease.web.vo;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * @author zhuxn
 * 登录返回Vo
 */
public class LoginResponse {
    //登录密钥
    private String token;
    private List<String> permissions;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public List<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("token", token)
				.append("permissions", permissions)
				.toString();
	}
}
