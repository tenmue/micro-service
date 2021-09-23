package com.tenmue.uaa.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.tenmue.uaa.filter.JwtAuthenticationFilter;
import com.tenmue.uaa.filter.JwtLoginFilter;

/**
 * 权限设置
 * @author yangtengjiao
 *
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private EntryPointUnauthorizedHandler entryPointUnauthorizedHandler;
	
	@Bean
	public JwtLoginFilter jwtLoginFilter() throws Exception {
		return new JwtLoginFilter("/login", authenticationManager());
	}
	
	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
		return new JwtAuthenticationFilter();
	}
	
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
		.cors()//允许跨域访问
		.and()
		.csrf().disable()//关闭csrf验证
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//基于token，因此不需要session
		.and()
        .authorizeRequests()//对请求进行认证
		.antMatchers(HttpMethod.OPTIONS).permitAll()
		.antMatchers(HttpMethod.POST, "/login").permitAll()
		.anyRequest().authenticated()//所有请求需要身份认证
		.and()
		//添加一个过滤器 所有访问/login的请求交给JWTLoginFilter
		.addFilterBefore(jwtLoginFilter(), UsernamePasswordAuthenticationFilter.class)
		//添加一个过滤器验证其他请求的Token是否合法
		.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
		.headers().cacheControl();//禁用缓存
		
		//自定义身份验证失败的返回值
		httpSecurity.exceptionHandling().authenticationEntryPoint(entryPointUnauthorizedHandler);
	}
}
