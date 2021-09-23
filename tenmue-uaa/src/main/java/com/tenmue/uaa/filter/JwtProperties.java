package com.tenmue.uaa.filter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "uaa")
public class JwtProperties {

	private Long expTime;
	
	private String secret;

	public Long getExpTime() {
		return expTime;
	}

	public void setExpTime(Long expTime) {
		this.expTime = expTime;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
	
	
}
