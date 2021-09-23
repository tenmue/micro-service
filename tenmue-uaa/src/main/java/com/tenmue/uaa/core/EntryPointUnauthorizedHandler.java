package com.tenmue.uaa.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenmue.common.result.ResponseResult;
import com.tenmue.common.result.ResultCode;

/**
 * 自定义认证未通过的返回值
 * @author yangtengjiao
 *
 */
@Component	
public class EntryPointUnauthorizedHandler implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		
		response.setCharacterEncoding("UTF-8");
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		
		response.getWriter().write(new ObjectMapper().writeValueAsString(ResponseResult.fail(ResultCode.INVALID_AUTHTOKEN)));
	}
}
