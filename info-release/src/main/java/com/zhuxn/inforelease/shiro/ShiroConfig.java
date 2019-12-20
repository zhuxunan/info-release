package com.zhuxn.inforelease.shiro;

import com.zhuxn.inforelease.common.ExceptionHandler;
import com.zhuxn.inforelease.shiro.overwrite.UserRealmAuthenticator;
import com.zhuxn.inforelease.shiro.realm.UserDbRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.FirstSuccessfulStrategy;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import javax.annotation.Resource;
import javax.servlet.Filter;
import java.util.*;


@Component
@ConfigurationProperties(prefix="spring.shiro.redis")
public class ShiroConfig {
	    private String host;  
	    private int port;  
	    private int timeout;  
	    private String password; 
	    private int database;
	    private int sessionExpire;
	    @Resource
	    private UserDbRealm userDbRealm;
	    @Bean
		@Order(value = Integer.MAX_VALUE)
	    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) { 
	        System.out.println("ShiroConfiguration.shirFilter()");  
	        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();  
	        shiroFilterFactoryBean.setSecurityManager(securityManager);
	        Map<String, Filter> filters = shiroFilterFactoryBean.getFilters();//获取filters
			//将自定义 的ShiroFilterFactoryBean注入shiroFilter
			filters.put("perms", new ShiroPermissionsFilter());
			filters.put("roles", new ShiroRolesFilter());
	        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();  
	        filterChainDefinitionMap.put("/shiro/logout", "anon");  
	        // 配置不会被拦截的链接 顺序判断
	        filterChainDefinitionMap.put("/login/**", "anon");
			filterChainDefinitionMap.put("/visitor/**", "anon");

			//业务拦截器
			filterChainDefinitionMap.put("/user/**", "roles[user]");
			filterChainDefinitionMap.put("/** ", "authc");

			//配置shiro默认登录界面地址，前后端分离中登录界面跳转应由前端路由控制，后台仅返回json数据
	        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap); 
	        return shiroFilterFactoryBean;  
	    }  
	  
	  
	    /**  
	     * 凭证匹配器  
	     * （由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了  
	     * ）  
	     *  
	     * @return  
	     */  
	    @Bean
	    public HashedCredentialsMatcher hashedCredentialsMatcher() {  
	        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();  
	        hashedCredentialsMatcher.setHashAlgorithmName("md5");//散列算法:这里使用MD5算法;  
	        hashedCredentialsMatcher.setHashIterations(1);//散列的次数，比如散列两次，相当于 md5(md5(""));  
	        return hashedCredentialsMatcher;  
	    }  
	  
	    @Bean
	    public SecurityManager securityManager() {  
	        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager(); 
	        //实现多realm认证
	        List<Realm> realms = new ArrayList<Realm>();
	        realms.add(userDbRealm);
	        securityManager.setRealms(realms);
	        //使用自定义的认证器
			UserRealmAuthenticator authenticator = new UserRealmAuthenticator();
	        HashMap<String, Object> map = new HashMap<String, Object>();
	        map.put(UserType.NORMAL, userDbRealm);
	        authenticator.setAuthenticationStrategy(new FirstSuccessfulStrategy());
	        authenticator.setDefinedRealms(map);
	        authenticator.setRealms(realms);
	        securityManager.setAuthenticator(authenticator);
	        // 自定义session管理 使用redis  
	        securityManager.setSessionManager(sessionManager()); 
	        // 自定义缓存实现 使用redis  
	        securityManager.setCacheManager(cacheManager());  
	        return securityManager;  
	    }  
	  
	    /**
	     * 自定义sessionManager
	     */
	    @Bean
	    public SessionManager sessionManager() {  
	    	HeaderSessionManager mySessionManager = new HeaderSessionManager();  
	        mySessionManager.setSessionDAO(redisSessionDAO());  
	        return mySessionManager;  
	    }  
	  
	    /** 
	     * 配置shiro redisManager 
	     * 使用的是shiro-redis开源插件
	     * @return
	     */  
	    public RedisManager redisManager() {  
	        RedisManager redisManager = new RedisManager();  
	        redisManager.setHost(host);  
	        redisManager.setPort(port);  
	        redisManager.setExpire(sessionExpire);// 配置缓存过期时间
	        redisManager.setTimeout(timeout);  
	        redisManager.setPassword(password); 
	        redisManager.setDatabase(database);
	        return redisManager;  
	    }  
	  
	    /** 
	     * cacheManager 缓存 redis实现 
	     * 使用的是shiro-redis开源插件
	     * @return
	     */  
	    @Bean
	    public RedisCacheManager cacheManager() {  
	        RedisCacheManager redisCacheManager = new RedisCacheManager();  
	        redisCacheManager.setRedisManager(redisManager());
	        return redisCacheManager;  
	    }  
	  
	    /** 
	     * RedisSessionDAO shiro sessionDao层的实现 通过redis 
	     * 使用的是shiro-redis开源插件
	     */  
	    @Bean
	    public RedisSessionDAO redisSessionDAO() {  
	        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();  
	        redisSessionDAO.setRedisManager(redisManager());  
	        return redisSessionDAO;  
	    }  
	  
//	    /** 
//	     * 开启shiro aop注解支持. 
//	     * 使用代理方式;所以需要开启代码支持; 
//	     * 
//	     * @param securityManager 
//	     * @return 
//	     */  
	    @Bean
	    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {  
	        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();  
	        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);  
	        return authorizationAttributeSourceAdvisor;  
	    }
	    
	    @Bean
	    @ConditionalOnMissingBean
	    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
	        DefaultAdvisorAutoProxyCreator defaultAAP = new DefaultAdvisorAutoProxyCreator();
	        defaultAAP.setProxyTargetClass(true);
	        return defaultAAP;
	    }

	    /**
	     * 注册全局异常处理 
	     * @return 
	     */  
	    @Bean(name = "exceptionHandler")
	    public HandlerExceptionResolver handlerExceptionResolver() {
	        return new ExceptionHandler();
	    }

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public int getTimeout() {
			return timeout;
		}

		public void setTimeout(int timeout) {
			this.timeout = timeout;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public int getDatabase() {
			return database;
		}

		public void setDatabase(int database) {
			this.database = database;
		}

		public int getSessionExpire() {
			return sessionExpire;
		}

		public void setSessionExpire(int sessionExpire) {
			this.sessionExpire = sessionExpire;
		}
}
