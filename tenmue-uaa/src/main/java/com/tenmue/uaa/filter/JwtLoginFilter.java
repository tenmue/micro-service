package com.tenmue.uaa.filter;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenmue.common.jwt.JwtUtils;
import com.tenmue.common.result.ResponseResult;
import com.tenmue.common.result.ResultCode;
import com.tenmue.uaa.common.UserLogin;

/**
 * 登录过滤器
 * @author yang_yancy
 *
 */
public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {
	
	private static Logger log = LoggerFactory.getLogger(JwtUtils.class);
	
//	@Autowired
//	private RedisUtils redisUtils;
	
	@Autowired
	private JwtProperties jwtProperties;
	
	public JwtLoginFilter(String url, AuthenticationManager authenticationManager) {
		super(new AntPathRequestMatcher(url));
		setAuthenticationManager(authenticationManager);
	}

	/**
	 * 登录需要验证时调用
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
        UserLogin userLogin = new ObjectMapper().readValue(request.getInputStream(), UserLogin.class);
        
        //返回一个验证令牌
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(userLogin.getLoginName(), userLogin.getPassword()));
	}

	/**
	 * 验证成功后调用
	 */
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
		generateToken(response, authentication.getName(), authentication.getAuthorities());
	}

	/**
	 * 验证失败后调用
	 */
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
			throws IOException, ServletException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        response.getWriter().write(failed.getMessage());
	}
	
	/**
	 * 生成token
	 * @param response
	 * @param userName
	 * @param roles
	 */
	private void generateToken(HttpServletResponse response, String userName, Collection<?> roles) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		try {
			String cacheToken = null;//redisUtils.getCache(StringUtils.join(userName, "_token"));
			String token = null;
			//如果缓存里面的token没有过期，则不重新生成
			if(StringUtils.isNotBlank(cacheToken)) {
				token = cacheToken;
			}else {
				token = JwtUtils.generateToken(userName, jwtProperties.getExpTime(), jwtProperties.getSecret(), roles);
				//保存在缓存中，用于后续刷新token
	            //redisUtils.putCache(StringUtils.join(userName, "_token"), token, 1);
			}
			
        	//将JWT Token写入header
            response.setHeader("Authentication-Info", token);
            
            response.getWriter().write(new ObjectMapper().writeValueAsString(ResponseResult.success()));
        } catch (Exception e) {
        	log.error("生成token失败：{}", e.getMessage());
        	 try {
				response.getWriter().write(new ObjectMapper().writeValueAsString(ResponseResult.fail(ResultCode.INTERNAL_SERVER_ERROR)));
			} catch (IOException e1) {
				log.error("序列化数据异常：{}", e1.getMessage());
			}
        }
	}
}
