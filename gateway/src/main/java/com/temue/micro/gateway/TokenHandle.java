package com.temue.micro.gateway;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenmue.common.jwt.JwtUtils;
import com.tenmue.common.result.ResponseResult;
import com.tenmue.common.result.ResultCode;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class TokenHandle implements GlobalFilter, Ordered {
	
	private static Logger log = LoggerFactory.getLogger(TokenHandle.class);

	@Override
	public int getOrder() {
		return -1;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		ServerHttpResponse response = exchange.getResponse();
		String path = request.getURI().getPath();
		if(StringUtils.indexOf(path, "/login") >= 0) {
			return chain.filter(exchange);
		}
		String token = request.getHeaders().getFirst("Authorization");
		if(StringUtils.isEmpty(token)) {
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			return getMono(response, "token缺失");
		}
		
		if (StringUtils.startsWith(token, "Bearer ")) {
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			try {
				JwtUtils.parseToken(token.replace("Bearer ", ""), "TW2*8@RFTqs9W@*vBlMIys0g#ijPUCtJrQEP6Pv8TxHYaTwlq&");
			} catch (Exception e) {
				return getMono(response, ResponseResult.fail(ResultCode.INVALID_AUTHTOKEN));
			}
		}
		return chain.filter(exchange);
	}
	
	/**
	 * sentinel异常信息自定义
	 */
//	@PostConstruct
//	private void initBlockHandler() {
//		BlockRequestHandler blockRequestHandler = new BlockRequestHandler() {
//			
//			@Override
//			public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable t) {
//				Map<String, Object> map = new HashMap<>();
//				map.put("code", 429);
//				map.put("message", "sentinel自定义接口限流异常信息返回");
//				return ServerResponse.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(map));
//			}
//		};
//		GatewayCallbackManager.setBlockHandler(blockRequestHandler);
//	}

	public Mono<Void> getMono(ServerHttpResponse response, Object obj) {
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
		try {
			DataBuffer warp = response.bufferFactory().wrap(new ObjectMapper().writeValueAsBytes(obj));
			return response.writeWith(Flux.just(warp));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
