package com.tenmue.uaa.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * 自定义身份验证
 *
 * @author yangtengjiao
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
	
	private static Logger log = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    @Override
    public Authentication authenticate(Authentication authentication) {
        String loginName = StringUtils.trim(authentication.getName());
        String password = authentication.getCredentials().toString();

        
    	log.info("账号【{}】成功登录系统", loginName);
        //这里设置权限和角色
        Collection<GrantedAuthority> authorities = obtionGrantedAuthorities();
        //生成令牌
        return new UsernamePasswordAuthenticationToken(loginName, password, authorities);
    }

    /**
     * 取得用户的权限
     *
     * @param account 用户信息对象
     * @return
     */
    private Set<GrantedAuthority> obtionGrantedAuthorities() {
        //根据用户信息获取用户角色信息
        //List<Role> userRoleList = userLoginService.getUserAuth(userInfo.getUserid());

        Set<GrantedAuthority> authSet = new HashSet<GrantedAuthority>();
        authSet.add(new SimpleGrantedAuthority("admin"));
//        if(account.getAccountType() == UserTypeEnum.ADMIN.getCode()) {
//            authSet.add(new SimpleGrantedAuthority(UserTypeEnum.ADMIN.getRole()));
//        }
//        if(account.getAccountType() == UserTypeEnum.GENERAL.getCode()) {
//            authSet.add(new SimpleGrantedAuthority(UserTypeEnum.GENERAL.getRole()));
//        }
        return authSet;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
