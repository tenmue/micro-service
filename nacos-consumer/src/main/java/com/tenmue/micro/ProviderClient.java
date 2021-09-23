package com.tenmue.micro;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-provider")
public interface ProviderClient {

	@GetMapping("/echo/{name}")
	String echo(@PathVariable String name);
}
