package com.tenmue.uaa.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenmue.common.jwt.JwtAttribute;
import com.tenmue.common.jwt.JwtUtils;
import com.tenmue.common.result.ResponseResult;
import com.tenmue.common.result.ResultCode;
import com.tenmue.uaa.common.UaaConstants;

/**
 * 拦截所有需要JWT的请求
 * @author yangtengjiao
 *
 */

public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	private static Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
	
	@Autowired
	private JwtProperties jwtProperties;

	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		//从Header中拿到token
        String token = request.getHeader(UaaConstants.TOKEN_HEADER);

        Authentication authentication = null;

        if (StringUtils.startsWith(token, UaaConstants.TOKEN_PREFIX)) {
        	try {
        		authentication = getAuthentication(token);
			} catch (Exception e) {
				log.error("token【{}】无效:{}", token, e.getMessage());
				response.setCharacterEncoding("UTF-8");
	    		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
	    		response.setStatus(HttpStatus.UNAUTHORIZED.value());
	    		
				response.getWriter().write(new ObjectMapper().writeValueAsString(ResponseResult.fail(ResultCode.INVALID_AUTHTOKEN)));
				return;
			}
		}
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        chain.doFilter(request, response);
	}
	
	/**
     * 获取用户认证信息 Authentication
     */
    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        token = token.replace(UaaConstants.TOKEN_PREFIX, "");
        JwtAttribute jwtAttribute = JwtUtils.parseToken(token.replace(UaaConstants.TOKEN_PREFIX, ""), jwtProperties.getSecret());
		String loginName = jwtAttribute.getLoginName();
		List<GrantedAuthority> authorities =  AuthorityUtils.commaSeparatedStringToAuthorityList(jwtAttribute.getAuthorities());
		return new UsernamePasswordAuthenticationToken(loginName, null, authorities);
    }
}
